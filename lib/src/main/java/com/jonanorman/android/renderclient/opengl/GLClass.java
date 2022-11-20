package com.jonanorman.android.renderclient.opengl;

import java.util.HashMap;
import java.util.Map;

public abstract class GLClass extends GLDispose {

    private final Map<Class, GLMethod> methodMap;

    private boolean init;
    private boolean external;

    public GLClass(GLRenderClient client) {
        super(client);
        this.methodMap = new HashMap<>();
        this.onRegisterMethod();
    }

    protected final void markExternal() {
        init = true;
        external = true;
    }

    public final void init() {
        checkDispose();
        if (init) {
            return;
        }
        init = true;
        getClient().checkRender();
        this.onClassInit();
    }

    public boolean isInit() {
        return init;
    }

    public boolean isAvailable() {
        return isInit() && !isDisposed();
    }

    @Override
    protected final void onDispose() {
        if (!external) {
            return;
        }
        getClient().checkRender();
        onClassDispose();
    }


    protected abstract void onRegisterMethod();

    protected abstract void onClassInit();

    protected abstract void onClassDispose();

    public <T extends GLMethod> T findMethod(Class cls) {
        return (T) methodMap.get(cls);
    }

    public <T extends GLMethod> void registerMethod(T method) {
        methodMap.put(method.getClass(), method);
    }

    public <T extends GLMethod> void registerMethod(Class tClass, T method) {
        methodMap.put(tClass, method);
    }

    public <T extends GLMethod> void unRegisterMethod(Class cls) {
        methodMap.remove(cls);
    }


    public <T extends GLMethod> void unRegisterMethod(T cls) {
        methodMap.remove(cls.getClass());
    }


    public abstract class GLMethod extends GLFunction {
        public GLMethod() {
            super(GLClass.this.getClient());
            registerMethod(GLMethod.this);
        }


        @Override
        protected final void onApply() {
            checkDispose();
            onMethodCall();
        }

        protected abstract void onMethodCall();
    }
}
