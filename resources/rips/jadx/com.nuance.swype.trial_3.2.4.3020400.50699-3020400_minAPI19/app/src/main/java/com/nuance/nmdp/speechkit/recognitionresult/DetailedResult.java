package com.nuance.nmdp.speechkit.recognitionresult;

import java.util.List;

/* loaded from: classes.dex */
public interface DetailedResult {
    List<AlternativeChoice> getAlternatives(int i, int i2);

    double getConfidenceScore();

    Token getTokenAtCursorPosition(int i);

    List<Token> getTokens();

    String toString();
}
