package io.fabric.sdk.android.services.common;

import android.content.Context;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.cache.MemoryValueCache;
import io.fabric.sdk.android.services.cache.ValueLoader;

/* loaded from: classes.dex */
public final class InstallerPackageNameProvider {
    private final ValueLoader<String> installerPackageNameLoader = new ValueLoader<String>() { // from class: io.fabric.sdk.android.services.common.InstallerPackageNameProvider.1
        @Override // io.fabric.sdk.android.services.cache.ValueLoader
        public final /* bridge */ /* synthetic */ String load(Context x0) throws Exception {
            String installerPackageName = x0.getPackageManager().getInstallerPackageName(x0.getPackageName());
            return installerPackageName == null ? "" : installerPackageName;
        }
    };
    private final MemoryValueCache<String> installerPackageNameCache = new MemoryValueCache<>();

    public final String getInstallerPackageName(Context appContext) {
        try {
            String name = this.installerPackageNameCache.get(appContext, this.installerPackageNameLoader);
            if ("".equals(name)) {
                return null;
            }
            return name;
        } catch (Exception e) {
            Fabric.getLogger().e("Fabric", "Failed to determine installer package name", e);
            return null;
        }
    }
}
