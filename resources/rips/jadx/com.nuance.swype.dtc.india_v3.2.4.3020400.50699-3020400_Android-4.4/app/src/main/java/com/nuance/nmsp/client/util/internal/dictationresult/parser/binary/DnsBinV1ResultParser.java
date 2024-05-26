package com.nuance.nmsp.client.util.internal.dictationresult.parser.binary;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.util.dictationresult.DictationResult;
import com.nuance.nmsp.client.util.internal.dictationresult.DictationResultImpl;
import com.nuance.nmsp.client.util.internal.dictationresult.SentenceImpl;
import com.nuance.nmsp.client.util.internal.dictationresult.TokenImpl;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.ResultParser;
import com.nuance.nmsp.client.util.internal.dictationresult.util.Util;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;

/* loaded from: classes.dex */
public class DnsBinV1ResultParser implements ResultParser {
    private static final LogFactory.Log a = LogFactory.getLog(DnsBinV1ResultParser.class);
    private byte[] b;
    private String c;
    private AnonymousClass1 d;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.nuance.nmsp.client.util.internal.dictationresult.parser.binary.DnsBinV1ResultParser$1, reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        public int a;
        public int b;
        public int c;
        public int d;
        public int e;

        private AnonymousClass1(DnsBinV1ResultParser dnsBinV1ResultParser) {
            this.a = -1;
            this.b = -1;
            this.c = -1;
            this.d = -1;
            this.e = -1;
        }

        /* synthetic */ AnonymousClass1(DnsBinV1ResultParser dnsBinV1ResultParser, byte b) {
            this(dnsBinV1ResultParser);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class a {
        public SentenceImpl a;
        public int b;
        public int c;

        private a(DnsBinV1ResultParser dnsBinV1ResultParser) {
            this.a = new SentenceImpl();
            new Vector();
            this.b = 0;
            this.c = 0;
        }

        /* synthetic */ a(DnsBinV1ResultParser dnsBinV1ResultParser, byte b) {
            this(dnsBinV1ResultParser);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class b {
        public int a;
        public int b;
        public TokenImpl c;

        private b(DnsBinV1ResultParser dnsBinV1ResultParser) {
            this.a = 0;
            this.b = 0;
            this.c = null;
        }

        /* synthetic */ b(DnsBinV1ResultParser dnsBinV1ResultParser, byte b) {
            this(dnsBinV1ResultParser);
        }
    }

    public DnsBinV1ResultParser(byte[] bArr) {
        String constructByteEncodedString;
        this.c = "Cp1252";
        this.d = null;
        this.b = bArr;
        AnonymousClass1 anonymousClass1 = new AnonymousClass1(this, (byte) 0);
        int a2 = a(2, a(0));
        anonymousClass1.e = a2;
        int b2 = b(a2 + 4, b(a2));
        anonymousClass1.d = b2;
        int a3 = a(b2 + 2, f(b2));
        anonymousClass1.b = a(3, a3, bArr.length);
        anonymousClass1.c = a(4, a3, bArr.length);
        anonymousClass1.a = a(1, a3, bArr.length);
        this.d = anonymousClass1;
        AnonymousClass1 anonymousClass12 = this.d;
        if (anonymousClass12.c == -1) {
            constructByteEncodedString = "Cp1252";
        } else {
            constructByteEncodedString = Util.constructByteEncodedString(this.b, anonymousClass12.c + 4 + 4, ((int) Util.bytesToUInt(this.b, r0)) - 1, "Cp1252");
            if (!a(constructByteEncodedString == "Windows-1252" ? "Cp1252" : constructByteEncodedString)) {
                constructByteEncodedString = "Cp1252";
            }
        }
        this.c = constructByteEncodedString;
    }

    private int a(int i) {
        int bytesToUShort = Util.bytesToUShort(this.b, i);
        if (a.isTraceEnabled()) {
            a.trace("Number of words: " + bytesToUShort);
        }
        return bytesToUShort;
    }

    private int a(int i, int i2) {
        int i3 = 0;
        while (i < this.b.length && i3 < i2) {
            if (this.b[i] == 0) {
                i3++;
            }
            i++;
        }
        return i;
    }

    private int a(int i, int i2, int i3) {
        int i4 = i2;
        while (i4 < i3) {
            long bytesToUInt = Util.bytesToUInt(this.b, i4);
            if (a.isTraceEnabled()) {
                a.trace("Received extension id=" + bytesToUInt);
            }
            if (bytesToUInt == i) {
                return i4;
            }
            int i5 = i4 + 4;
            long bytesToUInt2 = Util.bytesToUInt(this.b, i5);
            if (a.isTraceEnabled()) {
                a.trace("Received payload size: " + bytesToUInt2);
            }
            i4 = i5 + ((int) bytesToUInt2) + 4;
        }
        return -1;
    }

    private a a(int i, int i2, Vector vector) {
        int c = c(i);
        int i3 = i + 4;
        SentenceImpl sentenceImpl = new SentenceImpl();
        a aVar = new a(this, 0 == true ? 1 : 0);
        for (int i4 = 0; i4 < c; i4++) {
            b a2 = a(i3, i2, 2, vector);
            sentenceImpl.appendTokenToSentence(a2.c);
            i3 = a2.a;
            i2 = a2.b;
            if (a.isTraceEnabled()) {
                TokenImpl tokenImpl = a2.c;
                a.trace("Extracted word: startTime [" + tokenImpl.getStartTime() + "] endTime [" + tokenImpl.getEndTime() + "] content [" + tokenImpl.getWord() + "]");
            }
        }
        aVar.b = i3;
        aVar.c = i2;
        aVar.a = sentenceImpl;
        return aVar;
    }

    private b a(int i, int i2, int i3, Vector vector) {
        long d;
        long d2;
        int i4;
        b bVar = new b(this, (byte) 0);
        int i5 = i + 2;
        String str = (String) vector.elementAt(Util.bytesToUShort(this.b, i) - 1);
        if (i3 == 4) {
            d = e(i5);
            int i6 = i5 + i3;
            d2 = e(i6);
            i4 = i6 + i3;
        } else {
            d = d(i5);
            int i7 = i5 + i3;
            d2 = d(i7);
            i4 = i7 + i3;
        }
        double d3 = 0.0d;
        if (i2 != -1) {
            d3 = Util.bytesToUShort(this.b, i2);
            i2 += 2;
        }
        bVar.c = new TokenImpl(str, d, d2, d3, true);
        bVar.a = i4;
        bVar.b = i2;
        return bVar;
    }

    private Vector a(AnonymousClass1 anonymousClass1, Vector vector) {
        int i = 0;
        boolean z = anonymousClass1.a != -1;
        int i2 = z ? anonymousClass1.a : anonymousClass1.e;
        boolean z2 = anonymousClass1.b != -1;
        if (z) {
            i2 = i2 + 4 + 4;
        }
        int b2 = b(i2);
        int i3 = i2 + 4;
        int i4 = anonymousClass1.b;
        if (z2) {
            i4 = i4 + 4 + 4 + 4 + 4;
        }
        Vector vector2 = new Vector();
        int i5 = i4;
        int i6 = i3;
        while (i < b2) {
            a b3 = z ? b(i6, i5, vector) : a(i6, i5, vector);
            if (a.isTraceEnabled()) {
                a.trace("Number of words in Sentence " + i + ": [" + b3.a.size() + "]");
            }
            vector2.addElement(b3.a);
            int i7 = b3.b;
            int i8 = b3.c;
            if (a.isTraceEnabled()) {
                a.trace("Extracted sentence: [" + b3.a + "]");
            }
            i++;
            i6 = i7;
            i5 = i8;
        }
        return vector2;
    }

    private static void a(Hashtable hashtable) {
        for (int i = 0; hashtable.remove("CFD" + i) != null; i++) {
        }
        if (hashtable.containsKey("IAL")) {
            hashtable.put("InputAudioLength", hashtable.get("IAL"));
        }
    }

    private static void a(Hashtable hashtable, Vector vector) {
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= vector.size()) {
                return;
            }
            String str = (String) hashtable.get("CFD" + i2);
            if (str != null) {
                try {
                    ((SentenceImpl) vector.elementAt(i2)).setConfidenceScore(Double.parseDouble(str));
                } catch (NumberFormatException e) {
                }
            }
            i = i2 + 1;
        }
    }

    private static boolean a(String str) {
        try {
            new byte[1][0] = 20;
            return true;
        } catch (UnsupportedEncodingException e) {
            if (a.isWarnEnabled()) {
                a.warn(str + " character encoding is not available in your VM. Using the default one.");
            }
            return false;
        }
    }

    private int b(int i) {
        int bytesToInt = Util.bytesToInt(this.b, i);
        if (a.isTraceEnabled()) {
            a.trace("Number of sentences: " + bytesToInt);
        }
        return bytesToInt;
    }

    private int b(int i, int i2) {
        int i3 = 0;
        while (i3 < i2) {
            int i4 = i + 4;
            for (int i5 = 0; i5 < c(i); i5++) {
                i4 += 6;
            }
            i3++;
            i = i4;
        }
        return i;
    }

    private a b(int i, int i2, Vector vector) {
        int c = c(i);
        int i3 = i + 4;
        a aVar = new a(this, 0 == true ? 1 : 0);
        for (int i4 = 0; i4 < c; i4++) {
            b a2 = a(i3, i2, 4, vector);
            aVar.a.appendTokenToSentence(a2.c);
            i3 = a2.a;
            i2 = a2.b;
            if (a.isTraceEnabled()) {
                TokenImpl tokenImpl = a2.c;
                a.trace("Extracted word: startTime [" + tokenImpl.getStartTime() + "] endTime [" + tokenImpl.getEndTime() + "] content [" + tokenImpl.getWord() + "]");
            }
        }
        aVar.b = i3;
        aVar.c = i2;
        return aVar;
    }

    private int c(int i) {
        return Util.bytesToInt(this.b, i);
    }

    private Vector c(int i, int i2) {
        Vector vector = new Vector();
        for (int i3 = 0; i3 < i; i3++) {
            int computeStrLen = Util.computeStrLen(this.b, i2);
            String constructByteEncodedString = Util.constructByteEncodedString(this.b, i2, computeStrLen, getEncoding());
            vector.addElement(constructByteEncodedString);
            if (a.isTraceEnabled()) {
                a.trace("Added a word to the list: [" + constructByteEncodedString + "] offset [" + i2 + "] len [" + computeStrLen + "]");
            }
            i2 += computeStrLen + 1;
        }
        return vector;
    }

    private int d(int i) {
        return Util.bytesToUShort(this.b, i);
    }

    private long e(int i) {
        return Util.bytesToUInt(this.b, i);
    }

    private int f(int i) {
        int bytesToUShort = Util.bytesToUShort(this.b, i);
        if (a.isTraceEnabled()) {
            a.trace("Number of key-value pairs: " + bytesToUShort);
        }
        return bytesToUShort;
    }

    private Hashtable g(int i) {
        Hashtable hashtable = new Hashtable();
        Vector c = c(f(i), i + 2);
        if (a.isTraceEnabled()) {
            a.trace("Extracted " + c.size() + " words from the set of key-value pairs.");
        }
        for (int i2 = 0; i2 < c.size(); i2++) {
            String str = (String) c.elementAt(i2);
            int indexOf = str.indexOf(61);
            if (indexOf == -1) {
                throw new IllegalArgumentException("Received an invalid key-value pair: " + ((String) c.elementAt(i2)));
            }
            hashtable.put(str.substring(0, indexOf), str.substring(indexOf + 1, str.length()));
        }
        return hashtable;
    }

    public String getEncoding() {
        return this.c.equals("Cp1252") ? "Windows-1252" : this.c;
    }

    @Override // com.nuance.nmsp.client.util.internal.dictationresult.parser.ResultParser
    public DictationResult parse() {
        a.debug("Unpacking DNS binary version 3.2 results.");
        Vector c = c(a(0), 2);
        if (a.isDebugEnabled()) {
            a.debug("Found " + c.size() + " in word list");
            if (a.isTraceEnabled()) {
                for (int i = 0; i < c.size(); i++) {
                    a.trace(c.elementAt(i).toString());
                }
            }
        }
        Vector a2 = a(this.d, c);
        if (a.isDebugEnabled()) {
            a.debug("Found " + a2.size() + " in n-best list");
            if (a.isTraceEnabled()) {
                for (int i2 = 0; i2 < a2.size(); i2++) {
                    a.trace(a2.elementAt(i2).toString());
                }
            }
        }
        Hashtable g = g(this.d.d);
        a(g, a2);
        a(g);
        return new DictationResultImpl(a2, g);
    }
}
