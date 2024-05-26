package com.bumptech.glide.load.engine.cache;

import com.bumptech.glide.load.Key;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: classes.dex */
final class DiskCacheWriteLocker {
    final Map<Key, WriteLock> locks = new HashMap();
    final WriteLockPool writeLockPool = new WriteLockPool(0);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void release(Key key) {
        WriteLock writeLock;
        synchronized (this) {
            writeLock = this.locks.get(key);
            if (writeLock == null || writeLock.interestedThreads <= 0) {
                throw new IllegalArgumentException("Cannot release a lock that is not held, key: " + key + ", interestedThreads: " + (writeLock == null ? 0 : writeLock.interestedThreads));
            }
            int i = writeLock.interestedThreads - 1;
            writeLock.interestedThreads = i;
            if (i == 0) {
                WriteLock removed = this.locks.remove(key);
                if (!removed.equals(writeLock)) {
                    throw new IllegalStateException("Removed the wrong lock, expected to remove: " + writeLock + ", but actually removed: " + removed + ", key: " + key);
                }
                WriteLockPool writeLockPool = this.writeLockPool;
                synchronized (writeLockPool.pool) {
                    if (writeLockPool.pool.size() < 10) {
                        writeLockPool.pool.offer(removed);
                    }
                }
            }
        }
        writeLock.lock.unlock();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class WriteLock {
        int interestedThreads;
        final Lock lock;

        private WriteLock() {
            this.lock = new ReentrantLock();
        }

        /* synthetic */ WriteLock(byte b) {
            this();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class WriteLockPool {
        final Queue<WriteLock> pool;

        private WriteLockPool() {
            this.pool = new ArrayDeque();
        }

        /* synthetic */ WriteLockPool(byte b) {
            this();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final WriteLock obtain() {
            WriteLock result;
            synchronized (this.pool) {
                result = this.pool.poll();
            }
            if (result == null) {
                return new WriteLock((byte) 0);
            }
            return result;
        }
    }
}
