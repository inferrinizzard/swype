package android.support.v7.view.menu;

import android.content.Context;

/* loaded from: classes.dex */
public interface MenuPresenter {

    /* loaded from: classes.dex */
    public interface Callback {
        void onCloseMenu(MenuBuilder menuBuilder, boolean z);

        boolean onOpenSubMenu(MenuBuilder menuBuilder);
    }

    boolean collapseItemActionView$5c2da31d(MenuItemImpl menuItemImpl);

    boolean expandItemActionView$5c2da31d(MenuItemImpl menuItemImpl);

    boolean flagActionItems();

    void initForMenu(Context context, MenuBuilder menuBuilder);

    void onCloseMenu(MenuBuilder menuBuilder, boolean z);

    boolean onSubMenuSelected(SubMenuBuilder subMenuBuilder);

    void setCallback(Callback callback);

    void updateMenuView(boolean z);
}
