package com.jonanorman.android.renderclient.sample;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jonanorman.android.renderclient.GLDrawMode;
import com.jonanorman.android.renderclient.GLDrawType;
import com.jonanorman.android.renderclient.GLRenderClient;
import com.jonanorman.android.renderclient.GLRenderThread;
import com.jonanorman.android.renderclient.GLShaderLayer;
import com.jonanorman.android.renderclient.math.GravityMode;
import com.jonanorman.android.renderclient.math.Matrix4;

public class GLShaderActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private static final int MESSAGE_LAYER_CREATE = 1;
    private static final int MESSAGE_SURFACE_RENDER = 2;
    private static final int MESSAGE_SURFACE_MOTION_EVENT = 3;

    private GLRenderThread renderThread = new GLRenderThread(new GLRenderClient.Builder());
    private SurfaceTexture surfaceTexture = null;

    final float cubePositions[] = {
            -1.0f, 1.0f, 1.0f,    //正面左上0
            -1.0f, -1.0f, 1.0f,   //正面左下1
            1.0f, -1.0f, 1.0f,    //正面右下2
            1.0f, 1.0f, 1.0f,     //正面右上3
            -1.0f, 1.0f, -1.0f,    //反面左上4
            -1.0f, -1.0f, -1.0f,   //反面左下5
            1.0f, -1.0f, -1.0f,    //反面右下6
            1.0f, 1.0f, -1.0f,     //反面右上7
    };
    final short index[] = {
            6, 7, 4, 6, 4, 5,    //后面
            6, 3, 7, 6, 2, 3,    //右面
            6, 5, 1, 6, 1, 2,    //下面
            0, 3, 2, 0, 2, 1,    //正面
            0, 1, 5, 0, 5, 4,    //左面
            0, 7, 3, 0, 4, 7,    //上面
    };

    float color[] = {
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
    };

    private String vertexCode = "\tattribute vec3 vPosition;\n" +
            "uniform mat4 viewPortMatrix;\n" +
            "uniform mat4 positionMatrix;\n" +
            "varying  vec4 vColor;\n" +
            "attribute vec4 aColor;\n" +
            "void main() {\n" +
            "  gl_Position = viewPortMatrix*positionMatrix*vec4(vPosition,1.0);\n" +
            "  vColor=aColor;\n" +
            "}";

    private String fragmentCode = "precision mediump float;\n" +
            "varying vec4 vColor;\n" +
            "void main() {\n" +
            "  gl_FragColor =vColor;\n" +
            "}";


    private Handler.Callback callback = new Handler.Callback() {

        long startTime;
        GLShaderLayer rootLayer;

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int what = msg.what;
            switch (what) {
                case MESSAGE_LAYER_CREATE: {
                    GLRenderClient renderClient = renderThread.getRenderClient();
                    rootLayer = renderClient.newShaderLayer(vertexCode, fragmentCode);
                    rootLayer.putShaderParam("vPosition", cubePositions);
                    rootLayer.putShaderParam("aColor", color);
                    Matrix4 matrix4 = new Matrix4();
                    matrix4.rotate(30, 0, 1, 0);
                    matrix4.perspective(300, 1, 1, 10);
                    rootLayer.putShaderParam("positionMatrix", matrix4.get());
                    rootLayer.setDrawElementIndices(index);
                    rootLayer.setDrawType(GLDrawType.DRAW_ELEMENT);
                    rootLayer.setDrawMode(GLDrawMode.TRIANGLES);
                    rootLayer.setWidth(400);
                    rootLayer.setHeight(400);
                    rootLayer.setGravity(GravityMode.CENTER);
                }

                return true;
                case MESSAGE_SURFACE_MOTION_EVENT: {
                    MotionEvent motionEvent = (MotionEvent) msg.obj;
                    rootLayer.dispatchTouchEvent(motionEvent);
                    motionEvent.recycle();
                    return true;
                }
                case MESSAGE_SURFACE_RENDER:
                    if (surfaceTexture != null) {
                        long time = 0;
                        if (startTime == 0) {
                            startTime = System.currentTimeMillis();
                        } else {
                            time = System.currentTimeMillis() - startTime;
                        }
                        rootLayer.setTime(time);
                        GLRenderClient renderClient = renderThread.getRenderClient();
                        rootLayer.render(renderClient.obtainWindowSurface(surfaceTexture));
                        if (!renderThread.hasMessages(MESSAGE_SURFACE_RENDER)) {
                            renderThread.sendEmptyMessageDelayed(MESSAGE_SURFACE_RENDER, 30);
                        }
                        return true;
                    } else {
                        startTime = 0;
                    }
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.texture_view);
        TextureView textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(this);
        textureView.setOpaque(false);
        textureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Message message = Message.obtain();
                message.what = MESSAGE_SURFACE_MOTION_EVENT;
                MotionEvent motionEvent = MotionEvent.obtain(event);
                message.obj = motionEvent;
                renderThread.sendMessage(message);
                return true;
            }
        });
        renderThread.start();
        renderThread.setRenderCallback(callback);
        renderThread.sendEmptyMessage(MESSAGE_LAYER_CREATE);
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        renderThread.quitAndWait();
    }

    @Override
    protected void onPause() {
        super.onPause();
        renderThread.removeMessages(MESSAGE_SURFACE_RENDER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderThread.sendEmptyMessage(MESSAGE_SURFACE_RENDER);
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        renderThread.post(new Runnable() {
            @Override
            public void run() {
                surfaceTexture = surface;
            }
        });
        renderThread.sendEmptyMessage(MESSAGE_SURFACE_RENDER);
    }


    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }


    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }
}