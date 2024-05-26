package com.google.android.gms.ads.internal.formats;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import com.google.android.gms.internal.zzin;
import java.lang.ref.WeakReference;
import java.util.Map;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public interface zzh {

    /* loaded from: classes.dex */
    public interface zza {
        String getCustomTemplateId();

        void zzb(zzh zzhVar);

        String zzkw();

        com.google.android.gms.ads.internal.formats.zza zzkx();
    }

    Context getContext();

    void recordImpression();

    void zza(View view, Map<String, WeakReference<View>> map, JSONObject jSONObject, JSONObject jSONObject2, JSONObject jSONObject3);

    void zza(String str, JSONObject jSONObject, JSONObject jSONObject2, JSONObject jSONObject3);

    void zzb(MotionEvent motionEvent);

    void zzb(View view, Map<String, WeakReference<View>> map);

    void zzg(View view);

    void zzh(View view);

    View zzlc();
}
