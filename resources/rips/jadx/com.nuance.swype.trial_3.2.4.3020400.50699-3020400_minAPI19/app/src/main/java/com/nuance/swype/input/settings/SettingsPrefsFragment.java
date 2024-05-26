package com.nuance.swype.input.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.DialogFragment;

/* loaded from: classes.dex */
public class SettingsPrefsFragment extends ActionbarPreferenceFragment {
    private SettingsPrefs delegate;

    @Override // com.nuance.swype.input.settings.ActionbarPreferenceFragment, com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(getActivity(), SettingsPrefs.PREFERENCES_XML, false);
        this.delegate = new SettingsPrefs(getActivity()) { // from class: com.nuance.swype.input.settings.SettingsPrefsFragment.1
            @Override // com.nuance.swype.input.settings.SettingsPrefs
            protected PreferenceScreen addPreferences() {
                SettingsPrefsFragment.this.addPreferencesFromResource(SettingsPrefs.PREFERENCES_XML);
                return SettingsPrefsFragment.this.getPreferenceScreen();
            }

            @Override // com.nuance.swype.input.settings.SettingsPrefs
            protected void showKeyboardHeightDialog(Bundle args) {
                DialogFragment dialog = new KeyboardHeightDialog();
                dialog.setTargetFragment(SettingsPrefsFragment.this, 0);
                dialog.setArguments(args);
                dialog.show(SettingsPrefsFragment.this.getFragmentManager(), "dialog");
            }

            @Override // com.nuance.swype.input.settings.SettingsPrefs
            protected void showCandidateSizeDialog(Bundle args) {
                DialogFragment dialog = new CandidateSizeDialog();
                dialog.setTargetFragment(SettingsPrefsFragment.this, 0);
                dialog.setArguments(args);
                dialog.show(SettingsPrefsFragment.this.getFragmentManager(), "dialog");
            }

            @Override // com.nuance.swype.input.settings.SettingsPrefs
            protected void showEmojiSkinToneDialog(Bundle args) {
                DialogFragment dialog = new SkinToneDialog();
                dialog.setTargetFragment(SettingsPrefsFragment.this, 0);
                dialog.setArguments(args);
                dialog.show(SettingsPrefsFragment.this.getFragmentManager(), "dialog");
            }

            @Override // com.nuance.swype.input.settings.SettingsPrefs
            protected Preference createInputLanguagePref(Bundle args) {
                Context context = SettingsPrefsFragment.this.getActivity();
                Preference pref = new Preference(context);
                pref.setIntent(new Intent(context, (Class<?>) LanguagePrefsFragmentActivity.class).putExtras(args));
                return pref;
            }

            @Override // com.nuance.swype.input.settings.SettingsPrefs
            void doShowDialog(int id, Bundle args) {
                DialogFragment dialog = null;
                if (id == 9) {
                    dialog = new VibrationDurationDialog();
                } else if (id == 10) {
                    dialog = new LongPressDelayDialog();
                } else if (id == 11) {
                    dialog = new RecognitionSpeedDialog();
                }
                if (dialog != null) {
                    dialog.setArguments(args);
                    dialog.setTargetFragment(SettingsPrefsFragment.this, 0);
                    dialog.show(SettingsPrefsFragment.this.getFragmentManager(), String.valueOf(id));
                }
            }

            @Override // com.nuance.swype.input.settings.SettingsPrefs
            void requestPermissions(String[] permissions, int requestCode) {
                if (Build.VERSION.SDK_INT >= 23) {
                    SettingsPrefsFragment.this.requestPermissions(permissions, requestCode);
                }
            }
        };
        this.delegate.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        this.delegate.onResume();
    }

    @Override // com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.delegate.onSaveInstanceState(bundle);
    }

    /* loaded from: classes.dex */
    public static class VibrationDurationDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((SettingsPrefsFragment) getTargetFragment()).delegate.createVibrationDurationDialog();
        }
    }

    /* loaded from: classes.dex */
    public static class LongPressDelayDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((SettingsPrefsFragment) getTargetFragment()).delegate.createLongPressDurationDialog();
        }
    }

    /* loaded from: classes.dex */
    public static class RecognitionSpeedDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((SettingsPrefsFragment) getTargetFragment()).delegate.createRecognitionSpeedDialog();
        }
    }

    /* loaded from: classes.dex */
    public static class KeyboardHeightDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((SettingsPrefsFragment) getTargetFragment()).delegate.createKeyboardHeightDialog(getArguments());
        }
    }

    /* loaded from: classes.dex */
    public static class CandidateSizeDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((SettingsPrefsFragment) getTargetFragment()).delegate.createCandidateSizeDialog(getArguments());
        }
    }

    /* loaded from: classes.dex */
    public static class SkinToneDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((SettingsPrefsFragment) getTargetFragment()).delegate.createSkinToneDialog(getArguments());
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        this.delegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
}
