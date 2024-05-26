package com.google.android.gms.internal;

import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzhd {
    private final boolean zzbqx;
    private final boolean zzbqy;
    private final boolean zzbqz;
    private final boolean zzbra;
    private final boolean zzbrb;

    /* loaded from: classes.dex */
    public static final class zza {
        boolean zzbqx;
        boolean zzbqy;
        boolean zzbqz;
        boolean zzbra;
        boolean zzbrb;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public /* synthetic */ zzhd(zza zzaVar, byte b) {
        this(zzaVar);
    }

    public final JSONObject toJson() {
        try {
            return new JSONObject().put("sms", this.zzbqx).put("tel", this.zzbqy).put("calendar", this.zzbqz).put("storePicture", this.zzbra).put("inlineVideo", this.zzbrb);
        } catch (JSONException e) {
            zzkd.zzb("Error occured while obtaining the MRAID capabilities.", e);
            return null;
        }
    }

    private zzhd(zza zzaVar) {
        this.zzbqx = zzaVar.zzbqx;
        this.zzbqy = zzaVar.zzbqy;
        this.zzbqz = zzaVar.zzbqz;
        this.zzbra = zzaVar.zzbra;
        this.zzbrb = zzaVar.zzbrb;
    }
}
