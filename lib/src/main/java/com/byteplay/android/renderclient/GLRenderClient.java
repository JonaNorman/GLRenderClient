package com.byteplay.android.renderclient;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.view.Surface;
import android.view.SurfaceHolder;

public abstract class GLRenderClient {


    public abstract GLBlend newBlend();

    public abstract GL20 getGL20();

    public abstract GLShader newVertexShader();

    public abstract GLShader newFragmentShader();

    public abstract GLProgram newProgram();

    public abstract GLShaderEffect newShaderEffect();

    public abstract GLEffectSet newEffectSet();

    public abstract GLShaderParam newShaderParam();

    public abstract GLDrawArray newDrawArray();


    public abstract GLDrawElement newDrawElement();


    public abstract GLEnable newEnable();


    public abstract GLFrameBuffer newFrameBuffer(int width, int height);

    public abstract GLFrameBuffer newFrameBuffer(EGLSurface eglSurface);

    public abstract GLFrameBuffer newFrameBuffer(Surface surface);

    public abstract GLFrameBuffer newFrameBuffer(SurfaceTexture surface);

    public abstract GLFrameBuffer newFrameBuffer(SurfaceHolder surface);

    public abstract GLFrameBuffer newFrameBuffer(GLTexture texture);

    public abstract GLFrameBuffer getBindFrameBuffer();

    public abstract GLTexture newTexture(GLTextureType textureType);

    public abstract GLTexture newTexture(GLTextureType textureType, int textureId);

    public abstract GLViewPort newViewPort();

    public abstract EGLWindowSurface newWindowSurface(Surface surface);

    public abstract EGLWindowSurface newWindowSurface(SurfaceHolder surface);

    public abstract EGLWindowSurface newWindowSurface(SurfaceTexture surface);

    public abstract EGLPbufferSurface newBufferSurface(int width, int height);


    protected abstract EGLPbufferSurface getDefaultEGLBufferSurface();

    protected abstract void makeCurrentEGLSurface(EGLSurface surface);

    protected abstract GLFrameBuffer onBindFrameBuffer(GLFrameBuffer newFrameBuffer);

    public abstract void release();

    public abstract boolean isRelease();


    public abstract GLTextureLayer newTextureLayer();


    public abstract GLLayer newLayer(String vertexCode, String fragmentCode, GLDraw draw);

    public abstract GLColorLayer newColorLayer();

    public abstract GLLayerSet newLayerSet();

    public abstract GLFrameLayoutLayer newFrameLayoutLayer(Context context, int styleRes);

    public abstract GLFrameLayoutLayer newFrameLayoutLayer(Context context);


    public abstract void setMaxProgramSize(int maxProgramSize);


    public abstract void attachCurrentThread();

    public abstract void detachThread();

    public abstract Thread getAttachThread();

    protected abstract void render(GLLayer layer, GLFrameBuffer frameBuffer, long renderTimeMs);

    protected abstract void render(GLLayerSet layer, GLFrameBuffer frameBuffer, long renderTimeMs);

    protected abstract GLFrameBuffer applyEffect(GLEffect effect, GLFrameBuffer input, long timeMs);

    protected abstract GLFrameBuffer applyEffect(GLShaderEffect effect, GLFrameBuffer input, long timeMs);

    protected abstract GLFrameBuffer applyEffect(GLEffectSet effect, GLFrameBuffer input, long timeMs);

    protected abstract void createObject(GLObject object);

    protected abstract void disposeObject(GLObject object);

    protected abstract void createEGLSurface(EGLSurface surface);

    protected abstract void disposeEGLSurface(EGLSurface surface);

    protected abstract void checkThread();

    protected abstract void checkRelease();

    public abstract EGLContext getEGLContext();

    protected abstract android.opengl.EGLConfig getEGLConfig();

    protected abstract EGLDisplay getEGLDisplay();

    protected abstract android.opengl.EGLSurface createEGLWindowSurface(EGLWindowSurface windowSurface);

    protected abstract android.opengl.EGLSurface createEGLPbufferSurface(EGLPbufferSurface eglPbufferSurface);

    protected abstract void destroyEGLSurface(EGLSurface eglSurface);

    public abstract void addGLErrorListener(GLErrorListener listener);

    public abstract void removeGLErrorListener(GLErrorListener listener);

    public abstract void setThrowError(boolean throwError);

    public abstract void setCheckGLError(boolean checkGLError);

    public abstract void setCheckEGLError(boolean checkEGLError);

    public abstract void setCheckThread(boolean checkThread);

    public abstract boolean isCheckThread();

    public abstract boolean isThrowError();

    public abstract boolean isCheckGLError();

    public abstract boolean isCheckEGLError();

    public abstract int getMaxTextureSize();

    protected abstract void checkEGLError();


    public static class Builder {
        private EGLContext context;
        private EGLConfigChooser configChooser;


        public Builder() {
        }

        public void setContext(EGLContext context) {
            this.context = context;
        }

        public void setConfigChooser(EGLConfigChooser configChooser) {
            this.configChooser = configChooser;
        }

        public GLRenderClient build() {
            EGLConfigChooser chooser = configChooser;
            if (chooser == null) {
                chooser = new EGLComponentChooser.Builder().build();
            }
            return new EGL14RenderClient(context,
                    chooser);
        }
    }
}
