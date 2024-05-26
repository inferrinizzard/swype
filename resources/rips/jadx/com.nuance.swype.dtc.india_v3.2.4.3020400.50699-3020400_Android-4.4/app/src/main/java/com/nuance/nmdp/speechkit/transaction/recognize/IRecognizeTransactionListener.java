package com.nuance.nmdp.speechkit.transaction.recognize;

import com.nuance.nmdp.speechkit.transaction.ITransaction;
import com.nuance.nmdp.speechkit.transaction.ITransactionListener;

/* loaded from: classes.dex */
public interface IRecognizeTransactionListener extends ITransactionListener {
    void recordingStarted(ITransaction iTransaction);

    void recordingStopped(ITransaction iTransaction);

    void results(ITransaction iTransaction);
}
