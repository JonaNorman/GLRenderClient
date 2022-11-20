package com.jonanorman.android.renderclient.math;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Status {


    private static final int STATUS_NEW = 1;
    private static final int STATUS_START = 2;
    private static final int STATUS_CANCEL = 3;
    private static final int STATUS_DONE = 4;

    private int status = STATUS_NEW;

    private final ReentrantLock lock = new ReentrantLock();
    public Condition condition = lock.newCondition();


    public void start() {
        lock.lock();
        try {
            status = STATUS_START;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public void done() {
        lock.lock();
        try {
            status = STATUS_DONE;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }


    public void cancel() {
        lock.lock();
        try {
            status = STATUS_CANCEL;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }


    public boolean isStarted() {
        lock.lock();
        try {
            return status == STATUS_START;
        } finally {
            lock.unlock();
        }
    }

    public boolean isDone() {
        boolean done;
        lock.lock();
        try {
            done = status == STATUS_DONE;
        } finally {
            lock.unlock();
        }
        return done;
    }


    public boolean isCanceled() {
        boolean cancel;
        lock.lock();
        try {
            cancel = status == STATUS_CANCEL;

        } finally {
            lock.unlock();
        }
        return cancel;
    }


    public boolean waitDone() {
        lock.lock();
        try {
            for (; ; ) {
                try {
                    if (status == STATUS_NEW || status == STATUS_START) {
                        condition.await();
                    } else {
                        return status == STATUS_DONE;
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
