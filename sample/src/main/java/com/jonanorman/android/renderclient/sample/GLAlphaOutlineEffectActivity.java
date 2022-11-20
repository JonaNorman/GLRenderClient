package com.jonanorman.android.renderclient.sample;

import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jonanorman.android.renderclient.effect.GLAlphaOutlineEffect;
import com.jonanorman.android.renderclient.layer.GLBitmapLayer;
import com.jonanorman.android.renderclient.layer.GLLayer;
import com.jonanorman.android.renderclient.math.GravityMode;
import com.jonanorman.android.renderclient.math.TimeStamp;
import com.jonanorman.android.renderclient.opengl.GLRenderMessage;
import com.jonanorman.android.renderclient.view.GLTextureViewRender;

public class GLAlphaOutlineEffectActivity extends AppCompatActivity {

    private GLRenderMessage renderMessage;
    private GLTextureViewRender textureViewRender;
    private GLAlphaOutlineEffect outlineEffect;
    private GLBitmapLayer rootLayer;


    private SeekBar seekBar1;
    private SeekBar seekBar2;
    private SeekBar seekBar3;
    private TextView textViewSeekBar1;
    private TextView textViewSeekBar2;
    private TextView textViewSeekBar3;
    private TextView textView;
    private TextureView textureView;


    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar == seekBar1) {
                textViewSeekBar1.setText("" + progress);
            } else if (seekBar == seekBar2) {
                textViewSeekBar2.setText("" + progress / 1000.0f);
            } else if (seekBar == seekBar3) {
                textViewSeekBar3.setText("" + progress);
            }
            renderMessage.post(new Runnable() {
                @Override
                public void run() {
                    if (seekBar == seekBar1) {
                        outlineEffect.setMaxOutlineWidth(progress);
                    } else if (seekBar == seekBar2) {
                        outlineEffect.setIntensity(progress / 1000.0f);
                    } else if (seekBar == seekBar3) {
                        outlineEffect.setOutlineStyle(progress);
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
            rootLayer.render(surfaceTexture, timeStamp);
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
        setContentView(R.layout.activity_alpha_outline_effect);
        initRenderMessage();
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
        seekBar2.setMax(1000);
        seekBar3.setMax(5);
        seekBar1.setProgress(30);
        seekBar2.setProgress(500);
        seekBar3.setProgress(1);
    }

    private void initRenderMessage() {
        renderMessage = GLRenderMessage.obtain();
        renderMessage.post(new Runnable() {
            @Override
            public void run() {
                initLayer();
            }
        });


    }

    private void initLayer() {
        rootLayer = new GLBitmapLayer();
        rootLayer.setBitmapResId(getApplicationContext(), R.drawable.alpha2);
        outlineEffect = new GLAlphaOutlineEffect();
        rootLayer.addEffect(outlineEffect);
        rootLayer.setGravity(GravityMode.CENTER);
        rootLayer.addOnRenderListener(renderListener);
        outlineEffect.setOutlineColor(Color.WHITE);
    }

    private void initTextureView() {
        int motionEventId = renderMessage.getAutoMessageId();
        textureView = findViewById(R.id.textureView);
        textureView.setOpaque(false);
        renderMessage.addHandlerCallback(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                int what = msg.what;
                if (what == motionEventId) {
                    MotionEvent motionEvent = (MotionEvent) msg.obj;
                    rootLayer.queueTouchEvent(motionEvent);
                    motionEvent.recycle();
                    return true;
                }
                return false;
            }
        });
        textureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Message message = Message.obtain();
                message.what = motionEventId;
                MotionEvent motionEvent = MotionEvent.obtain(event);
                message.obj = motionEvent;
                renderMessage.sendMessage(message);
                return true;
            }
        });

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