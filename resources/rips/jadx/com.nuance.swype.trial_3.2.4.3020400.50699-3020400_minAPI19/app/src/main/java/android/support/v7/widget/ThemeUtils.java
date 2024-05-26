package android.support.v7.widget;

import android.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.TypedValue;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ThemeUtils {
    private static final ThreadLocal<TypedValue> TL_TYPED_VALUE = new ThreadLocal<>();
    static final int[] DISABLED_STATE_SET = {-16842910};
    static final int[] FOCUSED_STATE_SET = {R.attr.state_focused};
    static final int[] ACTIVATED_STATE_SET = {R.attr.state_activated};
    static final int[] PRESSED_STATE_SET = {R.attr.state_pressed};
    static final int[] CHECKED_STATE_SET = {R.attr.state_checked};
    static final int[] SELECTED_STATE_SET = {R.attr.state_selected};
    static final int[] NOT_PRESSED_OR_FOCUSED_STATE_SET = {-16842919, -16842908};
    static final int[] EMPTY_STATE_SET = new int[0];
    private static final int[] TEMP_ARRAY = new int[1];

    public static int getThemeAttrColor(Context context, int attr) {
        TEMP_ARRAY[0] = attr;
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, (AttributeSet) null, TEMP_ARRAY);
        try {
            return a.getColor(0, 0);
        } finally {
            a.mWrapped.recycle();
        }
    }

    public static ColorStateList getThemeAttrColorStateList(Context context, int attr) {
        TEMP_ARRAY[0] = attr;
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, (AttributeSet) null, TEMP_ARRAY);
        try {
            return a.getColorStateList(0);
        } finally {
            a.mWrapped.recycle();
        }
    }

    public static int getDisabledThemeAttrColor(Context context, int attr) {
        TypedValue tv;
        ColorStateList csl = getThemeAttrColorStateList(context, attr);
        if (csl != null && csl.isStateful()) {
            return csl.getColorForState(DISABLED_STATE_SET, csl.getDefaultColor());
        }
        TypedValue typedValue = TL_TYPED_VALUE.get();
        if (typedValue == null) {
            tv = new TypedValue();
            TL_TYPED_VALUE.set(tv);
        } else {
            tv = typedValue;
        }
        context.getTheme().resolveAttribute(R.attr.disabledAlpha, tv, true);
        float disabledAlpha = tv.getFloat();
        return ColorUtils.setAlphaComponent(getThemeAttrColor(context, attr), Math.round(Color.alpha(r3) * disabledAlpha));
    }
}
