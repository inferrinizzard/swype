package io.fabric.sdk.android.services.cache;

/* loaded from: classes.dex */
public final class MemoryValueCache<T> extends AbstractValueCache<T> {
    private T value;

    public MemoryValueCache() {
        this((byte) 0);
    }

    private MemoryValueCache(byte b) {
    }

    @Override // io.fabric.sdk.android.services.cache.AbstractValueCache
    protected final T getCached$dc0f261() {
        return this.value;
    }

    @Override // io.fabric.sdk.android.services.cache.AbstractValueCache
    protected final void cacheValue$127ac70f(T value) {
        this.value = value;
    }
}
