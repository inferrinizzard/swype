package com.nuance.swype.input.chinese;

import android.content.Context;
import android.util.AttributeSet;
import com.nuance.swype.input.R;
import com.nuance.swype.input.chinese.CJKCandidatesListView;
import java.util.List;

/* loaded from: classes.dex */
public class VerCandidatesListContainer extends CJKWordListViewContainer {
    private VerCandidatesListView mCandidates;

    public VerCandidatesListContainer(Context screen, AttributeSet attrs) {
        super(screen, attrs);
    }

    @Override // com.nuance.swype.input.chinese.CJKWordListViewContainer
    public void setAttrs(CJKCandidatesListView.OnWordSelectActionListener aWordSelectedListener, int aStyleResID) {
        super.setAttrs(aWordSelectedListener, aStyleResID);
    }

    @Override // com.nuance.swype.input.chinese.CJKWordListViewContainer
    public void initViews() {
        if (this.mCandidates == null) {
            this.mCandidates = (VerCandidatesListView) findViewById(R.id.vertical_spell_candidates);
            this.mCandidates.readStyles(this.mStyleResID);
            this.mCandidates.setOnWordSelectActionVerListener(this.mWordSelectedListener);
            this.mCandidates.init();
            this.mCandidates.setContainer(this);
        }
    }

    public void setSpellPrefix(boolean prefix) {
        if (this.mCandidates != null) {
            this.mCandidates.setSpellPrefix(prefix);
        }
    }

    public void setCandidates(List<CharSequence> aPrefixList) {
        if (this.mCandidates != null && aPrefixList != null && !aPrefixList.isEmpty()) {
            this.mCandidates.setSuggestions(aPrefixList, 0);
        }
    }

    public void setCandidates(List<CharSequence> aPrefixList, int activePrefixIndex, boolean highlightPrefix) {
        if (this.mCandidates != null && aPrefixList != null && !aPrefixList.isEmpty()) {
            if (highlightPrefix) {
                this.mCandidates.enablePrefixHighlight();
            } else {
                this.mCandidates.disablePrefixHighlight();
            }
            this.mCandidates.setSuggestions(aPrefixList, activePrefixIndex);
        }
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        super.requestLayout();
    }

    public void clear() {
        this.mCandidates.clear();
    }

    public void updateCandidatesSize() {
        this.mCandidates.updateCandidatesSize();
    }

    public void setKeyboardHeight(int height) {
        if (this.mCandidates != null) {
            this.mCandidates.setKeyboardHeight(height);
        }
    }
}
