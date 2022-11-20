package com.jonanorman.android.renderclient.opengl.gl20;

import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLViewPort;

public class GL20ViewPort extends GLViewPort {


    private GL20 gl20;

    public GL20ViewPort(GLRenderClient renderClient) {
        super(renderClient);
        gl20 = getGL();
    }

    @Override
    protected void onViewPort(int x, int y, int width, int height) {
        gl20.glViewport(x, y, width, height);
    }

    @Override
    public String toString() {
        return "GL20ViewPort[" +
                "x=" + getX() +
                ", y=" + getY() +
                ", width=" + getWidth() +
                ", height=" + getHeight() +
                ']';
    }
}
