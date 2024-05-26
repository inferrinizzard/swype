package com.localytics.android;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.facebook.internal.ServerProtocol;
import com.localytics.android.CreativeManager;
import com.localytics.android.InboxCampaign;
import com.localytics.android.Localytics;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.json.JSONArray;
import org.json.JSONException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class InboxManager extends BaseMarketingManager {
    protected CreativeManager mCreativeManager;
    private final Set<Object> mDisplayingInboxFragments;
    private long mLastRefreshTimeMillis;
    private ManifestHolder mManifestToProcess;
    private MarketingHandler mMarketingHandler;
    private InboxRefreshListener mRefreshCallback;
    private final Object mRefreshLock;
    private long mThrottleMillis;
    protected ThumbnailManager mThumbnailManager;
    private static final String SELECTION_BY_CAMPAIGN_ROW_ID = String.format("%s = ?", "_id");
    private static final String[] INBOX_INFO_PROJECTION = {"_id", "campaign_id", ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, "read", "creative_location", "received_date"};

    /* JADX INFO: Access modifiers changed from: package-private */
    public InboxManager(LocalyticsDao localyticsDao, MarketingHandler marketingHandler) {
        this(localyticsDao, marketingHandler, new CreativeManager(localyticsDao, marketingHandler), new ThumbnailManager(localyticsDao, marketingHandler));
    }

    InboxManager(LocalyticsDao localyticsDao, MarketingHandler marketingHandler, CreativeManager creativeManager, ThumbnailManager thumbnailManager) {
        super(localyticsDao);
        this.mDisplayingInboxFragments = Collections.newSetFromMap(new WeakHashMap());
        this.mManifestToProcess = null;
        this.mRefreshLock = new Object();
        this.mThrottleMillis = 240000L;
        this.mMarketingHandler = marketingHandler;
        this.mCreativeManager = creativeManager;
        this.mThumbnailManager = thumbnailManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canRefresh() {
        return this.mLastRefreshTimeMillis == 0 || this.mLocalyticsDao.getCurrentTimeMillis() - this.mLastRefreshTimeMillis > this.mThrottleMillis;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLastRefreshTimeMillis(long millis) {
        this.mLastRefreshTimeMillis = millis;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInboxRefreshCallback(InboxRefreshListener callback) {
        synchronized (this.mRefreshLock) {
            this.mRefreshCallback = callback;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void _processMarketingObject(boolean successful, Map<String, Object> marketingMap, Map<String, Object> config) {
        if (!_manifestProcessingAllowed()) {
            this.mManifestToProcess = new ManifestHolder(successful, _inboxOnlyMap(marketingMap), config);
            return;
        }
        this.mManifestToProcess = null;
        if (successful) {
            try {
                if (config != null) {
                    try {
                        long throttleSecs = JsonHelper.getSafeLongFromMap(config, "inbox_throttle");
                        if (throttleSecs > 0) {
                            this.mThrottleMillis = 1000 * throttleSecs;
                        }
                    } catch (JSONException e) {
                        Localytics.Log.e("JSONException", e);
                        synchronized (this.mRefreshLock) {
                            if (this.mRefreshCallback != null) {
                                final List<InboxCampaign> inboxCampaigns = _getInboxCampaigns();
                                final InboxRefreshListener refreshListener = this.mRefreshCallback;
                                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.localytics.android.InboxManager.2
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        refreshListener.localyticsRefreshedInboxCampaigns(inboxCampaigns);
                                    }
                                });
                                this.mRefreshCallback = null;
                            }
                            return;
                        }
                    }
                }
                HashMap<Integer, ContentValues> localInboxes = _queryInboxCampaignInfo();
                if (marketingMap != null) {
                    List<MarketingMessage> remoteInboxes = new ArrayList<>();
                    Object data = marketingMap.get("inboxes");
                    if (data == null) {
                        _removeDeactivatedInbox(localInboxes, new HashSet<>());
                        synchronized (this.mRefreshLock) {
                            if (this.mRefreshCallback != null) {
                                final List<InboxCampaign> inboxCampaigns2 = _getInboxCampaigns();
                                final InboxRefreshListener refreshListener2 = this.mRefreshCallback;
                                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.localytics.android.InboxManager.2
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        refreshListener2.localyticsRefreshedInboxCampaigns(inboxCampaigns2);
                                    }
                                });
                                this.mRefreshCallback = null;
                            }
                        }
                        return;
                    }
                    for (Map<String, Object> obj : JsonHelper.toList((JSONArray) JsonHelper.toJSON(data))) {
                        remoteInboxes.add(new MarketingMessage(obj));
                    }
                    HashSet<Integer> campaignIds = new HashSet<>();
                    for (MarketingMessage inbox : remoteInboxes) {
                        campaignIds.add((Integer) inbox.get("campaign_id"));
                    }
                    _removeDeactivatedInbox(localInboxes, campaignIds);
                    List<MarketingMessage> creativesToDownload = new ArrayList<>();
                    for (MarketingMessage remoteInbox : remoteInboxes) {
                        long inboxId = _saveInboxCampaign(remoteInbox, localInboxes, config);
                        if (inboxId > 0) {
                            MarketingMessage message = remoteInbox;
                            ContentValues localInbox = localInboxes.get(Integer.valueOf(JsonHelper.getSafeIntegerFromMap(message, "campaign_id")));
                            String oldCreativeLocation = localInbox == null ? null : localInbox.getAsString("creative_location");
                            String newCreativeLocation = JsonHelper.getSafeStringFromMap(message, "creative_location");
                            if (!TextUtils.isEmpty(newCreativeLocation) && !newCreativeLocation.equals(oldCreativeLocation)) {
                                message.put("_id", Long.valueOf(inboxId));
                                _updateMessageWithSpecialKeys(message);
                                creativesToDownload.add(message);
                            }
                        }
                    }
                    if (creativesToDownload.size() > 0 && !Constants.isTestModeEnabled()) {
                        this.mCreativeManager.downloadCreatives(creativesToDownload, new CreativeManager.LastDownloadedCallback() { // from class: com.localytics.android.InboxManager.1
                            @Override // com.localytics.android.CreativeManager.LastDownloadedCallback
                            public void onLastDownloaded() {
                                if (InboxManager.this.mManifestToProcess != null) {
                                    InboxManager.this.mMarketingHandler.localyticsDidDownloadManifest(InboxManager.this.mManifestToProcess.marketingMap, InboxManager.this.mManifestToProcess.config, InboxManager.this.mManifestToProcess.successful);
                                }
                            }
                        });
                    }
                } else {
                    _removeDeactivatedInbox(localInboxes, new HashSet<>());
                }
                this.mProvider.vacuumIfNecessary();
            } catch (Throwable th) {
                synchronized (this.mRefreshLock) {
                    if (this.mRefreshCallback != null) {
                        final List<InboxCampaign> inboxCampaigns3 = _getInboxCampaigns();
                        final InboxRefreshListener refreshListener3 = this.mRefreshCallback;
                        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.localytics.android.InboxManager.2
                            @Override // java.lang.Runnable
                            public void run() {
                                refreshListener3.localyticsRefreshedInboxCampaigns(inboxCampaigns3);
                            }
                        });
                        this.mRefreshCallback = null;
                    }
                    throw th;
                }
            }
        }
        synchronized (this.mRefreshLock) {
            if (this.mRefreshCallback != null) {
                final List<InboxCampaign> inboxCampaigns4 = _getInboxCampaigns();
                final InboxRefreshListener refreshListener4 = this.mRefreshCallback;
                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.localytics.android.InboxManager.2
                    @Override // java.lang.Runnable
                    public void run() {
                        refreshListener4.localyticsRefreshedInboxCampaigns(inboxCampaigns4);
                    }
                });
                this.mRefreshCallback = null;
            }
        }
    }

    private Map<String, Object> _inboxOnlyMap(Map<String, Object> marketingMap) {
        Map<String, Object> inboxOnly = new HashMap<>(1);
        if (marketingMap != null) {
            inboxOnly.put("inboxes", marketingMap.get("inboxes"));
        }
        return inboxOnly;
    }

    private void _updateMessageWithSpecialKeys(MarketingMessage message) {
        int inboxId = JsonHelper.getSafeIntegerFromMap(message, "_id");
        String remoteFileLocation = JsonHelper.getSafeStringFromMap(message, "creative_location");
        if (!TextUtils.isEmpty(remoteFileLocation)) {
            String localHtmlURL = "file://" + CreativeManager.getInboxLocalHtmlLocation(inboxId, this.mLocalyticsDao);
            String localFileLocation = CreativeManager.getInboxLocalFileURL(inboxId, remoteFileLocation.endsWith(".zip"), this.mLocalyticsDao);
            message.put("creative_url", remoteFileLocation);
            message.put("html_url", localHtmlURL);
            message.put("base_path", CreativeManager.getInboxUnzipFileDirPath(inboxId, this.mLocalyticsDao));
            message.put("zip_name", String.format("inbox_creative_assets_%d.zip", Integer.valueOf(inboxId)));
            message.put("local_file_location", localFileLocation);
            message.put("download_url", remoteFileLocation);
        }
    }

    boolean _manifestProcessingAllowed() {
        boolean z;
        synchronized (this.mDisplayingInboxFragments) {
            z = this.mDisplayingInboxFragments.size() == 0 && !this.mCreativeManager.hasPendingDownloads();
        }
        return z;
    }

    void _removeDeactivatedInbox(Map<Integer, ContentValues> localInboxes, Set<Integer> remoteCampaignIds) {
        Set<Integer> removing = new HashSet<>(localInboxes.keySet());
        Set<Integer> existing = new HashSet<>(remoteCampaignIds);
        removing.removeAll(existing);
        for (Integer campaignId : removing) {
            int rowId = localInboxes.get(campaignId).getAsInteger("_id").intValue();
            this.mProvider.remove("inbox_campaigns", SELECTION_BY_CAMPAIGN_ROW_ID, new String[]{Integer.toString(rowId)});
            _removeInboxAssetFiles(rowId);
        }
    }

    void _removeInboxAssetFiles(long rowId) {
        String zippedFilePath = CreativeManager.getInboxLocalFileURL(rowId, true, this.mLocalyticsDao);
        String unzippedDirPath = CreativeManager.getInboxUnzipFileDirPath(rowId, this.mLocalyticsDao);
        Utils.deleteFile(new File(unzippedDirPath));
        if (!new File(zippedFilePath).delete()) {
            Localytics.Log.w(String.format("Delete %s failed.", zippedFilePath));
        }
        String thumbnailFilePath = ThumbnailManager.getInboxLocalThumbnailLocation(rowId, this.mLocalyticsDao);
        if (new File(thumbnailFilePath).delete()) {
            Localytics.Log.w(String.format("Delete %s successfully.", thumbnailFilePath));
        }
    }

    HashMap<Integer, ContentValues> _queryInboxCampaignInfo() {
        HashMap<Integer, ContentValues> inboxInfo = new HashMap<>();
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("inbox_campaigns", INBOX_INFO_PROJECTION, null, null, null);
            while (cursor.moveToNext()) {
                int rowId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                int campaignId = cursor.getInt(cursor.getColumnIndexOrThrow("campaign_id"));
                int version = cursor.getInt(cursor.getColumnIndexOrThrow(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION));
                int read = cursor.getInt(cursor.getColumnIndexOrThrow("read"));
                long receivedDate = cursor.getLong(cursor.getColumnIndexOrThrow("received_date"));
                String creativeLocation = cursor.getString(cursor.getColumnIndexOrThrow("creative_location"));
                ContentValues values = new ContentValues(cursor.getColumnCount());
                values.put("_id", Integer.valueOf(rowId));
                values.put("campaign_id", Integer.valueOf(campaignId));
                values.put(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, Integer.valueOf(version));
                values.put("read", Integer.valueOf(read));
                values.put("received_date", Long.valueOf(receivedDate));
                values.put("creative_location", creativeLocation);
                inboxInfo.put(Integer.valueOf(campaignId), values);
            }
            return inboxInfo;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    long _saveInboxCampaign(Map<String, Object> data, Map<Integer, ContentValues> localInboxes, Map<String, Object> config) {
        int schemaVersion;
        ContentValues newValues = new ContentValues(data.size());
        newValues.put("campaign_id", Integer.valueOf(JsonHelper.getSafeIntegerFromMap(data, "campaign_id")));
        newValues.put("expiration", Integer.valueOf(JsonHelper.getSafeIntegerFromMap(data, "expiration")));
        newValues.put(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, Integer.valueOf(JsonHelper.getSafeIntegerFromMap(data, ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION)));
        newValues.put("ab_test", JsonHelper.getSafeStringFromMap(data, "ab"));
        newValues.put("rule_name", JsonHelper.getSafeStringFromMap(data, "rule_name"));
        newValues.put("listing_title", JsonHelper.getSafeStringFromMap(data, "listing_title"));
        newValues.put("listing_summary", JsonHelper.getSafeStringFromMap(data, "listing_summary"));
        newValues.put("sort_order", Integer.valueOf(JsonHelper.getSafeIntegerFromMap(data, "sort_order")));
        newValues.put("thumbnail_location", JsonHelper.getSafeStringFromMap(data, "thumbnail_location"));
        newValues.put("creative_location", JsonHelper.getSafeStringFromMap(data, "creative_location"));
        newValues.put("received_date", Long.valueOf(this.mLocalyticsDao.getCurrentTimeMillis()));
        if (config != null && (schemaVersion = JsonHelper.getSafeIntegerFromMap(config, "schema_version")) > 0) {
            newValues.put("schema_version", Integer.valueOf(schemaVersion));
        }
        if (!_validateInboxData(newValues)) {
            Localytics.Log.e(String.format("Inbox data is invalid:\n%s", newValues.toString()));
            return -1L;
        }
        long campaignId = newValues.getAsInteger("campaign_id").intValue();
        ContentValues oldValues = localInboxes.get(Integer.valueOf((int) campaignId));
        if (oldValues != null) {
            Localytics.Log.w(String.format("Existing inbox already exists for this campaign\n\t campaignID = %d", Long.valueOf(campaignId)));
            long oldVersion = oldValues.getAsLong(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION).longValue();
            long newVersion = newValues.getAsLong(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION).longValue();
            if (oldVersion >= newVersion) {
                Localytics.Log.w(String.format("No update needed. Campaign version has not been updated\n\t version: %d", Long.valueOf(oldVersion)));
                return 0L;
            }
            _removeInboxAssetFiles(oldValues.getAsInteger("_id").intValue());
            newValues.put("read", oldValues.getAsInteger("read"));
            newValues.put("received_date", oldValues.getAsLong("received_date"));
        }
        long rowId = this.mProvider.replace("inbox_campaigns", newValues);
        if (rowId == -1) {
            Localytics.Log.e(String.format("Failed to replace inbox campaign %d", Long.valueOf(campaignId)));
            return -1L;
        }
        _saveInboxCampaignAttributes(rowId, JsonHelper.getSafeMapFromMap(data, "attributes"));
        return rowId;
    }

    void _saveInboxCampaignAttributes(long rowId, Map<String, Object> attributes) {
        if (attributes != null) {
            try {
                for (String key : attributes.keySet()) {
                    ContentValues values = new ContentValues(attributes.size() + 1);
                    values.put("key", key);
                    values.put("value", attributes.get(key).toString());
                    values.put("inbox_id_ref", Integer.valueOf((int) rowId));
                    if (this.mProvider.insert("inbox_campaign_attributes", values) <= 0) {
                        Localytics.Log.e(String.format("Failed to insert attributes for inbox campaign row id %d", Long.valueOf(rowId)));
                    }
                }
            } catch (ClassCastException e) {
                Localytics.Log.e(String.format("Cannot parse inbox attributes data: %s", attributes.toString()));
            }
        }
    }

    boolean _validateInboxData(ContentValues values) {
        Long campaignId = values.getAsLong("campaign_id");
        Long expiration = values.getAsLong("expiration");
        Long version = values.getAsLong(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION);
        Long sortOrder = values.getAsLong("sort_order");
        String ruleName = values.getAsString("rule_name");
        String title = values.getAsString("listing_title");
        long now = this.mLocalyticsDao.getCurrentTimeMillis() / 1000;
        return (campaignId == null || campaignId.longValue() <= 0 || version == null || version.longValue() <= 0 || sortOrder == null || sortOrder.longValue() < 0 || ((expiration == null || expiration.longValue() <= now) && !Constants.isTestModeEnabled()) || TextUtils.isEmpty(ruleName) || TextUtils.isEmpty(title)) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<InboxCampaign> _getInboxCampaigns() {
        List<InboxCampaign> campaigns = new ArrayList<>();
        Cursor result = null;
        try {
            result = this.mProvider.query("inbox_campaigns", null, String.format("%s > ?", "expiration"), new String[]{Long.toString(this.mLocalyticsDao.getCurrentTimeMillis() / 1000)}, String.format("%s DESC", "sort_order"));
            while (result.moveToNext()) {
                long rowId = result.getLong(result.getColumnIndexOrThrow("_id"));
                InboxCampaign.Builder builder = new InboxCampaign.Builder().setCampaignId(result.getLong(result.getColumnIndexOrThrow("campaign_id"))).setInboxId(rowId).setRuleName(result.getString(result.getColumnIndexOrThrow("rule_name"))).setTitle(result.getString(result.getColumnIndexOrThrow("listing_title"))).setSummary(result.getString(result.getColumnIndexOrThrow("listing_summary"))).setSortOrder(result.getInt(result.getColumnIndexOrThrow("sort_order"))).setRead(result.getInt(result.getColumnIndexOrThrow("read")) > 0).setAbTest(result.getString(result.getColumnIndexOrThrow("ab_test"))).setVersion(result.getInt(result.getColumnIndexOrThrow(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION))).setReceivedDate(result.getLong(result.getColumnIndexOrThrow("received_date"))).setSchemaVersion(result.getInt(result.getColumnIndexOrThrow("schema_version"))).setAttributes(_getAttributes(rowId));
                String thumbnailLocation = result.getString(result.getColumnIndexOrThrow("thumbnail_location"));
                if (!TextUtils.isEmpty(thumbnailLocation)) {
                    String localThumbnailLocation = ThumbnailManager.getInboxLocalThumbnailLocation(rowId, this.mLocalyticsDao);
                    builder.setThumbnailUri(Uri.parse(thumbnailLocation));
                    builder.setLocalThumbnailUri(Uri.fromFile(new File(localThumbnailLocation)));
                }
                String creativeLocation = result.getString(result.getColumnIndexOrThrow("creative_location"));
                if (!TextUtils.isEmpty(creativeLocation)) {
                    builder.setCreativeUri(Uri.parse(creativeLocation));
                    builder.setLocalCreativeUri(Uri.parse("file://" + CreativeManager.getInboxLocalHtmlLocation(rowId, this.mLocalyticsDao)));
                    Map<String, String> webViewAttributes = new HashMap<>();
                    String localHtmlURL = "file://" + CreativeManager.getInboxLocalHtmlLocation(rowId, this.mLocalyticsDao);
                    String localFileLocation = CreativeManager.getInboxLocalFileURL(rowId, creativeLocation.endsWith(".zip"), this.mLocalyticsDao);
                    webViewAttributes.put("creative_url", creativeLocation);
                    webViewAttributes.put("html_url", localHtmlURL);
                    webViewAttributes.put("base_path", CreativeManager.getInboxUnzipFileDirPath(rowId, this.mLocalyticsDao));
                    webViewAttributes.put("zip_name", String.format("inbox_creative_assets_%d.zip", Long.valueOf(rowId)));
                    webViewAttributes.put("local_file_location", localFileLocation);
                    builder.setWebViewAttributes(webViewAttributes);
                }
                campaigns.add(builder.build());
            }
            return campaigns;
        } finally {
            if (result != null) {
                result.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int _getInboxCampaignsUnreadCount() {
        int i = 0;
        Cursor result = null;
        try {
            result = this.mProvider.mDb.rawQuery(String.format("SELECT COUNT(*) FROM %s WHERE %s > ? AND %s = 0", "inbox_campaigns", "expiration", "read"), new String[]{Long.toString(this.mLocalyticsDao.getCurrentTimeMillis() / 1000)});
            if (result.moveToFirst()) {
                i = result.getInt(0);
            } else if (result != null) {
                result.close();
            }
            return i;
        } finally {
            if (result != null) {
                result.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void _getInboxCampaignsAsync(final InboxRefreshListener listener) {
        final List<InboxCampaign> inboxCampaigns = _getInboxCampaigns();
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.localytics.android.InboxManager.3
            @Override // java.lang.Runnable
            public void run() {
                listener.localyticsRefreshedInboxCampaigns(inboxCampaigns);
            }
        });
    }

    Map<String, String> _getAttributes(long rowId) {
        Map<String, String> attributes = new HashMap<>();
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("inbox_campaign_attributes", null, String.format("%s = ?", "inbox_id_ref"), new String[]{Long.toString(rowId)}, null);
            while (cursor.moveToNext()) {
                attributes.put(cursor.getString(cursor.getColumnIndexOrThrow("key")), cursor.getString(cursor.getColumnIndexOrThrow("value")));
            }
            return attributes;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean _setInboxCampaignRead(long campaignId, boolean read) {
        ContentValues values = new ContentValues(1);
        values.put("read", Boolean.valueOf(read));
        return this.mProvider.update("inbox_campaigns", values, String.format("%s = ?", "campaign_id"), new String[]{Long.toString(campaignId)}) == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInboxDetailFragmentDisplaying(Object fragment, boolean displaying) {
        if ((Build.VERSION.SDK_INT >= 11 && (fragment instanceof InboxDetailFragment)) || (fragment instanceof InboxDetailSupportFragment)) {
            synchronized (this.mDisplayingInboxFragments) {
                if (displaying) {
                    this.mDisplayingInboxFragments.add(fragment);
                } else {
                    this.mDisplayingInboxFragments.remove(fragment);
                    if (this.mDisplayingInboxFragments.size() == 0) {
                        this.mMarketingHandler.post(new Runnable() { // from class: com.localytics.android.InboxManager.4
                            @Override // java.lang.Runnable
                            public void run() {
                                if (InboxManager.this.mManifestToProcess != null) {
                                    InboxManager.this.mMarketingHandler.localyticsDidDownloadManifest(InboxManager.this.mManifestToProcess.marketingMap, InboxManager.this.mManifestToProcess.config, InboxManager.this.mManifestToProcess.successful);
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void tagDismissForInboxDetailFragments() {
        synchronized (this.mDisplayingInboxFragments) {
            for (Object fragment : this.mDisplayingInboxFragments) {
                MarketingWebViewManager webViewManager = null;
                if (Build.VERSION.SDK_INT >= 11 && (fragment instanceof InboxDetailFragment)) {
                    webViewManager = ((InboxDetailFragment) fragment).getWebViewManager();
                } else if (fragment instanceof InboxDetailSupportFragment) {
                    webViewManager = ((InboxDetailSupportFragment) fragment).getWebViewManager();
                }
                if (webViewManager != null) {
                    webViewManager.tagMarketingActionEventWithAction("X");
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void _downloadInboxThumbnails(List<InboxCampaign> campaigns) {
        List<MarketingMessage> thumbnailsToDownload = new LinkedList<>();
        for (InboxCampaign campaign : campaigns) {
            if (campaign.hasThumbnail() && campaign.getLocalThumbnailUri() != null) {
                String localThumbnailLocation = campaign.getLocalThumbnailUri().getPath();
                if (!new File(localThumbnailLocation).exists()) {
                    MarketingMessage message = new MarketingMessage(campaign.getWebViewAttributes());
                    message.put("campaign_id", Long.valueOf(campaign.getCampaignId()));
                    message.put("download_url", campaign.getThumbnailUri().toString());
                    message.put("local_file_location", localThumbnailLocation);
                    thumbnailsToDownload.add(message);
                }
            }
        }
        if (thumbnailsToDownload.size() > 0) {
            this.mThumbnailManager.downloadThumbnails(thumbnailsToDownload);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void _priorityDownloadCreative(InboxCampaign campaign, CreativeManager.FirstDownloadedCallback firstDownloadedCallback) {
        MarketingMessage message = new MarketingMessage();
        message.put("_id", Long.valueOf(campaign.getInboxId()));
        message.put("campaign_id", Long.valueOf(campaign.getCampaignId()));
        String creativeLocation = campaign.getCreativeUri().toString();
        message.put("creative_location", creativeLocation);
        _updateMessageWithSpecialKeys(message);
        List<MarketingMessage> campaigns = new LinkedList<>();
        campaigns.add(message);
        this.mCreativeManager.priorityDownloadCreatives(campaigns, firstDownloadedCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ManifestHolder {
        Map<String, Object> config;
        Map<String, Object> marketingMap;
        boolean successful;

        public ManifestHolder(boolean successful, Map<String, Object> marketingMap, Map<String, Object> config) {
            this.successful = successful;
            this.marketingMap = marketingMap;
            this.config = config;
        }
    }
}
