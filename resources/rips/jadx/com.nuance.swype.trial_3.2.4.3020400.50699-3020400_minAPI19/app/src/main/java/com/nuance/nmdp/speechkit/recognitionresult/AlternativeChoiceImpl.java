package com.nuance.nmdp.speechkit.recognitionresult;

import com.nuance.nmsp.client.util.dictationresult.Alternative;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
class AlternativeChoiceImpl implements AlternativeChoice {
    private Alternative _nmspAlt;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlternativeChoiceImpl(Alternative nmspAlt) {
        this._nmspAlt = nmspAlt;
    }

    @Override // com.nuance.nmdp.speechkit.recognitionresult.AlternativeChoice
    public List<Token> getTokens() {
        List<Token> wordList = new ArrayList<>();
        for (int i = 0; i < this._nmspAlt.size(); i++) {
            wordList.add(new TokenImpl(this._nmspAlt.tokenAt(i)));
        }
        return wordList;
    }

    @Override // com.nuance.nmdp.speechkit.recognitionresult.AlternativeChoice
    public String toString() {
        return this._nmspAlt.toString();
    }
}
