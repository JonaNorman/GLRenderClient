package com.jonanorman.android.renderclient.util;

import android.os.Handler;
import android.os.SystemClock;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class MessageThread extends android.os.HandlerThread {

    final List<BlockRunnable> blockingList = new CopyOnWriteArrayList<>();
    volatile boolean looperTerminated;
    long cacheTime;

    MessageThread(String name) {
        super(name);
    }

    public boolean isTerminated() {
        return looperTerminated || getState() == State.TERMINATED;
    }

    @Override
    public void run() {
        super.run();
        notifyLoopTerminated();
        notifyBlockingRunnable();
    }


    private void notifyLoopTerminated() {
        synchronized (this) {
            looperTerminated = true;
            notifyAll();
        }
    }

    private void notifyBlockingRunnable() {
        Iterator<BlockRunnable> iterator = blockingList.iterator();
        while (iterator.hasNext()) {
            BlockRunnable runnable = iterator.next();
            blockingList.remove(runnable);
            synchronized (runnable) {
                runnable.notifyAll();
            }
        }
    }

    final class BlockRunnable implements Runnable {
        private final Runnable task;
        private boolean done;
        private Handler handler;


        public BlockRunnable(Handler handler, Runnable task) {
            this.handler = handler;
            this.task = task;
        }

        @Override
        public void run() {
            try {
                task.run();
            } finally {
                synchronized (this) {
                    done = true;
                    notifyAll();
                }
            }
        }

        public boolean postAndWait(long timeout) {
            return postAndWait(timeout, false);
        }

        public boolean postAndWait(long timeout, boolean atFront) {
            try {
                blockingList.add(this);
                if (atFront ? !handler.postAtFrontOfQueue(this) : !handler.post(this)) {
                    return false;
                }
                synchronized (this) {
                    if (timeout > 0) {
                        final long expirationTime = SystemClock.uptimeMillis() + timeout;
                        while (!done) {
                            long delay = expirationTime - SystemClock.uptimeMillis();
                            if (delay <= 0) {
                                return false;
                            }
                            if (isTerminated()) {
                                return false;
                            }
                            try {
                                wait(delay);
                            } catch (InterruptedException ex) {
                                return false;
                            }
                        }
                    } else {
                        while (!done) {
                            if (isTerminated()) {
                                return false;
                            }
                            try {
                                wait();
                            } catch (InterruptedException ex) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            } finally {
                blockingList.remove(this);
            }
        }
    }
}
