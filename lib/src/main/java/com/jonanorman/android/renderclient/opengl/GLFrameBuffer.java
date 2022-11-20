package com.jonanorman.android.renderclient.opengl;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.CallSuper;

import com.jonanorman.android.renderclient.math.Matrix4;

public abstract class GLFrameBuffer extends GLClass {
    private final boolean defaultFrameBuffer;

    private int initWidth;
    private int initHeight;
    private int frameBufferId;
    private EGLSurface renderSurface;
    private GLTexture initTexture;
    private GLTexture createTexture;
    private GLDepthBuffer createDepthBuffer;


    private GLTexture attachColorTexture;
    private GLDepthBuffer attachDepthBuffer;

    private GLClearColorMethod clearColorMethod;
    private GLBindMethod bindMethod;
    private GLCopyToTextureMethod copyToTextureMethod;
    private GLReadBitmapMethod readBitmapMethod;
    private GLAttachColorTextureMethod attachColorTextureMethod;
    private GLAttachDepthBufferMethod attachDepthBufferMethod;
    private GLClearDepthBufferMethod clearDepthBufferMethod;
    private GLDrawColorMethod drawColorMethod;
    private GLDrawTextureMethod drawTextureMethod;


    public GLFrameBuffer(GLRenderClient client, int width, int height) {
        super(client);
        this.initWidth = width;
        this.initHeight = height;
        this.renderSurface = client.getDefaultPBufferSurface();
        this.defaultFrameBuffer = false;
    }

    public GLFrameBuffer(GLRenderClient client, EGLSurface surface) {
        super(client);
        this.defaultFrameBuffer = true;
        this.renderSurface = surface;
        markExternal();
    }

    public GLFrameBuffer(GLRenderClient client, GLTexture texture) {
        super(client);
        this.initTexture = texture;
        this.renderSurface = client.getDefaultPBufferSurface();
        this.defaultFrameBuffer = false;
    }

    @Override
    protected void onRegisterMethod() {
        clearColorMethod = new GLClearColorMethod();
        bindMethod = new GLBindMethod();
        copyToTextureMethod = new GLCopyToTextureMethod();
        readBitmapMethod = new GLReadBitmapMethod();
        attachColorTextureMethod = new GLAttachColorTextureMethod();
        attachDepthBufferMethod = new GLAttachDepthBufferMethod();
        clearDepthBufferMethod = new GLClearDepthBufferMethod();
        drawColorMethod = new GLDrawColorMethod();
        drawTextureMethod = new GLDrawTextureMethod();
    }


    public GLTexture getAttachColorTexture() {
        return attachColorTexture;
    }

    public GLDepthBuffer getAttachDepthBuffer() {
        return attachDepthBuffer;
    }


    @Override
    @CallSuper
    protected void onClassInit() {
        frameBufferId = onFrameBufferCreate();
        GLTexture texture = initTexture;
        if (texture == null) {
            createTexture = onCreateColorTexture();
            createTexture.setSize(initWidth, initHeight);
            texture = createTexture;
        }
        createDepthBuffer = onCreateDepthBuffer();
        createDepthBuffer.setSize(texture.getWidth(), texture.getHeight());
        attachColorTexture(texture);
        attachDepthBuffer(createDepthBuffer);
    }

    @Override
    @CallSuper
    protected void onClassDispose() {
        onFrameBufferDispose(frameBufferId);
        if (createTexture != null) createTexture.dispose();
        if (createDepthBuffer != null) createDepthBuffer.dispose();
    }

    protected abstract int onFrameBufferCreate();

    protected abstract void onFrameBufferDispose(int id);


    public EGLSurface getSurface() {
        return renderSurface;
    }

    public int getWidth() {
        return defaultFrameBuffer ? renderSurface.getWidth() : attachColorTexture.getWidth();
    }

    public int getHeight() {
        return defaultFrameBuffer ? renderSurface.getHeight() : attachColorTexture.getHeight();
    }


    public int getFrameBufferId() {
        return frameBufferId;
    }

    public boolean isDefaultFrameBuffer() {
        return defaultFrameBuffer;
    }

    public final void swapBuffers() {
        getSurface().swapBuffers();
    }


    public final void clearColor(int color) {
        clearColorMethod.setClearColor(color);
        clearColorMethod.apply();
    }

    public final void clearDepthBuffer() {
        clearDepthBufferMethod.apply();
    }

    public final GLFrameBuffer bind() {
        bindMethod.apply();
        return bindMethod.preFrameBuffer;
    }

    public final void copyToTexture(GLTexture texture) {
        copyToTexture(0, 0, 0, 0, texture);
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

    public final void drawTexture(Matrix4 matrix, GLXfermode xfermode, GLTexture texture) {
        drawTextureMethod.xfermode = xfermode;
        drawTextureMethod.matrix = matrix;
        drawTextureMethod.texture = texture;
        drawTextureMethod.apply();
    }

    public final void drawTexture(Matrix4 matrix, GLTexture texture) {
        drawTexture(matrix, GLXfermode.SRC_OVER, texture);
    }

    public final void drawTexture(GLTexture texture) {
        drawTexture(new Matrix4(), GLXfermode.SRC_OVER, texture);
    }

    public final void drawTexture(GLXfermode xfermode, GLTexture texture) {
        drawTexture(new Matrix4(), xfermode, texture);
    }


    public final void copyToTexture(int x, int y, int width, int height, GLTexture texture) {
        copyToTextureMethod.setX(x);
        copyToTextureMethod.setY(y);
        copyToTextureMethod.setWidth(width);
        copyToTextureMethod.setHeight(height);
        copyToTextureMethod.setTexture(texture);
        copyToTextureMethod.apply();
    }

    public final void attachColorTexture(GLTexture texture) {
        attachColorTexture = texture;
        attachColorTextureMethod.apply();
    }

    public final void attachDepthBuffer(GLDepthBuffer depthBuffer) {
        attachDepthBuffer = depthBuffer;
        attachDepthBufferMethod.apply();
    }

    public final Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        readBitmap(bitmap);
        return bitmap;
    }

    public final void readBitmap(int x, int y, int width, int height, Bitmap bitmap) {
        readBitmapMethod.setBitmap(bitmap);
        readBitmapMethod.setX(x);
        readBitmapMethod.setY(y);
        readBitmapMethod.setWidth(width);
        readBitmapMethod.setHeight(height);
        readBitmapMethod.apply();
    }

    public final void readBitmap(Bitmap bitmap) {
        readBitmapMethod.setBitmap(bitmap);
        readBitmapMethod.setX(0);
        readBitmapMethod.setY(0);
        readBitmapMethod.setWidth(0);
        readBitmapMethod.setHeight(0);
        readBitmapMethod.apply();
    }

    public void setSize(int width, int height) {
        GLTexture attachTexture = getAttachColorTexture();
        attachTexture.setSize(width, height);
        GLDepthBuffer depthBuffer = getAttachDepthBuffer();
        depthBuffer.setSize(width, height);
    }


    protected abstract void onClearClear(float red, float green, float blue, float alpha);

    protected abstract void onClearDepthBuffer();

    protected abstract void onAttachColorTexture(GLTexture texture);

    protected abstract void onAttachDepthBuffer(GLDepthBuffer depthBuffer);

    protected abstract void onBindFrameBuffer(int frameBufferId);

    protected abstract void onReadBitmap(int x, int y, int width, int height, Bitmap bitmap);

    protected abstract void onCopyToTexture(int x, int y, int width, int height, GLTexture texture);

    protected abstract GLTexture onCreateColorTexture();

    protected abstract GLDepthBuffer onCreateDepthBuffer();

    protected abstract void onDrawColor(Matrix4 matrix, GLXfermode xfermode, float red, float green, float blue, float alpha);

    protected abstract void onDrawTexture(Matrix4 matrix, GLXfermode xfermode, GLTexture texture);


    class GLAttachColorTextureMethod extends GLMethod {


        public GLAttachColorTextureMethod() {
            super();
        }

        @Override
        protected void onMethodCall() {
            GLFrameBuffer old = bind();
            onAttachColorTexture(attachColorTexture);
            old.bind();
        }
    }

    class GLAttachDepthBufferMethod extends GLMethod {


        public GLAttachDepthBufferMethod() {
            super();
        }

        @Override
        protected void onMethodCall() {
            GLFrameBuffer old = bind();
            onAttachDepthBuffer(attachDepthBuffer);
            old.bind();
        }
    }

    class GLClearColorMethod extends GLMethod {

        private int clearColor;

        public GLClearColorMethod() {
            super();
        }

        @Override
        protected void onMethodCall() {
            GLFrameBuffer old = bind();
            float alphaColor = Color.alpha(clearColor) / 255.0f;//use premult color
            float redColor = Color.red(clearColor) / 255.0f * alphaColor;
            float greenColor = Color.green(clearColor) / 255.0f * alphaColor;
            float blueColor = Color.blue(clearColor) / 255.0f * alphaColor;
            onClearClear(redColor, greenColor, blueColor, alphaColor);
            old.bind();
        }

        public void setClearColor(int color) {
            this.clearColor = color;
        }
    }


    class GLClearDepthBufferMethod extends GLMethod {


        public GLClearDepthBufferMethod() {
            super();
        }

        @Override
        protected void onMethodCall() {
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
        protected void onMethodCall() {
            renderSurface.makeCurrent();
            onBindFrameBuffer(frameBufferId);
            preFrameBuffer = getClient().getCurrentFrameBuffer();
            getClient().setCurrentFrameBuffer(GLFrameBuffer.this);
        }
    }


    class GLCopyToTextureMethod extends GLMethod {
        private GLTexture texture;
        private int x;
        private int y;
        private int width;
        private int height;

        public GLCopyToTextureMethod() {
            super();
        }

        @Override
        protected void onMethodCall() {
            GLFrameBuffer oldFrameBuffer = bind();
            int oldUnit = texture.active(0);
            GLTexture oldTexture = texture.bind();
            onCopyToTexture(x, y, width, height, texture);
            oldTexture.active(oldUnit);
            oldTexture.bind();
            oldFrameBuffer.bind();
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
        private Bitmap bitmap;

        public GLReadBitmapMethod() {
            super();
        }

        @Override
        protected void onMethodCall() {
            GLFrameBuffer old = bind();
            onReadBitmap(x, y, width, height, bitmap);
            old.bind();
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
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
            float alphaColor = Color.alpha(color) / 255.0f;
            float redColor = Color.red(color) / 255.0f * alphaColor;
            float greenColor = Color.green(color) / 255.0f * alphaColor;
            float blueColor = Color.blue(color) / 255.0f * alphaColor;//use premult color
            onDrawColor(matrix, xfermode, redColor, greenColor, blueColor, alphaColor);
        }
    }


    class GLDrawTextureMethod extends GLMethod {
        private Matrix4 matrix;
        private GLXfermode xfermode;
        private GLTexture texture;

        public GLDrawTextureMethod() {
            super();
            matrix = new Matrix4();
            xfermode = GLXfermode.SRC_OVER;
        }

        @Override
        protected void onMethodCall() {
            onDrawTexture(matrix, xfermode, texture);
        }
    }


}
