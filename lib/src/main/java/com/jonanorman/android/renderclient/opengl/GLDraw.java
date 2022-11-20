package com.jonanorman.android.renderclient.opengl;

public abstract class GLDraw extends GLClass {

    private Mode drawMode = Mode.TRIANGLE_STRIP;

    private GLDrawMethod drawMethod;


    public GLDraw(GLRenderClient client) {
        super(client);
    }

    @Override
    protected void onRegisterMethod() {
        drawMethod = new GLDrawMethod();
    }

    public final void draw() {
        drawMethod.apply();
    }

    public void setMode(Mode drawMode) {
        this.drawMode = drawMode;
    }

    public abstract void onDraw();

    public abstract Type getType();


    public Mode getMode() {
        return drawMode;
    }


    class GLDrawMethod extends GLMethod {

        public GLDrawMethod() {
            super();
        }

        @Override
        protected void onMethodCall() {
            onDraw();
        }
    }

    public enum Mode {
        POINTS,
        LINES,
        LINE_LOOP,
        LINE_STRIP,
        TRIANGLES,
        TRIANGLE_STRIP,
        TRIANGLE_FAN;
    }

    public enum Type {
        DRAW_ARRAY,
        DRAW_ELEMENT
    }
}
