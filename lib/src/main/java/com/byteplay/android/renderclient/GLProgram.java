package com.byteplay.android.renderclient;

import java.util.Objects;

public abstract class GLProgram extends GLObject {
    private GLShader vertexShader;
    private GLShader fragmentShader;
    private GLShaderParam shaderParam;
    private GLDraw draw;

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

    public GLShader getVertexShader() {
        return vertexShader;
    }

    public GLShader getFragmentShader() {
        return fragmentShader;
    }

    public GLDraw getDraw() {
        return draw;
    }

    public void setDraw(GLDraw draw) {
        this.draw = draw;
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
                && Objects.equals(draw, glProgram.draw)
                && Objects.equals(isCreated(), glProgram.isCreated())
                && Objects.equals(isDisposed(), glProgram.isDisposed());
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertexShader, fragmentShader, shaderParam, draw,isCreated(),isDisposed());
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
