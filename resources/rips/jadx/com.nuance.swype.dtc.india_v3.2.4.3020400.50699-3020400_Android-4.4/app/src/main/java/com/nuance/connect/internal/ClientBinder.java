package com.nuance.connect.internal;

import android.os.Message;
import com.nuance.connect.internal.common.InternalMessages;

/* loaded from: classes.dex */
public interface ClientBinder {
    long getLastMessageSent();

    int[] getLock();

    boolean isBound();

    void pause();

    void restart();

    boolean sendConnectMessage(Message message);

    boolean sendConnectMessage(InternalMessages internalMessages);

    boolean sendConnectMessage(InternalMessages internalMessages, Object obj);

    boolean sendConnectMessage(InternalMessages internalMessages, Object obj, int i, int i2);

    boolean sendConnectPriorityMessage(Message message);

    boolean sendConnectPriorityMessage(InternalMessages internalMessages);

    boolean sendConnectPriorityMessage(InternalMessages internalMessages, Object obj);

    boolean sendConnectPriorityMessage(InternalMessages internalMessages, Object obj, int i, int i2);

    void setClientComplete();

    void start();

    void stop();
}
