package com.bumptech.glide.load.resource.transcode;

import com.bumptech.glide.util.MultiClassKey;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class TranscoderRegistry {
    private static final MultiClassKey GET_KEY = new MultiClassKey();
    private final Map<MultiClassKey, ResourceTranscoder<?, ?>> factories = new HashMap();

    public final <Z, R> void register(Class<Z> decodedClass, Class<R> transcodedClass, ResourceTranscoder<Z, R> transcoder) {
        this.factories.put(new MultiClassKey(decodedClass, transcodedClass), transcoder);
    }

    public final <Z, R> ResourceTranscoder<Z, R> get(Class<Z> decodedClass, Class<R> transcodedClass) {
        ResourceTranscoder<Z, R> resourceTranscoder;
        if (decodedClass.equals(transcodedClass)) {
            return UnitTranscoder.get();
        }
        synchronized (GET_KEY) {
            GET_KEY.set(decodedClass, transcodedClass);
            resourceTranscoder = (ResourceTranscoder) this.factories.get(GET_KEY);
        }
        if (resourceTranscoder == null) {
            throw new IllegalArgumentException("No transcoder registered for " + decodedClass + " and " + transcodedClass);
        }
        return resourceTranscoder;
    }
}
