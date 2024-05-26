package com.nuance.swype.input.settings;

import android.os.Bundle;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.swype.input.IMEApplication;

/* loaded from: classes.dex */
public class ChinesePrefsDispatch extends SettingsDispatch {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.settings.SettingsDispatch, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!ActivityManagerCompat.isUserAMonkey()) {
            IMEApplication.from(this).showChineseSettings();
        }
        finish();
    }

    @Override // com.nuance.swype.input.settings.SettingsDispatch, android.app.Activity, android.view.Window.Callback
    public void onAttachedToWindow() {
    }
}
