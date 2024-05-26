package com.crashlytics.android;

import android.content.Context;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.settings.PromptSettingsData;

/* loaded from: classes.dex */
final class DialogStringResolver {
    private final Context context;
    final PromptSettingsData promptData;

    public DialogStringResolver(Context context, PromptSettingsData promptData) {
        this.context = context;
        this.promptData = promptData;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String resourceOrFallbackValue(String resourceName, String settingsValue) {
        String settingsValue2 = CommonUtils.getStringsFileValue(this.context, resourceName);
        return settingsValue2 == null || settingsValue2.length() == 0 ? settingsValue : settingsValue2;
    }
}
