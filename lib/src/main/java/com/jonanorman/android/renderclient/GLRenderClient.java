package com.jonanorman.android.renderclient;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.jonanorman.android.renderclient.math.Matrix4;

public abstract class GLRenderClient {


    public abstract GLBlend newBlend();

    public abstract GL20 getGL20();

    public abstract GLShader newVertexShader();

    public abstract GLShader newFragmentShader();

    public abstract GLProgram newProgram();

    public abstract GLShaderEffect newShaderEffect();

    public abstract GLEffectGroup newEffectSet();

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


    public abstract GLTexture newTexture(GLTextureType textureType);

    public abstract GLDepthBuffer newDepthBuffer();

    public abstract GLTexture newTexture(GLTextureType textureType, int textureId);

    public abstract GLViewPort newViewPort();

    public abstract GLWindowSurface obtainWindowSurface(Surface surface);

    public abstract GLWindowSurface obtainWindowSurface(SurfaceHolder surface);

    public abstract GLWindowSurface obtainWindowSurface(SurfaceTexture surface);

    public abstract GLPbufferSurface newPbufferSurface(int width, int height);


    protected abstract GLPbufferSurface getDefaultPBufferSurface();

    public abstract void release();

    public abstract boolean isRelease();


    public abstract GLTextureLayer newTextureLayer();


    public abstract GLShaderLayer newShaderLayer(String vertexCode, String fragmentCode);

    public abstract GLColorLayer newColorLayer();

    public abstract GLBitmapLayer newBitmapLayer();

    public abstract GLLayerGroup newLayerGroup();

    public abstract GLFrameBufferCache getFrameBufferCache();

    public abstract GLViewLayer newLayoutLayer(Context context, int styleRes);

    public abstract GLViewLayer newLayoutLayer(Context context);

    public abstract GLSurfaceTextureLayer newSurfaceTextureLayer();

    public abstract GLProgramCache getProgramCache();

    public abstract void attachCurrentThread();

    public abstract void detachCurrentThread();

    public abstract Thread getAttachThread();


    abstract void drawColor(GLFrameBuffer outBuffer, Matrix4 viewPortMatrix, int backgroundColor);

    abstract void drawTexture(GLFrameBuffer outBuffer, Matrix4 viewPortMatrix, GLXfermode xfermode, GLTexture texture);

    protected abstract void checkThread();

    protected abstract void checkRelease();

    public abstract EGLContext getEGLContext();

    public abstract EGLConfig getEGLChooseConfig();

    protected abstract android.opengl.EGLConfig getEGLConfig();

    protected abstract EGLDisplay getEGLDisplay();


    public abstract void addGLErrorListener(GLErrorListener listener);

    public abstract void removeGLErrorListener(GLErrorListener listener);

    public abstract void addClientReleaseListener(GLRenderClientReleaseListener releaseListener);

    public abstract void removeClientReleaseListener(GLRenderClientReleaseListener releaseListener);

    public abstract void setThrowError(boolean throwError);

    public abstract void setCheckGLError(boolean checkGLError);

    public abstract void setCheckEGLError(boolean checkEGLError);

    public abstract void setCheckThread(boolean checkThread);

    public abstract int getMaxTextureSize();

    protected abstract void checkEGLError();

    public abstract GLFrameBuffer getCurrentFrameBuffer();

    protected abstract void setCurrentFrameBuffer(GLFrameBuffer currentFrameBuffer);

    public abstract GLRenderSurface getCurrentRenderSurface();

    protected abstract void setCurrentRenderSurface(GLRenderSurface currentRenderSurface);

    protected abstract void removeWindowSurface(GLWindowSurface windowSurface);


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
