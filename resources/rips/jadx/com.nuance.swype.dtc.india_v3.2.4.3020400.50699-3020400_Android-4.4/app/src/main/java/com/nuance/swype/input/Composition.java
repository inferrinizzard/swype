package com.nuance.swype.input;

import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;
import android.view.inputmethod.InputConnection;

/* loaded from: classes.dex */
public class Composition {
    private int mEndSelection;
    private InputConnection mInputConnection;
    private int mStartSelection;
    private SpannableStringBuilder mInline = new SpannableStringBuilder();
    private final UnderlineSpan mWordComposeSpan = new UnderlineSpan();

    public void setSelection(int startSel, int endSel) {
        this.mStartSelection = startSel;
        this.mEndSelection = endSel;
    }

    public void setInputConnection(InputConnection ic) {
        this.mInputConnection = ic;
        clears();
    }

    public void showInline(CharSequence text) {
        if (this.mInputConnection != null) {
            this.mInputConnection.beginBatchEdit();
            clears();
            this.mInline.append(text);
            this.mInline.setSpan(this.mWordComposeSpan, 0, this.mInline.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
            this.mInputConnection.setComposingText(this.mInline, 1);
            this.mInputConnection.endBatchEdit();
        }
    }

    public void acceptCurrentInline() {
        if (this.mInputConnection != null) {
            this.mInputConnection.beginBatchEdit();
            this.mInputConnection.finishComposingText();
            clears();
            this.mInputConnection.endBatchEdit();
        }
    }

    public void clearCurrentInline() {
        if (this.mInline != null && this.mInline.length() > 0 && this.mInputConnection != null) {
            clears();
            this.mInputConnection.beginBatchEdit();
            this.mInputConnection.commitText("", 1);
            this.mInputConnection.endBatchEdit();
        }
    }

    public void insertText(CharSequence text) {
        if (this.mInputConnection != null) {
            clears();
            this.mInputConnection.commitText(text, 1);
        }
    }

    public int length() {
        return this.mInline.length();
    }

    public CharSequence getTextBeforeCursor(int n, int flags) {
        if (this.mInputConnection != null) {
            return this.mInputConnection.getTextBeforeCursor(n, flags);
        }
        return null;
    }

    public CharSequence getTextAfterCursor(int n, int flags) {
        if (this.mInputConnection != null) {
            return this.mInputConnection.getTextAfterCursor(n, flags);
        }
        return null;
    }

    public void deleteWordToRightOfCursor() {
        if (this.mInputConnection != null) {
            if (this.mEndSelection != this.mStartSelection) {
                clearSelection();
                return;
            }
            CharSequence seq = getTextAfterCursor(64, 0);
            if (seq != null && seq.length() > 0) {
                int deleteChars = 0;
                if (Character.isWhitespace(seq.charAt(0))) {
                    deleteChars = 0 + 1;
                } else {
                    for (int i = 0; i < seq.length() && (!Character.isWhitespace(seq.charAt(i)) || deleteChars >= seq.length()); i++) {
                        deleteChars++;
                    }
                }
                this.mInputConnection.deleteSurroundingText(0, deleteChars);
            }
        }
    }

    private void clearSelection() {
        this.mInputConnection.commitText("", 1);
        this.mEndSelection = 0;
        this.mStartSelection = 0;
    }

    public void deleteWordToLeftOfCursor() {
        if (this.mInputConnection != null) {
            if (this.mEndSelection != this.mStartSelection) {
                clearSelection();
                return;
            }
            CharSequence seq = getTextBeforeCursor(64, 0);
            if (seq != null && seq.length() > 0) {
                int deleteChars = 0;
                if (Character.isWhitespace(seq.charAt(seq.length() - 1))) {
                    deleteChars = 0 + 1;
                } else {
                    for (int i = seq.length() - 1; i >= 0 && !Character.isWhitespace(seq.charAt(i)); i--) {
                        deleteChars++;
                    }
                }
                this.mInputConnection.deleteSurroundingText(deleteChars, 0);
            }
        }
    }

    private void clears() {
        this.mStartSelection = 0;
        this.mEndSelection = 0;
        this.mInline.clear();
        this.mInline.clearSpans();
    }
}
