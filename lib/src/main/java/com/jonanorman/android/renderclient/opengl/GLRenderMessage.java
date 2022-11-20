package com.jonanorman.android.renderclient.opengl;


import android.os.Build;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.RequiresApi;

import com.jonanorman.android.renderclient.opengl.egl14.EGL14RenderClientFactory;
import com.jonanorman.android.renderclient.util.MessageHandler;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class GLRenderMessage {

    private static final String GL_RENDER_THREAD_NAME = "GLRenderMessage";

    private final MessageHandler messageHandler;
    private final Object lock = new Object();
    private GLRenderClient renderClient;
    private boolean release;

    GLRenderMessage(GLRenderClient.Factory factory) {
        this(factory, null);
    }

    GLRenderMessage(GLRenderClient.Factory factory, Handler.Callback callback) {
        this.messageHandler = MessageHandler.obtain(GL_RENDER_THREAD_NAME, callback);
        this.messageHandler.addHandlerRecycleCallback(new MessageHandler.RecycleCallback() {
            @Override
            public void onHandlerRecycle(MessageHandler handler) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (lock) {
                            renderClient.release();
                            release = true;
                            lock.notifyAll();
                        }
                    }
                });
            }
        });
        this.messageHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    GLRenderClient client = factory.create();
                    client.attachCurrentThread();
                    renderClient = client;
                    lock.notifyAll();
                }
            }
        });
    }

    public MessageHandler getHandler() {
        return messageHandler;
    }

    public boolean recycleSafe() {
        return messageHandler.recycleSafe();
    }

    public boolean recycleSafeAndWait() {
        return messageHandler.recycleSafeAndWait();
    }

    public boolean recycle() {
        return messageHandler.recycle();
    }

    public boolean recycleAndWait() {
        return messageHandler.recycleAndWait();
    }


    public final boolean post(Runnable r) {
        return messageHandler.post(r);
    }


    public final boolean postAtTime(Runnable r, long uptimeMillis) {
        return messageHandler.postAtTime(r, uptimeMillis);
    }


    public final boolean postAtTime(Runnable r, Object token, long uptimeMillis) {
        return messageHandler.postAtTime(r, token, uptimeMillis);
    }


    public final boolean postDelayed(Runnable r, long delayMillis) {
        return messageHandler.postDelayed(r, delayMillis);
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    public final boolean postDelayed(Runnable r, Object token, long delayMillis) {
        return messageHandler.postDelayed(r, token, delayMillis);
    }


    public final boolean postAtFrontOfQueue(Runnable r) {
        return messageHandler.postAtFrontOfQueue(r);
    }


    public final void removeCallbacks(Runnable r) {
        messageHandler.removeCallbacks(r);
    }


    public final void removeCallbacks(Runnable r, Object token) {
        messageHandler.removeCallbacks(r, token);
    }

    public final boolean sendMessage(Message msg) {
        return messageHandler.sendMessage(msg);
    }


    public final boolean sendEmptyMessage(int what) {
        return messageHandler.sendEmptyMessage(what);
    }


    public final boolean sendEmptyMessageDelayed(int what, long delayMillis) {
        return messageHandler.sendEmptyMessageDelayed(what, delayMillis);
    }


    public final boolean sendEmptyMessageAtTime(int what, long uptimeMillis) {
        return messageHandler.sendEmptyMessageAtTime(what, uptimeMillis);
    }

    public final boolean sendMessageDelayed(Message msg, long delayMillis) {
        return messageHandler.sendMessageDelayed(msg, delayMillis);
    }


    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        return messageHandler.sendMessageAtTime(msg, uptimeMillis);
    }


    public final boolean sendMessageAtFrontOfQueue(Message msg) {
        return messageHandler.sendMessageAtFrontOfQueue(msg);
    }


    public final void removeMessages(int what) {
        messageHandler.removeMessages(what);
    }

    public final void removeMessages(int what, Object object) {
        messageHandler.removeMessages(what, object);
    }


    public final void removeCallbacksAndMessages(Object token) {
        messageHandler.removeCallbacksAndMessages(token);
    }


    public final boolean hasMessages(int what) {
        return messageHandler.hasMessages(what);
    }

    public final boolean hasMessagesOrCallbacks() {
        return messageHandler.hasMessagesOrCallbacks();
    }


    public final boolean hasMessages(int what, Object object) {
        return messageHandler.hasMessages(what, object);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public final boolean hasCallbacks(Runnable r) {
        return messageHandler.hasCallbacks(r);
    }

    public boolean postAndWait(Runnable runnable) {
        return messageHandler.postAndWait(runnable, 0);
    }

    public boolean postAndWait(Runnable runnable, long timeout) {
        return messageHandler.postAndWait(runnable, timeout);
    }

    public boolean postAtFrontOfQueueAndWait(Runnable runnable) {
        return messageHandler.postAtFrontOfQueueAndWait(runnable);
    }

    public boolean postAtFrontOfQueueAndWait(Runnable runnable, long timeout) {

        return messageHandler.postAtFrontOfQueueAndWait(runnable, timeout);
    }

    public boolean execute(Runnable runnable) {
        return messageHandler.execute(runnable);
    }

    public <T> Future<T> submit(Callable<T> callable) {
        return messageHandler.submit(callable);
    }


    public boolean postAndWait() {
        return messageHandler.postAndWait();
    }

    public boolean removeAllMessageAndWait() {
        return messageHandler.removeAllMessageAndWait();
    }


    public void removeAllMessage() {
        messageHandler.removeAllMessage();
    }


    public boolean isRecycle() {
        return messageHandler.isRecycle();
    }

    public GLRenderClient getRenderClient() {
        synchronized (lock) {
            while (!release && renderClient == null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                }
            }
            return renderClient;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        recycle();
    }

    public int getAutoMessageId() {
        return messageHandler.getAutoMessageId();
    }

    public void addRecycleCallback(MessageHandler.RecycleCallback recycleCallback) {
        messageHandler.addHandlerRecycleCallback(recycleCallback);
    }

    public void removeRecycleCallback(MessageHandler.RecycleCallback callback) {
        messageHandler.addHandlerRecycleCallback(callback);
    }

    public void addHandlerCallback(Handler.Callback callback) {
        messageHandler.addHandlerCallback(callback);
    }

    public void removeHandlerCallback(Handler.Callback callback) {
        messageHandler.removeHandlerCallback(callback);
    }

    public static GLRenderMessage obtain() {
        return obtain(null, null);
    }

    public static GLRenderMessage obtain(GLRenderClient.Factory factory) {
        return new GLRenderMessage(factory, null);
    }

    public static GLRenderMessage obtain(Handler.Callback callback) {
        return obtain(null, callback);
    }

    public static GLRenderMessage obtain(GLRenderClient.Factory factory, Handler.Callback callback) {
        if (factory == null) {
            factory = new EGL14RenderClientFactory();
        }
        return new GLRenderMessage(factory, callback);
    }
}
