package com.byteplay.android.renderclient;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class GL20Enable extends GLEnable {

    private static final ArrayMap<GLCapability, Integer> CAP_MAP = new ArrayMap<>();
    private static final List<GLCapability> ALL_CAP_LIST = new ArrayList<>();

    static {
        CAP_MAP.put(GLCapability.BLEND, GL20.GL_BLEND);
        CAP_MAP.put(GLCapability.CULL_FACE, GL20.GL_CULL_FACE);
        CAP_MAP.put(GLCapability.DEPTH_TEST, GL20.GL_DEPTH_TEST);
        CAP_MAP.put(GLCapability.DITHER, GL20.GL_DITHER);
        CAP_MAP.put(GLCapability.POLYGON_OFFSET_FILL, GL20.GL_POLYGON_OFFSET_FILL);
        CAP_MAP.put(GLCapability.SAMPLE_ALPHA_TO_COVERAGE, GL20.GL_SAMPLE_ALPHA_TO_COVERAGE);
        CAP_MAP.put(GLCapability.SAMPLE_COVERAGE, GL20.GL_SAMPLE_COVERAGE);
        CAP_MAP.put(GLCapability.SCISSOR_TEST, GL20.GL_SCISSOR_TEST);
        CAP_MAP.put(GLCapability.STENCIL_TEST, GL20.GL_STENCIL_TEST);

        ALL_CAP_LIST.add(GLCapability.BLEND);
        ALL_CAP_LIST.add(GLCapability.CULL_FACE);
        ALL_CAP_LIST.add(GLCapability.DEPTH_TEST);
        ALL_CAP_LIST.add(GLCapability.DITHER);
        ALL_CAP_LIST.add(GLCapability.POLYGON_OFFSET_FILL);
        ALL_CAP_LIST.add(GLCapability.SAMPLE_ALPHA_TO_COVERAGE);
        ALL_CAP_LIST.add(GLCapability.SAMPLE_COVERAGE);
        ALL_CAP_LIST.add(GLCapability.SCISSOR_TEST);
        ALL_CAP_LIST.add(GLCapability.STENCIL_TEST);
    }

    private List<GLCapability> disableCapList = new ArrayList<>();

    protected GL20 gl;

    protected GL20Enable(GLRenderClient client) {
        super(client);
        gl = client.getGL20();
    }


    @Override
    protected void onCall() {
        disableCapList.clear();
        disableCapList.addAll(ALL_CAP_LIST);
        int size = getCapabilitySize();
        for (int i = 0; i < size; i++) {
            GLCapability capability = getCapability(i);
            Integer value = CAP_MAP.get(capability);
            gl.glEnable(value);
            disableCapList.remove(capability);
        }
        for (GLCapability capability : disableCapList) {
            Integer value = CAP_MAP.get(capability);
            gl.glDisable(value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GL20Enable)) return false;
        if (!super.equals(o)) return false;
        GL20Enable that = (GL20Enable) o;
        return Objects.equals(gl, that.gl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gl);
    }
}
