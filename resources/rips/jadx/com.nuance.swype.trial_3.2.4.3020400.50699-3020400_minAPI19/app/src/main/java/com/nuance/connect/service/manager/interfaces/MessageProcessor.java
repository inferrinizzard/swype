package com.nuance.connect.service.manager.interfaces;

import android.os.Message;

/* loaded from: classes.dex */
public interface MessageProcessor {
    int[] getMessageIDs();

    boolean onHandleMessage(Message message);
}
