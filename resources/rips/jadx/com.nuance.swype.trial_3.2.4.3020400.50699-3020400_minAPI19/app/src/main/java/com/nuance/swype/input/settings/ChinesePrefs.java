package com.nuance.swype.input.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.CategoryDBList;
import com.nuance.swype.input.ChinaNetworkNotificationDialog;
import com.nuance.swype.input.CloudNetworkAdapter;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.preference.ConnectionAwarePreferences;
import com.nuance.swype.preference.DialogPrefs;
import com.nuance.swype.stats.StatisticsManager;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACChineseDictionaryDownloadService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class ChinesePrefs extends DialogPrefs implements Preference.OnPreferenceClickListener, ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener {
    static final int ACTIVATION_DIALOG = 5;
    static final int ADDON_DIALOG = 1;
    private static final String CATEGORY_DB_CAT = "addon_dictionaries_cat";
    protected static final String CHINESE_SETTINGS_CAT = "chinese_settings";
    static final int CONNECTION_DIALOG = 2;
    protected static final String DOWNLOAD_ADDON_DICTIONARIES_KEY = "download_addon_dictionaries";
    private static final int MAX_CatDB_COUNT = 8;
    static final int NO_NETWORK_DIALOG = 3;
    static final int PRIVACY_DIALOG = 4;
    public static final int REQUEST_CHINESE_CLOUD_INPUT = 1;
    protected final Activity activity;
    private List<String> cDBDescriptions;
    private List<String> cDBNames;
    private CategoryDBList cdbList;
    private List<String> cdbs;
    public Dialog cloudDialog;
    private final Connect connect;
    private final ConnectionAwarePreferences connection;
    private InputMethods.Language currrentInputLanguage;
    private boolean isNetworkForCloud;
    private PreferenceScreen screen;
    private static final LogManager.Log log = LogManager.getLog("Chinese");
    public static final int CHINESE_PREFS_XML = R.xml.chinesepreferences;
    private Map<String, String> mCloudItem = new HashMap();
    private List<String> mCloudKeyItem = new ArrayList();
    private boolean started = false;
    DialogPrefs.DialogCreator addonDialog = new DialogPrefs.DialogCreator() { // from class: com.nuance.swype.input.settings.ChinesePrefs.9
        @Override // com.nuance.swype.preference.DialogPrefs.DialogCreator
        public Dialog doCreateDialog(Context context, Bundle args) {
            return null;
        }
    };
    DialogPrefs.DialogCreator connectionDialog = new DialogPrefs.DialogCreator() { // from class: com.nuance.swype.input.settings.ChinesePrefs.10
        @Override // com.nuance.swype.preference.DialogPrefs.DialogCreator
        public Dialog doCreateDialog(Context context, Bundle args) {
            return ChinesePrefs.this.connection.getConnectDialog(context).create();
        }
    };
    DialogPrefs.DialogCreator privacyDialog = new DialogPrefs.DialogCreator() { // from class: com.nuance.swype.input.settings.ChinesePrefs.11
        @Override // com.nuance.swype.preference.DialogPrefs.DialogCreator
        public Dialog doCreateDialog(Context context, Bundle args) {
            return null;
        }
    };
    DialogPrefs.DialogCreator activationDialog = new DialogPrefs.DialogCreator() { // from class: com.nuance.swype.input.settings.ChinesePrefs.12
        @Override // com.nuance.swype.preference.DialogPrefs.DialogCreator
        public Dialog doCreateDialog(Context context, Bundle args) {
            return null;
        }
    };
    DialogPrefs.DialogCreator noNetworkDialog = new DialogPrefs.DialogCreator() { // from class: com.nuance.swype.input.settings.ChinesePrefs.13
        @Override // com.nuance.swype.preference.DialogPrefs.DialogCreator
        public Dialog doCreateDialog(Context context, Bundle args) {
            return new AlertDialog.Builder(context).setTitle(R.string.no_network_available).setMessage(R.string.no_network_try_again_msg).setNegativeButton(R.string.dismiss_button, (DialogInterface.OnClickListener) null).create();
        }
    };

    protected abstract PreferenceScreen addPreferences();

    protected abstract Preference creatAddOnDictionaryPref();

    protected abstract Preference createClouldNetWorkPref();

    protected abstract Preference createFunctionBarPref();

    protected abstract Preference createInputModePref(Bundle bundle);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void doCancelDialog(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void doShowDialog(int i, Bundle bundle);

    abstract void doStartActivityForResult(Intent intent, int i);

    protected abstract void showConnectDialog();

    protected abstract void showDownloadAddonDictionaries();

    protected abstract void showMaxCountdialog();

    protected abstract void showNetworkNotificationDialog();

    public ChinesePrefs(Activity activity) {
        this.activity = activity;
        this.connect = Connect.from(activity);
        this.connection = new ConnectionAwarePreferences(activity) { // from class: com.nuance.swype.input.settings.ChinesePrefs.1
            @Override // com.nuance.swype.preference.ConnectionAwarePreferences
            public void showConnectDialog() {
                ChinesePrefs.this.doShowDialog(2, null);
            }
        };
        this.mCloudItem.put(AppPreferences.CHINESE_CLOUD_WIFI_ONLY, activity.getResources().getString(R.string.chinese_cloud_wifi_only));
        this.mCloudItem.put(AppPreferences.CHINESE_CLOUD_ALL, activity.getResources().getString(R.string.chinese_cloud_all));
        this.mCloudItem.put(AppPreferences.CHINESE_CLOUD_DISABLED, activity.getResources().getString(R.string.chinese_cloud_disabled));
        this.mCloudKeyItem.add(AppPreferences.CHINESE_CLOUD_WIFI_ONLY);
        this.mCloudKeyItem.add(AppPreferences.CHINESE_CLOUD_ALL);
        this.mCloudKeyItem.add(AppPreferences.CHINESE_CLOUD_DISABLED);
        registerDialog(1, this.addonDialog);
        registerDialog(4, this.privacyDialog);
        registerDialog(2, this.connectionDialog);
        registerDialog(3, this.noNetworkDialog);
        registerDialog(5, this.activationDialog);
        UsageData.recordScreenVisited(UsageData.Screen.CHINESE_PREFERENCES);
    }

    public void onStart() {
        this.started = true;
    }

    public void onResume() {
        rebuildSettings();
    }

    public void onStop() {
        this.started = false;
    }

    protected final Preference findPreference(String key) {
        if (this.screen != null) {
            return this.screen.findPreference(key);
        }
        return null;
    }

    protected final void rebuildSettings() {
        if (this.screen != null) {
            this.screen.removeAll();
        }
        InputMethods inputMethods = InputMethods.from(this.activity);
        this.currrentInputLanguage = inputMethods.getCurrentInputLanguage();
        if (this.cdbList != null) {
            this.cdbList = null;
        }
        this.cdbList = new CategoryDBList(this.activity.getApplicationContext(), true);
        this.screen = addPreferences();
        PreferenceCategory chineseCategory = (PreferenceCategory) this.screen.findPreference(CHINESE_SETTINGS_CAT);
        if (this.currrentInputLanguage.isChineseLanguage()) {
            chineseCategory.setTitle(this.currrentInputLanguage.getDisplayName());
            this.screen.addPreference(chineseCategory);
            List<InputMethods.InputMode> inputModes = this.currrentInputLanguage.getChineseInputModes();
            if (inputModes != null) {
                InputMethods.InputMode currentInputMode = this.currrentInputLanguage.getCurrentInputMode();
                for (InputMethods.InputMode inputMode : inputModes) {
                    if (!inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_STROKE) && !inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_ZHUYIN_NINE_KEYS) && !inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_ZHUYIN_QWERTY) && !inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_PINYIN_NINE_KEYS) && !inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_DOUBLEPINYIN) && !inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_CANGJIE_NINE_KEYS) && !inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_CANGJIE_QWERTY) && !inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_QUICK_CANGJIE_NINE_KEYS) && !inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_QUICK_CANGJIE_QWERTY) && !inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_HANDWRITING_HALF_SCREEN) && (!this.currrentInputLanguage.isChineseTraditional() || !inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_PINYIN_QWERTY))) {
                        if (!inputMode.isHandwriting()) {
                            Preference pref = createInputModePref(InputPrefs.createArgs(inputMode));
                            pref.setKey(inputMode.getEnabledPrefKey());
                            if (InputMethods.isChineseInputModePinyin(inputMode.mInputMode)) {
                                pref.setTitle(this.activity.getResources().getString(R.string.pinyin));
                            }
                            if (InputMethods.getChineseInputModeType(currentInputMode.mInputMode).equals(InputMethods.getChineseInputModeType(inputMode.mInputMode))) {
                                pref.setSummary(R.string.current_use_input_mode);
                            }
                            chineseCategory.addPreference(pref);
                        }
                    }
                }
            }
            Preference pref2 = createFunctionBarPref();
            pref2.setTitle(R.string.function_bar);
            pref2.setSummary(R.string.function_bar_summary);
            chineseCategory.addPreference(pref2);
            CheckBoxPreference bilingCheckBox = new CheckBoxPreference(this.activity);
            bilingCheckBox.setKey(UserPreferences.PREF_ENABLE_CHINESE_BILINGUAL);
            bilingCheckBox.setTitle(R.string.enable_chinese_bilingual);
            bilingCheckBox.setSummary(R.string.enable_chinese_bilingual_summary);
            bilingCheckBox.setChecked(UserPreferences.from(this.activity).getEnableChineseBilingual());
            chineseCategory.addPreference(bilingCheckBox);
            bilingCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.nuance.swype.input.settings.ChinesePrefs.2
                @Override // android.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    CheckBoxPreference check = (CheckBoxPreference) preference;
                    boolean currentVaule = Boolean.parseBoolean(newValue.toString());
                    check.setChecked(currentVaule);
                    StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(ChinesePrefs.this.activity);
                    if (sessionScribe != null) {
                        sessionScribe.recordSettingsChange(check.getKey(), newValue.toString(), Boolean.valueOf(!currentVaule));
                    }
                    return true;
                }
            });
            if (this.connect.isLicensed()) {
                Preference pref3 = createClouldNetWorkPref();
                pref3.setTitle(R.string.chinese_cloud_input);
                pref3.setSummary(this.mCloudItem.get(AppPreferences.from(this.activity).getChineseCloudNetworkOption()));
                pref3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.ChinesePrefs.3
                    @Override // android.preference.Preference.OnPreferenceClickListener
                    public boolean onPreferenceClick(Preference preference) {
                        UserPreferences userPrefs = UserPreferences.from(ChinesePrefs.this.activity);
                        if (ChinesePrefs.this.activity.getResources().getBoolean(R.bool.enable_china_connect_special) && !userPrefs.getNetworkAgreement()) {
                            ChinesePrefs.this.isNetworkForCloud = true;
                            ChinesePrefs.this.showNetworkNotificationDialog();
                            return false;
                        }
                        Bundle requestData = new Bundle();
                        if (ChinesePrefs.this.showLegalRequirements(1, true, true, requestData)) {
                            return false;
                        }
                        if (!UserPreferences.from(ChinesePrefs.this.activity).isDataUsageOptAccepted() && ChinesePrefs.this.showCUDRequirements(1, true, requestData)) {
                            return false;
                        }
                        ChinesePrefs.this.showCloudNetworkOpt();
                        return true;
                    }
                });
                chineseCategory.addPreference(pref3);
            }
            this.isNetworkForCloud = false;
        }
        PreferenceCategory categorydbCategory = (PreferenceCategory) this.screen.findPreference(CATEGORY_DB_CAT);
        if (categorydbCategory != null) {
            BuildInfo info = BuildInfo.from(this.activity);
            if (!this.connect.isLicensed() || !info.isConnectEnabled()) {
                this.screen.removePreference(categorydbCategory);
            } else {
                categorydbCategory.removeAll();
                Preference pref4 = creatAddOnDictionaryPref();
                pref4.setTitle(R.string.download_addon_dictionaries);
                pref4.setKey(DOWNLOAD_ADDON_DICTIONARIES_KEY);
                categorydbCategory.addPreference(pref4);
            }
        }
        retrieveDictionaryNamesInBackground();
    }

    public void showCloudNetworkOpt() {
        if (this.screen == null) {
            rebuildSettings();
        }
        String currentKey = AppPreferences.from(this.activity).getChineseCloudNetworkOption();
        if (currentKey == null || currentKey.isEmpty()) {
            currentKey = AppPreferences.CHINESE_CLOUD_WIFI_ONLY;
            AppPreferences.from(this.activity).setChineseCloudNetworkOption(AppPreferences.CHINESE_CLOUD_WIFI_ONLY);
            this.screen.findPreference(UserPreferences.PREF_CLOUD_INPUT).setSummary(this.mCloudItem.get(AppPreferences.CHINESE_CLOUD_WIFI_ONLY));
        }
        final CloudNetworkAdapter cloudNetworkAdapter = new CloudNetworkAdapter(this.activity, this.mCloudItem, this.mCloudKeyItem, currentKey);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setCancelable(true);
        builder.setNegativeButton(R.string.ok_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.ChinesePrefs.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int whichButton) {
                StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(ChinesePrefs.this.activity);
                if (sessionScribe != null) {
                    String option = AppPreferences.from(ChinesePrefs.this.activity).getChineseCloudNetworkOption();
                    sessionScribe.recordSettingsChange("cloud input", option, option);
                }
            }
        });
        builder.setAdapter(cloudNetworkAdapter, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.ChinesePrefs.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface di, int position) {
                if (!ActivityManagerCompat.isUserAMonkey()) {
                    String itemKey = (String) cloudNetworkAdapter.getItem(position);
                    StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(ChinesePrefs.this.activity);
                    if (sessionScribe != null) {
                        sessionScribe.recordSettingsChange("cloud input", itemKey, AppPreferences.from(ChinesePrefs.this.activity).getChineseCloudNetworkOption() + XMLResultsHandler.SEP_SPACE);
                    }
                    AppPreferences.from(ChinesePrefs.this.activity).setChineseCloudNetworkOption(itemKey);
                    ChinesePrefs.this.screen.findPreference(UserPreferences.PREF_CLOUD_INPUT).setSummary((CharSequence) ChinesePrefs.this.mCloudItem.get(itemKey));
                }
            }
        });
        builder.setTitle(this.activity.getString(R.string.chinese_cloud_input));
        this.cloudDialog = builder.create();
        this.cloudDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == -1 && data.getBundleExtra("result_data") != null) {
                    showCloudNetworkOpt();
                    return;
                }
                return;
            default:
                log.d("Not REQUEST_CHINESE_CLOUD_INPUT do nothing");
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean showLegalRequirements(int requestCode, boolean tosRequired, boolean optInRequired, Bundle resultData) {
        Intent intent;
        if (!IMEApplication.from(this.activity).isUserUnlockFinished() || (intent = ConnectLegal.getLegalActivitiesStartIntent(this.activity, tosRequired, optInRequired, resultData)) == null) {
            return false;
        }
        doStartActivityForResult(intent, requestCode);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean showCUDRequirements(int requestCode, boolean optInRequired, Bundle resultData) {
        Intent intent = ConnectLegal.getLegalCUDActivitiesStartIntent(this.activity, optInRequired, resultData);
        if (intent == null) {
            return false;
        }
        doStartActivityForResult(intent, requestCode);
        return true;
    }

    private void retrieveDictionaryNamesInBackground() {
        Runnable r = new Runnable() { // from class: com.nuance.swype.input.settings.ChinesePrefs.6
            @Override // java.lang.Runnable
            public void run() {
                ChinesePrefs.this.cdbs = null;
                ChinesePrefs.this.cDBNames = new ArrayList();
                ChinesePrefs.this.cDBDescriptions = new ArrayList();
                if (ChinesePrefs.this.cdbList != null && ChinesePrefs.this.currrentInputLanguage != null && ChinesePrefs.this.currrentInputLanguage.mEnglishName != null) {
                    ChinesePrefs.this.cdbs = ChinesePrefs.this.cdbList.getShowableCDBs(ChinesePrefs.this.currrentInputLanguage.mEnglishName);
                }
                if (ChinesePrefs.this.cdbs != null && ChinesePrefs.this.cdbs.size() > 0) {
                    for (String cdb : ChinesePrefs.this.cdbs) {
                        String cdbName = ChinesePrefs.this.cdbList.getFileName(cdb);
                        if (cdbName != null) {
                            cdbName = cdbName.trim();
                        }
                        String name = ChinesePrefs.this.getNameForDictionary(cdbName);
                        if (name != null) {
                            ChinesePrefs.this.cDBNames.add(String.valueOf(name.trim()));
                            String description = ChinesePrefs.this.getCategoryNameForDictionary(cdbName);
                            if (description != null) {
                                description = description.trim();
                            }
                            ChinesePrefs.this.cDBDescriptions.add(String.valueOf(description));
                        }
                    }
                    ChinesePrefs.this.updateCDBInWorkerThread(ChinesePrefs.this.cdbs, ChinesePrefs.this.cDBNames, ChinesePrefs.this.cDBDescriptions);
                }
            }
        };
        Thread t = new Thread(r);
        t.setPriority(5);
        t.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCDBInWorkerThread(final List<String> cdbs, final List<String> names, final List<String> descriptions) {
        this.activity.runOnUiThread(new Runnable() { // from class: com.nuance.swype.input.settings.ChinesePrefs.7
            @Override // java.lang.Runnable
            public void run() {
                ChinesePrefs.this.updateCategoryDBCheckBoxes(cdbs, names, descriptions);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCategoryDBCheckBoxes(List<String> cdbs, List<String> names, List<String> descriptions) {
        if (this.started && cdbs != null && cdbs.size() > 0 && names.size() > 0) {
            AppPreferences appPrefs = AppPreferences.from(this.activity);
            PreferenceCategory categorydbCategory = (PreferenceCategory) this.screen.findPreference(CATEGORY_DB_CAT);
            if (categorydbCategory != null) {
                categorydbCategory.removeAll();
                Preference pref = creatAddOnDictionaryPref();
                pref.setTitle(R.string.download_addon_dictionaries);
                pref.setKey(DOWNLOAD_ADDON_DICTIONARIES_KEY);
                categorydbCategory.addPreference(pref);
                int index = 0;
                for (String cdb : cdbs) {
                    String cdbName = this.cdbList.getFileName(cdb);
                    if (cdbName != null) {
                        cdbName = cdbName.trim();
                    }
                    final CheckBoxPreference cdbPreference = new CheckBoxPreference(this.activity);
                    boolean isChecked = appPrefs.getBoolean(cdbName, false);
                    UserPreferences.from(this.activity).setBoolean(cdbName, isChecked);
                    cdbPreference.setKey(cdbName);
                    cdbPreference.setChecked(isChecked);
                    String name = names.get(index);
                    cdbPreference.setTitle(name);
                    cdbPreference.setEnabled(true);
                    String description = descriptions.get(index);
                    index++;
                    if (!(description != null && description.equals(this.activity.getText(R.string.sports_entertainment)))) {
                        if (description == null) {
                            description = cdbName;
                        }
                        cdbPreference.setSummary(description);
                    }
                    cdbPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.ChinesePrefs.8
                        @Override // android.preference.Preference.OnPreferenceClickListener
                        public boolean onPreferenceClick(Preference preference) {
                            AppPreferences.from(ChinesePrefs.this.activity).setBoolean(preference.getKey(), ((CheckBoxPreference) preference).isChecked());
                            if (ChinesePrefs.this.getSelectedCatDBCount() > 8) {
                                cdbPreference.setChecked(false);
                                AppPreferences.from(ChinesePrefs.this.activity).setBoolean(preference.getKey(), false);
                                UserPreferences.from(ChinesePrefs.this.activity).setBoolean(preference.getKey(), false);
                                ChinesePrefs.this.showMaxCountdialog();
                                return true;
                            }
                            return true;
                        }
                    });
                    categorydbCategory.addPreference(cdbPreference);
                }
            }
        }
    }

    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {
        pref.getKey();
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getNameForDictionary(String dictKey) {
        ACChineseDictionaryDownloadService chineseService = this.connect.getChineseDictionaryDownloadService();
        if (chineseService != null) {
            for (ACChineseDictionaryDownloadService.ACChineseDictionary d : chineseService.getDownloadedDictionaries()) {
                if (d.getKey().equals(dictKey)) {
                    return d.getName();
                }
            }
            log.d("Dictionary key not found in downloaded!");
            for (ACChineseDictionaryDownloadService.ACChineseDictionary d2 : chineseService.getUpdatableDictionaries()) {
                if (d2.getKey().equals(dictKey)) {
                    return d2.getName();
                }
            }
            for (ACChineseDictionaryDownloadService.ACChineseDictionary d3 : chineseService.getAvailableDictionaries()) {
                if (d3.getKey().equals(dictKey)) {
                    return d3.getName();
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getCategoryNameForDictionary(String dictKey) {
        ACChineseDictionaryDownloadService chineseService = this.connect.getChineseDictionaryDownloadService();
        if (chineseService != null) {
            for (ACChineseDictionaryDownloadService.ACChineseDictionary d : chineseService.getDownloadedDictionaries()) {
                if (d.getKey().equals(dictKey)) {
                    return d.getCategory();
                }
            }
            log.d("Dictionary key not found in downloaded!");
            for (ACChineseDictionaryDownloadService.ACChineseDictionary d2 : chineseService.getUpdatableDictionaries()) {
                if (d2.getKey().equals(dictKey)) {
                    return d2.getCategory();
                }
            }
            for (ACChineseDictionaryDownloadService.ACChineseDictionary d3 : chineseService.getAvailableDictionaries()) {
                if (d3.getKey().equals(dictKey)) {
                    return d3.getCategory();
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getSelectedCatDBCount() {
        int count = 0;
        if (this.cdbs == null) {
            this.cdbs = this.cdbList.getShowableCDBs(this.currrentInputLanguage.mEnglishName);
        }
        if (this.cdbs != null && this.cdbs.size() > 0) {
            AppPreferences appPrefs = AppPreferences.from(this.activity);
            for (String cdb : this.cdbs) {
                String cdbName = this.cdbList.getFileName(cdb);
                if (appPrefs.getBoolean(cdbName, false)) {
                    count++;
                }
            }
        }
        return count;
    }

    public Dialog createMaxItemDlg() {
        String maxContentMsg = String.format(this.activity.getString(R.string.eight_items_max), 8);
        return new AlertDialog.Builder(this.activity).setTitle(R.string.max_function_item_count_title).setIcon(R.drawable.swype_logo).setMessage(maxContentMsg).setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.ChinesePrefs.14
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).create();
    }

    public Dialog createNetworkNotificationDialg() {
        return ChinaNetworkNotificationDialog.create(this.activity, this);
    }

    @Override // com.nuance.swype.input.ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener
    public boolean onNegativeButtonClick() {
        this.isNetworkForCloud = false;
        return true;
    }

    @Override // com.nuance.swype.input.ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener
    public boolean onPositiveButtonClick() {
        if (this.isNetworkForCloud) {
            Bundle requestData = new Bundle();
            if (showLegalRequirements(1, true, true, requestData)) {
                return false;
            }
            showCloudNetworkOpt();
            this.isNetworkForCloud = false;
            return true;
        }
        showDownloadAddonDictionaries();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Dialog createConnectDialog() {
        return this.connection.getConnectDialog().create();
    }

    public void dismissDialog() {
        if (this.cloudDialog != null) {
            this.cloudDialog.dismiss();
        }
        cancelAllActiveDialogs();
    }
}
