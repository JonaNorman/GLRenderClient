package com.byteplay.android.renderclient;

import android.graphics.Bitmap;

import java.util.Objects;

public abstract class EGLSurface {
    private boolean disposed;
    private boolean created;
    protected final GLRenderClient client;
    private final GLFrameBuffer defaultFrameBuffer;

    public EGLSurface(GLRenderClient client) {
        this.client = client;
        this.defaultFrameBuffer = client.newFrameBuffer(this);
    }

    public final void create() {
        client.createEGLSurface(this);
    }

    public final void dispose() {
        client.disposeEGLSurface(this);
    }


    protected void createSurface() {
        if (created) {
            return;
        }
        if (disposed) {
            throw new IllegalStateException(getClass() + "it is disposed");
        }
        created = true;
        onCreate();
    }

    protected void disposeSurface() {
        if (!created) {
            disposed = true;
            created = false;
            return;
        }
        if (disposed) {
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

    public abstract android.opengl.EGLSurface getEGLSurface();

    protected abstract void onSwapBuffers();

    public final void makeCurrent() {
        create();
        client.makeCurrentEGLSurface(this);
    }

    public final void makeNoCurrent() {
        client.makeCurrentEGLSurface(null);
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
        create();
        onSwapBuffers();
    }


    @Override
    public int hashCode() {
        return Objects.hash(disposed, created, client);
    }
}
