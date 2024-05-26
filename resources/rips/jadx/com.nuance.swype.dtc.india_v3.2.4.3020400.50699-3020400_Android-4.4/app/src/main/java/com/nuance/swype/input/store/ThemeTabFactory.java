package com.nuance.swype.input.store;

import android.content.Context;
import android.view.View;
import android.widget.TabHost;

/* loaded from: classes.dex */
public class ThemeTabFactory implements TabHost.TabContentFactory {
    private final Context mContext;

    public ThemeTabFactory(Context context) {
        this.mContext = context;
    }

    @Override // android.widget.TabHost.TabContentFactory
    public View createTabContent(String tag) {
        View v = new View(this.mContext);
        v.setMinimumWidth(0);
        v.setMinimumHeight(0);
        return v;
    }
}
