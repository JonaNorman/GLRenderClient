package com.jonanorman.android.renderclient;


import android.text.TextUtils;

import java.util.Objects;

class GL20Shader extends GLShader {

    private int shaderId;
    private String compileCode;
    private boolean compiled;
    private GL20 gl;


    public GL20Shader(GLRenderClient client, GLShaderType type) {
        super(client, type);
        gl = client.getGL20();
    }


    @Override
    protected void onCreate() {
        GLShaderType shaderType = getShaderType();
        if (shaderType == GLShaderType.VERTEX) {
            shaderId = gl.glCreateShader(GL20.GL_VERTEX_SHADER);
        } else if (shaderType == GLShaderType.FRAGMENT) {
            shaderId = gl.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        }
        if (shaderId <= 0) {
            throw new IllegalStateException("shaderId create fail");
        }
    }

    @Override
    public String getCompileCode() {
        return compileCode;
    }

    @Override
    protected void onDispose() {
        gl.glDeleteShader(shaderId);
        compiled = false;
    }


    @Override
    public boolean hasNewCompile() {
        if (isDisposed()) {
            return false;
        }
        if (TextUtils.isEmpty(compileCode)) {
            return true;
        } else {
            return !Objects.equals(getShaderCode(), compileCode);
        }
    }

    @Override
    public int getShaderId() {
        return shaderId;
    }

    @Override
    public boolean isCompiled() {
        return compiled;
    }

    @Override
    protected void onShaderCompile(String shaderCode, int shaderId) {
        String shaderTypeName = getShaderTypeName();
        if (TextUtils.isEmpty(shaderCode)) {
            throw new IllegalArgumentException(shaderTypeName + " is empty");
        }
        gl.glShaderSource(shaderId, shaderCode);
        gl.glCompileShader(shaderId);
        int compileStatus = gl.glGetShaderiv(shaderId, GL20.GL_COMPILE_STATUS);
        if (compileStatus == GL20.GL_FALSE) {
            String error = gl.glGetShaderInfoLog(shaderId);
            throw new RuntimeException(shaderTypeName + " compile error: " + error + "\n" + shaderCode);
        }
        compileCode = shaderCode;
        compiled = true;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GL20Shader)) return false;
        if (!super.equals(o)) return false;
        GL20Shader that = (GL20Shader) o;
        return Objects.equals(compiled, that.compiled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), compiled);
    }

}
