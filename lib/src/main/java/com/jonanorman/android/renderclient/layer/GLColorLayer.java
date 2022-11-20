package com.jonanorman.android.renderclient.layer;

import android.animation.TypeEvaluator;
import android.graphics.Color;

import com.jonanorman.android.renderclient.math.KeyframeSet;
import com.jonanorman.android.renderclient.math.TimeStamp;
import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLShaderParam;

public class GLColorLayer extends GLShaderLayer {


    private static final String FRAGMENT_SHADER = "precision mediump float;\n" +
            "uniform vec2 viewPortSize;\n" +
            "uniform float renderTime;\n" +
            "uniform float renderDuration;\n" +
            "uniform vec4 color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_FragColor = color;\n" +
            "}";

    public static final String KEY_COLOR = "color";

    private int color = Color.TRANSPARENT;
    private float redColor;
    private float greenColor;
    private float blueColor;
    private float alphaColor;

    public GLColorLayer() {
        super();
        setFragmentShaderCode(FRAGMENT_SHADER);
    }


    public void setColor(int color) {
        alphaColor = Color.alpha(color) / 255.0f;
        redColor = Color.red(color) / 255.0f * alphaColor;
        greenColor = Color.green(color) / 255.0f * alphaColor;
        blueColor = Color.blue(color) / 255.0f * alphaColor;
        this.color = color;
    }


    @Override
    protected void onRenderLayerParam(GLFrameBuffer inputBuffer, GLShaderParam shaderParam) {
        super.onRenderLayerParam(inputBuffer, shaderParam);
        shaderParam.set(KEY_COLOR, redColor, greenColor, blueColor, alphaColor);
    }


    public int getColor() {
        return color;
    }


    public static KeyframeSet ofColor(TimeStamp duration, int... colors) {
        float[][] shaderColorArr = new float[colors.length][4];
        for (int i = 0; i < colors.length; i++) {
            float alpha = Color.alpha(colors[i]) / 255.0f;
            shaderColorArr[i][0] = Color.red(colors[i]) / 255.0f * alpha;
            shaderColorArr[i][1] = Color.green(colors[i]) / 255.0f * alpha;
            shaderColorArr[i][2] = Color.blue(colors[i]) / 255.0f * alpha;
            shaderColorArr[i][3] = alpha;
        }
        return KeyframeSet.ofFloatArray(duration, shaderColorArr);
    }

    public static KeyframeSet ofColor(int... colors) {
        float[][] shaderColorArr = new float[colors.length][4];
        for (int i = 0; i < colors.length; i++) {
            float alpha = Color.alpha(colors[i]) / 255.0f;
            shaderColorArr[i][0] = Color.red(colors[i]) / 255.0f * alpha;
            shaderColorArr[i][1] = Color.green(colors[i]) / 255.0f * alpha;
            shaderColorArr[i][2] = Color.blue(colors[i]) / 255.0f * alpha;
            shaderColorArr[i][3] = alpha;
        }
        return KeyframeSet.ofFloatArray(shaderColorArr);
    }


    public static class ColorEvaluator implements TypeEvaluator<float[]> {
        float[] floats = new float[4];

        @Override
        public float[] evaluate(float fraction, float[] startValue, float[] endValue) {
            float startR = startValue[0];
            float startG = startValue[1];
            float startB = startValue[2];
            float startA = startValue[3];

            float endR = endValue[0];
            float endG = endValue[1];
            float endB = endValue[2];
            float endA = endValue[3];

            // convert from sRGB to linear
            startR = (float) Math.pow(startR, 2.2);
            startG = (float) Math.pow(startG, 2.2);
            startB = (float) Math.pow(startB, 2.2);

            endR = (float) Math.pow(endR, 2.2);
            endG = (float) Math.pow(endG, 2.2);
            endB = (float) Math.pow(endB, 2.2);

            // compute the interpolated color in linear space
            float a = startA + fraction * (endA - startA);
            float r = startR + fraction * (endR - startR);
            float g = startG + fraction * (endG - startG);
            float b = startB + fraction * (endB - startB);

            // convert back to sRGB in the [0..255] range
            r = (float) Math.pow(r, 1.0 / 2.2);
            g = (float) Math.pow(g, 1.0 / 2.2);
            b = (float) Math.pow(b, 1.0 / 2.2);

            floats[0] = r;
            floats[1] = g;
            floats[2] = b;
            floats[3] = a;
            return floats;
        }
    }
}
