package com.jonanorman.android.renderclient.opengl;

import android.graphics.SurfaceTexture;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import com.jonanorman.android.renderclient.math.TimeStamp;

public abstract class EGLWindowSurface extends EGLSurface {

    private final Object surfaceObject;
    private TimeStamp presentationTime;
    private int lastWidth;
    private int lastHeight;


    public EGLWindowSurface(GLRenderClient client, Object surface) {
        super(client);
        this.surfaceObject = surface;
        this.presentationTime = TimeStamp.ofNanos(0);
        checkSurface();
    }

    public final Object getSurface() {
        return surfaceObject;
    }


    @Override
    protected final android.opengl.EGLSurface onCreateEGLSurface(EGLDisplay eglDisplay, EGLConfig eglConfig) {
        checkSurface();
        return onCreateEGLWindowSurface(eglDisplay, eglConfig);
    }


    @Override
    protected final boolean onMakeCurrent(EGLDisplay eglDisplay, android.opengl.EGLSurface eglSurface, EGLContext eglContext) {
        checkSurface();
        return onEGLWindowSurfaceMakeCurrent(eglDisplay, eglSurface, eglContext);
    }


    @Override
    protected final boolean onMakeNoCurrent(EGLDisplay eglDisplay) {
        checkSurface();
        return onEGLWindowSurfaceMakeNoCurrent(eglDisplay);

    }


    @Override
    protected final boolean onSwapBuffers(EGLDisplay eglDisplay, android.opengl.EGLSurface eglSurface) {
        onEGLWindowSurfacePresentationTime(eglDisplay, eglSurface, presentationTime.toNanos());
        getClient().checkEGLError();
        return onEGLWindowSurfaceSwapBuffers(eglDisplay, eglSurface);
    }


    @Override
    public final int getWidth() {
        if (!isAlive()) return lastWidth;
        return lastWidth = onEGLWindowSurfaceGetWidth(getEGLDisplay(), getEGLSurface());
    }


    @Override
    public final int getHeight() {
        if (!isAlive()) return lastHeight;
        return lastHeight = onEGLWindowSurfaceGetHeight(getEGLDisplay(), getEGLSurface());
    }


    public void setPresentationTime(@NonNull TimeStamp timeStamp) {
        this.presentationTime.setTimeStamp(timeStamp);
    }

    public TimeStamp getPresentationTime() {
        return presentationTime;
    }


    private void checkSurface() {
        if (surfaceObject == null) {
            throw new NullPointerException(this + " surface is null");
        }
        if (surfaceObject instanceof Surface) {
            Surface surface = (Surface) surfaceObject;
            if (!surface.isValid()) {
                throw new RuntimeException(this + " surface is not valid");
            }
        } else if (surfaceObject instanceof SurfaceTexture) {
            SurfaceTexture surfaceTexture = (SurfaceTexture) surfaceObject;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (surfaceTexture.isReleased()) {
                    throw new RuntimeException(this + " surfaceTexture is released");
                }
            }
        } else if (surfaceObject instanceof SurfaceHolder) {
            Surface surface = ((SurfaceHolder) surfaceObject).getSurface();
            if (surface == null) {
                throw new IllegalStateException(this + " surfaceHolder getSurface null");
            }
            if (!surface.isValid()) {
                throw new RuntimeException(this + " surfaceHolder is not valid");
            }
        }
    }


    protected abstract android.opengl.EGLSurface onCreateEGLWindowSurface(EGLDisplay eglDisplay, EGLConfig eglConfig);


    protected abstract boolean onEGLWindowSurfaceMakeCurrent(EGLDisplay eglDisplay, android.opengl.EGLSurface eglSurface, EGLContext eglContext);

    protected abstract boolean onEGLWindowSurfaceMakeNoCurrent(EGLDisplay eglDisplay);

    protected abstract boolean onEGLWindowSurfaceSwapBuffers(EGLDisplay eglDisplay, android.opengl.EGLSurface eglSurface);


    protected abstract void onEGLWindowSurfacePresentationTime(EGLDisplay eglDisplay, android.opengl.EGLSurface eglSurface, long presentationNs);

    protected abstract int onEGLWindowSurfaceGetWidth(EGLDisplay eglDisplay, android.opengl.EGLSurface eglSurface);

    protected abstract int onEGLWindowSurfaceGetHeight(EGLDisplay eglDisplay, android.opengl.EGLSurface eglSurface);
}
