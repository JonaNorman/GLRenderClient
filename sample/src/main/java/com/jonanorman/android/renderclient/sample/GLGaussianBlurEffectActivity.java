package com.jonanorman.android.renderclient.sample;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jonanorman.android.renderclient.effect.GLGaussianBlurEffect;
import com.jonanorman.android.renderclient.layer.GLBitmapLayer;
import com.jonanorman.android.renderclient.layer.GLLayer;
import com.jonanorman.android.renderclient.math.GravityMode;
import com.jonanorman.android.renderclient.math.ScaleMode;
import com.jonanorman.android.renderclient.math.TimeStamp;
import com.jonanorman.android.renderclient.opengl.GLRenderMessage;
import com.jonanorman.android.renderclient.view.GLTextureViewRender;

import java.text.DecimalFormat;

public class GLGaussianBlurEffectActivity extends AppCompatActivity {


    private GLRenderMessage renderMessage;
    private GLTextureViewRender textureViewRender;
    private GLGaussianBlurEffect gaussianBlurEffect;
    private GLBitmapLayer renderLayer;


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
            renderLayer.render(surfaceTexture, timeStamp);
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


    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        DecimalFormat df = new DecimalFormat("#.00");

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar == seekBar1) {
                textViewSeekBar1.setText("" + progress);
            } else if (seekBar == seekBar2) {
                textViewSeekBar2.setText(df.format(progress * 1.0f / seekBar.getMax() * 10));
            } else if (seekBar == seekBar3) {
                textViewSeekBar3.setText(df.format(+progress * 1.0f / seekBar.getMax()));
            }
            renderMessage.post(new Runnable() {
                @Override
                public void run() {
                    if (seekBar == seekBar1) {
                        gaussianBlurEffect.setBlurRadius(progress);
                    } else if (seekBar == seekBar2) {
                        gaussianBlurEffect.setBlurStep(progress * 1.0f / seekBar.getMax() * 10);
                    } else if (seekBar == seekBar3) {
                        gaussianBlurEffect.setBlurSigma(progress * 1.0f / seekBar.getMax());
                    }

                }
            });

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    private SeekBar seekBar1;
    private SeekBar seekBar2;
    private SeekBar seekBar3;
    private TextView textViewSeekBar1;
    private TextView textViewSeekBar2;
    private TextView textViewSeekBar3;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaussian_blur);
        initRenderMesssage();
        initTextureView();
        initView();
    }

    private void initView() {
        textView = findViewById(R.id.textView);
        seekBar1 = findViewById(R.id.seek_bar_1);
        seekBar1.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar2 = findViewById(R.id.seek_bar_2);
        seekBar2.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar3 = findViewById(R.id.seek_bar_3);
        seekBar3.setOnSeekBarChangeListener(seekBarChangeListener);
        textViewSeekBar1 = findViewById(R.id.seek_bar_1_text);
        textViewSeekBar2 = findViewById(R.id.seek_bar_2_text);
        textViewSeekBar3 = findViewById(R.id.seek_bar_3_text);

        seekBar1.setMax(100);
        seekBar2.setMax(10);
        seekBar3.setMax(100);
        seekBar1.setProgress(10);
        seekBar2.setProgress(2);
        seekBar3.setProgress(50);
    }

    private void initRenderMesssage() {
        renderMessage = GLRenderMessage.obtain();

        renderMessage.post(new Runnable() {
            @Override
            public void run() {
                renderLayer = new GLBitmapLayer();
                renderLayer.setWidth(900);
                renderLayer.setHeight(500);
                renderLayer.setScaleMode(ScaleMode.FIT);
                renderLayer.setTextureScaleMode(ScaleMode.FILL);
                renderLayer.setBitmapResId(getApplicationContext(), R.drawable.pic9);
                gaussianBlurEffect = new GLGaussianBlurEffect();
                renderLayer.addEffect(gaussianBlurEffect);
                renderLayer.addOnRenderListener(renderListener);
                renderLayer.setGravity(GravityMode.CENTER);
            }
        });
    }

    private void initTextureView() {
        TextureView textureView = findViewById(R.id.textureView);
        textureView.setOpaque(false);
        textureViewRender = new GLTextureViewRender(renderMessage, textureView);
        textureViewRender.setFrameRenderCallback(frameRenderCallback);
        textureViewRender.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        textureViewRender.release();
        renderMessage.recycleAndWait();
    }
}