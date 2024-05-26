package com.nuance.swype.util;

import android.os.Handler;
import android.os.Message;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public abstract class SafeMessageHandler<T> extends Handler {
    private final WeakReference<T> mReference;

    public abstract void handleMessage(Message message, T t);

    public SafeMessageHandler(T reference) {
        this.mReference = new WeakReference<>(reference);
    }

    @Override // android.os.Handler
    public final void handleMessage(Message msg) {
        T parent = this.mReference.get();
        if (parent != null) {
            handleMessage(msg, parent);
        }
    }
}
