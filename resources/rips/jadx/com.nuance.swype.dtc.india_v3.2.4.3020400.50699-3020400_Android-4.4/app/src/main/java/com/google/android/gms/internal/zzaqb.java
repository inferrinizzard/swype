package com.google.android.gms.internal;

import android.content.ComponentName;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsServiceConnection;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public final class zzaqb extends CustomTabsServiceConnection {
    private WeakReference<zzaqc> bkx;

    public zzaqb(zzaqc zzaqcVar) {
        this.bkx = new WeakReference<>(zzaqcVar);
    }

    @Override // android.support.customtabs.CustomTabsServiceConnection
    public final void onCustomTabsServiceConnected$51e7d8cd(CustomTabsClient customTabsClient) {
        zzaqc zzaqcVar = this.bkx.get();
        if (zzaqcVar != null) {
            zzaqcVar.zza(customTabsClient);
        }
    }

    @Override // android.content.ServiceConnection
    public final void onServiceDisconnected(ComponentName componentName) {
        zzaqc zzaqcVar = this.bkx.get();
        if (zzaqcVar != null) {
            zzaqcVar.zzkm();
        }
    }
}
