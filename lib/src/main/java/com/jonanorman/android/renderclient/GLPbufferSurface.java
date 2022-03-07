package com.jonanorman.android.renderclient;

import android.opengl.EGLSurface;

public abstract class GLPbufferSurface extends GLRenderSurface {

    private final int width;
    private final int height;
    protected EGLSurface eglSurface;

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
    protected EGLSurface getEGLSurface() {
        return eglSurface;
    }
}
