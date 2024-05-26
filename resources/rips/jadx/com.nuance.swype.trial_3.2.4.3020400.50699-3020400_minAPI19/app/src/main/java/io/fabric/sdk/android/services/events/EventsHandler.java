package io.fabric.sdk.android.services.events;

import android.content.Context;
import io.fabric.sdk.android.services.common.CommonUtils;
import java.util.concurrent.ScheduledExecutorService;

/* loaded from: classes.dex */
public abstract class EventsHandler<T> implements EventsStorageListener {
    protected final Context context;
    protected final ScheduledExecutorService executor;
    public EventsStrategy<T> strategy;

    public abstract EventsStrategy<T> getDisabledEventsStrategy();

    public EventsHandler(Context context, EventsStrategy<T> strategy, EventsFilesManager filesManager, ScheduledExecutorService executor) {
        this.context = context.getApplicationContext();
        this.executor = executor;
        this.strategy = strategy;
        filesManager.registerRollOverListener(this);
    }

    public final void recordEventAsync(final T event, final boolean sendImmediately) {
        executeAsync(new Runnable() { // from class: io.fabric.sdk.android.services.events.EventsHandler.1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.lang.Runnable
            public final void run() {
                try {
                    EventsHandler.this.strategy.recordEvent(event);
                    if (sendImmediately) {
                        EventsHandler.this.strategy.rollFileOver();
                    }
                } catch (Exception e) {
                    CommonUtils.logControlledError$43da9ce8(EventsHandler.this.context, "Failed to record event.");
                }
            }
        });
    }

    public final void recordEventSync(final T event) {
        try {
            this.executor.submit(new Runnable() { // from class: io.fabric.sdk.android.services.events.EventsHandler.2
                /* JADX WARN: Multi-variable type inference failed */
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        EventsHandler.this.strategy.recordEvent(event);
                    } catch (Exception e) {
                        CommonUtils.logControlledError$43da9ce8(EventsHandler.this.context, "Crashlytics failed to record event");
                    }
                }
            }).get();
        } catch (Exception e) {
            CommonUtils.logControlledError$43da9ce8(this.context, "Failed to run events task");
        }
    }

    @Override // io.fabric.sdk.android.services.events.EventsStorageListener
    public final void onRollOver$552c4e01() {
        executeAsync(new Runnable() { // from class: io.fabric.sdk.android.services.events.EventsHandler.3
            @Override // java.lang.Runnable
            public final void run() {
                try {
                    EventsHandler.this.strategy.sendEvents();
                } catch (Exception e) {
                    CommonUtils.logControlledError$43da9ce8(EventsHandler.this.context, "Failed to send events files.");
                }
            }
        });
    }

    public final void executeAsync(Runnable runnable) {
        try {
            this.executor.submit(runnable);
        } catch (Exception e) {
            CommonUtils.logControlledError$43da9ce8(this.context, "Failed to submit events task");
        }
    }
}
