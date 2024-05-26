package com.crashlytics.android;

import java.io.InputStream;

/* loaded from: classes.dex */
final class CrashlyticsPinningInfoProvider implements io.fabric.sdk.android.services.network.PinningInfoProvider {
    private final PinningInfoProvider pinningInfo;

    public CrashlyticsPinningInfoProvider(PinningInfoProvider pinningInfo) {
        this.pinningInfo = pinningInfo;
    }

    @Override // io.fabric.sdk.android.services.network.PinningInfoProvider
    public final InputStream getKeyStoreStream() {
        return this.pinningInfo.getKeyStoreStream();
    }

    @Override // io.fabric.sdk.android.services.network.PinningInfoProvider
    public final String getKeyStorePassword() {
        return this.pinningInfo.getKeyStorePassword();
    }

    @Override // io.fabric.sdk.android.services.network.PinningInfoProvider
    public final String[] getPins() {
        return this.pinningInfo.getPins();
    }
}
