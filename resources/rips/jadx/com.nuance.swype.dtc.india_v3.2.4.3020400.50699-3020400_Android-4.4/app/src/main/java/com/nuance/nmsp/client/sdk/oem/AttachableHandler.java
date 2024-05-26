package com.nuance.nmsp.client.sdk.oem;

import android.os.Handler;
import android.os.Message;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class AttachableHandler extends Handler {
    private Handler a;
    private final ArrayList<a> b = new ArrayList<>();

    /* loaded from: classes.dex */
    class a {
        final Message a;
        final long b;

        a(AttachableHandler attachableHandler, Message message, long j) {
            this.a = message;
            this.b = j;
        }
    }

    public synchronized void attachToCurrentThread() {
        this.a = new Handler();
        if (this.b.size() > 0) {
            Iterator<a> it = this.b.iterator();
            while (it.hasNext()) {
                a next = it.next();
                this.a.sendMessageAtTime(next.a, next.b);
            }
            this.b.clear();
        }
    }

    @Override // android.os.Handler
    public synchronized boolean sendMessageAtTime(Message message, long j) {
        boolean z = true;
        synchronized (this) {
            if (this.a == null) {
                this.b.add(new a(this, message, j));
            } else if (this.a.getLooper().getThread().isAlive()) {
                z = this.a.sendMessageAtTime(message, j);
            }
        }
        return z;
    }
}
