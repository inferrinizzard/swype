package com.google.android.gms.ads.mediation.customevent;

import com.google.ads.mediation.NetworkExtras;
import java.util.HashMap;

@Deprecated
/* loaded from: classes.dex */
public final class CustomEventExtras implements NetworkExtras {
    private final HashMap<String, Object> zzcqw = new HashMap<>();

    public final Object getExtra(String str) {
        return this.zzcqw.get(str);
    }

    public final void setExtra(String str, Object obj) {
        this.zzcqw.put(str, obj);
    }
}
