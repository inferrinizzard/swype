package com.nuance.nmdp.speechkit.transaction;

import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult;

/* loaded from: classes.dex */
public interface ITransactionState {
    void cancel();

    void commandCreated(String str);

    void commandEvent(short s);

    void createCommandFailed();

    void enter();

    void leave();

    void queryError(int i, String str);

    void queryResult(QueryResult queryResult);

    void queryRetry(String str, String str2);

    void start();
}
