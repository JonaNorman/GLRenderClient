package com.jonanorman.android.renderclient;

public abstract class GLUniform extends GLVariable {

    public GLUniform(GLRenderClient client, GLProgram program, int id, int type, String name, int length) {
        super(client,program , id, type, name, length);
    }


}
