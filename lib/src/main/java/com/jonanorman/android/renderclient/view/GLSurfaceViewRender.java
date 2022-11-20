package com.jonanorman.android.renderclient.view;

import android.os.Handler;
import android.os.Message;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;

import com.jonanorman.android.renderclient.math.TimeStamp;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLRenderMessage;

import java.util.concurrent.atomic.AtomicBoolean;

public class GLSurfaceViewRender {


    private int renderId;
    private boolean windowFocus;
    private SurfaceView surfaceView;
    private GLRenderMessage renderMessage;
    private Surface renderSurface;
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

    private SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            renderMessage.post(new Runnable() {
                @Override
                public void run() {
                    renderSurface = holder.getSurface();
                }
            });
            requestRender();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            renderMessage.postAtFrontOfQueueAndWait(new Runnable() {
                @Override
                public void run() {
                    GLRenderClient.getCurrentClient().obtainWindowSurface(renderSurface).dispose();
                    renderSurface = null;
                    callFrameStop();
                }
            });
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

    public GLSurfaceViewRender(GLRenderMessage renderMessage, SurfaceView surfaceView) {
        this.renderMessage = renderMessage;
        this.surfaceView = surfaceView;
        this.windowFocus = surfaceView.hasWindowFocus();
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(surfaceHolderCallback);
        this.renderSurface = holder.getSurface();
        this.renderMessage.addHandlerCallback(callback);
        this.renderId = renderMessage.getAutoMessageId();
        this.surfaceView.getViewTreeObserver().addOnWindowFocusChangeListener(windowFocusChangeListener);
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
            this.surfaceView.getViewTreeObserver().removeOnWindowFocusChangeListener(windowFocusChangeListener);
            this.renderMessage.removeHandlerCallback(callback);
            this.surfaceView.getHolder().removeCallback(surfaceHolderCallback);
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

    public void setIntervalTime(TimeStamp intervalTime) {
        this.intervalTime = intervalTime;
    }

    public interface onFrameRenderCallback {

        void onFrameStart();

        void onFrameRender(Surface surface);

        void onFrameStop();

    }

}
