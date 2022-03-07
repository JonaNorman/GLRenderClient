package com.jonanorman.android.renderclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class GLEffectGroup extends GLEffect {

    private List<GLEffect> effectList = new ArrayList<>();

    public GLEffectGroup(GLRenderClient client) {
        super(client);
    }


    @Override
    protected void calculateEffect(long parentEffectTimeMs, long parentDurationMs) {
        super.calculateEffect(parentEffectTimeMs, parentDurationMs);
        for (int i = 0; i < getEffectSize(); i++) {
            GLEffect child = getEffect(i);
            child.calculateEffect(getRenderTime(), getRenderDuration());
            if (!isRenderEnable() && child.isRenderEnable()) {
                setRenderEnable(true);
            }
        }
    }

    @Override
    protected GLFrameBuffer renderEffect(GLFrameBuffer input) {
        if (!isRenderEnable()) {
            return input;
        }
        GLFrameBufferCache frameBufferCache = client.getFrameBufferCache();
        GLFrameBuffer out = input;
        for (int i = 0; i < effectList.size(); i++) {
            GLEffect child = effectList.get(i);
            GLFrameBuffer effectBuffer = child.renderEffect(out);
            if (out != effectBuffer && out != input) {
                frameBufferCache.cache(out);
            }
            out = effectBuffer;
        }
        return out;
    }

    public void addEffect(GLEffect effect) {
        if (effect == null) return;
        effectList.add(effect);
    }

    public void addEffect(int index, GLEffect effect) {
        if (effect == null) return;
        effectList.add(index, effect);
    }

    public void setEffect(int index, GLEffect effect) {
        if (effect == null) return;
        effectList.set(index, effect);
    }

    public void removeEffect(GLEffect effect) {
        if (effect == null || !effectList.contains(effect)) return;
        effectList.remove(effect);
    }

    public int indexOf(GLEffect effect) {
        return effectList.indexOf(effect);
    }

    public void addAllEffect(Collection<GLEffect> effects) {
        effectList.addAll(effects);
    }

    public boolean containEffect(GLEffect effect) {
        return effectList.contains(effect);
    }

    public void removeAllEffect(Collection<GLEffect> effects) {
        effectList.removeAll(effects);
    }

    public int getEffectSize() {
        return effectList.size();
    }

    public GLEffect getEffect(int index) {
        return index < 0 || index >= getEffectSize() ? null : effectList.get(index);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GLEffectGroup)) return false;
        if (!super.equals(o)) return false;
        GLEffectGroup that = (GLEffectGroup) o;
        return Objects.equals(effectList, that.effectList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), effectList);
    }
}
