package io.fabric.sdk.android.services.events;

import android.content.Context;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.QueueFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class QueueFileEventStorage implements EventsStorage {
    private final Context context;
    private QueueFile queueFile;
    private File targetDirectory;
    private final String targetDirectoryName;
    private final File workingDirectory;
    private final File workingFile;

    public QueueFileEventStorage(Context context, File workingDirectory, String workingFileName, String targetDirectoryName) throws IOException {
        this.context = context;
        this.workingDirectory = workingDirectory;
        this.targetDirectoryName = targetDirectoryName;
        this.workingFile = new File(this.workingDirectory, workingFileName);
        this.queueFile = new QueueFile(this.workingFile);
        this.targetDirectory = new File(this.workingDirectory, this.targetDirectoryName);
        if (this.targetDirectory.exists()) {
            return;
        }
        this.targetDirectory.mkdirs();
    }

    @Override // io.fabric.sdk.android.services.events.EventsStorage
    public final void add(byte[] data) throws IOException {
        this.queueFile.add$1cf967a4(data, data.length);
    }

    @Override // io.fabric.sdk.android.services.events.EventsStorage
    public final int getWorkingFileUsedSizeInBytes() {
        return this.queueFile.usedBytes();
    }

    @Override // io.fabric.sdk.android.services.events.EventsStorage
    public final void rollOver(String targetName) throws IOException {
        FileInputStream fileInputStream;
        OutputStream outputStream = null;
        this.queueFile.close();
        File file = this.workingFile;
        File file2 = new File(this.targetDirectory, targetName);
        try {
            fileInputStream = new FileInputStream(file);
            try {
                outputStream = getMoveOutputStream(file2);
                CommonUtils.copyStream(fileInputStream, outputStream, new byte[1024]);
                CommonUtils.closeOrLog(fileInputStream, "Failed to close file input stream");
                CommonUtils.closeOrLog(outputStream, "Failed to close output stream");
                file.delete();
                this.queueFile = new QueueFile(this.workingFile);
            } catch (Throwable th) {
                th = th;
                CommonUtils.closeOrLog(fileInputStream, "Failed to close file input stream");
                CommonUtils.closeOrLog(outputStream, "Failed to close output stream");
                file.delete();
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            fileInputStream = null;
        }
    }

    public OutputStream getMoveOutputStream(File targetFile) throws IOException {
        return new FileOutputStream(targetFile);
    }

    @Override // io.fabric.sdk.android.services.events.EventsStorage
    public final List<File> getBatchOfFilesToSend$22f3aa59() {
        List<File> batch = new ArrayList<>();
        File[] arr$ = this.targetDirectory.listFiles();
        for (File file : arr$) {
            batch.add(file);
            if (batch.size() > 0) {
                break;
            }
        }
        return batch;
    }

    @Override // io.fabric.sdk.android.services.events.EventsStorage
    public final void deleteFilesInRollOverDirectory(List<File> files) {
        for (File file : files) {
            Context context = this.context;
            String.format("deleting sent analytics file %s", file.getName());
            CommonUtils.logControlled$5ffc00fd(context);
            file.delete();
        }
    }

    @Override // io.fabric.sdk.android.services.events.EventsStorage
    public final List<File> getAllFilesInRollOverDirectory() {
        return Arrays.asList(this.targetDirectory.listFiles());
    }

    @Override // io.fabric.sdk.android.services.events.EventsStorage
    public final void deleteWorkingFile() {
        try {
            this.queueFile.close();
        } catch (IOException e) {
        }
        this.workingFile.delete();
    }

    @Override // io.fabric.sdk.android.services.events.EventsStorage
    public final boolean isWorkingFileEmpty() {
        return this.queueFile.isEmpty();
    }

    @Override // io.fabric.sdk.android.services.events.EventsStorage
    public final boolean canWorkingFileStore(int newEventSizeInBytes, int maxByteSizePerFile) {
        return (this.queueFile.usedBytes() + 4) + newEventSizeInBytes <= maxByteSizePerFile;
    }
}
