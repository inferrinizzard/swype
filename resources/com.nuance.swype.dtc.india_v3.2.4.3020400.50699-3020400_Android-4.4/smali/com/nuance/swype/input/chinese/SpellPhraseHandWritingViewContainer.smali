.class public Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;
.super Lcom/nuance/swype/input/chinese/CJKWordListViewContainer;
.source "SpellPhraseHandWritingViewContainer.java"

# interfaces
.implements Landroid/view/View$OnTouchListener;


# instance fields
.field private candidateContainer:Landroid/view/View;

.field private mFunctionBarLayout:Landroid/view/View;

.field private mFunctionBarListView:Lcom/nuance/swype/input/FunctionBarListView;

.field private mPhraseButtonLeft:Landroid/view/View;

.field private mPhraseButtonRight:Landroid/view/View;

.field private mPhraseLayout:Landroid/view/View;

.field private mShowLeftArrow:Z

.field private spellPrefixSuffixButtonLeft:Landroid/view/View;

.field private spellPrefixSuffixButtonRight:Landroid/view/View;

.field private spellPrefixSuffixCandidates:Lcom/nuance/swype/input/chinese/SpellListView;

.field private spellPrefixSuffixLayout:Landroid/view/View;


# direct methods
.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "screen"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    .prologue
    .line 32
    invoke-direct {p0, p1, p2}, Lcom/nuance/swype/input/chinese/CJKWordListViewContainer;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    .line 27
    const/4 v0, 0x0

    iput-boolean v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mShowLeftArrow:Z

    .line 33
    return-void
.end method


# virtual methods
.method public clear()V
    .locals 1

    .prologue
    .line 81
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixCandidates:Lcom/nuance/swype/input/chinese/SpellListView;

    if-eqz v0, :cond_0

    .line 82
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixCandidates:Lcom/nuance/swype/input/chinese/SpellListView;

    invoke-virtual {v0}, Lcom/nuance/swype/input/chinese/SpellListView;->clear()V

    .line 84
    :cond_0
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mCJKCandidates:Lcom/nuance/swype/input/chinese/CJKCandidatesListView;

    if-eqz v0, :cond_1

    .line 85
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mCJKCandidates:Lcom/nuance/swype/input/chinese/CJKCandidatesListView;

    invoke-virtual {v0}, Lcom/nuance/swype/input/chinese/CJKCandidatesListView;->clear()V

    .line 87
    :cond_1
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mFunctionBarListView:Lcom/nuance/swype/input/FunctionBarListView;

    if-eqz v0, :cond_2

    .line 88
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mFunctionBarListView:Lcom/nuance/swype/input/FunctionBarListView;

    invoke-virtual {v0}, Lcom/nuance/swype/input/FunctionBarListView;->clear()V

    .line 90
    :cond_2
    const/4 v0, 0x0

    iput-boolean v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mShowLeftArrow:Z

    .line 91
    invoke-virtual {p0}, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->requestLayout()V

    .line 92
    return-void
.end method

.method public clearSpellPrefixSuffixListView()V
    .locals 1

    .prologue
    .line 95
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixCandidates:Lcom/nuance/swype/input/chinese/SpellListView;

    if-eqz v0, :cond_0

    .line 96
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixCandidates:Lcom/nuance/swype/input/chinese/SpellListView;

    invoke-virtual {v0}, Lcom/nuance/swype/input/chinese/SpellListView;->clear()V

    .line 98
    :cond_0
    invoke-virtual {p0}, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->requestLayout()V

    .line 99
    return-void
.end method

.method public final getRightPhraseImageButtonWidth()I
    .locals 1

    .prologue
    .line 193
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseButtonRight:Landroid/view/View;

    if-eqz v0, :cond_0

    .line 194
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseButtonRight:Landroid/view/View;

    invoke-virtual {v0}, Landroid/view/View;->getWidth()I

    move-result v0

    .line 197
    :goto_0
    return v0

    :cond_0
    const/4 v0, 0x0

    goto :goto_0
.end method

.method public final getRightSpellPrefixSuffixImageButtonWidth()I
    .locals 1

    .prologue
    .line 184
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixButtonRight:Landroid/view/View;

    if-eqz v0, :cond_0

    .line 185
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixButtonRight:Landroid/view/View;

    invoke-virtual {v0}, Landroid/view/View;->getWidth()I

    move-result v0

    .line 188
    :goto_0
    return v0

    :cond_0
    const/4 v0, 0x0

    goto :goto_0
.end method

.method public getSpellPrefixSuffixWordListView()Landroid/view/View;
    .locals 1

    .prologue
    .line 103
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixCandidates:Lcom/nuance/swype/input/chinese/SpellListView;

    return-object v0
.end method

.method public hideFunctionBarListView()V
    .locals 2

    .prologue
    const/16 v1, 0x8

    .line 129
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mFunctionBarLayout:Landroid/view/View;

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 130
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mFunctionBarListView:Lcom/nuance/swype/input/FunctionBarListView;

    invoke-virtual {v0, v1}, Lcom/nuance/swype/input/FunctionBarListView;->setVisibility(I)V

    .line 131
    return-void
.end method

.method public hidePhraseListView()V
    .locals 2

    .prologue
    const/16 v1, 0x8

    .line 107
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseLayout:Landroid/view/View;

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 108
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mCJKCandidates:Lcom/nuance/swype/input/chinese/CJKCandidatesListView;

    invoke-virtual {v0, v1}, Lcom/nuance/swype/input/chinese/CJKCandidatesListView;->setVisibility(I)V

    .line 109
    return-void
.end method

.method public hideSpellPrefixSuffixList()V
    .locals 2

    .prologue
    const/16 v1, 0x8

    .line 124
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixLayout:Landroid/view/View;

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 125
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixCandidates:Lcom/nuance/swype/input/chinese/SpellListView;

    invoke-virtual {v0, v1}, Lcom/nuance/swype/input/chinese/SpellListView;->setVisibility(I)V

    .line 126
    return-void
.end method

.method public initViews()V
    .locals 2

    .prologue
    .line 37
    invoke-super {p0}, Lcom/nuance/swype/input/chinese/CJKWordListViewContainer;->initViews()V

    .line 39
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->candidateContainer:Landroid/view/View;

    if-nez v0, :cond_0

    .line 40
    sget v0, Lcom/nuance/swype/input/R$id;->candidate_container:I

    invoke-virtual {p0, v0}, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->candidateContainer:Landroid/view/View;

    .line 42
    :cond_0
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixLayout:Landroid/view/View;

    if-nez v0, :cond_4

    .line 43
    sget v0, Lcom/nuance/swype/input/R$id;->spell_layout_window:I

    invoke-virtual {p0, v0}, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixLayout:Landroid/view/View;

    .line 44
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixLayout:Landroid/view/View;

    if-eqz v0, :cond_1

    .line 45
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixLayout:Landroid/view/View;

    const/16 v1, 0x8

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 47
    :cond_1
    sget v0, Lcom/nuance/swype/input/R$id;->spell_candidate_left:I

    invoke-virtual {p0, v0}, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixButtonLeft:Landroid/view/View;

    .line 48
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixButtonLeft:Landroid/view/View;

    if-eqz v0, :cond_2

    .line 49
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixButtonLeft:Landroid/view/View;

    invoke-virtual {v0, p0}, Landroid/view/View;->setOnTouchListener(Landroid/view/View$OnTouchListener;)V

    .line 51
    :cond_2
    sget v0, Lcom/nuance/swype/input/R$id;->spell_candidate_right:I

    invoke-virtual {p0, v0}, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixButtonRight:Landroid/view/View;

    .line 52
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixButtonRight:Landroid/view/View;

    if-eqz v0, :cond_3

    .line 53
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixButtonRight:Landroid/view/View;

    invoke-virtual {v0, p0}, Landroid/view/View;->setOnTouchListener(Landroid/view/View$OnTouchListener;)V

    .line 55
    :cond_3
    sget v0, Lcom/nuance/swype/input/R$id;->spell_candidates:I

    invoke-virtual {p0, v0}, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Lcom/nuance/swype/input/chinese/SpellListView;

    iput-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixCandidates:Lcom/nuance/swype/input/chinese/SpellListView;

    .line 56
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixCandidates:Lcom/nuance/swype/input/chinese/SpellListView;

    sget v1, Lcom/nuance/swype/input/R$style;->WordListView:I

    invoke-virtual {v0, v1}, Lcom/nuance/swype/input/chinese/SpellListView;->readStyles(I)V

    .line 57
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixCandidates:Lcom/nuance/swype/input/chinese/SpellListView;

    invoke-virtual {v0}, Lcom/nuance/swype/input/chinese/SpellListView;->init()V

    .line 60
    :cond_4
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseLayout:Landroid/view/View;

    if-nez v0, :cond_6

    .line 61
    sget v0, Lcom/nuance/swype/input/R$id;->phrase_layout_window:I

    invoke-virtual {p0, v0}, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseLayout:Landroid/view/View;

    .line 63
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseLayout:Landroid/view/View;

    sget v1, Lcom/nuance/swype/input/R$id;->phrase_candidate_left:I

    invoke-virtual {v0, v1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseButtonLeft:Landroid/view/View;

    .line 64
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseButtonLeft:Landroid/view/View;

    if-eqz v0, :cond_5

    .line 65
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseButtonLeft:Landroid/view/View;

    invoke-virtual {v0, p0}, Landroid/view/View;->setOnTouchListener(Landroid/view/View$OnTouchListener;)V

    .line 68
    :cond_5
    sget v0, Lcom/nuance/swype/input/R$id;->phrase_candidate_right:I

    invoke-virtual {p0, v0}, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseButtonRight:Landroid/view/View;

    .line 69
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseButtonRight:Landroid/view/View;

    if-eqz v0, :cond_6

    .line 70
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseButtonRight:Landroid/view/View;

    invoke-virtual {v0, p0}, Landroid/view/View;->setOnTouchListener(Landroid/view/View$OnTouchListener;)V

    .line 73
    :cond_6
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mFunctionBarListView:Lcom/nuance/swype/input/FunctionBarListView;

    if-nez v0, :cond_7

    .line 74
    sget v0, Lcom/nuance/swype/input/R$id;->functionbar_layout_window:I

    invoke-virtual {p0, v0}, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mFunctionBarLayout:Landroid/view/View;

    .line 75
    sget v0, Lcom/nuance/swype/input/R$id;->functionbar:I

    invoke-virtual {p0, v0}, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Lcom/nuance/swype/input/FunctionBarListView;

    iput-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mFunctionBarListView:Lcom/nuance/swype/input/FunctionBarListView;

    .line 77
    :cond_7
    return-void
.end method

.method public isRightPhraseArrowShowable()Z
    .locals 1

    .prologue
    .line 202
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseButtonRight:Landroid/view/View;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseButtonRight:Landroid/view/View;

    invoke-virtual {v0}, Landroid/view/View;->isShown()Z

    move-result v0

    if-eqz v0, :cond_0

    const/4 v0, 0x1

    :goto_0
    return v0

    :cond_0
    const/4 v0, 0x0

    goto :goto_0
.end method

.method public isRightSpellPrefixSuffixArrowShowable()Z
    .locals 1

    .prologue
    .line 206
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixButtonRight:Landroid/view/View;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixButtonRight:Landroid/view/View;

    invoke-virtual {v0}, Landroid/view/View;->isShown()Z

    move-result v0

    if-eqz v0, :cond_0

    const/4 v0, 0x1

    :goto_0
    return v0

    :cond_0
    const/4 v0, 0x0

    goto :goto_0
.end method

.method public onTouch(Landroid/view/View;Landroid/view/MotionEvent;)Z
    .locals 1
    .param p1, "v"    # Landroid/view/View;
    .param p2, "event"    # Landroid/view/MotionEvent;
    .annotation build Landroid/annotation/SuppressLint;
        value = {
            "ClickableViewAccessibility"
        }
    .end annotation

    .prologue
    .line 168
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getAction()I

    move-result v0

    if-nez v0, :cond_0

    .line 169
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixButtonRight:Landroid/view/View;

    if-ne p1, v0, :cond_1

    .line 170
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixCandidates:Lcom/nuance/swype/input/chinese/SpellListView;

    invoke-virtual {v0}, Lcom/nuance/swype/input/chinese/SpellListView;->scrollNext()V

    .line 180
    :cond_0
    :goto_0
    const/4 v0, 0x0

    return v0

    .line 171
    :cond_1
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixButtonLeft:Landroid/view/View;

    if-ne p1, v0, :cond_2

    .line 172
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixCandidates:Lcom/nuance/swype/input/chinese/SpellListView;

    invoke-virtual {v0}, Lcom/nuance/swype/input/chinese/SpellListView;->scrollPrev()V

    goto :goto_0

    .line 173
    :cond_2
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseButtonRight:Landroid/view/View;

    if-ne p1, v0, :cond_3

    .line 174
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mCJKCandidates:Lcom/nuance/swype/input/chinese/CJKCandidatesListView;

    invoke-virtual {v0}, Lcom/nuance/swype/input/chinese/CJKCandidatesListView;->scrollNext()V

    goto :goto_0

    .line 175
    :cond_3
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseButtonLeft:Landroid/view/View;

    if-ne p1, v0, :cond_0

    .line 176
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mCJKCandidates:Lcom/nuance/swype/input/chinese/CJKCandidatesListView;

    invoke-virtual {v0}, Lcom/nuance/swype/input/chinese/CJKCandidatesListView;->scrollPrev()V

    goto :goto_0
.end method

.method public setVisibility(I)V
    .locals 0
    .param p1, "visibility"    # I

    .prologue
    .line 211
    invoke-super {p0, p1}, Lcom/nuance/swype/input/chinese/CJKWordListViewContainer;->setVisibility(I)V

    .line 212
    return-void
.end method

.method public showFSHandwritingSpellList()V
    .locals 2

    .prologue
    const/4 v1, 0x0

    .line 118
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixLayout:Landroid/view/View;

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 119
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixCandidates:Lcom/nuance/swype/input/chinese/SpellListView;

    invoke-virtual {v0, v1}, Lcom/nuance/swype/input/chinese/SpellListView;->setVisibility(I)V

    .line 120
    invoke-virtual {p0, v1}, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->setVisibility(I)V

    .line 121
    return-void
.end method

.method public showFunctionBarListView()V
    .locals 2

    .prologue
    const/4 v1, 0x0

    .line 134
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mFunctionBarLayout:Landroid/view/View;

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 135
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mFunctionBarListView:Lcom/nuance/swype/input/FunctionBarListView;

    invoke-virtual {v0, v1}, Lcom/nuance/swype/input/FunctionBarListView;->setVisibility(I)V

    .line 136
    invoke-virtual {p0, v1}, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->setVisibility(I)V

    .line 137
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mFunctionBarListView:Lcom/nuance/swype/input/FunctionBarListView;

    invoke-virtual {v0}, Lcom/nuance/swype/input/FunctionBarListView;->requestLayout()V

    .line 138
    return-void
.end method

.method public showLeftArrow(Z)V
    .locals 0
    .param p1, "showLeftArrow"    # Z

    .prologue
    .line 215
    iput-boolean p1, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mShowLeftArrow:Z

    .line 216
    invoke-virtual {p0}, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->updateScrollArrowVisibility()V

    .line 217
    return-void
.end method

.method public showPhraseListView()V
    .locals 2

    .prologue
    const/4 v1, 0x0

    .line 112
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseLayout:Landroid/view/View;

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 113
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mCJKCandidates:Lcom/nuance/swype/input/chinese/CJKCandidatesListView;

    invoke-virtual {v0, v1}, Lcom/nuance/swype/input/chinese/CJKCandidatesListView;->setVisibility(I)V

    .line 114
    invoke-virtual {p0, v1}, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->setVisibility(I)V

    .line 115
    return-void
.end method

.method public updateScrollArrowVisibility()V
    .locals 4

    .prologue
    const/16 v1, 0x8

    .line 146
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixLayout:Landroid/view/View;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixCandidates:Lcom/nuance/swype/input/chinese/SpellListView;

    if-eqz v0, :cond_0

    .line 147
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixCandidates:Lcom/nuance/swype/input/chinese/SpellListView;

    invoke-virtual {v0}, Lcom/nuance/swype/input/chinese/SpellListView;->getListSize()I

    move-result v0

    if-lez v0, :cond_2

    .line 148
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixCandidates:Lcom/nuance/swype/input/chinese/SpellListView;

    const/4 v1, 0x0

    iget-object v2, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixButtonLeft:Landroid/view/View;

    iget-object v3, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixButtonRight:Landroid/view/View;

    invoke-static {v0, v1, v2, v3}, Lcom/nuance/swype/input/chinese/SpellPhraseViewContainer;->updateScrollArrowVisibility(Lcom/nuance/swype/input/chinese/CJKCandidatesListView;ZLandroid/view/View;Landroid/view/View;)V

    .line 158
    :cond_0
    :goto_0
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mCJKCandidates:Lcom/nuance/swype/input/chinese/CJKCandidatesListView;

    if-eqz v0, :cond_1

    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mCJKCandidates:Lcom/nuance/swype/input/chinese/CJKCandidatesListView;

    invoke-virtual {v0}, Lcom/nuance/swype/input/chinese/CJKCandidatesListView;->isShowingSwypeTooltip()Z

    move-result v0

    if-nez v0, :cond_1

    .line 159
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mCJKCandidates:Lcom/nuance/swype/input/chinese/CJKCandidatesListView;

    iget-boolean v1, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mShowLeftArrow:Z

    iget-object v2, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseButtonLeft:Landroid/view/View;

    iget-object v3, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->mPhraseButtonRight:Landroid/view/View;

    invoke-static {v0, v1, v2, v3}, Lcom/nuance/swype/input/chinese/SpellPhraseViewContainer;->updateScrollArrowVisibility(Lcom/nuance/swype/input/chinese/CJKCandidatesListView;ZLandroid/view/View;Landroid/view/View;)V

    .line 162
    :cond_1
    invoke-virtual {p0}, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->invalidate()V

    .line 163
    return-void

    .line 150
    :cond_2
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixButtonLeft:Landroid/view/View;

    if-eqz v0, :cond_3

    .line 151
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixButtonLeft:Landroid/view/View;

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 153
    :cond_3
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixButtonRight:Landroid/view/View;

    if-eqz v0, :cond_0

    .line 154
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/SpellPhraseHandWritingViewContainer;->spellPrefixSuffixButtonRight:Landroid/view/View;

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    goto :goto_0
.end method
