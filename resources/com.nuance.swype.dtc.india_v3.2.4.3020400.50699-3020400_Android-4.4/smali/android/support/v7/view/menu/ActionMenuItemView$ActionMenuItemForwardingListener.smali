.class final Landroid/support/v7/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;
.super Landroid/support/v7/widget/ForwardingListener;
.source "ActionMenuItemView.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/view/menu/ActionMenuItemView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "ActionMenuItemForwardingListener"
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/view/menu/ActionMenuItemView;


# direct methods
.method public constructor <init>(Landroid/support/v7/view/menu/ActionMenuItemView;)V
    .locals 0

    .prologue
    .line 311
    iput-object p1, p0, Landroid/support/v7/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;->this$0:Landroid/support/v7/view/menu/ActionMenuItemView;

    .line 312
    invoke-direct {p0, p1}, Landroid/support/v7/widget/ForwardingListener;-><init>(Landroid/view/View;)V

    .line 313
    return-void
.end method


# virtual methods
.method public final getPopup()Landroid/support/v7/view/menu/ShowableListMenu;
    .locals 1

    .prologue
    .line 317
    iget-object v0, p0, Landroid/support/v7/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;->this$0:Landroid/support/v7/view/menu/ActionMenuItemView;

    invoke-static {v0}, Landroid/support/v7/view/menu/ActionMenuItemView;->access$000(Landroid/support/v7/view/menu/ActionMenuItemView;)Landroid/support/v7/view/menu/ActionMenuItemView$PopupCallback;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 318
    iget-object v0, p0, Landroid/support/v7/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;->this$0:Landroid/support/v7/view/menu/ActionMenuItemView;

    invoke-static {v0}, Landroid/support/v7/view/menu/ActionMenuItemView;->access$000(Landroid/support/v7/view/menu/ActionMenuItemView;)Landroid/support/v7/view/menu/ActionMenuItemView$PopupCallback;

    move-result-object v0

    invoke-virtual {v0}, Landroid/support/v7/view/menu/ActionMenuItemView$PopupCallback;->getPopup()Landroid/support/v7/view/menu/ShowableListMenu;

    move-result-object v0

    .line 320
    :goto_0
    return-object v0

    :cond_0
    const/4 v0, 0x0

    goto :goto_0
.end method

.method protected final onForwardingStarted()Z
    .locals 4

    .prologue
    const/4 v1, 0x0

    .line 326
    iget-object v2, p0, Landroid/support/v7/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;->this$0:Landroid/support/v7/view/menu/ActionMenuItemView;

    invoke-static {v2}, Landroid/support/v7/view/menu/ActionMenuItemView;->access$100(Landroid/support/v7/view/menu/ActionMenuItemView;)Landroid/support/v7/view/menu/MenuBuilder$ItemInvoker;

    move-result-object v2

    if-eqz v2, :cond_0

    iget-object v2, p0, Landroid/support/v7/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;->this$0:Landroid/support/v7/view/menu/ActionMenuItemView;

    invoke-static {v2}, Landroid/support/v7/view/menu/ActionMenuItemView;->access$100(Landroid/support/v7/view/menu/ActionMenuItemView;)Landroid/support/v7/view/menu/MenuBuilder$ItemInvoker;

    move-result-object v2

    iget-object v3, p0, Landroid/support/v7/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;->this$0:Landroid/support/v7/view/menu/ActionMenuItemView;

    invoke-static {v3}, Landroid/support/v7/view/menu/ActionMenuItemView;->access$200(Landroid/support/v7/view/menu/ActionMenuItemView;)Landroid/support/v7/view/menu/MenuItemImpl;

    move-result-object v3

    invoke-interface {v2, v3}, Landroid/support/v7/view/menu/MenuBuilder$ItemInvoker;->invokeItem(Landroid/support/v7/view/menu/MenuItemImpl;)Z

    move-result v2

    if-eqz v2, :cond_0

    .line 327
    invoke-virtual {p0}, Landroid/support/v7/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;->getPopup()Landroid/support/v7/view/menu/ShowableListMenu;

    move-result-object v0

    .line 328
    .local v0, "popup":Landroid/support/v7/view/menu/ShowableListMenu;
    if-eqz v0, :cond_0

    invoke-interface {v0}, Landroid/support/v7/view/menu/ShowableListMenu;->isShowing()Z

    move-result v2

    if-eqz v2, :cond_0

    const/4 v1, 0x1

    .line 330
    .end local v0    # "popup":Landroid/support/v7/view/menu/ShowableListMenu;
    :cond_0
    return v1
.end method
