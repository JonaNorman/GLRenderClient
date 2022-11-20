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
import android.view.Surface;
import android.view.TextureView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jonanorman.android.renderclient.layer.GLLayer;
import com.jonanorman.android.renderclient.layer.GLSurfaceTextureLayer;
import com.jonanorman.android.renderclient.math.TimeStamp;
import com.jonanorman.android.renderclient.opengl.GLRenderMessage;
import com.jonanorman.android.renderclient.view.GLTextureViewRender;

public class GLSurfaceTextureLayerActivity extends AppCompatActivity {

    private GLRenderMessage renderMessage;
    private GLTextureViewRender textureViewRender;
    private SurfaceAnimationRenderer animationRenderer;
    private GLSurfaceTextureLayer surfaceTextureLayer;
    private TextView textView;


    private GLTextureViewRender.onFrameRenderCallback frameRenderCallback = new GLTextureViewRender.onFrameRenderCallback() {

        long startTime;
        TimeStamp timeStamp = TimeStamp.ofMills(0);

        @Override
        public void onFrameStart() {
            startTime = System.currentTimeMillis();
        }

        @Override
        public void onFrameRender(SurfaceTexture surfaceTexture) {

            long durationMs = System.currentTimeMillis() - startTime;
            startTime = System.currentTimeMillis();
            timeStamp.setDuration(timeStamp.getDuration() + durationMs);
            surfaceTextureLayer.render(surfaceTexture, timeStamp);

        }

        @Override
        public void onFrameStop() {

        }
    };

    private GLLayer.OnRenderListener renderListener = new GLLayer.OnRenderListener() {
        long effectStartTime;
        long sumTime;
        long sumCount = 0;

        @Override
        public void onRenderStart(GLLayer layer) {
            effectStartTime = System.currentTimeMillis();
        }

        @Override
        public void onRenderEnd(GLLayer layer, boolean success) {
            sumCount++;
            sumTime += System.currentTimeMillis() - effectStartTime;
            if (sumCount > 10) {
                long avgTime = sumTime / sumCount;
                sumCount = 0;
                sumTime = 0;
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("render time:" + avgTime + "ms");
                    }
                });
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_surfacetexture_layer);
        initRenderMessage();
        initTextureView();
        initTextView();
        initSurfaceAnimation();

    }

    private void initTextView() {
        textView = findViewById(R.id.textView);
    }

    private void initSurfaceAnimation() {
        animationRenderer = new SurfaceAnimationRenderer();
        animationRenderer.start();
    }

    private void initTextureView() {
        TextureView textureView = findViewById(R.id.textureView);
        textureViewRender = new GLTextureViewRender(renderMessage, textureView);
        textureViewRender.setFrameRenderCallback(frameRenderCallback);
        textureViewRender.start();
    }

    private void initRenderMessage() {
        renderMessage = GLRenderMessage.obtain();
        renderMessage.post(new Runnable() {
            @Override
            public void run() {
                surfaceTextureLayer = new GLSurfaceTextureLayer(animationRenderer.getSurfaceTexture());
                surfaceTextureLayer.setSurfaceWidth(animationRenderer.getWidth());
                surfaceTextureLayer.setSurfaceHeight(animationRenderer.getHeight());
                surfaceTextureLayer.addOnRenderListener(renderListener);
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        textureViewRender.release();
        renderMessage.recycleAndWait();
        animationRenderer.quit();
    }


    private class SurfaceAnimationRenderer extends Thread {

        private Object mLock = new Object();
        private boolean mDone;
        private final SurfaceTexture surfaceTexture;
        private final Surface surface;
        private int width = 640;
        private int height = 673;

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
            paint.setColor(Color.BLUE);
            rectF.set(0, 0, 50, 50);
            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.xiaowanzi);

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

        int color1 = Color.YELLOW;
        int color2 = Color.RED;
        int aa = 1;

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