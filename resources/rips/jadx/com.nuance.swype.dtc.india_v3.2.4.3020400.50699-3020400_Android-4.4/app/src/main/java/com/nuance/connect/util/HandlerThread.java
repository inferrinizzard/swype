package com.nuance.connect.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import java.util.concurrent.CountDownLatch;

/* loaded from: classes.dex */
public abstract class HandlerThread extends Thread {
    private volatile Handler handler;
    private Looper mLooper;
    private int mTid;
    private final Handler.Callback processCallback;
    private volatile CountDownLatch readySignal;
    private final Handler resultHandler;

    public HandlerThread() {
        this(null);
    }

    public HandlerThread(Handler.Callback callback) {
        this.mTid = -1;
        this.processCallback = new Handler.Callback() { // from class: com.nuance.connect.util.HandlerThread.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message message) {
                Message handleMessage = HandlerThread.this.handleMessage(message);
                if (handleMessage == null || HandlerThread.this.resultHandler == null) {
                    return false;
                }
                if (handleMessage.getWhen() != 0) {
                    handleMessage = Message.obtain(handleMessage);
                }
                handleMessage.setTarget(HandlerThread.this.resultHandler);
                HandlerThread.this.resultHandler.sendMessage(handleMessage);
                return false;
            }
        };
        if (callback != null) {
            this.resultHandler = WeakReferenceHandler.create(callback);
        } else {
            this.resultHandler = null;
        }
    }

    private void initHandler() {
        if (this.handler == null) {
            this.handler = WeakReferenceHandler.create(this.processCallback);
        }
    }

    public Looper getLooper() {
        Looper looper;
        if (!isAlive()) {
            return null;
        }
        synchronized (this) {
            while (isAlive() && this.mLooper == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
            looper = this.mLooper;
        }
        return looper;
    }

    public int getThreadId() {
        return this.mTid;
    }

    public abstract Message handleMessage(Message message);

    public void process(int i, int i2, int i3, Object obj) {
        Message.obtain(this.handler, i, i2, i3, obj).sendToTarget();
    }

    public void process(int i, Object obj) {
        Message.obtain(this.handler, i, obj).sendToTarget();
    }

    public void process(Runnable runnable) {
        this.handler.post(runnable);
    }

    public void processDelayed(int i, long j) {
        this.handler.sendEmptyMessageDelayed(i, j);
    }

    public void processDelayed(Runnable runnable, long j, boolean z) {
        if (z) {
            this.handler.removeCallbacks(runnable);
        }
        this.handler.postDelayed(runnable, j);
    }

    public boolean quit() {
        Looper looper = getLooper();
        if (looper == null) {
            return false;
        }
        looper.quit();
        return true;
    }

    public void removeMessages(int i) {
        this.handler.removeMessages(i);
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        this.mTid = Process.myTid();
        Looper.prepare();
        initHandler();
        synchronized (this) {
            this.mLooper = Looper.myLooper();
            notifyAll();
        }
        this.readySignal.countDown();
        Looper.loop();
    }

    @Override // java.lang.Thread
    public void start() {
        try {
            this.readySignal = new CountDownLatch(1);
            super.start();
            this.readySignal.await();
        } catch (InterruptedException e) {
            initHandler();
        }
    }
}
