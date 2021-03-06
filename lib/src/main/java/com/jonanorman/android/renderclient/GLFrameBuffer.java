package com.jonanorman.android.renderclient;

import android.graphics.Bitmap;
import android.graphics.Color;

public abstract class GLFrameBuffer extends GLObject {


    public GLFrameBuffer(GLRenderClient client) {
        super(client);
        registerMethod(GLClearColorMethod.class, new GLClearColorMethod());
        registerMethod(GLBindMethod.class, new GLBindMethod());
        registerMethod(GLCopyToTextureMethod.class, new GLCopyToTextureMethod());
        registerMethod(GLReadBitmapMethod.class, new GLReadBitmapMethod());
        registerMethod(GLAttachColorTextureMethod.class, new GLAttachColorTextureMethod());
        registerMethod(GLAttachDepthBufferMethod.class, new GLAttachDepthBufferMethod());
        registerMethod(GLClearDepthBufferMethod.class, new GLClearDepthBufferMethod());
    }


    public GLTexture getColorTexture() {
        if (!isDisposed()) {
            create();
        }
        return findMethod(GLAttachColorTextureMethod.class).getTexture();
    }

    public GLDepthBuffer getDepthBuffer() {
        if (!isDisposed()) {
            create();
        }
        return findMethod(GLAttachDepthBufferMethod.class).getDepthBuffer();
    }

    public abstract GLRenderSurface getRenderSurface();

    public abstract int getWidth();

    public abstract int getHeight();

    public final void swapBuffers() {
        getRenderSurface().swapBuffers();
    }


    public final void clearColor(int color) {
        GLClearColorMethod clearColorMethod = findMethod(GLClearColorMethod.class);
        clearColorMethod.setClearColor(color);
        clearColorMethod.call();
    }

    public final void clearDepthBuffer() {
        GLClearDepthBufferMethod clearDepthBufferMethod = findMethod(GLClearDepthBufferMethod.class);
        clearDepthBufferMethod.call();
    }

    public final GLFrameBuffer bind() {
        GLBindMethod bindMethod = findMethod(GLBindMethod.class);
        bindMethod.call();
        return bindMethod.getPreFrameBuffer();
    }

    public final void copyToTexture(GLTexture texture) {
        GLCopyToTextureMethod copyToTextureMethod = findMethod(GLCopyToTextureMethod.class);
        copyToTextureMethod.setX(0);
        copyToTextureMethod.setY(0);
        copyToTextureMethod.setWidth(0);
        copyToTextureMethod.setHeight(0);
        copyToTextureMethod.setTexture(texture);
        copyToTextureMethod.call();
    }


    public final void copyToTexture(int x, int y, int width, int height, GLTexture texture) {
        GLCopyToTextureMethod copyToTextureMethod = findMethod(GLCopyToTextureMethod.class);
        copyToTextureMethod.setX(x);
        copyToTextureMethod.setY(y);
        copyToTextureMethod.setWidth(width);
        copyToTextureMethod.setHeight(height);
        copyToTextureMethod.setTexture(texture);
        copyToTextureMethod.call();
    }

    public final void attachColorTexture(GLTexture texture) {
        GLAttachColorTextureMethod attachColorTextureMethod = findMethod(GLAttachColorTextureMethod.class);
        attachColorTextureMethod.setTexture(texture);
        attachColorTextureMethod.call();
    }

    public final void attachDepthBuffer(GLDepthBuffer depthBuffer) {
        GLAttachDepthBufferMethod attachColorTextureMethod = findMethod(GLAttachDepthBufferMethod.class);
        attachColorTextureMethod.setDepthBuffer(depthBuffer);
        attachColorTextureMethod.call();
    }

    public final Bitmap getBitmap() {
        if (isDisposed()) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        readBitmap(bitmap);
        return bitmap;
    }

    public final void readBitmap(int x, int y, int width, int height, Bitmap bitmap) {
        GLReadBitmapMethod readBitmapMethod = findMethod(GLReadBitmapMethod.class);
        readBitmapMethod.setBitmap(bitmap);
        readBitmapMethod.setX(x);
        readBitmapMethod.setY(y);
        readBitmapMethod.setWidth(width);
        readBitmapMethod.setHeight(height);
        readBitmapMethod.call();
    }

    public final void readBitmap(Bitmap bitmap) {
        GLReadBitmapMethod readBitmapMethod = findMethod(GLReadBitmapMethod.class);
        readBitmapMethod.setBitmap(bitmap);
        readBitmapMethod.setX(0);
        readBitmapMethod.setY(0);
        readBitmapMethod.setWidth(0);
        readBitmapMethod.setHeight(0);
        readBitmapMethod.call();
    }

    public void setSize(int width, int height) {
        GLTexture attachTexture = getColorTexture();
        if (attachTexture == null) {
            throw new IllegalArgumentException("Surface Frame Buffer can not set size");
        }
        attachTexture.setTextureSize(width, height);
        GLDepthBuffer depthBuffer = getDepthBuffer();
        if (depthBuffer == null) {
            throw new IllegalArgumentException("Surface Frame Buffer can not set size");
        }
        depthBuffer.setBufferSize(width, height);
    }

    protected abstract void onClearClear(float red, float green, float blue, float alpha);

    protected abstract void onClearDepthBuffer();

    protected abstract void onAttachColorTexture(GLTexture texture);

    protected abstract void onAttachDepthBuffer(GLDepthBuffer depthBuffer);

    protected abstract void onBind();

    protected abstract void onReadBitmap(int x, int y, int width, int height, Bitmap bitmap);

    protected abstract void onCopyToTexture(int x, int y, int width, int height, GLTexture texture);


    class GLAttachColorTextureMethod extends GLMethod {

        protected GLTexture texture;

        public GLAttachColorTextureMethod() {
            super();
        }

        @Override
        protected void onCallMethod() {
            GLFrameBuffer old = bind();
            if (!texture.isDisposed()) {
                texture.create();
            }
            onAttachColorTexture(texture);
            old.bind();
        }

        public void setTexture(GLTexture texture) {
            this.texture = texture;
        }

        public GLTexture getTexture() {
            return texture;
        }
    }

    class GLAttachDepthBufferMethod extends GLMethod {
        protected GLDepthBuffer depthBuffer;

        public GLAttachDepthBufferMethod() {
            super();
        }

        @Override
        protected void onCallMethod() {
            GLFrameBuffer old = bind();
            if (!depthBuffer.isDisposed()) {
                depthBuffer.create();
            }
            onAttachDepthBuffer(depthBuffer);
            old.bind();
        }

        public void setDepthBuffer(GLDepthBuffer depthBuffer) {
            this.depthBuffer = depthBuffer;
        }

        public GLDepthBuffer getDepthBuffer() {
            return depthBuffer;
        }
    }

    class GLClearColorMethod extends GLMethod {

        protected float redColor;
        protected float greenColor;
        protected float blueColor;
        protected float alphaColor;

        public GLClearColorMethod() {
            super();
        }

        @Override
        protected void onCallMethod() {
            GLFrameBuffer old = bind();
            onClearClear(redColor, greenColor, blueColor, alphaColor);
            old.bind();
        }

        public void setClearColor(int color) {
            alphaColor = Color.alpha(color) / 255.0f;//use premult color
            redColor = Color.red(color) / 255.0f * alphaColor;
            greenColor = Color.green(color) / 255.0f * alphaColor;
            blueColor = Color.blue(color) / 255.0f * alphaColor;
        }

    }


    class GLClearDepthBufferMethod extends GLMethod {


        public GLClearDepthBufferMethod() {
            super();
        }

        @Override
        protected void onCallMethod() {
            GLFrameBuffer old = bind();
            onClearDepthBuffer();
            old.bind();
        }


    }


    class GLBindMethod extends GLMethod {

        public GLBindMethod() {
            super();
        }

        GLFrameBuffer preFrameBuffer;

        @Override
        protected void onCallMethod() {
            onBind();
            preFrameBuffer = client.getCurrentFrameBuffer();
            client.setCurrentFrameBuffer(GLFrameBuffer.this);
        }

        public GLFrameBuffer getPreFrameBuffer() {
            return preFrameBuffer;
        }
    }


    class GLCopyToTextureMethod extends GLMethod {
        protected GLTexture texture;
        private int x;
        private int y;
        private int width;
        private int height;

        public GLCopyToTextureMethod() {
            super();
        }

        @Override
        protected void onCallMethod() {
            GLFrameBuffer old = bind();
            if (!texture.isDisposed()) {
                texture.create();
            }
            onCopyToTexture(x, y, width, height, texture);
            old.bind();
        }

        public void setTexture(GLTexture texture) {
            this.texture = texture;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }


        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    class GLReadBitmapMethod extends GLMethod {

        private int x;
        private int y;
        private int width;
        private int height;

        public GLReadBitmapMethod() {
            super();
        }

        @Override
        protected void onCallMethod() {
            GLFrameBuffer old = bind();
            onReadBitmap(x, y, width, height, bitmap);
            old.bind();
        }

        protected Bitmap bitmap;

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }


        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public abstract int getFrameBufferId();
}
