package com.jonanorman.android.renderclient.opengl;

import android.graphics.Bitmap;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;

public abstract class EGLSurface extends GLDispose {

    private GLFrameBuffer defaultFrameBuffer;
    private android.opengl.EGLSurface eglSurface;
    private boolean init = false;
    private final EGLDisplay eglDisplay;
    private final EGLConfig eglConfig;
    private final EGLContext eglContext;
    private GLRenderClient client;


    public EGLSurface(GLRenderClient client) {
        super(client);
        this.client = client;
        this.eglDisplay = client.getEGLDisplay();
        this.eglConfig = client.getEGLConfig();
        this.eglContext = client.getEGLContext();
    }

    public final void init() {
        checkDispose();
        if (init) {
            return;
        }
        init = true;
        this.defaultFrameBuffer = onCreateDefaultFrameBuffer();
        this.eglSurface = onCreateEGLSurface(eglDisplay, eglConfig);
        if (eglSurface == null) {
            throw new NullPointerException(this + " createEGLSurface fail");
        }
        client.checkEGLError();
    }

    public boolean isInit() {
        return init;
    }

    public boolean isAlive() {
        return isInit() && !isDisposed();
    }


    @Override
    protected void onDispose() {
        defaultFrameBuffer.dispose();
        onDestroyEGLSurface(eglDisplay, eglSurface);
        client.checkEGLError();
        if (client.getCurrentSurface() == this) client.setCurrentSurface(null);
    }


    final void makeCurrent() {
        checkDispose();
        this.client.checkRelease();
        EGLSurface currentEGLSurface = client.getCurrentSurface();
        if (this == currentEGLSurface) {
            return;
        }
        if (!onMakeCurrent(eglDisplay, eglSurface, eglContext)) {
            client.checkEGLError();
        }
        client.setCurrentSurface(this);
    }

    final void makeNoCurrent() {
        this.client.checkRelease();
        EGLSurface currentEGLSurface = client.getCurrentSurface();
        if (currentEGLSurface == null) {
            return;
        }
        if (!onMakeNoCurrent(eglDisplay)) {
            client.checkEGLError();
        }
        client.setCurrentSurface(null);
    }

    public final boolean swapBuffers() {
        makeCurrent();
        boolean success = onSwapBuffers(eglDisplay, eglSurface);
        if (!success) {
            client.checkEGLError();
        }
        return success;
    }


    public GLFrameBuffer getDefaultFrameBuffer() {
        return defaultFrameBuffer;
    }

    public final void readBitmap(Bitmap bitmap) {
        defaultFrameBuffer.readBitmap(bitmap);
    }

    public final void readBitmap(int x, int y, int width, int height, Bitmap bitmap) {
        defaultFrameBuffer.readBitmap(x, y, width, height, bitmap);
    }

    public final void clearColor(int color) {
        defaultFrameBuffer.clearColor(color);
    }

    public final void clearDepthBuffer() {
        defaultFrameBuffer.clearDepthBuffer();
    }


    public final android.opengl.EGLSurface getEGLSurface() {
        return eglSurface;
    }


    public abstract int getHeight();

    public abstract int getWidth();

    public final EGLConfig getEGLConfig() {
        return eglConfig;
    }

    public final EGLDisplay getEGLDisplay() {
        return eglDisplay;
    }

    public final EGLContext getEGLContext() {
        return eglContext;
    }

    protected abstract android.opengl.EGLSurface onCreateEGLSurface(EGLDisplay eglDisplay, EGLConfig eglConfig);

    protected abstract void onDestroyEGLSurface(EGLDisplay eglDisplay, android.opengl.EGLSurface eglSurface);


    protected abstract boolean onMakeCurrent(EGLDisplay eglDisplay, android.opengl.EGLSurface eglSurface, EGLContext eglContext);

    protected abstract boolean onMakeNoCurrent(EGLDisplay eglDisplay);

    protected abstract boolean onSwapBuffers(EGLDisplay eglDisplay, android.opengl.EGLSurface eglSurface);

    protected abstract GLFrameBuffer onCreateDefaultFrameBuffer();
}
