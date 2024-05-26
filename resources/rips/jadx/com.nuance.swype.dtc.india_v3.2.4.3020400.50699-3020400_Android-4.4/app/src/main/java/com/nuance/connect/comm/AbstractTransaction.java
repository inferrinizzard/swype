package com.nuance.connect.comm;

import com.nuance.connect.comm.Command;
import com.nuance.connect.util.Logger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public abstract class AbstractTransaction implements Transaction {
    protected static final int MAX_RETRIES = 3;
    public volatile Command currentCommand;
    private final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    public final AtomicInteger retryCount = new AtomicInteger(0);
    protected final AtomicBoolean processingComplete = new AtomicBoolean(false);

    /* loaded from: classes.dex */
    protected abstract class AbstractResponseCallback implements ResponseCallback {
        public AbstractResponseCallback() {
        }

        @Override // com.nuance.connect.comm.ResponseCallback
        public void onCancel(Command command) {
            AbstractTransaction.this.log.d("onCancel transaction:" + AbstractTransaction.this.getName());
            if (AbstractTransaction.this.currentCommand != null) {
                AbstractTransaction.this.currentCommand.canceled = true;
            }
            rollback();
            AbstractTransaction.this.currentCommand = null;
        }

        @Override // com.nuance.connect.comm.ResponseCallback
        public void onDownloadStatusResponse(Command command, int i, int i2) {
            AbstractTransaction.this.log.d("onDownloadStatusResponse ");
        }

        @Override // com.nuance.connect.comm.ResponseCallback
        public void onFailure(Command command) {
            AbstractTransaction.this.log.d("onFailure transaction: " + AbstractTransaction.this.getName());
            AbstractTransaction.this.currentCommand = null;
            rollback();
        }

        @Override // com.nuance.connect.comm.ResponseCallback
        public void onFileResponse(Response response) {
            AbstractTransaction.this.log.d("onFileResponse");
            throw new UnsupportedOperationException("onFileResponse not overloaded (If called, this needs to be overridden)");
        }

        @Override // com.nuance.connect.comm.ResponseCallback
        public void onIOExceptionResponse(Command command) {
            AbstractTransaction.this.currentCommand = null;
            rollback();
        }

        @Override // com.nuance.connect.comm.ResponseCallback
        public boolean onRetry(Command command, int i, int i2, String str) {
            AbstractTransaction.this.log.d("onRetry transaction:", AbstractTransaction.this.getName(), toString(), " delay: ", Integer.valueOf(i), " statusCode: ", Integer.valueOf(i2), " statusMessage: " + str, " retryCount: ", Integer.valueOf(AbstractTransaction.this.retryCount.get()));
            if (AbstractTransaction.this.currentCommand == null || AbstractTransaction.this.processingComplete.get() || (AbstractTransaction.this.getRequestType() == Command.REQUEST_TYPE.USER && i != 0)) {
                AbstractTransaction.this.log.d((Object) "retry: ", (Object) false);
                return false;
            }
            AbstractTransaction.this.log.d("retry: ", Boolean.valueOf(AbstractTransaction.this.retryCount.get() <= 3));
            return AbstractTransaction.this.retryCount.getAndIncrement() <= 3;
        }

        public void rollback() {
            AbstractTransaction.this.rollback();
        }
    }

    @Override // com.nuance.connect.comm.Transaction
    public boolean allowDuplicates() {
        return false;
    }

    @Override // com.nuance.connect.comm.Transaction
    public String getDownloadFile() {
        return null;
    }

    @Override // com.nuance.connect.comm.Transaction
    public PersistantConnectionConfig getPersistantConfig() {
        return null;
    }

    @Override // com.nuance.connect.comm.Transaction
    public boolean isSame(Transaction transaction) {
        return getName().equals(transaction.getName());
    }

    @Override // com.nuance.connect.comm.Transaction
    public void onEndProcessing() {
        if (this.processingComplete.getAndSet(true)) {
            throw new RuntimeException("onEndProcessing can be called at most once.");
        }
    }

    @Override // com.nuance.connect.comm.Transaction
    public boolean onTransactionOfflineQueued() {
        return true;
    }

    @Override // com.nuance.connect.comm.Transaction
    public void onTransactionRejected(int i) {
        if (this.processingComplete.get()) {
            throw new RuntimeException("onTransactionRejected() cannot be called after onEndProcessing()");
        }
    }

    @Override // com.nuance.connect.comm.Transaction
    public boolean requiresDeviceId() {
        return true;
    }

    @Override // com.nuance.connect.comm.Transaction
    public boolean requiresPersistantConnection() {
        return false;
    }

    @Override // com.nuance.connect.comm.Transaction
    public boolean requiresSessionId() {
        return true;
    }

    public abstract void rollback();

    @Override // com.nuance.connect.comm.Transaction
    public boolean wifiOnly() {
        return false;
    }
}
