package com.byteplay.android.renderclient;

public abstract class GLDrawElement extends GLDraw {
    public GLDrawElement(GLRenderClient client) {
        super(client);
    }

    public abstract void set(byte[] value);

    public abstract void set(int[] value);

    public abstract void set(short[] value);

    @Override
    public final GLDrawType getDrawType() {
        return GLDrawType.DRAW_ELEMENT;
    }
}
