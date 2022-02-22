package com.byteplay.android.renderclient;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLExt;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.Objects;

public abstract class EGLWindowSurface extends EGLSurface {

    private final Object surfaceObject;
    private long timeNs;


    public EGLWindowSurface(GLRenderClient client, Surface surface) {
        super(client);
        this.surfaceObject = surface;
        if (surface == null) {
            throw new RuntimeException("surface is null");
        }
    }

    public EGLWindowSurface(GLRenderClient client, SurfaceTexture surfaceTexture) {
        super(client);
        this.surfaceObject = surfaceTexture;
        if (surfaceTexture == null) {
            throw new RuntimeException("surfaceTexture is null");
        }
    }

    public EGLWindowSurface(GLRenderClient client, SurfaceHolder holder) {
        super(client);
        this.surfaceObject = holder;
        if (holder == null) {
            throw new RuntimeException("SurfaceHolder is null");
        }
    }

    public final Object getSurface() {
        return surfaceObject;
    }


    public abstract int getWidth();


    public abstract int getHeight();

    @Override
    protected void onSwapBuffers() {
        makeCurrent();
        if (!EGLExt.eglPresentationTimeANDROID(client.getEGLDisplay(), getEGLSurface(), timeNs)) {
            client.checkEGLError();
        }
        if (!EGL14.eglSwapBuffers(client.getEGLDisplay(), getEGLSurface())) {
            client.checkEGLError();
        }
    }

    public void setTime(long timeNs) {
        this.timeNs = timeNs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EGLWindowSurface)) return false;
        if (!super.equals(o)) return false;
        EGLWindowSurface that = (EGLWindowSurface) o;
        return Objects.equals(surfaceObject, that.surfaceObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), surfaceObject);
    }
}
