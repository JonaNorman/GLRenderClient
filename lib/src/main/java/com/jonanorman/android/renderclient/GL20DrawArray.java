package com.jonanorman.android.renderclient;

import android.util.ArrayMap;

import java.util.Objects;

class GL20DrawArray extends GLDrawArray {

    private static final ArrayMap<GLDrawMode, Integer> MODE_MAP = new ArrayMap<>();

    static {
        MODE_MAP.put(GLDrawMode.POINTS, GL20.GL_POINTS);
        MODE_MAP.put(GLDrawMode.LINES, GL20.GL_LINES);
        MODE_MAP.put(GLDrawMode.LINE_LOOP, GL20.GL_LINE_LOOP);
        MODE_MAP.put(GLDrawMode.LINE_STRIP, GL20.GL_LINE_STRIP);
        MODE_MAP.put(GLDrawMode.TRIANGLES, GL20.GL_TRIANGLES);
        MODE_MAP.put(GLDrawMode.TRIANGLE_STRIP, GL20.GL_TRIANGLE_STRIP);
        MODE_MAP.put(GLDrawMode.TRIANGLE_FAN, GL20.GL_TRIANGLE_FAN);
    }

    private GL20 gl;

    public GL20DrawArray(GLRenderClient client) {
        super(client);
        gl = client.getGL20();
    }

    @Override
    protected void onDispose() {

    }

    @Override
    protected void onCreate() {

    }


    @Override
    public void onDraw() {
        GLDrawMode drawMode = getDrawMode();
        int vertexStart = getVertexStart();
        int vertexCount = getVertexCount();
        if (drawMode == null) {
            throw new IllegalArgumentException("drawMode is null");
        }
        if (vertexStart < 0) {
            throw new IllegalArgumentException("vertexStart is <0");
        }
        if (vertexCount <= 0) {
            throw new IllegalArgumentException("vertexCount is <=0");
        }
        gl.glDrawArrays(MODE_MAP.get(drawMode), vertexStart, vertexCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GL20DrawArray)) return false;
        if (!super.equals(o)) return false;
        GL20DrawArray that = (GL20DrawArray) o;
        return Objects.equals(gl, that.gl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gl);
    }
}
