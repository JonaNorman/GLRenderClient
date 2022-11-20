package com.jonanorman.android.renderclient.opengl.gl20;

import android.util.ArrayMap;

import com.jonanorman.android.renderclient.opengl.GLBlend;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;

public class GL20Blend extends GLBlend {

    private static final ArrayMap<Factor, Integer> BLEND_MAP = new ArrayMap<>();

    private static final ArrayMap<Equation, Integer> EQUATION_MAP = new ArrayMap<>();

    static {
        BLEND_MAP.put(Factor.ZERO, GL20.GL_ZERO);
        BLEND_MAP.put(Factor.ONE, GL20.GL_ONE);
        BLEND_MAP.put(Factor.SRC_COLOR, GL20.GL_SRC_COLOR);
        BLEND_MAP.put(Factor.ONE_MINUS_SRC_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR);
        BLEND_MAP.put(Factor.DST_COLOR, GL20.GL_DST_COLOR);
        BLEND_MAP.put(Factor.ONE_MINUS_DST_COLOR, GL20.GL_ONE_MINUS_DST_COLOR);
        BLEND_MAP.put(Factor.SRC_ALPHA, GL20.GL_SRC_ALPHA);
        BLEND_MAP.put(Factor.ONE_MINUS_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        BLEND_MAP.put(Factor.DST_ALPHA, GL20.GL_DST_ALPHA);
        BLEND_MAP.put(Factor.ONE_MINUS_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
        BLEND_MAP.put(Factor.CONSTANT_COLOR, GL20.GL_CONSTANT_COLOR);
        BLEND_MAP.put(Factor.ONE_MINUS_CONSTANT_COLOR, GL20.GL_ONE_MINUS_CONSTANT_COLOR);
        BLEND_MAP.put(Factor.CONSTANT_ALPHA, GL20.GL_CONSTANT_ALPHA);
        BLEND_MAP.put(Factor.ONE_MINUS_CONSTANT_ALPHA, GL20.GL_ONE_MINUS_CONSTANT_ALPHA);
        EQUATION_MAP.put(Equation.ADD, GL20.GL_FUNC_ADD);
        EQUATION_MAP.put(Equation.SUBTRACT, GL20.GL_FUNC_SUBTRACT);
        EQUATION_MAP.put(Equation.REVERSE_SUBTRACT, GL20.GL_FUNC_REVERSE_SUBTRACT);
    }

    private GL20 gl20;


    public GL20Blend(GLRenderClient renderClient) {
        super(renderClient);
        gl20 = getGL();
    }

    @Override
    protected void onBlendFunc(Factor srcMode, Factor dstMode) {
        Integer src = BLEND_MAP.get(srcMode);
        Integer dst = BLEND_MAP.get(dstMode);
        gl20.glBlendFunc(src, dst);
    }

    @Override
    protected void onBlendEquation(Equation blendEquation) {
        Integer equation = EQUATION_MAP.get(blendEquation);
        gl20.glBlendEquation(equation);
    }

    @Override
    protected void onBlendFuncSeparate(Factor rgbSrcFactor, Factor rgbDstFactor, Factor alphaSrcFactor, Factor alphaDstFactor) {
        Integer rgbSrc = BLEND_MAP.get(rgbSrcFactor);
        Integer rgbDst = BLEND_MAP.get(rgbDstFactor);
        Integer alphaSrc = BLEND_MAP.get(alphaSrcFactor);
        Integer alphaDst = BLEND_MAP.get(alphaDstFactor);
        gl20.glBlendFuncSeparate(rgbSrc, rgbDst, alphaSrc, alphaDst);
    }

    @Override
    protected void onBlendEquationSeparate(Equation rgbBlendEquation, Equation alphaBlendEquation) {
        Integer rgbEquation = EQUATION_MAP.get(rgbBlendEquation);
        Integer alphaEquation = EQUATION_MAP.get(alphaBlendEquation);
        gl20.glBlendEquationSeparate(rgbEquation, alphaEquation);
    }

    @Override
    protected void onBlendColor(float redColor, float greenColor, float blueColor, float alphaColor) {
        gl20.glBlendColor(redColor, greenColor, blueColor, alphaColor);
    }

    @Override
    public String toString() {
        return "GL20Blend@" + hashCode();
    }
}
