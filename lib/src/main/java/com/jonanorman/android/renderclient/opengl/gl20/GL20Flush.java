package com.jonanorman.android.renderclient.opengl.gl20;

import com.jonanorman.android.renderclient.opengl.GLFlush;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;

public class GL20Flush extends GLFlush {
    private GL20 gl20;

    public GL20Flush(GLRenderClient renderClient) {
        super(renderClient);
        gl20 = getGL();
    }

    @Override
    protected void onFlush() {
        gl20.glFlush();
    }
}
