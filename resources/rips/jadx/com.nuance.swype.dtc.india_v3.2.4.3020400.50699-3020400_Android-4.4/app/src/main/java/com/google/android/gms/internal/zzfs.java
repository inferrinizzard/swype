package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.internal.zzfp;
import com.google.android.gms.internal.zzla;
import com.google.api.client.http.ExponentialBackOffPolicy;
import java.util.Map;

@zzin
/* loaded from: classes.dex */
public final class zzfs {
    final Context mContext;
    final Object zzail;
    final VersionInfoParcel zzalo;
    final String zzblr;
    zzkl<zzfp> zzbls;
    private zzkl<zzfp> zzblt;
    zzd zzblu;
    int zzblv;

    /* loaded from: classes.dex */
    static class zza {
        static int zzbmg = ExponentialBackOffPolicy.DEFAULT_MAX_INTERVAL_MILLIS;
        static int zzbmh = 10000;
    }

    /* loaded from: classes.dex */
    public static class zzb<T> implements zzkl<T> {
        @Override // com.google.android.gms.internal.zzkl
        public final void zzd(T t) {
        }
    }

    /* loaded from: classes.dex */
    public static class zzc extends zzlb<zzft> {
        private final Object zzail = new Object();
        final zzd zzbmi;
        private boolean zzbmj;

        public zzc(zzd zzdVar) {
            this.zzbmi = zzdVar;
        }

        public final void release() {
            synchronized (this.zzail) {
                if (this.zzbmj) {
                    return;
                }
                this.zzbmj = true;
                zza(new zzla.zzc<zzft>() { // from class: com.google.android.gms.internal.zzfs.zzc.1
                    @Override // com.google.android.gms.internal.zzla.zzc
                    public final /* synthetic */ void zzd(zzft zzftVar) {
                        zzkd.v("Ending javascript session.");
                        ((zzfu) zzftVar).zzmf();
                    }
                }, new zzla.zzb());
                zza(new zzla.zzc<zzft>() { // from class: com.google.android.gms.internal.zzfs.zzc.2
                    @Override // com.google.android.gms.internal.zzla.zzc
                    public final /* synthetic */ void zzd(zzft zzftVar) {
                        zzkd.v("Releasing engine reference.");
                        zzc.this.zzbmi.zzmc();
                    }
                }, new zzla.zza() { // from class: com.google.android.gms.internal.zzfs.zzc.3
                    @Override // com.google.android.gms.internal.zzla.zza
                    public final void run() {
                        zzc.this.zzbmi.zzmc();
                    }
                });
            }
        }
    }

    /* loaded from: classes.dex */
    public static class zzd extends zzlb<zzfp> {
        zzkl<zzfp> zzblt;
        private final Object zzail = new Object();
        private boolean zzbml = false;
        private int zzbmm = 0;

        public zzd(zzkl<zzfp> zzklVar) {
            this.zzblt = zzklVar;
        }

        private void zzme() {
            synchronized (this.zzail) {
                com.google.android.gms.common.internal.zzab.zzbn(this.zzbmm >= 0);
                if (this.zzbml && this.zzbmm == 0) {
                    zzkd.v("No reference is left (including root). Cleaning up engine.");
                    zza(new zzla.zzc<zzfp>() { // from class: com.google.android.gms.internal.zzfs.zzd.3
                        @Override // com.google.android.gms.internal.zzla.zzc
                        public final /* synthetic */ void zzd(zzfp zzfpVar) {
                            final zzfp zzfpVar2 = zzfpVar;
                            com.google.android.gms.ads.internal.zzu.zzfq();
                            zzkh.runOnUiThread(new Runnable() { // from class: com.google.android.gms.internal.zzfs.zzd.3.1
                                @Override // java.lang.Runnable
                                public final void run() {
                                    zzd.this.zzblt.zzd(zzfpVar2);
                                    zzfpVar2.destroy();
                                }
                            });
                        }
                    }, new zzla.zzb());
                } else {
                    zzkd.v("There are still references to the engine. Not destroying.");
                }
            }
        }

        public final zzc zzmb() {
            final zzc zzcVar = new zzc(this);
            synchronized (this.zzail) {
                zza(new zzla.zzc<zzfp>() { // from class: com.google.android.gms.internal.zzfs.zzd.1
                    @Override // com.google.android.gms.internal.zzla.zzc
                    public final /* synthetic */ void zzd(zzfp zzfpVar) {
                        zzkd.v("Getting a new session for JS Engine.");
                        zzcVar.zzg(zzfpVar.zzly());
                    }
                }, new zzla.zza() { // from class: com.google.android.gms.internal.zzfs.zzd.2
                    @Override // com.google.android.gms.internal.zzla.zza
                    public final void run() {
                        zzkd.v("Rejecting reference for JS Engine.");
                        zzcVar.reject();
                    }
                });
                com.google.android.gms.common.internal.zzab.zzbn(this.zzbmm >= 0);
                this.zzbmm++;
            }
            return zzcVar;
        }

        protected final void zzmc() {
            synchronized (this.zzail) {
                com.google.android.gms.common.internal.zzab.zzbn(this.zzbmm > 0);
                zzkd.v("Releasing 1 reference for JS Engine");
                this.zzbmm--;
                zzme();
            }
        }

        public final void zzmd() {
            synchronized (this.zzail) {
                com.google.android.gms.common.internal.zzab.zzbn(this.zzbmm >= 0);
                zzkd.v("Releasing root reference. JS Engine will be destroyed once other references are released.");
                this.zzbml = true;
                zzme();
            }
        }
    }

    public zzfs(Context context, VersionInfoParcel versionInfoParcel, String str) {
        this.zzail = new Object();
        this.zzblv = 1;
        this.zzblr = str;
        this.mContext = context.getApplicationContext();
        this.zzalo = versionInfoParcel;
        this.zzbls = new zzb();
        this.zzblt = new zzb();
    }

    public zzfs(Context context, VersionInfoParcel versionInfoParcel, String str, zzkl<zzfp> zzklVar, zzkl<zzfp> zzklVar2) {
        this(context, versionInfoParcel, str);
        this.zzbls = zzklVar;
        this.zzblt = zzklVar2;
    }

    public final zzc zzc(zzas zzasVar) {
        zzc zzmb;
        synchronized (this.zzail) {
            if (this.zzblu == null || this.zzblu.getStatus() == -1) {
                this.zzblv = 2;
                this.zzblu = zzb(zzasVar);
                zzmb = this.zzblu.zzmb();
            } else if (this.zzblv == 0) {
                zzmb = this.zzblu.zzmb();
            } else if (this.zzblv == 1) {
                this.zzblv = 2;
                zzb(zzasVar);
                zzmb = this.zzblu.zzmb();
            } else {
                zzmb = this.zzblv == 2 ? this.zzblu.zzmb() : this.zzblu.zzmb();
            }
        }
        return zzmb;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.google.android.gms.internal.zzfs$1, reason: invalid class name */
    /* loaded from: classes.dex */
    public final class AnonymousClass1 implements Runnable {
        final /* synthetic */ zzas zzblw;
        final /* synthetic */ zzd zzblx;

        /* renamed from: com.google.android.gms.internal.zzfs$1$1, reason: invalid class name and collision with other inner class name */
        /* loaded from: classes.dex */
        final class C00791 implements zzfp.zza {
            final /* synthetic */ zzfp zzblz;

            C00791(zzfp zzfpVar) {
                this.zzblz = zzfpVar;
            }

            @Override // com.google.android.gms.internal.zzfp.zza
            public final void zzlz() {
                zzkh.zzclc.postDelayed(new Runnable() { // from class: com.google.android.gms.internal.zzfs.1.1.1
                    @Override // java.lang.Runnable
                    public final void run() {
                        synchronized (zzfs.this.zzail) {
                            if (AnonymousClass1.this.zzblx.getStatus() == -1 || AnonymousClass1.this.zzblx.getStatus() == 1) {
                                return;
                            }
                            AnonymousClass1.this.zzblx.reject();
                            com.google.android.gms.ads.internal.zzu.zzfq();
                            zzkh.runOnUiThread(new Runnable() { // from class: com.google.android.gms.internal.zzfs.1.1.1.1
                                @Override // java.lang.Runnable
                                public final void run() {
                                    C00791.this.zzblz.destroy();
                                }
                            });
                            zzkd.v("Could not receive loaded message in a timely manner. Rejecting.");
                        }
                    }
                }, zza.zzbmh);
            }
        }

        AnonymousClass1(zzas zzasVar, zzd zzdVar) {
            this.zzblw = zzasVar;
            this.zzblx = zzdVar;
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r1v3, types: [com.google.android.gms.internal.zzfs$1$3, T, com.google.android.gms.internal.zzep] */
        @Override // java.lang.Runnable
        public final void run() {
            final zzfr zzfrVar = new zzfr(zzfs.this.mContext, zzfs.this.zzalo, this.zzblw);
            zzfrVar.zza(new C00791(zzfrVar));
            zzfrVar.zza("/jsLoaded", new zzep() { // from class: com.google.android.gms.internal.zzfs.1.2
                @Override // com.google.android.gms.internal.zzep
                public final void zza(zzlh zzlhVar, Map<String, String> map) {
                    synchronized (zzfs.this.zzail) {
                        if (AnonymousClass1.this.zzblx.getStatus() == -1 || AnonymousClass1.this.zzblx.getStatus() == 1) {
                            return;
                        }
                        zzfs.this.zzblv = 0;
                        zzfs.this.zzbls.zzd(zzfrVar);
                        AnonymousClass1.this.zzblx.zzg(zzfrVar);
                        zzfs.this.zzblu = AnonymousClass1.this.zzblx;
                        zzkd.v("Successfully loaded JS Engine.");
                    }
                }
            });
            final zzks zzksVar = new zzks();
            ?? r1 = new zzep() { // from class: com.google.android.gms.internal.zzfs.1.3
                @Override // com.google.android.gms.internal.zzep
                public final void zza(zzlh zzlhVar, Map<String, String> map) {
                    synchronized (zzfs.this.zzail) {
                        zzkd.zzcw("JS Engine is requesting an update");
                        if (zzfs.this.zzblv == 0) {
                            zzkd.zzcw("Starting reload.");
                            zzfs.this.zzblv = 2;
                            zzfs.this.zzb(AnonymousClass1.this.zzblw);
                        }
                        zzfrVar.zzb("/requestReload", (zzep) zzksVar.zzcmu);
                    }
                }
            };
            zzksVar.zzcmu = r1;
            zzfrVar.zza("/requestReload", (zzep) r1);
            if (zzfs.this.zzblr.endsWith(".js")) {
                zzfrVar.zzbg(zzfs.this.zzblr);
            } else if (zzfs.this.zzblr.startsWith("<html>")) {
                zzfrVar.zzbi(zzfs.this.zzblr);
            } else {
                zzfrVar.zzbh(zzfs.this.zzblr);
            }
            zzkh.zzclc.postDelayed(new Runnable() { // from class: com.google.android.gms.internal.zzfs.1.4
                @Override // java.lang.Runnable
                public final void run() {
                    synchronized (zzfs.this.zzail) {
                        if (AnonymousClass1.this.zzblx.getStatus() == -1 || AnonymousClass1.this.zzblx.getStatus() == 1) {
                            return;
                        }
                        AnonymousClass1.this.zzblx.reject();
                        com.google.android.gms.ads.internal.zzu.zzfq();
                        zzkh.runOnUiThread(new Runnable() { // from class: com.google.android.gms.internal.zzfs.1.4.1
                            @Override // java.lang.Runnable
                            public final void run() {
                                zzfrVar.destroy();
                            }
                        });
                        zzkd.v("Could not receive loaded message in a timely manner. Rejecting.");
                    }
                }
            }, zza.zzbmg);
        }
    }

    /* loaded from: classes.dex */
    public static class zze extends zzlb<zzft> {
        private zzc zzbmr;

        public zze(zzc zzcVar) {
            this.zzbmr = zzcVar;
        }

        public final void finalize() {
            this.zzbmr.release();
            this.zzbmr = null;
        }

        @Override // com.google.android.gms.internal.zzlb
        public final int getStatus() {
            return this.zzbmr.getStatus();
        }

        @Override // com.google.android.gms.internal.zzlb
        public final void reject() {
            this.zzbmr.reject();
        }

        @Override // com.google.android.gms.internal.zzlb, com.google.android.gms.internal.zzla
        public final void zza(zzla.zzc<zzft> zzcVar, zzla.zza zzaVar) {
            this.zzbmr.zza(zzcVar, zzaVar);
        }

        @Override // com.google.android.gms.internal.zzlb, com.google.android.gms.internal.zzla
        public final /* synthetic */ void zzg(Object obj) {
            this.zzbmr.zzg((zzft) obj);
        }
    }

    protected final zzd zzb(zzas zzasVar) {
        final zzd zzdVar = new zzd(this.zzblt);
        com.google.android.gms.ads.internal.zzu.zzfq();
        zzkh.runOnUiThread(new AnonymousClass1(zzasVar, zzdVar));
        zzdVar.zza(new zzla.zzc<zzfp>() { // from class: com.google.android.gms.internal.zzfs.2
            @Override // com.google.android.gms.internal.zzla.zzc
            public final /* synthetic */ void zzd(zzfp zzfpVar) {
                synchronized (zzfs.this.zzail) {
                    zzfs.this.zzblv = 0;
                    if (zzfs.this.zzblu != null && zzdVar != zzfs.this.zzblu) {
                        zzkd.v("New JS engine is loaded, marking previous one as destroyable.");
                        zzfs.this.zzblu.zzmd();
                    }
                    zzfs.this.zzblu = zzdVar;
                }
            }
        }, new zzla.zza() { // from class: com.google.android.gms.internal.zzfs.3
            @Override // com.google.android.gms.internal.zzla.zza
            public final void run() {
                synchronized (zzfs.this.zzail) {
                    zzfs.this.zzblv = 1;
                    zzkd.v("Failed loading new engine. Marking new engine destroyable.");
                    zzdVar.zzmd();
                }
            }
        });
        return zzdVar;
    }
}
