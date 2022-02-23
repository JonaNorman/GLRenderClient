package com.byteplay.android.renderclient;


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
    private GLDraw draw;
    private final GLShaderParam shaderParam;
    private final GLShaderParam defaultShaderParam;
    private Map<String, GLKeyframeSet> keyframesMap = new HashMap<>();


    protected GLShaderEffect(GLRenderClient client) {
        super(client);
        this.draw = client.newDrawArray();
        this.vertexShaderCode = VERTEX_SHADER;
        this.fragmentShaderCode = FRAGMENT_SHADER;
        this.shaderParam = client.newShaderParam();
        this.defaultShaderParam = client.newShaderParam();
    }

    @Override
    protected GLFrameBuffer actualApplyEffect(GLEffect effect, GLFrameBuffer input, long timeMs) {
        return client.applyEffect((GLShaderEffect) effect, input, timeMs);
    }

    protected void onApplyShaderEffect(GLShaderEffect effect, GLFrameBuffer input, long timeMs) {
        int textureWidth = input.getWidth();
        int textureHeight = input.getHeight();
        GLTexture texture = input.getColorTexture();
        GLShaderParam shaderParam = effect.getDefaultShaderParam();
        shaderParam.put("inputImageTexture", texture.getTextureId());
        shaderParam.put("inputTextureSize", textureWidth, textureHeight);
    }


    public void setKeyframe(String key, GLKeyframeSet keyframeSet) {
        keyframesMap.put(key, keyframeSet);
    }

    public Set<String> getFrameKeySet() {
        return keyframesMap.keySet();
    }

    public GLKeyframeSet getKeyframes(String key) {
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


    public void setDraw(GLDraw draw) {
        this.draw = draw;
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


    public GLDraw getDraw() {
        return draw;
    }

}
