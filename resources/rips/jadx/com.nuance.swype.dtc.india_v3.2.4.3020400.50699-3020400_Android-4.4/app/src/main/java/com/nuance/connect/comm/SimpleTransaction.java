package com.nuance.connect.comm;

import android.content.Context;
import com.nuance.connect.comm.Command;
import com.nuance.connect.util.Logger;
import java.io.File;
import java.io.IOException;

/* loaded from: classes.dex */
public class SimpleTransaction extends AbstractTransaction implements ResponseCallback {
    protected String downloadFilePath;
    protected String name;
    protected Command.REQUEST_TYPE requestType;
    protected ResponseCallback responder;
    protected final Command[] commandQueue = new Command[1];
    protected boolean canceled = false;

    public SimpleTransaction(Command command) {
        this.currentCommand = command;
        this.commandQueue[0] = command;
        this.name = command.commandFamily + "/" + command.command;
        this.requestType = command.requestType;
        this.responder = this.currentCommand.responseCallback;
        this.currentCommand.responseCallback = this;
    }

    @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
    public boolean allowDuplicates() {
        if (this.currentCommand == null) {
            return false;
        }
        return this.currentCommand.allowDuplicateOfCommand;
    }

    @Override // com.nuance.connect.comm.Transaction
    public void cancel() {
        this.canceled = true;
        if (this.currentCommand != null) {
            this.currentCommand.canceled = true;
        }
    }

    @Override // com.nuance.connect.comm.Transaction
    public String createDownloadFile(Context context) {
        if (this.downloadFilePath == null) {
            try {
                this.downloadFilePath = File.createTempFile("temp", ".download").getAbsolutePath();
            } catch (IOException e) {
                Logger.getLog(Logger.LoggerType.OEM).e("Unable to create file for download transaction");
            }
        }
        return this.downloadFilePath;
    }

    @Override // com.nuance.connect.comm.Transaction
    public String getName() {
        return this.name;
    }

    @Override // com.nuance.connect.comm.Transaction
    public Command getNextCommand() {
        if (this.canceled) {
            this.commandQueue[0] = null;
        }
        this.currentCommand = this.commandQueue[0];
        this.commandQueue[0] = null;
        if (this.currentCommand == null || this.currentCommand.canceled) {
            return null;
        }
        if (this.currentCommand.responseCallback != this) {
            this.responder = this.currentCommand.responseCallback;
            this.currentCommand.responseCallback = this;
        }
        return this.currentCommand;
    }

    @Override // com.nuance.connect.comm.Transaction
    public int getPriority() {
        return (this.requestType == Command.REQUEST_TYPE.CRITICAL || this.requestType == Command.REQUEST_TYPE.USER) ? 10 : 0;
    }

    @Override // com.nuance.connect.comm.Transaction
    public Command.REQUEST_TYPE getRequestType() {
        return this.requestType;
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public void onCancel(Command command) {
        try {
            if (this.responder != null) {
                this.responder.onCancel(command);
            }
        } finally {
            rollback();
        }
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public void onDownloadStatusResponse(Command command, int i, int i2) {
        if (this.responder != null) {
            this.responder.onDownloadStatusResponse(command, i, i2);
        }
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public void onFailure(Command command) {
        try {
            if (this.responder != null) {
                this.responder.onFailure(command);
            }
        } finally {
            rollback();
        }
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public void onFileResponse(Response response) {
        try {
            if (this.responder != null) {
                this.responder.onFileResponse(response);
            }
        } finally {
            this.commandQueue[0] = null;
            this.currentCommand = null;
        }
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public void onIOExceptionResponse(Command command) {
        try {
            if (this.responder != null) {
                this.responder.onIOExceptionResponse(command);
            }
        } finally {
            rollback();
        }
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public void onResponse(Response response) {
        try {
            if (this.responder != null) {
                this.responder.onResponse(response);
            }
        } finally {
            this.commandQueue[0] = null;
            this.currentCommand = null;
        }
    }

    @Override // com.nuance.connect.comm.ResponseCallback
    public boolean onRetry(Command command, int i, int i2, String str) {
        try {
            boolean onRetry = this.processingComplete.get() ? false : (this.currentCommand == null || this.responder == null) ? (this.currentCommand == null || (getRequestType() == Command.REQUEST_TYPE.USER && i != 0)) ? false : this.retryCount.getAndIncrement() <= 3 : this.responder.onRetry(command, i, i2, str);
            if (onRetry) {
                this.commandQueue[0] = this.currentCommand;
            }
            return onRetry;
        } finally {
            this.commandQueue[0] = null;
            this.currentCommand = null;
        }
    }

    @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
    public boolean requiresDeviceId() {
        return this.currentCommand != null && (this.currentCommand.requireDevice || this.currentCommand.needDevice);
    }

    @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
    public boolean requiresSessionId() {
        if (this.currentCommand == null) {
            return false;
        }
        return this.currentCommand.requireSession;
    }

    @Override // com.nuance.connect.comm.AbstractTransaction
    public void rollback() {
        this.commandQueue[0] = null;
        this.currentCommand = null;
    }

    @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
    public boolean wifiOnly() {
        if (this.currentCommand == null) {
            return false;
        }
        return this.currentCommand.wifiOnly;
    }
}
