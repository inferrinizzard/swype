package com.google.android.gms.auth.api.credentials.internal;

import android.net.Uri;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzab;

/* loaded from: classes.dex */
public final class zzb {
    public static String zzfm(String str) {
        zzab.zzb(!TextUtils.isEmpty(str), "account type cannot be empty");
        String scheme = Uri.parse(str).getScheme();
        zzab.zzb("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme), "Account type must be an http or https URI");
        return str;
    }
}
