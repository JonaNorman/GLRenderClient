package com.jonanorman.android.renderclient.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
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
import com.jonanorman.android.renderclient.GLViewLayer;
import com.jonanorman.android.renderclient.GLRenderClient;
import com.jonanorman.android.renderclient.GLRenderThread;
import com.jonanorman.android.renderclient.GLShaderEffect;
import com.jonanorman.android.renderclient.GLTexture;
import com.jonanorman.android.renderclient.GLTextureFilter;
import com.jonanorman.android.renderclient.GLTextureLayer;
import com.jonanorman.android.renderclient.GLTextureType;
import com.jonanorman.android.renderclient.math.GravityMode;
import com.jonanorman.android.renderclient.math.KeyframeSet;
import com.jonanorman.android.renderclient.math.ScaleMode;

public class GLLayerGroupActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private static final int MESSAGE_LAYER_CREATE = 1;
    private static final int MESSAGE_SURFACE_RENDER = 2;
    private static final int MESSAGE_SURFACE_MOTION_EVENT = 3;

    private static final String LOG_TAG = "GLLayerActivity";


    private GLRenderThread renderThread = new GLRenderThread(new GLRenderClient.Builder());
    private SurfaceTexture surfaceTexture = null;


    private Handler.Callback callback = new Handler.Callback() {

        long startTime;
        GLLayerGroup rootLayer;

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int what = msg.what;
            switch (what) {
                case MESSAGE_LAYER_CREATE: {
                    GLRenderClient renderClient = renderThread.getRenderClient();
                    rootLayer = renderClient.newLayerGroup();
                    rootLayer.setDuration(10000);
                    GLColorLayer colorLayer = renderClient.newColorLayer();
                    KeyframeSet colorFrameSet = GLColorLayer.ofColor(Color.RED, Color.YELLOW, Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.RED);
                    colorFrameSet.setTypeEvaluator(new GLColorLayer.ColorEvaluator());
                    colorLayer.setKeyframes(GLColorLayer.KEY_COLOR, colorFrameSet);
                    colorLayer.setOnClickListener(new GLLayer.OnClickListener() {
                        @Override
                        public void onClick(GLLayer layer) {
                            Log.v(LOG_TAG, "colorLayer click");
                        }
                    });
                    rootLayer.addLayer(colorLayer);
                    {

                        GLLayerGroup topGroup = renderClient.newLayerGroup();
                        topGroup.setGravity(GravityMode.TOP_CENTER_HORIZONTAL);
                        topGroup.setWidth(500);
                        topGroup.setHeight(500);
                        topGroup.setDuration(10000);
                        KeyframeSet keyframes = KeyframeSet.ofFloat(10000, 500, 1000, 0);
                        topGroup.setKeyframes(GLLayer.KEY_FRAMES_KEY_LAYER_WIDTH, keyframes);
                        keyframes = KeyframeSet.ofFloat(10000, 500, 600, 500);
                        topGroup.setKeyframes(GLLayer.KEY_FRAMES_KEY_LAYER_HEIGHT, keyframes);
                        rootLayer.addLayer(topGroup);


                        GLViewLayer layoutLayer = renderClient.newLayoutLayer(getApplicationContext());
                        layoutLayer.setBackgroundColor(Color.RED);
                        layoutLayer.setStartTime(4000);
                        layoutLayer.setDuration(6000);
                        layoutLayer.setWidth(600);
                        layoutLayer.setHeight(400);
                        layoutLayer.setGravity(GravityMode.TOP_CENTER_HORIZONTAL);
                        topGroup.addLayer(layoutLayer);

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
                        textview.setText("abcd");
                        textview.setTextSize(40);
                        textview.setBackgroundColor(Color.WHITE);
                        layoutLayer.addView(textview);

                        textview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.v(LOG_TAG, "textview click");
                            }
                        });

                        GLTextureLayer textureLayer = renderClient.newTextureLayer();
                        textureLayer.setDuration(5000);
                        textureLayer.setWidth(200);
                        textureLayer.setHeight(200);
                        GLTexture texture = renderClient.newTexture(GLTextureType.TEXTURE_2D);
                        texture.setMinFilter(GLTextureFilter.LINEAR_MIPMAP_LINEAR);
                        texture.setMagFilter(GLTextureFilter.LINEAR);
                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                        bitmapOptions.inPremultiplied = false;
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.alpha, bitmapOptions);
                        texture.updateBitmap(bitmap);
                        bitmap.recycle();
                        textureLayer.setTexture(texture);
                        topGroup.addLayer(textureLayer);
                    }


                    {

                        GLLayerGroup layerGroup = renderClient.newLayerGroup();
                        layerGroup.setGravity(GravityMode.CENTER);
                        layerGroup.setHeight(500);
                        rootLayer.addLayer(layerGroup);


                        GLBitmapLayer bitmapLayer1 = renderClient.newBitmapLayer();
                        bitmapLayer1.setBitmapResId(getApplicationContext(), R.drawable.pic2);
                        bitmapLayer1.setGravity(GravityMode.CENTER);
                        bitmapLayer1.setTextureScaleMode(ScaleMode.FILL);
                        layerGroup.addLayer(bitmapLayer1);

                        GLBitmapLayer bitmapLayer2 = renderClient.newBitmapLayer();
                        bitmapLayer2.setWidth(300);
                        bitmapLayer2.setHeight(300);
                        bitmapLayer2.setTextureScaleMode(ScaleMode.FILL);
                        bitmapLayer2.setBitmapResId(getApplicationContext(), R.drawable.pic1);
                        layerGroup.addLayer(bitmapLayer2);


                        GLShaderEffect effect = renderClient.newShaderEffect();
                        effect.setFragmentShaderCode("precision mediump float;\n" +
                                "varying highp vec2 textureCoordinate;\n" +
                                "uniform sampler2D inputImageTexture;\n" +
                                "uniform vec2 inputTextureSize;\n" +
                                "uniform vec2 viewPortSize;\n" +
                                "uniform float renderTime;\n" +
                                "uniform float alpha;\n" +
                                "\n" +
                                "void main()\n" +
                                "{\n" +
                                "    vec4 color  = texture2D(inputImageTexture, textureCoordinate);\n" +
                                "    gl_FragColor = color*alpha;\n" +
                                "}");
                        KeyframeSet keyframeSet = KeyframeSet.ofFloat(1.0f, 0.0f, 1.0f);
                        keyframeSet.setDuration(3000);
                        effect.setKeyframes("alpha", keyframeSet);

                        layerGroup.addEffect(effect);
                    }
                    {
                        GLLayerGroup bottomGroup = renderClient.newLayerGroup();
                        bottomGroup.setWidth(600);
                        bottomGroup.setHeight(500);
                        bottomGroup.setGravity(GravityMode.BOTTOM_CENTER_HORIZONTAL);
                        bottomGroup.setBackgroundColor(Color.BLUE);
                        bottomGroup.setDuration(5000);


                        {
                            GLBitmapLayer bitmapLayer = renderClient.newBitmapLayer();
                            bitmapLayer.setBitmapResId(getApplicationContext(), R.drawable.pic3);
                            bitmapLayer.setBackgroundColor(Color.YELLOW);
                            bitmapLayer.setGravity(GravityMode.CENTER);
                            bottomGroup.addLayer(bitmapLayer);

                        }

                        {
                            GLBitmapLayer bitmapLayer = renderClient.newBitmapLayer();
                            bitmapLayer.setBitmapResId(getApplicationContext(), R.drawable.pic4);
                            bitmapLayer.setBackgroundColor(Color.YELLOW);
                            bitmapLayer.setGravity(GravityMode.CENTER);
                            bottomGroup.addLayer(bitmapLayer);
                        }
                        rootLayer.addLayer(bottomGroup);
                    }
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
                        if (time > rootLayer.getDuration()) {
                            startTime = System.currentTimeMillis();
                            time = 0;
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