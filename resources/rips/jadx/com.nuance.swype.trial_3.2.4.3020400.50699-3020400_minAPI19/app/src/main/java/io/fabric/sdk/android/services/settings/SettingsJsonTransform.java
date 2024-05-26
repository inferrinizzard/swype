package io.fabric.sdk.android.services.settings;

import io.fabric.sdk.android.services.common.CurrentTimeProvider;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public interface SettingsJsonTransform {
    SettingsData buildFromJson(CurrentTimeProvider currentTimeProvider, JSONObject jSONObject) throws JSONException;

    JSONObject sanitizeTraceInfo(JSONObject jSONObject) throws JSONException;
}
