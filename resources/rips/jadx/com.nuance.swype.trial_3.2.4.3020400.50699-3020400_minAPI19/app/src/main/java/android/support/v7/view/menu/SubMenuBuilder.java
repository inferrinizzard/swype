package android.support.v7.view.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.view.menu.MenuBuilder;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/* loaded from: classes.dex */
public final class SubMenuBuilder extends MenuBuilder implements SubMenu {
    private MenuItemImpl mItem;
    public MenuBuilder mParentMenu;

    public SubMenuBuilder(Context context, MenuBuilder parentMenu, MenuItemImpl item) {
        super(context);
        this.mParentMenu = parentMenu;
        this.mItem = item;
    }

    @Override // android.support.v7.view.menu.MenuBuilder, android.view.Menu
    public final void setQwertyMode(boolean isQwerty) {
        this.mParentMenu.setQwertyMode(isQwerty);
    }

    @Override // android.support.v7.view.menu.MenuBuilder
    public final boolean isQwertyMode() {
        return this.mParentMenu.isQwertyMode();
    }

    @Override // android.support.v7.view.menu.MenuBuilder
    public final boolean isShortcutsVisible() {
        return this.mParentMenu.isShortcutsVisible();
    }

    @Override // android.view.SubMenu
    public final MenuItem getItem() {
        return this.mItem;
    }

    @Override // android.support.v7.view.menu.MenuBuilder
    public final void setCallback(MenuBuilder.Callback callback) {
        this.mParentMenu.setCallback(callback);
    }

    @Override // android.support.v7.view.menu.MenuBuilder
    public final MenuBuilder getRootMenu() {
        return this.mParentMenu.getRootMenu();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.view.menu.MenuBuilder
    public final boolean dispatchMenuItemSelected(MenuBuilder menu, MenuItem item) {
        return super.dispatchMenuItemSelected(menu, item) || this.mParentMenu.dispatchMenuItemSelected(menu, item);
    }

    @Override // android.view.SubMenu
    public final SubMenu setIcon(Drawable icon) {
        this.mItem.setIcon(icon);
        return this;
    }

    @Override // android.view.SubMenu
    public final SubMenu setIcon(int iconRes) {
        this.mItem.setIcon(iconRes);
        return this;
    }

    @Override // android.view.SubMenu
    public final SubMenu setHeaderIcon(Drawable icon) {
        super.setHeaderInternal(0, null, 0, icon, null);
        return this;
    }

    @Override // android.view.SubMenu
    public final SubMenu setHeaderTitle(CharSequence title) {
        super.setHeaderInternal(0, title, 0, null, null);
        return this;
    }

    @Override // android.view.SubMenu
    public final SubMenu setHeaderView(View view) {
        super.setHeaderInternal(0, null, 0, null, view);
        return this;
    }

    @Override // android.support.v7.view.menu.MenuBuilder
    public final boolean expandItemActionView(MenuItemImpl item) {
        return this.mParentMenu.expandItemActionView(item);
    }

    @Override // android.support.v7.view.menu.MenuBuilder
    public final boolean collapseItemActionView(MenuItemImpl item) {
        return this.mParentMenu.collapseItemActionView(item);
    }

    @Override // android.view.SubMenu
    public final SubMenu setHeaderIcon(int iconRes) {
        super.setHeaderInternal(0, null, iconRes, null, null);
        return this;
    }

    @Override // android.view.SubMenu
    public final SubMenu setHeaderTitle(int titleRes) {
        super.setHeaderInternal(titleRes, null, 0, null, null);
        return this;
    }
}
