package com.jonanorman.android.renderclient.view;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;
import android.view.TextureView;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;

import com.jonanorman.android.renderclient.math.TimeStamp;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLRenderMessage;

import java.util.concurrent.atomic.AtomicBoolean;

public class GLTextureViewRender {


    private int renderId;
    private boolean windowFocus;
    private TextureView textureView;
    private GLRenderMessage renderMessage;
    private SurfaceTexture renderSurface;
    private AtomicBoolean start;
    private AtomicBoolean release;

    private AtomicBoolean frameStart;
    private volatile onFrameRenderCallback frameRenderCallback;

    private volatile TimeStamp intervalTime =TimeStamp.ofMills(30);


    private ViewTreeObserver.OnWindowFocusChangeListener windowFocusChangeListener = new ViewTreeObserver.OnWindowFocusChangeListener() {
        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            if (hasFocus) {
                renderMessage.post(new Runnable() {
                    @Override
                    public void run() {
                        windowFocus = true;
                    }
                });
                requestRender();
            } else {
                renderMessage.postAtFrontOfQueueAndWait(new Runnable() {
                    @Override
                    public void run() {
                        windowFocus = false;
                        callFrameStop();
                    }
                });
            }
        }
    };

    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            renderMessage.post(new Runnable() {
                @Override
                public void run() {
                    renderSurface = surface;

                }
            });
            requestRender();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            renderMessage.postAtFrontOfQueueAndWait(new Runnable() {
                @Override
                public void run() {
                    GLRenderClient.getCurrentClient().obtainWindowSurface(renderSurface).dispose();
                    renderSurface = null;
                    callFrameStop();
                }
            });
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int what = msg.what;
            if (what != renderId || isRelease() && !start.get()) {
                return false;
            }
            if (windowFocus && renderSurface != null) {
                callFrameStart();
                if (frameRenderCallback != null) {
                    frameRenderCallback.onFrameRender(renderSurface);
                }
                renderMessage.removeMessages(renderId);
                renderMessage.sendEmptyMessageDelayed(renderId, intervalTime.toMillis());
            }
            return true;
        }
    };

    public GLTextureViewRender(GLRenderMessage renderMessage, TextureView textureView) {
        this.renderMessage = renderMessage;
        this.textureView = textureView;
        this.windowFocus = textureView.hasWindowFocus();
        this.renderSurface = textureView.getSurfaceTexture();
        this.renderMessage.addHandlerCallback(callback);
        this.renderId = renderMessage.getAutoMessageId();
        this.textureView.getViewTreeObserver().addOnWindowFocusChangeListener(windowFocusChangeListener);
        this.textureView.setSurfaceTextureListener(surfaceTextureListener);
        this.release = new AtomicBoolean();
        this.start = new AtomicBoolean();
        this.frameStart = new AtomicBoolean();
    }

    public void release() {
        if (!release.getAndSet(true)) {
            this.renderMessage.postAtFrontOfQueueAndWait(new Runnable() {
                @Override
                public void run() {
                    renderMessage.removeMessages(renderId);
                    callFrameStop();
                }
            });
            this.textureView.getViewTreeObserver().removeOnWindowFocusChangeListener(windowFocusChangeListener);
            this.renderMessage.removeHandlerCallback(callback);
            this.textureView.setSurfaceTextureListener(null);
        }
    }

    public boolean isRelease() {
        return release.get();
    }


    public void start() {
        if (isRelease()) {
            return;
        }
        if (!start.getAndSet(true)) {
            requestRender();
        }
    }

    private void requestRender() {
        Message message = Message.obtain();
        message.what = renderId;
        renderMessage.sendMessage(message);
    }

    public void stop() {
        if (isRelease()) {
            return;
        }
        if (start.getAndSet(false)) {
            renderMessage.removeMessages(renderId);
            renderMessage.post(new Runnable() {
                @Override
                public void run() {
                    callFrameStop();
                }
            });
        }

    }

    public void setIntervalTime(TimeStamp intervalTime) {
        this.intervalTime = intervalTime;
    }

    public void setFrameRenderCallback(onFrameRenderCallback frameRenderCallback) {
        this.frameRenderCallback = frameRenderCallback;
    }

    private void callFrameStart() {

        if (!frameStart.getAndSet(true)) {
            if (frameRenderCallback != null) {
                frameRenderCallback.onFrameStart();
            }
        }
    }

    private void callFrameStop() {
        if (frameStart.getAndSet(false)) {
            if (frameRenderCallback != null) {
                frameRenderCallback.onFrameStop();
            }
        }
    }

    public interface onFrameRenderCallback {

        void onFrameStart();

        void onFrameRender(SurfaceTexture surfaceTexture);

        void onFrameStop();


    }

}
