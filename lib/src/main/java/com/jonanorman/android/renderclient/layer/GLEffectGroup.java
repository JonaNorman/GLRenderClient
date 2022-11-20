package com.jonanorman.android.renderclient.layer;

import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLFrameBufferCache;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.gl20.GL20FrameBufferCache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GLEffectGroup extends GLEffect {

    private List<GLEffect> effectList = new ArrayList<>();

    public GLEffectGroup() {
        super();
    }

    @Override
    protected void onRenderInit(GLRenderClient client) {

    }

    @Override
    public void clean(boolean safe) {
        super.clean(safe);
        for (GLEffect effect : effectList) {
            effect.clean(safe);
        }
    }

    @Override
    protected void onRenderClean(GLRenderClient client) {

    }

    @Override
    protected GLFrameBuffer onRenderEffect(GLRenderClient client, GLFrameBuffer inputBuffer) {
        if (effectList.size() == 0) {
            return inputBuffer;
        }
        GLFrameBufferCache frameBufferCache = GL20FrameBufferCache.getCache(client);
        GLFrameBuffer outputBuffer = inputBuffer;
        for (int i = 0; i < effectList.size(); i++) {
            GLEffect child = effectList.get(i);
            GLFrameBuffer effectBuffer = child.render(outputBuffer, getRenderTime(), getRenderDuration());
            if (outputBuffer != effectBuffer && outputBuffer != inputBuffer) {
                frameBufferCache.cache(outputBuffer);
            }
            outputBuffer = effectBuffer;
        }
        return outputBuffer;
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

    public boolean contain(GLEffect effect) {
        return effectList.contains(effect);
    }

    public void remove(Collection<GLEffect> effects) {
        effectList.removeAll(effects);
    }

    public int getSize() {
        return effectList.size();
    }

    public GLEffect get(int index) {
        return index < 0 || index >= getSize() ? null : effectList.get(index);
    }
}
