package io.fabric.sdk.android.services.cache;

import android.content.Context;

/* loaded from: classes.dex */
public abstract class AbstractValueCache<T> implements ValueCache<T> {
    private final ValueCache<T> childCache = null;

    protected abstract void cacheValue$127ac70f(T t);

    protected abstract T getCached$dc0f261();

    @Override // io.fabric.sdk.android.services.cache.ValueCache
    public final synchronized T get(Context context, ValueLoader<T> loader) throws Exception {
        T value;
        value = getCached$dc0f261();
        if (value == null) {
            value = this.childCache != null ? this.childCache.get(context, loader) : loader.load(context);
            if (value == null) {
                throw new NullPointerException();
            }
            cacheValue$127ac70f(value);
        }
        return value;
    }
}
