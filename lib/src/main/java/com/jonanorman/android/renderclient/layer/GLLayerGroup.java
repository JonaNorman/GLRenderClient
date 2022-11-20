package com.jonanorman.android.renderclient.layer;


import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GLLayerGroup extends GLLayer {

    private final List<GLLayer> layerList = new ArrayList<>();

    private GLLayer mFirstTouchLayer;
    private final float[] touchRangeTempPoint;


    public GLLayerGroup() {
        super();
        touchRangeTempPoint = new float[2];
    }

    @Override
    protected void onRenderInit(GLRenderClient client) {

    }

    @Override
    protected void onRenderClean(GLRenderClient client) {

    }


    public void add(@NonNull GLLayer layer) {
        layerList.add(layer);
    }

    public void add(int index, @NonNull GLLayer layer) {
        layerList.add(index, layer);
    }

    public void set(int index, @NonNull GLLayer layer) {
        layerList.set(index, layer);
    }

    public void remove(int index) {
        if (index < 0 || index >= layerList.size()) {
            return;
        }
        layerList.remove(index);
    }

    public void remove(@NonNull GLLayer layer) {
        if (!layerList.contains(layer)) {
            return;
        }
        layerList.remove(layer);
    }


    public int indexOf(GLLayer layer) {
        return layerList.indexOf(layer);
    }

    public boolean contain(GLLayer layer) {
        return layerList.contains(layer);
    }

    public void addAll(Collection<GLLayer> layers) {
        layerList.addAll(layers);
    }

    public void removeAll(Collection<GLLayer> layers) {
        layerList.removeAll(layers);
    }


    public int getSize() {
        return layerList.size();
    }

    public GLLayer get(int index) {
        return index < 0 || index >= layerList.size() ? null : layerList.get(index);
    }


    public void clear() {
        layerList.clear();
    }


    protected boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    protected boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev == null) {
            return false;
        }
        if (!isEnable()) {
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
                int size = getSize();
                for (int i = size - 1; i >= 0; i--) {
                    GLLayer child = get(i);
                    int actionIndex = ev.getActionIndex();
                    final float x = ev.getX(actionIndex);
                    final float y = ev.getY(actionIndex);
                    if (isTouchRange(x, y, child)) {
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
        } else if (child.isEnable()) {
            final MotionEvent transformedEvent = MotionEvent.obtain(ev);
            int actionIndex = ev.getActionIndex();
            final float x = ev.getX(actionIndex);
            final float y = ev.getY(actionIndex);
            final float[] point = touchRangeTempPoint;
            child.mapPoint(x, y, touchRangeTempPoint);
            transformedEvent.setLocation(point[0], point[1]);
            handled = child.dispatchTouchEvent(transformedEvent);
            transformedEvent.recycle();
        } else {
            handled = false;
        }
        ev.setAction(oldAction);
        return handled;
    }

    boolean isTouchRange(float x, float y, GLLayer child) {
        if (!child.isEnable()) {
            return false;
        }
        final float[] point = touchRangeTempPoint;
        child.mapPoint(x, y, touchRangeTempPoint);
        return child.inTouchRange(point[0], point[1]);
    }


    @Override
    protected boolean onRenderLayer(GLRenderClient client, GLFrameBuffer inputBuffer) {
        if (layerList.isEmpty()) {
            return false;
        }
        boolean success = false;
        for (int i = 0; i < layerList.size(); i++) {
            GLLayer child = layerList.get(i);
            success |= child.render(inputBuffer, getRenderTime(), getRenderDuration());
        }
        return success;
    }
}
