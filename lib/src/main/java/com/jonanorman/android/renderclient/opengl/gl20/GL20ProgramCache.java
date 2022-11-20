package com.jonanorman.android.renderclient.opengl.gl20;

import androidx.annotation.NonNull;

import com.jonanorman.android.renderclient.opengl.GLProgram;
import com.jonanorman.android.renderclient.opengl.GLProgramCache;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLShader;

public class GL20ProgramCache extends GLProgramCache {

    private static final String KEY_PROGRAM_CACHE_GL20 = "key_program_cache_gl20";


    public GL20ProgramCache(GLRenderClient client) {
        super(client);
    }

    @Override
    protected GLProgram onCreateProgram() {
        return new GL20Program(getClient());
    }

    @Override
    protected GLShader onCreateFragmentShader() {
        return new GL20Shader(getClient(), GLShader.Type.FRAGMENT);
    }

    @Override
    protected GLShader onCreateVertexShader() {
        return new GL20Shader(getClient(), GLShader.Type.VERTEX);
    }

    public static GLProgramCache getCache(GLRenderClient renderClient) {
        GLProgramCache programCache = renderClient.getExtraParam(KEY_PROGRAM_CACHE_GL20);
        if (programCache != null) {
            return programCache;
        }
        programCache = new GL20ProgramCache(renderClient);
        renderClient.putExtraParam(KEY_PROGRAM_CACHE_GL20, programCache);
        return programCache;
    }

    public static GLProgramCache getCurrentCache() {
        return getCache(GLRenderClient.getCurrentClient());
    }

    @NonNull
    @Override
    public String toString() {
        return "GL20ProgramCache@" + hashCode();
    }
}
