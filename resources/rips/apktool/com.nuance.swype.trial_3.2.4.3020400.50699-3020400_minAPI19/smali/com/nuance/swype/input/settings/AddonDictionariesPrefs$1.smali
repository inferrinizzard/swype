.class Lcom/nuance/swype/input/settings/AddonDictionariesPrefs$1;
.super Ljava/lang/Object;
.source "AddonDictionariesPrefs.java"

# interfaces
.implements Landroid/os/Handler$Callback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/nuance/swype/input/settings/AddonDictionariesPrefs;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/nuance/swype/input/settings/AddonDictionariesPrefs;


# direct methods
.method constructor <init>(Lcom/nuance/swype/input/settings/AddonDictionariesPrefs;)V
    .locals 0
    .param p1, "this$0"    # Lcom/nuance/swype/input/settings/AddonDictionariesPrefs;

    .prologue
    .line 69
    iput-object p1, p0, Lcom/nuance/swype/input/settings/AddonDictionariesPrefs$1;->this$0:Lcom/nuance/swype/input/settings/AddonDictionariesPrefs;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public handleMessage(Landroid/os/Message;)Z
    .locals 2
    .param p1, "arg0"    # Landroid/os/Message;

    .prologue
    const/4 v0, 0x0

    .line 73
    iget v1, p1, Landroid/os/Message;->what:I

    packed-switch v1, :pswitch_data_0

    .line 83
    :goto_0
    return v0

    .line 75
    :pswitch_0
    iget-object v1, p0, Lcom/nuance/swype/input/settings/AddonDictionariesPrefs$1;->this$0:Lcom/nuance/swype/input/settings/AddonDictionariesPrefs;

    invoke-static {v1}, Lcom/nuance/swype/input/settings/AddonDictionariesPrefs;->access$000(Lcom/nuance/swype/input/settings/AddonDictionariesPrefs;)Landroid/widget/ProgressBar;

    move-result-object v1

    invoke-virtual {v1, v0}, Landroid/widget/ProgressBar;->setVisibility(I)V

    .line 83
    :goto_1
    const/4 v0, 0x1

    goto :goto_0

    .line 78
    :pswitch_1
    iget-object v0, p0, Lcom/nuance/swype/input/settings/AddonDictionariesPrefs$1;->this$0:Lcom/nuance/swype/input/settings/AddonDictionariesPrefs;

    invoke-static {v0}, Lcom/nuance/swype/input/settings/AddonDictionariesPrefs;->access$000(Lcom/nuance/swype/input/settings/AddonDictionariesPrefs;)Landroid/widget/ProgressBar;

    move-result-object v0

    const/4 v1, 0x4

    invoke-virtual {v0, v1}, Landroid/widget/ProgressBar;->setVisibility(I)V

    goto :goto_1

    .line 73
    nop

    :pswitch_data_0
    .packed-switch 0x65
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method
