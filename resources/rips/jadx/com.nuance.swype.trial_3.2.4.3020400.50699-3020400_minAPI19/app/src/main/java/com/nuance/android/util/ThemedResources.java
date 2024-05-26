package com.nuance.android.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.util.StateSet;
import android.util.TypedValue;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.plugin.MainApkResourceBroker;
import com.nuance.swype.plugin.ThemeApkResourceBroker;
import com.nuance.swype.plugin.ThemeLoader;
import com.nuance.swype.util.LogManager;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public final class ThemedResources extends Resources {
    private static ThemedResources instance;
    protected static final LogManager.Log log = LogManager.getLog("ThemedResources");
    private final IMEApplication imeApp;
    private SparseIntArray rIdMap;
    private Context themedContext;

    public static ThemedResources from(AssetManager assets, DisplayMetrics metrics, Configuration config, Context context) {
        if (instance == null) {
            instance = new ThemedResources(assets, metrics, config, context);
        }
        return instance;
    }

    public static void onThemeChanged() {
        instance = null;
    }

    public static void onConfigChanged() {
        instance = null;
    }

    private ThemedResources(AssetManager assets, DisplayMetrics metrics, Configuration config, Context context) {
        super(assets, metrics, config);
        this.rIdMap = new SparseIntArray();
        this.imeApp = IMEApplication.from(context);
        this.themedContext = this.imeApp.getThemedContext();
    }

    @Override // android.content.res.Resources
    public final TypedArray obtainAttributes(AttributeSet set, int[] attrs) {
        this.imeApp.getThemeLoader().buildResourceIdMap(set, this.rIdMap);
        return this.themedContext.obtainStyledAttributes(set, attrs);
    }

    @Override // android.content.res.Resources
    public final ColorStateList getColorStateList(int id) {
        try {
            XmlResourceParser xml = this.themedContext.getResources().getXml(id);
            return Build.VERSION.SDK_INT < 23 ? ColorStateList.createFromXml(this, new ColorStateListParser(xml, this.themedContext)) : ThemedColorStateList.createFromXml(this.themedContext, xml);
        } catch (Exception ex) {
            log.e("Exception when loading downloadable theme: " + ex.getMessage());
            throw new Resources.NotFoundException("Loading downloadable theme: Failed to load ColorStateList from resource " + Integer.toHexString(id));
        }
    }

    /* loaded from: classes.dex */
    private static class ThemedColorStateList {
        public static ColorStateList createFromXml(Context themedContext, XmlResourceParser parser) throws IOException, XmlPullParserException {
            int j;
            int eventType = parser.next();
            int[] colors = new int[20];
            int[][] states = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, 20, 32);
            int itemCount = 0;
            IMEApplication imeApp = IMEApplication.from(themedContext);
            while (eventType != 1) {
                switch (eventType) {
                    case 2:
                        if (!parser.getName().equals("item")) {
                            break;
                        } else {
                            int attrCount = parser.getAttributeCount();
                            int[] stateSpec = new int[attrCount];
                            int i = 0;
                            int j2 = 0;
                            while (i < attrCount) {
                                String attrName = parser.getAttributeName(i);
                                String attrValue = parser.getAttributeValue(i);
                                int stateResId = parser.getAttributeNameResource(i);
                                ThemedResources.log.d(String.format("\tattrName: %s, attrValue: %s, nameResource %d [%X]", attrName, attrValue, Integer.valueOf(stateResId), Integer.valueOf(stateResId)));
                                if (attrName.equals("color")) {
                                    ThemedResources.log.d("Assigning color");
                                    if (attrValue == null || attrValue.length() <= 1 || !attrValue.startsWith("?")) {
                                        j = j2;
                                    } else {
                                        ThemedResources.log.d("getting themes attr");
                                        try {
                                            int themeAttr = Integer.parseInt(attrValue.substring(1));
                                            colors[itemCount] = imeApp.getThemedColor(themeAttr);
                                            ThemedResources.log.d("ColorStateListParser: themed value is ", Integer.valueOf(colors[itemCount]));
                                            j = j2;
                                        } catch (NumberFormatException e) {
                                            ThemedResources.log.e("ColorStateListParser: exception: " + e.getLocalizedMessage());
                                            j = j2;
                                        }
                                    }
                                } else {
                                    j = j2 + 1;
                                    if (!parser.getAttributeBooleanValue(i, false)) {
                                        stateResId = -stateResId;
                                    }
                                    stateSpec[j2] = stateResId;
                                }
                                i++;
                                j2 = j;
                            }
                            states[itemCount] = StateSet.trimStateSet(stateSpec, j2);
                            itemCount++;
                            break;
                        }
                }
                eventType = parser.next();
            }
            ThemedResources.log.d("Number of items in the color state list are: ", Integer.valueOf(itemCount));
            int[] finalColors = new int[itemCount];
            int[][] finalStates = new int[itemCount];
            System.arraycopy(colors, 0, finalColors, 0, itemCount);
            System.arraycopy(states, 0, finalStates, 0, itemCount);
            return new ColorStateList(finalStates, finalColors);
        }
    }

    private Drawable getDrawableFromThemeApk(int id) {
        int idInApk = this.rIdMap.get(id);
        if (idInApk == 0) {
            return null;
        }
        try {
            this.imeApp.getThemeLoader();
            ThemeLoader.log.d("ThemeLoader.getDrawableInThemeApk(): res name is: ", ThemeLoader.getResourceName$47921032());
            if (MainApkResourceBroker.getInstance().mHasInited && ThemeApkResourceBroker.getInstance().mHasInited) {
                return ThemeApkResourceBroker.getInstance().mResourceAccessor.mResources.getDrawable(idInApk);
            }
            return null;
        } catch (Exception e) {
            this.imeApp.getThemeLoader();
            String message = String.format("Failed to get Drawable from apk: %s. Res name is: %s", Integer.toHexString(idInApk), ThemeLoader.getResourceName$47921032());
            log.e(message);
            return null;
        }
    }

    @Override // android.content.res.Resources
    public final Drawable getDrawable(int id) {
        Drawable drawable = getDrawableFromThemeApk(id);
        return drawable != null ? drawable : super.getDrawable(id);
    }

    @Override // android.content.res.Resources
    @TargetApi(21)
    public final Drawable getDrawable(int id, Resources.Theme theme) {
        Drawable drawable = getDrawableFromThemeApk(id);
        return drawable != null ? drawable : super.getDrawable(id, theme);
    }

    @Override // android.content.res.Resources
    public final InputStream openRawResource(int id, TypedValue value) throws Resources.NotFoundException {
        int idInApk = this.rIdMap.get(id);
        if (idInApk != 0) {
            try {
                this.imeApp.getThemeLoader();
                InputStream in = ThemeLoader.openRawResourceInThemeApk(idInApk);
                if (in != null) {
                    getValue(id, value, true);
                    return in;
                }
            } catch (Exception e) {
                throw new Resources.NotFoundException("Failed to load Raw resource from apk: " + Integer.toHexString(idInApk));
            }
        }
        return super.openRawResource(id, value);
    }
}
