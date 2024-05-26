package com.nuance.swype.input.settings;

import android.os.Bundle;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.swype.connect.SDKDownloadManager;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputMethods;

/* loaded from: classes.dex */
public class LanguageDispatch extends SettingsDispatch {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.settings.SettingsDispatch, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!ActivityManagerCompat.isUserAMonkey()) {
            Bundle data = getIntent().getExtras();
            int langId = 0;
            if (data != null) {
                langId = data.getInt(SDKDownloadManager.SELECT_INSTALLED_LANGUAGE);
            }
            if (langId != 0) {
                InputMethods.from(this).setCurrentLanguageById(langId);
            }
            IMEApplication.from(this).showLanguages();
        }
        finish();
    }

    @Override // com.nuance.swype.input.settings.SettingsDispatch, android.app.Activity, android.view.Window.Callback
    public void onAttachedToWindow() {
    }
}
