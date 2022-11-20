package com.jonanorman.android.renderclient.opengl;

import android.text.TextUtils;

import java.util.Objects;

public abstract class GLShader extends GLClass {
    private final Type shaderType;
    private String shaderCode;
    private GLCompileMethod compileMethod;
    private int shaderId;

    public GLShader(GLRenderClient client, Type type) {
        super(client);
        this.shaderType = type;
    }

    @Override
    protected void onRegisterMethod() {
        compileMethod = new GLCompileMethod();
    }

    @Override
    protected final void onClassInit() {
        shaderId = onShaderCreate(shaderType);
    }


    @Override
    protected final void onClassDispose() {
        onShaderDispose(shaderId);
    }

    protected abstract int onShaderCreate(Type type);

    protected abstract void onShaderDispose(int shaderId);

    public final void compile() {
        compileMethod.apply();
    }

    public String getShaderCode() {
        return shaderCode;
    }

    public Type getShaderType() {
        return shaderType;
    }


    public void setShaderCode(String shaderCode) {
        this.shaderCode = shaderCode;
    }

    public abstract String getCompileCode();

    public abstract boolean isShaderUpdated();

    public int getShaderId() {
        return shaderId;
    }

    public abstract boolean isCompiled();

    protected abstract void onShaderCompile(String shaderCode, int shaderId);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GLShader)) return false;
        if (!super.equals(o)) return false;
        GLShader glShader = (GLShader) o;
        return shaderType == glShader.shaderType && Objects.equals(shaderCode, glShader.shaderCode) && Objects.equals(getCompileCode(), glShader.getCompileCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(shaderType, shaderCode, getCompileCode());
    }


    class GLCompileMethod extends GLMethod {

        public GLCompileMethod() {
            super();
        }

        @Override
        protected void onMethodCall() {
            if (!isShaderUpdated()) {
                return;
            }
            String shaderCode = getShaderCode();
            int shaderId = getShaderId();
            if (TextUtils.isEmpty(shaderCode)) {
                throw new IllegalArgumentException(GLShader.this + " shaderCode is empty");
            }
            onShaderCompile(shaderCode, shaderId);
        }
    }


    public enum Type {
        VERTEX,
        FRAGMENT
    }
}
