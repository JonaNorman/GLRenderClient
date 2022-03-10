package com.jonanorman.android.renderclient;

public abstract class GLDepthBuffer extends GLObject {
    private int width;
    private int height;

    public GLDepthBuffer(GLRenderClient client) {
        super(client);
        initMethod();
    }

    private void initMethod() {
        registerMethod(GLDepthBufferSizeMethod.class, new GLDepthBufferSizeMethod());
    }


    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public abstract int getDepthBufferId();


    public void setBufferSize(int width, int height) {
        GLDepthBufferSizeMethod bufferSizeMethod = findMethod(GLDepthBufferSizeMethod.class);
        bufferSizeMethod.setWidth(width);
        bufferSizeMethod.setHeight(height);
        bufferSizeMethod.call();
    }

    class GLDepthBufferSizeMethod extends GLMethod {

        private int bufferWidth;
        private int bufferHeight;

        public GLDepthBufferSizeMethod() {
            super();
        }

        @Override
        protected void onCallMethod() {
            onConfigDepthBufferSize(bufferWidth, bufferHeight);
            width = bufferWidth;
            height = bufferHeight;
        }

        public void setWidth(int width) {
            this.bufferWidth = width;
        }

        public void setHeight(int height) {
            this.bufferHeight = height;
        }
    }

    protected abstract void onConfigDepthBufferSize(int bufferWidth, int bufferHeight);
}
