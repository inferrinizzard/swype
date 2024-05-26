package com.nuance.swype.input.settings;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.stats.StatisticsManager;
import java.util.List;

/* loaded from: classes.dex */
public abstract class LanguagePrefs {
    protected static final String LANGUAGE_ID_KEY = "language_id";
    private final Context context;
    private final String languageId;
    private final Preference.OnPreferenceChangeListener savePortratInputPanel = new Preference.OnPreferenceChangeListener() { // from class: com.nuance.swype.input.settings.LanguagePrefs.1
        @Override // android.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (!(preference instanceof ListPreference)) {
                return false;
            }
            ListPreference list = (ListPreference) preference;
            AppPreferences.from(LanguagePrefs.this.context).setString(list.getKey(), newValue.toString());
            StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(LanguagePrefs.this.context);
            if (sessionScribe != null) {
                sessionScribe.recordSettingsChange(list.getKey(), newValue.toString(), list.getValue());
            }
            return true;
        }
    };
    private final PreferenceScreen screen;

    protected abstract Preference createInputModePref(Bundle bundle);

    public static Bundle createArgs(InputMethods.Language language) {
        Bundle args = new Bundle();
        args.putString(LANGUAGE_ID_KEY, language.getLanguageId());
        return args;
    }

    public LanguagePrefs(PreferenceScreen screen, Bundle args) {
        this.context = screen.getContext();
        this.screen = screen;
        this.languageId = args.getString(LANGUAGE_ID_KEY);
        if (TextUtils.isEmpty(this.languageId)) {
            throw new IllegalArgumentException(args.keySet().toString());
        }
    }

    public void buildLanguagesScreen() {
        InputMethods.Language language = InputMethods.from(this.context).findInputLanguage(this.languageId);
        if (language == null) {
            throw new IllegalStateException(String.format("Can't find %s in available input languages", this.languageId));
        }
        this.screen.setTitle(language.getDisplayName());
        this.screen.removeAll();
        UserPreferences userPrefs = UserPreferences.from(this.context);
        if (language.isKoreanLanguage()) {
            PreferenceCategory cat = new PreferenceCategory(this.context);
            cat.setTitle(language.getDisplayName());
            this.screen.addPreference(cat);
            List<InputMethods.InputMode> inputModes = language.getKoreanInputModes();
            if (inputModes != null) {
                InputMethods.InputMode currentInputMode = language.getCurrentInputMode();
                for (InputMethods.InputMode inputMode : inputModes) {
                    if (!inputMode.isHandwriting() || userPrefs.isHandwritingEnabled()) {
                        Preference pref = createInputModePref(InputPrefs.createArgs(inputMode));
                        pref.setKey(inputMode.getEnabledPrefKey());
                        pref.setTitle(inputMode.getDisplayInputMode());
                        if (currentInputMode.equals(inputMode)) {
                            pref.setSummary(R.string.current_use_input_mode);
                        }
                        cat.addPreference(pref);
                    }
                }
                return;
            }
            return;
        }
        if (language.isJapaneseLanguage()) {
            PreferenceCategory cat2 = new PreferenceCategory(this.context);
            cat2.setTitle(language.getDisplayName());
            this.screen.addPreference(cat2);
            addPortraitLayoutSettings(cat2);
            if (userPrefs.isHandwritingEnabled()) {
                addJapaneseHandWritingSettings(cat2);
            }
        }
    }

    private void addJapaneseHandWritingSettings(PreferenceCategory cat) {
        InputMethods.InputMode currentInputMode = InputMethods.from(this.context).getCurrentInputLanguage().getHandwritingMode();
        Preference pref = createInputModePref(InputPrefs.createArgs(currentInputMode));
        pref.setKey(currentInputMode.getEnabledPrefKey());
        pref.setTitle(R.string.handwriting);
        cat.addPreference(pref);
    }

    private void addPortraitLayoutSettings(PreferenceCategory cat) {
        InputMethods.Language currentInputLanguage = InputMethods.from(this.context).getCurrentInputLanguage();
        if (currentInputLanguage.isJapaneseLanguage()) {
            InputMethods.InputMode defaultInputMode = currentInputLanguage.getInputMode(currentInputLanguage.mDefaultInputMode);
            ListPreference list = createListPref(cat, defaultInputMode.getPortaitLayoutOptionsPrefKey(), String.valueOf(defaultInputMode.getDefaultPortraitLayoutOptions()));
            list.setTitle(R.string.portrait_input_panel);
            list.setSummary(R.string.portrait_input_panel_summary);
            list.setDialogIcon(R.drawable.swype_logo);
            list.setDialogTitle(R.string.portrait_input_panel);
            list.setEntries(R.array.entries_japanese_keyboard_portrait_options);
            list.setEntryValues(R.array.entryValues_japanese_keyboard_portrait_options);
            list.setValue(String.valueOf(defaultInputMode.getDefaultPortraitLayoutOptions()));
        }
    }

    private ListPreference createListPref(PreferenceCategory cat, String key, Object defaultValue) {
        ListPreference list = (ListPreference) cat.findPreference(key);
        if (list == null) {
            ListPreference list2 = new ListPreference(this.context);
            list2.setKey(key);
            list2.setDefaultValue(defaultValue);
            list2.setOnPreferenceChangeListener(this.savePortratInputPanel);
            cat.addPreference(list2);
            return list2;
        }
        return list;
    }
}
