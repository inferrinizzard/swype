package android.support.v7.view.menu;

import android.content.Context;
import android.graphics.Rect;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.PopupWindow;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class MenuPopup implements MenuPresenter, ShowableListMenu, AdapterView.OnItemClickListener {
    Rect mEpicenterBounds;

    public abstract void addMenu(MenuBuilder menuBuilder);

    public abstract void setAnchorView(View view);

    public abstract void setForceShowIcon(boolean z);

    public abstract void setGravity(int i);

    public abstract void setHorizontalOffset(int i);

    public abstract void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener);

    public abstract void setShowTitle(boolean z);

    public abstract void setVerticalOffset(int i);

    @Override // android.support.v7.view.menu.MenuPresenter
    public final void initForMenu(Context context, MenuBuilder menu) {
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public final boolean expandItemActionView$5c2da31d(MenuItemImpl item) {
        return false;
    }

    @Override // android.support.v7.view.menu.MenuPresenter
    public final boolean collapseItemActionView$5c2da31d(MenuItemImpl item) {
        return false;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListAdapter outerAdapter = (ListAdapter) parent.getAdapter();
        toMenuAdapter(outerAdapter).mAdapterMenu.performItemAction((MenuItem) outerAdapter.getItem(position), 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int measureIndividualMenuWidth(ListAdapter adapter, ViewGroup parent, Context context, int maxAllowedWidth) {
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }
            if (parent == null) {
                parent = new FrameLayout(context);
            }
            itemView = adapter.getView(i, itemView, parent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            int itemWidth = itemView.getMeasuredWidth();
            if (itemWidth < maxAllowedWidth) {
                if (itemWidth > maxWidth) {
                    maxWidth = itemWidth;
                }
            } else {
                return maxAllowedWidth;
            }
        }
        return maxWidth;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static MenuAdapter toMenuAdapter(ListAdapter adapter) {
        return adapter instanceof HeaderViewListAdapter ? (MenuAdapter) ((HeaderViewListAdapter) adapter).getWrappedAdapter() : (MenuAdapter) adapter;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean shouldPreserveIconSpacing(MenuBuilder menu) {
        int count = menu.size();
        for (int i = 0; i < count; i++) {
            MenuItem childItem = menu.getItem(i);
            if (childItem.isVisible() && childItem.getIcon() != null) {
                return true;
            }
        }
        return false;
    }
}
