package com.localytics.android;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.localytics.android.BaseUploadThread;
import com.localytics.android.Localytics;
import com.localytics.android.Region;
import com.nuance.connect.util.TimeConversion;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ManifestHandler extends BaseHandler implements AnalyticsListener, LocationListener {
    private static final int MESSAGE_LOCATION_UPDATE = 501;
    private AtomicBoolean mIsRefreshing;
    private final ListenersSet<ManifestListener> mListeners;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ManifestHandler(LocalyticsDao localyticsDao, Looper looper) {
        super(localyticsDao, looper);
        this.siloName = "Manifest";
        this.mListeners = new ListenersSet<>(ManifestListener.class);
        this.doesRetry = false;
        this.mIsRefreshing = new AtomicBoolean();
        queueMessage(obtainMessage(1));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addListener(ManifestListener listener) {
        this.mListeners.add(listener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void refreshManifest() {
        if (!this.mIsRefreshing.getAndSet(true)) {
            this.mListeners.getProxy().localyticsWillDownloadManifest();
            upload();
            Localytics.Log.e("Manifest upload called");
        }
    }

    @Override // com.localytics.android.BaseHandler
    protected void _init() {
        this.mProvider = new ManifestProvider(this.siloName.toLowerCase(), this.mLocalyticsDao);
        _initializeInfo();
    }

    protected void _initializeInfo() {
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("info", null, null, null, null);
            if (!cursor.moveToFirst()) {
                Localytics.Log.v("Performing first-time initialization for MarketingProvider info table");
                ContentValues values = new ContentValues();
                values.put("last_campaign_download", (Integer) 0);
                this.mProvider.insert("info", values);
            }
            this.mProvider.vacuumIfNecessary();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void locationUpdated() {
        queueMessage(obtainMessage(501));
    }

    protected void _locationUpdated() {
        Cursor query = null;
        try {
            query = this.mProvider.query("info", null, null, null, null);
            if (query.moveToFirst()) {
                long lastUpdate = query.getLong(query.getColumnIndexOrThrow("last_campaign_download"));
                if (this.mLocalyticsDao.getCurrentTimeMillis() - lastUpdate > TimeConversion.MILLIS_IN_DAY) {
                    refreshManifest();
                }
            }
        } finally {
            if (query != null) {
                query.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.localytics.android.BaseHandler
    public void handleMessageExtended(Message msg) throws Exception {
        switch (msg.what) {
            case 501:
                Localytics.Log.d("Marketing handler received MESSAGE_LOCATION_UPDATE");
                this.mProvider.runBatchTransaction(new Runnable() { // from class: com.localytics.android.ManifestHandler.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ManifestHandler.this._locationUpdated();
                    }
                });
                return;
            default:
                super.handleMessageExtended(msg);
                return;
        }
    }

    @Override // com.localytics.android.BaseHandler
    protected void _deleteUploadedData(int maxRowToDelete) {
    }

    @Override // com.localytics.android.BaseHandler
    protected void _onUploadCompleted(boolean successful, String responseBody) {
        try {
            if (!TextUtils.isEmpty(responseBody)) {
                Map<String, Object> marketingMap = JsonHelper.toMap(new JSONObject(responseBody));
                Map<String, Object> config = (Map) marketingMap.get("config");
                this.mListeners.getProxy().localyticsDidDownloadManifest(marketingMap, config, successful);
            } else {
                this.mListeners.getProxy().localyticsDidDownloadManifest(null, null, successful);
            }
            ContentValues values = new ContentValues();
            values.put("last_campaign_download", Long.valueOf(this.mLocalyticsDao.getCurrentTimeMillis()));
            this.mProvider.update("info", values, null, null);
        } catch (JSONException e) {
            Localytics.Log.w("JSONException", e);
        } finally {
            this.mIsRefreshing.set(false);
        }
        this.mProvider.vacuumIfNecessary();
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
        return new MarketingDownloader(BaseUploadThread.UploadType.MANIFEST, this, data, customerId, this.mLocalyticsDao);
    }

    @Override // com.localytics.android.AnalyticsListener
    public void localyticsSessionWillOpen(boolean isFirst, boolean isUpgrade, boolean isResume) {
        if (!isResume) {
            this.mListeners.getProxy().localyticsWillDownloadManifest();
            upload(this.mLocalyticsDao.getCustomerIdFuture());
        }
    }

    @Override // com.localytics.android.AnalyticsListener
    public void localyticsSessionDidOpen(boolean isFirst, boolean isUpgrade, boolean isResume) {
    }

    @Override // com.localytics.android.AnalyticsListener
    public void localyticsSessionWillClose() {
    }

    @Override // com.localytics.android.AnalyticsListener
    public void localyticsDidTagEvent(String eventName, Map<String, String> attributes, long customerValueIncrease) {
    }

    @Override // com.localytics.android.LocationListener
    public void localyticsDidUpdateLocation(Location location) {
        locationUpdated();
    }

    @Override // com.localytics.android.LocationListener
    public void localyticsDidTriggerRegions(List<Region> regions, Region.Event event) {
    }

    @Override // com.localytics.android.LocationListener
    public void localyticsDidUpdateMonitoredGeofences(List<CircularRegion> added, List<CircularRegion> removed) {
    }
}
