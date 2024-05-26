package android.support.v4.util;

import java.util.LinkedHashMap;

/* loaded from: classes.dex */
public class LruCache<K, V> {
    private int evictionCount;
    private int hitCount;
    private int missCount;
    private int putCount;
    private int size;
    private int maxSize = 6;
    private final LinkedHashMap<K, V> map = new LinkedHashMap<>(0, 0.75f, true);

    public final V get(K key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        synchronized (this) {
            V mapValue = this.map.get(key);
            if (mapValue != null) {
                this.hitCount++;
                return mapValue;
            }
            this.missCount++;
            return null;
        }
    }

    public final V put(K key, V value) {
        V previous;
        if (key == null) {
            throw new NullPointerException("key == null || value == null");
        }
        synchronized (this) {
            this.putCount++;
            this.size++;
            previous = this.map.put(key, value);
            if (previous != null) {
                this.size--;
            }
        }
        trimToSize(this.maxSize);
        return previous;
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0032, code lost:            throw new java.lang.IllegalStateException(getClass().getName() + ".sizeOf() is reporting inconsistent results!");     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void trimToSize(int r6) {
        /*
            r5 = this;
        L0:
            monitor-enter(r5)
            int r2 = r5.size     // Catch: java.lang.Throwable -> L33
            if (r2 < 0) goto L11
            java.util.LinkedHashMap<K, V> r2 = r5.map     // Catch: java.lang.Throwable -> L33
            boolean r2 = r2.isEmpty()     // Catch: java.lang.Throwable -> L33
            if (r2 == 0) goto L36
            int r2 = r5.size     // Catch: java.lang.Throwable -> L33
            if (r2 == 0) goto L36
        L11:
            java.lang.IllegalStateException r2 = new java.lang.IllegalStateException     // Catch: java.lang.Throwable -> L33
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L33
            r3.<init>()     // Catch: java.lang.Throwable -> L33
            java.lang.Class r4 = r5.getClass()     // Catch: java.lang.Throwable -> L33
            java.lang.String r4 = r4.getName()     // Catch: java.lang.Throwable -> L33
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch: java.lang.Throwable -> L33
            java.lang.String r4 = ".sizeOf() is reporting inconsistent results!"
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch: java.lang.Throwable -> L33
            java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> L33
            r2.<init>(r3)     // Catch: java.lang.Throwable -> L33
            throw r2     // Catch: java.lang.Throwable -> L33
        L33:
            r2 = move-exception
            monitor-exit(r5)     // Catch: java.lang.Throwable -> L33
            throw r2
        L36:
            int r2 = r5.size     // Catch: java.lang.Throwable -> L33
            if (r2 <= r6) goto L42
            java.util.LinkedHashMap<K, V> r2 = r5.map     // Catch: java.lang.Throwable -> L33
            boolean r2 = r2.isEmpty()     // Catch: java.lang.Throwable -> L33
            if (r2 == 0) goto L44
        L42:
            monitor-exit(r5)     // Catch: java.lang.Throwable -> L33
            return
        L44:
            java.util.LinkedHashMap<K, V> r2 = r5.map     // Catch: java.lang.Throwable -> L33
            java.util.Set r2 = r2.entrySet()     // Catch: java.lang.Throwable -> L33
            java.util.Iterator r2 = r2.iterator()     // Catch: java.lang.Throwable -> L33
            java.lang.Object r1 = r2.next()     // Catch: java.lang.Throwable -> L33
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1     // Catch: java.lang.Throwable -> L33
            java.lang.Object r0 = r1.getKey()     // Catch: java.lang.Throwable -> L33
            r1.getValue()     // Catch: java.lang.Throwable -> L33
            java.util.LinkedHashMap<K, V> r2 = r5.map     // Catch: java.lang.Throwable -> L33
            r2.remove(r0)     // Catch: java.lang.Throwable -> L33
            int r2 = r5.size     // Catch: java.lang.Throwable -> L33
            int r2 = r2 + (-1)
            r5.size = r2     // Catch: java.lang.Throwable -> L33
            int r2 = r5.evictionCount     // Catch: java.lang.Throwable -> L33
            int r2 = r2 + 1
            r5.evictionCount = r2     // Catch: java.lang.Throwable -> L33
            monitor-exit(r5)     // Catch: java.lang.Throwable -> L33
            goto L0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.util.LruCache.trimToSize(int):void");
    }

    public final synchronized String toString() {
        String format;
        synchronized (this) {
            int accesses = this.hitCount + this.missCount;
            int hitPercent = accesses != 0 ? (this.hitCount * 100) / accesses : 0;
            format = String.format("LruCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]", Integer.valueOf(this.maxSize), Integer.valueOf(this.hitCount), Integer.valueOf(this.missCount), Integer.valueOf(hitPercent));
        }
        return format;
    }
}
