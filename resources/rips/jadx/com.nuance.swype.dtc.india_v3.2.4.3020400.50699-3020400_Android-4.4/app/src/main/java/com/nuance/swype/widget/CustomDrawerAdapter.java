package com.nuance.swype.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nuance.swype.input.R;
import com.nuance.swype.util.LogManager;
import java.util.List;

/* loaded from: classes.dex */
public final class CustomDrawerAdapter extends ArrayAdapter<DrawerItem> {
    protected static final LogManager.Log log = LogManager.getLog("CustomDrawerAdapter");
    Context context;
    List<DrawerItem> drawerItemList;
    int layoutResID;

    public CustomDrawerAdapter(Context context, int layoutResourceID, List<DrawerItem> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.drawerItemList = listItems;
        this.layoutResID = layoutResourceID;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public final View getView(int position, View convertView, ViewGroup parent) {
        DrawerItemHolder drawerHolder;
        byte b = 0;
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService("layout_inflater");
            drawerHolder = new DrawerItemHolder(b);
            view = inflater.inflate(this.layoutResID, parent, false);
            drawerHolder.drawerItem = (LinearLayout) view.findViewById(R.id.drawer_item);
            drawerHolder.ItemName = (TextView) view.findViewById(R.id.drawer_itemName);
            drawerHolder.icon = (ImageView) view.findViewById(R.id.drawer_icon);
            drawerHolder.divider = view.findViewById(R.id.divider);
            view.setTag(drawerHolder);
        } else {
            drawerHolder = (DrawerItemHolder) view.getTag();
        }
        DrawerItem dItem = this.drawerItemList.get(position);
        drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(dItem.imgResID));
        drawerHolder.ItemName.setText(dItem.ItemName);
        log.d("getView...position: ", Integer.valueOf(position), "...name: ", dItem.ItemName);
        if (dItem.ItemName.equals(this.context.getString(R.string.drawer_divider_text))) {
            log.d("getView...drawerHolder.ItemName: ", drawerHolder.ItemName, "...drawerHolder.icon: ", drawerHolder.icon, "...drawerHolder.divider: ", drawerHolder.divider);
            drawerHolder.drawerItem.setVisibility(8);
            drawerHolder.divider.setVisibility(0);
        }
        return view;
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public final boolean isEnabled(int position) {
        DrawerItem dItem = this.drawerItemList.get(position);
        return (dItem == null || dItem.ItemName.equals(this.context.getString(R.string.drawer_divider_text))) ? false : true;
    }

    /* loaded from: classes.dex */
    private static class DrawerItemHolder {
        TextView ItemName;
        View divider;
        LinearLayout drawerItem;
        ImageView icon;

        private DrawerItemHolder() {
        }

        /* synthetic */ DrawerItemHolder(byte b) {
            this();
        }
    }
}
