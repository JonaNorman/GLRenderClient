package com.jonanorman.android.renderclient;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLExt;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.Objects;

class EGL14WindowSurface extends GLWindowSurface {

    private final int[] tempInt = new int[1];


    protected EGL14WindowSurface(GLRenderClient client, Object surface) {
        super(client, surface);

    }

    @Override
    protected void onMakeCurrent(GLRenderSurface renderSurface) {
        checkSurfaceObject(surfaceObject);
        GLRenderSurface currentEGLSurface = client.getCurrentRenderSurface();
        if (renderSurface == null) {
            boolean success = EGL14.eglMakeCurrent(client.getEGLDisplay(), EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            if (!success) {
                client.checkEGLError();
            }
            client.setCurrentRenderSurface(null);
        } else if (!Objects.equals(renderSurface, currentEGLSurface)) {
            boolean success = EGL14.eglMakeCurrent(client.getEGLDisplay(), getEGLSurface(), getEGLSurface(), client.getEGLContext());
            if (!success) {
                client.checkEGLError();
            }
            client.setCurrentRenderSurface(this);
        }
    }

    @Override
    protected void onSwapBuffers() {
        if (!EGLExt.eglPresentationTimeANDROID(client.getEGLDisplay(), getEGLSurface(), getTime())) {
            client.checkEGLError();
        }
        if (!EGL14.eglSwapBuffers(client.getEGLDisplay(), getEGLSurface())) {
            client.checkEGLError();
        }
    }

    @Override
    public int getWidth() {
        if (isDisposed()) {
            return 0;
        }
        create();
        EGL14.eglQuerySurface(client.getEGLDisplay(), getEGLSurface(), EGL14.EGL_WIDTH, tempInt, 0);
        return tempInt[0];
    }

    @Override
    public int getHeight() {
        if (isDisposed()) {
            return 0;
        }
        create();
        EGL14.eglQuerySurface(client.getEGLDisplay(), getEGLSurface(), EGL14.EGL_HEIGHT, tempInt, 0);
        return tempInt[0];
    }

    private void checkSurfaceObject(Object surfaceObject) {
        if (surfaceObject instanceof Surface) {
            Surface surface = (Surface) surfaceObject;
            if (!surface.isValid()) {
                throw new RuntimeException("surface is not valid");
            }
        } else if (surfaceObject instanceof SurfaceTexture) {
            SurfaceTexture surfaceTexture = (SurfaceTexture) surfaceObject;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (surfaceTexture.isReleased()) {
                    throw new RuntimeException("surface texture is released");
                }
            }
        } else if (surfaceObject instanceof SurfaceHolder) {
            Surface surface = ((SurfaceHolder) surfaceObject).getSurface();
            if (surface == null) {
                throw new IllegalStateException("surfaceHolder getSurface null");
            }
            if (!surface.isValid()) {
                throw new RuntimeException("surface is not valid");
            }
        }
    }

    @Override
    protected void onCreate() {
        checkSurfaceObject(surfaceObject);
        eglSurface = EGL14.eglCreateWindowSurface(client.getEGLDisplay(), client.getEGLConfig(), surfaceObject, new int[]{EGL14.EGL_NONE, 0, EGL14.EGL_NONE}, 0);
        if (eglSurface == EGL14.EGL_NO_SURFACE || eglSurface == null) {
            throw new RuntimeException("unable create window surface");
        }
        client.checkEGLError();
    }


    @Override
    protected void onDispose() {
        EGL14.eglDestroySurface(client.getEGLDisplay(), eglSurface);
        client.checkEGLError();
        client.removeWindowSurface(this);
    }
}
