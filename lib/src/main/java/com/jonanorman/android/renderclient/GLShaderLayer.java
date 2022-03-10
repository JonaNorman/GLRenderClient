package com.jonanorman.android.renderclient;

import com.jonanorman.android.renderclient.math.KeyframeSet;
import com.jonanorman.android.renderclient.math.Matrix4;

public class GLShaderLayer extends GLLayer {


    public static final Matrix4 DEFAULT_MATRIX = new Matrix4();

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

    public static final String KEY_RENDER_TIME = "renderTime";
    public static final String KEY_VIEW_PORT_SIZE = "viewPortSize";
    public static final String KEY_POSITION = "position";
    public static final String KEY_INPUT_TEXTURE_COORDINATE = "inputTextureCoordinate";
    public static final String KEY_POSITION_MATRIX = "positionMatrix";
    public static final String KEY_TEXTURE_MATRIX = "textureMatrix";
    public static final String KEY_VIEW_PORT_MATRIX_MATRIX = "viewPortMatrix";


    private static final int DRAW_ELEMENT_DATA_TYPE_BYTE = 0;
    private static final int DRAW_ELEMENT_DATA_TYPE_SHORT = 1;
    private static final int DRAW_ELEMENT_DATA_TYPE_INT = 2;


    String vertexShaderCode;
    String fragmentShaderCode;
    private GLShaderParam shaderParam;
    private GLShaderParam defaultShaderParam;
    private GLEnable enable;
    private GLDrawType drawType = GLDrawType.DRAW_ARRAY;
    private GLDrawMode drawMode = GLDrawMode.TRIANGLE_STRIP;
    private int drawArrayStart;
    private int drawArrayCount = 4;
    private int[] drawElementIntIndices;
    private short[] drawElementShortIndices;
    private byte[] drawElementByteIndices;
    private int drawElementDataType;


    private GLViewPort viewPort;
    private GLBlend blend;


    protected GLShaderLayer(GLRenderClient client, String vertexShaderCode, String fragmentShaderCode) {
        super(client);
        this.enable = client.newEnable();
        this.vertexShaderCode = vertexShaderCode;
        this.fragmentShaderCode = fragmentShaderCode;
        this.shaderParam = client.newShaderParam();
        this.defaultShaderParam = client.newShaderParam();
        this.effectGroup = client.newEffectSet();
        this.viewPort = client.newViewPort();
        this.blend = client.newBlend();
    }

    @Override
    final protected void onRenderLayer(int currentWidth, int currentHeight, GLFrameBuffer outputBuffer) {
        if (effectGroup.isRenderEnable()) {
            GLFrameBufferCache frameBufferCache = client.getFrameBufferCache();
            GLFrameBuffer currentFrameBuffer = frameBufferCache.obtain(currentWidth, currentHeight);
            drawLayer(currentFrameBuffer, DEFAULT_MATRIX, GLXfermode.SRC_OVER, renderTime);
            GLFrameBuffer effectBuffer = effectGroup.renderEffect(currentFrameBuffer);
            if (currentFrameBuffer != effectBuffer) {
                frameBufferCache.cache(currentFrameBuffer);
            }
            GLTexture effectTexture = effectBuffer.getColorTexture();
            client.drawTexture(outputBuffer, viewPortMatrix, xfermode, effectTexture);
            frameBufferCache.cache(effectBuffer);
            return;
        }
        drawLayer(outputBuffer, viewPortMatrix, xfermode, renderTime);
    }

    void drawLayer(GLFrameBuffer outputBuffer, Matrix4 viewPortMatrix, GLXfermode xfermode, long renderTimeMs) {
        GLProgramCache programCache = client.getProgramCache();
        GLProgram program = programCache.obtain(vertexShaderCode, fragmentShaderCode);
        program.setDrawType(drawType);
        if (drawType == GLDrawType.DRAW_ARRAY) {
            GLDrawArray drawArray = program.getDrawArray();
            drawArray.setDrawMode(getDrawMode());
            drawArray.setVertexStart(getDrawArrayStart());
            drawArray.setVertexCount(getDrawArrayCount());
            program.setDrawType(getDrawType());
        } else if (drawType == GLDrawType.DRAW_ELEMENT) {
            GLDrawElement drawElement = program.getDrawElement();
            drawElement.setDrawMode(getDrawMode());
            if (drawElementDataType == DRAW_ELEMENT_DATA_TYPE_SHORT) {
                drawElement.set(drawElementShortIndices);
            } else if (drawElementDataType == DRAW_ELEMENT_DATA_TYPE_INT) {
                drawElement.set(drawElementIntIndices);
            } else {
                drawElement.set(drawElementByteIndices);
            }

            program.setDrawType(getDrawType());
        }
        GLFrameBuffer old = outputBuffer.bind();
        enable.call();
        viewPort.set(0, 0, outputBuffer.getWidth(), outputBuffer.getHeight());
        viewPort.call();
        xfermode.apply(blend);
        blend.call();
        program.clearShaderParam();
        GLShaderParam programParam = program.getShaderParam();
        programParam.put(KEY_RENDER_TIME, renderTimeMs / 1000.0f);
        programParam.put(KEY_VIEW_PORT_SIZE, viewPort.getWidth(), viewPort.getHeight());
        programParam.put(KEY_POSITION, POSITION_COORDINATES);
        programParam.put(KEY_INPUT_TEXTURE_COORDINATE, TEXTURE_COORDINATES);
        programParam.put(KEY_POSITION_MATRIX, DEFAULT_MATRIX.get());
        programParam.put(KEY_TEXTURE_MATRIX, DEFAULT_MATRIX.get());
        programParam.put(KEY_VIEW_PORT_MATRIX_MATRIX, viewPortMatrix.get());
        boolean render = onShaderLayerRender(renderTimeMs);
        if (!render) {
            old.bind();
            return;
        }
        programParam.put(defaultShaderParam);
        for (String key : getKeyNames()) {
            KeyframeSet keyFrames = getKeyFrames(key);
            if (keyFrames != null) {
                Object keyValue = keyFrames.getValueByTime(renderTimeMs, getRenderDuration());
                if (keyValue != null) {
                    Class valueType = keyFrames.getValueType();
                    if (valueType == int.class) {
                        programParam.put(key, (int) keyValue);
                    } else if (valueType == float.class) {
                        programParam.put(key, (float) keyValue);
                    } else if (valueType == int[].class) {
                        programParam.put(key, (float) keyValue);
                    } else if (valueType == float[].class) {
                        programParam.put(key, (float[]) keyValue);
                    }
                }
            }
        }
        programParam.put(shaderParam);
        program.execute();
        GLRenderSurface eglSurface = outputBuffer.getRenderSurface();
        eglSurface.setTime(renderTimeMs * 1000000L);
        old.bind();
    }


    public GLEnable getGLEnable() {
        return enable;
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


    public void putShaderParam(String position, float... coordinates) {
        shaderParam.put(position, coordinates);
    }

    public void putShaderParam(String position, boolean coordinates) {
        shaderParam.put(position, coordinates);
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


    protected boolean onShaderLayerRender(long renderTimeMs) {
        return true;
    }

}
