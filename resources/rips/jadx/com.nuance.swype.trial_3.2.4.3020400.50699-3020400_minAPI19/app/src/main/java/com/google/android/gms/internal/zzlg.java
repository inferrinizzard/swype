package com.google.android.gms.internal;

import android.content.Context;
import android.view.ViewGroup;

@zzin
/* loaded from: classes.dex */
public final class zzlg {
    final Context mContext;
    final zzlh zzbgf;
    com.google.android.gms.ads.internal.overlay.zzk zzbwf;
    final ViewGroup zzcoi;

    public zzlg(Context context, ViewGroup viewGroup, zzlh zzlhVar) {
        this(context, viewGroup, zzlhVar, (byte) 0);
    }

    private zzlg(Context context, ViewGroup viewGroup, zzlh zzlhVar, byte b) {
        this.mContext = context;
        this.zzcoi = viewGroup;
        this.zzbgf = zzlhVar;
        this.zzbwf = null;
    }

    public final void onDestroy() {
        com.google.android.gms.common.internal.zzab.zzhi("onDestroy must be called from the UI thread.");
        if (this.zzbwf != null) {
            this.zzbwf.destroy();
            this.zzcoi.removeView(this.zzbwf);
            this.zzbwf = null;
        }
    }

    public final com.google.android.gms.ads.internal.overlay.zzk zzub() {
        com.google.android.gms.common.internal.zzab.zzhi("getAdVideoUnderlay must be called from the UI thread.");
        return this.zzbwf;
    }
}
