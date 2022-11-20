package com.jonanorman.android.renderclient.opengl;

import com.jonanorman.android.renderclient.math.Keyframe;
import com.jonanorman.android.renderclient.math.Matrix4;
import com.jonanorman.android.renderclient.math.Vector3;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class GLShaderParam {

    private final Map<String, float[]> shaderParam = new HashMap<>();

    public GLShaderParam() {
        super();
    }

    public Set<String> keySet() {
        return shaderParam.keySet();
    }

    public void set(String key, float... data) {
        float[] floats = shaderParam.get(key);
        if (floats == null || floats.length != data.length) {
            floats = new float[data.length];
        }
        System.arraycopy(data, 0, floats, 0, data.length);
        shaderParam.put(key, floats);
    }

    public void set(String key, Matrix4 matrix) {
        set(key, matrix.get());
    }

    public void set(String key, GLTexture texture) {
        set(key, texture.getTextureId());
    }

    public void set(String key, Vector3 vector3) {
        set(key, vector3.getX(),vector3.getY(),vector3.getZ(),1.0f);
    }

    public void set(String key, Keyframe keyframe) {
        Object keyValue = keyframe.getValue();
        Class valueType = keyframe.getValueType();
        if (valueType == int.class) {
            set(key, (int) keyValue);
        } else if (valueType == float.class) {
            set(key, (float) keyValue);
        } else if (valueType == int[].class) {
            set(key, (float) keyValue);
        } else if (valueType == float[].class) {
            set(key, (float[]) keyValue);
        } else {
            throw new RuntimeException(this + " not support  keyframe for " + valueType);
        }
    }

    public void set(String key, int... data) {
        float[] floats = shaderParam.get(key);
        if (floats == null || floats.length != data.length) {
            floats = new float[data.length];
        }
        for (int i = 0; i < data.length; i++) {
            floats[i] = data[i];
        }
        shaderParam.put(key, floats);
    }

    public void set(String key, boolean data) {
        set(key, data ? 1 : 0);
    }


    public void set(String key, boolean... data) {
        float[] floats = shaderParam.get(key);
        if (floats == null || floats.length != data.length) {
            floats = new float[data.length];
        }
        for (int i = 0; i < data.length; i++) {
            floats[i] = data[i] ? 1 : 0;
        }
        set(key, floats);
    }

    public float[] get(String key) {
        return shaderParam.get(key);
    }

    public void remove(String key) {
        shaderParam.remove(key);
    }

    public void set(Map<String, float[]> param) {
        shaderParam.putAll(param);
    }

    public void set(GLShaderParam param) {
        set(param.shaderParam);
    }

    public void clear() {
        shaderParam.clear();
    }

    public boolean containsKey(String key){
        return shaderParam.containsKey(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GLShaderParam)) return false;
        if (!super.equals(o)) return false;
        GLShaderParam that = (GLShaderParam) o;
        return Objects.equals(shaderParam, that.shaderParam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shaderParam);
    }

    @Override
    public String toString() {
        return "GLShaderParam[" +
                "shaderParam=" + shaderParam +
                ']';
    }
}
