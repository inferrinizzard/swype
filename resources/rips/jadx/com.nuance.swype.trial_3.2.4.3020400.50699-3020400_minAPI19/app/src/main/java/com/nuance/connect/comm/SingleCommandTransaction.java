package com.nuance.connect.comm;

import android.content.Context;
import com.nuance.connect.comm.Command;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes.dex */
class SingleCommandTransaction implements ResponseCallback, Transaction {
    private static final int MAX_RETRIES = 3;
    private volatile Command c;
    private String downloadFilePath;
    private final String name;
    private final AtomicBoolean processingComplete = new AtomicBoolean(false);
    private final Command.REQUEST_TYPE requestType;
    private final ResponseCallback responder;

    public SingleCommandTransaction(Command command) {
        this.c = command;
        this.name = command.commandFamily + "/" + command.command;
        this.requestType = command.requestType;
        this.responder = command.responseCallback;
        command.responseCallback = this;
    }

    @Override // com.nuance.connect.comm.Transaction
    public boolean allowDuplicates() {
        return this.c != null && this.c.allowDuplicateOfCommand;
    }

    @Override // com.nuance.connect.comm.Transaction
    public void cancel() {
        this.c.canceled = true;
    }

    @Override // com.nuance.connect.comm.Transaction
    public String createDownloadFile(Context context) {
        if (this.downloadFilePath == null) {
            try {
                this.downloadFilePath = File.createTempFile("temp", ".download", context.getCacheDir()).getAbsolutePath();
            } catch (IOException e) {
                this.downloadFilePath = null;
            }
        }
        return this.downloadFilePath;
    }

    @Override // com.nuance.connect.comm.Transaction
    public String getDownloadFile() {
        return this.downloadFilePath;
    }

    @Override // com.nuance.connect.comm.Transaction
    public String getName() {
        return this.name;
    }

    @Override // com.nuance.connect.comm.Transaction
    public Command getNextCommand() {
        if (this.c == null || this.c.canceled) {
            return null;
        }
        return this.c;
    }

    @Override // com.nuance.connect.comm.Transaction
    public PersistantConnectionConfig getPersistantConfig() {
        return null;
    }

    @Override // com.nuance.connect.comm.Transaction
    public int getPriority() {
        return 0;
    }

    @Override // com.nuance.connect.comm.Transaction
    public Command.REQUEST_TYPE getRequestType() {
        return this.requestType;
    }

    @Override // com.nuance.connect.comm.Transaction
    public boolean isSame(Transaction transaction) {
        return transaction.getName().equals(getName());
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public void onCancel(Command command) {
        try {
            if (this.responder != null) {
                this.responder.onCancel(command);
            }
        } finally {
            this.c = null;
        }
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public void onDownloadStatusResponse(Command command, int i, int i2) {
        if (this.responder != null) {
            this.responder.onDownloadStatusResponse(command, i, i2);
        }
    }

    @Override // com.nuance.connect.comm.Transaction
    public void onEndProcessing() {
        if (this.processingComplete.getAndSet(true)) {
            throw new RuntimeException("onEndProcessing can be called at most once.");
        }
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public void onFailure(Command command) {
        try {
            if (this.responder != null) {
                this.responder.onFailure(command);
            }
        } finally {
            this.c = null;
        }
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public void onFileResponse(Response response) {
        try {
            if (this.responder != null) {
                this.responder.onFileResponse(response);
            }
        } finally {
            this.c = null;
        }
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public void onIOExceptionResponse(Command command) {
        try {
            if (this.responder != null) {
                this.responder.onIOExceptionResponse(command);
            }
        } finally {
            this.c = null;
        }
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public void onResponse(Response response) {
        try {
            if (this.responder != null) {
                this.responder.onResponse(response);
            }
        } finally {
            this.c = null;
        }
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public boolean onRetry(Command command, int i, int i2, String str) {
        boolean z = false;
        try {
            if (!this.processingComplete.get()) {
                if (this.c != null && this.responder != null) {
                    z = this.responder.onRetry(command, i, i2, str);
                } else if (command != null && (command.requestType != Command.REQUEST_TYPE.USER || i == 0)) {
                    int i3 = command.retryCount;
                    command.retryCount = i3 + 1;
                    if (i3 <= 3) {
                        z = true;
                    }
                }
            }
            if (z) {
                this.c = command;
            }
            return z;
        } finally {
            this.c = null;
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
        return this.c != null && (this.c.requireDevice || this.c.needDevice);
    }

    @Override // com.nuance.connect.comm.Transaction
    public boolean requiresPersistantConnection() {
        return false;
    }

    @Override // com.nuance.connect.comm.Transaction
    public boolean requiresSessionId() {
        return this.c != null && this.c.requireSession;
    }

    @Override // com.nuance.connect.comm.Transaction
    public boolean wifiOnly() {
        return this.c != null && this.c.wifiOnly;
    }
}
