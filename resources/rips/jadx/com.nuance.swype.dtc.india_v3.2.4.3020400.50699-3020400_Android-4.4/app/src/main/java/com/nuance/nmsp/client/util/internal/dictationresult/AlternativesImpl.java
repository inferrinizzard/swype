package com.nuance.nmsp.client.util.internal.dictationresult;

import com.nuance.nmsp.client.util.dictationresult.Alternative;
import com.nuance.nmsp.client.util.dictationresult.Alternatives;
import java.util.Vector;

/* loaded from: classes.dex */
public class AlternativesImpl implements Alternatives {
    private int a;
    private int b;
    private Vector c;

    public AlternativesImpl(int i, int i2, Vector vector) {
        this.a = i;
        this.b = i2;
        this.c = vector;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Alternatives
    public Alternative getAlternativeAt(int i) {
        return (Alternative) this.c.elementAt(i);
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Alternatives
    public int getBeginIndexSelection() {
        return this.a;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Alternatives
    public int getEndIndexSelection() {
        return this.b;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Alternatives
    public int size() {
        return this.c.size();
    }
}
