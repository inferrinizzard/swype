package com.nuance.swype.plugin;

import android.content.Context;
import java.util.HashSet;

/* loaded from: classes.dex */
public final class MainApkResourceBroker {
    static MainApkResourceBroker sLocalResBroker;
    Context mContext;
    ThemeResourceAccessor mResourceAccessor;
    final HashSet<Integer> mThemeStyleableAttrResIds = new HashSet<>();
    public boolean mHasInited = false;

    MainApkResourceBroker() {
    }

    public static MainApkResourceBroker getInstance() {
        if (sLocalResBroker == null) {
            sLocalResBroker = new MainApkResourceBroker();
        }
        return sLocalResBroker;
    }
}
