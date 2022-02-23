package com.byteplay.android.renderclient;

import java.util.Objects;

public abstract class GLDraw extends GLObject {

    private GLDrawMode drawMode = GLDrawMode.TRIANGLE_STRIP;


    public GLDraw(GLRenderClient client) {
        super(client);
        registerMethod(GLDrawMethod.class, new GLDrawMethod());
    }


    public final void draw() {
        findMethod(GLDrawMethod.class).call();
    }

    public void setDrawMode(GLDrawMode drawMode) {
        this.drawMode = drawMode;
    }

    public abstract void onDraw();

    public abstract GLDrawType getDrawType();

    class GLDrawMethod extends GLMethod {

        public GLDrawMethod() {
            super();
        }

        @Override
        protected void onCallMethod() {
            onDraw();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GLDraw)) return false;
        GLDraw glDraw = (GLDraw) o;
        return drawMode == glDraw.drawMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(drawMode);
    }

    public GLDrawMode getDrawMode() {
        return drawMode;
    }
}
