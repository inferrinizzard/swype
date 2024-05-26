package com.nuance.connect.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class WeakReferenceHandler {

    /* loaded from: classes.dex */
    private static class WeakReferenceHandlerCallback implements Handler.Callback {
        private final WeakReference<Handler.Callback> callbackRef;

        public WeakReferenceHandlerCallback(Handler.Callback callback) {
            this.callbackRef = new WeakReference<>(callback);
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            Handler.Callback callback = this.callbackRef.get();
            if (callback != null) {
                return callback.handleMessage(message);
            }
            return true;
        }
    }

    private WeakReferenceHandler() {
    }

    public static Handler create(Handler.Callback callback) {
        return new Handler(new WeakReferenceHandlerCallback(callback));
    }

    public static Handler create(Looper looper, Handler.Callback callback) {
        return new Handler(looper, new WeakReferenceHandlerCallback(callback));
    }
}
