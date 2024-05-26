package com.localytics.android;

/* loaded from: classes.dex */
class PlayServicesUtils {
    PlayServicesUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isLocationAvailable() {
        try {
            Class.forName("com.google.android.gms.location.LocationServices");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isAdvertisingAvailable() {
        try {
            Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
