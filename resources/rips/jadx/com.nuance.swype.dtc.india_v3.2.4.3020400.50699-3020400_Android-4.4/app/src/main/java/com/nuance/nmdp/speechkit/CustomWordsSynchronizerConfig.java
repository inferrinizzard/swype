package com.nuance.nmdp.speechkit;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/* loaded from: classes.dex */
public class CustomWordsSynchronizerConfig {
    public static final String CUSTOM_WORDS_SYNCHRONIZE_CURRENT_CHECKSUM = "custom_words_synchronize_current_checksum";
    private static final String DEFAULT_ALGORITHM_ID = "Random";
    private static final String DEFAULT_CHECKSUM = "0";

    public static String getCurrentChecksum(Context context) {
        return getCurrentChecksum(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public static String getNewChecksum(Context context) {
        return getNewChecksum(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public static String getCurrentChecksum(SharedPreferences sp) {
        return sp == null ? "0" : sp.getString(CUSTOM_WORDS_SYNCHRONIZE_CURRENT_CHECKSUM, "0");
    }

    public static String getNewChecksum(SharedPreferences sp) {
        if (sp == null) {
            return "0";
        }
        String newChecksum = Long.toString(System.currentTimeMillis());
        if (newChecksum.compareTo(getCurrentChecksum(sp)) == 0) {
            return Long.toString(System.currentTimeMillis() + 1);
        }
        return newChecksum;
    }

    public static void setNewChecksum(SharedPreferences sp, String checksum) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CUSTOM_WORDS_SYNCHRONIZE_CURRENT_CHECKSUM, checksum);
        editor.commit();
    }

    public static String getAlgorithmID() {
        return DEFAULT_ALGORITHM_ID;
    }
}
