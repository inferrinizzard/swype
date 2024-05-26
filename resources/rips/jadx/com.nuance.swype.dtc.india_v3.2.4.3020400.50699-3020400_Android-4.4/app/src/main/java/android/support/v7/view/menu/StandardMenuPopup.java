package android.support.v7.view.menu;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.appcompat.R;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.widget.DropDownListView;
import android.support.v7.widget.MenuPopupWindow;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class StandardMenuPopup extends MenuPopup implements MenuPresenter, View.OnKeyListener, AdapterView.OnItemClickListener, PopupWindow.OnDismissListener {
    private final MenuAdapter mAdapter;
    private View mAnchorView;
    private int mContentWidth;
    private final Context mContext;
    private boolean mHasContentWidth;
    private final MenuBuilder mMenu;
    private PopupWindow.OnDismissListener mOnDismissListener;
    private final boolean mOverflowOnly;
    private final MenuPopupWindow mPopup;
    private final int mPopupMaxWidth;
    private final int mPopupStyleAttr;
    private final int mPopupStyleRes;
    private MenuPresenter.Callback mPresenterCallback;
    private boolean mShowTitle;
    private View mShownAnchorView;
    private ViewTreeObserver mTreeObserver;
    private boolean mWasDismissed;
    private final ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: android.support.v7.view.menu.StandardMenuPopup.1
        @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
        public final void onGlobalLayout() {
            if (StandardMenuPopup.this.isShowing() && !StandardMenuPopup.this.mPopup.mModal) {
                View anchor = StandardMenuPopup.this.mShownAnchorView;
                if (anchor != null && anchor.isShown()) {
                    StandardMenuPopup.this.mPopup.show();
                } else {
                    StandardMenuPopup.this.dismiss();
                }
            }
        }
    };
    private int mDropDownGravity = 0;

    public StandardMenuPopup(Context context, MenuBuilder menu, View anchorView, int popupStyleAttr, int popupStyleRes, boolean overflowOnly) {
        this.mContext = context;
        this.mMenu = menu;
        this.mOverflowOnly = overflowOnly;
        LayoutInflater inflater = LayoutInflater.from(context);
        this.mAdapter = new MenuAdapter(menu, inflater, this.mOverflowOnly);
        this.mPopupStyleAttr = popupStyleAttr;
        this.mPopupStyleRes = popupStyleRes;
        Resources res = context.getResources();
        this.mPopupMaxWidth = Math.max(res.getDisplayMetrics().widthPixels / 2, res.getDimensionPixelSize(R.dimen.abc_config_prefDialogWidth));
        this.mAnchorView = anchorView;
        this.mPopup = new MenuPopupWindow(this.mContext, this.mPopupStyleAttr, this.mPopupStyleRes);
        menu.addMenuPresenter(this, context);
    }

    @Override // android.support.v7.view.menu.MenuPopup
    public final void setForceShowIcon(boolean forceShow) {
        this.mAdapter.mForceShowIcon = forceShow;
    }

    @Override // android.support.v7.view.menu.MenuPopup
    public final void setGravity(int gravity) {
        this.mDropDownGravity = gravity;
    }

    @Override // android.support.v7.view.menu.ShowableListMenu
    public final void dismiss() {
        if (isShowing()) {
            this.mPopup.dismiss();
        }
    }

    @Override // android.support.v7.view.menu.MenuPopup
    public final void addMenu(MenuBuilder menu) {
    }

    @Override // android.support.v7.view.menu.ShowableListMenu
    public final boolean isShowing() {
        return !this.mWasDismissed && this.mPopup.mPopup.isShowing();
    }

    @Override // android.widget.PopupWindow.OnDismissListener
    public final void onDismiss() {
        this.mWasDismissed = true;
        this.mMenu.close();
        if (this.mTreeObserver != null) {
            if (!this.mTreeObserver.isAlive()) {
                this.mTreeObserver = this.mShownAnchorView.getViewTreeObserver();
            }
            this.mTreeObserver.removeGlobalOnLayoutListener(this.mGlobalLayoutListener);
            this.mTreeObserver = null;
        }
        if (this.mOnDismissListener != null) {
            this.mOnDismissListener.onDismiss();
        }
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public final void updateMenuView(boolean cleared) {
        this.mHasContentWidth = false;
        if (this.mAdapter != null) {
            this.mAdapter.notifyDataSetChanged();
        }
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public final void setCallback(MenuPresenter.Callback cb) {
        this.mPresenterCallback = cb;
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public final boolean onSubMenuSelected(SubMenuBuilder subMenu) {
        boolean z;
        if (subMenu.hasVisibleItems()) {
            MenuPopupHelper subPopup = new MenuPopupHelper(this.mContext, subMenu, this.mShownAnchorView, this.mOverflowOnly, this.mPopupStyleAttr, this.mPopupStyleRes);
            subPopup.setPresenterCallback(this.mPresenterCallback);
            subPopup.setForceShowIcon(MenuPopup.shouldPreserveIconSpacing(subMenu));
            subPopup.mOnDismissListener = this.mOnDismissListener;
            this.mOnDismissListener = null;
            this.mMenu.close(false);
            int horizontalOffset = this.mPopup.mDropDownHorizontalOffset;
            int verticalOffset = this.mPopup.getVerticalOffset();
            if (subPopup.isShowing()) {
                z = true;
            } else if (subPopup.mAnchorView == null) {
                z = false;
            } else {
                subPopup.showPopup(horizontalOffset, verticalOffset, true, true);
                z = true;
            }
            if (z) {
                if (this.mPresenterCallback != null) {
                    this.mPresenterCallback.onOpenSubMenu(subMenu);
                }
                return true;
            }
        }
        return false;
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public final void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        if (menu == this.mMenu) {
            dismiss();
            if (this.mPresenterCallback != null) {
                this.mPresenterCallback.onCloseMenu(menu, allMenusAreClosing);
            }
        }
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public final boolean flagActionItems() {
        return false;
    }

    @Override // android.support.v7.view.menu.MenuPopup
    public final void setAnchorView(View anchor) {
        this.mAnchorView = anchor;
    }

    @Override // android.view.View.OnKeyListener
    public final boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() != 1 || keyCode != 82) {
            return false;
        }
        dismiss();
        return true;
    }

    @Override // android.support.v7.view.menu.MenuPopup
    public final void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        this.mOnDismissListener = listener;
    }

    @Override // android.support.v7.view.menu.ShowableListMenu
    public final ListView getListView() {
        return this.mPopup.mDropDownList;
    }

    @Override // android.support.v7.view.menu.MenuPopup
    public final void setHorizontalOffset(int x) {
        this.mPopup.mDropDownHorizontalOffset = x;
    }

    @Override // android.support.v7.view.menu.MenuPopup
    public final void setVerticalOffset(int y) {
        this.mPopup.setVerticalOffset(y);
    }

    @Override // android.support.v7.view.menu.MenuPopup
    public final void setShowTitle(boolean showTitle) {
        this.mShowTitle = showTitle;
    }

    @Override // android.support.v7.view.menu.ShowableListMenu
    public final void show() {
        boolean z = true;
        if (!isShowing()) {
            if (this.mWasDismissed || this.mAnchorView == null) {
                z = false;
            } else {
                this.mShownAnchorView = this.mAnchorView;
                this.mPopup.setOnDismissListener(this);
                this.mPopup.mItemClickListener = this;
                this.mPopup.setModal$1385ff();
                View view = this.mShownAnchorView;
                boolean z2 = this.mTreeObserver == null;
                this.mTreeObserver = view.getViewTreeObserver();
                if (z2) {
                    this.mTreeObserver.addOnGlobalLayoutListener(this.mGlobalLayoutListener);
                }
                this.mPopup.mDropDownAnchorView = view;
                this.mPopup.mDropDownGravity = this.mDropDownGravity;
                if (!this.mHasContentWidth) {
                    this.mContentWidth = measureIndividualMenuWidth(this.mAdapter, null, this.mContext, this.mPopupMaxWidth);
                    this.mHasContentWidth = true;
                }
                this.mPopup.setContentWidth(this.mContentWidth);
                this.mPopup.setInputMethodMode$13462e();
                this.mPopup.mEpicenterBounds = this.mEpicenterBounds;
                this.mPopup.show();
                DropDownListView dropDownListView = this.mPopup.mDropDownList;
                dropDownListView.setOnKeyListener(this);
                if (this.mShowTitle && this.mMenu.mHeaderTitle != null) {
                    FrameLayout frameLayout = (FrameLayout) LayoutInflater.from(this.mContext).inflate(R.layout.abc_popup_menu_header_item_layout, (ViewGroup) dropDownListView, false);
                    TextView textView = (TextView) frameLayout.findViewById(android.R.id.title);
                    if (textView != null) {
                        textView.setText(this.mMenu.mHeaderTitle);
                    }
                    frameLayout.setEnabled(false);
                    dropDownListView.addHeaderView(frameLayout, null, false);
                }
                this.mPopup.setAdapter(this.mAdapter);
                this.mPopup.show();
            }
        }
        if (!z) {
            throw new IllegalStateException("StandardMenuPopup cannot be used without an anchor");
        }
    }
}
