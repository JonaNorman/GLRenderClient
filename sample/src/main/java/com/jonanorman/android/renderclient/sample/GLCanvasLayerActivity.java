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
import android.view.TextureView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jonanorman.android.renderclient.layer.GLCanvasLayer;
import com.jonanorman.android.renderclient.layer.GLLayer;
import com.jonanorman.android.renderclient.math.TimeStamp;
import com.jonanorman.android.renderclient.opengl.GLRenderMessage;
import com.jonanorman.android.renderclient.view.GLTextureViewRender;

public class GLCanvasLayerActivity extends AppCompatActivity implements GLCanvasLayer.CanvasProvider {


    private GLRenderMessage renderMessage;
    private GLTextureViewRender textureViewRender;
    private GLCanvasLayer canvasLayer;

    private TextureView textureView;
    private TextView textView;

    private RectF rectF = new RectF();
    private Paint paint;
    private Matrix matrix = new Matrix();
    private Paint clearPaint = new Paint();
    private Bitmap bitmap;


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
            canvasLayer.render(surfaceTexture, timeStamp);

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
        setContentView(R.layout.activity_canvas_layer);
        initRenderMessage();
        initTextureView();
        initTextView();
        initCanvas();

    }

    private void initTextureView() {
        textureView = findViewById(R.id.textureView);
        textureViewRender = new GLTextureViewRender(renderMessage, textureView);
        textureViewRender.setFrameRenderCallback(frameRenderCallback);
        textureViewRender.start();
    }

    private void initTextView() {
        textView = findViewById(R.id.textView);

    }

    private void initCanvas() {
        paint = new Paint();
        paint.setColor(Color.RED);
        rectF.set(0, 0, 50, 50);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic3);
    }

    private void initRenderMessage() {
        renderMessage = GLRenderMessage.obtain();
        renderMessage.post(new Runnable() {
            @Override
            public void run() {
                canvasLayer = new GLCanvasLayer(GLCanvasLayerActivity.this);
                canvasLayer.addOnRenderListener(renderListener);
                canvasLayer.setCanvasWidth(500);
                canvasLayer.setCanvasHeight(500);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        textureViewRender.release();
        renderMessage.recycleAndWait();
    }


    @Override
    public void onDraw(Canvas canvas, TimeStamp timeStamp) {
        float rectWidth = rectF.width();
        float rectHeight = rectF.height();
        rectF.offset(20, 0);
        if (rectF.right > canvas.getWidth()) {
            rectF.offset(-rectF.left, rectHeight);
            if (rectF.bottom > canvas.getHeight()) {
                rectF.set(0, 0, rectWidth, rectHeight);
            }
        }
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), clearPaint);
        canvas.drawColor(Color.YELLOW);
        canvas.drawBitmap(bitmap, matrix, null);
        canvas.drawRect(rectF, paint);
    }

}