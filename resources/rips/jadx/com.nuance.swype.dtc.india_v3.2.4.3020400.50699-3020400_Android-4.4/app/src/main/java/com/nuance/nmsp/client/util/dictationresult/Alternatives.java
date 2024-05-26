package com.nuance.nmsp.client.util.dictationresult;

/* loaded from: classes.dex */
public interface Alternatives {
    Alternative getAlternativeAt(int i);

    int getBeginIndexSelection();

    int getEndIndexSelection();

    int size();
}
