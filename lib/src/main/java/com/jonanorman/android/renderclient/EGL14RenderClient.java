package com.jonanorman.android.renderclient;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLException;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.jonanorman.android.renderclient.math.Matrix4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class EGL14RenderClient extends GLRenderClient {


    private static final int MAX_EGL_CONFIG_SIZE = 100;

    private final Map<Object, GLWindowSurface> windowSurfaceCache = new HashMap<>();
    private final List<GLErrorListener> errorListenerList = new ArrayList<>();
    private final List<GLRenderClientReleaseListener> clientReleaseListenerList = new ArrayList<>();
    private final EGLDisplay eglDisplay;
    private final EGLContext eglContext;
    private final android.opengl.EGLConfig eglConfig;
    private final GLPbufferSurface defaultBufferSurface;
    private final GL20 gl20;
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

    private EGLSurface attachRecordReadEGLSurface;
    private EGLSurface attachRecordDrawEGLSurface;
    private EGLContext attachRecordContext;
    private GLFrameBuffer currentFrameBuffer;
    private GLRenderSurface currentRenderSurface;
    private GLProgramCache programCache;
    private GLFrameBufferCache frameBufferCache;

    private boolean release;
    private boolean throwError = true;
    private boolean checkGLError = true;
    private boolean checkThread = true;
    private boolean checkEGLError = true;
    private int maxTextureSize;
    private Thread attachThread;
    private GLColorLayer colorDrawer;
    private GLTextureLayer textureDrawer;


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
        this.gl20.addGLMonitor(gl20ErrorMonitor);
        currentFrameBuffer = newFrameBuffer(defaultBufferSurface);
        programCache = new GLProgramCache(this);
        frameBufferCache = new GLFrameBufferCache(this);
        colorDrawer = newColorLayer();
        textureDrawer = newTextureLayer();
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
        if (currentRenderSurface != null) {
            currentRenderSurface.makeNoCurrent();
        }
        EGL14.eglMakeCurrent(eglDisplay, attachRecordDrawEGLSurface, attachRecordReadEGLSurface, attachRecordContext);
        checkEGLError();
        attachThread = null;
    }


    @Override
    public Thread getAttachThread() {
        return attachThread;
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
        return obtainGLWindowSurface(surface);
    }


    @Override
    public GLWindowSurface obtainWindowSurface(SurfaceHolder surface) {
        return obtainGLWindowSurface(surface);
    }


    @Override
    public GLWindowSurface obtainWindowSurface(SurfaceTexture surface) {
        return obtainGLWindowSurface(surface);
    }

    private GLWindowSurface obtainGLWindowSurface(Object object) {
        GLWindowSurface windowSurface = windowSurfaceCache.get(object);
        if (windowSurface != null) {
            return windowSurface;
        }
        windowSurface = new EGL14WindowSurface(this, object);
        windowSurfaceCache.put(object, windowSurface);
        return windowSurface;
    }


    @Override
    public GLPbufferSurface newPbufferSurface(int width, int height) {
        return new EGL14PbufferSurface(this, width, height);
    }


    @Override
    public GLTextureLayer newTextureLayer() {
        return new GLTextureLayer(this);
    }


    @Override
    public GLBitmapLayer newBitmapLayer() {
        return new GLBitmapLayer(this);
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
    public GLSurfaceTextureLayer newSurfaceTextureLayer() {
        return new GLSurfaceTextureLayer(this);
    }

    @Override
    public GLLayoutLayer newLayoutLayer(Context context, int styleRes) {
        return new GLLayoutLayer(this, context, styleRes);
    }

    @Override
    public GLShaderLayer newShaderLayer(String vertexCode, String fragmentCode) {
        return new GLShaderLayer(this, vertexCode, fragmentCode);
    }

    @Override
    public GLColorLayer newColorLayer() {
        return new GLColorLayer(this);
    }


    @Override
    public void release() {
        if (release) {
            return;
        }
        attachCurrentThread();
        List<GLRenderClientReleaseListener> releaseListenerList = new ArrayList<>();
        releaseListenerList.addAll(clientReleaseListenerList);
        for (GLRenderClientReleaseListener releaseListener : releaseListenerList) {
            releaseListener.onClientRelease(this);
        }
        clientReleaseListenerList.clear();
        windowSurfaceCache.clear();
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
    public void addClientReleaseListener(GLRenderClientReleaseListener releaseListener) {
        if (releaseListener == null || clientReleaseListenerList.contains(releaseListener)) {
            return;
        }
        clientReleaseListenerList.add(releaseListener);
    }

    @Override
    public void removeClientReleaseListener(GLRenderClientReleaseListener releaseListener) {
        if (releaseListener == null || !clientReleaseListenerList.contains(releaseListener)) {
            return;
        }
        clientReleaseListenerList.remove(releaseListener);
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
    public int getMaxTextureSize() {
        if (maxTextureSize > 0) {
            return maxTextureSize;
        }
        maxTextureSize = gl20.glGetInteger(GL20.GL_MAX_TEXTURE_SIZE);
        return maxTextureSize;
    }

    @Override
    public GLFrameBuffer getCurrentFrameBuffer() {
        return currentFrameBuffer;
    }

    @Override
    protected void setCurrentFrameBuffer(GLFrameBuffer currentFrameBuffer) {
        this.currentFrameBuffer = currentFrameBuffer;
    }

    @Override
    public GLRenderSurface getCurrentRenderSurface() {
        return currentRenderSurface;
    }

    @Override
    protected void setCurrentRenderSurface(GLRenderSurface currentRenderSurface) {
        this.currentRenderSurface = currentRenderSurface;
    }

    @Override
    protected void removeWindowSurface(GLWindowSurface windowSurface) {
        windowSurfaceCache.remove(windowSurface.getSurface());
    }

    @Override
    protected GLPbufferSurface getDefaultPBufferSurface() {
        return defaultBufferSurface;
    }

    @Override
    public GLProgramCache getProgramCache() {
        return programCache;
    }


    @Override
    public GLFrameBufferCache getFrameBufferCache() {
        return frameBufferCache;
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

    @Override
    void drawColor(GLFrameBuffer outBuffer, Matrix4 viewPortMatrix, int backgroundColor) {
        if (backgroundColor != Color.TRANSPARENT) {
            colorDrawer.setColor(backgroundColor);
            colorDrawer.drawLayer(outBuffer, viewPortMatrix, GLXfermode.SRC_OVER, 0);
        }
    }

    @Override
    void drawTexture(GLFrameBuffer outBuffer, Matrix4 viewPortMatrix, GLXfermode xfermode, GLTexture texture) {
        textureDrawer.setTexture(texture);
        textureDrawer.drawLayer(outBuffer, viewPortMatrix, xfermode, 0);
    }

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
}
