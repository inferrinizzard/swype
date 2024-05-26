package com.nuance.swype.plugin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.nuance.android.compat.ViewCompat;
import com.nuance.swype.plugin.ThemeLayoutAttributeParser;
import com.nuance.swype.util.LogManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Stack;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public final class ThemeLoader {
    public static final LogManager.Log log = LogManager.getLog("ThemeLoader");
    private static ThemeLoader sThemeLoader;
    private int defStyle;
    private int[] themeTemplateAttrs;
    public boolean bThemeApkLoaded = false;
    private final Stack<ThemeLayoutAttributeParser> mParsers = new Stack<>();

    public static ThemeLoader getInstance() {
        if (sThemeLoader == null) {
            sThemeLoader = new ThemeLoader();
        }
        return sThemeLoader;
    }

    public final void setLayoutInflaterFactory(LayoutInflater inflater) {
        if (this.bThemeApkLoaded) {
            ThemeLayoutAttributeParser parser = new ThemeLayoutAttributeParser();
            parser.clear();
            inflater.setFactory(parser.mLayoutInflateFactory);
            this.mParsers.push(parser);
        }
    }

    public final void applyTheme(View view) {
        TypedValue typedValue;
        ThemeResourceAccessor themeResourceAccessor;
        log.d("ThemeLoader.applyTheme(), Applying theme to view: ", view.getClass().getSimpleName());
        if (this.bThemeApkLoaded) {
            ThemeLayoutAttributeParser parser = null;
            try {
                parser = this.mParsers.pop();
            } catch (EmptyStackException e) {
                log.d("applyTheme", e);
            }
            if (parser != null) {
                ThemeLayoutAttributeParser.log.d("in applyThemeFromCurrentThemeApk()");
                if (!ThemeApkResourceBroker.getInstance().mHasInited) {
                    ThemeLayoutAttributeParser.log.d("apk resource broker not initialized. Returning");
                    return;
                }
                ThemeResourceAccessor themeResourceAccessor2 = ThemeApkResourceBroker.getInstance().mResourceAccessor;
                Iterator<ThemeLayoutAttributeParser.ThemeStyledItem> it = parser.mLayoutInflateFactory.mThemeItems.iterator();
                ThemeResourceAccessor themeResourceAccessor3 = themeResourceAccessor2;
                while (it.hasNext()) {
                    ThemeLayoutAttributeParser.ThemeStyledItem next = it.next();
                    Iterator<ThemeAttrAssociation> it2 = next.mThemeAttrs.iterator();
                    ThemeResourceAccessor themeResourceAccessor4 = themeResourceAccessor3;
                    while (it2.hasNext()) {
                        ThemeAttrAssociation next2 = it2.next();
                        ThemeLayoutAttributeParser.log.d("Processing ", next2.attrName);
                        TypedValue attrTypedValue = themeResourceAccessor4.getAttrTypedValue(next2.attrStyleableFullName);
                        if (attrTypedValue == null) {
                            ThemeResourceAccessor themeResourceAccessor5 = MainApkResourceBroker.getInstance().mResourceAccessor;
                            TypedValue attrTypedValue2 = themeResourceAccessor5.getAttrTypedValue(next2.attrStyleableFullName);
                            LogManager.Log log2 = ThemeLayoutAttributeParser.log;
                            Object[] objArr = new Object[2];
                            objArr[0] = "attr typed value is: ";
                            objArr[1] = attrTypedValue2 == null ? null : attrTypedValue2.coerceToString();
                            log2.d(objArr);
                            themeResourceAccessor = themeResourceAccessor5;
                            typedValue = attrTypedValue2;
                        } else {
                            typedValue = attrTypedValue;
                            themeResourceAccessor = themeResourceAccessor4;
                        }
                        LogManager.Log log3 = ThemeLayoutAttributeParser.log;
                        Object[] objArr2 = new Object[2];
                        objArr2[0] = "tVal null?";
                        objArr2[1] = Boolean.valueOf(typedValue == null);
                        log3.d(objArr2);
                        if (typedValue != null) {
                            ThemeLayoutAttributeParser.log.d("tVal is: ", typedValue.coerceToString());
                            View findViewById = view.findViewById(next.id);
                            if (findViewById == null) {
                                ThemeLayoutAttributeParser.log.d("localView is null");
                                themeResourceAccessor4 = themeResourceAccessor;
                            } else {
                                ThemeLayoutAttributeParser.log.d("Apply theme to:", findViewById.getClass().getSimpleName());
                                if ("background".equals(next2.attrName)) {
                                    ThemeLayoutAttributeParser.log.d("Applying background: ", typedValue.coerceToString());
                                    ViewCompat.setBackground(findViewById, themeResourceAccessor.getDrawable(typedValue));
                                    themeResourceAccessor4 = themeResourceAccessor;
                                } else if (findViewById instanceof TextView) {
                                    if ("textColor".equals(next2.attrName)) {
                                        ((TextView) findViewById).setTextColor(typedValue.data);
                                        themeResourceAccessor4 = themeResourceAccessor;
                                    } else if ("textSize".equals(next2.attrName)) {
                                        ((TextView) findViewById).setTextSize(typedValue.getDimension(ThemeApkResourceBroker.getInstance().mThemeApkInfo.apkResources.getDisplayMetrics()));
                                        themeResourceAccessor4 = themeResourceAccessor;
                                    } else if ((findViewById instanceof CompoundButton) && "button".equals(next2.attrName)) {
                                        ((CompoundButton) findViewById).setButtonDrawable(themeResourceAccessor.getDrawable(typedValue));
                                        themeResourceAccessor4 = themeResourceAccessor;
                                    }
                                } else if (findViewById instanceof AbsListView) {
                                    if ("listSelector".equals(next2.attrName)) {
                                        ((AbsListView) findViewById).setSelector(themeResourceAccessor.getDrawable(typedValue));
                                        themeResourceAccessor4 = themeResourceAccessor;
                                    } else if ((findViewById instanceof ListView) && "divider".equals(next2.attrName)) {
                                        ((ListView) findViewById).setDivider(themeResourceAccessor.getDrawable(typedValue));
                                        themeResourceAccessor4 = themeResourceAccessor;
                                    }
                                } else if ((findViewById instanceof ImageView) && "src".equals(next2.attrName)) {
                                    ((ImageView) findViewById).setImageDrawable(themeResourceAccessor.getDrawable(typedValue));
                                }
                            }
                        }
                        themeResourceAccessor4 = themeResourceAccessor;
                    }
                    themeResourceAccessor3 = themeResourceAccessor4;
                }
                parser.clear();
            }
        }
    }

    public static boolean getThemedBoolean(int attrValueResId) {
        if (!MainApkResourceBroker.getInstance().mHasInited) {
            return false;
        }
        if (ThemeApkResourceBroker.getInstance().mHasInited) {
            String attrName = MainApkResourceBroker.getInstance().mContext.getResources().getResourceName(attrValueResId);
            int bVal = ThemeResourceAccessor.getBoolean(ThemeApkResourceBroker.getInstance().mResourceAccessor.getAttrTypedValue(attrName));
            if (bVal != -1) {
                return bVal > 0;
            }
        }
        return ThemeResourceAccessor.getBoolean(MainApkResourceBroker.getInstance().mResourceAccessor.getAttrTypedValue(attrValueResId, false)) > 0;
    }

    public static boolean getThemedBoolean(TypedValue tVal) {
        int bVal;
        return MainApkResourceBroker.getInstance().mHasInited && (bVal = ThemeResourceAccessor.getBoolean(tVal)) != -1 && bVal > 0;
    }

    public static int getThemedColor(int attrValueResId) {
        if (MainApkResourceBroker.getInstance().mHasInited) {
            if (ThemeApkResourceBroker.getInstance().mHasInited) {
                String attrName = MainApkResourceBroker.getInstance().mContext.getResources().getResourceName(attrValueResId);
                ThemeResourceAccessor themeResourceAccessor = ThemeApkResourceBroker.getInstance().mResourceAccessor;
                themeResourceAccessor.getAttrTypedValue(attrName);
                if (themeResourceAccessor.mAttrName2Values.containsKey(attrName)) {
                    ThemeResourceAccessor themeResourceAccessor2 = ThemeApkResourceBroker.getInstance().mResourceAccessor;
                    return themeResourceAccessor2.getColor(themeResourceAccessor2.getAttrTypedValue(attrName));
                }
            }
            ThemeResourceAccessor themeResourceAccessor3 = MainApkResourceBroker.getInstance().mResourceAccessor;
            return themeResourceAccessor3.getColor(themeResourceAccessor3.getAttrTypedValue(attrValueResId, false));
        }
        return -1;
    }

    public static int getThemedColor(TypedValue tVal) {
        int colorVal;
        if (!MainApkResourceBroker.getInstance().mHasInited) {
            return -1;
        }
        if (!ThemeApkResourceBroker.getInstance().mHasInited || (colorVal = ThemeApkResourceBroker.getInstance().mResourceAccessor.getColor(tVal)) == -1) {
            return MainApkResourceBroker.getInstance().mResourceAccessor.getColor(tVal);
        }
        return colorVal;
    }

    public static ColorStateList getThemedColorStateList(int attrValueResId) {
        if (MainApkResourceBroker.getInstance().mHasInited) {
            if (ThemeApkResourceBroker.getInstance().mHasInited) {
                String attrName = MainApkResourceBroker.getInstance().mContext.getResources().getResourceName(attrValueResId);
                ThemeResourceAccessor themeResourceAccessor = ThemeApkResourceBroker.getInstance().mResourceAccessor;
                ColorStateList colorVal = themeResourceAccessor.getColorStateList(themeResourceAccessor.getAttrTypedValue(attrName));
                if (colorVal != null) {
                    return colorVal;
                }
            }
            ThemeResourceAccessor themeResourceAccessor2 = MainApkResourceBroker.getInstance().mResourceAccessor;
            return themeResourceAccessor2.getColorStateList(themeResourceAccessor2.getAttrTypedValue(attrValueResId, false));
        }
        return null;
    }

    public static ColorStateList getThemedColorStateList(TypedValue tVal) {
        ColorStateList colorVal;
        if (MainApkResourceBroker.getInstance().mHasInited) {
            if (!ThemeApkResourceBroker.getInstance().mHasInited || (colorVal = ThemeApkResourceBroker.getInstance().mResourceAccessor.getColorStateList(tVal)) == null) {
                return MainApkResourceBroker.getInstance().mResourceAccessor.getColorStateList(tVal);
            }
            return colorVal;
        }
        return null;
    }

    public static Drawable getThemedDrawable(int attrValueResId) {
        if (MainApkResourceBroker.getInstance().mHasInited) {
            if (ThemeApkResourceBroker.getInstance().mHasInited) {
                String attrName = MainApkResourceBroker.getInstance().mContext.getResources().getResourceName(attrValueResId);
                ThemeResourceAccessor themeResourceAccessor = ThemeApkResourceBroker.getInstance().mResourceAccessor;
                Drawable dVal = themeResourceAccessor.getDrawable(themeResourceAccessor.getAttrTypedValue(attrName));
                if (dVal != null) {
                    return dVal;
                }
            }
            ThemeResourceAccessor themeResourceAccessor2 = MainApkResourceBroker.getInstance().mResourceAccessor;
            return themeResourceAccessor2.getDrawable(themeResourceAccessor2.getAttrTypedValue(attrValueResId, false));
        }
        return null;
    }

    public static Drawable getThemedDrawable(TypedValue tVal) {
        LogManager.Log log2 = log;
        int i = tVal.resourceId;
        log2.d("ThemeLoader.getThemedDrawable. Resource name is: ", "<release build>");
        if (MainApkResourceBroker.getInstance().mHasInited) {
            log.d("Main broker is initialized.");
            if (ThemeApkResourceBroker.getInstance().mHasInited) {
                log.d("APK broker is initialized");
                Drawable dVal = ThemeApkResourceBroker.getInstance().mResourceAccessor.getDrawable(tVal);
                if (dVal != null) {
                    log.d("returning from APK. dVal is not null");
                    return dVal;
                }
            }
            ThemeResourceAccessor accessor = MainApkResourceBroker.getInstance().mResourceAccessor;
            log.d("got mResourceAccessor from main. Returning drawable");
            return accessor.getDrawable(tVal);
        }
        log.d("returning null");
        return null;
    }

    public static CharSequence getThemedText(TypedValue tVal) {
        if (MainApkResourceBroker.getInstance().mHasInited) {
            return ThemeResourceAccessor.getText(tVal);
        }
        return null;
    }

    public static float getThemedDimension(int attrValueResId) {
        if (MainApkResourceBroker.getInstance().mHasInited) {
            if (ThemeApkResourceBroker.getInstance().mHasInited) {
                String attrName = MainApkResourceBroker.getInstance().mContext.getResources().getResourceName(attrValueResId);
                ThemeResourceAccessor themeResourceAccessor = ThemeApkResourceBroker.getInstance().mResourceAccessor;
                return themeResourceAccessor.getDimension(themeResourceAccessor.getAttrTypedValue(attrName));
            }
            ThemeResourceAccessor themeResourceAccessor2 = MainApkResourceBroker.getInstance().mResourceAccessor;
            return themeResourceAccessor2.getDimension(themeResourceAccessor2.getAttrTypedValue(attrValueResId, false));
        }
        return 0.0f;
    }

    public static float getThemedDimension(TypedValue tVal) {
        if (MainApkResourceBroker.getInstance().mHasInited) {
            if (ThemeApkResourceBroker.getInstance().mHasInited) {
                return ThemeApkResourceBroker.getInstance().mResourceAccessor.getDimension(tVal);
            }
            return MainApkResourceBroker.getInstance().mResourceAccessor.getDimension(tVal);
        }
        return 0.0f;
    }

    @SuppressLint({"Recycle"})
    public final TypedArrayWrapper obtainStyledAttributes$6d3b0587(Context context, AttributeSet mainAttrsSet, int[] attrs, int defStyleAttr, int defStyleRes, int defXmlAttr, String defStyleResTag) {
        int attr_index;
        SparseArray<TypedValue> typedValsInApk = null;
        TypedArray taMain = context.obtainStyledAttributes(mainAttrsSet, attrs, defStyleAttr, defStyleRes);
        if (this.bThemeApkLoaded && MainApkResourceBroker.getInstance().mHasInited && ThemeApkResourceBroker.getInstance().mHasInited) {
            SparseIntArray attrsMap = new SparseIntArray();
            for (int i = 0; i < attrs.length; i++) {
                attrsMap.put(attrs[i], i);
            }
            typedValsInApk = new SparseArray<>();
            if (mainAttrsSet != null) {
                obtainTypedValuesFromThemeApk(attrsMap, mainAttrsSet, typedValsInApk);
            }
            if (defStyleAttr != 0) {
                AttributeSet attrSetDefAttr = loadDefValXMLAsAttributeSet(context, defXmlAttr, null);
                obtainTypedValuesFromThemeApk(attrsMap, attrSetDefAttr, typedValsInApk);
            }
            if (defStyleRes != 0) {
                AttributeSet attrSetDefRes = loadDefValXMLAsAttributeSet(context, defXmlAttr, defStyleResTag);
                obtainTypedValuesFromThemeApk(attrsMap, attrSetDefRes, typedValsInApk);
            }
            MainApkResourceBroker localResMgr = MainApkResourceBroker.getInstance();
            ThemeResourceAccessor accessor = ThemeApkResourceBroker.getInstance().mResourceAccessor;
            for (int i2 = 0; i2 < attrsMap.size(); i2++) {
                int attrResId = attrsMap.keyAt(i2);
                if (localResMgr.mThemeStyleableAttrResIds.contains(Integer.valueOf(attrResId)) && (attr_index = attrsMap.valueAt(i2)) >= 0) {
                    String attrFullName = localResMgr.mContext.getResources().getResourceName(attrResId);
                    TypedValue tValFoundInApk = accessor.obtainAttrTypedValue(attrFullName);
                    if (tValFoundInApk != null) {
                        typedValsInApk.put(attr_index, tValFoundInApk);
                    }
                }
            }
        }
        return new TypedArrayWrapper(taMain, typedValsInApk);
    }

    private static AttributeSet loadDefValXMLAsAttributeSet(Context context, int xmlRId, String Deftag) {
        XmlResourceParser parser = context.getResources().getXml(xmlRId);
        int state = 0;
        do {
            try {
                state = parser.next();
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
            if (state == 2 && parser.getName().equals(Deftag)) {
                return Xml.asAttributeSet(parser);
            }
        } while (state != 1);
        return null;
    }

    private static void obtainTypedValuesFromThemeApk(SparseIntArray attrsMap, AttributeSet attrSet, SparseArray<TypedValue> typedValsInApk) {
        int count = attrSet.getAttributeCount();
        MainApkResourceBroker localResMgr = MainApkResourceBroker.getInstance();
        ThemeResourceAccessor accessor = ThemeApkResourceBroker.getInstance().mResourceAccessor;
        for (int j = 0; j < count && attrsMap.size() != 0; j++) {
            int attrRIdInSet = attrSet.getAttributeNameResource(j);
            log.d("checking:" + attrSet.getAttributeName(j));
            int attr_index = attrsMap.get(attrRIdInSet, -1);
            if (attr_index >= 0) {
                attrsMap.delete(attrRIdInSet);
            }
            String attrValueIdStr = attrSet.getAttributeValue(j);
            if (attrValueIdStr.startsWith("?") && attrValueIdStr.length() > 1) {
                try {
                    int attrResId = Integer.parseInt(attrValueIdStr.substring(1));
                    if (localResMgr.mThemeStyleableAttrResIds.contains(Integer.valueOf(attrResId)) && attr_index >= 0) {
                        String attrFullName = localResMgr.mContext.getResources().getResourceName(attrResId);
                        TypedValue tValFoundInApk = accessor.obtainAttrTypedValue(attrFullName);
                        if (tValFoundInApk != null) {
                            typedValsInApk.put(attr_index, tValFoundInApk);
                        }
                    }
                } catch (Resources.NotFoundException e) {
                    log.e("LayoutInflaterFactory: parse attributeValueReferenceName Exception");
                } catch (NumberFormatException e2) {
                    log.e("LayoutInflaterFactory: parse attributeValueReferenceId Exception");
                }
            }
        }
    }

    public final boolean buildResourceIdMap(AttributeSet paramAttributeSet, SparseIntArray paramArrayList) {
        TypedValue tVal;
        String resourceName;
        if (paramArrayList == null) {
            return false;
        }
        if (!this.bThemeApkLoaded || !ThemeApkResourceBroker.getInstance().mHasInited) {
            paramArrayList.clear();
            return false;
        }
        MainApkResourceBroker localResMgr = MainApkResourceBroker.getInstance();
        for (int j = 0; j < paramAttributeSet.getAttributeCount(); j++) {
            String attrResIdStr = paramAttributeSet.getAttributeValue(j);
            if (attrResIdStr.startsWith("?")) {
                try {
                    int attrResId = Integer.parseInt(attrResIdStr.substring(1));
                    if (localResMgr.mThemeStyleableAttrResIds.contains(Integer.valueOf(attrResId))) {
                        TypedValue t = MainApkResourceBroker.getInstance().mHasInited ? MainApkResourceBroker.getInstance().mResourceAccessor.getAttrTypedValue(attrResId, false) : null;
                        if (t != null) {
                            int attrThemedValueRId = t.resourceId;
                            if (!MainApkResourceBroker.getInstance().mHasInited || !ThemeApkResourceBroker.getInstance().mHasInited || (resourceName = MainApkResourceBroker.getInstance().mContext.getResources().getResourceName(attrResId)) == null || (tVal = ThemeApkResourceBroker.getInstance().mResourceAccessor.getAttrTypedValue(resourceName)) == null) {
                                tVal = null;
                            }
                            if (tVal != null && tVal.resourceId != 0) {
                                paramArrayList.put(attrThemedValueRId, tVal.resourceId);
                            }
                        }
                    }
                } catch (Resources.NotFoundException e) {
                    log.e("LayoutInflaterFactory: parse attributeValueReferenceName Exception");
                } catch (NumberFormatException e2) {
                    log.e("LayoutInflaterFactory: parse attributeValueReferenceId Exception");
                }
            }
        }
        return true;
    }

    public static InputStream openRawResourceInThemeApk(int id) throws Resources.NotFoundException {
        if (MainApkResourceBroker.getInstance().mHasInited && ThemeApkResourceBroker.getInstance().mHasInited) {
            return ThemeApkResourceBroker.getInstance().mResourceAccessor.mResources.openRawResource(id);
        }
        return null;
    }

    public final boolean init(int[] themeStyleableAttrs, int defStyle) {
        if (themeStyleableAttrs == null) {
            return false;
        }
        this.defStyle = defStyle;
        this.themeTemplateAttrs = themeStyleableAttrs;
        return true;
    }

    public final void loadThemeApkFile(Context themedMainContext, String themeApkPath) {
        if (themedMainContext != null && themeApkPath != null) {
            if (!this.bThemeApkLoaded) {
                MainApkResourceBroker mainApkResourceBroker = MainApkResourceBroker.getInstance();
                mainApkResourceBroker.mContext = themedMainContext;
                if (!mainApkResourceBroker.mHasInited) {
                    for (int i : this.themeTemplateAttrs) {
                        mainApkResourceBroker.mThemeStyleableAttrResIds.add(Integer.valueOf(i));
                    }
                } else if (mainApkResourceBroker.mResourceAccessor != null) {
                    mainApkResourceBroker.mResourceAccessor.release();
                }
                mainApkResourceBroker.mResourceAccessor = new ThemeResourceAccessor(themedMainContext.getTheme(), themedMainContext.getResources(), this.themeTemplateAttrs, mainApkResourceBroker.mContext.getApplicationInfo().packageName, this.defStyle);
                mainApkResourceBroker.mHasInited = true;
            }
            ThemeApkResourceBroker themeApkResourceBroker = ThemeApkResourceBroker.getInstance();
            ThemeApkInfo themeApkInfo = ThemeApkInfo.fromStaticApkFile(themedMainContext, themeApkPath);
            if (themeApkInfo != null && !themeApkInfo.equals(themeApkResourceBroker.mThemeApkInfo) && themeApkResourceBroker.setThemeFromApk(themeApkInfo)) {
                this.bThemeApkLoaded = true;
            }
        }
    }

    public final void clear() {
        if (this.bThemeApkLoaded) {
            if (ThemeApkResourceBroker.getInstance().mHasInited) {
                ThemeApkResourceBroker themeApkResourceBroker = ThemeApkResourceBroker.getInstance();
                themeApkResourceBroker.mResourceAccessor.release();
                themeApkResourceBroker.mResourceAccessor = null;
                themeApkResourceBroker.mThemeApkInfo = null;
                themeApkResourceBroker.mHasInited = false;
                if (ThemeApkResourceBroker.sThemeApkResBroker != null) {
                    ThemeApkResourceBroker.sThemeApkResBroker = null;
                }
            }
            if (MainApkResourceBroker.getInstance().mHasInited) {
                MainApkResourceBroker mainApkResourceBroker = MainApkResourceBroker.getInstance();
                mainApkResourceBroker.mResourceAccessor.release();
                mainApkResourceBroker.mResourceAccessor = null;
                mainApkResourceBroker.mThemeStyleableAttrResIds.clear();
                mainApkResourceBroker.mContext = null;
                mainApkResourceBroker.mHasInited = false;
                if (MainApkResourceBroker.sLocalResBroker != null) {
                    MainApkResourceBroker.sLocalResBroker = null;
                }
            }
            this.mParsers.clear();
            this.bThemeApkLoaded = false;
        }
    }

    public static String getResourceName$47921032() {
        return "<release build>";
    }
}
