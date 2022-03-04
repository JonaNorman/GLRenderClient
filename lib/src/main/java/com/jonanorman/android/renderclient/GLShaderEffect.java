package com.jonanorman.android.renderclient;


import com.jonanorman.android.renderclient.math.KeyframeSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GLShaderEffect extends GLEffect {


    private static final String VERTEX_SHADER = "precision highp float;\n" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "uniform mat4 positionMatrix;\n" +
            "uniform mat4 textureMatrix;\n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = positionMatrix*position;\n" +
            "    textureCoordinate =(textureMatrix*inputTextureCoordinate).xy;\n" +
            "}";


    private static final String FRAGMENT_SHADER = "precision mediump float;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform vec2 inputTextureSize;\n" +
            "uniform vec2 viewPortSize;\n" +
            "uniform float renderTime;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    vec4 color  = texture2D(inputImageTexture, textureCoordinate);\n" +
            "    gl_FragColor = color;\n" +
            "}";


    private String vertexShaderCode;
    private String fragmentShaderCode;

    private GLDrawType drawType = GLDrawType.DRAW_ARRAY;
    private GLDrawMode drawMode = GLDrawMode.TRIANGLE_STRIP;
    private int drawArrayStart;
    private int drawArrayCount = 4;
    private int[] drawElementIndices;

    private final GLShaderParam shaderParam;
    private final GLShaderParam defaultShaderParam;
    private Map<String, KeyframeSet> keyframesMap = new HashMap<>();


    protected GLShaderEffect(GLRenderClient client) {
        super(client);
        this.vertexShaderCode = VERTEX_SHADER;
        this.fragmentShaderCode = FRAGMENT_SHADER;
        this.shaderParam = client.newShaderParam();
        this.defaultShaderParam = client.newShaderParam();
    }


    @Override
    protected GLFrameBuffer renderEffect(GLFrameBuffer input) {
        return client.renderEffect(this, input);
    }

    protected void onRenderShaderEffect(GLFrameBuffer input) {
        int textureWidth = input.getWidth();
        int textureHeight = input.getHeight();
        GLTexture texture = input.getColorTexture();
        GLShaderParam shaderParam = getDefaultShaderParam();
        shaderParam.put("inputImageTexture", texture.getTextureId());
        shaderParam.put("inputTextureSize", textureWidth, textureHeight);
    }

    public void setKeyframes(String key, KeyframeSet keyframeSet) {
        Class valueType = keyframeSet.getValueType();
        if (valueType != int.class
                && valueType != float.class
                && valueType != float[].class
                && valueType != int[].class) {
            throw new RuntimeException("key frame set not support class " + valueType);
        }
        keyframesMap.put(key, keyframeSet);
    }

    public Set<String> getKeyNames() {
        return keyframesMap.keySet();
    }

    public KeyframeSet getKeyframes(String key) {
        return keyframesMap.get(key);
    }


    public void setFragmentShaderCode(String fragmentShaderCode) {
        this.fragmentShaderCode = fragmentShaderCode;
    }

    public void setVertexShaderCode(String vertexShaderCode) {
        this.vertexShaderCode = vertexShaderCode;
    }

    public String getVertexShaderCode() {
        return vertexShaderCode;
    }

    public String getFragmentShaderCode() {
        return fragmentShaderCode;
    }


    public void putShaderParam(String position, float... coordinates) {
        shaderParam.put(position, coordinates);
    }

    public void putShaderParam(String position, boolean coordinates) {
        shaderParam.put(position, coordinates);
    }

    public void putDefaultShaderParam(String position, float... coordinates) {
        defaultShaderParam.put(position, coordinates);
    }

    public void putDefaultShaderParam(String position, boolean coordinates) {
        defaultShaderParam.put(position, coordinates);
    }

    public void putShaderParam(GLShaderParam param) {
        shaderParam.put(param);
    }

    public void clearShaderParam() {
        shaderParam.clear();
    }


    public GLShaderParam getShaderParam() {
        return shaderParam;
    }

    public GLShaderParam getDefaultShaderParam() {
        return defaultShaderParam;
    }


    public GLDrawType getDrawType() {
        return drawType;
    }

    public void setDrawType(GLDrawType drawType) {
        this.drawType = drawType;
    }

    public GLDrawMode getDrawMode() {
        return drawMode;
    }

    public void setDrawMode(GLDrawMode drawMode) {
        this.drawMode = drawMode;
    }

    public int getDrawArrayStart() {
        return drawArrayStart;
    }

    public void setDrawArrayStart(int drawArrayStart) {
        this.drawArrayStart = drawArrayStart;
    }

    public int getDrawArrayCount() {
        return drawArrayCount;
    }

    public void setDrawArrayCount(int drawArrayCount) {
        this.drawArrayCount = drawArrayCount;
    }

    public int[] getDrawElementIndices() {
        return drawElementIndices;
    }

    public void setDrawElementIndices(int[] drawElementIndices) {
        this.drawElementIndices = drawElementIndices;
    }
}
