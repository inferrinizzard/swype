.class Lcom/nuance/swype/input/chinese/ChineseInputView$6;
.super Lcom/nuance/swype/input/KeyboardViewEx$KeyboardActionAdapter;
.source "ChineseInputView.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/nuance/swype/input/chinese/ChineseInputView;->updateGridViewByPrefix(Lcom/nuance/swype/input/chinese/CJKCandidatesListView;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/nuance/swype/input/chinese/ChineseInputView;

.field final synthetic val$keyboard:Lcom/nuance/swype/input/KeyboardEx;


# direct methods
.method constructor <init>(Lcom/nuance/swype/input/chinese/ChineseInputView;Lcom/nuance/swype/input/KeyboardEx;)V
    .locals 0
    .param p1, "this$0"    # Lcom/nuance/swype/input/chinese/ChineseInputView;

    .prologue
    .line 3782
    iput-object p1, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->this$0:Lcom/nuance/swype/input/chinese/ChineseInputView;

    iput-object p2, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->val$keyboard:Lcom/nuance/swype/input/KeyboardEx;

    invoke-direct {p0}, Lcom/nuance/swype/input/KeyboardViewEx$KeyboardActionAdapter;-><init>()V

    return-void
.end method


# virtual methods
.method public onRelease(I)V
    .locals 1
    .param p1, "primaryCode"    # I

    .prologue
    .line 3813
    iget-object v0, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->val$keyboard:Lcom/nuance/swype/input/KeyboardEx;

    invoke-virtual {v0}, Lcom/nuance/swype/input/KeyboardEx;->clearStickyKeys()V

    .line 3814
    return-void
.end method

.method public onText(Ljava/lang/CharSequence;J)V
    .locals 4
    .param p1, "text"    # Ljava/lang/CharSequence;
    .param p2, "eventTime"    # J

    .prologue
    const/4 v3, 0x1

    .line 3787
    iget-object v1, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->this$0:Lcom/nuance/swype/input/chinese/ChineseInputView;

    invoke-static {v1}, Lcom/nuance/swype/input/chinese/ChineseInputView;->access$4200(Lcom/nuance/swype/input/chinese/ChineseInputView;)Z

    move-result v1

    if-nez v1, :cond_0

    if-nez p1, :cond_1

    .line 3809
    :cond_0
    :goto_0
    return-void

    .line 3791
    :cond_1
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_1
    iget-object v1, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->this$0:Lcom/nuance/swype/input/chinese/ChineseInputView;

    invoke-static {v1}, Lcom/nuance/swype/input/chinese/ChineseInputView;->access$4400(Lcom/nuance/swype/input/chinese/ChineseInputView;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    if-ge v0, v1, :cond_2

    .line 3792
    iget-object v1, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->this$0:Lcom/nuance/swype/input/chinese/ChineseInputView;

    invoke-static {v1}, Lcom/nuance/swype/input/chinese/ChineseInputView;->access$4400(Lcom/nuance/swype/input/chinese/ChineseInputView;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v1, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Ljava/lang/CharSequence;

    invoke-interface {v1}, Ljava/lang/CharSequence;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-interface {p1}, Ljava/lang/CharSequence;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_3

    .line 3793
    iget-object v1, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->this$0:Lcom/nuance/swype/input/chinese/ChineseInputView;

    iget-object v2, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->this$0:Lcom/nuance/swype/input/chinese/ChineseInputView;

    iget-object v2, v2, Lcom/nuance/swype/input/chinese/ChineseInputView;->candidatesListViewCJK:Lcom/nuance/swype/input/chinese/CJKCandidatesListView;

    invoke-virtual {v1, v0, p1, v2}, Lcom/nuance/swype/input/chinese/ChineseInputView;->selectWord(ILjava/lang/CharSequence;Landroid/view/View;)V

    .line 3798
    :cond_2
    iget-object v1, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->this$0:Lcom/nuance/swype/input/chinese/ChineseInputView;

    invoke-static {v1}, Lcom/nuance/swype/input/chinese/ChineseInputView;->access$3300(Lcom/nuance/swype/input/chinese/ChineseInputView;)V

    .line 3799
    iget-object v1, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->this$0:Lcom/nuance/swype/input/chinese/ChineseInputView;

    invoke-static {v1}, Lcom/nuance/swype/input/chinese/ChineseInputView;->access$4500(Lcom/nuance/swype/input/chinese/ChineseInputView;)Z

    move-result v1

    if-eqz v1, :cond_4

    iget-object v1, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->this$0:Lcom/nuance/swype/input/chinese/ChineseInputView;

    .line 3800
    invoke-static {v1}, Lcom/nuance/swype/input/chinese/ChineseInputView;->access$4800(Lcom/nuance/swype/input/chinese/ChineseInputView;)Lcom/nuance/swype/input/KeyboardSwitcher;

    move-result-object v1

    check-cast v1, Lcom/nuance/swype/input/chinese/ChineseKeyboardSwitcher;

    invoke-virtual {v1}, Lcom/nuance/swype/input/chinese/ChineseKeyboardSwitcher;->isMixedInput()Z

    move-result v1

    if-eqz v1, :cond_4

    .line 3801
    iget-object v1, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->this$0:Lcom/nuance/swype/input/chinese/ChineseInputView;

    iget-object v2, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->this$0:Lcom/nuance/swype/input/chinese/ChineseInputView;

    iget-object v2, v2, Lcom/nuance/swype/input/chinese/ChineseInputView;->candidatesListViewCJK:Lcom/nuance/swype/input/chinese/CJKCandidatesListView;

    invoke-virtual {v1, v2}, Lcom/nuance/swype/input/chinese/ChineseInputView;->nextBtnPressed(Lcom/nuance/swype/input/chinese/CJKCandidatesListView;)V

    goto :goto_0

    .line 3791
    :cond_3
    add-int/lit8 v0, v0, 0x1

    goto :goto_1

    .line 3802
    :cond_4
    iget-object v1, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->this$0:Lcom/nuance/swype/input/chinese/ChineseInputView;

    invoke-static {v1}, Lcom/nuance/swype/input/chinese/ChineseInputView;->access$4500(Lcom/nuance/swype/input/chinese/ChineseInputView;)Z

    move-result v1

    if-eqz v1, :cond_5

    iget-object v1, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->this$0:Lcom/nuance/swype/input/chinese/ChineseInputView;

    invoke-static {v1}, Lcom/nuance/swype/input/chinese/ChineseInputView;->access$4900(Lcom/nuance/swype/input/chinese/ChineseInputView;)Lcom/nuance/swype/input/KeyboardSwitcher;

    move-result-object v1

    invoke-virtual {v1}, Lcom/nuance/swype/input/KeyboardSwitcher;->isKeypadInput()Z

    move-result v1

    if-eqz v1, :cond_5

    .line 3803
    iget-object v1, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->this$0:Lcom/nuance/swype/input/chinese/ChineseInputView;

    invoke-virtual {v1, v3}, Lcom/nuance/swype/input/chinese/ChineseInputView;->setCandidatesViewShown(Z)V

    .line 3804
    iget-object v1, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->this$0:Lcom/nuance/swype/input/chinese/ChineseInputView;

    invoke-static {v1}, Lcom/nuance/swype/input/chinese/ChineseInputView;->access$3400(Lcom/nuance/swype/input/chinese/ChineseInputView;)Lcom/nuance/swype/input/chinese/SpellPhraseViewContainer;

    move-result-object v1

    invoke-virtual {v1}, Lcom/nuance/swype/input/chinese/SpellPhraseViewContainer;->showPhraseListView()V

    .line 3805
    iget-object v1, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->this$0:Lcom/nuance/swype/input/chinese/ChineseInputView;

    invoke-static {v1}, Lcom/nuance/swype/input/chinese/ChineseInputView;->access$3700(Lcom/nuance/swype/input/chinese/ChineseInputView;)V

    goto :goto_0

    .line 3807
    :cond_5
    iget-object v1, p0, Lcom/nuance/swype/input/chinese/ChineseInputView$6;->this$0:Lcom/nuance/swype/input/chinese/ChineseInputView;

    invoke-virtual {v1, v3}, Lcom/nuance/swype/input/chinese/ChineseInputView;->setCandidatesViewShown(Z)V

    goto/16 :goto_0
.end method
