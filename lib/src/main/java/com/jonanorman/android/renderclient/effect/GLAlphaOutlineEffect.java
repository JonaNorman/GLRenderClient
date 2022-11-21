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
                "#define OUT_LINE_STEP float(maxOutlineWidth)/textureWidth\n" +

                "float sd(vec2 uv)\n" +
                "{\n" +
                "    float x =  texture2D(inputImageTexture, uv).x;\n" +
                "    float d = -2.0*x+1.0;\n" +
                "    if(d>=1.0)return d+0.001;\n" +
                "    return d;\n" +
                "}\n" +

                "\n" +
                "\n" +
                "float getOutlineMask(){\n" +
                "    float d = sd(textureCoordinate);\n" +
                "    float b =   outlineWidth;\n" +
                "    if(outlineStyle ==1.0){\n" +
                "       return step(d,b);\n" +
                "    }else if(outlineStyle == 2.0){\n" +
                "        return  step(abs(d-0.5)*2.0,b*0.5);\n" +
                "    }else if(outlineStyle == 3.0){\n" +
                "         return  smoothstep(0.0,1.0,1.0-clamp(d/b,0.0,1.0));\n" +
                "    }else if(outlineStyle == 4.0){\n" +
                "         return texture2D(originalImageTexture, textureCoordinate+vec2(outlineWidth*OUT_LINE_STEP,0.0)).a;\n" +
                "    }else if(outlineStyle == 5.0){\n" +
                "        return  texture2D(originalImageTexture, textureCoordinate-vec2(outlineWidth*OUT_LINE_STEP,0.0)).a;\n" +
                "    }else{\n" +
                "        return  0.0;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    vec4 textureColor = texture2D(originalImageTexture, textureCoordinate);\n" +
                "    float outlineAlpha = getOutlineMask();\n" +
                "    gl_FragColor =textureColor+(1.0-textureColor.a)*outlineAlpha*outlineColor;\n" +
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
