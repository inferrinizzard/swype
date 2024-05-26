package com.bumptech.glide.load.engine.cache;

import android.util.Log;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskCacheWriteLocker;
import java.io.File;
import java.io.IOException;

/* loaded from: classes.dex */
public final class DiskLruCacheWrapper implements DiskCache {
    private static DiskLruCacheWrapper wrapper = null;
    private final File directory;
    private DiskLruCache diskLruCache;
    private final int maxSize;
    private final DiskCacheWriteLocker writeLocker = new DiskCacheWriteLocker();
    private final SafeKeyGenerator safeKeyGenerator = new SafeKeyGenerator();

    public static synchronized DiskCache get(File directory, int maxSize) {
        DiskLruCacheWrapper diskLruCacheWrapper;
        synchronized (DiskLruCacheWrapper.class) {
            if (wrapper == null) {
                wrapper = new DiskLruCacheWrapper(directory, maxSize);
            }
            diskLruCacheWrapper = wrapper;
        }
        return diskLruCacheWrapper;
    }

    private DiskLruCacheWrapper(File directory, int maxSize) {
        this.directory = directory;
        this.maxSize = maxSize;
    }

    private synchronized DiskLruCache getDiskCache() throws IOException {
        if (this.diskLruCache == null) {
            this.diskLruCache = DiskLruCache.open$641e3723(this.directory, this.maxSize);
        }
        return this.diskLruCache;
    }

    @Override // com.bumptech.glide.load.engine.cache.DiskCache
    public final File get(Key key) {
        String safeKey = this.safeKeyGenerator.getSafeKey(key);
        try {
            DiskLruCache.Value value = getDiskCache().get(safeKey);
            if (value == null) {
                return null;
            }
            File result = value.getFile$54ec799f();
            return result;
        } catch (IOException e) {
            if (!Log.isLoggable("DiskLruCacheWrapper", 5)) {
                return null;
            }
            Log.w("DiskLruCacheWrapper", "Unable to get from disk cache", e);
            return null;
        }
    }

    @Override // com.bumptech.glide.load.engine.cache.DiskCache
    public final void put(Key key, DiskCache.Writer writer) {
        DiskCacheWriteLocker.WriteLock writeLock;
        String safeKey = this.safeKeyGenerator.getSafeKey(key);
        DiskCacheWriteLocker diskCacheWriteLocker = this.writeLocker;
        synchronized (diskCacheWriteLocker) {
            writeLock = diskCacheWriteLocker.locks.get(key);
            if (writeLock == null) {
                writeLock = diskCacheWriteLocker.writeLockPool.obtain();
                diskCacheWriteLocker.locks.put(key, writeLock);
            }
            writeLock.interestedThreads++;
        }
        writeLock.lock.lock();
        try {
            DiskLruCache.Editor editor = getDiskCache().edit(safeKey);
            if (editor != null) {
                try {
                    File file = editor.getFile$54ec799f();
                    if (writer.write(file)) {
                        editor.commit();
                    }
                } finally {
                    editor.abortUnlessCommitted();
                }
            }
        } catch (IOException e) {
            if (Log.isLoggable("DiskLruCacheWrapper", 5)) {
                Log.w("DiskLruCacheWrapper", "Unable to put to disk cache", e);
            }
        } finally {
            this.writeLocker.release(key);
        }
    }

    @Override // com.bumptech.glide.load.engine.cache.DiskCache
    public final void delete(Key key) {
        String safeKey = this.safeKeyGenerator.getSafeKey(key);
        try {
            getDiskCache().remove(safeKey);
        } catch (IOException e) {
            if (Log.isLoggable("DiskLruCacheWrapper", 5)) {
                Log.w("DiskLruCacheWrapper", "Unable to delete from disk cache", e);
            }
        }
    }
}
