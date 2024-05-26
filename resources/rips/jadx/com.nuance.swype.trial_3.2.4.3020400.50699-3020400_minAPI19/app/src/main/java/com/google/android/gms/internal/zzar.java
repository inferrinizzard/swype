package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.internal.zzae;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/* loaded from: classes.dex */
public class zzar extends zzaq {
    private static final String TAG = zzar.class.getSimpleName();

    private zzar(Context context, String str, boolean z) {
        super(context, str, z);
    }

    public static zzar zza(String str, Context context, boolean z) {
        zza(context, z);
        return new zzar(context, str, z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.internal.zzaq
    public final List<Callable<Void>> zzb(zzax zzaxVar, zzae.zza zzaVar) {
        if (zzaxVar.zzagg == null || !this.zzafn) {
            return super.zzb(zzaxVar, zzaVar);
        }
        int zzat = zzaxVar.zzat();
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(super.zzb(zzaxVar, zzaVar));
        arrayList.add(new zzbh(zzaxVar, "lLpTIaE60qRmDJilKTnB6dMslmEDCMG+aJ7xPwxeE01HtxatTPhAFeGxL2EFpKqq", "LwAyv7R7EEW6/T7p6KlsghmfaITLnkCV2ffewHyZJ4E=", zzaVar, zzat));
        return arrayList;
    }
}
