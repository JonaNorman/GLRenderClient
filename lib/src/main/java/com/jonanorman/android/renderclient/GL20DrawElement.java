package com.jonanorman.android.renderclient;


import android.util.ArrayMap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Objects;

class GL20DrawElement extends GLDrawElement {

    private static final ArrayMap<GLDrawMode, Integer> MODE_MAP = new ArrayMap<>();
    private static final int INIT_CAPACITY = 32;
    private static final double MORE_CAPACITY_FACTOR = 1.5;

    static {
        MODE_MAP.put(GLDrawMode.POINTS, GL20.GL_POINTS);
        MODE_MAP.put(GLDrawMode.LINES, GL20.GL_LINES);
        MODE_MAP.put(GLDrawMode.LINE_LOOP, GL20.GL_LINE_LOOP);
        MODE_MAP.put(GLDrawMode.LINE_STRIP, GL20.GL_LINE_STRIP);
        MODE_MAP.put(GLDrawMode.TRIANGLES, GL20.GL_TRIANGLES);
        MODE_MAP.put(GLDrawMode.TRIANGLE_STRIP, GL20.GL_TRIANGLE_STRIP);
        MODE_MAP.put(GLDrawMode.TRIANGLE_FAN, GL20.GL_TRIANGLE_FAN);
    }

    private ByteBuffer dataBuffer;
    private ByteBuffer elementBuffer;
    private int bufferId;
    private int bufferType;
    private GL20 gl;


    public GL20DrawElement(GLRenderClient client) {
        super(client);
        dataBuffer = ByteBuffer.allocateDirect(INIT_CAPACITY).order(ByteOrder.nativeOrder());
        elementBuffer = ByteBuffer.allocateDirect(INIT_CAPACITY).order(ByteOrder.nativeOrder());
        gl = client.getGL20();
    }

    @Override
    protected void onCreate() {
        bufferId = gl.glGenBuffer();
        if (bufferId <= 0) {
            throw new RuntimeException("array buffer create fail");
        }
    }

    @Override
    protected void onDispose() {
        gl.glDeleteBuffer(bufferId);
    }

    @Override
    public void onDraw() {
        GLDrawMode drawMode = getDrawMode();
        if (!elementBuffer.equals(dataBuffer)) {
            if (dataBuffer.limit() > elementBuffer.capacity()) {
                elementBuffer = ByteBuffer.allocateDirect((int) (dataBuffer.limit() * MORE_CAPACITY_FACTOR)).order(ByteOrder.nativeOrder());
            }
            dataBuffer.position(0);
            elementBuffer.clear();
            elementBuffer.put(dataBuffer);
            elementBuffer.flip();
            gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, bufferId);
            gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, elementBuffer.limit(), elementBuffer, GL20.GL_STATIC_DRAW);
            gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
        }

        gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, bufferId);
        int size = elementBuffer.limit();
        if (bufferType == GL20.GL_UNSIGNED_INT) {
            size = size >> 2;
        } else if (bufferType == GL20.GL_UNSIGNED_SHORT) {
            size = size >> 1;
        }
        gl.glDrawElements(MODE_MAP.get(drawMode), size, bufferType, 0);
        gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public void set(byte[] value) {
        bufferType = GL20.GL_UNSIGNED_BYTE;
        int dataSize = value.length;
        if (dataSize > dataBuffer.capacity()) {
            dataBuffer = ByteBuffer.allocateDirect((int) (dataSize * MORE_CAPACITY_FACTOR)).order(ByteOrder.nativeOrder());
        }
        dataBuffer.position(0);
        dataBuffer.put(value);
        dataBuffer.flip();
    }

    @Override
    public void set(int[] value) {
        bufferType = GL20.GL_UNSIGNED_INT;
        int dataSize = value.length * 4;
        if (dataSize > dataBuffer.capacity()) {
            dataBuffer = ByteBuffer.allocateDirect((int) (dataSize * MORE_CAPACITY_FACTOR)).order(ByteOrder.nativeOrder());
        }
        dataBuffer.clear();
        IntBuffer intBuffer = dataBuffer.asIntBuffer();
        intBuffer.put(value);
        dataBuffer.position(0);
        dataBuffer.limit(dataSize);
    }

    @Override
    public void set(short[] value) {
        bufferType = GL20.GL_UNSIGNED_SHORT;
        int dataSize = value.length * 2;
        if (dataSize > dataBuffer.capacity()) {
            dataBuffer = ByteBuffer.allocateDirect((int) (dataSize * MORE_CAPACITY_FACTOR)).order(ByteOrder.nativeOrder());
        }
        dataBuffer.clear();
        ShortBuffer shortBuffer = dataBuffer.asShortBuffer();
        shortBuffer.put(value);
        dataBuffer.position(0);
        dataBuffer.limit(dataSize);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GL20DrawElement)) return false;
        if (!super.equals(o)) return false;
        GL20DrawElement that = (GL20DrawElement) o;
        return Objects.equals(dataBuffer, that.dataBuffer) && Objects.equals(gl, that.gl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dataBuffer, gl);
    }
}
