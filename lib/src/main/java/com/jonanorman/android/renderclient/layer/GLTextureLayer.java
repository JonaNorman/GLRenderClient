package com.jonanorman.android.renderclient.layer;


import com.jonanorman.android.renderclient.math.MathUtils;
import com.jonanorman.android.renderclient.math.Matrix4;
import com.jonanorman.android.renderclient.math.ScaleMode;
import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLShaderParam;
import com.jonanorman.android.renderclient.opengl.GLTexture;


public class GLTextureLayer extends GLShaderLayer {

    public static final String KEY_INPUT_TEXTURE_COORDINATE = "inputTextureCoordinate";
    public static final String KEY_MAX_MIPMAP_LEVEL = "maxMipmapLevel";
    public static final String KEY_MIPMAP_LEVEL = "mipmapLevel";
    public static final String KEY_INPUT_IMAGE_TEXTURE = "inputImageTexture";
    public static final String KEY_INPUT_TEXTURE_SIZE = "inputTextureSize";
    public static final String KEY_INPUT_TEXTURE_PRE_MUL = "inputTexturePreMul";
    public static final String KEY_SCALE_TYPE_MATRIX = "scaleTypeMatrix";
    public static final String KEY_INPUT_TEXTURE_MATRIX = "inputTextureMatrix";


    private static final String VERTEX_SHADER = "precision highp float;\n" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "uniform mat4 positionMatrix;\n" +
            "uniform mat4 scaleTypeMatrix;\n" +
            "uniform mat4 inputTextureMatrix;\n" +
            "varying vec2 textureCoordinate;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position =scaleTypeMatrix*positionMatrix*position;\n" +
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
            "uniform float renderDuration;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    vec4 color  = texture2D(inputImageTexture, textureCoordinate,mipmapLevel);\n" +
            "    if(!inputTexturePreMul) color = vec4(color.rgb*color.a,color.a);\n" +
            "    gl_FragColor = color;\n" +
            "}";




    private static final String OES_FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "uniform samplerExternalOES inputImageTexture;\n" +
            "uniform vec2 inputTextureSize;\n" +
            "uniform float maxMipmapLevel;\n" +
            "uniform float mipmapLevel;\n" +
            "uniform vec2 viewPortSize;\n" +
            "uniform bool inputTexturePreMul;\n" +
            "uniform float renderTime;\n" +
            "uniform float renderDuration;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    vec4 color  = texture2D(inputImageTexture, textureCoordinate,mipmapLevel);\n" +
            "    if(!inputTexturePreMul) color = vec4(color.rgb*color.a,color.a);\n" +
            "    gl_FragColor = color;\n" +
            "}";


    private static final float TEXTURE_COORDINATES[] = {
            0.0f, 0.0f, 0.0f, 1.0f,//left bottom
            1.0f, 0.0f, 0.0f, 1.0f,//right bottom
            0.0f, 1.0f, 0.0f, 1.0f,//left top
            1.0f, 1.0f, 0.0f, 1.0f,//right  top
    };

    private GLTexture texture;
    private ScaleMode textureScaleMode;
    private final Matrix4 scaleTypeMatrix;

    private float[] textureCoordinates;

    public GLTextureLayer() {
        this(false);
    }

    public GLTextureLayer(boolean oesShader) {
        super(VERTEX_SHADER, oesShader ? OES_FRAGMENT_SHADER : FRAGMENT_SHADER);
        this.textureCoordinates = TEXTURE_COORDINATES;
        this.scaleTypeMatrix = new Matrix4();
        this.textureScaleMode = ScaleMode.FIT;
    }

    public void setTexture(GLTexture texture) {
        this.texture = texture;
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

    public void setTextureCoordinates(float[] textureCoordinates) {
        this.textureCoordinates = textureCoordinates;
    }

    @Override
    protected boolean onRenderLayer(GLRenderClient client, GLFrameBuffer inputBuffer) {
        if (texture == null || !texture.isAvailable()) {
            return false;
        }
        return super.onRenderLayer(client, inputBuffer);
    }

    @Override
    protected void onRenderLayerParam(GLFrameBuffer inputBuffer, GLShaderParam shaderParam) {
        super.onRenderLayerParam(inputBuffer, shaderParam);
        loadScaleType();
        int textureWidth = texture.getWidth();
        int textureHeight = texture.getHeight();
        float renderWidth = getRenderWidth();
        float renderHeight = getRenderHeight();
        int maxMipmapLevel = Math.max(texture.getMaxMipmapLevel(), 0);
        float mipMapLevel = MathUtils.clamp(
                Math.max(renderWidth / textureWidth,
                        renderHeight / textureHeight) - 1.0f,
                0.0f, maxMipmapLevel);
        shaderParam.set(KEY_MAX_MIPMAP_LEVEL, maxMipmapLevel);
        shaderParam.set(KEY_MIPMAP_LEVEL, mipMapLevel);
        shaderParam.set(KEY_INPUT_IMAGE_TEXTURE, texture);
        shaderParam.set(KEY_INPUT_TEXTURE_SIZE, textureWidth, textureHeight);
        shaderParam.set(KEY_INPUT_TEXTURE_PRE_MUL, texture.isPremultiplied());
        shaderParam.set(KEY_SCALE_TYPE_MATRIX, scaleTypeMatrix);
        shaderParam.set(KEY_INPUT_TEXTURE_MATRIX, texture.getTextureMatrix());
        shaderParam.set(KEY_INPUT_TEXTURE_COORDINATE, textureCoordinates);

    }


    private void loadScaleType() {
        int textureWidth = texture.getWidth();
        int textureHeight = texture.getHeight();
        float renderWidth = getRenderWidth();
        float renderHeight = getRenderHeight();
        scaleTypeMatrix.clearIdentity();
        float viewportWidth = textureScaleMode.getWidth(textureWidth, textureHeight, renderWidth, renderHeight);
        float viewportHeight = textureScaleMode.getHeight(textureWidth, textureHeight, renderWidth, renderHeight);
        scaleTypeMatrix.scale(viewportWidth / renderWidth, viewportHeight / renderHeight, 1.0f);
    }
}
