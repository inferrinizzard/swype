package com.google.android.gms.internal;

import android.content.Context;
import android.text.TextUtils;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.internal.zzae;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/* loaded from: classes.dex */
public class zzap extends zzaq {
    private static final String TAG = zzap.class.getSimpleName();
    AdvertisingIdClient.Info zzafm;

    private zzap(Context context) {
        super(context, "");
    }

    public static String zza(String str, String str2) {
        return zzak.zza$166451eb(str, str2);
    }

    public static zzap zze(Context context) {
        zza(context, true);
        return new zzap(context);
    }

    @Override // com.google.android.gms.internal.zzaq
    protected final void zza(zzax zzaxVar, zzae.zza zzaVar) {
        if (!zzaxVar.zzci()) {
            zza(zzb(zzaxVar, zzaVar));
            return;
        }
        if (this.zzafm != null) {
            String id = this.zzafm.getId();
            if (!TextUtils.isEmpty(id)) {
                zzaVar.zzeg = zzay.zzo(id);
                zzaVar.zzeh = 5;
                zzaVar.zzei = Boolean.valueOf(this.zzafm.isLimitAdTrackingEnabled());
            }
            this.zzafm = null;
        }
    }

    @Override // com.google.android.gms.internal.zzaq, com.google.android.gms.internal.zzao
    protected final zzae.zza zzd(Context context) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.internal.zzaq
    public final List<Callable<Void>> zzb(zzax zzaxVar, zzae.zza zzaVar) {
        ArrayList arrayList = new ArrayList();
        if (zzaxVar.zzagg == null) {
            return arrayList;
        }
        arrayList.add(new zzbh(zzaxVar, "lLpTIaE60qRmDJilKTnB6dMslmEDCMG+aJ7xPwxeE01HtxatTPhAFeGxL2EFpKqq", "LwAyv7R7EEW6/T7p6KlsghmfaITLnkCV2ffewHyZJ4E=", zzaVar, zzaxVar.zzat()));
        return arrayList;
    }
}
