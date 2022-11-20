package com.jonanorman.android.renderclient.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jonanorman.android.renderclient.layer.GLEffect;
import com.jonanorman.android.renderclient.layer.GLEffectGroup;
import com.jonanorman.android.renderclient.layer.GLSurfaceTextureLayer;
import com.jonanorman.android.renderclient.math.Status;
import com.jonanorman.android.renderclient.math.TimeStamp;
import com.jonanorman.android.renderclient.opengl.GLRenderMessage;

public class GLEffectLayout extends FrameLayout {

    private GLRenderMessage renderMessage;
    private GLSurfaceTextureLayer renderLayer;
    private SurfaceTexture surfaceTexture;
    private Surface surface;
    private int surfaceWidth;
    private int surfaceHeight;
    private GLEffectGroup renderEffectGroup = new GLEffectGroup();
    private Runnable renderRunnable;
    private TimeStamp renderTime = TimeStamp.ofMills(0);
    private long renderStartTime;
    private boolean renderDirty;


    private TextureView targetTextureView;
    private ViewTreeObserver.OnPreDrawListener dirtyUpdateListener;
    private TextureView.SurfaceTextureListener prepareTextureViewListener;
    private Status prepareTextureViewStatus;
    private Status prepareRenderStatus;
    private Paint clearPaint = new Paint();
    private boolean willNotDraw = true;
    private volatile boolean refreshMode;

    public GLEffectLayout(@NonNull Context context) {
        super(context);
        initView();
    }

    public GLEffectLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public GLEffectLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public GLEffectLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initTextureView() {
        targetTextureView = new TextureView(getContext());
        targetTextureView.setOpaque(false);
        addView(targetTextureView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    private void initView() {
        initTextureView();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        renderRunnable = () -> {
            if (!prepareTextureViewStatus.waitDone()) {
                return;
            }
            if (renderStartTime == 0) {
                renderStartTime = System.currentTimeMillis();
            }
            renderTime.setDuration(System.currentTimeMillis() - renderStartTime);
            renderLayer.render(targetTextureView.getSurfaceTexture(), renderTime);
        };
        dirtyUpdateListener = () -> {
            if (renderDirty || refreshMode) {
                invalidate();
            }
            return true;
        };
        prepareTextureViewListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                prepareTextureViewStatus.done();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                renderMessage.postAtFrontOfQueueAndWait(new Runnable() {
                    @Override
                    public void run() {
                        prepareTextureViewStatus.cancel();
                        renderMessage.removeCallbacks(renderRunnable);
                    }
                });
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        };
        targetTextureView.setSurfaceTextureListener(prepareTextureViewListener);

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(dirtyUpdateListener);
        surfaceTexture = new SurfaceTexture(0);
        surface = new Surface(surfaceTexture);
        renderMessage = GLRenderMessage.obtain();
        renderLayer = new GLSurfaceTextureLayer(surfaceTexture);
        renderLayer.addEffect(renderEffectGroup);
        prepareTextureViewStatus = new Status();
        prepareRenderStatus = new Status();
        renderMessage.post(new Runnable() {
            @Override
            public void run() {
                renderLayer.init();
                prepareRenderStatus.done();
            }
        });
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ViewTreeObserver observer = getViewTreeObserver();
        observer.removeOnPreDrawListener(dirtyUpdateListener);
        prepareTextureViewStatus.cancel();
        prepareRenderStatus.cancel();
        renderMessage.recycleAndWait();
        surface.release();
        surfaceTexture.release();
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child == targetTextureView) {
            return false;
        }
        return super.drawChild(canvas, child, drawingTime);
    }


    @Override
    public void draw(Canvas canvas) {
        if (!willNotDraw) {
            render(canvas);
        } else {
            super.draw(canvas);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (willNotDraw) {
            render(canvas);
        } else {
            super.dispatchDraw(canvas);
        }
    }

    private void render(Canvas canvas) {
        renderDirty = false;
        if (!prepare(canvas)) {
            finalDraw(canvas);
            return;
        }
        drawRenderSurface();
        renderMessage.post(renderRunnable);
        targetTextureView.draw(canvas);
    }

    private void finalDraw(Canvas canvas) {
        if (willNotDraw) {
            super.dispatchDraw(canvas);
        } else {
            super.draw(canvas);
        }
    }

    private boolean prepare(Canvas canvas) {
        if (targetTextureView.getSurfaceTexture() == null) {
            return false;
        }
        if (!prepareRenderStatus.waitDone()) {
            return false;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        if (surfaceWidth != width || surfaceHeight != height) {
            surfaceTexture.setDefaultBufferSize(width, height);
            renderLayer.setSurfaceWidth(width);
            renderLayer.setSurfaceHeight(height);
            surfaceWidth = width;
            surfaceHeight = height;
        }
        return true;
    }

    private void drawRenderSurface() {
        Canvas canvas = surface.lockCanvas(null);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), clearPaint);
        finalDraw(canvas);
        surface.unlockCanvasAndPost(canvas);
    }

    @Override
    public void onDescendantInvalidated(@NonNull View child, @NonNull View target) {
        super.onDescendantInvalidated(child, target);
        renderDirty |= child != targetTextureView;
    }

    @Override
    public void setWillNotDraw(boolean willNotDraw) {
        super.setWillNotDraw(willNotDraw);
        this.willNotDraw = willNotDraw;
    }

    public void addEffect(GLEffect effect) {
        renderEffectGroup.add(effect);
    }

    public void removeEffect(GLEffect effect) {
        renderEffectGroup.remove(effect);
    }

    public GLEffectGroup getEffectGroup() {
        return renderEffectGroup;
    }


    public void enableRefreshMode() {
        this.refreshMode = true;
    }

    public void disableRefreshMode() {
        this.refreshMode = false;
    }
}
