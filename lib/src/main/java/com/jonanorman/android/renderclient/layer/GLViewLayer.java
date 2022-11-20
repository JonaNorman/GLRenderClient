package com.jonanorman.android.renderclient.layer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLShaderParam;
import com.jonanorman.android.renderclient.opengl.GLTexture;
import com.jonanorman.android.renderclient.opengl.gl20.GL20Texture;

/**
 * GLViewLayer  not exist viewRootImpl, some functions cannot be used,for example, post, animation
 * If you just want to add special effects to the view,you can use {@link com.jonanorman.android.renderclient.view.GLEffectLayout}
 */
public class GLViewLayer extends GLTextureLayer {


    private final FrameLayout rootLayout;

    private GLTexture viewTexture;
    private SurfaceTexture surfaceTexture;
    private Surface surface;
    private Context context;
    private Paint clearPaint;


    public GLViewLayer(Context context) {
        this(context, 0);
    }

    public GLViewLayer(Context context, int style) {
        super(true);
        this.context = new ContextThemeWrapper(context.getApplicationContext(), style);
        this.rootLayout = new FrameLayout(this.context);
        this.clearPaint = new Paint();
        this.clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }


    public Context getContext() {
        return context;
    }


    @Override
    protected void onRenderInit(GLRenderClient client) {
        super.onRenderInit(client);
        viewTexture = new GL20Texture(client, GLTexture.Type.TEXTURE_OES);
        surfaceTexture = new SurfaceTexture(viewTexture.getTextureId());
        surface = new Surface(surfaceTexture);
        super.setTexture(viewTexture);
    }

    @Override
    protected void onRenderClean(GLRenderClient client) {
        super.onRenderClean(client);
        viewTexture.dispose();
        surface.release();
        surfaceTexture.release();
    }

    @Override
    protected void onRenderLayerParam(GLFrameBuffer inputBuffer, GLShaderParam shaderParam) {
        int renderWidth = Math.round(getRenderWidth());
        int renderHeight = Math.round(getRenderHeight());
        viewTexture.setWidth(renderWidth);
        viewTexture.setHeight(renderHeight);
        surfaceTexture.setDefaultBufferSize(renderWidth, renderHeight);
        renderView(renderWidth, renderHeight);
        surfaceTexture.getTransformMatrix(viewTexture.getTextureMatrix().get());
        super.onRenderLayerParam(inputBuffer, shaderParam);
    }


    public void addView(View view) {
        rootLayout.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
        dispatchMeasureAndLayout(viewWidth, viewHeight);
        dispatchOnGlobalLayout(rootLayout);
        dispatchPreDraw(rootLayout);
        drawRootLayout(rootLayout);
        dispatchOnDraw(rootLayout);
    }


    private void dispatchMeasureAndLayout(int viewWidth, int viewHeight) {
        rootLayout.measure(
                View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY));
        do {
            rootLayout.layout(0, 0, rootLayout.getMeasuredWidth(), rootLayout.getMeasuredHeight());
        } while (rootLayout.isLayoutRequested());
    }

    public FrameLayout getRootLayout() {
        return rootLayout;
    }

    @Override
    protected boolean dispatchTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        if (!isEnable()) {
            return false;
        }
        if (rootLayout.dispatchTouchEvent(event)) {
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

    private void drawRootLayout(View view) {
        Canvas canvas = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                canvas = surface.lockHardwareCanvas();
            } else {
                canvas = surface.lockCanvas(null);
            }
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), clearPaint);
            view.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                surface.unlockCanvasAndPost(canvas);
            }
        }
        surfaceTexture.updateTexImage();
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


}
