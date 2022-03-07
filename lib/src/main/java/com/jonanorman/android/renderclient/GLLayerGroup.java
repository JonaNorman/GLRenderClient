package com.jonanorman.android.renderclient;


import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GLLayerGroup extends GLLayer {

    private final List<GLLayer> layerList = new ArrayList<>();

    private GLXfermode selfXfermode = GLXfermode.SRC_OVER;
    private GLLayer mFirstTouchLayer;
    private final float[] tempPoint = new float[2];


    public GLLayerGroup(GLRenderClient client) {
        super(client, null, null);
    }


    public void addLayer(GLLayer layer) {
        if (layer == null) {
            return;
        }
        layerList.add(layer);
    }

    public void addLayer(int index, GLLayer layer) {
        if (layer == null) {
            return;
        }
        layerList.add(index, layer);
    }

    public void setLayer(int index, GLLayer layer) {
        if (layer == null) return;
        layerList.set(index, layer);
    }

    public void removeLayer(int index) {
        if (index < 0 || index >= layerList.size()) {
            return;
        }
        layerList.remove(index);
    }

    public void removeLayer(GLLayer layer) {
        if (layer == null || !layerList.contains(layer)) {
            return;
        }
        layerList.remove(layer);
    }


    public int indexOf(GLLayer layer) {
        return layerList.indexOf(layer);
    }

    public boolean containLayer(GLLayer layer) {
        return layerList.contains(layer);
    }

    public void addAllLayer(Collection<GLLayer> layers) {
        layerList.addAll(layers);
    }

    public void removeAllLayer(Collection<GLLayer> layers) {
        layerList.removeAll(layers);
    }


    public int getLayerSize() {
        return layerList.size();
    }

    public GLLayer getLayer(int index) {
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


    @Override
    protected void calculateLayer(long parentRenderTimeMs, long parentDurationMs) {
        super.calculateLayer(parentRenderTimeMs, parentDurationMs);
        for (int i = 0; i < getLayerSize(); i++) {
            GLLayer child = getLayer(i);
            child.setParentRenderWidth(getRenderWidth());
            child.setParentRenderHeight(getRenderHeight());
            child.calculateLayer(getRenderTime(), getRenderDuration());
        }
    }

    protected boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev == null) {
            return false;
        }
        if (!isRenderEnable()) {
            return false;
        }
        boolean intercepted;
        boolean handled = false;
        int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            mFirstTouchLayer = null;
        }
        if (action == MotionEvent.ACTION_DOWN || mFirstTouchLayer != null) {
            intercepted = onInterceptTouchEvent(ev);
        } else {
            intercepted = true;
        }
        boolean canceled = action == MotionEvent.ACTION_CANCEL;
        boolean alreadyDispatchedToNewTouchTarget = false;
        if (!intercepted) {
            if (action == MotionEvent.ACTION_DOWN) {
                int size = getLayerSize();
                for (int i = size - 1; i >= 0; i--) {
                    GLLayer child = getLayer(i);
                    int actionIndex = ev.getActionIndex();
                    final float x = ev.getX(actionIndex);
                    final float y = ev.getY(actionIndex);
                    if (pointInLayer(x, y, child)) {
                        if (dispatchTransformedTouchEvent(ev, false, child)) {
                            mFirstTouchLayer = child;
                            alreadyDispatchedToNewTouchTarget = true;
                        }
                        break;
                    }
                }
            }
        }
        if (mFirstTouchLayer == null) {
            handled = dispatchTransformedTouchEvent(ev, canceled, null);
        } else {
            if (alreadyDispatchedToNewTouchTarget) {
                handled = true;
            } else {
                boolean cancelChild = canceled || intercepted;
                if (dispatchTransformedTouchEvent(ev, cancelChild, mFirstTouchLayer)) {
                    handled = true;
                }
                if (cancelChild) {
                    mFirstTouchLayer = null;
                }
            }
        }
        if (canceled || action == MotionEvent.ACTION_UP) {
            mFirstTouchLayer = null;
        }
        return handled;
    }


    private boolean dispatchTransformedTouchEvent(MotionEvent ev, boolean cancel, GLLayer child) {
        if (cancel) {
            ev.setAction(MotionEvent.ACTION_CANCEL);
        }
        int oldAction = ev.getAction();
        boolean handled;
        if (child == null) {
            handled = super.dispatchTouchEvent(ev);
        } else if (child.isRenderEnable()) {
            final MotionEvent transformedEvent = MotionEvent.obtain(ev);
            int actionIndex = ev.getActionIndex();
            final float x = ev.getX(actionIndex);
            final float y = ev.getY(actionIndex);
            final float[] point = tempPoint;
            child.mapPoint(x, y, tempPoint);
            transformedEvent.setLocation(point[0], point[1]);
            handled = child.dispatchTouchEvent(transformedEvent);
            transformedEvent.recycle();
        } else {
            handled = false;
        }
        ev.setAction(oldAction);
        return handled;
    }

    protected boolean pointInLayer(float x, float y, GLLayer child) {
        if (!child.isRenderEnable()) {
            return false;
        }
        final float[] point = tempPoint;
        child.mapPoint(x, y, tempPoint);
        final boolean isInView = child.pointInLayer(point[0], point[1]);
        return isInView;
    }


    @Override
    protected void onRenderLayer(int currentWidth, int currentHeight, GLFrameBuffer outputBuffer) {
        GLFrameBufferCache frameBufferCache = client.getFrameBufferCache();
        GLFrameBuffer groupFrameBuffer = frameBufferCache.obtain(currentWidth, currentHeight);
        if (vertexShaderCode != null &&
                fragmentShaderCode != null) {
            drawLayer(groupFrameBuffer, DEFAULT_MATRIX, selfXfermode, renderTime);
        }
        for (int i = 0; i < layerList.size(); i++) {
            GLLayer child = layerList.get(i);
            child.renderLayer(groupFrameBuffer);
        }
        GLFrameBuffer effectBuffer = effectGroup.renderEffect(groupFrameBuffer);
        if (groupFrameBuffer != effectBuffer) {
            frameBufferCache.cache(groupFrameBuffer);
        }
        GLTexture effectTexture = effectBuffer.getColorTexture();
        client.drawTexture(effectTexture, xfermode, viewPortMatrix, outputBuffer);
        frameBufferCache.cache(effectBuffer);
    }


}
