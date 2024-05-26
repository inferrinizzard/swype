package io.fabric.sdk.android.services.common;

/* loaded from: classes.dex */
public final class SystemCurrentTimeProvider implements CurrentTimeProvider {
    @Override // io.fabric.sdk.android.services.common.CurrentTimeProvider
    public final long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
