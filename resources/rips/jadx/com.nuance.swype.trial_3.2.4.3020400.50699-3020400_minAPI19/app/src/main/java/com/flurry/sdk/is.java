package com.flurry.sdk;

import android.text.TextUtils;
import bolts.MeasurementEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import jp.co.omronsoft.openwnn.JAJP.OpenWnnEngineJAJP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class is implements kz<hs> {
    private static final String a = is.class.getSimpleName();

    @Override // com.flurry.sdk.kz
    public final /* synthetic */ hs a(InputStream inputStream) throws IOException {
        return b(inputStream);
    }

    private static hs b(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }
        String str = new String(lr.a(inputStream));
        kf.a(5, a, "Proton response string: " + str);
        hs hsVar = new hs();
        try {
            JSONObject jSONObject = new JSONObject(str);
            hsVar.a = jSONObject.optLong("issued_at", -1L);
            hsVar.b = jSONObject.optLong("refresh_ttl", 3600L);
            hsVar.c = jSONObject.optLong("expiration_ttl", 86400L);
            JSONObject optJSONObject = jSONObject.optJSONObject("global_settings");
            hsVar.d = new hz();
            if (optJSONObject != null) {
                hsVar.d.a = b(optJSONObject.optString("log_level"));
            }
            JSONObject optJSONObject2 = jSONObject.optJSONObject("pulse");
            hq hqVar = new hq();
            if (optJSONObject2 != null) {
                a(hqVar, optJSONObject2.optJSONArray("callbacks"));
                hqVar.b = optJSONObject2.optInt("max_callback_retries", 3);
                hqVar.c = optJSONObject2.optInt("max_callback_attempts_per_report", 15);
                hqVar.d = optJSONObject2.optInt("max_report_delay_seconds", OpenWnnEngineJAJP.FREQ_LEARN);
                hqVar.e = optJSONObject2.optString("agent_report_url", "");
            }
            hsVar.e = hqVar;
            JSONObject optJSONObject3 = jSONObject.optJSONObject("analytics");
            hsVar.f = new ic();
            if (optJSONObject3 == null) {
                return hsVar;
            }
            hsVar.f.b = optJSONObject3.optBoolean("analytics_enabled", true);
            hsVar.f.a = optJSONObject3.optInt("max_session_properties", 10);
            return hsVar;
        } catch (JSONException e) {
            throw new IOException("Exception while deserialize: ", e);
        }
    }

    private static void a(hq hqVar, JSONArray jSONArray) throws JSONException {
        JSONObject optJSONObject;
        if (jSONArray != null) {
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject optJSONObject2 = jSONArray.optJSONObject(i);
                if (optJSONObject2 != null) {
                    hp hpVar = new hp();
                    hpVar.b = optJSONObject2.optString("partner", "");
                    a(hpVar, optJSONObject2.optJSONArray("events"));
                    hpVar.d = a(optJSONObject2.optString("method"));
                    hpVar.e = optJSONObject2.optString("uri_template", "");
                    JSONObject optJSONObject3 = optJSONObject2.optJSONObject("body_template");
                    if (optJSONObject3 != null) {
                        String optString = optJSONObject3.optString("string", "null");
                        if (!optString.equals("null")) {
                            hpVar.f = optString;
                        }
                    }
                    hpVar.g = optJSONObject2.optInt("max_redirects", 5);
                    hpVar.h = optJSONObject2.optInt("connect_timeout", 20);
                    hpVar.i = optJSONObject2.optInt("request_timeout", 20);
                    hpVar.a = optJSONObject2.optLong("callback_id", -1L);
                    JSONObject optJSONObject4 = optJSONObject2.optJSONObject("headers");
                    if (optJSONObject4 != null && (optJSONObject = optJSONObject4.optJSONObject("map")) != null) {
                        hpVar.j = lt.a(optJSONObject);
                    }
                    arrayList.add(hpVar);
                }
            }
            hqVar.a = arrayList;
        }
    }

    private static void a(hp hpVar, JSONArray jSONArray) {
        String[] strArr;
        if (jSONArray != null) {
            ArrayList arrayList = null;
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject optJSONObject = jSONArray.optJSONObject(i);
                if (optJSONObject != null) {
                    if (optJSONObject.has("string")) {
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                        }
                        hv hvVar = new hv();
                        hvVar.a = optJSONObject.optString("string", "");
                        arrayList.add(hvVar);
                    } else if (optJSONObject.has("com.flurry.proton.generated.avro.v2.EventParameterCallbackTrigger")) {
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                        }
                        JSONObject optJSONObject2 = optJSONObject.optJSONObject("com.flurry.proton.generated.avro.v2.EventParameterCallbackTrigger");
                        if (optJSONObject2 != null) {
                            hw hwVar = new hw();
                            hwVar.a = optJSONObject2.optString(MeasurementEvent.MEASUREMENT_EVENT_NAME_KEY, "");
                            hwVar.c = optJSONObject2.optString("event_parameter_name", "");
                            JSONArray optJSONArray = optJSONObject2.optJSONArray("event_parameter_values");
                            if (optJSONArray != null) {
                                String[] strArr2 = new String[optJSONArray.length()];
                                for (int i2 = 0; i2 < optJSONArray.length(); i2++) {
                                    strArr2[i2] = optJSONArray.optString(i2, "");
                                }
                                strArr = strArr2;
                            } else {
                                strArr = new String[0];
                            }
                            hwVar.d = strArr;
                            arrayList.add(hwVar);
                        }
                    }
                }
            }
            hpVar.c = arrayList;
        }
    }

    private static ip a(String str) {
        ip ipVar = ip.GET;
        try {
            if (!TextUtils.isEmpty(str)) {
                return (ip) Enum.valueOf(ip.class, str);
            }
        } catch (Exception e) {
        }
        return ipVar;
    }

    private static ia b(String str) {
        ia iaVar = ia.OFF;
        try {
            if (!TextUtils.isEmpty(str)) {
                return (ia) Enum.valueOf(ia.class, str);
            }
        } catch (Exception e) {
        }
        return iaVar;
    }

    @Override // com.flurry.sdk.kz
    public final /* synthetic */ void a(OutputStream outputStream, hs hsVar) throws IOException {
        throw new IOException("Serialize not supported for response");
    }
}
