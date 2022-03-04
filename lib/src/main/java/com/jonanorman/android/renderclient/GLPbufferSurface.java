package com.jonanorman.android.renderclient;

import android.opengl.EGLSurface;

import java.util.Objects;

public class GLPbufferSurface extends GLRenderSurface {

    private final int width;
    private final int height;
    private EGLSurface eglSurface;

    public GLPbufferSurface(GLRenderClient client, int width, int height) {
        super(client);
        if (width <= 0) {
            throw new IllegalArgumentException("width <=0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height <=0");
        }
        this.width = width;
        this.height = height;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    protected void onDispose() {
        client.destroy(this);
    }

    @Override
    protected void onCreate() {
        eglSurface = client.create(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GLPbufferSurface)) return false;
        GLPbufferSurface that = (GLPbufferSurface) o;
        return Objects.equals(eglSurface, that.eglSurface)
                && Objects.equals(width, that.width)
                && Objects.equals(height, that.height);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, eglSurface);
    }

    @Override
    protected EGLSurface getEGLSurface() {
        return eglSurface;
    }
}
