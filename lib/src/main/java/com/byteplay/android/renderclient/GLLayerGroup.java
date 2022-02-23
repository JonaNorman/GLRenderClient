package com.byteplay.android.renderclient;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GLLayerGroup extends GLLayer {

    private final List<GLLayer> layerList = new ArrayList<>();

    private GLScale scale = GLScale.FIT;
    private GLXfermode selfXfermode;

    public GLLayerGroup(GLRenderClient client) {
        super(client, null, null, client.newDrawArray());
        selfXfermode = GLXfermode.SRC_OVER;
    }


    public void add(GLLayer layer) {
        if (layer == null) {
            return;
        }
        layerList.add(layer);
    }

    public void add(int index, GLLayer layer) {
        if (layer == null) {
            return;
        }
        layerList.add(index, layer);
    }

    public void set(int index, GLLayer layer) {
        if (layer == null) return;
        layerList.set(index, layer);
    }

    public void remove(int index) {
        if (index < 0 || index >= layerList.size()) {
            return;
        }
        layerList.remove(index);
    }

    public void remove(GLLayer layer) {
        if (layer == null || !layerList.contains(layer)) {
            return;
        }
        layerList.remove(layer);
    }


    public int indexOf(GLLayer layer) {
        return layerList.indexOf(layer);
    }

    public boolean contains(GLLayer layer) {
        return layerList.contains(layer);
    }

    public void addAll(Collection<GLLayer> layers) {
        layerList.addAll(layers);
    }

    public void removeAll(Collection<GLLayer> layers) {
        layerList.removeAll(layers);
    }


    public int size() {
        return layerList.size();
    }

    public GLLayer get(int index) {
        return index < 0 || index >= layerList.size() ? null : layerList.get(index);
    }

    public void setSelfXfermode(GLXfermode selfXfermode) {
        this.selfXfermode = selfXfermode;
    }

    public GLXfermode getSelfXfermode() {
        return selfXfermode;
    }

    public void clear() {
        layerList.clear();
    }


    public void setScale(GLScale scale) {
        this.scale = scale;
    }

    public GLScale getScale() {
        return scale;
    }
}
