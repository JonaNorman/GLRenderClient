package com.byteplay.android.renderclient;

import android.opengl.EGLSurface;

import java.util.Objects;

class EGL14PbufferSurface extends EGLPbufferSurface {


    private android.opengl.EGLSurface eglSurface;

    public EGL14PbufferSurface(GLRenderClient client, int width, int height) {
        super(client, width, height);
    }


    @Override
    protected void onDispose() {
        client.destroyEGLSurface(this);
    }

    @Override
    protected void onCreate() {
        eglSurface = client.createEGLPbufferSurface(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EGL14PbufferSurface)) return false;
        if (!super.equals(o)) return false;
        EGL14PbufferSurface that = (EGL14PbufferSurface) o;
        return Objects.equals(eglSurface, that.eglSurface);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), eglSurface);
    }

    @Override
    public EGLSurface getEGLSurface() {
        return eglSurface;
    }
}
