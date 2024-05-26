package com.nuance.swype.input.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.KeyEvent;
import android.view.View;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.connect.ConnectedStatus;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.BilingualLanguage;
import com.nuance.swype.input.ChinaNetworkNotificationDialog;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputMethodToast;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.LanguageListAdapter;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.preference.ConnectionAwarePreferences;
import com.nuance.swype.preference.CurrentLanguagePreference;
import com.nuance.swype.preference.KeyboardListAdapter;
import com.nuance.swype.preference.LanguagePreference;
import com.nuance.swype.preference.NoMarginClassActionPreference;
import com.nuance.swype.preference.ViewClickPreference;
import com.nuance.swype.stats.StatisticsManager;
import com.nuance.swype.usagedata.UsageData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public abstract class LanguageOptions implements SharedPreferences.OnSharedPreferenceChangeListener, ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener, CurrentLanguagePreference.CurrentLanguagePreferenceListener, ViewClickPreference.ViewClickPreferenceListener {
    private static final String ADDITIONAL_LANGUAGE_KEY = "additional_language";
    protected static final String LANGUAGE_KEY = "language";
    public static final int LANGUAGE_PREFS_XML = R.xml.prefs_language;
    private static final String PREF_KEY_CURRENT_LANGUAGE = "current_language";
    protected static final String PREF_KEY_DOWNLOAD_LANGUAGES = "download_languages";
    private static final String PREF_KEY_RECENT_LANGUAGES = "recent_languages";
    protected static final int REQUEST_CODE_ALM_INSTALL = 2;
    protected static final int REQUEST_CODE_LANGUAGE_DOWNLOAD = 1;
    protected final Activity activity;
    private ConnectedStatus connectedStatus;
    private final ConnectionAwarePreferences connection;
    private final PreferenceScreen screen;
    private boolean isDialogShowing = false;
    protected String mPrefKey = "";
    protected boolean keyIsDown = false;
    List<String> mJapaneseLayoutsValueList = new ArrayList();
    private final Preference.OnPreferenceClickListener languageDownloadListener = new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.LanguageOptions.10
        @Override // android.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            return LanguageOptions.this.handleDownloadClick();
        }
    };

    protected abstract void doStartActivityForResult(Intent intent, int i);

    protected abstract void refresh();

    protected abstract void showConnectDialog();

    protected abstract void showKeyboardDialog(Bundle bundle);

    protected abstract void showLanguageDialog(Bundle bundle);

    protected abstract void showLanguageDownload(Preference preference);

    protected abstract void showLanguageDownload(String str);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void showNetworkNotificationDialog(String str);

    public LanguageOptions(Activity activity, PreferenceScreen screen) {
        this.activity = activity;
        this.screen = screen;
        this.connection = new ConnectionAwarePreferences(activity) { // from class: com.nuance.swype.input.settings.LanguageOptions.1
            @Override // com.nuance.swype.preference.ConnectionAwarePreferences
            public void showConnectDialog() {
                LanguageOptions.this.showConnectDialog();
            }
        };
        UsageData.recordScreenVisited(UsageData.Screen.LANGUAGES);
    }

    public void onStart() {
        if (this.connectedStatus == null) {
            this.connectedStatus = new ConnectedStatus(this.activity) { // from class: com.nuance.swype.input.settings.LanguageOptions.2
                @Override // com.nuance.swype.connect.ConnectedStatus
                public void onConnectionChanged(boolean isConnected) {
                    LanguageOptions.this.buildLanguagePrefs();
                }
            };
        }
        this.connectedStatus.register();
    }

    public void onResume() {
        this.screen.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        buildLanguagePrefs();
    }

    public void onPause() {
        this.screen.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onStop() {
        this.connectedStatus.unregister();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Preference languageDownloadPref;
        switch (requestCode) {
            case 1:
                if (resultCode == -1 && (languageDownloadPref = getLanguageDownloadPref()) != null) {
                    showLanguageDownload(languageDownloadPref);
                    return;
                }
                return;
            default:
                return;
        }
    }

    protected final Preference findPreference(String key) {
        if (this.screen != null) {
            return this.screen.findPreference(key);
        }
        return null;
    }

    public Dialog createConnectDialog() {
        return this.connection.getConnectDialog().create();
    }

    public Dialog createLanguageDialog(Bundle args) {
        List<InputMethods.Language> languageList;
        String languageKey = args.getString("language");
        InputMethods inputMethods = InputMethods.from(this.activity);
        final InputMethods.Language originalLanguage = inputMethods.findInputLanguage(languageKey);
        final boolean isAdditionalLanguage = args.getBoolean(ADDITIONAL_LANGUAGE_KEY, false);
        InputMethods.Language checkedLanguage = originalLanguage;
        InputMethods.Language firstLanguageTemp = originalLanguage;
        if (isAdditionalLanguage) {
            if (checkedLanguage instanceof BilingualLanguage) {
                firstLanguageTemp = ((BilingualLanguage) checkedLanguage).getFirstLanguage();
                checkedLanguage = ((BilingualLanguage) checkedLanguage).getSecondLanguage();
            } else {
                checkedLanguage = null;
            }
        }
        final InputMethods.Language firstLanguage = firstLanguageTemp;
        final InputMethods.Language secondaryLanguage = checkedLanguage;
        if (isAdditionalLanguage) {
            languageList = inputMethods.getCompatibleInputLanguages(firstLanguage);
        } else {
            languageList = inputMethods.getInputLanguagesCopy();
        }
        final LanguageListAdapter languageAdapter = new LanguageListAdapter(this.activity, languageList, checkedLanguage, isAdditionalLanguage);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.swype_logo);
        builder.setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null);
        builder.setAdapter(languageAdapter, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.LanguageOptions.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface di, int position) {
                String languageId;
                if (!ActivityManagerCompat.isUserAMonkey()) {
                    InputMethods inputMethods2 = InputMethods.from(LanguageOptions.this.activity);
                    InputMethods.Language language = (InputMethods.Language) languageAdapter.getItem(position);
                    if (language == null) {
                        languageId = firstLanguage.getLanguageId();
                        language = firstLanguage;
                    } else {
                        languageId = isAdditionalLanguage ? BilingualLanguage.getLanguageId(originalLanguage, language) : language.getLanguageId();
                    }
                    inputMethods2.setCurrentLanguage(languageId);
                    LanguageOptions.this.buildLanguagePrefs();
                    IME ime = IMEApplication.from(LanguageOptions.this.activity.getApplicationContext()).getIME();
                    if (ime != null) {
                        ime.getKeyboardBackgroundManager().setReloadRequiredFromResources(true);
                    }
                    AppPreferences appPrefs = AppPreferences.from(LanguageOptions.this.activity);
                    if (!appPrefs.isRecentLanguageTipAlreadyShown()) {
                        appPrefs.setRecentLanguageTipShown();
                        InputMethodToast.show(LanguageOptions.this.activity, LanguageOptions.this.activity.getText(R.string.tips_bilingual_languageswitchtoast), 1);
                    }
                    new Bundle().putString("language", language.getLanguageId());
                    StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(LanguageOptions.this.activity);
                    if (sessionScribe != null) {
                        if (isAdditionalLanguage) {
                            sessionScribe.recordSettingsChange(LanguageOptions.ADDITIONAL_LANGUAGE_KEY, language.mEnglishName, secondaryLanguage != null ? secondaryLanguage.mEnglishName : "None");
                        } else {
                            sessionScribe.recordSettingsChange("language", language.mEnglishName, firstLanguage.mEnglishName);
                        }
                    }
                }
            }
        });
        builder.setTitle(this.activity.getString(R.string.pref_current_languages));
        return builder.create();
    }

    public Dialog createKeyboardDialog(Bundle args) {
        String key = args.getString("language");
        final InputMethods.Language language = InputMethods.from(this.activity).findInputLanguage(key);
        final KeyboardListAdapter keyboardAdapter = new KeyboardListAdapter(this.activity, language);
        final InputMethods.InputMode currentInputMode = keyboardAdapter.curInputMode;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.swype_logo);
        builder.setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null);
        if (language.isKoreanLanguage()) {
            builder.setAdapter(keyboardAdapter, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.LanguageOptions.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface di, int position) {
                    ((InputMethods.Layout) keyboardAdapter.getItem(position)).saveAsCurrent();
                }
            });
        } else if (language.isJapaneseLanguage()) {
            builder.setAdapter(keyboardAdapter, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.LanguageOptions.5
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface di, int position) {
                    String layout = (String) keyboardAdapter.getItem(position);
                    language.getCurrentInputMode().setCurrentJapaneseLayout(layout);
                    String[] mJapaneseLayouts = LanguageOptions.this.activity.getApplicationContext().getResources().getStringArray(R.array.entryValues_japanese_keyboard_portrait_options);
                    Collections.addAll(LanguageOptions.this.mJapaneseLayoutsValueList, mJapaneseLayouts);
                    String japaneseLayout = LanguageOptions.this.mJapaneseLayoutsValueList.get(position);
                    AppPreferences.from(LanguageOptions.this.activity.getApplicationContext()).setString(language.getCurrentInputMode().getPortaitLayoutOptionsPrefKey(), japaneseLayout);
                }
            });
        } else {
            builder.setAdapter(keyboardAdapter, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.LanguageOptions.6
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface di, int position) {
                    InputMethods.InputMode inputMode = (InputMethods.InputMode) keyboardAdapter.getItem(position);
                    inputMode.setCurrent();
                    AppPreferences appPrefs = AppPreferences.from(LanguageOptions.this.activity);
                    if (language.isHindiLanguage() && !inputMode.mInputMode.equals(InputMethods.KEYBOARD_TRANSLITERATION)) {
                        appPrefs.setString(AppPreferences.PREF_HINDI_INPUT_MODE, inputMode.mInputMode);
                    }
                    if (language.isChineseLanguage() && inputMode.isHandwriting()) {
                        if (InputMethods.CHINESE_INPUT_MODE_HANDWRITING_FULL_SCREEN.equals(inputMode.mInputMode)) {
                            appPrefs.setBoolean(AppPreferences.CJK_FULL_SCREEN_ENABLED + language.getCoreLanguageId(), true);
                        } else if (InputMethods.CHINESE_INPUT_MODE_HANDWRITING_HALF_SCREEN.equals(inputMode.mInputMode)) {
                            appPrefs.setBoolean(AppPreferences.CJK_FULL_SCREEN_ENABLED + language.getCoreLanguageId(), false);
                        }
                    }
                    StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(LanguageOptions.this.activity);
                    if (sessionScribe != null) {
                        sessionScribe.recordSettingsChange("Languages", "Keyboard:" + inputMode.mInputMode, currentInputMode.mInputMode);
                    }
                }
            });
        }
        builder.setTitle(this.activity.getString(R.string.keyboard_input));
        return builder.create();
    }

    protected void buildLanguagePrefs() {
        float langPrefTitleFontSize = LanguagePreference.getDefaultTitleTextSize(this.activity.getApplicationContext());
        Preference downloadLanguages = getLanguageDownloadPref();
        if (downloadLanguages != null) {
            if (downloadLanguages instanceof ViewClickPreference) {
                ((ViewClickPreference) downloadLanguages).setListener(this);
                ((ViewClickPreference) downloadLanguages).setFocusable(true);
                ViewClickPreference viewClickPreference = (ViewClickPreference) downloadLanguages;
                View.OnKeyListener onKeyListener = new View.OnKeyListener() { // from class: com.nuance.swype.input.settings.LanguageOptions.7
                    @Override // android.view.View.OnKeyListener
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        return LanguageOptions.this.onKeyForFocusChanging(v, keyCode, event);
                    }
                };
                viewClickPreference.mKeyListener = onKeyListener;
                if (viewClickPreference.mCurrentView != null) {
                    viewClickPreference.mCurrentView.setOnKeyListener(onKeyListener);
                }
            } else {
                downloadLanguages.setOnPreferenceClickListener(this.languageDownloadListener);
            }
            ((NoMarginClassActionPreference) downloadLanguages).titleTextSize = langPrefTitleFontSize;
            if (!Connect.from(this.activity).isLicensed() || ((!this.activity.getResources().getBoolean(R.bool.enable_china_connect_special) && !Connect.from(this.activity).isStarted()) || IMEApplication.from(this.activity).isTrialExpired())) {
                this.screen.removePreference(downloadLanguages);
            }
        }
        InputMethods inputMethods = InputMethods.from(this.activity);
        final InputMethods.Language curLanguage = inputMethods.getCurrentInputLanguage();
        CurrentLanguagePreference currentLangPref = (CurrentLanguagePreference) this.screen.findPreference("current_language");
        currentLangPref.setLanguage(curLanguage);
        currentLangPref.setLanguagePreferenceListener(this);
        currentLangPref.setKeyboardVisible(curLanguage.getDisplayModes().size() > 1 || curLanguage.isJapaneseLanguage() || curLanguage.isKoreanLanguage());
        currentLangPref.setAdditionalLanguageVisible(inputMethods.getCompatibleInputLanguages(curLanguage).size() > 0);
        currentLangPref.titleTextSize = langPrefTitleFontSize;
        PreferenceCategory recentCategory = (PreferenceCategory) this.screen.findPreference(PREF_KEY_RECENT_LANGUAGES);
        if (recentCategory != null) {
            recentCategory.removeAll();
            for (InputMethods.Language language : inputMethods.getRecentLanguages()) {
                LanguagePreference preference = new LanguagePreference(this.activity);
                preference.setLanguage(language);
                preference.setCurrent(curLanguage.equals(language));
                preference.setKeyboardVisible(language.getDisplayModes().size() > 1);
                preference.titleTextSize = langPrefTitleFontSize;
                preference.setLanguagePreferenceListener(new LanguagePreference.LanguagePreferenceListener() { // from class: com.nuance.swype.input.settings.LanguageOptions.8
                    @Override // com.nuance.swype.preference.LanguagePreference.LanguagePreferenceListener
                    public void onLanguageClicked(InputMethods.Language language2) {
                        if (!ActivityManagerCompat.isUserAMonkey()) {
                            InputMethods.from(LanguageOptions.this.activity).setCurrentLanguage(language2.getLanguageId());
                            LanguageOptions.this.buildLanguagePrefs();
                            StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(LanguageOptions.this.activity);
                            if (sessionScribe != null) {
                                sessionScribe.recordSettingsChange("Recent Languages", language2.mEnglishName, curLanguage.mEnglishName);
                            }
                            LanguageOptions.this.refresh();
                        }
                    }

                    @Override // com.nuance.swype.preference.LanguagePreference.LanguagePreferenceListener
                    public void onKeyboardClicked(InputMethods.Language language2) {
                        LanguageOptions.this.onKeyboardClicked(language2);
                    }
                });
                recentCategory.addPreference(preference);
            }
        }
    }

    @Override // com.nuance.swype.preference.LanguagePreference.LanguagePreferenceListener
    public void onLanguageClicked(InputMethods.Language language) {
        if (!isDialogShowing()) {
            Bundle args = new Bundle();
            args.putString("language", language.getLanguageId());
            showLanguageDialog(args);
            setDialogShowing(true);
        }
    }

    @Override // com.nuance.swype.preference.LanguagePreference.LanguagePreferenceListener
    public void onKeyboardClicked(InputMethods.Language language) {
        if (!isDialogShowing()) {
            Bundle args = new Bundle();
            args.putString("language", language.getLanguageId());
            showKeyboardDialog(args);
            setDialogShowing(true);
        }
    }

    @Override // com.nuance.swype.preference.CurrentLanguagePreference.CurrentLanguagePreferenceListener
    public void onAdditionalLanguageClicked(final InputMethods.Language language) {
        AppPreferences appPrefs = AppPreferences.from(this.activity);
        if (!appPrefs.isBilingualTipAlreadyShown()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
            builder.setIcon(R.drawable.swype_logo);
            builder.setTitle(R.string.pref_bilingual_popupheader);
            builder.setMessage(R.string.bilingual_first_time_tip);
            builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.LanguageOptions.9
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    LanguageOptions.this.showLanguageDialog(language);
                }
            });
            builder.create().show();
            appPrefs.setBilingualTipShown();
            return;
        }
        showLanguageDialog(language);
    }

    @Override // com.nuance.swype.preference.ViewClickPreference.ViewClickPreferenceListener
    public void onViewClick(Preference pf) {
        if (!handleDownloadClick()) {
            showLanguageDownload(pf);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showLanguageDialog(InputMethods.Language language) {
        if (!isDialogShowing()) {
            Bundle args = new Bundle();
            args.putString("language", language.getLanguageId());
            args.putBoolean(ADDITIONAL_LANGUAGE_KEY, true);
            showLanguageDialog(args);
            setDialogShowing(true);
        }
    }

    @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        if ("current_language".equals(key) || InputMethods.CJK_SETTINGS.contains(key)) {
            buildLanguagePrefs();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean handleDownloadClick() {
        if (!this.connectedStatus.isConnectedWifi() && (!this.connectedStatus.isConnectedCellular() || !this.connectedStatus.isDataConnectionPermitted())) {
            showConnectDialog();
            return true;
        }
        UserPreferences userPrefs = UserPreferences.from(this.activity);
        if (this.activity.getResources().getBoolean(R.bool.enable_china_connect_special) && userPrefs.shouldShowNetworkAgreementDialog()) {
            showNetworkNotificationDialog(getLanguageDownloadPref().getKey());
            return true;
        }
        if (showLegalRequirements(1, true, false, null)) {
            return true;
        }
        if (this.connectedStatus.isConnectedWifi() || (this.connectedStatus.isConnected() && this.connectedStatus.isDataConnectionPermitted())) {
            return false;
        }
        showConnectDialog();
        return true;
    }

    private boolean showLegalRequirements(int requestCode, boolean tosRequired, boolean optInRequired, Bundle resultData) {
        Intent intent = ConnectLegal.getLegalActivitiesStartIntent(this.activity, tosRequired, optInRequired, resultData);
        if (intent == null) {
            return false;
        }
        doStartActivityForResult(intent, requestCode);
        return true;
    }

    private Preference getLanguageDownloadPref() {
        return this.screen.findPreference(PREF_KEY_DOWNLOAD_LANGUAGES);
    }

    public void setDialogShowing(boolean isDialogShowing) {
        this.isDialogShowing = isDialogShowing;
    }

    private boolean isDialogShowing() {
        return this.isDialogShowing;
    }

    public Dialog createNetworkNotificationDialg() {
        return ChinaNetworkNotificationDialog.create(this.activity, this);
    }

    @Override // com.nuance.swype.input.ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener
    public boolean onNegativeButtonClick() {
        return true;
    }

    @Override // com.nuance.swype.input.ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener
    public boolean onPositiveButtonClick() {
        showLanguageDownload(this.mPrefKey);
        return true;
    }

    protected boolean onKeyForFocusChanging(View v, int keyCode, KeyEvent event) {
        View nextView;
        if (keyCode != 20 && keyCode != 19 && keyCode != 21 && keyCode != 22) {
            return false;
        }
        if (event.getAction() == 0) {
            this.keyIsDown = true;
            return true;
        }
        if (!this.keyIsDown) {
            return true;
        }
        this.keyIsDown = false;
        if (keyCode == 19) {
            nextView = v.focusSearch(33);
        } else if (keyCode == 21) {
            nextView = v.focusSearch(17);
        } else if (keyCode == 22) {
            nextView = v.focusSearch(66);
        } else {
            nextView = v.focusSearch(130);
        }
        if (nextView != null) {
            nextView.requestFocus();
        }
        return true;
    }
}
