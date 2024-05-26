package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.LanguageTableRequest;
import com.nuance.nmdp.speechkit.oem.OemCallbackProxyBase;
import com.nuance.nmdp.speechkit.transaction.ITransaction;
import com.nuance.nmdp.speechkit.transaction.ITransactionListener;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParam;
import com.nuance.nmdp.speechkit.util.JobRunner;
import com.nuance.nmdp.speechkit.util.PdxParam;
import com.nuance.nmdp.speechkit.util.pdx.PdxValue;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
final class LanguageTableRequestProxy extends OemCallbackProxyBase implements LanguageTableRequest {
    private final SpeechKitInternal _kit;
    private final LanguageTableRequest.Listener _listener;
    private final List<IGenericParam> _params;
    private final LanguageTableImpl _table;
    private ITransaction _transaction;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LanguageTableRequestProxy(SpeechKitInternal kit, LanguageTableRequest.Listener listener, Object callbackHandler) {
        super(callbackHandler);
        this._table = new LanguageTableImpl();
        this._kit = kit;
        this._listener = listener;
        this._transaction = null;
        this._params = new ArrayList();
    }

    /* renamed from: com.nuance.nmdp.speechkit.LanguageTableRequestProxy$1, reason: invalid class name */
    /* loaded from: classes.dex */
    class AnonymousClass1 implements Runnable {
        AnonymousClass1() {
        }

        @Override // java.lang.Runnable
        public void run() {
            LanguageTableRequestProxy.this._transaction = LanguageTableRequestProxy.this._kit.getRunner().createLanguageTransaction(LanguageTableRequestProxy.this._params, LanguageTableRequestProxy.this._table.getParser(), new ITransactionListener() { // from class: com.nuance.nmdp.speechkit.LanguageTableRequestProxy.1.1
                private SpeechError _error = null;

                @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
                public void error(ITransaction t, int errorCode, String errorText, String suggestion) {
                    this._error = new SpeechErrorImpl(errorCode, errorText, suggestion);
                }

                @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
                public void transactionDone(ITransaction t) {
                    LanguageTableRequestProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.LanguageTableRequestProxy.1.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (C01241.this._error == null) {
                                LanguageTableRequestProxy.this._listener.onResult(LanguageTableRequestProxy.this._table);
                            } else {
                                LanguageTableRequestProxy.this._listener.onError(C01241.this._error);
                            }
                        }
                    });
                }
            });
            if (LanguageTableRequestProxy.this._transaction != null) {
                LanguageTableRequestProxy.this._transaction.start();
            }
        }
    }

    @Override // com.nuance.nmdp.speechkit.LanguageTableRequest, com.nuance.nmdp.speechkit.ICommand
    public final void start() {
        JobRunner.addJob(new AnonymousClass1());
    }

    @Override // com.nuance.nmdp.speechkit.LanguageTableRequest, com.nuance.nmdp.speechkit.ICommand
    public final void cancel() {
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.LanguageTableRequestProxy.2
            @Override // java.lang.Runnable
            public void run() {
                if (LanguageTableRequestProxy.this._transaction != null) {
                    LanguageTableRequestProxy.this._transaction.cancel();
                    LanguageTableRequestProxy.this._transaction = null;
                }
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.LanguageTableRequest, com.nuance.nmdp.speechkit.ICommand
    public final void addParam(String name, PdxValue.Dictionary dictParam) {
        this._params.add(new PdxParam(name, dictParam));
    }

    @Override // com.nuance.nmdp.speechkit.LanguageTableRequest, com.nuance.nmdp.speechkit.ICommand
    public final void addParam(String name, PdxValue.String strParam) {
        this._params.add(new PdxParam(name, strParam));
    }
}
