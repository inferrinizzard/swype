package io.fabric.sdk.android.services.settings;

import com.facebook.share.internal.ShareConstants;
import com.nuance.swypeconnect.ac.ACBuildConfigRuntime;
import io.fabric.sdk.android.services.common.CurrentTimeProvider;
import jp.co.omronsoft.openwnn.JAJP.OpenWnnEngineJAJP;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class DefaultSettingsJsonTransform implements SettingsJsonTransform {
    @Override // io.fabric.sdk.android.services.settings.SettingsJsonTransform
    public final SettingsData buildFromJson(CurrentTimeProvider currentTimeProvider, JSONObject json) throws JSONException {
        long expiresAtMillis;
        int settingsVersion = json.optInt("settings_version", 0);
        int cacheDuration = json.optInt("cache_duration", ACBuildConfigRuntime.MINUMUM_REFRESH_INTERVAL);
        JSONObject jSONObject = json.getJSONObject("app");
        String string = jSONObject.getString("identifier");
        String string2 = jSONObject.getString("status");
        String string3 = jSONObject.getString("url");
        String string4 = jSONObject.getString("reports_url");
        boolean optBoolean = jSONObject.optBoolean("update_required", false);
        AppIconSettingsData appIconSettingsData = null;
        if (jSONObject.has("icon") && jSONObject.getJSONObject("icon").has("hash")) {
            JSONObject jSONObject2 = jSONObject.getJSONObject("icon");
            appIconSettingsData = new AppIconSettingsData(jSONObject2.getString("hash"), jSONObject2.getInt("width"), jSONObject2.getInt("height"));
        }
        AppSettingsData appData = new AppSettingsData(string, string2, string3, string4, optBoolean, appIconSettingsData);
        JSONObject jSONObject3 = json.getJSONObject("session");
        SessionSettingsData settingsData = new SessionSettingsData(jSONObject3.optInt("log_buffer_size", 64000), jSONObject3.optInt("max_chained_exception_depth", 8), jSONObject3.optInt("max_custom_exception_events", 64), jSONObject3.optInt("max_custom_key_value_pairs", 64), jSONObject3.optInt("identifier_mask", 255), jSONObject3.optBoolean("send_session_without_crash", false));
        JSONObject jSONObject4 = json.getJSONObject("prompt");
        PromptSettingsData promptData = new PromptSettingsData(jSONObject4.optString(ShareConstants.WEB_DIALOG_PARAM_TITLE, "Send Crash Report?"), jSONObject4.optString("message", "Looks like we crashed! Please help us fix the problem by sending a crash report."), jSONObject4.optString("send_button_title", "Send"), jSONObject4.optBoolean("show_cancel_button", true), jSONObject4.optString("cancel_button_title", "Don't Send"), jSONObject4.optBoolean("show_always_send_button", true), jSONObject4.optString("always_send_button_title", "Always Send"));
        JSONObject jSONObject5 = json.getJSONObject("features");
        FeaturesSettingsData featureData = new FeaturesSettingsData(jSONObject5.optBoolean("prompt_enabled", false), jSONObject5.optBoolean("collect_logged_exceptions", true), jSONObject5.optBoolean("collect_reports", true), jSONObject5.optBoolean("collect_analytics", false));
        JSONObject jSONObject6 = json.getJSONObject("analytics");
        AnalyticsSettingsData analyticsData = new AnalyticsSettingsData(jSONObject6.optString("url", "https://e.crashlytics.com/spi/v2/events"), jSONObject6.optInt("flush_interval_secs", OpenWnnEngineJAJP.FREQ_LEARN), jSONObject6.optInt("max_byte_size_per_file", 8000), jSONObject6.optInt("max_file_count_per_send", 1), jSONObject6.optInt("max_pending_send_file_count", 100));
        JSONObject jSONObject7 = json.getJSONObject("beta");
        BetaSettingsData betaData = new BetaSettingsData(jSONObject7.optString("update_endpoint", SettingsJsonConstants.BETA_UPDATE_ENDPOINT_DEFAULT), jSONObject7.optInt("update_suspend_duration", ACBuildConfigRuntime.MINUMUM_REFRESH_INTERVAL));
        long j = cacheDuration;
        if (json.has("expires_at")) {
            expiresAtMillis = json.getLong("expires_at");
        } else {
            expiresAtMillis = currentTimeProvider.getCurrentTimeMillis() + (j * 1000);
        }
        return new SettingsData(expiresAtMillis, appData, settingsData, promptData, featureData, analyticsData, betaData, settingsVersion, cacheDuration);
    }

    @Override // io.fabric.sdk.android.services.settings.SettingsJsonTransform
    public final JSONObject sanitizeTraceInfo(JSONObject json) throws JSONException {
        JSONObject sanitized = new JSONObject(json.toString());
        sanitized.getJSONObject("features").remove("collect_analytics");
        sanitized.remove("analytics");
        return sanitized;
    }
}
