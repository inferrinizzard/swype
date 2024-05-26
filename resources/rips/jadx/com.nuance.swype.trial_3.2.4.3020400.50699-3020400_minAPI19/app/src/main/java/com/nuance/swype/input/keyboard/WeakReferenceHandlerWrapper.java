package com.nuance.swype.input.keyboard;

import android.os.Handler;
import android.os.Looper;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class WeakReferenceHandlerWrapper<T> extends Handler {
    private final WeakReference<T> ownerInstance;

    public WeakReferenceHandlerWrapper(T ownerInstance) {
        this(ownerInstance, Looper.myLooper());
    }

    public WeakReferenceHandlerWrapper(T ownerInstance, Looper looper) {
        super(looper);
        this.ownerInstance = new WeakReference<>(ownerInstance);
    }

    public T getOwnerInstance() {
        return this.ownerInstance.get();
    }
}
