package com.jonanorman.android.renderclient.sample;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jonanorman.android.renderclient.layer.GLLayer;
import com.jonanorman.android.renderclient.layer.GLViewLayer;
import com.jonanorman.android.renderclient.math.TimeStamp;
import com.jonanorman.android.renderclient.opengl.GLRenderMessage;
import com.jonanorman.android.renderclient.view.GLTextureViewRender;

import java.util.ArrayList;
import java.util.List;

public class GLViewLayerActivity extends AppCompatActivity {


    private GLRenderMessage renderMessage;
    private GLTextureViewRender textureViewRender;
    private GLViewLayer renderLayer;
    private TextureView textureView;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_layer);
        initRenderMessage();
        initTextureView();
        initTextView();
    }

    private void initTextView() {
        textView = findViewById(R.id.textView);
    }

    private void initTextureView() {

        int motionEventId = renderMessage.getAutoMessageId();
        textureView = findViewById(R.id.textureView);
        textureView.setOpaque(false);
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
        renderMessage.addHandlerCallback(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                int what = msg.what;
                if (what == motionEventId) {
                    MotionEvent motionEvent = (MotionEvent) msg.obj;
                    renderLayer.queueTouchEvent(motionEvent);
                    motionEvent.recycle();
                    return true;
                }
                return false;
            }
        });
        textureViewRender = new GLTextureViewRender(renderMessage, textureView);
        textureViewRender.setFrameRenderCallback(frameRenderCallback);
        textureViewRender.start();
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
        renderLayer = new GLViewLayer(getApplicationContext(), R.style.AppTheme);
        renderLayer.addOnRenderListener(renderListener);
        renderLayer.setBackgroundColor(Color.WHITE);

        LayoutInflater layoutInflater = LayoutInflater.from(renderLayer.getContext());
        View view = layoutInflater.inflate(R.layout.layout_view_layer, null);
        renderLayer.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ListView listView = view.findViewById(R.id.list_view);
        List<String> nameList = new ArrayList<>();
        int length = 40;
        for (int i = 0; i < length; i++) {
            nameList.add("item:" + i);
        }
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        listView.setAdapter(new ArrayAdapter(
                layoutInflater.getContext(),
                android.R.layout.simple_list_item_1,
                nameList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //can not call this, because not exist viewRootImpl

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

        TextView textView = view.findViewById(R.id.textView);
        float curTranslationX = textView.getTranslationX();
        ObjectAnimator animator = ObjectAnimator.ofFloat(textView, "translationX", curTranslationX, -250f, curTranslationX);
        animator.setDuration(3000);
        animator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textureViewRender.release();
        renderMessage.recycleAndWait();
    }


}