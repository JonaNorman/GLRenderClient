package com.jonanorman.android.renderclient.opengl;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

public abstract class GLProgramCache extends GLDispose {

    private static final int MAX_PROGRAM_SIZE = 50;
    private final Map<String, GLShader> shaderMap;
    private final Map<GLShader, Integer> shaderUsingMap;
    private final LinkedList<GLProgram> programQueue;
    private final Map<ShaderCode, GLProgram> programMap;
    private ShaderCode shaderCodeTemp;
    private int maxCacheSize;

    public GLProgramCache(GLRenderClient client) {
        super(client);
        shaderMap = new HashMap<>();
        shaderUsingMap = new HashMap<>();
        programQueue = new LinkedList<>();
        programMap = new HashMap<>();
        shaderCodeTemp = new ShaderCode();
        maxCacheSize = MAX_PROGRAM_SIZE;
    }


    @Override
    protected void onDispose() {
        for (GLProgram program : programQueue) {
            disposeProgram(program);
        }
        shaderUsingMap.clear();
        shaderMap.clear();
    }


    public void setMaxCacheSize(int maxProgramSize) {
        maxCacheSize = maxProgramSize;
    }


    public GLProgram obtain(String vertexShaderCode, String fragmentShaderCode) {
        if (TextUtils.isEmpty(vertexShaderCode)) {
            throw new NullPointerException(this + " vertexShaderCode is null");
        }
        if (TextUtils.isEmpty(fragmentShaderCode)) {
            throw new NullPointerException(this + " fragmentShaderCode is null");
        }
        checkDispose();
        GLShader vertexShader = shaderMap.get(vertexShaderCode);
        if (vertexShader == null) {
            vertexShader = onCreateVertexShader();
            vertexShader.setShaderCode(vertexShaderCode);
            shaderMap.put(vertexShaderCode, vertexShader);
        }
        GLShader fragmentShader = shaderMap.get(fragmentShaderCode);
        if (fragmentShader == null) {
            fragmentShader = onCreateFragmentShader();
            fragmentShader.setShaderCode(fragmentShaderCode);
            shaderMap.put(fragmentShaderCode, fragmentShader);
        }
        shaderCodeTemp.vertexCode = vertexShaderCode;
        shaderCodeTemp.fragmentCode = fragmentShaderCode;
        GLProgram program = programMap.get(shaderCodeTemp);
        if (program == null) {
            program = onCreateProgram();
            program.setVertexShader(vertexShader);
            program.setFragmentShader(fragmentShader);
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

    public void cache(GLProgram program) {
        if (program == null|| program.isDisposed()) return;
        checkDispose();
        programQueue.remove(program);
        programQueue.addLast(program);
        programMap.put(create(program), program);
        while (programQueue.size() > maxCacheSize) {
            GLProgram glProgram = programQueue.poll();
            disposeProgram(glProgram);
        }
    }

    protected abstract GLProgram onCreateProgram();

    protected abstract GLShader onCreateFragmentShader();

    protected abstract GLShader onCreateVertexShader();


    private void disposeProgram(GLProgram program) {
        program.dispose();
        ShaderCode shaderCode = create(program);
        programMap.remove(shaderCode);
        disposeShader(shaderCode.vertexCode);
        disposeShader(shaderCode.fragmentCode);
    }

    private void disposeShader(String code) {
        GLShader shader = shaderMap.get(code);
        if (shader == null) {
            return;
        }
        Integer count = shaderUsingMap.get(shader);
        count = count == null ? 0 : count - 1;
        if (count <= 0) {
            shaderUsingMap.remove(shader);
            shaderMap.remove(code);
            shader.dispose();
        } else {
            shaderUsingMap.put(shader, count);
        }
    }

    static class ShaderCode {
        String vertexCode;
        String fragmentCode;

        public ShaderCode(String vertexCode, String fragmentCode) {
            this.vertexCode = vertexCode;
            this.fragmentCode = fragmentCode;
        }

        public ShaderCode() {
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ShaderCode)) {
                return false;
            }
            ShaderCode p = (ShaderCode) o;
            return Objects.equals(p.vertexCode, vertexCode) && Objects.equals(p.fragmentCode, fragmentCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(vertexCode, fragmentCode);
        }


        @Override
        public String toString() {
            return "ShaderCode[" +
                    "vertexCode='" + vertexCode + '\'' +
                    ", fragmentCode='" + fragmentCode + '\'' +
                    ']';
        }
    }

    static ShaderCode create(GLProgram program) {
        String vertexCode = program.getCompileVertexCode();
        GLShader vertexShader = program.getVertexShader();
        if (TextUtils.isEmpty(vertexCode) && vertexShader != null) {
            vertexCode = vertexShader.getShaderCode();
        }
        String fragmentCode = program.getCompileFragmentCode();
        GLShader fragmentShader = program.getFragmentShader();
        if (TextUtils.isEmpty(fragmentCode) && fragmentShader != null) {
            fragmentCode = fragmentShader.getShaderCode();
        }
        return new ShaderCode(vertexCode, fragmentCode);
    }


}
