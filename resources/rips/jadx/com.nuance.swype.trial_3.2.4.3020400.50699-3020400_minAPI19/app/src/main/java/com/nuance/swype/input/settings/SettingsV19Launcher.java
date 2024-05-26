package com.nuance.swype.input.settings;

import android.annotation.TargetApi;
import android.support.v4.app.FragmentActivity;

@TargetApi(19)
/* loaded from: classes.dex */
public class SettingsV19Launcher extends SettingsV11Launcher {
    @Override // com.nuance.swype.input.settings.SettingsV11Launcher
    protected Class<? extends FragmentActivity> getSettingsClass() {
        return SettingsV19.class;
    }
}
