package com.nuance.connect.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class FeaturesLastUsed {
    private final Map<String, String> featuresMap = new HashMap();

    /* loaded from: classes.dex */
    public enum Feature {
        LANGUAGE,
        LLUDA,
        HOTWORDS,
        REPORTING,
        CHINESEDICTIONARIES,
        CCPS,
        DLT,
        BACKUP_SYNC,
        SCANNER_CALLLOG,
        SCANNER_CONTACTS,
        SCANNER_FACEBOOK,
        SCANNER_GMAIL,
        SCANNER_MEDIA,
        SCANNER_SMS,
        SCANNER_TWITTER;

        public static boolean isFeatureKnown(String str) {
            if (str != null) {
                try {
                    valueOf(str);
                    return true;
                } catch (IllegalArgumentException e) {
                }
            }
            return false;
        }
    }

    public FeaturesLastUsed(String str) {
        updateWithString(str == null ? "" : str);
    }

    public String getLastUsed(Feature feature) {
        return this.featuresMap.get(feature.name());
    }

    public void setLastUsed(Feature feature, long j) {
        if (feature != null) {
            this.featuresMap.put(feature.name(), String.valueOf(j));
        }
    }

    public String toString() {
        return new JSONObject(this.featuresMap).toString();
    }

    public void updateWithString(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                if (Feature.isFeatureKnown(next)) {
                    this.featuresMap.put(next, jSONObject.getString(next));
                }
            }
        } catch (JSONException e) {
        }
    }
}
