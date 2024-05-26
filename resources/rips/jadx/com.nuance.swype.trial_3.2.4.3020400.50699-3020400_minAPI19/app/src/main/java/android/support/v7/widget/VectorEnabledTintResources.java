package android.support.v7.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatDelegate;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public final class VectorEnabledTintResources extends Resources {
    private final WeakReference<Context> mContextRef;

    public static boolean shouldBeUsed() {
        return AppCompatDelegate.isCompatVectorFromResourcesEnabled() && Build.VERSION.SDK_INT <= 20;
    }

    public VectorEnabledTintResources(Context context, Resources res) {
        super(res.getAssets(), res.getDisplayMetrics(), res.getConfiguration());
        this.mContextRef = new WeakReference<>(context);
    }

    @Override // android.content.res.Resources
    public final Drawable getDrawable(int id) throws Resources.NotFoundException {
        Context context = this.mContextRef.get();
        if (context != null) {
            AppCompatDrawableManager appCompatDrawableManager = AppCompatDrawableManager.get();
            Drawable loadDrawableFromDelegates = appCompatDrawableManager.loadDrawableFromDelegates(context, id);
            if (loadDrawableFromDelegates == null) {
                loadDrawableFromDelegates = super.getDrawable(id);
            }
            if (loadDrawableFromDelegates != null) {
                return appCompatDrawableManager.tintDrawable(context, id, false, loadDrawableFromDelegates);
            }
            return null;
        }
        return super.getDrawable(id);
    }
}
