package com.nuance.connect.comm;

import android.content.Context;

/* loaded from: classes.dex */
public interface MessageSendingBus {
    Context getContext();

    String getDeviceId();

    String getDeviceRegisterCommand();

    String getSessionCreateCommand();

    String getSessionId();

    boolean isLicensed();
}
