package com.jonanorman.android.renderclient.opengl;

import android.graphics.SurfaceTexture;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.GLException;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jonanorman.android.renderclient.opengl.gl20.GL20;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public abstract class GLRenderClient {


    private static ThreadLocal<GLRenderClient> THREAD_LOCAL_RENDER_CLIENT = new ThreadLocal();
    private final Map<Object, EGLWindowSurface> windowSurfaceCache;
    private final List<OnClientReleaseListener> clientReleaseListenerList;
    private final List<OnClientAttachThreadListener> clientAttachListenerList;
    private final List<OnClientDetachThreadListener> clientDetachListenerList;
    private final List<OnClientErrorListener> errorListenerList;
    private final Map<String, Object> extraParamMap;
    private final int eglContextClientVersion;
    private final EGLDisplay eglDisplay;
    private final android.opengl.EGLConfig eglConfig;
    private final EGLContext eglContext;
    private final EGLConfig chooseEGLConfig;
    private final int majorEGLVersion;
    private final int minorEGLVersion;
    private final GL gl;
    private final EGLPbufferSurface defaultPbufferSurface;
    private final GLTexture defaultTexture;
    private Thread attachThread;
    private boolean release;
    private GLFrameBuffer currentFrameBuffer;
    private EGLSurface currentRenderSurface;
    private int currentTextureUnit;
    private GLTexture currentTexture;
    private boolean checkRender = true;
    private boolean checkEGLError = true;
    private boolean throwError = true;
    private boolean checkGLError = true;

    private final GL.GLMonitor checkMonitor = new GL.GLMonitor() {
        boolean onCheckError = false;

        @Override
        public void onGLCall() {
            if (onCheckError) {
                return;
            }
            checkRender();
            if (checkGLError) {
                onCheckError = true;
                GL20 gl20 = getGL();
                while (gl20.glGetError() != GL20.GL_NO_ERROR) ;
                onCheckError = false;
            }
        }

        @Override
        public void onGLError(GLException exception) {
            StackTraceElement[] cloneStackTraceElement = exception.getStackTrace();
            StackTraceElement[] stackTraceElement = new StackTraceElement[cloneStackTraceElement.length - 2];
            System.arraycopy(cloneStackTraceElement, 2, stackTraceElement, 0, stackTraceElement.length);
            exception.setStackTrace(stackTraceElement);
            onNotifyGLError(exception);
        }
    };

    public GLRenderClient(EGLContext shareContext, EGLConfigChooser configChooser) {
        clientReleaseListenerList = new ArrayList<>();
        clientAttachListenerList = new ArrayList<>();
        clientDetachListenerList = new ArrayList<>();
        errorListenerList = new ArrayList<>();
        windowSurfaceCache = new HashMap<>();
        extraParamMap = new ConcurrentHashMap<>();
        eglDisplay = onCreateEGLDisplay();
        if (eglDisplay == null) {
            throw new NullPointerException(this + "createEGLDisplay fail");
        }
        checkEGLError();
        int[] eglVersion = onInitEGLDisplay(eglDisplay);
        if (eglVersion == null) {
            throw new NullPointerException(this + " initEGLDisplay fail");
        }
        checkEGLError();
        majorEGLVersion = eglVersion[0];
        minorEGLVersion = eglVersion[1];
        int maxConfigNum = onGetMaxEGLConfigNum(eglDisplay);
        checkEGLError();
        if (maxConfigNum <= 0) {
            throw new IllegalArgumentException(this + " getMaxEGLConfigNum fail");
        }
        android.opengl.EGLConfig[] openglEGLConfigs = onGetAllEGLConfig(eglDisplay, maxConfigNum);
        if (openglEGLConfigs == null || openglEGLConfigs.length == 0) {
            throw new IllegalArgumentException(this + " getAllEGLConfig fail");
        }
        checkEGLError();
        EGLConfig[] eglConfigs = new EGLConfig[openglEGLConfigs.length];
        for (int i = 0; i < openglEGLConfigs.length; i++) {
            eglConfigs[i] = onCreateEGLConfig(eglDisplay, openglEGLConfigs[i]);
        }
        checkEGLError();
        EGLConfig chooseConfig = configChooser.chooseConfig(eglConfigs);
        if (chooseConfig == null) {
            throw new NullPointerException(this + " chooseConfig fail");
        }
        chooseEGLConfig = chooseConfig;
        eglConfig = chooseConfig.getEGLConfig();
        eglContextClientVersion = chooseConfig.isRenderGL30() ? 3 : (chooseConfig.isRenderGL20() ? 2 : 1);
        eglContext = onCreateEGLContext(eglDisplay, eglConfig, shareContext, eglContextClientVersion);
        if (eglContext == null) {
            throw new RuntimeException(this + " createEGLContext fail");
        }
        checkEGLError();
        gl = onCreateGL();
        gl.addMonitor(checkMonitor);
        defaultPbufferSurface = onCreateDefaultPBufferSurface();
        defaultTexture = onCreateDefaultTexture();
    }


    public final EGLContext getEGLContext() {
        return eglContext;
    }


    public final android.opengl.EGLConfig getEGLConfig() {
        return eglConfig;
    }

    public final EGLDisplay getEGLDisplay() {
        return eglDisplay;
    }

    public final EGLConfig getChooseEGLConfig() {
        return chooseEGLConfig;
    }

    public final int getMajorEGLVersion() {
        return majorEGLVersion;
    }

    public final int getMinorEGLVersion() {
        return minorEGLVersion;
    }

    public final int getEglContextClientVersion() {
        return eglContextClientVersion;
    }

    public final <T extends GL> T getGL() {
        return (T) gl;
    }

    public final void setCheckEGLError(boolean checkEGLError) {
        this.checkEGLError = checkEGLError;
    }

    final EGLPbufferSurface getDefaultPBufferSurface() {
        return defaultPbufferSurface;
    }

    final void checkEGLError() {
        if (!checkEGLError) {
            return;
        }
        while (true) {
            String message = getEGLErrorMessage();
            if (message == null) {
                break;
            }
            RuntimeException exception = new RuntimeException(message);
            onNotifyGLError(exception);
        }
    }

    final boolean hasEGLError(String error) {
        while (true) {
            String message = getEGLErrorMessage();
            if (message == null) {
                break;
            }
            if (message.equals(error)) {
                return true;
            }
            RuntimeException exception = new RuntimeException(message);
            onNotifyGLError(exception);
        }
        return false;
    }

    public static GLRenderClient getCurrentClient() {
        return THREAD_LOCAL_RENDER_CLIENT.get();
    }

    public final void attachCurrentThread() {
        checkRelease();
        Thread currentThread = Thread.currentThread();
        if (currentThread.equals(attachThread)) {
            return;
        }
        GLRenderClient preClient = getCurrentClient();
        if (preClient != null && preClient != this) {
            preClient.detachCurrentThread();
//            throw new IllegalStateException(preClient + " " + "please first detach");
        }
        attachThread = currentThread;
        THREAD_LOCAL_RENDER_CLIENT.set(this);
        defaultPbufferSurface.makeCurrent();
        GLFrameBuffer frameBuffer = defaultPbufferSurface.getDefaultFrameBuffer();
        frameBuffer.bind();
        GLTexture texture = defaultTexture;
        texture.active(0);
        texture.bind();
        for (OnClientAttachThreadListener attachThreadListener : clientAttachListenerList) {
            attachThreadListener.onAttachThread(this);
        }
    }

    public final void detachCurrentThread() {
        checkRelease();
        if (attachThread == null) {
            return;
        }
        Thread currentThread = Thread.currentThread();
        if (!currentThread.equals(attachThread)) {
            throw new IllegalThreadStateException(this + " detach must in attached thread");
        }
        for (OnClientDetachThreadListener detachThreadListener : clientDetachListenerList) {
            detachThreadListener.onDetachThread(this);
        }
        defaultPbufferSurface.makeNoCurrent();
        THREAD_LOCAL_RENDER_CLIENT.set(null);
        currentRenderSurface = null;
        currentFrameBuffer = null;
        currentTextureUnit = 0;
        currentTexture = null;
        attachThread = null;
    }


    public final boolean isAttached() {
        return attachThread != null;
    }

    public final Thread getAttachThread() {
        return attachThread;
    }

    public final boolean isCurrentThread() {
        return Thread.currentThread() == attachThread;
    }

    public final void release() {
        if (release) {
            return;
        }
        attachCurrentThread();
        while (!clientReleaseListenerList.isEmpty()) {
            OnClientReleaseListener releaseListener = clientReleaseListenerList.remove(0);
            releaseListener.onClientRelease(this);
        }
        detachCurrentThread();
        onReleaseEGLContext(eglDisplay, eglConfig, eglContext);
        release = true;
    }


    public final boolean isRelease() {
        return release;
    }

    public final void checkRelease() {
        if (isRelease()) {
            throw new IllegalStateException(this + " is released");
        }
    }

    final void checkRender() {
        if (!checkRender) {
            return;
        }
        checkRelease();
        if (!isAttached()) {
            throw new IllegalStateException(this + " is not attached");
        }
        Thread currentThread = Thread.currentThread();
        if (!currentThread.equals(getAttachThread())) {
            throw new IllegalStateException(this + " must render in " + getAttachThread());
        }
    }


    public void addClientReleaseListener(OnClientReleaseListener releaseListener) {
        if (releaseListener == null || clientReleaseListenerList.contains(releaseListener)) {
            return;
        }
        clientReleaseListenerList.add(releaseListener);
    }

    public void removeClientReleaseListener(OnClientReleaseListener releaseListener) {
        if (releaseListener == null || !clientReleaseListenerList.contains(releaseListener)) {
            return;
        }
        clientReleaseListenerList.remove(releaseListener);
    }

    public void addClientAttachThreadListener(OnClientAttachThreadListener attachThreadListener) {
        if (attachThreadListener == null || clientAttachListenerList.contains(attachThreadListener)) {
            return;
        }
        clientAttachListenerList.add(attachThreadListener);
    }

    public void removeClientAttachThreadListener(OnClientAttachThreadListener attachThreadListener) {
        if (attachThreadListener == null || !clientAttachListenerList.contains(attachThreadListener)) {
            return;
        }
        clientAttachListenerList.remove(attachThreadListener);
    }

    public void addClientDetachThreadListener(OnClientDetachThreadListener detachThreadListener) {
        if (detachThreadListener == null || clientDetachListenerList.contains(detachThreadListener)) {
            return;
        }
        clientDetachListenerList.add(detachThreadListener);
    }

    public void removeClientDetachThreadListener(OnClientDetachThreadListener clientDetachThreadListener) {
        if (clientDetachThreadListener == null || !clientDetachListenerList.contains(clientDetachThreadListener)) {
            return;
        }
        clientDetachListenerList.remove(clientDetachThreadListener);
    }


    public final void addGLErrorListener(OnClientErrorListener listener) {
        if (listener == null || errorListenerList.contains(listener)) {
            return;
        }
        errorListenerList.add(listener);
    }


    public final void removeGLErrorListener(OnClientErrorListener listener) {
        if (listener == null || !errorListenerList.contains(listener)) {
            return;
        }
        errorListenerList.remove(listener);
    }

    public final void setThrowError(boolean throwError) {
        this.throwError = throwError;
    }

    public final void setCheckGLError(boolean checkGLError) {
        this.checkGLError = checkGLError;
    }


    public final void setCheckRender(boolean checkRender) {
        this.checkRender = checkRender;
    }

    private void onNotifyGLError(Exception exception) {
        if (throwError) {
            RuntimeException runtimeException = new RuntimeException(exception.getMessage(), exception.getCause());
            runtimeException.setStackTrace(exception.getStackTrace());
            throw runtimeException;
        }
        for (OnClientErrorListener listener : errorListenerList) {
            listener.onClientError(exception);
        }
    }


    public final GLFrameBuffer getCurrentFrameBuffer() {
        return currentFrameBuffer;
    }

    void setCurrentFrameBuffer(GLFrameBuffer currentFrameBuffer) {
        this.currentFrameBuffer = currentFrameBuffer;
    }


    public final EGLSurface getCurrentSurface() {
        return currentRenderSurface;
    }

    void setCurrentSurface(EGLSurface currentRenderSurface) {
        this.currentRenderSurface = currentRenderSurface;
    }

    public GLTexture getCurrentTexture() {
        return currentTexture;
    }

    public int getCurrentTextureUnit() {
        return currentTextureUnit;
    }

    void setCurrentTexture(GLTexture currentTexture) {
        this.currentTexture = currentTexture;
    }

    void setCurrentTextureUnit(int currentTextureUnit) {
        this.currentTextureUnit = currentTextureUnit;
    }

    public final EGLWindowSurface obtainWindowSurface(Surface surface) {
        return obtainGLWindowSurface(surface);
    }


    public final EGLWindowSurface obtainWindowSurface(SurfaceHolder surface) {
        return obtainGLWindowSurface(surface);
    }


    public final EGLWindowSurface obtainWindowSurface(SurfaceTexture surface) {
        return obtainGLWindowSurface(surface);
    }

    private EGLWindowSurface obtainGLWindowSurface(Object object) {
        EGLWindowSurface windowSurface = windowSurfaceCache.get(object);
        if (windowSurface != null) {
            return windowSurface;
        }
        windowSurface = onCreateEGLWindowSurface(object);
        windowSurface.addOnDisposeListener(new GLDispose.OnDisposeListener() {
            @Override
            public void onDispose(GLDispose dispose) {
                windowSurfaceCache.remove(object);
            }
        });
        windowSurfaceCache.put(object, windowSurface);
        return windowSurface;
    }


    public <T extends Object> T getExtraParam(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        return (T) extraParamMap.get(key);
    }


    public <T extends Object> T putExtraParam(@NonNull String key, @Nullable T value) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        T old = (T) extraParamMap.put(key, value);
        return old;
    }

    public Object removeExtraParam(@Nullable String key) {
        return extraParamMap.remove(key);
    }


    public boolean containsExtraParam(@NonNull String key) {
        return extraParamMap.containsKey(key);
    }


    public boolean containsExtraParam(@Nullable Object value) {
        return extraParamMap.containsValue(value);
    }


    protected abstract EGLContext onCreateEGLContext(EGLDisplay eglDisplay, android.opengl.EGLConfig chooseConfig, EGLContext shareContext, int glVersion);

    protected abstract android.opengl.EGLConfig[] onGetAllEGLConfig(EGLDisplay eglDisplay, int maxConfigNum);

    protected abstract EGLConfig onCreateEGLConfig(EGLDisplay eglDisplay, android.opengl.EGLConfig config);

    protected abstract int onGetMaxEGLConfigNum(EGLDisplay eglDisplay);

    protected abstract int[] onInitEGLDisplay(EGLDisplay eglDisplay);

    protected abstract EGLDisplay onCreateEGLDisplay();

    protected abstract GL onCreateGL();

    protected abstract EGLPbufferSurface onCreateDefaultPBufferSurface();

    protected abstract GLTexture onCreateDefaultTexture();

    protected abstract String getEGLErrorMessage();


    protected abstract void onReleaseEGLContext(EGLDisplay eglDisplay, android.opengl.EGLConfig eglConfig, EGLContext eglContext);

    protected abstract EGLContext onGetCurrentEGLContext();

    protected abstract EGLWindowSurface onCreateEGLWindowSurface(Object surface);


    public interface OnClientReleaseListener {
        void onClientRelease(GLRenderClient renderClient);
    }

    public interface OnClientAttachThreadListener {
        void onAttachThread(GLRenderClient renderClient);
    }

    public interface OnClientDetachThreadListener {
        void onDetachThread(GLRenderClient renderClient);
    }

    public interface OnClientErrorListener {
        void onClientError(Exception e);
    }

    public interface Factory {
        void setShareEGLContext(EGLContext context);

        void setEGLConfigChooser(EGLConfigChooser configChooser);

        GLRenderClient create();
    }
}
