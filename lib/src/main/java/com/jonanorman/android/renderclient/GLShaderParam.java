package com.jonanorman.android.renderclient;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class GLShaderParam {

    private final Map<String, float[]> shaderParam = new HashMap<>();

    protected GLShaderParam() {
        super();
    }

    public Set<String> ketSet() {
        return shaderParam.keySet();
    }

    public void put(String key, float... data) {
        float[] floats = shaderParam.get(key);
        if (floats == null || floats.length != data.length) {
            floats = new float[data.length];
        }
        System.arraycopy(data, 0, floats, 0, data.length);
        shaderParam.put(key, floats);
    }

    public void put(String key, int... data) {
        float[] floats = shaderParam.get(key);
        if (floats == null || floats.length != data.length) {
            floats = new float[data.length];
        }
        for (int i = 0; i < data.length; i++) {
            floats[i] = data[i];
        }
        shaderParam.put(key, floats);
    }

    public void put(String key, boolean data) {
        put(key, data ? 1 : 0);
    }


    public void put(String key, boolean... data) {
        float[] floats = shaderParam.get(key);
        if (floats == null || floats.length != data.length) {
            floats = new float[data.length];
        }
        for (int i = 0; i < data.length; i++) {
            floats[i] = data[i] ? 1 : 0;
        }
        put(key, floats);
    }

    public float[] get(String key) {
        return shaderParam.get(key);
    }

    public void remove(String key) {
        shaderParam.remove(key);
    }

    public void put(Map<String, float[]> param) {
        if (param != null) {
            shaderParam.putAll(param);
        }
    }

    public void put(GLShaderParam param) {
        if (shaderParam != null) {
            put(param.shaderParam);
        }
    }

    public void clear() {
        shaderParam.clear();
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

}
