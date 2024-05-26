package com.nuance.swype.input;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.RadioButton;
import android.widget.TextView;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class CloudNetworkAdapter extends BaseAdapter {
    private final Context mContext;
    private String mCurrentNetworkKey;
    private final List<String> mKeyList;
    private final Map<String, String> mNetWorkOpt;

    public CloudNetworkAdapter(Context context, Map<String, String> NetWorkOpt, List<String> keySet, String currentNetworkKey) {
        this.mContext = context;
        this.mNetWorkOpt = NetWorkOpt;
        this.mKeyList = keySet;
        this.mCurrentNetworkKey = currentNetworkKey;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mNetWorkOpt.size();
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean isEnabled(int position) {
        return true;
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return this.mKeyList.get(position);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return 0L;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.chinese_cloud_item, parent, false);
            TextView label = (TextView) convertView.findViewById(android.R.id.text1);
            RadioButton button = (RadioButton) convertView.findViewById(android.R.id.button1);
            convertView.setTag(new Holder(label, button));
        }
        String label2 = this.mNetWorkOpt.get(this.mKeyList.get(position));
        boolean checked = this.mCurrentNetworkKey.equals(this.mKeyList.get(position));
        Holder holder = (Holder) convertView.getTag();
        holder.label.setText(label2);
        holder.checkable.setChecked(checked);
        return convertView;
    }

    /* loaded from: classes.dex */
    private static class Holder {
        public final Checkable checkable;
        public final TextView label;

        public Holder(TextView label, Checkable button) {
            this.label = label;
            this.checkable = button;
        }
    }
}
