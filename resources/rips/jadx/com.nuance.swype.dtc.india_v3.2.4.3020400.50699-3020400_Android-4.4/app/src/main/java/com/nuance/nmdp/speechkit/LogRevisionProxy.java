package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.GenericCommand;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParam;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class LogRevisionProxy extends CommandProxyBase<GenericResult> implements GenericCommand {
    private String _appSessionId;
    private SpeechError _error;
    private GenericCommand.Listener _listener;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LogRevisionProxy(SpeechKitInternal kit, String appSessionId, GenericCommand.Listener listener, Object callbackHandler) {
        super(kit, kit.getLogRevisionCmd(), callbackHandler);
        this._error = null;
        this._appSessionId = appSessionId;
        synchronized (this._listenerSync) {
            this._listener = listener;
        }
        init();
    }

    final void setListener(GenericCommand.Listener listener) {
        SpeechKitInternal.checkArgForNull(listener, "listener");
        synchronized (this._listenerSync) {
            this._listener = listener;
        }
    }

    @Override // com.nuance.nmdp.speechkit.CommandProxyBase
    protected final CommandBase<GenericResult> createCommand(TransactionRunner runner, String name, List<IGenericParam> params) {
        return new LogRevisionCmdImpl(runner, name, this._appSessionId, params) { // from class: com.nuance.nmdp.speechkit.LogRevisionProxy.1
            @Override // com.nuance.nmdp.speechkit.CommandBase
            protected void onError(SpeechError error) {
                LogRevisionProxy.this._error = error;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.nuance.nmdp.speechkit.CommandBase
            public void onResults(final GenericResult result) {
                LogRevisionProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.LogRevisionProxy.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (LogRevisionProxy.this._listener != null) {
                            LogRevisionProxy.this._listener.onComplete(LogRevisionProxy.this, result, LogRevisionProxy.this._error);
                        }
                    }
                });
            }
        };
    }
}
