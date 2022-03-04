package com.jonanorman.android.renderclient;


import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;

import java.nio.ByteBuffer;
import java.util.Objects;

class GL20Attribute extends GLAttribute {

    private static final int STREAM_DRAW_COUNT = 6;
    private static final int DYNAMIC_DRAW_COUNT = 2;
    private static SparseArray<String> TYPE_NAME_MAP = new SparseArray();
    private static SparseIntArray TYPE_SIZE_MAP = new SparseIntArray();
    private static SparseBooleanArray TYPE_FLOAT_MAP = new SparseBooleanArray();

    static {
        TYPE_NAME_MAP.put(GL20.GL_BOOL, "bool");
        TYPE_NAME_MAP.put(GL20.GL_BOOL_VEC2, "bool2");
        TYPE_NAME_MAP.put(GL20.GL_BOOL_VEC3, "bool3");
        TYPE_NAME_MAP.put(GL20.GL_BOOL_VEC4, "bool4");
        TYPE_NAME_MAP.put(GL20.GL_INT, "int");
        TYPE_NAME_MAP.put(GL20.GL_INT_VEC2, "int2");
        TYPE_NAME_MAP.put(GL20.GL_INT_VEC3, "int3");
        TYPE_NAME_MAP.put(GL20.GL_INT_VEC4, "int4");
        TYPE_NAME_MAP.put(GL20.GL_FLOAT, "float");
        TYPE_NAME_MAP.put(GL20.GL_FLOAT_VEC2, "float2");
        TYPE_NAME_MAP.put(GL20.GL_FLOAT_VEC3, "float3");
        TYPE_NAME_MAP.put(GL20.GL_FLOAT_VEC4, "float4");
        TYPE_NAME_MAP.put(GL20.GL_FLOAT_MAT2, "mat2");
        TYPE_NAME_MAP.put(GL20.GL_FLOAT_MAT3, "mat3");
        TYPE_NAME_MAP.put(GL20.GL_FLOAT_MAT4, "mat4");
        TYPE_NAME_MAP.put(GL20.GL_SAMPLER_2D, "sampler2D");
        TYPE_NAME_MAP.put(GL20.GL_SAMPLER_EXTERNAL_OES, "samplerExternalOES");
        TYPE_NAME_MAP.put(GL20.GL_SAMPLER_CUBE, "samplerCube");


        TYPE_SIZE_MAP.put(GL20.GL_BOOL, 1);
        TYPE_SIZE_MAP.put(GL20.GL_BOOL_VEC2, 2);
        TYPE_SIZE_MAP.put(GL20.GL_BOOL_VEC3, 3);
        TYPE_SIZE_MAP.put(GL20.GL_BOOL_VEC4, 4);
        TYPE_SIZE_MAP.put(GL20.GL_INT, 1);
        TYPE_SIZE_MAP.put(GL20.GL_INT_VEC2, 2);
        TYPE_SIZE_MAP.put(GL20.GL_INT_VEC3, 3);
        TYPE_SIZE_MAP.put(GL20.GL_INT_VEC4, 4);
        TYPE_SIZE_MAP.put(GL20.GL_FLOAT, 1);
        TYPE_SIZE_MAP.put(GL20.GL_FLOAT_VEC2, 2);
        TYPE_SIZE_MAP.put(GL20.GL_FLOAT_VEC3, 3);
        TYPE_SIZE_MAP.put(GL20.GL_FLOAT_VEC4, 4);
        TYPE_SIZE_MAP.put(GL20.GL_FLOAT_MAT2, 4);
        TYPE_SIZE_MAP.put(GL20.GL_FLOAT_MAT3, 9);
        TYPE_SIZE_MAP.put(GL20.GL_FLOAT_MAT4, 16);
        TYPE_SIZE_MAP.put(GL20.GL_SAMPLER_2D, 1);
        TYPE_SIZE_MAP.put(GL20.GL_SAMPLER_EXTERNAL_OES, 1);
        TYPE_SIZE_MAP.put(GL20.GL_SAMPLER_CUBE, 1);


        TYPE_FLOAT_MAP.put(GL20.GL_BOOL, false);
        TYPE_FLOAT_MAP.put(GL20.GL_BOOL_VEC2, false);
        TYPE_FLOAT_MAP.put(GL20.GL_BOOL_VEC3, false);
        TYPE_FLOAT_MAP.put(GL20.GL_BOOL_VEC4, false);
        TYPE_FLOAT_MAP.put(GL20.GL_INT, false);
        TYPE_FLOAT_MAP.put(GL20.GL_INT_VEC2, false);
        TYPE_FLOAT_MAP.put(GL20.GL_INT_VEC3, false);
        TYPE_FLOAT_MAP.put(GL20.GL_INT_VEC4, false);
        TYPE_FLOAT_MAP.put(GL20.GL_FLOAT, true);
        TYPE_FLOAT_MAP.put(GL20.GL_FLOAT_VEC2, true);
        TYPE_FLOAT_MAP.put(GL20.GL_FLOAT_VEC3, true);
        TYPE_FLOAT_MAP.put(GL20.GL_FLOAT_VEC4, true);
        TYPE_FLOAT_MAP.put(GL20.GL_FLOAT_MAT2, true);
        TYPE_FLOAT_MAP.put(GL20.GL_FLOAT_MAT3, true);
        TYPE_FLOAT_MAP.put(GL20.GL_FLOAT_MAT4, true);
        TYPE_FLOAT_MAP.put(GL20.GL_SAMPLER_2D, false);
        TYPE_FLOAT_MAP.put(GL20.GL_SAMPLER_EXTERNAL_OES, false);
        TYPE_FLOAT_MAP.put(GL20.GL_SAMPLER_CUBE, false);
    }

    private final boolean typeFloat;
    private final int typeSize;
    private final String typeName;

    private int bufferId;
    private GL20 gl;
    private int changeCount = 0;

    GL20Attribute(GLRenderClient client, GLProgram program, int id, int type, String name, int length) {
        super(client, program, id, type, name, length);
        this.typeSize = TYPE_SIZE_MAP.get(type);
        this.typeName = TYPE_NAME_MAP.get(type);
        this.typeFloat = TYPE_FLOAT_MAP.get(type);
        this.gl = client.getGL20();
    }

    @Override
    protected void onCreate() {
        bufferId = gl.glGenBuffer();
        if (bufferId <= 0) {
            throw new RuntimeException("array buffer create fail");
        }
    }

    @Override
    protected void onDispose() {
        gl.glDeleteBuffer(bufferId);
    }


    @Override
    protected void onBufferUpdate(ByteBuffer buffer, boolean change) {
        if (change) {
            changeCount++;
            int usage = GL20.GL_STATIC_DRAW;
            if (changeCount > STREAM_DRAW_COUNT) {
                usage = GL20.GL_STREAM_DRAW;
            } else if (changeCount > DYNAMIC_DRAW_COUNT) {
                usage = GL20.GL_DYNAMIC_DRAW;
            }
            gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, bufferId);
            gl.glBufferData(GL20.GL_ARRAY_BUFFER, buffer.limit(), buffer, usage);
            gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        }
        gl.glEnableVertexAttribArray(getId());
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, bufferId);
        gl.glVertexAttribPointer(getId(), getTypeSize(), GL20.GL_FLOAT, false, 0, 0);// GLES20 only support float attribute
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
    }


    @Override
    public boolean isTypeFloat() {
        return typeFloat;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public int getTypeSize() {
        return typeSize;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GL20Attribute)) return false;
        if (!super.equals(o)) return false;
        GL20Attribute that = (GL20Attribute) o;
        return Objects.equals(gl, that.gl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gl);
    }
}
