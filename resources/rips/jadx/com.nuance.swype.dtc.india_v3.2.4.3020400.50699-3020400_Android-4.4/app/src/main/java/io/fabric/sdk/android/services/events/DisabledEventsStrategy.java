package io.fabric.sdk.android.services.events;

/* loaded from: classes.dex */
public class DisabledEventsStrategy<T> implements EventsStrategy<T> {
    @Override // io.fabric.sdk.android.services.events.EventsManager
    public final void sendEvents() {
    }

    @Override // io.fabric.sdk.android.services.events.EventsManager
    public final void deleteAllEvents() {
    }

    @Override // io.fabric.sdk.android.services.events.EventsManager
    public final void recordEvent(T event) {
    }

    @Override // io.fabric.sdk.android.services.events.FileRollOverManager
    public final void cancelTimeBasedFileRollOver() {
    }

    @Override // io.fabric.sdk.android.services.events.FileRollOverManager
    public final boolean rollFileOver() {
        return false;
    }

    @Override // io.fabric.sdk.android.services.events.EventsStrategy
    public final FilesSender getFilesSender() {
        return null;
    }
}
