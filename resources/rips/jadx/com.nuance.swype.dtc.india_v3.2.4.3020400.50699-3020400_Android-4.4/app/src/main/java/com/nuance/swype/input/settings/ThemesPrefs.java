package com.nuance.swype.input.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.preference.ViewClickPreference;
import com.nuance.swype.stats.StatisticsManager;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.AdsUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public abstract class ThemesPrefs implements Preference.OnPreferenceChangeListener {
    public static final String CANDIDATES_SIZE = "Candidates_Size";
    private static final String CANDIDATES_SIZES_SETTINGS_KEY = "candidates_size_setting_preference";
    private static final int CANDIDATE_SIZE_BAR_MAX = 10;
    private static final int CANDIDATE_SIZE_BAR_OFFSET = 100;
    private static final int KEYBOARD_HEIGHT_BAR_MAX = 4;
    private static final int KEYBOARD_HEIGHT_BAR_OFFSET = 8;
    private static final String KEYBOARD_HEIGHT_SETTINGS_KEY = "keyboard_height_settings";
    private static final String LANDSCAPE_DOCKING_MODE_KEY = "landscape_docking_modes";
    private static final String ORIENTATION_BUNDLE_KEY = "orientation_bundle";
    private static final String PORTRAIT_DOCKING_MODE_KEY = "portrait_docking_modes";
    public static final int PREFERENCES_XML = R.xml.themespreferences;
    private static final float QVGA_DEVICE = 0.8f;
    private final Activity activity;
    private float mValue;
    private float mValueKeyboardHeightLandscape;
    private float mValueKeyboardHeightPortrait;
    private PreferenceScreen screen;
    private final List<KeyboardEx.KeyboardDockMode> validPortraitDockModes = getValidDockModes(1);
    private final List<KeyboardEx.KeyboardDockMode> validLandscapeDockModes = getValidDockModes(2);

    protected abstract PreferenceScreen addPreferences();

    protected abstract void showCandidateSizeDialog(Bundle bundle);

    protected abstract void showKeyboardHeightDialog(Bundle bundle);

    protected abstract void showKeyboardModesDialog(Bundle bundle);

    public ThemesPrefs(Activity activity) {
        this.activity = activity;
        UsageData.recordScreenVisited(UsageData.Screen.THEMES_OPTIONS);
    }

    public void onResume() {
        rebuildSettings();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Context getContext() {
        return this.activity;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshDockModes() {
        refreshDockModes(PORTRAIT_DOCKING_MODE_KEY, this.validPortraitDockModes, 1);
        refreshDockModes(LANDSCAPE_DOCKING_MODE_KEY, this.validLandscapeDockModes, 2);
    }

    private void rebuildSettings() {
        if (this.screen != null) {
            this.screen.removeAll();
        }
        this.screen = addPreferences();
        Preference candidateSizePrefs = this.screen.findPreference(CANDIDATES_SIZES_SETTINGS_KEY);
        if (candidateSizePrefs != null) {
            if (!UserPreferences.from(getContext()).isShowWordChoiceSizePrefEnabled()) {
                this.screen.removePreference(candidateSizePrefs);
            } else if (candidateSizePrefs instanceof ViewClickPreference) {
                ((ViewClickPreference) candidateSizePrefs).setListener(new ViewClickPreference.ViewClickPreferenceListener() { // from class: com.nuance.swype.input.settings.ThemesPrefs.1
                    @Override // com.nuance.swype.preference.ViewClickPreference.ViewClickPreferenceListener
                    public void onViewClick(Preference pref) {
                        Bundle args = new Bundle();
                        ThemesPrefs.this.showCandidateSizeDialog(args);
                    }
                });
            } else {
                candidateSizePrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.ThemesPrefs.2
                    @Override // android.preference.Preference.OnPreferenceClickListener
                    public boolean onPreferenceClick(Preference preference) {
                        Bundle args = new Bundle();
                        ThemesPrefs.this.showCandidateSizeDialog(args);
                        return true;
                    }
                });
            }
        }
        Preference keyboardHeightPrefs = this.screen.findPreference(KEYBOARD_HEIGHT_SETTINGS_KEY);
        if (keyboardHeightPrefs != null) {
            if (!UserPreferences.from(getContext()).isShowKeyboardHeightPrefEnabled()) {
                this.screen.removePreference(keyboardHeightPrefs);
            } else if (keyboardHeightPrefs instanceof ViewClickPreference) {
                ((ViewClickPreference) keyboardHeightPrefs).setListener(new ViewClickPreference.ViewClickPreferenceListener() { // from class: com.nuance.swype.input.settings.ThemesPrefs.3
                    @Override // com.nuance.swype.preference.ViewClickPreference.ViewClickPreferenceListener
                    public void onViewClick(Preference pref) {
                        Bundle args = new Bundle();
                        ThemesPrefs.this.showKeyboardHeightDialog(args);
                    }
                });
            } else {
                keyboardHeightPrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.ThemesPrefs.4
                    @Override // android.preference.Preference.OnPreferenceClickListener
                    public boolean onPreferenceClick(Preference preference) {
                        Bundle args = new Bundle();
                        ThemesPrefs.this.showKeyboardHeightDialog(args);
                        return true;
                    }
                });
            }
        }
        refreshDockModes();
        removeNonAccessibleSettings();
        setupPreferenceHandler(this.screen);
    }

    private void removeNonAccessibleSettings() {
        if (IMEApplication.from(getContext()).getIME() != null && IMEApplication.from(getContext()).getIME().isAccessibilitySupportEnabled()) {
            Preference portraitDockingMode = this.screen.findPreference(PORTRAIT_DOCKING_MODE_KEY);
            if (portraitDockingMode != null) {
                this.screen.removePreference(portraitDockingMode);
            }
            Preference landscapeDockingMode = this.screen.findPreference(LANDSCAPE_DOCKING_MODE_KEY);
            if (landscapeDockingMode != null) {
                this.screen.removePreference(landscapeDockingMode);
            }
        }
    }

    private void refreshDockModes(String prefKey, List<KeyboardEx.KeyboardDockMode> validDockModes, final int config) {
        Preference dockPref = this.screen.findPreference(prefKey);
        if (dockPref != null) {
            if (validDockModes.size() <= 1) {
                this.screen.removePreference(dockPref);
            } else {
                dockPref.setSummary(UserPreferences.from(this.activity).getKeyboardDockingMode(config).getName(this.activity.getResources()));
                dockPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.ThemesPrefs.5
                    @Override // android.preference.Preference.OnPreferenceClickListener
                    public boolean onPreferenceClick(Preference preference) {
                        Bundle args = new Bundle();
                        args.putInt(ThemesPrefs.ORIENTATION_BUNDLE_KEY, config);
                        ThemesPrefs.this.showKeyboardModesDialog(args);
                        return true;
                    }
                });
            }
        }
    }

    @Override // android.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        IME currentIME;
        IMEApplication imeApp = IMEApplication.from(getContext());
        if (imeApp != null && (currentIME = imeApp.getIME()) != null) {
            currentIME.clearSavedKeyboardState();
            StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(getContext());
            if (sessionScribe != null) {
                boolean value = ((Boolean) newValue).booleanValue();
                sessionScribe.recordSettingsChange(preference.getKey(), Boolean.valueOf(value), Boolean.valueOf(!value));
            }
        }
        return true;
    }

    private void setupPreferenceHandler(Preference preference) {
        if (preference instanceof PreferenceGroup) {
            PreferenceGroup group = (PreferenceGroup) preference;
            int count = group.getPreferenceCount();
            for (int i = 0; i < count; i++) {
                setupPreferenceHandler(group.getPreference(i));
            }
            return;
        }
        preference.setOnPreferenceChangeListener(this);
    }

    private List<KeyboardEx.KeyboardDockMode> getValidDockModes(int orientation) {
        List<KeyboardEx.KeyboardDockMode> validDockModes = new ArrayList<>();
        Resources res = this.activity.getResources();
        for (KeyboardEx.KeyboardDockMode mode : KeyboardEx.KeyboardDockMode.values()) {
            if (mode.isEnabled(res, orientation)) {
                validDockModes.add(mode);
            }
        }
        return validDockModes;
    }

    protected Dialog createKeyboardModeDialog(Bundle args) {
        final int orientation = args.getInt(ORIENTATION_BUNDLE_KEY);
        final List<KeyboardEx.KeyboardDockMode> validDockModes = orientation == 1 ? this.validPortraitDockModes : this.validLandscapeDockModes;
        List<String> modeNames = new ArrayList<>();
        int currentModeIndex = 0;
        final KeyboardEx.KeyboardDockMode currentMode = UserPreferences.from(this.activity).getKeyboardDockingMode(orientation);
        int modeCount = validDockModes.size();
        for (int i = 0; i < modeCount; i++) {
            KeyboardEx.KeyboardDockMode mode = validDockModes.get(i);
            modeNames.add(mode.getName(this.activity.getResources()));
            if (mode == currentMode) {
                currentModeIndex = i;
            }
        }
        return new AlertDialog.Builder(this.activity).setIcon(R.drawable.swype_logo).setTitle(orientation == 1 ? R.string.kb_layout_portrait_title : R.string.kb_layout_landscape_title).setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null).setSingleChoiceItems((CharSequence[]) modeNames.toArray(new String[modeNames.size()]), currentModeIndex, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.ThemesPrefs.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                UserPreferences.from(ThemesPrefs.this.activity).setKeyboardDockingMode((KeyboardEx.KeyboardDockMode) validDockModes.get(which), orientation);
                ThemesPrefs.this.refreshDockModes();
                dialog.dismiss();
                StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(ThemesPrefs.this.activity);
                if (sessionScribe != null) {
                    sessionScribe.recordSettingsChange(orientation == 1 ? "Portrait keyboard" : "Landscape keyboard", ((KeyboardEx.KeyboardDockMode) validDockModes.get(which)).getName(ThemesPrefs.this.activity.getResources()), currentMode.getName(ThemesPrefs.this.activity.getResources()));
                }
            }
        }).create();
    }

    protected Dialog createKeyboardHeightDialog(Bundle args) {
        DisplayMetrics display = IMEApplication.from(getContext()).getDisplay();
        ViewGroup scrollView = (ViewGroup) ((LayoutInflater) this.activity.getSystemService("layout_inflater")).inflate(R.layout.keyboard_height_dialog, (ViewGroup) null);
        final TextView valueTextPortrait = (TextView) scrollView.findViewById(R.id.valueTextPortrait);
        UserPreferences userPrefs = IMEApplication.from(getContext()).getUserPreferences();
        this.mValueKeyboardHeightPortrait = userPrefs.getKeyboardScalePortrait();
        valueTextPortrait.setText(String.format(getContext().getResources().getString(R.string.percent), Integer.valueOf((int) (this.mValueKeyboardHeightPortrait * 100.0f))));
        SeekBar seekBarPortrait = (SeekBar) scrollView.findViewById(R.id.portrait_seekbar);
        if (display.density < 0.8f) {
            seekBarPortrait.setPadding((int) getContext().getResources().getDimension(R.dimen.seek_bar_padding), 0, (int) getContext().getResources().getDimension(R.dimen.seek_bar_padding), 0);
        }
        seekBarPortrait.setMax(4);
        seekBarPortrait.incrementProgressBy(1);
        seekBarPortrait.setProgress(((int) (this.mValueKeyboardHeightPortrait * 10.0f)) - 8);
        seekBarPortrait.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.nuance.swype.input.settings.ThemesPrefs.7
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ThemesPrefs.this.mValueKeyboardHeightPortrait = (progress + 8) / 10.0f;
                valueTextPortrait.setText(String.format(ThemesPrefs.this.getContext().getResources().getString(R.string.percent), Integer.valueOf((progress + 8) * 10)));
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        final TextView valueTextLandscape = (TextView) scrollView.findViewById(R.id.valueTextLandscape);
        this.mValueKeyboardHeightLandscape = userPrefs.getKeyboardScaleLandscape();
        valueTextLandscape.setText(String.format(getContext().getResources().getString(R.string.percent), Integer.valueOf((int) (this.mValueKeyboardHeightLandscape * 100.0f))));
        SeekBar seekBarLandscape = (SeekBar) scrollView.findViewById(R.id.landscape_seekbar);
        if (display.density < 0.8f) {
            seekBarLandscape.setPadding((int) getContext().getResources().getDimension(R.dimen.seek_bar_padding), 0, (int) getContext().getResources().getDimension(R.dimen.seek_bar_padding), 0);
        }
        seekBarLandscape.setMax(4);
        seekBarLandscape.incrementProgressBy(1);
        seekBarLandscape.setProgress(((int) (this.mValueKeyboardHeightLandscape * 10.0f)) - 8);
        seekBarLandscape.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.nuance.swype.input.settings.ThemesPrefs.8
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ThemesPrefs.this.mValueKeyboardHeightLandscape = (progress + 8) / 10.0f;
                valueTextLandscape.setText(String.format(ThemesPrefs.this.getContext().getResources().getString(R.string.percent), Integer.valueOf((progress + 8) * 10)));
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity).setIcon(R.drawable.swype_logo).setTitle(R.string.pref_kb_height_title).setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.ThemesPrefs.9
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(ThemesPrefs.this.getContext());
                UserPreferences userPrefs2 = IMEApplication.from(ThemesPrefs.this.getContext()).getUserPreferences();
                if (sessionScribe != null) {
                    sessionScribe.recordSettingsChange("Keyboard Height", "Portrait:" + (ThemesPrefs.this.mValueKeyboardHeightPortrait / 10.0f) + " Landscape:" + (ThemesPrefs.this.mValueKeyboardHeightLandscape / 10.0f), "Portrait:" + userPrefs2.getKeyboardScalePortrait() + " Landscape:" + userPrefs2.getKeyboardScaleLandscape());
                }
                IME ime = IMEApplication.from(ThemesPrefs.this.getContext()).getIME();
                if (ime != null) {
                    ime.getKeyboardBackgroundManager().setReloadRequiredFromResources(true);
                }
                userPrefs2.setKeyboardScalePortrait(ThemesPrefs.this.mValueKeyboardHeightPortrait);
                userPrefs2.setKeyboardScaleLandscape(ThemesPrefs.this.mValueKeyboardHeightLandscape);
                if (AdsUtil.sAdsSupported) {
                    IMEApplication.from(ThemesPrefs.this.getContext()).getAdSessionTracker().setKeyboardHeight(ThemesPrefs.this.mValueKeyboardHeightPortrait);
                }
            }
        });
        builder.setView(scrollView);
        return builder.create();
    }

    protected Dialog createCandidateSizeDialog(Bundle args) {
        DisplayMetrics display = IMEApplication.from(getContext()).getDisplay();
        LayoutInflater systemInflater = (LayoutInflater) this.activity.getSystemService("layout_inflater");
        ViewGroup scrollView = (ViewGroup) systemInflater.inflate(R.layout.candidate_size_dialog, (ViewGroup) null);
        View candidateView = IMEApplication.from(getContext()).getThemedLayoutInflater(systemInflater).inflate(R.layout.candidate_size_setting_view, (ViewGroup) null);
        candidateView.setBackgroundDrawable(IMEApplication.from(getContext()).getThemedDrawable(R.attr.keyboardSuggestStrip));
        ((ViewGroup) scrollView.findViewById(R.id.candidate_size_group)).addView(candidateView, 0, new LinearLayout.LayoutParams(-1, (int) getContext().getResources().getDimension(R.dimen.candidates_list_height)));
        UserPreferences sp = IMEApplication.from(getContext()).getUserPreferences();
        final String displayText = getContext().getString(R.string.swype);
        final CandidateSizeSettingView cs = (CandidateSizeSettingView) candidateView;
        this.mValue = getCandidatesSize(sp, "Candidates_Size", 1.0f);
        cs.setTextSize(this.mValue);
        cs.setDisplayText(displayText);
        SeekBar sizeSeekBar = (SeekBar) scrollView.findViewById(R.id.size_seekbar);
        if (display.density < 0.8f) {
            sizeSeekBar.setPadding((int) getContext().getResources().getDimension(R.dimen.seek_bar_padding), 0, (int) getContext().getResources().getDimension(R.dimen.seek_bar_padding), 0);
        }
        sizeSeekBar.setMax(10);
        sizeSeekBar.incrementProgressBy(1);
        sizeSeekBar.setProgress((int) (((this.mValue * 100.0f) - 100.0f) / 3.0f));
        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.nuance.swype.input.settings.ThemesPrefs.10
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ThemesPrefs.this.mValue = 1.0f + (progress * 0.03f);
                cs.setTextSize(ThemesPrefs.this.mValue);
                cs.setDisplayText(displayText);
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity).setIcon(R.drawable.swype_logo).setTitle(R.string.pref_word_choice_title).setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.ThemesPrefs.11
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(ThemesPrefs.this.getContext());
                if (sessionScribe != null) {
                    float defaultValue = IMEApplication.from(ThemesPrefs.this.getContext()).getUserPreferences().getFloat("Candidates_Size", 1.0f);
                    sessionScribe.recordSettingsChange("Word Choice Size", Float.valueOf(ThemesPrefs.this.mValue), Float.valueOf(defaultValue));
                }
                ThemesPrefs.this.saveCandidatesSize("Candidates_Size", ThemesPrefs.this.mValue);
            }
        });
        builder.setView(scrollView);
        return builder.create();
    }

    public static float getCandidatesSize(UserPreferences sp, String key, float defValue) {
        return sp.getFloat(key, defValue);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveCandidatesSize(String key, float size) {
        IMEApplication.from(getContext()).getUserPreferences().setFloat(key, size);
    }
}
