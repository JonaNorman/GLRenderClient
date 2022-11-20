package com.jonanorman.android.renderclient.opengl.gl20;

import android.util.ArrayMap;

import com.jonanorman.android.renderclient.opengl.GLEnable;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;

public class GL20Enable extends GLEnable {

    private static final ArrayMap<Capability, Integer> CAP_MAP = new ArrayMap<>();


    static {
        CAP_MAP.put(Capability.BLEND, GL20.GL_BLEND);
        CAP_MAP.put(Capability.CULL_FACE, GL20.GL_CULL_FACE);
        CAP_MAP.put(Capability.DEPTH_TEST, GL20.GL_DEPTH_TEST);
        CAP_MAP.put(Capability.DITHER, GL20.GL_DITHER);
        CAP_MAP.put(Capability.POLYGON_OFFSET_FILL, GL20.GL_POLYGON_OFFSET_FILL);
        CAP_MAP.put(Capability.SAMPLE_ALPHA_TO_COVERAGE, GL20.GL_SAMPLE_ALPHA_TO_COVERAGE);
        CAP_MAP.put(Capability.SAMPLE_COVERAGE, GL20.GL_SAMPLE_COVERAGE);
        CAP_MAP.put(Capability.SCISSOR_TEST, GL20.GL_SCISSOR_TEST);
        CAP_MAP.put(Capability.STENCIL_TEST, GL20.GL_STENCIL_TEST);
    }

    private GL20 gl20;


    public GL20Enable(GLRenderClient client) {
        super(client);
        gl20 = getGL();
    }

    @Override
    protected void onDisableCapability(Capability capability) {
        Integer value = CAP_MAP.get(capability);
        gl20.glDisable(value);
    }

    @Override
    protected void onEnableCapability(Capability capability) {
        Integer value = CAP_MAP.get(capability);
        gl20.glEnable(value);
    }

    @Override
    public String toString() {
        return "GL20Enable@" + hashCode();
    }
}
