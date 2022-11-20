package com.jonanorman.android.renderclient.opengl.gl20;

import androidx.annotation.NonNull;

import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLShader;
import com.jonanorman.android.renderclient.opengl.GLShaderCache;

public class GL20ShadeCache extends GLShaderCache {

    private static final String KEY_PROGRAM_CACHE_GL20 = "key_shader_pool_gl20";


    public GL20ShadeCache(GLRenderClient client) {
        super(client);
    }


    @Override
    protected GLShader onCreateShader(GLShader.Type type) {
        return new GL20Shader(getClient(), type);
    }

    public static GLShaderCache getCache(GLRenderClient renderClient) {
        GLShaderCache shaderCache = renderClient.getExtraParam(KEY_PROGRAM_CACHE_GL20);
        if (shaderCache != null) {
            return shaderCache;
        }
        shaderCache = new GL20ShadeCache(renderClient);
        renderClient.putExtraParam(KEY_PROGRAM_CACHE_GL20, shaderCache);
        return shaderCache;
    }

    public static GLShaderCache getCurrentCache() {
        return getCache(GLRenderClient.getCurrentClient());
    }

    @NonNull
    @Override
    public String toString() {
        return "GL20ShaderCache@" + hashCode();
    }
}
