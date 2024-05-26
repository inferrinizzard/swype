package com.nuance.connect.internal;

import android.os.Handler;
import android.os.Message;

/* loaded from: classes.dex */
public interface ConnectHandler {
    String getHandlerName();

    int[] getMessageIDs();

    void handleMessage(Handler handler, Message message);

    void onPostUpgrade();
}
