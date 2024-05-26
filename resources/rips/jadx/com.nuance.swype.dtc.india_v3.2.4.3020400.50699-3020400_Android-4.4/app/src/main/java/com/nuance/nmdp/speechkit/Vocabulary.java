package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.util.dataupload.VocabularyType;
import java.util.List;

/* loaded from: classes.dex */
public interface Vocabulary<Type extends VocabularyType> {

    /* loaded from: classes.dex */
    public interface Listener {
        void onError(Vocabulary<?> vocabulary, SpeechError speechError);

        void onResults(Vocabulary<?> vocabulary, DataUploadResult dataUploadResult);

        void onSendBegin();

        void onSendDone(Vocabulary<?> vocabulary);
    }

    boolean add(Type type);

    void deleteAll(Listener listener, Object obj);

    boolean exists(Type type);

    long getChecksum();

    long getDataCount();

    List<Type> getItemList();

    String getLanguage();

    String getName();

    String getType();

    boolean remove(Type type);

    void upload(Listener listener, Object obj);
}
