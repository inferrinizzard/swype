package com.localytics.android;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import com.localytics.android.BaseProvider;
import com.localytics.android.CircularRegion;
import com.localytics.android.Localytics;
import com.localytics.android.Region;
import com.nuance.connect.comm.MessageAPI;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class LocationHandler extends BaseHandler implements LocationChangedListener, ManifestListener {
    private static final String LOCATION_SQUARED_DELTA_FORMAT = "((%s - %s) * (%s - %s) + (%s - %s) * (%s - %s) * %s) LIMIT %s";
    private static final int MESSAGE_LOCATION_CHANGED = 402;
    private static final int MESSAGE_MANIFEST_CONFIG = 404;
    private static final int MESSAGE_SET_LOCATION_MONITORING_ENABLED = 401;
    private static final int MESSAGE_STOPPED_MONITORING_ALL_GEOFENCES = 403;
    private static final int MESSAGE_TRIGGER_REGION = 405;
    private static final int MESSAGE_TRIGGER_REGIONS = 406;
    private final ListenersSet<LocationListener> mListeners;
    private LocationManager mLocationManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LocationHandler(LocalyticsDao localyticsDao, Looper looper) {
        this(localyticsDao, looper, null);
    }

    LocationHandler(LocalyticsDao localyticsDao, Looper looper, LocationManager locationManager) {
        super(localyticsDao, looper);
        this.siloName = "Location";
        this.mListeners = new ListenersSet<>(LocationListener.class);
        if (locationManager != null) {
            this.mLocationManager = locationManager;
            this.mLocationManager.initialize(localyticsDao, this);
        }
        queueMessage(obtainMessage(1));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDeveloperListener(LocationListener listener) {
        this.mListeners.setDevListener(listener);
    }

    private LocationManager _getLocationManager() {
        if (this.mLocationManager == null && PlayServicesUtils.isLocationAvailable()) {
            this.mLocationManager = new LocationManager();
            this.mLocationManager.initialize(this.mLocalyticsDao, this);
        }
        return this.mLocationManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addListener(LocationListener listener) {
        this.mListeners.add(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.localytics.android.BaseHandler
    public void handleMessageExtended(Message msg) throws Exception {
        switch (msg.what) {
            case 401:
                Localytics.Log.d("Location handler received MESSAGE_SET_LOCATION_MONITORING_ENABLED");
                final boolean enabled = ((Boolean) msg.obj).booleanValue();
                this.mProvider.runBatchTransaction(new Runnable() { // from class: com.localytics.android.LocationHandler.1
                    @Override // java.lang.Runnable
                    public void run() {
                        LocationHandler.this._setLocationMonitoringEnabled(enabled);
                    }
                });
                return;
            case 402:
                Localytics.Log.d("Location handler received MESSAGE_LOCATION_CHANGED");
                final Location location = (Location) msg.obj;
                this.mProvider.runBatchTransaction(new Runnable() { // from class: com.localytics.android.LocationHandler.2
                    @Override // java.lang.Runnable
                    public void run() {
                        LocationHandler.this._locationChanged(location);
                    }
                });
                return;
            case 403:
                Localytics.Log.d("Location handler received MESSAGE_STOPPED_MONITORING_ALL_GEOFENCES");
                this.mProvider.runBatchTransaction(new Runnable() { // from class: com.localytics.android.LocationHandler.3
                    @Override // java.lang.Runnable
                    public void run() {
                        LocationHandler.this._stoppedMonitoringAllGeofences();
                    }
                });
                return;
            case 404:
                Localytics.Log.d("Location handler received MESSAGE_MANIFEST_CONFIG");
                final Map<String, Object> config = (Map) msg.obj;
                this.mProvider.runBatchTransaction(new Runnable() { // from class: com.localytics.android.LocationHandler.4
                    @Override // java.lang.Runnable
                    public void run() {
                        LocationHandler.this._handleManifestConfig(config);
                    }
                });
                return;
            case 405:
                Localytics.Log.d("Location handler received MESSAGE_TRIGGER_REGION");
                Object[] params = (Object[]) msg.obj;
                final Region.Event event = (Region.Event) params[0];
                final Region region = (Region) params[1];
                this.mProvider.runBatchTransaction(new Runnable() { // from class: com.localytics.android.LocationHandler.5
                    @Override // java.lang.Runnable
                    public void run() {
                        LocationHandler.this._triggerRegion(region, event);
                    }
                });
                return;
            case 406:
                Localytics.Log.d("Location handler received MESSAGE_TRIGGER_REGIONS");
                Object[] params2 = (Object[]) msg.obj;
                final Region.Event event2 = (Region.Event) params2[0];
                final List<Region> regions = (List) params2[1];
                this.mProvider.runBatchTransaction(new Runnable() { // from class: com.localytics.android.LocationHandler.6
                    @Override // java.lang.Runnable
                    public void run() {
                        LocationHandler.this._triggerRegions(regions, event2);
                    }
                });
                return;
            default:
                super.handleMessageExtended(msg);
                return;
        }
    }

    @Override // com.localytics.android.BaseHandler
    void _init() {
        this.mProvider = new LocationProvider(this.siloName.toLowerCase(), this.mLocalyticsDao);
        _initializeInfo();
    }

    protected void _initializeInfo() {
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("info", null, null, null, null);
            if (!cursor.moveToFirst()) {
                Localytics.Log.v("Performing first-time initialization for LocationProvider info table");
                ContentValues values = new ContentValues();
                values.put("places_data_last_modified", (Integer) 0);
                values.put("location_monitoring_enabled", (Integer) 0);
                this.mProvider.insert("info", values);
            }
            this.mProvider.vacuumIfNecessary();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override // com.localytics.android.BaseHandler
    protected void _deleteUploadedData(int maxRowToDelete) {
    }

    @Override // com.localytics.android.BaseHandler
    protected void _onUploadCompleted(boolean successful, String responseBody) {
    }

    @Override // com.localytics.android.BaseHandler
    protected int _getMaxRowToUpload() {
        return 0;
    }

    @Override // com.localytics.android.BaseHandler
    protected TreeMap<Integer, Object> _getDataToUpload() {
        return new TreeMap<>();
    }

    @Override // com.localytics.android.BaseHandler
    protected BaseUploadThread _getUploadThread(TreeMap<Integer, Object> data, String customerId) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isLocationMonitoringEnabled() {
        return getBool(new Callable<Boolean>() { // from class: com.localytics.android.LocationHandler.7
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public Boolean call() throws Exception {
                Cursor query = LocationHandler.this.mProvider.query("info", new String[]{"location_monitoring_enabled"}, null, null, null);
                return Boolean.valueOf(query.moveToFirst() && query.getInt(query.getColumnIndexOrThrow("location_monitoring_enabled")) != 0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLocationMonitoringEnabled(boolean enabled) {
        queueMessage(obtainMessage(401, Boolean.valueOf(enabled)));
    }

    void _setLocationMonitoringEnabled(boolean enabled) {
        LocationManager locationManager = _getLocationManager();
        if (locationManager != null) {
            locationManager.setMonitoringEnabled(enabled);
            ContentValues values = new ContentValues();
            values.put("location_monitoring_enabled", Integer.valueOf(enabled ? 1 : 0));
            this.mProvider.update("info", values, null, null);
        }
    }

    void _locationChanged(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            List<CircularRegion> previousGeofences = _getMonitoredGeofences();
            final List<CircularRegion> geofencesToMonitor = _getGeofencesToMonitor(latitude, longitude);
            final List<CircularRegion> geofencesToRemove = new LinkedList<>(previousGeofences);
            geofencesToRemove.removeAll(geofencesToMonitor);
            geofencesToMonitor.removeAll(previousGeofences);
            LocationManager locationManager = _getLocationManager();
            if (locationManager != null) {
                locationManager.removeGeofences(geofencesToRemove);
                _stoppedMonitoringGeofences(geofencesToRemove);
                locationManager.addGeofences(geofencesToMonitor);
                _startedMonitoringGeofences(geofencesToMonitor);
            }
            if (geofencesToMonitor.size() > 0 || geofencesToRemove.size() > 0) {
                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.localytics.android.LocationHandler.8
                    @Override // java.lang.Runnable
                    public void run() {
                        ((LocationListener) LocationHandler.this.mListeners.getProxy()).localyticsDidUpdateMonitoredGeofences(geofencesToMonitor, geofencesToRemove);
                    }
                });
            }
        }
    }

    void _startedMonitoringGeofences(List<CircularRegion> regions) {
        if (regions.size() > 0) {
            String inClause = BaseProvider.buildSqlInClause(regions, new BaseProvider.InClauseBuilder<CircularRegion>() { // from class: com.localytics.android.LocationHandler.9
                @Override // com.localytics.android.BaseProvider.InClauseBuilder
                public String getValue(CircularRegion object) {
                    return String.format("'%s'", object.getUniqueId());
                }
            });
            _updateGeofencesIsMonitored(true, inClause);
        }
    }

    void _stoppedMonitoringGeofences(List<CircularRegion> geofences) {
        if (geofences.size() > 0) {
            String inClause = BaseProvider.buildSqlInClause(geofences, new BaseProvider.InClauseBuilder<CircularRegion>() { // from class: com.localytics.android.LocationHandler.10
                @Override // com.localytics.android.BaseProvider.InClauseBuilder
                public String getValue(CircularRegion object) {
                    return String.format("'%s'", object.getUniqueId());
                }
            });
            _updateGeofencesIsMonitored(false, inClause);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stoppedMonitoringAllGeofences() {
        queueMessage(obtainMessage(403));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _stoppedMonitoringAllGeofences() {
        List<CircularRegion> monitoredGeofences = _getMonitoredGeofences();
        _stoppedMonitoringGeofences(monitoredGeofences);
    }

    private void _updateGeofencesIsMonitored(boolean isMonitored, String inClause) {
        ContentValues values = new ContentValues();
        values.put("is_monitored", Integer.valueOf(isMonitored ? 1 : 0));
        if (this.mProvider.update("geofences", values, String.format("%s IN %s", "identifier", inClause), null) == 0) {
            Localytics.Log.w("Failed to update geofences is_monitored to '" + isMonitored + "' for IDs " + inClause);
        }
    }

    List<CircularRegion> _getMonitoredGeofences() {
        List<CircularRegion> monitoredGeofences = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("geofences", null, String.format("%s = ?", "is_monitored"), new String[]{"1"}, null);
            while (cursor.moveToNext()) {
                CircularRegion region = createCircularRegion(cursor);
                monitoredGeofences.add(region);
            }
            return monitoredGeofences;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<CircularRegion> getGeofencesToMonitor(final double latitude, final double longitude) {
        return (List) getType(new Callable<List<CircularRegion>>() { // from class: com.localytics.android.LocationHandler.11
            @Override // java.util.concurrent.Callable
            public List<CircularRegion> call() throws Exception {
                return LocationHandler.this._getGeofencesToMonitor(latitude, longitude);
            }
        }, new LinkedList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<CircularRegion> _getGeofencesToMonitor(double latitude, double longitude) {
        List<CircularRegion> geofences = new LinkedList<>();
        Cursor cursor = null;
        try {
            double longitudeFudge = Math.pow(Math.cos(Math.toRadians(latitude)), 2.0d);
            String sortOrder = String.format(LOCATION_SQUARED_DELTA_FORMAT, Double.valueOf(latitude), "latitude", Double.valueOf(latitude), "latitude", Double.valueOf(longitude), "longitude", Double.valueOf(longitude), "longitude", Double.valueOf(longitudeFudge), Integer.valueOf(Constants.GEOFENCES_MONITORING_LIMIT));
            cursor = this.mProvider.query("geofences", null, null, null, sortOrder);
            while (cursor.moveToNext()) {
                CircularRegion region = createCircularRegion(cursor);
                geofences.add(region);
            }
            return geofences;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private CircularRegion createCircularRegion(Cursor cursor) {
        String identifier = cursor.getString(cursor.getColumnIndexOrThrow("identifier"));
        return new CircularRegion.Builder().setPlaceId(cursor.getLong(cursor.getColumnIndexOrThrow("place_id"))).setUniqueId(identifier).setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"))).setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"))).setRadius(cursor.getInt(cursor.getColumnIndexOrThrow("radius"))).setName(cursor.getString(cursor.getColumnIndexOrThrow("name"))).setAttributes(_geofenceAttributes(identifier)).build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _handleManifestConfig(Map<String, Object> config) {
        if (config != null) {
            long placesLastModified = JsonHelper.getSafeLongFromMap(config, "places_data_last_modified");
            String placesDataUrl = JsonHelper.getSafeStringFromMap(config, "places_data_url");
            int schemaVersion = 1;
            if (config.containsKey("schema_version")) {
                schemaVersion = JsonHelper.getSafeIntegerFromMap(config, "schema_version");
            }
            Cursor query = this.mProvider.query("info", new String[]{"places_data_last_modified"}, null, null, null);
            try {
                if (query.moveToNext()) {
                    long storedPlacesLastModified = query.getLong(query.getColumnIndexOrThrow("places_data_last_modified"));
                    if (!TextUtils.isEmpty(placesDataUrl) && (Constants.isTestModeEnabled() || storedPlacesLastModified == 0 || placesLastModified > storedPlacesLastModified)) {
                        try {
                            String result = MarketingDownloader.request(placesDataUrl);
                            JSONObject placesData = new JSONObject();
                            if (!TextUtils.isEmpty(result)) {
                                placesData = new JSONObject(result);
                            }
                            _handlePlacesData(placesData, placesLastModified, schemaVersion);
                        } catch (IOException e) {
                            Localytics.Log.e("IOException while downloading places data", e);
                        } catch (JSONException e2) {
                            Localytics.Log.e("JSONException while downloading places data", e2);
                        }
                    }
                }
            } finally {
                query.close();
            }
        }
    }

    void _handlePlacesData(JSONObject placesData, long lastModified, int schemaVersion) throws JSONException {
        _saveGeofences(placesData.optJSONArray("geofences"), schemaVersion);
        LocationManager locationManager = _getLocationManager();
        if (locationManager != null) {
            locationManager.updateListenersWithLastLocation();
        }
        if (!Constants.isTestModeEnabled()) {
            ContentValues values = new ContentValues();
            values.put("places_data_last_modified", Long.valueOf(lastModified));
            this.mProvider.update("info", values, null, null);
        }
        this.mProvider.vacuumIfNecessary();
    }

    private void _saveGeofences(JSONArray geofences, int schemaVersion) throws JSONException {
        _removeDeactivatedGeofences(geofences);
        if (geofences != null) {
            LongSparseArray<ContentValues> localGeofences = _queryGeofencesInfo();
            for (int i = 0; i < geofences.length(); i++) {
                _saveGeofence(geofences.getJSONObject(i), localGeofences, schemaVersion);
            }
        }
    }

    private void _removeDeactivatedGeofences(JSONArray geofences) throws JSONException {
        final List<CircularRegion> removedGeofences = new ArrayList<>();
        List<CircularRegion> monitoredGeofences = _getMonitoredGeofences();
        if (geofences != null && geofences.length() > 0) {
            HashSet<String> downloadedGeofenceIdentifiers = new HashSet<>();
            for (int i = 0; i < geofences.length(); i++) {
                JSONObject fence = geofences.getJSONObject(i);
                downloadedGeofenceIdentifiers.add(fence.getString("identifier"));
            }
            ArrayList<CircularRegion> stopMonitoring = new ArrayList<>();
            for (CircularRegion monitoredFence : monitoredGeofences) {
                if (!downloadedGeofenceIdentifiers.contains(monitoredFence.getUniqueId())) {
                    stopMonitoring.add(monitoredFence);
                }
            }
            removedGeofences.addAll(stopMonitoring);
            LocationManager locationManager = _getLocationManager();
            if (locationManager != null) {
                locationManager.removeGeofences(stopMonitoring);
                _stoppedMonitoringGeofences(stopMonitoring);
            }
            String inClause = BaseProvider.buildSqlInClause(geofences, new BaseProvider.InClauseBuilder<JSONObject>() { // from class: com.localytics.android.LocationHandler.12
                @Override // com.localytics.android.BaseProvider.InClauseBuilder
                public String getValue(JSONObject object) {
                    return String.format("'%s'", object.optString("identifier"));
                }
            });
            this.mProvider.remove("geofences", String.format("%s NOT IN %s", "identifier", inClause), null);
        } else {
            removedGeofences.addAll(monitoredGeofences);
            this.mProvider.remove("geofences", null, null);
        }
        if (removedGeofences.size() > 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.localytics.android.LocationHandler.13
                @Override // java.lang.Runnable
                public void run() {
                    ((LocationListener) LocationHandler.this.mListeners.getProxy()).localyticsDidUpdateMonitoredGeofences(new ArrayList(), removedGeofences);
                }
            });
        }
    }

    LongSparseArray<ContentValues> _queryGeofencesInfo() {
        LongSparseArray<ContentValues> geofencesInfo = new LongSparseArray<>();
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("geofences", null, null, null, null);
            while (cursor.moveToNext()) {
                long placeId = cursor.getLong(cursor.getColumnIndexOrThrow("place_id"));
                String identifier = cursor.getString(cursor.getColumnIndexOrThrow("identifier"));
                int isMonitored = cursor.getInt(cursor.getColumnIndexOrThrow("is_monitored"));
                long enteredTime = cursor.getLong(cursor.getColumnIndexOrThrow("entered_time"));
                long exitedTime = cursor.getLong(cursor.getColumnIndexOrThrow("exited_time"));
                ContentValues values = new ContentValues(5);
                values.put("place_id", Long.valueOf(placeId));
                values.put("identifier", identifier);
                values.put("is_monitored", Integer.valueOf(isMonitored));
                values.put("entered_time", Long.valueOf(enteredTime));
                values.put("exited_time", Long.valueOf(exitedTime));
                geofencesInfo.put(placeId, values);
            }
            return geofencesInfo;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private long _saveGeofence(JSONObject geofence, LongSparseArray<ContentValues> localGeofences, int schemaVersion) throws JSONException {
        if (!_validateGeofence(geofence)) {
            Localytics.Log.e(String.format("geofence data is invalid:\n%s", geofence.toString()));
            return 0L;
        }
        ContentValues newValues = _parseGeofence(geofence, schemaVersion);
        ContentValues oldValues = localGeofences.get(newValues.getAsLong("place_id").longValue());
        if (oldValues != null) {
            newValues.put("is_monitored", oldValues.getAsInteger("is_monitored"));
            newValues.put("entered_time", oldValues.getAsInteger("entered_time"));
            newValues.put("exited_time", oldValues.getAsInteger("exited_time"));
        }
        long placeId = this.mProvider.replace("geofences", newValues);
        if (placeId > 0) {
            _saveGeofenceAttributes(placeId, geofence.optJSONObject("attributes"));
            return placeId;
        }
        return placeId;
    }

    private void _saveGeofenceAttributes(long placeId, JSONObject attributes) throws JSONException {
        this.mProvider.remove("geofences_attributes", String.format("%s = ?", "place_id"), new String[]{Long.toString(placeId)});
        if (attributes != null) {
            Iterator<String> allKeys = attributes.keys();
            while (allKeys.hasNext()) {
                String key = allKeys.next();
                ContentValues values = new ContentValues();
                values.put("place_id", Long.valueOf(placeId));
                values.put("key", key);
                values.put("value", attributes.getString(key));
                this.mProvider.insert("geofences_attributes", values);
            }
        }
    }

    private boolean _validateGeofence(JSONObject geofence) throws JSONException {
        long placeId = geofence.optLong("place_id");
        String identifier = geofence.optString("identifier");
        double latitude = 360.0d;
        double longitude = 360.0d;
        if (geofence.has("latitude") && (geofence.get("latitude") instanceof Number)) {
            latitude = geofence.getDouble("latitude");
        }
        if (geofence.has("longitude") && (geofence.get("longitude") instanceof Number)) {
            longitude = geofence.getDouble("longitude");
        }
        int radius = geofence.optInt("radius");
        return placeId > 0 && !TextUtils.isEmpty(identifier) && latitude >= -90.0d && latitude <= 90.0d && longitude >= -180.0d && longitude <= 180.0d && radius > 0;
    }

    private ContentValues _parseGeofence(JSONObject geofence, int schemaVersion) throws JSONException {
        ContentValues values = new ContentValues();
        values.put("place_id", Long.valueOf(geofence.getLong("place_id")));
        values.put("identifier", geofence.getString("identifier"));
        values.put("latitude", Double.valueOf(geofence.getDouble("latitude")));
        values.put("longitude", Double.valueOf(geofence.getDouble("longitude")));
        values.put("radius", Integer.valueOf(geofence.getInt("radius")));
        values.put("name", geofence.optString("name"));
        values.put("schema_version", Integer.valueOf(schemaVersion));
        boolean enterAnalyticsEnabled = geofence.optBoolean("enter_analytics_enabled");
        boolean exitAnalyticsEnabled = geofence.optBoolean("exit_analytics_enabled");
        values.put("enter_analytics_enabled", Integer.valueOf(enterAnalyticsEnabled ? 1 : 0));
        values.put("exit_analytics_enabled", Integer.valueOf(exitAnalyticsEnabled ? 1 : 0));
        return values;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void triggerRegion(Region region, Region.Event event) {
        queueMessage(obtainMessage(405, new Object[]{event, region}));
    }

    void _triggerRegion(Region region, Region.Event event) {
        CircularRegion validRegion;
        if ((region instanceof CircularRegion) && (validRegion = _getGeofencePopulated(region.getUniqueId())) != null && _tagPlacesEvent(event, validRegion)) {
            _callListenersDidTriggerRegionsOnMainThread(validRegion, event);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void triggerRegions(List<Region> regions, Region.Event event) {
        queueMessage(obtainMessage(406, new Object[]{event, regions}));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _triggerRegions(List<Region> regions, Region.Event event) {
        CircularRegion validRegion;
        List<Region> list = new LinkedList<>();
        for (Region region : regions) {
            if ((region instanceof CircularRegion) && (validRegion = _getGeofencePopulated(region.getUniqueId())) != null && _tagPlacesEvent(event, validRegion)) {
                list.add(validRegion);
            }
        }
        if (!list.isEmpty()) {
            _callListenersDidTriggerRegionsOnMainThread(list, event);
        }
    }

    private void _callListenersDidTriggerRegionsOnMainThread(Region region, Region.Event event) {
        List<Region> list = new LinkedList<>();
        list.add(region);
        _callListenersDidTriggerRegionsOnMainThread(list, event);
    }

    private void _callListenersDidTriggerRegionsOnMainThread(final List<Region> regions, final Region.Event event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.localytics.android.LocationHandler.14
            @Override // java.lang.Runnable
            public void run() {
                ((LocationListener) LocationHandler.this.mListeners.getProxy()).localyticsDidTriggerRegions(regions, event);
            }
        });
    }

    private CircularRegion _getGeofencePopulated(String identifier) {
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("geofences", null, String.format("%s = ?", "identifier"), new String[]{identifier}, null);
            if (cursor.moveToFirst()) {
                CircularRegion build = new CircularRegion.Builder().setPlaceId(cursor.getLong(cursor.getColumnIndexOrThrow("place_id"))).setUniqueId(cursor.getString(cursor.getColumnIndexOrThrow("identifier"))).setName(cursor.getString(cursor.getColumnIndexOrThrow("name"))).setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"))).setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"))).setRadius(cursor.getInt(cursor.getColumnIndexOrThrow("radius"))).setEnterAnalyticsEnabled(cursor.getInt(cursor.getColumnIndexOrThrow("enter_analytics_enabled")) == 1).setExitAnalyticsEnabled(cursor.getInt(cursor.getColumnIndexOrThrow("exit_analytics_enabled")) == 1).setSchemaVersion(cursor.getInt(cursor.getColumnIndexOrThrow("schema_version"))).setAttributes(_geofenceAttributes(identifier)).build();
            }
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    protected long _getPlaceId(String identifier) {
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("geofences", null, String.format("%s = ?", "identifier"), new String[]{identifier}, null);
            if (cursor.moveToFirst()) {
                long j = cursor.getLong(cursor.getColumnIndexOrThrow("place_id"));
            }
            if (cursor != null) {
                cursor.close();
            }
            return 0L;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private Map<String, String> _geofenceAttributes(String identifier) {
        Map<String, String> attributes = new HashMap<>();
        long placeId = _getPlaceId(identifier);
        if (placeId > 0) {
            Cursor cursor = null;
            try {
                cursor = this.mProvider.query("geofences_attributes", null, String.format("%s = ?", "place_id"), new String[]{Long.toString(placeId)}, null);
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String key = cursor.getString(cursor.getColumnIndexOrThrow("key"));
                    String val = cursor.getString(cursor.getColumnIndexOrThrow("value"));
                    attributes.put(key, val);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return attributes;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private boolean _tagPlacesEvent(Region.Event event, Region region) {
        String identifier = region.getUniqueId();
        long nowMillis = this.mLocalyticsDao.getCurrentTimeMillis();
        long enteredTimeMillis = _getRegionEnteredTime(identifier);
        long exitedTimeMillis = _getRegionExitedTime(identifier);
        long lastEventTimeMillis = Math.max(enteredTimeMillis, exitedTimeMillis);
        switch (event) {
            case ENTER:
                if (lastEventTimeMillis == 0 || nowMillis - lastEventTimeMillis > Constants.REGION_THROTTLE_CUTOFF_TIME_MILLIS) {
                    _saveEnteredTime(identifier, nowMillis);
                    _tagEnterEvent(region);
                    return true;
                }
                return false;
            case EXIT:
                long dwellTimeMillis = nowMillis - enteredTimeMillis;
                if (dwellTimeMillis >= Constants.MIN_REGION_DWELL_TIME_MILLIS && dwellTimeMillis <= Constants.MAX_REGION_DWELL_TIME_MILLIS) {
                    _saveExitedTime(identifier, nowMillis);
                    _removeEnteredTime(identifier);
                    _tagExitEvent(region, dwellTimeMillis);
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    private void _removeEnteredTime(String identifier) {
        _updateTime(identifier, 0L, "entered_time");
    }

    void _saveEnteredTime(String identifier, long nowMillis) {
        _updateTime(identifier, nowMillis, "entered_time");
    }

    void _saveExitedTime(String identifier, long nowMillis) {
        _updateTime(identifier, nowMillis, "exited_time");
    }

    private void _updateTime(String identifier, long millis, String columnName) {
        ContentValues values = new ContentValues();
        values.put(columnName, Long.valueOf(millis));
        this.mProvider.update("geofences", values, String.format("%s = ?", "identifier"), new String[]{identifier});
    }

    private void _tagEnterEvent(Region region) {
        if (region.isEnterAnalyticsEnabled()) {
            this.mLocalyticsDao.tagEvent("Localytics Place Entered", _getTagEventAttributes(region));
            this.mLocalyticsDao.upload();
        }
    }

    private void _tagExitEvent(Region region, long dwellTimeMillis) {
        if (region.isExitAnalyticsEnabled()) {
            Map<String, String> attributes = _getTagEventAttributes(region);
            long dwellTimeMinutes = Math.max(Math.round(dwellTimeMillis / 60000.0d), 1L);
            attributes.put("Dwell Time (minutes)", String.valueOf(dwellTimeMinutes));
            this.mLocalyticsDao.tagEvent("Localytics Place Visited", attributes);
            this.mLocalyticsDao.upload();
        }
    }

    private Map<String, String> _getTagEventAttributes(Region region) {
        Map<String, String> attributes = new HashMap<>();
        String uniqueId = region.getUniqueId();
        attributes.put("Localytics Place ID", String.valueOf(_getPlaceId(uniqueId)));
        attributes.put("Region Identifier", uniqueId);
        attributes.put("Region Type", region.getType());
        attributes.put("Schema Version - Client", MessageAPI.DEVICE_ID);
        attributes.put("Schema Version - Server", String.valueOf(region.getSchemaVersion()));
        String wifiEnabled = DatapointHelper.isWifiEnabled(this.mLocalyticsDao.getAppContext());
        attributes.put("Wifi Enabled", wifiEnabled);
        attributes.putAll(region.getAttributes());
        return attributes;
    }

    private long _getRegionEnteredTime(String identifier) {
        return _getRegionTime(identifier, "entered_time");
    }

    private long _getRegionExitedTime(String identifier) {
        return _getRegionTime(identifier, "exited_time");
    }

    private long _getRegionTime(String identifier, String columnName) {
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("geofences", new String[]{columnName}, String.format("%s = ?", "identifier"), new String[]{identifier}, null);
            if (cursor.moveToFirst()) {
                long j = cursor.getLong(cursor.getColumnIndexOrThrow(columnName));
            }
            if (cursor != null) {
                cursor.close();
            }
            return 0L;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override // com.localytics.android.ManifestListener
    public void localyticsWillDownloadManifest() {
    }

    @Override // com.localytics.android.ManifestListener
    public void localyticsDidDownloadManifest(Map<String, Object> marketingMap, Map<String, Object> config, boolean successful) {
        if (successful) {
            queueMessage(obtainMessage(404, config));
        }
    }

    @Override // com.localytics.android.LocationChangedListener
    public void onLocationChanged(Location location) {
        this.mListeners.getProxy().localyticsDidUpdateLocation(location);
        queueMessage(obtainMessage(402, location));
    }
}
