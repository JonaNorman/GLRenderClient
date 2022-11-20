package com.jonanorman.android.renderclient.opengl;

import java.util.ArrayList;
import java.util.List;


public abstract class GLDispose {

    private final GLRenderClient client;
    private final GL gl;
    private final List<OnDisposeListener> disposeListenerList;
    private boolean disposed;

    private GLRenderClient.OnClientReleaseListener releaseListener = new GLRenderClient.OnClientReleaseListener() {
        @Override
        public void onClientRelease(GLRenderClient renderClient) {
            dispose();
        }
    };


    public GLDispose(GLRenderClient client) {
        if (client == null) {
            throw new IllegalStateException(this + " client is null");
        }
        this.client = client;
        this.disposeListenerList = new ArrayList<>();
        this.gl = client.getGL();
        this.client.addClientReleaseListener(releaseListener);
        this.client.checkRelease();
    }


    public final GLRenderClient getClient() {
        return client;
    }

    public <T extends GL> T getGL() {
        return (T) gl;
    }

    public void checkDispose() {
        if (isDisposed()) {
            throw new IllegalStateException(this + " is disposed");
        }
    }

    public final void dispose() {
        if (disposed) {
            return;
        }
        this.client.checkRelease();
        this.client.removeClientReleaseListener(releaseListener);
        onDispose();
        while (!disposeListenerList.isEmpty()) {
            OnDisposeListener disposeListener = disposeListenerList.remove(0);
            disposeListener.onDispose(this);
        }
        disposed = true;
    }

    public void addOnDisposeListener(OnDisposeListener disposeListener) {
        if (disposeListenerList.contains(disposeListener)) {
            return;
        }
        disposeListenerList.add(disposeListener);
    }

    public void removeOnDisposeListener(OnDisposeListener disposeListener) {
        if (!disposeListenerList.contains(disposeListener)) {
            return;
        }
        disposeListenerList.remove(disposeListener);
    }


    public final boolean isDisposed() {
        return disposed;
    }

    protected abstract void onDispose();

    public interface OnDisposeListener {
        void onDispose(GLDispose dispose);
    }
}
