package com.bumptech.glide.load.model;

import android.text.TextUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public final class LazyHeaders implements Headers {
    private volatile Map<String, String> combinedHeaders;
    private final Map<String, List<LazyHeaderFactory>> headers;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LazyHeaders(Map<String, List<LazyHeaderFactory>> headers) {
        this.headers = Collections.unmodifiableMap(headers);
    }

    @Override // com.bumptech.glide.load.model.Headers
    public final Map<String, String> getHeaders() {
        if (this.combinedHeaders == null) {
            synchronized (this) {
                if (this.combinedHeaders == null) {
                    HashMap hashMap = new HashMap();
                    for (Map.Entry<String, List<LazyHeaderFactory>> entry : this.headers.entrySet()) {
                        StringBuilder sb = new StringBuilder();
                        List<LazyHeaderFactory> value = entry.getValue();
                        for (int i = 0; i < value.size(); i++) {
                            sb.append(value.get(i).buildHeader());
                            if (i != value.size() - 1) {
                                sb.append(',');
                            }
                        }
                        hashMap.put(entry.getKey(), sb.toString());
                    }
                    this.combinedHeaders = Collections.unmodifiableMap(hashMap);
                }
            }
        }
        return this.combinedHeaders;
    }

    public final String toString() {
        return "LazyHeaders{headers=" + this.headers + '}';
    }

    public final boolean equals(Object o) {
        if (!(o instanceof LazyHeaders)) {
            return false;
        }
        LazyHeaders other = (LazyHeaders) o;
        return this.headers.equals(other.headers);
    }

    public final int hashCode() {
        return this.headers.hashCode();
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private static final Map<String, List<LazyHeaderFactory>> DEFAULT_HEADERS;
        private static final String DEFAULT_USER_AGENT = System.getProperty("http.agent");
        boolean copyOnModify = true;
        Map<String, List<LazyHeaderFactory>> headers = DEFAULT_HEADERS;
        private boolean isEncodingDefault = true;
        private boolean isUserAgentDefault = true;

        static {
            Map<String, List<LazyHeaderFactory>> temp = new HashMap<>(2);
            if (!TextUtils.isEmpty(DEFAULT_USER_AGENT)) {
                temp.put("User-Agent", Collections.singletonList(new StringHeaderFactory(DEFAULT_USER_AGENT)));
            }
            temp.put("Accept-Encoding", Collections.singletonList(new StringHeaderFactory("identity")));
            DEFAULT_HEADERS = Collections.unmodifiableMap(temp);
        }
    }

    /* loaded from: classes.dex */
    static final class StringHeaderFactory implements LazyHeaderFactory {
        private final String value;

        StringHeaderFactory(String value) {
            this.value = value;
        }

        @Override // com.bumptech.glide.load.model.LazyHeaderFactory
        public final String buildHeader() {
            return this.value;
        }

        public final String toString() {
            return "StringHeaderFactory{value='" + this.value + "'}";
        }

        public final boolean equals(Object o) {
            if (!(o instanceof StringHeaderFactory)) {
                return false;
            }
            StringHeaderFactory other = (StringHeaderFactory) o;
            return this.value.equals(other.value);
        }

        public final int hashCode() {
            return this.value.hashCode();
        }
    }
}
