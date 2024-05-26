package com.nuance.swype.input.japanese;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.nuance.swype.input.R;
import com.nuance.swype.input.chinese.CJKWordListViewContainer;
import com.nuance.swype.input.chinese.SpellPhraseViewContainer;

/* loaded from: classes.dex */
public class JapaneseWordListViewContainer extends CJKWordListViewContainer implements View.OnTouchListener {
    private View mPhraseButtonLeft;
    private View mPhraseButtonRight;
    private View mPhraseLayout;
    private boolean mShowLeftArrow;

    public JapaneseWordListViewContainer(Context screen, AttributeSet attrs) {
        super(screen, attrs);
        this.mShowLeftArrow = false;
    }

    @Override // com.nuance.swype.input.chinese.CJKWordListViewContainer
    public void initViews() {
        super.initViews();
        if (this.mPhraseLayout == null) {
            this.mPhraseLayout = findViewById(R.id.phrase_layout_window);
            this.mPhraseButtonLeft = this.mPhraseLayout.findViewById(R.id.phrase_candidate_left);
            if (this.mPhraseButtonLeft != null) {
                this.mPhraseButtonLeft.setOnTouchListener(this);
            }
            this.mPhraseButtonRight = findViewById(R.id.phrase_candidate_right);
            if (this.mPhraseButtonRight != null) {
                this.mPhraseButtonRight.setOnTouchListener(this);
            }
        }
    }

    public void clearPhraseListView() {
        if (this.mCJKCandidates != null) {
            this.mCJKCandidates.clear();
        }
        this.mShowLeftArrow = false;
        requestLayout();
    }

    public void updateScrollArrowVisibility() {
        if (this.mCJKCandidates != null && !this.mCJKCandidates.isShowingSwypeTooltip()) {
            SpellPhraseViewContainer.updateScrollArrowVisibility(this.mCJKCandidates, this.mShowLeftArrow, this.mPhraseButtonLeft, this.mPhraseButtonRight);
            invalidate();
        }
    }

    @Override // android.view.View.OnTouchListener
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == 0) {
            if (v == this.mPhraseButtonRight) {
                this.mCJKCandidates.scrollNext();
                return false;
            }
            if (v == this.mPhraseButtonLeft) {
                this.mCJKCandidates.scrollPrev();
                return false;
            }
            return false;
        }
        return false;
    }
}
