package com.jonanorman.android.renderclient.opengl;

import android.graphics.Bitmap;

import com.jonanorman.android.renderclient.math.MathUtils;
import com.jonanorman.android.renderclient.math.Matrix4;

public abstract class GLTexture extends GLClass {


    private final static float[] TEXTURE_FLIP_Y_MATRIX = new Matrix4().translateY(-1).flipY().get();
    private final Type textureType;
    private int textureId;

    private boolean premultiplied = true;
    private int maxMipmapLevel;
    private FilterMode minFilter;
    private FilterMode magFilter;
    private WrapMode wraps;
    private WrapMode wrapt;
    private Matrix4 textureMatrix;

    private int textureWidth;
    private int textureHeight;

    private GLTextureFilterMethod textureFilterMethod;
    private GLTextureWrapMethod textureWrapMethod;
    private GLTextureSizeMethod textureSizeMethod;
    private GLUpdateBitmapMethod updateBitmapMethod;
    private GLGenerateMipmapMethod generateMipmapMethod;
    private GLBindMethod bindMethod;
    private GLActiveMethod activeMethod;
    private GLDrawColorMethod drawColorMethod;

    public GLTexture(GLRenderClient client, Type type) {
        super(client);
        initTexture();
        textureType = type;
    }

    public GLTexture(GLRenderClient client, Type type, int id) {
        super(client);
        initTexture();
        if (id < 0) {
            throw new IllegalArgumentException(this + " textureId must >= 0");
        }
        textureType = type;
        textureId = id;
        markExternal();
    }

    private void initTexture() {
        minFilter = FilterMode.NEAREST;
        magFilter = FilterMode.NEAREST;
        wraps = WrapMode.CLAMP_TO_EDGE;
        wrapt = WrapMode.CLAMP_TO_EDGE;
        textureMatrix = new Matrix4();
    }

    @Override
    protected void onRegisterMethod() {
        textureFilterMethod = new GLTextureFilterMethod();
        textureWrapMethod = new GLTextureWrapMethod();
        textureSizeMethod = new GLTextureSizeMethod();
        updateBitmapMethod = new GLUpdateBitmapMethod();
        generateMipmapMethod = new GLGenerateMipmapMethod();
        bindMethod = new GLBindMethod();
        activeMethod = new GLActiveMethod();
        drawColorMethod = new GLDrawColorMethod();
    }

    @Override
    protected final void onClassInit() {
        textureId = onTextureCreate();
        findMethod(GLTextureFilterMethod.class).apply();
        findMethod(GLTextureWrapMethod.class).apply();
    }

    @Override
    protected final void onClassDispose() {
        onTextureDispose(textureId);
    }


    protected abstract int onTextureCreate();

    protected abstract void onTextureDispose(int id);


    public int getHeight() {
        return textureHeight;
    }

    public int getWidth() {
        return textureWidth;
    }

    public Type getTextureType() {
        return textureType;
    }

    public int getTextureId() {
        return textureId;
    }

    public void setWidth(int width) {
        textureWidth = width;
    }

    public void setHeight(int height) {
        this.textureHeight = height;
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

    public void setMinFilter(FilterMode minFilter) {
        this.minFilter = minFilter;
        textureFilterMethod.apply();
    }

    public void setMagFilter(FilterMode magFilter) {
        if (magFilter == FilterMode.LINEAR_MIPMAP_LINEAR
                || magFilter == FilterMode.LINEAR_MIPMAP_NEAREST
                || magFilter == FilterMode.NEAREST_MIPMAP_LINEAR
                || magFilter == FilterMode.NEAREST_MIPMAP_NEAREST) {
            throw new IllegalArgumentException(this + " mag filter can not set mipmap");
        }
        this.magFilter = magFilter;
        textureFilterMethod.apply();
    }


    public FilterMode getMinFilter() {
        return minFilter;
    }

    public FilterMode getMagFilter() {
        return magFilter;
    }

    public void setWrapS(WrapMode wraps) {
        this.wraps = wraps;
        textureWrapMethod.apply();
    }

    public void setWrapT(WrapMode wrapT) {
        this.wrapt = wraps;
        textureWrapMethod.apply();
    }

    public void setSize(int width, int height) {
        textureWidth = width;
        textureHeight = height;
        textureSizeMethod.apply();
    }

    public void updateBitmap(Bitmap bitmap) {
        updateBitmapMethod.setBitmap(bitmap);
        updateBitmapMethod.apply();
    }

    public void generateMipmap() {
        generateMipmapMethod.apply();
    }

    public final GLTexture bind() {
        bindMethod.apply();
        return bindMethod.preTexture;
    }


    public final int active(int unit) {
        activeMethod.unit = unit;
        activeMethod.apply();
        return activeMethod.preUnit;
    }

    public final void drawColor(Matrix4 matrix, GLXfermode xfermode, int color) {
        drawColorMethod.xfermode = xfermode;
        drawColorMethod.matrix = matrix;
        drawColorMethod.color = color;
        drawColorMethod.apply();
    }

    public final void drawColor(Matrix4 matrix, int color) {
        drawColor(matrix, GLXfermode.SRC_OVER, color);
    }

    public final void drawColor(int color) {
        drawColor(new Matrix4(), GLXfermode.SRC_OVER, color);
    }

    public final void drawColor(GLXfermode xfermode, int color) {
        drawColor(new Matrix4(), xfermode, color);
    }


    class GLTextureFilterMethod extends GLMethod {


        public GLTextureFilterMethod() {
            super();
        }


        @Override
        protected void onMethodCall() {
            GLTexture old = bind();
            onConfigTextureFilter(getTextureTarget(), minFilter, magFilter);
            old.bind();
        }

    }


    class GLTextureWrapMethod extends GLMethod {


        public GLTextureWrapMethod() {
            super();
        }

        @Override
        protected void onMethodCall() {
            GLTexture old = bind();
            onConfigTextureWrap(getTextureTarget(), wraps, wrapt);
            old.bind();
        }

    }


    class GLTextureSizeMethod extends GLMethod {


        public GLTextureSizeMethod() {
            super();
        }

        @Override
        protected void onMethodCall() {
            GLTexture old = bind();
            onConfigTextureSize(getTextureTarget(), textureWidth, textureHeight);
            old.bind();
            textureMatrix.clearIdentity();
            premultiplied = true;
            maxMipmapLevel = 0;
        }
    }


    class GLUpdateBitmapMethod extends GLMethod {

        private Bitmap bitmap;

        public GLUpdateBitmapMethod() {
            super();
        }


        @Override
        protected void onMethodCall() {
            boolean mipmap = false;
            if (minFilter != FilterMode.LINEAR && minFilter != FilterMode.NEAREST) {
                mipmap = true;
            }
            GLTexture old = bind();
            onUpdateBitmap(getTextureTarget(), bitmap, mipmap);
            old.bind();
            textureWidth = bitmap.getWidth();
            textureHeight = bitmap.getHeight();
            premultiplied = bitmap.isPremultiplied();
            textureMatrix.set(TEXTURE_FLIP_Y_MATRIX);
            if (mipmap) generateMipmap();
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }

    class GLGenerateMipmapMethod extends GLMethod {
        public GLGenerateMipmapMethod() {
            super();
        }

        @Override
        protected void onMethodCall() {
            if (minFilter == FilterMode.LINEAR || minFilter == FilterMode.NEAREST) {
                throw new IllegalArgumentException(this + " must set mipmap filter");
            }
            GLTexture old = bind();
            onGenerateMipmap(getTextureTarget());
            old.bind();
            maxMipmapLevel = MathUtils.floorLogOfTwo(Math.max(textureWidth, textureHeight));
        }
    }

    class GLBindMethod extends GLMethod {
        private GLTexture preTexture;

        public GLBindMethod() {
            super();
        }

        @Override
        protected void onMethodCall() {
            onBindTexture(getTextureTarget(), textureId);
            preTexture = getClient().getCurrentTexture();
            getClient().setCurrentTexture(GLTexture.this);
        }

    }


    class GLActiveMethod extends GLMethod {
        private int unit;
        private int preUnit;

        public GLActiveMethod() {
            super();
        }

        @Override
        protected void onMethodCall() {
            onActiveTexture(unit);
            preUnit = getClient().getCurrentTextureUnit();
            getClient().setCurrentTextureUnit(unit);
        }
    }

    class GLDrawColorMethod extends GLMethod {

        private Matrix4 matrix;
        private GLXfermode xfermode;
        private int color;

        public GLDrawColorMethod() {
            super();
            matrix = new Matrix4();
            xfermode = GLXfermode.SRC_OVER;
        }

        @Override
        protected void onMethodCall() {
            onDrawColor(matrix, xfermode, color);
        }
    }

    public abstract int getTextureTarget();

    protected abstract void onConfigTextureFilter(int textureTarget, FilterMode minFilter, FilterMode magFilter);

    protected abstract void onConfigTextureWrap(int textureTarget, WrapMode wraps, WrapMode wrapt);

    protected abstract void onConfigTextureSize(int textureTarget, int textureWidth, int textureHeight);

    protected abstract void onUpdateBitmap(int textureTarget, Bitmap bitmap, boolean mipmap);

    protected abstract void onGenerateMipmap(int textureTarget);

    protected abstract void onActiveTexture(int unit);

    protected abstract void onBindTexture(int textureTarget, int textureId);


    protected abstract void onDrawColor(Matrix4 matrix, GLXfermode xfermode, int color);


    public enum Type {
        TEXTURE_2D,
        TEXTURE_OES,
        TEXTURE_CUBE_MAP
    }

    public enum FilterMode {
        NEAREST,
        LINEAR,
        NEAREST_MIPMAP_NEAREST,
        LINEAR_MIPMAP_NEAREST,
        NEAREST_MIPMAP_LINEAR,
        LINEAR_MIPMAP_LINEAR;
    }

    public enum WrapMode {
        MIRRORED_REPEAT,
        CLAMP_TO_EDGE,
        REPEAT;
    }
}
