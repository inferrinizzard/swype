package com.nuance.swype.input;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

/* loaded from: classes.dex */
public class WordCandidatesListView extends CandidatesListView {
    public WordCandidatesListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.nuance.swype.input.CandidatesListView
    int getMinCandidateWidth(Context context) {
        DisplayMetrics dm = IMEApplication.from(context).getDisplay();
        return getResources().getConfiguration().orientation == 1 ? (int) (dm.widthPixels * 0.15d) : (int) (dm.widthPixels * 0.1d);
    }

    @Override // com.nuance.swype.input.CandidatesListView
    void setColor(Paint paint, int color) {
        paint.setColor(color);
    }

    @Override // com.nuance.swype.input.CandidatesListView
    void setTypeface(Paint paint, Typeface typeface) {
        paint.setTypeface(typeface);
    }
}
