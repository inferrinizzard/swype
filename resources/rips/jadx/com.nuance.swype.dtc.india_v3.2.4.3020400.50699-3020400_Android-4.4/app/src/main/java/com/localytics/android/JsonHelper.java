package com.localytics.android;

import com.facebook.internal.ServerProtocol;
import com.localytics.android.Localytics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class JsonHelper {
    JsonHelper() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object toJSON(Object object) throws JSONException {
        if (object instanceof Map) {
            JSONObject json = new JSONObject();
            Map map = (Map) object;
            for (Object key : map.keySet()) {
                json.put(key.toString(), toJSON(map.get(key)));
            }
            return json;
        }
        if (!(object instanceof Iterable)) {
            return object;
        }
        JSONArray json2 = new JSONArray();
        for (Object value : (Iterable) object) {
            json2.put(toJSON(value));
        }
        return json2;
    }

    static boolean isEmptyObject(JSONObject object) {
        return object.names() == null;
    }

    static Map<String, Object> getMap(JSONObject object, String key) throws JSONException {
        return toMap(object.getJSONObject(key));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            map.put(key, fromJson(object.get(key)));
        }
        return map;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List toList(JSONArray array) throws JSONException {
        List list = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    private static Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        }
        if (json instanceof JSONObject) {
            return toMap((JSONObject) json);
        }
        if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        }
        return json;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getSafeStringFromValue(Object value) {
        String stringValue = null;
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            stringValue = value.toString();
        } else if (value instanceof String) {
            stringValue = (String) value;
        }
        return stringValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getSafeIntegerFromMap(Map<String, Object> map, String key) {
        int integerValue = 0;
        Object value = map.get(key);
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            integerValue = ((Number) value).intValue();
        } else if (value instanceof String) {
            integerValue = Integer.parseInt((String) value);
        }
        return integerValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long getSafeLongFromMap(Map<String, Object> map, String key) {
        long longValue = 0;
        Object value = map.get(key);
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number) {
            longValue = ((Number) value).longValue();
        } else if (value instanceof String) {
            longValue = Long.parseLong((String) value);
        }
        return longValue;
    }

    static double getSafeDoubleFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            double doubleValue = ((Number) value).doubleValue();
            return doubleValue;
        }
        if (!(value instanceof String)) {
            return 0.0d;
        }
        double doubleValue2 = Double.parseDouble((String) value);
        return doubleValue2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getSafeStringFromMap(Map<String, Object> map, String key) {
        String stringValue = null;
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            stringValue = Integer.toString(((Integer) value).intValue());
        } else if (value instanceof String) {
            stringValue = (String) value;
        }
        return stringValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Map<String, Object> getSafeMapFromMap(Map<String, Object> map, String key) {
        Map<String, Object> mapValue = null;
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Map) {
            mapValue = (Map) value;
        }
        return mapValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<Object> getSafeListFromMap(Map<String, Object> map, String key) {
        List<Object> listValue = null;
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof List) {
            listValue = (List) value;
        }
        return listValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean getSafeBooleanFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Boolean) {
            boolean booleanValue = ((Boolean) value).booleanValue();
            return booleanValue;
        }
        if (value instanceof Integer) {
            return ((Integer) value).intValue() != 0;
        }
        if (!(value instanceof String)) {
            return false;
        }
        boolean booleanValue2 = ((String) value).equalsIgnoreCase(ServerProtocol.DIALOG_RETURN_SCOPES_TRUE);
        return booleanValue2;
    }

    static List<Map<String, Object>> convertToListOfMaps(Object object) {
        if (object != null) {
            try {
                JSONArray array = (JSONArray) toJSON(object);
                if (array != null) {
                    return toList(array);
                }
            } catch (JSONException e) {
                Localytics.Log.w("JSONException", e);
            }
        }
        return null;
    }
}
