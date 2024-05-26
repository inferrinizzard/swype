package com.google.android.gms.internal;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.internal.zzae;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes.dex */
public final class zzbh extends zzbp {
    public zzbh(zzax zzaxVar, String str, String str2, zzae.zza zzaVar, int i) {
        super(zzaxVar, str, str2, zzaVar, i, 24);
    }

    @Override // com.google.android.gms.internal.zzbp
    protected final void zzcu() throws IllegalAccessException, InvocationTargetException {
        if (this.zzaey.zzci()) {
            AdvertisingIdClient zzcr = this.zzaey.zzcr();
            if (zzcr != null) {
                try {
                    AdvertisingIdClient.Info info = zzcr.getInfo();
                    String zzo = zzay.zzo(info.getId());
                    if (zzo != null) {
                        synchronized (this.zzaha) {
                            this.zzaha.zzeg = zzo;
                            this.zzaha.zzei = Boolean.valueOf(info.isLimitAdTrackingEnabled());
                            this.zzaha.zzeh = 5;
                        }
                        return;
                    }
                    return;
                } catch (IOException e) {
                    return;
                }
            }
            return;
        }
        synchronized (this.zzaha) {
            this.zzaha.zzeg = (String) this.zzahh.invoke(null, this.zzaey.getContext());
        }
    }
}
