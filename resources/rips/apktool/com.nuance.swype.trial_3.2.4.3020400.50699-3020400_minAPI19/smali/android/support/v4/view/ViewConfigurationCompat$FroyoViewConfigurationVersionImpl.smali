.class Landroid/support/v4/view/ViewConfigurationCompat$FroyoViewConfigurationVersionImpl;
.super Landroid/support/v4/view/ViewConfigurationCompat$BaseViewConfigurationVersionImpl;
.source "ViewConfigurationCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/ViewConfigurationCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "FroyoViewConfigurationVersionImpl"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    .prologue
    .line 53
    invoke-direct {p0}, Landroid/support/v4/view/ViewConfigurationCompat$BaseViewConfigurationVersionImpl;-><init>()V

    return-void
.end method


# virtual methods
.method public final getScaledPagingTouchSlop(Landroid/view/ViewConfiguration;)I
    .locals 1
    .param p1, "config"    # Landroid/view/ViewConfiguration;

    .prologue
    .line 56
    .line 1026
    invoke-virtual {p1}, Landroid/view/ViewConfiguration;->getScaledPagingTouchSlop()I

    move-result v0

    .line 56
    return v0
.end method
