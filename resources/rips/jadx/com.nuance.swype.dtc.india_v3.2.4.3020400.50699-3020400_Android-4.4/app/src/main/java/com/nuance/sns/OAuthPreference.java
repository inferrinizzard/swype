package com.nuance.sns;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import com.nuance.android.compat.SharedPreferencesEditorCompat;

/* loaded from: classes.dex */
public class OAuthPreference {
    private final String pref;

    private OAuthPreference(String pref) {
        this.pref = pref;
    }

    public void putString(Context context, String key, String value) {
        SharedPreferencesEditorCompat.apply(context.getSharedPreferences(this.pref, 0).edit().putString(key, value));
    }

    private void clear(Context context) {
        SharedPreferencesEditorCompat.apply(context.getSharedPreferences(this.pref, 0).edit().clear());
    }

    public String getString(Context context, String key) {
        return context.getSharedPreferences(this.pref, 0).getString(key, null);
    }

    private static OAuthPreference getTwitterOAuthPreference() {
        return new OAuthPreference("twitter");
    }

    public static void reset(Context context) {
        getTwitterOAuthPreference().clear(context);
        CookieSyncManager.createInstance(context);
        CookieManager.getInstance().removeAllCookie();
    }
}
