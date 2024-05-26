package com.nuance.swype.util;

import android.R;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import com.nuance.android.compat.InputConnectionCompat;
import com.nuance.swype.input.appspecific.AppSpecificInputConnection;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swype.util.LogManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public final class InputConnectionUtils {
    private static final LogManager.Log log = LogManager.getLog("InputConnectionUtils");
    private static final Set<Character> terminals = new HashSet(Arrays.asList((char) 191, (char) 161, (char) 183, (char) 167));

    public static int getCapsMode(InputConnection ic, EditorInfo attr) {
        ExtractedText eText;
        if (ic == null || attr == null || (eText = ic.getExtractedText(new ExtractedTextRequest(), 0)) == null || eText.text == null) {
            return 0;
        }
        int caps = getCapsMode(eText.text, Math.min(eText.selectionStart, eText.selectionEnd), attr.inputType);
        return caps;
    }

    public static int getCapsMode(CharSequence cs, int off, int reqModes) {
        int caps = TextUtils.getCapsMode(cs, off, reqModes);
        if (caps == 0 && (reqModes & HardKeyboardManager.META_CTRL_RIGHT_ON) != 0 && off <= cs.length()) {
            if (cs.length() > 1 && cs.charAt(off - 1) == ' ' && cs.charAt(off - 2) == '.') {
                return HardKeyboardManager.META_CTRL_RIGHT_ON;
            }
            int i = off - 1;
            while (true) {
                if (i < 0) {
                    break;
                }
                char c = cs.charAt(i);
                if (CharacterUtilities.isWhiteSpace(c) || c == '\'' || c == '\"') {
                    i--;
                } else if (terminals.contains(Character.valueOf(c))) {
                    caps = HardKeyboardManager.META_CTRL_RIGHT_ON;
                }
            }
        }
        return caps;
    }

    public static boolean hasSelection(InputConnection ic) {
        ExtractedText eText;
        return (ic == null || (eText = ic.getExtractedText(new ExtractedTextRequest(), 0)) == null || eText.selectionStart == eText.selectionEnd) ? false : true;
    }

    public static boolean isComposingTextSelected(InputConnection ic) {
        int[] composing;
        if (ic == null) {
            return false;
        }
        ExtractedTextRequest request = new ExtractedTextRequest();
        request.flags = 1;
        ExtractedText eText = ic.getExtractedText(request, 0);
        int[] selection = getSelection(eText);
        return (selection == null || selection[0] == selection[1] || (composing = getComposing(eText)) == null || composing[0] == composing[1] || selection[0] != composing[0] || selection[1] != composing[1]) ? false : true;
    }

    public static int[] getComposing(InputConnection ic) {
        if (ic == null) {
            return null;
        }
        ExtractedTextRequest request = new ExtractedTextRequest();
        request.flags = 1;
        ExtractedText eText = ic.getExtractedText(request, 0);
        int[] range = getComposing(eText);
        if (range == null && eText != null && !TextUtils.isEmpty(eText.text)) {
            return getComposing(ic.getTextBeforeCursor(64, 1));
        }
        return range;
    }

    public static int[] getComposing(CharSequence text) {
        Spanned textSpanned;
        Object[] spans;
        int[] range = null;
        if (!TextUtils.isEmpty(text) && (text instanceof Spanned) && (spans = (textSpanned = (Spanned) text).getSpans(0, text.length(), Object.class)) != null) {
            for (Object span : spans) {
                if ((textSpanned.getSpanFlags(span) & 256) != 0) {
                    range = new int[]{textSpanned.getSpanStart(span), textSpanned.getSpanEnd(span)};
                }
            }
        }
        return range;
    }

    public static int[] getComposing(ExtractedText eText) {
        CharSequence text;
        Spanned textSpanned;
        Object[] spans;
        if (eText == null || TextUtils.isEmpty(eText.text) || (text = eText.text) == null || !(text instanceof Spanned) || (spans = (textSpanned = (Spanned) text).getSpans(0, text.length(), Object.class)) == null) {
            return null;
        }
        for (Object span : spans) {
            if ((textSpanned.getSpanFlags(span) & 256) != 0) {
                int start = textSpanned.getSpanStart(span) + eText.startOffset;
                int end = textSpanned.getSpanEnd(span) + eText.startOffset;
                int[] range = {Math.min(start, end), Math.max(start, end)};
                return range;
            }
        }
        return null;
    }

    public static void setComposingText(InputConnection ic, CharSequence text) {
        ic.setComposingText(text, 1);
    }

    public static void setSelection(InputConnection ic, int start, int end) {
        ic.beginBatchEdit();
        ic.setSelection(start, end);
        if (InputConnectionCompat.getSelectedText(ic, 0) == null) {
            ic.setSelection(start, end);
        }
        ic.endBatchEdit();
    }

    public static void selectAll(AppSpecificInputConnection ic) {
        CharSequence selectedStr;
        ic.beginBatchEdit();
        ic.performContextMenuAction(R.id.selectAll);
        int reTryNum = 2;
        ic.setByPassCache(true);
        while (true) {
            int reTryNum2 = reTryNum;
            reTryNum = reTryNum2 - 1;
            if (reTryNum2 <= 0 || ((selectedStr = InputConnectionCompat.getSelectedText(ic, 0)) != null && (selectedStr.length() <= 0 || ic.hasSelection()))) {
                break;
            } else {
                ic.performContextMenuAction(R.id.selectAll);
            }
        }
        ic.setByPassCache(false);
        ic.endBatchEdit();
    }

    public static int[] getSelection(ExtractedText extractedText) {
        int textLength;
        if (extractedText != null && (extractedText.selectionStart < 0 || extractedText.selectionEnd < 0)) {
            extractedText.selectionStart = extractedText.text != null ? extractedText.text.length() : 0;
            extractedText.selectionEnd = extractedText.selectionStart;
            return new int[]{extractedText.selectionStart, extractedText.selectionEnd};
        }
        if (extractedText != null && extractedText.text != null && extractedText.selectionStart >= 0 && extractedText.selectionEnd >= 0 && extractedText.selectionStart <= (textLength = extractedText.text.length()) && extractedText.selectionEnd <= textLength) {
            return new int[]{Math.min(extractedText.selectionStart, extractedText.selectionEnd), Math.max(extractedText.selectionStart, extractedText.selectionEnd)};
        }
        return null;
    }

    public static CharSequence getWordBeforeCursor$498a830e(InputConnection ic, CharacterUtilities charUtil) {
        CharSequence seqBeforeCursor;
        if (ic == null || (seqBeforeCursor = ic.getTextBeforeCursor(65, 0)) == null || seqBeforeCursor.length() <= 0) {
            return "";
        }
        int index = seqBeforeCursor.length() - 1;
        while (index >= 0 && !CharacterUtilities.isWhiteSpace(seqBeforeCursor.charAt(index)) && !charUtil.isWordSeparator(seqBeforeCursor.charAt(index))) {
            index--;
        }
        if ((seqBeforeCursor.length() - 1) - index <= 0) {
            return "";
        }
        CharSequence wordBeforeCursor = ic.getTextBeforeCursor((seqBeforeCursor.length() - 1) - index, 0);
        return wordBeforeCursor;
    }

    public static boolean isWordChar(CharacterUtilities charUtils, char ch) {
        return (Character.isWhitespace(ch) || charUtils.isWordSeparator(ch)) ? false : true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x0032, code lost:            r0 = r2 - r12.length();     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0038, code lost:            if (r0 < 0) goto L29;     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x003a, code lost:            r3 = r1.text.subSequence(r0, r2);     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0044, code lost:            if (r3.equals(r12) == false) goto L28;     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0046, code lost:            if (r14 == false) goto L23;     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0048, code lost:            r2 = r1.selectionEnd;     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0054, code lost:            if (com.nuance.android.compat.InputConnectionCompat.setComposingRegion(r10, r1.startOffset + r0, r1.startOffset + r2) != false) goto L26;     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0056, code lost:            com.nuance.swype.util.InputConnectionUtils.log.d("setComposingRegionBeforeCursor(): setComposingRegion failed (alternate approach)");        r10.beginBatchEdit();        r10.setSelection(r1.startOffset + r0, r1.startOffset + r2);        r10.commitText("", 1);        r10.setComposingText(r12, 1);        r10.endBatchEdit();     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x007f, code lost:            return r1.startOffset + r1.selectionEnd;     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0080, code lost:            com.nuance.swype.util.InputConnectionUtils.log.d("setComposingRegionBeforeCursor(): no match! prev: " + ((java.lang.Object) r3) + "; text: " + ((java.lang.Object) r12));     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static int setComposingRegionBeforeCursor(android.view.inputmethod.InputConnection r10, com.nuance.swype.util.CharacterUtilities r11, java.lang.CharSequence r12, int r13, boolean r14) {
        /*
            r9 = 0
            r8 = 1
            if (r10 == 0) goto La4
            if (r12 == 0) goto La4
            android.view.inputmethod.ExtractedTextRequest r5 = new android.view.inputmethod.ExtractedTextRequest
            r5.<init>()
            android.view.inputmethod.ExtractedText r1 = r10.getExtractedText(r5, r9)
            if (r1 == 0) goto La6
            java.lang.CharSequence r5 = r1.text
            if (r5 == 0) goto La6
            int r2 = r1.selectionEnd
            r4 = r13
        L18:
            int r13 = r4 + (-1)
            if (r4 <= 0) goto L30
            if (r2 <= 0) goto L30
            java.lang.CharSequence r5 = r1.text
            int r6 = r2 + (-1)
            char r5 = r5.charAt(r6)
            boolean r5 = isWordChar(r11, r5)
            if (r5 != 0) goto L30
            int r2 = r2 + (-1)
            r4 = r13
            goto L18
        L30:
            if (r2 <= 0) goto La4
            int r5 = r12.length()
            int r0 = r2 - r5
            if (r0 < 0) goto La4
            java.lang.CharSequence r5 = r1.text
            java.lang.CharSequence r3 = r5.subSequence(r0, r2)
            boolean r5 = r3.equals(r12)
            if (r5 == 0) goto L80
            if (r14 == 0) goto L4a
            int r2 = r1.selectionEnd
        L4a:
            int r5 = r1.startOffset
            int r5 = r5 + r0
            int r6 = r1.startOffset
            int r6 = r6 + r2
            boolean r5 = com.nuance.android.compat.InputConnectionCompat.setComposingRegion(r10, r5, r6)
            if (r5 != 0) goto L7a
            com.nuance.swype.util.LogManager$Log r5 = com.nuance.swype.util.InputConnectionUtils.log
            java.lang.Object[] r6 = new java.lang.Object[r8]
            java.lang.String r7 = "setComposingRegionBeforeCursor(): setComposingRegion failed (alternate approach)"
            r6[r9] = r7
            r5.d(r6)
            r10.beginBatchEdit()
            int r5 = r1.startOffset
            int r5 = r5 + r0
            int r6 = r1.startOffset
            int r6 = r6 + r2
            r10.setSelection(r5, r6)
            java.lang.String r5 = ""
            r10.commitText(r5, r8)
            r10.setComposingText(r12, r8)
            r10.endBatchEdit()
        L7a:
            int r5 = r1.startOffset
            int r6 = r1.selectionEnd
            int r5 = r5 + r6
        L7f:
            return r5
        L80:
            com.nuance.swype.util.LogManager$Log r5 = com.nuance.swype.util.InputConnectionUtils.log
            java.lang.Object[] r6 = new java.lang.Object[r8]
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            java.lang.String r8 = "setComposingRegionBeforeCursor(): no match! prev: "
            r7.<init>(r8)
            java.lang.StringBuilder r7 = r7.append(r3)
            java.lang.String r8 = "; text: "
            java.lang.StringBuilder r7 = r7.append(r8)
            java.lang.StringBuilder r7 = r7.append(r12)
            java.lang.String r7 = r7.toString()
            r6[r9] = r7
            r5.d(r6)
        La4:
            r5 = -1
            goto L7f
        La6:
            com.nuance.swype.util.LogManager$Log r5 = com.nuance.swype.util.InputConnectionUtils.log
            java.lang.Object[] r6 = new java.lang.Object[r8]
            java.lang.String r7 = "setComposingRegionBeforeCursor(): getExtractedText failed"
            r6[r9] = r7
            r5.d(r6)
            goto La4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.util.InputConnectionUtils.setComposingRegionBeforeCursor(android.view.inputmethod.InputConnection, com.nuance.swype.util.CharacterUtilities, java.lang.CharSequence, int, boolean):int");
    }
}
