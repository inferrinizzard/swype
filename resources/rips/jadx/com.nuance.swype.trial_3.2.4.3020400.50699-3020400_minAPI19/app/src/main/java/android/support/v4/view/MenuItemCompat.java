package android.support.v4.view;

import android.os.Build;
import android.support.v4.internal.view.SupportMenuItem;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

/* loaded from: classes.dex */
public final class MenuItemCompat {
    static final MenuVersionImpl IMPL;

    /* loaded from: classes.dex */
    interface MenuVersionImpl {
        boolean expandActionView(MenuItem menuItem);

        MenuItem setActionView(MenuItem menuItem, int i);

        MenuItem setActionView(MenuItem menuItem, View view);

        void setShowAsAction(MenuItem menuItem, int i);
    }

    /* loaded from: classes.dex */
    public interface OnActionExpandListener {
        boolean onMenuItemActionCollapse(MenuItem menuItem);

        boolean onMenuItemActionExpand(MenuItem menuItem);
    }

    /* loaded from: classes.dex */
    static class BaseMenuVersionImpl implements MenuVersionImpl {
        BaseMenuVersionImpl() {
        }

        @Override // android.support.v4.view.MenuItemCompat.MenuVersionImpl
        public final void setShowAsAction(MenuItem item, int actionEnum) {
        }

        @Override // android.support.v4.view.MenuItemCompat.MenuVersionImpl
        public final MenuItem setActionView(MenuItem item, View view) {
            return item;
        }

        @Override // android.support.v4.view.MenuItemCompat.MenuVersionImpl
        public final MenuItem setActionView(MenuItem item, int resId) {
            return item;
        }

        @Override // android.support.v4.view.MenuItemCompat.MenuVersionImpl
        public final boolean expandActionView(MenuItem item) {
            return false;
        }
    }

    /* loaded from: classes.dex */
    static class HoneycombMenuVersionImpl implements MenuVersionImpl {
        HoneycombMenuVersionImpl() {
        }

        @Override // android.support.v4.view.MenuItemCompat.MenuVersionImpl
        public boolean expandActionView(MenuItem item) {
            return false;
        }

        @Override // android.support.v4.view.MenuItemCompat.MenuVersionImpl
        public final void setShowAsAction(MenuItem item, int actionEnum) {
            item.setShowAsAction(actionEnum);
        }

        @Override // android.support.v4.view.MenuItemCompat.MenuVersionImpl
        public final MenuItem setActionView(MenuItem item, View view) {
            return item.setActionView(view);
        }

        @Override // android.support.v4.view.MenuItemCompat.MenuVersionImpl
        public final MenuItem setActionView(MenuItem item, int resId) {
            return item.setActionView(resId);
        }
    }

    /* loaded from: classes.dex */
    static class IcsMenuVersionImpl extends HoneycombMenuVersionImpl {
        IcsMenuVersionImpl() {
        }

        @Override // android.support.v4.view.MenuItemCompat.HoneycombMenuVersionImpl, android.support.v4.view.MenuItemCompat.MenuVersionImpl
        public final boolean expandActionView(MenuItem item) {
            return item.expandActionView();
        }
    }

    static {
        int version = Build.VERSION.SDK_INT;
        if (version >= 14) {
            IMPL = new IcsMenuVersionImpl();
        } else if (version >= 11) {
            IMPL = new HoneycombMenuVersionImpl();
        } else {
            IMPL = new BaseMenuVersionImpl();
        }
    }

    public static void setShowAsAction(MenuItem item, int actionEnum) {
        if (item instanceof SupportMenuItem) {
            ((SupportMenuItem) item).setShowAsAction(actionEnum);
        } else {
            IMPL.setShowAsAction(item, actionEnum);
        }
    }

    public static MenuItem setActionView(MenuItem item, View view) {
        return item instanceof SupportMenuItem ? ((SupportMenuItem) item).setActionView(view) : IMPL.setActionView(item, view);
    }

    public static MenuItem setActionView(MenuItem item, int resId) {
        return item instanceof SupportMenuItem ? ((SupportMenuItem) item).setActionView(resId) : IMPL.setActionView(item, resId);
    }

    public static MenuItem setActionProvider(MenuItem item, ActionProvider provider) {
        if (item instanceof SupportMenuItem) {
            return ((SupportMenuItem) item).setSupportActionProvider(provider);
        }
        Log.w("MenuItemCompat", "setActionProvider: item does not implement SupportMenuItem; ignoring");
        return item;
    }

    public static boolean expandActionView(MenuItem item) {
        return item instanceof SupportMenuItem ? ((SupportMenuItem) item).expandActionView() : IMPL.expandActionView(item);
    }
}
