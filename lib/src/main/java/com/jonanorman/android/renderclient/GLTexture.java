package com.jonanorman.android.renderclient;

import android.graphics.Bitmap;

import com.jonanorman.android.renderclient.math.Matrix4;
import com.jonanorman.android.renderclient.utils.MathUtils;

import java.util.Objects;

public abstract class GLTexture extends GLObject {


    public final static float[] TEXTURE_FLIP_Y_MATRIX = new Matrix4().translateY(-1).flipY().get();
    private final GLTextureType textureType;
    protected int textureId;
    private boolean newTexture;

    private int width;
    private int height;
    private boolean premultiplied = true;
    private int maxMipmapLevel;
    private Matrix4 textureMatrix = new Matrix4();


    public GLTexture(GLRenderClient client, GLTextureType type) {
        super(client);
        textureType = type;
        newTexture = true;
        initMethod();
    }


    public GLTexture(GLRenderClient client, GLTextureType type, int textureId) {
        super(client);
        textureType = type;
        if (textureId <= 0) {
            throw new IllegalArgumentException("textureId must >0");
        }
        this.textureId = textureId;
        initMethod();
    }

    boolean isNewTexture() {
        return newTexture;
    }

    private void initMethod() {
        registerMethod(GLTextureFilterMethod.class, new GLTextureFilterMethod());
        registerMethod(GLTextureWrapMethod.class, new GLTextureWrapMethod());
        registerMethod(GLTextureSizeMethod.class, new GLTextureSizeMethod());
        registerMethod(GLUpdateBitmapMethod.class, new GLUpdateBitmapMethod());
        registerMethod(GLGenerateMipmapMethod.class, new GLGenerateMipmapMethod());
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }


    public int getTextureId() {
        if (!isDisposed()) {
            create();
        }
        return textureId;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setMaxMipmapLevel(int maxMipmapLevel) {
        this.maxMipmapLevel = maxMipmapLevel;
    }

    public int getMaxMipmapLevel() {
        return maxMipmapLevel;
    }

    public void setTextureMatrix(Matrix4 textureMatrix) {
        this.textureMatrix.set(textureMatrix.get());
    }

    public Matrix4 getTextureMatrix() {
        return textureMatrix;
    }

    public boolean isPremultiplied() {
        return premultiplied;
    }

    public void setPremultiplied(boolean premultiplied) {
        this.premultiplied = premultiplied;
    }

    public void setMinFilter(GLTextureFilter minFilter) {
        GLTextureFilterMethod textureFilterMethod = findMethod(GLTextureFilterMethod.class);
        textureFilterMethod.setMinFilter(minFilter);
    }

    public void setMagFilter(GLTextureFilter magFilter) {
        GLTextureFilterMethod textureFilterMethod = findMethod(GLTextureFilterMethod.class);
        textureFilterMethod.setMagFilter(magFilter);
    }

    public void applyFilter() {
        GLTextureFilterMethod textureFilterMethod = findMethod(GLTextureFilterMethod.class);
        textureFilterMethod.call();
    }

    public GLTextureFilter getMinFilter() {
        GLTextureFilterMethod textureFilterMethod = findMethod(GLTextureFilterMethod.class);
        return textureFilterMethod.getMinFilter();
    }

    public GLTextureFilter getMagFilter() {
        GLTextureFilterMethod textureFilterMethod = findMethod(GLTextureFilterMethod.class);
        return textureFilterMethod.getMagFilter();
    }

    public void setWrapS(GLTextureWrap wraps) {
        GLTextureWrapMethod textureWrapMethod = findMethod(GLTextureWrapMethod.class);
        textureWrapMethod.setWrapS(wraps);
    }

    public void setWrapT(GLTextureWrap wraps) {
        GLTextureWrapMethod textureWrapMethod = findMethod(GLTextureWrapMethod.class);
        textureWrapMethod.setWrapT(wraps);
    }

    public void applyWrap() {
        GLTextureWrapMethod textureWrapMethod = findMethod(GLTextureWrapMethod.class);
        textureWrapMethod.call();
    }

    public void setTextureSize(int width, int height) {
        GLTextureSizeMethod textureSizeMethod = findMethod(GLTextureSizeMethod.class);
        textureSizeMethod.setWidth(width);
        textureSizeMethod.setHeight(height);
        textureSizeMethod.call();
    }

    public void updateBitmap(Bitmap bitmap) {
        GLUpdateBitmapMethod updateBitmapMethod = findMethod(GLUpdateBitmapMethod.class);
        updateBitmapMethod.setBitmap(bitmap);
        updateBitmapMethod.call();
    }

    public void generateMipmap() {
        GLGenerateMipmapMethod generateMipmapMethod = findMethod(GLGenerateMipmapMethod.class);
        generateMipmapMethod.call();
    }

    public abstract int getTarget();

    protected abstract void onConfigTextureFilter(GLTextureFilter minFilter, GLTextureFilter magFilter);

    protected abstract void onConfigTextureWrap(GLTextureWrap wraps, GLTextureWrap wrapt);

    protected abstract void onConfigTextureSize(int textureWidth, int textureHeight);

    protected abstract void onUpdateBitmap(boolean mipmap, Bitmap bitmap);

    protected abstract void onGenerateMipmap();


    class GLTextureFilterMethod extends GLMethod {
        private GLTextureFilter minFilter = GLTextureFilter.NEAREST;
        private GLTextureFilter magFilter = GLTextureFilter.NEAREST;

        public GLTextureFilterMethod() {
            super();
        }


        @Override
        protected void onCallMethod() {
            onConfigTextureFilter(minFilter, magFilter);
        }


        public void setMagFilter(GLTextureFilter magFilter) {
            if (magFilter == GLTextureFilter.LINEAR_MIPMAP_LINEAR
                    || magFilter == GLTextureFilter.LINEAR_MIPMAP_NEAREST
                    || magFilter == GLTextureFilter.NEAREST_MIPMAP_LINEAR
                    || magFilter == GLTextureFilter.NEAREST_MIPMAP_NEAREST) {
                throw new IllegalArgumentException("mag filter can not set mipmap");
            }
            this.magFilter = magFilter;
        }

        public void setMinFilter(GLTextureFilter minFilter) {
            this.minFilter = minFilter;
        }

        public GLTextureFilter getMagFilter() {
            return magFilter;
        }

        public GLTextureFilter getMinFilter() {
            return minFilter;
        }
    }


    class GLTextureWrapMethod extends GLMethod {
        private GLTextureWrap wraps = GLTextureWrap.CLAMP_TO_EDGE;
        private GLTextureWrap wrapt = GLTextureWrap.CLAMP_TO_EDGE;

        public GLTextureWrapMethod() {
            super();
        }

        @Override
        protected void onCallMethod() {
            onConfigTextureWrap(wraps, wrapt);
        }

        public void setWrapS(GLTextureWrap wraps) {
            this.wraps = wraps;
        }

        public void setWrapT(GLTextureWrap wrapt) {
            this.wrapt = wrapt;
        }
    }


    class GLTextureSizeMethod extends GLMethod {

        private int textureWidth;
        private int textureHeight;

        public GLTextureSizeMethod() {
            super();
        }

        @Override
        protected void onCallMethod() {
            onConfigTextureSize(textureWidth, textureHeight);
            width = textureWidth;
            height = textureHeight;
            premultiplied = true;
            textureMatrix.setIdentity();
            maxMipmapLevel = 0;
        }

        public void setWidth(int width) {
            this.textureWidth = width;
        }

        public void setHeight(int height) {
            this.textureHeight = height;
        }
    }


    class GLUpdateBitmapMethod extends GLMethod {

        private Bitmap bitmap;

        public GLUpdateBitmapMethod() {
            super();
        }


        @Override
        protected void onCallMethod() {
            boolean mipmap = false;
            if (getMinFilter() != GLTextureFilter.LINEAR && getMinFilter() != GLTextureFilter.NEAREST) {
                mipmap = true;
            }
            onUpdateBitmap(mipmap, bitmap);
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            premultiplied = bitmap.isPremultiplied();
            textureMatrix.set(TEXTURE_FLIP_Y_MATRIX);
            if (mipmap) {
                generateMipmap();
            }
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }

    class GLGenerateMipmapMethod extends GLMethod {
        @Override
        protected void onCallMethod() {
            if (getMinFilter() == GLTextureFilter.LINEAR || getMinFilter() == GLTextureFilter.NEAREST) {
                throw new IllegalArgumentException("must set mipmap filter");
            }
            onGenerateMipmap();
            maxMipmapLevel = (int) MathUtils.floorLogOfTwo(Math.max(width, height));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GLTexture)) return false;
        if (!super.equals(o)) return false;
        GLTexture texture = (GLTexture) o;
        return textureId == texture.textureId
                && textureType == texture.textureType
                && isCreated() == texture.isCreated()
                && isDisposed() == texture.isDisposed();
    }

    @Override
    public int hashCode() {
        return Objects.hash(textureType, textureId, isCreated(), isDisposed());
    }
}
