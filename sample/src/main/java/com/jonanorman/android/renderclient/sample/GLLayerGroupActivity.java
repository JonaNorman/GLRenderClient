package com.jonanorman.android.renderclient.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jonanorman.android.renderclient.layer.GLBitmapLayer;
import com.jonanorman.android.renderclient.layer.GLColorLayer;
import com.jonanorman.android.renderclient.layer.GLLayer;
import com.jonanorman.android.renderclient.layer.GLLayerGroup;
import com.jonanorman.android.renderclient.layer.GLShaderEffect;
import com.jonanorman.android.renderclient.layer.GLTextureLayer;
import com.jonanorman.android.renderclient.layer.GLViewLayer;
import com.jonanorman.android.renderclient.math.GravityMode;
import com.jonanorman.android.renderclient.math.KeyframeSet;
import com.jonanorman.android.renderclient.math.ScaleMode;
import com.jonanorman.android.renderclient.math.TimeStamp;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLRenderMessage;
import com.jonanorman.android.renderclient.opengl.GLTexture;
import com.jonanorman.android.renderclient.opengl.gl20.GL20Texture;
import com.jonanorman.android.renderclient.view.GLSurfaceViewRender;

public class GLLayerGroupActivity extends AppCompatActivity {



    private GLRenderMessage renderMessage;
    private GLSurfaceViewRender surfaceViewRender;
    private GLLayerGroup rootLayer;
    private long lastRenderTimeMs;
    private TextView textView;
    private TimeStamp renderTimeStamp = TimeStamp.ofMills(0);

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
        setContentView(R.layout.activity_layout_group);
        initRenderMessage();
        initSurfaceView();
        initTextView();
    }

    private void initTextView() {
        textView = findViewById(R.id.textView);
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

    private void initSurfaceView() {
        int motionEventId = renderMessage.getAutoMessageId();
        SurfaceView surfaceView = findViewById(R.id.surfaceview);
        surfaceViewRender = new GLSurfaceViewRender(renderMessage, surfaceView);
        surfaceViewRender.setFrameRenderCallback(new GLSurfaceViewRender.onFrameRenderCallback() {
            @Override
            public void onFrameStart() {
                lastRenderTimeMs = System.currentTimeMillis();
            }

            @Override
            public void onFrameRender(Surface surfaceTexture) {
                updateLayerTime();
                rootLayer.render(surfaceTexture, renderTimeStamp);
            }

            @Override
            public void onFrameStop() {

            }
        });
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
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
        surfaceViewRender.start();
    }

    private void initLayer() {
        rootLayer = new GLLayerGroup();
        rootLayer.setDuration(TimeStamp.ofMills(10000));
        rootLayer.addOnRenderListener(renderListener);
        GLColorLayer colorLayer = new GLColorLayer();
        KeyframeSet colorFrameSet = GLColorLayer.ofColor(Color.RED, Color.YELLOW, Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.RED);
        colorFrameSet.setTypeEvaluator(new GLColorLayer.ColorEvaluator());
        colorLayer.setKeyframes(GLColorLayer.KEY_COLOR, colorFrameSet);
        colorLayer.setOnClickListener(new GLLayer.OnClickListener() {
            @Override
            public void onClick(GLLayer layer) {
                Toast.makeText(GLLayerGroupActivity.this,"ColorLayer click",Toast.LENGTH_SHORT).show();

            }
        });
        rootLayer.add(colorLayer);
        {
            GLLayerGroup topGroup = new GLLayerGroup();
            topGroup.setGravity(GravityMode.TOP_CENTER_HORIZONTAL);
            topGroup.setWidth(500);
            topGroup.setHeight(500);
            KeyframeSet keyframes = KeyframeSet.ofFloat(0, 500, 1000, 0);
            topGroup.setKeyframes(GLLayer.KEY_FRAMES_WIDTH, keyframes);
            keyframes = KeyframeSet.ofFloat(0, 500, 600, 500);
            topGroup.setKeyframes(GLLayer.KEY_FRAMES_HEIGHT, keyframes);
            rootLayer.add(topGroup);


            GLViewLayer layoutLayer = new GLViewLayer(getApplicationContext());
            layoutLayer.setBackgroundColor(Color.RED);
            layoutLayer.setStartTime(TimeStamp.ofMills(4000));
            layoutLayer.setDuration(TimeStamp.ofMills(6000));
            layoutLayer.setWidth(600);
            layoutLayer.setHeight(400);
            layoutLayer.setGravity(GravityMode.TOP_CENTER_HORIZONTAL);
            topGroup.add(layoutLayer);

            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setImageResource(R.drawable.pic4);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            layoutLayer.addView(imageView);

            TextView textview = new TextView(getApplicationContext());
            textview.setText("abcd");
            textview.setTextSize(40);
            textview.setBackgroundColor(Color.WHITE);
            layoutLayer.addView(textview);

            GLTextureLayer textureLayer = new GLTextureLayer();
            textureLayer.setDuration(TimeStamp.ofMills(5000));
            textureLayer.setWidth(200);
            textureLayer.setHeight(200);
            GLTexture texture = new GL20Texture(GLRenderClient.getCurrentClient(), GLTexture.Type.TEXTURE_2D);
            texture.setMinFilter(GLTexture.FilterMode.LINEAR_MIPMAP_LINEAR);
            texture.setMagFilter(GLTexture.FilterMode.LINEAR);
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inPremultiplied = false;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.alpha, bitmapOptions);
            texture.updateBitmap(bitmap);
            bitmap.recycle();
            textureLayer.setTexture(texture);
            topGroup.add(textureLayer);
        }
//
//
        {

            GLLayerGroup layerGroup = new GLLayerGroup();
            layerGroup.setGravity(GravityMode.CENTER);
            layerGroup.setHeight(500);
            rootLayer.add(layerGroup);


            GLBitmapLayer bitmapLayer1 = new GLBitmapLayer();
            bitmapLayer1.setBitmapResId(getApplicationContext(), R.drawable.pic2);
            bitmapLayer1.setGravity(GravityMode.CENTER);
            bitmapLayer1.setTextureScaleMode(ScaleMode.FILL);
            layerGroup.add(bitmapLayer1);
//
            GLBitmapLayer bitmapLayer2 = new GLBitmapLayer();
            bitmapLayer2.setWidth(300);
            bitmapLayer2.setHeight(300);
            bitmapLayer2.setTextureScaleMode(ScaleMode.FILL);
            bitmapLayer2.setBitmapResId(getApplicationContext(), R.drawable.pic1);
            layerGroup.add(bitmapLayer2);


            GLShaderEffect effect = new GLShaderEffect();
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
            keyframeSet.setDuration(TimeStamp.ofMills(3000));
            effect.setKeyframes("alpha", keyframeSet);
            layerGroup.addEffect(effect);
        }
        {
            GLLayerGroup bottomGroup = new GLLayerGroup();
            bottomGroup.setWidth(600);
            bottomGroup.setHeight(500);
            bottomGroup.setGravity(GravityMode.BOTTOM_CENTER_HORIZONTAL);
            bottomGroup.setBackgroundColor(Color.BLUE);
            bottomGroup.setDuration(TimeStamp.ofMills(5000));

            {
                GLBitmapLayer bitmapLayer = new GLBitmapLayer();
                bitmapLayer.setBitmapResId(getApplicationContext(), R.drawable.pic3);
                bitmapLayer.setBackgroundColor(Color.YELLOW);
                bitmapLayer.setGravity(GravityMode.CENTER);
                bottomGroup.add(bitmapLayer);

            }

            {
                GLBitmapLayer bitmapLayer = new GLBitmapLayer();
                bitmapLayer.setBitmapResId(getApplicationContext(), R.drawable.pic4);
                bitmapLayer.setBackgroundColor(Color.YELLOW);
                bitmapLayer.setGravity(GravityMode.CENTER);
                bottomGroup.add(bitmapLayer);
            }
            rootLayer.add(bottomGroup);
        }
    }


    private void updateLayerTime() {
        long durationMs = System.currentTimeMillis() - lastRenderTimeMs;
        lastRenderTimeMs = System.currentTimeMillis();
        renderTimeStamp.setDuration(renderTimeStamp.getDuration() + durationMs);
        if (renderTimeStamp.toMillis() > rootLayer.getDuration().toMillis()) {
            renderTimeStamp.setDuration(0);
            lastRenderTimeMs = System.currentTimeMillis();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        surfaceViewRender.release();
        renderMessage.recycleAndWait();
    }


}