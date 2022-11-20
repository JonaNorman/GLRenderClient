package com.jonanorman.android.renderclient.opengl.gl20;

import android.util.ArrayMap;

import com.jonanorman.android.renderclient.opengl.GLDrawArray;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;

public final class GL20DrawArray extends GLDrawArray {

    private static final ArrayMap<Mode, Integer> MODE_MAP = new ArrayMap<>();

    static {
        MODE_MAP.put(Mode.POINTS, GL20.GL_POINTS);
        MODE_MAP.put(Mode.LINES, GL20.GL_LINES);
        MODE_MAP.put(Mode.LINE_LOOP, GL20.GL_LINE_LOOP);
        MODE_MAP.put(Mode.LINE_STRIP, GL20.GL_LINE_STRIP);
        MODE_MAP.put(Mode.TRIANGLES, GL20.GL_TRIANGLES);
        MODE_MAP.put(Mode.TRIANGLE_STRIP, GL20.GL_TRIANGLE_STRIP);
        MODE_MAP.put(Mode.TRIANGLE_FAN, GL20.GL_TRIANGLE_FAN);
    }

    private GL20 gl20;

    public GL20DrawArray(GLRenderClient client) {
        super(client);
        gl20 = getGL();
        init();
    }

    @Override
    public void onDraw() {
        Mode drawMode = getMode();
        int vertexStart = getVertexStart();
        int vertexCount = getVertexCount();
        if (drawMode == null) {
            throw new IllegalArgumentException(this + " drawMode is null");
        }
        if (vertexStart < 0) {
            throw new IllegalArgumentException(this + " vertexStart is <0");
        }
        if (vertexCount <= 0) {
            throw new IllegalArgumentException(this + " vertexCount is <=0");
        }
        gl20.glDrawArrays(MODE_MAP.get(drawMode), vertexStart, vertexCount);
    }

    @Override
    public String toString() {
        return "GL20DrawArray[" +
                "vertexStart=" + getVertexStart() + "," +
                "vertexCount=" + getVertexCount() + "," +
                "drawMode=" + getMode() +
                "]";
    }
}
