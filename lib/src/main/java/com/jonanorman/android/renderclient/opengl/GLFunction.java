package com.jonanorman.android.renderclient.opengl;


public abstract class GLFunction {

    private final GLRenderClient client;
    private final GL gl;


    public GLFunction(GLRenderClient renderClient) {
        if (renderClient == null) {
            throw new IllegalStateException(this + " client is null");
        }
        this.client = renderClient;
        this.gl = renderClient.getGL();
    }

    public final <T extends GL> T getGL() {
        return (T) gl;
    }

    public final GLRenderClient getClient() {
        return client;
    }

    public final void apply() {
        client.checkRender();
        onApply();
    }


    protected abstract void onApply();


}
