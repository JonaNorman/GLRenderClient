package com.byteplay.android.renderclient;

import android.opengl.EGL14;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;

import java.util.Objects;

class EGL14Config extends EGLConfig {

    private static final int[] EGL_CONFIG_ENUMS =
            {
                    EGL14.EGL_BUFFER_SIZE,
                    EGL14.EGL_ALPHA_SIZE,
                    EGL14.EGL_BLUE_SIZE,
                    EGL14.EGL_GREEN_SIZE,
                    EGL14.EGL_RED_SIZE,
                    EGL14.EGL_DEPTH_SIZE,
                    EGL14.EGL_STENCIL_SIZE,
                    EGL14.EGL_CONFIG_CAVEAT,
                    EGL14.EGL_CONFIG_ID,
                    EGL14.EGL_LEVEL,
                    EGL14.EGL_MAX_PBUFFER_HEIGHT,
                    EGL14.EGL_MAX_PBUFFER_PIXELS,
                    EGL14.EGL_MAX_PBUFFER_WIDTH,
                    EGL14.EGL_NATIVE_RENDERABLE,
                    EGL14.EGL_NATIVE_VISUAL_ID,
                    EGL14.EGL_NATIVE_VISUAL_TYPE,
                    EGL14.EGL_SAMPLES,
                    EGL14.EGL_SAMPLE_BUFFERS,
                    EGL14.EGL_SURFACE_TYPE,
                    EGL14.EGL_TRANSPARENT_TYPE,
                    EGL14.EGL_TRANSPARENT_RED_VALUE,
                    EGL14.EGL_TRANSPARENT_GREEN_VALUE,
                    EGL14.EGL_TRANSPARENT_BLUE_VALUE,
                    EGL14.EGL_BIND_TO_TEXTURE_RGB,
                    EGL14.EGL_BIND_TO_TEXTURE_RGBA,
                    EGL14.EGL_MIN_SWAP_INTERVAL,
                    EGL14.EGL_MAX_SWAP_INTERVAL,
                    EGL14.EGL_LUMINANCE_SIZE,
                    EGL14.EGL_ALPHA_MASK_SIZE,
                    EGL14.EGL_COLOR_BUFFER_TYPE,
                    EGL14.EGL_RENDERABLE_TYPE,
                    EGL14.EGL_CONFORMANT,
                    EGLExt.EGL_RECORDABLE_ANDROID
            };
    private final int EGL_BUFFER_SIZE = 0;
    private final int EGL_ALPHA_SIZE = 1;
    private final int EGL_BLUE_SIZE = 2;
    private final int EGL_GREEN_SIZE = 3;
    private final int EGL_RED_SIZE = 4;
    private final int EGL_DEPTH_SIZE = 5;
    private final int EGL_STENCIL_SIZE = 6;
    private final int EGL_CONFIG_CAVEAT = 7;
    private final int EGL_CONFIG_ID = 8;
    private final int EGL_LEVEL = 9;
    private final int EGL_MAX_PBUFFER_HEIGHT = 10;
    private final int EGL_MAX_PBUFFER_PIXELS = 11;
    private final int EGL_MAX_PBUFFER_WIDTH = 12;
    private final int EGL_NATIVE_RENDERABLE = 13;
    private final int EGL_NATIVE_VISUAL_ID = 14;
    private final int EGL_NATIVE_VISUAL_TYPE = 15;
    private final int EGL_SAMPLES = 16;
    private final int EGL_SAMPLE_BUFFERS = 17;
    private final int EGL_SURFACE_TYPE = 18;
    private final int EGL_TRANSPARENT_TYPE = 19;
    private final int EGL_TRANSPARENT_RED_VALUE = 20;
    private final int EGL_TRANSPARENT_GREEN_VALUE = 21;
    private final int EGL_TRANSPARENT_BLUE_VALUE = 22;
    private final int EGL_BIND_TO_TEXTURE_RGB = 23;
    private final int EGL_BIND_TO_TEXTURE_RGBA = 24;
    private final int EGL_MIN_SWAP_INTERVAL = 25;
    private final int EGL_MAX_SWAP_INTERVAL = 26;
    private final int EGL_LUMINANCE_SIZE = 27;
    private final int EGL_ALPHA_MASK_SIZE = 28;
    private final int EGL_COLOR_BUFFER_TYPE = 29;
    private final int EGL_RENDERABLE_TYPE = 30;
    private final int EGL_CONFORMANT = 31;
    private final int EGL_RECORDABLE_ANDROID = 32;


    private final int[] configEnums = new int[EGL_CONFIG_ENUMS.length];
    private final android.opengl.EGLConfig eglConfig;
    private final int bufferSize;
    private final int alphaSize;
    private final int blueSize;
    private final int greenSize;
    private final int redSize;
    private final int depthSize;
    private final int stencilSize;
    private final boolean slow;
    private final int configId;
    private final int level;
    private final int maxPbufferHeight;
    private final int maxPBufferPixels;
    private final int maxPBufferWidth;
    private final boolean nativeRenderable;
    private final int nativeVisualId;
    private final int nativeVisualType;
    private final int samples;
    private final int sampleBuffers;
    private final boolean windowSurface;
    private final boolean pBufferSurface;
    private final boolean transparent;
    private final int transparentRedValue;
    private final int transparentGreenValue;
    private final int transparentBlueValue;
    private final boolean bindTextureRgb;
    private final boolean bindToTextureRgba;
    private final int minSwapInterval;
    private final int maxSwapInterval;
    private final int luminanceSize;
    private final int alphaMaskSize;
    private final boolean rgbColor;
    private final boolean luminanceColor;
    private final boolean renderGL30;
    private final boolean renderGL20;
    private final boolean renderGL10;
    private final boolean conformantGL30;
    private final boolean conformantGL20;
    private final boolean conformantGL10;
    private final boolean recordable;


    public EGL14Config(EGLDisplay display, android.opengl.EGLConfig config) {
        int[] value = new int[1];
        for (int i = 0; i < EGL_CONFIG_ENUMS.length; i++) {
            if (EGL14.eglGetConfigAttrib(display, config, EGL_CONFIG_ENUMS[i], value, 0)) {
                configEnums[i] = value[0];
            }
            while (EGL14.eglGetError() != EGL14.EGL_SUCCESS) {
            }
        }

        eglConfig = config;
        bufferSize = configEnums[EGL_BUFFER_SIZE];
        alphaSize = configEnums[EGL_ALPHA_SIZE];
        blueSize = configEnums[EGL_BLUE_SIZE];
        greenSize = configEnums[EGL_GREEN_SIZE];
        redSize = configEnums[EGL_RED_SIZE];
        depthSize = configEnums[EGL_DEPTH_SIZE];
        stencilSize = configEnums[EGL_STENCIL_SIZE];
        slow = configEnums[EGL_CONFIG_CAVEAT] == EGL14.EGL_SLOW_CONFIG;
        configId = configEnums[EGL_CONFIG_ID];
        level = configEnums[EGL_LEVEL];
        maxPbufferHeight = configEnums[EGL_MAX_PBUFFER_HEIGHT];
        maxPBufferPixels = configEnums[EGL_MAX_PBUFFER_PIXELS];
        maxPBufferWidth = configEnums[EGL_MAX_PBUFFER_WIDTH];
        nativeRenderable = configEnums[EGL_NATIVE_RENDERABLE] == EGL14.EGL_TRUE;
        nativeVisualId = configEnums[EGL_NATIVE_VISUAL_ID];
        nativeVisualType = configEnums[EGL_NATIVE_VISUAL_TYPE];
        samples = configEnums[EGL_SAMPLES];
        sampleBuffers = configEnums[EGL_SAMPLE_BUFFERS];
        windowSurface = (configEnums[EGL_SURFACE_TYPE] & EGL14.EGL_WINDOW_BIT) == EGL14.EGL_WINDOW_BIT;
        pBufferSurface = (configEnums[EGL_SURFACE_TYPE] & EGL14.EGL_PBUFFER_BIT) == EGL14.EGL_PBUFFER_BIT;
        transparent = configEnums[EGL_TRANSPARENT_TYPE] == EGL14.EGL_TRANSPARENT_RGB;
        transparentRedValue = configEnums[EGL_TRANSPARENT_RED_VALUE];
        transparentGreenValue = configEnums[EGL_TRANSPARENT_GREEN_VALUE];
        transparentBlueValue = configEnums[EGL_TRANSPARENT_BLUE_VALUE];
        bindTextureRgb = configEnums[EGL_BIND_TO_TEXTURE_RGB] == EGL14.EGL_TRUE;
        bindToTextureRgba = configEnums[EGL_BIND_TO_TEXTURE_RGBA] == EGL14.EGL_TRUE;
        minSwapInterval = configEnums[EGL_MIN_SWAP_INTERVAL];
        maxSwapInterval = configEnums[EGL_MAX_SWAP_INTERVAL];
        luminanceSize = configEnums[EGL_LUMINANCE_SIZE];
        alphaMaskSize = configEnums[EGL_ALPHA_MASK_SIZE];
        rgbColor = (configEnums[EGL_COLOR_BUFFER_TYPE] & EGL14.EGL_RGB_BUFFER) == EGL14.EGL_RGB_BUFFER;
        luminanceColor = (configEnums[EGL_COLOR_BUFFER_TYPE] & EGL14.EGL_LUMINANCE_BUFFER) == EGL14.EGL_LUMINANCE_BUFFER;
        renderGL30 = (configEnums[EGL_RENDERABLE_TYPE] & EGLExt.EGL_OPENGL_ES3_BIT_KHR) == EGLExt.EGL_OPENGL_ES3_BIT_KHR;
        renderGL20 = (configEnums[EGL_RENDERABLE_TYPE] & EGL14.EGL_OPENGL_ES2_BIT) == EGL14.EGL_OPENGL_ES2_BIT;
        renderGL10 = (configEnums[EGL_RENDERABLE_TYPE] & EGL14.EGL_OPENGL_ES_BIT) == EGL14.EGL_OPENGL_ES_BIT;
        conformantGL30 = (configEnums[EGL_CONFORMANT] & EGLExt.EGL_OPENGL_ES3_BIT_KHR) == EGLExt.EGL_OPENGL_ES3_BIT_KHR;
        conformantGL20 = (configEnums[EGL_CONFORMANT] & EGL14.EGL_OPENGL_ES2_BIT) == EGL14.EGL_OPENGL_ES2_BIT;
        conformantGL10 = (configEnums[EGL_CONFORMANT] & EGL14.EGL_OPENGL_ES_BIT) == EGL14.EGL_OPENGL_ES_BIT;
        recordable = configEnums[EGL_RECORDABLE_ANDROID] == EGL14.EGL_TRUE;
    }


    @Override
    public int getBufferSize() {
        return bufferSize;
    }


    @Override
    public int getAlphaSize() {
        return alphaSize;
    }


    @Override
    public int getBlueSize() {
        return blueSize;
    }


    @Override
    public int getGreenSize() {
        return greenSize;
    }


    @Override
    public int getRedSize() {
        return redSize;
    }


    @Override
    public int getDepthSize() {
        return depthSize;
    }


    @Override
    public int getStencilSize() {
        return stencilSize;
    }


    @Override
    public boolean isSlow() {
        return slow;
    }


    @Override
    public int getConfigId() {
        return configId;
    }


    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getMaxPBufferHeight() {
        return maxPbufferHeight;
    }


    @Override
    public int getMaxPBufferPixels() {
        return maxPBufferPixels;
    }


    @Override
    public int getMaxPBufferWidth() {
        return maxPBufferWidth;
    }


    @Override
    public boolean isNativeRenderable() {
        return nativeRenderable;
    }


    @Override
    public int getNativeVisualId() {
        return nativeVisualId;
    }


    @Override
    public int getNativeVisualType() {
        return nativeVisualType;
    }


    @Override
    public int getSamples() {
        return samples;
    }


    @Override
    public int getSampleBuffers() {
        return sampleBuffers;
    }


    @Override
    public boolean isWindowSurface() {
        return windowSurface;
    }


    @Override
    public boolean isPBufferSurface() {
        return pBufferSurface;
    }


    @Override
    public boolean isTransparent() {
        return transparent;
    }


    @Override
    public int getTransparentRedValue() {
        return transparentRedValue;
    }


    @Override
    public int getTransparentGreenValue() {
        return transparentGreenValue;
    }


    @Override
    public int getTransparentBlueValue() {
        return transparentBlueValue;
    }


    @Override
    public boolean isBindTextureRgb() {
        return bindTextureRgb;
    }


    @Override
    public boolean isBindToTextureRgba() {
        return bindToTextureRgba;
    }


    @Override
    public int getMinSwapInterval() {
        return minSwapInterval;
    }


    @Override
    public int getMaxSwapInterval() {
        return maxSwapInterval;
    }


    @Override
    public int getLuminanceSize() {
        return luminanceSize;
    }


    @Override
    public int getAlphaMaskSize() {
        return alphaMaskSize;
    }


    @Override
    public boolean isRgbColor() {
        return rgbColor;
    }


    @Override
    public boolean isLuminanceColor() {
        return luminanceColor;
    }


    @Override
    public boolean isRenderGL30() {
        return renderGL30;
    }


    @Override
    public boolean isRenderGL20() {
        return renderGL20;
    }


    @Override
    public boolean isRenderGL10() {
        return renderGL10;
    }


    @Override
    public boolean isConformantGL30() {
        return conformantGL30;
    }


    @Override
    public boolean isConformantGL20() {
        return conformantGL20;
    }


    @Override
    public boolean isConformantGL10() {
        return conformantGL10;
    }


    @Override
    public boolean isRecordable() {
        return recordable;
    }


    @Override
    protected android.opengl.EGLConfig getEGLConfig() {
        return eglConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EGL14Config)) return false;
        EGL14Config that = (EGL14Config) o;
        return Objects.equals(eglConfig, that.eglConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eglConfig);
    }
}
