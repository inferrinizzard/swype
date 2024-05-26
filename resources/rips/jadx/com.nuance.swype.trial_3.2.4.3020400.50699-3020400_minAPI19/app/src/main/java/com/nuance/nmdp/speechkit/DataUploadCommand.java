package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.util.pdx.PdxValue;

/* loaded from: classes.dex */
public interface DataUploadCommand extends ICommand {

    /* loaded from: classes.dex */
    public interface Listener {
        void onError(DataUploadCommand dataUploadCommand, SpeechError speechError);

        void onResults(DataUploadCommand dataUploadCommand, DataUploadResult dataUploadResult);
    }

    @Override // com.nuance.nmdp.speechkit.ICommand
    void addParam(String str, PdxValue.Dictionary dictionary);

    @Override // com.nuance.nmdp.speechkit.ICommand
    void addParam(String str, PdxValue.String string);

    @Override // com.nuance.nmdp.speechkit.ICommand
    void cancel();

    void setListener(Listener listener);

    @Override // com.nuance.nmdp.speechkit.ICommand
    void start();
}
