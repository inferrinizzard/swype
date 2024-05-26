package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.DataUploadCommand;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParam;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class DataUploadProxy extends CommandProxyBase<DataUploadResult> implements DataUploadCommand {
    private boolean _isReady;
    private DataUploadCommand.Listener _listener;

    public DataUploadProxy(SpeechKitInternal kit, DataUploadCommand.Listener listener, Object callbackHandler) {
        super(kit, kit.getDataUploadCmd(), callbackHandler);
        synchronized (this._listenerSync) {
            this._listener = listener;
        }
        this._isReady = false;
        init();
    }

    @Override // com.nuance.nmdp.speechkit.DataUploadCommand
    public final void setListener(DataUploadCommand.Listener listener) {
        SpeechKitInternal.checkArgForNull(listener, "listener");
        synchronized (this._listenerSync) {
            this._listener = listener;
        }
    }

    @Override // com.nuance.nmdp.speechkit.CommandProxyBase
    protected final CommandBase<DataUploadResult> createCommand(TransactionRunner runner, String name, List<IGenericParam> params) {
        return new DataUploadCmdImpl(runner, name, params) { // from class: com.nuance.nmdp.speechkit.DataUploadProxy.1
            @Override // com.nuance.nmdp.speechkit.CommandBase
            protected void onError(final SpeechError error) {
                DataUploadProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.DataUploadProxy.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (DataUploadProxy.this._listener != null) {
                            DataUploadProxy.this._listener.onError(DataUploadProxy.this, error);
                        }
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.nuance.nmdp.speechkit.CommandBase
            public void onResults(final DataUploadResult result) {
                DataUploadProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.DataUploadProxy.1.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (DataUploadProxy.this._listener != null) {
                            DataUploadProxy.this._listener.onResults(DataUploadProxy.this, result);
                        }
                    }
                });
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setReady() {
        this._isReady = true;
    }

    @Override // com.nuance.nmdp.speechkit.CommandProxyBase, com.nuance.nmdp.speechkit.ICommand
    public final void start() {
        if (this._isReady) {
            super.start();
        }
    }
}
