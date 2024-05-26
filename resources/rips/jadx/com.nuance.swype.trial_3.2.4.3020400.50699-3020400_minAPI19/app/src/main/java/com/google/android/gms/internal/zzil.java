package com.google.android.gms.internal;

import android.support.v4.util.SimpleArrayMap;
import com.google.android.gms.internal.zzii;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzil implements zzii.zza<com.google.android.gms.ads.internal.formats.zzf> {
    private final boolean zzcaa;

    public zzil(boolean z) {
        this.zzcaa = z;
    }

    private static <K, V> SimpleArrayMap<K, V> zzc(SimpleArrayMap<K, Future<V>> simpleArrayMap) throws InterruptedException, ExecutionException {
        SimpleArrayMap<K, V> simpleArrayMap2 = new SimpleArrayMap<>();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= simpleArrayMap.size()) {
                return simpleArrayMap2;
            }
            simpleArrayMap2.put(simpleArrayMap.keyAt(i2), simpleArrayMap.valueAt(i2).get());
            i = i2 + 1;
        }
    }

    @Override // com.google.android.gms.internal.zzii.zza
    public final /* synthetic */ com.google.android.gms.ads.internal.formats.zzf zza(zzii zziiVar, JSONObject jSONObject) throws JSONException, InterruptedException, ExecutionException {
        SimpleArrayMap simpleArrayMap = new SimpleArrayMap();
        SimpleArrayMap simpleArrayMap2 = new SimpleArrayMap();
        zzky<com.google.android.gms.ads.internal.formats.zza> zzg = zziiVar.zzg(jSONObject);
        JSONArray jSONArray = jSONObject.getJSONArray("custom_assets");
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
            String string = jSONObject2.getString("type");
            if ("string".equals(string)) {
                simpleArrayMap2.put(jSONObject2.getString("name"), jSONObject2.getString("string_value"));
            } else if (!"image".equals(string)) {
                String valueOf = String.valueOf(string);
                zzkd.zzcx(valueOf.length() != 0 ? "Unknown custom asset type: ".concat(valueOf) : new String("Unknown custom asset type: "));
            } else {
                String string2 = jSONObject2.getString("name");
                boolean z = this.zzcaa;
                JSONObject jSONObject3 = jSONObject2.getJSONObject("image_value");
                boolean optBoolean = jSONObject3.optBoolean("require", true);
                if (jSONObject3 == null) {
                    jSONObject3 = new JSONObject();
                }
                simpleArrayMap.put(string2, zziiVar.zza(jSONObject3, optBoolean, z));
            }
        }
        return new com.google.android.gms.ads.internal.formats.zzf(jSONObject.getString("custom_template_id"), zzc(simpleArrayMap), simpleArrayMap2, zzg.get());
    }
}
