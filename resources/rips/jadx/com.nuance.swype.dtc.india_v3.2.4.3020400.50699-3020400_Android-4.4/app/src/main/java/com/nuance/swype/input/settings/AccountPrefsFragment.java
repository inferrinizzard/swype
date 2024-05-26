package com.nuance.swype.input.settings;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.v4.app.DialogFragment;
import com.nuance.connect.common.Strings;

/* loaded from: classes.dex */
public class AccountPrefsFragment extends ActionbarPreferenceFragment {
    protected AccountPrefs delegate;

    @Override // com.nuance.swype.input.settings.ActionbarPreferenceFragment, com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.delegate = new AccountPrefs(getActivity()) { // from class: com.nuance.swype.input.settings.AccountPrefsFragment.1
            @Override // com.nuance.swype.input.settings.AccountPrefs
            protected void doStartActivityForResult(Intent intent, int requestCode) {
                AccountPrefsFragment.this.startActivityForResult(intent, requestCode);
            }

            @Override // com.nuance.swype.input.settings.AccountPrefs
            protected PreferenceScreen addPreferences() {
                AccountPrefsFragment.this.addPreferencesFromResource(ACCOUNT_PREFS_XML);
                return AccountPrefsFragment.this.getPreferenceScreen();
            }

            @Override // com.nuance.swype.input.settings.AccountPrefs
            protected void showDeleteAccountDialog() {
                DeleteAccountDialog dialog = new DeleteAccountDialog();
                dialog.setTargetFragment(AccountPrefsFragment.this, 0);
                dialog.show(AccountPrefsFragment.this.getFragmentManager(), "delete_account");
            }

            @Override // com.nuance.swype.input.settings.AccountPrefs
            protected void showUnlinkDialog(String deviceId) {
                Bundle b = new Bundle();
                b.putString(Strings.DEFAULT_KEY, deviceId);
                UnlinkDeviceDialog dialog = new UnlinkDeviceDialog();
                dialog.setTargetFragment(AccountPrefsFragment.this, 0);
                dialog.setArguments(b);
                dialog.show(AccountPrefsFragment.this.getFragmentManager(), "unlink_device");
            }

            @Override // com.nuance.swype.input.settings.AccountPrefs
            protected void showConnectDialog() {
                DialogFragment dialog = new ConnectDialog();
                dialog.setTargetFragment(AccountPrefsFragment.this, 0);
                dialog.show(AccountPrefsFragment.this.getFragmentManager(), "connect");
            }
        };
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
        super.onStop();
        this.delegate.onStop();
    }

    @Override // com.android.support.v4.preference.PreferenceFragment, com.android.support.v4.preference.PreferenceManagerCompat.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {
        return this.delegate.onPreferenceTreeClick(prefScreen, pref) || super.onPreferenceTreeClick(prefScreen, pref);
    }

    /* loaded from: classes.dex */
    public static class DeleteAccountDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((AccountPrefsFragment) getTargetFragment()).delegate.createDeleteAccountDialog();
        }
    }

    /* loaded from: classes.dex */
    public static class UnlinkDeviceDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AccountPrefs delegate = ((AccountPrefsFragment) getTargetFragment()).delegate;
            String deviceId = getArguments().getString(Strings.DEFAULT_KEY);
            return delegate.createUnlinkDialog(deviceId);
        }
    }

    /* loaded from: classes.dex */
    public static class ConnectDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((AccountPrefsFragment) getTargetFragment()).delegate.createConnectDialog();
        }
    }

    @Override // com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.delegate.onActivityResult(requestCode, resultCode, data);
    }
}
