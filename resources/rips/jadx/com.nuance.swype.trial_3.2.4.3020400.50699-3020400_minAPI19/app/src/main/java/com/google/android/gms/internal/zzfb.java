package com.google.android.gms.internal;

@zzin
/* loaded from: classes.dex */
public final class zzfb extends zzkc {
    final zzlh zzbgf;
    final zzfd zzbjb;
    private final String zzbjc;

    @Override // com.google.android.gms.internal.zzkc
    public final void onStop() {
        this.zzbjb.abort();
    }

    @Override // com.google.android.gms.internal.zzkc
    public final void zzew() {
        try {
            this.zzbjb.zzaz(this.zzbjc);
        } finally {
            zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.internal.zzfb.1
                @Override // java.lang.Runnable
                public final void run() {
                    zzfc zzgj = com.google.android.gms.ads.internal.zzu.zzgj();
                    zzgj.zzbje.remove(zzfb.this);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public zzfb(zzlh zzlhVar, zzfd zzfdVar, String str) {
        this.zzbgf = zzlhVar;
        this.zzbjb = zzfdVar;
        this.zzbjc = str;
        com.google.android.gms.ads.internal.zzu.zzgj().zzbje.add(this);
    }
}
