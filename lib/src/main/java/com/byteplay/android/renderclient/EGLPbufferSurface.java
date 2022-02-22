package com.byteplay.android.renderclient;

import java.util.Objects;

public abstract class EGLPbufferSurface extends EGLSurface {

    private final int width;
    private final int height;

    public EGLPbufferSurface(GLRenderClient client, int width, int height) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EGLPbufferSurface)) return false;
        if (!super.equals(o)) return false;
        EGLPbufferSurface that = (EGLPbufferSurface) o;
        return width == that.width && height == that.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), width, height);
    }

    @Override
    protected void onSwapBuffers() {
        // not need to do

    }
}
