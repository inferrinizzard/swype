package com.nuance.swype.input.appspecific;

import android.os.Build;

/* loaded from: classes.dex */
public class BehaviorInfo {
    public static final int INVALID_VERSION = -1;
    private boolean enabled = true;
    private final int maxVersion;
    private final int minVersion;
    private final Object value;

    public BehaviorInfo(Object value, int minVersion, int maxVersion, String brand, int minSdk, int maxSdk) {
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
        if ((brand != null && !brand.equalsIgnoreCase(Build.BRAND) && !brand.equalsIgnoreCase(Build.MANUFACTURER)) || ((minSdk != 0 && Build.VERSION.SDK_INT < minSdk) || (maxSdk != 0 && Build.VERSION.SDK_INT > maxSdk))) {
            this.value = null;
        } else {
            this.value = value;
        }
    }

    public void updateEnabled(int packageVersion) {
        this.enabled = (this.minVersion == -1 || this.minVersion <= packageVersion) && (this.maxVersion == -1 || this.maxVersion >= packageVersion);
    }

    public Object getValue() {
        if (this.enabled) {
            return this.value;
        }
        return null;
    }
}
