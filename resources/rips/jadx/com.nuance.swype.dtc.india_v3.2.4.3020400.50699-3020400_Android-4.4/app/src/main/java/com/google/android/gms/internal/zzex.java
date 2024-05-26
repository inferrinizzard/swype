package com.google.android.gms.internal;

import android.text.TextUtils;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzex implements zzep {
    private final Object zzail = new Object();
    private final Map<String, zza> zzbix = new HashMap();

    /* loaded from: classes.dex */
    public interface zza {
    }

    @Override // com.google.android.gms.internal.zzep
    public final void zza(zzlh zzlhVar, Map<String, String> map) {
        String str = map.get("id");
        String str2 = map.get("fail");
        map.get("fail_reason");
        String str3 = map.get(XMLResultsHandler.ELEM_RESULT);
        synchronized (this.zzail) {
            if (this.zzbix.remove(str) == null) {
                String valueOf = String.valueOf(str);
                zzkd.zzcx(valueOf.length() != 0 ? "Received result for unexpected method invocation: ".concat(valueOf) : new String("Received result for unexpected method invocation: "));
            } else if (TextUtils.isEmpty(str2)) {
                if (str3 == null) {
                    return;
                }
                try {
                    new JSONObject(str3);
                } catch (JSONException e) {
                    e.getMessage();
                }
            }
        }
    }
}
