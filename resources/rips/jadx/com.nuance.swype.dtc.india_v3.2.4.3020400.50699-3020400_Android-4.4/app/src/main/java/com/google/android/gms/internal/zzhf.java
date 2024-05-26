package com.google.android.gms.internal;

import com.facebook.internal.NativeProtocol;
import com.facebook.internal.ServerProtocol;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public class zzhf {
    final zzlh zzbgf;
    private final String zzbrm;

    public zzhf(zzlh zzlhVar) {
        this(zzlhVar, "");
    }

    public zzhf(zzlh zzlhVar, String str) {
        this.zzbgf = zzlhVar;
        this.zzbrm = str;
    }

    public final void zza(int i, int i2, int i3, int i4, float f, int i5) {
        try {
            this.zzbgf.zzb("onScreenInfoChanged", new JSONObject().put("width", i).put("height", i2).put("maxSizeWidth", i3).put("maxSizeHeight", i4).put("density", f).put("rotation", i5));
        } catch (JSONException e) {
            zzkd.zzb("Error occured while obtaining screen information.", e);
        }
    }

    public final void zzbt(String str) {
        try {
            this.zzbgf.zzb("onError", new JSONObject().put("message", str).put(NativeProtocol.WEB_DIALOG_ACTION, this.zzbrm));
        } catch (JSONException e) {
            zzkd.zzb("Error occurred while dispatching error event.", e);
        }
    }

    public final void zzbv(String str) {
        try {
            this.zzbgf.zzb("onStateChanged", new JSONObject().put(ServerProtocol.DIALOG_PARAM_STATE, str));
        } catch (JSONException e) {
            zzkd.zzb("Error occured while dispatching state change.", e);
        }
    }
}
