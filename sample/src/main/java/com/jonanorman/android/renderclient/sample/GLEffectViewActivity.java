package com.jonanorman.android.renderclient.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jonanorman.android.renderclient.effect.GLWobbleEffect;
import com.jonanorman.android.renderclient.view.GLEffectLayout;

public class GLEffectViewActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effect_view);
        findViewById(R.id.Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(v.getContext(), "Click", Toast.LENGTH_SHORT).show();
            }
        });
        initEffect();
    }

    private void initEffect() {
        GLEffectLayout effectView = findViewById(R.id.effectView);
        effectView.addEffect(new GLWobbleEffect());
        effectView.enableRefreshMode();
    }

}