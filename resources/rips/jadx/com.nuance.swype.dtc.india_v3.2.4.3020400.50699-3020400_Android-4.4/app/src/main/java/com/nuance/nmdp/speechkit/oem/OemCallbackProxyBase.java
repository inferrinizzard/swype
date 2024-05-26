package com.nuance.nmdp.speechkit.oem;

import android.os.Handler;
import com.nuance.nmdp.speechkit.util.Logger;

/* loaded from: classes.dex */
public abstract class OemCallbackProxyBase {
    private final Handler _handler;
    public final Object _listenerSync = new Object();

    public OemCallbackProxyBase(Object callbackHandler) {
        this._handler = (Handler) callbackHandler;
    }

    public final void callback(Runnable r) {
        if (this._handler == null) {
            try {
                r.run();
                return;
            } catch (Throwable tr) {
                Logger.error(this, "Exception in application callback", tr);
                return;
            }
        }
        try {
            if (!this._handler.post(r)) {
                Logger.error(this, "Unable to post callback to handler");
            }
        } catch (Throwable tr2) {
            Logger.error(this, "Exception posting callback to handler", tr2);
        }
    }
}
