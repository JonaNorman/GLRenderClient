package com.byteplay.android.renderclient;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.opengl.GLException;
import android.os.Build;
import android.util.LruCache;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.byteplay.android.renderclient.math.KeyframeSet;
import com.byteplay.android.renderclient.math.Matrix4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

class EGL14RenderClient extends GLRenderClient {

    public static final String KEY_RENDER_TIME = "renderTime";
    public static final String KEY_VIEW_PORT_SIZE = "viewPortSize";
    public static final String KEY_POSITION = "position";
    public static final String KEY_INPUT_TEXTURE_COORDINATE = "inputTextureCoordinate";
    public static final String KEY_POSITION_MATRIX = "positionMatrix";
    public static final String KEY_TEXTURE_MATRIX = "textureMatrix";
    public static final String KEY_VIEW_PORT_MATRIX_MATRIX = "viewPortMatrix";

    private static final float POSITION_COORDINATES[] = {
            -1.0f, -1.0f, 0.0f, 1.0f,//left bottom
            1.0f, -1.0f, 0.0f, 1.0f,//right bottom
            -1.0f, 1.0f, 0.0f, 1.0f, //left top
            1.0f, 1.0f, 0.0f, 1.0f//right top
    };

    private static final float TEXTURE_COORDINATES[] = {
            0.0f, 0.0f, 0.0f, 1.0f,//left bottom
            1.0f, 0.0f, 0.0f, 1.0f,//right bottom
            0.0f, 1.0f, 0.0f, 1.0f,//left top
            1.0f, 1.0f, 0.0f, 1.0f,//right  top
    };
    private static final int MAX_PROGRAM_SIZE = 20;

    private LruCache<Integer, GLProgram> programCache = new LruCache<Integer, GLProgram>(MAX_PROGRAM_SIZE) {
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

    private static final Matrix4 DEFAULT_MATRIX = new Matrix4();
    private static final int MAX_EGL_CONFIG_SIZE = 100;
    private static final int MAX_FRAME_BUFFER_CACHE_SIZE = 5;
    private final Queue<GLFrameBuffer> frameBufferCache = new LinkedList<>();
    private final List<GLObject> objectList = new ArrayList<>();
    private final Map<String, GLShader> shaderMap = new HashMap<>();
    private final Map<GLShader, Integer> shaderUsingMap = new HashMap<>();
    private final List<GLRenderSurface> renderSurfaceList = new ArrayList<>();
    private final Map<Object, GLWindowSurface> windowSurfaceCache = new HashMap<>();
    private final EGLDisplay eglDisplay;
    private final EGLContext eglContext;
    private final android.opengl.EGLConfig eglConfig;
    private final GLPbufferSurface defaultBufferSurface;
    private final GL20 gl20;
    private final GLColorLayer backgroundColorLayer;
    private final GLTextureLayer outTextureLayer;
    private final GLBlend blend;
    private GLRenderSurface currentEGLSurface;
    private GLFrameBuffer bindFrameBuffer;
    private GLEnable glEnable;
    private GLViewPort glViewPort;
    private EGLSurface attachRecordReadEGLSurface;
    private EGLSurface attachRecordDrawEGLSurface;
    private EGLContext attachRecordContext;

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
            EGLContext currentContext = EGL14.eglGetCurrentContext();
            if (!currentContext.equals(eglContext)) {
                throw new IllegalStateException("currentEGLContext is wrong");
            }
        }
    }

    @Override
    protected void checkRelease() {
        if (isRelease()) {
            throw new IllegalStateException("client is released");
        }
    }


    private boolean release;
    private int maxFrameBufferCacheSize = MAX_FRAME_BUFFER_CACHE_SIZE;
    private final List<GLErrorListener> errorListenerList = new ArrayList<>();
    private boolean throwError = true;
    private boolean checkGLError = true;
    private boolean checkThread = true;
    private boolean checkEGLError = true;
    private int maxTextureSize;
    private Thread attachThread;
    private final int[] tempInt = new int[1];


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
        this.defaultBufferSurface = newPbufferSurface(1, 1);
        this.gl20 = new GL20Command(this);
        this.bindFrameBuffer = newFrameBuffer(defaultBufferSurface);
        this.gl20.addGLMonitor(gl20ErrorMonitor);
        backgroundColorLayer = newColorLayer();
        outTextureLayer = newTextureLayer();
        blend = newBlend();
        glEnable = newEnable();
        glViewPort = newViewPort();
    }

    @Override
    public void attachCurrentThread() {
        checkRelease();
        Thread currentThread = Thread.currentThread();
        if (currentThread.equals(attachThread)) {
            return;
        }
        attachRecordReadEGLSurface = EGL14.eglGetCurrentSurface(EGL14.EGL_READ);
        attachRecordDrawEGLSurface = EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW);
        attachRecordContext = EGL14.eglGetCurrentContext();
        defaultBufferSurface.makeCurrent();
        attachThread = currentThread;
    }

    @Override
    public void detachCurrentThread() {
        if (attachThread == null) {
            return;
        }
        checkRelease();
        Thread currentThread = Thread.currentThread();
        if (!currentThread.equals(attachThread)) {
            throw new IllegalThreadStateException("detach must in attached thread");
        }
        EGL14.eglMakeCurrent(eglDisplay, attachRecordDrawEGLSurface, attachRecordReadEGLSurface, attachRecordContext);
        checkEGLError();
        currentEGLSurface = null;
        attachThread = null;
    }

    @Override
    public Thread getAttachThread() {
        return attachThread;
    }


    @Override
    protected void renderLayer(GLLayer layer, GLFrameBuffer outputBuffer) {
        if (outputBuffer == null) {
            throw new IllegalArgumentException("outputBuffer is null");
        }
        if (!layer.isRenderEnable()) {
            return;
        }
        if (!layer.isDisposed()) {
            layer.create();
        }
        int currentWidth = layer.getRenderWidth();
        int currentHeight = layer.getRenderHeight();
        long currentTimeMs = layer.getRenderTime();
        int transformSize = layer.getTransformSize();
        for (int i = 0; i < transformSize; i++) {
            GLLayer.LayerTransform transform = layer.getTransform(i);
            transform.onLayerTransform(layer, layer.getRenderTime());
        }
        if (layer.getBackgroundColor() != Color.TRANSPARENT) {
            backgroundColorLayer.setColor(layer.getBackgroundColor());
            renderLayer(backgroundColorLayer, outputBuffer, layer.getViewPortMatrix(), GLXfermode.SRC_OVER, currentTimeMs);
        }
        if (layer instanceof GLLayerGroup) {
            GLLayerGroup group = (GLLayerGroup) layer;
            GLFrameBuffer groupFrameBuffer = obtainFrameBuffer(currentWidth, currentHeight);
            String vertexCode = group.getVertexShaderCode();
            String fragmentCode = group.getFragmentShaderCode();
            if (vertexCode != null &&
                    fragmentCode != null) {
                renderLayer(group, groupFrameBuffer, DEFAULT_MATRIX, group.getSelfXfermode(), currentTimeMs);
            }
            for (int i = 0; i < group.getLayerSize(); i++) {
                GLLayer child = group.getLayer(i);
                renderLayer(child, groupFrameBuffer);
            }
            GLEffect effect = group.getEffectGroup();
            GLFrameBuffer effectBuffer = renderEffect(effect, groupFrameBuffer);
            if (groupFrameBuffer != effectBuffer) {
                cacheFrameBuffer(groupFrameBuffer);
            }
            GLTexture effectTexture = effectBuffer.getColorTexture();
            outTextureLayer.setTexture(effectTexture);
            renderLayer(outTextureLayer, outputBuffer, group.getViewPortMatrix(), group.getXfermode(), currentTimeMs);
            cacheFrameBuffer(effectBuffer);
            return;
        }
        GLEffect effect = layer.getEffectGroup();
        if (effect.isRenderEnable()) {
            GLFrameBuffer layerFrameBuffer = obtainFrameBuffer(currentWidth, currentHeight);
            renderLayer(layer, layerFrameBuffer, DEFAULT_MATRIX, GLXfermode.SRC, currentTimeMs);
            GLFrameBuffer effectBuffer = renderEffect(effect, layerFrameBuffer);
            if (layerFrameBuffer != effectBuffer) {
                cacheFrameBuffer(layerFrameBuffer);
            }
            GLTexture effectTexture = effectBuffer.getColorTexture();
            outTextureLayer.setTexture(effectTexture);
            renderLayer(outTextureLayer, outputBuffer, layer.getViewPortMatrix(), layer.getXfermode(), currentTimeMs);
            cacheFrameBuffer(effectBuffer);
            return;
        }
        renderLayer(layer, outputBuffer, layer.getViewPortMatrix(), layer.getXfermode(), currentTimeMs);
    }

    private void renderLayer(GLLayer layer, GLFrameBuffer outputBuffer, Matrix4 viewPortMatrix, GLXfermode xfermode, long renderTimeMs) {
        GLProgram program = getGlProgram(layer.getVertexShaderCode(), layer.getFragmentShaderCode());
        program.setDraw(layer.getDraw());
        GLFrameBuffer old = outputBuffer.bind();
        glViewPort.set(0, 0, outputBuffer.getWidth(), outputBuffer.getHeight());
        glViewPort.call();
        GLEnable enable = layer.getEnable();
        enable.call();
        xfermode.apply(blend);
        blend.call();
        program.clearShaderParam();
        GLShaderParam programParam = program.getShaderParam();
        programParam.put(KEY_RENDER_TIME, renderTimeMs / 1000.0f);
        programParam.put(KEY_VIEW_PORT_SIZE, glViewPort.getWidth(), glViewPort.getHeight());
        programParam.put(KEY_POSITION, POSITION_COORDINATES);
        programParam.put(KEY_INPUT_TEXTURE_COORDINATE, TEXTURE_COORDINATES);
        programParam.put(KEY_POSITION_MATRIX, DEFAULT_MATRIX.get());
        programParam.put(KEY_TEXTURE_MATRIX, DEFAULT_MATRIX.get());
        programParam.put(KEY_VIEW_PORT_MATRIX_MATRIX, viewPortMatrix.get());
        boolean render = layer.onRenderLayer(layer, renderTimeMs);
        if (!render) {
            return;
        }
        programParam.put(layer.getDefaultShaderParam());
        for (String key : layer.getKeyNames()) {
            KeyframeSet keyFrames = layer.getKeyFrames(key);
            if (keyFrames != null) {
                Object keyValue = keyFrames.getValueByTime(renderTimeMs, layer.getRenderDuration());
                if (keyValue != null) {
                    Class valueType = keyFrames.getValueType();
                    if (valueType == int.class) {
                        programParam.put(key, (int) keyValue);
                    } else if (valueType == float.class) {
                        programParam.put(key, (float) keyValue);
                    } else if (valueType == int[].class) {
                        programParam.put(key, (float) keyValue);
                    } else if (valueType == float[].class) {
                        programParam.put(key, (float[]) keyValue);
                    }
                }
            }
        }
        programParam.put(layer.getShaderParam());
        program.execute();
        GLRenderSurface eglSurface = outputBuffer.getRenderSurface();
        if (eglSurface instanceof GLWindowSurface) {
            GLWindowSurface windowSurface = (GLWindowSurface) eglSurface;
            windowSurface.setTime(renderTimeMs * 1000000L);
        }
        old.bind();
    }

    @Override
    protected GLFrameBuffer renderEffect(GLEffect effect, GLFrameBuffer input) {
        if (!effect.isRenderEnable()) {
            return input;
        }
        if (effect instanceof GLEffectGroup) {
            GLEffectGroup group = (GLEffectGroup) effect;
            GLFrameBuffer out = input;
            for (int i = 0; i < group.getEffectSize(); i++) {
                GLEffect child = group.getEffect(i);
                GLFrameBuffer effectBuffer = renderEffect(child, out);
                if (out != effectBuffer && out != input) {
                    cacheFrameBuffer(out);
                }
                out = effectBuffer;
            }
            return out;
        }
        if (effect instanceof GLShaderEffect) {
            long effectTime = effect.getRenderTime();
            GLShaderEffect shaderEffect = (GLShaderEffect) effect;
            GLFrameBuffer outputBuffer = obtainFrameBuffer(input.getWidth(), input.getHeight());
            glViewPort.set(0, 0, input.getWidth(), input.getHeight());
            GLProgram program = getGlProgram(shaderEffect.getVertexShaderCode(), shaderEffect.getFragmentShaderCode());
            program.setDraw(shaderEffect.getDraw());
            GLFrameBuffer old = outputBuffer.bind();
            glViewPort.call();
            glEnable.call();
            GLXfermode.SRC.apply(blend);
            blend.call();
            program.clearShaderParam();
            GLShaderParam programParam = program.getShaderParam();
            programParam.put(KEY_RENDER_TIME, effectTime / 1000.0f);
            programParam.put(KEY_VIEW_PORT_SIZE, glViewPort.getWidth(), glViewPort.getHeight());
            programParam.put(KEY_POSITION, POSITION_COORDINATES);
            programParam.put(KEY_INPUT_TEXTURE_COORDINATE, TEXTURE_COORDINATES);
            programParam.put(KEY_POSITION_MATRIX, DEFAULT_MATRIX.get());
            programParam.put(KEY_TEXTURE_MATRIX, DEFAULT_MATRIX.get());
            shaderEffect.onRenderShaderEffect(input);
            programParam.put(shaderEffect.getDefaultShaderParam());
            for (String key : shaderEffect.getKeyNames()) {
                KeyframeSet keyFrames = shaderEffect.getKeyframes(key);
                if (keyFrames != null) {
                    Object keyValue = keyFrames.getValueByTime(effectTime, shaderEffect.getRenderDuration());
                    if (keyValue != null) {
                        Class valueType = keyFrames.getValueType();
                        if (valueType == int.class) {
                            programParam.put(key, (int) keyValue);
                        } else if (valueType == float.class) {
                            programParam.put(key, (float) keyValue);
                        } else if (valueType == int[].class) {
                            programParam.put(key, (float) keyValue);
                        } else if (valueType == float[].class) {
                            programParam.put(key, (float[]) keyValue);
                        }
                    }
                }
            }
            programParam.put(shaderEffect.getShaderParam());
            program.execute();
            old.bind();
            return outputBuffer;
        }
        return effect.renderEffect(input);
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
            if (currentFrameBuffer.getWidth() != currentWidth || currentFrameBuffer.getHeight() != currentHeight) {
                currentFrameBuffer.setSize(currentWidth, currentHeight);
            }
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
    public GLEffectGroup newEffectSet() {
        return new GLEffectGroup(this);
    }

    @Override
    public GLShaderParam newShaderParam() {
        return new GLShaderParam();
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
    public GLFrameBuffer newFrameBuffer(GLRenderSurface eglSurface) {
        return new GL20FrameBuffer(this, eglSurface);
    }

    @Override
    public GLFrameBuffer newFrameBuffer(Surface surface) {
        return new GL20FrameBuffer(this, obtainWindowSurface(surface));
    }

    @Override
    public GLFrameBuffer newFrameBuffer(SurfaceTexture surface) {
        return new GL20FrameBuffer(this, obtainWindowSurface(surface));
    }

    @Override
    public GLFrameBuffer newFrameBuffer(SurfaceHolder surface) {
        return new GL20FrameBuffer(this, obtainWindowSurface(surface));
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
    public GLWindowSurface obtainWindowSurface(Surface surface) {
        GLWindowSurface windowSurface = windowSurfaceCache.get(surface);
        if (windowSurface != null) {
            return windowSurface;
        }
        windowSurface = new GLWindowSurface(this, surface);
        windowSurfaceCache.put(surface, windowSurface);
        return windowSurface;
    }


    @Override
    public GLWindowSurface obtainWindowSurface(SurfaceHolder surface) {
        GLWindowSurface windowSurface = windowSurfaceCache.get(surface);
        if (windowSurface != null) {
            return windowSurface;
        }
        windowSurface = new GLWindowSurface(this, surface);
        windowSurfaceCache.put(surface, windowSurface);
        return windowSurface;
    }


    @Override
    public GLWindowSurface obtainWindowSurface(SurfaceTexture surface) {
        GLWindowSurface windowSurface = windowSurfaceCache.get(surface);
        if (windowSurface != null) {
            return windowSurface;
        }
        windowSurface = new GLWindowSurface(this, surface);
        windowSurfaceCache.put(surface, windowSurface);
        return windowSurface;
    }


    @Override
    public GLPbufferSurface newPbufferSurface(int width, int height) {
        return new GLPbufferSurface(this, width, height);
    }


    @Override
    protected GLPbufferSurface getDefaultPBufferSurface() {
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
    public GLLayoutLayer newLayoutLayer(Context context) {
        return new GLLayoutLayer(this, context);
    }

    @Override
    public GLLayoutLayer newLayoutLayer(Context context, int styleRes) {
        return new GLLayoutLayer(this, context, styleRes);
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
            checkThread();
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
    protected void createEGLSurface(GLRenderSurface surface) {
        checkRelease();
        if (!surface.isCreated()) {
            renderSurfaceList.add(surface);
            surface.createSurface();
        }
    }

    @Override
    protected void disposeEGLSurface(GLRenderSurface surface) {
        if (!surface.isDisposed()) {
            renderSurfaceList.remove(surface);
            surface.disposeSurface();
        }
    }


    @Override
    protected void makeCurrent(GLRenderSurface renderSurface) {
        checkRelease();
        if (renderSurface == null) {
            currentEGLSurface = null;
            boolean success = EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            if (!success) {
                checkEGLError();
            }
        } else if (!Objects.equals(renderSurface, currentEGLSurface)) {
            if (renderSurface instanceof GLWindowSurface) {
                GLWindowSurface windowSurface = (GLWindowSurface) renderSurface;
                checkSurfaceObject(windowSurface.getSurface());
            }
            boolean success = EGL14.eglMakeCurrent(eglDisplay, renderSurface.getEGLSurface(), renderSurface.getEGLSurface(), eglContext);
            if (!success) {
                checkEGLError();
            }
            currentEGLSurface = renderSurface;
        }
    }

    @Override
    protected void swapBuffers(GLRenderSurface surface) {
        if (!surface.isDisposed()) {
            surface.create();
        }
        surface.makeCurrent();
        if (surface instanceof GLWindowSurface) {
            GLWindowSurface windowSurface = (GLWindowSurface) surface;
            if (!EGLExt.eglPresentationTimeANDROID(eglDisplay, surface.getEGLSurface(), windowSurface.getTime())) {
                checkEGLError();
            }
            if (!EGL14.eglSwapBuffers(eglDisplay, surface.getEGLSurface())) {
                checkEGLError();
            }
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
        attachCurrentThread();
        List<GLObject> disposeObjectList = new ArrayList<>();
        disposeObjectList.addAll(objectList);
        for (GLObject object : disposeObjectList) {
            object.dispose();
        }
        List<GLRenderSurface> disposeSurfaceList = new ArrayList<>();
        disposeSurfaceList.addAll(renderSurfaceList);
        for (GLRenderSurface eglSurface : disposeSurfaceList) {
            eglSurface.dispose();
        }
        objectList.clear();
        renderSurfaceList.clear();
        windowSurfaceCache.clear();
        shaderUsingMap.clear();
        shaderMap.clear();
        detachCurrentThread();
        EGL14.eglDestroyContext(eglDisplay, eglContext);
        checkEGLError();
        EGL14.eglReleaseThread();
        checkEGLError();
        EGL14.eglTerminate(eglDisplay);
        checkEGLError();
        release = true;
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
    protected android.opengl.EGLSurface create(GLWindowSurface windowSurface) {
        if (windowSurface.getEGLSurface() != null) {
            return windowSurface.getEGLSurface();
        }
        Object surfaceObject = windowSurface.getSurface();
        checkSurfaceObject(surfaceObject);
        EGLSurface eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, surfaceObject, new int[]{EGL14.EGL_NONE, 0, EGL14.EGL_NONE}, 0);
        if (eglSurface == EGL14.EGL_NO_SURFACE || eglSurface == null) {
            throw new RuntimeException("unable create window surface");
        }
        checkEGLError();
        return eglSurface;
    }

    private void checkSurfaceObject(Object surfaceObject) {
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
    }

    @Override
    protected android.opengl.EGLSurface create(GLPbufferSurface bufferSurface) {
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
    protected void destroy(GLWindowSurface windowSurface) {
        EGL14.eglDestroySurface(eglDisplay, windowSurface.getEGLSurface());
        checkEGLError();
        windowSurfaceCache.remove(windowSurface.getSurface());
    }

    @Override
    protected void destroy(GLPbufferSurface pbufferSurface) {
        EGL14.eglDestroySurface(eglDisplay, pbufferSurface.getEGLSurface());
        checkEGLError();
    }

    @Override
    protected int queryWidth(GLWindowSurface windowSurface) {
        if (windowSurface.isDisposed()) {
            return 0;
        }
        windowSurface.create();
        EGL14.eglQuerySurface(eglDisplay, windowSurface.getEGLSurface(), EGL14.EGL_WIDTH, tempInt, 0);
        return tempInt[0];
    }

    @Override
    protected int queryHeight(GLWindowSurface windowSurface) {
        if (windowSurface.isDisposed()) {
            return 0;
        }
        windowSurface.create();
        EGL14.eglQuerySurface(eglDisplay, windowSurface.getEGLSurface(), EGL14.EGL_HEIGHT, tempInt, 0);
        return tempInt[0];
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
