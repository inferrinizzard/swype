package com.nuance.android.compat;

import android.content.Context;
import android.text.ParcelableSpan;
import android.text.style.UnderlineSpan;

/* loaded from: classes.dex */
public class CorrectionSpanFactory {
    private static final Boolean hasSuggestionSpan = CompatUtil.doesClassExist("android.text.style.SuggestionSpan");

    public ParcelableSpan createSpan(Context context, int underlineColor, int underlineThickness) {
        return !hasSuggestionSpan.booleanValue() ? new UnderlineSpan() : CorrectionSpan.createCorrectionSpan(context, underlineColor, underlineThickness);
    }
}
