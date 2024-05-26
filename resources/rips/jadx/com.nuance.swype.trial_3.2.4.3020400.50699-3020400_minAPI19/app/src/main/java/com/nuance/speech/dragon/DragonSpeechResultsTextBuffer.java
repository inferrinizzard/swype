package com.nuance.speech.dragon;

import com.nuance.nmsp.client.util.dictationresult.Alternatives;
import com.nuance.nmsp.client.util.dictationresult.DictationResult;
import com.nuance.nmsp.client.util.dictationresult.Sentence;
import com.nuance.nmsp.client.util.dictationresult.Token;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.speech.SpeechResultTextBuffer;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class DragonSpeechResultsTextBuffer extends SpeechResultTextBuffer {
    private static final LogManager.Log log = LogManager.getLog("DragonSpeechResultsTextBuffer");
    private Sentence mSentence;

    @Override // com.nuance.speech.SpeechResultTextBuffer
    public CharSequence updateResult(Object dictationResult, boolean isLanguageSupportingLeadingSpace) {
        Sentence sentence;
        DictationResult dicResult = (DictationResult) dictationResult;
        if (dicResult == null || dicResult.size() == 0 || (sentence = dicResult.sentenceAt(0)) == null || sentence.size() == 0) {
            return "";
        }
        for (int i = 0; i < dicResult.size(); i++) {
            log.d("sentence[", Integer.valueOf(i), "] = ", dicResult.sentenceAt(i).toString());
            log.d("score[", Integer.valueOf(i), "] = ", Double.valueOf(dicResult.sentenceAt(i).getConfidenceScore()));
        }
        String result = sentence.toString();
        if (result == null || result.length() == 0) {
            log.d("result = <empty>, dicResult.size() = ", Integer.valueOf(dicResult.size()));
            return "";
        }
        String result2 = result.replace("\r\n", "\n");
        if (this.mSentence == null) {
            this.mSentence = sentence;
            try {
                sentence.updateSentence(this.mTextBuffer + result2, this.mCursorBegin);
            } catch (Exception e) {
                log.e("Failed to updateSentence.", e);
                log.e("mSentence was null");
                log.e("mCursorBegin = " + this.mCursorBegin);
                log.e("mCursorEnd = " + this.mCursorEnd);
                log.e("mTextBuffer = " + this.mTextBuffer);
                log.e("result = sentence.toString() = " + result2);
                log.e("call sentence.updateSentence(mTextBuffer + result, mCursorBegin);");
                return "";
            }
        } else {
            try {
                this.mSentence.updateSentence(sentence, this.mCursorBegin, this.mCursorEnd);
            } catch (Exception e2) {
                log.e("Failed to updateSentence.", e2);
                log.e("mSentence not null");
                log.e("mCursorBegin = " + this.mCursorBegin);
                log.e("mCursorEnd = " + this.mCursorEnd);
                log.e("mTextBuffer = " + this.mTextBuffer);
                log.e("result = sentence.toString() = " + result2);
                log.e("mSentence.toString() = " + this.mSentence.toString());
                log.e("call mSentence.updateSentence(sentence, mCursorBegin, mCursorEnd);");
                return "";
            }
        }
        String strResult = "";
        boolean hasSpaceBefore = result2.charAt(0) == ' ';
        boolean hasSpaceAfter = result2.charAt(result2.length() + (-1)) == ' ';
        log.d("hasSpaceBefore = ", Boolean.valueOf(hasSpaceBefore));
        log.d("hasSpaceAfter = ", Boolean.valueOf(hasSpaceAfter));
        if (this.mCursorBegin > 0 && this.mTextBuffer.length() >= this.mCursorBegin && this.mTextBuffer.charAt(this.mCursorBegin - 1) != ' ' && !hasSpaceBefore && isLanguageSupportingLeadingSpace) {
            strResult = "" + XMLResultsHandler.SEP_SPACE;
        }
        String strResult2 = strResult + result2;
        if (!hasSpaceAfter && this.mCursorEnd < this.mTextBuffer.length() && this.mTextBuffer.charAt(this.mCursorEnd) != ' ') {
            return strResult2 + XMLResultsHandler.SEP_SPACE;
        }
        return strResult2;
    }

    @Override // com.nuance.speech.SpeechResultTextBuffer
    public void chooseCandidate(int index) {
        if (this.mSentence != null && this.mCursorBegin != this.mCursorEnd) {
            try {
                Alternatives alts = this.mSentence.getAlternatives(this.mCursorBegin, this.mCursorEnd);
                if (index <= alts.size()) {
                    this.mSentence.chooseAlternative(alts.getAlternativeAt(index - 1));
                }
            } catch (IndexOutOfBoundsException e) {
            }
        }
    }

    @Override // com.nuance.speech.SpeechResultTextBuffer
    public boolean isTextDictated() {
        if (this.mCursorBegin == this.mCursorEnd) {
            return false;
        }
        boolean isDictated = false;
        if (this.mSentence != null) {
            int cursor = this.mCursorBegin + ((this.mCursorEnd - this.mCursorBegin) / 2);
            Token token = null;
            try {
                token = this.mSentence.tokenAtCursorPosition(cursor);
            } catch (IndexOutOfBoundsException e) {
            }
            if (token != null) {
                isDictated = token.getTokenType() == Token.TokenType.VOICE_TOKEN;
            }
        }
        return isDictated;
    }

    @Override // com.nuance.speech.SpeechResultTextBuffer
    public ArrayList<CharSequence> getCandidates() {
        ArrayList<CharSequence> lstCandidates = new ArrayList<>();
        if (this.mSentence != null && this.mCursorBegin != this.mCursorEnd) {
            Alternatives alts = null;
            try {
                alts = this.mSentence.getAlternatives(this.mCursorBegin, this.mCursorEnd);
            } catch (IndexOutOfBoundsException e) {
                log.e("getCandidates(): bounds error: tl: " + this.mTextBuffer.length() + "; cb: " + this.mCursorBegin + "; ce: " + this.mCursorEnd, e);
            }
            if (alts != null && alts.size() != 0) {
                lstCandidates.add(this.mTextBuffer.substring(this.mCursorBegin, this.mCursorEnd));
                for (int i = 0; i < alts.size(); i++) {
                    lstCandidates.add(alts.getAlternativeAt(i).toString());
                }
            }
        }
        return lstCandidates;
    }

    @Override // com.nuance.speech.SpeechResultTextBuffer
    public void updateText(String text, int cursorBegin, int cursorEnd) {
        super.updateText(text, cursorBegin, cursorEnd);
        if (this.mTextBuffer.length() == 0) {
            this.mSentence = null;
        }
        if (this.mSentence != null) {
            try {
                this.mSentence.updateSentence(text, this.mCursorBegin);
            } catch (IndexOutOfBoundsException e) {
                log.e("updateText(): bounds error: tl: " + text.length() + "; cb: " + cursorBegin + "; ce: " + cursorEnd, e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.speech.SpeechResultTextBuffer
    public void updateSentence(int cursorBegin, int cursorEnd) {
        super.updateSentence(cursorBegin, cursorEnd);
        if (this.mSentence != null) {
            try {
                this.mSentence.updateSentence(this.mTextBuffer, this.mCursorBegin);
            } catch (IndexOutOfBoundsException e) {
                log.e("updateSentence(): bounds error: tl: " + this.mTextBuffer.length() + "; cb: " + cursorBegin + "; ce: " + cursorEnd, e);
            }
        }
    }
}
