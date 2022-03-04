package com.jonanorman.android.renderclient.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jonanorman.android.renderclient.GLLayer;
import com.jonanorman.android.renderclient.GLLayerGroup;
import com.jonanorman.android.renderclient.GLLayoutLayer;
import com.jonanorman.android.renderclient.GLRenderClient;
import com.jonanorman.android.renderclient.GLRenderSurface;
import com.jonanorman.android.renderclient.GLTexture;
import com.jonanorman.android.renderclient.GLTextureLayer;
import com.jonanorman.android.renderclient.GLTextureType;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class GLLayerActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private static final int MESSAGE_CLIENT_CREATE = 1;
    private static final int MESSAGE_CLIENT_RELEASE = 2;
    private static final int MESSAGE_SURFACE_CREATE = 3;
    private static final int MESSAGE_SURFACE_DISPOSE = 4;
    private static final int MESSAGE_SURFACE_RENDER = 5;
    private static final int MESSAGE_SURFACE_MOTION_EVENT = 6;

    private Handler handler;

    private AtomicBoolean surfaceDestroy = new AtomicBoolean();


    private Handler.Callback callback = new Handler.Callback() {
        GLRenderSurface eglSurface;
        GLRenderClient renderClient;
        GLTextureLayer textureLayer;
        long startTime = System.currentTimeMillis();
        GLLayerGroup layerGroup;
        Queue<MotionEvent> motionEventQueue = new LinkedList<>();


        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int what = msg.what;
            switch (what) {
                case MESSAGE_CLIENT_CREATE:
                    renderClient = new GLRenderClient.Builder().build();
                    renderClient.attachCurrentThread();
                    textureLayer = renderClient.newTextureLayer();
                    layerGroup = renderClient.newLayerGroup();
                    layerGroup.setDuration(10000);
                    layerGroup.add(textureLayer);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inScaled = false;
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic1, options);
                    GLTexture texture = renderClient.newTexture(GLTextureType.TEXTURE_2D);
                    texture.updateBitmap(bitmap);
                    bitmap.recycle();
                    textureLayer.setWidth(400);
                    textureLayer.setHeight(400);
                    textureLayer.setScaleX(2.0f);
                    textureLayer.setScaleY(2.0f);
//                    textureLayer.setRotation(45);
                    textureLayer.setTexture(texture);
                    textureLayer.setOnTouchListener(new GLLayer.OnTouchListener() {
                        @Override
                        public boolean onTouch(GLLayer layer, MotionEvent event) {
                            Log.v("sdasdas", "x:" + event.getX() + "y:" + event.getY());
                            return true;
                        }
                    });

                    GLLayoutLayer viewLayer = renderClient.newLayoutLayer(getApplicationContext());
                    viewLayer.setWidth(600);
                    viewLayer.setHeight(600);

                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setImageResource(R.drawable.pic4);
                    viewLayer.addView(imageView, new FrameLayout.LayoutParams(500, 500));

                    TextView textview = new TextView(getApplicationContext());
                    textview.setText("123\nabc");
                    textview.setTextSize(40);
                    textview.setBackgroundColor(Color.TRANSPARENT);
                    viewLayer.addView(textview);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(v.getContext(), "toas", Toast.LENGTH_LONG).show();
                        }
                    });
                    textview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(v.getContext(), "abc", Toast.LENGTH_LONG).show();
                        }
                    });
                    layerGroup.add(viewLayer);
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
                    motionEventQueue.offer(motionEvent);
                    return true;
                }
                case MESSAGE_SURFACE_RENDER:
                    handler.removeMessages(MESSAGE_SURFACE_RENDER);
                    long time = System.currentTimeMillis() - startTime;
                    if (time > layerGroup.getDuration()) {
                        startTime = System.currentTimeMillis();
                        time = 0;
                    }
                    layerGroup.setTime(time);
                    MotionEvent motionEvent = motionEventQueue.poll();
                    layerGroup.render(eglSurface, motionEvent);
                    if (motionEvent != null) {
                        motionEvent.recycle();
                    }
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