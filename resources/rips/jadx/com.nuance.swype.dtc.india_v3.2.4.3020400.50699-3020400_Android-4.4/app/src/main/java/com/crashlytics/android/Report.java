package com.crashlytics.android;

import java.io.File;
import java.util.Map;

/* loaded from: classes.dex */
interface Report {
    Map<String, String> getCustomHeaders();

    File getFile();

    String getFileName();

    String getIdentifier();

    boolean remove();
}
