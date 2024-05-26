package com.nuance.swype.input.chinese;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.nuance.swype.input.R;
import com.nuance.swype.input.chinese.CJKCandidatesListView;

/* loaded from: classes.dex */
public class CJKWordListViewContainer extends LinearLayout {
    public CJKCandidatesListView mCJKCandidates;
    protected int mStyleResID;
    protected CJKCandidatesListView.OnWordSelectActionListener mWordSelectedListener;

    public CJKWordListViewContainer(Context screen, AttributeSet attrs) {
        super(screen, attrs);
        this.mCJKCandidates = null;
        this.mStyleResID = R.style.WordListView;
    }

    public void setAttrs(CJKCandidatesListView.OnWordSelectActionListener aWordSelectedListener, int aStyleResID) {
        this.mWordSelectedListener = aWordSelectedListener;
        this.mStyleResID = aStyleResID;
    }

    public void initViews() {
        if (this.mCJKCandidates == null) {
            this.mCJKCandidates = (CJKCandidatesListView) findViewById(R.id.cjk_candidates);
            this.mCJKCandidates.readStyles(this.mStyleResID);
            this.mCJKCandidates.init();
        }
    }

    public CJKCandidatesListView getCJKCandidatesListView() {
        return this.mCJKCandidates;
    }
}
