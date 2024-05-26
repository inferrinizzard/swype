package com.nuance.swype.util;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.InputConnection;
import com.nuance.android.compat.SuggestionSpanCompat;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.R;
import java.text.BreakIterator;

/* loaded from: classes.dex */
public final class WordDecorator {
    private final InputView inputView;
    private int underlineColor;
    private float underlineThickness;

    public WordDecorator(InputView inputView) {
        this.inputView = inputView;
        IMEApplication from = IMEApplication.from(inputView.getContext());
        this.underlineColor = from.getThemedColor(R.attr.unrecognizedWordUnderlineHighlightColor);
        this.underlineThickness = from.getThemedDimension(R.attr.unrecognizedWordUnderlineThickness);
    }

    public final CharSequence decorateUnrecognizedWord(CharSequence text) {
        SpannableString string = new SpannableString(text);
        Object createSpan = SuggestionSpanCompat.createSpan(this.inputView.getContext(), this.underlineColor, (int) this.underlineThickness);
        if (createSpan == null) {
            createSpan = new UnderlineSpan();
        }
        string.setSpan(createSpan, 0, string.length(), 33);
        return string;
    }

    private static void replace(InputConnection ic, int begin, int end, CharSequence with) {
        ic.beginBatchEdit();
        ic.setSelection(begin, end);
        ic.commitText(with, 1);
        ic.endBatchEdit();
    }

    public static void removeDecoration(Context context, InputConnection ic, ExtractedText eText, CharSequence what) {
        CharSequence stripped;
        String text = eText.text.toString();
        BreakIterator bi = BreakIterator.getWordInstance(context.getResources().getConfiguration().locale);
        bi.setText(eText.text.toString());
        Spanned spanned = null;
        if (SuggestionSpanCompat.CLASS_SuggestionSpan != null && (eText.text instanceof Spanned)) {
            spanned = (Spanned) eText.text;
        }
        int start = bi.first();
        for (int end = bi.next(); end != -1; end = bi.next()) {
            String word = text.substring(start, end);
            if (word.equals(what)) {
                if (spanned == null) {
                    replace(ic, eText.startOffset + start, eText.startOffset + end, word);
                } else {
                    int i = eText.startOffset + start;
                    int i2 = eText.startOffset + end;
                    Object[] spans = spanned.getSpans(i, i2, SuggestionSpanCompat.CLASS_SuggestionSpan);
                    if (spans != null) {
                        SpannableStringBuilder spannableStringBuilder = null;
                        for (Object obj : spans) {
                            if (SuggestionSpanCompat.isAutoCorrectionSpan(obj)) {
                                if (spannableStringBuilder == null) {
                                    spannableStringBuilder = new SpannableStringBuilder(spanned.subSequence(i, i2));
                                }
                                spannableStringBuilder.removeSpan(obj);
                            }
                        }
                        stripped = spannableStringBuilder;
                    } else {
                        stripped = null;
                    }
                    if (stripped != null) {
                        replace(ic, eText.startOffset + start, eText.startOffset + end, stripped);
                    }
                }
            }
            start = end;
        }
    }
}
