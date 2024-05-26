package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import com.nuance.connect.common.Integers;

/* loaded from: classes.dex */
public class ActionMenuView extends LinearLayoutCompat implements MenuBuilder.ItemInvoker, MenuView {
    private MenuPresenter.Callback mActionMenuPresenterCallback;
    private boolean mFormatItems;
    private int mFormatItemsWidth;
    private int mGeneratedItemPadding;
    MenuBuilder mMenu;
    private MenuBuilder.Callback mMenuBuilderCallback;
    private int mMinCellSize;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private Context mPopupContext;
    private int mPopupTheme;
    ActionMenuPresenter mPresenter;
    private boolean mReserveOverflow;

    /* loaded from: classes.dex */
    public interface ActionMenuChildView {
        boolean needsDividerAfter();

        boolean needsDividerBefore();
    }

    /* loaded from: classes.dex */
    public interface OnMenuItemClickListener {
        boolean onMenuItemClick$1b88ab4c();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup
    public final /* bridge */ /* synthetic */ LinearLayoutCompat.LayoutParams generateDefaultLayoutParams() {
        return generateDefaultLayoutParams();
    }

    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup
    protected /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return generateDefaultLayoutParams();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup
    public final /* bridge */ /* synthetic */ LinearLayoutCompat.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return generateLayoutParams(layoutParams);
    }

    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup
    protected /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return generateLayoutParams(layoutParams);
    }

    public ActionMenuView(Context context) {
        this(context, null);
    }

    public ActionMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBaselineAligned(false);
        float density = context.getResources().getDisplayMetrics().density;
        this.mMinCellSize = (int) (56.0f * density);
        this.mGeneratedItemPadding = (int) (4.0f * density);
        this.mPopupContext = context;
        this.mPopupTheme = 0;
    }

    public void setPopupTheme(int resId) {
        if (this.mPopupTheme != resId) {
            this.mPopupTheme = resId;
            if (resId == 0) {
                this.mPopupContext = getContext();
            } else {
                this.mPopupContext = new ContextThemeWrapper(getContext(), resId);
            }
        }
    }

    public int getPopupTheme() {
        return this.mPopupTheme;
    }

    public void setPresenter(ActionMenuPresenter presenter) {
        this.mPresenter = presenter;
        this.mPresenter.setMenuView(this);
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        if (Build.VERSION.SDK_INT >= 8) {
            super.onConfigurationChanged(newConfig);
        }
        if (this.mPresenter != null) {
            this.mPresenter.updateMenuView(false);
            if (this.mPresenter.isOverflowMenuShowing()) {
                this.mPresenter.hideOverflowMenu();
                this.mPresenter.showOverflowMenu();
            }
        }
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.mOnMenuItemClickListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Removed duplicated region for block: B:134:0x02b6  */
    /* JADX WARN: Removed duplicated region for block: B:138:0x02c5  */
    /* JADX WARN: Removed duplicated region for block: B:178:0x02f9  */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onMeasure(int r39, int r40) {
        /*
            Method dump skipped, instructions count: 936
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.ActionMenuView.onMeasure(int, int):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int measureChildForCells(View child, int cellSize, int cellsRemaining, int parentHeightMeasureSpec, int parentHeightPadding) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int childHeightSize = View.MeasureSpec.getSize(parentHeightMeasureSpec) - parentHeightPadding;
        int childHeightMode = View.MeasureSpec.getMode(parentHeightMeasureSpec);
        int childHeightSpec = View.MeasureSpec.makeMeasureSpec(childHeightSize, childHeightMode);
        ActionMenuItemView itemView = child instanceof ActionMenuItemView ? (ActionMenuItemView) child : null;
        boolean hasText = itemView != null && itemView.hasText();
        int cellsUsed = 0;
        if (cellsRemaining > 0 && (!hasText || cellsRemaining >= 2)) {
            int childWidthSpec = View.MeasureSpec.makeMeasureSpec(cellSize * cellsRemaining, Integers.STATUS_SUCCESS);
            child.measure(childWidthSpec, childHeightSpec);
            int measuredWidth = child.getMeasuredWidth();
            cellsUsed = measuredWidth / cellSize;
            if (measuredWidth % cellSize != 0) {
                cellsUsed++;
            }
            if (hasText && cellsUsed < 2) {
                cellsUsed = 2;
            }
        }
        boolean expandable = !lp.isOverflowButton && hasText;
        lp.expandable = expandable;
        lp.cellsUsed = cellsUsed;
        int targetWidth = cellsUsed * cellSize;
        child.measure(View.MeasureSpec.makeMeasureSpec(targetWidth, 1073741824), childHeightSpec);
        return cellsUsed;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int r;
        int l;
        if (!this.mFormatItems) {
            super.onLayout(changed, left, top, right, bottom);
            return;
        }
        int childCount = getChildCount();
        int midVertical = (bottom - top) / 2;
        int dividerWidth = getDividerWidth();
        int nonOverflowCount = 0;
        int widthRemaining = ((right - left) - getPaddingRight()) - getPaddingLeft();
        boolean hasOverflow = false;
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            if (v.getVisibility() != 8) {
                LayoutParams p = (LayoutParams) v.getLayoutParams();
                if (p.isOverflowButton) {
                    int overflowWidth = v.getMeasuredWidth();
                    if (hasSupportDividerBeforeChildAt(i)) {
                        overflowWidth += dividerWidth;
                    }
                    int height = v.getMeasuredHeight();
                    if (isLayoutRtl) {
                        l = getPaddingLeft() + p.leftMargin;
                        r = l + overflowWidth;
                    } else {
                        r = (getWidth() - getPaddingRight()) - p.rightMargin;
                        l = r - overflowWidth;
                    }
                    int t = midVertical - (height / 2);
                    int b = t + height;
                    v.layout(l, t, r, b);
                    widthRemaining -= overflowWidth;
                    hasOverflow = true;
                } else {
                    int size = v.getMeasuredWidth() + p.leftMargin + p.rightMargin;
                    widthRemaining -= size;
                    hasSupportDividerBeforeChildAt(i);
                    nonOverflowCount++;
                }
            }
        }
        if (childCount == 1 && !hasOverflow) {
            View v2 = getChildAt(0);
            int width = v2.getMeasuredWidth();
            int height2 = v2.getMeasuredHeight();
            int l2 = ((right - left) / 2) - (width / 2);
            int t2 = midVertical - (height2 / 2);
            v2.layout(l2, t2, l2 + width, t2 + height2);
            return;
        }
        int spacerCount = nonOverflowCount - (hasOverflow ? 0 : 1);
        int spacerSize = Math.max(0, spacerCount > 0 ? widthRemaining / spacerCount : 0);
        if (isLayoutRtl) {
            int startRight = getWidth() - getPaddingRight();
            for (int i2 = 0; i2 < childCount; i2++) {
                View v3 = getChildAt(i2);
                LayoutParams lp = (LayoutParams) v3.getLayoutParams();
                if (v3.getVisibility() != 8 && !lp.isOverflowButton) {
                    int startRight2 = startRight - lp.rightMargin;
                    int width2 = v3.getMeasuredWidth();
                    int height3 = v3.getMeasuredHeight();
                    int t3 = midVertical - (height3 / 2);
                    v3.layout(startRight2 - width2, t3, startRight2, t3 + height3);
                    startRight = startRight2 - ((lp.leftMargin + width2) + spacerSize);
                }
            }
            return;
        }
        int startLeft = getPaddingLeft();
        for (int i3 = 0; i3 < childCount; i3++) {
            View v4 = getChildAt(i3);
            LayoutParams lp2 = (LayoutParams) v4.getLayoutParams();
            if (v4.getVisibility() != 8 && !lp2.isOverflowButton) {
                int startLeft2 = startLeft + lp2.leftMargin;
                int width3 = v4.getMeasuredWidth();
                int height4 = v4.getMeasuredHeight();
                int t4 = midVertical - (height4 / 2);
                v4.layout(startLeft2, t4, startLeft2 + width3, t4 + height4);
                startLeft = startLeft2 + lp2.rightMargin + width3 + spacerSize;
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mPresenter == null) {
            return;
        }
        this.mPresenter.dismissPopupMenus();
    }

    public void setOverflowIcon(Drawable icon) {
        getMenu();
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        if (actionMenuPresenter.mOverflowButton != null) {
            actionMenuPresenter.mOverflowButton.setImageDrawable(icon);
        } else {
            actionMenuPresenter.mPendingOverflowIconSet = true;
            actionMenuPresenter.mPendingOverflowIcon = icon;
        }
    }

    public Drawable getOverflowIcon() {
        getMenu();
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        if (actionMenuPresenter.mOverflowButton != null) {
            return actionMenuPresenter.mOverflowButton.getDrawable();
        }
        if (actionMenuPresenter.mPendingOverflowIconSet) {
            return actionMenuPresenter.mPendingOverflowIcon;
        }
        return null;
    }

    public void setOverflowReserved(boolean reserveOverflow) {
        this.mReserveOverflow = reserveOverflow;
    }

    private static LayoutParams generateDefaultLayoutParams() {
        LayoutParams params = new LayoutParams();
        params.gravity = 16;
        return params;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p != null) {
            LayoutParams result = p instanceof LayoutParams ? new LayoutParams((LayoutParams) p) : new LayoutParams(p);
            if (result.gravity <= 0) {
                result.gravity = 16;
                return result;
            }
            return result;
        }
        return generateDefaultLayoutParams();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p != null && (p instanceof LayoutParams);
    }

    public static LayoutParams generateOverflowButtonLayoutParams() {
        LayoutParams result = generateDefaultLayoutParams();
        result.isOverflowButton = true;
        return result;
    }

    @Override // android.support.v7.view.menu.MenuBuilder.ItemInvoker
    public final boolean invokeItem(MenuItemImpl item) {
        return this.mMenu.performItemAction(item, 0);
    }

    public int getWindowAnimations() {
        return 0;
    }

    public Menu getMenu() {
        byte b = 0;
        if (this.mMenu == null) {
            Context context = getContext();
            this.mMenu = new MenuBuilder(context);
            this.mMenu.setCallback(new MenuBuilderCallback(this, b));
            this.mPresenter = new ActionMenuPresenter(context);
            ActionMenuPresenter actionMenuPresenter = this.mPresenter;
            actionMenuPresenter.mReserveOverflow = true;
            actionMenuPresenter.mReserveOverflowSet = true;
            this.mPresenter.mCallback = this.mActionMenuPresenterCallback != null ? this.mActionMenuPresenterCallback : new ActionMenuPresenterCallback(this, b);
            this.mMenu.addMenuPresenter(this.mPresenter, this.mPopupContext);
            this.mPresenter.setMenuView(this);
        }
        return this.mMenu;
    }

    public void setMenuCallbacks(MenuPresenter.Callback pcb, MenuBuilder.Callback mcb) {
        this.mActionMenuPresenterCallback = pcb;
        this.mMenuBuilderCallback = mcb;
    }

    private boolean hasSupportDividerBeforeChildAt(int childIndex) {
        if (childIndex == 0) {
            return false;
        }
        KeyEvent.Callback childAt = getChildAt(childIndex - 1);
        KeyEvent.Callback childAt2 = getChildAt(childIndex);
        boolean result = false;
        if (childIndex < getChildCount() && (childAt instanceof ActionMenuChildView)) {
            result = ((ActionMenuChildView) childAt).needsDividerAfter() | false;
        }
        if (childIndex > 0 && (childAt2 instanceof ActionMenuChildView)) {
            return result | ((ActionMenuChildView) childAt2).needsDividerBefore();
        }
        return result;
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return false;
    }

    public void setExpandedActionViewsExclusive(boolean exclusive) {
        this.mPresenter.mExpandedActionViewsExclusive = exclusive;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MenuBuilderCallback implements MenuBuilder.Callback {
        private MenuBuilderCallback() {
        }

        /* synthetic */ MenuBuilderCallback(ActionMenuView x0, byte b) {
            this();
        }

        @Override // android.support.v7.view.menu.MenuBuilder.Callback
        public final boolean onMenuItemSelected$6cb56673$1b88ab4c() {
            return ActionMenuView.this.mOnMenuItemClickListener != null && ActionMenuView.this.mOnMenuItemClickListener.onMenuItemClick$1b88ab4c();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ActionMenuPresenterCallback implements MenuPresenter.Callback {
        private ActionMenuPresenterCallback() {
        }

        /* synthetic */ ActionMenuPresenterCallback(ActionMenuView x0, byte b) {
            this();
        }

        @Override // android.support.v7.view.menu.MenuPresenter.Callback
        public final void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        }

        @Override // android.support.v7.view.menu.MenuPresenter.Callback
        public final boolean onOpenSubMenu(MenuBuilder subMenu) {
            return false;
        }
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends LinearLayoutCompat.LayoutParams {

        @ViewDebug.ExportedProperty
        public int cellsUsed;

        @ViewDebug.ExportedProperty
        public boolean expandable;
        boolean expanded;

        @ViewDebug.ExportedProperty
        public int extraPixels;

        @ViewDebug.ExportedProperty
        public boolean isOverflowButton;

        @ViewDebug.ExportedProperty
        public boolean preventEdgeOffset;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(ViewGroup.LayoutParams other) {
            super(other);
        }

        public LayoutParams(LayoutParams other) {
            super(other);
            this.isOverflowButton = other.isOverflowButton;
        }

        public LayoutParams() {
            super(-2, -2);
            this.isOverflowButton = false;
        }
    }
}
