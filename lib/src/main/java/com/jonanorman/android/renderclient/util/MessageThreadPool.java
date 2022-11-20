package com.jonanorman.android.renderclient.util;

import android.os.Handler;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import com.jonanorman.android.renderclient.math.TimeStamp;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageThreadPool {

    private final static String DEFAULT_POOL_NAME = "DefaultPool";
    private final static String DEFAULT_THREAD_NAME = "MessageThread";

    private final static Map<String, MessageThreadPool> POOL_MAP = new ConcurrentHashMap<>();


    public static final int CACHE_POOL_SIZE = 5;
    public static final int CACHE_SECOND = 10;
    public static final int INTERVAL_SECOND = 1;
    private final LinkedList<MessageThread> threadCache = new LinkedList<>();
    private final Object threadLock = new Object();
    private final Object cleanLock = new Object();
    private volatile int poolCacheSize = CACHE_POOL_SIZE;
    private volatile TimeStamp maxCacheTime = TimeStamp.ofSeconds(CACHE_SECOND);
    private volatile TimeStamp intervalCleanTime = TimeStamp.ofSeconds(INTERVAL_SECOND);
    private final Map<String, AtomicInteger> map = new ConcurrentHashMap<>();
    private boolean cleanIng;


    MessageThreadPool() {
    }

    public void setCacheTime(TimeStamp maxCacheTime) {
        this.maxCacheTime = maxCacheTime;
    }

    public void setPoolCacheSize(int poolSize) {
        this.poolCacheSize = poolSize;
    }

    public void setIntervalCleanTime(TimeStamp intervalCleanTime) {
        this.intervalCleanTime = intervalCleanTime;
    }

    MessageThread obtain() {
        return obtain(null);
    }

    MessageThread obtain(String name) {
        if (name == null) {
            name = DEFAULT_THREAD_NAME;
        }
        AtomicInteger id = getNameId(name);
        synchronized (threadLock) {
            cleanCacheThread();
            name = name + id.incrementAndGet();
            MessageThread handlerThread = threadCache.pollLast();
            if (handlerThread != null) {
                handlerThread.setName(name);
                return handlerThread;
            }
            handlerThread = new MessageThread(name);
            handlerThread.start();
            return handlerThread;
        }
    }

    @NonNull
    private AtomicInteger getNameId(String name) {
        AtomicInteger id = map.get(name);
        if (id == null) {
            id = new AtomicInteger();
            map.put(name, id);
        }
        return id;
    }


    void recycle(MessageThread handlerThread) {
        if (handlerThread.isTerminated()) {
            return;
        }
        synchronized (threadLock) {
            threadCache.remove(handlerThread);
            handlerThread.cacheTime = SystemClock.elapsedRealtime();
            threadCache.addLast(handlerThread);
            AtomicInteger threadId = getNameId(handlerThread.getName());
            threadId.decrementAndGet();
        }
        startCleanThread();
    }

    private void cleanCacheThread() {
        synchronized (threadLock) {
            Iterator<MessageThread> iterator = threadCache.iterator();
            while (iterator.hasNext()) {
                MessageThread thread = iterator.next();
                long duration = SystemClock.elapsedRealtime() - thread.cacheTime;
                if (thread.isTerminated()
                        || threadCache.size() > poolCacheSize
                        || duration > maxCacheTime.toMillis()) {
                    iterator.remove();
                    thread.quitSafely();
                }
            }
        }
    }

    private boolean isEmptyCacheThread() {
        synchronized (threadLock) {
            return threadCache.isEmpty();
        }
    }

    private void startCleanThread() {
        synchronized (cleanLock) {
            if (cleanIng) {
                return;
            }
            cleanIng = true;
        }
        MessageThread handlerThread = obtain();
        String name = handlerThread.getName();
        handlerThread.setName("CleanHandlerThread");
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                cleanCacheThread();
                if (isEmptyCacheThread()) {
                    handlerThread.setName(name);
                    recycle(handlerThread);
                    synchronized (cleanLock) {
                        cleanIng = false;
                    }
                } else {
                    handler.postDelayed(this, intervalCleanTime.toMillis());
                }
            }
        });
    }

    public static MessageThreadPool get(String name) {
        MessageThreadPool pool = POOL_MAP.get(name);
        if (pool != null) {
            return pool;
        }
        pool = new MessageThreadPool();
        POOL_MAP.put(name, pool);
        return pool;
    }

    public static MessageThreadPool get() {
        return get(DEFAULT_POOL_NAME);
    }

}
