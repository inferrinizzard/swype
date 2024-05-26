package com.google.android.gms.ads.internal.request;

import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Looper;
import android.os.RemoteException;
import com.facebook.internal.NativeProtocol;
import com.google.android.gms.ads.internal.request.zzc;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.ads.internal.zzu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzd;
import com.google.android.gms.internal.zzcv;
import com.google.android.gms.internal.zzcw;
import com.google.android.gms.internal.zzdc;
import com.google.android.gms.internal.zzfx;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzio;
import com.google.android.gms.internal.zzip;
import com.google.android.gms.internal.zzja;
import com.google.android.gms.internal.zzjb;
import com.google.android.gms.internal.zzjc;
import com.google.android.gms.internal.zzjr;
import com.google.android.gms.internal.zzjs;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkj;
import com.google.android.gms.internal.zzko;
import com.google.android.gms.internal.zzla;

@zzin
/* loaded from: classes.dex */
public abstract class zzd implements zzc.zza, zzkj<Void> {
    private final Object zzail = new Object();
    private final zzla<AdRequestInfoParcel> zzcaj;
    private final zzc.zza zzcak;

    public zzd(zzla<AdRequestInfoParcel> zzlaVar, zzc.zza zzaVar) {
        this.zzcaj = zzlaVar;
        this.zzcak = zzaVar;
    }

    @Override // com.google.android.gms.internal.zzkj
    public void cancel() {
        zzqw();
    }

    final boolean zza(zzk zzkVar, AdRequestInfoParcel adRequestInfoParcel) {
        try {
            zzkVar.zza(adRequestInfoParcel, new zzg(this));
            return true;
        } catch (RemoteException e) {
            zzkd.zzd("Could not fetch ad response from ad request service.", e);
            zzu.zzft().zzb((Throwable) e, true);
            this.zzcak.zzb(new AdResponseParcel(0));
            return false;
        } catch (NullPointerException e2) {
            zzkd.zzd("Could not fetch ad response from ad request service due to an Exception.", e2);
            zzu.zzft().zzb((Throwable) e2, true);
            this.zzcak.zzb(new AdResponseParcel(0));
            return false;
        } catch (SecurityException e3) {
            zzkd.zzd("Could not fetch ad response from ad request service due to an Exception.", e3);
            zzu.zzft().zzb((Throwable) e3, true);
            this.zzcak.zzb(new AdResponseParcel(0));
            return false;
        } catch (Throwable th) {
            zzkd.zzd("Could not fetch ad response from ad request service due to an Exception.", th);
            zzu.zzft().zzb(th, true);
            this.zzcak.zzb(new AdResponseParcel(0));
            return false;
        }
    }

    @Override // com.google.android.gms.ads.internal.request.zzc.zza
    public void zzb(AdResponseParcel adResponseParcel) {
        synchronized (this.zzail) {
            this.zzcak.zzb(adResponseParcel);
            zzqw();
        }
    }

    @Override // com.google.android.gms.internal.zzkj
    /* renamed from: zzpv, reason: merged with bridge method [inline-methods] */
    public Void zzpy() {
        final zzk zzqx = zzqx();
        if (zzqx == null) {
            this.zzcak.zzb(new AdResponseParcel(0));
            zzqw();
        } else {
            this.zzcaj.zza(new zzla.zzc<AdRequestInfoParcel>() { // from class: com.google.android.gms.ads.internal.request.zzd.1
                @Override // com.google.android.gms.internal.zzla.zzc
                public final /* synthetic */ void zzd(AdRequestInfoParcel adRequestInfoParcel) {
                    if (zzd.this.zza(zzqx, adRequestInfoParcel)) {
                        return;
                    }
                    zzd.this.zzqw();
                }
            }, new zzla.zza() { // from class: com.google.android.gms.ads.internal.request.zzd.2
                @Override // com.google.android.gms.internal.zzla.zza
                public final void run() {
                    zzd.this.zzqw();
                }
            });
        }
        return null;
    }

    public abstract void zzqw();

    public abstract zzk zzqx();

    @zzin
    /* loaded from: classes.dex */
    public static final class zza extends zzd {
        private final Context mContext;

        public zza(Context context, zzla<AdRequestInfoParcel> zzlaVar, zzc.zza zzaVar) {
            super(zzlaVar, zzaVar);
            this.mContext = context;
        }

        @Override // com.google.android.gms.ads.internal.request.zzd, com.google.android.gms.internal.zzkj
        public final /* synthetic */ Void zzpy() {
            return super.zzpy();
        }

        @Override // com.google.android.gms.ads.internal.request.zzd
        public final void zzqw() {
        }

        @Override // com.google.android.gms.ads.internal.request.zzd
        public final zzk zzqx() {
            return zzip.zza(this.mContext, new zzcv((String) zzu.zzfz().zzd(zzdc.zzaxy)), new zzio(new zzjr(), new zzcw(), new zzja(), new zzfx(), new zzjs(), new zzjc(), new zzjb()));
        }
    }

    @zzin
    /* loaded from: classes.dex */
    public static class zzb extends zzd implements zzd.zzb, zzd.zzc {
        private Context mContext;
        private final Object zzail;
        private VersionInfoParcel zzalo;
        private zzla<AdRequestInfoParcel> zzcaj;
        private final zzc.zza zzcak;
        protected zze zzcan;
        private boolean zzcao;

        @Override // com.google.android.gms.common.internal.zzd.zzb
        public void onConnected(Bundle bundle) {
            zzpy();
        }

        @Override // com.google.android.gms.common.internal.zzd.zzb
        public void onConnectionSuspended(int i) {
            zzkd.zzcv("Disconnected from remote ad request service.");
        }

        @Override // com.google.android.gms.ads.internal.request.zzd, com.google.android.gms.internal.zzkj
        public /* synthetic */ Void zzpy() {
            return super.zzpy();
        }

        @Override // com.google.android.gms.ads.internal.request.zzd
        public zzk zzqx() {
            zzk zzkVar;
            synchronized (this.zzail) {
                try {
                    zzkVar = this.zzcan.zzrb();
                } catch (DeadObjectException | IllegalStateException e) {
                    zzkVar = null;
                }
            }
            return zzkVar;
        }

        public zzb(Context context, VersionInfoParcel versionInfoParcel, zzla<AdRequestInfoParcel> zzlaVar, zzc.zza zzaVar) {
            super(zzlaVar, zzaVar);
            Looper mainLooper;
            this.zzail = new Object();
            this.mContext = context;
            this.zzalo = versionInfoParcel;
            this.zzcaj = zzlaVar;
            this.zzcak = zzaVar;
            if (((Boolean) zzu.zzfz().zzd(zzdc.zzayy)).booleanValue()) {
                this.zzcao = true;
                mainLooper = zzu.zzgc().zztq();
            } else {
                mainLooper = context.getMainLooper();
            }
            this.zzcan = new zze(context, mainLooper, this, this, this.zzalo.zzcnl);
            this.zzcan.zzarx();
        }

        @Override // com.google.android.gms.ads.internal.request.zzd
        public void zzqw() {
            synchronized (this.zzail) {
                if (this.zzcan.isConnected() || this.zzcan.isConnecting()) {
                    this.zzcan.disconnect();
                }
                Binder.flushPendingCommands();
                if (this.zzcao) {
                    zzko zzgc = zzu.zzgc();
                    synchronized (zzgc.zzail) {
                        zzab.zzb(zzgc.zzcmn > 0, "Invalid state: release() called more times than expected.");
                        int i = zzgc.zzcmn - 1;
                        zzgc.zzcmn = i;
                        if (i == 0) {
                            zzgc.mHandler.post(new Runnable() { // from class: com.google.android.gms.internal.zzko.1
                                public AnonymousClass1() {
                                }

                                @Override // java.lang.Runnable
                                public final void run() {
                                    synchronized (zzko.this.zzail) {
                                        zzkd.v("Suspending the looper thread");
                                        while (zzko.this.zzcmn == 0) {
                                            try {
                                                zzko.this.zzail.wait();
                                                zzkd.v("Looper thread resumed");
                                            } catch (InterruptedException e) {
                                                zzkd.v("Looper thread interrupted.");
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                    this.zzcao = false;
                }
            }
        }

        @Override // com.google.android.gms.common.internal.zzd.zzc
        public void onConnectionFailed(ConnectionResult connectionResult) {
            zzkd.zzcv("Cannot connect to remote service, fallback to local instance.");
            new zza(this.mContext, this.zzcaj, this.zzcak).zzpy();
            Bundle bundle = new Bundle();
            bundle.putString(NativeProtocol.WEB_DIALOG_ACTION, "gms_connection_failed_fallback_to_local");
            zzu.zzfq().zzb(this.mContext, this.zzalo.zzcs, "gmob-apps", bundle, true);
        }
    }
}
