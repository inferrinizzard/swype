package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.util.pdx.PdxValue;

/* loaded from: classes.dex */
interface LanguageTableRequest extends ICommand {

    /* loaded from: classes.dex */
    public interface Listener {
        void onError(SpeechError speechError);

        void onResult(LanguageTable languageTable);
    }

    @Override // com.nuance.nmdp.speechkit.ICommand
    void addParam(String str, PdxValue.Dictionary dictionary);

    @Override // com.nuance.nmdp.speechkit.ICommand
    void addParam(String str, PdxValue.String string);

    @Override // com.nuance.nmdp.speechkit.ICommand
    void cancel();

    @Override // com.nuance.nmdp.speechkit.ICommand
    void start();
}
