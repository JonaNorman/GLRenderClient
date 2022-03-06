package com.jonanorman.android.renderclient;


import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GLRenderThread extends HandlerThread {

    final GLRenderClient.Builder builder;
    GLRenderClient renderClient;
    Handler handler;
    volatile boolean release;
    List<BlockingRunnable> blockingRunnableList = new CopyOnWriteArrayList<>();

    volatile Handler.Callback renderCallback;


    public GLRenderThread(GLRenderClient.Builder builder) {
        this(builder, "GLRenderThread");

    }

    public GLRenderThread(GLRenderClient.Builder builder, String threadName) {
        super(threadName);
        this.builder = builder;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        GLRenderClient client = builder.build();
        client.attachCurrentThread();
        synchronized (this) {
            renderClient = client;
            handler = new Handler(Looper.myLooper()) {
                @Override
                public void dispatchMessage(Message msg) {
                    if (renderCallback != null) {
                        if (renderCallback.handleMessage(msg)) {
                            return;
                        }
                    }
                    super.dispatchMessage(msg);
                }
            };
            notifyAll();
        }
    }

    public GLRenderClient getRenderClient() {
        if (release) {
            return renderClient;
        }
        if (!isAlive()) {
            throw new IllegalStateException("please first start");
        }
        synchronized (this) {
            while (!release && renderClient == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return renderClient;
    }

    public Handler getRenderHandler() {
        if (release) {
            return handler;
        }
        if (!isAlive()) {
            throw new IllegalStateException("please first start");
        }
        synchronized (this) {
            while (!release && handler == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return handler;
    }

    public boolean postAndWait(Runnable runnable) {
        return postAndWait(runnable, 0);
    }

    public boolean postAndWait(Runnable runnable, long timeout) {
        if (Looper.myLooper() == handler.getLooper()) {
            runnable.run();
            return true;
        }
        BlockingRunnable br = new BlockingRunnable(runnable);
        blockingRunnableList.add(br);
        return br.postAndWait(handler, timeout);
    }


    public void setRenderCallback(Handler.Callback renderCallback) {
        this.renderCallback = renderCallback;
    }


    @Override
    public void run() {
        try {
            super.run();
        } finally {
            renderClient.release();
            Iterator<BlockingRunnable> iterator = blockingRunnableList.iterator();
            while (iterator.hasNext()) {
                BlockingRunnable runnable = iterator.next();
                synchronized (runnable) {
                    runnable.notifyAll();
                }
            }
            synchronized (this) {
                release = true;
                notifyAll();
            }
        }
    }


    public boolean quitAndWait() {
        boolean quit = quitSafely();
        if (quit) {
            synchronized (this) {
                while (!release && !Thread.currentThread().equals(this)) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
        return quit;
    }


    public final Message obtainMessage() {
        return getRenderHandler().obtainMessage();
    }

    public final Message obtainMessage(int what) {
        return getRenderHandler().obtainMessage(what);
    }

    public final Message obtainMessage(int what, Object obj) {
        return getRenderHandler().obtainMessage(what, obj);
    }

    public final Message obtainMessage(int what, int arg1, int arg2) {
        return getRenderHandler().obtainMessage(what, arg1, arg2);
    }

    public final Message obtainMessage(int what, int arg1, int arg2, Object obj) {
        return getRenderHandler().obtainMessage(what, arg1, arg2, obj);
    }


    public final boolean post(Runnable r) {
        return getRenderHandler().post(r);
    }


    public final boolean postAtTime(Runnable r, long uptimeMillis) {
        return getRenderHandler().postAtTime(r, uptimeMillis);
    }


    public final boolean postAtTime(Runnable r, Object token, long uptimeMillis) {
        return getRenderHandler().postAtTime(r, token, uptimeMillis);
    }


    public final boolean postDelayed(Runnable r, long delayMillis) {
        return getRenderHandler().postDelayed(r, delayMillis);
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    public final boolean postDelayed(Runnable r, Object token, long delayMillis) {
        return getRenderHandler().postDelayed(r, token, delayMillis);
    }


    public final boolean postAtFrontOfQueue(Runnable r) {
        return getRenderHandler().postAtFrontOfQueue(r);
    }


    public final void removeCallbacks(Runnable r) {
        getRenderHandler().removeCallbacks(r);
    }


    public final void removeCallbacks(Runnable r, Object token) {
        getRenderHandler().removeCallbacks(r, token);
    }


    public final boolean sendMessage(@NonNull Message msg) {
        return getRenderHandler().sendMessage(msg);
    }

    public final boolean sendEmptyMessage(int what) {
        return getRenderHandler().sendEmptyMessage(what);
    }


    public final boolean sendEmptyMessageDelayed(int what, long delayMillis) {

        return getRenderHandler().sendEmptyMessageDelayed(what, delayMillis);
    }


    public final boolean sendEmptyMessageAtTime(int what, long uptimeMillis) {
        return getRenderHandler().sendEmptyMessageAtTime(what, uptimeMillis);
    }


    public final boolean sendMessageDelayed(@NonNull Message msg, long delayMillis) {
        return getRenderHandler().sendMessageDelayed(msg, delayMillis);
    }


    public boolean sendMessageAtTime(@NonNull Message msg, long uptimeMillis) {
        return getRenderHandler().sendMessageAtTime(msg, uptimeMillis);
    }


    public final boolean sendMessageAtFrontOfQueue(@NonNull Message msg) {
        return getRenderHandler().sendMessageAtFrontOfQueue(msg);
    }

    public final void removeMessages(int what) {
        getRenderHandler().removeMessages(what);
    }


    public final void removeMessages(int what, @Nullable Object object) {
        getRenderHandler().removeMessages(what, object);
    }


    public final void removeCallbacksAndMessages(@Nullable Object token) {
        getRenderHandler().removeCallbacksAndMessages(token);
    }

    public final boolean hasMessages(int what) {
        return getRenderHandler().hasMessages(what);
    }


    public final boolean hasMessages(int what, @Nullable Object object) {
        return getRenderHandler().hasMessages(what, object);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public final boolean hasCallbacks(@androidx.annotation.NonNull Runnable r) {
        return getRenderHandler().hasCallbacks(r);
    }


    private final class BlockingRunnable implements Runnable {
        private final Runnable mTask;
        private boolean mDone;

        public BlockingRunnable(Runnable task) {
            mTask = task;
        }

        @Override
        public void run() {
            try {
                mTask.run();
            } finally {
                synchronized (this) {
                    mDone = true;
                    notifyAll();
                }
            }
        }

        public boolean postAndWait(Handler handler, long timeout) {
            blockingRunnableList.add(this);
            try {
                if (!handler.post(this)) {
                    return false;
                }
                synchronized (this) {
                    if (timeout > 0) {
                        final long expirationTime = SystemClock.uptimeMillis() + timeout;
                        while (!mDone) {
                            long delay = expirationTime - SystemClock.uptimeMillis();
                            if (delay <= 0) {
                                return false; // timeout
                            }
                            if (release) {
                                return false;
                            }
                            try {
                                wait(delay);
                            } catch (InterruptedException ex) {
                            }
                        }
                    } else {
                        while (!mDone) {
                            if (release) {
                                return false;
                            }
                            try {
                                wait();
                            } catch (InterruptedException ex) {
                            }
                        }
                    }
                }
                return true;
            } finally {
                blockingRunnableList.remove(this);
            }

        }
    }

}
