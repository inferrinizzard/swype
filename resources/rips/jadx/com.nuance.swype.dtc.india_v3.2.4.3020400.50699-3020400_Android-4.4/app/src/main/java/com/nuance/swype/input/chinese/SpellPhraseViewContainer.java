package com.nuance.swype.input.chinese;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.nuance.swype.input.FunctionBarListView;
import com.nuance.swype.input.KeyboardViewEx;
import com.nuance.swype.input.R;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class SpellPhraseViewContainer extends CJKWordListViewContainer implements View.OnTouchListener {
    private View candidateContainer;
    private View mFunctionBarLayout;
    private FunctionBarListView mFunctionBarListView;
    private WeakReference<KeyboardViewEx> mKeyboardViewWeakRef;
    private View mPhraseButtonLeft;
    private View mPhraseButtonRight;
    private View mPhraseLayout;
    private boolean mShowLeftArrow;
    private SpellListView spellPrefixSuffixCandidates;
    private View spellPrefixSuffixLayout;

    public SpellPhraseViewContainer(Context screen, AttributeSet attrs) {
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

    public void setKeyboardViewEx(KeyboardViewEx keyboardView) {
        this.mKeyboardViewWeakRef = new WeakReference<>(keyboardView);
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
        invalidate();
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

    public void showSpellPrefixSuffixList() {
        hideFunctionBarListView();
        hidePhraseListView();
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
    }

    public static void updateScrollArrowVisibility(CJKCandidatesListView lv, boolean alwaysShowLeftArrow, View scrollLeft, View scrollRight) {
        int neededWidth = lv.computeHorizontalScrollRange();
        int candidateWidth = lv.getMeasuredWidth();
        int x = lv.getScrollX();
        boolean leftVisible = (alwaysShowLeftArrow || x > 0) && !lv.getAltCharacterConverted();
        boolean rightVisible = (x + candidateWidth < neededWidth || lv.isScrolling()) && !lv.getAltCharacterConverted();
        if (scrollLeft != null) {
            scrollLeft.setVisibility(leftVisible ? 0 : 8);
        }
        if (scrollRight != null) {
            scrollRight.setVisibility(rightVisible ? 0 : 8);
        }
    }

    public void updateScrollArrowVisibility() {
        updateScrollArrowVisibility(this.mCJKCandidates, this.mShowLeftArrow, this.mPhraseButtonLeft, this.mPhraseButtonRight);
        invalidate();
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.spellPrefixSuffixLayout != null && this.spellPrefixSuffixCandidates != null && this.spellPrefixSuffixCandidates.getListSize() > 0 && this.spellPrefixSuffixLayout.getVisibility() != 0 && getKeyboardView() != null) {
            getKeyboardView().clearKeyOffsets();
        }
        super.requestLayout();
    }

    private KeyboardViewEx getKeyboardView() {
        if (this.mKeyboardViewWeakRef != null) {
            return this.mKeyboardViewWeakRef.get();
        }
        return null;
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

    public void showLeftArrow(boolean showLeftArrow) {
        this.mShowLeftArrow = showLeftArrow;
        updateScrollArrowVisibility();
    }

    public View getFunctionBarListView() {
        return this.mFunctionBarListView;
    }
}
