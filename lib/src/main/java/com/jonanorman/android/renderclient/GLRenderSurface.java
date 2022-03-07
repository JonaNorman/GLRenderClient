package com.jonanorman.android.renderclient;

import android.graphics.Bitmap;

public abstract class GLRenderSurface implements GLRenderClientReleaseListener{
    private boolean disposed;
    private boolean created;
    protected final GLRenderClient client;
    private final GLFrameBuffer defaultFrameBuffer;
    private long timeNs;

    public GLRenderSurface(GLRenderClient client) {
        this.client = client;
        this.defaultFrameBuffer = client.newFrameBuffer(this);
        client.addClientReleaseListener(this);
    }

    public final void create() {
        if (created || disposed) {
            return;
        }
        created = true;
        onCreate();
    }

    public final void dispose() {
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
        if (client.getCurrentRenderSurface() == this) {
            client.setCurrentRenderSurface(null);
        }
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
        onMakeCurrent(this);
    }

    public final void makeNoCurrent() {
        create();
        if (disposed) {
            throw new IllegalStateException(getClass() + "it is disposed");
        }
        onMakeCurrent(null);
    }


    protected abstract void onMakeCurrent(GLRenderSurface renderSurface);

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
        if (!isDisposed()) {
            create();
        }
        makeCurrent();
        onSwapBuffers();
    }

    public void setTime(long timeNs) {
        this.timeNs = timeNs;
    }

    public long getTime() {
        return timeNs;
    }

    protected abstract void onSwapBuffers();

    @Override
    public void onClientRelease(GLRenderClient renderClient) {
        dispose();
    }
}
