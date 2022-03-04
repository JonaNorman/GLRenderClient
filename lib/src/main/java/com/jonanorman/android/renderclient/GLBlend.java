package com.jonanorman.android.renderclient;

import android.graphics.Color;

import java.util.Objects;

public abstract class GLBlend extends GLFunction {


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
    private final GLBlendFuncSeparateMethod blendFuncSeparateMethod;
    private final GLBlendEquationSeparateMethod blendEquationSeparateMethod;
    private final GLBlendColorMethod blendColorMethod;

    protected GLBlend(GLRenderClient renderClient) {
        super(renderClient);
        blendFuncSeparateMethod = new GLBlendFuncSeparateMethod();
        blendEquationSeparateMethod = new GLBlendEquationSeparateMethod();
        blendColorMethod = new GLBlendColorMethod();
    }


    @Override
    protected void onCall() {
        blendFuncSeparateMethod.call();
        blendEquationSeparateMethod.call();
        blendColorMethod.call();
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
        return Objects.hash(color, rgbSrcFactor, alphaSrcFactor, rgbDstFactor, alphaDstFactor, rgbBlendEquation, alphaBlendEquation);
    }

    class GLBlendFuncSeparateMethod extends GLFunction {


        public GLBlendFuncSeparateMethod() {
            super(GLBlend.this.client);
        }

        @Override
        protected void onCall() {
            onBlendFuncSeparate(rgbSrcFactor, rgbDstFactor, alphaSrcFactor, alphaDstFactor);
        }
    }

    class GLBlendEquationSeparateMethod extends GLFunction {


        public GLBlendEquationSeparateMethod() {
            super(GLBlend.this.client);
        }

        @Override
        protected void onCall() {
            onBlendEquationSeparate(rgbBlendEquation, alphaBlendEquation);
        }
    }

    class GLBlendColorMethod extends GLFunction {


        public GLBlendColorMethod() {
            super(GLBlend.this.client);
        }

        @Override
        protected void onCall() {
            onBlendColor(redColor, greenColor, blueColor, alphaColor);
        }
    }


}
