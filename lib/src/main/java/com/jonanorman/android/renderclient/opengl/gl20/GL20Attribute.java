package com.jonanorman.android.renderclient.opengl.gl20;


import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;

import com.jonanorman.android.renderclient.opengl.GLAttribute;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;

import java.nio.ByteBuffer;

public final class GL20Attribute extends GLAttribute {

    private static SparseArray<String> TYPE_NAME_MAP = new SparseArray();
    private static SparseIntArray TYPE_SIZE_MAP = new SparseIntArray();
    private static SparseBooleanArray TYPE_FLOAT_MAP = new SparseBooleanArray();

    private static final int STREAM_DRAW_COUNT = 6;
    private static final int DYNAMIC_DRAW_COUNT = 2;

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
    private GL20 gl20;

    private int bufferId;
    private int changeCount;


    public GL20Attribute(GLRenderClient client, int id, int type, String name, int length) {
        super(client, id, type, name, length);
        this.typeSize = TYPE_SIZE_MAP.get(type);
        this.typeName = TYPE_NAME_MAP.get(type);
        this.typeFloat = TYPE_FLOAT_MAP.get(type);
        this.gl20 = getGL();
        init();
    }


    @Override
    protected void onClassInit() {
        bufferId = gl20.glGenBuffer();
        if (bufferId <= 0) {
            throw new RuntimeException(this + " createBuffer fail");
        }
    }

    @Override
    protected void onClassDispose() {
        gl20.glDeleteBuffer(bufferId);
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
            gl20.glBindBuffer(GL20.GL_ARRAY_BUFFER, bufferId);
            gl20.glBufferData(GL20.GL_ARRAY_BUFFER, buffer.limit(), buffer, usage);
            gl20.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        }
    }

    @Override
    protected void onBind() {
        gl20.glEnableVertexAttribArray(getId());
        gl20.glBindBuffer(GL20.GL_ARRAY_BUFFER, bufferId);
        gl20.glVertexAttribPointer(getId(), getTypeSize(), GL20.GL_FLOAT, false, 0, 0);
        gl20.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
    }

    @Override
    protected void onUnBind() {
        gl20.glDisableVertexAttribArray(getId());
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
    public String toString() {
        return "GL20Attribute[" +
                "typeName='" + typeName + '\'' +
                "name='" + getName() + '\'' +
                ']';
    }
}
