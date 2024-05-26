package android.support.v7.view.menu;

import android.content.Context;
import android.os.Build;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.internal.view.SupportSubMenu;
import android.support.v4.util.ArrayMap;
import android.view.MenuItem;
import android.view.SubMenu;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class BaseMenuWrapper<T> extends BaseWrapper<T> {
    final Context mContext;
    Map<SupportMenuItem, MenuItem> mMenuItems;
    Map<SupportSubMenu, SubMenu> mSubMenus;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseMenuWrapper(Context context, T object) {
        super(object);
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final MenuItem getMenuItemWrapper(MenuItem menuItem) {
        MenuItem wrappedItem;
        if (!(menuItem instanceof SupportMenuItem)) {
            return menuItem;
        }
        SupportMenuItem supportMenuItem = (SupportMenuItem) menuItem;
        if (this.mMenuItems == null) {
            this.mMenuItems = new ArrayMap();
        }
        MenuItem wrappedItem2 = this.mMenuItems.get(menuItem);
        if (wrappedItem2 == null) {
            Context context = this.mContext;
            if (Build.VERSION.SDK_INT >= 16) {
                wrappedItem = new MenuItemWrapperJB(context, supportMenuItem);
            } else if (Build.VERSION.SDK_INT >= 14) {
                wrappedItem = new MenuItemWrapperICS(context, supportMenuItem);
            } else {
                throw new UnsupportedOperationException();
            }
            this.mMenuItems.put(supportMenuItem, wrappedItem);
            return wrappedItem;
        }
        return wrappedItem2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final SubMenu getSubMenuWrapper(SubMenu subMenu) {
        if (!(subMenu instanceof SupportSubMenu)) {
            return subMenu;
        }
        SupportSubMenu supportSubMenu = (SupportSubMenu) subMenu;
        if (this.mSubMenus == null) {
            this.mSubMenus = new ArrayMap();
        }
        SubMenu wrappedMenu = this.mSubMenus.get(supportSubMenu);
        if (wrappedMenu == null) {
            Context context = this.mContext;
            if (Build.VERSION.SDK_INT >= 14) {
                SubMenu wrappedMenu2 = new SubMenuWrapperICS(context, supportSubMenu);
                this.mSubMenus.put(supportSubMenu, wrappedMenu2);
                return wrappedMenu2;
            }
            throw new UnsupportedOperationException();
        }
        return wrappedMenu;
    }
}
