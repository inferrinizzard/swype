package com.nuance.android.compat;

import android.content.Context;
import android.os.Build;
import android.os.UserManager;

/* loaded from: classes.dex */
public class UserManagerCompat {
    public static boolean isUserUnlocked(Context ctx) {
        UserManager usmgr;
        return Build.VERSION.SDK_INT < 24 || ((usmgr = (UserManager) ctx.getSystemService("user")) != null && usmgr.isUserUnlocked());
    }
}
