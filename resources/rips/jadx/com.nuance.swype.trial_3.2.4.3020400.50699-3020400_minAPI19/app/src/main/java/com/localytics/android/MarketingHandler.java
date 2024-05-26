package com.localytics.android;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.localytics.android.BaseUploadThread;
import com.localytics.android.CreativeManager;
import com.localytics.android.InAppManager;
import com.localytics.android.Localytics;
import com.localytics.android.Region;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class MarketingHandler extends BaseHandler implements AnalyticsListener, LocationListener, ManifestListener {
    private static final int MESSAGE_DOWNLOAD_INBOX_THUMBNAILS = 209;
    private static final int MESSAGE_GET_INBOX_CAMPAIGNS_ASYNC = 211;
    private static final int MESSAGE_HANDLE_PUSH_RECEIVED = 203;
    private static final int MESSAGE_IN_APP_MESSAGE_TRIGGER = 201;
    private static final int MESSAGE_MANIFEST_DOWNLOADED = 207;
    private static final int MESSAGE_PRIORITY_DOWNLOAD_CREATIVE = 210;
    private static final int MESSAGE_SET_INBOX_CAMPAIGN_READ = 208;
    private static final int MESSAGE_SET_IN_APP_MESSAGE_AS_NOT_DISPLAYED = 204;
    private static final int MESSAGE_SHOW_IN_APP_MESSAGES_TEST = 202;
    private static final int MESSAGE_TAG_PUSH_RECEIVED_EVENT = 205;
    private static final int MESSAGE_TRIGGER_REGIONS = 212;
    private static final int MESSAGE_WILL_DOWNLOAD_MANIFEST = 206;
    private int lastMarketingMessagesHash;
    private boolean mHasRunSessionStartRunnable;
    protected InAppManager mInAppManager;
    protected InboxManager mInboxManager;
    protected final ListenersSet<MessagingListener> mListeners;
    InAppManager.ManifestHolder mManifestToProcess;
    protected PlacesManager mPlacesManager;
    protected PushManager mPushManager;
    private Runnable mSessionStartRunnable;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MarketingHandler(LocalyticsDao localyticsDao, Looper looper) {
        super(localyticsDao, looper);
        this.lastMarketingMessagesHash = -1;
        this.mSessionStartRunnable = null;
        this.siloName = "In-app";
        this.mListeners = new ListenersSet<>(MessagingListener.class);
        this.doesRetry = false;
        this.mInAppManager = new InAppManager(localyticsDao, this);
        this.mPushManager = new PushManager(localyticsDao, this);
        this.mInboxManager = new InboxManager(localyticsDao, this);
        this.mPlacesManager = new PlacesManager(localyticsDao, this);
        _createLocalyticsDirectory(localyticsDao.getAppContext());
        queueMessage(obtainMessage(1));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDeveloperListener(MessagingListener listener) {
        this.mListeners.setDevListener(listener);
    }

    void addListener(MessagingListener listener) {
        this.mListeners.add(listener);
    }

    @TargetApi(21)
    private boolean _createLocalyticsDirectory(Context context) {
        StringBuilder builder = new StringBuilder();
        if (this.mLocalyticsDao.isUsingNewCreativeLocation()) {
            builder.append(context.getNoBackupFilesDir().getAbsolutePath());
        } else {
            builder.append(context.getFilesDir().getAbsolutePath());
        }
        builder.append(File.separator);
        builder.append(".localytics");
        File dir = new File(builder.toString());
        return dir.mkdirs() || dir.isDirectory();
    }

    @Override // com.localytics.android.BaseHandler
    protected void _init() {
        this.mProvider = new MarketingProvider(this.siloName.toLowerCase(), this.mLocalyticsDao);
        _setProviderForManagers();
    }

    protected void _setProviderForManagers() {
        this.mInAppManager.setProvider(this.mProvider);
        this.mInboxManager.setProvider(this.mProvider);
        this.mPushManager.setProvider(this.mProvider);
        this.mPlacesManager.setProvider(this.mProvider);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFragmentManager(FragmentManager fragmentManager) {
        this.mInAppManager.setFragmentManager(fragmentManager);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dismissCurrentInAppMessage() {
        if (Build.VERSION.SDK_INT >= 11) {
            this.mInAppManager.dismissCurrentInAppMessage();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handlePushNotificationOpened(Intent intent) {
        this.mPushManager.handlePushNotificationOpened(intent);
        this.mPlacesManager.handlePushNotificationOpened(intent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean setInAppAsDisplayed(final int campaignId) {
        return getBool(new Callable<Boolean>() { // from class: com.localytics.android.MarketingHandler.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public Boolean call() throws Exception {
                return Boolean.valueOf(MarketingHandler.this.mInAppManager._setInAppMessageAsDisplayed(campaignId));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInAppMessageAsNotDisplayed(int campaignId) {
        queueMessage(obtainMessage(204, Integer.valueOf(campaignId)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void displayInAppMessage(String eventName, Map<String, String> attributes) {
        if (Build.VERSION.SDK_INT >= 11) {
            if ("open".equals(eventName)) {
                queueMessage(obtainMessage(201, new Object[]{eventName, null}));
            } else {
                queueMessage(obtainMessage(201, new Object[]{eventName, attributes}));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleNotificationReceived(Bundle data) {
        queueMessage(obtainMessage(203, data));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void tagPushReceivedEvent(Bundle data) {
        queueMessage(obtainMessage(205, data));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleTestModeIntent(Intent intent) {
        if (intent != null) {
            try {
                Uri uri = intent.getData();
                if (uri != null && uri.getScheme() != null && uri.getScheme().equals("amp" + this.mLocalyticsDao.getAppKey())) {
                    String path = uri.getPath().substring(1);
                    String host = uri.getHost();
                    String[] components = path.split("[/]");
                    if (components.length != 0 && !TextUtils.isEmpty(host) && host.equalsIgnoreCase("testMode")) {
                        if (components[0].equalsIgnoreCase("enabled")) {
                            this.mLocalyticsDao.setTestModeEnabled(true);
                        } else if (components[0].equalsIgnoreCase("launch") && components[1].equalsIgnoreCase("push")) {
                            this.mPushManager.handlePushTestMode(components);
                        }
                    }
                }
            } catch (Exception e) {
                Localytics.Log.e("Exception while handling test mode", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Localytics.InAppMessageDismissButtonLocation getInAppDismissButtonLocation() {
        if (Build.VERSION.SDK_INT >= 11) {
            return InAppDialogFragment.getInAppDismissButtonLocation();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInAppDismissButtonLocation(Localytics.InAppMessageDismissButtonLocation buttonLocation) {
        if (Build.VERSION.SDK_INT >= 11) {
            InAppDialogFragment.setInAppDismissButtonLocation(buttonLocation);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void showMarketingTest() {
        if (Build.VERSION.SDK_INT >= 11) {
            queueMessage(obtainMessage(202, this.mLocalyticsDao.getCustomerIdFuture()));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDismissButtonImage(Bitmap image) {
        if (Build.VERSION.SDK_INT >= 11) {
            InAppDialogFragment.setDismissButtonImage(image);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<InboxCampaign> getInboxCampaigns() {
        return (List) getType(new Callable<List<InboxCampaign>>() { // from class: com.localytics.android.MarketingHandler.2
            @Override // java.util.concurrent.Callable
            public List<InboxCampaign> call() throws Exception {
                return MarketingHandler.this.mInboxManager._getInboxCampaigns();
            }
        }, new ArrayList());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getInboxCampaignsUnreadCount() {
        return getInt(new Callable<Integer>() { // from class: com.localytics.android.MarketingHandler.3
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public Integer call() throws Exception {
                return Integer.valueOf(MarketingHandler.this.mInboxManager._getInboxCampaignsUnreadCount());
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getInboxCampaignsAsync(InboxRefreshListener callback) {
        queueMessage(obtainMessage(211, callback));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canRefreshInbox() {
        return this.mInboxManager.canRefresh();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInboxRefreshCallback(InboxRefreshListener callback) {
        this.mInboxManager.setInboxRefreshCallback(callback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInboxCampaignRead(long campaignId, boolean read) {
        queueMessage(obtainMessage(208, new Object[]{Long.valueOf(campaignId), Boolean.valueOf(read)}));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInboxDetailFragmentDisplaying(Object fragment, boolean displaying) {
        this.mInboxManager.setInboxDetailFragmentDisplaying(fragment, displaying);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void downloadInboxThumbnails(List<InboxCampaign> campaigns) {
        queueMessage(obtainMessage(209, campaigns));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void priorityDownloadCreative(InboxCampaign campaign, CreativeManager.FirstDownloadedCallback firstDownloadedCallback) {
        queueMessage(obtainMessage(210, new Object[]{campaign, firstDownloadedCallback}));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _processPendingManifest() {
        if (this.mManifestToProcess != null) {
            _onUploadCompleted(this.mManifestToProcess.successful, this.mManifestToProcess.manifest);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void tagDismissForInboxDetailFragments() {
        this.mInboxManager.tagDismissForInboxDetailFragments();
    }

    @Override // com.localytics.android.BaseHandler
    protected int _getMaxRowToUpload() {
        return 1;
    }

    @Override // com.localytics.android.BaseHandler
    protected TreeMap<Integer, Object> _getDataToUpload() {
        TreeMap<Integer, Object> tree = new TreeMap<>();
        tree.put(0, "");
        return tree;
    }

    @Override // com.localytics.android.BaseHandler
    protected BaseUploadThread _getUploadThread(TreeMap<Integer, Object> data, String customerId) {
        if (Build.VERSION.SDK_INT >= 11) {
            return new MarketingDownloader(BaseUploadThread.UploadType.MARKETING, this, data, customerId, this.mLocalyticsDao);
        }
        return null;
    }

    @Override // com.localytics.android.BaseHandler
    protected void _deleteUploadedData(int maxRowToDelete) {
    }

    @Override // com.localytics.android.BaseHandler
    protected void _onUploadCompleted(boolean successful, String responseBody) {
        if (this.mInAppManager._manifestProcessingAllowed()) {
            this.mManifestToProcess = null;
            if (successful) {
                try {
                    if (!TextUtils.isEmpty(responseBody)) {
                        int responseBodyHash = responseBody.hashCode();
                        if (responseBodyHash != this.lastMarketingMessagesHash) {
                            Map<String, Object> marketingMap = JsonHelper.toMap(new JSONObject(responseBody));
                            Map<String, Object> config = (Map) marketingMap.get("config");
                            this.mInAppManager._processMarketingObject(marketingMap, config);
                            this.lastMarketingMessagesHash = responseBodyHash;
                        }
                    } else {
                        this.mInAppManager._processMarketingObject(new HashMap(), null);
                        this.lastMarketingMessagesHash = -1;
                    }
                } catch (JSONException e) {
                    Localytics.Log.w("JSONException", e);
                }
            }
            if (!this.mInAppManager._handleTestCampaigns() && !this.mHasRunSessionStartRunnable && this.mSessionStartRunnable != null) {
                removeCallbacks(this.mSessionStartRunnable);
                this.mSessionStartRunnable.run();
                return;
            }
            return;
        }
        this.mManifestToProcess = new InAppManager.ManifestHolder(successful, responseBody);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.localytics.android.BaseHandler
    public void handleMessageExtended(Message msg) throws Exception {
        switch (msg.what) {
            case 201:
                Localytics.Log.d("In-app handler received MESSAGE_INAPP_TRIGGER");
                Object[] params = (Object[]) msg.obj;
                final String event = (String) params[0];
                final Map<String, String> attributes = (Map) params[1];
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.MarketingHandler.4
                    @Override // java.lang.Runnable
                    public void run() {
                        MarketingHandler.this.mInAppManager._inAppMessageTrigger(event, attributes);
                    }
                });
                return;
            case 202:
                Localytics.Log.d("In-app handler received MESSAGE_SHOW_INAPP_TEST");
                _upload(true, (String) ((Future) msg.obj).get());
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.localytics.android.MarketingHandler.5
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            MarketingHandler.this.mInAppManager.showInAppTest();
                        } catch (Exception e) {
                            Localytics.Log.e("Exception handling MESSAGE_SHOW_IN_APP_MESSAGES_TEST", e);
                        }
                    }
                }, 1000L);
                return;
            case 203:
                Localytics.Log.d("In-app handler received MESSAGE_HANDLE_PUSH_RECEIVED");
                final Bundle data = (Bundle) msg.obj;
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.MarketingHandler.6
                    @Override // java.lang.Runnable
                    public void run() {
                        MarketingHandler.this.mPushManager._handlePushReceived(data);
                    }
                });
                return;
            case 204:
                Localytics.Log.d("Marketing handler received MESSAGE_SET_IN_APP_MESSAGE_AS_NOT_DISPLAYED");
                final int campaignId = ((Integer) msg.obj).intValue();
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.MarketingHandler.7
                    @Override // java.lang.Runnable
                    public void run() {
                        MarketingHandler.this.mInAppManager._setInAppMessageAsNotDisplayed(campaignId);
                    }
                });
                return;
            case 205:
                Localytics.Log.d("Marketing handler received MESSAGE_TAG_PUSH_RECEIVED_EVENT");
                final Bundle data2 = (Bundle) msg.obj;
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.MarketingHandler.8
                    @Override // java.lang.Runnable
                    public void run() {
                        MarketingHandler.this.mPushManager._tagPushReceivedEvent(data2);
                    }
                });
                return;
            case 206:
                Localytics.Log.d("Marketing handler received MESSAGE_WILL_DOWNLOAD_MANIFEST");
                final long currentTimeInMillis = ((Long) msg.obj).longValue();
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.MarketingHandler.9
                    @Override // java.lang.Runnable
                    public void run() {
                        MarketingHandler.this.mInboxManager.setLastRefreshTimeMillis(currentTimeInMillis);
                    }
                });
                return;
            case 207:
                Localytics.Log.d("Marketing handler received MESSAGE_MANIFEST_DOWNLOADED");
                Object[] objects = (Object[]) msg.obj;
                final Map<String, Object> marketingMap = (Map) objects[0];
                final Map<String, Object> config = (Map) objects[1];
                final Boolean successful = (Boolean) objects[2];
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.MarketingHandler.10
                    @Override // java.lang.Runnable
                    public void run() {
                        MarketingHandler.this.mInboxManager._processMarketingObject(successful.booleanValue(), marketingMap, config);
                    }
                });
                this.mProvider.runBatchTransaction(new Runnable() { // from class: com.localytics.android.MarketingHandler.11
                    @Override // java.lang.Runnable
                    public void run() {
                        MarketingHandler.this.mPlacesManager._processMarketingObject(successful.booleanValue(), marketingMap, config);
                    }
                });
                return;
            case 208:
                Localytics.Log.d("Marketing handler received MESSAGE_SET_INBOX_CAMPAIGN_READ");
                Object[] params2 = (Object[]) msg.obj;
                final long campaignId2 = ((Long) params2[0]).longValue();
                final boolean read = ((Boolean) params2[1]).booleanValue();
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.MarketingHandler.12
                    @Override // java.lang.Runnable
                    public void run() {
                        MarketingHandler.this.mInboxManager._setInboxCampaignRead(campaignId2, read);
                    }
                });
                return;
            case 209:
                Localytics.Log.d("Marketing handler received MESSAGE_DOWNLOAD_INBOX_THUMBNAILS");
                final List<InboxCampaign> campaigns = (List) msg.obj;
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.MarketingHandler.13
                    @Override // java.lang.Runnable
                    public void run() {
                        MarketingHandler.this.mInboxManager._downloadInboxThumbnails(campaigns);
                    }
                });
                return;
            case 210:
                Localytics.Log.d("Marketing handler received MESSAGE_PRIORITY_DOWNLOAD_CREATIVE");
                Object[] params3 = (Object[]) msg.obj;
                final InboxCampaign campaign = (InboxCampaign) params3[0];
                final CreativeManager.FirstDownloadedCallback callback = (CreativeManager.FirstDownloadedCallback) params3[1];
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.MarketingHandler.14
                    @Override // java.lang.Runnable
                    public void run() {
                        MarketingHandler.this.mInboxManager._priorityDownloadCreative(campaign, callback);
                    }
                });
                return;
            case 211:
                Localytics.Log.d("Marketing handler received MESSAGE_GET_INBOX_CAMPAIGNS_ASYNC");
                final InboxRefreshListener listener = (InboxRefreshListener) msg.obj;
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.MarketingHandler.15
                    @Override // java.lang.Runnable
                    public void run() {
                        MarketingHandler.this.mInboxManager._getInboxCampaignsAsync(listener);
                    }
                });
                return;
            case 212:
                Localytics.Log.d("Marketing handler received MESSAGE_TRIGGER_REGIONS");
                Object[] params4 = (Object[]) msg.obj;
                final List<Region> regions = (List) params4[0];
                final Region.Event event2 = (Region.Event) params4[1];
                this.mProvider.runBatchTransaction(new Runnable() { // from class: com.localytics.android.MarketingHandler.16
                    @Override // java.lang.Runnable
                    public void run() {
                        MarketingHandler.this.mPlacesManager._triggerRegions(regions, event2);
                    }
                });
                return;
            default:
                super.handleMessageExtended(msg);
                return;
        }
    }

    @Override // com.localytics.android.AnalyticsListener
    public void localyticsSessionWillOpen(boolean isFirstEverSession, boolean isFirstSessionSinceUpgrade, boolean willResumeOldSession) {
    }

    @Override // com.localytics.android.AnalyticsListener
    public void localyticsSessionDidOpen(final boolean isFirstEverSession, final boolean isFirstSessionSinceUpgrade, final boolean didResumeOldSession) {
        if (!didResumeOldSession) {
            upload();
        }
        if (!Constants.isTestModeEnabled()) {
            this.mHasRunSessionStartRunnable = false;
            this.mSessionStartRunnable = new Runnable() { // from class: com.localytics.android.MarketingHandler.17
                @Override // java.lang.Runnable
                public void run() {
                    if (Build.VERSION.SDK_INT >= 11) {
                        if (isFirstEverSession) {
                            MarketingHandler.this.mInAppManager._inAppMessageTrigger("AMP First Run", null);
                        }
                        if (isFirstSessionSinceUpgrade) {
                            MarketingHandler.this.mInAppManager._inAppMessageTrigger("AMP upgrade", null);
                        }
                        if (!didResumeOldSession) {
                            MarketingHandler.this.mInAppManager._inAppMessageTrigger("open", null);
                        }
                    }
                    MarketingHandler.this.mHasRunSessionStartRunnable = true;
                }
            };
            postDelayed(this.mSessionStartRunnable, 5000L);
        }
    }

    @Override // com.localytics.android.AnalyticsListener
    public void localyticsSessionWillClose() {
    }

    @Override // com.localytics.android.AnalyticsListener
    public void localyticsDidTagEvent(String eventName, Map<String, String> attributes, long clv) {
        displayInAppMessage(eventName, attributes);
    }

    @Override // com.localytics.android.ManifestListener
    public void localyticsWillDownloadManifest() {
        queueMessage(obtainMessage(206, Long.valueOf(this.mLocalyticsDao.getCurrentTimeMillis())));
    }

    @Override // com.localytics.android.ManifestListener
    public void localyticsDidDownloadManifest(Map<String, Object> marketingMap, Map<String, Object> config, boolean successful) {
        queueMessage(obtainMessage(207, new Object[]{marketingMap, config, Boolean.valueOf(successful)}));
    }

    @Override // com.localytics.android.LocationListener
    public void localyticsDidUpdateLocation(Location location) {
    }

    @Override // com.localytics.android.LocationListener
    public void localyticsDidTriggerRegions(List<Region> regions, Region.Event event) {
        queueMessage(obtainMessage(212, new Object[]{regions, event}));
    }

    @Override // com.localytics.android.LocationListener
    public void localyticsDidUpdateMonitoredGeofences(List<CircularRegion> added, List<CircularRegion> removed) {
    }
}
