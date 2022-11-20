package com.jonanorman.android.renderclient.opengl.gl20;


import android.text.TextUtils;

import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLShader;

import java.util.Objects;

public final class GL20Shader extends GLShader {

    private String compileCode;
    private boolean compiled;
    private GL20 gl;


    public GL20Shader(GLRenderClient client, Type type) {
        super(client, type);
        gl = getGL();
        init();

    }

    @Override
    protected int onShaderCreate(Type type) {
        int shaderId = 0;
        if (type == Type.VERTEX) {
            shaderId = gl.glCreateShader(GL20.GL_VERTEX_SHADER);
        } else if (type == Type.FRAGMENT) {
            shaderId = gl.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        }
        if (shaderId <= 0) {
            throw new IllegalStateException(this + " createShader fail");
        }
        return shaderId;
    }

    @Override
    protected void onShaderDispose(int shaderId) {
        gl.glDeleteShader(shaderId);
    }

    @Override
    public String getCompileCode() {
        return compileCode;
    }


    @Override
    public boolean isShaderUpdated() {
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
    public boolean isCompiled() {
        return compiled;
    }

    @Override
    protected void onShaderCompile(String shaderCode, int shaderId) {
        String shaderTypeName = getShaderType() == Type.VERTEX ? "vertexShader" : "fragmentShader";
        if (TextUtils.isEmpty(shaderCode)) {
            throw new IllegalArgumentException(this + " " + shaderTypeName + " code is empty");
        }
        gl.glShaderSource(shaderId, shaderCode);
        gl.glCompileShader(shaderId);
        int compileStatus = gl.glGetShaderiv(shaderId, GL20.GL_COMPILE_STATUS);
        if (compileStatus == GL20.GL_FALSE) {
            String error = gl.glGetShaderInfoLog(shaderId);
            throw new RuntimeException("compile error:\n " + error + "\n" + shaderCode);
        }
        compileCode = shaderCode;
        compiled = true;
    }

    @Override
    public String toString() {
        return "GL20Shader[" +
                "compileCode='" + compileCode + '\'' +
                ", shaderType=" + getShaderType() +
                ", shaderCode='" + getShaderCode() + '\'' +
                ", shaderId=" + getShaderId() +
                ']';
    }
}
