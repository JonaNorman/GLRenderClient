package com.jonanorman.android.renderclient.opengl;

public abstract class GLUniform extends GLVariable {

    private final GLProgram program;

    public GLUniform(GLRenderClient client, GLProgram program, int id, int type, String name, int length) {
        super(client, id, type, name, length);
        this.program = program;
    }

    protected final int dequeueProgramTextureUnit() {
        return program.dequeueTextureUnit();
    }


    @Override
    protected void onClassDispose() {

    }

    @Override
    protected void onClassInit() {

    }



}
