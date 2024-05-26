package com.nuance.nmsp.client.util.internal.dictationresult;

import com.nuance.nmsp.client.util.dictationresult.Token;
import com.nuance.nmsp.client.util.internal.dictationresult.util.Util;

/* loaded from: classes.dex */
public class TokenImpl implements Token {
    private String a;
    private long b;
    private long c;
    private double d;
    private boolean e;
    private boolean f;
    private boolean g;
    private boolean h;
    private int i;
    private int j;
    private Token.TokenType k;

    public TokenImpl(String str, long j, long j2, double d, Token.TokenType tokenType, boolean z) {
        this.e = true;
        this.f = false;
        this.g = false;
        this.h = false;
        this.k = null;
        this.a = str;
        this.b = j;
        this.c = j2;
        this.d = d;
        this.e = z;
        this.k = tokenType;
        if (this.a.indexOf("\\*no-space-before") != -1) {
            this.g = true;
            int indexOf = this.a.indexOf("\\*no-space-before");
            if (indexOf + 17 == this.a.length()) {
                this.a = this.a.substring(0, indexOf);
            } else {
                this.a = this.a.substring(0, indexOf) + this.a.substring(indexOf + 17);
            }
        }
        if (this.a.indexOf("\\*no-space-after") != -1) {
            this.h = true;
            int indexOf2 = this.a.indexOf("\\*no-space-after");
            if (indexOf2 + 16 == this.a.length()) {
                this.a = this.a.substring(0, indexOf2);
            } else {
                this.a = this.a.substring(0, indexOf2) + this.a.substring(indexOf2 + 16);
            }
        }
        if (this.a.length() == 0 || Util.trimWhiteSpace(this.a).length() != 0) {
            return;
        }
        this.f = true;
    }

    public TokenImpl(String str, long j, long j2, double d, boolean z) {
        this(str, j, j2, d, Token.TokenType.VOICE_TOKEN, z);
    }

    public void append(String str) {
        this.a += str;
    }

    public TokenImpl copy() {
        TokenImpl tokenImpl = new TokenImpl(this.a, this.b, this.c, this.d, this.e);
        tokenImpl.f = this.f;
        tokenImpl.i = this.i;
        tokenImpl.h = this.h;
        tokenImpl.g = this.g;
        tokenImpl.k = this.k;
        tokenImpl.j = this.j;
        return tokenImpl;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Token
    public double getConfidenceScore() {
        return this.d;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Token
    public long getEndTime() {
        return this.c;
    }

    public int getLeadingSpaces() {
        return this.i;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Token
    public long getStartTime() {
        return this.b;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Token
    public Token.TokenType getTokenType() {
        return this.k;
    }

    public int getTrailingSpaces() {
        return this.j;
    }

    public String getWord() {
        return this.a;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Token
    public boolean hasNoSpaceAfterDirective() {
        return this.h;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Token
    public boolean hasNoSpaceBeforeDirective() {
        return this.g;
    }

    public boolean isOriginalData() {
        return this.e;
    }

    public boolean isWhiteSpace() {
        return this.f;
    }

    public void setConfidenceScore(double d) {
        this.d = d;
    }

    public void setEndTime(long j) {
        this.c = j;
    }

    public void setLeadingSpaces(int i) {
        this.i = i;
    }

    public void setStartTime(long j) {
        this.b = j;
    }

    public void setTrailingSpaces(int i) {
        this.j = i;
    }

    public void setWord(String str) {
        this.a = str;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Token
    public String toString() {
        return this.a;
    }
}
