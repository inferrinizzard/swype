package com.nuance.swype.input.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.DialogFragment;

/* loaded from: classes.dex */
public class InputPrefsFragment extends ActionbarPreferenceFragment {
    private InputPrefs delegate;

    @Override // com.nuance.swype.input.settings.ActionbarPreferenceFragment, com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(InputPrefs.INPUT_PREFS_XML);
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle == null) {
            bundle = getArguments();
        }
        this.delegate = new InputPrefs(getPreferenceScreen(), bundle) { // from class: com.nuance.swype.input.settings.InputPrefsFragment.1
            @Override // android.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return false;
            }

            @Override // com.nuance.swype.input.settings.InputPrefs
            void doShowDialog(int id, Bundle args) {
                DialogFragment dialog = null;
                if (id == 11) {
                    dialog = new RecognitionSpeedDialog();
                } else if (id == 12) {
                    dialog = new StrokeWidthDialog();
                } else if (id == 13) {
                    dialog = new ColorPickerDialog();
                }
                if (dialog != null) {
                    dialog.setArguments(args);
                    dialog.setTargetFragment(InputPrefsFragment.this, 0);
                    dialog.show(InputPrefsFragment.this.getFragmentManager(), String.valueOf(id));
                }
            }
        };
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        this.delegate.onResume();
        if (isAdded()) {
            getActivity().setTitle(this.delegate.getInputModeName());
        }
    }

    /* loaded from: classes.dex */
    public static class RecognitionSpeedDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((InputPrefsFragment) getTargetFragment()).delegate.createRecognitionSpeedDialog();
        }
    }

    /* loaded from: classes.dex */
    public static class StrokeWidthDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((InputPrefsFragment) getTargetFragment()).delegate.createStrokeWidthDialog();
        }
    }

    /* loaded from: classes.dex */
    public static class ColorPickerDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((InputPrefsFragment) getTargetFragment()).delegate.createColorPickerDialog();
        }
    }
}
