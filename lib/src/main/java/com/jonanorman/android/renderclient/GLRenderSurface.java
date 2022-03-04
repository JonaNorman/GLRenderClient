package com.jonanorman.android.renderclient;

import android.graphics.Bitmap;

public abstract class GLRenderSurface {
    private boolean disposed;
    private boolean created;
    protected final GLRenderClient client;
    private final GLFrameBuffer defaultFrameBuffer;

    public GLRenderSurface(GLRenderClient client) {
        this.client = client;
        this.defaultFrameBuffer = client.newFrameBuffer(this);
    }

    public final void create() {
        client.createEGLSurface(this);
    }

    public final void dispose() {
        client.disposeEGLSurface(this);
    }


    protected final void createSurface() {
        if (created || disposed) {
            return;
        }
        created = true;
        onCreate();
    }

    protected final void disposeSurface() {
        if (disposed) {
            return;
        }
        if (!created) {
            disposed = true;
            created = false;
            return;
        }
        disposed = true;
        created = false;
        onDispose();
    }

    public final boolean isCreated() {
        return created;
    }

    public final boolean isDisposed() {
        return disposed;
    }

    protected abstract void onCreate();

    protected abstract void onDispose();

    public GLRenderClient getClient() {
        return client;
    }

    public abstract int getHeight();


    public abstract int getWidth();

    protected abstract android.opengl.EGLSurface getEGLSurface();


    public final void makeCurrent() {
        create();
        if (disposed) {
            throw new IllegalStateException(getClass() + "it is disposed");
        }
        client.makeCurrent(this);
    }

    public final void makeNoCurrent() {
        client.makeCurrent(null);
    }

    public final void readBitmap(Bitmap bitmap) {
        defaultFrameBuffer.readBitmap(bitmap);
    }

    public final void readBitmap(int x, int y, int width, int height, Bitmap bitmap) {
        defaultFrameBuffer.readBitmap(x, y, width, height, bitmap);
    }

    public GLFrameBuffer getDefaultFrameBuffer() {
        return defaultFrameBuffer;
    }


    public final void clearColor(int color) {
        defaultFrameBuffer.clearColor(color);
    }

    public final void swapBuffers() {
        client.swapBuffers(this);
    }

}
