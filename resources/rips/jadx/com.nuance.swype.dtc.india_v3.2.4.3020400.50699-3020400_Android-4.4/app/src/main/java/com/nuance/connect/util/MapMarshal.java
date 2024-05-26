package com.nuance.connect.util;

import com.nuance.connect.util.Logger;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class MapMarshal {
    protected static final String OBJECT_KEY = "key";
    protected static final String OBJECT_VALUE = "value";
    private static Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, "MapMarshal");

    public static String toString(Map<?, ?> map) throws Exception {
        JSONArray jSONArray = new JSONArray();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(OBJECT_KEY, entry.getKey().toString());
            jSONObject.put(OBJECT_VALUE, entry.getValue().toString());
            jSONArray.put(jSONObject);
        }
        return jSONArray.toString();
    }

    public static Map<String, Integer> toStringIntegerMap(String str) throws Exception {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        JSONArray jSONArray = new JSONArray(str);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= jSONArray.length()) {
                return linkedHashMap;
            }
            JSONObject jSONObject = jSONArray.getJSONObject(i2);
            linkedHashMap.put((String) jSONObject.get(OBJECT_KEY), Integer.valueOf((String) jSONObject.get(OBJECT_VALUE)));
            i = i2 + 1;
        }
    }

    public static Map<String, Integer> toStringIntegerMapFromJSON(JSONArray jSONArray) throws Exception {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONArray jSONArray2 = jSONArray.getJSONArray(i);
            linkedHashMap.put((String) jSONArray2.get(0), Integer.valueOf((String) jSONArray2.get(1)));
        }
        return linkedHashMap;
    }

    public static Map<String, String> toStringMap(String str) throws Exception {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        JSONArray jSONArray = new JSONArray(str);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= jSONArray.length()) {
                return linkedHashMap;
            }
            JSONObject jSONObject = jSONArray.getJSONObject(i2);
            linkedHashMap.put((String) jSONObject.get(OBJECT_KEY), (String) jSONObject.get(OBJECT_VALUE));
            i = i2 + 1;
        }
    }

    public static Map<String, String> toStringMapFromJSON(JSONArray jSONArray) throws Exception {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONArray jSONArray2 = jSONArray.getJSONArray(i);
            linkedHashMap.put((String) jSONArray2.get(0), (String) jSONArray2.get(1));
        }
        return linkedHashMap;
    }
}
