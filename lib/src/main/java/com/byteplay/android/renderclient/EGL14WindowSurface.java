package com.byteplay.android.renderclient;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLSurface;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.Objects;


class EGL14WindowSurface extends EGLWindowSurface {

    private EGLSurface eglSurface;
    private final int[] value = new int[1];

    public EGL14WindowSurface(GLRenderClient client, Surface surface) {
        super(client, surface);
    }

    public EGL14WindowSurface(GLRenderClient client, SurfaceTexture surfaceTexture) {
        super(client, surfaceTexture);
    }

    public EGL14WindowSurface(GLRenderClient client, SurfaceHolder holder) {
        super(client, holder);
    }


    @Override
    public int getWidth() {
        if (isDisposed()) {
            return 0;
        }
        create();
        EGL14.eglQuerySurface(client.getEGLDisplay(), eglSurface, EGL14.EGL_WIDTH, value, 0);
        return value[0];
    }

    @Override
    public int getHeight() {
        if (isDisposed()) {
            return 0;
        }
        create();
        EGL14.eglQuerySurface(client.getEGLDisplay(), eglSurface, EGL14.EGL_HEIGHT, value, 0);
        return value[0];
    }

    @Override
    public EGLSurface getEGLSurface() {
        return eglSurface;
    }


    @Override
    protected void onDispose() {
        client.destroyEGLSurface(this);
    }

    @Override
    protected void onCreate() {
        eglSurface = client.createEGLWindowSurface(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EGL14WindowSurface)) return false;
        if (!super.equals(o)) return false;
        EGL14WindowSurface that = (EGL14WindowSurface) o;
        return Objects.equals(eglSurface, that.eglSurface);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), eglSurface);
    }
}
