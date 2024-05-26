package com.nuance.swype.input.appspecific;

import android.R;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.os.SystemClock;
import android.text.ClipboardManager;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import com.nuance.android.compat.InputConnectionCompat;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.InputFieldInfo;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swype.util.CharacterUtilities;
import com.nuance.swype.util.InputConnectionUtils;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class AppSpecificInputConnection extends InputConnectionWrapper {
    private static final int EXTRACTED_TEXT_OFFSET_SEARCH = 1000;
    private static final int INVALID_CURSOR_POSITION = -1;
    private static final int MAX_SURROUNDING_TEXT = 100;
    protected static final LogManager.Log log = LogManager.getLog("AppSpecificInputConnection");
    private AppSpecificBehavior appSpecific;
    private boolean byPassCache;
    private final StringBuilder cachedAfterCursorText;
    private final StringBuilder cachedCommittedText;
    private final StringBuilder cachedComposingText;
    private int cachedCursorPosition;
    private final StringBuilder cachedSelectedText;
    private int cachedSelectionEnd;
    private CharacterUtilities charUtils;
    private final ClipboardManager clipboard;
    private int editorComposingEnd;
    private int editorComposingStart;
    private int editorExtractedTextOffset;
    private final ExtractViewState extractViewState;
    private boolean isCacheUpdatesReliable;
    private boolean isEditorTextRetrievable;
    private int mBatchLevel;
    private boolean mSkipCacheChangeInSendKeyEvent;
    private InputConnection target;

    /* loaded from: classes.dex */
    public interface ExtractViewState {
        boolean isExtractViewShown();

        void onFlushActiveWord();
    }

    public AppSpecificInputConnection(InputConnection target, ExtractViewState extractViewState, AppSpecificBehavior appSpecific, ClipboardManager clipboard, CharacterUtilities charUtils) {
        super(target, true);
        this.cachedCursorPosition = -1;
        this.cachedSelectionEnd = -1;
        this.editorExtractedTextOffset = 0;
        this.editorComposingStart = 0;
        this.editorComposingEnd = 0;
        this.cachedCommittedText = new StringBuilder();
        this.cachedComposingText = new StringBuilder();
        this.cachedSelectedText = new StringBuilder();
        this.cachedAfterCursorText = new StringBuilder();
        this.mSkipCacheChangeInSendKeyEvent = false;
        this.target = target;
        this.appSpecific = appSpecific;
        this.clipboard = clipboard;
        this.extractViewState = extractViewState;
        this.isEditorTextRetrievable = false;
        this.isCacheUpdatesReliable = appSpecific.shouldByPassInternalCache() ? false : true;
        this.byPassCache = false;
        this.charUtils = charUtils;
    }

    public void setTarget(InputConnection target, AppSpecificBehavior appSpecific) {
        if (this.target != target) {
            setTarget(target);
            clearExtractedTextCache();
            this.target = target;
            this.appSpecific = appSpecific;
            this.isEditorTextRetrievable = false;
            this.isCacheUpdatesReliable = !appSpecific.shouldByPassInternalCache();
            this.byPassCache = false;
        }
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public boolean beginBatchEdit() {
        int i = this.mBatchLevel + 1;
        this.mBatchLevel = i;
        if (i == 1 && this.target != null) {
            return this.target.beginBatchEdit();
        }
        log.d("Begin Batch Nest level too deep : " + this.mBatchLevel);
        return false;
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public boolean endBatchEdit() {
        if (this.mBatchLevel <= 0) {
            throw new RuntimeException("Batch Edit level mis-match");
        }
        int i = this.mBatchLevel - 1;
        this.mBatchLevel = i;
        return i == 0 && this.target != null && this.target.endBatchEdit();
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public boolean commitText(CharSequence text, int newCursorPosition) {
        return commitText(text, newCursorPosition, true);
    }

    private boolean commitText(CharSequence text, int newCursorPosition, boolean bAction) {
        if (text == null) {
            return false;
        }
        if (text.length() == 0 && this.appSpecific.shouldClearComposingAddDeleteSpace() && bAction) {
            return commitText(XMLResultsHandler.SEP_SPACE, 1) && deleteSurroundingText(1, 0);
        }
        log.d("cursor commitText cachedCursorPosition before:", Integer.valueOf(this.cachedCursorPosition));
        if (isCacheSyncedWithEditor()) {
            this.cachedCommittedText.append(text);
            this.cachedCursorPosition += text.length() - this.cachedComposingText.length();
            log.d("cursor commitText cachedCursorPosition after:", Integer.valueOf(this.cachedCursorPosition), " cachedComposingText:", this.cachedComposingText.toString());
            this.cachedComposingText.setLength(0);
            this.cachedSelectionEnd = this.cachedCursorPosition;
            this.cachedSelectedText.setLength(0);
            this.editorComposingEnd = 0;
            this.editorComposingStart = 0;
        }
        if (bAction) {
            return this.target.commitText(text, 1);
        }
        return true;
    }

    public String getCachedContextBuffer() {
        ExtractedText extractedText = getCachedExtractedText(new ExtractedTextRequest(), 0);
        return (extractedText == null || TextUtils.isEmpty(extractedText.text)) ? "" : extractedText.text.toString();
    }

    private ExtractedText getCachedExtractedText(ExtractedTextRequest request, int flags) {
        ExtractedText eText = null;
        if (-1 != this.cachedCursorPosition && flags == 0 && isCacheSyncedWithEditor() && request.flags == 0 && !this.byPassCache) {
            eText = new ExtractedText();
            int textBeforeCount = this.cachedCommittedText.length() + this.cachedComposingText.length();
            int startOffset = textBeforeCount - 100;
            if (startOffset < 0) {
                startOffset = 0;
                eText.selectionStart = textBeforeCount;
            } else {
                eText.selectionStart = 100;
            }
            StringBuilder s = new StringBuilder(this.cachedCommittedText);
            s.append(this.cachedComposingText.toString());
            s.delete(0, startOffset);
            s.append((CharSequence) this.cachedSelectedText);
            s.append(this.cachedAfterCursorText.substring(0, Math.min(100, this.cachedAfterCursorText.length())));
            eText.selectionEnd = eText.selectionStart + this.cachedSelectedText.length();
            eText.startOffset = this.editorExtractedTextOffset + startOffset;
            eText.text = s;
            eText.partialStartOffset = -1;
            eText.flags = 0;
        }
        return eText;
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
        Spanned textSpanned;
        Object[] spans;
        ExtractedText eText = getCachedExtractedText(request, flags);
        if (eText != null) {
            return eText;
        }
        this.cachedCommittedText.setLength(0);
        this.cachedSelectedText.setLength(0);
        this.cachedComposingText.setLength(0);
        log.d("cursor getExtractedText set cachedComposingText:0");
        this.cachedAfterCursorText.setLength(0);
        this.editorComposingEnd = 0;
        this.editorComposingStart = 0;
        ExtractedText eText2 = this.target.getExtractedText(request, flags);
        if (eText2 != null) {
            if (eText2.selectionStart < 0 || eText2.selectionEnd < 0) {
                eText2.selectionStart = eText2.text != null ? eText2.text.length() : 0;
                eText2.selectionEnd = eText2.selectionStart;
            }
            if (eText2.selectionStart > eText2.selectionEnd) {
                int tmp = eText2.selectionStart;
                eText2.selectionStart = eText2.selectionEnd;
                eText2.selectionEnd = tmp;
            }
            if (eText2.text != null) {
                int txtLength = eText2.text.length();
                eText2.selectionStart = Math.min(eText2.selectionStart, txtLength);
                eText2.selectionEnd = Math.min(eText2.selectionEnd, txtLength);
                if (eText2.selectionStart != eText2.selectionEnd) {
                    this.cachedSelectedText.append(eText2.text.subSequence(eText2.selectionStart, eText2.selectionEnd));
                }
                this.cachedAfterCursorText.append(eText2.text.subSequence(eText2.selectionEnd, txtLength));
                if ((eText2.text instanceof Spanned) && (spans = (textSpanned = (Spanned) eText2.text).getSpans(0, txtLength, Object.class)) != null) {
                    int length = spans.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            break;
                        }
                        Object span = spans[i];
                        if ((textSpanned.getSpanFlags(span) & 256) == 0) {
                            i++;
                        } else {
                            int start = textSpanned.getSpanStart(span);
                            int end = textSpanned.getSpanEnd(span);
                            this.editorComposingStart = Math.min(start, end);
                            this.editorComposingEnd = Math.max(start, end);
                            this.cachedComposingText.append(eText2.text.subSequence(this.editorComposingStart, this.editorComposingEnd));
                            log.d("cursor getExtractedText append cachedComposingText:", this.cachedComposingText.toString());
                            this.editorComposingStart += eText2.startOffset;
                            this.editorComposingEnd += eText2.startOffset;
                            break;
                        }
                    }
                }
            }
            this.cachedCursorPosition = eText2.selectionStart + eText2.startOffset;
            this.cachedSelectionEnd = eText2.selectionEnd + eText2.startOffset;
            if (this.editorComposingEnd != this.editorComposingStart && this.editorComposingStart >= eText2.startOffset) {
                this.cachedCommittedText.append(eText2.text.subSequence(0, this.editorComposingStart - eText2.startOffset));
            } else {
                this.cachedCommittedText.append(eText2.text.subSequence(0, eText2.selectionStart));
            }
            this.editorExtractedTextOffset = eText2.startOffset;
            this.isEditorTextRetrievable = true;
            log.d("getExtractedText from target - cachedCursorPosition:" + this.cachedCursorPosition + " cachedSelectionEnd" + this.cachedSelectionEnd);
            return eText2;
        }
        if ((flags & 1) != 0) {
            return null;
        }
        CharSequence textBefore = this.target.getTextBeforeCursor(200, request.flags);
        CharSequence textAfter = this.target.getTextAfterCursor(65535, request.flags);
        CharSequence selectedTextInEditor = InputConnectionCompat.getSelectedText(this.target, request.flags);
        if (textBefore == null || textAfter == null) {
            return null;
        }
        int textBeforeCount = textBefore.length();
        int searchSize = 200;
        while (textBeforeCount == searchSize) {
            searchSize += EXTRACTED_TEXT_OFFSET_SEARCH;
            CharSequence textBeforeCursor = this.target.getTextBeforeCursor(searchSize, 0);
            if (TextUtils.isEmpty(textBeforeCursor)) {
                textBeforeCount = 0;
            } else {
                textBeforeCount = textBeforeCursor.length();
            }
        }
        int startOffset = textBeforeCount + KeyboardEx.KEYCODE_ALTPOPUP;
        if (startOffset < 0) {
            startOffset = 0;
        }
        if (selectedTextInEditor == null) {
            selectedTextInEditor = "";
        }
        ExtractedText eText3 = new ExtractedText();
        eText3.selectionStart = textBefore.length();
        eText3.selectionEnd = eText3.selectionStart + selectedTextInEditor.length();
        eText3.startOffset = startOffset;
        eText3.text = TextUtils.concat(textBefore, selectedTextInEditor, textAfter);
        int[] composingRange = InputConnectionUtils.getComposing(textBefore);
        if (composingRange != null) {
            this.editorComposingStart = Math.min(composingRange[0], composingRange[1]);
            this.editorComposingEnd = Math.max(composingRange[0], composingRange[1]);
            this.cachedComposingText.append(eText3.text.subSequence(this.editorComposingStart, this.editorComposingEnd));
            this.editorComposingStart += eText3.startOffset;
            this.editorComposingEnd += eText3.startOffset;
        }
        if (this.editorComposingEnd != this.editorComposingStart && this.editorComposingStart >= eText3.startOffset) {
            this.cachedCommittedText.append(eText3.text.subSequence(0, this.editorComposingStart - eText3.startOffset));
        } else {
            this.cachedCommittedText.append(eText3.text.subSequence(0, eText3.selectionStart));
        }
        this.cachedSelectedText.append(selectedTextInEditor);
        this.cachedAfterCursorText.append(textAfter);
        this.cachedCursorPosition = eText3.selectionStart + eText3.startOffset;
        this.cachedSelectionEnd = eText3.selectionEnd + eText3.startOffset;
        this.editorExtractedTextOffset = startOffset;
        log.d("cursor getExtractedText new cachedCursorPosition:", Integer.valueOf(this.cachedCursorPosition), " cachedSelectionEnd:", Integer.valueOf(this.cachedSelectionEnd), " cachedAfterCursorText", this.cachedAfterCursorText, " cachedSelectedText", this.cachedSelectedText, " editorExtractedTextOffset", Integer.valueOf(this.editorExtractedTextOffset), " cachedCommittedText:", this.cachedCommittedText);
        this.isEditorTextRetrievable = true;
        return eText3;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0008. Please report as an issue. */
    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public boolean performContextMenuAction(int id) {
        ClipData clipData;
        boolean bTakeAction = this.appSpecific.noContextMenuEditing();
        switch (id) {
            case R.id.selectAll:
                if (!isCacheSyncedWithEditor() && bTakeAction) {
                    ExtractedTextRequest request = new ExtractedTextRequest();
                    request.flags = 1;
                    ExtractedText eText = getExtractedText(request, 0);
                    if (InputConnectionUtils.getComposing(eText) != null) {
                        this.extractViewState.onFlushActiveWord();
                    }
                    this.target.setSelection(0, eText.startOffset + eText.text.length());
                } else {
                    setSelection(0, this.editorExtractedTextOffset + this.cachedComposingText.length() + this.cachedCommittedText.length() + this.cachedSelectedText.length() + this.cachedAfterCursorText.length(), bTakeAction);
                }
                if (bTakeAction) {
                    return true;
                }
                return this.target.performContextMenuAction(id);
            case R.id.cut:
                String selectionStr = getSelectedTextInEditor();
                if (!TextUtils.isEmpty(selectionStr)) {
                    if (bTakeAction) {
                        if (this.clipboard instanceof android.content.ClipboardManager) {
                            ((android.content.ClipboardManager) this.clipboard).setPrimaryClip(ClipData.newPlainText(selectionStr, selectionStr));
                        } else {
                            this.clipboard.setText(selectionStr);
                        }
                        commitText("", 1, true);
                        return true;
                    }
                    commitText("", 1, false);
                }
                return this.target.performContextMenuAction(id);
            case R.id.copy:
                if (bTakeAction) {
                    String selectionStr2 = getSelectedTextInEditor();
                    if (this.clipboard instanceof android.content.ClipboardManager) {
                        android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) this.clipboard;
                        if (clipboardManager.getPrimaryClip() != null && !TextUtils.isEmpty(selectionStr2)) {
                            clipboardManager.setPrimaryClip(ClipData.newPlainText(selectionStr2, selectionStr2));
                        }
                    } else if (!TextUtils.isEmpty(selectionStr2)) {
                        this.clipboard.setText(selectionStr2);
                    }
                    return true;
                }
                return this.target.performContextMenuAction(id);
            case R.id.paste:
                if (this.clipboard instanceof android.content.ClipboardManager) {
                    android.content.ClipboardManager clipboardManager2 = (android.content.ClipboardManager) this.clipboard;
                    if (clipboardManager2.hasPrimaryClip() && (clipData = clipboardManager2.getPrimaryClip()) != null && clipData.getItemCount() > 0) {
                        if (bTakeAction) {
                            commitText(clipData.getItemAt(0).getText(), 1);
                            return true;
                        }
                        commitText(clipData.getItemAt(0).getText(), 1, false);
                    }
                } else if (this.clipboard.hasText()) {
                    if (bTakeAction) {
                        commitText(this.clipboard.getText(), 1);
                        return true;
                    }
                    commitText(this.clipboard.getText(), 1, false);
                }
                return this.target.performContextMenuAction(id);
            default:
                return this.target.performContextMenuAction(id);
        }
    }

    private void adjustCacheAfterDeletion(int beforeLength, int afterLength) {
        int composingLen;
        if (-1 != this.cachedCursorPosition && isCacheSyncedWithEditor()) {
            int remainingChars = this.cachedComposingText.length() - beforeLength;
            if (remainingChars >= 0) {
                this.cachedComposingText.setLength(remainingChars);
                composingLen = remainingChars;
            } else {
                this.cachedComposingText.setLength(0);
                composingLen = 0;
                int len = Math.max(this.cachedCommittedText.length() + remainingChars, 0);
                this.cachedCommittedText.setLength(len);
            }
            if (this.cachedCursorPosition > beforeLength) {
                this.cachedCursorPosition -= beforeLength;
            } else {
                this.cachedCursorPosition = 0;
            }
            this.cachedSelectionEnd = this.cachedCursorPosition;
            this.cachedSelectedText.setLength(0);
            if (composingLen == 0) {
                this.editorComposingEnd = -1;
                this.editorComposingStart = -1;
            } else {
                this.editorComposingStart = this.cachedCommittedText.length();
                this.editorComposingEnd = this.editorComposingStart + remainingChars;
            }
            int textLength = this.cachedAfterCursorText.length();
            if (afterLength > 0 && textLength > 0) {
                if (afterLength < textLength) {
                    this.cachedAfterCursorText.delete(0, afterLength);
                } else {
                    this.cachedAfterCursorText.setLength(0);
                }
            }
        }
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        if (beforeLength == 1 && afterLength == 0 && this.appSpecific.shouldDeleteSurroundingTextUsingKeyEvent()) {
            this.mSkipCacheChangeInSendKeyEvent = true;
            sendDownUpKeyEvents(67);
            return true;
        }
        adjustCacheAfterDeletion(beforeLength, afterLength);
        if (afterLength == 0 && this.appSpecific.shouldDeleteSurroundingBeforeTextCharByChar()) {
            boolean bVal = true;
            for (int i = 0; i < beforeLength && bVal; i++) {
                bVal = this.target.deleteSurroundingText(1, 0);
            }
            return bVal;
        }
        return this.target.deleteSurroundingText(beforeLength, afterLength);
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public boolean setSelection(int start, int end) {
        return setSelection(start, end, true);
    }

    public boolean setSelection(int start, int end, boolean bSet) {
        Boolean bVal = false;
        if (bSet && this.appSpecific.noSetSelection()) {
            return bVal.booleanValue();
        }
        if (hasComposing()) {
            this.extractViewState.onFlushActiveWord();
        }
        if (bSet) {
            bVal = Boolean.valueOf(this.target.setSelection(start, end));
        }
        rebuildInternalStrings(Math.min(start, end), Math.max(start, end), false);
        return bVal.booleanValue();
    }

    public boolean setSelection(int start, int end, int reTryNum) {
        Boolean bVal = false;
        while (true) {
            int reTryNum2 = reTryNum;
            reTryNum = reTryNum2 - 1;
            if (reTryNum2 <= 0) {
                break;
            }
            this.target.setSelection(start, end);
            reSyncFromEditor();
            int[] selection = getSelectionRangeInEditor();
            if (getComposingRangeInEditor() != null) {
                this.extractViewState.onFlushActiveWord();
            }
            if (selection != null && selection[0] == start && selection[1] == end) {
                bVal = true;
                break;
            }
        }
        return bVal.booleanValue();
    }

    public void reSyncFromEditor() {
        clearExtractedTextCache();
        ExtractedTextRequest request = new ExtractedTextRequest();
        request.flags = 1;
        getExtractedText(request, 0);
    }

    private void rebuildInternalStrings(int start, int end, boolean shouldSyncFromEditor) {
        if (isCacheSyncedWithEditor()) {
            if (shouldSyncFromEditor) {
                reSyncFromEditor();
                return;
            }
            int cachIndexStart = (this.cachedCursorPosition - this.cachedComposingText.length()) - this.cachedCommittedText.length();
            int cacheIndexEnd = this.cachedSelectionEnd + this.cachedAfterCursorText.length();
            log.d("rebuildInternalStrings - cachIndexStart:" + cachIndexStart + " cacheIndexEnd:" + cacheIndexEnd + " start:" + start + " end:" + end, " cursor cachedCursorPosition old:", Integer.valueOf(this.cachedCursorPosition));
            if (cachIndexStart == this.editorExtractedTextOffset && start >= cachIndexStart && end <= cacheIndexEnd) {
                this.cachedCursorPosition = start;
                log.d("cursor new cachedCursorPosition", Integer.valueOf(this.cachedCursorPosition));
                this.cachedSelectionEnd = end;
                StringBuilder allString = new StringBuilder(this.cachedCommittedText.append((CharSequence) this.cachedComposingText).append((CharSequence) this.cachedSelectedText).append((CharSequence) this.cachedAfterCursorText));
                int txtLength = allString.length();
                int internalStart = Math.min(Math.max(this.cachedCursorPosition - this.editorExtractedTextOffset, 0), txtLength);
                int internalEnd = Math.min(this.cachedSelectionEnd - this.editorExtractedTextOffset, txtLength);
                this.cachedSelectedText.setLength(0);
                if (this.cachedCursorPosition != this.cachedSelectionEnd) {
                    this.cachedSelectedText.append(allString.substring(internalStart, internalEnd));
                }
                this.cachedCommittedText.setLength(0);
                this.cachedCommittedText.append(allString.substring(0, internalStart));
                this.cachedAfterCursorText.setLength(0);
                this.cachedAfterCursorText.append(allString.substring(internalEnd));
            } else {
                this.cachedCursorPosition = -1;
                getExtractedText(new ExtractedTextRequest(), 0);
            }
            log.d("rebuildInternalStrings - cachedCommittedText:" + ((Object) this.cachedCommittedText) + " cachedComposingText:" + ((Object) this.cachedComposingText) + " cachedSelectedText:" + ((Object) this.cachedSelectedText) + " cachedAfterCursorText:" + ((Object) this.cachedAfterCursorText));
        }
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public boolean setComposingRegion(int start, int end) {
        int cachedLength;
        if (this.appSpecific.shouldAvoidSetComposingRegion()) {
            return false;
        }
        if (isCacheSyncedWithEditor() && (cachedLength = this.cachedCommittedText.length() + this.cachedComposingText.length()) > 0) {
            StringBuilder s = new StringBuilder(this.cachedCommittedText);
            s.append(this.cachedComposingText.toString());
            this.cachedCommittedText.setLength(0);
            int indexOfStartOfComposingText = Math.max(cachedLength - (end - start), 0);
            this.cachedComposingText.append(s.subSequence(indexOfStartOfComposingText, cachedLength));
            log.d("cursor setComposingRegion cachedComposingText,", this.cachedComposingText.toString());
            this.cachedCommittedText.append(s.subSequence(0, indexOfStartOfComposingText));
            this.editorComposingStart = this.editorExtractedTextOffset + indexOfStartOfComposingText;
            this.editorComposingEnd = this.editorComposingStart + this.cachedComposingText.length();
        }
        return InputConnectionCompat.setComposingRegion(this.target, start, end);
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public boolean setComposingText(CharSequence text, int newCursorPosition) {
        int cachedCursorPositionBackup = this.cachedCursorPosition;
        int cachedSelectionEndBackup = this.cachedSelectionEnd;
        int editorComposingEndBackup = this.editorComposingEnd;
        int editorComposingStartBackup = this.editorComposingStart;
        String cachedComposingTextBackup = this.cachedComposingText.toString();
        String cachedSelectedTextBackup = this.cachedSelectedText.toString();
        log.d("cursor setComposingText cachedCursorPosition before:", Integer.valueOf(this.cachedCursorPosition));
        if (isCacheSyncedWithEditor()) {
            if (hasSelection() && this.cachedCommittedText.length() > 0 && this.cachedCursorPosition >= 0 && this.cachedCursorPosition < this.cachedSelectionEnd && this.cachedSelectionEnd <= this.cachedCommittedText.length()) {
                this.cachedCommittedText.delete(this.cachedCursorPosition, this.cachedSelectionEnd);
            }
            this.cachedCursorPosition += text.length() - this.cachedComposingText.length();
            this.cachedComposingText.setLength(0);
            this.cachedComposingText.append(text);
            log.d("cursor setComposingText cachedCursorPosition:", Integer.valueOf(this.cachedCursorPosition), " cachedComposingText:", this.cachedComposingText.toString());
            this.cachedSelectionEnd = this.cachedCursorPosition;
            this.cachedSelectedText.setLength(0);
            this.editorComposingEnd = this.cachedCursorPosition;
            this.editorComposingStart = this.editorComposingEnd - this.cachedComposingText.length();
        }
        log.d("cursor setComposingText cachedCursorPosition after:", Integer.valueOf(this.cachedCursorPosition), " cachedComposingText:", this.cachedComposingText.toString());
        Boolean bVal = true;
        if (!this.appSpecific.shouldSkipWrongStartInputView()) {
            bVal = Boolean.valueOf(this.target.setComposingText(text, newCursorPosition));
        }
        if (!bVal.booleanValue() && isCacheSyncedWithEditor()) {
            this.cachedCursorPosition = cachedCursorPositionBackup;
            this.cachedComposingText.setLength(0);
            this.cachedComposingText.append(cachedComposingTextBackup);
            this.cachedSelectionEnd = cachedSelectionEndBackup;
            this.cachedSelectedText.setLength(0);
            this.cachedSelectedText.append(cachedSelectedTextBackup);
            this.editorComposingEnd = editorComposingEndBackup;
            this.editorComposingStart = editorComposingStartBackup;
        }
        return bVal.booleanValue();
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public boolean finishComposingText() {
        if (isCacheSyncedWithEditor()) {
            if (this.cachedComposingText.length() > 0) {
                if (!hasSelection()) {
                    this.cachedCommittedText.append((CharSequence) this.cachedComposingText);
                }
                this.cachedComposingText.setLength(0);
                log.d("cursor finishComposingText set cachedComposingText 0");
            }
            this.editorComposingEnd = 0;
            this.editorComposingStart = 0;
        }
        return this.target.finishComposingText();
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public boolean commitCompletion(CompletionInfo completionInfo) {
        if (isCacheSyncedWithEditor()) {
            CharSequence text = completionInfo.getText();
            if (text == null) {
                text = "";
            }
            this.cachedCommittedText.append(text);
            this.cachedCursorPosition += text.length() - this.cachedComposingText.length();
            this.cachedComposingText.setLength(0);
            log.d("cursor commitCompletion set cachedComposingText 0");
            this.cachedSelectionEnd = this.cachedCursorPosition;
            this.cachedSelectedText.setLength(0);
            this.editorComposingEnd = -1;
            this.editorComposingStart = -1;
        }
        if (this.target == null) {
            return true;
        }
        boolean bVal = this.target.commitCompletion(completionInfo);
        return bVal;
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public boolean sendKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0 && isCacheSyncedWithEditor() && !this.mSkipCacheChangeInSendKeyEvent) {
            switch (keyEvent.getKeyCode()) {
                case 0:
                    if (keyEvent.getCharacters() != null) {
                        this.cachedCommittedText.append(keyEvent.getCharacters());
                        this.cachedCursorPosition += keyEvent.getCharacters().length();
                        this.cachedSelectionEnd = this.cachedCursorPosition;
                        this.cachedSelectedText.setLength(0);
                        break;
                    }
                    break;
                case 66:
                    this.cachedCommittedText.append("\n");
                    this.cachedCursorPosition++;
                    this.cachedSelectionEnd = this.cachedCursorPosition;
                    this.cachedSelectedText.setLength(0);
                    break;
                case 67:
                    if (this.cachedComposingText.length() == 0) {
                        if (this.cachedCommittedText.length() > 0) {
                            this.cachedCommittedText.delete(this.cachedCommittedText.length() - 1, this.cachedCommittedText.length());
                        }
                    } else {
                        log.d("cursor befoer sendKeyEvent cachedComposingText,", this.cachedComposingText.toString());
                        this.cachedComposingText.delete(this.cachedComposingText.length() - 1, this.cachedComposingText.length());
                        log.d("cursor after sendKeyEvent cachedComposingText,", this.cachedComposingText.toString());
                    }
                    if (this.cachedCursorPosition > 0) {
                        this.cachedCursorPosition--;
                    }
                    this.cachedSelectionEnd = this.cachedCursorPosition;
                    this.cachedSelectedText.setLength(0);
                    break;
                default:
                    if (keyEvent.getUnicodeChar() != 0) {
                        String text = new String(new int[]{keyEvent.getUnicodeChar()}, 0, 1);
                        if (text.length() > 0) {
                            this.cachedCommittedText.append(text);
                            this.cachedCursorPosition += text.length();
                            this.cachedSelectionEnd = this.cachedCursorPosition;
                            this.cachedSelectedText.setLength(0);
                            break;
                        }
                    }
                    break;
            }
            this.editorComposingStart = this.cachedCommittedText.length();
            this.editorComposingEnd = this.editorComposingStart + this.cachedComposingText.length();
        }
        this.mSkipCacheChangeInSendKeyEvent = false;
        if (this.target != null) {
            return this.target.sendKeyEvent(keyEvent);
        }
        return false;
    }

    @TargetApi(11)
    public void sendDownUpKeyEvents(int keyEventCode) {
        long eventTime = SystemClock.uptimeMillis();
        sendKeyEvent(new KeyEvent(eventTime, eventTime, 0, keyEventCode, 0, 0, -1, 0, 6));
        sendKeyEvent(new KeyEvent(eventTime, SystemClock.uptimeMillis(), 1, keyEventCode, 0, 0, -1, 0, 6));
    }

    public boolean resetInternalEditingStates(int newSelStart, int newSelEnd, boolean shouldSyncFromEditor) {
        int start = Math.min(newSelStart, newSelEnd);
        int end = Math.max(newSelStart, newSelEnd);
        rebuildInternalStrings(start, end, shouldSyncFromEditor);
        return true;
    }

    public int getCapsModeAtCursor(EditorInfo attr) {
        if (!isCacheSyncedWithEditor()) {
            return InputConnectionUtils.getCapsMode(this.target, attr);
        }
        if (!TextUtils.isEmpty(this.cachedComposingText)) {
            return attr.inputType & HardKeyboardManager.META_CTRL_ON;
        }
        if (TextUtils.isEmpty(this.cachedCommittedText) && this.cachedCursorPosition != 0) {
            CharSequence textBeforeCursor = getTextBeforeCursor(1024, 0);
            if (!TextUtils.isEmpty(textBeforeCursor)) {
                this.cachedCommittedText.append(textBeforeCursor);
            }
        }
        return InputConnectionUtils.getCapsMode(this.cachedCommittedText, this.cachedCommittedText.length(), attr.inputType);
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public CharSequence getTextBeforeCursor(int n, int flags) {
        return getTextBeforeCursor(n, flags, true);
    }

    private CharSequence getTextBeforeCursor(int n, int flags, boolean includeComposing) {
        if (isCacheSyncedWithEditor()) {
            int cachedLength = this.cachedCommittedText.length() + this.cachedComposingText.length();
            if (-1 != this.cachedCursorPosition && (cachedLength >= n || cachedLength >= this.cachedCursorPosition - this.editorExtractedTextOffset)) {
                StringBuilder s = new StringBuilder(this.cachedCommittedText);
                if (includeComposing) {
                    s.append(this.cachedComposingText.toString());
                    if (cachedLength > n) {
                        s.delete(0, cachedLength - n);
                        return s;
                    }
                    return s;
                }
                return s;
            }
        }
        if (this.target != null) {
            return this.target.getTextBeforeCursor(n, flags);
        }
        return null;
    }

    public CharSequence getTextBeforeCursorWithOutComposingText(int n, int flag) {
        return getTextBeforeCursor(n, flag, false);
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public CharSequence getTextAfterCursor(int n, int flags) {
        if (isCacheSyncedWithEditor()) {
            int cachedLength = this.cachedAfterCursorText.length();
            if (-1 != this.cachedSelectionEnd) {
                if (cachedLength > n) {
                    return this.cachedAfterCursorText.subSequence(0, n);
                }
                return this.cachedAfterCursorText;
            }
        }
        if (this.target != null) {
            return this.target.getTextAfterCursor(n, flags);
        }
        return null;
    }

    public void clearExtractedTextCache() {
        this.cachedCursorPosition = -1;
        this.editorExtractedTextOffset = 0;
        this.editorComposingStart = 0;
        this.editorComposingEnd = 0;
        this.isEditorTextRetrievable = false;
    }

    public boolean isCursorUpdateFromKeyboard(int oldSelStart, int newSelStart, int oldSelEnd, int newSelEnd) {
        int sum = newSelStart + newSelEnd;
        int newSelStart2 = Math.min(newSelStart, newSelEnd);
        int newSelEnd2 = sum - newSelStart2;
        int sum2 = oldSelStart + oldSelEnd;
        int oldSelStart2 = Math.min(oldSelStart, oldSelEnd);
        int oldSelEnd2 = sum2 - oldSelStart2;
        if (this.cachedCursorPosition == newSelStart2 && this.cachedSelectionEnd == newSelEnd2) {
            return true;
        }
        if ((oldSelStart2 != this.cachedCursorPosition || oldSelStart2 == newSelStart2) && (oldSelEnd2 != this.cachedSelectionEnd || oldSelEnd2 == newSelEnd2)) {
            return (newSelStart2 - oldSelStart2) * (this.cachedCursorPosition - newSelStart2) >= 0;
        }
        return false;
    }

    public int[] getSelectionRangeInEditor() {
        if (!this.byPassCache && isCacheSyncedWithEditor() && this.cachedCursorPosition != -1 && this.cachedSelectionEnd != -1) {
            return new int[]{this.cachedCursorPosition, this.cachedSelectionEnd};
        }
        ExtractedTextRequest request = new ExtractedTextRequest();
        return InputConnectionUtils.getSelection(this.target.getExtractedText(request, 0));
    }

    public int[] getComposingRangeInEditor() {
        if (!this.byPassCache && isCacheSyncedWithEditor()) {
            if (this.editorComposingStart != this.editorComposingEnd && this.editorComposingStart >= 0 && this.editorComposingEnd >= 0) {
                return new int[]{Math.min(this.editorComposingStart, this.editorComposingEnd), Math.max(this.editorComposingStart, this.editorComposingEnd)};
            }
            return null;
        }
        return InputConnectionUtils.getComposing(this.target);
    }

    public boolean hasSelection() {
        if (!isCacheSyncedWithEditor() || this.byPassCache) {
            return InputConnectionUtils.hasSelection(this.target);
        }
        return this.cachedCursorPosition != this.cachedSelectionEnd;
    }

    public boolean hasComposing() {
        return (!isCacheSyncedWithEditor() || this.byPassCache) ? InputConnectionUtils.getComposing(this.target) != null : this.editorComposingStart != this.editorComposingEnd;
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0035, code lost:            r0 = r1 - r11.length();     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x003b, code lost:            if (r0 < 0) goto L31;     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x003d, code lost:            r2 = r10.cachedCommittedText.subSequence(r0, r1);     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0047, code lost:            if (r2.equals(r11) == false) goto L25;     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0049, code lost:            if (r13 == false) goto L21;     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x004b, code lost:            r1 = r10.cachedCursorPosition - r10.editorExtractedTextOffset;     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x005b, code lost:            if (setComposingRegion(r10.editorExtractedTextOffset + r0, r10.editorExtractedTextOffset + r1) != false) goto L24;     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x005d, code lost:            com.nuance.swype.input.appspecific.AppSpecificInputConnection.log.d("setComposingRegionBeforeCursor(): setComposingRegion failed (alternate approach)");        beginBatchEdit();        setSelection(r10.editorExtractedTextOffset + r0, r10.editorExtractedTextOffset + r1);        commitText("", 1);        setComposingText(r11, 1);        endBatchEdit();     */
    /* JADX WARN: Code restructure failed: missing block: B:26:?, code lost:            return r10.cachedCursorPosition;     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0084, code lost:            com.nuance.swype.input.appspecific.AppSpecificInputConnection.log.d("setComposingRegionBeforeCursor(): no match! prev: " + ((java.lang.Object) r2) + "; text: " + ((java.lang.Object) r11));     */
    /* JADX WARN: Code restructure failed: missing block: B:28:?, code lost:            return -1;     */
    /* JADX WARN: Code restructure failed: missing block: B:29:?, code lost:            return -1;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int setComposingRegionBeforeCursor(java.lang.CharSequence r11, int r12, boolean r13) {
        /*
            r10 = this;
            r9 = 0
            r4 = -1
            r8 = 1
            boolean r5 = r10.isCacheSyncedWithEditor()
            if (r5 == 0) goto Laa
            java.lang.StringBuilder r5 = r10.cachedCommittedText
            int r5 = r5.length()
            if (r5 != 0) goto L12
        L11:
            return r4
        L12:
            int r5 = r10.cachedCursorPosition
            int r6 = r10.editorExtractedTextOffset
            int r1 = r5 - r6
            r3 = r12
        L19:
            int r12 = r3 + (-1)
            if (r3 <= 0) goto L33
            if (r1 <= 0) goto L33
            com.nuance.swype.util.CharacterUtilities r5 = r10.charUtils
            java.lang.StringBuilder r6 = r10.cachedCommittedText
            int r7 = r1 + (-1)
            char r6 = r6.charAt(r7)
            boolean r5 = com.nuance.swype.util.InputConnectionUtils.isWordChar(r5, r6)
            if (r5 != 0) goto L33
            int r1 = r1 + (-1)
            r3 = r12
            goto L19
        L33:
            if (r1 <= 0) goto L11
            int r5 = r11.length()
            int r0 = r1 - r5
            if (r0 < 0) goto L11
            java.lang.StringBuilder r5 = r10.cachedCommittedText
            java.lang.CharSequence r2 = r5.subSequence(r0, r1)
            boolean r5 = r2.equals(r11)
            if (r5 == 0) goto L84
            if (r13 == 0) goto L51
            int r4 = r10.cachedCursorPosition
            int r5 = r10.editorExtractedTextOffset
            int r1 = r4 - r5
        L51:
            int r4 = r10.editorExtractedTextOffset
            int r4 = r4 + r0
            int r5 = r10.editorExtractedTextOffset
            int r5 = r5 + r1
            boolean r4 = r10.setComposingRegion(r4, r5)
            if (r4 != 0) goto L81
            com.nuance.swype.util.LogManager$Log r4 = com.nuance.swype.input.appspecific.AppSpecificInputConnection.log
            java.lang.Object[] r5 = new java.lang.Object[r8]
            java.lang.String r6 = "setComposingRegionBeforeCursor(): setComposingRegion failed (alternate approach)"
            r5[r9] = r6
            r4.d(r5)
            r10.beginBatchEdit()
            int r4 = r10.editorExtractedTextOffset
            int r4 = r4 + r0
            int r5 = r10.editorExtractedTextOffset
            int r5 = r5 + r1
            r10.setSelection(r4, r5)
            java.lang.String r4 = ""
            r10.commitText(r4, r8)
            r10.setComposingText(r11, r8)
            r10.endBatchEdit()
        L81:
            int r4 = r10.cachedCursorPosition
            goto L11
        L84:
            com.nuance.swype.util.LogManager$Log r5 = com.nuance.swype.input.appspecific.AppSpecificInputConnection.log
            java.lang.Object[] r6 = new java.lang.Object[r8]
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            java.lang.String r8 = "setComposingRegionBeforeCursor(): no match! prev: "
            r7.<init>(r8)
            java.lang.StringBuilder r7 = r7.append(r2)
            java.lang.String r8 = "; text: "
            java.lang.StringBuilder r7 = r7.append(r8)
            java.lang.StringBuilder r7 = r7.append(r11)
            java.lang.String r7 = r7.toString()
            r6[r9] = r7
            r5.d(r6)
            goto L11
        Laa:
            android.view.inputmethod.InputConnection r4 = r10.target
            com.nuance.swype.util.CharacterUtilities r5 = r10.charUtils
            int r4 = com.nuance.swype.util.InputConnectionUtils.setComposingRegionBeforeCursor(r4, r5, r11, r12, r13)
            goto L11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.appspecific.AppSpecificInputConnection.setComposingRegionBeforeCursor(java.lang.CharSequence, int, boolean):int");
    }

    public String getSelectedTextInEditor(InputFieldInfo info) {
        if (info.isPasswordField()) {
            return null;
        }
        return getSelectedTextInEditor();
    }

    private String getSelectedTextInEditor() {
        if (-1 != this.cachedCursorPosition && -1 != this.cachedSelectionEnd && isCacheSyncedWithEditor()) {
            return this.cachedSelectedText.toString();
        }
        if (this.target != null) {
            return InputConnectionCompat.getSelectedTextString(this.target);
        }
        return null;
    }

    public boolean isCursorWithinWord(CharacterUtilities charUtils) {
        if (hasComposing()) {
            return false;
        }
        CharSequence cSeqBefore = getTextBeforeCursor(1, 0);
        if (TextUtils.isEmpty(cSeqBefore) || charUtils.isWordBoundary(cSeqBefore)) {
            return false;
        }
        CharSequence cSeqAfter = getTextAfterCursor(1, 0);
        return (TextUtils.isEmpty(cSeqAfter) || charUtils.isWordBoundary(cSeqAfter)) ? false : true;
    }

    private boolean isCacheSyncedWithEditor() {
        return this.isCacheUpdatesReliable && this.isEditorTextRetrievable;
    }

    public void setByPassCache(boolean bVal) {
        this.byPassCache = bVal;
    }

    public InputConnection getTarget() {
        return this.target;
    }

    public void clearHighlightedText() {
        if (this.appSpecific.useBackspaceKeyToClearHighlightedText()) {
            sendDownUpKeyEvents(67);
            if (hasSelection()) {
                sendDownUpKeyEvents(67);
            }
            reSyncFromEditor();
            return;
        }
        commitText("", 1);
    }
}
