package com.jonanorman.android.renderclient.opengl.egl14;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;

import androidx.annotation.NonNull;

import com.jonanorman.android.renderclient.opengl.EGLPbufferSurface;
import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.gl20.GL20FrameBuffer;

public final class EGL14PbufferSurface extends EGLPbufferSurface {


    public EGL14PbufferSurface(GLRenderClient client, int width, int height) {
        super(client, width, height);
        init();
    }


    @Override
    protected android.opengl.EGLSurface onCreateEGLSurface(EGLDisplay eglDisplay, EGLConfig eglConfig) {
        android.opengl.EGLSurface eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, eglConfig, new int[]{
                EGL14.EGL_WIDTH, getWidth(),
                EGL14.EGL_HEIGHT, getHeight(),
                EGL14.EGL_NONE
        }, 0);
        if (eglSurface == EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException(this+" unable createEGLSurface");
        }
        return eglSurface;
    }


    @Override
    protected void onDestroyEGLSurface(EGLDisplay eglDisplay, EGLSurface eglSurface) {
        EGL14.eglDestroySurface(eglDisplay, eglSurface);
    }


    @Override
    protected boolean onMakeCurrent(EGLDisplay eglDisplay, EGLSurface eglSurface, EGLContext eglContext) {
        return EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
    }

    @Override
    protected boolean onMakeNoCurrent(EGLDisplay eglDisplay) {
        return EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
    }

    @Override
    protected boolean onSwapBuffers(EGLDisplay eglDisplay, EGLSurface eglSurface) {
        return true;
    }

    @Override
    protected GLFrameBuffer onCreateDefaultFrameBuffer() {
        return new GL20FrameBuffer(getClient(), this);
    }

    @NonNull
    @Override
    public String toString() {
        return "EGL14PbufferSurface[width:" + getWidth() + ",height" + getHeight() + "]";
    }
}
