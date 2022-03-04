package com.jonanorman.android.renderclient;

public abstract class EGLConfig {

    public abstract int getBufferSize();

    public abstract int getAlphaSize();

    public abstract int getBlueSize();

    public abstract int getGreenSize();

    public abstract int getRedSize();

    public abstract int getDepthSize();

    public abstract int getStencilSize();

    public abstract boolean isSlow();

    public abstract int getConfigId();

    public abstract int getLevel();

    public abstract int getMaxPBufferHeight();

    public abstract int getMaxPBufferPixels();

    public abstract int getMaxPBufferWidth();

    public abstract boolean isNativeRenderable();

    public abstract int getNativeVisualId();

    public abstract int getNativeVisualType();

    public abstract int getSamples();

    public abstract int getSampleBuffers();

    public abstract boolean isWindowSurface();

    public abstract boolean isPBufferSurface();

    public abstract boolean isTransparent();

    public abstract int getTransparentRedValue();

    public abstract int getTransparentGreenValue();

    public abstract int getTransparentBlueValue();

    public abstract boolean isBindTextureRgb();

    public abstract boolean isBindToTextureRgba();

    public abstract int getMinSwapInterval();

    public abstract int getMaxSwapInterval();

    public abstract int getLuminanceSize();

    public abstract int getAlphaMaskSize();

    public abstract boolean isRgbColor();

    public abstract boolean isLuminanceColor();

    public abstract boolean isRenderGL30();

    public abstract boolean isRenderGL20();

    public abstract boolean isRenderGL10();

    public abstract boolean isConformantGL30();

    public abstract boolean isConformantGL20();

    public abstract boolean isConformantGL10();

    public abstract boolean isRecordable();

    protected abstract android.opengl.EGLConfig getEGLConfig();
}
