package io.fabric.sdk.android.services.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public final class PriorityThreadPoolExecutor extends ThreadPoolExecutor {
    private static final int CORE_POOL_SIZE;
    private static final int CPU_COUNT;
    private static final int MAXIMUM_POOL_SIZE;

    static {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        CPU_COUNT = availableProcessors;
        CORE_POOL_SIZE = availableProcessors + 1;
        MAXIMUM_POOL_SIZE = (CPU_COUNT * 2) + 1;
    }

    private <T extends Runnable & Dependency & Task & PriorityProvider> PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, TimeUnit unit, DependencyPriorityBlockingQueue<T> workQueue, ThreadFactory factory) {
        super(corePoolSize, maximumPoolSize, 1L, unit, workQueue, factory);
        prestartAllCoreThreads();
    }

    public static PriorityThreadPoolExecutor create() {
        return new PriorityThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, TimeUnit.SECONDS, new DependencyPriorityBlockingQueue(), new PriorityThreadFactory());
    }

    @Override // java.util.concurrent.AbstractExecutorService
    protected final <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new PriorityFutureTask(runnable, value);
    }

    @Override // java.util.concurrent.AbstractExecutorService
    protected final <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new PriorityFutureTask(callable);
    }

    @Override // java.util.concurrent.ThreadPoolExecutor, java.util.concurrent.Executor
    public final void execute(Runnable command) {
        if (PriorityTask.isProperDelegate(command)) {
            super.execute(command);
        } else {
            super.execute(newTaskFor(command, null));
        }
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    protected final void afterExecute(Runnable runnable, Throwable throwable) {
        Task task = (Task) runnable;
        task.setFinished(true);
        task.setError(throwable);
        ((DependencyPriorityBlockingQueue) super.getQueue()).recycleBlockedQueue();
        super.afterExecute(runnable, throwable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static final class PriorityThreadFactory implements ThreadFactory {
        private final int threadPriority = 10;

        @Override // java.util.concurrent.ThreadFactory
        public final Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setPriority(this.threadPriority);
            thread.setName("Queue");
            return thread;
        }
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public final /* bridge */ /* synthetic */ BlockingQueue getQueue() {
        return (DependencyPriorityBlockingQueue) super.getQueue();
    }
}
