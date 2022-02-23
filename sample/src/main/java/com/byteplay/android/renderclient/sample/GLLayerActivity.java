package com.byteplay.android.renderclient.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.byteplay.android.renderclient.GLRenderSurface;
import com.byteplay.android.renderclient.GLRenderClient;
import com.byteplay.android.renderclient.GLTexture;
import com.byteplay.android.renderclient.GLTextureLayer;
import com.byteplay.android.renderclient.GLTextureType;

import java.util.concurrent.atomic.AtomicBoolean;

public class GLLayerActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private static final int MESSAGE_CLIENT_CREATE = 1;
    private static final int MESSAGE_CLIENT_RELEASE = 2;
    private static final int MESSAGE_SURFACE_CREATE = 3;
    private static final int MESSAGE_SURFACE_DISPOSE = 4;
    private static final int MESSAGE_SURFACE_RENDER = 5;

    private Handler handler;

    private AtomicBoolean surfaceDestroy = new AtomicBoolean();

    private Handler.Callback callback = new Handler.Callback() {
        GLRenderSurface eglSurface;
        GLRenderClient renderClient;
        GLTextureLayer textureLayer;
        long startTime = System.currentTimeMillis();


        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int what = msg.what;
            switch (what) {
                case MESSAGE_CLIENT_CREATE:
                    renderClient = new GLRenderClient.Builder().build();
                    renderClient.attachCurrentThread();
                    textureLayer = renderClient.newTextureLayer();
                    textureLayer.setDuration(10000);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic1);
                    GLTexture texture = renderClient.newTexture(GLTextureType.TEXTURE_2D);
                    texture.updateBitmap(bitmap);
                    bitmap.recycle();
                    textureLayer.setTexture(texture);
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
                case MESSAGE_SURFACE_RENDER:
                    handler.removeMessages(MESSAGE_SURFACE_RENDER);
                    long time = System.currentTimeMillis() - startTime;
                    if (time > textureLayer.getDuration()) {
                        startTime = System.currentTimeMillis();
                        time = 0;
                    }
                    textureLayer.setTime(time);
                    textureLayer.render(eglSurface);
                    handler.sendEmptyMessageDelayed(MESSAGE_SURFACE_RENDER, 40);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_render_client);
        TextureView textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(this);
        textureView.setOpaque(false);
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