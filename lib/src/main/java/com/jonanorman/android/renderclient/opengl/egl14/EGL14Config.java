package com.jonanorman.android.renderclient.opengl.egl14;

import android.opengl.EGL14;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;

import com.jonanorman.android.renderclient.opengl.EGLConfig;

import java.util.HashMap;
import java.util.Map;

public class EGL14Config implements EGLConfig {

    enum Attrib {
        EGL_BUFFER_SIZE(EGL14.EGL_BUFFER_SIZE),
        EGL_ALPHA_SIZE(EGL14.EGL_ALPHA_SIZE),
        EGL_BLUE_SIZE(EGL14.EGL_BLUE_SIZE),
        EGL_GREEN_SIZE(EGL14.EGL_GREEN_SIZE),
        EGL_RED_SIZE(EGL14.EGL_RED_SIZE),
        EGL_DEPTH_SIZE(EGL14.EGL_DEPTH_SIZE),
        EGL_STENCIL_SIZE(EGL14.EGL_STENCIL_SIZE),
        EGL_CONFIG_CAVEAT(EGL14.EGL_CONFIG_CAVEAT),
        EGL_CONFIG_ID(EGL14.EGL_CONFIG_ID),
        EGL_LEVEL(EGL14.EGL_LEVEL),
        EGL_MAX_PBUFFER_HEIGHT(EGL14.EGL_MAX_PBUFFER_HEIGHT),
        EGL_MAX_PBUFFER_PIXELS(EGL14.EGL_MAX_PBUFFER_PIXELS),
        EGL_MAX_PBUFFER_WIDTH(EGL14.EGL_MAX_PBUFFER_WIDTH),
        EGL_NATIVE_RENDERABLE(EGL14.EGL_NATIVE_RENDERABLE),
        EGL_NATIVE_VISUAL_ID(EGL14.EGL_NATIVE_VISUAL_ID),
        EGL_NATIVE_VISUAL_TYPE(EGL14.EGL_NATIVE_VISUAL_TYPE),
        EGL_SAMPLES(EGL14.EGL_SAMPLES),
        EGL_SAMPLE_BUFFERS(EGL14.EGL_SAMPLE_BUFFERS),
        EGL_SURFACE_TYPE(EGL14.EGL_SURFACE_TYPE),
        EGL_TRANSPARENT_TYPE(EGL14.EGL_TRANSPARENT_TYPE),
        EGL_TRANSPARENT_RED_VALUE(EGL14.EGL_TRANSPARENT_RED_VALUE),
        EGL_TRANSPARENT_GREEN_VALUE(EGL14.EGL_TRANSPARENT_GREEN_VALUE),
        EGL_TRANSPARENT_BLUE_VALUE(EGL14.EGL_TRANSPARENT_BLUE_VALUE),
        EGL_BIND_TO_TEXTURE_RGB(EGL14.EGL_BIND_TO_TEXTURE_RGB),
        EGL_BIND_TO_TEXTURE_RGBA(EGL14.EGL_BIND_TO_TEXTURE_RGBA),
        EGL_MIN_SWAP_INTERVAL(EGL14.EGL_MIN_SWAP_INTERVAL),
        EGL_MAX_SWAP_INTERVAL(EGL14.EGL_MAX_SWAP_INTERVAL),
        EGL_LUMINANCE_SIZE(EGL14.EGL_LUMINANCE_SIZE),
        EGL_ALPHA_MASK_SIZE(EGL14.EGL_ALPHA_MASK_SIZE),
        EGL_COLOR_BUFFER_TYPE(EGL14.EGL_COLOR_BUFFER_TYPE),
        EGL_RENDERABLE_TYPE(EGL14.EGL_RENDERABLE_TYPE),
        EGL_CONFORMANT(EGL14.EGL_CONFORMANT),
        EGL_RECORDABLE_ANDROID(EGLExt.EGL_RECORDABLE_ANDROID);

        int attribute;

        Attrib(int attribute) {
            this.attribute = attribute;
        }
    }


    private final Map<Attrib, Integer> attribMap;
    private final android.opengl.EGLConfig eglConfig;
    private final int[] tempInt;


    public EGL14Config(EGLDisplay display, android.opengl.EGLConfig config) {
        eglConfig = config;
        tempInt = new int[1];
        attribMap = new HashMap<>();
        initAttribMap(display, config);
    }

    private void initAttribMap(EGLDisplay display, android.opengl.EGLConfig config) {
        for (Attrib attrib : Attrib.values()) {
            if (EGL14.eglGetConfigAttrib(display, config, attrib.attribute, tempInt, 0)) {
                attribMap.put(attrib, tempInt[0]);
            } else {
                while (EGL14.eglGetError() != EGL14.EGL_SUCCESS) ;
            }
        }
    }


    @Override
    public int getBufferSize() {
        return attribMap.get(Attrib.EGL_BUFFER_SIZE);
    }


    @Override
    public int getAlphaSize() {
        return attribMap.get(Attrib.EGL_ALPHA_SIZE);
    }


    @Override
    public int getBlueSize() {
        return attribMap.get(Attrib.EGL_BUFFER_SIZE);
    }


    @Override
    public int getGreenSize() {
        return attribMap.get(Attrib.EGL_GREEN_SIZE);
    }


    @Override
    public int getRedSize() {
        return attribMap.get(Attrib.EGL_RED_SIZE);
    }


    @Override
    public int getDepthSize() {
        return attribMap.get(Attrib.EGL_DEPTH_SIZE);
    }


    @Override
    public int getStencilSize() {
        return attribMap.get(Attrib.EGL_STENCIL_SIZE);
    }


    @Override
    public boolean isSlow() {
        return attribMap.get(Attrib.EGL_CONFIG_CAVEAT) == EGL14.EGL_SLOW_CONFIG;
    }


    @Override
    public int getConfigId() {
        return attribMap.get(Attrib.EGL_CONFIG_ID);
    }


    @Override
    public int getLevel() {
        return attribMap.get(Attrib.EGL_LEVEL);
    }

    @Override
    public int getMaxPBufferHeight() {
        return attribMap.get(Attrib.EGL_MAX_PBUFFER_HEIGHT);
    }


    @Override
    public int getMaxPBufferPixels() {
        return attribMap.get(Attrib.EGL_MAX_PBUFFER_PIXELS);
    }


    @Override
    public int getMaxPBufferWidth() {
        return attribMap.get(Attrib.EGL_MAX_PBUFFER_WIDTH);
    }


    @Override
    public boolean isNativeRenderable() {
        return attribMap.get(Attrib.EGL_NATIVE_RENDERABLE) == EGL14.EGL_TRUE;
    }


    @Override
    public int getNativeVisualId() {
        return attribMap.get(Attrib.EGL_NATIVE_VISUAL_ID);
    }


    @Override
    public int getNativeVisualType() {
        return attribMap.get(Attrib.EGL_NATIVE_VISUAL_TYPE);
    }


    @Override
    public int getSamples() {
        return attribMap.get(Attrib.EGL_SAMPLES);
    }


    @Override
    public int getSampleBuffers() {
        return attribMap.get(Attrib.EGL_SAMPLE_BUFFERS);
    }


    @Override
    public boolean isWindowSurface() {
        return (attribMap.get(Attrib.EGL_SURFACE_TYPE) & EGL14.EGL_WINDOW_BIT) == EGL14.EGL_WINDOW_BIT;
    }


    @Override
    public boolean isPBufferSurface() {

        return (attribMap.get(Attrib.EGL_SURFACE_TYPE) & EGL14.EGL_PBUFFER_BIT) == EGL14.EGL_PBUFFER_BIT;

    }


    @Override
    public boolean isTransparent() {
        return attribMap.get(Attrib.EGL_TRANSPARENT_TYPE) == EGL14.EGL_TRANSPARENT_RGB;
    }


    @Override
    public int getTransparentRedValue() {
        return attribMap.get(Attrib.EGL_TRANSPARENT_RED_VALUE);
    }


    @Override
    public int getTransparentGreenValue() {
        return attribMap.get(Attrib.EGL_TRANSPARENT_GREEN_VALUE);
    }


    @Override
    public int getTransparentBlueValue() {
        return attribMap.get(Attrib.EGL_TRANSPARENT_BLUE_VALUE);
    }


    @Override
    public boolean isBindTextureRgb() {
        return attribMap.get(Attrib.EGL_BIND_TO_TEXTURE_RGB) == EGL14.EGL_TRUE;
    }


    @Override
    public boolean isBindToTextureRgba() {
        return attribMap.get(Attrib.EGL_BIND_TO_TEXTURE_RGBA) == EGL14.EGL_TRUE;
    }


    @Override
    public int getMinSwapInterval() {
        return attribMap.get(Attrib.EGL_MIN_SWAP_INTERVAL);
    }


    @Override
    public int getMaxSwapInterval() {
        return attribMap.get(Attrib.EGL_MAX_SWAP_INTERVAL);
    }


    @Override
    public int getLuminanceSize() {
        return attribMap.get(Attrib.EGL_LUMINANCE_SIZE);
    }


    @Override
    public int getAlphaMaskSize() {
        return attribMap.get(Attrib.EGL_ALPHA_MASK_SIZE);
    }


    @Override
    public boolean isRgbColor() {
        return (attribMap.get(Attrib.EGL_COLOR_BUFFER_TYPE) & EGL14.EGL_RGB_BUFFER) == EGL14.EGL_RGB_BUFFER;
    }


    @Override
    public boolean isLuminanceColor() {
        return (attribMap.get(Attrib.EGL_COLOR_BUFFER_TYPE) & EGL14.EGL_LUMINANCE_BUFFER) == EGL14.EGL_LUMINANCE_BUFFER;
    }


    @Override
    public boolean isRenderGL30() {
        return (attribMap.get(Attrib.EGL_RENDERABLE_TYPE) & EGLExt.EGL_OPENGL_ES3_BIT_KHR) == EGLExt.EGL_OPENGL_ES3_BIT_KHR;
    }


    @Override
    public boolean isRenderGL20() {
        return (attribMap.get(Attrib.EGL_RENDERABLE_TYPE) & EGL14.EGL_OPENGL_ES2_BIT) == EGL14.EGL_OPENGL_ES2_BIT;
    }


    @Override
    public boolean isRenderGL10() {
        return (attribMap.get(Attrib.EGL_RENDERABLE_TYPE) & EGL14.EGL_OPENGL_ES_BIT) == EGL14.EGL_OPENGL_ES_BIT;
    }


    @Override
    public boolean isConformantGL30() {
        return (attribMap.get(Attrib.EGL_CONFORMANT) & EGLExt.EGL_OPENGL_ES3_BIT_KHR) == EGLExt.EGL_OPENGL_ES3_BIT_KHR;
    }


    @Override
    public boolean isConformantGL20() {
        return (attribMap.get(Attrib.EGL_CONFORMANT) & EGL14.EGL_OPENGL_ES2_BIT) == EGL14.EGL_OPENGL_ES2_BIT;

    }


    @Override
    public boolean isConformantGL10() {
        return (attribMap.get(Attrib.EGL_CONFORMANT) & EGL14.EGL_OPENGL_ES_BIT) == EGL14.EGL_OPENGL_ES_BIT;
    }


    @Override
    public boolean isRecordable() {
        return attribMap.get(Attrib.EGL_RECORDABLE_ANDROID) == EGL14.EGL_TRUE;
    }


    @Override
    public android.opengl.EGLConfig getEGLConfig() {
        return eglConfig;
    }

    @Override
    public String toString() {
        return "EGL14Config@" + hashCode();
    }
}
