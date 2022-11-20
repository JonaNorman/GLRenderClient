package com.jonanorman.android.renderclient.opengl;

public abstract class GLDrawElement extends GLDraw {


    public GLDrawElement(GLRenderClient client) {
        super(client);
    }

    public abstract void set(byte[] value);

    public abstract void set(int[] value);

    public abstract void set(short[] value);

    @Override
    public final Type getType() {
        return Type.DRAW_ELEMENT;
    }
}
