package com.crashlytics.android;

import java.io.InputStream;

/* loaded from: classes.dex */
public interface PinningInfoProvider {
    String getKeyStorePassword();

    InputStream getKeyStoreStream();

    String[] getPins();
}
