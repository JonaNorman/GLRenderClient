package com.jonanorman.android.renderclient.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jonanorman.android.renderclient.GLRenderClient;
import com.jonanorman.android.renderclient.GLRenderThread;
import com.jonanorman.android.renderclient.GLSurfaceTextureLayer;

public class GLSurfaceLayerActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private static final int MESSAGE_LAYER_CREATE = 1;
    private static final int MESSAGE_SURFACE_RENDER = 2;

    private GLRenderThread renderThread = new GLRenderThread(new GLRenderClient.Builder());
    private SurfaceTexture surfaceTexture = null;
    private SurfaceAnimationRenderer animationRenderer;


    private Handler.Callback callback = new Handler.Callback() {

        long startTime;
        GLSurfaceTextureLayer surfaceTextureLayer;

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int what = msg.what;
            switch (what) {
                case MESSAGE_LAYER_CREATE: {
                    GLRenderClient renderClient = renderThread.getRenderClient();
                    surfaceTextureLayer = renderClient.newSurfaceTextureLayer();
                    surfaceTextureLayer.setSurfaceTexture(animationRenderer.getSurfaceTexture());
                    surfaceTextureLayer.setSurfaceWidth(animationRenderer.getWidth());
                    surfaceTextureLayer.setSurfaceHeight(animationRenderer.getHeight());

                }

                return true;
                case MESSAGE_SURFACE_RENDER:
                    if (surfaceTexture != null) {
                        long time = 0;
                        if (startTime == 0) {
                            startTime = System.currentTimeMillis();
                        } else {
                            time = System.currentTimeMillis() - startTime;
                        }
                        surfaceTextureLayer.setTime(time);
                        GLRenderClient renderClient = renderThread.getRenderClient();
                        surfaceTextureLayer.render(renderClient.obtainWindowSurface(surfaceTexture));
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

        animationRenderer = new SurfaceAnimationRenderer();
        animationRenderer.start();
        TextureView textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(this);
        renderThread.start();
        renderThread.setRenderCallback(callback);
        renderThread.sendEmptyMessage(MESSAGE_LAYER_CREATE);
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        renderThread.quitAndWait();
        animationRenderer.quit();
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
        surfaceTexture = surface;
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

    private class SurfaceAnimationRenderer extends Thread {

        private Object mLock = new Object();
        private boolean mDone;
        private final SurfaceTexture surfaceTexture;
        private final Surface surface;
        private int width = 500;
        private int height = 500;

        private RectF rectF = new RectF();
        private Paint paint;
        Matrix matrix = new Matrix();

        Paint clearPaint = new Paint();

        Bitmap bitmap;


        public SurfaceAnimationRenderer() {
            super("TextureViewCanvas Renderer");
            surfaceTexture = new SurfaceTexture(0);
            surfaceTexture.setDefaultBufferSize(width, height);
            surface = new Surface(surfaceTexture);


            paint = new Paint();
            paint.setColor(Color.RED);
            rectF.set(0, 0, 50, 50);
            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic3);

        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public SurfaceTexture getSurfaceTexture() {
            return surfaceTexture;
        }

        @Override
        public void run() {
            try {
                while (!mDone) {

                    Canvas canvas = surface.lockCanvas(null);
                    try {
                        float rectWidth = rectF.width();
                        float rectHeight = rectF.height();
                        rectF.offset(0.2f, 0);
                        if (rectF.right > width) {
                            rectF.offset(-rectF.left, rectHeight);
                            if (rectF.bottom > height) {
                                rectF.set(0, 0, rectWidth, rectHeight);
                            }
                        }

                        canvas.drawRect(0, 0, width, height, clearPaint);
                        canvas.drawColor(Color.WHITE);
                        canvas.drawBitmap(bitmap, matrix, null);
                        canvas.drawRect(rectF, paint);

                    } finally {
                        try {
                            surface.unlockCanvasAndPost(canvas);
                        } catch (IllegalArgumentException iae) {
                            break;
                        }
                    }

                }
            } finally {
                try {
                    surface.release();
                } catch (Exception e) {

                }
                try {
                    surfaceTexture.release();
                } catch (Exception e) {

                }
            }

        }

        public void quit() {
            synchronized (mLock) {
                mDone = true;
                mLock.notify();
            }
        }


    }
}