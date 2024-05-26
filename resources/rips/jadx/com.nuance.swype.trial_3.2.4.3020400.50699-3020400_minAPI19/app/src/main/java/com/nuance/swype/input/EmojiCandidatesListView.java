package com.nuance.swype.input;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

/* loaded from: classes.dex */
public class EmojiCandidatesListView extends CandidatesListView {
    public EmojiCandidatesListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.nuance.swype.input.CandidatesListView
    int getMinCandidateWidth(Context context) {
        return 0;
    }

    @Override // com.nuance.swype.input.CandidatesListView
    void setColor(Paint paint, int color) {
    }

    @Override // com.nuance.swype.input.CandidatesListView
    void setTypeface(Paint paint, Typeface typeface) {
    }

    @Override // com.nuance.swype.input.CandidatesListView
    public void updateCandidatesSize() {
        if (this.mPaint != null) {
            getCandidateSize();
            float scaleFactor = getContext().getResources().getFraction(R.fraction.ecl_scale_factor, 1, 1);
            this.mPaint.setTextSize(this.mTextSize + ((int) (this.mTextSize * scaleFactor)));
        }
    }
}
