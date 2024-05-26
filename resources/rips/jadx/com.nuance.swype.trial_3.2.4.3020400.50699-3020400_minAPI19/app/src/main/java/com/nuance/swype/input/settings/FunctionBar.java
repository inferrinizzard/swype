package com.nuance.swype.input.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.stats.StatisticsManager;

/* loaded from: classes.dex */
public abstract class FunctionBar {
    private static final String ADDONDICTIONARIES_CATEGORY = "addondictionaries";
    private static final String CHINESESETTINGS_CATEGORY = "chinesesettings";
    private static final String EDITKEYBOARD_CATEGORY = "edit_layer";
    private static final String EMOJISETTING_CATEGORY = "function_bar_emoji";
    private static final String FUNCTION_BAR_CATEGORY = "functionitem_category";
    private static final String INPUTMODE_CATEGORY = "inputmode";
    private static final String LANGUAGE_OPTION_CATEGORY = "language_option";
    private static final int MAX_FUNCTION_ITEM = 4;
    private static final String NUMBERKEYBOARD_CATEGORY = "number_keyboard";
    private static final String QUICKTOGGLE_CATEGORY = "quicktoggle";
    private static final String SETTINGS_CATEGORY = "settings";
    private static final String THEMES_CATEGORY = "themes";
    private final Activity activity;
    private final PreferenceScreen screen;

    protected abstract void showMaxCountdialog();

    public FunctionBar(Activity activity, PreferenceScreen screen) {
        this.activity = activity;
        this.screen = screen;
    }

    public void onResume() {
        buildFunctionBarScreen();
    }

    private void buildFunctionBarScreen() {
        InputMethods.from(this.activity).syncWithCurrentUserConfiguration();
        BuildInfo buildInfo = BuildInfo.from(this.activity.getApplicationContext());
        CheckBoxPreference addOnDictionaries = (CheckBoxPreference) this.screen.findPreference("addondictionaries");
        if (!buildInfo.isConnectEnabled() && addOnDictionaries != null) {
            addOnDictionaries.setChecked(false);
            addOnDictionaries.setEnabled(false);
            ((PreferenceCategory) this.screen.findPreference(FUNCTION_BAR_CATEGORY)).removePreference(addOnDictionaries);
        }
        setFuctionItemClickEvent();
        getFunctionItemSelectedCount();
    }

    private void setFuctionItemClickEvent() {
        final CheckBoxPreference settings = (CheckBoxPreference) this.screen.findPreference("settings");
        if (settings != null) {
            settings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.FunctionBar.1
                @Override // android.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    if (FunctionBar.this.getFunctionItemSelectedCount() > 4) {
                        settings.setChecked(false);
                        FunctionBar.this.showMaxCountdialog();
                    }
                    StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(FunctionBar.this.activity);
                    if (sessionScribe != null) {
                        boolean checked = ((CheckBoxPreference) preference).isChecked();
                        sessionScribe.recordSettingsChange("settings", Boolean.valueOf(checked), Boolean.valueOf(checked ? false : true));
                    }
                    return true;
                }
            });
        }
        final CheckBoxPreference languageOption = (CheckBoxPreference) this.screen.findPreference("language_option");
        if (languageOption != null) {
            languageOption.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.FunctionBar.2
                @Override // android.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    if (FunctionBar.this.getFunctionItemSelectedCount() > 4) {
                        languageOption.setChecked(false);
                        FunctionBar.this.showMaxCountdialog();
                    }
                    StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(FunctionBar.this.activity);
                    if (sessionScribe != null) {
                        boolean checked = ((CheckBoxPreference) preference).isChecked();
                        sessionScribe.recordSettingsChange("language_option", Boolean.valueOf(checked), Boolean.valueOf(checked ? false : true));
                    }
                    return true;
                }
            });
        }
        final CheckBoxPreference inputMode = (CheckBoxPreference) this.screen.findPreference("inputmode");
        if (inputMode != null) {
            inputMode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.FunctionBar.3
                @Override // android.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    if (FunctionBar.this.getFunctionItemSelectedCount() > 4) {
                        inputMode.setChecked(false);
                        FunctionBar.this.showMaxCountdialog();
                    }
                    StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(FunctionBar.this.activity);
                    if (sessionScribe != null) {
                        boolean checked = ((CheckBoxPreference) preference).isChecked();
                        sessionScribe.recordSettingsChange("inputmode", Boolean.valueOf(checked), Boolean.valueOf(checked ? false : true));
                    }
                    return true;
                }
            });
        }
        final CheckBoxPreference qucikToggle = (CheckBoxPreference) this.screen.findPreference("quicktoggle");
        if (UserPreferences.from(this.activity).isHandwritingEnabled()) {
            if (qucikToggle != null) {
                qucikToggle.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.FunctionBar.4
                    @Override // android.preference.Preference.OnPreferenceClickListener
                    public boolean onPreferenceClick(Preference preference) {
                        if (FunctionBar.this.getFunctionItemSelectedCount() > 4) {
                            qucikToggle.setChecked(false);
                            FunctionBar.this.showMaxCountdialog();
                        }
                        StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(FunctionBar.this.activity);
                        if (sessionScribe != null) {
                            boolean checked = ((CheckBoxPreference) preference).isChecked();
                            sessionScribe.recordSettingsChange("quicktoggle", Boolean.valueOf(checked), Boolean.valueOf(checked ? false : true));
                        }
                        return true;
                    }
                });
            }
        } else {
            PreferenceCategory category = (PreferenceCategory) this.screen.findPreference(FUNCTION_BAR_CATEGORY);
            if (category != null && qucikToggle != null) {
                category.removePreference(qucikToggle);
            }
        }
        final CheckBoxPreference numberKeyboard = (CheckBoxPreference) this.screen.findPreference("number_keyboard");
        if (numberKeyboard != null) {
            numberKeyboard.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.FunctionBar.5
                @Override // android.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    if (FunctionBar.this.getFunctionItemSelectedCount() > 4) {
                        numberKeyboard.setChecked(false);
                        FunctionBar.this.showMaxCountdialog();
                    }
                    StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(FunctionBar.this.activity);
                    if (sessionScribe != null) {
                        boolean checked = ((CheckBoxPreference) preference).isChecked();
                        sessionScribe.recordSettingsChange("number_keyboard", Boolean.valueOf(checked), Boolean.valueOf(checked ? false : true));
                    }
                    return true;
                }
            });
        }
        final CheckBoxPreference editKeyboard = (CheckBoxPreference) this.screen.findPreference("edit_layer");
        if (editKeyboard != null) {
            editKeyboard.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.FunctionBar.6
                @Override // android.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    if (FunctionBar.this.getFunctionItemSelectedCount() > 4) {
                        editKeyboard.setChecked(false);
                        FunctionBar.this.showMaxCountdialog();
                    }
                    StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(FunctionBar.this.activity);
                    if (sessionScribe != null) {
                        boolean checked = ((CheckBoxPreference) preference).isChecked();
                        sessionScribe.recordSettingsChange("edit_layer", Boolean.valueOf(checked), Boolean.valueOf(checked ? false : true));
                    }
                    return true;
                }
            });
        }
        final CheckBoxPreference themes = (CheckBoxPreference) this.screen.findPreference("themes");
        if (themes != null) {
            themes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.FunctionBar.7
                @Override // android.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    if (FunctionBar.this.getFunctionItemSelectedCount() > 4) {
                        themes.setChecked(false);
                        FunctionBar.this.showMaxCountdialog();
                    }
                    StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(FunctionBar.this.activity);
                    if (sessionScribe != null) {
                        boolean checked = ((CheckBoxPreference) preference).isChecked();
                        sessionScribe.recordSettingsChange("themes", Boolean.valueOf(checked), Boolean.valueOf(checked ? false : true));
                    }
                    return true;
                }
            });
        }
        final CheckBoxPreference emojiOnFunctionBar = (CheckBoxPreference) this.screen.findPreference("function_bar_emoji");
        if (emojiOnFunctionBar != null) {
            emojiOnFunctionBar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.FunctionBar.8
                @Override // android.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    if (FunctionBar.this.getFunctionItemSelectedCount() > 4) {
                        emojiOnFunctionBar.setChecked(false);
                        FunctionBar.this.showMaxCountdialog();
                    }
                    StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(FunctionBar.this.activity);
                    if (sessionScribe != null) {
                        boolean checked = ((CheckBoxPreference) preference).isChecked();
                        sessionScribe.recordSettingsChange("function_bar_emoji", Boolean.valueOf(checked), Boolean.valueOf(checked ? false : true));
                    }
                    return true;
                }
            });
        }
        final CheckBoxPreference addOnDictionaries = (CheckBoxPreference) this.screen.findPreference("addondictionaries");
        if (addOnDictionaries != null) {
            addOnDictionaries.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.FunctionBar.9
                @Override // android.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    if (FunctionBar.this.getFunctionItemSelectedCount() > 4) {
                        addOnDictionaries.setChecked(false);
                        FunctionBar.this.showMaxCountdialog();
                    }
                    StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(FunctionBar.this.activity);
                    if (sessionScribe != null) {
                        boolean checked = ((CheckBoxPreference) preference).isChecked();
                        sessionScribe.recordSettingsChange("addondictionaries", Boolean.valueOf(checked), Boolean.valueOf(checked ? false : true));
                    }
                    return true;
                }
            });
        }
        final CheckBoxPreference chineseSettings = (CheckBoxPreference) this.screen.findPreference("chinesesettings");
        if (chineseSettings != null) {
            chineseSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.FunctionBar.10
                @Override // android.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    if (FunctionBar.this.getFunctionItemSelectedCount() > 4) {
                        chineseSettings.setChecked(false);
                        FunctionBar.this.showMaxCountdialog();
                    }
                    StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(FunctionBar.this.activity);
                    if (sessionScribe != null) {
                        boolean checked = ((CheckBoxPreference) preference).isChecked();
                        sessionScribe.recordSettingsChange("chinesesettings", Boolean.valueOf(checked), Boolean.valueOf(checked ? false : true));
                    }
                    return true;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getFunctionItemSelectedCount() {
        CheckBoxPreference quickToggle;
        int selectedCount = 0;
        CheckBoxPreference settings = (CheckBoxPreference) this.screen.findPreference("settings");
        if (settings != null && settings.isChecked()) {
            selectedCount = 0 + 1;
        }
        CheckBoxPreference languageOption = (CheckBoxPreference) this.screen.findPreference("language_option");
        if (languageOption != null && languageOption.isChecked()) {
            selectedCount++;
        }
        CheckBoxPreference inputMode = (CheckBoxPreference) this.screen.findPreference("inputmode");
        if (inputMode != null && inputMode.isChecked()) {
            selectedCount++;
        }
        if (UserPreferences.from(this.activity).isHandwritingEnabled() && (quickToggle = (CheckBoxPreference) this.screen.findPreference("quicktoggle")) != null && quickToggle.isChecked()) {
            selectedCount++;
        }
        CheckBoxPreference editKeyboard = (CheckBoxPreference) this.screen.findPreference("edit_layer");
        if (editKeyboard != null && editKeyboard.isChecked()) {
            selectedCount++;
        }
        CheckBoxPreference numberKeyboard = (CheckBoxPreference) this.screen.findPreference("number_keyboard");
        if (numberKeyboard != null && numberKeyboard.isChecked()) {
            selectedCount++;
        }
        CheckBoxPreference themes = (CheckBoxPreference) this.screen.findPreference("themes");
        if (themes != null && themes.isChecked()) {
            selectedCount++;
        }
        CheckBoxPreference emojiOnFunctionBar = (CheckBoxPreference) this.screen.findPreference("function_bar_emoji");
        if (emojiOnFunctionBar != null && emojiOnFunctionBar.isChecked()) {
            selectedCount++;
        }
        CheckBoxPreference addOnDictionaries = (CheckBoxPreference) this.screen.findPreference("addondictionaries");
        if (addOnDictionaries != null && addOnDictionaries.isChecked()) {
            selectedCount++;
        }
        CheckBoxPreference chineseSettings = (CheckBoxPreference) this.screen.findPreference("chinesesettings");
        if (chineseSettings != null && chineseSettings.isChecked()) {
            return selectedCount + 1;
        }
        return selectedCount;
    }

    public Dialog createMaxItemDlg() {
        String maxContentMsg = String.format(this.activity.getString(R.string.max_function_item_count_content), 4);
        return new AlertDialog.Builder(this.activity).setTitle(R.string.max_function_item_count_title).setIcon(R.drawable.swype_logo).setMessage(maxContentMsg).setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.FunctionBar.11
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).create();
    }
}
