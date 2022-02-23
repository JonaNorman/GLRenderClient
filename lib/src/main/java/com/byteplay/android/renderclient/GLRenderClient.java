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

    public abstract GLFrameBuffer newFrameBuffer(GLRenderSurface eglSurface);

    public abstract GLFrameBuffer newFrameBuffer(Surface surface);

    public abstract GLFrameBuffer newFrameBuffer(SurfaceTexture surface);

    public abstract GLFrameBuffer newFrameBuffer(SurfaceHolder surface);

    public abstract GLFrameBuffer newFrameBuffer(GLTexture texture);

    public abstract GLFrameBuffer getBindFrameBuffer();

    public abstract GLTexture newTexture(GLTextureType textureType);

    public abstract GLTexture newTexture(GLTextureType textureType, int textureId);

    public abstract GLViewPort newViewPort();

    public abstract GLWindowSurface obtainWindowSurface(Surface surface);

    public abstract GLWindowSurface obtainWindowSurface(SurfaceHolder surface);

    public abstract GLWindowSurface obtainWindowSurface(SurfaceTexture surface);

    public abstract GLPbufferSurface newPbufferSurface(int width, int height);


    protected abstract GLPbufferSurface getDefaultPBufferSurface();

    protected abstract void makeCurrent(GLRenderSurface surface);

    protected abstract void swapBuffers(GLRenderSurface surface);

    protected abstract GLFrameBuffer onBindFrameBuffer(GLFrameBuffer newFrameBuffer);

    public abstract void release();

    public abstract boolean isRelease();


    public abstract GLTextureLayer newTextureLayer();


    public abstract GLLayer newLayer(String vertexCode, String fragmentCode, GLDraw draw);

    public abstract GLColorLayer newColorLayer();

    public abstract GLLayerGroup newLayerGroup();

    public abstract GLLayoutLayer newLayoutLayer(Context context, int styleRes);

    public abstract GLLayoutLayer newLayoutLayer(Context context);


    public abstract void setMaxProgramSize(int maxProgramSize);


    public abstract void attachCurrentThread();

    public abstract void detachCurrentThread();

    public abstract Thread getAttachThread();

    protected abstract void render(GLLayer layer, GLFrameBuffer frameBuffer, long renderTimeMs);

    protected abstract void render(GLLayerGroup layer, GLFrameBuffer frameBuffer, long renderTimeMs);

    protected abstract GLFrameBuffer applyEffect(GLEffect effect, GLFrameBuffer input, long timeMs);

    protected abstract GLFrameBuffer applyEffect(GLShaderEffect effect, GLFrameBuffer input, long timeMs);

    protected abstract GLFrameBuffer applyEffect(GLEffectSet effect, GLFrameBuffer input, long timeMs);

    protected abstract void createObject(GLObject object);

    protected abstract void disposeObject(GLObject object);

    protected abstract void createEGLSurface(GLRenderSurface surface);

    protected abstract void disposeEGLSurface(GLRenderSurface surface);

    protected abstract void checkThread();

    protected abstract void checkRelease();

    public abstract EGLContext getEGLContext();

    protected abstract android.opengl.EGLConfig getEGLConfig();

    protected abstract EGLDisplay getEGLDisplay();

    protected abstract android.opengl.EGLSurface create(GLWindowSurface windowSurface);

    protected abstract android.opengl.EGLSurface create(GLPbufferSurface eglPbufferSurface);


    protected abstract void destroy(GLWindowSurface windowSurface);

    protected abstract void destroy(GLPbufferSurface pbufferSurface);

    protected abstract int queryWidth(GLWindowSurface windowSurface);

    protected abstract int queryHeight(GLWindowSurface windowSurface);

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

        public void setEGLConfigChooser(EGLConfigChooser configChooser) {
            this.configChooser = configChooser;
        }

        public GLRenderClient build() {
            EGLConfigChooser chooser = configChooser;
            if (chooser == null) {
                chooser = new EGLConfigSimpleChooser.Builder().build();
            }
            return new EGL14RenderClient(context,
                    chooser);
        }
    }
}
