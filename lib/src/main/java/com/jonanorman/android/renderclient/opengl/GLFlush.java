package com.jonanorman.android.renderclient.opengl;

public abstract class GLFlush extends GLFunction {


    public GLFlush(GLRenderClient renderClient) {
        super(renderClient);
    }

    public void flush() {
        apply();
    }

    @Override
    protected final void onApply() {

        onFlush();
    }

    protected abstract void onFlush();
}
