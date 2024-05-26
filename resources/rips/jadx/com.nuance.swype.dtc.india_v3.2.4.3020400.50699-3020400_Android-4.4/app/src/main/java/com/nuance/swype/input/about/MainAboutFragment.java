package com.nuance.swype.input.about;

import android.annotation.TargetApi;
import android.os.Bundle;
import com.nuance.swype.input.settings.ActionbarPreferenceFragment;

@TargetApi(11)
/* loaded from: classes.dex */
public class MainAboutFragment extends ActionbarPreferenceFragment {
    @Override // com.nuance.swype.input.settings.ActionbarPreferenceFragment, com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(AboutBuilders.ABOUT_PREFS_XML);
        AboutBuilders.fixupAboutPreferences(getPreferenceScreen());
    }
}
