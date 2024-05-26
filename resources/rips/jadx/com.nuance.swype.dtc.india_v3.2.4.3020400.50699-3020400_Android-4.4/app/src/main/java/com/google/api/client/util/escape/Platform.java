package com.google.api.client.util.escape;

/* loaded from: classes.dex */
final class Platform {
    private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal<char[]>() { // from class: com.google.api.client.util.escape.Platform.1
        @Override // java.lang.ThreadLocal
        protected final /* bridge */ /* synthetic */ char[] initialValue() {
            return new char[1024];
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public static char[] charBufferFromThreadLocal() {
        return DEST_TL.get();
    }
}
