package android.support.v7.widget;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatDelegate;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class TintContextWrapper extends ContextWrapper {
    private static final ArrayList<WeakReference<TintContextWrapper>> sCache = new ArrayList<>();
    private final Resources mResources;
    private final Resources.Theme mTheme;

    private TintContextWrapper(Context base) {
        super(base);
        if (VectorEnabledTintResources.shouldBeUsed()) {
            this.mResources = new VectorEnabledTintResources(this, base.getResources());
            this.mTheme = this.mResources.newTheme();
            this.mTheme.setTo(base.getTheme());
        } else {
            this.mResources = new TintResources(this, base.getResources());
            this.mTheme = null;
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final Resources.Theme getTheme() {
        return this.mTheme == null ? super.getTheme() : this.mTheme;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final void setTheme(int resid) {
        if (this.mTheme == null) {
            super.setTheme(resid);
        } else {
            this.mTheme.applyStyle(resid, true);
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final Resources getResources() {
        return this.mResources;
    }

    public static Context wrap(Context context) {
        boolean z = false;
        if (!(context instanceof TintContextWrapper) && !(context.getResources() instanceof TintResources) && !(context.getResources() instanceof VectorEnabledTintResources) && (!AppCompatDelegate.isCompatVectorFromResourcesEnabled() || Build.VERSION.SDK_INT <= 20)) {
            z = true;
        }
        if (!z) {
            return context;
        }
        int count = sCache.size();
        for (int i = 0; i < count; i++) {
            WeakReference<TintContextWrapper> ref = sCache.get(i);
            TintContextWrapper wrapper = ref != null ? ref.get() : null;
            if (wrapper != null && wrapper.getBaseContext() == context) {
                return wrapper;
            }
        }
        TintContextWrapper wrapper2 = new TintContextWrapper(context);
        sCache.add(new WeakReference<>(wrapper2));
        return wrapper2;
    }
}
