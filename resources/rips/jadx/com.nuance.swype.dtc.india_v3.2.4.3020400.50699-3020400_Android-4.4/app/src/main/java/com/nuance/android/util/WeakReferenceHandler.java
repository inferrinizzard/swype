package com.nuance.android.util;

import android.os.Handler;
import android.os.Message;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public final class WeakReferenceHandler {
    public static Handler create(Handler.Callback callback) {
        return new Handler(new WeakReferenceHandlerCallback(callback));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class WeakReferenceHandlerCallback implements Handler.Callback {
        private final WeakReference<Handler.Callback> callbackRef;

        public WeakReferenceHandlerCallback(Handler.Callback callback) {
            this.callbackRef = new WeakReference<>(callback);
        }

        @Override // android.os.Handler.Callback
        public final boolean handleMessage(Message msg) {
            Handler.Callback callback = this.callbackRef.get();
            if (callback != null) {
                return callback.handleMessage(msg);
            }
            return true;
        }
    }
}
