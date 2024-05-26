package com.nuance.swype.input;

import android.text.TextUtils;
import android.view.inputmethod.InputConnection;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.swype.input.appspecific.AppSpecificInputConnection;
import com.nuance.swype.util.CharacterUtilities;

/* loaded from: classes.dex */
public class AutospaceHandler {
    private final CharacterUtilities charUtils;
    private boolean forceSingleAutospace;
    private final IME ime;
    private final IMEApplication imeApp;
    private boolean noAutospace;
    private boolean noAutospaceLanguage;
    private boolean suppressSingleAutospace;
    private boolean webSearchUrlBoxAutoSpace;

    public AutospaceHandler(IME ime) {
        this.ime = ime;
        this.imeApp = (IMEApplication) ime.getApplication();
        this.charUtils = this.imeApp.getCharacterUtilities();
    }

    public void onUpdateEditorInfo(InputFieldInfo inputFieldInfo) {
        this.noAutospace = false;
        this.webSearchUrlBoxAutoSpace = false;
        if (inputFieldInfo.isEmailAddressField() || inputFieldInfo.isPasswordField()) {
            this.noAutospace = true;
        } else if (inputFieldInfo.isURLField()) {
            if (!inputFieldInfo.isWebSearchField() && !inputFieldInfo.isURLWithSearchField() && !this.imeApp.getAppSpecificBehavior().shouldAutoSpaceInUrlField()) {
                this.noAutospace = true;
            } else {
                this.webSearchUrlBoxAutoSpace = true;
            }
        }
        if (!this.noAutospace) {
            this.suppressSingleAutospace = false;
        }
    }

    public void setEnabled(boolean enabled, boolean disabledLang) {
        this.noAutospace = !enabled;
        this.noAutospaceLanguage = disabledLang;
    }

    public void onTrace(WordCandidate newWord, int lastInputType) {
        if (newWord.shouldRemoveSpaceBefore()) {
            this.suppressSingleAutospace = false;
            this.forceSingleAutospace = false;
        } else {
            onText(newWord.toString(), lastInputType, true);
        }
    }

    public void onCharKey(int charKey) {
        this.forceSingleAutospace = (this.charUtils.isEmbeddedPunct((char) charKey) || this.charUtils.isWordCompounder((char) charKey)) ? false : true;
        this.suppressSingleAutospace = false;
    }

    public boolean onKey(int keyCode) {
        if (keyCode == -106) {
            this.forceSingleAutospace = false;
            this.suppressSingleAutospace = true;
            return true;
        }
        if (keyCode == 2899) {
            this.forceSingleAutospace = false;
            this.suppressSingleAutospace = false;
            return true;
        }
        if (keyCode == 8) {
            this.forceSingleAutospace = false;
            this.suppressSingleAutospace = this.ime.isEditorComposingText() ? false : true;
        } else if (keyCode == 32) {
            this.forceSingleAutospace = false;
        } else if (keyCode == 10) {
            this.forceSingleAutospace = false;
            this.suppressSingleAutospace = true;
        }
        return false;
    }

    public boolean onText(CharSequence text, int lastInputType, boolean sendSpace) {
        boolean z = false;
        boolean shouldSendSpace = shouldAutospaceText(text, lastInputType);
        if (shouldSendSpace && sendSpace) {
            sendAutospace();
        }
        this.suppressSingleAutospace = false;
        if (text != null) {
            char lastChar = text.charAt(text.length() - 1);
            if (this.charUtils.isEmbeddedPunct(lastChar) && !this.charUtils.isWordCompounder(lastChar)) {
                z = true;
            }
            this.forceSingleAutospace = z;
        }
        return shouldSendSpace;
    }

    public void onSelectCandidate(Candidates.Source source, WordCandidate defaultCandidate, WordCandidate selectedCandidate, int lastInput) {
        InputConnection ic;
        AppSpecificInputConnection ic2;
        CharSequence seq;
        if (source == Candidates.Source.NEXT_WORD_PREDICTION) {
            if (!selectedCandidate.shouldRemoveSpaceBefore() && !this.noAutospaceLanguage) {
                if (lastInput == 6) {
                    sendAutospace();
                } else {
                    sendAutospaceWCheck();
                }
            } else {
                AppSpecificInputConnection ic3 = this.ime.getAppSpecificInputConnection();
                if (ic3 != null && (seq = ic3.getTextBeforeCursor(2, 0)) != null && seq.length() == 2 && !CharacterUtilities.isWhiteSpace(seq.charAt(0)) && seq.charAt(seq.length() - 1) == ' ') {
                    ic3.deleteSurroundingText(1, 0);
                }
                if (selectedCandidate != null && selectedCandidate.length() > 0) {
                    char lastChar = selectedCandidate.toString().charAt(selectedCandidate.length() - 1);
                    this.forceSingleAutospace = this.charUtils.isEmbeddedPunct(lastChar) && !this.charUtils.isWordCompounder(lastChar);
                }
            }
        } else {
            boolean removeSpaceBeforeSelected = selectedCandidate.shouldRemoveSpaceBefore();
            boolean removeSpaceBeforeDefault = defaultCandidate.shouldRemoveSpaceBefore();
            if (removeSpaceBeforeSelected != removeSpaceBeforeDefault && (ic2 = this.ime.getAppSpecificInputConnection()) != null) {
                int lengthWordPlusOne = defaultCandidate.length() + 1;
                CharSequence textBefore = ic2.getTextBeforeCursor(lengthWordPlusOne, 0);
                int lengthTextBefore = textBefore != null ? textBefore.length() : 0;
                if (lengthTextBefore == lengthWordPlusOne) {
                    boolean hasSpaceBefore = false;
                    for (int i = 0; i < lengthTextBefore && !hasSpaceBefore; i++) {
                        hasSpaceBefore = Character.isWhitespace(textBefore.charAt(i));
                    }
                    if (removeSpaceBeforeSelected && hasSpaceBefore) {
                        ic2.deleteSurroundingText(1, 0);
                    } else if (!removeSpaceBeforeSelected && !hasSpaceBefore && autoSpaceEnabled()) {
                        sendAutospace();
                    }
                }
            }
            if (Character.isWhitespace(selectedCandidate.toString().charAt(selectedCandidate.length() - 1)) && (ic = this.ime.getCurrentInputConnection()) != null) {
                CharSequence cSeqAfter = ic.getTextAfterCursor(1, 0);
                if (!TextUtils.isEmpty(cSeqAfter) && Character.isWhitespace(cSeqAfter.charAt(0))) {
                    ic.deleteSurroundingText(0, 1);
                }
            }
        }
        this.suppressSingleAutospace = false;
    }

    private boolean sendAutospaceWCheck() {
        boolean isAutoSpaceSent = false;
        if (shouldAutoSpace()) {
            sendAutospace();
            isAutoSpaceSent = true;
        }
        this.suppressSingleAutospace = false;
        return isAutoSpaceSent;
    }

    private void sendAutospace() {
        this.ime.sendSpace();
        this.forceSingleAutospace = false;
    }

    private boolean autoSpaceEnabled() {
        return !this.noAutospace && (!this.suppressSingleAutospace || this.forceSingleAutospace);
    }

    public boolean shouldAutoSpace() {
        AppSpecificInputConnection ic;
        if (!autoSpaceEnabled() || (ic = this.ime.getAppSpecificInputConnection()) == null) {
            return false;
        }
        CharSequence cs = ic.getTextBeforeCursor(65535, 0);
        if (cs != null) {
            if (ic.hasSelection() && !this.webSearchUrlBoxAutoSpace) {
                return false;
            }
            int cursorStart = cs.length();
            return shouldAutoSpace(cs, cursorStart);
        }
        return this.imeApp.getAppSpecificBehavior().shouldAutoSpaceOnTextExtractFailure();
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0032 A[ORIG_RETURN, RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean shouldAutoSpace(java.lang.CharSequence r6, int r7) {
        /*
            r5 = this;
            r1 = 1
            r2 = 0
            if (r7 <= 0) goto L37
            int r3 = r6.length()
            if (r7 > r3) goto L37
            int r3 = r7 + (-1)
            char r0 = r6.charAt(r3)
            com.nuance.swype.util.CharacterUtilities r3 = r5.charUtils
            boolean r4 = com.nuance.swype.util.CharacterUtilities.isControl(r0)
            if (r4 != 0) goto L2f
            boolean r4 = com.nuance.swype.util.CharacterUtilities.isWhiteSpace(r0)
            if (r4 != 0) goto L2f
            boolean r4 = r3.isEmbeddedPunct(r0)
            if (r4 != 0) goto L2f
            char[] r3 = r3.leftPunct
            int r3 = java.util.Arrays.binarySearch(r3, r0)
            if (r3 < 0) goto L33
            r3 = r1
        L2d:
            if (r3 == 0) goto L35
        L2f:
            r3 = r1
        L30:
            if (r3 != 0) goto L37
        L32:
            return r1
        L33:
            r3 = r2
            goto L2d
        L35:
            r3 = r2
            goto L30
        L37:
            r1 = r2
            goto L32
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.AutospaceHandler.shouldAutoSpace(java.lang.CharSequence, int):boolean");
    }

    private boolean shouldAutospaceText(CharSequence cs, int lastInputType) {
        boolean outsideWord = true;
        InputConnection ic = this.ime.getCurrentInputConnection();
        if (ic != null) {
            outsideWord = lastInputType == 2 || lastInputType == 1 || lastInputType == 4;
            if (!outsideWord) {
                CharSequence charAfter = ic.getTextAfterCursor(1, 0);
                outsideWord = (TextUtils.isEmpty(charAfter) || !Character.isLetter(charAfter.charAt(0))) | false;
            }
        }
        if (cs != null && outsideWord && cs.length() > 0 && cs.charAt(0) != ' ' && !this.charUtils.isEmbeddedPunct(cs.charAt(0))) {
            if (!(cs.length() == 2 && this.charUtils.isTerminalPunctuation(cs.charAt(0)) && cs.charAt(1) == ' ') && shouldAutoSpace()) {
                return true;
            }
        }
        return false;
    }

    public void resetSingleAutospaceFlags() {
        this.suppressSingleAutospace = false;
        this.forceSingleAutospace = false;
    }
}
