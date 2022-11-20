package com.jonanorman.android.renderclient.opengl.egl14;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;

import androidx.annotation.NonNull;

import com.jonanorman.android.renderclient.opengl.EGLWindowSurface;
import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.gl20.GL20FrameBuffer;

public final class EGL14WindowSurface extends EGLWindowSurface {

    private final int[] tempInt;


    public EGL14WindowSurface(GLRenderClient client, Object surface) {
        super(client, surface);
        tempInt = new int[2];
        init();
    }


    @Override
    protected EGLSurface onCreateEGLWindowSurface(EGLDisplay eglDisplay, EGLConfig eglConfig) {
        android.opengl.EGLSurface eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, getSurface(), new int[]{EGL14.EGL_NONE, 0, EGL14.EGL_NONE}, 0);
        if (eglSurface == EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException(this + " unable createEGLSurface");
        }
        return eglSurface;
    }

    @Override
    protected boolean onEGLWindowSurfaceMakeCurrent(EGLDisplay eglDisplay, EGLSurface eglSurface, EGLContext eglContext) {
        return EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
    }

    @Override
    protected boolean onEGLWindowSurfaceMakeNoCurrent(EGLDisplay eglDisplay) {
        return EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
    }

    @Override
    protected boolean onEGLWindowSurfaceSwapBuffers(EGLDisplay eglDisplay, EGLSurface eglSurface) {
        return EGL14.eglSwapBuffers(eglDisplay, eglSurface);
    }

    @Override
    protected void onEGLWindowSurfacePresentationTime(EGLDisplay eglDisplay, EGLSurface eglSurface, long presentationNs) {
        EGLExt.eglPresentationTimeANDROID(eglDisplay, eglSurface, presentationNs);
    }

    @Override
    protected GLFrameBuffer onCreateDefaultFrameBuffer() {
        return new GL20FrameBuffer(getClient(), this);
    }

    @Override
    protected int onEGLWindowSurfaceGetWidth(EGLDisplay eglDisplay, EGLSurface eglSurface) {
        EGL14.eglQuerySurface(getEGLDisplay(), getEGLSurface(), EGL14.EGL_WIDTH, tempInt, 0);
        return tempInt[0];
    }

    @Override
    protected int onEGLWindowSurfaceGetHeight(EGLDisplay eglDisplay, EGLSurface eglSurface) {
        EGL14.eglQuerySurface(getEGLDisplay(), getEGLSurface(), EGL14.EGL_HEIGHT, tempInt, 1);
        return tempInt[1];
    }


    @Override
    protected void onDestroyEGLSurface(EGLDisplay eglDisplay, EGLSurface eglSurface) {
        EGL14.eglDestroySurface(getEGLDisplay(), getEGLSurface());
    }

    @NonNull
    @Override
    public String toString() {
        return "EGL14WindowSurface@" + Integer.toHexString(hashCode());
    }
}
