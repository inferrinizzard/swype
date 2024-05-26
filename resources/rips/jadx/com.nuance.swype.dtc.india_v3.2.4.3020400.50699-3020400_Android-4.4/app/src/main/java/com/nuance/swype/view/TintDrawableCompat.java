package com.nuance.swype.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import com.nuance.android.compat.CompatUtil;
import java.lang.reflect.Constructor;

/* loaded from: classes.dex */
public class TintDrawableCompat {
    public static final Class<?> CLASS_TintDrawable = CompatUtil.getClass(getTintDrawableClassName());
    private static final Class<?>[] ARGS_TintDrawable = {Drawable.class};
    private static Constructor<?> CONSTRUCTOR_TintDrawable = CompatUtil.getConstructor(CLASS_TintDrawable, ARGS_TintDrawable);

    private static String getTintDrawableClassName() {
        String base = TintDrawableCompat.class.getPackage().getName();
        String suffix = Build.VERSION.SDK_INT >= 21 ? "TintDrawableV21" : "TintDrawable";
        return base + "." + suffix;
    }

    private static TintDrawable setTintDrawableBackground(Context context, View view) {
        Drawable drawable = view.getBackground();
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof TintDrawable) {
            return (TintDrawable) drawable;
        }
        view.setBackgroundDrawable(null);
        TintDrawable td = createTintDrawable(context, drawable);
        view.setBackgroundDrawable(td);
        return td;
    }

    public static TintDrawable createTintDrawable(Context context, int resId) {
        return createTintDrawable(context, context.getResources().getDrawable(resId));
    }

    public static TintDrawable createTintDrawable(Context context, Drawable drawable) {
        if (CONSTRUCTOR_TintDrawable == null) {
            CONSTRUCTOR_TintDrawable = CompatUtil.getConstructor(CLASS_TintDrawable, ARGS_TintDrawable);
        }
        Object[] args = {drawable};
        return (TintDrawable) CompatUtil.newInstance(CONSTRUCTOR_TintDrawable, args);
    }
}
