.class public Lcom/nuance/swype/input/keyboard/WordRecaptureWrapper;
.super Ljava/lang/Object;
.source "WordRecaptureWrapper.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    .prologue
    .line 10
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public hasPendingRecaptureMessage(Lcom/nuance/swype/input/RecaptureHandler;)Z
    .locals 1
    .param p1, "recaptureHandler"    # Lcom/nuance/swype/input/RecaptureHandler;

    .prologue
    .line 39
    if-eqz p1, :cond_0

    .line 40
    invoke-virtual {p1}, Lcom/nuance/swype/input/RecaptureHandler;->hasPendingRecaptureMessage()Z

    .line 43
    :cond_0
    const/4 v0, 0x0

    return v0
.end method

.method public onCharKey(CLcom/nuance/swype/input/RecaptureHandler;)Z
    .locals 1
    .param p1, "ch"    # C
    .param p2, "recaptureHandler"    # Lcom/nuance/swype/input/RecaptureHandler;

    .prologue
    .line 20
    if-eqz p2, :cond_0

    .line 21
    invoke-virtual {p2, p1}, Lcom/nuance/swype/input/RecaptureHandler;->onCharKey(C)Z

    move-result v0

    .line 23
    :goto_0
    return v0

    :cond_0
    const/4 v0, 0x0

    goto :goto_0
.end method

.method public onText(Ljava/lang/String;Lcom/nuance/swype/input/RecaptureHandler;)V
    .locals 0
    .param p1, "text"    # Ljava/lang/String;
    .param p2, "recaptureHandler"    # Lcom/nuance/swype/input/RecaptureHandler;

    .prologue
    .line 27
    if-eqz p2, :cond_0

    .line 28
    invoke-virtual {p2, p1}, Lcom/nuance/swype/input/RecaptureHandler;->onText(Ljava/lang/CharSequence;)V

    .line 30
    :cond_0
    return-void
.end method

.method public removePendingRecaptureMessage(Lcom/nuance/swype/input/RecaptureHandler;)V
    .locals 0
    .param p1, "recaptureHandler"    # Lcom/nuance/swype/input/RecaptureHandler;

    .prologue
    .line 33
    if-eqz p1, :cond_0

    .line 34
    invoke-virtual {p1}, Lcom/nuance/swype/input/RecaptureHandler;->removePendingRecaptureMessage()V

    .line 36
    :cond_0
    return-void
.end method

.method public reselect(ILcom/nuance/swype/input/RecaptureHandler;)Z
    .locals 1
    .param p1, "keyCode"    # I
    .param p2, "recaptureHandler"    # Lcom/nuance/swype/input/RecaptureHandler;

    .prologue
    const/4 v0, 0x0

    .line 13
    if-eqz p2, :cond_0

    .line 14
    invoke-virtual {p2, p1, v0}, Lcom/nuance/swype/input/RecaptureHandler;->onKey(II)Z

    move-result v0

    .line 16
    :cond_0
    return v0
.end method
