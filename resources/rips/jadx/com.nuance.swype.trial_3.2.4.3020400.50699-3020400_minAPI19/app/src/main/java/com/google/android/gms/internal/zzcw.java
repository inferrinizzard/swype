package com.google.android.gms.internal;

import com.google.android.gms.ads.internal.request.AdRequestInfoParcel;
import java.util.Collections;
import java.util.List;

@zzin
/* loaded from: classes.dex */
public final class zzcw implements zzcx {
    @Override // com.google.android.gms.internal.zzcx
    public final List<String> zza(AdRequestInfoParcel adRequestInfoParcel) {
        return adRequestInfoParcel.zzcbh == null ? Collections.emptyList() : adRequestInfoParcel.zzcbh;
    }
}
