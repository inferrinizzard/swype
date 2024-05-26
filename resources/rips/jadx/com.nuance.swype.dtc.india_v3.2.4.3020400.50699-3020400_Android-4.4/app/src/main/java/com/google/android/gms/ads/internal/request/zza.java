package com.google.android.gms.ads.internal.request;

import android.content.Context;
import com.google.android.gms.ads.internal.request.AdRequestInfoParcel;
import com.google.android.gms.internal.zzas;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzju;
import com.google.android.gms.internal.zzkc;

@zzin
/* loaded from: classes.dex */
public class zza {

    /* renamed from: com.google.android.gms.ads.internal.request.zza$zza, reason: collision with other inner class name */
    /* loaded from: classes.dex */
    public interface InterfaceC0027zza {
        void zza(zzju.zza zzaVar);
    }

    public zzkc zza(Context context, AdRequestInfoParcel.zza zzaVar, zzas zzasVar, InterfaceC0027zza interfaceC0027zza) {
        zzkc zznVar = zzaVar.zzcar.extras.getBundle("sdk_less_server_data") != null ? new zzn(context, zzaVar, interfaceC0027zza) : new zzb(context, zzaVar, zzasVar, interfaceC0027zza);
        zznVar.zzpy();
        return zznVar;
    }
}
