package com.nuance.connect.internal;

import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.StringUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public class HandlerRegistry {
    Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    final Map<String, ConnectHandler> handlers = new HashMap();
    final SparseArray<WeakReference<ConnectHandler>> messageArray = new SparseArray<>();

    private void mapMessages(ConnectHandler connectHandler) {
        if (connectHandler.getMessageIDs() == null) {
            return;
        }
        for (int i : connectHandler.getMessageIDs()) {
            if (this.messageArray.indexOfKey(i) >= 0) {
                this.log.w("More than one handler is processing this message: ", Integer.valueOf(i));
            }
            this.messageArray.put(i, new WeakReference<>(connectHandler));
        }
    }

    public HandlerRegistry add(ConnectHandler connectHandler) {
        if (!this.handlers.containsKey(connectHandler.getHandlerName())) {
            this.handlers.put(connectHandler.getHandlerName(), connectHandler);
            mapMessages(connectHandler);
        }
        return this;
    }

    public HandlerRegistry add(String str) {
        if (!this.handlers.containsKey(str)) {
            this.handlers.put(str, null);
        }
        return this;
    }

    public ConnectHandler getHandlerByName(String str) {
        return this.handlers.get(str);
    }

    public String getRegisteredHandlers() {
        return StringUtils.listToString(new ArrayList(this.handlers.keySet()), ",");
    }

    public boolean hasHandler(String str) {
        return this.handlers.containsKey(str);
    }

    public void postUpgrade() {
        Iterator<ConnectHandler> it = this.handlers.values().iterator();
        while (it.hasNext()) {
            it.next().onPostUpgrade();
        }
    }

    public boolean tryHandleMessage(Handler handler, Message message) {
        ConnectHandler connectHandler;
        if (this.messageArray.indexOfKey(message.what) < 0 || (connectHandler = this.messageArray.get(message.what).get()) == null) {
            return false;
        }
        connectHandler.handleMessage(handler, message);
        return true;
    }
}
