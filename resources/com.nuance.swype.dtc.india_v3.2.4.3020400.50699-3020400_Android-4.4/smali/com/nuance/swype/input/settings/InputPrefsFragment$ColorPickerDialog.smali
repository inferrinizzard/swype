.class public Lcom/nuance/swype/input/settings/InputPrefsFragment$ColorPickerDialog;
.super Landroid/support/v4/app/DialogFragment;
.source "InputPrefsFragment.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/nuance/swype/input/settings/InputPrefsFragment;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "ColorPickerDialog"
.end annotation


# direct methods
.method public constructor <init>()V
    .locals 0

    .prologue
    .line 77
    invoke-direct {p0}, Landroid/support/v4/app/DialogFragment;-><init>()V

    return-void
.end method


# virtual methods
.method public onCreateDialog(Landroid/os/Bundle;)Landroid/app/Dialog;
    .locals 1
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    .prologue
    .line 81
    invoke-virtual {p0}, Lcom/nuance/swype/input/settings/InputPrefsFragment$ColorPickerDialog;->getTargetFragment()Landroid/support/v4/app/Fragment;

    move-result-object v0

    check-cast v0, Lcom/nuance/swype/input/settings/InputPrefsFragment;

    invoke-static {v0}, Lcom/nuance/swype/input/settings/InputPrefsFragment;->access$000(Lcom/nuance/swype/input/settings/InputPrefsFragment;)Lcom/nuance/swype/input/settings/InputPrefs;

    move-result-object v0

    .line 82
    invoke-virtual {v0}, Lcom/nuance/swype/input/settings/InputPrefs;->createColorPickerDialog()Landroid/app/Dialog;

    move-result-object v0

    return-object v0
.end method
