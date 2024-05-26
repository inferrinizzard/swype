package com.localytics.android;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import java.io.File;
import java.util.List;

/* loaded from: classes.dex */
class ThumbnailManager extends CreativeManager {
    /* JADX INFO: Access modifiers changed from: package-private */
    public ThumbnailManager(LocalyticsDao localyticsDao, Handler handler) {
        this(localyticsDao, handler, new CreativeDownloadTaskFactory());
    }

    ThumbnailManager(LocalyticsDao localyticsDao, Handler handler, CreativeDownloadTaskFactory creativeDownloadTaskFactory) {
        super(localyticsDao, handler, creativeDownloadTaskFactory);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void downloadThumbnails(List<MarketingMessage> campaigns) {
        super.downloadCreatives(campaigns, null);
    }

    @Override // com.localytics.android.CreativeManager
    protected void handleCreativeForCampaign(MarketingMessage campaign) {
        long campaignId = JsonHelper.getSafeLongFromMap(campaign, "campaign_id");
        Intent intent = new Intent("com.localytics.intent.action.THUMBNAIL_DOWNLOADED");
        intent.putExtra("campaign_id", campaignId);
        LocalBroadcastManager.getInstance(this.mLocalyticsDao.getAppContext()).sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getInboxLocalThumbnailLocation(long inboxId, LocalyticsDao localyticsDao) {
        return getInboxThumbnailFileDirPath(localyticsDao) + File.separator + String.format("inbox_%d.png", Long.valueOf(inboxId));
    }

    static String getInboxThumbnailFileDirPath(LocalyticsDao localyticsDao) {
        StringBuilder builder = new StringBuilder();
        builder.append(getZipFileDirPath(localyticsDao));
        builder.append(File.separator);
        builder.append("thumbnails");
        if (createDir(builder.toString())) {
            return builder.toString();
        }
        return null;
    }
}
