package com.jonanorman.android.renderclient.opengl;

import android.opengl.GLException;


public interface GL {

    void addMonitor(GLMonitor monitor);

    void removeMonitor(GLMonitor monitor);

    interface GLMonitor {

        default void onGLCall() {

        }

        default void onGLError(GLException e) {

        }
    }
}
