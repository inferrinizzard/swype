package com.google.android.gms.ads.internal.client;

import android.os.RemoteException;
import com.google.android.gms.ads.internal.client.zzr;
import com.google.android.gms.ads.internal.client.zzs;
import com.google.android.gms.ads.internal.formats.NativeAdOptionsParcel;
import com.google.android.gms.internal.zzeb;
import com.google.android.gms.internal.zzec;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzee;

/* loaded from: classes.dex */
public class zzaj extends zzs.zza {
    private zzq zzalf;

    /* loaded from: classes.dex */
    private class zza extends zzr.zza {
        private zza() {
        }

        /* synthetic */ zza(zzaj zzajVar, byte b) {
            this();
        }

        @Override // com.google.android.gms.ads.internal.client.zzr
        public final String getMediationAdapterClassName() throws RemoteException {
            return null;
        }

        @Override // com.google.android.gms.ads.internal.client.zzr
        public final boolean isLoading() throws RemoteException {
            return false;
        }

        @Override // com.google.android.gms.ads.internal.client.zzr
        public final void zzf(AdRequestParcel adRequestParcel) throws RemoteException {
            com.google.android.gms.ads.internal.util.client.zzb.e("This app is using a lightweight version of the Google Mobile Ads SDK that requires the latest Google Play services to be installed, but Google Play services is either missing or out of date.");
            com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.ads.internal.client.zzaj.zza.1
                @Override // java.lang.Runnable
                public final void run() {
                    if (zzaj.this.zzalf != null) {
                        try {
                            zzaj.this.zzalf.onAdFailedToLoad(1);
                        } catch (RemoteException e) {
                            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not notify onAdFailedToLoad event.", e);
                        }
                    }
                }
            });
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzs
    public void zza(NativeAdOptionsParcel nativeAdOptionsParcel) throws RemoteException {
    }

    @Override // com.google.android.gms.ads.internal.client.zzs
    public void zza(zzeb zzebVar) throws RemoteException {
    }

    @Override // com.google.android.gms.ads.internal.client.zzs
    public void zza(zzec zzecVar) throws RemoteException {
    }

    @Override // com.google.android.gms.ads.internal.client.zzs
    public void zza(String str, zzee zzeeVar, zzed zzedVar) throws RemoteException {
    }

    @Override // com.google.android.gms.ads.internal.client.zzs
    public void zzb(zzq zzqVar) throws RemoteException {
        this.zzalf = zzqVar;
    }

    @Override // com.google.android.gms.ads.internal.client.zzs
    public void zzb(zzy zzyVar) throws RemoteException {
    }

    @Override // com.google.android.gms.ads.internal.client.zzs
    public zzr zzes() throws RemoteException {
        return new zza(this, (byte) 0);
    }
}
