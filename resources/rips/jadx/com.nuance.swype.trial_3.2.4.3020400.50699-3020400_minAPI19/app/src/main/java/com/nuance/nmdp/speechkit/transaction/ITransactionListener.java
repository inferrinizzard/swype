package com.nuance.nmdp.speechkit.transaction;

/* loaded from: classes.dex */
public interface ITransactionListener {
    void error(ITransaction iTransaction, int i, String str, String str2);

    void transactionDone(ITransaction iTransaction);
}
