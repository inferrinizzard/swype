package com.nuance.swype.input.settings;

import android.content.Context;
import android.util.AttributeSet;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.swype.input.CandidatesListView;
import com.nuance.swype.input.WordCandidatesListView;

/* loaded from: classes.dex */
public class CandidateSizeSettingView extends WordCandidatesListView {
    public CandidateSizeSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDisplayText(String inString) {
        Candidates candidates = new Candidates(Candidates.Source.TAP);
        candidates.add(new WordCandidate(inString));
        setSuggestions(candidates, CandidatesListView.Format.DEFAULT);
    }

    public void setTextSize(float size) {
        setCandidateSize(size);
        if (this.mPaint != null) {
            this.mPaint.setTextSize(this.mTextSize);
        }
    }
}
