package android.support.v7.view.menu;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/* loaded from: classes.dex */
public final class MenuItemImpl implements SupportMenuItem {
    static String sDeleteShortcutLabel;
    static String sEnterShortcutLabel;
    static String sPrependShortcutLabel;
    static String sSpaceShortcutLabel;
    public ActionProvider mActionProvider;
    private View mActionView;
    private final int mCategoryOrder;
    private MenuItem.OnMenuItemClickListener mClickListener;
    private final int mGroup;
    private Drawable mIconDrawable;
    private final int mId;
    private Intent mIntent;
    private Runnable mItemCallback;
    MenuBuilder mMenu;
    ContextMenu.ContextMenuInfo mMenuInfo;
    private MenuItemCompat.OnActionExpandListener mOnActionExpandListener;
    final int mOrdering;
    private char mShortcutAlphabeticChar;
    private char mShortcutNumericChar;
    int mShowAsAction;
    private SubMenuBuilder mSubMenu;
    private CharSequence mTitle;
    private CharSequence mTitleCondensed;
    private int mIconResId = 0;
    private int mFlags = 16;
    private boolean mIsActionViewExpanded = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MenuItemImpl(MenuBuilder menu, int group, int id, int categoryOrder, int ordering, CharSequence title, int showAsAction) {
        this.mShowAsAction = 0;
        this.mMenu = menu;
        this.mId = id;
        this.mGroup = group;
        this.mCategoryOrder = categoryOrder;
        this.mOrdering = ordering;
        this.mTitle = title;
        this.mShowAsAction = showAsAction;
    }

    public final boolean invoke() {
        if ((this.mClickListener != null && this.mClickListener.onMenuItemClick(this)) || this.mMenu.dispatchMenuItemSelected(this.mMenu.getRootMenu(), this)) {
            return true;
        }
        if (this.mItemCallback != null) {
            this.mItemCallback.run();
            return true;
        }
        if (this.mIntent != null) {
            try {
                this.mMenu.mContext.startActivity(this.mIntent);
                return true;
            } catch (ActivityNotFoundException e) {
                Log.e("MenuItemImpl", "Can't find activity to handle intent; ignoring", e);
            }
        }
        return this.mActionProvider != null && this.mActionProvider.onPerformDefaultAction();
    }

    @Override // android.view.MenuItem
    public final boolean isEnabled() {
        return (this.mFlags & 16) != 0;
    }

    @Override // android.view.MenuItem
    public final MenuItem setEnabled(boolean enabled) {
        if (enabled) {
            this.mFlags |= 16;
        } else {
            this.mFlags &= -17;
        }
        this.mMenu.onItemsChanged(false);
        return this;
    }

    @Override // android.view.MenuItem
    public final int getGroupId() {
        return this.mGroup;
    }

    @Override // android.view.MenuItem
    @ViewDebug.CapturedViewProperty
    public final int getItemId() {
        return this.mId;
    }

    @Override // android.view.MenuItem
    public final int getOrder() {
        return this.mCategoryOrder;
    }

    @Override // android.view.MenuItem
    public final Intent getIntent() {
        return this.mIntent;
    }

    @Override // android.view.MenuItem
    public final MenuItem setIntent(Intent intent) {
        this.mIntent = intent;
        return this;
    }

    @Override // android.view.MenuItem
    public final char getAlphabeticShortcut() {
        return this.mShortcutAlphabeticChar;
    }

    @Override // android.view.MenuItem
    public final MenuItem setAlphabeticShortcut(char alphaChar) {
        if (this.mShortcutAlphabeticChar != alphaChar) {
            this.mShortcutAlphabeticChar = Character.toLowerCase(alphaChar);
            this.mMenu.onItemsChanged(false);
        }
        return this;
    }

    @Override // android.view.MenuItem
    public final char getNumericShortcut() {
        return this.mShortcutNumericChar;
    }

    @Override // android.view.MenuItem
    public final MenuItem setNumericShortcut(char numericChar) {
        if (this.mShortcutNumericChar != numericChar) {
            this.mShortcutNumericChar = numericChar;
            this.mMenu.onItemsChanged(false);
        }
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setShortcut(char numericChar, char alphaChar) {
        this.mShortcutNumericChar = numericChar;
        this.mShortcutAlphabeticChar = Character.toLowerCase(alphaChar);
        this.mMenu.onItemsChanged(false);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final char getShortcut() {
        return this.mMenu.isQwertyMode() ? this.mShortcutAlphabeticChar : this.mShortcutNumericChar;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean shouldShowShortcut() {
        return this.mMenu.isShortcutsVisible() && getShortcut() != 0;
    }

    @Override // android.view.MenuItem
    public final SubMenu getSubMenu() {
        return this.mSubMenu;
    }

    @Override // android.view.MenuItem
    public final boolean hasSubMenu() {
        return this.mSubMenu != null;
    }

    public final void setSubMenu(SubMenuBuilder subMenu) {
        this.mSubMenu = subMenu;
        subMenu.setHeaderTitle(getTitle());
    }

    @Override // android.view.MenuItem
    @ViewDebug.CapturedViewProperty
    public final CharSequence getTitle() {
        return this.mTitle;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final CharSequence getTitleForItemView(MenuView.ItemView itemView) {
        return (itemView == null || !itemView.prefersCondensedTitle()) ? getTitle() : getTitleCondensed();
    }

    @Override // android.view.MenuItem
    public final MenuItem setTitle(CharSequence title) {
        this.mTitle = title;
        this.mMenu.onItemsChanged(false);
        if (this.mSubMenu != null) {
            this.mSubMenu.setHeaderTitle(title);
        }
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setTitle(int title) {
        return setTitle(this.mMenu.mContext.getString(title));
    }

    @Override // android.view.MenuItem
    public final CharSequence getTitleCondensed() {
        CharSequence ctitle = this.mTitleCondensed != null ? this.mTitleCondensed : this.mTitle;
        if (Build.VERSION.SDK_INT < 18 && ctitle != null && !(ctitle instanceof String)) {
            return ctitle.toString();
        }
        return ctitle;
    }

    @Override // android.view.MenuItem
    public final MenuItem setTitleCondensed(CharSequence title) {
        this.mTitleCondensed = title;
        this.mMenu.onItemsChanged(false);
        return this;
    }

    @Override // android.view.MenuItem
    public final Drawable getIcon() {
        if (this.mIconDrawable != null) {
            return this.mIconDrawable;
        }
        if (this.mIconResId == 0) {
            return null;
        }
        Drawable icon = AppCompatDrawableManager.get().getDrawable(this.mMenu.mContext, this.mIconResId, false);
        this.mIconResId = 0;
        this.mIconDrawable = icon;
        return icon;
    }

    @Override // android.view.MenuItem
    public final MenuItem setIcon(Drawable icon) {
        this.mIconResId = 0;
        this.mIconDrawable = icon;
        this.mMenu.onItemsChanged(false);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setIcon(int iconResId) {
        this.mIconDrawable = null;
        this.mIconResId = iconResId;
        this.mMenu.onItemsChanged(false);
        return this;
    }

    @Override // android.view.MenuItem
    public final boolean isCheckable() {
        return (this.mFlags & 1) == 1;
    }

    @Override // android.view.MenuItem
    public final MenuItem setCheckable(boolean checkable) {
        int oldFlags = this.mFlags;
        this.mFlags = (checkable ? 1 : 0) | (this.mFlags & (-2));
        if (oldFlags != this.mFlags) {
            this.mMenu.onItemsChanged(false);
        }
        return this;
    }

    public final void setExclusiveCheckable(boolean exclusive) {
        this.mFlags = (exclusive ? 4 : 0) | (this.mFlags & (-5));
    }

    public final boolean isExclusiveCheckable() {
        return (this.mFlags & 4) != 0;
    }

    @Override // android.view.MenuItem
    public final boolean isChecked() {
        return (this.mFlags & 2) == 2;
    }

    @Override // android.view.MenuItem
    public final MenuItem setChecked(boolean checked) {
        if ((this.mFlags & 4) != 0) {
            MenuBuilder menuBuilder = this.mMenu;
            int groupId = getGroupId();
            int size = menuBuilder.mItems.size();
            for (int i = 0; i < size; i++) {
                MenuItemImpl menuItemImpl = menuBuilder.mItems.get(i);
                if (menuItemImpl.getGroupId() == groupId && menuItemImpl.isExclusiveCheckable() && menuItemImpl.isCheckable()) {
                    menuItemImpl.setCheckedInt(menuItemImpl == this);
                }
            }
        } else {
            setCheckedInt(checked);
        }
        return this;
    }

    private void setCheckedInt(boolean checked) {
        int oldFlags = this.mFlags;
        this.mFlags = (checked ? 2 : 0) | (this.mFlags & (-3));
        if (oldFlags != this.mFlags) {
            this.mMenu.onItemsChanged(false);
        }
    }

    @Override // android.view.MenuItem
    public final boolean isVisible() {
        return (this.mActionProvider == null || !this.mActionProvider.overridesItemVisibility()) ? (this.mFlags & 8) == 0 : (this.mFlags & 8) == 0 && this.mActionProvider.isVisible();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean setVisibleInt(boolean shown) {
        int oldFlags = this.mFlags;
        this.mFlags = (shown ? 0 : 8) | (this.mFlags & (-9));
        return oldFlags != this.mFlags;
    }

    @Override // android.view.MenuItem
    public final MenuItem setVisible(boolean shown) {
        if (setVisibleInt(shown)) {
            this.mMenu.onItemVisibleChanged$4da0fe86();
        }
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener clickListener) {
        this.mClickListener = clickListener;
        return this;
    }

    public final String toString() {
        if (this.mTitle != null) {
            return this.mTitle.toString();
        }
        return null;
    }

    @Override // android.view.MenuItem
    public final ContextMenu.ContextMenuInfo getMenuInfo() {
        return this.mMenuInfo;
    }

    public final boolean isActionButton() {
        return (this.mFlags & 32) == 32;
    }

    public final boolean requestsActionButton() {
        return (this.mShowAsAction & 1) == 1;
    }

    public final boolean requiresActionButton() {
        return (this.mShowAsAction & 2) == 2;
    }

    public final void setIsActionButton(boolean isActionButton) {
        if (isActionButton) {
            this.mFlags |= 32;
        } else {
            this.mFlags &= -33;
        }
    }

    @Override // android.support.v4.internal.view.SupportMenuItem, android.view.MenuItem
    public final void setShowAsAction(int actionEnum) {
        switch (actionEnum & 3) {
            case 0:
            case 1:
            case 2:
                this.mShowAsAction = actionEnum;
                this.mMenu.onItemActionRequestChanged$4da0fe86();
                return;
            default:
                throw new IllegalArgumentException("SHOW_AS_ACTION_ALWAYS, SHOW_AS_ACTION_IF_ROOM, and SHOW_AS_ACTION_NEVER are mutually exclusive.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Override // android.support.v4.internal.view.SupportMenuItem, android.view.MenuItem
    public SupportMenuItem setActionView(View view) {
        this.mActionView = view;
        this.mActionProvider = null;
        if (view != null && view.getId() == -1 && this.mId > 0) {
            view.setId(this.mId);
        }
        this.mMenu.onItemActionRequestChanged$4da0fe86();
        return this;
    }

    @Override // android.support.v4.internal.view.SupportMenuItem, android.view.MenuItem
    public final View getActionView() {
        if (this.mActionView != null) {
            return this.mActionView;
        }
        if (this.mActionProvider != null) {
            this.mActionView = this.mActionProvider.onCreateActionView(this);
            return this.mActionView;
        }
        return null;
    }

    @Override // android.view.MenuItem
    public final MenuItem setActionProvider(android.view.ActionProvider actionProvider) {
        throw new UnsupportedOperationException("This is not supported, use MenuItemCompat.setActionProvider()");
    }

    @Override // android.view.MenuItem
    public final android.view.ActionProvider getActionProvider() {
        throw new UnsupportedOperationException("This is not supported, use MenuItemCompat.getActionProvider()");
    }

    @Override // android.support.v4.internal.view.SupportMenuItem
    public final ActionProvider getSupportActionProvider() {
        return this.mActionProvider;
    }

    @Override // android.support.v4.internal.view.SupportMenuItem
    public final SupportMenuItem setSupportActionProvider(ActionProvider actionProvider) {
        if (this.mActionProvider != null) {
            ActionProvider actionProvider2 = this.mActionProvider;
            actionProvider2.mVisibilityListener = null;
            actionProvider2.mSubUiVisibilityListener = null;
        }
        this.mActionView = null;
        this.mActionProvider = actionProvider;
        this.mMenu.onItemsChanged(true);
        if (this.mActionProvider != null) {
            this.mActionProvider.setVisibilityListener(new ActionProvider.VisibilityListener() { // from class: android.support.v7.view.menu.MenuItemImpl.1
                @Override // android.support.v4.view.ActionProvider.VisibilityListener
                public final void onActionProviderVisibilityChanged$1385ff() {
                    MenuItemImpl.this.mMenu.onItemVisibleChanged$4da0fe86();
                }
            });
        }
        return this;
    }

    @Override // android.support.v4.internal.view.SupportMenuItem, android.view.MenuItem
    public final boolean expandActionView() {
        if (!hasCollapsibleActionView()) {
            return false;
        }
        if (this.mOnActionExpandListener == null || this.mOnActionExpandListener.onMenuItemActionExpand(this)) {
            return this.mMenu.expandItemActionView(this);
        }
        return false;
    }

    @Override // android.support.v4.internal.view.SupportMenuItem, android.view.MenuItem
    public final boolean collapseActionView() {
        if ((this.mShowAsAction & 8) == 0) {
            return false;
        }
        if (this.mActionView == null) {
            return true;
        }
        if (this.mOnActionExpandListener == null || this.mOnActionExpandListener.onMenuItemActionCollapse(this)) {
            return this.mMenu.collapseItemActionView(this);
        }
        return false;
    }

    @Override // android.support.v4.internal.view.SupportMenuItem
    public final SupportMenuItem setSupportOnActionExpandListener(MenuItemCompat.OnActionExpandListener listener) {
        this.mOnActionExpandListener = listener;
        return this;
    }

    public final boolean hasCollapsibleActionView() {
        if ((this.mShowAsAction & 8) == 0) {
            return false;
        }
        if (this.mActionView == null && this.mActionProvider != null) {
            this.mActionView = this.mActionProvider.onCreateActionView(this);
        }
        return this.mActionView != null;
    }

    public final void setActionViewExpanded(boolean isExpanded) {
        this.mIsActionViewExpanded = isExpanded;
        this.mMenu.onItemsChanged(false);
    }

    @Override // android.support.v4.internal.view.SupportMenuItem, android.view.MenuItem
    public final boolean isActionViewExpanded() {
        return this.mIsActionViewExpanded;
    }

    @Override // android.view.MenuItem
    public final MenuItem setOnActionExpandListener(MenuItem.OnActionExpandListener listener) {
        throw new UnsupportedOperationException("This is not supported, use MenuItemCompat.setOnActionExpandListener()");
    }

    @Override // android.support.v4.internal.view.SupportMenuItem, android.view.MenuItem
    public final /* bridge */ /* synthetic */ MenuItem setActionView(int i) {
        Context context = this.mMenu.mContext;
        setActionView(LayoutInflater.from(context).inflate(i, (ViewGroup) new LinearLayout(context), false));
        return this;
    }

    @Override // android.support.v4.internal.view.SupportMenuItem, android.view.MenuItem
    public final /* bridge */ /* synthetic */ MenuItem setShowAsActionFlags(int i) {
        setShowAsAction(i);
        return this;
    }
}
