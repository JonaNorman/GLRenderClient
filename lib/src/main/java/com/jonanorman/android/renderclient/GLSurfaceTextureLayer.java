package com.jonanorman.android.renderclient;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.HandlerThread;

import com.jonanorman.android.renderclient.math.Matrix4;
import com.jonanorman.android.renderclient.math.ScaleMode;

public class GLSurfaceTextureLayer extends GLShaderLayer {

    private static final String VERTEX_SHADER = "precision highp float;\n" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "uniform mat4 inputTextureMatrix;\n" +
            "uniform mat4 viewPortMatrix;\n" +
            "uniform mat4 scaleTypeMatrix;\n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = scaleTypeMatrix*viewPortMatrix*position;\n" +
            "    textureCoordinate =(inputTextureMatrix*inputTextureCoordinate).xy;\n" +
            "}";


    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "uniform samplerExternalOES inputImageTexture;\n" +
            "uniform vec2 inputTextureSize;\n" +
            "uniform vec2 viewPortSize;\n" +
            "uniform float renderTime;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    vec4 color  = texture2D(inputImageTexture, textureCoordinate);\n" +
            "    gl_FragColor = color;\n" +
            "}";

    private static Handler FRAME_AVAILABLE_HANDLER;
    private static int FRAME_AVAILABLE_COUNT;

    private final Matrix4 textureMatrix = new Matrix4();

    private Handler surfaceTextureHandler;
    private GLTexture viewTexture;
    private SurfaceTexture surfaceTexture;
    private Matrix4 scaleTypeMatrix = new Matrix4();

    private Object object = new Object();
    private volatile boolean done;
    private volatile boolean waitTexImage;
    private ScaleMode textureScaleMode = ScaleMode.FIT;
    private int surfaceWidth;
    private int surfaceHeight;
    private boolean textureAttached;


    protected GLSurfaceTextureLayer(GLRenderClient client) {
        super(client, VERTEX_SHADER, FRAGMENT_SHADER);
    }


    @Override
    protected void onCreate() {
        viewTexture = client.newTexture(GLTextureType.TEXTURE_OES);

    }

    private Handler getSurfaceTextureHandler() {
        synchronized (GLLayoutLayer.class) {
            if (surfaceTextureHandler == null) {
                if (FRAME_AVAILABLE_HANDLER == null) {
                    HandlerThread handlerThread = new HandlerThread("SurfaceTextureFrameAvailableThread");
                    handlerThread.start();
                    FRAME_AVAILABLE_HANDLER = new Handler(handlerThread.getLooper());
                }
                FRAME_AVAILABLE_COUNT++;
                surfaceTextureHandler = FRAME_AVAILABLE_HANDLER;
            }
        }
        return surfaceTextureHandler;
    }

    public void setSurfaceTexture(SurfaceTexture newSurface) {
        if (isDisposed()) {
            return;
        }
        create();
        if (this.surfaceTexture != newSurface) {
            if (this.surfaceTexture != null) {
                this.surfaceTexture.setOnFrameAvailableListener(null);
                try {
                    surfaceTexture.detachFromGLContext();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.surfaceTexture = newSurface;
            textureAttached = false;
        }

    }

    public void setTextureScaleMode(ScaleMode textureScaleMode) {
        this.textureScaleMode = textureScaleMode;
    }

    public ScaleMode getTextureScaleMode() {
        return textureScaleMode;
    }

    public void setSurfaceHeight(int surfaceHeight) {
        this.surfaceHeight = surfaceHeight;
    }

    public void setSurfaceWidth(int surfaceWidth) {
        this.surfaceWidth = surfaceWidth;
    }

    @Override
    protected void onLayerRenderSize(float renderWidth, float renderHeight, float parentWidth, float parentHeight) {
        super.onLayerRenderSize(renderWidth, renderHeight, parentWidth, parentHeight);
        scaleTypeMatrix.setIdentity();
        if (surfaceWidth != 0 && surfaceHeight != 0) {
            float viewportWidth = textureScaleMode.getWidth(surfaceWidth, surfaceHeight, renderWidth, renderHeight);
            float viewportHeight = textureScaleMode.getHeight(surfaceWidth, surfaceHeight, renderWidth, renderHeight);
            scaleTypeMatrix.scale(viewportWidth / renderWidth, viewportHeight / renderHeight, 1.0f);
        }
    }


    public void setWaitTexImage(boolean waitTexImage) {
        this.waitTexImage = waitTexImage;
    }

    @Override
    protected void onDispose() {
        if (surfaceTexture != null) {
            surfaceTexture.detachFromGLContext();
            surfaceTexture.setOnFrameAvailableListener(null);
            surfaceTexture = null;
        }
        releaseSurfaceTextureHandler();
        viewTexture.dispose();
    }

    private void releaseSurfaceTextureHandler() {
        synchronized (GLLayoutLayer.class) {
            if (surfaceTextureHandler != null) {
                surfaceTextureHandler.removeCallbacksAndMessages(null);
                FRAME_AVAILABLE_COUNT--;
                if (FRAME_AVAILABLE_COUNT <= 0) {
                    if (FRAME_AVAILABLE_HANDLER != null) {
                        FRAME_AVAILABLE_HANDLER.getLooper().quit();
                        FRAME_AVAILABLE_HANDLER = null;
                    }
                }
                surfaceTextureHandler = null;
            }
        }
    }


    @Override
    protected boolean onShaderLayerRender(long renderTimeMs) {
        super.onShaderLayerRender(renderTimeMs);
        synchronized (object) {
            while (!done && waitTexImage) {
                try {
                    object.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        try {
            if (!textureAttached) {
                try {
                    surfaceTexture.detachFromGLContext();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                surfaceTexture.attachToGLContext(viewTexture.getTextureId());
                surfaceTexture.setOnFrameAvailableListener(surfaceTexture -> {
                    synchronized (object) {
                        done = true;
                        object.notify();
                    }
                }, getSurfaceTextureHandler());
                textureAttached = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            surfaceTexture.updateTexImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int textureWidth = viewTexture.getWidth();
        int textureHeight = viewTexture.getHeight();
        GLShaderParam shaderParam = getDefaultShaderParam();
        shaderParam.put("inputImageTexture", viewTexture.getTextureId());
        shaderParam.put("inputTextureSize", textureWidth, textureHeight);
        float viewWidth = getRenderWidth();
        float viewHeight = getRenderHeight();
        viewTexture.setWidth((int) viewWidth);
        viewTexture.setHeight((int) viewHeight);
        surfaceTexture.getTransformMatrix(textureMatrix.get());
        shaderParam.put("scaleTypeMatrix", scaleTypeMatrix.get());
        shaderParam.put("inputTextureMatrix", textureMatrix.get());
        return true;
    }
}
