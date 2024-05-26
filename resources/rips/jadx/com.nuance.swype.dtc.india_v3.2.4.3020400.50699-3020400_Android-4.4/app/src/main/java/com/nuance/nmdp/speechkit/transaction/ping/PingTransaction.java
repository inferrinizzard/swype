package com.nuance.nmdp.speechkit.transaction.ping;

import com.nuance.nmdp.speechkit.transaction.ITransactionListener;
import com.nuance.nmdp.speechkit.transaction.TransactionConfig;
import com.nuance.nmdp.speechkit.transaction.generic.GenericTransaction;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;

/* loaded from: classes.dex */
public final class PingTransaction extends GenericTransaction {
    public PingTransaction(Manager mgr, TransactionConfig config, ITransactionListener listener) {
        super(mgr, config, "PING", null, null, listener);
    }
}
