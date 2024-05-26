package com.nuance.swype.input;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.nuance.swype.input.settings.SettingsPrefs;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class SettingsSoftResetBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION_SETTINGS_SOFT_RESET = "android.intent.action.SETTINGS_SOFT_RESET";
    protected static final LogManager.Log log = LogManager.getLog("SettingsSoftResetBroadcastReceiver");

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            log.d("onReceive(): " + action);
            if (ACTION_SETTINGS_SOFT_RESET.equals(action)) {
                log.d("onReceive(): performing soft reset...");
                SettingsPrefs.resetSettings(context);
            }
        }
    }
}
