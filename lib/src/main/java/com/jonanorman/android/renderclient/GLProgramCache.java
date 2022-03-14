package com.jonanorman.android.renderclient;

import android.util.LruCache;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GLProgramCache extends GLObject {

    private static final int MAX_PROGRAM_SIZE = 20;
    private final Map<String, GLShader> shaderMap = new HashMap<>();
    private final Map<GLShader, Integer> shaderUsingMap = new HashMap<>();
    private final LruCache<Integer, GLProgram> programCache = new LruCache<Integer, GLProgram>(MAX_PROGRAM_SIZE) {
        @Override
        protected void entryRemoved(boolean evicted, Integer key, GLProgram oldValue, GLProgram newValue) {
            disposeProgram(oldValue);
        }
    };

    public GLProgramCache(GLRenderClient client) {
        super(client);
    }


    @Override
    protected void onCreate() {

    }

    @Override
    protected void onDispose() {
        for (Integer key : programCache.snapshot().keySet()) {
            programCache.remove(key);
        }
        shaderUsingMap.clear();
        shaderMap.clear();
    }

    public void setMaxCacheSize(int maxProgramSize) {
        programCache.resize(maxProgramSize);
    }


    public GLProgram obtain(String vertexShaderCode, String fragmentShaderCode) {
        if (isDisposed()) {
            throw new IllegalStateException("it is disposed");
        }
        create();
        GLShader vertexShader = shaderMap.get(vertexShaderCode);
        if (vertexShader == null) {
            vertexShader = client.newVertexShader();
            vertexShader.setShaderCode(vertexShaderCode);
            shaderMap.put(vertexShaderCode, vertexShader);
        }
        GLShader fragmentShader = shaderMap.get(fragmentShaderCode);
        if (fragmentShader == null) {
            fragmentShader = client.newFragmentShader();
            fragmentShader.setShaderCode(fragmentShaderCode);
            shaderMap.put(fragmentShaderCode, fragmentShader);
        }
        Integer programHashCode = Objects.hash(vertexShaderCode.hashCode(), fragmentShaderCode.hashCode());
        GLProgram program = programCache.get(programHashCode);
        if (program == null) {
            program = client.newProgram();
            GLDrawArray drawArray = client.newDrawArray();
            GLDrawElement drawElement = client.newDrawElement();
            program.setDrawArray(drawArray);
            program.setDrawElement(drawElement);
            program.setVertexShader(vertexShader);
            program.setFragmentShader(fragmentShader);
            programCache.put(programHashCode, program);
            Integer count = shaderUsingMap.get(vertexShader);
            if (count == null || count == 0) {
                shaderUsingMap.put(vertexShader, 1);
            } else {
                shaderUsingMap.put(vertexShader, count + 1);
            }
            count = shaderUsingMap.get(fragmentShader);
            if (count == null || count == 0) {
                shaderUsingMap.put(fragmentShader, 1);
            } else {
                shaderUsingMap.put(fragmentShader, count + 1);
            }
        }
        return program;
    }

    private void disposeProgram(GLProgram program) {
        program.dispose();
        disposeShader(program.getVertexShader());
        disposeShader(program.getFragmentShader());
    }

    private void disposeShader(GLShader shader) {
        Integer count = shaderUsingMap.get(shader);
        count = count == null ? 0 : count - 1;
        if (count <= 0) {
            shaderUsingMap.remove(shader);
            shaderMap.remove(shader.getCompileCode());
            shader.dispose();
        } else {
            shaderUsingMap.put(shader, count);
        }
    }
}
