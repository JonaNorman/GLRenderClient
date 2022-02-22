package com.byteplay.android.renderclient.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.TextureView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.byteplay.android.renderclient.EGLSurface;
import com.byteplay.android.renderclient.GLFrameLayoutLayer;
import com.byteplay.android.renderclient.GLGravity;
import com.byteplay.android.renderclient.GLKeyframes;
import com.byteplay.android.renderclient.GLLayer;
import com.byteplay.android.renderclient.GLLayerSet;
import com.byteplay.android.renderclient.GLRenderClient;
import com.byteplay.android.renderclient.GLShaderEffect;
import com.byteplay.android.renderclient.GLTexture;
import com.byteplay.android.renderclient.GLTextureFilter;
import com.byteplay.android.renderclient.GLTextureLayer;
import com.byteplay.android.renderclient.GLTextureType;

import java.util.concurrent.atomic.AtomicBoolean;

public class RenderClientActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private TextureView textureView;

    private Handler handler;

    private GLRenderClient renderClient = new GLRenderClient.Builder().build();
    private Object object = new Object();
    private AtomicBoolean done = new AtomicBoolean();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_render_client);
        textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(this);
        textureView.setOpaque(false);
        HandlerThread handlerThread = new HandlerThread("TextureView");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        handler.post(new Runnable() {
            @Override
            public void run() {
                renderClient.release();
                handler.getLooper().quitSafely();
                synchronized (object) {
                    done.set(true);
                    object.notifyAll();
                }
            }
        });

    }


    GLLayerSet frameLayer;
    EGLSurface eglSurface;

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (object) {
                    done.set(false);
                    object.notifyAll();
                }
                eglSurface = renderClient.newWindowSurface(surface);
                renderClient.attachCurrentThread();
                frameLayer = renderClient.newLayerSet();
                frameLayer.setDuration(10000);

                {
                    GLTextureLayer textureLayer = renderClient.newTextureLayer();
                    GLTexture texture = renderClient.newTexture(GLTextureType.TEXTURE_2D);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic2);
                    texture.updateBitmap(bitmap);
                    bitmap.recycle();
                    textureLayer.setTexture(texture);
                    GLKeyframes keyframes = GLKeyframes.ofFloat(5000, 600, 1200);
                    textureLayer.setKeyframe(GLLayer.KEY_FRAMES_KEY_LAYER_WIDTH, keyframes);
                    keyframes = GLKeyframes.ofFloat(5000, 600, 1200);
                    textureLayer.setKeyframe(GLLayer.KEY_FRAMES_KEY_LAYER_HEIGHT, keyframes);
                    frameLayer.addLayer(textureLayer);
                }

                {
                    GLTextureLayer textureLayer1 = renderClient.newTextureLayer();
                    GLTexture texture1 = renderClient.newTexture(GLTextureType.TEXTURE_2D);
                    texture1.setTextureFilter(GLTextureFilter.LINEAR_MIPMAP_LINEAR, GLTextureFilter.LINEAR);
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        bitmapOptions.inPremultiplied = false;
                    }
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.alpha, bitmapOptions);
                    texture1.updateBitmap(bitmap);
                    bitmap.recycle();
                    textureLayer1.setTexture(texture1);
                    frameLayer.addLayer(textureLayer1);

                }

                {
                    GLLayerSet frameLayer = renderClient.newLayerSet();
                    frameLayer.setWidth(500);
                    frameLayer.setHeight(500);
                    frameLayer.setGravity(GLGravity.TOP_CENTER_HORIZONTAL);
                    RenderClientActivity.this.frameLayer.addLayer(frameLayer);

                    {
                        GLTextureLayer textureLayer = renderClient.newTextureLayer();
                        GLTexture texture = renderClient.newTexture(GLTextureType.TEXTURE_2D);
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic1);
                        texture.updateBitmap(bitmap);
                        bitmap.recycle();
                        textureLayer.setTexture(texture);
                        frameLayer.addLayer(textureLayer);
                    }

                    {
                        GLTextureLayer textureLayer1 = renderClient.newTextureLayer();
                        GLTexture texture1 = renderClient.newTexture(GLTextureType.TEXTURE_2D);
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic5);
                        texture1.updateBitmap(bitmap);
                        bitmap.recycle();
                        textureLayer1.setTexture(texture1);
                        frameLayer.addLayer(textureLayer1);
                    }
                    GLShaderEffect effect = renderClient.newShaderEffect();
                    effect.setFragmentShaderCode("precision mediump float;\n" +
                            "varying highp vec2 textureCoordinate;\n" +
                            "uniform sampler2D inputImageTexture;\n" +
                            "uniform vec2 inputTextureSize;\n" +
                            "uniform vec2 viewPortSize;\n" +
                            "uniform float renderTime;\n" +
                            "\n" +
                            "void main()\n" +
                            "{\n" +
                            "    vec4 color  = texture2D(inputImageTexture, textureCoordinate);\n" +
                            "    gl_FragColor = color;\n" +
                            "}");

                    frameLayer.addEffect(effect);

                    effect = renderClient.newShaderEffect();
                    effect.setFragmentShaderCode("precision mediump float;\n" +
                            "varying highp vec2 textureCoordinate;\n" +
                            "uniform sampler2D inputImageTexture;\n" +
                            "uniform vec2 inputTextureSize;\n" +
                            "uniform vec2 viewPortSize;\n" +
                            "uniform float renderTime;\n" +
                            "\n" +
                            "void main()\n" +
                            "{\n" +
                            "    vec4 color  = texture2D(inputImageTexture, textureCoordinate);\n" +
                            "    color = color.r ==0.0?vec4(1.0):color;\n" +
                            "    gl_FragColor = color;\n" +
                            "}");

                    frameLayer.addEffect(effect);

                }

                {
                    GLLayerSet frameLayer = renderClient.newLayerSet();
                    frameLayer.setWidth(600);
                    frameLayer.setHeight(500);
                    frameLayer.setGravity(GLGravity.BOTTOM_CENTER_HORIZONTAL);
                    frameLayer.setBackgroundColor(Color.GREEN);
                    RenderClientActivity.this.frameLayer.addLayer(frameLayer);

                    {
                        GLTextureLayer textureLayer = renderClient.newTextureLayer();
                        GLTexture texture = renderClient.newTexture(GLTextureType.TEXTURE_2D);
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic3);
                        texture.updateBitmap(bitmap);
                        textureLayer.setTexture(texture);
                        textureLayer.setBackgroundColor(Color.YELLOW);
                        frameLayer.addLayer(textureLayer);
                    }

                    {
                        GLTextureLayer textureLayer1 = renderClient.newTextureLayer();
                        GLTexture texture1 = renderClient.newTexture(GLTextureType.TEXTURE_2D);
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic4);
                        texture1.updateBitmap(bitmap);
                        bitmap.recycle();
                        textureLayer1.setTexture(texture1);
                        frameLayer.addLayer(textureLayer1);
                    }
                }
                {

                    GLFrameLayoutLayer viewLayer = renderClient.newFrameLayoutLayer(getApplicationContext());
                    viewLayer.setWidth(600);
                    viewLayer.setHeight(600);

                    ImageView imageView = new ImageView(RenderClientActivity.this.getApplicationContext());
                    imageView.setImageResource(R.drawable.pic4);
                    viewLayer.addView(imageView, new FrameLayout.LayoutParams(500, 500));

                    TextView textview = new TextView(RenderClientActivity.this.getApplicationContext());
                    textview.setText("123\nabc");
                    textview.setTextSize(40);
                    textview.setBackgroundColor(Color.TRANSPARENT);
                    viewLayer.addView(textview);
                    frameLayer.addLayer(viewLayer);
                }

//                frameLayer.addTransform(new GLLayer.LayerTransform<GLFrameLayer>() {
//                    @Override
//                    public void onLayerTransform(GLFrameLayer layer, long renderTimeMs) {
//                        layer.setWidth(Math.min((int) (width * renderTimeMs / 2000.0), width));
//                        layer.setHeight(Math.min((int) (height * renderTimeMs / 2000.0), height));
//                    }
//                });

            }
        });

        Runnable runnable = new Runnable() {
            long startTime = System.currentTimeMillis();

            @Override
            public void run() {
                long time = System.currentTimeMillis() - startTime;
                if (time > frameLayer.getDuration()) {
                    startTime = System.currentTimeMillis();
                    time = 0;
                }
                frameLayer.setTime(time);
                frameLayer.render(eglSurface);

                handler.postDelayed(this, 50);
            }
        };

        handler.post(runnable);
    }


    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                eglSurface.dispose();
                synchronized (object) {
                    done.set(true);
                    object.notifyAll();
                }
            }
        });
        synchronized (object) {
            try {
                while (!done.get()) {
                    object.wait();
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