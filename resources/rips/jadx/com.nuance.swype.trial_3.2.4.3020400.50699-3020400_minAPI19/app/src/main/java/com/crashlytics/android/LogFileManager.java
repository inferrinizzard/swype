package com.crashlytics.android;

import android.content.Context;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.QueueFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
final class LogFileManager {
    private final Context context;
    private final File filesDir;
    QueueFile logFile;

    public LogFileManager(Context context, File filesDir) {
        this.context = context;
        this.filesDir = filesDir;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ByteString getByteStringForLog(QueueFile logFile) {
        if (logFile == null) {
            return null;
        }
        final int[] offsetHolder = {0};
        final byte[] logBytes = new byte[logFile.usedBytes()];
        try {
            logFile.forEach(new QueueFile.ElementReader() { // from class: com.crashlytics.android.LogFileManager.1
                @Override // io.fabric.sdk.android.services.common.QueueFile.ElementReader
                public final void read(InputStream in, int length) throws IOException {
                    try {
                        in.read(logBytes, offsetHolder[0], length);
                        int[] iArr = offsetHolder;
                        iArr[0] = iArr[0] + length;
                    } finally {
                        in.close();
                    }
                }
            });
        } catch (IOException e) {
            Fabric.getLogger().e("Fabric", "A problem occurred while reading the Crashlytics log file.", e);
        }
        return ByteString.copyFrom$49030a4c(logBytes, offsetHolder[0]);
    }
}
