package com.jonanorman.android.renderclient;

import java.util.HashMap;
import java.util.Map;

public abstract class GLObject implements GLRenderClientReleaseListener{

    protected final GLRenderClient client;

    private boolean disposed;
    private boolean created;

    private final Map<Class, GLMethod> methodMap = new HashMap<>();

    public GLObject(GLRenderClient client) {
        this.client = client;
        this.client.addClientReleaseListener(this);
    }


    public final boolean isDisposed() {
        return disposed;
    }

    public final boolean isCreated() {
        return created;
    }

    public <T extends GLMethod> T findMethod(Class<T> cls) {
        return (T) methodMap.get(cls);
    }

    public <T extends GLMethod> void registerMethod(Class<T> cls, T method) {
        methodMap.put(cls, method);
    }

    public <T extends GLMethod> void unRegisterMethod(Class<T> cls) {
        methodMap.remove(cls);
    }

    public final void create() {
        if (created) {
            return;
        }
        if (disposed) {
            throw new IllegalStateException(getClass() + "it is disposed");
        }
        created = true;
        client.checkThread();
        onCreate();
    }


    public final void dispose() {
        if (!created) {
            disposed = true;
            created = false;
            return;
        }
        if (disposed) {
            return;
        }
        disposed = true;
        created = false;
        onDispose();
    }

    @Override
    public void onClientRelease(GLRenderClient renderClient) {
        dispose();
    }

    protected abstract void onCreate();

    protected abstract void onDispose();


    public abstract class GLMethod extends GLFunction {

        public GLMethod() {
            super(GLObject.this.client);
        }

        @Override
        protected final void onCall() {
            GLObject.this.create();
            onCallMethod();
        }

        protected abstract void onCallMethod();
    }
}
