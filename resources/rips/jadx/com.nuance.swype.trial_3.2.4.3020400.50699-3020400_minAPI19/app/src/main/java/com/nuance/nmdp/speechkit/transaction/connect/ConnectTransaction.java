package com.nuance.nmdp.speechkit.transaction.connect;

import com.nuance.nmdp.speechkit.transaction.ITransactionListener;
import com.nuance.nmdp.speechkit.transaction.TransactionBase;
import com.nuance.nmdp.speechkit.transaction.TransactionConfig;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;

/* loaded from: classes.dex */
public final class ConnectTransaction extends TransactionBase {
    public ConnectTransaction(Manager mgr, TransactionConfig config, ITransactionListener listener) {
        super(mgr, config, listener);
        this._currentState = new IdleState(this);
    }
}
