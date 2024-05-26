package com.nuance.swype.input;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.nuance.android.util.LruCache;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class DrawBufferManager<T> {
    private static final int MAX_CACHE_COUNT = 1;
    protected static final LogManager.Log log = LogManager.getLog("DrawBufferManager");
    private final LruCache<Integer, T> bufferCache;

    public static DrawBufferManager from(Context context) {
        return IMEApplication.from(context).getDrawBufferManager();
    }

    public DrawBufferManager() {
        this.bufferCache = new LruCache<Integer, T>(1) { // from class: com.nuance.swype.input.DrawBufferManager.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.nuance.android.util.LruCache
            public void entryRemoved(boolean evicted, Integer key, Object oldValue, Object newValue) {
                if (oldValue != null) {
                    if (oldValue instanceof Bitmap) {
                        ((Bitmap) oldValue).recycle();
                    } else if (oldValue instanceof BitmapDrawable) {
                        DrawBufferManager.log.d("cache entry removed:", oldValue.toString());
                        ((BitmapDrawable) oldValue).getBitmap().recycle();
                    }
                }
            }
        };
    }

    public DrawBufferManager(int maxMega) {
        this.bufferCache = new LruCache<Integer, T>(maxMega * 1024 * 1024) { // from class: com.nuance.swype.input.DrawBufferManager.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.nuance.android.util.LruCache
            public void entryRemoved(boolean evicted, Integer key, Object oldValue, Object newValue) {
                if (oldValue != null) {
                    if (oldValue instanceof Bitmap) {
                        ((Bitmap) oldValue).recycle();
                    } else if (oldValue instanceof BitmapDrawable) {
                        ((BitmapDrawable) oldValue).getBitmap().recycle();
                    }
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.nuance.android.util.LruCache
            public int sizeOf(Integer key, Object object) {
                if (object instanceof Bitmap) {
                    int byteCount = ((Bitmap) object).getByteCount();
                    return byteCount;
                }
                if (!(object instanceof BitmapDrawable)) {
                    return 0;
                }
                int byteCount2 = ((BitmapDrawable) object).getBitmap().getByteCount();
                return byteCount2;
            }
        };
    }

    public T getObjectFromCache(int key) {
        if (this.bufferCache == null) {
            return null;
        }
        return this.bufferCache.get(Integer.valueOf(key));
    }

    public void addObjectToCache(int key, T value) {
        log.d("addObjectToCache...key: ", Integer.valueOf(key), "...value: ", value);
        if (key > 0 && value != null && getObjectFromCache(key) == null) {
            this.bufferCache.put(Integer.valueOf(key), value);
        }
    }

    public void removeObjectFromCache(int key) {
        log.d("removeObjectFromCache...key: ", Integer.valueOf(key));
        if (key > 0 && this.bufferCache != null) {
            this.bufferCache.remove(Integer.valueOf(key));
        }
    }

    public void evictAll() {
        log.d("evictAll...");
        if (this.bufferCache != null) {
            this.bufferCache.evictAll();
        }
    }
}
