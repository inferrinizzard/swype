package android.support.v7.content.res;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import java.util.WeakHashMap;
import org.xmlpull.v1.XmlPullParser;

/* loaded from: classes.dex */
public final class AppCompatResources {
    private static final ThreadLocal<TypedValue> TL_TYPED_VALUE = new ThreadLocal<>();
    private static final WeakHashMap<Context, SparseArray<ColorStateListCacheEntry>> sColorStateCaches = new WeakHashMap<>(0);
    private static final Object sColorStateCacheLock = new Object();

    public static ColorStateList getColorStateList(Context context, int resId) {
        if (Build.VERSION.SDK_INT >= 23) {
            return context.getColorStateList(resId);
        }
        ColorStateList csl = getCachedColorStateList(context, resId);
        if (csl == null) {
            ColorStateList csl2 = inflateColorStateList(context, resId);
            if (csl2 == null) {
                return ContextCompat.getColorStateList(context, resId);
            }
            synchronized (sColorStateCacheLock) {
                SparseArray<ColorStateListCacheEntry> sparseArray = sColorStateCaches.get(context);
                if (sparseArray == null) {
                    sparseArray = new SparseArray<>();
                    sColorStateCaches.put(context, sparseArray);
                }
                sparseArray.append(resId, new ColorStateListCacheEntry(csl2, context.getResources().getConfiguration()));
            }
            return csl2;
        }
        return csl;
    }

    private static ColorStateList getCachedColorStateList(Context context, int resId) {
        ColorStateListCacheEntry entry;
        synchronized (sColorStateCacheLock) {
            SparseArray<ColorStateListCacheEntry> entries = sColorStateCaches.get(context);
            if (entries != null && entries.size() > 0 && (entry = entries.get(resId)) != null) {
                if (entry.configuration.equals(context.getResources().getConfiguration())) {
                    return entry.value;
                }
                entries.remove(resId);
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ColorStateListCacheEntry {
        final Configuration configuration;
        final ColorStateList value;

        ColorStateListCacheEntry(ColorStateList value, Configuration configuration) {
            this.value = value;
            this.configuration = configuration;
        }
    }

    private static ColorStateList inflateColorStateList(Context context, int resId) {
        Resources resources = context.getResources();
        TypedValue typedValue = TL_TYPED_VALUE.get();
        if (typedValue == null) {
            typedValue = new TypedValue();
            TL_TYPED_VALUE.set(typedValue);
        }
        resources.getValue(resId, typedValue, true);
        if (typedValue.type >= 28 && typedValue.type <= 31) {
            return null;
        }
        Resources r = context.getResources();
        XmlPullParser xml = r.getXml(resId);
        try {
            return AppCompatColorStateListInflater.createFromXml(r, xml, context.getTheme());
        } catch (Exception e) {
            Log.e("AppCompatResources", "Failed to inflate ColorStateList, leaving it to the framework", e);
            return null;
        }
    }
}
