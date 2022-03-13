package com.jonanorman.android.renderclient;

import com.jonanorman.android.renderclient.utils.MathUtils;

public class GLGaussianBlurEffect extends GLEffectGroup {

    private GLEffectGroup effectList;
    private GLGaussianDirectionEffect verticalEffect;
    private GLGaussianDirectionEffect horizontalEffect;


    public GLGaussianBlurEffect(GLRenderClient client) {
        super(client);
        effectList = new GLEffectGroup(client);
        verticalEffect = new GLGaussianDirectionEffect(client, GLGaussianDirectionEffect.DIRECTION_VERTICAL);
        horizontalEffect = new GLGaussianDirectionEffect(client, GLGaussianDirectionEffect.DIRECTION_HORIZONTAL);
        effectList.addEffect(verticalEffect);
        effectList.addEffect(horizontalEffect);
        setBlurRadius(10);
        setBlurSigma(0.5f);
    }


    public void setBlurStep(float blurStep) {
        verticalEffect.setBlurStep(blurStep);
        horizontalEffect.setBlurStep(blurStep);
    }

    public void setBlurRadius(int radius) {
        verticalEffect.setBlurRadius(radius);
        horizontalEffect.setBlurRadius(radius);
    }

    public void setBlurSigma(float blurSigma) {
        verticalEffect.setBlurSigma(blurSigma);
        horizontalEffect.setBlurSigma(blurSigma);
    }

    @Override
    protected void calculateEffect(long parentEffectTimeMs, long parentDurationMs) {
        super.calculateEffect(parentEffectTimeMs, parentDurationMs);
        effectList.calculateEffect(getRenderTime(), getRenderDuration());
        if (effectList.isRenderEnable()) {
            setRenderEnable(true);
        }
    }

    @Override
    protected GLFrameBuffer renderEffect(GLFrameBuffer input) {
        if (!isRenderEnable()) {
            return input;
        }
        GLFrameBuffer effectBuffer = effectList.renderEffect(input);
        return super.renderEffect(effectBuffer);
    }

    static class GLGaussianDirectionEffect extends GLShaderEffect {

        private static final String FRAGMENT_CODE = "precision highp float;\n" +
                "varying  vec2 textureCoordinate;\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "uniform  float blurWeight[101];\n" +
                "uniform  vec2 directionOffset;\n" +
                "uniform  int blurRadius;\n" +
                "uniform  bool blurEnable;\n" +
                "uniform vec2 inputTextureSize;\n" +
                "uniform vec2 viewPortSize;\n" +
                "uniform float renderTime;\n" +
                "\n" +
                "\n" +
                "vec4 texture2DOffset(vec2 offset){\n" +
                "    vec2 uv = textureCoordinate+offset;\n" +
                "    vec4 color  = texture2D(inputImageTexture, uv);\n" +
                "    return color;\n" +
                "}\n" +
                "\n" +
                "vec4 gamma(vec4 color){\n" +
                "    color.rgb = pow(color.rgb, vec3(1.0/2.2));\n" +
                "    return color;\n" +
                "}\n" +
                "\n" +
                "vec4 inverseGamma(vec4 color){\n" +
                "    color.rgb = pow(color.rgb, vec3(2.2));\n" +
                "    return color;\n" +
                "}\n" +
                "\n" +
                "vec4 straight(vec4 color){\n" +
                "    if (color.a != 0.0){\n" +
                "        return vec4(color.rgb/color.a, color.a);\n" +
                "    }\n" +
                "    return vec4(0.0);\n" +
                "}\n" +
                "\n" +
                "vec4 premult(vec4 color){\n" +
                "    return vec4(color.rgb*color.a,color.a);\n" +
                "}\n" +
                "\n" +
                "void main() {\n" +
                "    vec4 color = texture2D(inputImageTexture, textureCoordinate);\n" +
                "    if (!blurEnable){\n" +
                "        gl_FragColor = color;\n" +
                "    } else {\n" +
                "        vec4 sum = inverseGamma(color)*blurWeight[0];\n" +
                "        for (int i = 1; i <= blurRadius; i++) {\n" +
                "            vec4 offsetColor =  texture2DOffset(directionOffset*vec2(i, i));\n" +
                "            offsetColor = inverseGamma(offsetColor)*blurWeight[i];\n" +
                "            sum +=offsetColor;\n" +
                "            offsetColor =  texture2DOffset(-directionOffset*vec2(i, i));\n" +
                "            offsetColor = inverseGamma(offsetColor)*blurWeight[i];\n" +
                "            sum += offsetColor;\n" +
                "        }\n" +
                "        vec4 gammaColor = gamma(sum);\n" +
                "        gammaColor = straight(gammaColor);\n" +
                "        gammaColor = premult(gammaColor);\n" +
                "        gl_FragColor = gammaColor;\n" +
                "    }\n" +
                "}";
        public final static int DIRECTION_VERTICAL = 1;
        public final static int DIRECTION_HORIZONTAL = 2;


        private static final double RECIPROCAL_SQRT_TOW_PI = 1.0 / Math.sqrt(Math.PI * 2);
        private final static int MAX_RADIUS = 100;
        private int blurRadius;
        private float[] direction = new float[2];

        private double[] tempWeights = new double[MAX_RADIUS + 1];
        private float[] blurWeight = new float[MAX_RADIUS + 1];
        private float blurStep = 1.0f;
        private float blurSigma = 0.1f;
        private float oldBlurSigma;
        private float oldBlurRadius;
        private boolean blurEnable;


        protected GLGaussianDirectionEffect(GLRenderClient client, int directionType) {
            super(client);
            setFragmentShaderCode(FRAGMENT_CODE);
            if (directionType == DIRECTION_HORIZONTAL) {
                direction[0] = 1;
                direction[1] = 0;
            } else if (directionType == DIRECTION_VERTICAL) {
                direction[0] = 0;
                direction[1] = 1;
            }
        }

        @Override
        protected void onRenderShaderEffect(GLFrameBuffer input) {
            super.onRenderShaderEffect(input);
            calculateBlurWeight(blurRadius, blurSigma);
            GLShaderParam shaderParam = getDefaultShaderParam();
            shaderParam.put("blurEnable", blurEnable);
            shaderParam.put("blurRadius", blurRadius);
            shaderParam.put("blurWeight", blurWeight);
            shaderParam.put("directionOffset", blurStep * direction[0] / input.getWidth(), blurStep * direction[1] / input.getHeight());
        }

        public void setBlurRadius(int radius) {
            blurRadius = MathUtils.clamp(radius, 0, MAX_RADIUS);
        }

        public void setBlurStep(float blurStep) {
            this.blurStep = blurStep;
        }

        public void setBlurSigma(float blurSigma) {
            this.blurSigma = blurSigma;
        }

        private void calculateBlurWeight(float blurRadius, float blurSigma) {
            if (this.oldBlurRadius == blurRadius && oldBlurSigma == blurSigma) {
                return;
            }
            this.oldBlurRadius = blurRadius;
            this.oldBlurSigma = blurSigma;
            if (blurRadius <= 0 || blurSigma == 0) {
                blurWeight[0] = 1;
                for (int x = 1; x <= blurRadius; x++) {
                    blurWeight[x] = 0;
                }
                blurEnable = false;
            } else {
                blurEnable = true;
                float sigma = blurSigma * blurRadius;
                double sumWeight = 0;
                for (int x = 0; x <= blurRadius; x++) {
                    double weight = RECIPROCAL_SQRT_TOW_PI / sigma * Math.exp(-Math.pow(x, 2.0) / 2.0 / Math.pow(sigma, 2.0));
                    tempWeights[x] = weight;
                    sumWeight += weight;
                }
                sumWeight = sumWeight * 2 - tempWeights[0];
                for (int x = 0; x <= blurRadius; ++x) {
                    blurWeight[x] = (float) (tempWeights[x] / sumWeight);
                }
            }

        }
    }
}
