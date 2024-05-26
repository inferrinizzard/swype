package com.google.android.gms.internal;

import com.google.android.gms.internal.zzm;
import java.io.UnsupportedEncodingException;

/* loaded from: classes.dex */
public class zzab extends zzk<String> {
    private final zzm.zzb<String> zzcg;

    public zzab(int i, String str, zzm.zzb<String> zzbVar, zzm.zza zzaVar) {
        super(0, str, zzaVar);
        this.zzcg = zzbVar;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.internal.zzk
    public final zzm<String> zza(zzi zziVar) {
        String str;
        try {
            str = new String(zziVar.data, zzx.zzb(zziVar.zzz, "ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            str = new String(zziVar.data);
        }
        return zzm.zza(str, zzx.zzb(zziVar));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.internal.zzk
    public final /* synthetic */ void zza(String str) {
        this.zzcg.zzb(str);
    }
}
