package com.flurry.sdk;

import com.flurry.sdk.lx;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class ke<T extends lx> {
    private static final String a = ke.class.getSimpleName();
    private final jw<Object, T> b = new jw<>();
    private final HashMap<T, Object> c = new HashMap<>();
    private final HashMap<T, Future<?>> d = new HashMap<>();
    private final ThreadPoolExecutor e;

    public ke(String str, TimeUnit timeUnit, BlockingQueue<Runnable> blockingQueue) {
        this.e = new ThreadPoolExecutor(timeUnit, blockingQueue) { // from class: com.flurry.sdk.ke.1
            @Override // java.util.concurrent.ThreadPoolExecutor
            protected final void beforeExecute(Thread thread, Runnable runnable) {
                super.beforeExecute(thread, runnable);
                final lx a2 = ke.a(runnable);
                if (a2 != null) {
                    new lw() { // from class: com.flurry.sdk.ke.1.1
                        @Override // com.flurry.sdk.lw
                        public final void a() {
                        }
                    }.run();
                }
            }

            @Override // java.util.concurrent.ThreadPoolExecutor
            protected final void afterExecute(Runnable runnable, Throwable th) {
                super.afterExecute(runnable, th);
                final lx a2 = ke.a(runnable);
                if (a2 != null) {
                    synchronized (ke.this.d) {
                        ke.this.d.remove(a2);
                    }
                    ke.this.b(a2);
                    new lw() { // from class: com.flurry.sdk.ke.1.2
                        @Override // com.flurry.sdk.lw
                        public final void a() {
                        }
                    }.run();
                }
            }

            @Override // java.util.concurrent.AbstractExecutorService
            protected final <V> RunnableFuture<V> newTaskFor(Callable<V> callable) {
                throw new UnsupportedOperationException("Callable not supported");
            }

            @Override // java.util.concurrent.AbstractExecutorService
            protected final <V> RunnableFuture<V> newTaskFor(Runnable runnable, V v) {
                kd kdVar = new kd(runnable, v);
                synchronized (ke.this.d) {
                    ke.this.d.put((lx) runnable, kdVar);
                }
                return kdVar;
            }
        };
        this.e.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy() { // from class: com.flurry.sdk.ke.2
            @Override // java.util.concurrent.ThreadPoolExecutor.DiscardPolicy, java.util.concurrent.RejectedExecutionHandler
            public final void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                super.rejectedExecution(runnable, threadPoolExecutor);
                final lx a2 = ke.a(runnable);
                if (a2 != null) {
                    synchronized (ke.this.d) {
                        ke.this.d.remove(a2);
                    }
                    ke.this.b(a2);
                    new lw() { // from class: com.flurry.sdk.ke.2.1
                        @Override // com.flurry.sdk.lw
                        public final void a() {
                        }
                    }.run();
                }
            }
        });
        this.e.setThreadFactory(new lk(str));
    }

    public final synchronized void a(Object obj, T t) {
        if (obj != null && t != null) {
            b(obj, t);
            this.e.submit(t);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final synchronized void a(Object obj) {
        if (obj != null) {
            HashSet hashSet = new HashSet();
            hashSet.addAll(this.b.a(obj));
            Iterator it = hashSet.iterator();
            while (it.hasNext()) {
                a((ke<T>) it.next());
            }
        }
    }

    private synchronized void a(final T t) {
        Future<?> remove;
        if (t != null) {
            synchronized (this.d) {
                remove = this.d.remove(t);
            }
            b(t);
            if (remove != null) {
                remove.cancel(true);
            }
            new lw() { // from class: com.flurry.sdk.ke.3
                @Override // com.flurry.sdk.lw
                public final void a() {
                    t.g();
                }
            }.run();
        }
    }

    private synchronized void b(Object obj, T t) {
        this.b.a((jw<Object, T>) obj, t);
        this.c.put(t, obj);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void b(T t) {
        c(this.c.get(t), t);
    }

    private synchronized void c(Object obj, T t) {
        this.b.b(obj, t);
        this.c.remove(t);
    }

    static /* synthetic */ lx a(Runnable runnable) {
        if (runnable instanceof kd) {
            return (lx) ((kd) runnable).a();
        }
        if (runnable instanceof lx) {
            return (lx) runnable;
        }
        kf.a(6, a, "Unknown runnable class: " + runnable.getClass().getName());
        return null;
    }
}
