package com.jonanorman.android.renderclient.opengl.egl14;

import android.opengl.EGLContext;

import com.jonanorman.android.renderclient.opengl.EGLConfigChooser;
import com.jonanorman.android.renderclient.opengl.EGLConfigSimpleChooser;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;

import java.util.Objects;

public class EGL14RenderClientFactory implements GLRenderClient.Factory {
    private EGLContext shareContext;
    private EGLConfigChooser configChooser;

    public EGL14RenderClientFactory() {
    }

    @Override
    public void setShareEGLContext(EGLContext context) {
        this.shareContext = context;
    }

    @Override
    public void setEGLConfigChooser(EGLConfigChooser configChooser) {
        this.configChooser = configChooser;
    }


    @Override
    public GLRenderClient create() {
        EGLConfigChooser chooser = configChooser;
        if (chooser == null) {
            chooser = new EGLConfigSimpleChooser.Builder().build();
        }
        return new EGL14RenderClient(shareContext,
                chooser);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EGL14RenderClientFactory)) return false;
        EGL14RenderClientFactory that = (EGL14RenderClientFactory) o;
        return Objects.equals(shareContext, that.shareContext) && Objects.equals(configChooser, that.configChooser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shareContext, configChooser);
    }
}
