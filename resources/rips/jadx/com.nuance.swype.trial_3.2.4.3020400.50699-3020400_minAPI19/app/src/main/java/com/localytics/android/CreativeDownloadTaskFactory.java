package com.localytics.android;

import com.localytics.android.ICreativeDownloadTask;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class CreativeDownloadTaskFactory {
    public ICreativeDownloadTask creativeDownloadTask(MarketingMessage campaign, ICreativeDownloadTask.Priority priority, int sequence, LocalyticsDao localyticsDao, ICreativeDownloadTaskCallback callback) {
        return new CreativeDownloadTask(campaign, priority, sequence, localyticsDao, callback);
    }
}
