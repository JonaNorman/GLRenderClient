package com.jonanorman.android.renderclient.opengl.gl20;


import android.graphics.Bitmap;

import com.jonanorman.android.renderclient.math.MathUtils;
import com.jonanorman.android.renderclient.math.Matrix4;
import com.jonanorman.android.renderclient.opengl.EGLSurface;
import com.jonanorman.android.renderclient.opengl.GLBlend;
import com.jonanorman.android.renderclient.opengl.GLDepthBuffer;
import com.jonanorman.android.renderclient.opengl.GLEnable;
import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLProgram;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLShader;
import com.jonanorman.android.renderclient.opengl.GLShaderParam;
import com.jonanorman.android.renderclient.opengl.GLShaderCache;
import com.jonanorman.android.renderclient.opengl.GLTexture;
import com.jonanorman.android.renderclient.opengl.GLViewPort;
import com.jonanorman.android.renderclient.opengl.GLXfermode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class GL20FrameBuffer extends GLFrameBuffer {

    private final static String KET_DRAW_COLOR_PROGRAM = "gl20_framebuffer_draw_color_program";
    private final static String KET_DRAW_TEXTURE_PROGRAM = "gl20_framebuffer_draw_texture_program";

    private static final float POSITION_COORDINATES[] = {
            -1.0f, -1.0f, 0.0f, 1.0f,//left bottom
            1.0f, -1.0f, 0.0f, 1.0f,//right bottom
            -1.0f, 1.0f, 0.0f, 1.0f, //left top
            1.0f, 1.0f, 0.0f, 1.0f//right top
    };

    private static final float TEXTURE_COORDINATES[] = {
            0.0f, 0.0f, 0.0f, 1.0f,//left bottom
            1.0f, 0.0f, 0.0f, 1.0f,//right bottom
            0.0f, 1.0f, 0.0f, 1.0f,//left top
            1.0f, 1.0f, 0.0f, 1.0f,//right  top
    };

    private static final String COLOR_VERTEX_SHADER = "precision highp float;\n" +
            "attribute vec4 position;\n" +
            "uniform mat4 drawMatrix;\n" +
            "\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = drawMatrix*position;\n" +
            "}";


    private static final String COLOR_FRAGMENT_SHADER = "precision mediump float;\n" +
            "uniform vec4 color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_FragColor = color;\n" +
            "}";


    private static final String TEXTURE_VERTEX_SHADER = "precision highp float;\n" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "uniform mat4 drawMatrix;\n" +
            "uniform mat4 inputTextureMatrix;\n" +
            "varying vec2 textureCoordinate;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position =drawMatrix*position;\n" +
            "    textureCoordinate =(inputTextureMatrix*inputTextureCoordinate).xy;\n" +
            "}";


    private static final String TEXTURE_FRAGMENT_SHADER = "precision mediump float;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform float maxMipmapLevel;\n" +
            "uniform float mipmapLevel;\n" +
            "uniform bool inputTexturePreMul;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    vec4 color  = texture2D(inputImageTexture, textureCoordinate,mipmapLevel);\n" +
            "    if(!inputTexturePreMul) color = vec4(color.rgb*color.a,color.a);\n" +
            "    gl_FragColor = color;\n" +
            "}";

    private static final String KEY_COLOR = "color";
    private static final String KEY_POSITION = "position";
    private static final String KEY_INPUT_TEXTURE_COORDINATE = "inputTextureCoordinate";
    private static final String KEY_DRAW_MATRIX = "drawMatrix";
    private static final String KEY_MAX_MIPMAP_LEVEL = "maxMipmapLevel";
    private static final String KEY_MIPMAP_LEVEL = "mipmapLevel";
    private static final String KEY_INPUT_IMAGE_TEXTURE = "inputImageTexture";
    private static final String KEY_INPUT_TEXTURE_SIZE = "inputTextureSize";
    private static final String KEY_INPUT_TEXTURE_PRE_MUL = "inputTexturePreMul";
    private static final String KEY_INPUT_TEXTURE_MATRIX = "inputTextureMatrix";


    private GL20 gl;
    private GLViewPort viewPort;
    private GLEnable enable;
    private GLBlend blend;

    public GL20FrameBuffer(GLRenderClient client, int width, int height) {
        super(client, width, height);
        gl = getGL();
        init();
    }

    public GL20FrameBuffer(GLRenderClient client, EGLSurface surface) {
        super(client, surface);
        gl = getGL();
    }

    public GL20FrameBuffer(GLRenderClient client, GLTexture texture) {
        super(client, texture);
        gl = getGL();
        init();
    }

    @Override
    protected void onRegisterMethod() {
        super.onRegisterMethod();
        viewPort = new GL20ViewPort(getClient());
        blend = new GL20Blend(getClient());
        enable = new GL20Enable(getClient());
    }

    @Override
    protected void onClassInit() {
        super.onClassInit();

    }

    @Override
    protected int onFrameBufferCreate() {
        return gl.glGenFramebuffer();
    }

    @Override
    protected void onFrameBufferDispose(int id) {
        gl.glDeleteFramebuffer(id);
    }


    @Override
    protected void onClearClear(float red, float green, float blue, float alpha) {
        gl.glClearColor(red, green, blue, alpha);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    protected void onClearDepthBuffer() {
        gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    protected void onAttachColorTexture(GLTexture texture) {
        gl.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, texture.getTextureTarget(), texture.getTextureId(), 0);
    }

    @Override
    protected void onAttachDepthBuffer(GLDepthBuffer depthBuffer) {
        gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER, GL20.GL_DEPTH_ATTACHMENT,
                GL20.GL_RENDERBUFFER, depthBuffer.getBufferId());
    }

    @Override
    protected void onBindFrameBuffer(int frameBufferId) {
        gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, frameBufferId);
    }


    @Override
    protected void onCopyToTexture(int x, int y, int width, int height, GLTexture texture) {
        int frameWidth = getWidth();
        int frameHeight = getHeight();
        if (width <= 0) {
            width = frameWidth;
        }
        if (height <= 0) {
            height = frameHeight;
        }
        if (texture.getWidth() <= 0 || texture.getHeight() <= 0) {
            throw new IllegalArgumentException(this + " copy to texture size is zero");
        }
        if (width != texture.getWidth() || height != texture.getHeight()) {
            texture.setSize(width, height);
        }
        gl.glCopyTexSubImage2D(texture.getTextureTarget(), 0, x, y, 0, 0, texture.getWidth(), texture.getHeight());
    }

    @Override
    protected GLTexture onCreateColorTexture() {
        return new GL20Texture(getClient(), GLTexture.Type.TEXTURE_2D);
    }

    @Override
    protected GLDepthBuffer onCreateDepthBuffer() {
        return new GL20DepthBuffer(getClient());
    }

    @Override
    protected void onReadBitmap(int x, int y, int width, int height, Bitmap bitmap) {
        int frameWidth = getWidth();
        int frameHeight = getHeight();
        if (width <= 0) {
            width = frameWidth;
        }
        if (height <= 0) {
            height = frameHeight;
        }
        int flipY = frameHeight - y - height;
        Bitmap.Config config = bitmap.getConfig();
        int span;
        ByteBuffer buf;
        if (config == Bitmap.Config.ARGB_8888) {
            span = width * 4;
            buf = ByteBuffer.allocateDirect(span * height);
            buf.order(ByteOrder.nativeOrder());
            gl.glReadPixels(x, flipY, width, height,
                    GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, buf);
        } else if (config
                == Bitmap.Config.RGB_565) {
            span = width * 2;
            buf = ByteBuffer.allocateDirect(span * height);
            buf.order(ByteOrder.nativeOrder());
            gl.glReadPixels(x, flipY, width, height,
                    GL20.GL_RGB, GL20.GL_UNSIGNED_SHORT_5_6_5, buf);
        } else if (config
                == Bitmap.Config.ARGB_4444) {
            span = width * 2;
            buf = ByteBuffer.allocateDirect(span * height);
            buf.order(ByteOrder.nativeOrder());
            gl.glReadPixels(x, flipY, width, height,
                    GL20.GL_RGBA, GL20.GL_UNSIGNED_SHORT_4_4_4_4, buf);
        } else if (config
                == Bitmap.Config.ALPHA_8) {
            span = width;
            buf = ByteBuffer.allocateDirect(span * height);
            buf.order(ByteOrder.nativeOrder());
            // read 14ms
            gl.glReadPixels(x, flipY, width, height,
                    GL20.GL_ALPHA, GL20.GL_UNSIGNED_BYTE, buf);
            span = width;
        } else {
            throw new RuntimeException(this + " not support bitmap config " + config);
        }
        int i = 0;
        byte[] tmp = new byte[span];
        // flip 2ms
        while (i++ < height / 2) {
            buf.get(tmp);
            System.arraycopy(buf.array(), buf.limit() - buf.position(), buf.array(), buf.position() - span, span);
            System.arraycopy(tmp, 0, buf.array(), buf.limit() - buf.position(), span);
        }
        buf.rewind();
        if (width != bitmap.getWidth() || height != bitmap.getHeight()) {
            bitmap.reconfigure(width, height, bitmap.getConfig());
        }
        bitmap.copyPixelsFromBuffer(buf);
    }

    @Override
    protected void onDrawColor(Matrix4 matrix, GLXfermode xfermode, float red, float green, float blue, float alpha) {
        GLProgram program = getDrawColorProgram();
        GLFrameBuffer old = bind();
        enable.apply();
        viewPort.setX(0);
        viewPort.setY(0);
        viewPort.setWidth(getWidth());
        viewPort.setHeight(getHeight());
        viewPort.apply();
        xfermode.apply(blend);
        GLShaderParam programParam = program.getShaderParam();
        programParam.clear();
        programParam.set(KEY_COLOR, red, green, blue, alpha);
        programParam.set(KEY_POSITION, POSITION_COORDINATES);
        programParam.set(KEY_INPUT_TEXTURE_COORDINATE, TEXTURE_COORDINATES);
        programParam.set(KEY_DRAW_MATRIX, matrix);
        program.execute();
        old.bind();
    }

    @Override
    protected void onDrawTexture(Matrix4 matrix, GLXfermode xfermode, GLTexture texture) {
        GLProgram program = getDrawTextureProgram();
        GLFrameBuffer old = bind();
        enable.apply();
        viewPort.setX(0);
        viewPort.setY(0);
        viewPort.setWidth(getWidth());
        viewPort.setHeight(getHeight());
        viewPort.apply();
        xfermode.apply(blend);
        GLShaderParam programParam = program.getShaderParam();
        programParam.clear();
        programParam.set(KEY_POSITION, POSITION_COORDINATES);
        programParam.set(KEY_INPUT_TEXTURE_COORDINATE, TEXTURE_COORDINATES);
        programParam.set(KEY_DRAW_MATRIX, matrix);
        int textureWidth = texture.getWidth();
        int textureHeight = texture.getHeight();
        float renderWidth = getWidth();
        float renderHeight = getHeight();
        int maxMipmapLevel = Math.max(texture.getMaxMipmapLevel(), 0);
        float mipMapLevel = MathUtils.clamp(
                Math.max(renderWidth / textureWidth,
                        renderHeight / textureHeight) - 1.0f,
                0.0f, maxMipmapLevel);
        programParam.set(KEY_MAX_MIPMAP_LEVEL, maxMipmapLevel);
        programParam.set(KEY_MIPMAP_LEVEL, mipMapLevel);
        programParam.set(KEY_INPUT_IMAGE_TEXTURE, texture.getTextureId());
        programParam.set(KEY_INPUT_TEXTURE_SIZE, textureWidth, textureHeight);
        programParam.set(KEY_INPUT_TEXTURE_PRE_MUL, texture.isPremultiplied());
        programParam.set(KEY_INPUT_TEXTURE_MATRIX, texture.getTextureMatrix());
        program.execute();
        old.bind();
    }


    @Override
    public String toString() {
        return "GL20FrameBuffer[" +
                "width=" + getWidth() +
                ", height=" + getHeight() +
                ", frameBufferId=" + getFrameBufferId() +
                ']';
    }


    private GLProgram getDrawColorProgram() {
        GLRenderClient client = getClient();
        GLProgram program = client.getExtraParam(KET_DRAW_COLOR_PROGRAM);
        if (program == null) {
            program = new GL20Program(client);
            GLShaderCache shaderPool = GL20ShadeCache.getCache(client);
            GLShader vertexShader = shaderPool.get(GLShader.Type.VERTEX, COLOR_VERTEX_SHADER);
            GLShader fragmentShader = shaderPool.get(GLShader.Type.FRAGMENT, COLOR_FRAGMENT_SHADER);
            program.setVertexShader(vertexShader);
            program.setFragmentShader(fragmentShader);
            client.putExtraParam(KET_DRAW_COLOR_PROGRAM,program);
        }
        return program;
    }

    private GLProgram getDrawTextureProgram() {
        GLRenderClient client = getClient();
        GLProgram program = client.getExtraParam(KET_DRAW_TEXTURE_PROGRAM);
        if (program == null) {
            program = new GL20Program(client);
            GLShaderCache shaderPool = GL20ShadeCache.getCache(client);
            GLShader vertexShader = shaderPool.get(GLShader.Type.VERTEX, TEXTURE_VERTEX_SHADER);
            GLShader fragmentShader = shaderPool.get(GLShader.Type.FRAGMENT, TEXTURE_FRAGMENT_SHADER);
            program.setVertexShader(vertexShader);
            program.setFragmentShader(fragmentShader);
            client.putExtraParam(KET_DRAW_TEXTURE_PROGRAM,program);
        }
        return program;
    }
}
