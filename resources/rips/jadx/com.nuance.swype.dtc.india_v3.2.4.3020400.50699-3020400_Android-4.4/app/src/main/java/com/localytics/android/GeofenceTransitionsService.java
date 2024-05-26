package com.localytics.android;

import android.app.IntentService;
import android.content.Intent;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.localytics.android.CircularRegion;
import com.localytics.android.Localytics;
import com.localytics.android.Region;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public class GeofenceTransitionsService extends IntentService {
    public GeofenceTransitionsService() {
        super("GeofenceTransitionsService");
    }

    @Override // android.app.IntentService
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            GeofencingEvent event = GeofencingEvent.fromIntent(intent);
            if (event.hasError()) {
                logError(event.getErrorCode());
                return;
            }
            int transition = event.getGeofenceTransition();
            if (transition == 1 || transition == 2) {
                List<Geofence> triggeringGeofences = event.getTriggeringGeofences();
                List<Region> regions = new LinkedList<>();
                for (Geofence geofence : triggeringGeofences) {
                    regions.add(new CircularRegion.Builder().setUniqueId(geofence.getRequestId()).build());
                }
                Region.Event transitionEvent = eventForTransition(transition);
                if (transitionEvent != null) {
                    Localytics.triggerRegions(regions, transitionEvent);
                    return;
                }
                return;
            }
            Localytics.Log.d("Geofence transition ignored - not enter or exit");
        }
    }

    private Region.Event eventForTransition(int transition) {
        switch (transition) {
            case 1:
                return Region.Event.ENTER;
            case 2:
                return Region.Event.EXIT;
            default:
                return null;
        }
    }

    private void logError(int errorCode) {
        String message = "Geofence: Unknown error";
        switch (errorCode) {
            case 1000:
                message = "Geofence: Geofence not available";
                break;
            case 1001:
                message = "Geofence: Too many geofences";
                break;
            case 1002:
                message = "Geofence: Too many pending intents";
                break;
        }
        Localytics.Log.w(message);
    }
}
