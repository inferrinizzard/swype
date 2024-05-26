package com.flurry.sdk;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ir implements kz<hr> {
    private static final String a = ir.class.getSimpleName();

    @Override // com.flurry.sdk.kz
    public final /* synthetic */ void a(OutputStream outputStream, hr hrVar) throws IOException {
        JSONObject jSONObject;
        JSONObject jSONObject2;
        hr hrVar2 = hrVar;
        if (outputStream != null && hrVar2 != null) {
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream) { // from class: com.flurry.sdk.ir.1
                @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
                public final void close() {
                }
            };
            JSONObject jSONObject3 = new JSONObject();
            try {
                try {
                    a(jSONObject3, "project_key", hrVar2.a);
                    a(jSONObject3, "bundle_id", hrVar2.b);
                    a(jSONObject3, "app_version", hrVar2.c);
                    jSONObject3.put("sdk_version", hrVar2.d);
                    jSONObject3.put("platform", hrVar2.e);
                    a(jSONObject3, "platform_version", hrVar2.f);
                    jSONObject3.put("limit_ad_tracking", hrVar2.g);
                    if (hrVar2.h == null || hrVar2.h.a == null) {
                        jSONObject = null;
                    } else {
                        jSONObject = new JSONObject();
                        JSONObject jSONObject4 = new JSONObject();
                        a(jSONObject4, "model", hrVar2.h.a.a);
                        a(jSONObject4, "brand", hrVar2.h.a.b);
                        a(jSONObject4, "id", hrVar2.h.a.c);
                        a(jSONObject4, "device", hrVar2.h.a.d);
                        a(jSONObject4, "product", hrVar2.h.a.e);
                        a(jSONObject4, "version_release", hrVar2.h.a.f);
                        jSONObject.put("com.flurry.proton.generated.avro.v2.AndroidTags", jSONObject4);
                    }
                    if (jSONObject != null) {
                        jSONObject3.put("device_tags", jSONObject);
                    } else {
                        jSONObject3.put("device_tags", JSONObject.NULL);
                    }
                    JSONArray jSONArray = new JSONArray();
                    for (ht htVar : hrVar2.i) {
                        JSONObject jSONObject5 = new JSONObject();
                        jSONObject5.put("type", htVar.a);
                        a(jSONObject5, "id", htVar.b);
                        jSONArray.put(jSONObject5);
                    }
                    jSONObject3.put("device_ids", jSONArray);
                    if (hrVar2.j == null || hrVar2.j.a == null) {
                        jSONObject2 = null;
                    } else {
                        jSONObject2 = new JSONObject();
                        JSONObject jSONObject6 = new JSONObject();
                        jSONObject6.putOpt("latitude", Double.valueOf(hrVar2.j.a.a));
                        jSONObject6.putOpt("longitude", Double.valueOf(hrVar2.j.a.b));
                        jSONObject6.putOpt("accuracy", Float.valueOf(hrVar2.j.a.c));
                        jSONObject2.put("com.flurry.proton.generated.avro.v2.Geolocation", jSONObject6);
                    }
                    if (jSONObject2 != null) {
                        jSONObject3.put("geo", jSONObject2);
                    } else {
                        jSONObject3.put("geo", JSONObject.NULL);
                    }
                    JSONObject jSONObject7 = new JSONObject();
                    if (hrVar2.k != null) {
                        a(jSONObject7, "string", hrVar2.k.a);
                        jSONObject3.put("publisher_user_id", jSONObject7);
                    } else {
                        jSONObject3.put("publisher_user_id", JSONObject.NULL);
                    }
                    kf.a(5, a, "Proton Request String: " + jSONObject3.toString());
                    dataOutputStream.write(jSONObject3.toString().getBytes());
                    dataOutputStream.flush();
                } catch (JSONException e) {
                    throw new IOException("Invalid Json", e);
                }
            } finally {
                dataOutputStream.close();
            }
        }
    }

    private static void a(JSONObject jSONObject, String str, String str2) throws IOException, JSONException {
        if (str2 != null) {
            jSONObject.put(str, str2);
        } else {
            jSONObject.put(str, JSONObject.NULL);
        }
    }

    @Override // com.flurry.sdk.kz
    public final /* synthetic */ hr a(InputStream inputStream) throws IOException {
        throw new IOException("Deserialize not supported for request");
    }
}
