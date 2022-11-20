package com.jonanorman.android.renderclient.opengl;

public abstract class EGLPbufferSurface extends EGLSurface {

    private final int width;
    private final int height;

    public EGLPbufferSurface(GLRenderClient client, int width, int height) {
        super(client);
        this.width = width;
        this.height = height;
        if (width <= 0) {
            throw new IllegalArgumentException(this + " width <=0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException(this + "height <=0");
        }
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }
}
