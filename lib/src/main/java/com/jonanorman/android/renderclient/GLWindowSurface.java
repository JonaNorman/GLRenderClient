package com.jonanorman.android.renderclient;

import android.opengl.EGLSurface;

public abstract class GLWindowSurface extends GLRenderSurface {

    protected final Object surfaceObject;
    protected EGLSurface eglSurface;


    protected GLWindowSurface(GLRenderClient client, Object surface) {
        super(client);
        this.surfaceObject = surface;
        if (surface == null) {
            throw new NullPointerException("surface is null");
        }
    }

    public final Object getSurface() {
        return surfaceObject;
    }




    @Override
    protected EGLSurface getEGLSurface() {
        return eglSurface;
    }

}
