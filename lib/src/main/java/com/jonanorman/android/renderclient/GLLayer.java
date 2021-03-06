package com.jonanorman.android.renderclient;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.view.MotionEvent;
import android.view.Surface;

import com.jonanorman.android.renderclient.math.GravityMode;
import com.jonanorman.android.renderclient.math.KeyframeSet;
import com.jonanorman.android.renderclient.math.Matrix4;
import com.jonanorman.android.renderclient.math.ScaleMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public abstract class GLLayer extends GLObject {


    public static final long DURATION_MATCH_PARENT = -1;
    public static final int SIZE_MATCH_PARENT = -1;
    public static final String KEY_FRAMES_KEY_LAYER_X = "layer_x";
    public static final String KEY_FRAMES_KEY_LAYER_Y = "layer_y";
    public static final String KEY_FRAMES_KEY_LAYER_WIDTH = "layer_width";
    public static final String KEY_FRAMES_KEY_LAYER_HEIGHT = "layer_height";
    public static final String KEY_FRAMES_KEY_LAYER_SCALE_X = "layer_scaleX";
    public static final String KEY_FRAMES_KEY_LAYER_SCALE_Y = "layer_scaleY";
    public static final String KEY_FRAMES_KEY_LAYER_ROTATION = "layer_rotation";
    public static final String KEY_FRAMES_KEY_LAYER_TRANSLATE_X = "layer_translateX";
    public static final String KEY_FRAMES_KEY_LAYER_TRANSLATE_Y = "layer_translateY";
    public static final int TOUCH_SLOP = 8;

    private GravityMode gravity = GravityMode.LEFT_TOP;
    private int x;
    private int y;
    private int width = SIZE_MATCH_PARENT;
    private int height = SIZE_MATCH_PARENT;
    private long timeMs;
    private float renderX;
    private float renderY;
    private float renderWidth;
    private float renderHeight;
    private float parentRenderWidth;
    private float parentRenderHeight;
    private float renderScaleX = 1;
    private float renderScaleY = 1;
    private float renderRotation = 0;
    private float renderTranslateX = 0;
    private float renderTranslateY = 0;
    private long renderDuration;
    protected long renderTime;
    private boolean renderEnable = true;
    private float scaleX = 1;
    private float scaleY = 1;
    private float rotation = 0;
    private float translateX = 0;
    private float translateY = 0;
    private GLRenderSurface outEGLSurface;
    private GLFrameBuffer ownFrameBuffer;
    private int backgroundColor;
    private List<LayerTransform> layerTransforms = new ArrayList<>();
    private long startTime;
    private long duration = DURATION_MATCH_PARENT;

    private Map<String, KeyframeSet> keyframesMap = new HashMap<>();
    GLEffectGroup effectGroup;
    final Matrix4 viewPortMatrix = new Matrix4();
    private final Matrix transformMatrix = new Matrix();
    private final Matrix4 transformMatrix4 = new Matrix4();
    private final Matrix transformInvertMatrix = new Matrix();
    private OnTouchListener onTouchListener;
    private OnClickListener onClickListener;
    private boolean downed = false;
    private int touchSlop = TOUCH_SLOP;
    private float[] floatTemp = new float[1];
    protected GLXfermode xfermode = GLXfermode.SRC_OVER;


    private final float[] tempPoint = new float[2];

    private ScaleMode layerScaleMode = ScaleMode.NONE;
    private Queue<MotionEvent> motionEventQueue = new LinkedList<>();


    protected GLLayer(GLRenderClient client) {
        super(client);
        this.effectGroup = client.newEffectSet();
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onDispose() {
        if (ownFrameBuffer != null) {
            ownFrameBuffer.dispose();
            ownFrameBuffer = null;
        }
        for (MotionEvent motionEvent : motionEventQueue) {
            motionEvent.recycle();
        }
        motionEventQueue.clear();
    }

    public void render(GLFrameBuffer frameBuffer) {
        long duration = getDuration() == DURATION_MATCH_PARENT ? Long.MAX_VALUE : getDuration();
        setParentRenderWidth(frameBuffer.getWidth());
        setParentRenderHeight(frameBuffer.getHeight());
        calculateLayer(getTime(), duration);
        MotionEvent motionEvent;
        while ((motionEvent = motionEventQueue.poll()) != null) {
            dispatchTouchEvent(motionEvent);
            motionEvent.recycle();
        }
        motionEventQueue.clear();
        renderLayer(frameBuffer);
    }


    public void render(GLRenderSurface eglSurface, SurfaceReadBitmapCallback callback) {
        if (outEGLSurface != eglSurface) {
            outEGLSurface = eglSurface;
            if (ownFrameBuffer != null) {
                ownFrameBuffer.dispose();
            }
            ownFrameBuffer = client.newFrameBuffer(eglSurface);
        }
        ownFrameBuffer.clearColor(Color.TRANSPARENT);
        ownFrameBuffer.clearDepthBuffer();
        render(ownFrameBuffer);
        if (callback != null) {
            Bitmap bitmap = callback.bitmap;
            if (bitmap != null) {
                ownFrameBuffer.readBitmap(bitmap);
                callback.onBitmapRead(bitmap);
            } else {
                callback.onBitmapRead(ownFrameBuffer.getBitmap());
            }
        }
        ownFrameBuffer.swapBuffers();
    }

    public void render(GLRenderSurface eglSurface) {
        render(eglSurface, null);
    }

    public void render(Surface surface) {
        render(client.obtainWindowSurface(surface));
    }


    public void render(SurfaceTexture surface) {
        render(client.obtainWindowSurface(surface));
    }


    public void calculateLayer(long parentRenderTimeMs, long parentDurationMs) {
        setRenderEnable(false);
        long renderDurationMs = getDuration() == DURATION_MATCH_PARENT ? parentDurationMs : Math.max(getDuration(), 0);
        long startTime = getStartTime();
        long renderTime = parentRenderTimeMs - startTime;
        setRenderDuration(renderDurationMs);
        if (renderTime > getRenderDuration() || renderTime < 0) {
            return;
        }
        float parentRenderWidth = getParentRenderWidth();
        float parentRenderHeight = getParentRenderHeight();
        setRenderTime(renderTime);
        setRenderX(getX());
        setRenderY(getY());
        setRenderWidth(getWidth() == SIZE_MATCH_PARENT ? parentRenderWidth : Math.max(getWidth(), 0));
        setRenderHeight(getHeight() == SIZE_MATCH_PARENT ? parentRenderHeight : Math.max(getHeight(), 0));
        setRenderScaleX(getScaleX());
        setRenderScaleY(getScaleY());
        setRenderRotation(getRotation());
        setRenderTranslateX(getTranslateX());
        setRenderTranslateY(getTranslateY());
        generateLayerKeyFrame();
        onLayerRenderSize(getRenderWidth(), getRenderHeight(), parentRenderWidth, parentRenderHeight);
        onLayerGravity(parentRenderWidth, parentRenderHeight);
        onLayerViewPortMatrix(parentRenderWidth, parentRenderHeight);
        int currentWidth = (int) getRenderWidth();
        int currentHeight = (int) getRenderHeight();
        if (currentWidth <= 0 || currentHeight <= 0) {
            return;
        }
        setRenderEnable(true);
        effectGroup.calculateEffect(renderTime, renderDurationMs);
    }

    public void setLayerScaleMode(ScaleMode layerScaleMode) {
        this.layerScaleMode = layerScaleMode;
    }

    public ScaleMode getLayerScaleMode() {
        return layerScaleMode;
    }

    protected void onLayerRenderSize(float renderWidth, float renderHeight, float parentRenderWidth, float parentRenderHeight) {
        if (layerScaleMode != null) {
            float viewportWidth = layerScaleMode.getWidth(renderWidth, renderHeight, parentRenderWidth, parentRenderHeight);
            float viewportHeight = layerScaleMode.getHeight(renderWidth, renderHeight, parentRenderWidth, parentRenderHeight);
            setRenderWidth(viewportWidth);
            setRenderHeight(viewportHeight);
        }
    }

    protected void onLayerGravity(float parentWidth, float parentHeight) {
        float x = gravity.getX(renderX, renderWidth, parentWidth);
        float y = gravity.getY(renderY, renderHeight, parentHeight);
        setRenderX(x);
        setRenderY(y);
    }

    private void generateLayerKeyFrame() {
        long renderTimeMs = getRenderTime();
        if (loadKeyFrameFloat(GLLayer.KEY_FRAMES_KEY_LAYER_X, renderTimeMs, floatTemp)) {
            setRenderX(floatTemp[0]);
        }
        if (loadKeyFrameFloat(GLLayer.KEY_FRAMES_KEY_LAYER_Y, renderTimeMs, floatTemp)) {
            setRenderY(floatTemp[0]);
        }
        if (loadKeyFrameFloat(GLLayer.KEY_FRAMES_KEY_LAYER_WIDTH, renderTimeMs, floatTemp)) {
            setRenderWidth(floatTemp[0]);
        }
        if (loadKeyFrameFloat(GLLayer.KEY_FRAMES_KEY_LAYER_HEIGHT, renderTimeMs, floatTemp)) {
            setRenderHeight(floatTemp[0]);
        }
        if (loadKeyFrameFloat(GLLayer.KEY_FRAMES_KEY_LAYER_SCALE_X, renderTimeMs, floatTemp)) {
            setRenderScaleX(floatTemp[0]);
        }
        if (loadKeyFrameFloat(GLLayer.KEY_FRAMES_KEY_LAYER_SCALE_Y, renderTimeMs, floatTemp)) {
            setRenderScaleY(floatTemp[0]);
        }
        if (loadKeyFrameFloat(GLLayer.KEY_FRAMES_KEY_LAYER_TRANSLATE_X, renderTimeMs, floatTemp)) {
            setRenderTranslateX(floatTemp[0]);
        }
        if (loadKeyFrameFloat(GLLayer.KEY_FRAMES_KEY_LAYER_TRANSLATE_Y, renderTimeMs, floatTemp)) {
            setRenderTranslateY(floatTemp[0]);
        }
        if (loadKeyFrameFloat(GLLayer.KEY_FRAMES_KEY_LAYER_ROTATION, renderTimeMs, floatTemp)) {
            setRotation(floatTemp[0]);
        }
    }

    private boolean loadKeyFrameFloat(String key, long renderTimeMs, float[] floatTemp) {
        KeyframeSet keyFrames = getKeyFrames(key);
        if (keyFrames == null) {
            return false;
        }
        Class valueType = keyFrames.getValueType();
        Object value = keyFrames.getValueByTime(renderTimeMs, getRenderDuration());
        if (value == null) {
            return false;
        }
        if (valueType == int.class
                || valueType == float.class) {
            floatTemp[0] = (float) value;
        } else if (valueType == int[].class) {
            int[] arr = (int[]) value;
            floatTemp[0] = (float) arr[0];
        } else if (valueType == float[].class) {
            float[] arr = (float[]) value;
            floatTemp[0] = (float) arr[0];
        } else {
            return false;
        }
        return true;
    }

    protected void onLayerViewPortMatrix(float frameWidth, float frameHeight) {
        viewPortMatrix.setIdentity();

        viewPortMatrix.scale(
                renderWidth / 2,
                -renderHeight / 2,
                1);
        viewPortMatrix.translate(
                renderWidth / 2.0f,
                renderHeight / 2.0f,
                0);


        transformMatrix.reset();
        transformMatrix.postTranslate(
                -renderWidth / 2.0f,
                -renderHeight / 2.0f);
        transformMatrix.postScale(
                renderScaleX,
                renderScaleY);
        transformMatrix.postRotate(renderRotation);
        transformMatrix.postTranslate(
                renderTranslateX + renderX,
                renderTranslateY + renderY);
        transformMatrix.postTranslate(
                renderWidth / 2.0f,
                renderHeight / 2.0f);

        transformMatrix.invert(transformInvertMatrix);

        transformMatrix4.set(transformMatrix);
        viewPortMatrix.postMul(transformMatrix4);
        viewPortMatrix.translate(
                -frameWidth / 2.0f,
                -frameHeight / 2.0f,
                0);
        viewPortMatrix.scale(
                2.0f / frameWidth,
                -2.0f / frameHeight,
                1);


    }

    public void queueTouchEvent(MotionEvent motionEvent) {
        motionEventQueue.offer(motionEvent);
    }

    public void clearTouchEventQueue() {
        motionEventQueue.clear();
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        if (!isRenderEnable()) {
            return false;
        }
        if (onTouchListener != null && onTouchListener.onTouch(this, event)) {
            return true;
        }
        if (onTouchEvent(event)) {
            return true;
        }
        return false;
    }

    protected boolean onTouchEvent(MotionEvent ev) {
        final float localX = ev.getX();
        final float localY = ev.getY();
        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downed = true;
                break;
            case MotionEvent.ACTION_UP:
                if (downed && pointInLayer(localX, localY, touchSlop)) {
                    downed = false;
                    if (onClickListener != null) {
                        onClickListener.onClick(this);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!pointInLayer(localX, localY, touchSlop)) {
                    downed = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                downed = false;
                break;
        }
        return true;
    }

    boolean pointInLayer(float localX, float localY) {
        return pointInLayer(localX, localY, 0);
    }


    boolean pointInLayer(float localX, float localY, float slop) {
        return localX >= -slop && localY >= -slop && localX < (renderWidth + slop) &&
                localY < (renderHeight + slop);
    }


    public final void renderLayer(GLFrameBuffer outputBuffer) {
        if (outputBuffer == null) {
            throw new IllegalArgumentException("outputBuffer is null");
        }
        if (!isRenderEnable()) {
            return;
        }
        transformLayer();
        client.drawColor(outputBuffer, viewPortMatrix, backgroundColor);
        int currentWidth = (int) getRenderWidth();
        int currentHeight = (int) getRenderHeight();
        onRenderLayer(currentWidth, currentHeight, outputBuffer);
    }

    protected abstract void onRenderLayer(int currentWidth, int currentHeight, GLFrameBuffer outputBuffer);

    private void transformLayer() {
        if (!isDisposed()) {
            create();
        }
        for (int i = 0; i < layerTransforms.size(); i++) {
            LayerTransform transform = layerTransforms.get(i);
            transform.onLayerTransform(this, renderTime);
        }
    }


    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }


    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }


    public void setTranslateX(float translateX) {
        this.translateX = translateX;
    }

    public void setTranslateY(float translateY) {
        this.translateY = translateY;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }


    public float getTranslateX() {
        return translateX;
    }

    public float getTranslateY() {
        return translateY;
    }


    public int getX() {
        return x;
    }


    public int getY() {
        return y;
    }


    public int getWidth() {
        return width;
    }


    public int getHeight() {
        return height;
    }

    public void addEffect(GLEffect effect) {
        if (effect == null) return;
        effectGroup.addEffect(effect);
    }

    public void removeEffect(GLEffect effect) {
        if (effect == null || !effectGroup.containEffect(effect)) return;
        effectGroup.removeEffect(effect);
    }

    public int getEffectIndex(GLEffect effect) {
        return effectGroup.indexOf(effect);
    }

    public void addEffect(Collection<GLEffect> effects) {
        effectGroup.addAllEffect(effects);
    }

    public void removeEffect(Collection<GLEffect> effects) {
        effectGroup.removeAllEffect(effects);
    }


    public void addTransform(LayerTransform transform) {
        if (transform == null) return;
        layerTransforms.add(transform);
    }

    public void removeTransform(LayerTransform transform) {
        if (transform == null) return;
        layerTransforms.remove(transform);
    }

    public boolean containTransform(LayerTransform transform) {
        return layerTransforms.contains(transform);
    }

    public void addTransform(Collection<LayerTransform> transformCollection) {
        layerTransforms.addAll(transformCollection);
    }

    public void addTransform(int index, LayerTransform transform) {
        if (transform == null) return;
        layerTransforms.add(index, transform);
    }

    public void setKeyframes(String key, KeyframeSet keyframeSet) {
        Class valueType = keyframeSet.getValueType();
        if (valueType != int.class
                && valueType != float.class
                && valueType != float[].class
                && valueType != int[].class) {
            throw new RuntimeException("key frame set not support class " + valueType);
        }
        keyframesMap.put(key, keyframeSet);
    }

    public Set<String> getKeyNames() {
        return keyframesMap.keySet();
    }

    public KeyframeSet getKeyFrames(String key) {
        return keyframesMap.get(key);
    }


    public void clearTransform() {
        layerTransforms.clear();
    }


    public void setTime(long timeMs) {
        this.timeMs = timeMs;
    }

    public long getTime() {
        return timeMs;
    }


    public void setXfermode(GLXfermode xfermode) {
        this.xfermode = xfermode;
    }

    public GLXfermode getXfermode() {
        return xfermode;
    }


    public void setGravity(GravityMode gravity) {
        this.gravity = gravity;
    }

    public GravityMode getGravity() {
        return gravity;
    }


    Matrix4 getViewPortMatrix() {
        return viewPortMatrix;
    }


    void mapPoint(float x, float y, float[] dst) {
        tempPoint[0] = x;
        tempPoint[1] = y;
        transformInvertMatrix.mapPoints(dst, tempPoint);
    }

    Matrix getTransformInvertMatrix() {
        return transformInvertMatrix;
    }


    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }


    protected void setRenderDuration(long renderDuration) {
        this.renderDuration = renderDuration;
    }

    public long getRenderDuration() {
        return renderDuration;
    }


    protected void setRenderEnable(boolean renderEnable) {
        this.renderEnable = renderEnable;
    }

    public boolean isRenderEnable() {
        return renderEnable;
    }

    protected void setRenderTime(long renderTime) {
        this.renderTime = renderTime;
    }

    public long getRenderTime() {
        return renderTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public float getRenderX() {
        return renderX;
    }

    protected void setRenderX(float renderX) {
        this.renderX = renderX;
    }

    public float getRenderY() {
        return renderY;
    }

    protected void setRenderY(float renderY) {
        this.renderY = renderY;
    }

    public float getRenderWidth() {
        return renderWidth;
    }

    protected void setRenderWidth(float renderWidth) {
        this.renderWidth = renderWidth;
    }

    public float getRenderHeight() {
        return renderHeight;
    }

    protected void setRenderHeight(float renderHeight) {
        this.renderHeight = renderHeight;
    }

    public float getRenderScaleX() {
        return renderScaleX;
    }

    protected void setRenderScaleX(float renderScaleX) {
        this.renderScaleX = renderScaleX;
    }

    public float getRenderScaleY() {
        return renderScaleY;
    }

    protected void setRenderScaleY(float renderScaleY) {
        this.renderScaleY = renderScaleY;
    }

    public float getRenderRotation() {
        return renderRotation;
    }

    protected void setRenderRotation(float renderRotation) {
        this.renderRotation = renderRotation;
    }

    public float getRenderTranslateX() {
        return renderTranslateX;
    }

    protected void setRenderTranslateX(float renderTranslateX) {
        this.renderTranslateX = renderTranslateX;
    }

    public float getRenderTranslateY() {
        return renderTranslateY;
    }

    protected void setRenderTranslateY(float renderTranslateY) {
        this.renderTranslateY = renderTranslateY;
    }

    public float getParentRenderWidth() {
        return parentRenderWidth;
    }

    protected void setParentRenderWidth(float parentRenderWidth) {
        this.parentRenderWidth = parentRenderWidth;
    }

    public float getParentRenderHeight() {
        return parentRenderHeight;
    }

    protected void setParentRenderHeight(float parentRenderHeight) {
        this.parentRenderHeight = parentRenderHeight;
    }


    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public static abstract class SurfaceReadBitmapCallback {
        private Bitmap bitmap;

        public SurfaceReadBitmapCallback() {
        }

        public SurfaceReadBitmapCallback(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public abstract void onBitmapRead(Bitmap bitmap);
    }

    public interface LayerTransform<L extends GLLayer> {
        void onLayerTransform(L layer, long renderTimeMs);
    }

    public interface OnTouchListener {
        boolean onTouch(GLLayer layer, MotionEvent event);
    }

    public interface OnClickListener {
        void onClick(GLLayer layer);
    }
}
