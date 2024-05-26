package com.localytics.android;

import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.localytics.android.Localytics;
import com.localytics.android.Utils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class LocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static final long UPDATE_FASTEST_INTERVAL = 60000;
    private static final long UPDATE_INTERVAL = 300000;
    private GoogleApiClient mApiClient;
    private PendingIntent mGeofencePendingIntent;
    private Location mLastLocation;
    private LocationChangedListener mListener;
    private LocalyticsDao mLocalyticsDao;
    private OnConnectedTask mOnConnectedTask;

    /* loaded from: classes.dex */
    private enum OnConnectedTask {
        REQUEST_LOCATION_UPDATES,
        STOP_LOCATION_UPDATES
    }

    public void initialize(LocalyticsDao localyticsDao, LocationChangedListener listener) {
        this.mLocalyticsDao = localyticsDao;
        this.mListener = listener;
    }

    public synchronized void setMonitoringEnabled(boolean enabled) {
        createApiClientIfNecessary();
        Localytics.Log.d("LocationManager setMonitoringEnabled: " + enabled);
        if (enabled) {
            startMonitoring();
        } else {
            stopMonitoring();
        }
    }

    public void updateListenersWithLastLocation() {
        if (this.mLastLocation != null) {
            onLocationChanged(this.mLastLocation);
        }
    }

    public void addGeofences(final List<CircularRegion> regions) {
        createApiClientIfNecessary();
        if (regions.size() > 0) {
            runOnMainThread(new Runnable() { // from class: com.localytics.android.LocationManager.1
                @Override // java.lang.Runnable
                public void run() {
                    if (ActivityCompat.checkSelfPermission(LocationManager.this.mLocalyticsDao.getAppContext(), "android.permission.ACCESS_FINE_LOCATION") == 0) {
                        LocationManager.this.mGeofencePendingIntent = LocationManager.this.getGeofencePendingIntent();
                        LocationServices.GeofencingApi.addGeofences(LocationManager.this.mApiClient, LocationManager.this.getGeofencingRequest(regions), LocationManager.this.mGeofencePendingIntent).setResultCallback(new ResultCallback<Status>() { // from class: com.localytics.android.LocationManager.1.1
                            @Override // com.google.android.gms.common.api.ResultCallback
                            public void onResult(Status status) {
                                if (status.isSuccess()) {
                                    Localytics.Log.d("LocationManager successfully added geofences: " + regions);
                                } else {
                                    Localytics.Log.d("LocationManager failed to add geofences: " + regions);
                                    LocationManager.this.handleErrorStatus(status);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public void removeGeofences(final List<CircularRegion> geofencesToRemove) {
        createApiClientIfNecessary();
        final List<String> geofenceIdentifiersToRemove = new ArrayList<>(geofencesToRemove.size());
        Utils.map(geofencesToRemove, geofenceIdentifiersToRemove, new Utils.Mapper<CircularRegion, String>() { // from class: com.localytics.android.LocationManager.2
            @Override // com.localytics.android.Utils.Mapper
            public String transform(CircularRegion in) {
                return in.getUniqueId();
            }
        });
        if (geofenceIdentifiersToRemove.size() > 0) {
            runOnMainThread(new Runnable() { // from class: com.localytics.android.LocationManager.3
                @Override // java.lang.Runnable
                public void run() {
                    LocationServices.GeofencingApi.removeGeofences(LocationManager.this.mApiClient, geofenceIdentifiersToRemove).setResultCallback(new ResultCallback<Status>() { // from class: com.localytics.android.LocationManager.3.1
                        @Override // com.google.android.gms.common.api.ResultCallback
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Localytics.Log.d("LocationManager successfully removed geofences IDs: " + geofenceIdentifiersToRemove);
                            } else {
                                Localytics.Log.d("LocationManager failed to remove geofences: " + geofencesToRemove);
                                LocationManager.this.handleErrorStatus(status);
                            }
                        }
                    });
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleErrorStatus(Status status) {
        int statusCode = status.getStatusCode();
        switch (statusCode) {
            case 1000:
                Localytics.Log.d("GEOFENCE_NOT_AVAILABLE. Resetting database state to none monitored.");
                this.mLocalyticsDao.stoppedMonitoringAllGeofences();
                return;
            case 1001:
                Localytics.Log.d("GEOFENCE_TOO_MANY_GEOFENCES. Removing all monitored geofences to reset state.");
                removeAllGeofences();
                return;
            default:
                Localytics.Log.d("ERROR: " + statusCode + " - " + status.getStatusMessage() + ". Removing all monitored geofences to reset state.");
                removeAllGeofences();
                this.mLocalyticsDao.stoppedMonitoringAllGeofences();
                return;
        }
    }

    private synchronized void createApiClientIfNecessary() {
        if (this.mApiClient == null) {
            this.mApiClient = new GoogleApiClient.Builder(this.mLocalyticsDao.getAppContext()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }
    }

    private void removeAllGeofences() {
        runOnMainThread(new Runnable() { // from class: com.localytics.android.LocationManager.4
            @Override // java.lang.Runnable
            public void run() {
                LocationServices.GeofencingApi.removeGeofences(LocationManager.this.mApiClient, LocationManager.this.getGeofencePendingIntent()).setResultCallback(new ResultCallback<Status>() { // from class: com.localytics.android.LocationManager.4.1
                    @Override // com.google.android.gms.common.api.ResultCallback
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Localytics.Log.d("LocationManager successfully removed all geofences");
                        } else {
                            Localytics.Log.d("LocationManager failed to remove all geofences. Reason: " + status.getStatusMessage());
                        }
                        LocationManager.this.mLocalyticsDao.stoppedMonitoringAllGeofences();
                    }
                });
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public GeofencingRequest getGeofencingRequest(List<CircularRegion> regions) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(1);
        builder.addGeofences(convertRegions(regions));
        return builder.build();
    }

    private List<Geofence> convertRegions(List<CircularRegion> regions) {
        List<Geofence> geofences = new LinkedList<>();
        for (CircularRegion reg : regions) {
            geofences.add(new Geofence.Builder().setRequestId(reg.getUniqueId()).setCircularRegion(reg.getLatitude(), reg.getLongitude(), reg.getRadius()).setExpirationDuration(-1L).setTransitionTypes(3).build());
        }
        return geofences;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public PendingIntent getGeofencePendingIntent() {
        if (this.mGeofencePendingIntent != null) {
            return this.mGeofencePendingIntent;
        }
        Intent intent = new Intent(this.mLocalyticsDao.getAppContext(), (Class<?>) GeofenceTransitionsService.class);
        return PendingIntent.getService(this.mLocalyticsDao.getAppContext(), 0, intent, 134217728);
    }

    private void startMonitoring() {
        runOnMainThread(new Runnable() { // from class: com.localytics.android.LocationManager.5
            @Override // java.lang.Runnable
            public void run() {
                Localytics.Log.d("LocationManager startMonitoring called");
                LocationManager.this.mOnConnectedTask = OnConnectedTask.REQUEST_LOCATION_UPDATES;
                if (LocationManager.this.mApiClient.isConnected()) {
                    LocationManager.this.requestLocationUpdates();
                } else if (!LocationManager.this.mApiClient.isConnecting()) {
                    Localytics.Log.d("LocationManager connecting to GoogleApiClient for startMonitoring");
                    LocationManager.this.mApiClient.connect();
                }
            }
        });
    }

    private void stopMonitoring() {
        runOnMainThread(new Runnable() { // from class: com.localytics.android.LocationManager.6
            @Override // java.lang.Runnable
            public void run() {
                Localytics.Log.d("LocationManager stopMonitoring called");
                LocationManager.this.mOnConnectedTask = OnConnectedTask.STOP_LOCATION_UPDATES;
                if (LocationManager.this.mApiClient.isConnected()) {
                    LocationManager.this.stopLocationUpdates();
                } else if (!LocationManager.this.mApiClient.isConnecting()) {
                    Localytics.Log.d("LocationManager connecting to GoogleApiClient for stopMonitoring");
                    LocationManager.this.mApiClient.connect();
                }
            }
        });
    }

    private void runOnMainThread(Runnable runnable) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            new Handler(Looper.getMainLooper()).post(runnable);
        } else {
            runnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this.mLocalyticsDao.getAppContext(), "android.permission.ACCESS_FINE_LOCATION") == 0) {
            LocationRequest request = new LocationRequest();
            request.setInterval(UPDATE_INTERVAL);
            request.setFastestInterval(60000L);
            request.setPriority(102);
            LocationServices.FusedLocationApi.requestLocationUpdates(this.mApiClient, request, this);
            Localytics.Log.d("LocationManager requesting location updates");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopLocationUpdates() {
        this.mLastLocation = null;
        LocationServices.FusedLocationApi.removeLocationUpdates(this.mApiClient, this);
        removeAllGeofences();
        this.mApiClient.disconnect();
        Localytics.Log.d("LocationManager stopped monitoring location");
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
    public void onConnected(Bundle connectionHint) {
        Localytics.Log.d("LocationManager GoogleApiClient connected");
        switch (this.mOnConnectedTask) {
            case REQUEST_LOCATION_UPDATES:
                requestLocationUpdates();
                return;
            case STOP_LOCATION_UPDATES:
                stopLocationUpdates();
                return;
            default:
                return;
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
    public void onConnectionSuspended(int cause) {
        Localytics.Log.w("LocationManager GoogleApiClient onConnectionSuspended. cause: " + cause);
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult result) {
        Localytics.Log.w("LocationManager GoogleApiClient onConnectionFailed. result: " + result);
    }

    public void onLocationChanged(Location location) {
        Localytics.Log.d("LocationManager onLocationChanged: " + location);
        this.mLastLocation = location;
        this.mListener.onLocationChanged(location);
    }
}
