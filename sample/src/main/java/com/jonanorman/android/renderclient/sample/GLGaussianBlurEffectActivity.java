package com.jonanorman.android.renderclient.sample;

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

import com.jonanorman.android.renderclient.EGLConfigSimpleChooser;
import com.jonanorman.android.renderclient.GLBitmapLayer;
import com.jonanorman.android.renderclient.GLFrameBuffer;
import com.jonanorman.android.renderclient.GLGaussianBlurEffect;
import com.jonanorman.android.renderclient.GLRenderClient;
import com.jonanorman.android.renderclient.GLRenderThread;
import com.jonanorman.android.renderclient.math.GravityMode;

import java.text.DecimalFormat;

public class GLGaussianBlurEffectActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private static final int MESSAGE_LAYER_CREATE = 1;
    private static final int MESSAGE_SURFACE_RENDER = 2;
    private static final int MESSAGE_SURFACE_MOTION_EVENT = 3;

    private GLRenderThread renderThread;
    private SurfaceTexture surfaceTexture = null;
    GLGaussianBlurEffect gaussianBlurEffect;


    private Handler.Callback callback = new Handler.Callback() {

        long startTime;
        GLBitmapLayer rootLayer;
        long sumTime;
        long sumCount = 0;


        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int what = msg.what;

            switch (what) {
                case MESSAGE_LAYER_CREATE: {
                    GLRenderClient renderClient = renderThread.getRenderClient();
                    rootLayer = renderClient.newBitmapLayer();
                    rootLayer.setBitmapResId(getApplicationContext(), R.drawable.alpha);
                    gaussianBlurEffect = new GLGaussianBlurEffect(renderClient) {
                        @Override
                        protected GLFrameBuffer renderEffect(GLFrameBuffer input) {
                            long effectStartTime = System.currentTimeMillis();
                            try {
                                return super.renderEffect(input);
                            } finally {
                                sumCount++;
                                sumTime += System.currentTimeMillis() - effectStartTime;
                                if (sumCount > 10) {
                                    long avgTime = sumTime / sumCount;
                                    sumCount = 0;
                                    sumTime = 0;
                                    textView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            textView.setText("blur time:" + avgTime + "ms");
                                        }
                                    });
                                }
                            }

                        }
                    };
                    rootLayer.addEffect(gaussianBlurEffect);
                    rootLayer.setGravity(GravityMode.CENTER);
                }

                return true;
                case MESSAGE_SURFACE_MOTION_EVENT: {
                    MotionEvent motionEvent = (MotionEvent) msg.obj;
                    rootLayer.queueTouchEvent(motionEvent);
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
                        rootLayer.render(surfaceTexture);
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


    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        DecimalFormat df = new DecimalFormat("#.00");

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar == seekBar1) {
                textViewSeekBar1.setText("" + progress);
            } else if (seekBar == seekBar2) {
                textViewSeekBar2.setText(df.format(progress * 1.0f / seekBar.getMax() * 5));
            } else if (seekBar == seekBar3) {
                textViewSeekBar3.setText(df.format(+progress * 1.0f / seekBar.getMax()));
            }
            renderThread.post(new Runnable() {
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
        textView = findViewById(R.id.textView);
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
        GLRenderClient.Builder builder = new GLRenderClient.Builder();
        EGLConfigSimpleChooser.Builder simpleChooser = new EGLConfigSimpleChooser.Builder();
        simpleChooser.setDepthSize(8);
        builder.setEGLConfigChooser(simpleChooser.build());
        renderThread = new GLRenderThread(builder);
        renderThread.start();
        renderThread.setRenderCallback(callback);
        renderThread.sendEmptyMessage(MESSAGE_LAYER_CREATE);
        seekBar1 = findViewById(R.id.seek_bar_1);
        seekBar1.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar2 = findViewById(R.id.seek_bar_2);
        seekBar2.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar3 = findViewById(R.id.seek_bar_3);
        seekBar3.setOnSeekBarChangeListener(seekBarChangeListener);
        textViewSeekBar1 = findViewById(R.id.seek_bar_1_text);
        textViewSeekBar2 = findViewById(R.id.seek_bar_2_text);
        textViewSeekBar3 = findViewById(R.id.seek_bar_3_text);


        seekBar1.setProgress(0);
        seekBar1.setMax(100);
        seekBar2.setMax(10);
        seekBar2.setProgress(0);
        seekBar3.setMax(100);
        seekBar3.setProgress(0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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