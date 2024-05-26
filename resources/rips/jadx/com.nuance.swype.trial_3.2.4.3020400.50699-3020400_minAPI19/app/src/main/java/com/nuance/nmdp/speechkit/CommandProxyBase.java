package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.oem.OemCallbackProxyBase;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParam;
import com.nuance.nmdp.speechkit.util.JobRunner;
import com.nuance.nmdp.speechkit.util.PdxParam;
import com.nuance.nmdp.speechkit.util.pdx.PdxValue;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class CommandProxyBase<ResultType> extends OemCallbackProxyBase implements ICommand {
    private CommandBase<ResultType> _command;
    private Runnable _initRunnable;
    private final SpeechKitInternal _kit;
    private final String _name;
    private final List<IGenericParam> _params;

    protected abstract CommandBase<ResultType> createCommand(TransactionRunner transactionRunner, String str, List<IGenericParam> list);

    /* JADX INFO: Access modifiers changed from: package-private */
    public CommandProxyBase(SpeechKitInternal kit, String name, Object callbackHandler) {
        super(callbackHandler);
        this._name = name;
        this._params = new ArrayList();
        this._kit = kit;
        this._initRunnable = new Runnable() { // from class: com.nuance.nmdp.speechkit.CommandProxyBase.1
            @Override // java.lang.Runnable
            public void run() {
                CommandProxyBase.this._command = CommandProxyBase.this.createCommand(CommandProxyBase.this._kit.getRunner(), CommandProxyBase.this._name, CommandProxyBase.this._params);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void init() {
        if (this._initRunnable != null) {
            JobRunner.addJob(this._initRunnable);
            this._initRunnable = null;
        }
    }

    @Override // com.nuance.nmdp.speechkit.ICommand
    public void addParam(String name, PdxValue.Dictionary dictParam) {
        this._params.add(new PdxParam(name, dictParam));
    }

    @Override // com.nuance.nmdp.speechkit.ICommand
    public void addParam(String name, PdxValue.String strParam) {
        this._params.add(new PdxParam(name, strParam));
    }

    @Override // com.nuance.nmdp.speechkit.ICommand
    public void start() {
        this._kit.checkForInvalid();
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.CommandProxyBase.2
            @Override // java.lang.Runnable
            public void run() {
                CommandProxyBase.this._command.start();
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.ICommand
    public void cancel() {
        this._kit.checkForInvalid();
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.CommandProxyBase.3
            @Override // java.lang.Runnable
            public void run() {
                CommandProxyBase.this._command.cancel();
            }
        });
    }
}
