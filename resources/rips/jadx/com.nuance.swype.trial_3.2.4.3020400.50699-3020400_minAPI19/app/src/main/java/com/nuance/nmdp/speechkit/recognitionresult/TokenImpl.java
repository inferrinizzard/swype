package com.nuance.nmdp.speechkit.recognitionresult;

/* loaded from: classes.dex */
class TokenImpl implements Token {
    private com.nuance.nmsp.client.util.dictationresult.Token _nmspToken;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TokenImpl(com.nuance.nmsp.client.util.dictationresult.Token nmspToken) {
        this._nmspToken = nmspToken;
    }

    @Override // com.nuance.nmdp.speechkit.recognitionresult.Token
    public double getConfidenceScore() {
        return this._nmspToken.getConfidenceScore();
    }

    @Override // com.nuance.nmdp.speechkit.recognitionresult.Token
    public String toString() {
        return this._nmspToken.toString();
    }
}
