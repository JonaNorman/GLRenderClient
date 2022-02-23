package com.byteplay.android.renderclient;


import android.graphics.Bitmap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

class GL20FrameBuffer extends GLFrameBuffer {


    private int frameBufferId;
    private final boolean defaultFrameBuffer;

    private final GL20 gl;
    private int initWidth;
    private int initHeight;
    private GLTexture texture;
    private final GLRenderSurface renderSurface;

    public GL20FrameBuffer(GLRenderClient client, int width, int height) {
        super(client);
        this.defaultFrameBuffer = false;
        this.initWidth = width;
        this.initHeight = height;
        gl = client.getGL20();
        renderSurface = client.getDefaultPBufferSurface();
    }

    public GL20FrameBuffer(GLRenderClient client, GLTexture texture) {
        super(client);
        this.defaultFrameBuffer = false;
        gl = client.getGL20();
        renderSurface = client.getDefaultPBufferSurface();
        this.texture = texture;
    }

    public GL20FrameBuffer(GLRenderClient client, GLRenderSurface surface) {
        super(client);
        defaultFrameBuffer = true;
        renderSurface = surface;
        gl = client.getGL20();
    }


    @Override
    protected void onCreate() {
        if (!defaultFrameBuffer) {
            frameBufferId = gl.glGenFramebuffer();
            if (frameBufferId <= 0) {
                throw new IllegalStateException("frameBuffer generate fail");
            }
            if (texture == null) {
                texture = client.newTexture(GLTextureType.TEXTURE_2D);
                texture.setTextureSize(initWidth, initHeight);
            }
            attachColorTexture(texture);
        }
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
    }

    @Override
    public GLRenderSurface getRenderSurface() {
        return renderSurface;
    }

    @Override
    public int getWidth() {
        return defaultFrameBuffer ? renderSurface.getWidth() : getColorTexture().getWidth();
    }

    @Override
    public int getHeight() {
        return defaultFrameBuffer ? renderSurface.getHeight() : getColorTexture().getHeight();
    }

    @Override
    protected void onDispose() {
        if (!defaultFrameBuffer) {
            gl.glDeleteFramebuffer(frameBufferId);
        }
        if (initWidth > 0 && initHeight > 0) {
            texture.dispose();
        }
    }

    @Override
    public int getFrameBufferId() {
        return frameBufferId;
    }


    @Override
    protected void onClearClear(float red, float green, float blue, float alpha) {
        gl.glClearColor(red, green, blue, alpha);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    protected void onAttachColorTexture(GLTexture texture) {
        gl.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, texture.getTarget(), texture.getTextureId(), 0);
    }

    @Override
    protected void onBind() {
        renderSurface.makeCurrent();
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
            throw new IllegalArgumentException("copy to texture size is zero");
        }
        if (width != texture.getWidth() || height != texture.getHeight()) {
            texture.setTextureSize(width, height);
        }
        gl.glActiveTexture(GL20.GL_TEXTURE0);
        gl.glBindTexture(texture.getTarget(), texture.getTextureId());
        gl.glCopyTexSubImage2D(texture.getTarget(), 0, x, y, 0, 0, texture.getWidth(), texture.getHeight());
        gl.glBindTexture(texture.getTarget(), 0);
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
            throw new RuntimeException("not support bitmap config " + config);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GL20FrameBuffer)) return false;
        GL20FrameBuffer that = (GL20FrameBuffer) o;
        return frameBufferId == that.frameBufferId && defaultFrameBuffer == that.defaultFrameBuffer && initWidth == that.initWidth && initHeight == that.initHeight && Objects.equals(gl, that.gl) && Objects.equals(texture, that.texture) && Objects.equals(renderSurface, that.renderSurface);
    }

    @Override
    public int hashCode() {
        return Objects.hash(frameBufferId, defaultFrameBuffer, gl, initWidth, initHeight, texture, renderSurface);
    }
}
