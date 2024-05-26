package com.nuance.swype.input.settings;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.support.v4.app.DialogFragment;
import com.android.support.v4.preference.PreferenceFragment;

/* loaded from: classes.dex */
public class UpdatesFragment extends PreferenceFragment {
    private Updates delegate;

    @Override // com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.delegate = new Updates(getActivity()) { // from class: com.nuance.swype.input.settings.UpdatesFragment.1
            @Override // com.nuance.swype.input.settings.Updates
            protected PreferenceScreen addPreferences() {
                UpdatesFragment.this.addPreferencesFromResource(UPDATES_XML);
                return UpdatesFragment.this.getPreferenceScreen();
            }

            @Override // com.nuance.swype.input.settings.Updates
            protected void showConnectDialog() {
                DialogFragment dialog = new ConnectDialog();
                dialog.setTargetFragment(UpdatesFragment.this, 0);
                dialog.show(UpdatesFragment.this.getFragmentManager(), "dialog");
            }

            @Override // com.nuance.swype.input.settings.Updates
            protected void doStartActivityForResult(Intent intent, int requestCode) {
                UpdatesFragment.this.startActivityForResult(intent, requestCode);
            }

            @Override // com.nuance.swype.input.settings.Updates
            protected void showUnknownSourcesDialog() {
                DialogFragment dialog = new UnknownSourcesDialog();
                dialog.setTargetFragment(UpdatesFragment.this, 0);
                dialog.show(UpdatesFragment.this.getFragmentManager(), "dialog");
            }
        };
        setHasOptionsMenu(false);
    }

    @Override // com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        this.delegate.onStart();
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        this.delegate.onResume();
    }

    @Override // android.support.v4.app.Fragment
    public void onPause() {
        super.onPause();
        this.delegate.onPause();
    }

    @Override // com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onStop() {
        this.delegate.onStop();
        super.onStop();
    }

    @Override // com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.delegate.onActivityResult(requestCode, resultCode, data);
    }

    /* loaded from: classes.dex */
    public static class ConnectDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((UpdatesFragment) getTargetFragment()).delegate.createConnectDialog();
        }
    }

    /* loaded from: classes.dex */
    public static class UnknownSourcesDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((UpdatesFragment) getTargetFragment()).delegate.createUnknownSourcesDialog();
        }
    }
}
