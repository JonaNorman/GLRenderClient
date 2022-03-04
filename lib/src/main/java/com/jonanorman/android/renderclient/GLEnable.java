package com.jonanorman.android.renderclient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class GLEnable extends GLFunction {
    private List<GLCapability> capabilityList = new ArrayList<>();

    public GLEnable(GLRenderClient client) {
        super(client);
        add(GLCapability.DITHER);
        add(GLCapability.BLEND);
    }

    public void add(GLCapability capability) {
        if (capability == null) return;
        if (capabilityList.contains(capability)) {
            return;
        }
        capabilityList.add(capability);
    }

    public void remove(GLCapability capability) {
        if (capability == null) return;
        if (!capabilityList.contains(capability)) {
            return;
        }
        capabilityList.remove(capability);
    }

    public void clear() {
        capabilityList.clear();
    }

    public boolean contains(GLCapability capability) {
        return capability == null ? false : capabilityList.contains(capability);
    }

    public int getCapabilitySize() {
        return capabilityList.size();
    }

    public GLCapability getCapability(int index) {
        return index < 0 || index >= capabilityList.size() ? null: capabilityList.get(index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GLEnable)) return false;
        GLEnable glEnable = (GLEnable) o;
        return Objects.equals(capabilityList, glEnable.capabilityList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(capabilityList);
    }
}
