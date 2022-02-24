package com.byteplay.android.renderclient;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.byteplay.android.renderclient.math.Matrix4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GLLayer extends GLObject {


    public static final long DURATION_MATCH_PARENT = -1;
    public static final int SIZE_MATCH_PARENT = -1;
    public static final String KEY_FRAMES_KEY_LAYER_X = "layer_x";
    public static final String KEY_FRAMES_KEY_LAYER_Y = "layer_y";
    public static final String KEY_FRAMES_KEY_LAYER_WIDTH = "layer_width";
    public static final String KEY_FRAMES_KEY_LAYER_HEIGHT = "layer_height";
    public static final String KEY_FRAMES_KEY_LAYER_SCALE_X = "layer_scaleX";
    public static final String KEY_FRAMES_KEY_LAYER_SCALE_Y = "layer_scaleY";
    public static final String KEY_FRAMES_KEY_LAYER_ROTATION= "layer_rotation";
    public static final String KEY_FRAMES_KEY_LAYER_TRANSLATE_X = "layer_translateX";
    public static final String KEY_FRAMES_KEY_LAYER_TRANSLATE_Y = "layer_translateY";



    private String vertexShaderCode;
    private String fragmentShaderCode;
    private GLDraw draw;
    private GLShaderParam shaderParam;
    private GLShaderParam defaultShaderParam;
    private GLEnable enable;
    private GLXfermode xfermode;
    private GLGravity gravity = GLGravity.CENTER;
    private int x;
    private int y;
    private int width = SIZE_MATCH_PARENT;
    private int height = SIZE_MATCH_PARENT;
    private long timeMs;
    private int renderX;
    private int renderY;
    private int renderWidth;
    private int renderHeight;
    private float renderScaleX = 1;
    private float renderScaleY = 1;
    private float renderRotation = 0;
    private float renderTranslateX = 0;
    private float renderTranslateY = 0;
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
    private long renderDuration = DURATION_MATCH_PARENT;
    private Map<String, GLKeyframeSet> keyframesMap = new HashMap<>();
    private GLEffectSet effectSet;
    private final Matrix4 viewPortMatrix = new Matrix4();

    protected GLLayer(GLRenderClient client, String vertexShaderCode, String fragmentShaderCode, GLDraw draw) {
        super(client);
        this.enable = client.newEnable();
        this.xfermode = GLXfermode.SRC_OVER;
        this.draw = draw;
        this.vertexShaderCode = vertexShaderCode;
        this.fragmentShaderCode = fragmentShaderCode;
        this.shaderParam = client.newShaderParam();
        this.defaultShaderParam = client.newShaderParam();
        this.effectSet = client.newEffectSet();
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
    }

    public void render(GLFrameBuffer frameBuffer) {
        render(frameBuffer, getTime());
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


    protected void render(GLFrameBuffer frameBuffer, long timeMs) {
        client.render(this, frameBuffer, timeMs);
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
        effectSet.add(effect);
    }

    public void removeEffect(GLEffect effect) {
        if (effect == null || !effectSet.contains(effect)) return;
        effectSet.remove(effect);
    }

    public int getEffectIndex(GLEffect effect) {
        return effectSet.indexOf(effect);
    }

    public void addEffect(Collection<GLEffect> effects) {
        effectSet.addAll(effects);
    }

    public void removeEffect(Collection<GLEffect> effects) {
        effectSet.removeAll(effects);
    }


    public int getEffectSize() {
        return effectSet.size();
    }

    public GLEffectSet getEffectSet() {
        return effectSet;
    }

    public GLEffect getEffect(int index) {
        return index < 0 || index >= getEffectSize() ? null : effectSet.get(index);
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

    public void setKeyframe(String key, GLKeyframeSet keyframeSet) {
        keyframesMap.put(key, keyframeSet);
    }

    public Set<String> getKeyframeKeySet() {
        return keyframesMap.keySet();
    }

    public GLKeyframeSet getKeyframes(String key) {
        return keyframesMap.get(key);
    }

    public int getTransformSize() {
        return layerTransforms.size();
    }

    public LayerTransform getTransform(int index) {
        return index < 0 || index >= getTransformSize() ? null : layerTransforms.get(index);
    }

    public int getIndexOfTransform(LayerTransform layerTransform) {
        return layerTransforms.indexOf(layerTransform);
    }

    public void clearTransform() {
        layerTransforms.clear();
    }


    public GLEnable getEnable() {
        return enable;
    }

    public void setFragmentShaderCode(String fragmentShaderCode) {
        this.fragmentShaderCode = fragmentShaderCode;
    }

    public void setVertexShaderCode(String vertexShaderCode) {
        this.vertexShaderCode = vertexShaderCode;
    }

    public String getVertexShaderCode() {
        return vertexShaderCode;
    }

    public String getFragmentShaderCode() {
        return fragmentShaderCode;
    }


    public void setTime(long timeMs) {
        this.timeMs = timeMs;
    }

    public long getTime() {
        return timeMs;
    }


    public void setDraw(GLDraw draw) {
        this.draw = draw;
    }

    public void setEnable(GLEnable enable) {
        this.enable = enable;
    }

    public void putShaderParam(String position, float... coordinates) {
        shaderParam.put(position, coordinates);
    }

    public void putShaderParam(String position, boolean coordinates) {
        shaderParam.put(position, coordinates);
    }

    public void putShaderParam(GLShaderParam param) {
        shaderParam.put(param);
    }

    public void clearShaderParam() {
        shaderParam.clear();
    }


    public GLShaderParam getShaderParam() {
        return shaderParam;
    }

    public GLShaderParam getDefaultShaderParam() {
        return defaultShaderParam;
    }

    public void setXfermode(GLXfermode xfermode) {
        this.xfermode = xfermode;
    }

    public GLXfermode getXfermode() {
        return xfermode;
    }

    public GLDraw getDraw() {
        return draw;
    }


    public void setGravity(GLGravity gravity) {
        this.gravity = gravity;
    }

    public GLGravity getGravity() {
        return gravity;
    }

    protected void onRenderViewPortMatrix(int frameWidth, int frameHeight) {
        viewPortMatrix.setIdentity();

        float x = gravity.getX(renderX, renderWidth, frameWidth);
        float y = gravity.getY(renderY, renderHeight, frameHeight);
        viewPortMatrix.scale(renderScaleX * renderWidth / 2, renderScaleY * renderHeight / 2, 1);
        viewPortMatrix.rotate(renderRotation, 0, 0, 1);
        viewPortMatrix.translate(renderTranslateX + x - (frameWidth / 2.0f - renderWidth / 2.0f), -(renderTranslateY + y - (frameHeight / 2.0f - renderHeight / 2.0f)), 0);
        viewPortMatrix.scale(2.0f / frameWidth, 2.0f / frameHeight, 1);
    }

    public Matrix4 getViewPortMatrix() {
        return viewPortMatrix;
    }

    protected boolean onRenderLayer(GLLayer layer, long renderTimeMs) {
        return true;
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

    protected void setRenderX(int renderX) {
        this.renderX = renderX;
    }

    public float getRenderY() {
        return renderY;
    }

    protected void setRenderY(int renderY) {
        this.renderY = renderY;
    }

    public int getRenderWidth() {
        return renderWidth;
    }

    protected void setRenderWidth(int renderWidth) {
        this.renderWidth = renderWidth;
    }

    public int getRenderHeight() {
        return renderHeight;
    }

    protected void setRenderHeight(int renderHeight) {
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

    public void setRenderTranslateX(float renderTranslateX) {
        this.renderTranslateX = renderTranslateX;
    }

    public float getRenderTranslateY() {
        return renderTranslateY;
    }

    public void setRenderTranslateY(float renderTranslateY) {
        this.renderTranslateY = renderTranslateY;
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
}
