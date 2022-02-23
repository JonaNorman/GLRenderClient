package com.byteplay.android.renderclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class GLEffectSet extends GLEffect {

    private List<GLEffect> effectList = new ArrayList<>();

    public GLEffectSet(GLRenderClient client) {
        super(client);
    }


    @Override
    protected GLFrameBuffer actualApplyEffect(GLEffect effect, GLFrameBuffer input, long timeMs) {
        return client.applyEffect((GLEffectSet) effect, input, timeMs);
    }


    public void add(GLEffect effect) {
        if (effect == null) return;
        effectList.add(effect);
    }

    public void add(int index, GLEffect effect) {
        if (effect == null) return;
        effectList.add(index, effect);
    }

    public void set(int index, GLEffect effect) {
        if (effect == null) return;
        effectList.set(index, effect);
    }

    public void remove(GLEffect effect) {
        if (effect == null || !effectList.contains(effect)) return;
        effectList.remove(effect);
    }

    public int indexOf(GLEffect effect) {
        return effectList.indexOf(effect);
    }

    public void addAll(Collection<GLEffect> effects) {
        effectList.addAll(effects);
    }

    public boolean contains(GLEffect effect) {
        return effectList.contains(effect);
    }

    public void removeAll(Collection<GLEffect> effects) {
        effectList.removeAll(effects);
    }

    public int size() {
        return effectList.size();
    }

    public GLEffect get(int index) {
        return index < 0 || index >= size() ? null : effectList.get(index);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GLEffectSet)) return false;
        if (!super.equals(o)) return false;
        GLEffectSet that = (GLEffectSet) o;
        return Objects.equals(effectList, that.effectList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), effectList);
    }
}
