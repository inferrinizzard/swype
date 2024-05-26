package io.fabric.sdk.android.services.common;

import io.fabric.sdk.android.Fabric;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: classes.dex */
public final class ExecutorUtils {
    public static ScheduledExecutorService buildSingleThreadScheduledExecutorService(String name) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(getNamedThreadFactory(name));
        addDelayedShutdownHook(name, executor);
        return executor;
    }

    public static final ThreadFactory getNamedThreadFactory(final String threadNameTemplate) {
        final AtomicLong count = new AtomicLong(1L);
        return new ThreadFactory() { // from class: io.fabric.sdk.android.services.common.ExecutorUtils.1
            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(final Runnable runnable) {
                Thread thread = Executors.defaultThreadFactory().newThread(new BackgroundPriorityRunnable() { // from class: io.fabric.sdk.android.services.common.ExecutorUtils.1.1
                    @Override // io.fabric.sdk.android.services.common.BackgroundPriorityRunnable
                    public final void onRun() {
                        runnable.run();
                    }
                });
                thread.setName(threadNameTemplate + count.getAndIncrement());
                return thread;
            }
        };
    }

    public static final void addDelayedShutdownHook(final String serviceName, final ExecutorService service) {
        final TimeUnit timeUnit = TimeUnit.SECONDS;
        Runtime.getRuntime().addShutdownHook(new Thread(new BackgroundPriorityRunnable() { // from class: io.fabric.sdk.android.services.common.ExecutorUtils.2
            final /* synthetic */ long val$terminationTimeout = 2;

            @Override // io.fabric.sdk.android.services.common.BackgroundPriorityRunnable
            public final void onRun() {
                try {
                    Fabric.getLogger();
                    new StringBuilder("Executing shutdown hook for ").append(serviceName);
                    service.shutdown();
                    if (!service.awaitTermination(this.val$terminationTimeout, timeUnit)) {
                        Fabric.getLogger();
                        new StringBuilder().append(serviceName).append(" did not shut down in the allocated time. Requesting immediate shutdown.");
                        service.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    Fabric.getLogger();
                    String.format(Locale.US, "Interrupted while waiting for %s to shut down. Requesting immediate shutdown.", serviceName);
                    service.shutdownNow();
                }
            }
        }, "Crashlytics Shutdown Hook for " + serviceName));
    }
}
