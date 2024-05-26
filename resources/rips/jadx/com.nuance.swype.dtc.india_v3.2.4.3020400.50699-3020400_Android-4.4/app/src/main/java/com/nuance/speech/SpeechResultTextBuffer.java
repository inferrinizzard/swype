package com.nuance.speech;

import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import com.nuance.swype.input.InputFieldInfo;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;

/* loaded from: classes.dex */
public abstract class SpeechResultTextBuffer {
    protected static final LogManager.Log log = LogManager.getLog("SpeechResultTextBuffer");
    public int mCursorBegin;
    public int mCursorEnd;
    protected InputFieldInfo mInputFieldInfo;
    public String mTextBuffer = "";

    public abstract void chooseCandidate(int i);

    public abstract ArrayList<CharSequence> getCandidates();

    public abstract boolean isTextDictated();

    public abstract CharSequence updateResult(Object obj, boolean z);

    public void setFieldInputInfo(InputFieldInfo inputFieldInfo) {
        this.mInputFieldInfo = inputFieldInfo;
    }

    public InputFieldInfo getInputFieldInfo() {
        return this.mInputFieldInfo;
    }

    public boolean isCursorInRange(String text, int cursorBegin, int cursorEnd) {
        return cursorBegin >= 0 && cursorBegin <= text.length() && cursorEnd >= 0 && cursorEnd <= text.length();
    }

    private boolean isCursorInRange() {
        return isCursorInRange(this.mTextBuffer, this.mCursorBegin, this.mCursorEnd);
    }

    public void updateText(String text, int cursorBegin, int cursorEnd) {
        if (!isCursorInRange(text, cursorBegin, cursorEnd)) {
            log.d("updateText(): invalid: beg: ", Integer.valueOf(cursorBegin), "; end: ", Integer.valueOf(cursorEnd), "; len: ", Integer.valueOf(text.length()));
            return;
        }
        this.mCursorBegin = Math.min(cursorBegin, cursorEnd);
        this.mCursorEnd = Math.max(cursorBegin, cursorEnd);
        this.mTextBuffer = text;
    }

    public void updateSentence(int cursorBegin, int cursorEnd) {
        if (!isCursorInRange(this.mTextBuffer, cursorBegin, cursorEnd)) {
            log.d("updateSentence(): invalid: beg: ", Integer.valueOf(cursorBegin), "; end: ", Integer.valueOf(cursorEnd), "; len: ", Integer.valueOf(this.mTextBuffer.length()));
        } else {
            this.mCursorBegin = Math.min(cursorBegin, cursorEnd);
            this.mCursorEnd = Math.max(cursorBegin, cursorEnd);
        }
    }

    public int getCursorBegin() {
        return this.mCursorBegin;
    }

    public int getCursorEnd() {
        return this.mCursorEnd;
    }

    public String getTextBuffer() {
        return this.mTextBuffer;
    }

    public String getHighlightedText() {
        return (this.mTextBuffer.length() == 0 || this.mCursorBegin == this.mCursorEnd) ? "" : this.mTextBuffer.substring(this.mCursorBegin, this.mCursorEnd);
    }

    public void updateSelection(InputConnection ic, int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) {
        if (ic != null) {
            ExtractedText text = ic.getExtractedText(new ExtractedTextRequest(), 0);
            if (text != null && text.text != null && getTextBuffer().length() != text.text.length()) {
                updateText(text.text.toString(), text.selectionStart, text.selectionEnd);
            }
            updateSentence(newSelStart, newSelEnd);
        }
    }

    public static void onResultCheckLeadingSpace(InputConnection ic, CharSequence result) {
        CharSequence seqBeforeCursor;
        if (ic != null && result != null && result.length() > 0) {
            if ((result.charAt(0) == ' ' || isLikelyDomain(result)) && (seqBeforeCursor = ic.getTextBeforeCursor(1, 0)) != null && seqBeforeCursor.length() > 0 && seqBeforeCursor.charAt(0) == ' ') {
                ic.deleteSurroundingText(1, 0);
            }
        }
    }

    private static boolean isLikelyDomain(CharSequence text) {
        String domain = text.toString();
        return domain.startsWith(".com") || domain.startsWith(".net") || domain.startsWith(".org") || domain.startsWith(".gov") || domain.startsWith(".edu") || domain.startsWith(".tv");
    }
}
