package com.google.android.gms.internal;

import android.content.Context;
import android.text.TextUtils;
import com.facebook.GraphResponse;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import java.io.BufferedOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzeq implements zzep {
    private final Context mContext;
    private final VersionInfoParcel zzalo;

    /* JADX INFO: Access modifiers changed from: package-private */
    @zzin
    /* loaded from: classes.dex */
    public static class zza {
        final String mValue;
        final String zzaxp;

        public zza(String str, String str2) {
            this.zzaxp = str;
            this.mValue = str2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @zzin
    /* loaded from: classes.dex */
    public static class zzb {
        final String zzbii;
        final URL zzbij;
        final ArrayList<zza> zzbik;
        final String zzbil;

        public zzb(String str, URL url, ArrayList<zza> arrayList, String str2) {
            this.zzbii = str;
            this.zzbij = url;
            this.zzbik = arrayList;
            this.zzbil = str2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @zzin
    /* loaded from: classes.dex */
    public class zzc {
        final zzd zzbim;
        final boolean zzbin;
        final String zzbio;

        public zzc(boolean z, zzd zzdVar, String str) {
            this.zzbin = z;
            this.zzbim = zzdVar;
            this.zzbio = str;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @zzin
    /* loaded from: classes.dex */
    public static class zzd {
        final String zzbfi;
        final String zzbii;
        final int zzbip;
        final List<zza> zzbiq;

        public zzd(String str, int i, List<zza> list, String str2) {
            this.zzbii = str;
            this.zzbip = i;
            this.zzbiq = list;
            this.zzbfi = str2;
        }
    }

    public zzeq(Context context, VersionInfoParcel versionInfoParcel) {
        this.mContext = context;
        this.zzalo = versionInfoParcel;
    }

    private static zzb zzc(JSONObject jSONObject) {
        URL url;
        String optString = jSONObject.optString("http_request_id");
        String optString2 = jSONObject.optString("url");
        String optString3 = jSONObject.optString("post_body", null);
        try {
            url = new URL(optString2);
        } catch (MalformedURLException e) {
            zzkd.zzb("Error constructing http request.", e);
            url = null;
        }
        ArrayList arrayList = new ArrayList();
        JSONArray optJSONArray = jSONObject.optJSONArray("headers");
        if (optJSONArray == null) {
            optJSONArray = new JSONArray();
        }
        for (int i = 0; i < optJSONArray.length(); i++) {
            JSONObject optJSONObject = optJSONArray.optJSONObject(i);
            if (optJSONObject != null) {
                arrayList.add(new zza(optJSONObject.optString("key"), optJSONObject.optString("value")));
            }
        }
        return new zzb(optString, url, arrayList, optString3);
    }

    @Override // com.google.android.gms.internal.zzep
    public final void zza(final zzlh zzlhVar, final Map<String, String> map) {
        zzkg.zza(new Runnable() { // from class: com.google.android.gms.internal.zzeq.1
            @Override // java.lang.Runnable
            public final void run() {
                zzkd.zzcv("Received Http request.");
                final JSONObject zzav = zzeq.this.zzav((String) map.get("http_request"));
                if (zzav == null) {
                    zzkd.e("Response should not be null.");
                } else {
                    zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.internal.zzeq.1.1
                        @Override // java.lang.Runnable
                        public final void run() {
                            zzlhVar.zzb("fetchHttpRequestCompleted", zzav);
                            zzkd.zzcv("Dispatched http response.");
                        }
                    });
                }
            }
        });
    }

    private static JSONObject zza(zzd zzdVar) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("http_request_id", zzdVar.zzbii);
            if (zzdVar.zzbfi != null) {
                jSONObject.put("body", zzdVar.zzbfi);
            }
            JSONArray jSONArray = new JSONArray();
            for (zza zzaVar : zzdVar.zzbiq) {
                jSONArray.put(new JSONObject().put("key", zzaVar.zzaxp).put("value", zzaVar.mValue));
            }
            jSONObject.put("headers", jSONArray);
            jSONObject.put("response_code", zzdVar.zzbip);
        } catch (JSONException e) {
            zzkd.zzb("Error constructing JSON for http response.", e);
        }
        return jSONObject;
    }

    private zzc zza(zzb zzbVar) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) zzbVar.zzbij.openConnection();
            com.google.android.gms.ads.internal.zzu.zzfq().zza$59850860(this.mContext, this.zzalo.zzcs, httpURLConnection);
            Iterator<zza> it = zzbVar.zzbik.iterator();
            while (it.hasNext()) {
                zza next = it.next();
                httpURLConnection.addRequestProperty(next.zzaxp, next.mValue);
            }
            if (!TextUtils.isEmpty(zzbVar.zzbil)) {
                httpURLConnection.setDoOutput(true);
                byte[] bytes = zzbVar.zzbil.getBytes();
                httpURLConnection.setFixedLengthStreamingMode(bytes.length);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
                bufferedOutputStream.write(bytes);
                bufferedOutputStream.close();
            }
            ArrayList arrayList = new ArrayList();
            if (httpURLConnection.getHeaderFields() != null) {
                for (Map.Entry<String, List<String>> entry : httpURLConnection.getHeaderFields().entrySet()) {
                    Iterator<String> it2 = entry.getValue().iterator();
                    while (it2.hasNext()) {
                        arrayList.add(new zza(entry.getKey(), it2.next()));
                    }
                }
            }
            String str = zzbVar.zzbii;
            int responseCode = httpURLConnection.getResponseCode();
            com.google.android.gms.ads.internal.zzu.zzfq();
            return new zzc(true, new zzd(str, responseCode, arrayList, zzkh.zza(new InputStreamReader(httpURLConnection.getInputStream()))), null);
        } catch (Exception e) {
            return new zzc(false, null, e.toString());
        }
    }

    public final JSONObject zzav(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            JSONObject jSONObject2 = new JSONObject();
            String str2 = "";
            try {
                str2 = jSONObject.optString("http_request_id");
                zzc zza2 = zza(zzc(jSONObject));
                if (zza2.zzbin) {
                    jSONObject2.put("response", zza(zza2.zzbim));
                    jSONObject2.put(GraphResponse.SUCCESS_KEY, true);
                } else {
                    jSONObject2.put("response", new JSONObject().put("http_request_id", str2));
                    jSONObject2.put(GraphResponse.SUCCESS_KEY, false);
                    jSONObject2.put("reason", zza2.zzbio);
                }
                return jSONObject2;
            } catch (Exception e) {
                try {
                    jSONObject2.put("response", new JSONObject().put("http_request_id", str2));
                    jSONObject2.put(GraphResponse.SUCCESS_KEY, false);
                    jSONObject2.put("reason", e.toString());
                    return jSONObject2;
                } catch (JSONException e2) {
                    return jSONObject2;
                }
            }
        } catch (JSONException e3) {
            zzkd.e("The request is not a valid JSON.");
            try {
                return new JSONObject().put(GraphResponse.SUCCESS_KEY, false);
            } catch (JSONException e4) {
                return new JSONObject();
            }
        }
    }
}
