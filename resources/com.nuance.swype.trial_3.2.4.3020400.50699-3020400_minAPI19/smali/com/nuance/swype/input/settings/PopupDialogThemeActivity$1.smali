.class Lcom/nuance/swype/input/settings/PopupDialogThemeActivity$1;
.super Ljava/lang/Object;
.source "PopupDialogThemeActivity.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;->onCreate(Landroid/os/Bundle;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;

.field final synthetic val$mySku:Ljava/lang/String;


# direct methods
.method constructor <init>(Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;Ljava/lang/String;)V
    .locals 0
    .param p1, "this$0"    # Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;

    .prologue
    .line 146
    iput-object p1, p0, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity$1;->this$0:Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;

    iput-object p2, p0, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity$1;->val$mySku:Ljava/lang/String;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 8
    .param p1, "v"    # Landroid/view/View;

    .prologue
    .line 150
    iget-object v0, p0, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity$1;->this$0:Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;

    const/4 v1, 0x1

    invoke-static {v0, v1}, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;->access$002(Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;Z)Z

    .line 151
    new-instance v7, Landroid/content/Intent;

    invoke-direct {v7}, Landroid/content/Intent;-><init>()V

    .line 152
    .local v7, "resultIntent":Landroid/content/Intent;
    const-string/jumbo v0, "com.nuance.swype.input.settings.PopupDialogThemeActivity.theme_id"

    iget-object v1, p0, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity$1;->val$mySku:Ljava/lang/String;

    invoke-virtual {v7, v0, v1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 153
    const-string/jumbo v0, "com.nuance.swype.input.settings.PopupDialogThemeActivity.category_id"

    iget-object v1, p0, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity$1;->this$0:Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;

    invoke-static {v1}, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;->access$100(Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v7, v0, v1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 154
    iget-object v0, p0, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity$1;->this$0:Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;

    const/4 v1, -0x1

    invoke-virtual {v0, v1, v7}, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;->setResult(ILandroid/content/Intent;)V

    .line 156
    iget-object v0, p0, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity$1;->this$0:Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;

    sget-object v1, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview$Action;->APPLY:Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview$Action;

    sget-object v2, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview$Result;->SUCCESS:Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview$Result;

    iget-object v3, p0, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity$1;->val$mySku:Ljava/lang/String;

    iget-object v4, p0, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity$1;->this$0:Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;

    .line 159
    invoke-static {v4}, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;->access$200(Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;)Ljava/lang/String;

    move-result-object v4

    iget-object v5, p0, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity$1;->this$0:Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;

    .line 160
    invoke-static {v5}, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;->access$300(Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;)I

    move-result v5

    iget-object v6, p0, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity$1;->this$0:Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;

    .line 161
    invoke-static {v6}, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;->access$100(Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;)Ljava/lang/String;

    move-result-object v6

    .line 156
    invoke-static/range {v0 .. v6}, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;->access$400(Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview$Action;Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview$Result;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)V

    .line 162
    iget-object v0, p0, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity$1;->this$0:Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;

    invoke-virtual {v0}, Lcom/nuance/swype/input/settings/PopupDialogThemeActivity;->finish()V

    .line 163
    return-void
.end method
