.class Lcom/nuance/swype/input/japanese/JapaneseInputView$1;
.super Ljava/lang/Object;
.source "JapaneseInputView.java"

# interfaces
.implements Landroid/os/Handler$Callback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/nuance/swype/input/japanese/JapaneseInputView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;


# direct methods
.method constructor <init>(Lcom/nuance/swype/input/japanese/JapaneseInputView;)V
    .locals 0
    .param p1, "this$0"    # Lcom/nuance/swype/input/japanese/JapaneseInputView;

    .prologue
    .line 331
    iput-object p1, p0, Lcom/nuance/swype/input/japanese/JapaneseInputView$1;->this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public handleMessage(Landroid/os/Message;)Z
    .locals 5
    .param p1, "msg"    # Landroid/os/Message;
    .annotation build Landroid/annotation/SuppressLint;
        value = {
            "PrivateResource"
        }
    .end annotation

    .prologue
    const/4 v4, 0x1

    const/4 v1, 0x0

    .line 336
    iget v0, p1, Landroid/os/Message;->what:I

    sparse-switch v0, :sswitch_data_0

    .line 375
    :cond_0
    :goto_0
    return v4

    .line 338
    :sswitch_0
    iget-object v0, p0, Lcom/nuance/swype/input/japanese/JapaneseInputView$1;->this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;

    .line 339
    invoke-virtual {v0}, Lcom/nuance/swype/input/japanese/JapaneseInputView;->getContext()Landroid/content/Context;

    move-result-object v0

    iget-object v1, p0, Lcom/nuance/swype/input/japanese/JapaneseInputView$1;->this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;

    .line 340
    invoke-virtual {v1}, Lcom/nuance/swype/input/japanese/JapaneseInputView;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    sget v2, Lcom/nuance/swype/input/R$string;->multitap_toggle_tip:I

    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v1

    iget-object v2, p0, Lcom/nuance/swype/input/japanese/JapaneseInputView$1;->this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;

    .line 342
    invoke-virtual {v2}, Lcom/nuance/swype/input/japanese/JapaneseInputView;->getHeight()I

    move-result v2

    iget-object v3, p0, Lcom/nuance/swype/input/japanese/JapaneseInputView$1;->this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;

    iget-object v3, v3, Lcom/nuance/swype/input/japanese/JapaneseInputView;->candidatesListViewCJK:Lcom/nuance/swype/input/chinese/CJKCandidatesListView;

    invoke-virtual {v3}, Lcom/nuance/swype/input/chinese/CJKCandidatesListView;->getHeight()I

    move-result v3

    add-int/2addr v2, v3

    .line 338
    invoke-static {v0, v1, v4, v2}, Lcom/nuance/swype/input/QuickToast;->show(Landroid/content/Context;Ljava/lang/CharSequence;II)V

    goto :goto_0

    .line 346
    :sswitch_1
    iget-object v0, p0, Lcom/nuance/swype/input/japanese/JapaneseInputView$1;->this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;

    invoke-static {v0, v1}, Lcom/nuance/swype/input/japanese/JapaneseInputView;->access$002(Lcom/nuance/swype/input/japanese/JapaneseInputView;Z)Z

    goto :goto_0

    .line 350
    :sswitch_2
    iget-object v0, p0, Lcom/nuance/swype/input/japanese/JapaneseInputView$1;->this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;

    iget-object v0, v0, Lcom/nuance/swype/input/japanese/JapaneseInputView;->mSpeechWrapper:Lcom/nuance/swype/input/SpeechWrapper;

    if-eqz v0, :cond_1

    iget-object v0, p0, Lcom/nuance/swype/input/japanese/JapaneseInputView$1;->this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;

    iget-object v0, v0, Lcom/nuance/swype/input/japanese/JapaneseInputView;->mSpeechWrapper:Lcom/nuance/swype/input/SpeechWrapper;

    invoke-virtual {v0}, Lcom/nuance/swype/input/SpeechWrapper;->isResumable()Z

    move-result v0

    if-eqz v0, :cond_1

    .line 351
    iget-object v0, p0, Lcom/nuance/swype/input/japanese/JapaneseInputView$1;->this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;

    invoke-virtual {v0}, Lcom/nuance/swype/input/japanese/JapaneseInputView;->flushCurrentActiveWord()V

    .line 353
    :cond_1
    iget-object v0, p0, Lcom/nuance/swype/input/japanese/JapaneseInputView$1;->this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;

    invoke-static {v0}, Lcom/nuance/swype/input/japanese/JapaneseInputView;->access$100(Lcom/nuance/swype/input/japanese/JapaneseInputView;)V

    .line 354
    iget-object v0, p0, Lcom/nuance/swype/input/japanese/JapaneseInputView$1;->this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;

    invoke-static {v0}, Lcom/nuance/swype/input/japanese/JapaneseInputView;->access$200(Lcom/nuance/swype/input/japanese/JapaneseInputView;)V

    goto :goto_0

    .line 357
    :sswitch_3
    iget-object v0, p0, Lcom/nuance/swype/input/japanese/JapaneseInputView$1;->this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;

    invoke-static {v0}, Lcom/nuance/swype/input/japanese/JapaneseInputView;->access$300(Lcom/nuance/swype/input/japanese/JapaneseInputView;)V

    .line 358
    iget-object v0, p0, Lcom/nuance/swype/input/japanese/JapaneseInputView$1;->this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;

    invoke-static {v0}, Lcom/nuance/swype/input/japanese/JapaneseInputView;->access$400(Lcom/nuance/swype/input/japanese/JapaneseInputView;)Z

    move-result v0

    if-nez v0, :cond_0

    .line 359
    iget-object v0, p0, Lcom/nuance/swype/input/japanese/JapaneseInputView$1;->this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;

    invoke-static {v0}, Lcom/nuance/swype/input/japanese/JapaneseInputView;->access$500(Lcom/nuance/swype/input/japanese/JapaneseInputView;)Lcom/nuance/swype/input/KeyboardSwitcher;

    move-result-object v0

    sget-object v1, Lcom/nuance/input/swypecorelib/Shift$ShiftState;->OFF:Lcom/nuance/input/swypecorelib/Shift$ShiftState;

    invoke-virtual {v0, v1}, Lcom/nuance/swype/input/KeyboardSwitcher;->setShiftState(Lcom/nuance/input/swypecorelib/Shift$ShiftState;)Z

    .line 360
    iget-object v0, p0, Lcom/nuance/swype/input/japanese/JapaneseInputView$1;->this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;

    invoke-static {v0}, Lcom/nuance/swype/input/japanese/JapaneseInputView;->access$600(Lcom/nuance/swype/input/japanese/JapaneseInputView;)V

    goto :goto_0

    .line 364
    :sswitch_4
    iget-object v0, p0, Lcom/nuance/swype/input/japanese/JapaneseInputView$1;->this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;

    invoke-virtual {v0, v1, v1}, Lcom/nuance/swype/input/japanese/JapaneseInputView;->updateSegemetationPopup(II)V

    goto :goto_0

    .line 367
    :sswitch_5
    iget-object v0, p0, Lcom/nuance/swype/input/japanese/JapaneseInputView$1;->this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;

    invoke-virtual {v0}, Lcom/nuance/swype/input/japanese/JapaneseInputView;->showSegmentationPopup()V

    goto :goto_0

    .line 370
    :sswitch_6
    iget-object v0, p0, Lcom/nuance/swype/input/japanese/JapaneseInputView$1;->this$0:Lcom/nuance/swype/input/japanese/JapaneseInputView;

    invoke-static {v0}, Lcom/nuance/swype/input/japanese/JapaneseInputView;->access$700(Lcom/nuance/swype/input/japanese/JapaneseInputView;)Lcom/nuance/swype/input/InputView$WclPrompt;

    move-result-object v0

    const-string/jumbo v1, "CJK"

    invoke-virtual {v0, v1}, Lcom/nuance/swype/input/InputView$WclPrompt;->showMessage(Ljava/lang/String;)V

    goto/16 :goto_0

    .line 336
    nop

    :sswitch_data_0
    .sparse-switch
        0x5 -> :sswitch_0
        0xb -> :sswitch_2
        0xf -> :sswitch_3
        0x15 -> :sswitch_1
        0x16 -> :sswitch_4
        0x17 -> :sswitch_5
        0x7d -> :sswitch_6
    .end sparse-switch
.end method
