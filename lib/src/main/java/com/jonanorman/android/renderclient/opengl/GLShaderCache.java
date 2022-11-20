package com.jonanorman.android.renderclient.opengl;

import android.text.TextUtils;

import java.util.LinkedHashMap;

public abstract class GLShaderCache extends GLDispose {


    private final LinkedHashMap<String, GLShader> shaderMap;

    public GLShaderCache(GLRenderClient client) {
        super(client);
        shaderMap = new LinkedHashMap<>();
    }


    @Override
    protected void onDispose() {
        for (GLShader shader : shaderMap.values()) {
            shader.dispose();
        }
        shaderMap.clear();
    }


    public GLShader get(GLShader.Type type, String shaderCode) {
        if (TextUtils.isEmpty(shaderCode)) {
            throw new NullPointerException(this + " vertexShaderCode is null");
        }
        checkDispose();
        GLShader shader = shaderMap.get(shaderCode);
        if (shader == null || shader.isDisposed()) {
            shader = onCreateShader(type);
            shader.setShaderCode(shaderCode);
            shaderMap.put(shaderCode, shader);
        }
        return shader;
    }

    protected abstract GLShader onCreateShader(GLShader.Type type);


}
