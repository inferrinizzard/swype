.class public Lcom/nuance/swype/input/accessibility/AccessibilityNotification;
.super Ljava/lang/Object;
.source "AccessibilityNotification.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;
    }
.end annotation


# static fields
.field private static instance:Lcom/nuance/swype/input/accessibility/AccessibilityNotification;

.field private static mToast:Landroid/widget/Toast;

.field private static mToast_DelayHandler:Landroid/os/Handler;

.field private static mToast_DelayRunnable:Ljava/lang/Runnable;

.field private static mToast_DelayTime:I


# direct methods
.method public constructor <init>()V
    .locals 0

    .prologue
    .line 25
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method private static cleanToastDelayHandler()V
    .locals 2

    .prologue
    .line 277
    sget-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->mToast_DelayHandler:Landroid/os/Handler;

    if-eqz v0, :cond_0

    sget-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->mToast_DelayRunnable:Ljava/lang/Runnable;

    if-eqz v0, :cond_0

    .line 278
    sget-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->mToast_DelayHandler:Landroid/os/Handler;

    sget-object v1, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->mToast_DelayRunnable:Ljava/lang/Runnable;

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeCallbacks(Ljava/lang/Runnable;)V

    .line 279
    const/4 v0, 0x0

    sput-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->mToast_DelayHandler:Landroid/os/Handler;

    .line 281
    :cond_0
    return-void
.end method

.method public static getInstance()Lcom/nuance/swype/input/accessibility/AccessibilityNotification;
    .locals 1

    .prologue
    .line 85
    sget-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->instance:Lcom/nuance/swype/input/accessibility/AccessibilityNotification;

    if-nez v0, :cond_0

    .line 86
    new-instance v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;

    invoke-direct {v0}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;-><init>()V

    sput-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->instance:Lcom/nuance/swype/input/accessibility/AccessibilityNotification;

    .line 88
    :cond_0
    sget-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->instance:Lcom/nuance/swype/input/accessibility/AccessibilityNotification;

    return-object v0
.end method

.method private isInVoiceUI(Landroid/content/Context;)Z
    .locals 4
    .param p1, "context"    # Landroid/content/Context;

    .prologue
    const/4 v2, 0x1

    .line 188
    if-eqz p1, :cond_0

    .line 189
    invoke-static {p1}, Lcom/nuance/swype/input/IMEApplication;->from(Landroid/content/Context;)Lcom/nuance/swype/input/IMEApplication;

    move-result-object v0

    .line 190
    .local v0, "app":Lcom/nuance/swype/input/IMEApplication;
    if-eqz v0, :cond_0

    .line 191
    invoke-virtual {v0}, Lcom/nuance/swype/input/IMEApplication;->getSpeechWrapper()Lcom/nuance/swype/input/SpeechWrapper;

    move-result-object v1

    .line 192
    .local v1, "sw":Lcom/nuance/swype/input/SpeechWrapper;
    if-eqz v1, :cond_1

    invoke-virtual {v1}, Lcom/nuance/swype/input/SpeechWrapper;->isResumable()Z

    move-result v3

    if-eqz v3, :cond_1

    .line 197
    .end local v0    # "app":Lcom/nuance/swype/input/IMEApplication;
    .end local v1    # "sw":Lcom/nuance/swype/input/SpeechWrapper;
    :cond_0
    :goto_0
    return v2

    .line 192
    .restart local v0    # "app":Lcom/nuance/swype/input/IMEApplication;
    .restart local v1    # "sw":Lcom/nuance/swype/input/SpeechWrapper;
    :cond_1
    const/4 v2, 0x0

    goto :goto_0
.end method

.method public static speakByToast(Landroid/content/Context;Ljava/lang/CharSequence;)V
    .locals 6
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "text"    # Ljava/lang/CharSequence;
    .annotation build Landroid/annotation/SuppressLint;
        value = {
            "InflateParams"
        }
    .end annotation

    .prologue
    .line 253
    if-nez p0, :cond_0

    .line 274
    :goto_0
    return-void

    .line 257
    :cond_0
    sget-object v3, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->mToast:Landroid/widget/Toast;

    if-nez v3, :cond_1

    .line 258
    new-instance v3, Landroid/widget/Toast;

    invoke-direct {v3, p0}, Landroid/widget/Toast;-><init>(Landroid/content/Context;)V

    sput-object v3, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->mToast:Landroid/widget/Toast;

    .line 259
    invoke-virtual {p0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v3

    sget v4, Lcom/nuance/swype/input/R$integer;->accessibility_notification_toast_delay_time:I

    invoke-virtual {v3, v4}, Landroid/content/res/Resources;->getInteger(I)I

    move-result v3

    sput v3, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->mToast_DelayTime:I

    .line 262
    :cond_1
    invoke-static {p0}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    move-result-object v3

    sget v4, Lcom/nuance/swype/input/R$layout;->toast:I

    const/4 v5, 0x0

    invoke-virtual {v3, v4, v5}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;)Landroid/view/View;

    move-result-object v2

    .line 264
    .local v2, "v":Landroid/view/View;
    sget v3, Lcom/nuance/swype/input/R$id;->message:I

    invoke-virtual {v2, v3}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroid/widget/TextView;

    .line 266
    .local v1, "tv":Landroid/widget/TextView;
    invoke-virtual {p0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v3

    const v4, 0x106000d

    invoke-virtual {v3, v4}, Landroid/content/res/Resources;->getColor(I)I

    move-result v0

    .line 267
    .local v0, "trans":I
    invoke-virtual {v2, v0}, Landroid/view/View;->setBackgroundColor(I)V

    .line 268
    invoke-virtual {v1, v0}, Landroid/widget/TextView;->setTextColor(I)V

    .line 269
    invoke-virtual {v1, p1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 271
    sget-object v3, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->mToast:Landroid/widget/Toast;

    invoke-virtual {v3, v2}, Landroid/widget/Toast;->setView(Landroid/view/View;)V

    .line 272
    sget-object v3, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->mToast:Landroid/widget/Toast;

    const/4 v4, 0x0

    invoke-virtual {v3, v4}, Landroid/widget/Toast;->setDuration(I)V

    .line 273
    sget-object v3, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->mToast:Landroid/widget/Toast;

    invoke-virtual {v3}, Landroid/widget/Toast;->show()V

    goto :goto_0
.end method


# virtual methods
.method public announceNotification(Landroid/content/Context;Ljava/lang/String;Z)V
    .locals 4
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "notice"    # Ljava/lang/String;
    .param p3, "instant"    # Z

    .prologue
    .line 234
    invoke-static {}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->cleanToastDelayHandler()V

    .line 235
    if-eqz p3, :cond_0

    .line 236
    invoke-static {p1, p2}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->speakByToast(Landroid/content/Context;Ljava/lang/CharSequence;)V

    .line 247
    :goto_0
    return-void

    .line 239
    :cond_0
    new-instance v0, Landroid/os/Handler;

    invoke-direct {v0}, Landroid/os/Handler;-><init>()V

    sput-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->mToast_DelayHandler:Landroid/os/Handler;

    .line 240
    new-instance v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$1;

    invoke-direct {v0, p0, p1, p2}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$1;-><init>(Lcom/nuance/swype/input/accessibility/AccessibilityNotification;Landroid/content/Context;Ljava/lang/String;)V

    sput-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->mToast_DelayRunnable:Ljava/lang/Runnable;

    .line 245
    sget-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->mToast_DelayHandler:Landroid/os/Handler;

    sget-object v1, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->mToast_DelayRunnable:Ljava/lang/Runnable;

    sget v2, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->mToast_DelayTime:I

    int-to-long v2, v2

    invoke-virtual {v0, v1, v2, v3}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    goto :goto_0
.end method

.method public notifyKeyboardClose(Landroid/content/Context;)V
    .locals 3
    .param p1, "context"    # Landroid/content/Context;

    .prologue
    .line 159
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    sget-object v2, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_CLOSE:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {v2}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->getResourceId()I

    move-result v2

    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v0

    .line 160
    .local v0, "notice":Ljava/lang/String;
    const/4 v1, 0x1

    invoke-virtual {p0, p1, v0, v1}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->announceNotification(Landroid/content/Context;Ljava/lang/String;Z)V

    .line 161
    return-void
.end method

.method public notifyKeyboardOpen(Landroid/content/Context;ILcom/nuance/swype/input/KeyboardEx$KeyboardLayerType;Lcom/nuance/input/swypecorelib/Shift$ShiftState;Ljava/lang/String;)V
    .locals 4
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "orientation"    # I
    .param p3, "keyboardLayer"    # Lcom/nuance/swype/input/KeyboardEx$KeyboardLayerType;
    .param p4, "shiftState"    # Lcom/nuance/input/swypecorelib/Shift$ShiftState;
    .param p5, "currentLanguage"    # Ljava/lang/String;

    .prologue
    const/4 v3, 0x1

    .line 94
    invoke-direct {p0, p1}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->isInVoiceUI(Landroid/content/Context;)Z

    move-result v1

    if-eqz v1, :cond_0

    .line 156
    :goto_0
    return-void

    .line 99
    :cond_0
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    .line 100
    .local v0, "notice":Ljava/lang/StringBuilder;
    if-eqz p5, :cond_1

    .line 101
    invoke-virtual {v0, p5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 102
    const-string/jumbo v1, " "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 104
    :cond_1
    if-ne p2, v3, :cond_2

    .line 105
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    sget-object v2, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_PORTRAIT:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {v2}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->getResourceId()I

    move-result v2

    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 110
    :goto_1
    sget-object v1, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$2;->$SwitchMap$com$nuance$swype$input$KeyboardEx$KeyboardLayerType:[I

    invoke-virtual {p3}, Lcom/nuance/swype/input/KeyboardEx$KeyboardLayerType;->ordinal()I

    move-result v2

    aget v1, v1, v2

    packed-switch v1, :pswitch_data_0

    .line 155
    :goto_2
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {p0, p1, v1, v3}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->announceNotification(Landroid/content/Context;Ljava/lang/String;Z)V

    goto :goto_0

    .line 107
    :cond_2
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    sget-object v2, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_LANDSCAPE:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {v2}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->getResourceId()I

    move-result v2

    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    goto :goto_1

    .line 112
    :pswitch_0
    const-string/jumbo v1, " "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 113
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    sget-object v2, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_LAYER_TEXT:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {v2}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->getResourceId()I

    move-result v2

    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 114
    const-string/jumbo v1, " "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 115
    sget-object v1, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$2;->$SwitchMap$com$nuance$input$swypecorelib$Shift$ShiftState:[I

    invoke-virtual {p4}, Lcom/nuance/input/swypecorelib/Shift$ShiftState;->ordinal()I

    move-result v2

    aget v1, v1, v2

    packed-switch v1, :pswitch_data_1

    goto :goto_2

    .line 117
    :pswitch_1
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    sget-object v2, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_TEXT_LOWER_CASE:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {v2}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->getResourceId()I

    move-result v2

    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    goto :goto_2

    .line 120
    :pswitch_2
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    sget-object v2, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_TEXT_CAP:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {v2}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->getResourceId()I

    move-result v2

    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    goto :goto_2

    .line 123
    :pswitch_3
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    sget-object v2, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_TEXT_ALL_CAP:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {v2}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->getResourceId()I

    move-result v2

    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    goto :goto_2

    .line 130
    :pswitch_4
    const-string/jumbo v1, " "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 131
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    sget-object v2, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_LAYER_SYMBOLS:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {v2}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->getResourceId()I

    move-result v2

    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 132
    const-string/jumbo v1, " "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 133
    sget-object v1, Lcom/nuance/input/swypecorelib/Shift$ShiftState;->OFF:Lcom/nuance/input/swypecorelib/Shift$ShiftState;

    if-ne p4, v1, :cond_3

    .line 134
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    sget-object v2, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_SYMBOL1:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {v2}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->getResourceId()I

    move-result v2

    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    goto/16 :goto_2

    .line 136
    :cond_3
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    sget-object v2, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_SYMBOL2:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {v2}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->getResourceId()I

    move-result v2

    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    goto/16 :goto_2

    .line 140
    :pswitch_5
    const-string/jumbo v1, " "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 141
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    sget-object v2, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_LAYER_EDIT:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {v2}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->getResourceId()I

    move-result v2

    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    goto/16 :goto_2

    .line 144
    :pswitch_6
    const-string/jumbo v1, " "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 145
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    sget-object v2, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_LAYER_NUM:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {v2}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->getResourceId()I

    move-result v2

    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    goto/16 :goto_2

    .line 148
    :pswitch_7
    const-string/jumbo v1, " "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 149
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    sget-object v2, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_LAYER_PHONE:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {v2}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->getResourceId()I

    move-result v2

    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    goto/16 :goto_2

    .line 110
    nop

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_4
        :pswitch_5
        :pswitch_6
        :pswitch_7
    .end packed-switch

    .line 115
    :pswitch_data_1
    .packed-switch 0x1
        :pswitch_1
        :pswitch_2
        :pswitch_3
    .end packed-switch
.end method

.method public speak(Landroid/content/Context;)V
    .locals 3
    .param p1, "context"    # Landroid/content/Context;
    .annotation build Landroid/annotation/SuppressLint;
        value = {
            "NewApi"
        }
    .end annotation

    .prologue
    .line 63
    invoke-static {p1}, Lcom/nuance/android/compat/AccessibilityEventCompat;->obtainTextEvent(Landroid/content/Context;)Landroid/view/accessibility/AccessibilityEvent;

    move-result-object v1

    .line 65
    .local v1, "event":Landroid/view/accessibility/AccessibilityEvent;
    invoke-static {p1}, Lcom/nuance/swype/input/IMEApplication;->from(Landroid/content/Context;)Lcom/nuance/swype/input/IMEApplication;

    move-result-object v2

    invoke-virtual {v2}, Lcom/nuance/swype/input/IMEApplication;->getIME()Lcom/nuance/swype/input/IME;

    move-result-object v2

    invoke-virtual {v2}, Lcom/nuance/swype/input/IME;->getCurrentInputView()Lcom/nuance/swype/input/InputView;

    move-result-object v0

    .line 66
    .local v0, "currentView":Lcom/nuance/swype/input/InputView;
    if-eqz v0, :cond_0

    .line 67
    invoke-virtual {v0, v1}, Lcom/nuance/swype/input/InputView;->onInitializeAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)V

    .line 68
    invoke-static {p1}, Lcom/nuance/swype/input/IMEApplication;->from(Landroid/content/Context;)Lcom/nuance/swype/input/IMEApplication;

    move-result-object v2

    invoke-virtual {v2}, Lcom/nuance/swype/input/IMEApplication;->getAppPreferences()Lcom/nuance/swype/input/AppPreferences;

    move-result-object v2

    invoke-virtual {v2}, Lcom/nuance/swype/input/AppPreferences;->getAccessibilityInfo()Lcom/nuance/swype/input/accessibility/AccessibilityInfo;

    move-result-object v2

    .line 69
    invoke-virtual {v2, v1}, Lcom/nuance/swype/input/accessibility/AccessibilityInfo;->requestSendAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)V

    .line 71
    :cond_0
    return-void
.end method

.method public speak(Landroid/content/Context;Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;)V
    .locals 3
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "notificationId"    # Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    .prologue
    .line 164
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    invoke-virtual {p2}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->getResourceId()I

    move-result v2

    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v0

    .line 165
    .local v0, "notice":Ljava/lang/String;
    const/4 v1, 0x0

    invoke-virtual {p0, p1, v0, v1}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->announceNotification(Landroid/content/Context;Ljava/lang/String;Z)V

    .line 166
    return-void
.end method

.method public speak(Landroid/content/Context;Ljava/lang/CharSequence;)V
    .locals 3
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "label"    # Ljava/lang/CharSequence;

    .prologue
    .line 74
    if-nez p2, :cond_0

    .line 82
    :goto_0
    return-void

    .line 77
    :cond_0
    invoke-static {p1}, Lcom/nuance/swype/input/IMEApplication;->from(Landroid/content/Context;)Lcom/nuance/swype/input/IMEApplication;

    move-result-object v2

    invoke-virtual {v2}, Lcom/nuance/swype/input/IMEApplication;->getAppPreferences()Lcom/nuance/swype/input/AppPreferences;

    move-result-object v2

    invoke-virtual {v2}, Lcom/nuance/swype/input/AppPreferences;->getAccessibilityInfo()Lcom/nuance/swype/input/accessibility/AccessibilityInfo;

    move-result-object v0

    .line 80
    .local v0, "accessibilityInfo":Lcom/nuance/swype/input/accessibility/AccessibilityInfo;
    invoke-static {p1, p2}, Lcom/nuance/android/compat/AccessibilityEventCompat;->initTextEvent(Landroid/content/Context;Ljava/lang/CharSequence;)Landroid/view/accessibility/AccessibilityEvent;

    move-result-object v1

    .line 81
    .local v1, "event":Landroid/view/accessibility/AccessibilityEvent;
    invoke-virtual {v0, v1}, Lcom/nuance/swype/input/accessibility/AccessibilityInfo;->requestSendAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)V

    goto :goto_0
.end method

.method public speakKeyboardLayer(Landroid/content/Context;Lcom/nuance/swype/input/KeyboardEx$KeyboardLayerType;)V
    .locals 2
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "keyboardLayer"    # Lcom/nuance/swype/input/KeyboardEx$KeyboardLayerType;

    .prologue
    .line 169
    sget-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$2;->$SwitchMap$com$nuance$swype$input$KeyboardEx$KeyboardLayerType:[I

    invoke-virtual {p2}, Lcom/nuance/swype/input/KeyboardEx$KeyboardLayerType;->ordinal()I

    move-result v1

    aget v0, v0, v1

    packed-switch v0, :pswitch_data_0

    .line 185
    :goto_0
    return-void

    .line 171
    :pswitch_0
    sget-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_LAYER_TEXT:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {p0, p1, v0}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->speak(Landroid/content/Context;Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;)V

    goto :goto_0

    .line 174
    :pswitch_1
    sget-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_LAYER_SYMBOLS:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {p0, p1, v0}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->speak(Landroid/content/Context;Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;)V

    goto :goto_0

    .line 177
    :pswitch_2
    sget-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_LAYER_EDIT:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {p0, p1, v0}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->speak(Landroid/content/Context;Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;)V

    goto :goto_0

    .line 180
    :pswitch_3
    sget-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_LAYER_NUM:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {p0, p1, v0}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->speak(Landroid/content/Context;Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;)V

    goto :goto_0

    .line 169
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_3
    .end packed-switch
.end method

.method public speakShiftState(Landroid/content/Context;Lcom/nuance/input/swypecorelib/Shift$ShiftState;Lcom/nuance/swype/input/KeyboardEx$KeyboardLayerType;)V
    .locals 2
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "state"    # Lcom/nuance/input/swypecorelib/Shift$ShiftState;
    .param p3, "keyboardLayer"    # Lcom/nuance/swype/input/KeyboardEx$KeyboardLayerType;

    .prologue
    .line 201
    sget-object v0, Lcom/nuance/swype/input/KeyboardEx$KeyboardLayerType;->KEYBOARD_TEXT:Lcom/nuance/swype/input/KeyboardEx$KeyboardLayerType;

    if-ne p3, v0, :cond_1

    .line 202
    sget-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$2;->$SwitchMap$com$nuance$input$swypecorelib$Shift$ShiftState:[I

    invoke-virtual {p2}, Lcom/nuance/input/swypecorelib/Shift$ShiftState;->ordinal()I

    move-result v1

    aget v0, v0, v1

    packed-switch v0, :pswitch_data_0

    .line 228
    :cond_0
    :goto_0
    return-void

    .line 204
    :pswitch_0
    sget-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_TEXT_LOWER_CASE:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {p0, p1, v0}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->speak(Landroid/content/Context;Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;)V

    goto :goto_0

    .line 207
    :pswitch_1
    sget-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_TEXT_CAP:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {p0, p1, v0}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->speak(Landroid/content/Context;Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;)V

    goto :goto_0

    .line 210
    :pswitch_2
    sget-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_TEXT_ALL_CAP:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {p0, p1, v0}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->speak(Landroid/content/Context;Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;)V

    goto :goto_0

    .line 215
    :cond_1
    sget-object v0, Lcom/nuance/swype/input/KeyboardEx$KeyboardLayerType;->KEYBOARD_SYMBOLS:Lcom/nuance/swype/input/KeyboardEx$KeyboardLayerType;

    if-ne p3, v0, :cond_0

    .line 216
    sget-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$2;->$SwitchMap$com$nuance$input$swypecorelib$Shift$ShiftState:[I

    invoke-virtual {p2}, Lcom/nuance/input/swypecorelib/Shift$ShiftState;->ordinal()I

    move-result v1

    aget v0, v0, v1

    packed-switch v0, :pswitch_data_1

    goto :goto_0

    .line 218
    :pswitch_3
    sget-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_SYMBOL1:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {p0, p1, v0}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->speak(Landroid/content/Context;Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;)V

    goto :goto_0

    .line 222
    :pswitch_4
    sget-object v0, Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;->ON_KEYBOARD_SYMBOL2:Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;

    invoke-virtual {p0, p1, v0}, Lcom/nuance/swype/input/accessibility/AccessibilityNotification;->speak(Landroid/content/Context;Lcom/nuance/swype/input/accessibility/AccessibilityNotification$Notification_Type;)V

    goto :goto_0

    .line 202
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
    .end packed-switch

    .line 216
    :pswitch_data_1
    .packed-switch 0x1
        :pswitch_3
        :pswitch_4
        :pswitch_4
    .end packed-switch
.end method
