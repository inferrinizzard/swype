.class Lcom/nuance/swype/input/RecaptureHandler$MsgHandler;
.super Lcom/nuance/swype/util/SafeMessageHandler;
.source "RecaptureHandler.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/nuance/swype/input/RecaptureHandler;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "MsgHandler"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/nuance/swype/util/SafeMessageHandler",
        "<",
        "Lcom/nuance/swype/input/RecaptureHandler;",
        ">;"
    }
.end annotation


# direct methods
.method public constructor <init>(Lcom/nuance/swype/input/RecaptureHandler;)V
    .locals 0
    .param p1, "parent"    # Lcom/nuance/swype/input/RecaptureHandler;

    .prologue
    .line 1184
    invoke-direct {p0, p1}, Lcom/nuance/swype/util/SafeMessageHandler;-><init>(Ljava/lang/Object;)V

    .line 1185
    return-void
.end method


# virtual methods
.method protected handleMessage(Landroid/os/Message;Lcom/nuance/swype/input/RecaptureHandler;)V
    .locals 6
    .param p1, "msg"    # Landroid/os/Message;
    .param p2, "parent"    # Lcom/nuance/swype/input/RecaptureHandler;

    .prologue
    const/4 v2, 0x1

    const/4 v3, 0x0

    .line 1189
    iget v1, p1, Landroid/os/Message;->what:I

    packed-switch v1, :pswitch_data_0

    .line 1206
    :cond_0
    :goto_0
    return-void

    .line 1191
    :pswitch_0
    invoke-static {p2}, Lcom/nuance/swype/input/RecaptureHandler;->access$000(Lcom/nuance/swype/input/RecaptureHandler;)Lcom/nuance/swype/input/IME;

    move-result-object v1

    invoke-virtual {v1}, Lcom/nuance/swype/input/IME;->getCurrentInputConnection()Landroid/view/inputmethod/InputConnection;

    move-result-object v0

    .line 1192
    .local v0, "ic":Landroid/view/inputmethod/InputConnection;
    if-eqz v0, :cond_0

    .line 1193
    iget v1, p1, Landroid/os/Message;->arg1:I

    iget v2, p1, Landroid/os/Message;->arg1:I

    invoke-interface {v0, v1, v2}, Landroid/view/inputmethod/InputConnection;->setSelection(II)Z

    .line 1194
    invoke-static {p2}, Lcom/nuance/swype/input/RecaptureHandler;->access$100(Lcom/nuance/swype/input/RecaptureHandler;)Lcom/nuance/swype/input/InputView;

    move-result-object v1

    invoke-virtual {v1}, Lcom/nuance/swype/input/InputView;->hasPendingSuggestionsUpdate()Z

    move-result v1

    if-eqz v1, :cond_1

    .line 1195
    invoke-static {p2}, Lcom/nuance/swype/input/RecaptureHandler;->access$100(Lcom/nuance/swype/input/RecaptureHandler;)Lcom/nuance/swype/input/InputView;

    move-result-object v1

    invoke-virtual {v1}, Lcom/nuance/swype/input/InputView;->clearPendingSuggestionsUpdate()V

    .line 1198
    :cond_1
    invoke-static {p2, v3, v3}, Lcom/nuance/swype/input/RecaptureHandler;->access$200(Lcom/nuance/swype/input/RecaptureHandler;ZZ)Z

    goto :goto_0

    .line 1202
    .end local v0    # "ic":Landroid/view/inputmethod/InputConnection;
    :pswitch_1
    sget-object v1, Lcom/nuance/swype/input/RecaptureHandler;->log:Lcom/nuance/swype/util/LogManager$Log;

    new-array v4, v2, [Ljava/lang/Object;

    const-string/jumbo v5, "cursor MSG_RECAPTURE handleMessage"

    aput-object v5, v4, v3

    invoke-interface {v1, v4}, Lcom/nuance/swype/util/LogManager$Log;->d([Ljava/lang/Object;)V

    .line 1203
    iget v1, p1, Landroid/os/Message;->arg1:I

    if-ne v1, v2, :cond_2

    move v1, v2

    :goto_1
    iget v4, p1, Landroid/os/Message;->arg2:I

    if-ne v4, v2, :cond_3

    :goto_2
    invoke-static {p2, v1, v2}, Lcom/nuance/swype/input/RecaptureHandler;->access$200(Lcom/nuance/swype/input/RecaptureHandler;ZZ)Z

    goto :goto_0

    :cond_2
    move v1, v3

    goto :goto_1

    :cond_3
    move v2, v3

    goto :goto_2

    .line 1189
    nop

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method

.method protected bridge synthetic handleMessage(Landroid/os/Message;Ljava/lang/Object;)V
    .locals 0

    .prologue
    .line 1181
    check-cast p2, Lcom/nuance/swype/input/RecaptureHandler;

    invoke-virtual {p0, p1, p2}, Lcom/nuance/swype/input/RecaptureHandler$MsgHandler;->handleMessage(Landroid/os/Message;Lcom/nuance/swype/input/RecaptureHandler;)V

    return-void
.end method
