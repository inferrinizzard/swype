package com.nuance.swype.inapp;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class InstalledList {
    public Map<String, ?> mInstalledMap = new HashMap();
    SharedPreferences mSharedPreferences;

    public InstalledList(Context context) {
        Context mAppContext = context.getApplicationContext();
        this.mSharedPreferences = mAppContext.getSharedPreferences("installed_themes", 0);
        loadAll();
    }

    public final void loadAll() {
        if (!this.mInstalledMap.isEmpty()) {
            this.mInstalledMap.clear();
        }
        this.mInstalledMap = this.mSharedPreferences.getAll();
    }
}
