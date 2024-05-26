package com.flurry.sdk;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class lt {
    public static Map<String, String> a(JSONObject jSONObject) throws JSONException {
        HashMap hashMap = new HashMap();
        Iterator<String> keys = jSONObject.keys();
        while (keys.hasNext()) {
            String next = keys.next();
            if (!(next instanceof String)) {
                throw new JSONException("JSONObject contains unsupported type for key in the map.");
            }
            String str = next;
            Object obj = jSONObject.get(str);
            if (!(obj instanceof String)) {
                throw new JSONException("JSONObject contains unsupported type for value in the map.");
            }
            hashMap.put(str, (String) obj);
        }
        return hashMap;
    }
}
