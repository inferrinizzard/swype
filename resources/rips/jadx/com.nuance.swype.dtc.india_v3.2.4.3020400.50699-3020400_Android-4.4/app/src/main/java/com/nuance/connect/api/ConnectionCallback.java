package com.nuance.connect.api;

/* loaded from: classes.dex */
public interface ConnectionCallback {
    void onConnected(int i, int i2);

    void onConnectionStatus(int i, String str);

    void onDisconnected(int i);
}
