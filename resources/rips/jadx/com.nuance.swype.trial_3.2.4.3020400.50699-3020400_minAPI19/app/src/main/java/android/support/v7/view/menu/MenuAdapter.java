package android.support.v7.view.menu;

import android.support.v7.appcompat.R;
import android.support.v7.view.menu.MenuView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class MenuAdapter extends BaseAdapter {
    static final int ITEM_LAYOUT = R.layout.abc_popup_menu_item_layout;
    public MenuBuilder mAdapterMenu;
    private int mExpandedIndex = -1;
    boolean mForceShowIcon;
    private final LayoutInflater mInflater;
    private final boolean mOverflowOnly;

    public MenuAdapter(MenuBuilder menu, LayoutInflater inflater, boolean overflowOnly) {
        this.mOverflowOnly = overflowOnly;
        this.mInflater = inflater;
        this.mAdapterMenu = menu;
        findExpandedIndex();
    }

    @Override // android.widget.Adapter
    public final int getCount() {
        ArrayList<MenuItemImpl> items = this.mOverflowOnly ? this.mAdapterMenu.getNonActionItems() : this.mAdapterMenu.getVisibleItems();
        if (this.mExpandedIndex < 0) {
            return items.size();
        }
        return items.size() - 1;
    }

    @Override // android.widget.Adapter
    public final MenuItemImpl getItem(int position) {
        ArrayList<MenuItemImpl> items = this.mOverflowOnly ? this.mAdapterMenu.getNonActionItems() : this.mAdapterMenu.getVisibleItems();
        if (this.mExpandedIndex >= 0 && position >= this.mExpandedIndex) {
            position++;
        }
        return items.get(position);
    }

    @Override // android.widget.Adapter
    public final long getItemId(int position) {
        return position;
    }

    @Override // android.widget.Adapter
    public final View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = this.mInflater.inflate(ITEM_LAYOUT, parent, false);
        }
        MenuView.ItemView itemView = (MenuView.ItemView) convertView;
        if (this.mForceShowIcon) {
            ((ListMenuItemView) convertView).setForceShowIcon(true);
        }
        itemView.initialize$667f453d(getItem(position));
        return convertView;
    }

    private void findExpandedIndex() {
        MenuItemImpl expandedItem = this.mAdapterMenu.mExpandedItem;
        if (expandedItem != null) {
            ArrayList<MenuItemImpl> items = this.mAdapterMenu.getNonActionItems();
            int count = items.size();
            for (int i = 0; i < count; i++) {
                if (items.get(i) == expandedItem) {
                    this.mExpandedIndex = i;
                    return;
                }
            }
        }
        this.mExpandedIndex = -1;
    }

    @Override // android.widget.BaseAdapter
    public final void notifyDataSetChanged() {
        findExpandedIndex();
        super.notifyDataSetChanged();
    }
}
