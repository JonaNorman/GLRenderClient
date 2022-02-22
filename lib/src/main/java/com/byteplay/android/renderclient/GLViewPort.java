package com.byteplay.android.renderclient;

import java.util.Objects;

public abstract class GLViewPort extends GLFunction {
    private int x;
    private int y;
    private int width;
    private int height;

    public GLViewPort(GLRenderClient client) {
        super(client);
    }

    public void set(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GLViewPort)) return false;
        GLViewPort viewPort = (GLViewPort) o;
        return x == viewPort.x && y == viewPort.y && width == viewPort.width && height == viewPort.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }
}
