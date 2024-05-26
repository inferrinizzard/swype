package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v7.view.menu.ListMenuItemView;
import android.support.v7.view.menu.MenuAdapter;
import android.support.v7.view.menu.MenuBuilder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public final class MenuPopupWindow extends ListPopupWindow implements MenuItemHoverListener {
    public static Method sSetTouchModalMethod;
    public MenuItemHoverListener mHoverListener;

    static {
        try {
            sSetTouchModalMethod = PopupWindow.class.getDeclaredMethod("setTouchModal", Boolean.TYPE);
        } catch (NoSuchMethodException e) {
            Log.i("MenuPopupWindow", "Could not find method setTouchModal() on PopupWindow. Oh well.");
        }
    }

    public MenuPopupWindow(Context context, int defStyleAttr, int defStyleRes) {
        super(context, null, defStyleAttr, defStyleRes);
    }

    @Override // android.support.v7.widget.ListPopupWindow
    final DropDownListView createDropDownListView(Context context, boolean hijackFocus) {
        MenuDropDownListView view = new MenuDropDownListView(context, hijackFocus);
        view.setHoverListener(this);
        return view;
    }

    @Override // android.support.v7.widget.MenuItemHoverListener
    public final void onItemHoverEnter(MenuBuilder menu, MenuItem item) {
        if (this.mHoverListener != null) {
            this.mHoverListener.onItemHoverEnter(menu, item);
        }
    }

    @Override // android.support.v7.widget.MenuItemHoverListener
    public final void onItemHoverExit(MenuBuilder menu, MenuItem item) {
        if (this.mHoverListener != null) {
            this.mHoverListener.onItemHoverExit(menu, item);
        }
    }

    /* loaded from: classes.dex */
    public static class MenuDropDownListView extends DropDownListView {
        final int mAdvanceKey;
        private MenuItemHoverListener mHoverListener;
        private MenuItem mHoveredMenuItem;
        final int mRetreatKey;

        @Override // android.support.v7.widget.DropDownListView, android.view.ViewGroup, android.view.View
        public /* bridge */ /* synthetic */ boolean hasFocus() {
            return super.hasFocus();
        }

        @Override // android.support.v7.widget.DropDownListView, android.view.View
        public /* bridge */ /* synthetic */ boolean hasWindowFocus() {
            return super.hasWindowFocus();
        }

        @Override // android.support.v7.widget.DropDownListView, android.view.View
        public /* bridge */ /* synthetic */ boolean isFocused() {
            return super.isFocused();
        }

        @Override // android.support.v7.widget.DropDownListView, android.view.View
        public /* bridge */ /* synthetic */ boolean isInTouchMode() {
            return super.isInTouchMode();
        }

        @Override // android.support.v7.widget.DropDownListView
        public final /* bridge */ /* synthetic */ boolean onForwardedEvent(MotionEvent motionEvent, int i) {
            return super.onForwardedEvent(motionEvent, i);
        }

        public MenuDropDownListView(Context context, boolean hijackFocus) {
            super(context, hijackFocus);
            Configuration config = context.getResources().getConfiguration();
            if (Build.VERSION.SDK_INT >= 17 && 1 == config.getLayoutDirection()) {
                this.mAdvanceKey = 21;
                this.mRetreatKey = 22;
            } else {
                this.mAdvanceKey = 22;
                this.mRetreatKey = 21;
            }
        }

        public void setHoverListener(MenuItemHoverListener hoverListener) {
            this.mHoverListener = hoverListener;
        }

        @Override // android.widget.ListView, android.widget.AbsListView, android.view.View, android.view.KeyEvent.Callback
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            ListMenuItemView selectedItem = (ListMenuItemView) getSelectedView();
            if (selectedItem != null && keyCode == this.mAdvanceKey) {
                if (selectedItem.isEnabled() && selectedItem.getItemData().hasSubMenu()) {
                    performItemClick(selectedItem, getSelectedItemPosition(), getSelectedItemId());
                }
                return true;
            }
            if (selectedItem != null && keyCode == this.mRetreatKey) {
                setSelection(-1);
                ((MenuAdapter) getAdapter()).mAdapterMenu.close(false);
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }

        @Override // android.view.View
        public boolean onHoverEvent(MotionEvent ev) {
            int headersCount;
            MenuAdapter menuAdapter;
            int position;
            int itemPosition;
            if (this.mHoverListener != null) {
                ListAdapter adapter = getAdapter();
                if (adapter instanceof HeaderViewListAdapter) {
                    HeaderViewListAdapter headerAdapter = (HeaderViewListAdapter) adapter;
                    headersCount = headerAdapter.getHeadersCount();
                    menuAdapter = (MenuAdapter) headerAdapter.getWrappedAdapter();
                } else {
                    headersCount = 0;
                    menuAdapter = (MenuAdapter) adapter;
                }
                MenuItem menuItem = null;
                if (ev.getAction() != 10 && (position = pointToPosition((int) ev.getX(), (int) ev.getY())) != -1 && (itemPosition = position - headersCount) >= 0 && itemPosition < menuAdapter.getCount()) {
                    menuItem = menuAdapter.getItem(itemPosition);
                }
                MenuItem oldMenuItem = this.mHoveredMenuItem;
                if (oldMenuItem != menuItem) {
                    MenuBuilder menu = menuAdapter.mAdapterMenu;
                    if (oldMenuItem != null) {
                        this.mHoverListener.onItemHoverExit(menu, oldMenuItem);
                    }
                    this.mHoveredMenuItem = menuItem;
                    if (menuItem != null) {
                        this.mHoverListener.onItemHoverEnter(menu, menuItem);
                    }
                }
            }
            return super.onHoverEvent(ev);
        }
    }
}
