package com.jonanorman.android.renderclient.layer;


import com.jonanorman.android.renderclient.math.Keyframe;
import com.jonanorman.android.renderclient.math.KeyframeSet;
import com.jonanorman.android.renderclient.math.Matrix4;
import com.jonanorman.android.renderclient.math.Vector3;
import com.jonanorman.android.renderclient.opengl.GLBlend;
import com.jonanorman.android.renderclient.opengl.GLDraw;
import com.jonanorman.android.renderclient.opengl.GLEnable;
import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLFrameBufferCache;
import com.jonanorman.android.renderclient.opengl.GLProgram;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLShader;
import com.jonanorman.android.renderclient.opengl.GLShaderCache;
import com.jonanorman.android.renderclient.opengl.GLShaderParam;
import com.jonanorman.android.renderclient.opengl.GLTexture;
import com.jonanorman.android.renderclient.opengl.GLViewPort;
import com.jonanorman.android.renderclient.opengl.GLXfermode;
import com.jonanorman.android.renderclient.opengl.gl20.GL20Blend;
import com.jonanorman.android.renderclient.opengl.gl20.GL20Enable;
import com.jonanorman.android.renderclient.opengl.gl20.GL20FrameBufferCache;
import com.jonanorman.android.renderclient.opengl.gl20.GL20Program;
import com.jonanorman.android.renderclient.opengl.gl20.GL20ShadeCache;
import com.jonanorman.android.renderclient.opengl.gl20.GL20ViewPort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GLShaderEffect extends GLEffect {

    public static final String KEY_RENDER_TIME = "renderTime";
    public static final String KEY_RENDER_DURATION = "renderDuration";
    public static final String KEY_VIEW_PORT_SIZE = "viewPortSize";
    public static final String KEY_POSITION = "position";
    public static final String KEY_INPUT_TEXTURE_COORDINATE = "inputTextureCoordinate";
    public static final String KEY_POSITION_MATRIX = "positionMatrix";
    public static final String KEY_TEXTURE_MATRIX = "textureMatrix";
    public static final String KEY_INPUT_IMAGE_TEXTURE = "inputImageTexture";
    public static final String KEY_INPUT_TEXTURE_SIZE = "inputTextureSize";

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


    private static final int DRAW_ELEMENT_DATA_TYPE_BYTE = 0;
    private static final int DRAW_ELEMENT_DATA_TYPE_SHORT = 1;
    private static final int DRAW_ELEMENT_DATA_TYPE_INT = 2;

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
            "uniform float renderDuration;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    vec4 color  = texture2D(inputImageTexture, textureCoordinate);\n" +
            "    gl_FragColor = color;\n" +
            "}";


    private String vertexShaderCode;
    private String fragmentShaderCode;

    private GLDraw.Type drawType;
    private GLDraw.Mode drawMode;
    private GLXfermode xfermode;
    private int drawArrayStart;
    private int drawArrayCount;
    private int[] drawElementIntIndices;
    private short[] drawElementShortIndices;
    private byte[] drawElementByteIndices;
    private int drawElementDataType;

    private GLViewPort viewPort;
    private GLEnable glEnable;
    private GLBlend blend;

    private final GLShaderParam shaderParam;
    private final Map<String, KeyframeSet> keyframesMap;

    private final Matrix4 positionMatrix;
    private final Matrix4 textureMatrix;
    private final Keyframe resultKeyFrame;
    private final List<GLEnable.Capability> enableCapList;
    private final List<GLEnable.Capability> disableCapList;

    private float[] positionCoordinates;
    private float[] textureCoordinates;
    private GLProgram program;


    public GLShaderEffect() {
        super();
        this.vertexShaderCode = VERTEX_SHADER;
        this.fragmentShaderCode = FRAGMENT_SHADER;
        this.shaderParam = new GLShaderParam();
        this.keyframesMap = new HashMap<>();
        this.drawType = GLDraw.Type.DRAW_ARRAY;
        this.drawMode = GLDraw.Mode.TRIANGLE_STRIP;
        this.xfermode = GLXfermode.SRC_OVER;
        this.drawArrayCount = 4;
        this.positionCoordinates = POSITION_COORDINATES;
        this.textureCoordinates = TEXTURE_COORDINATES;
        this.positionMatrix = new Matrix4();
        this.textureMatrix = new Matrix4();
        this.enableCapList = new ArrayList<>();
        this.disableCapList = new ArrayList<>();
        this.resultKeyFrame = Keyframe.ofValue(0, null, null);
    }

    @Override
    protected void onRenderInit(GLRenderClient client) {
        this.viewPort = new GL20ViewPort(client);
        this.glEnable = new GL20Enable(client);
        this.blend = new GL20Blend(client);
        this.program = new GL20Program(client);
    }

    @Override
    protected void onRenderClean(GLRenderClient client) {
        program.dispose();
    }

    @Override
    protected GLFrameBuffer onRenderEffect(GLRenderClient client, GLFrameBuffer inputBuffer) {
        int renderWidth = inputBuffer.getWidth();
        int renderHeight = inputBuffer.getHeight();
        GLFrameBufferCache frameBufferCache = GL20FrameBufferCache.getCache(client);
        GLShaderCache shaderPool = GL20ShadeCache.getCache(client);
        GLFrameBuffer effectBuffer = frameBufferCache.obtain(renderWidth, renderHeight);
        GLShader vertexShader = shaderPool.get(GLShader.Type.VERTEX, vertexShaderCode);
        GLShader fragmentShader = shaderPool.get(GLShader.Type.FRAGMENT, fragmentShaderCode);
        program.setVertexShader(vertexShader);
        program.setFragmentShader(fragmentShader);
        program.setDrawType(drawType);
        program.setDrawMode(drawMode);
        if (drawType == GLDraw.Type.DRAW_ARRAY) {
            program.setDrawArrayStart(drawArrayStart);
            program.setDrawArrayCount(drawArrayCount);
        } else if (drawType == GLDraw.Type.DRAW_ELEMENT) {
            if (drawElementDataType == DRAW_ELEMENT_DATA_TYPE_SHORT) {
                program.setDrawElementIndices(drawElementShortIndices);
            } else if (drawElementDataType == DRAW_ELEMENT_DATA_TYPE_INT) {
                program.setDrawElementIndices(drawElementIntIndices);
            } else {
                program.setDrawElementIndices(drawElementByteIndices);
            }
        }
        GLFrameBuffer preBuffer = effectBuffer.bind();
        xfermode.apply(blend);
        glEnable.addEnableCapability(enableCapList);
        glEnable.removeEnableCapability(disableCapList);
        glEnable.apply();
        viewPort.setX(0);
        viewPort.setY(0);
        viewPort.setWidth(renderWidth);
        viewPort.setHeight(renderHeight);
        viewPort.apply();
        GLShaderParam programParam = program.getShaderParam();
        programParam.clear();
        putProgramParam(inputBuffer, programParam);
        onRenderShaderEffect(inputBuffer, programParam);
        putKeyFrameParam(programParam);
        programParam.set(shaderParam);
        onPostRenderShaderEffect(inputBuffer, programParam);
        program.execute();
        preBuffer.bind();
        return effectBuffer;
    }


    private void putProgramParam(GLFrameBuffer input, GLShaderParam programParam) {
        programParam.set(KEY_RENDER_TIME, getRenderTime().toSecondsFloatValue());
        programParam.set(KEY_RENDER_DURATION, getRenderDuration().toSecondsFloatValue());
        programParam.set(KEY_VIEW_PORT_SIZE, viewPort.getWidth(), viewPort.getHeight());
        programParam.set(KEY_POSITION, positionCoordinates);
        programParam.set(KEY_INPUT_TEXTURE_COORDINATE, textureCoordinates);
        programParam.set(KEY_POSITION_MATRIX, positionMatrix);
        programParam.set(KEY_TEXTURE_MATRIX, textureMatrix);
        programParam.set(KEY_INPUT_IMAGE_TEXTURE, input.getAttachColorTexture());
        programParam.set(KEY_INPUT_TEXTURE_SIZE, input.getWidth(), input.getHeight());
    }

    private void putKeyFrameParam(GLShaderParam programParam) {
        for (String key : keyframesMap.keySet()) {
            KeyframeSet keyFrames = keyframesMap.get(key);
            boolean success = keyFrames.getValue(getRenderTime(), getRenderDuration(), resultKeyFrame);
            if (success) {
                programParam.set(key, resultKeyFrame);
            }
        }
    }

    protected void onRenderShaderEffect(GLFrameBuffer input, GLShaderParam shaderParam) {

    }

    protected void onPostRenderShaderEffect(GLFrameBuffer input, GLShaderParam shaderParam) {

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


    public GLShaderParam getShaderParam() {
        return shaderParam;
    }

    public void setShaderParam(String key, float... data) {
        shaderParam.set(key, data);
    }

    public void setShaderParam(String key, Matrix4 matrix) {
        shaderParam.set(key, matrix);
    }

    public void setShaderParam(String key, GLTexture texture) {
        shaderParam.set(key, texture);
    }

    public void setShaderParam(String key, Vector3 vector3) {
        shaderParam.set(key, vector3);
    }

    public void setShaderParam(String key, Keyframe keyframe) {
        shaderParam.set(key, keyframe);
    }

    public void setShaderParam(String key, int... data) {
        shaderParam.set(key, data);
    }

    public void setShaderParam(String key, boolean data) {
        shaderParam.set(key, data);
    }


    public void setShaderParam(String key, boolean... data) {
        shaderParam.set(key, data);
    }


    public void removeShaderParam(String key) {
        shaderParam.remove(key);
    }

    public void setShaderParam(Map<String, float[]> param) {
        shaderParam.set(param);
    }

    public void setShaderParam(GLShaderParam param) {
        shaderParam.set(param);
    }


    public boolean containsKey(String key) {
        return shaderParam.containsKey(key);
    }


    public Matrix4 getPositionMatrix() {
        return positionMatrix;
    }

    public Matrix4 getTextureMatrix() {
        return textureMatrix;
    }

    public void setPositionCoordinates(float[] positionCoordinates) {
        this.positionCoordinates = positionCoordinates;
    }

    public void setTextureCoordinates(float[] textureCoordinates) {
        this.textureCoordinates = textureCoordinates;
    }

    public float[] getPositionCoordinates() {
        return positionCoordinates;
    }

    public float[] getTextureCoordinates() {
        return textureCoordinates;
    }

    public GLDraw.Type getDrawType() {
        return drawType;
    }

    public void setDrawType(GLDraw.Type drawType) {
        this.drawType = drawType;
    }

    public GLDraw.Mode getDrawMode() {
        return drawMode;
    }

    public void setDrawMode(GLDraw.Mode drawMode) {
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


    public void setDrawElementIndices(int[] drawElementIndices) {
        this.drawElementIntIndices = drawElementIndices;
        this.drawElementDataType = DRAW_ELEMENT_DATA_TYPE_INT;
    }

    public void setDrawElementIndices(short[] drawElementIndices) {
        this.drawElementShortIndices = drawElementIndices;
        this.drawElementDataType = DRAW_ELEMENT_DATA_TYPE_SHORT;
    }

    public void setDrawElementIndices(byte[] drawElementIndices) {
        this.drawElementByteIndices = drawElementIndices;
        this.drawElementDataType = DRAW_ELEMENT_DATA_TYPE_BYTE;
    }

    public void setXfermode(GLXfermode xfermode) {
        this.xfermode = xfermode;
    }


    public void addEnableCapability(GLEnable.Capability capability) {
        if (capability == null) return;
        if (enableCapList.contains(capability)) {
            return;
        }
        enableCapList.add(capability);
        disableCapList.remove(capability);
    }

    public void removeEnableCapability(GLEnable.Capability capability) {
        if (capability == null) return;
        if (disableCapList.contains(capability)) {
            return;
        }
        disableCapList.add(capability);
        enableCapList.remove(capability);
    }

}
