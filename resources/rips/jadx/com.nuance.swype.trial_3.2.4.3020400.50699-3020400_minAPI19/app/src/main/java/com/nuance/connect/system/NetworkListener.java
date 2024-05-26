package com.nuance.connect.system;

/* loaded from: classes.dex */
public interface NetworkListener {
    void onNetworkAvailable();

    void onNetworkDisconnect();

    void onNetworkUnavailable();
}
