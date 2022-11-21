package com.jonanorman.android.renderclient.layer;

import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.jonanorman.android.renderclient.math.TimeStamp;
import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLShaderParam;
import com.jonanorman.android.renderclient.opengl.GLTexture;
import com.jonanorman.android.renderclient.opengl.gl20.GL20Texture;

public class GLCanvasLayer extends GLTextureLayer {



    private GLTexture viewTexture;
    private SurfaceTexture surfaceTexture;
    private Surface surface;
    private CanvasProvider canvasProvider;
    private int canvasWidth;
    private int canvasHeight;


    public GLCanvasLayer(CanvasProvider canvasProvider) {
        super(true);
        this.canvasProvider = canvasProvider;
    }


    @Override
    protected void onRenderInit(GLRenderClient client) {
        super.onRenderInit(client);
        viewTexture = new GL20Texture(client, GLTexture.Type.TEXTURE_OES);
        surfaceTexture = new SurfaceTexture(viewTexture.getTextureId());
        surface = new Surface(surfaceTexture);
        super.setTexture(viewTexture);
    }

    @Override
    protected void onRenderClean(GLRenderClient client) {
        super.onRenderClean(client);
        viewTexture.dispose();
        surface.release();
        surfaceTexture.release();
    }


    @Override
    public void setTexture(GLTexture texture) {
        throw new UnsupportedOperationException(this + "can not setSurfaceTexture");
    }

    public void setCanvasProvider(@NonNull CanvasProvider canvasProvider) {
        this.canvasProvider = canvasProvider;
    }

    @Override
    protected void onRenderLayerParam(GLFrameBuffer inputBuffer, GLShaderParam shaderParam) {
        int renderWidth = canvasWidth <= 0 ? Math.round(getRenderWidth()) : canvasWidth;
        int renderHeight = canvasHeight <= 0 ? Math.round(getRenderHeight()) : canvasHeight;
        drawSurface(renderWidth, renderHeight);
        viewTexture.setWidth(renderWidth);
        viewTexture.setHeight(renderHeight);
        super.onRenderLayerParam(inputBuffer, shaderParam);

    }

    private void drawSurface(int renderWidth, int renderHeight) {
        surfaceTexture.setDefaultBufferSize(renderWidth, renderHeight);
        Canvas canvas = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                canvas = surface.lockHardwareCanvas();
            } else {
                canvas = surface.lockCanvas(null);
            }
            canvasProvider.onDraw(canvas, getRenderTime());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                surface.unlockCanvasAndPost(canvas);
            }
        }
        surfaceTexture.updateTexImage();
        surfaceTexture.getTransformMatrix(viewTexture.getTextureMatrix().get());
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public interface CanvasProvider {
        void onDraw(Canvas canvas, TimeStamp timeStamp);
    }

}
