package com.bumptech.glide.disklrucache;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public final class DiskLruCache implements Closeable {
    private final File directory;
    private final File journalFile;
    private final File journalFileBackup;
    private final File journalFileTmp;
    private Writer journalWriter;
    private long maxSize;
    private int redundantOpCount;
    private long size = 0;
    private final LinkedHashMap<String, Entry> lruEntries = new LinkedHashMap<>(0, 0.75f, true);
    private long nextSequenceNumber = 0;
    final ThreadPoolExecutor executorService = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
    private final Callable<Void> cleanupCallable = new Callable<Void>() { // from class: com.bumptech.glide.disklrucache.DiskLruCache.1
        /* JADX INFO: Access modifiers changed from: private */
        @Override // java.util.concurrent.Callable
        public Void call() throws Exception {
            synchronized (DiskLruCache.this) {
                if (DiskLruCache.this.journalWriter != null) {
                    DiskLruCache.this.trimToSize();
                    if (DiskLruCache.this.journalRebuildRequired()) {
                        DiskLruCache.this.rebuildJournal();
                        DiskLruCache.access$402$7553e83e(DiskLruCache.this);
                    }
                }
            }
            return null;
        }
    };
    private final int appVersion = 1;
    private final int valueCount = 1;

    static /* synthetic */ int access$402$7553e83e(DiskLruCache x0) {
        x0.redundantOpCount = 0;
        return 0;
    }

    private DiskLruCache(File directory, long maxSize) {
        this.directory = directory;
        this.journalFile = new File(directory, "journal");
        this.journalFileTmp = new File(directory, "journal.tmp");
        this.journalFileBackup = new File(directory, "journal.bkp");
        this.maxSize = maxSize;
    }

    public static DiskLruCache open$641e3723(File directory, long maxSize) throws IOException {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        File backupFile = new File(directory, "journal.bkp");
        if (backupFile.exists()) {
            File journalFile = new File(directory, "journal");
            if (journalFile.exists()) {
                backupFile.delete();
            } else {
                renameTo(backupFile, journalFile, false);
            }
        }
        DiskLruCache cache = new DiskLruCache(directory, maxSize);
        if (cache.journalFile.exists()) {
            try {
                cache.readJournal();
                cache.processJournal();
                return cache;
            } catch (IOException journalIsCorrupt) {
                System.out.println("DiskLruCache " + directory + " is corrupt: " + journalIsCorrupt.getMessage() + ", removing");
                cache.close();
                Util.deleteContents(cache.directory);
            }
        }
        directory.mkdirs();
        DiskLruCache cache2 = new DiskLruCache(directory, maxSize);
        cache2.rebuildJournal();
        return cache2;
    }

    /* JADX WARN: Code restructure failed: missing block: B:55:0x0175, code lost:            throw new java.io.IOException("unexpected journal line: " + r9);     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void readJournal() throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 404
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.disklrucache.DiskLruCache.readJournal():void");
    }

    private void processJournal() throws IOException {
        deleteIfExists(this.journalFileTmp);
        Iterator<Entry> i = this.lruEntries.values().iterator();
        while (i.hasNext()) {
            Entry entry = i.next();
            if (entry.currentEditor == null) {
                for (int t = 0; t < this.valueCount; t++) {
                    this.size += entry.lengths[t];
                }
            } else {
                entry.currentEditor = null;
                for (int t2 = 0; t2 < this.valueCount; t2++) {
                    deleteIfExists(entry.cleanFiles[t2]);
                    deleteIfExists(entry.dirtyFiles[t2]);
                }
                i.remove();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void rebuildJournal() throws IOException {
        if (this.journalWriter != null) {
            this.journalWriter.close();
        }
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.journalFileTmp), Util.US_ASCII));
        try {
            writer.write("libcore.io.DiskLruCache");
            writer.write("\n");
            writer.write("1");
            writer.write("\n");
            writer.write(Integer.toString(this.appVersion));
            writer.write("\n");
            writer.write(Integer.toString(this.valueCount));
            writer.write("\n");
            writer.write("\n");
            for (Entry entry : this.lruEntries.values()) {
                if (entry.currentEditor != null) {
                    writer.write("DIRTY " + entry.key + '\n');
                } else {
                    writer.write("CLEAN " + entry.key + entry.getLengths() + '\n');
                }
            }
            writer.close();
            if (this.journalFile.exists()) {
                renameTo(this.journalFile, this.journalFileBackup, true);
            }
            renameTo(this.journalFileTmp, this.journalFile, false);
            this.journalFileBackup.delete();
            this.journalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.journalFile, true), Util.US_ASCII));
        } catch (Throwable th) {
            writer.close();
            throw th;
        }
    }

    private static void deleteIfExists(File file) throws IOException {
        if (file.exists() && !file.delete()) {
            throw new IOException();
        }
    }

    private static void renameTo(File from, File to, boolean deleteDestination) throws IOException {
        if (deleteDestination) {
            deleteIfExists(to);
        }
        if (!from.renameTo(to)) {
            throw new IOException();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x0026, code lost:            r12.redundantOpCount++;        r12.journalWriter.append((java.lang.CharSequence) "READ");        r12.journalWriter.append(' ');        r12.journalWriter.append((java.lang.CharSequence) r13);        r12.journalWriter.append('\n');     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x004b, code lost:            if (journalRebuildRequired() == false) goto L18;     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x004d, code lost:            r12.executorService.submit(r12.cleanupCallable);     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0054, code lost:            r1 = new com.bumptech.glide.disklrucache.DiskLruCache.Value(r12, r13, r9.sequenceNumber, r9.cleanFiles, r9.lengths, 0);     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final synchronized com.bumptech.glide.disklrucache.DiskLruCache.Value get(java.lang.String r13) throws java.io.IOException {
        /*
            r12 = this;
            r1 = 0
            monitor-enter(r12)
            r12.checkNotClosed()     // Catch: java.lang.Throwable -> L63
            java.util.LinkedHashMap<java.lang.String, com.bumptech.glide.disklrucache.DiskLruCache$Entry> r2 = r12.lruEntries     // Catch: java.lang.Throwable -> L63
            java.lang.Object r9 = r2.get(r13)     // Catch: java.lang.Throwable -> L63
            com.bumptech.glide.disklrucache.DiskLruCache$Entry r9 = (com.bumptech.glide.disklrucache.DiskLruCache.Entry) r9     // Catch: java.lang.Throwable -> L63
            if (r9 != 0) goto L11
        Lf:
            monitor-exit(r12)
            return r1
        L11:
            boolean r2 = r9.readable     // Catch: java.lang.Throwable -> L63
            if (r2 == 0) goto Lf
            java.io.File[] r0 = r9.cleanFiles     // Catch: java.lang.Throwable -> L63
            int r11 = r0.length     // Catch: java.lang.Throwable -> L63
            r10 = 0
        L19:
            if (r10 >= r11) goto L26
            r2 = r0[r10]     // Catch: java.lang.Throwable -> L63
            boolean r2 = r2.exists()     // Catch: java.lang.Throwable -> L63
            if (r2 == 0) goto Lf
            int r10 = r10 + 1
            goto L19
        L26:
            int r1 = r12.redundantOpCount     // Catch: java.lang.Throwable -> L63
            int r1 = r1 + 1
            r12.redundantOpCount = r1     // Catch: java.lang.Throwable -> L63
            java.io.Writer r1 = r12.journalWriter     // Catch: java.lang.Throwable -> L63
            java.lang.String r2 = "READ"
            r1.append(r2)     // Catch: java.lang.Throwable -> L63
            java.io.Writer r1 = r12.journalWriter     // Catch: java.lang.Throwable -> L63
            r2 = 32
            r1.append(r2)     // Catch: java.lang.Throwable -> L63
            java.io.Writer r1 = r12.journalWriter     // Catch: java.lang.Throwable -> L63
            r1.append(r13)     // Catch: java.lang.Throwable -> L63
            java.io.Writer r1 = r12.journalWriter     // Catch: java.lang.Throwable -> L63
            r2 = 10
            r1.append(r2)     // Catch: java.lang.Throwable -> L63
            boolean r1 = r12.journalRebuildRequired()     // Catch: java.lang.Throwable -> L63
            if (r1 == 0) goto L54
            java.util.concurrent.ThreadPoolExecutor r1 = r12.executorService     // Catch: java.lang.Throwable -> L63
            java.util.concurrent.Callable<java.lang.Void> r2 = r12.cleanupCallable     // Catch: java.lang.Throwable -> L63
            r1.submit(r2)     // Catch: java.lang.Throwable -> L63
        L54:
            com.bumptech.glide.disklrucache.DiskLruCache$Value r1 = new com.bumptech.glide.disklrucache.DiskLruCache$Value     // Catch: java.lang.Throwable -> L63
            long r4 = r9.sequenceNumber     // Catch: java.lang.Throwable -> L63
            java.io.File[] r6 = r9.cleanFiles     // Catch: java.lang.Throwable -> L63
            long[] r7 = r9.lengths     // Catch: java.lang.Throwable -> L63
            r8 = 0
            r2 = r12
            r3 = r13
            r1.<init>(r2, r3, r4, r6, r7, r8)     // Catch: java.lang.Throwable -> L63
            goto Lf
        L63:
            r1 = move-exception
            monitor-exit(r12)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.disklrucache.DiskLruCache.get(java.lang.String):com.bumptech.glide.disklrucache.DiskLruCache$Value");
    }

    public final Editor edit(String key) throws IOException {
        return edit$7efc45cb(key);
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x0058, code lost:            if (r1.currentEditor != null) goto L10;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private synchronized com.bumptech.glide.disklrucache.DiskLruCache.Editor edit$7efc45cb(java.lang.String r7) throws java.io.IOException {
        /*
            r6 = this;
            r0 = 0
            r4 = -1
            monitor-enter(r6)
            r6.checkNotClosed()     // Catch: java.lang.Throwable -> L53
            java.util.LinkedHashMap<java.lang.String, com.bumptech.glide.disklrucache.DiskLruCache$Entry> r2 = r6.lruEntries     // Catch: java.lang.Throwable -> L53
            java.lang.Object r1 = r2.get(r7)     // Catch: java.lang.Throwable -> L53
            com.bumptech.glide.disklrucache.DiskLruCache$Entry r1 = (com.bumptech.glide.disklrucache.DiskLruCache.Entry) r1     // Catch: java.lang.Throwable -> L53
            int r2 = (r4 > r4 ? 1 : (r4 == r4 ? 0 : -1))
            if (r2 == 0) goto L1d
            if (r1 == 0) goto L1b
            long r2 = r1.sequenceNumber     // Catch: java.lang.Throwable -> L53
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 == 0) goto L1d
        L1b:
            monitor-exit(r6)
            return r0
        L1d:
            if (r1 != 0) goto L56
            com.bumptech.glide.disklrucache.DiskLruCache$Entry r1 = new com.bumptech.glide.disklrucache.DiskLruCache$Entry     // Catch: java.lang.Throwable -> L53
            r2 = 0
            r1.<init>(r6, r7, r2)     // Catch: java.lang.Throwable -> L53
            java.util.LinkedHashMap<java.lang.String, com.bumptech.glide.disklrucache.DiskLruCache$Entry> r2 = r6.lruEntries     // Catch: java.lang.Throwable -> L53
            r2.put(r7, r1)     // Catch: java.lang.Throwable -> L53
        L2a:
            com.bumptech.glide.disklrucache.DiskLruCache$Editor r0 = new com.bumptech.glide.disklrucache.DiskLruCache$Editor     // Catch: java.lang.Throwable -> L53
            r2 = 0
            r0.<init>(r6, r1, r2)     // Catch: java.lang.Throwable -> L53
            r1.currentEditor = r0     // Catch: java.lang.Throwable -> L53
            java.io.Writer r2 = r6.journalWriter     // Catch: java.lang.Throwable -> L53
            java.lang.String r3 = "DIRTY"
            r2.append(r3)     // Catch: java.lang.Throwable -> L53
            java.io.Writer r2 = r6.journalWriter     // Catch: java.lang.Throwable -> L53
            r3 = 32
            r2.append(r3)     // Catch: java.lang.Throwable -> L53
            java.io.Writer r2 = r6.journalWriter     // Catch: java.lang.Throwable -> L53
            r2.append(r7)     // Catch: java.lang.Throwable -> L53
            java.io.Writer r2 = r6.journalWriter     // Catch: java.lang.Throwable -> L53
            r3 = 10
            r2.append(r3)     // Catch: java.lang.Throwable -> L53
            java.io.Writer r2 = r6.journalWriter     // Catch: java.lang.Throwable -> L53
            r2.flush()     // Catch: java.lang.Throwable -> L53
            goto L1b
        L53:
            r2 = move-exception
            monitor-exit(r6)
            throw r2
        L56:
            com.bumptech.glide.disklrucache.DiskLruCache$Editor r2 = r1.currentEditor     // Catch: java.lang.Throwable -> L53
            if (r2 == 0) goto L2a
            goto L1b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.disklrucache.DiskLruCache.edit$7efc45cb(java.lang.String):com.bumptech.glide.disklrucache.DiskLruCache$Editor");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void completeEdit(Editor editor, boolean success) throws IOException {
        Entry entry = editor.entry;
        if (entry.currentEditor != editor) {
            throw new IllegalStateException();
        }
        if (success && !entry.readable) {
            for (int i = 0; i < this.valueCount; i++) {
                if (!editor.written[i]) {
                    editor.abort();
                    throw new IllegalStateException("Newly created entry didn't create value for index " + i);
                }
                if (!entry.dirtyFiles[i].exists()) {
                    editor.abort();
                    break;
                }
            }
        }
        for (int i2 = 0; i2 < this.valueCount; i2++) {
            File dirty = entry.dirtyFiles[i2];
            if (success) {
                if (dirty.exists()) {
                    File clean = entry.cleanFiles[i2];
                    dirty.renameTo(clean);
                    long oldLength = entry.lengths[i2];
                    long newLength = clean.length();
                    entry.lengths[i2] = newLength;
                    this.size = (this.size - oldLength) + newLength;
                }
            } else {
                deleteIfExists(dirty);
            }
        }
        this.redundantOpCount++;
        entry.currentEditor = null;
        if (!(entry.readable | success)) {
            this.lruEntries.remove(entry.key);
            this.journalWriter.append((CharSequence) "REMOVE");
            this.journalWriter.append(' ');
            this.journalWriter.append((CharSequence) entry.key);
            this.journalWriter.append('\n');
        } else {
            entry.readable = true;
            this.journalWriter.append((CharSequence) "CLEAN");
            this.journalWriter.append(' ');
            this.journalWriter.append((CharSequence) entry.key);
            this.journalWriter.append((CharSequence) entry.getLengths());
            this.journalWriter.append('\n');
            if (success) {
                long j = this.nextSequenceNumber;
                this.nextSequenceNumber = 1 + j;
                entry.sequenceNumber = j;
            }
        }
        this.journalWriter.flush();
        if (this.size > this.maxSize || journalRebuildRequired()) {
            this.executorService.submit(this.cleanupCallable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean journalRebuildRequired() {
        return this.redundantOpCount >= 2000 && this.redundantOpCount >= this.lruEntries.size();
    }

    public final synchronized boolean remove(String key) throws IOException {
        boolean z;
        checkNotClosed();
        Entry entry = this.lruEntries.get(key);
        if (entry == null || entry.currentEditor != null) {
            z = false;
        } else {
            for (int i = 0; i < this.valueCount; i++) {
                File file = entry.cleanFiles[i];
                if (file.exists() && !file.delete()) {
                    throw new IOException("failed to delete " + file);
                }
                this.size -= entry.lengths[i];
                entry.lengths[i] = 0;
            }
            this.redundantOpCount++;
            this.journalWriter.append((CharSequence) "REMOVE");
            this.journalWriter.append(' ');
            this.journalWriter.append((CharSequence) key);
            this.journalWriter.append('\n');
            this.lruEntries.remove(key);
            if (journalRebuildRequired()) {
                this.executorService.submit(this.cleanupCallable);
            }
            z = true;
        }
        return z;
    }

    private void checkNotClosed() {
        if (this.journalWriter == null) {
            throw new IllegalStateException("cache is closed");
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public final synchronized void close() throws IOException {
        if (this.journalWriter != null) {
            Iterator i$ = new ArrayList(this.lruEntries.values()).iterator();
            while (i$.hasNext()) {
                Entry entry = (Entry) i$.next();
                if (entry.currentEditor != null) {
                    entry.currentEditor.abort();
                }
            }
            trimToSize();
            this.journalWriter.close();
            this.journalWriter = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void trimToSize() throws IOException {
        while (this.size > this.maxSize) {
            Map.Entry<String, Entry> toEvict = this.lruEntries.entrySet().iterator().next();
            remove(toEvict.getKey());
        }
    }

    /* loaded from: classes.dex */
    public final class Value {
        private final File[] files;
        private final String key;
        private final long[] lengths;
        private final long sequenceNumber;

        /* synthetic */ Value(DiskLruCache x0, String x1, long x2, File[] x3, long[] x4, byte b) {
            this(x1, x2, x3, x4);
        }

        private Value(String key, long sequenceNumber, File[] files, long[] lengths) {
            this.key = key;
            this.sequenceNumber = sequenceNumber;
            this.files = files;
            this.lengths = lengths;
        }

        public final File getFile$54ec799f() {
            return this.files[0];
        }
    }

    /* loaded from: classes.dex */
    public final class Editor {
        private boolean committed;
        final Entry entry;
        final boolean[] written;

        /* synthetic */ Editor(DiskLruCache x0, Entry x1, byte b) {
            this(x1);
        }

        private Editor(Entry entry) {
            this.entry = entry;
            this.written = entry.readable ? null : new boolean[DiskLruCache.this.valueCount];
        }

        public final File getFile$54ec799f() throws IOException {
            File dirtyFile;
            synchronized (DiskLruCache.this) {
                if (this.entry.currentEditor != this) {
                    throw new IllegalStateException();
                }
                if (!this.entry.readable) {
                    this.written[0] = true;
                }
                dirtyFile = this.entry.dirtyFiles[0];
                if (!DiskLruCache.this.directory.exists()) {
                    DiskLruCache.this.directory.mkdirs();
                }
            }
            return dirtyFile;
        }

        public final void commit() throws IOException {
            DiskLruCache.this.completeEdit(this, true);
            this.committed = true;
        }

        public final void abort() throws IOException {
            DiskLruCache.this.completeEdit(this, false);
        }

        public final void abortUnlessCommitted() {
            if (!this.committed) {
                try {
                    abort();
                } catch (IOException e) {
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class Entry {
        File[] cleanFiles;
        Editor currentEditor;
        File[] dirtyFiles;
        final String key;
        final long[] lengths;
        boolean readable;
        long sequenceNumber;

        /* synthetic */ Entry(DiskLruCache x0, String x1, byte b) {
            this(x1);
        }

        private Entry(String key) {
            this.key = key;
            this.lengths = new long[DiskLruCache.this.valueCount];
            this.cleanFiles = new File[DiskLruCache.this.valueCount];
            this.dirtyFiles = new File[DiskLruCache.this.valueCount];
            StringBuilder fileBuilder = new StringBuilder(key).append('.');
            int truncateTo = fileBuilder.length();
            for (int i = 0; i < DiskLruCache.this.valueCount; i++) {
                fileBuilder.append(i);
                this.cleanFiles[i] = new File(DiskLruCache.this.directory, fileBuilder.toString());
                fileBuilder.append(".tmp");
                this.dirtyFiles[i] = new File(DiskLruCache.this.directory, fileBuilder.toString());
                fileBuilder.setLength(truncateTo);
            }
        }

        public final String getLengths() throws IOException {
            StringBuilder result = new StringBuilder();
            long[] arr$ = this.lengths;
            for (long size : arr$) {
                result.append(' ').append(size);
            }
            return result.toString();
        }

        final void setLengths(String[] strings) throws IOException {
            if (strings.length != DiskLruCache.this.valueCount) {
                throw invalidLengths(strings);
            }
            for (int i = 0; i < strings.length; i++) {
                try {
                    this.lengths[i] = Long.parseLong(strings[i]);
                } catch (NumberFormatException e) {
                    throw invalidLengths(strings);
                }
            }
        }

        private static IOException invalidLengths(String[] strings) throws IOException {
            throw new IOException("unexpected journal line: " + Arrays.toString(strings));
        }
    }
}
