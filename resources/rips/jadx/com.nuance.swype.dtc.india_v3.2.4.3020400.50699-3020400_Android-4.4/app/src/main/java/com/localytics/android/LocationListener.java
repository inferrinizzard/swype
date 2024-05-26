package com.localytics.android;

import android.location.Location;
import com.localytics.android.Region;
import java.util.List;

/* loaded from: classes.dex */
public interface LocationListener {
    void localyticsDidTriggerRegions(List<Region> list, Region.Event event);

    void localyticsDidUpdateLocation(Location location);

    void localyticsDidUpdateMonitoredGeofences(List<CircularRegion> list, List<CircularRegion> list2);
}
