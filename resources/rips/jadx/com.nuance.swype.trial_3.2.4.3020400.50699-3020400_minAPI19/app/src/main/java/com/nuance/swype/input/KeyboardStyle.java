package com.nuance.swype.input;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.util.TypedValue;
import android.util.Xml;
import com.nuance.swype.plugin.ThemeLoader;
import com.nuance.swype.plugin.TypedArrayWrapper;
import com.nuance.swype.util.LogManager;
import org.xmlpull.v1.XmlPullParser;

/* loaded from: classes.dex */
public class KeyboardStyle {
    private static DrawBufferManager drawableCache;
    protected static final LogManager.Log log = LogManager.getLog("KeyboardStyle");
    private final Context context;
    private final Resources res;
    private final SparseArray<TypedValueWrapper[]> styles;
    ThemeLoader themeLoaderRef;

    /* loaded from: classes.dex */
    public enum StyleLevel {
        BASE,
        KEYBOARD,
        KEY,
        ALL
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class TypedValueWrapper {
        boolean bFromThemeApk;
        TypedValue value;

        TypedValueWrapper(TypedValue value, boolean fromApk) {
            this.value = value;
            this.bFromThemeApk = fromApk;
        }

        public TypedValue getRawValue() {
            return this.value;
        }
    }

    public KeyboardStyle(Context context, int styleId) {
        this(context, styleId, new int[][]{R.styleable.KeyboardViewEx, R.styleable.KeyboardEx, R.styleable.KeyboardEx_Key});
    }

    public KeyboardStyle(Context context, int styleId, int[][] styleables) {
        this.styles = new SparseArray<>();
        this.context = context;
        this.res = context.getResources();
        this.themeLoaderRef = IMEApplication.from(context).getThemeLoader();
        for (int i = 0; i < 3; i++) {
            TypedArrayWrapper attr = ThemeLoader.getInstance().obtainStyledAttributes$6d3b0587(context, null, styleables[i], 0, styleId, R.xml.defaults, "SwypeReference");
            loadStyle(attr, styleables[i], StyleLevel.BASE);
            attr.recycle();
        }
    }

    public void loadKeyboard(XmlPullParser parser, boolean clearCurrent) {
        if (clearCurrent) {
            clearLevel(StyleLevel.KEYBOARD);
        }
        TypedArrayWrapper attr = ThemeLoader.getInstance().obtainStyledAttributes$6d3b0587(this.context, Xml.asAttributeSet(parser), R.styleable.KeyboardViewEx, 0, 0, 0, null);
        loadStyle(attr, R.styleable.KeyboardViewEx, StyleLevel.KEYBOARD);
        attr.recycle();
        TypedArrayWrapper attr2 = ThemeLoader.getInstance().obtainStyledAttributes$6d3b0587(this.context, Xml.asAttributeSet(parser), R.styleable.KeyboardEx, 0, 0, 0, null);
        loadStyle(attr2, R.styleable.KeyboardEx, StyleLevel.KEYBOARD);
        attr2.recycle();
    }

    public void loadKey(XmlPullParser parser) {
        clearKey();
        TypedArrayWrapper attr = ThemeLoader.getInstance().obtainStyledAttributes$6d3b0587(this.context, Xml.asAttributeSet(parser), R.styleable.KeyboardEx_Key, 0, 0, 0, null);
        loadStyle(attr, R.styleable.KeyboardEx_Key, StyleLevel.KEY);
    }

    public void clearKey() {
        clearLevel(StyleLevel.KEY);
    }

    public int getDimensionOrFraction(int index, int base, int defValue) {
        TypedValueWrapper value = getValue(index);
        float returnValue = defValue;
        if (value != null) {
            if (value.getRawValue().type == 5) {
                returnValue = value.getRawValue().getDimension(this.res.getDisplayMetrics());
            } else if (value.getRawValue().type == 6) {
                returnValue = value.getRawValue().getFraction(base, base);
            }
        }
        return Math.round(returnValue);
    }

    public int getDimensionPixelSize(int index, int defValue) {
        TypedValueWrapper value = getValue(index);
        if (value == null) {
            return defValue;
        }
        int defValue2 = Math.round(value.getRawValue().getDimension(this.res.getDisplayMetrics()));
        return defValue2;
    }

    public boolean getValue(int index, TypedValue outValue) {
        TypedValueWrapper value = getValue(index);
        if (value == null) {
            return false;
        }
        outValue.setTo(value.getRawValue());
        return true;
    }

    public boolean getBoolean(int index, boolean defValue) {
        TypedValueWrapper value = getValue(index);
        return value != null ? value.getRawValue().data != 0 : defValue;
    }

    public int getInt(int index, int defValue) {
        TypedValueWrapper value = getValue(index);
        if (value == null) {
            return defValue;
        }
        int defValue2 = value.getRawValue().data;
        return defValue2;
    }

    public int getResourceId(int index) {
        return getResourceId(index, StyleLevel.ALL);
    }

    public int getResourceId(int index, StyleLevel level) {
        TypedValueWrapper value = getValue(index, level);
        if (value != null) {
            return value.getRawValue().resourceId;
        }
        return 0;
    }

    public Drawable getDrawable(int index) {
        TypedValueWrapper value = getValue(index);
        if (value == null) {
            return null;
        }
        int resId = value.getRawValue().resourceId;
        if (resId != 0) {
            log.d("getting drawable for: ", this.res.getResourceName(resId));
        } else {
            log.d("getting drawable for: ", "<res id is 0>");
        }
        return value.bFromThemeApk ? ThemeLoader.getThemedDrawable(value.getRawValue()) : this.res.getDrawable(value.getRawValue().resourceId);
    }

    public static void recycleDrawable() {
        if (drawableCache != null) {
            drawableCache.evictAll();
            drawableCache = null;
        }
    }

    public CharSequence getText(int index) {
        TypedValueWrapper value = getValue(index);
        if (value != null) {
            return value.getRawValue().string;
        }
        return null;
    }

    public ColorStateList getColorStateList(int index, int defValue) {
        TypedValueWrapper value = getValue(index);
        if (value != null && value.getRawValue().type == 3) {
            try {
                return value.bFromThemeApk ? ThemeLoader.getThemedColorStateList(value.getRawValue()) : this.res.getColorStateList(value.getRawValue().resourceId);
            } catch (Exception e) {
                return ColorStateList.valueOf(defValue);
            }
        }
        return ColorStateList.valueOf(getInt(index, defValue));
    }

    private TypedValueWrapper getValue(int index) {
        return getValue(index, StyleLevel.ALL);
    }

    private TypedValueWrapper getValue(int index, StyleLevel level) {
        TypedValueWrapper result = null;
        TypedValueWrapper[] values = this.styles.get(index);
        if (values == null) {
            return null;
        }
        if (level == StyleLevel.ALL) {
            for (int i = StyleLevel.KEY.ordinal(); i >= 0; i--) {
                result = values[i];
                if (result != null) {
                    return result;
                }
            }
            return result;
        }
        TypedValueWrapper result2 = values[level.ordinal()];
        return result2;
    }

    private void loadStyle(TypedArrayWrapper attr, int[] attrIndexes, StyleLevel level) {
        boolean value;
        TypedValue typedValue;
        TypedValue rawvalue = new TypedValue();
        int count = attr.delegateTypedArray.length();
        for (int i = 0; i < count; i++) {
            if (attr.tValsStyledByApk != null && (typedValue = attr.tValsStyledByApk.get(i)) != null) {
                rawvalue.setTo(typedValue);
                value = true;
            } else {
                value = attr.delegateTypedArray.getValue(i, rawvalue);
            }
            if (value) {
                int attrIndex = attrIndexes[i];
                TypedValueWrapper[] values = this.styles.get(attrIndex);
                if (values == null) {
                    values = new TypedValueWrapper[StyleLevel.values().length - 1];
                    this.styles.put(attrIndex, values);
                }
                values[level.ordinal()] = new TypedValueWrapper(rawvalue, (attr.tValsStyledByApk == null || attr.tValsStyledByApk.get(i) == null) ? false : true);
                rawvalue = new TypedValue();
            }
        }
    }

    private void clearLevel(StyleLevel level) {
        for (int i = this.styles.size() - 1; i >= 0; i--) {
            TypedValueWrapper[] values = this.styles.valueAt(i);
            if (values != null) {
                values[level.ordinal()] = null;
            }
        }
    }

    public boolean getDefaultKeyStyleSetting(int[] codes, int attr, boolean fallback) {
        return IMEApplication.from(this.context).getDefaultKeyStyleSetting(codes, attr, fallback);
    }
}
