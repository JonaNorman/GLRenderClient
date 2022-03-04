package com.jonanorman.android.renderclient;

import android.util.ArrayMap;

import java.util.Objects;

class GL20Blend extends GLBlend {

    private static final ArrayMap<GLBLendFactor, Integer> BLEND_MAP = new ArrayMap<>();

    private static final ArrayMap<GLBlendEquation, Integer> EQUATION_MAP = new ArrayMap<>();

    static {
        BLEND_MAP.put(GLBLendFactor.ZERO, GL20.GL_ZERO);
        BLEND_MAP.put(GLBLendFactor.ONE, GL20.GL_ONE);
        BLEND_MAP.put(GLBLendFactor.SRC_COLOR, GL20.GL_SRC_COLOR);
        BLEND_MAP.put(GLBLendFactor.ONE_MINUS_SRC_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR);
        BLEND_MAP.put(GLBLendFactor.DST_COLOR, GL20.GL_DST_COLOR);
        BLEND_MAP.put(GLBLendFactor.ONE_MINUS_DST_COLOR, GL20.GL_ONE_MINUS_DST_COLOR);
        BLEND_MAP.put(GLBLendFactor.SRC_ALPHA, GL20.GL_SRC_ALPHA);
        BLEND_MAP.put(GLBLendFactor.ONE_MINUS_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        BLEND_MAP.put(GLBLendFactor.DST_ALPHA, GL20.GL_DST_ALPHA);
        BLEND_MAP.put(GLBLendFactor.ONE_MINUS_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
        BLEND_MAP.put(GLBLendFactor.CONSTANT_COLOR, GL20.GL_CONSTANT_COLOR);
        BLEND_MAP.put(GLBLendFactor.ONE_MINUS_CONSTANT_COLOR, GL20.GL_ONE_MINUS_CONSTANT_COLOR);
        BLEND_MAP.put(GLBLendFactor.CONSTANT_ALPHA, GL20.GL_CONSTANT_ALPHA);
        BLEND_MAP.put(GLBLendFactor.ONE_MINUS_CONSTANT_ALPHA, GL20.GL_ONE_MINUS_CONSTANT_ALPHA);
        EQUATION_MAP.put(GLBlendEquation.ADD, GL20.GL_FUNC_ADD);
        EQUATION_MAP.put(GLBlendEquation.SUBTRACT, GL20.GL_FUNC_SUBTRACT);
        EQUATION_MAP.put(GLBlendEquation.REVERSE_SUBTRACT, GL20.GL_FUNC_REVERSE_SUBTRACT);
    }

    private GL20 gl;

    public GL20Blend(GLRenderClient client) {
        super(client);
        gl = client.getGL20();
    }


    @Override
    protected void onBlendFunc(GLBLendFactor srcMode, GLBLendFactor dstMode) {
        Integer src = BLEND_MAP.get(srcMode);
        Integer dst = BLEND_MAP.get(dstMode);
        gl.glBlendFunc(src, dst);
    }

    @Override
    protected void onBlendEquation(GLBlendEquation blendEquation) {
        Integer equation = EQUATION_MAP.get(blendEquation);
        gl.glBlendEquation(equation);
    }

    @Override
    protected void onBlendFuncSeparate(GLBLendFactor rgbSrcFactor, GLBLendFactor rgbDstFactor, GLBLendFactor alphaSrcFactor, GLBLendFactor alphaDstFactor) {
        Integer rgbSrc = BLEND_MAP.get(rgbSrcFactor);
        Integer rgbDst = BLEND_MAP.get(rgbDstFactor);
        Integer alphaSrc = BLEND_MAP.get(alphaSrcFactor);
        Integer alphaDst = BLEND_MAP.get(alphaDstFactor);
        gl.glBlendFuncSeparate(rgbSrc, rgbDst, alphaSrc, alphaDst);
    }

    @Override
    protected void onBlendEquationSeparate(GLBlendEquation rgbBlendEquation, GLBlendEquation alphaBlendEquation) {
        Integer rgbEquation = EQUATION_MAP.get(rgbBlendEquation);
        Integer alphaEquation = EQUATION_MAP.get(alphaBlendEquation);
        gl.glBlendEquationSeparate(rgbEquation, alphaEquation);
    }

    @Override
    protected void onBlendColor(float redColor, float greenColor, float blueColor, float alphaColor) {
        gl.glBlendColor(redColor, greenColor, blueColor, alphaColor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GL20Blend)) return false;
        if (!super.equals(o)) return false;
        GL20Blend gl20Blend = (GL20Blend) o;
        return Objects.equals(gl, gl20Blend.gl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gl);
    }
}
