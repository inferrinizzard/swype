package com.nuance.swype.input.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.support.v4.app.DialogFragment;

/* loaded from: classes.dex */
public class SocialIntegrationPrefsFragment extends ActionbarPreferenceFragment {
    private SocialIntegrationPrefs delegate;

    @Override // com.nuance.swype.input.settings.ActionbarPreferenceFragment, com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.delegate = new SocialIntegrationPrefs(getActivity()) { // from class: com.nuance.swype.input.settings.SocialIntegrationPrefsFragment.1
            @Override // com.nuance.swype.input.settings.SocialIntegrationPrefs
            protected PreferenceScreen addPreferences() {
                SocialIntegrationPrefsFragment.this.addPreferencesFromResource(SocialIntegrationPrefs.SOCIAL_INTEGRATION_PREFS_XML);
                return SocialIntegrationPrefsFragment.this.getPreferenceScreen();
            }

            @Override // com.nuance.swype.input.settings.SocialIntegrationPrefs
            protected void showNoNetworkDialog() {
                DialogFragment dialog = new NoNetworkDialog();
                dialog.setTargetFragment(SocialIntegrationPrefsFragment.this, 0);
                dialog.show(SocialIntegrationPrefsFragment.this.getFragmentManager(), "nonetwork");
            }
        };
    }

    @Override // com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        this.delegate.onStart();
    }

    @Override // com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onStop() {
        super.onStop();
        this.delegate.onStop();
    }

    /* loaded from: classes.dex */
    public static class NoNetworkDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((SocialIntegrationPrefsFragment) getTargetFragment()).delegate.createNoNetworkDialog();
        }
    }
}
