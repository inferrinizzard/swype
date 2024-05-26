package com.nuance.nmsp.client.util.internal.dictationresult;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.util.dictationresult.DictationResult;
import com.nuance.nmsp.client.util.dictationresult.DictationResultFactory;
import com.nuance.nmsp.client.util.dictationresult.Sentence;
import java.util.Hashtable;
import java.util.Vector;

/* loaded from: classes.dex */
public class DictationResultImpl implements DictationResult {
    private static final LogFactory.Log a = LogFactory.getLog(DictationResultImpl.class);
    private Vector b;
    private Hashtable c;

    public DictationResultImpl(Vector vector, Hashtable hashtable) {
        if (a.isDebugEnabled()) {
            a.debug("Received " + vector.size() + " sentences constituting the dictation result.");
        }
        this.b = vector;
        this.c = hashtable;
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this.b.size()) {
                return;
            }
            SentenceImpl sentenceImpl = (SentenceImpl) vector.elementAt(i2);
            sentenceImpl.a(this);
            sentenceImpl.d();
            i = i2 + 1;
        }
    }

    private void a(Sentence sentence) {
        if (sentence == null) {
            throw new IllegalArgumentException("Trying to add a null sentence to the dictation result");
        }
        this.b.addElement(sentence);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final DictationResult a() {
        Vector vector = new Vector(this.b.size());
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this.b.size()) {
                return new DictationResultImpl(vector, this.c);
            }
            vector.insertElementAt((SentenceImpl) ((SentenceImpl) this.b.elementAt(i2)).a(), i2);
            i = i2 + 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Vector a(SentenceImpl sentenceImpl, Vector vector) {
        if (vector.size() == 0) {
            return new Vector();
        }
        long startTime = ((TokenImpl) vector.elementAt(0)).getStartTime();
        long endTime = ((TokenImpl) vector.elementAt(vector.size() - 1)).getEndTime();
        if (a.isDebugEnabled()) {
            a.debug("Getting alternatives of " + sentenceImpl + " at times [" + startTime + ", " + endTime + "]");
        }
        Vector vector2 = new Vector();
        Hashtable hashtable = new Hashtable();
        Object obj = new Object();
        hashtable.put(sentenceImpl.b(startTime, endTime).toString(), obj);
        for (int i = 0; i < this.b.size(); i++) {
            SentenceImpl sentenceImpl2 = (SentenceImpl) this.b.elementAt(i);
            if (sentenceImpl2 != sentenceImpl) {
                AlternativeImpl b = sentenceImpl2.b(startTime, endTime);
                b.setParentTokens(vector);
                if (a.isDebugEnabled()) {
                    a.debug("Got alternative [" + b + "] for sentence at index " + i);
                }
                if (b.size() != 0) {
                    String alternativeImpl = b.toString();
                    if (!hashtable.containsKey(alternativeImpl)) {
                        hashtable.put(alternativeImpl, obj);
                        vector2.addElement(b);
                    } else if (a.isDebugEnabled()) {
                        a.debug("That alternative has already been given by another sentence.");
                    }
                } else if (a.isDebugEnabled()) {
                    a.debug("Got no alternative for sentence at index " + i);
                }
            } else if (a.isDebugEnabled()) {
                a.debug("Found the same sentence at index " + i);
            }
        }
        return vector2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void a(DictationResultImpl dictationResultImpl, long j, long j2, long j3) {
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= size()) {
                break;
            }
            ((SentenceImpl) sentenceAt(i2)).a(i2 < dictationResultImpl.size() ? (SentenceImpl) dictationResultImpl.sentenceAt(i2) : null, j, j2, j3);
            i = i2 + 1;
        }
        int size = size();
        while (true) {
            int i3 = size;
            if (i3 >= dictationResultImpl.size()) {
                return;
            }
            SentenceImpl sentenceImpl = (SentenceImpl) dictationResultImpl.sentenceAt(i3);
            sentenceImpl.a(j, 0);
            a((Sentence) sentenceImpl);
            size = i3 + 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void a(SentenceImpl sentenceImpl) {
        if (sentenceImpl.b()) {
            return;
        }
        sentenceImpl.c();
        int indexOf = this.b.indexOf(sentenceImpl);
        this.b.removeElementAt(indexOf);
        Sentence a2 = sentenceImpl.a();
        ((SentenceImpl) a2).a(this);
        this.b.insertElementAt(a2, indexOf);
        this.b.insertElementAt(sentenceImpl, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void a(SentenceImpl sentenceImpl, long j, long j2) {
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= size()) {
                return;
            }
            SentenceImpl sentenceImpl2 = (SentenceImpl) sentenceAt(i2);
            if (sentenceImpl2 != sentenceImpl) {
                sentenceImpl2.a(j, j2);
            }
            i = i2 + 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int b(SentenceImpl sentenceImpl) {
        for (int i = 0; i < this.b.size(); i++) {
            if (this.b.elementAt(i) == sentenceImpl) {
                return i;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void c(SentenceImpl sentenceImpl) {
        boolean z;
        int i = 0;
        while (true) {
            if (i >= this.b.size()) {
                z = false;
                break;
            } else {
                if (this.b.elementAt(i) == sentenceImpl) {
                    this.b.removeElementAt(i);
                    z = true;
                    break;
                }
                i++;
            }
        }
        if (!z) {
            throw new IllegalArgumentException("Did not find the provided sentence in the dictation result");
        }
        this.b.insertElementAt(sentenceImpl, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void d(SentenceImpl sentenceImpl) {
        this.b.removeAllElements();
        this.b.addElement(sentenceImpl);
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.DictationResult
    public Hashtable getExtraInformation() {
        return this.c;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.DictationResult
    public Sentence sentenceAt(int i) {
        if (i < 0 || i >= this.b.size()) {
            return null;
        }
        return (Sentence) this.b.elementAt(i);
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.DictationResult
    public int size() {
        return this.b.size();
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.DictationResult
    public String toString() {
        return this.b.size() > 0 ? this.b.elementAt(0).toString() : "";
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.DictationResult
    public void update(byte[] bArr) throws IllegalArgumentException {
        DictationResult createDictationResult = DictationResultFactory.createDictationResult(bArr);
        Vector vector = this.b;
        this.b = new Vector();
        int size = vector.size() > createDictationResult.size() ? vector.size() : createDictationResult.size();
        for (int i = 0; i < size; i++) {
            SentenceImpl sentenceImpl = (SentenceImpl) ((SentenceImpl) vector.elementAt(i % vector.size())).a();
            sentenceImpl.a(this);
            SentenceImpl sentenceImpl2 = (SentenceImpl) ((SentenceImpl) createDictationResult.sentenceAt(i % createDictationResult.size())).a();
            long e = sentenceImpl.e();
            sentenceImpl.a(sentenceImpl2, e, e, e);
            a((Sentence) sentenceImpl);
        }
        vector.removeAllElements();
        String str = (String) this.c.get("utterance-length");
        String str2 = (String) createDictationResult.getExtraInformation().get("utterance-length");
        if (str != null && str2 != null) {
            this.c.put("utterance-length", new Integer(Integer.parseInt(str) + Integer.parseInt(str2)).toString());
        }
        String str3 = (String) this.c.get("processed-audio-frame-count");
        String str4 = (String) createDictationResult.getExtraInformation().get("processed-audio-frame-count");
        if (str3 != null && str4 != null) {
            this.c.put("processed-audio-frame-count", new Integer(Integer.parseInt(str3) + Integer.parseInt(str4)).toString());
        }
        String str5 = (String) createDictationResult.getExtraInformation().get("InputAudioLength");
        if (str5 != null) {
            this.c.put("InputAudioLength", str5);
        }
    }
}
