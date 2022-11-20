package com.jonanorman.android.renderclient.layer;

import android.text.TextUtils;

import com.jonanorman.android.renderclient.math.Keyframe;
import com.jonanorman.android.renderclient.math.KeyframeSet;
import com.jonanorman.android.renderclient.math.Matrix4;
import com.jonanorman.android.renderclient.opengl.GLBlend;
import com.jonanorman.android.renderclient.opengl.GLDraw;
import com.jonanorman.android.renderclient.opengl.GLEnable;
import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLProgram;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLShader;
import com.jonanorman.android.renderclient.opengl.GLShaderParam;
import com.jonanorman.android.renderclient.opengl.GLShaderCache;
import com.jonanorman.android.renderclient.opengl.GLViewPort;
import com.jonanorman.android.renderclient.opengl.GLXfermode;
import com.jonanorman.android.renderclient.opengl.gl20.GL20Blend;
import com.jonanorman.android.renderclient.opengl.gl20.GL20Enable;
import com.jonanorman.android.renderclient.opengl.gl20.GL20Program;
import com.jonanorman.android.renderclient.opengl.gl20.GL20ShadeCache;
import com.jonanorman.android.renderclient.opengl.gl20.GL20ViewPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GLShaderLayer extends GLLayer {

    private static final String VERTEX_SHADER = "precision highp float;\n" +
            "attribute vec4 position;\n" +
            "uniform mat4 positionMatrix;\n" +
            "\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = positionMatrix*position;\n" +
            "}";


    private static final String FRAGMENT_SHADER = "precision mediump float;\n" +
            "uniform vec2 viewPortSize;\n" +
            "uniform float renderTime;\n" +
            "uniform float renderDuration;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_FragColor = vec4(0.0);\n" +
            "}";


    private static final float POSITION_COORDINATES[] = {
            -1.0f, -1.0f, 0.0f, 1.0f,//left bottom
            1.0f, -1.0f, 0.0f, 1.0f,//right bottom
            -1.0f, 1.0f, 0.0f, 1.0f, //left top
            1.0f, 1.0f, 0.0f, 1.0f//right top
    };


    public static final String KEY_RENDER_TIME = "renderTime";
    public static final String KEY_RENDER_DURATION = "renderDuration";
    public static final String KEY_VIEW_PORT_SIZE = "viewPortSize";
    public static final String KEY_POSITION = "position";
    public static final String KEY_POSITION_MATRIX = "positionMatrix";


    private static final int DRAW_ELEMENT_DATA_TYPE_BYTE = 0;
    private static final int DRAW_ELEMENT_DATA_TYPE_SHORT = 1;
    private static final int DRAW_ELEMENT_DATA_TYPE_INT = 2;


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

    private final List<GLEnable.Capability> enableCapList;
    private final List<GLEnable.Capability> disableCapList;
    private final GLShaderParam shaderParam;
    private final Keyframe resultKeyFrame;
    private final Matrix4 positionMatrix;


    private GLViewPort viewPort;
    private GLBlend blend;
    private GLEnable glEnable;
    private GLProgram program;

    private float[] positionCoordinates;

    public GLShaderLayer() {
        this(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public GLShaderLayer(String vertexShaderCode, String fragmentShaderCode) {
        super();
        this.vertexShaderCode = vertexShaderCode;
        this.fragmentShaderCode = fragmentShaderCode;
        this.shaderParam = new GLShaderParam();
        this.drawType = GLDraw.Type.DRAW_ARRAY;
        this.drawMode = GLDraw.Mode.TRIANGLE_STRIP;
        this.xfermode = GLXfermode.SRC_OVER;
        this.drawArrayCount = 4;
        this.enableCapList = new ArrayList<>();
        this.disableCapList = new ArrayList<>();
        this.positionCoordinates = POSITION_COORDINATES;
        this.positionMatrix = new Matrix4();
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
        this.program.dispose();
    }

    @Override
    protected boolean onRenderLayer(GLRenderClient client, GLFrameBuffer inputBuffer) {
        if (TextUtils.isEmpty(vertexShaderCode) ||
                TextUtils.isEmpty(fragmentShaderCode)) {
            return false;
        }
        int renderWidth = inputBuffer.getWidth();
        int renderHeight = inputBuffer.getHeight();
        GLShaderCache shaderPool = GL20ShadeCache.getCache(client);
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
        GLFrameBuffer old = inputBuffer.bind();
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
        putProgramParam(programParam);
        onRenderLayerParam(inputBuffer, programParam);
        putKeyFrameParam(programParam);
        programParam.set(shaderParam);
        onPostRenderLayerParam(inputBuffer, programParam);
        program.execute();
        old.bind();
        return true;
    }

    protected void onPostRenderLayerParam(GLFrameBuffer inputBuffer, GLShaderParam shaderParam) {

    }

    protected void onRenderLayerParam(GLFrameBuffer inputBuffer, GLShaderParam shaderParam) {

    }

    private void putKeyFrameParam(GLShaderParam programParam) {
        Set<String> keyNames = getKeyFrameSet();
        for (String key : keyNames) {
            KeyframeSet keyFrames = getKeyFrames(key);
            boolean success = keyFrames.getValue(getRenderTime(), getRenderDuration(), resultKeyFrame);
            if (success) {
                programParam.set(key, resultKeyFrame);
            }
        }
    }

    private void putProgramParam(GLShaderParam programParam) {
        programParam.set(KEY_RENDER_TIME, getRenderTime().toSecondsFloatValue());
        programParam.set(KEY_RENDER_DURATION, getRenderDuration().toSecondsFloatValue());
        programParam.set(KEY_VIEW_PORT_SIZE, viewPort.getWidth(), viewPort.getHeight());
        programParam.set(KEY_POSITION, positionCoordinates);
        programParam.set(KEY_POSITION_MATRIX, positionMatrix);
    }

    public void setPositionCoordinates(float[] positionCoordinates) {
        this.positionCoordinates = positionCoordinates;
    }

    public Matrix4 getPositionMatrix() {
        return positionMatrix;
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

    public float[] getPositionCoordinates() {
        return positionCoordinates;
    }

    public void putShaderParam(String position, float... data) {
        shaderParam.set(position, data);
    }


    public GLShaderParam getShaderParam() {
        return shaderParam;
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
