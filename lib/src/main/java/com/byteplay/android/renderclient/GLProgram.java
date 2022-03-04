package com.byteplay.android.renderclient;

import java.util.Objects;

public abstract class GLProgram extends GLObject {
    private GLShader vertexShader;
    private GLShader fragmentShader;
    private GLShaderParam shaderParam;
    private GLDrawArray drawArray;
    private GLDrawElement drawElement;
    private GLDrawType drawType;

    public GLProgram(GLRenderClient client) {
        super(client);
        registerMethod(GLExecuteMethod.class, new GLExecuteMethod());
        shaderParam = client.newShaderParam();
    }


    public void setVertexShader(GLShader vertexShader) {
        if (!vertexShader.isVertexShader()) {
            throw new IllegalArgumentException("must be vertex shader");
        }
        this.vertexShader = vertexShader;
    }

    public void setFragmentShader(GLShader fragmentShader) {
        if (!fragmentShader.isFragmentShader()) {
            throw new IllegalArgumentException("must be fragment shader");
        }
        this.fragmentShader = fragmentShader;
    }

    public void setDrawArray(GLDrawArray drawArray) {
        this.drawArray = drawArray;
    }


    public GLDrawArray getDrawArray() {
        return drawArray;
    }

    public GLDrawElement getDrawElement() {
        return drawElement;
    }

    public void setDrawElement(GLDrawElement drawElement) {
        this.drawElement = drawElement;
    }


    public void setDrawType(GLDrawType drawType) {
        this.drawType = drawType;
    }

    public GLDrawType getDrawType() {
        return drawType;
    }

    public GLShader getVertexShader() {
        return vertexShader;
    }

    public GLShader getFragmentShader() {
        return fragmentShader;
    }


    public final void execute() {
        findMethod(GLExecuteMethod.class).call();
    }

    protected abstract void onExecute(int programId);

    public void clearShaderParam() {
        this.shaderParam.clear();
    }

    public void put(GLShaderParam shaderParam) {
        this.shaderParam.put(shaderParam);
    }

    public GLShaderParam getShaderParam() {
        return shaderParam;
    }


    public abstract int getProgramId();

    protected abstract int dequeueTextureUnit();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GLProgram)) return false;
        if (!super.equals(o)) return false;
        GLProgram glProgram = (GLProgram) o;
        return Objects.equals(vertexShader, glProgram.vertexShader)
                && Objects.equals(fragmentShader, glProgram.fragmentShader)
                && Objects.equals(shaderParam, glProgram.shaderParam)
                && Objects.equals(drawType, glProgram.drawType)
                && Objects.equals(drawElement, glProgram.drawElement)
                && Objects.equals(drawArray, glProgram.drawArray)
                && Objects.equals(isCreated(), glProgram.isCreated())
                && Objects.equals(isDisposed(), glProgram.isDisposed());
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertexShader, fragmentShader, shaderParam, drawType, drawArray, drawElement, isCreated(), isDisposed());
    }

    class GLExecuteMethod extends GLMethod {
        public GLExecuteMethod() {
            super();
        }

        @Override
        protected void onCallMethod() {
            onExecute(getProgramId());
        }
    }


}
