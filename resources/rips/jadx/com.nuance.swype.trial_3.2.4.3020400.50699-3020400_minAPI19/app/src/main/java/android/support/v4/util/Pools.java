package android.support.v4.util;

/* loaded from: classes.dex */
public final class Pools {

    /* loaded from: classes.dex */
    public interface Pool<T> {
        T acquire();

        boolean release(T t);
    }

    /* loaded from: classes.dex */
    public static class SimplePool<T> implements Pool<T> {
        private final Object[] mPool;
        private int mPoolSize;

        public SimplePool(int maxPoolSize) {
            if (maxPoolSize <= 0) {
                throw new IllegalArgumentException("The max pool size must be > 0");
            }
            this.mPool = new Object[maxPoolSize];
        }

        @Override // android.support.v4.util.Pools.Pool
        public final T acquire() {
            if (this.mPoolSize <= 0) {
                return null;
            }
            int i = this.mPoolSize - 1;
            T t = (T) this.mPool[i];
            this.mPool[i] = null;
            this.mPoolSize--;
            return t;
        }

        @Override // android.support.v4.util.Pools.Pool
        public final boolean release(T instance) {
            boolean z;
            int i = 0;
            while (true) {
                if (i >= this.mPoolSize) {
                    z = false;
                    break;
                }
                if (this.mPool[i] == instance) {
                    z = true;
                    break;
                }
                i++;
            }
            if (z) {
                throw new IllegalStateException("Already in the pool!");
            }
            if (this.mPoolSize >= this.mPool.length) {
                return false;
            }
            this.mPool[this.mPoolSize] = instance;
            this.mPoolSize++;
            return true;
        }
    }
}
