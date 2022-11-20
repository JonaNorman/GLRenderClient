package com.jonanorman.android.renderclient.opengl;

public abstract class GLViewPort extends GLFunction {

    private int x;
    private int y;
    private int width;
    private int height;


    public GLViewPort(GLRenderClient renderClient) {
        super(renderClient);
    }


    @Override
    protected void onApply() {
        onViewPort(x, y, width, height);
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

    protected abstract void onViewPort(int x, int y, int width, int height);

}
