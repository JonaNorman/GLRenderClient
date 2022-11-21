package com.jonanorman.android.renderclient.effect;


import com.jonanorman.android.renderclient.layer.GLEffectGroup;
import com.jonanorman.android.renderclient.layer.GLShaderEffect;
import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLShaderParam;


public class GLAlphaSdfEffect extends GLEffectGroup {

    private final static String HORIZONTAL_FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "varying highp vec2 textureCoordinate;\n" +
                    "uniform sampler2D inputImageTexture;\n" +
                    "uniform vec2 inputTextureSize;\n" +
                    "uniform int distance;\n" +
                    "#define D float(distance)\n" +
                    "#define textureStep  vec2(1.0/inputTextureSize.x,.0/inputTextureSize.y)\n" +
                    "\n" +
                    "\n" +
                    "float source(vec2 uv)\n" +
                    "{\n" +
                    "    return texture2D(inputImageTexture,uv).a-0.5; \n" +
                    "}\n" +
                    "float normalized(float d)\n" +
                    "{\n" +
                    "    return d/D*0.5+0.5; \n" +
                    "}\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "    vec2 uv = textureCoordinate;\n" +
                    "    float s = sign(source(uv));\n" +
                    "    float d = 0.;   \n" +
                    "    for(int i= 0; i < distance; i++){\n" +
                    "        d++;\n" +
                    "        vec2 offset =  vec2(d * textureStep.x, 0.);\n" +
                    "        if(s * source(uv + offset) < 0.)break;\n" +
                    "        if(s * source(uv - offset) < 0.)break; \n" +
                    "    }\n" +
                    "\n" +
                    "    float sd = s*d;\n" +
                    "    float dMin = normalized(sd);\n" +
                    "    gl_FragColor =vec4(vec3(dMin),1.0);\n" +
                    "}";

    private final static String VERTICAL_FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "varying  highp vec2 textureCoordinate;\n" +
                    "uniform sampler2D inputImageTexture;\n" +
                    "uniform vec2 inputTextureSize;\n" +
                    "uniform int distance;\n" +
                    "#define D float(distance)\n" +
                    "#define textureStep  vec2(1.0/inputTextureSize.x,1.0/inputTextureSize.y)\n" +
                    "\n" +
                    "\n" +
                    "float reverseNormalized(float sd)\n" +
                    "{\n" +
                    "    return (sd-0.5)*2.0*D;\n" +
                    "}\n" +
                    "\n" +
                    "float sd(vec2 uv)\n" +
                    "{\n" +
                    "    return reverseNormalized(texture2D(inputImageTexture, uv).x);\n" +
                    "}\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "    vec2 uv = textureCoordinate;\n" +
                    "    float dx = sd(uv);\n" +
                    "    float dMin = abs(dx);\n" +
                    "    float dy = 0.;\n" +
                    "    for(int i= 0; i < distance; i++){\n" +
                    "        dy += 1.;\n" +
                    "        vec2 offset =  vec2(0., dy * textureStep.y);\n" +
                    "        float dx1 = sd(uv + offset);\n" +
                    "        if(dx1 * dx < 0.){\n" +
                    "            dMin = dy;\n" +
                    "            break;\n" +
                    "        }\n" +
                    "        dMin = min(dMin, length (vec2(dx1, dy)));\n" +
                    "        float dx2 = sd(uv - offset);\n" +
                    "        if(dx2 * dx < 0.){\n" +
                    "            dMin = dy;\n" +
                    "                break;\n" +
                    "        }\n" +
                    "        dMin = min(dMin, length (vec2(dx2, dy)));\n" +
                    "        if(dy > dMin)break;\n" +
                    "        }\n" +
                    "\n" +
                    "        dMin *= sign(dx);\n" +
                    "        float d = dMin/D;\n" +
                    "        \n" +
                    "        d =d*0.5+0.5;\n" +
                    "        gl_FragColor =vec4(vec3(d),1.0);\n" +
                    "}\n" +
                    " ";

    private GLShaderEffect horizontalFilter = new GLShaderEffect();
    private GLShaderEffect verticalFilter = new GLShaderEffect();

    private int distance;


    public GLAlphaSdfEffect() {
        super();
        horizontalFilter.setFragmentShaderCode(HORIZONTAL_FRAGMENT_SHADER);
        verticalFilter.setFragmentShaderCode(VERTICAL_FRAGMENT_SHADER);
        add(horizontalFilter);
        add(verticalFilter);
    }


    @Override
    protected GLFrameBuffer onRenderEffect(GLRenderClient client, GLFrameBuffer inputBuffer) {
        if (distance <= 0) {
            return inputBuffer;
        }
        return super.onRenderEffect(client, inputBuffer);
    }

    public void setDistance(int distance) {
        GLShaderParam shaderParam = horizontalFilter.getShaderParam();
        shaderParam.set("distance", distance);
        shaderParam = verticalFilter.getShaderParam();
        shaderParam.set("distance", distance);
        this.distance = distance;
    }


}
