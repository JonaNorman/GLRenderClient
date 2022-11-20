package com.jonanorman.android.renderclient.layer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import com.jonanorman.android.renderclient.math.Dimension;
import com.jonanorman.android.renderclient.math.GravityMode;
import com.jonanorman.android.renderclient.math.Keyframe;
import com.jonanorman.android.renderclient.math.KeyframeSet;
import com.jonanorman.android.renderclient.math.Matrix4;
import com.jonanorman.android.renderclient.math.ScaleMode;
import com.jonanorman.android.renderclient.math.TimeStamp;
import com.jonanorman.android.renderclient.opengl.EGLSurface;
import com.jonanorman.android.renderclient.opengl.EGLWindowSurface;
import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLFrameBufferCache;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLXfermode;
import com.jonanorman.android.renderclient.opengl.gl20.GL20FrameBufferCache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class GLLayer {


    public static final String KEY_FRAMES_X = "layerX";
    public static final String KEY_FRAMES_Y = "layerY";
    public static final String KEY_FRAMES_WIDTH = "layerWidth";
    public static final String KEY_FRAMES_HEIGHT = "layerHeight";
    public static final String KEY_FRAMES_SCALE_X = "layerScaleX";
    public static final String KEY_FRAMES_SCALE_Y = "layerScaleY";
    public static final String KEY_FRAMES_ROTATION = "layerRotation";
    public static final String KEY_FRAMES_TRANSLATE_X = "layerTranslateX";
    public static final String KEY_FRAMES_TRANSLATE_Y = "layerTranslateY";
    public static final int TOUCH_SLOP = 8;


    private final Queue<MotionEvent> motionEventQueue;
    private final GLEffectGroup effectGroup;
    private final GLEffectGroup afterEffectGroup;
    private final List<OnTransformListener> transformListeners;
    private final Map<String, KeyframeSet> keyframesMap;
    private final Matrix transformMatrix;
    private final Matrix transformInvertMatrix;
    private final Matrix4 viewPortMatrix;
    private final float[] transformTempPoint;
    private final float[] keyframeTempValue;


    private boolean enable;
    private float scaleX;
    private float scaleY;
    private float rotation;
    private int backgroundColor;

    private float x;
    private float y;
    private float translateX;
    private float translateY;
    private Dimension width;
    private Dimension widthTemp;
    private Dimension height;
    private Dimension heightTemp;
    private TimeStamp startTime;
    private TimeStamp duration;
    private boolean downed;
    private int touchSlop;
    private GravityMode gravity;
    private GLXfermode xfermode;
    private ScaleMode scaleMode;
    private OnTouchListener onTouchListener;
    private OnClickListener onClickListener;


    private TimeStamp renderTime;
    private TimeStamp renderDuration;
    private float renderScaleX;
    private float renderScaleY;
    private float renderRotation;
    private float renderY;
    private float renderX;
    private float renderTranslateX;
    private float renderTranslateY;
    private float renderWidth;
    private float renderHeight;
    private float parentRenderWidth;
    private float parentRenderHeight;
    private final Keyframe resultKeyFrame;
    private GLRenderClient lastRenderClient;

    private Queue<Runnable> postQueue;
    private List<OnRenderListener> renderListenerList = new CopyOnWriteArrayList<>();


    public GLLayer() {
        super();
        this.transformTempPoint = new float[2];
        this.keyframeTempValue = new float[1];
        this.motionEventQueue = new LinkedList<>();
        this.effectGroup = new GLEffectGroup();
        this.afterEffectGroup = new GLEffectGroup();
        this.transformListeners = new ArrayList<>();
        this.transformMatrix = new Matrix();
        this.transformInvertMatrix = new Matrix();
        this.viewPortMatrix = new Matrix4();
        this.keyframesMap = new HashMap<>();
        this.scaleX = 1;
        this.scaleY = 1;
        this.enable = true;
        this.width = Dimension.MATCH_PARENT_VALUE;
        this.height = Dimension.MATCH_PARENT_VALUE;
        this.startTime = TimeStamp.MIN_VALUE;
        this.duration = TimeStamp.MATCH_PARENT_VALUE;
        this.touchSlop = TOUCH_SLOP;
        this.gravity = GravityMode.LEFT_TOP;
        this.xfermode = GLXfermode.SRC_OVER;
        this.scaleMode = ScaleMode.NONE;
        this.renderTime = TimeStamp.ofNanos(0);
        this.renderDuration = TimeStamp.ofNanos(0);
        this.resultKeyFrame = Keyframe.ofValue(0, null, null);
        this.postQueue = new LinkedList<>();
        this.widthTemp = new Dimension();
        this.heightTemp = new Dimension();
    }


    public final boolean render(GLFrameBuffer outBuffer, @NonNull TimeStamp currentTime) {
        return render(outBuffer, currentTime, TimeStamp.MATCH_PARENT_VALUE);
    }

    public final boolean render(GLFrameBuffer outBuffer, @NonNull TimeStamp currentTime, OnReadBitmapListener onReadBitmapListener) {
        boolean success = render(outBuffer, currentTime, TimeStamp.MATCH_PARENT_VALUE);
        if (onReadBitmapListener != null) {
            onReadBitmapListener.onReadBitmap(this, outBuffer.getBitmap());
        }
        return success;
    }

    final boolean render(GLFrameBuffer outBuffer, @NonNull TimeStamp currentTime, @NonNull TimeStamp parentDuration) {
        boolean success = false;
        try {
            for (OnRenderListener onRenderListener : renderListenerList) {
                onRenderListener.onRenderStart(this);
            }
            if (!enable) {
                success = false;
                return false;
            }
            parentRenderWidth = outBuffer.getWidth();
            parentRenderHeight = outBuffer.getHeight();
            loadTime(currentTime, parentDuration);
            if (renderTime.toNanos() > renderDuration.toNanos() || renderTime.toNanos() < 0) {
                success = false;
                return false;
            }
            for (int i = 0; i < transformListeners.size(); i++) {
                OnTransformListener transform = transformListeners.get(i);
                transform.onTransform(this, renderTime);
            }
            loadRenderValue();
            loadKeyFrame();
            loadScaleMode();
            loadGravityMode();
            loadViewPortMatrix();
            int frameWidth = Math.round(renderWidth);
            int frameHeight = Math.round(renderHeight);
            if (frameWidth <= 0 || frameHeight <= 0) {
                success = false;
                return false;
            }
            init();
            while (!postQueue.isEmpty()) {
                Runnable runnable = postQueue.poll();
                runnable.run();
            }
            MotionEvent motionEvent;
            while ((motionEvent = motionEventQueue.poll()) != null) {
                dispatchTouchEvent(motionEvent);
                motionEvent.recycle();
            }
            GLFrameBufferCache frameBufferCache = GL20FrameBufferCache.getCache(lastRenderClient);
            GLFrameBuffer layerFrameBuffer = frameBufferCache.obtain(frameWidth, frameHeight);
            success = onRenderLayer(lastRenderClient,
                    layerFrameBuffer
            );
            if (!success){
                frameBufferCache.cache(layerFrameBuffer);
                outBuffer.drawColor(viewPortMatrix, backgroundColor);
                return false;
            }
            GLFrameBuffer effectBuffer = loadEffect(layerFrameBuffer);
            if (layerFrameBuffer != effectBuffer) frameBufferCache.cache(layerFrameBuffer);
            loadAfterEffect(outBuffer, effectBuffer);
            EGLSurface surface = outBuffer.getSurface();
            if (surface instanceof EGLWindowSurface) {
                EGLWindowSurface windowSurface = (EGLWindowSurface) surface;
                windowSurface.setPresentationTime(currentTime);
            }
            return true;
        } finally {
            for (OnRenderListener onRenderListener : renderListenerList) {
                onRenderListener.onRenderEnd(this, success);
            }
        }
    }


    public boolean render(Surface surface, @NonNull TimeStamp currentTime) {
        return render(surface, currentTime, null);
    }

    public boolean render(Surface surface, @NonNull TimeStamp currentTime, OnReadBitmapListener onReadBitmapListener) {
        init();
        EGLWindowSurface windowSurface = getClient().obtainWindowSurface(surface);
        boolean success = render(windowSurface.getDefaultFrameBuffer(), currentTime);
        if (onReadBitmapListener != null) {
            onReadBitmapListener.onReadBitmap(this, windowSurface.getDefaultFrameBuffer().getBitmap());
        }
        if (!success) {
            return false;
        }
        return windowSurface.swapBuffers();
    }

    public boolean render(SurfaceTexture surface, @NonNull TimeStamp currentTime) {
        return render(surface, currentTime, null);
    }

    public boolean render(SurfaceTexture surface, @NonNull TimeStamp currentTime, OnReadBitmapListener onReadBitmapListener) {
        init();
        EGLWindowSurface windowSurface = getClient().obtainWindowSurface(surface);
        GLFrameBuffer frameBuffer = windowSurface.getDefaultFrameBuffer();
        frameBuffer.clearColor(Color.TRANSPARENT);
        frameBuffer.clearDepthBuffer();
        boolean success = render(frameBuffer, currentTime);
        if (onReadBitmapListener != null) {
            onReadBitmapListener.onReadBitmap(this, frameBuffer.getBitmap());
        }
        if (!success) {
            return false;
        }
        return windowSurface.swapBuffers();
    }

    public boolean render(SurfaceHolder surface, @NonNull TimeStamp currentTime, OnReadBitmapListener onReadBitmapListener) {
        init();
        EGLWindowSurface windowSurface = getClient().obtainWindowSurface(surface);
        GLFrameBuffer frameBuffer = windowSurface.getDefaultFrameBuffer();
        frameBuffer.clearColor(Color.TRANSPARENT);
        frameBuffer.clearDepthBuffer();
        boolean success = render(windowSurface.getDefaultFrameBuffer(), currentTime);
        if (onReadBitmapListener != null) {
            onReadBitmapListener.onReadBitmap(this, windowSurface.getDefaultFrameBuffer().getBitmap());
        }
        if (!success) {
            return false;
        }
        return windowSurface.swapBuffers();
    }


    public boolean render(SurfaceHolder surface, @NonNull TimeStamp currentTime) {
        return render(surface, currentTime, null);
    }


    public void clean() {
        clean(false);
    }

    public void clean(boolean safe) {
        if (lastRenderClient == null) {
            return;
        }
        GLRenderClient currentClient = GLRenderClient.getCurrentClient();
        if (lastRenderClient != currentClient) {
            throw new IllegalStateException(this + " clean must in " + lastRenderClient.getAttachThread());
        }
        if (safe) {
            while (!postQueue.isEmpty()) {
                Runnable runnable = postQueue.poll();
                runnable.run();
            }
        } else {
            postQueue.clear();
        }
        cleanTouchEvent();
        onRenderClean(lastRenderClient);
        lastRenderClient = null;
    }


    public void init() {
        GLRenderClient currentClient = GLRenderClient.getCurrentClient();
        if (currentClient == null) {
            throw new IllegalStateException(this + " must render in attached thread");
        }
        if (lastRenderClient != currentClient) {
            if (lastRenderClient != null) {
                postQueue.clear();
            }
            lastRenderClient = currentClient;
            onRenderInit(currentClient);
        }
    }

    public void checkInit() {
        if (lastRenderClient == null) {
            RuntimeException runtimeException = new RuntimeException("please call after init");
            StackTraceElement[] cloneStackTraceElement = runtimeException.getStackTrace();
            StackTraceElement[] stackTraceElement = new StackTraceElement[cloneStackTraceElement.length - 1];
            System.arraycopy(cloneStackTraceElement, 1, stackTraceElement, 0, stackTraceElement.length);
            runtimeException.setStackTrace(stackTraceElement);
            throw runtimeException;
        }
    }

    public GLRenderClient getClient() {
        return lastRenderClient;
    }

    public void postRender(Runnable runnable) {
        postQueue.offer(runnable);
    }


    protected abstract void onRenderInit(GLRenderClient client);

    protected abstract void onRenderClean(GLRenderClient client);


    private GLFrameBuffer loadEffect(GLFrameBuffer layerFrameBuffer) {
        return effectGroup.render(layerFrameBuffer, renderTime, renderDuration);
    }

    private void loadAfterEffect(GLFrameBuffer outBuffer, GLFrameBuffer inputBuffer) {
        GLFrameBufferCache frameBufferCache = GL20FrameBufferCache.getCache(lastRenderClient);
        if (isAfterEffectDisable()) {
            outBuffer.drawColor(viewPortMatrix, backgroundColor);
            outBuffer.drawTexture(viewPortMatrix, inputBuffer.getAttachColorTexture());
            frameBufferCache.cache(inputBuffer);
            return;
        }
        GLFrameBuffer copyBuffer = frameBufferCache.obtain(outBuffer.getWidth(), outBuffer.getHeight());
        outBuffer.copyToTexture(copyBuffer.getAttachColorTexture());
        copyBuffer.drawColor(viewPortMatrix, backgroundColor);
        copyBuffer.drawTexture(viewPortMatrix, inputBuffer.getAttachColorTexture());
        frameBufferCache.cache(inputBuffer);
        GLFrameBuffer effectBuffer = afterEffectGroup.render(copyBuffer, renderTime, renderDuration);
        if (copyBuffer != effectBuffer) frameBufferCache.cache(copyBuffer);
        outBuffer.drawTexture(GLXfermode.DST, effectBuffer.getAttachColorTexture());
    }

    private boolean isAfterEffectDisable() {
        return !afterEffectGroup.isEnable() || afterEffectGroup.getSize() <= 0;
    }

    private void loadRenderValue() {
        renderScaleX = scaleX;
        renderScaleY = scaleY;
        renderX = x;
        renderY = y;
        renderTranslateX = translateX;
        renderTranslateY = translateY;
        if (width != Dimension.MATCH_PARENT_VALUE) {
            renderWidth = width.getLength();
        } else {
            renderWidth = parentRenderWidth;
        }
        if (height != Dimension.MATCH_PARENT_VALUE) {
            renderHeight = height.getLength();
        } else {
            renderHeight = parentRenderHeight;
        }
    }


    private void loadTime(@NonNull TimeStamp currentTime, @NonNull TimeStamp parentDuration) {
        long renderTimeNs = currentTime.toNanos() - startTime.toNanos();
        long renderDurationNs;
        if (duration != TimeStamp.MATCH_PARENT_VALUE && parentDuration != TimeStamp.MATCH_PARENT_VALUE) {
            renderDurationNs = Math.min(duration.toNanos(), parentDuration.toNanos() - startTime.toNanos());
        } else if (duration != TimeStamp.MATCH_PARENT_VALUE && parentDuration == TimeStamp.MATCH_PARENT_VALUE) {
            renderDurationNs = duration.toNanos();
        } else if (duration == TimeStamp.MATCH_PARENT_VALUE && parentDuration != TimeStamp.MATCH_PARENT_VALUE) {
            renderDurationNs = parentDuration.toNanos() - startTime.toNanos();
        } else {
            renderDurationNs = renderTimeNs;
        }
        renderTime.setDuration(renderTimeNs);
        renderDuration.setDuration(renderDurationNs);
    }

    private void loadKeyFrame() {
        if (loadKeyFrame(GLLayer.KEY_FRAMES_X, keyframeTempValue)) {
            renderX = keyframeTempValue[0];
        }
        if (loadKeyFrame(GLLayer.KEY_FRAMES_Y, keyframeTempValue)) {
            renderY = keyframeTempValue[0];
        }
        if (loadKeyFrame(GLLayer.KEY_FRAMES_WIDTH, keyframeTempValue)) {
            renderWidth = keyframeTempValue[0];
        }
        if (loadKeyFrame(GLLayer.KEY_FRAMES_HEIGHT, keyframeTempValue)) {
            renderHeight = keyframeTempValue[0];
        }
        if (loadKeyFrame(GLLayer.KEY_FRAMES_SCALE_X, keyframeTempValue)) {
            renderScaleX = keyframeTempValue[0];
        }
        if (loadKeyFrame(GLLayer.KEY_FRAMES_SCALE_Y, keyframeTempValue)) {
            renderScaleY = keyframeTempValue[0];
        }
        if (loadKeyFrame(GLLayer.KEY_FRAMES_TRANSLATE_X, keyframeTempValue)) {
            renderTranslateX = keyframeTempValue[0];
        }
        if (loadKeyFrame(GLLayer.KEY_FRAMES_TRANSLATE_Y, keyframeTempValue)) {
            renderTranslateY = keyframeTempValue[0];
        }
        if (loadKeyFrame(GLLayer.KEY_FRAMES_ROTATION, keyframeTempValue)) {
            renderRotation = keyframeTempValue[0];
        }
    }

    private boolean loadKeyFrame(String key,
                                 float[] floatValue) {
        KeyframeSet keyFrames = getKeyFrames(key);
        if (keyFrames == null) {
            return false;
        }
        Class valueType = keyFrames.getValueType();
        boolean success = keyFrames.getValue(renderTime, renderDuration, resultKeyFrame);
        if (!success) {
            return false;
        }
        if (valueType == int.class
                || valueType == float.class) {
            floatValue[0] = (float) resultKeyFrame.getValue();
        } else if (valueType == int[].class) {
            int[] arr = (int[]) resultKeyFrame.getValue();
            floatValue[0] = (float) arr[0];
        } else if (valueType == float[].class) {
            float[] arr = (float[]) resultKeyFrame.getValue();
            floatValue[0] = arr[0];
        } else {
            return false;
        }
        return true;
    }


    private void loadScaleMode() {
        this.renderWidth = scaleMode.getWidth(renderWidth, renderHeight, parentRenderWidth, parentRenderHeight);
        this.renderHeight = scaleMode.getHeight(renderWidth, renderHeight, parentRenderWidth, parentRenderHeight);
    }

    private void loadGravityMode() {
        renderX = gravity.getX(renderX, renderWidth, parentRenderWidth);
        renderY = gravity.getY(renderY, renderHeight, parentRenderHeight);
    }


    private void loadViewPortMatrix() {

        viewPortMatrix.clearIdentity();
        viewPortMatrix.scale(
                renderWidth / 2,
                -renderHeight / 2,
                1);
        viewPortMatrix.translate(
                renderWidth / 2,
                renderHeight / 2,
                0);

        transformMatrix.reset();
        transformMatrix.postTranslate(
                -renderWidth / 2,
                -renderHeight / 2);
        transformMatrix.postScale(
                renderScaleX,
                renderScaleY);
        transformMatrix.postRotate(renderRotation);
        transformMatrix.postTranslate(
                renderTranslateX + renderX,
                renderTranslateY + renderY);
        transformMatrix.postTranslate(
                renderWidth / 2,
                renderHeight / 2);
        transformMatrix.invert(transformInvertMatrix);

        viewPortMatrix.postMul(transformMatrix);
        viewPortMatrix.translate(
                -parentRenderWidth / 2,
                -parentRenderHeight / 2,
                0);
        viewPortMatrix.scale(
                2 / parentRenderWidth,
                -2 / parentRenderHeight,
                1);
    }


    protected abstract boolean onRenderLayer(GLRenderClient client, GLFrameBuffer inputBuffer);


    protected boolean dispatchTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        if (!isEnable()) {
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
                if (downed && inTouchRange(localX, localY)) {
                    downed = false;
                    if (onClickListener != null) {
                        onClickListener.onClick(this);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!inTouchRange(localX, localY)) {
                    downed = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                downed = false;
                break;
        }
        return true;
    }

    public void queueTouchEvent(MotionEvent event) {
        MotionEvent motionEvent = MotionEvent.obtain(event);
        motionEventQueue.offer(motionEvent);
    }

    public void cleanTouchEvent() {
        motionEventQueue.clear();
    }

    boolean inTouchRange(float localX, float localY) {
        return localX >= -touchSlop && localY >= -touchSlop && localX < (renderWidth + touchSlop) &&
                localY < (renderHeight + touchSlop);
    }


    final void mapPoint(float x, float y, float[] dst) {
        transformTempPoint[0] = x;
        transformTempPoint[1] = y;
        transformInvertMatrix.mapPoints(dst, transformTempPoint);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(@NonNull Dimension width) {
        this.width = width;
    }

    public void setWidth(int width) {
        widthTemp.setLength(width);
        setWidth(widthTemp);
    }

    public void setHeight(@NonNull Dimension height) {
        this.height = height;
    }

    public void setHeight(int height) {
        heightTemp.setLength(height);
        setHeight(heightTemp);
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


    public float getX() {
        return x;
    }


    public float getY() {
        return y;
    }


    public Dimension getWidth() {
        return width;
    }


    public Dimension getHeight() {
        return height;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public boolean isEnable() {
        return enable;
    }

    public TimeStamp getStartTime() {
        return startTime;
    }

    public void setStartTime(TimeStamp startTime) {
        this.startTime = startTime;
    }

    public void setDuration(TimeStamp duration) {
        this.duration = duration;
    }

    public TimeStamp getDuration() {
        return duration;
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


    public void setScaleMode(@NonNull ScaleMode layerScaleMode) {
        this.scaleMode = layerScaleMode;
    }

    public ScaleMode getScaleMode() {
        return scaleMode;
    }

    public float getRenderRotation() {
        return renderRotation;
    }

    public float getRenderScaleX() {
        return renderScaleX;
    }

    public float getRenderScaleY() {
        return renderScaleY;
    }

    public float getRenderY() {
        return renderY;
    }

    public float getRenderX() {
        return renderX;
    }

    public float getRenderTranslateX() {
        return renderTranslateX;
    }

    public float getRenderTranslateY() {
        return renderTranslateY;
    }

    public TimeStamp getRenderTime() {
        return renderTime;
    }

    public TimeStamp getRenderDuration() {
        return renderDuration;
    }

    public float getRenderWidth() {
        return renderWidth;
    }


    public float getRenderHeight() {
        return renderHeight;
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

    public Set<String> getKeyFrameSet() {
        return keyframesMap.keySet();
    }

    public KeyframeSet getKeyFrames(String key) {
        return keyframesMap.get(key);
    }


    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void addEffect(GLEffect effect) {
        effectGroup.add(effect);
    }

    public void removeEffect(GLEffect effect) {
        effectGroup.remove(effect);
    }


    public void addAfterEffect(GLEffect effect) {
        afterEffectGroup.add(effect);
    }

    public void removeAfterEffect(GLEffect effect) {
        afterEffectGroup.remove(effect);
    }


    public GLEffectGroup getEffectGroup() {
        return effectGroup;
    }

    public GLEffectGroup getAfterEffectGroup() {
        return afterEffectGroup;
    }

    public void addOnTransformListener(OnTransformListener transform) {
        transformListeners.add(transform);
    }

    public void removeOnTransformListener(OnTransformListener transform) {
        transformListeners.remove(transform);
    }

    public boolean contains(OnTransformListener transform) {
        return transformListeners.contains(transform);
    }

    public void addOnTransformListener(Collection<OnTransformListener> transformCollection) {
        transformListeners.addAll(transformCollection);
    }

    public void addOnTransformListener(int index, OnTransformListener transform) {
        transformListeners.add(index, transform);
    }

    public void clearTransforms() {
        transformListeners.clear();
    }

    public void addOnRenderListener(OnRenderListener renderListener) {
        if (renderListenerList.contains(renderListener)) {
            return;
        }
        renderListenerList.add(renderListener);
    }

    public void removeOnRenderListener(OnRenderListener renderListener) {
        if (!renderListenerList.contains(renderListener)) {
            return;
        }
        renderListenerList.remove(renderListener);
    }


    public interface OnTransformListener {
        void onTransform(GLLayer layer, TimeStamp renderTime);
    }

    public interface OnTouchListener {
        boolean onTouch(GLLayer layer, MotionEvent event);
    }

    public interface OnClickListener {
        void onClick(GLLayer layer);
    }

    public interface OnReadBitmapListener {
        void onReadBitmap(GLLayer layer, Bitmap bitmap);
    }

    public interface OnRenderListener {
        void onRenderStart(GLLayer layer);

        void onRenderEnd(GLLayer layer, boolean success);
    }
}
