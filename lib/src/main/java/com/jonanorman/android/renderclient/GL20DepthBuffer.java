package com.jonanorman.android.renderclient;

class GL20DepthBuffer extends GLDepthBuffer {
    private GL20 gl20;
    private int depthBufferId;

    public GL20DepthBuffer(GLRenderClient client) {
        super(client);
        gl20 = client.getGL20();
    }

    @Override
    protected void onCreate() {
        depthBufferId = gl20.glGenRenderbuffer();
    }

    @Override
    public int getDepthBufferId() {
        return depthBufferId;
    }

    @Override
    protected void onDispose() {
        gl20.glDeleteBuffer(depthBufferId);
    }

    @Override
    protected void onConfigDepthBufferSize(int bufferWidth, int bufferHeight) {
        gl20.glBindRenderbuffer(GL20.GL_RENDERBUFFER, depthBufferId);
        gl20.glRenderbufferStorage(GL20.GL_RENDERBUFFER, GL20.GL_DEPTH_COMPONENT16,
                bufferWidth, bufferHeight);
        gl20.glBindRenderbuffer(GL20.GL_RENDERBUFFER, 0);

    }
}
