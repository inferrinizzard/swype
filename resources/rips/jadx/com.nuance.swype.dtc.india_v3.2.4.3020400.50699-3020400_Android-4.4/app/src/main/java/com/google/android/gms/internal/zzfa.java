package com.google.android.gms.internal;

import android.os.RemoteException;
import java.util.Map;

@zzin
/* loaded from: classes.dex */
final class zzfa implements zzep {
    @Override // com.google.android.gms.internal.zzep
    public final void zza(zzlh zzlhVar, Map<String, String> map) {
        zzlm zzlmVar;
        int i;
        if (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbbb)).booleanValue()) {
            zzlm zzut = zzlhVar.zzut();
            if (zzut == null) {
                try {
                    zzlm zzlmVar2 = new zzlm(zzlhVar, Float.parseFloat(map.get("duration")));
                    zzlhVar.zza(zzlmVar2);
                    zzlmVar = zzlmVar2;
                } catch (NullPointerException | NumberFormatException e) {
                    zzkd.zzb("Unable to parse videoMeta message.", e);
                    com.google.android.gms.ads.internal.zzu.zzft().zzb(e, true);
                    return;
                }
            } else {
                zzlmVar = zzut;
            }
            boolean equals = "1".equals(map.get("muted"));
            float parseFloat = Float.parseFloat(map.get("currentTime"));
            int parseInt = Integer.parseInt(map.get("playbackState"));
            if (parseInt < 0 || 3 < parseInt) {
                parseInt = 0;
            }
            synchronized (zzlmVar.zzail) {
                zzlmVar.zzcqd = parseFloat;
                zzlmVar.zzcqc = equals;
                i = zzlmVar.zzcpz;
                zzlmVar.zzcpz = parseInt;
            }
            com.google.android.gms.ads.internal.zzu.zzfq();
            zzkh.runOnUiThread(new Runnable() { // from class: com.google.android.gms.internal.zzlm.2
                final /* synthetic */ int zzcqg;
                final /* synthetic */ int zzcqh;

                public AnonymousClass2(int i2, int parseInt2) {
                    r2 = i2;
                    r3 = parseInt2;
                }

                @Override // java.lang.Runnable
                public final void run() {
                    synchronized (zzlm.this.zzail) {
                        boolean z = r2 != r3;
                        boolean z2 = !zzlm.this.zzcqb && r3 == 1;
                        boolean z3 = z && r3 == 1;
                        boolean z4 = z && r3 == 2;
                        boolean z5 = z && r3 == 3;
                        zzlm.this.zzcqb = zzlm.this.zzcqb || z2;
                        if (zzlm.this.zzcqa == null) {
                            return;
                        }
                        if (z2) {
                            try {
                                zzlm.this.zzcqa.zzjb();
                            } catch (RemoteException e2) {
                                zzkd.zzd("Unable to call onVideoStart()", e2);
                            }
                        }
                        if (z3) {
                            try {
                                zzlm.this.zzcqa.zzjc();
                            } catch (RemoteException e3) {
                                zzkd.zzd("Unable to call onVideoPlay()", e3);
                            }
                        }
                        if (z4) {
                            try {
                                zzlm.this.zzcqa.zzjd();
                            } catch (RemoteException e4) {
                                zzkd.zzd("Unable to call onVideoPause()", e4);
                            }
                        }
                        if (z5) {
                            try {
                                zzlm.this.zzcqa.onVideoEnd();
                            } catch (RemoteException e5) {
                                zzkd.zzd("Unable to call onVideoEnd()", e5);
                            }
                        }
                    }
                }
            });
        }
    }
}
