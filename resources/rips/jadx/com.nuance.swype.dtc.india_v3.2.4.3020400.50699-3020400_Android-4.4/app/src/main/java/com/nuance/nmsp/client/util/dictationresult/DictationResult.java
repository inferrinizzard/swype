package com.nuance.nmsp.client.util.dictationresult;

import java.util.Hashtable;

/* loaded from: classes.dex */
public interface DictationResult {
    Hashtable getExtraInformation();

    Sentence sentenceAt(int i);

    int size();

    String toString();

    void update(byte[] bArr) throws IllegalArgumentException;
}
