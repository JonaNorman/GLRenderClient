package com.jonanorman.android.renderclient;

public abstract class GLFunction {
    protected final GLRenderClient client;

    public GLFunction(GLRenderClient client) {
        this.client = client;
    }

    public final void call() {
        client.checkRelease();
        onCall();
    }

    protected abstract void onCall();

}
