package com.flurry.sdk;

import android.content.Context;

/* loaded from: classes.dex */
public class ls {
    private static final String a = ls.class.getSimpleName();
    private static String b = "com.google.android.gms.common.GoogleApiAvailability";
    private static String c = "com.google.android.gms.common.GooglePlayServicesUtil";
    private static String d = "com.google.android.gms.ads.identifier.AdvertisingIdClient";

    public static boolean a(Context context) {
        try {
            return a(context, b);
        } catch (Exception e) {
            try {
                return a(context, c);
            } catch (Exception e2) {
                kf.b(a, "GOOGLE PLAY SERVICES EXCEPTION: " + e2.getMessage());
                kf.b(a, "There is a problem with the Google Play Services library, which is required for Android Advertising ID support. The Google Play Services library should be integrated in any app shipping in the Play Store that uses analytics or advertising.");
                return false;
            }
        }
    }

    private static boolean a(Context context, String str) throws Exception {
        Object a2 = lu.a(null, "isGooglePlayServicesAvailable").a(Class.forName(str)).a(Context.class, context).a();
        return a2 != null && ((Integer) a2).intValue() == 0;
    }

    public static jo b(Context context) {
        if (context == null) {
            return null;
        }
        try {
            Object a2 = lu.a(null, "getAdvertisingIdInfo").a(Class.forName(d)).a(Context.class, context).a();
            return new jo(a(a2), b(a2));
        } catch (Exception e) {
            kf.b(a, "GOOGLE PLAY SERVICES ERROR: " + e.getMessage());
            kf.b(a, "There is a problem with the Google Play Services library, which is required for Android Advertising ID support. The Google Play Services library should be integrated in any app shipping in the Play Store that uses analytics or advertising.");
            return null;
        }
    }

    private static String a(Object obj) {
        try {
            return (String) lu.a(obj, "getId").a();
        } catch (Exception e) {
            kf.b(a, "GOOGLE PLAY SERVICES ERROR: " + e.getMessage());
            kf.b(a, "There is a problem with the Google Play Services library, which is required for Android Advertising ID support. The Google Play Services library should be integrated in any app shipping in the Play Store that uses analytics or advertising.");
            return null;
        }
    }

    private static boolean b(Object obj) {
        try {
            Boolean bool = (Boolean) lu.a(obj, "isLimitAdTrackingEnabled").a();
            if (bool != null) {
                return bool.booleanValue();
            }
            return false;
        } catch (Exception e) {
            kf.b(a, "GOOGLE PLAY SERVICES ERROR: " + e.getMessage());
            kf.b(a, "There is a problem with the Google Play Services library, which is required for Android Advertising ID support. The Google Play Services library should be integrated in any app shipping in the Play Store that uses analytics or advertising.");
            return false;
        }
    }
}
