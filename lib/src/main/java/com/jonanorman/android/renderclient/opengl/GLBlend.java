package com.jonanorman.android.renderclient.opengl;

import android.graphics.Color;

public abstract class GLBlend extends GLFunction {

    private int blendColor;
    private Factor rgbSrcFactor = Factor.ONE;
    private Factor alphaSrcFactor = Factor.ZERO;
    private Factor rgbDstFactor = Factor.ONE;
    private Factor alphaDstFactor = Factor.ZERO;
    private Equation rgbBlendEquation = Equation.ADD;
    private Equation alphaBlendEquation = Equation.ADD;
    private float alphaColor;
    private float redColor;
    private float greenColor;
    private float blueColor;

    public GLBlend(GLRenderClient renderClient) {
        super(renderClient);

    }

    @Override
    protected void onApply() {
        onBlendColor(redColor, greenColor, blueColor, alphaColor);
        onBlendFuncSeparate(rgbSrcFactor, rgbDstFactor, alphaSrcFactor, alphaDstFactor);
        onBlendEquationSeparate(rgbBlendEquation, alphaBlendEquation);
    }


    protected abstract void onBlendFunc(Factor srcMode, Factor dstMode);

    protected abstract void onBlendEquation(Equation blendEquation);

    protected abstract void onBlendFuncSeparate(Factor rgbSrcFactor, Factor rgbDstFactor, Factor alphaSrcFactor, Factor alphaDstFactor);

    protected abstract void onBlendEquationSeparate(Equation rgbBlendEquation, Equation alphaBlendEquation);

    protected abstract void onBlendColor(float redColor, float greenColor, float blueColor, float alphaColor);


    public void setBlendFactor(Factor srcFactor, Factor dstFactor) {
        rgbSrcFactor = srcFactor;
        alphaSrcFactor = srcFactor;
        rgbDstFactor = dstFactor;
        alphaDstFactor = dstFactor;
    }


    public void setRgbSrcFactor(Factor rgbSrcFactor) {
        this.rgbSrcFactor = rgbSrcFactor;
    }


    public void setAlphaSrcFactor(Factor alphaSrcFactor) {
        this.alphaSrcFactor = alphaSrcFactor;
    }


    public void setRgbDstFactor(Factor rgbDstFactor) {
        this.rgbDstFactor = rgbDstFactor;
    }


    public void setAlphaDstFactor(Factor alphaDstFactor) {
        this.alphaDstFactor = alphaDstFactor;
    }


    public void setAlphaBlendEquation(Equation alphaBlendEquation) {
        this.alphaBlendEquation = alphaBlendEquation;
    }

    public void setRgbBlendEquation(Equation rgbBlendEquation) {
        this.rgbBlendEquation = rgbBlendEquation;
    }

    public void setBlendEquation(Equation equation) {
        rgbBlendEquation = equation;
        alphaBlendEquation = equation;
    }

    public void setBlendColor(int color) {
        blendColor = color;
        alphaColor = Color.alpha(blendColor) / 255.0f;
        redColor = Color.red(blendColor) / 255.0f;
        greenColor = Color.green(blendColor) / 255.0f;
        blueColor = Color.blue(blendColor) / 255.0f;
    }

    public Equation getRgbBlendEquation() {
        return rgbBlendEquation;
    }


    public Equation getAlphaBlendEquation() {
        return alphaBlendEquation;
    }

    public Factor getAlphaDstFactor() {
        return alphaDstFactor;
    }

    public Factor getRgbSrcFactor() {
        return rgbSrcFactor;
    }

    public Factor getAlphaSrcFactor() {
        return alphaSrcFactor;
    }

    public Factor getRgbDstFactor() {
        return rgbDstFactor;
    }


    public enum Equation {
        ADD,
        SUBTRACT,
        REVERSE_SUBTRACT
    }

    public enum Factor {
        ZERO,
        ONE,
        SRC_COLOR,
        ONE_MINUS_SRC_COLOR,
        DST_COLOR,
        ONE_MINUS_DST_COLOR,
        SRC_ALPHA,
        ONE_MINUS_SRC_ALPHA,
        DST_ALPHA,
        ONE_MINUS_DST_ALPHA,
        CONSTANT_COLOR,
        ONE_MINUS_CONSTANT_COLOR,
        CONSTANT_ALPHA,
        ONE_MINUS_CONSTANT_ALPHA
    }
}
