package com.bumptech.glide.provider;

import com.bumptech.glide.util.MultiClassKey;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class DataLoadProviderRegistry {
    private static final MultiClassKey GET_KEY = new MultiClassKey();
    private final Map<MultiClassKey, DataLoadProvider<?, ?>> providers = new HashMap();

    public final <T, Z> void register(Class<T> dataClass, Class<Z> resourceClass, DataLoadProvider<T, Z> provider) {
        this.providers.put(new MultiClassKey(dataClass, resourceClass), provider);
    }

    public final <T, Z> DataLoadProvider<T, Z> get(Class<T> dataClass, Class<Z> resourceClass) {
        DataLoadProvider<T, Z> dataLoadProvider;
        synchronized (GET_KEY) {
            GET_KEY.set(dataClass, resourceClass);
            dataLoadProvider = (DataLoadProvider) this.providers.get(GET_KEY);
        }
        if (dataLoadProvider == null) {
            return EmptyDataLoadProvider.get();
        }
        return dataLoadProvider;
    }
}
