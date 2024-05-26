package jp.co.omronsoft.openwnn;

import android.content.SharedPreferences;

/* loaded from: classes.dex */
public interface WnnEngine {
    public static final int DICTIONARY_TYPE_LEARN = 1;
    public static final int DICTIONARY_TYPE_USER = 2;

    int addWord(WnnWord wnnWord);

    void breakSequence();

    void close();

    int convert(ComposingText composingText);

    boolean deleteWord(WnnWord wnnWord);

    WnnWord getNextCandidate();

    WnnWord[] getUserDictionaryWords();

    void init();

    boolean initializeDictionary(int i);

    boolean initializeDictionary(int i, int i2);

    boolean learn(WnnWord wnnWord);

    int makeCandidateListOf(int i);

    int predict(ComposingText composingText, int i, int i2);

    int searchWords(String str);

    int searchWords(WnnWord wnnWord);

    void setPreferences(SharedPreferences sharedPreferences);
}
