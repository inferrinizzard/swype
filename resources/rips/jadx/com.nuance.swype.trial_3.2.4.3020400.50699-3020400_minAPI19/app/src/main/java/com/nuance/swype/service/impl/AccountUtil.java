package com.nuance.swype.service.impl;

import android.accounts.AccountManager;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.util.AdsUtil;
import com.nuance.swypeconnect.ac.ACDevice;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
public final class AccountUtil {
    private static boolean isGoogleAccountMissing = false;
    private static boolean isGoogleAccountSignedIn = true;

    public static boolean isGoogleAccountMissing() {
        return isGoogleAccountMissing;
    }

    public static boolean isGoogleAccountSignedIn() {
        return isGoogleAccountSignedIn;
    }

    public static void setIsGoogleAccountSignedIn(boolean isGoogleAccountSignedIn2) {
        isGoogleAccountSignedIn = isGoogleAccountSignedIn2;
    }

    public static boolean deviceHasGoogleAccount(Context context) {
        return AccountManager.get(context).getAccountsByType("com.google").length > 0;
    }

    public static void setGoogleAccountMissing(Context context, boolean missing) {
        isGoogleAccountMissing = missing;
        if (!AdsUtil.sAdsSupported || missing || missing == AdsUtil.sTagForChildDirectedTreatment) {
            return;
        }
        AdsUtil.sTagForChildDirectedTreatment = missing;
        UserPreferences.from(context).setTagForChildDirectedTreatment(missing);
    }

    public static String buildDeviceName(Context context) {
        String aCDeviceType;
        StringBuilder name = new StringBuilder();
        String modelName = "Android device";
        try {
            Field field = Build.class.getDeclaredField("MODEL");
            field.setAccessible(true);
            modelName = field.get(Build.class).toString();
        } catch (Exception e) {
        }
        StringBuilder append = name.append(modelName).append(XMLResultsHandler.SEP_SPACE);
        if (isTablet(context)) {
            aCDeviceType = ACDevice.ACDeviceType.TABLET.toString();
        } else {
            aCDeviceType = ACDevice.ACDeviceType.PHONE.toString();
        }
        append.append(aCDeviceType);
        return name.toString();
    }

    public static boolean isTablet(Context context) {
        int i;
        int screenLayoutSize = context != null ? context.getResources().getConfiguration().screenLayout & 15 : 0;
        if (context != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
            i = displayMetrics.densityDpi;
        } else {
            i = 160;
        }
        return i >= 160 && screenLayoutSize >= 4;
    }
}
