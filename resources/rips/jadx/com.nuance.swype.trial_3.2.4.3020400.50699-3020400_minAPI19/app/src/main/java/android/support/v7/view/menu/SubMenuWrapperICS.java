package android.support.v7.view.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.internal.view.SupportSubMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class SubMenuWrapperICS extends MenuWrapperICS implements SubMenu {
    /* JADX INFO: Access modifiers changed from: package-private */
    public SubMenuWrapperICS(Context context, SupportSubMenu subMenu) {
        super(context, subMenu);
    }

    @Override // android.view.SubMenu
    public final SubMenu setHeaderTitle(int titleRes) {
        ((SupportSubMenu) this.mWrappedObject).setHeaderTitle(titleRes);
        return this;
    }

    @Override // android.view.SubMenu
    public final SubMenu setHeaderTitle(CharSequence title) {
        ((SupportSubMenu) this.mWrappedObject).setHeaderTitle(title);
        return this;
    }

    @Override // android.view.SubMenu
    public final SubMenu setHeaderIcon(int iconRes) {
        ((SupportSubMenu) this.mWrappedObject).setHeaderIcon(iconRes);
        return this;
    }

    @Override // android.view.SubMenu
    public final SubMenu setHeaderIcon(Drawable icon) {
        ((SupportSubMenu) this.mWrappedObject).setHeaderIcon(icon);
        return this;
    }

    @Override // android.view.SubMenu
    public final SubMenu setHeaderView(View view) {
        ((SupportSubMenu) this.mWrappedObject).setHeaderView(view);
        return this;
    }

    @Override // android.view.SubMenu
    public final void clearHeader() {
        ((SupportSubMenu) this.mWrappedObject).clearHeader();
    }

    @Override // android.view.SubMenu
    public final SubMenu setIcon(int iconRes) {
        ((SupportSubMenu) this.mWrappedObject).setIcon(iconRes);
        return this;
    }

    @Override // android.view.SubMenu
    public final SubMenu setIcon(Drawable icon) {
        ((SupportSubMenu) this.mWrappedObject).setIcon(icon);
        return this;
    }

    @Override // android.view.SubMenu
    public final MenuItem getItem() {
        return getMenuItemWrapper(((SupportSubMenu) this.mWrappedObject).getItem());
    }
}
