package com.jonanorman.android.renderclient.effect;


import android.graphics.Color;

import com.jonanorman.android.renderclient.layer.GLEffectGroup;
import com.jonanorman.android.renderclient.layer.GLShaderEffect;
import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLShaderParam;

public class GLAlphaOutlineEffect extends GLEffectGroup {


    private GLAlphaSdfEffect alphaSdfFilter;
    private GLOutLineShader outlineEffect;
    private GLFrameBuffer originalBuffer;
    private int outlineColor = Color.TRANSPARENT;
    private int maxOutlineWidth;
    private float intensity;
    private int outlineStyle;


    public GLAlphaOutlineEffect() {
        super();
        alphaSdfFilter = new GLAlphaSdfEffect();
        outlineEffect = new GLOutLineShader();
        add(alphaSdfFilter);
        add(outlineEffect);
    }

    public void setMaxOutlineWidth(int distance) {
        alphaSdfFilter.setDistance(distance);
        outlineEffect.setMaxOutlineWidth(distance);
        this.maxOutlineWidth = distance;
    }


    public void setOutlineColor(int outlineColor) {
        float outlineAlpha = Color.alpha(outlineColor) / 255.0f;
        float outlineRed = Color.red(outlineColor) / 255.0f * outlineAlpha;
        float outlineGreen = Color.green(outlineColor) / 255.0f * outlineAlpha;
        float outlineBlue = Color.blue(outlineColor) / 255.0f * outlineAlpha;
        outlineEffect.setShaderParam("outlineColor", outlineRed, outlineGreen, outlineBlue, outlineAlpha);
        this.outlineColor = outlineColor;
    }

    public void setIntensity(float width) {
        outlineEffect.setOutlineWidth(width);
        this.intensity = width;
    }


    public void setOutlineStyle(int outlineStyle) {
        outlineEffect.setShaderParam("outlineStyle", outlineStyle);
        this.outlineStyle = outlineStyle;
    }


    @Override
    protected GLFrameBuffer onRenderEffect(GLRenderClient client, GLFrameBuffer inputBuffer) {
        if (outlineColor == Color.TRANSPARENT || intensity <= 0 || maxOutlineWidth <= 0 || outlineStyle <= 0) {
            return inputBuffer;
        }
        originalBuffer = inputBuffer;
        return super.onRenderEffect(client, inputBuffer);
    }


    class GLOutLineShader extends GLShaderEffect {
        private final String STROKE_FRAGMENT_SHADER = "precision mediump float;\n" +
                "varying highp vec2 textureCoordinate;\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "uniform sampler2D originalImageTexture;\n" +
                "uniform vec2 inputTextureSize;\n" +
                "#define textureWidth inputTextureSize.x\n" +
                "#define textureHeight inputTextureSize.y\n" +
                "uniform  float outlineWidth;\n" +
                "uniform  vec4 outlineColor;\n" +
                "uniform float outlineStyle;\n" +
                "uniform int maxOutlineWidth;\n" +
                "#define minAlphaStep 1.0/float(maxOutlineWidth)/3.0\n" +
                "\n" +
                "\n" +
                "float getOutlineMask(){\n" +
                "    float sampledAlpha = texture2D(inputImageTexture, textureCoordinate).r;\n" +
                "    sampledAlpha = (sampledAlpha-minAlphaStep)/(1.0-minAlphaStep);\n" +
                "    sampledAlpha = clamp(sampledAlpha,0.0,1.0);\n" +
                "    float b =   clamp(outlineWidth, 0.0, 0.99);\n" +
                "    if(outlineStyle ==1.0){\n" +
                "        b = step((1.0-sampledAlpha),b);\n" +
                "    }else if(outlineStyle == 2.0){\n" +
                "       b = step(abs(sampledAlpha-0.5)*2.0,b*0.8);\n" +
                "    }else if(outlineStyle == 3.0){\n" +
                "        b = texture2D(originalImageTexture, textureCoordinate+vec2(outlineWidth*float(maxOutlineWidth)/textureWidth*1.0,0.0)).a;\n" +
                "    }else if(outlineStyle == 4.0){\n" +
                "        b = smoothstep(1.0-b,1.0,sampledAlpha);\n" +
                "    }else if(outlineStyle == 5.0){\n" +
                "        b = texture2D(originalImageTexture, textureCoordinate-vec2(outlineWidth*float(maxOutlineWidth)/textureWidth*1.0,0.0)).a;\n" +
                "    }else{\n" +
                "        b = 0.0;\n" +
                "    }\n" +
                "    return b;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    vec4 textureColor = texture2D(originalImageTexture, textureCoordinate);\n" +
                "    float outlineAlpha = getOutlineMask();\n" +
                "    gl_FragColor =textureColor+(1.0-textureColor.a)*outlineAlpha*outlineColor;;\n" +
                "\n" +
                "}";

        public GLOutLineShader() {
            super();
            setFragmentShaderCode(STROKE_FRAGMENT_SHADER);
        }

        @Override
        protected void onRenderShaderEffect(GLFrameBuffer input, GLShaderParam shaderParam) {
            super.onRenderShaderEffect(input, shaderParam);
            shaderParam.set("originalImageTexture", originalBuffer.getAttachColorTexture());
        }

        public void setMaxOutlineWidth(int maxOutlineWidth) {
            setShaderParam("maxOutlineWidth", maxOutlineWidth);
        }

        public void setOutlineWidth(float width) {
            setShaderParam("outlineWidth", width);
        }
    }


}
