package com.nuance.connect.comm;

/* loaded from: classes.dex */
public interface ResponseCallback {
    void onCancel(Command command);

    void onDownloadStatusResponse(Command command, int i, int i2);

    void onFailure(Command command);

    void onFileResponse(Response response);

    void onIOExceptionResponse(Command command);

    void onResponse(Response response);

    boolean onRetry(Command command, int i, int i2, String str);
}
