package com.google.android.gms.internal;

import com.facebook.internal.NativeProtocol;
import com.google.android.gms.ads.internal.client.zzab;
import java.util.HashMap;
import java.util.Map;

@zzin
/* loaded from: classes.dex */
public final class zzlm extends zzab.zza {
    public final Object zzail = new Object();
    public boolean zzaio = true;
    private final zzlh zzbgf;
    private final float zzcpy;
    int zzcpz;
    private com.google.android.gms.ads.internal.client.zzac zzcqa;
    private boolean zzcqb;
    boolean zzcqc;
    float zzcqd;

    public zzlm(zzlh zzlhVar, float f) {
        this.zzbgf = zzlhVar;
        this.zzcpy = f;
    }

    @Override // com.google.android.gms.ads.internal.client.zzab
    public final int getPlaybackState() {
        int i;
        synchronized (this.zzail) {
            i = this.zzcpz;
        }
        return i;
    }

    @Override // com.google.android.gms.ads.internal.client.zzab
    public final boolean isMuted() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzcqc;
        }
        return z;
    }

    @Override // com.google.android.gms.ads.internal.client.zzab
    public final void zza(com.google.android.gms.ads.internal.client.zzac zzacVar) {
        synchronized (this.zzail) {
            this.zzcqa = zzacVar;
        }
    }

    public final void zzd(String str, Map<String, String> map) {
        final HashMap hashMap = map == null ? new HashMap() : new HashMap(map);
        hashMap.put(NativeProtocol.WEB_DIALOG_ACTION, str);
        com.google.android.gms.ads.internal.zzu.zzfq();
        zzkh.runOnUiThread(new Runnable() { // from class: com.google.android.gms.internal.zzlm.1
            @Override // java.lang.Runnable
            public final void run() {
                zzlm.this.zzbgf.zza("pubVideoCmd", hashMap);
            }
        });
    }

    @Override // com.google.android.gms.ads.internal.client.zzab
    public final float zziz() {
        return this.zzcpy;
    }

    @Override // com.google.android.gms.ads.internal.client.zzab
    public final float zzja() {
        float f;
        synchronized (this.zzail) {
            f = this.zzcqd;
        }
        return f;
    }

    @Override // com.google.android.gms.ads.internal.client.zzab
    public final void play() {
        zzd("play", null);
    }

    @Override // com.google.android.gms.ads.internal.client.zzab
    public final void pause() {
        zzd("pause", null);
    }

    @Override // com.google.android.gms.ads.internal.client.zzab
    public final void zzm(boolean z) {
        zzd(z ? "mute" : "unmute", null);
    }
}
