package com.jonanorman.android.renderclient.util;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Printer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageHandler {


    private static final String MESSAGE_HANDLER_PREFIX = "MessageHandler";
    private static final String ALL_MESSAGE_PREFIX = MESSAGE_HANDLER_PREFIX + "(Total messages: ";

    private final Object lock = new Object();
    private final MessageThreadPool threadPool;
    private final MessageThread thread;
    private final List<RecycleCallback> recycleCallbackList;
    private final List<Handler.Callback> handlerCallbackList;
    private final Handler handler;
    private boolean recycle = false;
    private AtomicInteger autoMessageId = new AtomicInteger();

    MessageHandler(MessageThreadPool threadPool, String threadName) {
        this(threadPool, threadName, null);
    }

    MessageHandler(MessageThreadPool threadPool, String threadName, Handler.Callback callback) {
        super();
        this.threadPool = threadPool;
        thread = threadPool.obtain(threadName);
        handler = new Handler(thread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                for (Handler.Callback handleCallback : handlerCallbackList) {
                    if (handleCallback.handleMessage(msg)) {
                        return true;
                    }
                }
                return false;
            }
        });
        recycleCallbackList = new CopyOnWriteArrayList<>();
        handlerCallbackList = new CopyOnWriteArrayList<>();
        addHandlerCallback(callback);
    }


    public final boolean post(Runnable r) {
        if (isRecycle()) {
            return false;
        }
        return handler.post(r);
    }


    public final boolean postAtTime(Runnable r, long uptimeMillis) {
        if (isRecycle()) {
            return false;
        }
        return handler.postAtTime(r, uptimeMillis);
    }


    public final boolean postAtTime(Runnable r, Object token, long uptimeMillis) {
        if (isRecycle()) {
            return false;
        }
        return handler.postAtTime(r, token, uptimeMillis);
    }


    public final boolean postDelayed(Runnable r, long delayMillis) {
        if (isRecycle()) {
            return false;
        }
        return handler.postDelayed(r, delayMillis);
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    public final boolean postDelayed(Runnable r, Object token, long delayMillis) {
        if (isRecycle()) {
            return false;
        }
        return handler.postDelayed(r, token, delayMillis);
    }


    public final boolean postAtFrontOfQueue(Runnable r) {
        if (isRecycle()) {
            return false;
        }
        return handler.postAtFrontOfQueue(r);
    }


    public final void removeCallbacks(Runnable r) {
        if (isRecycle()) {
            return;
        }
        handler.removeCallbacks(r);
    }


    public final void removeCallbacks(Runnable r, Object token) {
        if (isRecycle()) {
            return;
        }
        handler.removeCallbacks(r, token);
    }

    public final boolean sendMessage(Message msg) {
        if (isRecycle()) {
            return false;
        }
        return handler.sendMessage(msg);
    }


    public final boolean sendEmptyMessage(int what) {
        if (isRecycle()) {
            return false;
        }
        return handler.sendEmptyMessage(what);
    }


    public final boolean sendEmptyMessageDelayed(int what, long delayMillis) {
        if (isRecycle()) {
            return false;
        }
        return handler.sendEmptyMessageDelayed(what, delayMillis);
    }


    public final boolean sendEmptyMessageAtTime(int what, long uptimeMillis) {
        if (isRecycle()) {
            return false;
        }
        return handler.sendEmptyMessageAtTime(what, uptimeMillis);
    }

    public final boolean sendMessageDelayed(Message msg, long delayMillis) {
        if (isRecycle()) {
            return false;
        }
        return handler.sendMessageDelayed(msg, delayMillis);
    }


    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        if (isRecycle()) {
            return false;
        }
        return handler.sendMessageAtTime(msg, uptimeMillis);
    }


    public final boolean sendMessageAtFrontOfQueue(Message msg) {
        if (isRecycle()) {
            return false;
        }
        return handler.sendMessageAtFrontOfQueue(msg);
    }


    public final void removeMessages(int what) {
        if (isRecycle()) {
            return;
        }
        handler.removeMessages(what);
    }

    public final void removeMessages(int what, Object object) {
        if (isRecycle()) {
            return;
        }
        handler.removeMessages(what, object);
    }


    public final void removeCallbacksAndMessages(Object token) {
        if (isRecycle()) {
            return;
        }
        handler.removeCallbacksAndMessages(token);
    }


    public final boolean hasMessages(int what) {
        if (isRecycle()) {
            return false;
        }
        return handler.hasMessages(what);
    }

    public final boolean hasMessagesOrCallbacks() {
        if (isRecycle()) {
            return false;
        }
        AtomicInteger count = new AtomicInteger();
        handler.getLooper().dump(new Printer() {
            @Override
            public void println(String str) {
                boolean startsWith = str.startsWith(ALL_MESSAGE_PREFIX);
                if (startsWith) {
                    StringBuffer buffer = new StringBuffer();
                    for (int i = ALL_MESSAGE_PREFIX.length(); i < str.length(); i++) {
                        char chr = str.charAt(i);
                        if (chr >= 48 && chr <= 57) {
                            buffer.append(chr);
                        } else if (chr == 44) {
                            break;
                        }
                    }
                    if (buffer.length() > 0) {
                        count.set(Integer.valueOf(buffer.toString()));
                    }
                }
            }
        }, MESSAGE_HANDLER_PREFIX);
        return count.get() > 0;
    }


    public final boolean hasMessages(int what, Object object) {
        if (isRecycle()) {
            return false;
        }
        return handler.hasMessages(what, object);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public final boolean hasCallbacks(Runnable r) {
        if (isRecycle()) {
            return false;
        }
        return handler.hasCallbacks(r);
    }

    public boolean postAndWait(Runnable runnable) {
        return postAndWait(runnable, 0);
    }

    public boolean postAndWait(Runnable runnable, long timeout) {
        if (isRecycle()) {
            return false;
        }
        if (Looper.myLooper() == handler.getLooper()) {
            runnable.run();
            return true;
        }
        MessageThread handlerThread = (MessageThread) handler.getLooper().getThread();
        MessageThread.BlockRunnable br = handlerThread.new BlockRunnable(handler, runnable);
        return br.postAndWait(timeout);
    }

    public boolean postAtFrontOfQueueAndWait(Runnable runnable) {
        return postAtFrontOfQueueAndWait(runnable, 0);
    }

    public boolean postAtFrontOfQueueAndWait(Runnable runnable, long timeout) {
        if (isRecycle()) {
            return false;
        }
        if (Looper.myLooper() == handler.getLooper()) {
            runnable.run();
            return true;
        }
        MessageThread handlerThread = (MessageThread) handler.getLooper().getThread();
        MessageThread.BlockRunnable br = handlerThread.new BlockRunnable(handler, runnable);
        return br.postAndWait(timeout, true);
    }

    public boolean execute(Runnable runnable) {
        if (isRecycle()) {
            return false;
        }
        if (Looper.myLooper() == handler.getLooper()) {
            runnable.run();
            return true;
        }
        return handler.post(runnable);
    }

    public <T> Future<T> submit(Callable<T> callable) {
        RunnableFuture<T> task = new FutureTask<>(callable);
        if (!post(task)) task.cancel(true);
        return task;
    }


    public boolean postAndWait() {
        return postAndWait(() -> {
        });
    }

    public boolean removeAllMessageAndWait() {
        return postAtFrontOfQueueAndWait(new Runnable() {
            @Override
            public void run() {
                removeAllMessage();
            }
        });
    }


    public void removeAllMessage() {
        removeCallbacksAndMessages(null);
    }


    public boolean recycleSafe() {
        synchronized (lock) {
            if (recycle) {
                return false;
            }
            recycle = true;
            Iterator<RecycleCallback> iterator = recycleCallbackList.iterator();
            while (iterator.hasNext()) {
                RecycleCallback callback = iterator.next();
                recycleCallbackList.remove(callback);
                callback.onHandlerRecycle(this);
            }
            this.threadPool.recycle(thread);
            return true;
        }
    }

    public boolean recycleSafeAndWait() {
        return postAndWait(new Runnable() {
            @Override
            public void run() {
                recycleSafe();
            }
        });
    }

    public boolean recycle() {
        removeAllMessage();
        return recycleSafe();
    }

    public boolean recycleAndWait() {
        return postAtFrontOfQueueAndWait(new Runnable() {
            @Override
            public void run() {
                recycle();
            }
        });
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        recycle();
    }

    public boolean isRecycle() {
        synchronized (lock) {
            return recycle;
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public static MessageHandler obtain() {
        return obtain((String) null);
    }

    public static MessageHandler obtain(String threadName) {
        return obtain(threadName, null);
    }

    public static MessageHandler obtain(Handler.Callback callback) {
        return obtain((String) null, callback);
    }

    public static MessageHandler obtain(String threadName, Handler.Callback callback) {
        MessageThreadPool pool = MessageThreadPool.get();
        return new MessageHandler(pool, threadName, callback);
    }

    public static MessageHandler obtain(MessageThreadPool pool) {
        return obtain(pool, null, null);
    }

    public static MessageHandler obtain(MessageThreadPool pool, String threadName) {
        return obtain(pool, threadName, null);
    }

    public static MessageHandler obtain(MessageThreadPool pool, Handler.Callback callback) {
        return obtain(pool, null, callback);
    }


    public static MessageHandler obtain(MessageThreadPool pool, String threadName, Handler.Callback callback) {
        return new MessageHandler(pool, threadName, callback);
    }


    public void addHandlerRecycleCallback(RecycleCallback recycleCallback) {
        if (recycleCallbackList.contains(recycleCallback)) {
            return;
        }
        recycleCallbackList.add(recycleCallback);
    }

    public void removeHandlerRecycleCallback(RecycleCallback callback) {
        if (!recycleCallbackList.contains(callback)) {
            return;
        }
        recycleCallbackList.remove(callback);
    }

    public void addHandlerCallback(Handler.Callback callback) {
        if (callback == null || handlerCallbackList.contains(callback)) {
            return;
        }
        handlerCallbackList.add(callback);
    }

    public void removeHandlerCallback(Handler.Callback callback) {
        if (callback == null || !handlerCallbackList.contains(callback)) {
            return;
        }
        handlerCallbackList.remove(callback);
    }

    public int getAutoMessageId() {
        return autoMessageId.incrementAndGet();
    }

    public interface RecycleCallback {
        void onHandlerRecycle(MessageHandler handler);
    }


}
