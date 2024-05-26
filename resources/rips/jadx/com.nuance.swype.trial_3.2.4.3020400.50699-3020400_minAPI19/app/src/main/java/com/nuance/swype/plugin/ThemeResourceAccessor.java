package com.nuance.swype.plugin;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import com.nuance.swype.util.LogManager;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class ThemeResourceAccessor {
    private static final LogManager.Log log = LogManager.getLog("ThemeResourceAccessor");
    private final String mApkPkgName;
    final HashMap<String, Integer> mAttrName2Values = new HashMap<>();
    private final Map<Integer, Integer> mIdToIndexMap;
    public Resources mResources;
    private final Resources.Theme mTheme;
    private final TypedArray mThemeAttrValues;
    private int[] mThemeAttrs;

    public ThemeResourceAccessor(Resources.Theme currentContextTheme, Resources currentContextResources, int[] themeTemplateAttrs, String packageName, int defStyle) {
        this.mTheme = currentContextTheme;
        this.mResources = currentContextResources;
        this.mThemeAttrs = themeTemplateAttrs;
        this.mIdToIndexMap = new HashMap(this.mThemeAttrs.length);
        for (int i = 0; i < this.mThemeAttrs.length; i++) {
            this.mIdToIndexMap.put(Integer.valueOf(this.mThemeAttrs[i]), Integer.valueOf(i));
        }
        this.mApkPkgName = packageName;
        this.mThemeAttrValues = currentContextTheme.obtainStyledAttributes(defStyle, themeTemplateAttrs);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final TypedValue getAttrTypedValue(int paramInt, boolean bReturnNew) {
        int i;
        if (this.mThemeAttrValues == null) {
            TypedValue localTypedValue = new TypedValue();
            if (this.mTheme.resolveAttribute(paramInt, localTypedValue, true)) {
                return localTypedValue;
            }
            return null;
        }
        if (this.mIdToIndexMap.containsKey(Integer.valueOf(paramInt))) {
            i = this.mIdToIndexMap.get(Integer.valueOf(paramInt)).intValue();
        } else {
            i = -1;
        }
        if (i < 0 || i >= this.mThemeAttrs.length || !this.mThemeAttrValues.hasValue(i)) {
            return null;
        }
        if (bReturnNew) {
            TypedValue localNewTypedValue = new TypedValue();
            this.mThemeAttrValues.getValue(i, localNewTypedValue);
            return localNewTypedValue;
        }
        return this.mThemeAttrValues.peekValue(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final TypedValue obtainAttrTypedValue(String attrName) {
        Integer localInteger = this.mAttrName2Values.get(attrName);
        if (localInteger == null) {
            if (attrName.substring(0, 13).equals("android:attr/")) {
                localInteger = Integer.valueOf(this.mResources.getIdentifier(attrName, null, null));
            } else {
                localInteger = Integer.valueOf(this.mResources.getIdentifier(attrName.substring(attrName.indexOf("/") + 1), "attr", this.mApkPkgName));
            }
            addAttribute(attrName, localInteger);
        }
        int i = localInteger.intValue();
        if (i == 0) {
            return null;
        }
        return getAttrTypedValue(i, true);
    }

    private void addAttribute(String attributeName, Integer value) {
        log.d("Adding ", attributeName, " with value: ", value);
        this.mAttrName2Values.put(attributeName, value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final TypedValue getAttrTypedValue(String attrName) {
        Integer localInteger = this.mAttrName2Values.get(attrName);
        if (localInteger == null) {
            if (attrName.substring(0, 13).equals("android:attr/")) {
                localInteger = Integer.valueOf(this.mResources.getIdentifier(attrName, null, null));
            } else {
                localInteger = Integer.valueOf(this.mResources.getIdentifier(attrName.substring(attrName.indexOf("/") + 1), "attr", this.mApkPkgName));
            }
            addAttribute(attrName, localInteger);
        }
        int i = localInteger.intValue();
        if (i == 0) {
            return null;
        }
        return getAttrTypedValue(i, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CharSequence getText(TypedValue paramTypedValue) {
        if (paramTypedValue == null) {
            return null;
        }
        if (paramTypedValue.type == 3) {
            return paramTypedValue.string;
        }
        return paramTypedValue.coerceToString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final float getDimension(TypedValue paramTypedValue) {
        if (paramTypedValue == null) {
            return 0.0f;
        }
        if (paramTypedValue.type == 5) {
            return paramTypedValue.getDimension(this.mResources.getDisplayMetrics());
        }
        if (paramTypedValue.type == 6 && paramTypedValue != null && paramTypedValue.type == 6) {
            return paramTypedValue.getFraction(1.0f, 1.0f);
        }
        return 0.0f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getBoolean(TypedValue paramTypedValue) {
        if (paramTypedValue != null && paramTypedValue.type >= 16 && paramTypedValue.type <= 31) {
            return paramTypedValue.data != 0 ? 1 : 0;
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getColor(TypedValue paramTypedValue) {
        if (paramTypedValue == null) {
            return -1;
        }
        switch (paramTypedValue.type) {
            case 3:
                return this.mResources.getColor(paramTypedValue.resourceId);
            case 28:
            case 29:
            case 30:
            case 31:
                return paramTypedValue.data;
            default:
                return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Drawable getDrawable(TypedValue paramTypedValue) {
        if (paramTypedValue == null) {
            return null;
        }
        switch (paramTypedValue.type) {
            case 3:
                return this.mResources.getDrawable(paramTypedValue.resourceId);
            case 28:
            case 29:
            case 30:
            case 31:
                return new ColorDrawable(paramTypedValue.data);
            default:
                return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ColorStateList getColorStateList(TypedValue paramTypedValue) {
        if (paramTypedValue == null) {
            return null;
        }
        switch (paramTypedValue.type) {
            case 3:
                return this.mResources.getColorStateList(paramTypedValue.resourceId);
            case 28:
            case 29:
            case 30:
            case 31:
                return ColorStateList.valueOf(paramTypedValue.data);
            default:
                return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void release() {
        log.d("clearing mAttrName2Values");
        this.mAttrName2Values.clear();
        this.mThemeAttrValues.recycle();
        this.mThemeAttrs = null;
        this.mResources = null;
        this.mIdToIndexMap.clear();
    }
}
