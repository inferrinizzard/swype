package com.crashlytics.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ClsFileOutputStream extends FileOutputStream {
    public static final FilenameFilter TEMP_FILENAME_FILTER = new FilenameFilter() { // from class: com.crashlytics.android.ClsFileOutputStream.1
        @Override // java.io.FilenameFilter
        public final boolean accept(File dir, String filename) {
            return filename.endsWith(".cls_temp");
        }
    };
    private boolean closed;
    private File complete;
    private File inProgress;
    private final String root;

    public ClsFileOutputStream(File dir, String fileRoot) throws FileNotFoundException {
        super(new File(dir, fileRoot + ".cls_temp"));
        this.closed = false;
        this.root = dir + File.separator + fileRoot;
        this.inProgress = new File(this.root + ".cls_temp");
    }

    @Override // java.io.FileOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public final synchronized void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            super.flush();
            super.close();
            File complete = new File(this.root + ".cls");
            if (this.inProgress.renameTo(complete)) {
                this.inProgress = null;
                this.complete = complete;
            } else {
                String reason = "";
                if (complete.exists()) {
                    reason = " (target already exists)";
                } else if (!this.inProgress.exists()) {
                    reason = " (source does not exist)";
                }
                throw new IOException("Could not rename temp file: " + this.inProgress + " -> " + complete + reason);
            }
        }
    }

    public final void closeInProgressStream() throws IOException {
        if (!this.closed) {
            this.closed = true;
            super.flush();
            super.close();
        }
    }
}
