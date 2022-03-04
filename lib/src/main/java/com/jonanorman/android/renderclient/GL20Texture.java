package com.jonanorman.android.renderclient;


import android.graphics.Bitmap;
import android.opengl.GLUtils;

import com.jonanorman.android.renderclient.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class GL20Texture extends GLTexture {
    private static Map<Object, Integer> GL_ENUM_MAP = new HashMap<>();

    static {
        GL_ENUM_MAP.put(GLTextureFilter.NEAREST, GL20.GL_NEAREST);
        GL_ENUM_MAP.put(GLTextureFilter.LINEAR, GL20.GL_LINEAR);
        GL_ENUM_MAP.put(GLTextureFilter.NEAREST_MIPMAP_NEAREST, GL20.GL_NEAREST_MIPMAP_NEAREST);
        GL_ENUM_MAP.put(GLTextureFilter.LINEAR_MIPMAP_NEAREST, GL20.GL_LINEAR_MIPMAP_NEAREST);
        GL_ENUM_MAP.put(GLTextureFilter.NEAREST_MIPMAP_LINEAR, GL20.GL_NEAREST_MIPMAP_LINEAR);
        GL_ENUM_MAP.put(GLTextureFilter.LINEAR_MIPMAP_LINEAR, GL20.GL_LINEAR_MIPMAP_LINEAR);
        GL_ENUM_MAP.put(GLTextureType.TEXTURE_2D, GL20.GL_TEXTURE_2D);
        GL_ENUM_MAP.put(GLTextureType.TEXTURE_OES, GL20.GL_TEXTURE_EXTERNAL_OES);
        GL_ENUM_MAP.put(GLTextureType.TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_CUBE_MAP);
        GL_ENUM_MAP.put(GLTextureWrap.MIRRORED_REPEAT, GL20.GL_MIRRORED_REPEAT);
        GL_ENUM_MAP.put(GLTextureWrap.REPEAT, GL20.GL_REPEAT);
        GL_ENUM_MAP.put(GLTextureWrap.CLAMP_TO_EDGE, GL20.GL_CLAMP_TO_EDGE);

    }

    private final int textureTarget;

    private GL20 gl;


    public GL20Texture(GLRenderClient client, GLTextureType type) {
        super(client, type);
        textureTarget = GL_ENUM_MAP.get(type);
        gl = client.getGL20();
    }

    public GL20Texture(GLRenderClient client, GLTextureType type, int textureId) {
        super(client, type, textureId);
        textureTarget = GL_ENUM_MAP.get(type);
    }


    @Override
    protected void onCreate() {
        if (isNewTexture()) {
            int[] textureIds = new int[1];
            gl.glGenTextures(1, textureIds, 0);
            textureId = textureIds[0];
            if (textureId <= 0) {
                throw new IllegalStateException("textureId create fail");
            }
            findMethod(GLTextureFilterMethod.class).call();
            findMethod(GLTextureWrapMethod.class).call();
        }
    }

    @Override
    protected void onDispose() {
        if (isNewTexture()) {
            gl.glDeleteTextures(1, new int[]{textureId}, 0);
        }
    }

    public int getTarget() {
        return textureTarget;
    }


    @Override
    protected void onConfigTextureFilter(GLTextureFilter minFilter, GLTextureFilter magFilter) {
        gl.glBindTexture(textureTarget, textureId);
        gl.glTexParameteri(textureTarget, GL20.GL_TEXTURE_MIN_FILTER, GL_ENUM_MAP.get(minFilter));
        gl.glTexParameteri(textureTarget, GL20.GL_TEXTURE_MAG_FILTER, GL_ENUM_MAP.get(magFilter));
        gl.glBindTexture(textureTarget, 0);
    }

    @Override
    protected void onConfigTextureWrap(GLTextureWrap wraps, GLTextureWrap wrapt) {
        gl.glBindTexture(textureTarget, textureId);
        gl.glTexParameteri(textureTarget, GL20.GL_TEXTURE_WRAP_S, GL_ENUM_MAP.get(wraps));
        gl.glTexParameteri(textureTarget, GL20.GL_TEXTURE_WRAP_T, GL_ENUM_MAP.get(wrapt));
        gl.glBindTexture(textureTarget, 0);
    }

    @Override
    protected void onConfigTextureSize(int textureWidth, int textureHeight) {
        gl.glBindTexture(textureTarget, textureId);
        gl.glTexImage2D(textureTarget, 0, GL20.GL_RGBA, textureWidth, textureHeight, 0, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, null);
        gl.glBindTexture(textureTarget, 0);
    }


    @Override
    protected void onUpdateBitmap(boolean mipmap, Bitmap bitmap) {
        Bitmap uploadBitmap = bitmap;
        int imgWidth = bitmap.getWidth();
        int imgHeight = bitmap.getHeight();
        boolean needPowTwo = mipmap && (!MathUtils.isPowerOfTwo(imgWidth) || !MathUtils.isPowerOfTwo(imgHeight));
        float scale = 1;
        float maxSize = client.getMaxTextureSize();
        if (imgWidth > maxSize || imgHeight > maxSize) {
            scale = maxSize / Math.max(imgWidth, imgHeight);
        }
        if (scale < 1 || needPowTwo) {
            int width = (int) (imgWidth * scale), height = (int) (imgHeight * scale);
            if (needPowTwo) {
                width = (int) MathUtils.floorPowerOfTwo(scale * imgWidth);
                height = (int) MathUtils.floorPowerOfTwo(scale * imgHeight);
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
        gl.glBindTexture(textureTarget, getTextureId());
        GLUtils.texImage2D(textureTarget, 0, uploadBitmap, 0);
        gl.glBindTexture(textureTarget, 0);
        if (!Objects.equals(uploadBitmap, bitmap)) {
            uploadBitmap.recycle();
        }
    }

    @Override
    protected void onGenerateMipmap() {
        gl.glBindTexture(textureTarget, getTextureId());
        gl.glGenerateMipmap(textureTarget);
        gl.glBindTexture(textureTarget, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GL20Texture)) return false;
        if (!super.equals(o)) return false;
        GL20Texture that = (GL20Texture) o;
        return Objects.equals(gl, that.gl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gl);
    }
}
