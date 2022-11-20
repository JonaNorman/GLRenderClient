package com.jonanorman.android.renderclient.opengl.gl20;


import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;

import com.jonanorman.android.renderclient.opengl.GLProgram;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLTexture;
import com.jonanorman.android.renderclient.opengl.GLUniform;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public final class GL20Uniform extends GLUniform {

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
    private GL20 gl20;
    private int[] samplerArr;
    private GLTexture preTexture;
    private int preTextureUnit;

    public GL20Uniform(GLRenderClient client, GLProgram program, int id, int type, String name, int length) {
        super(client, program, id, type, name, length);
        this.gl20 = getGL();
        this.typeSize = TYPE_SIZE_MAP.get(type);
        this.typeName = TYPE_NAME_MAP.get(type);
        this.typeFloat = TYPE_FLOAT_MAP.get(type);
        if (type == GL20.GL_SAMPLER_2D || type == GL20.GL_SAMPLER_EXTERNAL_OES || type == GL20.GL_SAMPLER_CUBE) {
            this.samplerArr = new int[length];
            for (int i = 0; i < samplerArr.length; i++) {
                samplerArr[i] = dequeueProgramTextureUnit();
            }
        }
        init();
    }


    @Override
    public void onBufferUpdate(ByteBuffer buffer, boolean change) {
        int type = getType();
        if (change) {
            IntBuffer intBuffer = buffer.asIntBuffer();
            FloatBuffer floatBuffer = buffer.asFloatBuffer();
            int id = getId();
            int length = getLength();
            switch (type) {
                case GL20.GL_BOOL:
                case GL20.GL_INT:
                    gl20.glUniform1iv(id, length, intBuffer);
                    break;
                case GL20.GL_SAMPLER_2D:
                case GL20.GL_SAMPLER_EXTERNAL_OES:
                    gl20.glUniform1iv(id, length, samplerArr, 0);
                    break;
                case GL20.GL_BOOL_VEC2:
                case GL20.GL_INT_VEC2:
                    gl20.glUniform2iv(id, length, intBuffer);
                    break;
                case GL20.GL_BOOL_VEC3:
                case GL20.GL_INT_VEC3:
                    gl20.glUniform3iv(id, length, intBuffer);
                    break;
                case GL20.GL_BOOL_VEC4:
                case GL20.GL_INT_VEC4:
                    gl20.glUniform4iv(id, length, intBuffer);
                    break;
                case GL20.GL_FLOAT:
                    gl20.glUniform1fv(id, length, floatBuffer);
                    break;
                case GL20.GL_FLOAT_VEC2:
                    gl20.glUniform2fv(id, length, floatBuffer);
                    break;
                case GL20.GL_FLOAT_VEC3:
                    gl20.glUniform3fv(id, length, floatBuffer);
                    break;
                case GL20.GL_FLOAT_VEC4:
                    gl20.glUniform4fv(id, length, floatBuffer);
                    break;
                case GL20.GL_FLOAT_MAT2:
                    gl20.glUniformMatrix2fv(id, length, false, floatBuffer);
                    break;
                case GL20.GL_FLOAT_MAT3:
                    gl20.glUniformMatrix3fv(id, length, false, floatBuffer);
                    break;
                case GL20.GL_FLOAT_MAT4:
                    gl20.glUniformMatrix4fv(id, length, false, floatBuffer);
                    break;
            }
        }

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
    protected void onBind() {
        preTexture = getClient().getCurrentTexture();
        preTextureUnit = getClient().getCurrentTextureUnit();

        int type = getType();
        switch (type) {
            case GL20.GL_SAMPLER_2D:
                for (int i = 0; i < getLength(); i++) {
                    gl20.glActiveTexture(GL20.GL_TEXTURE0 + samplerArr[i]);
                    gl20.glBindTexture(GL20.GL_TEXTURE_2D, (int) get(i));
                }
                break;
            case GL20.GL_SAMPLER_EXTERNAL_OES:
                for (int i = 0; i < getLength(); i++) {
                    gl20.glActiveTexture(GL20.GL_TEXTURE0 + samplerArr[i]);
                    gl20.glBindTexture(GL20.GL_TEXTURE_EXTERNAL_OES, (int) get(i));
                }
                break;
            case GL20.GL_SAMPLER_CUBE:
                for (int i = 0; i < getLength(); i++) {
                    gl20.glActiveTexture(GL20.GL_TEXTURE0 + samplerArr[i]);
                    gl20.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, (int) get(i));
                }
                break;
        }


    }

    @Override
    protected void onUnBind() {
        int type = getType();
        switch (type) {
            case GL20.GL_SAMPLER_2D:
                for (int i = 0; i < getLength(); i++) {
                    gl20.glActiveTexture(GL20.GL_TEXTURE0 + samplerArr[i]);
                    gl20.glBindTexture(GL20.GL_TEXTURE_2D, 0);
                }
                break;
            case GL20.GL_SAMPLER_EXTERNAL_OES:
                for (int i = 0; i < getLength(); i++) {
                    gl20.glActiveTexture(GL20.GL_TEXTURE0 + samplerArr[i]);
                    gl20.glBindTexture(GL20.GL_TEXTURE_EXTERNAL_OES, 0);
                }
                break;
            case GL20.GL_SAMPLER_CUBE:
                for (int i = 0; i < getLength(); i++) {
                    gl20.glActiveTexture(GL20.GL_TEXTURE0 + samplerArr[i]);
                    gl20.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, 0);
                }
                break;
        }
        if (getLength() > 0) {
            preTexture.active(preTextureUnit);
            preTexture.bind();
        }
    }


    @Override
    public String toString() {
        return "GL20Uniform[" +
                "typeName='" + typeName + '\'' +
                "name='" + getName() + '\'' +
                ']';
    }
}
