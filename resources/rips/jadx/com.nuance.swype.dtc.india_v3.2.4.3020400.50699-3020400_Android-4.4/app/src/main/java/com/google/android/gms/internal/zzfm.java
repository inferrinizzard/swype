package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.client.zzp;
import com.google.android.gms.ads.internal.client.zzq;
import com.google.android.gms.ads.internal.client.zzw;
import com.google.android.gms.ads.internal.reward.client.zzd;
import com.google.android.gms.internal.zzdo;
import com.google.android.gms.internal.zzho;
import java.util.LinkedList;

/* JADX INFO: Access modifiers changed from: package-private */
@zzin
/* loaded from: classes.dex */
public final class zzfm {
    final String zzaln;
    final LinkedList<zza> zzbkr;
    AdRequestParcel zzbks;
    final int zzbkt;
    boolean zzbku;

    /* JADX INFO: Access modifiers changed from: package-private */
    public zzfm(AdRequestParcel adRequestParcel, String str, int i) {
        com.google.android.gms.common.internal.zzab.zzy(adRequestParcel);
        com.google.android.gms.common.internal.zzab.zzy(str);
        this.zzbkr = new LinkedList<>();
        this.zzbks = adRequestParcel;
        this.zzaln = str;
        this.zzbkt = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final zza zzm(AdRequestParcel adRequestParcel) {
        if (adRequestParcel != null) {
            this.zzbks = adRequestParcel;
        }
        return this.zzbkr.remove();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class zza {
        com.google.android.gms.ads.internal.zzl zzbkv;
        AdRequestParcel zzbkw;
        zzfi zzbkx;
        long zzbky;
        boolean zzbkz;
        boolean zzbla;

        /* JADX INFO: Access modifiers changed from: package-private */
        public zza(zzfm zzfmVar, zzfh zzfhVar, AdRequestParcel adRequestParcel) {
            this(zzfhVar);
            this.zzbkw = adRequestParcel;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public zza(zzfh zzfhVar) {
            this.zzbkv = new com.google.android.gms.ads.internal.zzl(zzfhVar.mContext.getApplicationContext(), new AdSizeParcel(), zzfm.this.zzaln, zzfhVar.zzajz, zzfhVar.zzalo, zzfhVar.zzajv);
            this.zzbkx = new zzfi();
            final zzfi zzfiVar = this.zzbkx;
            com.google.android.gms.ads.internal.zzl zzlVar = this.zzbkv;
            zzlVar.zza(new zzq.zza() { // from class: com.google.android.gms.internal.zzfi.1
                @Override // com.google.android.gms.ads.internal.client.zzq
                public final void onAdClosed() throws RemoteException {
                    zzfi.this.zzalc.add(new zza() { // from class: com.google.android.gms.internal.zzfi.1.1
                        @Override // com.google.android.gms.internal.zzfi.zza
                        public final void zzb(zzfj zzfjVar) throws RemoteException {
                            if (zzfjVar.zzalf != null) {
                                zzfjVar.zzalf.onAdClosed();
                            }
                            com.google.android.gms.ads.internal.zzu.zzgb().zzlo();
                        }
                    });
                }

                @Override // com.google.android.gms.ads.internal.client.zzq
                public final void onAdFailedToLoad(final int i) throws RemoteException {
                    zzfi.this.zzalc.add(new zza() { // from class: com.google.android.gms.internal.zzfi.1.2
                        @Override // com.google.android.gms.internal.zzfi.zza
                        public final void zzb(zzfj zzfjVar) throws RemoteException {
                            if (zzfjVar.zzalf != null) {
                                zzfjVar.zzalf.onAdFailedToLoad(i);
                            }
                        }
                    });
                    zzkd.v("Pooled interstitial failed to load.");
                }

                @Override // com.google.android.gms.ads.internal.client.zzq
                public final void onAdLeftApplication() throws RemoteException {
                    zzfi.this.zzalc.add(new zza() { // from class: com.google.android.gms.internal.zzfi.1.3
                        @Override // com.google.android.gms.internal.zzfi.zza
                        public final void zzb(zzfj zzfjVar) throws RemoteException {
                            if (zzfjVar.zzalf != null) {
                                zzfjVar.zzalf.onAdLeftApplication();
                            }
                        }
                    });
                }

                @Override // com.google.android.gms.ads.internal.client.zzq
                public final void onAdLoaded() throws RemoteException {
                    zzfi.this.zzalc.add(new zza() { // from class: com.google.android.gms.internal.zzfi.1.4
                        @Override // com.google.android.gms.internal.zzfi.zza
                        public final void zzb(zzfj zzfjVar) throws RemoteException {
                            if (zzfjVar.zzalf != null) {
                                zzfjVar.zzalf.onAdLoaded();
                            }
                        }
                    });
                    zzkd.v("Pooled interstitial loaded.");
                }

                @Override // com.google.android.gms.ads.internal.client.zzq
                public final void onAdOpened() throws RemoteException {
                    zzfi.this.zzalc.add(new zza() { // from class: com.google.android.gms.internal.zzfi.1.5
                        @Override // com.google.android.gms.internal.zzfi.zza
                        public final void zzb(zzfj zzfjVar) throws RemoteException {
                            if (zzfjVar.zzalf != null) {
                                zzfjVar.zzalf.onAdOpened();
                            }
                        }
                    });
                }
            });
            zzlVar.zza(new zzw.zza() { // from class: com.google.android.gms.internal.zzfi.2
                @Override // com.google.android.gms.ads.internal.client.zzw
                public final void onAppEvent(final String str, final String str2) throws RemoteException {
                    zzfi.this.zzalc.add(new zza() { // from class: com.google.android.gms.internal.zzfi.2.1
                        @Override // com.google.android.gms.internal.zzfi.zza
                        public final void zzb(zzfj zzfjVar) throws RemoteException {
                            if (zzfjVar.zzbkh != null) {
                                zzfjVar.zzbkh.onAppEvent(str, str2);
                            }
                        }
                    });
                }
            });
            zzlVar.zza(new zzho.zza() { // from class: com.google.android.gms.internal.zzfi.3
                @Override // com.google.android.gms.internal.zzho
                public final void zza(final zzhn zzhnVar) throws RemoteException {
                    zzfi.this.zzalc.add(new zza() { // from class: com.google.android.gms.internal.zzfi.3.1
                        @Override // com.google.android.gms.internal.zzfi.zza
                        public final void zzb(zzfj zzfjVar) throws RemoteException {
                            if (zzfjVar.zzbki != null) {
                                zzfjVar.zzbki.zza(zzhnVar);
                            }
                        }
                    });
                }
            });
            zzlVar.zza(new zzdo.zza() { // from class: com.google.android.gms.internal.zzfi.4
                @Override // com.google.android.gms.internal.zzdo
                public final void zza(final zzdn zzdnVar) throws RemoteException {
                    zzfi.this.zzalc.add(new zza() { // from class: com.google.android.gms.internal.zzfi.4.1
                        @Override // com.google.android.gms.internal.zzfi.zza
                        public final void zzb(zzfj zzfjVar) throws RemoteException {
                            if (zzfjVar.zzbkj != null) {
                                zzfjVar.zzbkj.zza(zzdnVar);
                            }
                        }
                    });
                }
            });
            zzlVar.zza(new zzp.zza() { // from class: com.google.android.gms.internal.zzfi.5
                @Override // com.google.android.gms.ads.internal.client.zzp
                public final void onAdClicked() throws RemoteException {
                    zzfi.this.zzalc.add(new zza() { // from class: com.google.android.gms.internal.zzfi.5.1
                        @Override // com.google.android.gms.internal.zzfi.zza
                        public final void zzb(zzfj zzfjVar) throws RemoteException {
                            if (zzfjVar.zzbkk != null) {
                                zzfjVar.zzbkk.onAdClicked();
                            }
                        }
                    });
                }
            });
            zzlVar.zza(new zzd.zza() { // from class: com.google.android.gms.internal.zzfi.6
                @Override // com.google.android.gms.ads.internal.reward.client.zzd
                public final void onRewardedVideoAdLoaded() throws RemoteException {
                    zzfi.this.zzalc.add(new zza() { // from class: com.google.android.gms.internal.zzfi.6.1
                        @Override // com.google.android.gms.internal.zzfi.zza
                        public final void zzb(zzfj zzfjVar) throws RemoteException {
                            if (zzfjVar.zzbkl != null) {
                                zzfjVar.zzbkl.onRewardedVideoAdLoaded();
                            }
                        }
                    });
                }

                @Override // com.google.android.gms.ads.internal.reward.client.zzd
                public final void onRewardedVideoAdOpened() throws RemoteException {
                    zzfi.this.zzalc.add(new zza() { // from class: com.google.android.gms.internal.zzfi.6.2
                        @Override // com.google.android.gms.internal.zzfi.zza
                        public final void zzb(zzfj zzfjVar) throws RemoteException {
                            if (zzfjVar.zzbkl != null) {
                                zzfjVar.zzbkl.onRewardedVideoAdOpened();
                            }
                        }
                    });
                }

                @Override // com.google.android.gms.ads.internal.reward.client.zzd
                public final void onRewardedVideoStarted() throws RemoteException {
                    zzfi.this.zzalc.add(new zza() { // from class: com.google.android.gms.internal.zzfi.6.3
                        @Override // com.google.android.gms.internal.zzfi.zza
                        public final void zzb(zzfj zzfjVar) throws RemoteException {
                            if (zzfjVar.zzbkl != null) {
                                zzfjVar.zzbkl.onRewardedVideoStarted();
                            }
                        }
                    });
                }

                @Override // com.google.android.gms.ads.internal.reward.client.zzd
                public final void onRewardedVideoAdClosed() throws RemoteException {
                    zzfi.this.zzalc.add(new zza() { // from class: com.google.android.gms.internal.zzfi.6.4
                        @Override // com.google.android.gms.internal.zzfi.zza
                        public final void zzb(zzfj zzfjVar) throws RemoteException {
                            if (zzfjVar.zzbkl != null) {
                                zzfjVar.zzbkl.onRewardedVideoAdClosed();
                            }
                        }
                    });
                }

                @Override // com.google.android.gms.ads.internal.reward.client.zzd
                public final void zza(final com.google.android.gms.ads.internal.reward.client.zza zzaVar) throws RemoteException {
                    zzfi.this.zzalc.add(new zza() { // from class: com.google.android.gms.internal.zzfi.6.5
                        @Override // com.google.android.gms.internal.zzfi.zza
                        public final void zzb(zzfj zzfjVar) throws RemoteException {
                            if (zzfjVar.zzbkl != null) {
                                zzfjVar.zzbkl.zza(zzaVar);
                            }
                        }
                    });
                }

                @Override // com.google.android.gms.ads.internal.reward.client.zzd
                public final void onRewardedVideoAdLeftApplication() throws RemoteException {
                    zzfi.this.zzalc.add(new zza() { // from class: com.google.android.gms.internal.zzfi.6.6
                        @Override // com.google.android.gms.internal.zzfi.zza
                        public final void zzb(zzfj zzfjVar) throws RemoteException {
                            if (zzfjVar.zzbkl != null) {
                                zzfjVar.zzbkl.onRewardedVideoAdLeftApplication();
                            }
                        }
                    });
                }

                @Override // com.google.android.gms.ads.internal.reward.client.zzd
                public final void onRewardedVideoAdFailedToLoad(final int i) throws RemoteException {
                    zzfi.this.zzalc.add(new zza() { // from class: com.google.android.gms.internal.zzfi.6.7
                        @Override // com.google.android.gms.internal.zzfi.zza
                        public final void zzb(zzfj zzfjVar) throws RemoteException {
                            if (zzfjVar.zzbkl != null) {
                                zzfjVar.zzbkl.onRewardedVideoAdFailedToLoad(i);
                            }
                        }
                    });
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final void zzlv() {
            if (this.zzbkz) {
                return;
            }
            AdRequestParcel adRequestParcel = this.zzbkw != null ? this.zzbkw : zzfm.this.zzbks;
            Parcel obtain = Parcel.obtain();
            adRequestParcel.writeToParcel(obtain, 0);
            obtain.setDataPosition(0);
            AdRequestParcel adRequestParcel2 = (AdRequestParcel) AdRequestParcel.CREATOR.createFromParcel(obtain);
            obtain.recycle();
            Bundle zzi = zzfk.zzi(adRequestParcel2);
            if (zzi == null) {
                zzi = new Bundle();
                adRequestParcel2.zzatw.putBundle("com.google.ads.mediation.admob.AdMobAdapter", zzi);
            }
            zzi.putBoolean("_skipMediation", true);
            this.zzbla = this.zzbkv.zzb(adRequestParcel2);
            this.zzbkz = true;
            this.zzbky = com.google.android.gms.ads.internal.zzu.zzfu().currentTimeMillis();
        }
    }
}
