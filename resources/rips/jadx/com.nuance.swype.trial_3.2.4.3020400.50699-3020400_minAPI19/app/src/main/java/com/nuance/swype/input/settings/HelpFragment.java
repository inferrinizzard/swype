package com.nuance.swype.input.settings;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

/* loaded from: classes.dex */
public class HelpFragment extends ActionbarPreferenceFragment {
    private Help delegate;

    @Override // com.nuance.swype.input.settings.ActionbarPreferenceFragment, com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(getActivity(), Help.HELP_XML, false);
        this.delegate = new Help(getActivity()) { // from class: com.nuance.swype.input.settings.HelpFragment.1
            @Override // com.nuance.swype.input.settings.Help
            protected PreferenceScreen addPreferences() {
                HelpFragment.this.addPreferencesFromResource(Help.HELP_XML);
                return HelpFragment.this.getPreferenceScreen();
            }
        };
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        this.delegate.onResume();
    }
}
