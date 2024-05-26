package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.ContainerHelpers;
import android.support.v4.util.LongSparseArray;
import android.support.v4.util.LruCache;
import android.support.v7.appcompat.R;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.util.Xml;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public final class AppCompatDrawableManager {
    private static AppCompatDrawableManager INSTANCE;
    private ArrayMap<String, InflateDelegate> mDelegates;
    private final Object mDrawableCacheLock = new Object();
    private final WeakHashMap<Context, LongSparseArray<WeakReference<Drawable.ConstantState>>> mDrawableCaches = new WeakHashMap<>(0);
    private boolean mHasCheckedVectorDrawableSetup;
    private SparseArray<String> mKnownDrawableIdTags;
    private WeakHashMap<Context, SparseArray<ColorStateList>> mTintLists;
    private TypedValue mTypedValue;
    private static final PorterDuff.Mode DEFAULT_MODE = PorterDuff.Mode.SRC_IN;
    private static final ColorFilterLruCache COLOR_FILTER_CACHE = new ColorFilterLruCache();
    private static final int[] COLORFILTER_TINT_COLOR_CONTROL_NORMAL = {R.drawable.abc_textfield_search_default_mtrl_alpha, R.drawable.abc_textfield_default_mtrl_alpha, R.drawable.abc_ab_share_pack_mtrl_alpha};
    private static final int[] TINT_COLOR_CONTROL_NORMAL = {R.drawable.abc_ic_commit_search_api_mtrl_alpha, R.drawable.abc_seekbar_tick_mark_material, R.drawable.abc_ic_menu_share_mtrl_alpha, R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.drawable.abc_ic_menu_cut_mtrl_alpha, R.drawable.abc_ic_menu_selectall_mtrl_alpha, R.drawable.abc_ic_menu_paste_mtrl_am_alpha};
    private static final int[] COLORFILTER_COLOR_CONTROL_ACTIVATED = {R.drawable.abc_textfield_activated_mtrl_alpha, R.drawable.abc_textfield_search_activated_mtrl_alpha, R.drawable.abc_cab_background_top_mtrl_alpha, R.drawable.abc_text_cursor_material};
    private static final int[] COLORFILTER_COLOR_BACKGROUND_MULTIPLY = {R.drawable.abc_popup_background_mtrl_mult, R.drawable.abc_cab_background_internal_bg, R.drawable.abc_menu_hardkey_panel_mtrl_mult};
    private static final int[] TINT_COLOR_CONTROL_STATE_LIST = {R.drawable.abc_tab_indicator_material, R.drawable.abc_textfield_search_material};
    private static final int[] TINT_CHECKABLE_BUTTON_LIST = {R.drawable.abc_btn_check_material, R.drawable.abc_btn_radio_material};

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface InflateDelegate {
        Drawable createFromXmlInner(Context context, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme);
    }

    public static AppCompatDrawableManager get() {
        byte b = 0;
        if (INSTANCE == null) {
            AppCompatDrawableManager appCompatDrawableManager = new AppCompatDrawableManager();
            INSTANCE = appCompatDrawableManager;
            int i = Build.VERSION.SDK_INT;
            if (i < 23) {
                appCompatDrawableManager.addDelegate("vector", new VdcInflateDelegate(b));
                if (i >= 11) {
                    appCompatDrawableManager.addDelegate("animated-vector", new AvdcInflateDelegate(b));
                }
            }
        }
        return INSTANCE;
    }

    private static long createCacheKey(TypedValue tv) {
        return (tv.assetCookie << 32) | tv.data;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Drawable tintDrawable(Context context, int resId, boolean failIfNotKnown, Drawable drawable) {
        ColorStateList tintList = getTintList(context, resId);
        if (tintList != null) {
            if (DrawableUtils.canSafelyMutateDrawable(drawable)) {
                drawable = drawable.mutate();
            }
            Drawable drawable2 = DrawableCompat.wrap(drawable);
            DrawableCompat.setTintList(drawable2, tintList);
            PorterDuff.Mode tintMode = null;
            if (resId == R.drawable.abc_switch_thumb_material) {
                tintMode = PorterDuff.Mode.MULTIPLY;
            }
            if (tintMode != null) {
                DrawableCompat.setTintMode(drawable2, tintMode);
                return drawable2;
            }
            return drawable2;
        }
        if (resId == R.drawable.abc_seekbar_track_material) {
            LayerDrawable ld = (LayerDrawable) drawable;
            setPorterDuffColorFilter(ld.findDrawableByLayerId(android.R.id.background), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlNormal), DEFAULT_MODE);
            setPorterDuffColorFilter(ld.findDrawableByLayerId(android.R.id.secondaryProgress), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlNormal), DEFAULT_MODE);
            setPorterDuffColorFilter(ld.findDrawableByLayerId(android.R.id.progress), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated), DEFAULT_MODE);
            return drawable;
        }
        if (resId == R.drawable.abc_ratingbar_material || resId == R.drawable.abc_ratingbar_indicator_material || resId == R.drawable.abc_ratingbar_small_material) {
            LayerDrawable ld2 = (LayerDrawable) drawable;
            setPorterDuffColorFilter(ld2.findDrawableByLayerId(android.R.id.background), ThemeUtils.getDisabledThemeAttrColor(context, R.attr.colorControlNormal), DEFAULT_MODE);
            setPorterDuffColorFilter(ld2.findDrawableByLayerId(android.R.id.secondaryProgress), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated), DEFAULT_MODE);
            setPorterDuffColorFilter(ld2.findDrawableByLayerId(android.R.id.progress), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated), DEFAULT_MODE);
            return drawable;
        }
        if (!tintDrawableUsingColorFilter(context, resId, drawable) && failIfNotKnown) {
            return null;
        }
        return drawable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Drawable loadDrawableFromDelegates(Context context, int resId) {
        int type;
        if (this.mDelegates != null && !this.mDelegates.isEmpty()) {
            if (this.mKnownDrawableIdTags != null) {
                String cachedTagName = this.mKnownDrawableIdTags.get(resId);
                if ("appcompat_skip_skip".equals(cachedTagName) || (cachedTagName != null && this.mDelegates.get(cachedTagName) == null)) {
                    return null;
                }
            } else {
                this.mKnownDrawableIdTags = new SparseArray<>();
            }
            if (this.mTypedValue == null) {
                this.mTypedValue = new TypedValue();
            }
            TypedValue tv = this.mTypedValue;
            Resources res = context.getResources();
            res.getValue(resId, tv, true);
            long key = createCacheKey(tv);
            Drawable dr = getCachedDrawable(context, key);
            if (dr == null) {
                if (tv.string != null && tv.string.toString().endsWith(".xml")) {
                    try {
                        XmlPullParser parser = res.getXml(resId);
                        AttributeSet attrs = Xml.asAttributeSet(parser);
                        do {
                            type = parser.next();
                            if (type == 2) {
                                break;
                            }
                        } while (type != 1);
                        if (type != 2) {
                            throw new XmlPullParserException("No start tag found");
                        }
                        String tagName = parser.getName();
                        this.mKnownDrawableIdTags.append(resId, tagName);
                        InflateDelegate delegate = this.mDelegates.get(tagName);
                        if (delegate != null) {
                            dr = delegate.createFromXmlInner(context, parser, attrs, context.getTheme());
                        }
                        if (dr != null) {
                            dr.setChangingConfigurations(tv.changingConfigurations);
                            addDrawableToCache(context, key, dr);
                        }
                    } catch (Exception e) {
                        Log.e("AppCompatDrawableManager", "Exception while inflating drawable", e);
                    }
                }
                if (dr == null) {
                    this.mKnownDrawableIdTags.append(resId, "appcompat_skip_skip");
                    return dr;
                }
                return dr;
            }
            return dr;
        }
        return null;
    }

    private Drawable getCachedDrawable(Context context, long key) {
        Drawable drawable = null;
        synchronized (this.mDrawableCacheLock) {
            LongSparseArray<WeakReference<Drawable.ConstantState>> cache = this.mDrawableCaches.get(context);
            if (cache != null) {
                WeakReference<Drawable.ConstantState> wr = cache.get(key);
                if (wr != null) {
                    Drawable.ConstantState entry = wr.get();
                    if (entry != null) {
                        drawable = entry.newDrawable(context.getResources());
                    } else {
                        int binarySearch = ContainerHelpers.binarySearch(cache.mKeys, cache.mSize, key);
                        if (binarySearch >= 0 && cache.mValues[binarySearch] != LongSparseArray.DELETED) {
                            cache.mValues[binarySearch] = LongSparseArray.DELETED;
                            cache.mGarbage = true;
                        }
                    }
                }
            }
        }
        return drawable;
    }

    private boolean addDrawableToCache(Context context, long key, Drawable drawable) {
        Drawable.ConstantState cs = drawable.getConstantState();
        if (cs != null) {
            synchronized (this.mDrawableCacheLock) {
                LongSparseArray<WeakReference<Drawable.ConstantState>> cache = this.mDrawableCaches.get(context);
                if (cache == null) {
                    cache = new LongSparseArray<>();
                    this.mDrawableCaches.put(context, cache);
                }
                cache.put(key, new WeakReference<>(cs));
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean tintDrawableUsingColorFilter(Context context, int resId, Drawable drawable) {
        PorterDuff.Mode tintMode = DEFAULT_MODE;
        boolean colorAttrSet = false;
        int colorAttr = 0;
        int alpha = -1;
        if (arrayContains(COLORFILTER_TINT_COLOR_CONTROL_NORMAL, resId)) {
            colorAttr = R.attr.colorControlNormal;
            colorAttrSet = true;
        } else if (arrayContains(COLORFILTER_COLOR_CONTROL_ACTIVATED, resId)) {
            colorAttr = R.attr.colorControlActivated;
            colorAttrSet = true;
        } else if (arrayContains(COLORFILTER_COLOR_BACKGROUND_MULTIPLY, resId)) {
            colorAttr = android.R.attr.colorBackground;
            colorAttrSet = true;
            tintMode = PorterDuff.Mode.MULTIPLY;
        } else if (resId == R.drawable.abc_list_divider_mtrl_alpha) {
            colorAttr = android.R.attr.colorForeground;
            colorAttrSet = true;
            alpha = Math.round(40.8f);
        } else if (resId == R.drawable.abc_dialog_material_background) {
            colorAttr = android.R.attr.colorBackground;
            colorAttrSet = true;
        }
        if (colorAttrSet) {
            if (DrawableUtils.canSafelyMutateDrawable(drawable)) {
                drawable = drawable.mutate();
            }
            int color = ThemeUtils.getThemeAttrColor(context, colorAttr);
            drawable.setColorFilter(getPorterDuffColorFilter(color, tintMode));
            if (alpha != -1) {
                drawable.setAlpha(alpha);
            }
            return true;
        }
        return false;
    }

    private void addDelegate(String tagName, InflateDelegate delegate) {
        if (this.mDelegates == null) {
            this.mDelegates = new ArrayMap<>();
        }
        this.mDelegates.put(tagName, delegate);
    }

    private static boolean arrayContains(int[] array, int value) {
        for (int i : array) {
            if (i == value) {
                return true;
            }
        }
        return false;
    }

    private static ColorStateList createButtonColorStateList(Context context, int baseColor) {
        int colorControlHighlight = ThemeUtils.getThemeAttrColor(context, R.attr.colorControlHighlight);
        int[][] states = {ThemeUtils.DISABLED_STATE_SET, ThemeUtils.PRESSED_STATE_SET, ThemeUtils.FOCUSED_STATE_SET, ThemeUtils.EMPTY_STATE_SET};
        int[] colors = {ThemeUtils.getDisabledThemeAttrColor(context, R.attr.colorButtonNormal), ColorUtils.compositeColors(colorControlHighlight, baseColor), ColorUtils.compositeColors(colorControlHighlight, baseColor), baseColor};
        return new ColorStateList(states, colors);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ColorFilterLruCache extends LruCache<Integer, PorterDuffColorFilter> {
        static int generateCacheKey(int color, PorterDuff.Mode mode) {
            int hashCode = color + 31;
            return (hashCode * 31) + mode.hashCode();
        }
    }

    public static void tintDrawable(Drawable drawable, TintInfo tint, int[] state) {
        PorterDuffColorFilter porterDuffColorFilter = null;
        if (!DrawableUtils.canSafelyMutateDrawable(drawable) || drawable.mutate() == drawable) {
            if (tint.mHasTintList || tint.mHasTintMode) {
                ColorStateList colorStateList = tint.mHasTintList ? tint.mTintList : null;
                PorterDuff.Mode mode = tint.mHasTintMode ? tint.mTintMode : DEFAULT_MODE;
                if (colorStateList != null && mode != null) {
                    porterDuffColorFilter = getPorterDuffColorFilter(colorStateList.getColorForState(state, 0), mode);
                }
                drawable.setColorFilter(porterDuffColorFilter);
            } else {
                drawable.clearColorFilter();
            }
            if (Build.VERSION.SDK_INT <= 23) {
                drawable.invalidateSelf();
            }
        }
    }

    private static PorterDuffColorFilter getPorterDuffColorFilter(int color, PorterDuff.Mode mode) {
        PorterDuffColorFilter filter = COLOR_FILTER_CACHE.get(Integer.valueOf(ColorFilterLruCache.generateCacheKey(color, mode)));
        if (filter == null) {
            PorterDuffColorFilter filter2 = new PorterDuffColorFilter(color, mode);
            COLOR_FILTER_CACHE.put(Integer.valueOf(ColorFilterLruCache.generateCacheKey(color, mode)), filter2);
            return filter2;
        }
        return filter;
    }

    private static void setPorterDuffColorFilter(Drawable d, int color, PorterDuff.Mode mode) {
        if (DrawableUtils.canSafelyMutateDrawable(d)) {
            d = d.mutate();
        }
        if (mode == null) {
            mode = DEFAULT_MODE;
        }
        d.setColorFilter(getPorterDuffColorFilter(color, mode));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class VdcInflateDelegate implements InflateDelegate {
        private VdcInflateDelegate() {
        }

        /* synthetic */ VdcInflateDelegate(byte b) {
            this();
        }

        @Override // android.support.v7.widget.AppCompatDrawableManager.InflateDelegate
        public final Drawable createFromXmlInner(Context context, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) {
            try {
                return VectorDrawableCompat.createFromXmlInner(context.getResources(), parser, attrs, theme);
            } catch (Exception e) {
                Log.e("VdcInflateDelegate", "Exception while inflating <vector>", e);
                return null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AvdcInflateDelegate implements InflateDelegate {
        private AvdcInflateDelegate() {
        }

        /* synthetic */ AvdcInflateDelegate(byte b) {
            this();
        }

        @Override // android.support.v7.widget.AppCompatDrawableManager.InflateDelegate
        public final Drawable createFromXmlInner(Context context, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) {
            try {
                return AnimatedVectorDrawableCompat.createFromXmlInner(context, context.getResources(), parser, attrs, theme);
            } catch (Exception e) {
                Log.e("AvdcInflateDelegate", "Exception while inflating <animated-vector>", e);
                return null;
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0026, code lost:            if (((r1 instanceof android.support.graphics.drawable.VectorDrawableCompat) || "android.graphics.drawable.VectorDrawable".equals(r1.getClass().getName())) == false) goto L12;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final android.graphics.drawable.Drawable getDrawable(android.content.Context r9, int r10, boolean r11) {
        /*
            r8 = this;
            r3 = 1
            r2 = 0
            boolean r1 = r8.mHasCheckedVectorDrawableSetup
            if (r1 != 0) goto L35
            r8.mHasCheckedVectorDrawableSetup = r3
            int r1 = android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material
            android.graphics.drawable.Drawable r1 = r8.getDrawable(r9, r1, r2)
            if (r1 == 0) goto L28
            boolean r4 = r1 instanceof android.support.graphics.drawable.VectorDrawableCompat
            if (r4 != 0) goto L25
            java.lang.String r4 = "android.graphics.drawable.VectorDrawable"
            java.lang.Class r1 = r1.getClass()
            java.lang.String r1 = r1.getName()
            boolean r1 = r4.equals(r1)
            if (r1 == 0) goto L33
        L25:
            r1 = r3
        L26:
            if (r1 != 0) goto L35
        L28:
            r8.mHasCheckedVectorDrawableSetup = r2
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.String r2 = "This app has been built with an incorrect configuration. Please configure your build for VectorDrawableCompat."
            r1.<init>(r2)
            throw r1
        L33:
            r1 = r2
            goto L26
        L35:
            android.graphics.drawable.Drawable r0 = r8.loadDrawableFromDelegates(r9, r10)
            if (r0 != 0) goto L7f
            android.util.TypedValue r1 = r8.mTypedValue
            if (r1 != 0) goto L46
            android.util.TypedValue r1 = new android.util.TypedValue
            r1.<init>()
            r8.mTypedValue = r1
        L46:
            android.util.TypedValue r1 = r8.mTypedValue
            android.content.res.Resources r4 = r9.getResources()
            r4.getValue(r10, r1, r3)
            long r4 = createCacheKey(r1)
            android.graphics.drawable.Drawable r0 = r8.getCachedDrawable(r9, r4)
            if (r0 != 0) goto L7f
            int r6 = android.support.v7.appcompat.R.drawable.abc_cab_background_top_material
            if (r10 != r6) goto L75
            android.graphics.drawable.LayerDrawable r0 = new android.graphics.drawable.LayerDrawable
            r6 = 2
            android.graphics.drawable.Drawable[] r6 = new android.graphics.drawable.Drawable[r6]
            int r7 = android.support.v7.appcompat.R.drawable.abc_cab_background_internal_bg
            android.graphics.drawable.Drawable r7 = r8.getDrawable(r9, r7, r2)
            r6[r2] = r7
            int r7 = android.support.v7.appcompat.R.drawable.abc_cab_background_top_mtrl_alpha
            android.graphics.drawable.Drawable r2 = r8.getDrawable(r9, r7, r2)
            r6[r3] = r2
            r0.<init>(r6)
        L75:
            if (r0 == 0) goto L7f
            int r1 = r1.changingConfigurations
            r0.setChangingConfigurations(r1)
            r8.addDrawableToCache(r9, r4, r0)
        L7f:
            if (r0 != 0) goto L85
            android.graphics.drawable.Drawable r0 = android.support.v4.content.ContextCompat.getDrawable(r9, r10)
        L85:
            if (r0 == 0) goto L8b
            android.graphics.drawable.Drawable r0 = r8.tintDrawable(r9, r10, r11, r0)
        L8b:
            if (r0 == 0) goto L90
            android.support.v7.widget.DrawableUtils.fixDrawable(r0)
        L90:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.AppCompatDrawableManager.getDrawable(android.content.Context, int, boolean):android.graphics.drawable.Drawable");
    }

    public final ColorStateList getTintList(Context context, int resId) {
        SparseArray<ColorStateList> sparseArray;
        ColorStateList tint = null;
        if (this.mTintLists != null && (sparseArray = this.mTintLists.get(context)) != null) {
            tint = sparseArray.get(resId);
        }
        if (tint == null) {
            if (resId == R.drawable.abc_edit_text_material) {
                tint = AppCompatResources.getColorStateList(context, R.color.abc_tint_edittext);
            } else if (resId == R.drawable.abc_switch_track_mtrl_alpha) {
                tint = AppCompatResources.getColorStateList(context, R.color.abc_tint_switch_track);
            } else if (resId == R.drawable.abc_switch_thumb_material) {
                tint = AppCompatResources.getColorStateList(context, R.color.abc_tint_switch_thumb);
            } else if (resId != R.drawable.abc_btn_default_mtrl_shape) {
                if (resId != R.drawable.abc_btn_borderless_material) {
                    if (resId != R.drawable.abc_btn_colored_material) {
                        if (resId == R.drawable.abc_spinner_mtrl_am_alpha || resId == R.drawable.abc_spinner_textfield_background_material) {
                            tint = AppCompatResources.getColorStateList(context, R.color.abc_tint_spinner);
                        } else if (arrayContains(TINT_COLOR_CONTROL_NORMAL, resId)) {
                            tint = ThemeUtils.getThemeAttrColorStateList(context, R.attr.colorControlNormal);
                        } else if (arrayContains(TINT_COLOR_CONTROL_STATE_LIST, resId)) {
                            tint = AppCompatResources.getColorStateList(context, R.color.abc_tint_default);
                        } else if (arrayContains(TINT_CHECKABLE_BUTTON_LIST, resId)) {
                            tint = AppCompatResources.getColorStateList(context, R.color.abc_tint_btn_checkable);
                        } else if (resId == R.drawable.abc_seekbar_thumb_material) {
                            tint = AppCompatResources.getColorStateList(context, R.color.abc_tint_seek_thumb);
                        }
                    } else {
                        tint = createButtonColorStateList(context, ThemeUtils.getThemeAttrColor(context, R.attr.colorAccent));
                    }
                } else {
                    tint = createButtonColorStateList(context, 0);
                }
            } else {
                tint = createButtonColorStateList(context, ThemeUtils.getThemeAttrColor(context, R.attr.colorButtonNormal));
            }
            if (tint != null) {
                if (this.mTintLists == null) {
                    this.mTintLists = new WeakHashMap<>();
                }
                SparseArray<ColorStateList> sparseArray2 = this.mTintLists.get(context);
                if (sparseArray2 == null) {
                    sparseArray2 = new SparseArray<>();
                    this.mTintLists.put(context, sparseArray2);
                }
                sparseArray2.append(resId, tint);
            }
        }
        return tint;
    }
}
