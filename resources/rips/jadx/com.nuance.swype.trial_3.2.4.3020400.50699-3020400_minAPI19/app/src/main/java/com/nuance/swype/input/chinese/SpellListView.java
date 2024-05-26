package com.nuance.swype.input.chinese;

import android.content.Context;
import android.util.AttributeSet;

/* loaded from: classes.dex */
public class SpellListView extends CJKCandidatesListView {
    int mTouchedIndex;

    public SpellListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTouchedIndex = -1;
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView
    public void touchWord(int i, CharSequence suggestion) {
        this.mTouchedIndex = i;
        this.mSelectedString = suggestion;
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView
    protected void trySelect() {
        if (this.mSelectedString != null) {
            this.mOnWordSelectActionListener.selectWord(this.mTouchedIndex, this.mSelectedString, this);
        }
        if (this.mTouchedIndex >= 0) {
            this.mSelectedIndex = this.mTouchedIndex;
            scrollToSelection();
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView
    public void clear() {
        super.clear();
        this.mTouchedIndex = -1;
    }

    public int getListSize() {
        return this.mSuggestions.size();
    }

    public void scrollToSelection() {
        if (this.mSuggestions != null && !this.mSuggestions.isEmpty()) {
            int totalWidth = getWidth();
            int start = this.mWordX[this.mSelectedIndex] - getScrollX();
            int end = start + this.mWordWidth[this.mSelectedIndex];
            if (totalWidth != 0) {
                if (start < 0) {
                    updateScrollPosition(this.mWordX[this.mSelectedIndex]);
                } else if (end > totalWidth) {
                    updateScrollPosition((getScrollX() + end) - totalWidth);
                }
            }
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView
    public void updateCandidatesSize() {
        if (this.mPaint != null) {
            getCandidateSize();
            this.mPaint.setTextSize(this.mTextSize);
        }
    }
}
