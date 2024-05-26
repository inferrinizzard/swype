package com.nuance.swype.input;

import android.view.inputmethod.ExtractedText;

/* loaded from: classes.dex */
public class EditState {
    static final int STATE_CAN_NOT_RECAPTURE = 3;
    public static final int STATE_COMPOSE_WORD_CANDIDATE = 1;
    static final int STATE_END = 10;
    static final int STATE_LAST_BACKSPACE_CLEAR_COMPOSING_WORD_LIST = 9;
    static final int STATE_PUNC_OR_SYMBOL_AFTER_SELECTED_WORD = 7;
    public static final int STATE_RECAPTURE = 2;
    public static final int STATE_RESELECT_TO_REPLACE = 8;
    static final int STATE_SELECT_DEFAULT_WORD = 4;
    static final int STATE_SELECT_NONE_DEFAULT_WORD = 5;
    public static final int STATE_SPACE_AFTER_SELECTED_WORD = 6;
    public static final int STATE_START = 0;
    protected final IMEApplication imeApp;
    private int mState;
    private final StringBuilder mSelectedWord = new StringBuilder();
    private final StringBuilder mCharacterTyped = new StringBuilder();

    public EditState(IMEApplication imeApp) {
        this.imeApp = imeApp;
        start();
    }

    public void startSession() {
        start();
    }

    public void endSession() {
        end();
    }

    public void spaceKey() {
        switch (this.mState) {
            case 4:
            case 5:
                set(6);
                return;
            default:
                start();
                return;
        }
    }

    public void cursorChanged(CharSequence charBeforeCusor) {
        if (charBeforeCusor == null || charBeforeCusor.length() == 0) {
            start();
        } else if ((this.mState == 3 || this.mState == 2) && isWhiteSpace(charBeforeCusor.charAt(0))) {
            start();
        }
    }

    public void backSpaceClearCompositingWordCandiateList() {
        this.mState = 9;
    }

    public void backSpace() {
        if (this.mCharacterTyped.length() > 0) {
            this.mCharacterTyped.deleteCharAt(this.mCharacterTyped.length() - 1);
        }
        switch (this.mState) {
            case 1:
            case 2:
            case 3:
                return;
            default:
                start();
                return;
        }
    }

    public void punctuationOrSymbols() {
        switch (this.mState) {
            case 1:
            case 4:
            case 5:
                set(7);
                return;
            case 2:
            case 3:
            default:
                start();
                return;
        }
    }

    public void characterTyped(char ch) {
        this.mCharacterTyped.append(ch);
    }

    public void appendCharacterTyped(CharSequence seq) {
        this.mCharacterTyped.append(seq);
    }

    public final void start() {
        this.mCharacterTyped.setLength(0);
        this.mSelectedWord.setLength(0);
        set(0);
    }

    public void end() {
        this.mSelectedWord.setLength(0);
        this.mCharacterTyped.setLength(0);
        set(10);
    }

    public void composeWordCandidate() {
        this.mSelectedWord.setLength(0);
        set(1);
    }

    public void recapture() {
        this.mSelectedWord.setLength(0);
        set(2);
    }

    public void reselectToReplace() {
        this.mSelectedWord.setLength(0);
        set(8);
    }

    public void selectWord(CharSequence wordSelected, CharSequence defaultWord) {
        if (wordSelected.equals(defaultWord)) {
            set(4);
        } else {
            set(5);
        }
        this.mSelectedWord.insert(0, wordSelected);
        this.mCharacterTyped.setLength(0);
    }

    public boolean isWhiteSpace(char c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t';
    }

    void set(int state) {
        this.mState = state;
    }

    public int current() {
        return this.mState;
    }

    public String selectedWord() {
        return this.mSelectedWord.toString();
    }

    public boolean canDoRecapture() {
        return (this.mState == 2 || this.mState == 3) ? false : true;
    }

    public void updateExtractedText(int token, ExtractedText text) {
    }

    public void enterSent() {
    }

    public void runSearch(String searchString) {
    }
}
