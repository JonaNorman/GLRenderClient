package com.jonanorman.android.renderclient.opengl;

import androidx.annotation.NonNull;

import java.util.Objects;

public abstract class GLProgram extends GLClass {
    private static final String KEY_GL_PROGRAM_COUNT = "key_gl_program_count";
    private static final int DRAW_ELEMENT_DATA_TYPE_BYTE = 0;
    private static final int DRAW_ELEMENT_DATA_TYPE_SHORT = 1;
    private static final int DRAW_ELEMENT_DATA_TYPE_INT = 2;

    private GLShader vertexShader;
    private GLShader fragmentShader;
    private GLShader attachVertexShader;
    private GLShader attachFragmentShader;

    private String compileVertexCode;
    private String compileFragmentCode;

    private GLShaderParam shaderParam;
    private GLDrawArray drawArray;
    private GLDrawElement drawElement;
    private GLDraw.Type drawType;
    private GLDraw.Mode drawMode;
    private GLExecuteMethod executeMethod;

    private int dequeueTextureUnit;
    private int programId;
    private int drawArrayStart;
    private int drawArrayCount;
    private int[] drawElementIntIndices;
    private short[] drawElementShortIndices;
    private byte[] drawElementByteIndices;
    private int drawElementDataType;
    private GLFlush flush;


    public GLProgram(GLRenderClient client) {
        super(client);
        shaderParam = new GLShaderParam();
        drawArray = onCreateDrawArray();
        drawElement = onCreateDrawElement();
        flush = onCreateFlush();
        drawType = GLDraw.Type.DRAW_ARRAY;
        drawMode = GLDraw.Mode.TRIANGLE_STRIP;
        drawArrayCount = 4;
    }


    @Override
    protected void onRegisterMethod() {
        executeMethod = new GLExecuteMethod();
    }

    @Override
    protected final void onClassInit() {
        programId = onProgramCreate();
        addProgramCount();

    }

    private void addProgramCount() {
        GLRenderClient client = getClient();
        Integer count = client.getExtraParam(KEY_GL_PROGRAM_COUNT);
        if (count == null) {
            count = 1;
        } else {
            count = count + 1;
        }
        client.putExtraParam(KEY_GL_PROGRAM_COUNT, count);
    }

    @Override
    protected final void onClassDispose() {
        if (attachVertexShader != null) {
            onDetachShader(programId, attachVertexShader.getShaderId());
        }
        if (attachFragmentShader != null) {
            onDetachShader(programId, attachFragmentShader.getShaderId());
        }
        onProgramDelete(programId);
        onProgramDispose(programId);
    }


    private void removeProgramCount() {
        GLRenderClient client = getClient();
        Integer count = client.getExtraParam(KEY_GL_PROGRAM_COUNT);
        if (count == null) {
            count = 0;
        } else {
            count = count <= 1 ? 0 : count - 1;
        }
        client.putExtraParam(KEY_GL_PROGRAM_COUNT, count);
    }

    int dequeueTextureUnit() {
        return dequeueTextureUnit++;
    }


    public final int getProgramId() {
        return programId;
    }


    public void setVertexShader(GLShader vertexShader) {
        if (vertexShader.getShaderType() != GLShader.Type.VERTEX) {
            throw new IllegalArgumentException(this + " must be vertex shader");
        }
        this.vertexShader = vertexShader;
    }

    public void setFragmentShader(GLShader fragmentShader) {
        if (fragmentShader.getShaderType() != GLShader.Type.FRAGMENT) {
            throw new IllegalArgumentException(this + " must be fragment shader");
        }
        this.fragmentShader = fragmentShader;
    }


    public void setDrawType(@NonNull GLDraw.Type drawType) {
        this.drawType = drawType;
    }

    public GLDraw.Type getDrawType() {
        return drawType;
    }


    public GLDraw.Mode getDrawMode() {
        return drawMode;
    }

    public void setDrawMode(@NonNull GLDraw.Mode drawMode) {
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

    public void setDrawElementIndices(@NonNull int[] drawElementIndices) {
        this.drawElementIntIndices = drawElementIndices;
        this.drawElementDataType = DRAW_ELEMENT_DATA_TYPE_INT;
    }

    public void setDrawElementIndices(@NonNull short[] drawElementIndices) {
        this.drawElementShortIndices = drawElementIndices;
        this.drawElementDataType = DRAW_ELEMENT_DATA_TYPE_SHORT;
    }

    public void setDrawElementIndices(@NonNull byte[] drawElementIndices) {
        this.drawElementByteIndices = drawElementIndices;
        this.drawElementDataType = DRAW_ELEMENT_DATA_TYPE_BYTE;
    }

    public GLShader getVertexShader() {
        return vertexShader;
    }

    public GLShader getFragmentShader() {
        return fragmentShader;
    }


    public final void execute() {
        executeMethod.apply();
    }

    public void put(GLShaderParam shaderParam) {
        this.shaderParam.set(shaderParam);
    }

    public GLShaderParam getShaderParam() {
        return shaderParam;
    }

    public GLShader getAttachFragmentShader() {
        return attachFragmentShader;
    }

    public GLShader getAttachVertexShader() {
        return attachVertexShader;
    }

    public String getCompileVertexCode() {
        return compileVertexCode;
    }

    public String getCompileFragmentCode() {
        return compileFragmentCode;
    }

    protected final void draw() {
        GLDraw draw = drawType == GLDraw.Type.DRAW_ARRAY ? drawArray : drawElement;
        draw.draw();
        flush.flush();
    }

    public static int getCurrentProgramCount(GLRenderClient renderClient) {
        Integer count = renderClient.getExtraParam(KEY_GL_PROGRAM_COUNT);
        return count == null ? 0 : count;
    }


    protected abstract void onProgramDispose(int programId);

    protected abstract void onProgramDelete(int programId);

    protected abstract int onProgramCreate();

    protected abstract GLDrawArray onCreateDrawArray();

    protected abstract GLDrawElement onCreateDrawElement();

    protected abstract GLFlush onCreateFlush();


    protected abstract void onDetachShader(int programId, int shaderId);

    protected abstract void onAttachShader(int programId, int shaderId);

    protected abstract void onLinkProgram(int programId);

    protected abstract void onProgramChange(int programId, String executeVertexCode, String executeFragmentCode);

    protected abstract void onProgramDraw(int programId);


    protected abstract void onUseProgram(int programId);


    class GLExecuteMethod extends GLMethod {
        public GLExecuteMethod() {
            super();
        }

        @Override
        protected void onMethodCall() {
            if (vertexShader == null) {
                throw new NullPointerException(this + " vertex shader is null");
            }
            if (fragmentShader == null) {
                throw new NullPointerException(this + " fragment shader is null");
            }
            vertexShader.compile();
            fragmentShader.compile();
            if (attachVertexShader == null || attachFragmentShader == null ||
                    !Objects.equals(vertexShader.getCompileCode(), compileVertexCode)
                    || !Objects.equals(fragmentShader.getCompileCode(), compileFragmentCode)) {
                onAttachShader(programId, vertexShader.getShaderId());
                onAttachShader(programId, fragmentShader.getShaderId());
                onLinkProgram(programId);
                attachVertexShader = vertexShader;
                attachFragmentShader = fragmentShader;
                compileVertexCode = attachVertexShader.getCompileCode();
                compileFragmentCode = attachFragmentShader.getCompileCode();
                onProgramChange(programId, vertexShader.getCompileCode(), fragmentShader.getCompileCode());
            }
            onUseProgram(programId);
            if (drawType == GLDraw.Type.DRAW_ARRAY) {
                drawArray.setMode(drawMode);
                drawArray.setVertexStart(drawArrayStart);
                drawArray.setVertexCount(drawArrayCount);
            } else if (drawType == GLDraw.Type.DRAW_ELEMENT) {
                drawElement.setMode(drawMode);
                if (drawElementDataType == DRAW_ELEMENT_DATA_TYPE_SHORT) {
                    drawElement.set(drawElementShortIndices);
                } else if (drawElementDataType == DRAW_ELEMENT_DATA_TYPE_INT) {
                    drawElement.set(drawElementIntIndices);
                } else {
                    drawElement.set(drawElementByteIndices);
                }
            }
            onProgramDraw(programId);
            onUseProgram(0);
        }
    }


}
