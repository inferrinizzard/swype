package com.nuance.swype.input;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;
import com.nuance.android.compat.SharedPreferencesEditorCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public abstract class SwypePreferences {
    private static final String SET_MARSHALL_DELIMITER = ",";
    private final Context context;
    private final Resources res;
    private final SharedPreferences sp;

    /* JADX INFO: Access modifiers changed from: protected */
    public SwypePreferences(Context context, SharedPreferences sp) {
        this.context = context;
        this.res = context.getResources();
        this.sp = sp;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Context getContext() {
        return this.context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SharedPreferences getPreferences() {
        return this.sp;
    }

    public void reset() {
        SharedPreferencesEditorCompat.apply(this.sp.edit().clear());
    }

    public void remove(String key) {
        SharedPreferencesEditorCompat.apply(this.sp.edit().remove(key));
    }

    public boolean contains(String key) {
        return this.sp.contains(key);
    }

    public boolean getBoolean(String key, boolean defval) {
        return this.sp.getBoolean(key, defval);
    }

    public void setBoolean(String key, boolean value) {
        SharedPreferencesEditorCompat.apply(this.sp.edit().putBoolean(key, value));
    }

    public int getInt(String key, int defval) {
        return this.sp.getInt(key, defval);
    }

    public void setInt(String key, int value) {
        SharedPreferencesEditorCompat.apply(this.sp.edit().putInt(key, value));
    }

    public long getLong(String key, long defval) {
        return this.sp.getLong(key, defval);
    }

    public void setFloat(String key, float value) {
        SharedPreferencesEditorCompat.apply(this.sp.edit().putFloat(key, value));
    }

    public float getFloat(String key, float defval) {
        return this.sp.getFloat(key, defval);
    }

    public void setLong(String key, long value) {
        SharedPreferencesEditorCompat.apply(this.sp.edit().putLong(key, value));
    }

    public String getString(String key, String defval) {
        return this.sp.getString(key, defval);
    }

    public Set<String> getStringSet(String key, Set<String> defValues) {
        return this.sp.getStringSet(key, defValues);
    }

    public void setString(String key, String value) {
        SharedPreferencesEditorCompat.apply(this.sp.edit().putString(key, value));
    }

    public boolean getDefaultBoolean(int resId) {
        return this.res.getBoolean(resId);
    }

    public int getDefaultInt(int resId) {
        return this.res.getInteger(resId);
    }

    public String getDefaultString(int resId) {
        return this.res.getString(resId);
    }

    public List<String> getStrings(String key, List<String> defaults) {
        String value = getString(key, null);
        if (value != null) {
            List<String> strings = Arrays.asList(TextUtils.split(value, ","));
            List<String> defaults2 = new ArrayList<>(strings);
            return defaults2;
        }
        return defaults;
    }

    public void setStrings(String key, List<String> values) {
        setString(key, TextUtils.join(",", values));
    }

    public void setStringSet(String key, Set<String> set) {
        this.sp.edit().putStringSet(key, set).apply();
    }

    public String getUpgradedString(String key, String defval, String format) {
        Object value;
        try {
            return this.sp.getString(key, defval);
        } catch (ClassCastException e) {
            if (!this.sp.contains(key) || (value = this.sp.getAll().get(key)) == null) {
                return defval;
            }
            return format == null ? value.toString() : String.format(format, value);
        }
    }

    public String getUpgradedString(String key, String defval) {
        return getUpgradedString(key, defval, null);
    }

    public void importPreferences(SharedPreferences sourceSp) {
        Map<String, ?> props = sourceSp.getAll();
        if (props != null && props.size() > 0) {
            for (Map.Entry<String, ?> pair : props.entrySet()) {
                if (!this.sp.contains(pair.getKey())) {
                    String valueType = pair.getValue().getClass().getSimpleName();
                    if (valueType.equals(Boolean.class.getSimpleName())) {
                        this.sp.edit().putBoolean(pair.getKey(), ((Boolean) pair.getValue()).booleanValue());
                    } else if (valueType.equals(Integer.class.getSimpleName())) {
                        this.sp.edit().putInt(pair.getKey(), ((Integer) pair.getValue()).intValue());
                    } else if (valueType.equals(Long.class.getCanonicalName())) {
                        this.sp.edit().putLong(pair.getKey(), ((Long) pair.getValue()).longValue());
                    } else if (valueType.equals(String.class.getSimpleName())) {
                        this.sp.edit().putString(pair.getKey(), (String) pair.getValue());
                    } else if (valueType.equals(Float.class.getSimpleName())) {
                        this.sp.edit().putFloat(pair.getKey(), ((Float) pair.getValue()).floatValue());
                    }
                }
            }
        }
        this.sp.edit().commit();
    }
}
