package io.fabric.sdk.android.services.persistence;

import android.content.Context;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import java.io.File;

/* loaded from: classes.dex */
public final class FileStoreImpl {
    private final String contentPath;
    private final Context context;
    private final String legacySupport;

    public FileStoreImpl(Kit kit) {
        if (kit.context == null) {
            throw new IllegalStateException("Cannot get directory before context has been set. Call Fabric.with() first");
        }
        this.context = kit.context;
        this.contentPath = kit.getPath();
        this.legacySupport = "Android/" + this.context.getPackageName();
    }

    public final File getFilesDir() {
        File filesDir = this.context.getFilesDir();
        if (filesDir != null) {
            if (filesDir.exists() || filesDir.mkdirs()) {
                return filesDir;
            }
            Fabric.getLogger().w("Fabric", "Couldn't create file");
        } else {
            Fabric.getLogger();
        }
        return null;
    }
}
