package com.jonanorman.android.renderclient.sample;

import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jonanorman.android.renderclient.GLBitmapLayer;
import com.jonanorman.android.renderclient.GLColorLayer;
import com.jonanorman.android.renderclient.GLLayer;
import com.jonanorman.android.renderclient.GLLayerGroup;
import com.jonanorman.android.renderclient.GLLayoutLayer;
import com.jonanorman.android.renderclient.GLRenderClient;
import com.jonanorman.android.renderclient.GLRenderSurface;
import com.jonanorman.android.renderclient.math.GravityMode;
import com.jonanorman.android.renderclient.math.KeyframeSet;

import java.util.concurrent.atomic.AtomicBoolean;

public class GLLayerActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private static final int MESSAGE_CLIENT_CREATE = 1;
    private static final int MESSAGE_CLIENT_RELEASE = 2;
    private static final int MESSAGE_SURFACE_CREATE = 3;
    private static final int MESSAGE_SURFACE_DISPOSE = 4;
    private static final int MESSAGE_SURFACE_RENDER = 5;
    private static final int MESSAGE_SURFACE_MOTION_EVENT = 6;

    private static final String LOG_TAG = "GLLayerActivity";

    private Handler handler;

    private AtomicBoolean surfaceDestroy = new AtomicBoolean();


    private Handler.Callback callback = new Handler.Callback() {
        GLRenderSurface eglSurface;
        GLRenderClient renderClient;
        long startTime = System.currentTimeMillis();
        GLLayerGroup rootLayer;


        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int what = msg.what;
            switch (what) {
                case MESSAGE_CLIENT_CREATE:
                    renderClient = new GLRenderClient.Builder().build();
                    renderClient.attachCurrentThread();
                    rootLayer = renderClient.newLayerGroup();
                    rootLayer.setDuration(60000);
                    GLColorLayer colorLayer = renderClient.newColorLayer();
                    rootLayer.addLayer(colorLayer);
                    KeyframeSet colorFrameSet = GLColorLayer.ofColor(Color.RED, Color.YELLOW, Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.RED);
                    colorFrameSet.setTypeEvaluator(new GLColorLayer.ColorEvaluator());
                    colorLayer.setKeyframes(GLColorLayer.KEY_COLOR, colorFrameSet);
                    colorLayer.setOnClickListener(new GLLayer.OnClickListener() {
                        @Override
                        public void onClick(GLLayer layer) {
                            Log.v(LOG_TAG, "colorLayer click");
                        }
                    });

                    GLBitmapLayer bitmapLayer = renderClient.newBitmapLayer();
                    bitmapLayer.setBitmapResId(getApplicationContext(), R.drawable.alpha);
                    bitmapLayer.setDuration(5000);
                    bitmapLayer.setWidth(400);
                    bitmapLayer.setHeight(400);
                    bitmapLayer.setOnClickListener(new GLLayer.OnClickListener() {
                        @Override
                        public void onClick(GLLayer layer) {
                            Log.v(LOG_TAG, "bitmapLayer click");
                        }
                    });
                    rootLayer.addLayer(bitmapLayer);


                    GLLayoutLayer layoutLayer = renderClient.newLayoutLayer(getApplicationContext());
                    layoutLayer.setBackgroundColor(Color.RED);
                    rootLayer.addLayer(layoutLayer);
                    layoutLayer.setStartTime(4000);
                    layoutLayer.setX(300);
                    layoutLayer.setWidth(600);
                    layoutLayer.setHeight(400);

                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setImageResource(R.drawable.pic4);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    layoutLayer.addView(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.v(LOG_TAG, "imageView click");
                        }
                    });

                    TextView textview = new TextView(getApplicationContext());
                    textview.setText("textView");
                    textview.setTextSize(40);
                    textview.setBackgroundColor(Color.WHITE);
                    layoutLayer.addView(textview);

                    textview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.v(LOG_TAG, "textview click");
                        }
                    });

                    GLLayerGroup layerGroup = renderClient.newLayerGroup();
                    layerGroup.setStartTime(10000);
                    layerGroup.setDuration(200000);
                    layerGroup.setGravity(GravityMode.CENTER);
                    layerGroup.setWidth(500);
                    layerGroup.setHeight(500);
                    layerGroup.setBackgroundColor(Color.BLUE);
                    rootLayer.addLayer(layerGroup);

                    return true;

                case MESSAGE_SURFACE_CREATE:
                    if (eglSurface != null) {
                        eglSurface.dispose();
                    }
                    SurfaceTexture surfaceTexture = (SurfaceTexture) msg.obj;
                    eglSurface = renderClient.obtainWindowSurface(surfaceTexture);
                    handler.sendEmptyMessage(MESSAGE_SURFACE_RENDER);
                    return true;
                case MESSAGE_SURFACE_DISPOSE:
                    if (eglSurface != null) {
                        eglSurface.dispose();
                        eglSurface = null;
                    }
                    synchronized (surfaceDestroy) {
                        surfaceDestroy.set(true);
                        surfaceDestroy.notifyAll();
                    }
                    handler.removeMessages(MESSAGE_SURFACE_RENDER);
                    return true;
                case MESSAGE_CLIENT_RELEASE:
                    renderClient.release();
                    handler.getLooper().quit();
                    synchronized (surfaceDestroy) {
                        surfaceDestroy.set(true);
                        surfaceDestroy.notifyAll();
                    }
                    return true;
                case MESSAGE_SURFACE_MOTION_EVENT: {
                    MotionEvent motionEvent = (MotionEvent) msg.obj;
                    rootLayer.dispatchTouchEvent(motionEvent);
                    motionEvent.recycle();
                    return true;
                }
                case MESSAGE_SURFACE_RENDER:
                    handler.removeMessages(MESSAGE_SURFACE_RENDER);
                    long time = System.currentTimeMillis() - startTime;
                    if (time > rootLayer.getDuration()) {
                        startTime = System.currentTimeMillis();
                        time = 0;
                    }
                    rootLayer.setTime(time);
                    rootLayer.render(eglSurface);
                    handler.sendEmptyMessageDelayed(MESSAGE_SURFACE_RENDER, 40);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl_layer);
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
                handler.sendMessage(message);

                return true;
            }
        });
        HandlerThread handlerThread = new HandlerThread("TextureView");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper(), callback);
        handler.sendEmptyMessage(MESSAGE_CLIENT_CREATE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.sendEmptyMessage(MESSAGE_CLIENT_RELEASE);
    }


    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        Message message = Message.obtain();
        message.what = MESSAGE_SURFACE_CREATE;
        message.obj = surface;
        handler.sendMessage(message);
    }


    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        handler.sendEmptyMessage(MESSAGE_SURFACE_DISPOSE);
        synchronized (surfaceDestroy) {
            try {
                while (!surfaceDestroy.get()) {
                    surfaceDestroy.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }
}