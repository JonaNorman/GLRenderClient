package com.byteplay.android.renderclient;

import android.graphics.Color;

import java.util.Objects;

public abstract class GLBlend extends GLObject {


    private float redColor;
    private float greenColor;
    private float blueColor;
    private float alphaColor;
    private int color;
    private GLBLendFactor rgbSrcFactor = GLBLendFactor.ONE;
    private GLBLendFactor alphaSrcFactor = GLBLendFactor.ZERO;
    private GLBLendFactor rgbDstFactor = GLBLendFactor.ONE;
    private GLBLendFactor alphaDstFactor = GLBLendFactor.ZERO;
    private GLBlendEquation rgbBlendEquation = GLBlendEquation.ADD;
    private GLBlendEquation alphaBlendEquation = GLBlendEquation.ADD;

    protected GLBlend(GLRenderClient GLRenderClient) {
        super(GLRenderClient);
        registerMethod(GLBlendFuncSeparateMethod.class, new GLBlendFuncSeparateMethod());
        registerMethod(GLBlendEquationSeparateMethod.class, new GLBlendEquationSeparateMethod());
        registerMethod(GLBlendColorMethod.class, new GLBlendColorMethod());
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onDispose() {

    }


    public void call() {
        findMethod(GLBlendFuncSeparateMethod.class).call();
        findMethod(GLBlendEquationSeparateMethod.class).call();
        findMethod(GLBlendColorMethod.class).call();
    }

    protected abstract void onBlendFunc(GLBLendFactor srcMode, GLBLendFactor dstMode);

    protected abstract void onBlendEquation(GLBlendEquation blendEquation);

    protected abstract void onBlendFuncSeparate(GLBLendFactor rgbSrcFactor, GLBLendFactor rgbDstFactor, GLBLendFactor alphaSrcFactor, GLBLendFactor alphaDstFactor);

    protected abstract void onBlendEquationSeparate(GLBlendEquation rgbBlendEquation, GLBlendEquation alphaBlendEquation);

    protected abstract void onBlendColor(float redColor, float greenColor, float blueColor, float alphaColor);


    public void setBlendFactor(GLBLendFactor srcFactor, GLBLendFactor dstFactor) {
        rgbSrcFactor = srcFactor;
        alphaSrcFactor = srcFactor;
        rgbDstFactor = dstFactor;
        alphaDstFactor = dstFactor;
    }

    public void setBlendEquation(GLBlendEquation equation) {
        rgbBlendEquation = equation;
        alphaBlendEquation = equation;
    }


    public GLBLendFactor getRgbSrcFactor() {
        return rgbSrcFactor;
    }

    public void setRgbSrcFactor(GLBLendFactor rgbSrcFactor) {
        this.rgbSrcFactor = rgbSrcFactor;
    }

    public GLBLendFactor getAlphaSrcFactor() {
        return alphaSrcFactor;
    }

    public void setAlphaSrcFactor(GLBLendFactor alphaSrcFactor) {
        this.alphaSrcFactor = alphaSrcFactor;
    }

    public GLBLendFactor getRgbDstFactor() {
        return rgbDstFactor;
    }

    public void setRgbDstFactor(GLBLendFactor rgbDstFactor) {
        this.rgbDstFactor = rgbDstFactor;
    }

    public GLBLendFactor getAlphaDstFactor() {
        return alphaDstFactor;
    }

    public void setAlphaDstFactor(GLBLendFactor alphaDstFactor) {
        this.alphaDstFactor = alphaDstFactor;
    }

    public GLBlendEquation getRgbBlendEquation() {
        return rgbBlendEquation;
    }

    public void setRgbBlendEquation(GLBlendEquation rgbBlendEquation) {
        this.rgbBlendEquation = rgbBlendEquation;
    }

    public GLBlendEquation getAlphaBlendEquation() {
        return alphaBlendEquation;
    }

    public void setAlphaBlendEquation(GLBlendEquation alphaBlendEquation) {
        this.alphaBlendEquation = alphaBlendEquation;
    }

    public void setBlendColor(int color) {
        alphaColor = Color.alpha(color) / 255.0f;
        redColor = Color.red(color) / 255.0f;
        greenColor = Color.green(color) / 255.0f;
        blueColor = Color.blue(color) / 255.0f;
        this.color = color;
    }

    public int getBlendColor() {
        return color;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GLBlend)) return false;
        if (!super.equals(o)) return false;
        GLBlend blend = (GLBlend) o;
        return color == blend.color && rgbSrcFactor == blend.rgbSrcFactor && alphaSrcFactor == blend.alphaSrcFactor && rgbDstFactor == blend.rgbDstFactor && alphaDstFactor == blend.alphaDstFactor && rgbBlendEquation == blend.rgbBlendEquation && alphaBlendEquation == blend.alphaBlendEquation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), color, rgbSrcFactor, alphaSrcFactor, rgbDstFactor, alphaDstFactor, rgbBlendEquation, alphaBlendEquation);
    }

    class GLBlendFuncSeparateMethod extends GLMethod {


        public GLBlendFuncSeparateMethod() {
            super();
        }

        @Override
        protected void onCallMethod() {
            onBlendFuncSeparate(rgbSrcFactor, rgbDstFactor, alphaSrcFactor, alphaDstFactor);
        }
    }

    class GLBlendEquationSeparateMethod extends GLMethod {


        public GLBlendEquationSeparateMethod() {
            super();
        }

        @Override
        protected void onCallMethod() {

            onBlendEquationSeparate(rgbBlendEquation, alphaBlendEquation);

        }

    }

    class GLBlendColorMethod extends GLMethod {


        public GLBlendColorMethod() {
            super();
        }

        @Override
        protected void onCallMethod() {
            onBlendColor(redColor, greenColor, blueColor, alphaColor);
        }
    }


}
