package android.support.v7.view.menu;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.widget.MenuItemHoverListener;
import android.support.v7.widget.MenuPopupWindow;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.PopupWindow;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class CascadingMenuPopup extends MenuPopup implements MenuPresenter, View.OnKeyListener, PopupWindow.OnDismissListener {
    private View mAnchorView;
    private final Context mContext;
    private boolean mHasXOffset;
    private boolean mHasYOffset;
    private final int mMenuMaxWidth;
    private PopupWindow.OnDismissListener mOnDismissListener;
    private final boolean mOverflowOnly;
    private final int mPopupStyleAttr;
    private final int mPopupStyleRes;
    private MenuPresenter.Callback mPresenterCallback;
    private boolean mShouldCloseImmediately;
    private boolean mShowTitle;
    private View mShownAnchorView;
    private final Handler mSubMenuHoverHandler;
    private ViewTreeObserver mTreeObserver;
    private int mXOffset;
    private int mYOffset;
    private final List<MenuBuilder> mPendingMenus = new LinkedList();
    private final List<CascadingMenuInfo> mShowingMenus = new ArrayList();
    private final ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: android.support.v7.view.menu.CascadingMenuPopup.1
        @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
        public final void onGlobalLayout() {
            if (CascadingMenuPopup.this.isShowing() && CascadingMenuPopup.this.mShowingMenus.size() > 0 && !((CascadingMenuInfo) CascadingMenuPopup.this.mShowingMenus.get(0)).window.mModal) {
                View anchor = CascadingMenuPopup.this.mShownAnchorView;
                if (anchor != null && anchor.isShown()) {
                    Iterator it = CascadingMenuPopup.this.mShowingMenus.iterator();
                    while (it.hasNext()) {
                        ((CascadingMenuInfo) it.next()).window.show();
                    }
                    return;
                }
                CascadingMenuPopup.this.dismiss();
            }
        }
    };
    private final MenuItemHoverListener mMenuItemHoverListener = new MenuItemHoverListener() { // from class: android.support.v7.view.menu.CascadingMenuPopup.2
        @Override // android.support.v7.widget.MenuItemHoverListener
        public final void onItemHoverExit(MenuBuilder menu, MenuItem item) {
            CascadingMenuPopup.this.mSubMenuHoverHandler.removeCallbacksAndMessages(menu);
        }

        @Override // android.support.v7.widget.MenuItemHoverListener
        public final void onItemHoverEnter(final MenuBuilder menu, final MenuItem item) {
            final CascadingMenuInfo nextInfo;
            CascadingMenuPopup.this.mSubMenuHoverHandler.removeCallbacksAndMessages(null);
            int menuIndex = -1;
            int i = 0;
            int count = CascadingMenuPopup.this.mShowingMenus.size();
            while (true) {
                if (i >= count) {
                    break;
                }
                if (menu != ((CascadingMenuInfo) CascadingMenuPopup.this.mShowingMenus.get(i)).menu) {
                    i++;
                } else {
                    menuIndex = i;
                    break;
                }
            }
            if (menuIndex != -1) {
                int nextIndex = menuIndex + 1;
                if (nextIndex < CascadingMenuPopup.this.mShowingMenus.size()) {
                    nextInfo = (CascadingMenuInfo) CascadingMenuPopup.this.mShowingMenus.get(nextIndex);
                } else {
                    nextInfo = null;
                }
                Runnable runnable = new Runnable() { // from class: android.support.v7.view.menu.CascadingMenuPopup.2.1
                    @Override // java.lang.Runnable
                    public final void run() {
                        if (nextInfo != null) {
                            CascadingMenuPopup.this.mShouldCloseImmediately = true;
                            nextInfo.menu.close(false);
                            CascadingMenuPopup.this.mShouldCloseImmediately = false;
                        }
                        if (item.isEnabled() && item.hasSubMenu()) {
                            menu.performItemAction(item, 0);
                        }
                    }
                };
                long uptimeMillis = SystemClock.uptimeMillis() + 200;
                CascadingMenuPopup.this.mSubMenuHoverHandler.postAtTime(runnable, menu, uptimeMillis);
            }
        }
    };
    private int mRawDropDownGravity = 0;
    private int mDropDownGravity = 0;
    private boolean mForceShowIcon = false;
    private int mLastPosition = getInitialMenuPosition();

    public CascadingMenuPopup(Context context, View anchor, int popupStyleAttr, int popupStyleRes, boolean overflowOnly) {
        this.mContext = context;
        this.mAnchorView = anchor;
        this.mPopupStyleAttr = popupStyleAttr;
        this.mPopupStyleRes = popupStyleRes;
        this.mOverflowOnly = overflowOnly;
        Resources res = context.getResources();
        this.mMenuMaxWidth = Math.max(res.getDisplayMetrics().widthPixels / 2, res.getDimensionPixelSize(R.dimen.abc_config_prefDialogWidth));
        this.mSubMenuHoverHandler = new Handler();
    }

    @Override // android.support.v7.view.menu.MenuPopup
    public final void setForceShowIcon(boolean forceShow) {
        this.mForceShowIcon = forceShow;
    }

    @Override // android.support.v7.view.menu.ShowableListMenu
    public final void show() {
        if (!isShowing()) {
            for (MenuBuilder menu : this.mPendingMenus) {
                showMenu(menu);
            }
            this.mPendingMenus.clear();
            this.mShownAnchorView = this.mAnchorView;
            if (this.mShownAnchorView != null) {
                boolean addGlobalListener = this.mTreeObserver == null;
                this.mTreeObserver = this.mShownAnchorView.getViewTreeObserver();
                if (addGlobalListener) {
                    this.mTreeObserver.addOnGlobalLayoutListener(this.mGlobalLayoutListener);
                }
            }
        }
    }

    @Override // android.support.v7.view.menu.ShowableListMenu
    public final void dismiss() {
        int length = this.mShowingMenus.size();
        if (length > 0) {
            CascadingMenuInfo[] addedMenus = (CascadingMenuInfo[]) this.mShowingMenus.toArray(new CascadingMenuInfo[length]);
            for (int i = length - 1; i >= 0; i--) {
                CascadingMenuInfo info = addedMenus[i];
                if (info.window.mPopup.isShowing()) {
                    info.window.dismiss();
                }
            }
        }
    }

    @Override // android.view.View.OnKeyListener
    public final boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() != 1 || keyCode != 82) {
            return false;
        }
        dismiss();
        return true;
    }

    private int getInitialMenuPosition() {
        return ViewCompat.getLayoutDirection(this.mAnchorView) == 1 ? 0 : 1;
    }

    @Override // android.support.v7.view.menu.MenuPopup
    public final void addMenu(MenuBuilder menu) {
        menu.addMenuPresenter(this, this.mContext);
        if (isShowing()) {
            showMenu(menu);
        } else {
            this.mPendingMenus.add(menu);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x0190  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x01d0  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x030b  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x0300  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void showMenu(android.support.v7.view.menu.MenuBuilder r30) {
        /*
            Method dump skipped, instructions count: 842
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.view.menu.CascadingMenuPopup.showMenu(android.support.v7.view.menu.MenuBuilder):void");
    }

    @Override // android.support.v7.view.menu.ShowableListMenu
    public final boolean isShowing() {
        return this.mShowingMenus.size() > 0 && this.mShowingMenus.get(0).window.mPopup.isShowing();
    }

    @Override // android.widget.PopupWindow.OnDismissListener
    public final void onDismiss() {
        CascadingMenuInfo dismissedInfo = null;
        int i = 0;
        int count = this.mShowingMenus.size();
        while (true) {
            if (i >= count) {
                break;
            }
            CascadingMenuInfo info = this.mShowingMenus.get(i);
            if (info.window.mPopup.isShowing()) {
                i++;
            } else {
                dismissedInfo = info;
                break;
            }
        }
        if (dismissedInfo != null) {
            dismissedInfo.menu.close(false);
        }
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public final void updateMenuView(boolean cleared) {
        Iterator<CascadingMenuInfo> it = this.mShowingMenus.iterator();
        while (it.hasNext()) {
            toMenuAdapter(it.next().window.mDropDownList.getAdapter()).notifyDataSetChanged();
        }
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public final void setCallback(MenuPresenter.Callback cb) {
        this.mPresenterCallback = cb;
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public final boolean onSubMenuSelected(SubMenuBuilder subMenu) {
        for (CascadingMenuInfo info : this.mShowingMenus) {
            if (subMenu == info.menu) {
                info.window.mDropDownList.requestFocus();
                return true;
            }
        }
        if (subMenu.hasVisibleItems()) {
            addMenu(subMenu);
            if (this.mPresenterCallback == null) {
                return true;
            }
            this.mPresenterCallback.onOpenSubMenu(subMenu);
            return true;
        }
        return false;
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public final boolean flagActionItems() {
        return false;
    }

    @Override // android.support.v7.view.menu.MenuPopup
    public final void setGravity(int dropDownGravity) {
        if (this.mRawDropDownGravity != dropDownGravity) {
            this.mRawDropDownGravity = dropDownGravity;
            this.mDropDownGravity = GravityCompat.getAbsoluteGravity(dropDownGravity, ViewCompat.getLayoutDirection(this.mAnchorView));
        }
    }

    @Override // android.support.v7.view.menu.MenuPopup
    public final void setAnchorView(View anchor) {
        if (this.mAnchorView != anchor) {
            this.mAnchorView = anchor;
            this.mDropDownGravity = GravityCompat.getAbsoluteGravity(this.mRawDropDownGravity, ViewCompat.getLayoutDirection(this.mAnchorView));
        }
    }

    @Override // android.support.v7.view.menu.MenuPopup
    public final void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        this.mOnDismissListener = listener;
    }

    @Override // android.support.v7.view.menu.ShowableListMenu
    public final ListView getListView() {
        if (this.mShowingMenus.isEmpty()) {
            return null;
        }
        return this.mShowingMenus.get(this.mShowingMenus.size() - 1).window.mDropDownList;
    }

    @Override // android.support.v7.view.menu.MenuPopup
    public final void setHorizontalOffset(int x) {
        this.mHasXOffset = true;
        this.mXOffset = x;
    }

    @Override // android.support.v7.view.menu.MenuPopup
    public final void setVerticalOffset(int y) {
        this.mHasYOffset = true;
        this.mYOffset = y;
    }

    @Override // android.support.v7.view.menu.MenuPopup
    public final void setShowTitle(boolean showTitle) {
        this.mShowTitle = showTitle;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CascadingMenuInfo {
        public final MenuBuilder menu;
        public final int position;
        public final MenuPopupWindow window;

        public CascadingMenuInfo(MenuPopupWindow window, MenuBuilder menu, int position) {
            this.window = window;
            this.menu = menu;
            this.position = position;
        }
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public final void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        int size = this.mShowingMenus.size();
        int menuIndex = 0;
        while (true) {
            if (menuIndex < size) {
                if (menu == this.mShowingMenus.get(menuIndex).menu) {
                    break;
                } else {
                    menuIndex++;
                }
            } else {
                menuIndex = -1;
                break;
            }
        }
        if (menuIndex >= 0) {
            int nextMenuIndex = menuIndex + 1;
            if (nextMenuIndex < this.mShowingMenus.size()) {
                this.mShowingMenus.get(nextMenuIndex).menu.close(false);
            }
            CascadingMenuInfo info = this.mShowingMenus.remove(menuIndex);
            info.menu.removeMenuPresenter(this);
            if (this.mShouldCloseImmediately) {
                MenuPopupWindow menuPopupWindow = info.window;
                if (Build.VERSION.SDK_INT >= 23) {
                    menuPopupWindow.mPopup.setExitTransition(null);
                }
                info.window.mPopup.setAnimationStyle(0);
            }
            info.window.dismiss();
            int count = this.mShowingMenus.size();
            if (count > 0) {
                this.mLastPosition = this.mShowingMenus.get(count - 1).position;
            } else {
                this.mLastPosition = getInitialMenuPosition();
            }
            if (count == 0) {
                dismiss();
                if (this.mPresenterCallback != null) {
                    this.mPresenterCallback.onCloseMenu(menu, true);
                }
                if (this.mTreeObserver != null) {
                    if (this.mTreeObserver.isAlive()) {
                        this.mTreeObserver.removeGlobalOnLayoutListener(this.mGlobalLayoutListener);
                    }
                    this.mTreeObserver = null;
                }
                this.mOnDismissListener.onDismiss();
                return;
            }
            if (allMenusAreClosing) {
                this.mShowingMenus.get(0).menu.close(false);
            }
        }
    }
}
