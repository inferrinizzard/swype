package com.nuance.swype.input.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;

/* loaded from: classes.dex */
public class LanguagePrefsFragment extends ActionbarPreferenceFragment {
    private LanguagePrefs delegate;

    @Override // com.nuance.swype.input.settings.ActionbarPreferenceFragment, com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onActivityCreated(Bundle icicle) {
        super.onActivityCreated(icicle);
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getActivity()));
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle == null) {
            bundle = getArguments();
        }
        this.delegate = new LanguagePrefs(getPreferenceScreen(), bundle) { // from class: com.nuance.swype.input.settings.LanguagePrefsFragment.1
            @Override // com.nuance.swype.input.settings.LanguagePrefs
            protected Preference createInputModePref(Bundle args) {
                Context context = LanguagePrefsFragment.this.getActivity();
                Preference pref = new Preference(context);
                pref.setIntent(new Intent(context, (Class<?>) InputPrefsFragmentActivity.class).putExtras(args));
                return pref;
            }
        };
    }

    @Override // com.nuance.swype.input.settings.ActionbarPreferenceFragment, com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        this.delegate.buildLanguagesScreen();
    }
}
