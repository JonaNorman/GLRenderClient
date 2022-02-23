package com.byteplay.android.renderclient;

import android.graphics.Color;

public class GLColorLayer extends GLLayer {



    private static final String VERTEX_SHADER = "precision highp float;\n" +
            "attribute vec4 position;\n" +
            "\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "}";


    private static final String FRAGMENT_SHADER = "precision mediump float;\n" +
            "uniform vec2 viewPortSize;\n" +
            "uniform float renderTime;\n" +
            "uniform vec4 color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_FragColor = color;\n" +
            "}";

    private int color = Color.TRANSPARENT;
    private float redColor;
    private float greenColor;
    private float blueColor;
    private float alphaColor;

    protected GLColorLayer(GLRenderClient client) {
        super(client, VERTEX_SHADER, FRAGMENT_SHADER, client.newDrawArray());
    }


    public void setColor(int color) {
        alphaColor = Color.alpha(color) / 255.0f;
        redColor = Color.red(color) / 255.0f * alphaColor;
        greenColor = Color.green(color) / 255.0f * alphaColor;
        blueColor = Color.blue(color) / 255.0f * alphaColor;//use premult color
        this.color = color;
    }

    @Override
    protected boolean onRenderLayer(GLLayer layer, long renderTimeMs) {
        super.onRenderLayer(layer, renderTimeMs);
        GLShaderParam shaderParam = layer.getDefaultShaderParam();
        shaderParam.put("color", redColor, greenColor, blueColor, alphaColor);
        return true;
    }

    public int getColor() {
        return color;
    }
}
