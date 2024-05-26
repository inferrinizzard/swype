package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.GenericCommand;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParam;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ResetUserProfileProxy extends CommandProxyBase<GenericResult> implements GenericCommand {
    private SpeechError _error;
    private GenericCommand.Listener _listener;

    public ResetUserProfileProxy(SpeechKitInternal kit, GenericCommand.Listener listener, Object callbackHandler) {
        super(kit, kit.getResetUerProfileCmd(), callbackHandler);
        this._error = null;
        synchronized (this._listenerSync) {
            this._listener = listener;
        }
        init();
    }

    public final void setListener(GenericCommand.Listener listener) {
        SpeechKitInternal.checkArgForNull(listener, "listener");
        synchronized (this._listenerSync) {
            this._listener = listener;
        }
    }

    @Override // com.nuance.nmdp.speechkit.CommandProxyBase
    protected final CommandBase<GenericResult> createCommand(TransactionRunner runner, String name, List<IGenericParam> params) {
        return new ResetUserProfileCmdImpl(runner, name) { // from class: com.nuance.nmdp.speechkit.ResetUserProfileProxy.1
            @Override // com.nuance.nmdp.speechkit.CommandBase
            protected void onError(SpeechError error) {
                ResetUserProfileProxy.this._error = error;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.nuance.nmdp.speechkit.CommandBase
            public void onResults(final GenericResult result) {
                ResetUserProfileProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.ResetUserProfileProxy.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (ResetUserProfileProxy.this._listener != null) {
                            ResetUserProfileProxy.this._listener.onComplete(ResetUserProfileProxy.this, result, ResetUserProfileProxy.this._error);
                        }
                    }
                });
            }
        };
    }
}
