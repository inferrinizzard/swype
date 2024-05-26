package com.bumptech.glide.load.model;

import com.bumptech.glide.load.model.LazyHeaders;
import java.util.Collections;
import java.util.Map;

/* loaded from: classes.dex */
public interface Headers {
    public static final Headers DEFAULT;

    @Deprecated
    public static final Headers NONE = new Headers() { // from class: com.bumptech.glide.load.model.Headers.1
        @Override // com.bumptech.glide.load.model.Headers
        public final Map<String, String> getHeaders() {
            return Collections.emptyMap();
        }
    };

    Map<String, String> getHeaders();

    static {
        LazyHeaders.Builder builder = new LazyHeaders.Builder();
        builder.copyOnModify = true;
        DEFAULT = new LazyHeaders(builder.headers);
    }
}
