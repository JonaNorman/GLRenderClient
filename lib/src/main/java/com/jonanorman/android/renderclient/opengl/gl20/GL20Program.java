package com.jonanorman.android.renderclient.opengl.gl20;


import com.jonanorman.android.renderclient.opengl.GLAttribute;
import com.jonanorman.android.renderclient.opengl.GLDrawArray;
import com.jonanorman.android.renderclient.opengl.GLDrawElement;
import com.jonanorman.android.renderclient.opengl.GLFlush;
import com.jonanorman.android.renderclient.opengl.GLProgram;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLShaderParam;
import com.jonanorman.android.renderclient.opengl.GLUniform;
import com.jonanorman.android.renderclient.opengl.GLVariable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class GL20Program extends GLProgram {

    public static final String UNIFORM_ARRAY_SUFFIX = "[0]";
    private final Map<String, GLVariable> variableMap = new HashMap<>();

    private GL20 gl20;


    public GL20Program(GLRenderClient client) {
        super(client);
        gl20 = getGL();
        init();
    }

    @Override
    protected int onProgramCreate() {
        int programId = gl20.glCreateProgram();
        if (programId <= 0) {
            throw new RuntimeException(this + " programId create fail");
        }
        return programId;
    }

    @Override
    protected void onProgramDelete(int programId) {
        gl20.glDeleteProgram(programId);

    }

    @Override
    protected void onProgramDispose(int programId) {
        for (GLVariable variable : variableMap.values()) {
            variable.dispose();
        }
        variableMap.clear();
    }


    @Override
    protected void onProgramChange(int programId, String programVertexCode, String programFragmentCode) {
        for (GLVariable uniform : variableMap.values()) {
            uniform.dispose();
        }
        variableMap.clear();
        int maxUniformCount = gl20.glGetProgram(programId, GL20.GL_ACTIVE_UNIFORMS);
        int[] size = new int[1];
        int[] type = new int[1];
        for (int i = 0; i < maxUniformCount; i++) {
            String name = gl20.glGetActiveUniform(programId, i, size, type);
            boolean hasArray = name.endsWith(UNIFORM_ARRAY_SUFFIX);
            String uniformName = hasArray ? name.substring(0, name.lastIndexOf(UNIFORM_ARRAY_SUFFIX)) : name;
            int location = gl20.glGetUniformLocation(programId, uniformName);
            if (type[0] == GL20.GL_SAMPLER_2D) {
                Pattern pattern = Pattern.compile("uniform\\s+samplerExternalOES\\s+" + uniformName);
                if (pattern.matcher(programFragmentCode).find()) {// Correct problems in the simulator
                    type[0] = GL20.GL_SAMPLER_EXTERNAL_OES;
                }
            }
            GLUniform uniform = new GL20Uniform(getClient(), this, location, type[0], uniformName, size[0]);
            variableMap.put(uniformName, uniform);
            if (hasArray) {
                for (int j = 0; j < size[0]; j++) {
                    String arrayName = uniformName + "[" + j + "]";
                    int arrayLocation = gl20.glGetUniformLocation(programId, arrayName);
                    GLUniform arrayUniform = new GL20Uniform(getClient(), this, arrayLocation, type[0], arrayName, 1);// array data is limited to one
                    variableMap.put(arrayName, arrayUniform);
                }
            }
        }
        int maxAttributeCount = gl20.glGetProgram(programId, GL20.GL_ACTIVE_ATTRIBUTES);
        for (int i = 0; i < maxAttributeCount; i++) {
            String name = gl20.glGetActiveAttrib(programId, i, size, type);
            int location = gl20.glGetAttribLocation(programId, name);
            GLAttribute attribute = new GL20Attribute(getClient(), location, type[0], name, size[0]);
            variableMap.put(name, attribute);
        }
    }

    @Override
    protected void onLinkProgram(int programId) {
        gl20.glLinkProgram(programId);
        int linkStatus = gl20.glGetProgram(programId, GL20.GL_LINK_STATUS);
        if (linkStatus != GL20.GL_TRUE) {
            throw new RuntimeException(this + " could not link program: " + gl20.glGetProgramInfoLog(programId));
        }
    }

    @Override
    protected void onUseProgram(int programId) {
        gl20.glUseProgram(programId);
    }

    @Override
    protected void onProgramDraw(int programId) {
        GLShaderParam shaderParam = getShaderParam();
        for (String key : variableMap.keySet()) {
            GLVariable variable = variableMap.get(key);
            if (shaderParam.containsKey(key)) variable.update(shaderParam.get(key));
            variable.bind();
        }
        draw();

        for (String key : variableMap.keySet()) {
            GLVariable variable = variableMap.get(key);
            variable.unBind();
        }
    }

    @Override
    protected GLDrawArray onCreateDrawArray() {
        return new GL20DrawArray(getClient());
    }

    @Override
    protected GLDrawElement onCreateDrawElement() {
        return new GL20DrawElement(getClient());
    }

    @Override
    protected GLFlush onCreateFlush() {
        return new GL20Flush(getClient());
    }


    @Override
    protected void onAttachShader(int programId, int shaderId) {
        gl20.glAttachShader(programId, shaderId);
    }

    @Override
    protected void onDetachShader(int programId, int shaderId) {
        gl20.glDetachShader(programId, shaderId);
    }


    @Override
    public String toString() {
        return "GL20Program[" +
                "vertexCode='" + getCompileVertexCode() + '\'' +
                ", fragmentCode='" + getCompileFragmentCode() + '\'' +
                ']';
    }

}
