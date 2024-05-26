package com.localytics.android;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.localytics.android.Localytics;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class InAppMessagesAdapter extends BaseAdapter {
    private final Context mContext;
    private InAppManager mInAppManager;
    private final List<MarketingMessage> mInAppMessages = new ArrayList();

    /* JADX INFO: Access modifiers changed from: package-private */
    public InAppMessagesAdapter(Context context, InAppManager manager) {
        this.mContext = context;
        this.mInAppManager = manager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r1v2, types: [com.localytics.android.InAppMessagesAdapter$2] */
    public final boolean updateDataSet() {
        this.mInAppMessages.clear();
        final FutureTask<Boolean> fTask = new FutureTask<>(new Callable<Boolean>() { // from class: com.localytics.android.InAppMessagesAdapter.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public Boolean call() throws Exception {
                boolean updated = false;
                for (MarketingMessage inApp : InAppMessagesAdapter.this.mInAppManager.getInAppMessages()) {
                    updated = true;
                    InAppMessagesAdapter.this.mInAppMessages.add(inApp);
                }
                return Boolean.valueOf(updated);
            }
        });
        new Thread() { // from class: com.localytics.android.InAppMessagesAdapter.2
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    fTask.run();
                } catch (Exception e) {
                    Localytics.Log.w("Caught an exception", e);
                }
            }
        }.start();
        try {
            return fTask.get().booleanValue();
        } catch (Exception e) {
            return false;
        }
    }

    @Override // android.widget.Adapter
    public final int getCount() {
        return this.mInAppMessages.size();
    }

    @Override // android.widget.Adapter
    public final Map<String, Object> getItem(int position) {
        return this.mInAppMessages.get(position);
    }

    @Override // android.widget.Adapter
    public final long getItemId(int position) {
        return ((Integer) this.mInAppMessages.get(position).get("campaign_id")).intValue();
    }

    @Override // android.widget.Adapter
    public final View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LinearLayout linearLayout = new LinearLayout(this.mContext);
            linearLayout.setLayoutParams(new AbsListView.LayoutParams(-1, -2));
            linearLayout.setOrientation(0);
            LinearLayout layout = new LinearLayout(this.mContext);
            layout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
            layout.setGravity(16);
            layout.setOrientation(1);
            int padding = (int) ((this.mContext.getResources().getDisplayMetrics().density * 8.0f) + 0.5f);
            layout.setPadding(padding << 1, padding, padding << 1, padding);
            linearLayout.addView(layout);
            TextView ruleId = new TextView(this.mContext);
            ruleId.setId(1);
            ruleId.setTextSize(16.0f);
            ruleId.setTextColor(-16777216);
            TextView ruleName = new TextView(this.mContext);
            ruleName.setId(2);
            ruleName.setTextSize(24.0f);
            ruleName.setTextColor(-16777216);
            layout.addView(ruleName);
            layout.addView(ruleId);
            view = linearLayout;
        }
        TextView ruleId2 = (TextView) view.findViewById(1);
        TextView ruleName2 = (TextView) view.findViewById(2);
        ruleId2.setText(String.format("Campaign ID: %d", Long.valueOf(getItemId(position))));
        ruleName2.setText((String) getItem(position).get("rule_name_non_unique"));
        return view;
    }
}
