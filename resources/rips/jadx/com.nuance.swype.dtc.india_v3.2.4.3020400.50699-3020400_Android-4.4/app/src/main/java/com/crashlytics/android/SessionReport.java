package com.crashlytics.android;

import io.fabric.sdk.android.Fabric;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
final class SessionReport implements Report {
    private final Map<String, String> customHeaders;
    private final File file;

    public SessionReport(File file) {
        this(file, Collections.emptyMap());
    }

    public SessionReport(File file, Map<String, String> customHeaders) {
        this.file = file;
        this.customHeaders = new HashMap(customHeaders);
        if (this.file.length() == 0) {
            this.customHeaders.putAll(ReportUploader.HEADER_INVALID_CLS_FILE);
        }
    }

    @Override // com.crashlytics.android.Report
    public final File getFile() {
        return this.file;
    }

    @Override // com.crashlytics.android.Report
    public final Map<String, String> getCustomHeaders() {
        return Collections.unmodifiableMap(this.customHeaders);
    }

    @Override // com.crashlytics.android.Report
    public final boolean remove() {
        Fabric.getLogger();
        new StringBuilder("Removing report at ").append(this.file.getPath());
        return this.file.delete();
    }

    @Override // com.crashlytics.android.Report
    public final String getFileName() {
        return this.file.getName();
    }

    @Override // com.crashlytics.android.Report
    public final String getIdentifier() {
        String fileName = this.file.getName();
        return fileName.substring(0, fileName.lastIndexOf(46));
    }
}
