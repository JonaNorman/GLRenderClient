package com.jonanorman.android.renderclient;

import android.graphics.SurfaceTexture;
import android.opengl.EGLSurface;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.Objects;

public class GLWindowSurface extends GLRenderSurface {

    private final Object surfaceObject;
    private EGLSurface eglSurface;
    private long timeNs;


    public GLWindowSurface(GLRenderClient client, Surface surface) {
        super(client);
        this.surfaceObject = surface;
        if (surface == null) {
            throw new NullPointerException("surface is null");
        }
    }

    public GLWindowSurface(GLRenderClient client, SurfaceTexture surfaceTexture) {
        super(client);
        this.surfaceObject = surfaceTexture;
        if (surfaceTexture == null) {
            throw new NullPointerException("surfaceTexture is null");
        }
    }

    public GLWindowSurface(GLRenderClient client, SurfaceHolder holder) {
        super(client);
        this.surfaceObject = holder;
        if (holder == null) {
            throw new NullPointerException("SurfaceHolder is null");
        }
    }

    public final Object getSurface() {
        return surfaceObject;
    }


    @Override
    public int getWidth() {
        return client.queryWidth(this);
    }


    @Override
    public int getHeight() {
        return client.queryHeight(this);
    }

    public void setTime(long timeNs) {
        this.timeNs = timeNs;
    }

    public long getTime() {
        return timeNs;
    }

    @Override
    protected EGLSurface getEGLSurface() {
        return eglSurface;
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
        if (!(o instanceof GLWindowSurface)) return false;
        GLWindowSurface that = (GLWindowSurface) o;
        return Objects.equals(surfaceObject, that.surfaceObject) && Objects.equals(eglSurface, that.eglSurface);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eglSurface, surfaceObject);
    }
}
