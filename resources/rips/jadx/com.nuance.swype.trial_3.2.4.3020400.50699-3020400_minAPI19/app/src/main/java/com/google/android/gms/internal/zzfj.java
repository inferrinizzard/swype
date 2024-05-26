package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.ads.internal.client.zzq;

/* JADX INFO: Access modifiers changed from: package-private */
@zzin
/* loaded from: classes.dex */
public final class zzfj {
    com.google.android.gms.ads.internal.client.zzq zzalf;
    com.google.android.gms.ads.internal.client.zzw zzbkh;
    zzho zzbki;
    zzdo zzbkj;
    com.google.android.gms.ads.internal.client.zzp zzbkk;
    com.google.android.gms.ads.internal.reward.client.zzd zzbkl;

    /* loaded from: classes.dex */
    private class zza extends zzq.zza {
        com.google.android.gms.ads.internal.client.zzq zzbkm;

        zza(com.google.android.gms.ads.internal.client.zzq zzqVar) {
            this.zzbkm = zzqVar;
        }

        @Override // com.google.android.gms.ads.internal.client.zzq
        public final void onAdClosed() throws RemoteException {
            this.zzbkm.onAdClosed();
            com.google.android.gms.ads.internal.zzu.zzgb().zzlo();
        }

        @Override // com.google.android.gms.ads.internal.client.zzq
        public final void onAdFailedToLoad(int i) throws RemoteException {
            this.zzbkm.onAdFailedToLoad(i);
        }

        @Override // com.google.android.gms.ads.internal.client.zzq
        public final void onAdLeftApplication() throws RemoteException {
            this.zzbkm.onAdLeftApplication();
        }

        @Override // com.google.android.gms.ads.internal.client.zzq
        public final void onAdLoaded() throws RemoteException {
            this.zzbkm.onAdLoaded();
        }

        @Override // com.google.android.gms.ads.internal.client.zzq
        public final void onAdOpened() throws RemoteException {
            this.zzbkm.onAdOpened();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void zzc(com.google.android.gms.ads.internal.zzl zzlVar) {
        if (this.zzalf != null) {
            zzlVar.zza(new zza(this.zzalf));
        }
        if (this.zzbkh != null) {
            zzlVar.zza(this.zzbkh);
        }
        if (this.zzbki != null) {
            zzlVar.zza(this.zzbki);
        }
        if (this.zzbkj != null) {
            zzlVar.zza(this.zzbkj);
        }
        if (this.zzbkk != null) {
            zzlVar.zza(this.zzbkk);
        }
        if (this.zzbkl != null) {
            zzlVar.zza(this.zzbkl);
        }
    }
}
