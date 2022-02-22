package com.byteplay.android.renderclient;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class GLShaderParam extends GLObject {

    private final Map<String, float[]> shaderParam = new HashMap<>();

    protected GLShaderParam(GLRenderClient client) {
        super(client);
    }

    public Set<String> ketSet() {
        return shaderParam.keySet();
    }

    public void put(String key, float... data) {
        shaderParam.put(key, data);
    }

    public void put(String key, boolean data) {
        put(key, data ? 1 : 0);
    }


    public Map<String, float[]> get() {
        return shaderParam;
    }


    public float[] get(String key) {
        return shaderParam.get(key);
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
        return Objects.hash(super.hashCode(), shaderParam);
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onDispose() {

    }
}
