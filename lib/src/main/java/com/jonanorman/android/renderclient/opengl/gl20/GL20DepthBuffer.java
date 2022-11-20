package com.jonanorman.android.renderclient.opengl.gl20;

import com.jonanorman.android.renderclient.opengl.GLDepthBuffer;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;

public class GL20DepthBuffer extends GLDepthBuffer {
    private GL20 gl20;


    public GL20DepthBuffer(GLRenderClient client) {
        super(client);
        gl20 = getGL();
        init();
    }


    @Override
    protected int onDepthBufferCreate() {
        return gl20.glGenRenderbuffer();
    }


    @Override
    protected void onDepthBufferDispose(int id) {
        gl20.glDeleteBuffer(id);
    }

    @Override
    protected void onDepthBufferSize(int bufferId, int bufferWidth, int bufferHeight) {
        gl20.glBindRenderbuffer(GL20.GL_RENDERBUFFER, bufferId);
        gl20.glRenderbufferStorage(GL20.GL_RENDERBUFFER, GL20.GL_DEPTH_COMPONENT16,
                bufferWidth, bufferHeight);
        gl20.glBindRenderbuffer(GL20.GL_RENDERBUFFER, 0);

    }

    @Override
    public String toString() {
        return "GL20DepthBuffer[" +
                "id=" + getBufferId() + "," +
                "width=" + getWidth() + "," +
                "height=" + getHeight() +
                ']';
    }
}
