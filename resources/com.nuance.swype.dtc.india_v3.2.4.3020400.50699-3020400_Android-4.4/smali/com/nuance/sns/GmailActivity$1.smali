.class Lcom/nuance/sns/GmailActivity$1;
.super Ljava/lang/Object;
.source "GmailActivity.java"

# interfaces
.implements Landroid/content/DialogInterface$OnDismissListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/nuance/sns/GmailActivity;->onCreate(Landroid/os/Bundle;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/nuance/sns/GmailActivity;


# direct methods
.method constructor <init>(Lcom/nuance/sns/GmailActivity;)V
    .locals 0
    .param p1, "this$0"    # Lcom/nuance/sns/GmailActivity;

    .prologue
    .line 65
    iput-object p1, p0, Lcom/nuance/sns/GmailActivity$1;->this$0:Lcom/nuance/sns/GmailActivity;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onDismiss(Landroid/content/DialogInterface;)V
    .locals 1
    .param p1, "dialogInterface"    # Landroid/content/DialogInterface;

    .prologue
    .line 68
    iget-object v0, p0, Lcom/nuance/sns/GmailActivity$1;->this$0:Lcom/nuance/sns/GmailActivity;

    invoke-virtual {v0}, Lcom/nuance/sns/GmailActivity;->closing()V

    .line 69
    return-void
.end method
