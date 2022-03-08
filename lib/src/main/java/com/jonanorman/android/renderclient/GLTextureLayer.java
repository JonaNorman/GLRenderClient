package com.jonanorman.android.renderclient;


import com.jonanorman.android.renderclient.math.Matrix4;
import com.jonanorman.android.renderclient.math.ScaleMode;
import com.jonanorman.android.renderclient.utils.MathUtils;


public class GLTextureLayer extends GLShaderLayer {


    private static final String VERTEX_SHADER = "precision highp float;\n" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "uniform mat4 viewPortMatrix;\n" +
            "uniform mat4 scaleTypeMatrix;\n" +
            "uniform mat4 inputTextureMatrix;\n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position =scaleTypeMatrix*viewPortMatrix*position;\n" +
            "    textureCoordinate =(inputTextureMatrix*inputTextureCoordinate).xy;\n" +
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
    private ScaleMode textureScaleMode = ScaleMode.FIT;
    private Matrix4 scaleTypeMatrix = new Matrix4();

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

    public GLTexture getTexture() {
        return texture;
    }

    public void setTextureScaleMode(ScaleMode textureScaleMode) {
        this.textureScaleMode = textureScaleMode;
    }

    public ScaleMode getTextureScaleMode() {
        return textureScaleMode;
    }

    @Override
    protected void onLayerRenderSize(float renderWidth, float renderHeight, float parentWidth, float parentHeight) {
        super.onLayerRenderSize(renderWidth, renderHeight, parentWidth, parentHeight);
        scaleTypeMatrix.setIdentity();
        int textureWidth = getTextureWidth();
        int textureHeight = getTextureHeight();
        if (textureWidth != 0 && textureHeight != 0) {
            float viewportWidth = textureScaleMode.getWidth(textureWidth, textureHeight, renderWidth, renderHeight);
            float viewportHeight = textureScaleMode.getHeight(textureWidth, textureHeight, renderWidth, renderHeight);
            scaleTypeMatrix.scale(viewportWidth / renderWidth, viewportHeight / renderHeight, 1.0f);
        }
    }

    @Override
    protected boolean onShaderLayerRender(long renderTimeMs) {
        super.onShaderLayerRender(renderTimeMs);
        if (getTextureWidth() <= 0 || getTextureHeight() <= 0) {
            return false;
        }
        GLShaderParam shaderParam = getDefaultShaderParam();
        int textureWidth = getTextureWidth();
        int textureHeight = getTextureHeight();
        int maxMipmapLevel = Math.max(texture == null ? 0 : texture.getMaxMipmapLevel(), 0);
        float mipMapLevel = textureWidth > 0 && textureHeight > 0 ? 0 :
                (float) MathUtils.clamp(
                        Math.max(getRenderWidth() * 1.0 / textureWidth,
                                getRenderHeight() * 1.0 / textureHeight) - 1.0,
                        0.0, maxMipmapLevel);
        shaderParam.put("maxMipmapLevel", maxMipmapLevel);
        shaderParam.put("mipmapLevel", mipMapLevel);
        shaderParam.put("inputImageTexture", getTextureId());
        shaderParam.put("inputTextureSize", textureWidth, textureHeight);
        shaderParam.put("inputTexturePreMul", texture == null ? true : texture.isPremultiplied());
        shaderParam.put("scaleTypeMatrix", scaleTypeMatrix.get());
        if (texture != null) {
            shaderParam.put("inputTextureMatrix", texture.getTextureMatrix().get());
        }
        return true;
    }
}
