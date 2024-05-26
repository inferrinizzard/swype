package com.nuance.nmsp.client.util.dictationresult;

/* loaded from: classes.dex */
public interface Sentence {
    void chooseAlternative(Alternative alternative);

    Alternatives getAlternatives(int i, int i2);

    double getConfidenceScore();

    int size();

    String toString();

    Token tokenAt(int i);

    Token tokenAtCursorPosition(int i);

    void updateSentence(Sentence sentence, int i);

    void updateSentence(Sentence sentence, int i, int i2);

    void updateSentence(String str, int i);
}
