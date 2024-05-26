package com.nuance.nmdp.speechkit.recognitionresult;

import com.nuance.nmsp.client.util.dictationresult.Alternatives;
import com.nuance.nmsp.client.util.dictationresult.Sentence;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
class DetailedResultImpl implements DetailedResult {
    private Sentence _sentence;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DetailedResultImpl(Sentence sentence) {
        this._sentence = sentence;
    }

    @Override // com.nuance.nmdp.speechkit.recognitionresult.DetailedResult
    public List<Token> getTokens() {
        List<Token> wordList = new ArrayList<>();
        for (int i = 0; i < this._sentence.size(); i++) {
            wordList.add(new TokenImpl(this._sentence.tokenAt(i)));
        }
        return wordList;
    }

    @Override // com.nuance.nmdp.speechkit.recognitionresult.DetailedResult
    public Token getTokenAtCursorPosition(int cursorPosition) {
        return new TokenImpl(this._sentence.tokenAtCursorPosition(cursorPosition));
    }

    @Override // com.nuance.nmdp.speechkit.recognitionresult.DetailedResult
    public double getConfidenceScore() {
        return this._sentence.getConfidenceScore();
    }

    @Override // com.nuance.nmdp.speechkit.recognitionresult.DetailedResult
    public List<AlternativeChoice> getAlternatives(int cursorPositionStart, int cursorPositionEnd) {
        List<AlternativeChoice> altList = new ArrayList<>();
        Alternatives alts = this._sentence.getAlternatives(cursorPositionStart, cursorPositionEnd);
        for (int i = 0; i < alts.size(); i++) {
            altList.add(new AlternativeChoiceImpl(alts.getAlternativeAt(i)));
        }
        return altList;
    }

    @Override // com.nuance.nmdp.speechkit.recognitionresult.DetailedResult
    public String toString() {
        return this._sentence.toString();
    }
}
