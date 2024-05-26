package com.nuance.connect.comm;

/* loaded from: classes.dex */
interface ConnectorCallback {
    int getDefaultDelay();

    String getDeviceId();

    String getSessionId();

    void onAccountInvalidated();

    void onDeviceInvalidated();

    void onDownloadStatus(Command command, int i, int i2);

    void onFailure(Command command, int i, String str);

    void onResponse(Response response);

    void onSessionInvalidated();

    void onSuccess(Command command);

    void onUnlicensed(int i);
}
