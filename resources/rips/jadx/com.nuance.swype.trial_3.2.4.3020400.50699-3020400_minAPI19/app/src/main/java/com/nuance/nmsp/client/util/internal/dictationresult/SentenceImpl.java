package com.nuance.nmsp.client.util.internal.dictationresult;

import com.nuance.nmsp.client.util.dictationresult.Alternative;
import com.nuance.nmsp.client.util.dictationresult.Alternatives;
import com.nuance.nmsp.client.util.dictationresult.Sentence;
import com.nuance.nmsp.client.util.dictationresult.Token;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.util.Vector;

/* loaded from: classes.dex */
public class SentenceImpl implements Sentence {
    private Vector a = new Vector();
    private DictationResultImpl b = null;
    private double c = 0.0d;
    private boolean d = false;

    private int a(long j) {
        for (int i = 0; i < this.a.size(); i++) {
            TokenImpl c = c(i);
            if (c.getStartTime() >= j && !c.isWhiteSpace()) {
                return i;
            }
        }
        return -1;
    }

    private static int a(String str, String str2) {
        int min = Math.min(str.length(), str2.length());
        for (int i = 0; i < min; i++) {
            if (str.charAt(i) != str2.charAt(i)) {
                return i;
            }
        }
        return min;
    }

    private static int a(String str, String str2, int i) {
        int length = str2.length();
        int min = Math.min(length - i, str.length() - i);
        for (int i2 = 0; i2 < min; i2++) {
            if (str.charAt((str.length() - 1) - i2) != str2.charAt((length - 1) - i2)) {
                return length - i2;
            }
        }
        return length - min;
    }

    private static TokenImpl a(TokenImpl tokenImpl, TokenImpl tokenImpl2) {
        return new TokenImpl(tokenImpl.getWord() + tokenImpl2.getWord(), tokenImpl.getStartTime(), tokenImpl2.getEndTime(), Double.MAX_VALUE, tokenImpl.getTokenType() == Token.TokenType.TEXT_TOKEN || tokenImpl2.getTokenType() == Token.TokenType.TEXT_TOKEN ? Token.TokenType.TEXT_TOKEN : Token.TokenType.VOICE_TOKEN, false);
    }

    private Vector a(int i) {
        Vector vector = new Vector();
        int length = toString().length();
        int i2 = i > 0 ? i - 1 : i;
        if (i < length) {
            i++;
        }
        Vector b = b(i2, i);
        int i3 = 0;
        while (true) {
            int i4 = i3;
            if (i4 >= b.size()) {
                return vector;
            }
            TokenImpl tokenImpl = (TokenImpl) b.elementAt(i4);
            if (tokenImpl.isOriginalData()) {
                vector.addElement(tokenImpl);
            }
            i3 = i4 + 1;
        }
    }

    private Vector a(int i, int i2) {
        Vector vector = new Vector();
        Vector b = b(i, i2);
        int i3 = 0;
        while (true) {
            int i4 = i3;
            if (i4 >= b.size()) {
                return vector;
            }
            TokenImpl tokenImpl = (TokenImpl) b.elementAt(i4);
            if (tokenImpl.isOriginalData()) {
                vector.addElement(tokenImpl);
            }
            i3 = i4 + 1;
        }
    }

    private void a(int i, SentenceImpl sentenceImpl) {
        int i2;
        TokenImpl tokenImpl;
        if (i != this.a.size() && !c(i).isWhiteSpace()) {
            b(i);
        }
        for (int size = sentenceImpl.a.size() - 1; size >= 0; size--) {
            this.a.insertElementAt(sentenceImpl.a.elementAt(size), i);
        }
        if (i > 0) {
            int i3 = i - 1;
            TokenImpl c = c(i3);
            if (c.isWhiteSpace()) {
                i2 = i3 - 1;
                tokenImpl = i2 >= 0 ? c(i2) : c;
            } else {
                i2 = i3;
                i3 = 0;
                c = null;
                tokenImpl = c;
            }
            TokenImpl c2 = sentenceImpl.c(0);
            if (tokenImpl.hasNoSpaceAfterDirective() || c2.hasNoSpaceBeforeDirective()) {
                TokenImpl a = a(tokenImpl, c2);
                if (a == null) {
                    return;
                }
                if (c != null) {
                    this.a.removeElementAt(i3);
                }
                if (i2 >= 0) {
                    this.a.removeElementAt(i2);
                    this.a.removeElementAt(i2);
                } else {
                    this.a.removeElementAt(0);
                    i2 = 0;
                }
                this.a.insertElementAt(a, i2);
                return;
            }
        }
        if (i <= 0 || c(i).isWhiteSpace() || c(i - 1).isWhiteSpace()) {
            return;
        }
        b(i);
    }

    private void a(String str) {
        String sentenceImpl = toString();
        int a = a(str, sentenceImpl);
        int a2 = a(str, sentenceImpl, a);
        int i = 0;
        int i2 = 0;
        while (i < this.a.size()) {
            int length = c(i).toString().length() + i2;
            boolean z = i2 <= a && i2 < a2 && length > a && length >= a2;
            boolean z2 = i2 >= a && i2 < a2 && length > a && length <= a2;
            boolean z3 = i2 <= a && length > a && i2 < a2 && length <= a2;
            boolean z4 = i2 >= a && length > a && i2 < a2 && length >= a2;
            if (z || z2 || z3 || z4) {
                this.a.removeElementAt(i);
                i--;
            }
            i++;
            i2 = length;
        }
    }

    private void a(Vector vector, int i) {
        int i2;
        boolean z;
        boolean z2;
        Vector vector2 = this.a;
        int i3 = i - 1;
        while (true) {
            if (i3 < 0) {
                i3 = -1;
                break;
            } else if (!((TokenImpl) vector2.elementAt(i3)).isWhiteSpace()) {
                break;
            } else {
                i3--;
            }
        }
        if (i3 != -1) {
            Vector vector3 = this.a;
            int size = vector.size() - 1;
            while (true) {
                if (size < 0) {
                    break;
                }
                TokenImpl tokenImpl = (TokenImpl) vector.elementAt(size);
                if (!tokenImpl.isWhiteSpace() && tokenImpl.toString().equals(vector3.elementAt(i3).toString())) {
                    int i4 = i3;
                    int i5 = size;
                    while (i4 >= 0 && i5 >= 0) {
                        TokenImpl tokenImpl2 = (TokenImpl) vector3.elementAt(i4);
                        TokenImpl tokenImpl3 = (TokenImpl) vector.elementAt(i5);
                        if (tokenImpl2.isWhiteSpace()) {
                            i4--;
                        } else if (!tokenImpl3.isWhiteSpace()) {
                            if (!tokenImpl2.toString().equals(tokenImpl3.toString())) {
                                break;
                            }
                            if (i5 == 0) {
                                z2 = true;
                                break;
                            } else {
                                i4--;
                                i5--;
                            }
                        } else {
                            i5--;
                        }
                    }
                    z2 = false;
                    if (z2) {
                        while (size + 1 < vector.size() && ((TokenImpl) vector.elementAt(size + 1)).isWhiteSpace()) {
                            vector.removeElementAt(size + 1);
                        }
                        for (int i6 = 0; i6 <= size; i6++) {
                            vector.removeElementAt(0);
                        }
                    }
                }
                size--;
            }
        }
        Vector vector4 = this.a;
        int i7 = i;
        while (true) {
            if (i7 >= vector4.size()) {
                i2 = -1;
                break;
            } else {
                if (!((TokenImpl) vector4.elementAt(i7)).isWhiteSpace()) {
                    i2 = i7;
                    break;
                }
                i7++;
            }
        }
        if (i2 != -1) {
            Vector vector5 = this.a;
            int i8 = 0;
            while (true) {
                if (i8 >= vector.size()) {
                    break;
                }
                TokenImpl tokenImpl4 = (TokenImpl) vector.elementAt(i8);
                if (!tokenImpl4.isWhiteSpace() && tokenImpl4.toString().equals(vector5.elementAt(i2).toString())) {
                    int i9 = i8;
                    int i10 = i2;
                    while (i10 < vector5.size() && i9 < vector.size()) {
                        TokenImpl tokenImpl5 = (TokenImpl) vector5.elementAt(i10);
                        TokenImpl tokenImpl6 = (TokenImpl) vector.elementAt(i9);
                        if (tokenImpl5.isWhiteSpace()) {
                            i10++;
                        } else if (!tokenImpl6.isWhiteSpace()) {
                            if (!tokenImpl5.toString().equals(tokenImpl6.toString())) {
                                break;
                            }
                            if (i9 == vector.size() - 1) {
                                z = true;
                                break;
                            } else {
                                i9++;
                                i10++;
                            }
                        } else {
                            i9++;
                        }
                    }
                    z = false;
                    if (z) {
                        int size2 = vector.size() - i8;
                        for (int i11 = 0; i11 < size2; i11++) {
                            vector.removeElementAt(i8);
                        }
                        for (int i12 = i8 - 1; i12 >= 0 && ((TokenImpl) vector.elementAt(i12)).isWhiteSpace(); i12--) {
                            vector.removeElementAt(i12);
                        }
                    }
                }
                i8++;
            }
        }
        if (vector.size() == 0) {
            this.a.removeElementAt(i);
            return;
        }
        for (int size3 = vector.size() - 1; size3 >= 0; size3--) {
            this.a.insertElementAt(vector.elementAt(size3), i);
        }
    }

    private Vector b(int i, int i2) {
        Vector vector = new Vector();
        int length = toString().length();
        int i3 = -1;
        int i4 = -1;
        int i5 = 0;
        for (int i6 = 0; i6 < this.a.size(); i6++) {
            if (i == i5 && i2 == i5) {
                return vector;
            }
            i5 += c(i6).getWord().length();
            if (i4 == -1 && i < i5) {
                i4 = i6;
            }
            if (i4 != -1 && i3 == -1 && i2 <= i5) {
                i3 = i6;
            }
        }
        if (i3 == -1 && i2 == length) {
            i3 = this.a.size() - 1;
        }
        if (i4 == -1 || i3 == -1) {
            return vector;
        }
        while (i4 <= i3) {
            vector.addElement(c(i4));
            i4++;
        }
        return vector;
    }

    private void b(int i) {
        long endTime = this.a.size() == 0 ? 0L : i == this.a.size() ? c(i - 1).getEndTime() : c(i).getStartTime();
        this.a.insertElementAt(new TokenImpl(XMLResultsHandler.SEP_SPACE, endTime, endTime, 0.0d, false), i);
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x0086  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x010f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void b(java.lang.String r22) {
        /*
            Method dump skipped, instructions count: 278
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.nmsp.client.util.internal.dictationresult.SentenceImpl.b(java.lang.String):void");
    }

    private TokenImpl c(int i) {
        return (TokenImpl) this.a.elementAt(i);
    }

    private Vector c(long j, long j2) {
        Vector vector = new Vector();
        for (int i = 0; i < this.a.size(); i++) {
            TokenImpl c = c(i);
            long startTime = c.getStartTime();
            long endTime = c.getEndTime();
            if (startTime >= j2 || endTime <= j) {
                if (startTime > j2) {
                    break;
                }
            } else {
                vector.addElement(c);
            }
        }
        if (vector.size() > 0 && ((TokenImpl) vector.firstElement()).isWhiteSpace()) {
            vector.removeElementAt(0);
        }
        if (vector.size() > 0 && ((TokenImpl) vector.lastElement()).isWhiteSpace()) {
            vector.removeElementAt(vector.size() - 1);
        }
        return vector;
    }

    private void f() {
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this.a.size()) {
                return;
            }
            TokenImpl tokenImpl = (TokenImpl) this.a.elementAt(i2);
            if (tokenImpl.isWhiteSpace()) {
                int i3 = i2 + 1;
                while (i3 < this.a.size() && ((TokenImpl) this.a.elementAt(i3)).isWhiteSpace()) {
                    tokenImpl.append(((TokenImpl) this.a.elementAt(i3)).toString());
                    this.a.removeElementAt(i3);
                }
                i2 = i3;
            }
            i = i2 + 1;
        }
    }

    private void g() {
        if (this.a.size() < 2) {
            return;
        }
        int i = 0;
        TokenImpl tokenImpl = null;
        while (i < this.a.size()) {
            TokenImpl c = c(i);
            if (c.getWord().length() == 0) {
                this.a.removeElementAt(i);
                i--;
            } else if (c.isWhiteSpace()) {
                tokenImpl = null;
            } else if (c.hasNoSpaceBeforeDirective()) {
                tokenImpl = c;
            } else if (tokenImpl != null && tokenImpl.hasNoSpaceAfterDirective()) {
                tokenImpl = c;
            } else if (tokenImpl == null) {
                tokenImpl = c;
            } else {
                tokenImpl = a(tokenImpl, c);
                this.a.removeElementAt(i - 1);
                this.a.removeElementAt(i - 1);
                this.a.insertElementAt(tokenImpl, i - 1);
                i--;
            }
            i++;
        }
    }

    private void h() {
        int i = 0;
        boolean z = false;
        while (i < this.a.size()) {
            if (!c(i).isWhiteSpace()) {
                z = false;
            } else if (z) {
                this.a.removeElementAt(i);
                i--;
            } else {
                z = true;
            }
            i++;
        }
    }

    private void i() {
        this.b.c(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Sentence a() {
        SentenceImpl sentenceImpl = new SentenceImpl();
        sentenceImpl.b = null;
        sentenceImpl.c = this.c;
        for (int i = 0; i < this.a.size(); i++) {
            sentenceImpl.a.addElement(c(i).copy());
        }
        return sentenceImpl;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void a(long j, int i) {
        while (i < this.a.size()) {
            TokenImpl c = c(i);
            c.setStartTime(c.getStartTime() + j);
            c.setEndTime(c.getEndTime() + j);
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void a(long j, long j2) {
        for (int i = 0; i < this.a.size(); i++) {
            TokenImpl c = c(i);
            long endTime = c.getEndTime();
            if (endTime >= j) {
                c.setEndTime(endTime + j2);
                a(j2, i + 1);
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void a(DictationResultImpl dictationResultImpl) {
        this.b = dictationResultImpl;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void a(SentenceImpl sentenceImpl, long j, long j2, long j3) {
        if (sentenceImpl != null && this.b == sentenceImpl.b) {
            throw new IllegalArgumentException("Insertion NOT supported on the same DictationResult instance");
        }
        if (j < 0 || j2 < 0) {
            throw new IllegalArgumentException("Timings cannot be negative");
        }
        if (j > j2) {
            throw new IllegalArgumentException("The timings are corrupted, the timingEnd is greater than the timingBegin");
        }
        if (this.a.size() == 0) {
            if (sentenceImpl != null) {
                this.a = sentenceImpl.a;
                return;
            }
            return;
        }
        f();
        Vector c = c(j, j2);
        if (c.size() != 0) {
            for (int i = 0; i < c.size(); i++) {
                this.a.removeElement(c.elementAt(i));
            }
        }
        h();
        int a = a(j);
        if (a != -1) {
            a(j3, a);
        }
        int a2 = a(j);
        if (a2 == -1) {
            a2 = this.a.size();
        }
        if (sentenceImpl != null && sentenceImpl.a.size() != 0) {
            sentenceImpl.a(j, 0);
            a(a2, sentenceImpl);
        }
        f();
    }

    public void appendTokenToSentence(TokenImpl tokenImpl) {
        if (this.a.size() != 0 && !((TokenImpl) this.a.lastElement()).hasNoSpaceAfterDirective() && !tokenImpl.hasNoSpaceBeforeDirective()) {
            b(this.a.size());
        }
        this.a.addElement(tokenImpl);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final AlternativeImpl b(long j, long j2) {
        return new AlternativeImpl(c(j, j2));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean b() {
        return this.d;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void c() {
        this.d = true;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Sentence
    public void chooseAlternative(Alternative alternative) {
        this.b.a(this);
        AlternativeImpl alternativeImpl = (AlternativeImpl) alternative;
        Vector parentTokens = alternativeImpl.getParentTokens();
        if (parentTokens.isEmpty()) {
            throw new IllegalArgumentException("Received an alternative with no words. This cannot happen. Please file a bug along with this error message and the binary you provided to the DictationResultFactory.");
        }
        int indexOf = this.a.indexOf(parentTokens.elementAt(0));
        int indexOf2 = this.a.indexOf(parentTokens.elementAt(parentTokens.size() - 1));
        if (indexOf == -1 || indexOf2 == -1) {
            throw new IllegalArgumentException("The provided alternative was not generated from this sentence.");
        }
        while (indexOf2 >= indexOf) {
            this.a.removeElementAt(indexOf2);
            indexOf2--;
        }
        a(alternativeImpl.getTokens(), indexOf);
    }

    public Sentence cloneSentence() {
        if (this.b == null) {
            return a();
        }
        int b = this.b.b(this);
        if (b == -1) {
            throw new IllegalArgumentException("Sentence being cloned is not part of its parent DictationResult.Please set the correct parent DictationResult for this sentence before cloning. If this sentence is not supposed to have a parent DictationResult set parent DictationResult to null.");
        }
        return this.b.a().sentenceAt(b);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void d() {
        int indexOf;
        int indexOf2;
        String property = System.getProperty("line.separator");
        if (property == null) {
            property = "\n";
        }
        for (int i = 0; i < this.a.size(); i++) {
            TokenImpl c = c(i);
            StringBuffer stringBuffer = new StringBuffer(c.getWord());
            int i2 = 0;
            while (i2 < stringBuffer.length() && (indexOf2 = stringBuffer.toString().indexOf("\r\n", i2)) != -1) {
                stringBuffer.deleteCharAt(indexOf2);
                stringBuffer.deleteCharAt(indexOf2);
                stringBuffer.insert(indexOf2, '\n');
                i2 = indexOf2 + 1;
            }
            int i3 = 0;
            while (i3 < stringBuffer.length() && (indexOf = stringBuffer.toString().indexOf("\n", i3)) != -1) {
                stringBuffer.deleteCharAt(indexOf);
                stringBuffer.insert(indexOf, property);
                i3 = indexOf + property.length();
            }
            c.setWord(stringBuffer.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final long e() {
        Token token;
        if (!this.a.isEmpty() && (token = (Token) this.a.lastElement()) != null) {
            return token.getEndTime();
        }
        return 0L;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Sentence
    public Alternatives getAlternatives(int i, int i2) {
        int length = toString().length();
        if (i < 0 || i2 > length) {
            throw new IndexOutOfBoundsException();
        }
        if (i2 < i) {
            throw new IllegalArgumentException("Cursor position end is less than begin");
        }
        Vector a = i == i2 ? a(i) : a(i, i2);
        if (a.isEmpty()) {
            return new AlternativesImpl(i, i2, new Vector());
        }
        Vector a2 = this.b.a(this, a);
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        for (int i6 = 0; i6 < this.a.size(); i6++) {
            TokenImpl c = c(i6);
            if (c == a.firstElement()) {
                i4 = i5;
            }
            i5 += c.toString().length();
            if (c == a.lastElement()) {
                i3 = i5;
            }
        }
        return new AlternativesImpl(i4, i3, a2);
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Sentence
    public double getConfidenceScore() {
        return this.c;
    }

    public void setConfidenceScore(double d) {
        this.c = d;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Sentence
    public int size() {
        int i = 0;
        int size = this.a.size();
        if (size != 0) {
            for (int i2 = 0; i2 < size; i2++) {
                if (!c(i2).isWhiteSpace()) {
                    i++;
                }
            }
        }
        return i;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Sentence
    public String toString() {
        if (this.a.size() == 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < this.a.size(); i++) {
            stringBuffer.append(c(i).toString());
        }
        return stringBuffer.toString();
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Sentence
    public Token tokenAt(int i) {
        int i2 = 0;
        int i3 = -1;
        int i4 = -1;
        while (i3 != i) {
            int i5 = i4 + 1;
            if (i5 > this.a.size()) {
                return null;
            }
            if (c(i5).isWhiteSpace()) {
                i4 = i5;
            } else {
                i3++;
                i4 = i5;
            }
        }
        TokenImpl c = c(i4);
        c.setLeadingSpaces((i4 + 1 >= this.a.size() || !c(i4 + 1).isWhiteSpace()) ? 0 : c(i4 + 1).toString().length());
        if (i4 != 0 && c(i4 - 1).isWhiteSpace()) {
            i2 = c(i4 - 1).toString().length();
        }
        c.setTrailingSpaces(i2);
        return c;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Sentence
    public Token tokenAtCursorPosition(int i) {
        int i2 = 0;
        Token token = null;
        if (this.a.size() > 0 && i <= toString().length()) {
            int i3 = 0;
            while (i3 <= i) {
                token = (Token) this.a.get(i2);
                i2++;
                if (token != null) {
                    i3 += token.toString().length();
                }
            }
        }
        return token;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Sentence
    public void updateSentence(Sentence sentence, int i) {
        updateSentence(sentence, i, i);
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Sentence
    public void updateSentence(Sentence sentence, int i, int i2) {
        long j;
        long j2 = 0;
        if (sentence == null) {
            throw new IllegalArgumentException("The new sentence in updateSentence is null");
        }
        int length = toString().length();
        if (i < 0 || i > length || i2 < 0 || i2 > length) {
            throw new IndexOutOfBoundsException("The provided cursor position is not within the sentence boundaries.");
        }
        if (i > i2) {
            throw new IllegalArgumentException("The provided begin cursor selection is greater than the end cursor selection.");
        }
        SentenceImpl sentenceImpl = (SentenceImpl) sentence;
        SentenceImpl sentenceImpl2 = (SentenceImpl) sentenceImpl.cloneSentence();
        i();
        sentenceImpl.i();
        sentenceImpl2.i();
        if (sentence.toString().length() == 0) {
            return;
        }
        Vector b = b(i, i2);
        if (b.size() != 0) {
            TokenImpl tokenImpl = (TokenImpl) b.firstElement();
            TokenImpl tokenImpl2 = (TokenImpl) b.lastElement();
            long startTime = tokenImpl.getStartTime();
            j2 = tokenImpl2.getEndTime();
            j = startTime;
        } else if (i == 0) {
            if (this.a.size() == 0) {
                j = 0;
            } else {
                j2 = ((TokenImpl) b(0, 1).elementAt(0)).getStartTime();
                j = j2;
            }
        } else if (i == length) {
            j2 = ((TokenImpl) b(length - 1, length).elementAt(0)).getEndTime();
            j = j2;
        } else {
            Vector b2 = b(i - 1, i + 1);
            TokenImpl tokenImpl3 = (TokenImpl) b2.firstElement();
            TokenImpl tokenImpl4 = (TokenImpl) b2.lastElement();
            if (tokenImpl3 == tokenImpl4) {
                j = tokenImpl3.getStartTime();
                j2 = tokenImpl3.getEndTime();
            } else {
                j2 = tokenImpl4.getStartTime();
                j = j2;
            }
        }
        this.b.a(sentenceImpl2.b, j, j2, (((TokenImpl) sentenceImpl2.a.lastElement()).getEndTime() - ((TokenImpl) sentenceImpl2.a.firstElement()).getStartTime()) - (j2 - j));
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Sentence
    public void updateSentence(String str, int i) {
        if (str == null) {
            throw new IllegalArgumentException("The new sentence in updateSentence is null");
        }
        if (i < 0 || i > str.length()) {
            throw new IndexOutOfBoundsException("The provided cursor position is not within the new sentence boundaries.");
        }
        if (toString().equals(str)) {
            return;
        }
        this.b.a(this);
        a(str);
        b(str);
        g();
        f();
        if (this.a.size() == 0) {
            this.b.d(this);
        }
        this.c = Double.MAX_VALUE;
    }
}
