package com.byteplay.android.renderclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.byteplay.android.renderclient.math.Matrix4;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class GLLayoutLayer extends GLLayer {


    private static final String VERTEX_SHADER = "precision highp float;\n" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "uniform mat4 inputTextureMatrix;\n" +
            "uniform mat4 viewPortMatrix;\n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = viewPortMatrix*position;\n" +
            "    textureCoordinate =(inputTextureMatrix*inputTextureCoordinate).xy;\n" +
            "}";


    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "uniform samplerExternalOES inputImageTexture;\n" +
            "uniform vec2 inputTextureSize;\n" +
            "uniform vec2 viewPortSize;\n" +
            "uniform float renderTime;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    vec4 color  = texture2D(inputImageTexture, textureCoordinate);\n" +
            "    gl_FragColor = color;\n" +
            "}";

    private static Handler FRAME_AVAILABLE_HANDLER;
    private static int FRAME_AVAILABLE_COUNT;

    private final Map<View, Boolean> viewAttachments = new HashMap<>();

    private final Matrix4 textureMatrix = new Matrix4();
    private final FrameLayout rootLayout;

    private GLTexture viewTexture;
    private SurfaceTexture surfaceTexture;
    private int lastSurfaceTextureWidth;
    private int lastSurfaceTextureHeight;
    private Surface surface;
    private Handler handler;
    private Handler surfaceTextureHandler;
    private Context context;
    private Object object = new Object();
    private boolean done;

    protected GLLayoutLayer(GLRenderClient client, Context context) {
        this(client, context, 0);
    }

    protected GLLayoutLayer(GLRenderClient client, Context context, int style) {
        super(client, VERTEX_SHADER, FRAGMENT_SHADER);
        this.context = new ContextThemeWrapper(context.getApplicationContext(), style);
        rootLayout = new FrameLayout(this.context);
        ReflectionLimitUtils.clearLimit();
    }

    public Context getContext() {
        return context;
    }

    @Override
    protected void onCreate() {
        viewTexture = client.newTexture(GLTextureType.TEXTURE_OES);
        surfaceTexture = new SurfaceTexture(viewTexture.getTextureId());
        surfaceTexture.setOnFrameAvailableListener(surfaceTexture -> {
            synchronized (object) {
                done = true;
                object.notify();
            }
        }, getSurfaceTextureHandler());
        surface = new Surface(surfaceTexture);
    }

    private Handler getSurfaceTextureHandler() {
        synchronized (GLLayoutLayer.class) {
            if (surfaceTextureHandler == null) {
                if (FRAME_AVAILABLE_HANDLER == null) {
                    HandlerThread handlerThread = new HandlerThread("SurfaceTextureFrameAvailableThread");
                    handlerThread.start();
                    FRAME_AVAILABLE_HANDLER = new Handler(handlerThread.getLooper());
                }
                FRAME_AVAILABLE_COUNT++;
                surfaceTextureHandler = FRAME_AVAILABLE_HANDLER;
            }
        }
        return surfaceTextureHandler;
    }

    @Override
    protected void onDispose() {
        detachWindow();
        surfaceTexture.setOnFrameAvailableListener(null);
        releaseSurfaceTextureHandler();
        surface.release();
        surfaceTexture.release();
        viewTexture.dispose();
    }

    private void releaseSurfaceTextureHandler() {
        synchronized (GLLayoutLayer.class) {
            if (surfaceTextureHandler != null) {
                surfaceTextureHandler.removeCallbacksAndMessages(null);
                FRAME_AVAILABLE_COUNT--;
                if (FRAME_AVAILABLE_COUNT <= 0) {
                    if (FRAME_AVAILABLE_HANDLER != null) {
                        FRAME_AVAILABLE_HANDLER.getLooper().quit();
                        FRAME_AVAILABLE_HANDLER = null;
                    }
                }
                surfaceTextureHandler = null;
            }
        }
    }


    @Override
    protected boolean onRenderLayer(GLLayer layer, long renderTimeMs) {
        super.onRenderLayer(layer, renderTimeMs);
        int textureWidth = viewTexture.getWidth();
        int textureHeight = viewTexture.getHeight();
        GLShaderParam shaderParam = layer.getDefaultShaderParam();
        shaderParam.put("inputImageTexture", viewTexture.getTextureId());
        shaderParam.put("inputTextureSize", textureWidth, textureHeight);
        int viewWidth = layer.getRenderWidth();
        int viewHeight = layer.getRenderHeight();
        if (lastSurfaceTextureWidth != viewWidth || lastSurfaceTextureHeight != viewHeight) {
            surfaceTexture.setDefaultBufferSize(viewWidth, viewHeight);
            lastSurfaceTextureWidth = viewWidth;
            lastSurfaceTextureHeight = viewHeight;
        }
        viewTexture.setWidth(viewWidth);
        viewTexture.setHeight(viewHeight);
        renderView(viewWidth, viewHeight);
        surfaceTexture.getTransformMatrix(textureMatrix.get());
        shaderParam.put("inputTextureMatrix", textureMatrix.get());
        return true;
    }


    public void addView(View view) {
        rootLayout.addView(view);
    }

    public void addView(View view, FrameLayout.LayoutParams params) {
        rootLayout.addView(view, params);
    }

    public void removeView(View view) {
        rootLayout.removeView(view);
    }

    public void removeViewAt(int index) {
        rootLayout.removeViewAt(index);
    }

    public void removeAllView() {
        rootLayout.removeAllViews();
    }

    public View getChildAt(int index) {
        return rootLayout.getChildAt(index);
    }

    public int getChildCount() {
        return rootLayout.getChildCount();
    }


    void renderView(int viewWidth, int viewHeight) {
        if (handler == null) {
            handler = new Handler();
        } else if (!handler.getLooper().equals(Looper.myLooper())) {
            detachWindow();
            handler = new Handler();
        }
        dispatchAttachWindow(rootLayout);
        measureAndLayoutRootLayer(viewWidth, viewHeight);
        dispatchOnGlobalLayout(rootLayout);
        dispatchPreDraw(rootLayout);
        drawRootLayout();
        dispatchOnDraw(rootLayout);
    }

    private void dispatchAttachWindow(View view) {
        if (view.getWindowToken() == null && !viewAttachments.containsKey(view)) {
            try {
                Object attachInfo = generateAttachInfo(view);
                if (attachInfo == null) {
                    viewAttachments.put(view, false);
                } else {
                    Method dispatch = View.class.getDeclaredMethod(
                            "dispatchAttachedToWindow", Class.forName("android.view.View$AttachInfo"), int.class);
                    dispatch.setAccessible(true);
                    dispatch.invoke(view, attachInfo, 0);
                    viewAttachments.put(view, true);
                }
            } catch (Exception e) {
                viewAttachments.put(view, false);
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                dispatchAttachWindow(vg.getChildAt(i));
            }
        }
    }

    private void measureAndLayoutRootLayer(int viewWidth, int viewHeight) {
        rootLayout.measure(
                View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY));
        do {
            rootLayout.layout(0, 0, rootLayout.getMeasuredWidth(), rootLayout.getMeasuredHeight());
        } while (rootLayout.isLayoutRequested());
    }

    @Override
    protected boolean dispatchTouchEvent(MotionEvent event) {
        boolean result = rootLayout.dispatchTouchEvent(event);
        if (result) {
            return true;
        }
        return super.dispatchTouchEvent(event);
    }

    private void dispatchOnGlobalLayout(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                dispatchOnGlobalLayout(vg.getChildAt(i));
            }
        }
        view.getViewTreeObserver().dispatchOnGlobalLayout();
    }

    private void dispatchPreDraw(View view) {
        while (view.getViewTreeObserver().dispatchOnPreDraw()) {
        }
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                dispatchPreDraw(vg.getChildAt(i));
            }
        }
    }

    private void drawRootLayout() {
        Canvas glCanvas = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                glCanvas = surface.lockHardwareCanvas();
            } else {
                glCanvas = surface.lockCanvas(null);
            }
            rootLayout.draw(glCanvas);
        } catch (Exception e) {

        } finally {
            if (glCanvas != null) {
                surface.unlockCanvasAndPost(glCanvas);
            }
        }
        synchronized (object) {
            while (!done) {
                try {
                    object.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        try {
            if (done) {
                surfaceTexture.updateTexImage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dispatchOnDraw(View view) {
        view.getViewTreeObserver().dispatchOnDraw();
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                dispatchPreDraw(vg.getChildAt(i));
            }
        }
    }

    private void detachWindow() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        dispatchDetachWindow(rootLayout);
    }


    private void dispatchDetachWindow(View view) {
        if (viewAttachments.containsKey(view)) {
            try {
                @SuppressLint("SoonBlockedPrivateApi")
                Method method = View.class.getDeclaredMethod("dispatchDetachedFromWindow");
                method.setAccessible(true);
                method.invoke(view);
            } catch (Exception e1) {

            }
            viewAttachments.remove(view);
        }
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                dispatchDetachWindow(vg.getChildAt(i));
            }
        }
    }


    private Object generateAttachInfo(View view) {
        try {
            Class cAttachInfo = Class.forName("android.view.View$AttachInfo");
            Class cViewRootImpl;
            if (Build.VERSION.SDK_INT >= 11) {
                cViewRootImpl = Class.forName("android.view.ViewRootImpl");
            } else {
                return null;
            }

            Class cIWindowSession = Class.forName("android.view.IWindowSession");
            Class cIWindow = Class.forName("android.view.IWindow");
            Class cICallbacks = Class.forName("android.view.View$AttachInfo$Callbacks");

            Context context = view.getContext();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Object window = Proxy.newProxyInstance(cIWindow.getClassLoader(), new Class[]{cIWindow}, (proxy, method, args) -> {
                if (method.getName().equals("asBinder")) {
                    return new Binder();
                }
                return null;
            });
            final Object viewRootImpl;
            final Class[] viewRootCtorParams;
            final Object[] viewRootCtorValues;

            if (Build.VERSION.SDK_INT >= 26) {
                viewRootImpl =
                        cViewRootImpl
                                .getConstructor(Context.class, Display.class)
                                .newInstance(context, display);

                viewRootCtorParams =
                        new Class[]{
                                cIWindowSession,
                                cIWindow,
                                Display.class,
                                cViewRootImpl,
                                Handler.class,
                                cICallbacks,
                                Context.class
                        };

                viewRootCtorValues =
                        new Object[]{
                                stub(cIWindowSession),
                                window,
                                display,
                                viewRootImpl,
                                handler,
                                stub(cICallbacks),
                                context
                        };
            } else if (Build.VERSION.SDK_INT >= 17) {
                viewRootImpl =
                        cViewRootImpl
                                .getConstructor(Context.class, Display.class)
                                .newInstance(context, display);

                viewRootCtorParams =
                        new Class[]{
                                cIWindowSession, cIWindow, Display.class, cViewRootImpl, Handler.class, cICallbacks
                        };

                viewRootCtorValues =
                        new Object[]{
                                stub(cIWindowSession), window, display, viewRootImpl, handler, stub(cICallbacks)
                        };
            } else if (Build.VERSION.SDK_INT >= 16) {
                viewRootImpl = cViewRootImpl.getConstructor(Context.class).newInstance(context);

                viewRootCtorParams =
                        new Class[]{cIWindowSession, cIWindow, cViewRootImpl, Handler.class, cICallbacks};

                viewRootCtorValues =
                        new Object[]{
                                stub(cIWindowSession), window, viewRootImpl, new Handler(), stub(cICallbacks)
                        };
            } else {
                viewRootCtorParams = new Class[]{cIWindowSession, cIWindow, Handler.class, cICallbacks};
                viewRootCtorValues =
                        new Object[]{stub(cIWindowSession), window, handler, stub(cICallbacks)};
            }

            Constructor cons = cAttachInfo.getDeclaredConstructor(viewRootCtorParams);
            cons.setAccessible(true);
            Object attachInfo = cons.newInstance(viewRootCtorValues);
            setField(attachInfo, "mHasWindowFocus", true);
            setField(attachInfo, "mWindowVisibility", View.VISIBLE);
            setField(attachInfo, "mInTouchMode", false);
            setField(attachInfo, "mRootView", rootLayout);

            if (Build.VERSION.SDK_INT >= 11) {
                setField(attachInfo, "mHardwareAccelerated", false);
            }
            return attachInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object stub(Class klass) {
        if (!klass.isInterface()) {
            throw new IllegalArgumentException("Cannot stub an non-interface");
        }
        return Proxy.newProxyInstance(klass.getClassLoader(), new Class[]{klass}, new InvocationHandler() {
            @Override
            public Object invoke(Object project, Method method, Object[] args) {
                if ("getCoverStateSwitch".equals(method.getName())) {
                    // needed for Samsung version of Android 8.0
                    return false;
                }
                return null;
            }
        });
    }

    private void setField(Object o, String fieldName, Object value) throws Exception {
        Class clazz = o.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(o, value);
    }


    static class ReflectionLimitUtils {
        //https://editor.csdn.net/md/?articleId=110383018
        private static Object sVMRuntime;
        private static Method setHiddenApiExemptions;
        private static boolean hasNoLimit = false;

        static {
            if (Build.VERSION.SDK_INT >= 29) {
                try {
                    Method forName = Class.class.getDeclaredMethod("forName", String.class);
                    Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
                    Class<?> vmRuntimeClass = (Class<?>) forName.invoke(null, "dalvik.system.VMRuntime");
                    Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", null);
                    setHiddenApiExemptions = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
                    setHiddenApiExemptions.setAccessible(true);
                    sVMRuntime = getRuntime.invoke(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //消除限制
        public static boolean clearLimit() {
            if (sVMRuntime == null || setHiddenApiExemptions == null || hasNoLimit) {
                return false;
            }
            try {
                setHiddenApiExemptions.invoke(sVMRuntime, new Object[]{new String[]{"L"}});
                hasNoLimit = true;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

    }


}
