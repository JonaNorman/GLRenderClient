package com.byteplay.android.renderclient;


import com.byteplay.android.renderclient.math.MathUtils;
import com.byteplay.android.renderclient.math.Matrix4;


public class GLTextureLayer extends GLLayer {


    private static final float POSITION_COORDINATES[] = {
            -1.0f, -1.0f, 0.0f, 1.0f,//left bottom
            1.0f, -1.0f, 0.0f, 1.0f,//right bottom
            -1.0f, 1.0f, 0.0f, 1.0f, //left top
            1.0f, 1.0f, 0.0f, 1.0f//right top
    };

    private static final float TEXTURE_COORDINATES[] = {
            0.0f, 0.0f, 0.0f, 1.0f,//left bottom
            1.0f, 0.0f, 0.0f, 1.0f,//right bottom
            0.0f, 1.0f, 0.0f, 1.0f,//left top
            1.0f, 1.0f, 0.0f, 1.0f,//right  top
    };

    private static final String VERTEX_SHADER = "precision highp float;\n" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "uniform bool inputTextureFlipY;\n" +
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
    private GLScale scale = GLScale.FIT;
    private float[] positionCoordinates = new float[POSITION_COORDINATES.length];
    private float[] textureCoordinates = new float[TEXTURE_COORDINATES.length];
    private final Matrix4 positionMatrix = new Matrix4();

    protected GLTextureLayer(GLRenderClient client) {
        super(client, VERTEX_SHADER, FRAGMENT_SHADER, client.newDrawArray());
        setTextureCoordinate(TEXTURE_COORDINATES);
        setPositionCoordinate(POSITION_COORDINATES);
    }

    public void setTexture(GLTexture texture) {
        this.texture = texture;
    }

    public void setPositionCoordinate(float[] coordinates) {
        int newLength = coordinates.length;
        if (this.positionCoordinates.length != newLength) {
            this.positionCoordinates = new float[newLength];
        }
        System.arraycopy(coordinates, 0, this.positionCoordinates, 0, newLength);
    }

    public void setTextureCoordinate(float[] coordinates) {
        int newLength = coordinates.length;
        if (this.textureCoordinates.length != newLength) {
            this.textureCoordinates = new float[newLength];
        }
        System.arraycopy(coordinates, 0, this.textureCoordinates, 0, newLength);
    }

    public void setPositionMatrix(Matrix4 matrix) {
        positionMatrix.set(matrix);
    }

    public Matrix4 getPositionMatrix() {
        return positionMatrix;
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

    public void setScale(GLScale scale) {
        this.scale = scale;
    }

    public GLScale getScale() {
        return scale;
    }

    protected void onViewPort(GLViewPort viewPort, int frameWidth, int frameHeight) {
        int textureWidth = getTextureWidth();
        int textureHeight = getTextureHeight();
        if (textureWidth == 0 || textureHeight == 0) {
            super.onViewPort(viewPort, frameWidth, frameHeight);
            return;
        }
        if (scale != null) {
            int renderWidth = viewPort.getWidth();
            int renderHeight = viewPort.getHeight();
            int viewportWidth = (int) scale.getWidth(textureWidth, textureHeight, renderWidth, renderHeight);
            int viewportHeight = (int) scale.getHeight(textureWidth, textureHeight, renderWidth, renderHeight);
            viewPort.setWidth(viewportWidth);
            viewPort.setHeight(viewportHeight);
        }
        super.onViewPort(viewPort, frameWidth, frameHeight);
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
        float mipMapLevel = textureWidth > 0 && textureHeight > 0 ? 0 : (float) MathUtils.clamp(Math.max(layer.getViewWidth() * 1.0 / textureWidth, layer.getHeight() * 1.0 / textureHeight) - 1.0, 0.0, maxMipmapLevel);
        shaderParam.put("maxMipmapLevel", maxMipmapLevel);
        shaderParam.put("mipmapLevel", mipMapLevel);
        shaderParam.put("inputImageTexture", getTextureId());
        shaderParam.put("inputTextureSize", textureWidth, textureHeight);
        shaderParam.put("inputTexturePreMul", texture == null ? true : texture.isPremult());
        shaderParam.put("position", positionCoordinates);
        shaderParam.put("inputTextureCoordinate", textureCoordinates);
        shaderParam.put("positionMatrix", positionMatrix.get());
        shaderParam.put("textureMatrix", texture == null ? new Matrix4().get() : texture.getTextureMatrix().get());
        return true;
    }
}
