package io.fabric.sdk.android.services.events;

import android.content.Context;
import com.nuance.connect.internal.common.Document;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.CurrentTimeProvider;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: classes.dex */
public abstract class EventsFilesManager<T> {
    protected final Context context;
    public final CurrentTimeProvider currentTimeProvider;
    protected final EventsStorage eventStorage;
    protected volatile long lastRollOverTime;
    protected final EventTransform<T> transform;
    protected final List<EventsStorageListener> rollOverListeners = new CopyOnWriteArrayList();
    private final int defaultMaxFilesToKeep = 100;

    public abstract String generateUniqueRollOverFileName();

    public EventsFilesManager(Context context, EventTransform<T> transform, CurrentTimeProvider currentTimeProvider, EventsStorage eventStorage) throws IOException {
        this.context = context.getApplicationContext();
        this.transform = transform;
        this.eventStorage = eventStorage;
        this.currentTimeProvider = currentTimeProvider;
        this.lastRollOverTime = this.currentTimeProvider.getCurrentTimeMillis();
    }

    public final void writeEvent(T event) throws IOException {
        byte[] eventBytes = this.transform.toBytes(event);
        int length = eventBytes.length;
        if (!this.eventStorage.canWorkingFileStore(length, getMaxByteSizePerFile())) {
            CommonUtils.logControlled$3aaf2084$3f52113a(this.context, String.format(Locale.US, "session analytics events file is %d bytes, new event is %d bytes, this is over flush limit of %d, rolling it over", Integer.valueOf(this.eventStorage.getWorkingFileUsedSizeInBytes()), Integer.valueOf(length), Integer.valueOf(getMaxByteSizePerFile())));
            rollFileOver();
        }
        this.eventStorage.add(eventBytes);
    }

    public final void registerRollOverListener(EventsStorageListener listener) {
        if (listener != null) {
            this.rollOverListeners.add(listener);
        }
    }

    public final boolean rollFileOver() throws IOException {
        boolean fileRolledOver = false;
        if (!this.eventStorage.isWorkingFileEmpty()) {
            String targetFileName = generateUniqueRollOverFileName();
            this.eventStorage.rollOver(targetFileName);
            CommonUtils.logControlled$3aaf2084$3f52113a(this.context, String.format(Locale.US, "generated new file %s", targetFileName));
            this.lastRollOverTime = this.currentTimeProvider.getCurrentTimeMillis();
            fileRolledOver = true;
        }
        Iterator<EventsStorageListener> it = this.rollOverListeners.iterator();
        while (it.hasNext()) {
            try {
                it.next().onRollOver$552c4e01();
            } catch (Exception e) {
                CommonUtils.logControlledError$43da9ce8(this.context, "One of the roll over listeners threw an exception");
            }
        }
        return fileRolledOver;
    }

    public int getMaxFilesToKeep() {
        return this.defaultMaxFilesToKeep;
    }

    public int getMaxByteSizePerFile() {
        return 8000;
    }

    public final List<File> getBatchOfFilesToSend() {
        return this.eventStorage.getBatchOfFilesToSend$22f3aa59();
    }

    public final void deleteSentFiles(List<File> files) {
        this.eventStorage.deleteFilesInRollOverDirectory(files);
    }

    public final void deleteAllEventsFiles() {
        this.eventStorage.deleteFilesInRollOverDirectory(this.eventStorage.getAllFilesInRollOverDirectory());
        this.eventStorage.deleteWorkingFile();
    }

    public final void deleteOldestInRollOverIfOverMax() {
        List<File> allFiles = this.eventStorage.getAllFilesInRollOverDirectory();
        int maxFiles = getMaxFilesToKeep();
        if (allFiles.size() > maxFiles) {
            int numberOfFilesToDelete = allFiles.size() - maxFiles;
            Context context = this.context;
            String.format(Locale.US, "Found %d files in  roll over directory, this is greater than %d, deleting %d oldest files", Integer.valueOf(allFiles.size()), Integer.valueOf(maxFiles), Integer.valueOf(numberOfFilesToDelete));
            CommonUtils.logControlled$5ffc00fd(context);
            TreeSet<FileWithTimestamp> sortedFiles = new TreeSet<>(new Comparator<FileWithTimestamp>() { // from class: io.fabric.sdk.android.services.events.EventsFilesManager.1
                @Override // java.util.Comparator
                public final /* bridge */ /* synthetic */ int compare(FileWithTimestamp fileWithTimestamp, FileWithTimestamp fileWithTimestamp2) {
                    return (int) (fileWithTimestamp.timestamp - fileWithTimestamp2.timestamp);
                }
            });
            for (File file : allFiles) {
                long creationTimestamp = parseCreationTimestampFromFileName(file.getName());
                sortedFiles.add(new FileWithTimestamp(file, creationTimestamp));
            }
            ArrayList<File> toDelete = new ArrayList<>();
            Iterator i$ = sortedFiles.iterator();
            while (i$.hasNext()) {
                FileWithTimestamp fileWithTimestamp = i$.next();
                toDelete.add(fileWithTimestamp.file);
                if (toDelete.size() == numberOfFilesToDelete) {
                    break;
                }
            }
            this.eventStorage.deleteFilesInRollOverDirectory(toDelete);
        }
    }

    private static long parseCreationTimestampFromFileName(String fileName) {
        String[] fileNameParts = fileName.split(Document.ID_SEPARATOR);
        if (fileNameParts.length != 3) {
            return 0L;
        }
        try {
            return Long.valueOf(fileNameParts[2]).longValue();
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /* loaded from: classes.dex */
    static class FileWithTimestamp {
        final File file;
        final long timestamp;

        public FileWithTimestamp(File file, long timestamp) {
            this.file = file;
            this.timestamp = timestamp;
        }
    }
}
