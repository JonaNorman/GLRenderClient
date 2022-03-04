package com.byteplay.android.renderclient;


import com.byteplay.android.renderclient.math.ScaleMode;
import com.byteplay.android.renderclient.utils.MathUtils;


public class GLTextureLayer extends GLLayer {


    private static final String VERTEX_SHADER = "precision highp float;\n" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "uniform mat4 positionMatrix;\n" +
            "uniform mat4 viewPortMatrix;\n" +
            "uniform mat4 textureMatrix;\n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position =viewPortMatrix* positionMatrix*position;\n" +
            "    textureCoordinate =(textureMatrix*inputTextureCoordinate).xy;\n" +
            "}";


    private static final String FRAGMENT_SHADER = "precision mediump float;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform vec2 inputTextureSize;\n" +
            "uniform float maxMipmapLevel;\n" +
            "uniform float mipmapLevel;\n" +
            "uniform vec2 viewPortSize;\n" +
            "uniform bool inputTexturePreMul;\n" +
            "uniform float renderTime;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    vec4 color  = texture2D(inputImageTexture, textureCoordinate,mipmapLevel);\n" +
            "    if(!inputTexturePreMul) color = vec4(color.rgb*color.a,color.a);\n" +
            "    gl_FragColor = color;\n" +
            "}";

    private GLTexture texture;
    private ScaleMode scale = ScaleMode.FIT;

    protected GLTextureLayer(GLRenderClient client) {
        super(client, VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public void setTexture(GLTexture texture) {
        this.texture = texture;
    }


    public int getTextureWidth() {
        return texture != null ? texture.getWidth() : 0;
    }

    public int getTextureHeight() {
        return texture != null ? texture.getHeight() : 0;
    }

    public int getTextureId() {
        return texture == null ? 0 : texture.getTextureId();
    }

    public void setScale(ScaleMode scale) {
        this.scale = scale;
    }

    public ScaleMode getScale() {
        return scale;
    }


    protected void calculateViewPortMatrix(int frameWidth, int frameHeight) {
        int textureWidth = getTextureWidth();
        int textureHeight = getTextureHeight();
        if (textureWidth == 0 || textureHeight == 0) {
            super.calculateViewPortMatrix(frameWidth, frameHeight);
            return;
        }
        if (scale != null) {
            int renderWidth = getRenderWidth();
            int renderHeight = getRenderHeight();
            float viewportWidth = scale.getWidth(textureWidth, textureHeight, renderWidth, renderHeight);
            float viewportHeight = scale.getHeight(textureWidth, textureHeight, renderWidth, renderHeight);
            setRenderWidth((int) (viewportWidth + 0.5));
            setRenderHeight((int) (viewportHeight + 0.5));
        }
        super.calculateViewPortMatrix(frameWidth, frameHeight);
    }

    @Override
    protected boolean onRenderLayer(GLLayer layer, long renderTimeMs) {
        super.onRenderLayer(layer, renderTimeMs);
        if (texture == null) {
            return false;
        }
        GLShaderParam shaderParam = getDefaultShaderParam();
        int textureWidth = getTextureWidth();
        int textureHeight = getTextureHeight();
        int maxMipmapLevel = Math.max(texture == null ? 0 : texture.getMaxMipmapLevel(), 0);
        float mipMapLevel = textureWidth > 0 && textureHeight > 0 ? 0 :
                (float) MathUtils.clamp(
                        Math.max(layer.getRenderWidth() * 1.0 / textureWidth,
                                layer.getHeight() * 1.0 / textureHeight) - 1.0,
                        0.0, maxMipmapLevel);
        shaderParam.put("maxMipmapLevel", maxMipmapLevel);
        shaderParam.put("mipmapLevel", mipMapLevel);
        shaderParam.put("inputImageTexture", getTextureId());
        shaderParam.put("inputTextureSize", textureWidth, textureHeight);
        shaderParam.put("inputTexturePreMul", texture == null ? true : texture.isPremultiplied());
        if (texture != null) {
            shaderParam.put("textureMatrix", texture.getTextureMatrix().get());//todo
        }
        return true;
    }
}
