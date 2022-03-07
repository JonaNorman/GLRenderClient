package com.jonanorman.android.renderclient;

import android.opengl.EGL14;

import java.util.Objects;

class EGL14PbufferSurface extends GLPbufferSurface {


    public EGL14PbufferSurface(GLRenderClient client, int width, int height) {
        super(client, width, height);
    }

    @Override
    protected void onCreate() {
        eglSurface = EGL14.eglCreatePbufferSurface(client.getEGLDisplay(), client.getEGLConfig(), new int[]{
                EGL14.EGL_WIDTH, getWidth(),
                EGL14.EGL_HEIGHT, getHeight(),
                EGL14.EGL_NONE
        }, 0);
        if (eglSurface == EGL14.EGL_NO_SURFACE || eglSurface == null) {
            throw new RuntimeException("unable  create buffer surface");
        }
        client.checkEGLError();
    }

    @Override
    protected void onDispose() {
        EGL14.eglDestroySurface(client.getEGLDisplay(), eglSurface);
        client.checkEGLError();
    }

    @Override
    protected void onMakeCurrent(GLRenderSurface renderSurface) {
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

    }
}
