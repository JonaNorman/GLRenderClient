package com.byteplay.android.renderclient;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.GLException;
import android.os.Build;
import android.util.LruCache;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

class EGL14RenderClient extends GLRenderClient {

    private static final int MAX_PROGRAM_SIZE = 20;
    private static final int MAX_EGL_CONFIG_SIZE = 100;
    private static final int MAX_FRAME_BUFFER_CACHE_SIZE = 5;
    private static final String RENDER_TIME = "renderTime";
    private static final String VIEW_PORT_SIZE = "viewPortSize";
    private final Queue<GLFrameBuffer> frameBufferCache = new LinkedList<>();
    private final EGLDisplay eglDisplay;
    private final EGLContext eglContext;
    private final android.opengl.EGLConfig eglConfig;
    private final EGLPbufferSurface defaultBufferSurface;
    private final GL20 gl20;
    private final List<GLObject> objectList = new ArrayList<>();
    private final Map<String, GLShader> shaderMap = new HashMap<>();
    private final Map<GLShader, Integer> shaderUsingMap = new HashMap<>();
    private final List<EGLSurface> eglSurfaceList = new ArrayList<>();
    private final Map<Object, android.opengl.EGLSurface> windowSurfaceCache = new HashMap<>();
    private final Map<Object, List<EGLWindowSurface>> windowSurfaceUsingMap = new HashMap<>();
    private final LruCache<Integer, GLProgram> programCache = new LruCache<Integer, GLProgram>(MAX_PROGRAM_SIZE) {
        @Override
        protected void entryRemoved(boolean evicted, Integer key, GLProgram oldValue, GLProgram newValue) {
            disposeProgram(oldValue);
        }
    };
    private final GL20Monitor gl20ErrorMonitor = new GL20CallMonitor() {
        @Override
        protected void onCall() {
            super.onCall();
            checkRelease();
            checkThread();
            if (checkGLError) {
                while (gl20.glGetError() != GL20.GL_NO_ERROR) {
                }
            }
        }

        @Override
        public void glGetError(int error) {
            if (error != GL20.GL_NO_ERROR) {
                GLException exception = new GLException(error);
                for (GLErrorListener listener : errorListenerList) {
                    listener.onError(exception);
                }
                if (throwError) {
                    throw exception;
                }
            }
        }
    };

    @Override
    protected void checkThread() {
        if (checkThread) {
            Thread currentThread = Thread.currentThread();
            if (attachThread == null) {
                throw new IllegalStateException("must first attachThread");
            }
            if (!currentThread.equals(attachThread)) {
                throw new IllegalStateException("must call in attachThread " + attachThread.getName());
            }
        }
    }

    @Override
    protected void checkRelease() {
        if (isRelease()) {
            throw new IllegalStateException("client is released");
        }
    }

    private final GLColorLayer backgroundColorLayer;
    private final GLTextureLayer outTextureLayer;
    private final GLViewPort tempViewPort;
    private boolean release;
    private final GLBlend blend;
    private int maxFrameBufferCacheSize = MAX_FRAME_BUFFER_CACHE_SIZE;
    private final List<GLErrorListener> errorListenerList = new ArrayList<>();
    private boolean throwError = true;
    private boolean checkGLError = true;
    private boolean checkThread = true;
    private boolean checkEGLError = true;
    private int maxTextureSize;
    private Thread attachThread;
    private EGLSurface currentEGLSurface;
    private GLFrameBuffer bindFrameBuffer;
    private GLEnable tempEnable;


    public EGL14RenderClient(EGLContext shareContext, EGLConfigChooser configChooser) {
        if (configChooser == null) {
            throw new NullPointerException("GLConfigChooser must not null");
        }
        EGLDisplay display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (display == EGL14.EGL_NO_DISPLAY || display == null) {
            throw new RuntimeException("unable to get EGL14 display");
        }
        int[] eglVersion = new int[2];
        if (!EGL14.eglInitialize(display, eglVersion, 0, eglVersion, 1)) {
            throw new RuntimeException("unable to initialize EGL14");
        }
        checkEGLError();
        int[] numConfig = new int[1];
        android.opengl.EGLConfig[] configs = new android.opengl.EGLConfig[MAX_EGL_CONFIG_SIZE];// null  will crash in other devices
        if (!EGL14.eglGetConfigs(display, configs, 0, configs.length, numConfig, 0)) {
            throw new IllegalArgumentException("eglGetConfigs failed.");
        }
        checkEGLError();
        if (numConfig[0] <= 0) {
            throw new IllegalArgumentException("no eglConfigs match configSpec");
        }
        android.opengl.EGLConfig[] eglConfigs = new android.opengl.EGLConfig[numConfig[0]];
        if (!EGL14.eglGetConfigs(display, eglConfigs, 0, numConfig[0], numConfig, 0)) {
            throw new IllegalArgumentException("eglGetConfigs  failed.");
        }
        ArrayList<EGL14Config> egl14ConfigList = new ArrayList<>();
        for (int i = 0; i < eglConfigs.length; i++) {
            if (eglConfigs[i] != null) {
                egl14ConfigList.add(new EGL14Config(display, eglConfigs[i]));
            }
        }
        checkEGLError();
        EGL14Config[] chooseConfigs = new EGL14Config[egl14ConfigList.size()];
        egl14ConfigList.toArray(chooseConfigs);
        EGLConfig chooseConfig = configChooser.chooseConfig(chooseConfigs);
        if (chooseConfig == null) {
            throw new NullPointerException("choose config is null");
        }
        android.opengl.EGLConfig config = chooseConfig.getEGLConfig();
        int createVersion = chooseConfig.isRenderGL30() ? 3 : (chooseConfig.isRenderGL20() ? 2 : 1);
        EGLContext context = EGL14.eglCreateContext(display, config, shareContext == null ? EGL14.EGL_NO_CONTEXT : shareContext,
                new int[]{
                        EGL14.EGL_CONTEXT_CLIENT_VERSION, createVersion,
                        EGL14.EGL_NONE}, 0);
        checkEGLError();
        if (context == EGL14.EGL_NO_CONTEXT || context == null) {
            throw new RuntimeException("unable to create EGLContext");
        }
        eglDisplay = display;
        eglConfig = config;
        eglContext = context;
        this.defaultBufferSurface = newBufferSurface(1, 1);
        this.gl20 = new GL20Command(this);
        this.bindFrameBuffer = newFrameBuffer(defaultBufferSurface);
        this.gl20.addGLMonitor(gl20ErrorMonitor);
        backgroundColorLayer = newColorLayer();
        outTextureLayer = newTextureLayer();
        tempViewPort = newViewPort();
        tempEnable = newEnable();
        blend = newBlend();
    }


    @Override
    public void attachCurrentThread() {
        checkRelease();
        Thread currentThread = Thread.currentThread();
        if (!currentThread.equals(attachThread)) {
            defaultBufferSurface.makeCurrent();
            attachThread = currentThread;
        }
    }

    @Override
    public void detachThread() {
        if (attachThread == null) {
            return;
        }
        checkRelease();
        Thread currentThread = Thread.currentThread();
        if (!currentThread.equals(attachThread)) {
            throw new IllegalThreadStateException("detach must in attached thread");
        }
        if (currentEGLSurface != null) {
            currentEGLSurface.makeNoCurrent();
        }
    }

    @Override
    public Thread getAttachThread() {
        return attachThread;
    }

    @Override
    protected void render(GLLayer layer, GLFrameBuffer outputBuffer, long renderTimeMs) {
        if (outputBuffer == null) {
            throw new IllegalArgumentException("outputBuffer is null");
        }
        long startTime = layer.getStartTime();
        if (layer.getRenderDuration() == GLLayer.DURATION_MATCH_PARENT) {
            layer.setRenderDuration(layer.getDuration() != GLLayer.DURATION_MATCH_PARENT ? layer.getDuration() : Long.MAX_VALUE / 2);
        }
        if (renderTimeMs > startTime + layer.getRenderDuration() || renderTimeMs < startTime) {
            return;
        }
        long layerTimeMs = renderTimeMs - startTime;
        if (!layer.isDisposed()) {
            layer.create();
        }
        int frameWidth = outputBuffer.getWidth();
        int frameHeight = outputBuffer.getHeight();
        GLViewPort viewPort = layer.getViewPort();
        int renderWidth = layer.getWidth() == GLLayer.SIZE_MATCH_PARENT ? frameWidth : Math.max(layer.getWidth(), 0);
        int renderHeight = layer.getWidth() == GLLayer.SIZE_MATCH_PARENT ? frameHeight : Math.max(layer.getHeight(), 0);
        viewPort.set(layer.getX(),
                layer.getY(),
                renderWidth,
                renderHeight);
        onViewPortKeyFrame(layer, viewPort, renderTimeMs);
        layer.onViewPort(viewPort, frameWidth, frameHeight);
        int transformSize = layer.getTransformSize();
        for (int i = 0; i < transformSize; i++) {
            GLLayer.LayerTransform transform = layer.getTransform(i);
            transform.onLayerTransform(layer, layerTimeMs);
        }
        if (viewPort.getWidth() <= 0 || viewPort.getHeight() <= 0) {
            return;
        }
        if (layer instanceof GLLayerGroup) {
            render((GLLayerGroup) layer, outputBuffer, layerTimeMs);
            return;
        }

        if (layer.getBackgroundColor() != Color.TRANSPARENT) {
            backgroundColorLayer.setColor(layer.getBackgroundColor());
            renderLayer(backgroundColorLayer, outputBuffer, viewPort, backgroundColorLayer.getXfermode(), layerTimeMs);
        }
        int effectCount = layer.getEffectSize();
        if (effectCount > 0) {
            int effectWidth = viewPort.getWidth();
            int effectHeight = viewPort.getHeight();
            GLFrameBuffer inBuffer = obtainFrameBuffer(effectWidth, effectHeight);
            tempViewPort.setWidth(effectWidth);
            tempViewPort.setHeight(effectHeight);
            renderLayer(layer, inBuffer, tempViewPort, GLXfermode.SRC, layerTimeMs);
            GLEffectSet effectSet = layer.getEffectSet();
            effectSet.setRenderDuration(effectSet.getDuration() == GLLayer.DURATION_MATCH_PARENT ?
                    layer.getRenderDuration() :
                    effectSet.getDuration());
            GLFrameBuffer effectBuffer = effectSet.apply(inBuffer, layerTimeMs);
            if (inBuffer != effectBuffer) {
                cacheFrameBuffer(inBuffer);
            }
            GLTexture effectTexture = effectBuffer.getColorTexture();
            outTextureLayer.setTexture(effectTexture);
            renderLayer(outTextureLayer, outputBuffer, viewPort, layer.getXfermode(), layerTimeMs);
            cacheFrameBuffer(effectBuffer);
        } else {
            renderLayer(layer, outputBuffer, viewPort, layer.getXfermode(), layerTimeMs);
        }
    }

    @Override
    protected void render(GLLayerGroup frameLayer, GLFrameBuffer outputBuffer, long renderTimeMs) {
        if (outputBuffer == null) {
            throw new IllegalArgumentException("outputBuffer is null");
        }
        GLViewPort viewPort = frameLayer.getViewPort();
        int currentWidth = viewPort.getWidth();
        int currentHeight = viewPort.getHeight();
        GLFrameBuffer currentFrameBuffer = obtainFrameBuffer(currentWidth, currentHeight);
        if (frameLayer.getBackgroundColor() != Color.TRANSPARENT) {
            backgroundColorLayer.setColor(frameLayer.getBackgroundColor());
            renderLayer(backgroundColorLayer, outputBuffer, viewPort, backgroundColorLayer.getXfermode(), renderTimeMs);
        }
        String vertexCode = frameLayer.getVertexShaderCode();
        String fragmentCode = frameLayer.getFragmentShaderCode();
        if (vertexCode != null &&
                fragmentCode != null) {
            tempViewPort.setWidth(currentWidth);
            tempViewPort.setHeight(currentHeight);
            renderLayer(frameLayer, currentFrameBuffer, tempViewPort, frameLayer.getSelfXfermode(), renderTimeMs);
        }
        for (int i = 0; i < frameLayer.getLayerSize(); i++) {
            GLLayer layer = frameLayer.get(i);
            layer.setRenderDuration(layer.getDuration() == GLLayer.DURATION_MATCH_PARENT ?
                    frameLayer.getRenderDuration() :
                    layer.getDuration());
            if (layer instanceof GLLayerGroup) {
                GLLayerGroup glLayerSet = (GLLayerGroup) layer;
                glLayerSet.render(currentFrameBuffer, renderTimeMs);
            } else {
                layer.render(currentFrameBuffer, renderTimeMs);
            }
        }
        if (frameLayer.getEffectSize() > 0) {
            GLEffectSet effectSet = frameLayer.getEffectSet();
            effectSet.setRenderDuration(effectSet.getDuration() == GLLayer.DURATION_MATCH_PARENT ?
                    frameLayer.getRenderDuration() :
                    effectSet.getDuration());
            GLFrameBuffer effectBuffer = effectSet.apply(currentFrameBuffer, renderTimeMs);
            if (currentFrameBuffer != effectBuffer) {
                cacheFrameBuffer(currentFrameBuffer);
            }
            GLTexture effectTexture = effectBuffer.getColorTexture();
            outTextureLayer.setTexture(effectTexture);
            renderLayer(outTextureLayer, outputBuffer, viewPort, frameLayer.getXfermode(), renderTimeMs);
            cacheFrameBuffer(effectBuffer);
        } else {
            GLTexture currentTexture = currentFrameBuffer.getColorTexture();
            outTextureLayer.setTexture(currentTexture);
            renderLayer(outTextureLayer, outputBuffer, viewPort, frameLayer.getXfermode(), renderTimeMs);
            cacheFrameBuffer(currentFrameBuffer);
        }
    }

    private void renderLayer(GLLayer layer, GLFrameBuffer outputBuffer, GLViewPort viewPort, GLXfermode xfermode, long renderTimeMs) {
        GLProgram program = getGlProgram(layer.getVertexShaderCode(), layer.getFragmentShaderCode());
        program.setDraw(layer.getDraw());
        GLFrameBuffer old = outputBuffer.bind();
        viewPort.call();
        GLEnable enable = layer.getEnable();
        enable.call();
        xfermode.apply(blend);
        blend.call();
        program.clearShaderParam();
        GLShaderParam programParam = program.getShaderParam();
        programParam.put(RENDER_TIME, renderTimeMs / 1000.0f);
        programParam.put(VIEW_PORT_SIZE, viewPort.getWidth(), viewPort.getHeight());
        boolean render = layer.onRenderLayer(layer, renderTimeMs);
        if (!render) {
            return;
        }
        programParam.put(layer.getDefaultShaderParam());
        for (String key : layer.getKeyframeKeySet()) {
            GLKeyframes keyFrames = layer.getKeyframes(key);
            long duration = Math.min(keyFrames.getDuration() - keyFrames.getStartTime(), layer.getRenderDuration() - keyFrames.getStartTime());
            if (duration <= 0) {
                continue;
            }
            float fraction = (renderTimeMs - keyFrames.getStartTime()) * 1.0f / duration;
            if (fraction < 0 || fraction > 1) {
                continue;
            }
            programParam.put(key, keyFrames.getValue(fraction));
        }
        programParam.put(layer.getShaderParam());
        program.execute();
        EGLSurface eglSurface = outputBuffer.getEGLSurface();
        if (eglSurface instanceof EGLWindowSurface) {
            EGLWindowSurface windowSurface = (EGLWindowSurface) eglSurface;
            windowSurface.setTime(renderTimeMs * 1000000L);
        }
        old.bind();
    }

    @Override
    protected GLFrameBuffer applyEffect(GLEffect effect, GLFrameBuffer input, long renderTimeMs) {
        long startTime = effect.getStartTime();
        if (effect.getRenderDuration() == GLLayer.DURATION_MATCH_PARENT) {
            effect.setRenderDuration(effect.getDuration() != GLLayer.DURATION_MATCH_PARENT ? effect.getDuration() : Long.MAX_VALUE / 2);
        }
        if (renderTimeMs > startTime + effect.getRenderDuration() || renderTimeMs < startTime) {
            return input;
        }
        long effectTimeMs = renderTimeMs - startTime;
        return effect.actualApplyEffect(effect, input, effectTimeMs);
    }

    @Override
    protected GLFrameBuffer applyEffect(GLEffectSet effectSet, GLFrameBuffer input, long renderTimeMs) {
        int effectCount = effectSet.size();
        if (effectCount > 0) {
            GLFrameBuffer out = input;
            for (int i = 0; i < effectCount; i++) {
                GLEffect effect = effectSet.get(i);
                effect.setRenderDuration(effect.getDuration() == GLLayer.DURATION_MATCH_PARENT ?
                        effectSet.getRenderDuration() :
                        effect.getDuration());
                GLFrameBuffer effectBuffer = effect.apply(out, renderTimeMs);
                if (effectBuffer != out && input != out) {
                    cacheFrameBuffer(out);
                }
                out = effectBuffer;
            }
            return out;
        } else {
            return input;
        }
    }


    @Override
    protected GLFrameBuffer applyEffect(GLShaderEffect effect, GLFrameBuffer input, long renderTimeMs) {
        if (effect.getRenderDuration() == GLLayer.DURATION_MATCH_PARENT) {
            effect.setRenderDuration(effect.getDuration() != GLLayer.DURATION_MATCH_PARENT ? effect.getDuration() : Long.MAX_VALUE / 2);
        }
        long startTime = effect.getStartTime();
        if (renderTimeMs > startTime + effect.getRenderDuration() || renderTimeMs < startTime) {
            return input;
        }
        GLFrameBuffer outputBuffer = obtainFrameBuffer(input.getWidth(), input.getHeight());
        tempViewPort.setWidth(input.getWidth());
        tempViewPort.setHeight(input.getHeight());
        long effectTime = renderTimeMs - startTime;
        GLProgram program = getGlProgram(effect.getVertexShaderCode(), effect.getFragmentShaderCode());
        program.setDraw(effect.getDraw());
        GLFrameBuffer old = outputBuffer.bind();
        tempViewPort.call();
        tempEnable.call();
        GLXfermode.SRC.apply(blend);
        blend.call();
        program.clearShaderParam();
        GLShaderParam programParam = program.getShaderParam();
        programParam.put(RENDER_TIME, effectTime / 1000.0f);
        programParam.put(VIEW_PORT_SIZE, tempViewPort.getWidth(), tempViewPort.getHeight());
        effect.onApplyShaderEffect(effect, input, effectTime);
        programParam.put(effect.getDefaultShaderParam());
        for (String key : effect.getKeyframeKeySet()) {
            GLKeyframes keyFrames = effect.getKeyframes(key);
            long duration = Math.min(keyFrames.getDuration() - keyFrames.getStartTime(), effect.getRenderDuration() - keyFrames.getStartTime());
            if (duration <= 0) {
                continue;
            }
            float fraction = (effectTime - keyFrames.getStartTime()) * 1.0f / duration;
            if (fraction < 0 || fraction > 1) {
                continue;
            }
            programParam.put(key, keyFrames.getValue(fraction));
        }
        programParam.put(effect.getShaderParam());
        program.execute();
        old.bind();
        return outputBuffer;
    }


    private GLFrameBuffer obtainFrameBuffer(int currentWidth, int currentHeight) {
        GLFrameBuffer currentFrameBuffer = null;
        int cacheSize = frameBufferCache.size();
        for (int i = 0; i < cacheSize; i++) {
            GLFrameBuffer frameBuffer = frameBufferCache.element();
            if (frameBuffer != null
                    && frameBuffer.getWidth() == currentWidth
                    && frameBuffer.getHeight() == currentHeight) {
                frameBufferCache.remove(frameBuffer);
                currentFrameBuffer = frameBuffer;
                break;
            }
        }
        if (currentFrameBuffer == null) {
            currentFrameBuffer = frameBufferCache.poll();
        }
        if (currentFrameBuffer == null) {
            currentFrameBuffer = newFrameBuffer(currentWidth, currentHeight);
        } else {
            currentFrameBuffer.setSize(currentWidth, currentHeight);
            currentFrameBuffer.clearColor(Color.TRANSPARENT);
        }
        return currentFrameBuffer;
    }

    private void cacheFrameBuffer(GLFrameBuffer currentFrameBuffer) {
        if (currentFrameBuffer == null) return;
        frameBufferCache.offer(currentFrameBuffer);
        while (frameBufferCache.size() > maxFrameBufferCacheSize) {
            GLFrameBuffer frameBuffer = frameBufferCache.poll();
            frameBuffer.dispose();
        }
    }

    private void onViewPortKeyFrame(GLLayer layer, GLViewPort viewPort, long renderTimeMs) {
        GLKeyframes keyFrames = layer.getKeyframes(GLLayer.KEY_FRAMES_KEY_LAYER_X);
        if (keyFrames != null) {
            long duration = Math.min(keyFrames.getDuration() - keyFrames.getStartTime(), layer.getRenderDuration() - keyFrames.getStartTime());
            if (duration > 0) {
                viewPort.setX((int) (keyFrames.getValue((renderTimeMs - keyFrames.getStartTime()) * 1.0f / duration)[0] + 0.5f));
            }
        }
        keyFrames = layer.getKeyframes(GLLayer.KEY_FRAMES_KEY_LAYER_Y);
        if (keyFrames != null) {
            long duration = Math.min(keyFrames.getDuration() - keyFrames.getStartTime(), layer.getRenderDuration() - keyFrames.getStartTime());
            if (duration > 0) {
                viewPort.setY((int) (keyFrames.getValue((renderTimeMs - keyFrames.getStartTime()) * 1.0f / duration)[0] + 0.5f));
            }
        }
        keyFrames = layer.getKeyframes(GLLayer.KEY_FRAMES_KEY_LAYER_WIDTH);
        if (keyFrames != null) {
            long duration = Math.min(keyFrames.getDuration() - keyFrames.getStartTime(), layer.getRenderDuration() - keyFrames.getStartTime());
            if (duration > 0) {
                viewPort.setWidth((int) (keyFrames.getValue((renderTimeMs - keyFrames.getStartTime()) * 1.0f / duration)[0] + 0.5f));
            }
        }
        keyFrames = layer.getKeyframes(GLLayer.KEY_FRAMES_KEY_LAYER_HEIGHT);
        if (keyFrames != null) {
            long duration = Math.min(keyFrames.getDuration() - keyFrames.getStartTime(), layer.getRenderDuration() - keyFrames.getStartTime());
            if (duration > 0) {
                viewPort.setHeight((int) (keyFrames.getValue((renderTimeMs - keyFrames.getStartTime()) * 1.0f / duration)[0] + 0.5f));
            }
        }
    }


    private GLProgram getGlProgram(String vertexShaderCode, String fragmentShaderCode) {
        GLShader vertexShader = shaderMap.get(vertexShaderCode);
        if (vertexShader == null) {
            vertexShader = newVertexShader();
            vertexShader.setShaderCode(vertexShaderCode);
            shaderMap.put(vertexShaderCode, vertexShader);
        }
        GLShader fragmentShader = shaderMap.get(fragmentShaderCode);
        if (fragmentShader == null) {
            fragmentShader = newFragmentShader();
            fragmentShader.setShaderCode(fragmentShaderCode);
            shaderMap.put(fragmentShaderCode, fragmentShader);
        }
        Integer programHashCode = vertexShaderCode.hashCode() + fragmentShaderCode.hashCode();
        GLProgram program = programCache.get(programHashCode);
        if (program == null) {
            program = new GL20Program(this);
            program.setVertexShader(vertexShader);
            program.setFragmentShader(fragmentShader);
            programCache.put(programHashCode, program);
            Integer count = shaderUsingMap.get(vertexShader);
            if (count == null || count == 0) {
                shaderUsingMap.put(vertexShader, 1);
            } else {
                shaderUsingMap.put(vertexShader, count + 1);
            }
            count = shaderUsingMap.get(fragmentShader);
            if (count == null || count == 0) {
                shaderUsingMap.put(fragmentShader, 1);
            } else {
                shaderUsingMap.put(fragmentShader, count + 1);
            }
        }
        return program;
    }

    @Override
    public GL20 getGL20() {
        return gl20;
    }

    @Override
    public GLBlend newBlend() {
        return new GL20Blend(this);
    }

    @Override
    public GLShader newVertexShader() {
        return new GL20Shader(this, GLShaderType.VERTEX);
    }

    @Override
    public GLShader newFragmentShader() {
        return new GL20Shader(this, GLShaderType.FRAGMENT);
    }

    @Override
    public GLProgram newProgram() {
        return new GL20Program(this);
    }

    @Override
    public GLShaderEffect newShaderEffect() {
        return new GLShaderEffect(this);
    }

    @Override
    public GLEffectSet newEffectSet() {
        return new GLEffectSet(this);
    }

    @Override
    public GLShaderParam newShaderParam() {
        return new GLShaderParam(this);
    }

    @Override
    public GLDrawArray newDrawArray() {
        return new GL20DrawArray(this);
    }

    @Override
    public GLDrawElement newDrawElement() {
        return new GL20DrawElement(this);
    }

    @Override
    public GLEnable newEnable() {
        return new GL20Enable(this);
    }


    @Override
    public GLFrameBuffer newFrameBuffer(int width, int height) {
        return new GL20FrameBuffer(this, width, height);
    }

    @Override
    public GLFrameBuffer newFrameBuffer(EGLSurface eglSurface) {
        return new GL20FrameBuffer(this, eglSurface);
    }

    @Override
    public GLFrameBuffer newFrameBuffer(Surface surface) {
        return new GL20FrameBuffer(this, newWindowSurface(surface));
    }

    @Override
    public GLFrameBuffer newFrameBuffer(SurfaceTexture surface) {
        return new GL20FrameBuffer(this, newWindowSurface(surface));
    }

    @Override
    public GLFrameBuffer newFrameBuffer(SurfaceHolder surface) {
        return new GL20FrameBuffer(this, newWindowSurface(surface));
    }

    @Override
    public GLFrameBuffer newFrameBuffer(GLTexture texture) {
        return new GL20FrameBuffer(this, texture);
    }


    @Override
    public GLFrameBuffer getBindFrameBuffer() {
        return bindFrameBuffer;
    }

    @Override
    public GLTexture newTexture(GLTextureType textureType) {
        return new GL20Texture(this, textureType);
    }

    @Override
    public GLTexture newTexture(GLTextureType textureType, int textureId) {
        return new GL20Texture(this, textureType, textureId);
    }


    @Override
    public GLViewPort newViewPort() {
        return new GL20ViewPort(this);
    }


    @Override
    public EGLWindowSurface newWindowSurface(Surface surface) {
        return new EGL14WindowSurface(this, surface);
    }


    @Override
    public EGLWindowSurface newWindowSurface(SurfaceHolder surface) {
        return new EGL14WindowSurface(this, surface);
    }


    @Override
    public EGLWindowSurface newWindowSurface(SurfaceTexture surface) {
        return new EGL14WindowSurface(this, surface);
    }


    @Override
    public EGLPbufferSurface newBufferSurface(int width, int height) {
        return new EGL14PbufferSurface(this, width, height);
    }


    @Override
    protected EGLPbufferSurface getDefaultEGLBufferSurface() {
        return defaultBufferSurface;
    }

    @Override
    public GLTextureLayer newTextureLayer() {
        return new GLTextureLayer(this);
    }


    @Override
    public GLLayerGroup newLayerGroup() {
        return new GLLayerGroup(this);
    }

    @Override
    public GLFrameLayoutLayer newFrameLayoutLayer(Context context) {
        return new GLFrameLayoutLayer(this, context);
    }

    @Override
    public GLFrameLayoutLayer newFrameLayoutLayer(Context context, int styleRes) {
        return new GLFrameLayoutLayer(this, context, styleRes);
    }

    @Override
    public GLLayer newLayer(String vertexCode, String fragmentCode, GLDraw draw) {
        return new GLLayer(this, vertexCode, fragmentCode, draw);
    }

    @Override
    public GLColorLayer newColorLayer() {
        return new GLColorLayer(this);
    }

    @Override
    public void setMaxProgramSize(int maxProgramSize) {
        programCache.resize(maxProgramSize);
    }


    @Override
    protected void createObject(GLObject object) {
        checkRelease();
        if (!object.isCreated()) {
            objectList.add(object);
            object.createObject();
        }
    }

    @Override
    protected void disposeObject(GLObject object) {
        if (!object.isDisposed()) {
            checkThread();
            objectList.remove(object);
            object.disposeObject();
        }
    }


    @Override
    protected void createEGLSurface(EGLSurface surface) {
        checkRelease();
        if (!surface.isCreated()) {
            eglSurfaceList.add(surface);
            surface.createSurface();
        }
    }

    @Override
    protected void disposeEGLSurface(EGLSurface surface) {
        if (!surface.isDisposed()) {
            eglSurfaceList.remove(surface);
            surface.disposeSurface();
        }
    }


    @Override
    protected void makeCurrentEGLSurface(EGLSurface surface) {
        checkRelease();
        if (surface == null) {
            currentEGLSurface = null;
            boolean success = EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            if (!success) {
                checkEGLError();
            }
        } else if (!Objects.equals(surface, currentEGLSurface)) {
            boolean success = EGL14.eglMakeCurrent(eglDisplay, surface.getEGLSurface(), surface.getEGLSurface(), eglContext);
            if (!success) {
                checkEGLError();
            }
            currentEGLSurface = surface;
        }
    }


    @Override
    protected GLFrameBuffer onBindFrameBuffer(GLFrameBuffer newFrameBuffer) {
        GLFrameBuffer oldFrameBuffer = bindFrameBuffer;
        bindFrameBuffer = newFrameBuffer;
        return oldFrameBuffer;
    }


    @Override
    public void release() {
        if (release) {
            return;
        }
        android.opengl.EGLSurface currentReadEGLSurface = EGL14.eglGetCurrentSurface(EGL14.EGL_READ);
        android.opengl.EGLSurface currentDrawEGLSurface = EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW);
        EGLDisplay currentDisplay = EGL14.eglGetCurrentDisplay();
        EGLContext currentContext = EGL14.eglGetCurrentContext();
        defaultBufferSurface.makeCurrent();
        List<GLObject> disposeObjectList = new ArrayList<>();
        disposeObjectList.addAll(objectList);
        for (GLObject object : disposeObjectList) {
            object.dispose();
        }
        defaultBufferSurface.makeNoCurrent();
        List<EGLSurface> disposeSurfaceList = new ArrayList<>();
        disposeSurfaceList.addAll(eglSurfaceList);
        for (EGLSurface eglSurface : disposeSurfaceList) {
            eglSurface.dispose();
        }
        objectList.clear();
        eglSurfaceList.clear();
        windowSurfaceCache.clear();
        shaderUsingMap.clear();
        shaderMap.clear();
        windowSurfaceUsingMap.clear();
        EGL14.eglDestroyContext(eglDisplay, eglContext);
        checkEGLError();
        EGL14.eglReleaseThread();
        checkEGLError();
        EGL14.eglTerminate(eglDisplay);
        checkEGLError();
        release = true;
        EGL14.eglMakeCurrent(currentDisplay, currentDrawEGLSurface, currentReadEGLSurface, currentContext);
    }


    @Override
    public EGLContext getEGLContext() {
        return eglContext;
    }

    @Override
    protected android.opengl.EGLConfig getEGLConfig() {
        return eglConfig;
    }

    @Override
    protected EGLDisplay getEGLDisplay() {
        return eglDisplay;
    }

    @Override
    public boolean isRelease() {
        return release;
    }

    private void disposeProgram(GLProgram program) {
        disposeObject(program);
        disposeShader(program.getVertexShader());
        disposeShader(program.getFragmentShader());
    }

    private void disposeShader(GLShader shader) {
        Integer count = shaderUsingMap.get(shader);
        count = count - 1;
        if (count <= 0) {
            shaderUsingMap.remove(shader);
            shaderMap.remove(shader.getCompileCode());
            disposeObject(shader);
        } else {
            shaderUsingMap.put(shader, count);
        }
    }

    public void setMaxFrameBufferCacheSize(int maxFrameBufferCacheSize) {
        this.maxFrameBufferCacheSize = maxFrameBufferCacheSize;
    }

    @Override
    protected android.opengl.EGLSurface createEGLWindowSurface(EGLWindowSurface windowSurface) {
        Object surfaceObject = windowSurface.getSurface();
        if (windowSurface.getEGLSurface() != null) {
            return windowSurface.getEGLSurface();
        }
        List<EGLWindowSurface> usingWindowSurfaceList = windowSurfaceUsingMap.get(surfaceObject);
        if (usingWindowSurfaceList == null) {
            usingWindowSurfaceList = new ArrayList<>();
            windowSurfaceUsingMap.put(surfaceObject, usingWindowSurfaceList);
        }
        usingWindowSurfaceList.add(windowSurface);
        android.opengl.EGLSurface eglSurface = windowSurfaceCache.get(surfaceObject);
        if (eglSurface != null) {
            return eglSurface;
        }
        if (surfaceObject instanceof Surface) {
            Surface surface = (Surface) surfaceObject;
            if (!surface.isValid()) {
                throw new RuntimeException("surface is not valid");
            }
        } else if (surfaceObject instanceof SurfaceTexture) {
            SurfaceTexture surfaceTexture = (SurfaceTexture) surfaceObject;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (surfaceTexture.isReleased()) {
                    throw new RuntimeException("surface texture is released");
                }
            }
        } else if (surfaceObject instanceof SurfaceHolder) {
            Surface surface = ((SurfaceHolder) surfaceObject).getSurface();
            if (surface == null) {
                throw new IllegalStateException("surfaceHolder getSurface null");
            }
            if (!surface.isValid()) {
                throw new RuntimeException("surface is not valid");
            }
        }
        eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, surfaceObject, new int[]{EGL14.EGL_NONE, 0, EGL14.EGL_NONE}, 0);
        if (eglSurface == EGL14.EGL_NO_SURFACE || eglSurface == null) {
            throw new RuntimeException("unable create window surface");
        }
        checkEGLError();
        windowSurfaceCache.put(surfaceObject, eglSurface);
        return eglSurface;
    }

    @Override
    protected android.opengl.EGLSurface createEGLPbufferSurface(EGLPbufferSurface bufferSurface) {
        if (bufferSurface.getEGLSurface() != null) {
            return bufferSurface.getEGLSurface();
        }
        android.opengl.EGLSurface eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, eglConfig, new int[]{
                EGL14.EGL_WIDTH, bufferSurface.getWidth(),
                EGL14.EGL_HEIGHT, bufferSurface.getHeight(),
                EGL14.EGL_NONE
        }, 0);
        if (eglSurface == EGL14.EGL_NO_SURFACE || eglSurface == null) {
            throw new RuntimeException("unable  create buffer surface");
        }
        checkEGLError();
        return eglSurface;
    }

    @Override
    protected void destroyEGLSurface(EGLSurface eglSurface) {
        if (eglSurface instanceof EGLWindowSurface) {
            Object surfaceObject = ((EGLWindowSurface) eglSurface).getSurface();
            windowSurfaceUsingMap.get(surfaceObject);
            List<EGLWindowSurface> usingWindowSurfaceList = windowSurfaceUsingMap.get(surfaceObject);
            if (usingWindowSurfaceList != null) {
                usingWindowSurfaceList.remove(surfaceObject);
            }
            if (usingWindowSurfaceList == null || usingWindowSurfaceList.size() == 0) {
                EGL14.eglDestroySurface(eglDisplay, eglSurface.getEGLSurface());
                checkEGLError();
            }
        } else {
            EGL14.eglDestroySurface(eglDisplay, eglSurface.getEGLSurface());
            checkEGLError();
        }
    }


    @Override
    public void addGLErrorListener(GLErrorListener listener) {
        if (listener == null || errorListenerList.contains(listener)) {
            return;
        }
        errorListenerList.add(listener);
        if (errorListenerList.size() > 0) gl20.addGLMonitor(gl20ErrorMonitor);
    }


    @Override
    public void removeGLErrorListener(GLErrorListener listener) {
        if (listener == null || !errorListenerList.contains(listener)) {
            return;
        }
        errorListenerList.remove(listener);
        if (errorListenerList.size() <= 0) gl20.removeGLMonitor(gl20ErrorMonitor);
    }

    @Override
    public void setThrowError(boolean throwError) {
        this.throwError = throwError;
    }

    @Override
    public void setCheckGLError(boolean checkGLError) {
        this.checkGLError = checkGLError;
    }

    @Override
    public void setCheckEGLError(boolean checkEGLError) {
        this.checkEGLError = checkEGLError;
    }

    @Override
    public void setCheckThread(boolean checkThread) {
        this.checkThread = checkThread;
    }

    @Override
    public boolean isCheckThread() {
        return checkThread;
    }

    @Override
    public boolean isThrowError() {
        return throwError;
    }

    @Override
    public boolean isCheckGLError() {
        return checkGLError;
    }

    @Override
    public boolean isCheckEGLError() {
        return checkEGLError;
    }

    @Override
    public int getMaxTextureSize() {
        if (maxTextureSize > 0) {
            return maxTextureSize;
        }
        maxTextureSize = gl20.glGetInteger(GL20.GL_MAX_TEXTURE_SIZE);
        return maxTextureSize;
    }

    @Override
    protected void checkEGLError() {
        if (!checkEGLError) {
            return;
        }
        int error;
        while ((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS) {
            String message;
            switch (error) {
                case EGL14.EGL_NOT_INITIALIZED:
                    message = "EGL_NOT_INITIALIZED";
                    break;
                case EGL14.EGL_BAD_ACCESS:
                    message = "EGL_BAD_ACCESS";
                    break;
                case EGL14.EGL_BAD_ALLOC:
                    message = "EGL_BAD_ALLOC";
                    break;
                case EGL14.EGL_BAD_ATTRIBUTE:
                    message = "EGL_BAD_ATTRIBUTE";
                    break;
                case EGL14.EGL_BAD_CONFIG:
                    message = "EGL_BAD_CONFIG";
                    break;
                case EGL14.EGL_BAD_CONTEXT:
                    message = "EGL_BAD_CONTEXT";
                    break;
                case EGL14.EGL_BAD_CURRENT_SURFACE:
                    message = "EGL_BAD_CURRENT_SURFACE";
                    break;
                case EGL14.EGL_BAD_DISPLAY:
                    message = "EGL_BAD_DISPLAY";
                    break;
                case EGL14.EGL_BAD_MATCH:
                    message = "EGL_BAD_MATCH";
                    break;
                case EGL14.EGL_BAD_NATIVE_PIXMAP:
                    message = "EGL_BAD_NATIVE_PIXMAP";
                    break;
                case EGL14.EGL_BAD_NATIVE_WINDOW:
                    message = "EGL_BAD_NATIVE_WINDOW";
                    break;
                case EGL14.EGL_BAD_PARAMETER:
                    message = "EGL_BAD_PARAMETER";
                    break;
                case EGL14.EGL_BAD_SURFACE:
                    message = "EGL_BAD_SURFACE";
                    break;
                case EGL14.EGL_CONTEXT_LOST:
                    message = "EGL_CONTEXT_LOST";
                    break;
                default:
                    message = "EGL_UNKNOWN";
                    break;
            }
            RuntimeException exception = new RuntimeException(message + " 0x" + Integer.toHexString(error));
            for (GLErrorListener listener : errorListenerList) {
                listener.onError(exception);
            }
            if (throwError) {
                throw exception;
            }

        }
    }

}
