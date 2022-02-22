package com.byteplay.android.renderclient;

import android.text.TextUtils;

import java.util.Objects;

public abstract class GLShader extends GLObject {
    private final GLShaderType shaderType;
    private final boolean vertexType;
    private final boolean fragmentType;
    private final String shaderTypeName;
    private String shaderCode;

    public GLShader(GLRenderClient client, GLShaderType type) {
        super(client);
        shaderType = type;
        if (shaderType == GLShaderType.VERTEX) {
            vertexType = true;
            fragmentType = false;
        } else if (type == GLShaderType.FRAGMENT) {
            fragmentType = true;
            vertexType = false;
        } else {
            throw new IllegalArgumentException("shader type not support");
        }
        shaderTypeName = vertexType ? "vertex shader" : "fragment shader";
        registerMethod(GLCompileMethod.class, new GLCompileMethod());
    }

    public final void compile() {
        findMethod(GLCompileMethod.class).call();
    }

    public String getShaderCode() {
        return shaderCode;
    }

    public GLShaderType getShaderType() {
        return shaderType;
    }

    public boolean isVertexShader() {
        return vertexType;
    }

    public boolean isFragmentShader() {
        return fragmentType;
    }

    public void setShaderCode(String shaderCode) {
        this.shaderCode = shaderCode;
    }

    public String getShaderTypeName() {
        return shaderTypeName;
    }

    public abstract String getCompileCode();

    public abstract boolean hasNewCompile();

    public abstract int getShaderId();

    public abstract boolean isCompiled();

    protected abstract void onShaderCompile(String shaderCode, int shaderId);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GLShader)) return false;
        if (!super.equals(o)) return false;
        GLShader glShader = (GLShader) o;
        return shaderType == glShader.shaderType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), shaderType);
    }

    class GLCompileMethod extends GLMethod {

        public GLCompileMethod() {
            super();
        }

        @Override
        protected void onCallMethod() {
            if (!hasNewCompile()) {
                return;
            }
            String shaderCode = getShaderCode();
            int shaderId = getShaderId();
            if (TextUtils.isEmpty(shaderCode)) {
                throw new IllegalArgumentException(shaderTypeName + " is empty");
            }
            onShaderCompile(shaderCode, shaderId);
        }
    }


}
