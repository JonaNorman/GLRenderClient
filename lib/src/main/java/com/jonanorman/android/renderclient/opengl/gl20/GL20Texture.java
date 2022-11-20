package com.jonanorman.android.renderclient.opengl.gl20;


import android.graphics.Bitmap;
import android.opengl.GLUtils;

import com.jonanorman.android.renderclient.math.MathUtils;
import com.jonanorman.android.renderclient.math.Matrix4;
import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLFrameBufferCache;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLTexture;
import com.jonanorman.android.renderclient.opengl.GLXfermode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class GL20Texture extends GLTexture {
    private static Map<Object, Integer> GL_ENUM_MAP = new HashMap<>();

    static {
        GL_ENUM_MAP.put(FilterMode.NEAREST, GL20.GL_NEAREST);
        GL_ENUM_MAP.put(FilterMode.LINEAR, GL20.GL_LINEAR);
        GL_ENUM_MAP.put(FilterMode.NEAREST_MIPMAP_NEAREST, GL20.GL_NEAREST_MIPMAP_NEAREST);
        GL_ENUM_MAP.put(FilterMode.LINEAR_MIPMAP_NEAREST, GL20.GL_LINEAR_MIPMAP_NEAREST);
        GL_ENUM_MAP.put(FilterMode.NEAREST_MIPMAP_LINEAR, GL20.GL_NEAREST_MIPMAP_LINEAR);
        GL_ENUM_MAP.put(FilterMode.LINEAR_MIPMAP_LINEAR, GL20.GL_LINEAR_MIPMAP_LINEAR);
        GL_ENUM_MAP.put(Type.TEXTURE_2D, GL20.GL_TEXTURE_2D);
        GL_ENUM_MAP.put(Type.TEXTURE_OES, GL20.GL_TEXTURE_EXTERNAL_OES);
        GL_ENUM_MAP.put(Type.TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_CUBE_MAP);
        GL_ENUM_MAP.put(WrapMode.MIRRORED_REPEAT, GL20.GL_MIRRORED_REPEAT);
        GL_ENUM_MAP.put(WrapMode.REPEAT, GL20.GL_REPEAT);
        GL_ENUM_MAP.put(WrapMode.CLAMP_TO_EDGE, GL20.GL_CLAMP_TO_EDGE);
    }


    private GL20 gl;


    public GL20Texture(GLRenderClient client, Type type) {
        super(client, type);
        gl = getGL();
        init();
    }

    public GL20Texture(GLRenderClient client, Type type, int textureId) {
        super(client, type, textureId);
        gl = getGL();
    }

    @Override
    protected int onTextureCreate() {
        int textureId = gl.glGenTexture();
        if (textureId <= 0) {
            throw new IllegalStateException(this + " createTexture fail");
        }
        return textureId;
    }

    @Override
    protected void onTextureDispose(int id) {
        gl.glDeleteTexture(id);
    }

    @Override
    public int getTextureTarget() {
        return GL_ENUM_MAP.get(getTextureType());
    }


    @Override
    protected void onConfigTextureFilter(int textureTarget, FilterMode minFilter, FilterMode magFilter) {
        gl.glTexParameteri(textureTarget, GL20.GL_TEXTURE_MIN_FILTER, GL_ENUM_MAP.get(minFilter));
        gl.glTexParameteri(textureTarget, GL20.GL_TEXTURE_MAG_FILTER, GL_ENUM_MAP.get(magFilter));
    }

    @Override
    protected void onConfigTextureWrap(int textureTarget, WrapMode wraps, WrapMode wrapt) {
        gl.glTexParameteri(textureTarget, GL20.GL_TEXTURE_WRAP_S, GL_ENUM_MAP.get(wraps));
        gl.glTexParameteri(textureTarget, GL20.GL_TEXTURE_WRAP_T, GL_ENUM_MAP.get(wrapt));
    }

    @Override
    protected void onConfigTextureSize(int textureTarget, int textureWidth, int textureHeight) {
        gl.glTexImage2D(textureTarget, 0, GL20.GL_RGBA, textureWidth, textureHeight, 0, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, null);
    }


    @Override
    protected void onUpdateBitmap(int textureTarget, Bitmap bitmap, boolean mipmap) {
        Bitmap uploadBitmap = bitmap;
        int imgWidth = bitmap.getWidth();
        int imgHeight = bitmap.getHeight();
        boolean needPowTwo = mipmap && (!MathUtils.isPowerOfTwo(imgWidth) || !MathUtils.isPowerOfTwo(imgHeight));
        float scale = 1;
        float maxSize = getMaxTextureSize();
        if (imgWidth > maxSize || imgHeight > maxSize) {
            scale = maxSize / Math.max(imgWidth, imgHeight);
        }
        if (scale < 1 || needPowTwo) {
            int width = (int) (imgWidth * scale), height = (int) (imgHeight * scale);
            if (needPowTwo) {
                width = MathUtils.floorPowerOfTwo(scale * imgWidth);
                height = MathUtils.floorPowerOfTwo(scale * imgHeight);
            }
            Bitmap premultBitmap = bitmap;
            if (!bitmap.isPremultiplied() && bitmap.hasAlpha()) {//To support mipmap, colors must be pre-multiplied
                premultBitmap = Bitmap.createBitmap(imgWidth, imgHeight, bitmap.getConfig());
                premultBitmap.setPremultiplied(false);
                int stride = imgWidth;
                int[] tempLine = new int[stride];
                for (int j = 0; j < imgHeight; j++) {
                    bitmap.getPixels(tempLine, 0, stride, 0, j, width, 1);
                    premultBitmap.setPixels(tempLine, 0, stride, 0, j, width, 1);
                }
                premultBitmap.setPremultiplied(true);
            }
            uploadBitmap = Bitmap.createScaledBitmap(premultBitmap, width, height, true);
            if (!Objects.equals(premultBitmap, bitmap)) {
                premultBitmap.recycle();
            }
        }
        GLUtils.texImage2D(textureTarget, 0, uploadBitmap, 0);
        if (!Objects.equals(uploadBitmap, bitmap)) {
            uploadBitmap.recycle();
        }
    }

    @Override
    protected void onBindTexture(int textureTarget, int textureId) {
        gl.glBindTexture(textureTarget, textureId);
    }

    @Override
    protected void onDrawColor(Matrix4 matrix, GLXfermode xfermode, int color) {
        GLFrameBufferCache frameBufferCache = GL20FrameBufferCache.getCache(getClient());
        GLFrameBuffer frameBuffer = frameBufferCache.obtain(getWidth(), getHeight());
        GLTexture colorTexture = frameBuffer.getAttachColorTexture();
        frameBuffer.attachColorTexture(this);
        frameBuffer.drawColor(matrix, xfermode, color);
        frameBuffer.attachColorTexture(colorTexture);
        frameBufferCache.cache(frameBuffer);
    }

    private int getMaxTextureSize() {
        return gl.glGetInteger(GL20.GL_MAX_TEXTURE_SIZE);
    }

    @Override
    protected void onGenerateMipmap(int textureTarget) {
        gl.glGenerateMipmap(textureTarget);
    }

    @Override
    protected void onActiveTexture(int unit) {
        gl.glActiveTexture(GL20.GL_TEXTURE0 + unit);
    }


    @Override
    public String toString() {
        return "GL20Texture[" +
                "textureType=" + getTextureType() +
                ", textureId=" + getTextureId() +
                ", textureWidth=" + getWidth() +
                ", textureHeight=" + getHeight() +
                ']';
    }
}
