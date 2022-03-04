package com.jonanorman.android.renderclient;

import java.util.Objects;

public abstract class GLDrawArray extends GLDraw {
    private int vertexStart;
    private int vertexCount = 4;

    public GLDrawArray(GLRenderClient client) {
        super(client);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GLDrawArray)) return false;
        if (!super.equals(o)) return false;
        GLDrawArray that = (GLDrawArray) o;
        return vertexStart == that.vertexStart && vertexCount == that.vertexCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), vertexStart, vertexCount);
    }

    @Override
    public final GLDrawType getDrawType() {
        return GLDrawType.DRAW_ARRAY;
    }
}
