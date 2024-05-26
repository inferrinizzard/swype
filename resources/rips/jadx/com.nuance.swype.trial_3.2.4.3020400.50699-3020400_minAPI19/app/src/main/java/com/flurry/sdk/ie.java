package com.flurry.sdk;

import android.location.Location;
import bolts.MeasurementEvent;
import java.util.Map;

/* loaded from: classes.dex */
public class ie extends kk {
    private static final String a = ie.class.getSimpleName();

    public final String a(String str, Map<String, String> map) {
        String replace;
        String a2 = a(str);
        while (a2 != null) {
            if (a("timestamp_epoch_millis", a2)) {
                String valueOf = String.valueOf(System.currentTimeMillis());
                kf.a(3, a, "Replacing param timestamp_epoch_millis with: " + valueOf);
                replace = str.replace(a2, lr.c(valueOf));
            } else if (a("session_duration_millis", a2)) {
                jd.a();
                String l = Long.toString(jd.f());
                kf.a(3, a, "Replacing param session_duration_millis with: " + l);
                replace = str.replace(a2, lr.c(l));
            } else if (a("fg_timespent_millis", a2)) {
                jd.a();
                String l2 = Long.toString(jd.f());
                kf.a(3, a, "Replacing param fg_timespent_millis with: " + l2);
                replace = str.replace(a2, lr.c(l2));
            } else if (a("install_referrer", a2)) {
                String b = new hl().b();
                if (b == null) {
                    b = "";
                }
                kf.a(3, a, "Replacing param install_referrer with: " + b);
                replace = str.replace(a2, lr.c(b));
            } else if (a("geo_latitude", a2)) {
                Location e = ji.a().e();
                String str2 = e != null ? "" + lr.a(e.getLatitude()) : "";
                kf.a(3, a, "Replacing param geo_latitude with: " + str2);
                replace = str.replace(a2, lr.c(str2));
            } else if (a("geo_longitude", a2)) {
                Location e2 = ji.a().e();
                String str3 = e2 != null ? "" + lr.a(e2.getLongitude()) : "";
                kf.a(3, a, "Replacing param geo_longitude with: " + str3);
                replace = str.replace(a2, lr.c(str3));
            } else if (a("publisher_user_id", a2)) {
                String str4 = (String) li.a().a("UserId");
                kf.a(3, a, "Replacing param publisher_user_id with: " + str4);
                replace = str.replace(a2, lr.c(str4));
            } else if (a(MeasurementEvent.MEASUREMENT_EVENT_NAME_KEY, a2)) {
                if (map.containsKey(MeasurementEvent.MEASUREMENT_EVENT_NAME_KEY)) {
                    kf.a(3, a, "Replacing param event_name with: " + map.get(MeasurementEvent.MEASUREMENT_EVENT_NAME_KEY));
                    replace = str.replace(a2, lr.c(map.get(MeasurementEvent.MEASUREMENT_EVENT_NAME_KEY)));
                } else {
                    kf.a(3, a, "Replacing param event_name with empty string");
                    replace = str.replace(a2, "");
                }
            } else if (a("event_time_millis", a2)) {
                if (map.containsKey("event_time_millis")) {
                    kf.a(3, a, "Replacing param event_time_millis with: " + map.get("event_time_millis"));
                    replace = str.replace(a2, lr.c(map.get("event_time_millis")));
                } else {
                    kf.a(3, a, "Replacing param event_time_millis with empty string");
                    replace = str.replace(a2, "");
                }
            } else {
                kf.a(3, a, "Unknown param: " + a2);
                replace = str.replace(a2, "");
            }
            a2 = a(replace);
            str = replace;
        }
        return str;
    }
}
