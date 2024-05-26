package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.util.pdx.PdxValue;

/* loaded from: classes.dex */
public interface VocabularyUploadCommand extends ICommand {

    /* loaded from: classes.dex */
    public interface Listener {
        void onError(VocabularyUploadCommand vocabularyUploadCommand, SpeechError speechError);

        void onResults(VocabularyUploadCommand vocabularyUploadCommand, VocabularyUploadResult vocabularyUploadResult);

        void onSendDone(VocabularyUploadCommand vocabularyUploadCommand);
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
