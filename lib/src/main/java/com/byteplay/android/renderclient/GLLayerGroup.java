package com.byteplay.android.renderclient;


import android.view.MotionEvent;

import com.byteplay.android.renderclient.math.ScaleMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GLLayerGroup extends GLLayer {

    private final List<GLLayer> layerList = new ArrayList<>();

    private ScaleMode scale = ScaleMode.FIT;
    private GLXfermode selfXfermode;
    private GLLayer mFirstTouchLayer;
    private final float[] tempPoint = new float[4];

    public GLLayerGroup(GLRenderClient client) {
        super(client, null, null);
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


    public void setScale(ScaleMode scale) {
        this.scale = scale;
    }

    public ScaleMode getScale() {
        return scale;
    }

    @Override
    protected void computeLayer(long parentRenderTimeMs, long parentDurationMs) {
        super.computeLayer(parentRenderTimeMs, parentDurationMs);
        for (int i = 0; i < getLayerSize(); i++) {
            GLLayer child = getLayer(i);
            child.setParentRenderWidth(getRenderWidth());
            child.setParentRenderHeight(getRenderHeight());
            child.computeLayer(getRenderTime(), getRenderDuration());
        }
    }

    protected boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    protected boolean dispatchTouchEvent(MotionEvent ev) {
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
                    if (pointInView(x, y, child)) {
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
        } else {
            final MotionEvent transformedEvent = MotionEvent.obtain(ev);
            int actionIndex = ev.getActionIndex();
            final float x = ev.getX(actionIndex);
            final float y = ev.getY(actionIndex);
            final float[] point = tempPoint;
            point[0] = x;
            point[1] = y;
            point[2] = 0;
            point[3] = 1;
            point[0] = point[0] / getRenderWidth() * 2.0f - 1.0f;
            point[1] = point[1] / getRenderHeight() * 2.0f - 1.0f;
            child.getViewPortInvertMatrix().mapPoints(point);
            point[0] = (point[0] + 1.0f) / 2.0f * child.getRenderWidth();
            point[1] = (point[1] + 1.0f) / 2.0f * child.getRenderHeight();


            transformedEvent.setLocation(point[0], point[1]);
            handled = child.dispatchTouchEvent(transformedEvent);
            transformedEvent.recycle();
        }
        ev.setAction(oldAction);
        return handled;
    }

    protected boolean pointInView(float x, float y, GLLayer child) {
        final float[] point = tempPoint;
        point[0] = x;
        point[1] = y;
        point[2] = 0;
        point[3] = 1;
        point[0] = point[0] / getRenderWidth() * 2.0f - 1.0f;
        point[1] = point[1] / getRenderHeight() * 2.0f - 1.0f;
        child.getViewPortInvertMatrix().mapPoints(point);
        point[0] = (point[0] + 1.0f) / 2.0f * child.getRenderWidth();
        point[1] = (point[1] + 1.0f) / 2.0f * child.getRenderHeight();
        final boolean isInView = child.pointInView(point[0], point[1]);
        return isInView;
    }

}
