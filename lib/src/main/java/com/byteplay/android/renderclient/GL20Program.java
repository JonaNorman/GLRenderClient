package com.byteplay.android.renderclient;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

class GL20Program extends GLProgram {

    public static final String UNIFORM_ARRAY_SUFFIX = "[0]";
    private final Map<String, GLUniform> uniformMap = new HashMap<>();
    private final Map<String, GLAttribute> attributeMap = new HashMap<>();
    private final List<Integer> enableVertexList = new ArrayList<>();
    private int programId;
    private boolean linked;
    private String programVertexCode;
    private String programFragmentCode;
    private final GL20 gl;
    private int dequeueTextureUnit;

    public GL20Program(GLRenderClient client) {
        super(client);
        gl = client.getGL20();

    }


    @Override
    protected void onCreate() {
        programId = gl.glCreateProgram();
        if (programId <= 0) {
            throw new RuntimeException("programId create fail");
        }
    }

    @Override
    protected void onDispose() {
        GLShader vertexShader = getVertexShader();
        GLShader fragmentShader = getFragmentShader();
        if (vertexShader != null && vertexShader.isCreated()) {
            gl.glDetachShader(programId, vertexShader.getShaderId());
        }
        if (fragmentShader != null && vertexShader.isCreated()) {
            gl.glDetachShader(programId, fragmentShader.getShaderId());
        }
        gl.glDeleteProgram(programId);
        for (GLUniform uniform : uniformMap.values()) {
            uniform.dispose();
        }
        uniformMap.clear();
        for (GLAttribute attribute : attributeMap.values()) {
            attribute.dispose();
        }
        attributeMap.clear();
        linked = false;
    }


    @Override
    protected void onExecute(int programId) {
        GLShader vertexShader = getVertexShader();
        GLShader fragmentShader = getFragmentShader();
        GLDrawType drawType = getDrawType();
        if (drawType == null) {
            throw new NullPointerException("drawType is null");
        }
        GLDraw draw = drawType == GLDrawType.DRAW_ARRAY ? getDrawArray() : getDrawElement();
        if (draw == null) {
            throw new NullPointerException("draw is null");
        }
        if (vertexShader == null) {
            throw new NullPointerException("vertex shader is null");
        }
        if (fragmentShader == null) {
            throw new NullPointerException("fragment shader is null");
        }
        vertexShader.compile();
        fragmentShader.compile();
        if (!Objects.equals(vertexShader.getCompileCode(), programVertexCode)
                || !Objects.equals(fragmentShader.getCompileCode(), programFragmentCode)) {
            for (GLUniform uniform : uniformMap.values()) {
                uniform.dispose();
            }
            for (GLAttribute attribute : attributeMap.values()) {
                attribute.dispose();
            }
            uniformMap.clear();
            attributeMap.clear();
            gl.glAttachShader(programId, vertexShader.getShaderId());
            gl.glAttachShader(programId, fragmentShader.getShaderId());
            gl.glLinkProgram(programId);
            int linkStatus = gl.glGetProgram(programId, GL20.GL_LINK_STATUS);
            if (linkStatus != GL20.GL_TRUE) {
                throw new RuntimeException("could not link program: " + gl.glGetProgramInfoLog(programId));
            }
            int maxUniformCount = gl.glGetProgram(programId, GL20.GL_ACTIVE_UNIFORMS);
            int[] size = new int[1];
            int[] type = new int[1];
            for (int i = 0; i < maxUniformCount; i++) {
                programVertexCode = vertexShader.getCompileCode();
                programFragmentCode = fragmentShader.getCompileCode();
                String name = gl.glGetActiveUniform(programId, i, size, type);
                boolean hasArray = name.endsWith(UNIFORM_ARRAY_SUFFIX);
                String uniformName = hasArray ? name.substring(0, name.lastIndexOf(UNIFORM_ARRAY_SUFFIX)) : name;
                int location = gl.glGetUniformLocation(programId, uniformName);
                if (type[0] == GL20.GL_SAMPLER_2D) {
                    Pattern pattern = Pattern.compile("uniform\\s+samplerExternalOES\\s+" + uniformName);
                    if (pattern.matcher(programFragmentCode).find()) {// Correct problems in the simulator
                        type[0] = GL20.GL_SAMPLER_EXTERNAL_OES;
                    }
                }
                GLUniform uniform = new GL20Uniform(client, this, location, type[0], uniformName, size[0]);
                uniformMap.put(uniformName, uniform);
                if (hasArray) {
                    for (int j = 0; j < size[0]; j++) {
                        String arrayName = uniformName + "[" + j + "]";
                        int arrayLocation = gl.glGetUniformLocation(programId, arrayName);
                        GLUniform arrayUniform = new GL20Uniform(client, this, arrayLocation, type[0], arrayName, 1);// array data is limited to one
                        uniformMap.put(arrayName, arrayUniform);
                    }
                }
            }
            int maxAttributeCount = gl.glGetProgram(programId, GL20.GL_ACTIVE_ATTRIBUTES);
            for (int i = 0; i < maxAttributeCount; i++) {
                String name = gl.glGetActiveAttrib(programId, i, size, type);
                int location = gl.glGetAttribLocation(programId, name);
                GLAttribute attribute = new GL20Attribute(client, this, location, type[0], name, size[0]);
                attributeMap.put(name, attribute);
            }

            linked = true;
        }
        if (!linked) {
            throw new IllegalStateException("use program fail, please first link");
        }
        gl.glUseProgram(programId);
        enableVertexList.clear();
        GLShaderParam shaderParam = getShaderParam();
        if (shaderParam != null) {
            for (String key : shaderParam.ketSet()) {
                GLUniform uniform = uniformMap.get(key);
                if (uniform != null) {
                    uniform.update(shaderParam.get(key));
                }//In Opengl, the data is not set and the last data is used,you can use defaultShaderParam
                GLAttribute attribute = attributeMap.get(key);
                if (attribute != null) {
                    attribute.update(shaderParam.get(key));
                    enableVertexList.add(attribute.getId());
                }
            }
        }
        draw.draw();
        for (Integer id : enableVertexList) {
            gl.glDisableVertexAttribArray(id);
        }
        gl.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, 0);
        gl.glBindTexture(GL20.GL_TEXTURE_EXTERNAL_OES, 0);
        gl.glBindTexture(GL20.GL_TEXTURE_2D, 0);
        gl.glUseProgram(0);
    }

    @Override
    public int getProgramId() {
        return programId;
    }

    @Override
    protected int dequeueTextureUnit() {
        return dequeueTextureUnit++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GL20Program)) return false;
        if (!super.equals(o)) return false;
        GL20Program that = (GL20Program) o;
        return Objects.equals(programVertexCode, that.programVertexCode) && Objects.equals(programFragmentCode, that.programFragmentCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), programVertexCode, programFragmentCode);
    }
}
