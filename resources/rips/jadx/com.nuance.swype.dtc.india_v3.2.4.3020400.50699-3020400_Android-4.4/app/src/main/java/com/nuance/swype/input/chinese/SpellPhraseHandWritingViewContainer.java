package com.nuance.swype.input.chinese;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.nuance.swype.input.FunctionBarListView;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public class SpellPhraseHandWritingViewContainer extends CJKWordListViewContainer implements View.OnTouchListener {
    private View candidateContainer;
    private View mFunctionBarLayout;
    private FunctionBarListView mFunctionBarListView;
    private View mPhraseButtonLeft;
    private View mPhraseButtonRight;
    private View mPhraseLayout;
    private boolean mShowLeftArrow;
    private View spellPrefixSuffixButtonLeft;
    private View spellPrefixSuffixButtonRight;
    private SpellListView spellPrefixSuffixCandidates;
    private View spellPrefixSuffixLayout;

    public SpellPhraseHandWritingViewContainer(Context screen, AttributeSet attrs) {
        super(screen, attrs);
        this.mShowLeftArrow = false;
    }

    @Override // com.nuance.swype.input.chinese.CJKWordListViewContainer
    public void initViews() {
        super.initViews();
        if (this.candidateContainer == null) {
            this.candidateContainer = findViewById(R.id.candidate_container);
        }
        if (this.spellPrefixSuffixLayout == null) {
            this.spellPrefixSuffixLayout = findViewById(R.id.spell_layout_window);
            if (this.spellPrefixSuffixLayout != null) {
                this.spellPrefixSuffixLayout.setVisibility(8);
            }
            this.spellPrefixSuffixButtonLeft = findViewById(R.id.spell_candidate_left);
            if (this.spellPrefixSuffixButtonLeft != null) {
                this.spellPrefixSuffixButtonLeft.setOnTouchListener(this);
            }
            this.spellPrefixSuffixButtonRight = findViewById(R.id.spell_candidate_right);
            if (this.spellPrefixSuffixButtonRight != null) {
                this.spellPrefixSuffixButtonRight.setOnTouchListener(this);
            }
            this.spellPrefixSuffixCandidates = (SpellListView) findViewById(R.id.spell_candidates);
            this.spellPrefixSuffixCandidates.readStyles(R.style.WordListView);
            this.spellPrefixSuffixCandidates.init();
        }
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
        if (this.mFunctionBarListView == null) {
            this.mFunctionBarLayout = findViewById(R.id.functionbar_layout_window);
            this.mFunctionBarListView = (FunctionBarListView) findViewById(R.id.functionbar);
        }
    }

    public void clear() {
        if (this.spellPrefixSuffixCandidates != null) {
            this.spellPrefixSuffixCandidates.clear();
        }
        if (this.mCJKCandidates != null) {
            this.mCJKCandidates.clear();
        }
        if (this.mFunctionBarListView != null) {
            this.mFunctionBarListView.clear();
        }
        this.mShowLeftArrow = false;
        requestLayout();
    }

    public void clearSpellPrefixSuffixListView() {
        if (this.spellPrefixSuffixCandidates != null) {
            this.spellPrefixSuffixCandidates.clear();
        }
        requestLayout();
    }

    public View getSpellPrefixSuffixWordListView() {
        return this.spellPrefixSuffixCandidates;
    }

    public void hidePhraseListView() {
        this.mPhraseLayout.setVisibility(8);
        this.mCJKCandidates.setVisibility(8);
    }

    public void showPhraseListView() {
        this.mPhraseLayout.setVisibility(0);
        this.mCJKCandidates.setVisibility(0);
        setVisibility(0);
    }

    public void showFSHandwritingSpellList() {
        this.spellPrefixSuffixLayout.setVisibility(0);
        this.spellPrefixSuffixCandidates.setVisibility(0);
        setVisibility(0);
    }

    public void hideSpellPrefixSuffixList() {
        this.spellPrefixSuffixLayout.setVisibility(8);
        this.spellPrefixSuffixCandidates.setVisibility(8);
    }

    public void hideFunctionBarListView() {
        this.mFunctionBarLayout.setVisibility(8);
        this.mFunctionBarListView.setVisibility(8);
    }

    public void showFunctionBarListView() {
        this.mFunctionBarLayout.setVisibility(0);
        this.mFunctionBarListView.setVisibility(0);
        setVisibility(0);
        this.mFunctionBarListView.requestLayout();
    }

    public void updateScrollArrowVisibility() {
        if (this.spellPrefixSuffixLayout != null && this.spellPrefixSuffixCandidates != null) {
            if (this.spellPrefixSuffixCandidates.getListSize() > 0) {
                SpellPhraseViewContainer.updateScrollArrowVisibility(this.spellPrefixSuffixCandidates, false, this.spellPrefixSuffixButtonLeft, this.spellPrefixSuffixButtonRight);
            } else {
                if (this.spellPrefixSuffixButtonLeft != null) {
                    this.spellPrefixSuffixButtonLeft.setVisibility(8);
                }
                if (this.spellPrefixSuffixButtonRight != null) {
                    this.spellPrefixSuffixButtonRight.setVisibility(8);
                }
            }
        }
        if (this.mCJKCandidates != null && !this.mCJKCandidates.isShowingSwypeTooltip()) {
            SpellPhraseViewContainer.updateScrollArrowVisibility(this.mCJKCandidates, this.mShowLeftArrow, this.mPhraseButtonLeft, this.mPhraseButtonRight);
        }
        invalidate();
    }

    @Override // android.view.View.OnTouchListener
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == 0) {
            if (v == this.spellPrefixSuffixButtonRight) {
                this.spellPrefixSuffixCandidates.scrollNext();
                return false;
            }
            if (v == this.spellPrefixSuffixButtonLeft) {
                this.spellPrefixSuffixCandidates.scrollPrev();
                return false;
            }
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

    public final int getRightSpellPrefixSuffixImageButtonWidth() {
        if (this.spellPrefixSuffixButtonRight != null) {
            return this.spellPrefixSuffixButtonRight.getWidth();
        }
        return 0;
    }

    public final int getRightPhraseImageButtonWidth() {
        if (this.mPhraseButtonRight != null) {
            return this.mPhraseButtonRight.getWidth();
        }
        return 0;
    }

    public boolean isRightPhraseArrowShowable() {
        return this.mPhraseButtonRight != null && this.mPhraseButtonRight.isShown();
    }

    public boolean isRightSpellPrefixSuffixArrowShowable() {
        return this.spellPrefixSuffixButtonRight != null && this.spellPrefixSuffixButtonRight.isShown();
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    public void showLeftArrow(boolean showLeftArrow) {
        this.mShowLeftArrow = showLeftArrow;
        updateScrollArrowVisibility();
    }
}
