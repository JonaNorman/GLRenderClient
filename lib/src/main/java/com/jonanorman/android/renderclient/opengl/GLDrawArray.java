package com.jonanorman.android.renderclient.opengl;

public abstract class GLDrawArray extends GLDraw {
    private int vertexStart;
    private int vertexCount = 4;

    public GLDrawArray(GLRenderClient client) {
        super(client);
    }

    @Override
    protected void onClassDispose() {

    }

    @Override
    protected void onClassInit() {

    }

    public void setVertexStart(int vertexStart) {
        this.vertexStart = vertexStart;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getVertexStart() {
        return vertexStart;
    }

    @Override
    public final Type getType() {
        return Type.DRAW_ARRAY;
    }
}
