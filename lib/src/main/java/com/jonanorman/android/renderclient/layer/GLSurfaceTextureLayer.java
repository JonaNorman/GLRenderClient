package com.jonanorman.android.renderclient.layer;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;

import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLShaderParam;
import com.jonanorman.android.renderclient.opengl.GLTexture;
import com.jonanorman.android.renderclient.opengl.gl20.GL20Texture;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class GLSurfaceTextureLayer extends GLTextureLayer {

    private GLTexture viewTexture;
    private SurfaceTexture surfaceTexture;
    private BlockingQueue<Boolean> blockingQueue = new LinkedBlockingQueue<>();
    private volatile boolean waitAvailable = true;
    private int surfaceWidth;
    private int surfaceHeight;

    public GLSurfaceTextureLayer(SurfaceTexture surfaceTexture) {
        super(true);
        this.surfaceTexture = surfaceTexture;
    }


    @Override
    protected void onRenderInit(GLRenderClient client) {
        super.onRenderInit(client);
        viewTexture = new GL20Texture(client, GLTexture.Type.TEXTURE_OES);
        try {
            surfaceTexture.detachFromGLContext();
        } catch (Exception e) {

        }
        surfaceTexture.attachToGLContext(viewTexture.getTextureId());
        surfaceTexture.setOnFrameAvailableListener(texture -> {
            blockingQueue.offer(true);
        }, new Handler(Looper.getMainLooper()));
        super.setTexture(viewTexture);
    }

    @Override
    protected void onRenderClean(GLRenderClient client) {
        super.onRenderClean(client);
        viewTexture.dispose();
        try {
            surfaceTexture.detachFromGLContext();
        } catch (Exception e) {

        }
        surfaceTexture.setOnFrameAvailableListener(null);
    }


    @Override
    protected boolean onRenderLayer(GLRenderClient client, GLFrameBuffer inputBuffer) {
        return super.onRenderLayer(client, inputBuffer);
    }

    @Override
    public void setTexture(GLTexture texture) {
        throw new UnsupportedOperationException(this + "can not setSurfaceTexture");
    }


    @Override
    protected void onRenderLayerParam(GLFrameBuffer inputBuffer, GLShaderParam shaderParam) {
        int width = surfaceWidth <= 0 ? inputBuffer.getWidth() : surfaceWidth;
        int height = surfaceHeight <= 0 ? inputBuffer.getHeight() : surfaceHeight;
        viewTexture.setWidth(width);
        viewTexture.setHeight(height);
        updateTexImage();
        super.onRenderLayerParam(inputBuffer, shaderParam);
    }

    private void updateTexImage() {
        long startTime = System.currentTimeMillis();
        while (true) {
            try {
                if (waitAvailable) {
                    blockingQueue.poll(60, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                surfaceTexture.updateTexImage();
                surfaceTexture.getTransformMatrix(viewTexture.getTextureMatrix().get());
                return;
            } catch (Exception e) {
                if (waitAvailable) {
                    return;
                }
            }
            if (System.currentTimeMillis() - startTime > 5000) {
                throw new IllegalStateException(this + " wait texImage timeOut 5s");
            }
        }

    }


    public void setSurfaceHeight(int surfaceHeight) {
        this.surfaceHeight = surfaceHeight;
    }

    public void setSurfaceWidth(int surfaceWidth) {
        this.surfaceWidth = surfaceWidth;
    }


    public void setWaitAvailable(boolean wait) {
        this.waitAvailable = wait;
    }


}
