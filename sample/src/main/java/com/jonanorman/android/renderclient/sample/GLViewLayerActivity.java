package com.jonanorman.android.renderclient.sample;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jonanorman.android.renderclient.GLRenderClient;
import com.jonanorman.android.renderclient.GLRenderThread;
import com.jonanorman.android.renderclient.GLViewLayer;

import java.util.ArrayList;
import java.util.List;

public class GLViewLayerActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private static final int MESSAGE_LAYER_CREATE = 1;
    private static final int MESSAGE_SURFACE_RENDER = 2;
    private static final int MESSAGE_SURFACE_MOTION_EVENT = 3;

    private GLRenderThread renderThread = new GLRenderThread(new GLRenderClient.Builder());
    private SurfaceTexture surfaceTexture = null;
    GLViewLayer viewLayer;


    private Handler.Callback callback = new Handler.Callback() {


        long startTime;


        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int what = msg.what;
            switch (what) {
                case MESSAGE_LAYER_CREATE: {
                    GLRenderClient renderClient = renderThread.getRenderClient();
                    viewLayer = renderClient.newLayoutLayer(getApplicationContext(), R.style.AppTheme);
                    viewLayer.setBackgroundColor(Color.WHITE);
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
                        viewLayer.setTime(time);
                        viewLayer.render(surfaceTexture);
                        if (!renderThread.hasMessages(MESSAGE_SURFACE_RENDER)) {
                            renderThread.sendEmptyMessageDelayed(MESSAGE_SURFACE_RENDER, 30);
                        }
                        return true;
                    } else {
                        startTime = 0;
                    }
                    return true;
                case MESSAGE_SURFACE_MOTION_EVENT: {
                    MotionEvent motionEvent = (MotionEvent) msg.obj;
                    viewLayer.queueTouchEvent(motionEvent);
                    return true;
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
        renderThread.post(new Runnable() {
            @Override
            public void run() {
                LayoutInflater layoutInflater = LayoutInflater.from(viewLayer.getContext());
                View view = layoutInflater.inflate(R.layout.layout_view_layer, null);
                viewLayer.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                ListView listView = view.findViewById(R.id.list_view);
                List<String> nameList = new ArrayList<>();
                int length = 40;
                for (int i = 0; i < length; i++) {
                    nameList.add("item:" + i);
                }
                listView.setAdapter(new ArrayAdapter(
                        layoutInflater.getContext(),
                        android.R.layout.simple_list_item_1,
                        nameList));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Toast.makeText(layoutInflater.getContext(), "listView position:" + position + " click", Toast.LENGTH_SHORT).show();
                    }
                });
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(layoutInflater.getContext(), "listView position:" + position + " long click", Toast.LENGTH_SHORT).show();

                        return true;
                    }
                });
                listView.setSelector(new ColorDrawable(Color.WHITE));

                TextView textView = view.findViewById(R.id.textView);
                float curTranslationX = textView.getTranslationX();
                ObjectAnimator animator = ObjectAnimator.ofFloat(textView, "translationX", curTranslationX, -250f, curTranslationX + 100);
                animator.setDuration(3000);
                animator.start();
                Animation alpha = new RotateAnimation(0, 45);
                alpha.setDuration(3000);
                alpha.setFillAfter(true);
                alpha.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        animation.getDuration();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        animation.getDuration();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        animation.getDuration();
                    }
                });

                textView.startAnimation(alpha);


            }
        });


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

}