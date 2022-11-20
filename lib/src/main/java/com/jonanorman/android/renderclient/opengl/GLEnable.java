package com.jonanorman.android.renderclient.opengl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class GLEnable extends GLFunction {
    private static final List<Capability> ALL_CAP_LIST = new ArrayList<>();

    static {
        ALL_CAP_LIST.add(Capability.BLEND);
        ALL_CAP_LIST.add(Capability.CULL_FACE);
        ALL_CAP_LIST.add(Capability.DEPTH_TEST);
        ALL_CAP_LIST.add(Capability.DITHER);
        ALL_CAP_LIST.add(Capability.POLYGON_OFFSET_FILL);
        ALL_CAP_LIST.add(Capability.SAMPLE_ALPHA_TO_COVERAGE);
        ALL_CAP_LIST.add(Capability.SAMPLE_COVERAGE);
        ALL_CAP_LIST.add(Capability.SCISSOR_TEST);
        ALL_CAP_LIST.add(Capability.STENCIL_TEST);
    }

    private List<Capability> enableCapList;
    private List<Capability> disableCapList;


    public GLEnable(GLRenderClient renderClient) {
        super(renderClient);
        enableCapList = new ArrayList<>();
        enableCapList.add(Capability.BLEND);
        enableCapList.add(Capability.DITHER);
        disableCapList = new ArrayList<>();
    }


    @Override
    protected void onApply() {
        disableCapList.clear();
        disableCapList.addAll(ALL_CAP_LIST);
        disableCapList.removeAll(enableCapList);
        for (Capability capability : enableCapList) {
            if (capability == Capability.DEPTH_TEST && getClient().getChooseEGLConfig().getDepthSize() <= 0) {
                throw new IllegalStateException("depth size is zero, please choose EGLConfig");
            }
            onEnableCapability(capability);
        }
        for (Capability capability : disableCapList) {
            onDisableCapability(capability);
        }
    }

    public void addEnableCapability(Capability capability) {
        if (capability == null) return;
        if (enableCapList.contains(capability)) {
            return;
        }
        enableCapList.add(capability);

    }


    public void removeEnableCapability(Capability capability) {
        if (capability == null) return;
        if (!enableCapList.contains(capability)) {
            return;
        }
        enableCapList.remove(capability);
    }


    public void addEnableCapability(Collection<Capability> capabilities) {
        for (Capability capability : capabilities) {
            addEnableCapability(capability);
        }
    }

    public void removeEnableCapability(Collection<Capability> capabilities) {
        for (Capability capability : capabilities) {
            removeEnableCapability(capability);
        }
    }


    public boolean contains(Capability capability) {
        if (capability == null) return false;
        return enableCapList.contains(capability);
    }


    protected abstract void onDisableCapability(Capability capability);

    protected abstract void onEnableCapability(Capability capability);

    public enum Capability {
        BLEND,
        CULL_FACE,
        DEPTH_TEST,
        DITHER,
        POLYGON_OFFSET_FILL,
        SAMPLE_ALPHA_TO_COVERAGE,
        SAMPLE_COVERAGE,
        SCISSOR_TEST,
        STENCIL_TEST
    }
}
