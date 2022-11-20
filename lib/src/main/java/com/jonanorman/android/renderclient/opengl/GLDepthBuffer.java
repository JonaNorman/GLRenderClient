package com.jonanorman.android.renderclient.opengl;

public abstract class GLDepthBuffer extends GLClass {

    private GLDepthBufferSizeMethod depthBufferSizeMethod;

    private int id;

    private int bufferWidth;
    private int bufferHeight;

    public GLDepthBuffer(GLRenderClient client) {
        super(client);
    }

    @Override
    protected void onRegisterMethod() {
        depthBufferSizeMethod = new GLDepthBufferSizeMethod();
    }

    @Override
    protected void onClassInit() {
        id = onDepthBufferCreate();
    }

    @Override
    protected void onClassDispose() {
        onDepthBufferDispose(id);
    }


    public int getHeight() {
        return bufferHeight;
    }

    public int getWidth() {
        return bufferWidth;
    }

    public int getBufferId() {
        return id;
    }

    public void setSize(int width, int height) {
        bufferWidth = width;
        bufferHeight = height;
        depthBufferSizeMethod.apply();
    }

    protected abstract int onDepthBufferCreate();

    protected abstract void onDepthBufferDispose(int id);

    protected abstract void onDepthBufferSize(int bufferId, int bufferWidth, int bufferHeight);


    class GLDepthBufferSizeMethod extends GLMethod {


        public GLDepthBufferSizeMethod() {
            super();
        }

        @Override
        protected void onMethodCall() {
            onDepthBufferSize(id, bufferWidth, bufferHeight);
        }

    }
}
