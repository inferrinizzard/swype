package io.fabric.sdk.android.services.events;

import android.content.Context;
import io.fabric.sdk.android.services.common.CommonUtils;
import java.io.IOException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes.dex */
public abstract class EnabledEventsStrategy<T> implements EventsStrategy<T> {
    public final Context context;
    protected final ScheduledExecutorService executorService;
    public final EventsFilesManager<T> filesManager;
    protected volatile int rolloverIntervalSeconds = -1;
    protected final AtomicReference<ScheduledFuture<?>> scheduledRolloverFutureRef = new AtomicReference<>();

    public EnabledEventsStrategy(Context context, ScheduledExecutorService executorService, EventsFilesManager<T> filesManager) {
        this.context = context;
        this.executorService = executorService;
        this.filesManager = filesManager;
    }

    private void scheduleTimeBasedFileRollOver(int initialDelaySecs, int frequencySecs) {
        try {
            Runnable rollOverRunnable = new TimeBasedFileRollOverRunnable(this.context, this);
            Context context = this.context;
            new StringBuilder("Scheduling time based file roll over every ").append(frequencySecs).append(" seconds");
            CommonUtils.logControlled$5ffc00fd(context);
            this.scheduledRolloverFutureRef.set(this.executorService.scheduleAtFixedRate(rollOverRunnable, initialDelaySecs, frequencySecs, TimeUnit.SECONDS));
        } catch (RejectedExecutionException e) {
            CommonUtils.logControlledError$43da9ce8(this.context, "Failed to schedule time based file roll over");
        }
    }

    @Override // io.fabric.sdk.android.services.events.FileRollOverManager
    public final void cancelTimeBasedFileRollOver() {
        if (this.scheduledRolloverFutureRef.get() != null) {
            CommonUtils.logControlled$5ffc00fd(this.context);
            this.scheduledRolloverFutureRef.get().cancel(false);
            this.scheduledRolloverFutureRef.set(null);
        }
    }

    public final void configureRollover(int rolloverIntervalSeconds) {
        this.rolloverIntervalSeconds = rolloverIntervalSeconds;
        scheduleTimeBasedFileRollOver(0, this.rolloverIntervalSeconds);
    }

    @Override // io.fabric.sdk.android.services.events.EventsManager
    public final void deleteAllEvents() {
        this.filesManager.deleteAllEventsFiles();
    }

    @Override // io.fabric.sdk.android.services.events.EventsManager
    public final void recordEvent(T event) {
        Context context = this.context;
        event.toString();
        CommonUtils.logControlled$5ffc00fd(context);
        try {
            this.filesManager.writeEvent(event);
        } catch (IOException e) {
            CommonUtils.logControlledError$43da9ce8(this.context, "Failed to write event.");
        }
        boolean z = this.rolloverIntervalSeconds != -1;
        boolean z2 = this.scheduledRolloverFutureRef.get() == null;
        if (!z || !z2) {
            return;
        }
        scheduleTimeBasedFileRollOver(this.rolloverIntervalSeconds, this.rolloverIntervalSeconds);
    }

    @Override // io.fabric.sdk.android.services.events.FileRollOverManager
    public final boolean rollFileOver() {
        try {
            return this.filesManager.rollFileOver();
        } catch (IOException e) {
            CommonUtils.logControlledError$43da9ce8(this.context, "Failed to roll file over.");
            return false;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x0073  */
    /* JADX WARN: Removed duplicated region for block: B:27:? A[RETURN, SYNTHETIC] */
    @Override // io.fabric.sdk.android.services.events.EventsManager
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void sendEvents() {
        /*
            r10 = this;
            r1 = 0
            io.fabric.sdk.android.services.events.FilesSender r3 = r10.getFilesSender()
            if (r3 != 0) goto Ld
            android.content.Context r0 = r10.context
            io.fabric.sdk.android.services.common.CommonUtils.logControlled$5ffc00fd(r0)
        Lc:
            return
        Ld:
            android.content.Context r0 = r10.context
            io.fabric.sdk.android.services.common.CommonUtils.logControlled$5ffc00fd(r0)
            io.fabric.sdk.android.services.events.EventsFilesManager<T> r0 = r10.filesManager
            java.util.List r0 = r0.getBatchOfFilesToSend()
            android.content.Context r2 = r10.context     // Catch: java.lang.Exception -> L56
            java.util.Locale r4 = java.util.Locale.US     // Catch: java.lang.Exception -> L56
            java.lang.String r5 = "attempt to send batch of %d files"
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch: java.lang.Exception -> L56
            r7 = 0
            int r8 = r0.size()     // Catch: java.lang.Exception -> L56
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch: java.lang.Exception -> L56
            r6[r7] = r8     // Catch: java.lang.Exception -> L56
            java.lang.String.format(r4, r5, r6)     // Catch: java.lang.Exception -> L56
            io.fabric.sdk.android.services.common.CommonUtils.logControlled$5ffc00fd(r2)     // Catch: java.lang.Exception -> L56
            r2 = r0
            r0 = r1
        L35:
            int r1 = r2.size()     // Catch: java.lang.Exception -> L79
            if (r1 <= 0) goto L71
            boolean r4 = r3.send(r2)     // Catch: java.lang.Exception -> L79
            if (r4 == 0) goto L4c
            int r1 = r2.size()     // Catch: java.lang.Exception -> L79
            int r1 = r1 + r0
            io.fabric.sdk.android.services.events.EventsFilesManager<T> r0 = r10.filesManager     // Catch: java.lang.Exception -> L56
            r0.deleteSentFiles(r2)     // Catch: java.lang.Exception -> L56
            r0 = r1
        L4c:
            if (r4 == 0) goto L71
            io.fabric.sdk.android.services.events.EventsFilesManager<T> r1 = r10.filesManager     // Catch: java.lang.Exception -> L79
            java.util.List r1 = r1.getBatchOfFilesToSend()     // Catch: java.lang.Exception -> L79
            r2 = r1
            goto L35
        L56:
            r0 = move-exception
        L57:
            android.content.Context r2 = r10.context
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            java.lang.String r4 = "Failed to send batch of analytics files to server: "
            r3.<init>(r4)
            java.lang.String r0 = r0.getMessage()
            java.lang.StringBuilder r0 = r3.append(r0)
            java.lang.String r0 = r0.toString()
            io.fabric.sdk.android.services.common.CommonUtils.logControlledError$43da9ce8(r2, r0)
            r0 = r1
        L71:
            if (r0 != 0) goto Lc
            io.fabric.sdk.android.services.events.EventsFilesManager<T> r0 = r10.filesManager
            r0.deleteOldestInRollOverIfOverMax()
            goto Lc
        L79:
            r1 = move-exception
            r9 = r1
            r1 = r0
            r0 = r9
            goto L57
        */
        throw new UnsupportedOperationException("Method not decompiled: io.fabric.sdk.android.services.events.EnabledEventsStrategy.sendEvents():void");
    }
}
