package com.jonanorman.android.renderclient.effect;

import com.jonanorman.android.renderclient.layer.GLShaderEffect;
import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLShaderParam;

public class GLWobbleEffect extends GLShaderEffect {

    private static final String FRAGMENT_SHADER = "precision mediump float;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform vec2 inputTextureSize;\n" +
            "uniform vec2 viewPortSize;\n" +
            "uniform   float renderTime;\n" +
            "uniform   float renderDuration;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    vec2 texcoord = textureCoordinate;\n" +
            "    texcoord.x += sin(texcoord.y * 4.0 * 2.0 * 3.14159 + mod(renderTime,20.0) * 2.0 * 3.14159 * 0.75) / 50.0;\n" +
            "    gl_FragColor = texture2D(inputImageTexture, texcoord);\n" +
            "}";


    public GLWobbleEffect() {
        super();
        setFragmentShaderCode(FRAGMENT_SHADER);
    }
}
