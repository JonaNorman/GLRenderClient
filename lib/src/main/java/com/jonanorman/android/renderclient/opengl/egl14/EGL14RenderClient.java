package com.jonanorman.android.renderclient.opengl.egl14;

import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;

import androidx.annotation.NonNull;

import com.jonanorman.android.renderclient.opengl.EGLConfig;
import com.jonanorman.android.renderclient.opengl.EGLConfigChooser;
import com.jonanorman.android.renderclient.opengl.EGLPbufferSurface;
import com.jonanorman.android.renderclient.opengl.EGLWindowSurface;
import com.jonanorman.android.renderclient.opengl.GL;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLTexture;
import com.jonanorman.android.renderclient.opengl.gl20.GL20Command;
import com.jonanorman.android.renderclient.opengl.gl20.GL20Texture;


public final class EGL14RenderClient extends GLRenderClient {



    public EGL14RenderClient(EGLContext shareContext, EGLConfigChooser configChooser) {
        super(shareContext, configChooser);
    }

    @Override
    protected EGLDisplay onCreateEGLDisplay() {
        EGLDisplay display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (display == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException(this + " unable  createEGLDisplay");
        }
        return display;
    }

    @Override
    protected int[] onInitEGLDisplay(EGLDisplay eglDisplay) {
        int[] majorVersion = new int[1];
        int[] minorVersion = new int[1];
        if (!EGL14.eglInitialize(eglDisplay, majorVersion, 0, minorVersion, 0)) {
            throw new RuntimeException(this + " unable initEGLDisplay");
        }
        return new int[]{majorVersion[0],minorVersion[0]};
    }

    @Override
    protected int onGetMaxEGLConfigNum(EGLDisplay eglDisplay) {
        int[] numConfig = {1};
        while (true) {
            android.opengl.EGLConfig[] configs = new android.opengl.EGLConfig[numConfig[0] * 2];
            if (!EGL14.eglGetConfigs(eglDisplay, configs, 0, configs.length, numConfig, 0)) {
                throw new IllegalArgumentException(this + " getMaxConfigNum  fail");
            }
            if (numConfig[0] < configs.length) {
                break;
            }
        }
        return numConfig[0];
    }

    @Override
    protected android.opengl.EGLConfig[] onGetAllEGLConfig(EGLDisplay eglDisplay, int maxConfigNum) {
        android.opengl.EGLConfig[] eglConfigs = new android.opengl.EGLConfig[maxConfigNum];
        if (!EGL14.eglGetConfigs(eglDisplay, eglConfigs, 0, maxConfigNum, new int[]{maxConfigNum}, 0)) {
            throw new IllegalArgumentException(this + " getAllConfig fail");
        }
        return eglConfigs;
    }

    @Override
    protected EGLConfig onCreateEGLConfig(EGLDisplay eglDisplay, android.opengl.EGLConfig config) {
        return new EGL14Config(eglDisplay, config);
    }

    @Override
    protected EGLContext onCreateEGLContext(EGLDisplay eglDisplay, android.opengl.EGLConfig eglConfig, EGLContext shareContext, int contextClientVersion) {
        EGLContext context = EGL14.eglCreateContext(eglDisplay, eglConfig, shareContext == null ? EGL14.EGL_NO_CONTEXT : shareContext,
                new int[]{
                        EGL14.EGL_CONTEXT_CLIENT_VERSION, contextClientVersion,
                        EGL14.EGL_NONE}, 0);
        if (context == EGL14.EGL_NO_CONTEXT) {
            throw new RuntimeException(this + " unable  createEGLContext");
        }
        return context;

    }

    @Override
    protected GL onCreateGL() {
        return new GL20Command();
    }

    @Override
    protected EGLPbufferSurface onCreateDefaultPBufferSurface() {
        return new EGL14PbufferSurface(this, 1, 1);
    }

    @Override
    protected GLTexture onCreateDefaultTexture() {
        return new GL20Texture(this, GLTexture.Type.TEXTURE_2D, 0);
    }

    @Override
    protected void onReleaseEGLContext(EGLDisplay eglDisplay, android.opengl.EGLConfig eglConfig, EGLContext eglContext) {
        EGL14.eglDestroyContext(eglDisplay, eglContext);
        EGL14.eglReleaseThread();
        EGL14.eglTerminate(eglDisplay);
    }

    @Override
    protected EGLWindowSurface onCreateEGLWindowSurface(Object surface) {
        return new EGL14WindowSurface(this, surface);
    }

    @Override
    protected EGLContext onGetCurrentEGLContext() {
        return EGL14.eglGetCurrentContext();
    }

    @Override
    protected String getEGLErrorMessage() {
        int error = EGL14.eglGetError();
        if (error == EGL14.EGL_SUCCESS) {
            return null;
        }
        String message;
        switch (error) {
            case EGL14.EGL_NOT_INITIALIZED:
                message = "EGL_NOT_INITIALIZED";
                break;
            case EGL14.EGL_BAD_ACCESS:
                message = "EGL_BAD_ACCESS";
                break;
            case EGL14.EGL_BAD_ALLOC:
                message = "EGL_BAD_ALLOC";
                break;
            case EGL14.EGL_BAD_ATTRIBUTE:
                message = "EGL_BAD_ATTRIBUTE";
                break;
            case EGL14.EGL_BAD_CONFIG:
                message = "EGL_BAD_CONFIG";
                break;
            case EGL14.EGL_BAD_CONTEXT:
                message = "EGL_BAD_CONTEXT";
                break;
            case EGL14.EGL_BAD_CURRENT_SURFACE:
                message = "EGL_BAD_CURRENT_SURFACE";
                break;
            case EGL14.EGL_BAD_DISPLAY:
                message = "EGL_BAD_DISPLAY";
                break;
            case EGL14.EGL_BAD_MATCH:
                message = "EGL_BAD_MATCH";
                break;
            case EGL14.EGL_BAD_NATIVE_PIXMAP:
                message = "EGL_BAD_NATIVE_PIXMAP";
                break;
            case EGL14.EGL_BAD_NATIVE_WINDOW:
                message = "EGL_BAD_NATIVE_WINDOW";
                break;
            case EGL14.EGL_BAD_PARAMETER:
                message = "EGL_BAD_PARAMETER";
                break;
            case EGL14.EGL_BAD_SURFACE:
                message = "EGL_BAD_SURFACE";
                break;
            case EGL14.EGL_CONTEXT_LOST:
                message = "EGL_CONTEXT_LOST";
                break;
            default:
                message = "EGL_UNKNOWN";
                break;
        }
        return message + " 0x" + Integer.toHexString(error);
    }

    @NonNull
    @Override
    public String toString() {
        return "EGL14RenderClient@" + hashCode();
    }
}
