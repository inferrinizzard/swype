package com.crashlytics.android.answers;

import android.content.Context;
import com.nuance.connect.internal.common.Document;
import io.fabric.sdk.android.services.common.CurrentTimeProvider;
import io.fabric.sdk.android.services.events.EventsFilesManager;
import io.fabric.sdk.android.services.events.EventsStorage;
import io.fabric.sdk.android.services.settings.AnalyticsSettingsData;
import java.io.IOException;
import java.util.UUID;

/* loaded from: classes.dex */
final class SessionAnalyticsFilesManager extends EventsFilesManager<SessionEvent> {
    AnalyticsSettingsData analyticsSettingsData;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SessionAnalyticsFilesManager(Context context, SessionEventTransform transform, CurrentTimeProvider currentTimeProvider, EventsStorage eventStorage) throws IOException {
        super(context, transform, currentTimeProvider, eventStorage);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // io.fabric.sdk.android.services.events.EventsFilesManager
    public final String generateUniqueRollOverFileName() {
        UUID targetUUIDComponent = UUID.randomUUID();
        return "sa_" + targetUUIDComponent.toString() + Document.ID_SEPARATOR + this.currentTimeProvider.getCurrentTimeMillis() + ".tap";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // io.fabric.sdk.android.services.events.EventsFilesManager
    public final int getMaxFilesToKeep() {
        return this.analyticsSettingsData == null ? super.getMaxFilesToKeep() : this.analyticsSettingsData.maxPendingSendFileCount;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // io.fabric.sdk.android.services.events.EventsFilesManager
    public final int getMaxByteSizePerFile() {
        return this.analyticsSettingsData == null ? super.getMaxByteSizePerFile() : this.analyticsSettingsData.maxByteSizePerFile;
    }
}
