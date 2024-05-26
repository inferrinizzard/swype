package com.localytics.android;

import android.annotation.TargetApi;
import android.os.Handler;
import android.util.SparseArray;
import com.localytics.android.ICreativeDownloadTask;
import com.localytics.android.Localytics;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
class CreativeManager implements ICreativeDownloadTaskCallback {
    private static final int CORE_POOL_SIZE = 5;
    private static final long KEEP_ALIVE_SECS = 10;
    private static final int MAX_POOL_SIZE = 5;
    private PriorityBlockingQueue<Runnable> mBlockingQueue;
    private CreativeDownloadTaskFactory mCreativeDownloadTaskFactory;
    private ThreadPoolExecutor mExecutor;
    private FirstDownloadedCallback mFirstDownloadedCallback;
    private Handler mHandler;
    private LastDownloadedCallback mLastDownloadedCallback;
    protected LocalyticsDao mLocalyticsDao;
    private SparseArray<ICreativeDownloadTask> mNormalDownloads;
    private SparseArray<ICreativeDownloadTask> mPriorityDownloads;
    private int mSequence;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface FirstDownloadedCallback {
        void onFirstDownloaded();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface LastDownloadedCallback {
        void onLastDownloaded();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CreativeManager(LocalyticsDao localyticsDao, Handler handler) {
        this(localyticsDao, handler, new CreativeDownloadTaskFactory());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CreativeManager(LocalyticsDao localyticsDao, Handler handler, CreativeDownloadTaskFactory creativeDownloadTaskFactory) {
        this.mLocalyticsDao = localyticsDao;
        this.mHandler = handler;
        this.mCreativeDownloadTaskFactory = creativeDownloadTaskFactory;
        this.mBlockingQueue = new PriorityBlockingQueue<>();
        this.mExecutor = new ThreadPoolExecutor(5, 5, KEEP_ALIVE_SECS, TimeUnit.SECONDS, this.mBlockingQueue, new ThreadFactory() { // from class: com.localytics.android.CreativeManager.1
            private final AtomicInteger threadCount = new AtomicInteger();

            @Override // java.util.concurrent.ThreadFactory
            public Thread newThread(Runnable r) {
                String name = String.format("%s #%d", CreativeManager.class.getSimpleName(), Integer.valueOf(this.threadCount.getAndIncrement()));
                Thread thread = new Thread(r, name);
                thread.setPriority(10);
                return thread;
            }
        });
        this.mExecutor.allowCoreThreadTimeOut(true);
        this.mNormalDownloads = new SparseArray<>();
        this.mPriorityDownloads = new SparseArray<>();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void downloadCreatives(List<MarketingMessage> campaigns, LastDownloadedCallback lastDownloadedCallback) {
        this.mLastDownloadedCallback = lastDownloadedCallback;
        for (MarketingMessage campaign : campaigns) {
            int campaignId = JsonHelper.getSafeIntegerFromMap(campaign, "campaign_id");
            if (this.mPriorityDownloads.get(campaignId) == null && this.mNormalDownloads.get(campaignId) == null) {
                CreativeDownloadTaskFactory creativeDownloadTaskFactory = this.mCreativeDownloadTaskFactory;
                ICreativeDownloadTask.Priority priority = ICreativeDownloadTask.Priority.NORMAL;
                int i = this.mSequence;
                this.mSequence = i + 1;
                ICreativeDownloadTask task = creativeDownloadTaskFactory.creativeDownloadTask(campaign, priority, i, this.mLocalyticsDao, this);
                this.mNormalDownloads.put(campaignId, task);
                this.mExecutor.execute(task);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void priorityDownloadCreatives(List<MarketingMessage> campaigns, FirstDownloadedCallback firstDownloadedCallback) {
        this.mFirstDownloadedCallback = firstDownloadedCallback;
        for (MarketingMessage campaign : campaigns) {
            int campaignId = JsonHelper.getSafeIntegerFromMap(campaign, "campaign_id");
            if (this.mPriorityDownloads.get(campaignId) == null) {
                ICreativeDownloadTask task = this.mNormalDownloads.get(campaignId);
                if (task == null) {
                    CreativeDownloadTaskFactory creativeDownloadTaskFactory = this.mCreativeDownloadTaskFactory;
                    ICreativeDownloadTask.Priority priority = ICreativeDownloadTask.Priority.HIGH;
                    int i = this.mSequence;
                    this.mSequence = i + 1;
                    task = creativeDownloadTaskFactory.creativeDownloadTask(campaign, priority, i, this.mLocalyticsDao, this);
                    this.mExecutor.execute(task);
                } else if (this.mBlockingQueue.remove(task)) {
                    task.setPriority(ICreativeDownloadTask.Priority.HIGH);
                    this.mExecutor.execute(task);
                }
                this.mNormalDownloads.remove(campaignId);
                this.mPriorityDownloads.put(campaignId, task);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasPendingDownloads() {
        return this.mNormalDownloads.size() > 0 && this.mPriorityDownloads.size() > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<MarketingMessage> inAppCampaignsWithDownloadedCreatives(List<MarketingMessage> campaigns) {
        List<MarketingMessage> withCreatives = new LinkedList<>();
        for (MarketingMessage message : campaigns) {
            int ruleId = JsonHelper.getSafeIntegerFromMap(message, "_id");
            if (inAppCreativeExists(ruleId)) {
                withCreatives.add(message);
            }
        }
        return withCreatives;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean inAppCreativeExists(int ruleId) {
        return new File(getInAppLocalHtmlLocation(ruleId, this.mLocalyticsDao)).exists();
    }

    protected void handleCreativeForCampaign(MarketingMessage campaign) {
        if (JsonHelper.getSafeStringFromMap(campaign, "creative_url").endsWith(".zip")) {
            String zipFileDirPath = getZipFileDirPath(this.mLocalyticsDao);
            String unzipFileDirPath = JsonHelper.getSafeStringFromMap(campaign, "base_path");
            String zipName = JsonHelper.getSafeStringFromMap(campaign, "zip_name");
            String fullZipPath = zipFileDirPath + File.separator + zipName;
            if (Utils.decompressZipFile(zipFileDirPath, unzipFileDirPath, zipName)) {
                Utils.deleteFile(new File(fullZipPath));
            } else {
                Localytics.Log.e("Failed to unzip creative file: " + fullZipPath);
            }
        }
    }

    @Override // com.localytics.android.ICreativeDownloadTaskCallback
    public void onComplete(final ICreativeDownloadTask task) {
        this.mHandler.post(new Runnable() { // from class: com.localytics.android.CreativeManager.2
            @Override // java.lang.Runnable
            public void run() {
                MarketingMessage campaign = task.getCampaign();
                CreativeManager.this.handleCreativeForCampaign(campaign);
                CreativeManager.this.postDownloadCleanup(task);
            }
        });
    }

    @Override // com.localytics.android.ICreativeDownloadTaskCallback
    public void onError(final ICreativeDownloadTask task) {
        this.mHandler.post(new Runnable() { // from class: com.localytics.android.CreativeManager.3
            @Override // java.lang.Runnable
            public void run() {
                CreativeManager.this.postDownloadCleanup(task);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postDownloadCleanup(ICreativeDownloadTask task) {
        int campaignId = JsonHelper.getSafeIntegerFromMap(task.getCampaign(), "campaign_id");
        this.mNormalDownloads.remove(campaignId);
        if (this.mPriorityDownloads.get(campaignId) != null) {
            this.mPriorityDownloads.remove(campaignId);
            if (this.mFirstDownloadedCallback != null) {
                this.mFirstDownloadedCallback.onFirstDownloaded();
                this.mFirstDownloadedCallback = null;
            }
        }
        if (this.mNormalDownloads.size() == 0 && this.mPriorityDownloads.size() == 0 && this.mLastDownloadedCallback != null) {
            this.mLastDownloadedCallback.onLastDownloaded();
            this.mLastDownloadedCallback = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getInAppLocalHtmlLocation(long ruleId, LocalyticsDao localyticsDao) {
        return getInAppUnzipFileDirPath(ruleId, localyticsDao) + File.separator + "index.html";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getInAppLocalFileURL(long ruleId, boolean isZipped, LocalyticsDao localyticsDao) {
        StringBuilder builder = new StringBuilder();
        builder.append(getZipFileDirPath(localyticsDao));
        builder.append(File.separator);
        if (isZipped) {
            builder.append(String.format("amp_rule_%d.zip", Long.valueOf(ruleId)));
        } else {
            builder.append(String.format("marketing_rule_%d", Long.valueOf(ruleId)));
            if (!createDir(builder.toString())) {
                return null;
            }
            builder.append(File.separator);
            builder.append("index.html");
        }
        return builder.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getInboxLocalHtmlLocation(long inboxId, LocalyticsDao localyticsDao) {
        return getInboxUnzipFileDirPath(inboxId, localyticsDao) + File.separator + "index.html";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getInboxLocalFileURL(long inboxId, boolean isZipped, LocalyticsDao localyticsDao) {
        StringBuilder builder = new StringBuilder();
        builder.append(getZipFileDirPath(localyticsDao));
        builder.append(File.separator);
        if (isZipped) {
            builder.append(String.format("inbox_creative_assets_%d.zip", Long.valueOf(inboxId)));
        } else {
            builder.append(String.format("inbox_creative_assets_%d", Long.valueOf(inboxId)));
            if (!createDir(builder.toString())) {
                return null;
            }
            builder.append(File.separator);
            builder.append("index.html");
        }
        return builder.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getOldZipFileDirPath(LocalyticsDao localyticsDao) {
        return localyticsDao.getAppContext().getFilesDir().getAbsolutePath() + File.separator + ".localytics" + File.separator + localyticsDao.getAppKey();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @TargetApi(21)
    public static String getZipFileDirPath(LocalyticsDao localyticsDao) {
        StringBuilder builder = new StringBuilder();
        if (!localyticsDao.isUsingNewCreativeLocation()) {
            return getOldZipFileDirPath(localyticsDao);
        }
        builder.append(localyticsDao.getAppContext().getNoBackupFilesDir().getAbsolutePath());
        builder.append(File.separator);
        builder.append(".localytics");
        builder.append(File.separator);
        builder.append(localyticsDao.getAppKey());
        return builder.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getInAppUnzipFileDirPath(long ruleId, LocalyticsDao localyticsDao) {
        String path = getZipFileDirPath(localyticsDao) + File.separator + String.format("marketing_rule_%d", Long.valueOf(ruleId));
        if (createDir(path)) {
            return path;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getInboxUnzipFileDirPath(long inboxRowId, LocalyticsDao localyticsDao) {
        String path = getZipFileDirPath(localyticsDao) + File.separator + String.format("inbox_creative_assets_%d", Long.valueOf(inboxRowId));
        if (createDir(path)) {
            return path;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean createDir(String absolutePath) {
        File file = new File(absolutePath);
        if ((file.exists() && file.isDirectory()) || file.mkdirs()) {
            return true;
        }
        Localytics.Log.w(String.format("Could not create the directory %s for saving the decompressed file.", file.getAbsolutePath()));
        return false;
    }
}
