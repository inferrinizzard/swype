package com.nuance.swype.input.settings;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nuance.connect.util.Logger;
import com.nuance.input.swypecorelib.XT9CoreAlphaInput;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.connect.ConnectedStatus;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.ChinaNetworkNotificationDialog;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.R;
import com.nuance.swype.input.SystemState;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.udb.UserDictionaryIterator;
import com.nuance.swype.preference.ConnectionAwarePreferences;
import com.nuance.swype.preference.DialogPrefs;
import com.nuance.swype.startup.StartupActivity;
import com.nuance.swype.stats.StatisticsManager;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swypeconnect.ac.ACAccount;
import com.nuance.swypeconnect.ac.ACAccountService;
import com.nuance.swypeconnect.ac.ACDevice;
import java.io.File;

/* loaded from: classes.dex */
public abstract class MyWordsPrefs extends DialogPrefs implements ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener {
    protected static final String ACCOUNT_MANAGE_KEY = "pref_account_manage";
    protected static final String ACTIVATION_CODE_KEY = "pref_activation_code";
    static final int ACTIVATION_DIALOG = 5;
    static final int ASK_BEFORE_ADD_DIALOG = 7;
    static final int CONNECTION_DIALOG = 2;
    protected static final String CONTRIBUTE_USAGE_DATA_KEY = "pref_connect_contribute_usage_data";
    protected static final String DATA_MANAGEMENT_CAT = "pref_data_management";
    protected static final String DICTIONARY_MANAGEMENT_CAT = "dictionary_management";
    static final int DISABLE_USAGE_DATA_WARNING = 6;
    protected static final String EDIT_MY_DICTIONARY = "edit_dictionary_preference_key";
    protected static final String ERASE_UDB_KEY = "erase_udb";
    protected static final String HOTWORDS_KEY = "pref_connect_hotwords";
    public static final String INVALID_CODE_KEY = "invalid_account_key";
    public static final int MY_WORDS_PREFS_XML = R.xml.mywordspreferences;
    static final int NO_NETWORK_DIALOG = 3;
    protected static final String PERSONAL_DICTIONARY_BEHAVIOR_KEY = "edit_dictionary_preference_change_behavior_key";
    protected static final String REGISTER_KEY = "pref_account_register";
    protected static final int REQUEST_CODE_DATA_OPT_IN = 1;
    protected static final int REQUEST_CODE_LIVING_LANGUAGE = 3;
    protected static final int REQUEST_CODE_PERSONALIZATION_TOS = 4;
    static final int SHOW_ERASE_UDB_DIALOG = 8;
    static final int SHOW_NETWORK_NOTIFICATION_DIALOG = 9;
    protected static final String SOCIAL_INTEGRATION = "pref_social_integration";
    private final Activity activity;
    private Connect connect;
    private ConnectedStatus connectedStatus;
    private ConnectionAwarePreferences connection;
    protected String mPrefKey;
    private PreferenceScreen screen;
    private boolean settingsChanged;
    private final UserDictionaryIterator userDictionaryIterator;
    private ACAccountService.ACAccountCallback accountCallback = new ACAccountService.ACAccountCallback() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.1
        @Override // com.nuance.swypeconnect.ac.ACAccountService.ACAccountCallback
        public void linked() {
            MyWordsPrefs.this.log.v("ACAccountCallback.linked()");
            MyWordsPrefs.this.cancelAllActiveDialogs();
            MyWordsPrefs.this.rebuildSettings();
            MyWordsPrefs.this.settingsChanged = true;
        }

        @Override // com.nuance.swypeconnect.ac.ACAccountService.ACAccountCallback
        public void devicesUpdated(ACDevice[] devices) {
        }

        @Override // com.nuance.swypeconnect.ac.ACAccountService.ACAccountCallback
        public void created() {
            MyWordsPrefs.this.log.v("ACAccountCallback.created()");
            MyWordsPrefs.this.rebuildSettings();
            MyWordsPrefs.this.settingsChanged = true;
        }

        @Override // com.nuance.swypeconnect.ac.ACAccountService.ACAccountCallback
        public void onError(int status, String message) {
            MyWordsPrefs.this.log.v("ACAccountCallback.onError(" + status + ", " + message + ")");
            if (status == 512 || status == 256 || status == 1024) {
                MyWordsPrefs.this.rebuildSettings();
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACAccountService.ACAccountCallback
        public void status(int status, String message) {
            MyWordsPrefs.this.log.v("ACAccountCallback.accountStatus(" + status + ", " + message + ")");
            if (status == 768) {
                Bundle b = new Bundle();
                b.putBoolean(MyWordsPrefs.INVALID_CODE_KEY, true);
                MyWordsPrefs.this.doShowDialog(5, b);
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACAccountService.ACAccountCallback
        public void verifyFailed() {
            MyWordsPrefs.this.log.v("verifyFailed()");
            Bundle b = new Bundle();
            b.putBoolean(MyWordsPrefs.INVALID_CODE_KEY, true);
            MyWordsPrefs.this.doShowDialog(5, b);
        }
    };
    private Connect.LegalDocumentPresenter livingLanguageLegalPresenter = new Connect.LegalDocumentPresenter() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.2
        Runnable accepted = null;

        @Override // com.nuance.swype.connect.Connect.LegalDocumentPresenter
        public boolean presentLegalRequirements(boolean tosRequired, boolean optInRequired, Runnable accepted) {
            this.accepted = accepted;
            Dialog activationDialog = MyWordsPrefs.this.getActiveDialog(9);
            UserPreferences userPrefs = UserPreferences.from(MyWordsPrefs.this.activity);
            if (!MyWordsPrefs.this.activity.getResources().getBoolean(R.bool.enable_china_connect_special) || !userPrefs.shouldShowNetworkAgreementDialog()) {
                MyWordsPrefs.this.showLegalRequirements(3, tosRequired, optInRequired, null);
            } else if (activationDialog != null && !activationDialog.isShowing()) {
                MyWordsPrefs.this.doShowDialog(9, null);
            }
            return false;
        }

        @Override // com.nuance.swype.connect.Connect.LegalDocumentPresenter
        public void accepted() {
            if (this.accepted != null) {
                this.accepted.run();
                MyWordsPrefs.this.setStatsCollection(true);
            }
        }
    };
    Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER);
    private Preference.OnPreferenceClickListener eraseUdbConfirmation = new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.8
        @Override // android.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            MyWordsPrefs.this.doShowDialog(8, null);
            return true;
        }
    };
    private Preference.OnPreferenceClickListener connectionListener = new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.9
        @Override // android.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            if (key != null) {
                MyWordsPrefs.this.mPrefKey = key;
            }
            MyWordsPrefs.this.doShowDialog(2, null);
            return true;
        }
    };
    private Preference.OnPreferenceClickListener showAskBeforeAddListener = new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.10
        @Override // android.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            MyWordsPrefs.this.doShowDialog(7, null);
            return true;
        }
    };
    DialogPrefs.DialogCreator connectionDialog = new DialogPrefs.DialogCreator() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.11
        @Override // com.nuance.swype.preference.DialogPrefs.DialogCreator
        public Dialog doCreateDialog(Context context, Bundle args) {
            return MyWordsPrefs.this.connection.getConnectDialog(context).create();
        }
    };
    DialogPrefs.DialogCreator activationDialog = new DialogPrefs.DialogCreator() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.12
        @Override // com.nuance.swype.preference.DialogPrefs.DialogCreator
        public Dialog doCreateDialog(final Context context, Bundle args) {
            String accountIdentifier = "";
            if (MyWordsPrefs.this.connect.getAccounts().getActiveAccount() != null) {
                accountIdentifier = MyWordsPrefs.this.connect.getAccounts().getActiveAccount().getIdentifier();
            }
            boolean invalidCode = args.getBoolean(MyWordsPrefs.INVALID_CODE_KEY);
            final View activationCodeView = LayoutInflater.from(MyWordsPrefs.this.activity).inflate(R.layout.activation_code_dialog, (ViewGroup) null);
            ((TextView) activationCodeView.findViewById(R.id.activation_email)).setText(accountIdentifier);
            if (invalidCode) {
                activationCodeView.findViewById(R.id.incorrect_code).setVisibility(0);
            }
            final AlertProgressDialog activateDialog = new AlertProgressDialog(context);
            activateDialog.setView(activationCodeView);
            if (Build.VERSION.SDK_INT < 21) {
                activateDialog.setButton(-1, MyWordsPrefs.this.activity.getText(R.string.ok_button), new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.12.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        MyWordsPrefs.this.onClickConfirmButton(context, activateDialog, activationCodeView);
                    }
                });
                activateDialog.setButton(-2, MyWordsPrefs.this.activity.getText(R.string.change_email_button), new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.12.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        MyWordsPrefs.this.onClickEmailChangeButton(activateDialog);
                    }
                });
                activateDialog.setButton(-3, MyWordsPrefs.this.activity.getText(R.string.resend_code_button), new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.12.3
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        MyWordsPrefs.this.onClickResendCodeButton(activateDialog);
                    }
                });
            } else {
                ((Button) activationCodeView.findViewById(R.id.change_email)).setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.12.4
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        MyWordsPrefs.this.onClickEmailChangeButton(activateDialog);
                    }
                });
                ((Button) activationCodeView.findViewById(R.id.resend_code)).setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.12.5
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        MyWordsPrefs.this.onClickResendCodeButton(activateDialog);
                    }
                });
                ((Button) activationCodeView.findViewById(R.id.confirm_ok)).setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.12.6
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        MyWordsPrefs.this.onClickConfirmButton(context, activateDialog, activationCodeView);
                    }
                });
            }
            ((EditText) activationCodeView.findViewById(R.id.editText_activation_code)).addTextChangedListener(new TextWatcher() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.12.7
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                    Button positive;
                    if (Build.VERSION.SDK_INT < 21) {
                        positive = activateDialog.getButton(-1);
                    } else {
                        positive = (Button) activationCodeView.findViewById(R.id.confirm_ok);
                    }
                    if (s.length() >= 4) {
                        positive.setEnabled(true);
                    } else {
                        positive.setEnabled(false);
                    }
                }
            });
            activateDialog.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.12.8
                @Override // android.content.DialogInterface.OnShowListener
                public void onShow(DialogInterface dialog) {
                    Button positive;
                    if (Build.VERSION.SDK_INT < 21) {
                        positive = activateDialog.getButton(-1);
                    } else {
                        positive = (Button) activationCodeView.findViewById(R.id.confirm_ok);
                    }
                    if (((EditText) activateDialog.findViewById(R.id.editText_activation_code)).getText().length() < 4) {
                        positive.setEnabled(false);
                    }
                }
            });
            return activateDialog;
        }
    };
    DialogPrefs.DialogCreator noNetworkDialog = new DialogPrefs.DialogCreator() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.13
        @Override // com.nuance.swype.preference.DialogPrefs.DialogCreator
        public Dialog doCreateDialog(Context context, Bundle args) {
            return new AlertDialog.Builder(context).setTitle(R.string.no_network_available).setMessage(R.string.no_network_try_again_msg).setNegativeButton(R.string.dismiss_button, (DialogInterface.OnClickListener) null).create();
        }
    };
    DialogPrefs.DialogCreator dataOptOutWarningDialog = new DialogPrefs.DialogCreator() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.14
        @Override // com.nuance.swype.preference.DialogPrefs.DialogCreator
        public Dialog doCreateDialog(Context context, Bundle args) {
            return new AlertDialog.Builder(context).setTitle(R.string.usage_statistics_log_title).setMessage(R.string.usage_data_opt_out_disclaimer).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.14.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    MyWordsPrefs.this.setStatsCollection(true);
                }
            }).setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.14.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    MyWordsPrefs.this.setStatsCollection(false);
                    MyWordsPrefs.this.rebuildSettings();
                    StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(MyWordsPrefs.this.activity);
                    if (sessionScribe != null) {
                        sessionScribe.recordSettingsChange(MyWordsPrefs.CONTRIBUTE_USAGE_DATA_KEY, false, true);
                    }
                }
            }).create();
        }
    };

    protected abstract PreferenceScreen addPreferences();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void doCancelDialog(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void doShowDialog(int i, Bundle bundle);

    protected abstract void doStartActivity(Intent intent);

    protected abstract void doStartActivityForResult(Intent intent, int i);

    protected abstract void showPreferenceFragment(Preference preference);

    @TargetApi(23)
    public MyWordsPrefs(Activity activity) {
        this.activity = activity;
        this.connect = Connect.from(activity);
        this.userDictionaryIterator = IMEApplication.from(activity).createUserDictionaryIterator(InputMethods.from(activity).getCurrentInputLanguage());
        registerDialog(2, this.connectionDialog);
        registerDialog(3, this.noNetworkDialog);
        registerDialog(5, this.activationDialog);
        registerDialog(6, this.dataOptOutWarningDialog);
        this.connection = new ConnectionAwarePreferences(activity) { // from class: com.nuance.swype.input.settings.MyWordsPrefs.3
            @Override // com.nuance.swype.preference.ConnectionAwarePreferences
            public void showConnectDialog() {
                MyWordsPrefs.this.doShowDialog(2, null);
            }
        };
        UsageData.recordScreenVisited(UsageData.Screen.MY_WORDS);
    }

    public void onStart() {
        this.connectedStatus = new ConnectedStatus(this.activity) { // from class: com.nuance.swype.input.settings.MyWordsPrefs.4
            @Override // com.nuance.swype.connect.ConnectedStatus
            public void onConnectionChanged(boolean isConnected) {
                MyWordsPrefs.this.rebuildSettings();
                if (isConnected) {
                    UserPreferences userPrefs = UserPreferences.from(MyWordsPrefs.this.activity);
                    if (userPrefs.isActivationCodePopupShown()) {
                        MyWordsPrefs.this.doShowDialog(5, null);
                        userPrefs.setActivationCodePopupShown(false);
                        return;
                    }
                    return;
                }
                Dialog activationDialog = MyWordsPrefs.this.getActiveDialog(5);
                if (activationDialog != null && activationDialog.isShowing()) {
                    MyWordsPrefs.this.doCancelDialog(5);
                    MyWordsPrefs.this.doShowDialog(2, null);
                }
            }
        };
        this.connectedStatus.register();
        this.settingsChanged = false;
    }

    public void onResume() {
        this.log.v("onResume");
        this.connect.getAccounts().registerCallback(this.accountCallback);
        rebuildSettings();
    }

    public void onPause() {
        this.log.v("onPause");
        this.connect.getAccounts().unregisterCallback(this.accountCallback);
    }

    public void onStop() {
        this.connectedStatus.unregister();
        if (this.settingsChanged) {
            recordMyWordsSettings();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == -1) {
                    setStatsCollection(true);
                    return;
                }
                return;
            case 2:
            default:
                this.log.d("No response to request code: ", Integer.valueOf(requestCode));
                return;
            case 3:
                if (resultCode == -1) {
                    this.livingLanguageLegalPresenter.accepted();
                    return;
                }
                return;
            case 4:
                if (resultCode == -1) {
                    Intent intent = new Intent(this.activity.getApplicationContext(), (Class<?>) SocialIntegrationPrefsFragmentActivity.class);
                    doStartActivity(intent);
                    return;
                }
                return;
        }
    }

    protected final Preference findPreference(String key) {
        if (this.screen != null) {
            return this.screen.findPreference(key);
        }
        return null;
    }

    private void removePreference(String categoryName, String prefName) {
        PreferenceCategory category = (PreferenceCategory) findPreference(categoryName);
        Preference preference = findPreference(prefName);
        if (category != null && preference != null) {
            category.removePreference(preference);
        }
    }

    @TargetApi(23)
    private void addDlmExportPreference(PreferenceCategory perPrefCat) {
        if (perPrefCat != null) {
            String state = Environment.getExternalStorageState();
            if ("mounted".equals(state)) {
                Preference exportDlmPref = new Preference(this.activity);
                exportDlmPref.setKey("export_dlm_pref");
                exportDlmPref.setTitle("Export Dlm to sd card");
                perPrefCat.addPreference(exportDlmPref);
                exportDlmPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.5
                    final XT9CoreAlphaInput alphaInput;
                    final int currentLanguage;

                    {
                        this.alphaInput = IMEApplication.from(MyWordsPrefs.this.activity).getSwypeCoreLibMgr().getXT9CoreAlphaInputSession();
                        this.currentLanguage = IMEApplication.from(MyWordsPrefs.this.activity).getInputMethods().getCurrentInputLanguage().getCoreLanguageId();
                    }

                    @Override // android.preference.Preference.OnPreferenceClickListener
                    public boolean onPreferenceClick(Preference preference) {
                        if (Build.VERSION.SDK_INT >= 23 && MyWordsPrefs.this.activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                            return true;
                        }
                        if (!"mounted".equals(Environment.getExternalStorageState())) {
                            MyWordsPrefs.this.showDlmExportMessage("sd card is not mounted or not available.", true);
                            return true;
                        }
                        this.alphaInput.setLanguage(this.currentLanguage);
                        if (MyWordsPrefs.this.activity.getExternalFilesDir(null) == null) {
                            return false;
                        }
                        File file = new File(MyWordsPrefs.this.activity.getExternalFilesDir(null).getAbsolutePath(), "exported_dlm.bin");
                        if (this.alphaInput.dlmExport(file.getAbsolutePath()) == 0) {
                            MyWordsPrefs.this.showDlmExportMessage("Failed to export. DLM may not available.", true);
                            return true;
                        }
                        MyWordsPrefs.this.showDlmExportMessage("DLM export to:\n" + file.getAbsolutePath(), false);
                        return true;
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDlmExportMessage(String message, boolean error) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this.activity);
        if (error) {
            dlgAlert.setIcon(android.R.drawable.ic_dialog_alert);
        }
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("Swype");
        dlgAlert.setPositiveButton("OK", (DialogInterface.OnClickListener) null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    @SuppressLint({"NewApi"})
    protected final void rebuildSettings() {
        if (this.screen != null) {
            this.screen.removeAll();
        }
        this.screen = addPreferences();
        InputMethods im = InputMethods.from(this.activity);
        AppPreferences appPrefs = AppPreferences.from(this.activity);
        if (im.isCJKOnDevice()) {
            InputMethods.Language lang = im.getCurrentInputLanguage();
            if (lang.isCJK()) {
                removePreference(DICTIONARY_MANAGEMENT_CAT, PERSONAL_DICTIONARY_BEHAVIOR_KEY);
            }
            if ((!appPrefs.isChinesePersonalDictionaryEnabled() && lang.isChineseLanguage()) || ((!appPrefs.isKoreanPersonalDictionaryEnabled() && lang.isKoreanLanguage()) || (!appPrefs.isJapanesePersonalDictionaryEnabled() && lang.isJapaneseLanguage()))) {
                removePreference(DICTIONARY_MANAGEMENT_CAT, EDIT_MY_DICTIONARY);
            }
        }
        if (!UserPreferences.from(this.activity).getAutoCorrection() || !appPrefs.isShowAskBeforeAddEnabled() || im.getCurrentInputLanguage().isNonSpacedLanguage()) {
            removePreference(DICTIONARY_MANAGEMENT_CAT, PERSONAL_DICTIONARY_BEHAVIOR_KEY);
        }
        Preference aBehaviorPref = this.screen.findPreference(PERSONAL_DICTIONARY_BEHAVIOR_KEY);
        if (aBehaviorPref != null) {
            aBehaviorPref.setOnPreferenceClickListener(this.showAskBeforeAddListener);
        }
        Preference eraseUdbPref = findPreference(ERASE_UDB_KEY);
        if (eraseUdbPref != null) {
            im.syncWithCurrentUserConfiguration();
            if (im.getCurrentInputLanguage() != null) {
                eraseUdbPref.setOnPreferenceClickListener(this.eraseUdbConfirmation);
            }
        }
        if (!this.connect.isLicensed() || (!this.connect.isStarted() && !this.activity.getResources().getBoolean(R.bool.enable_china_connect_special))) {
            removePreference(DICTIONARY_MANAGEMENT_CAT, REGISTER_KEY);
            removePreference(DICTIONARY_MANAGEMENT_CAT, ACTIVATION_CODE_KEY);
            removePreference(DICTIONARY_MANAGEMENT_CAT, ACCOUNT_MANAGE_KEY);
            removePreference(DICTIONARY_MANAGEMENT_CAT, HOTWORDS_KEY);
            removePreference(DICTIONARY_MANAGEMENT_CAT, SOCIAL_INTEGRATION);
            removePreference(DATA_MANAGEMENT_CAT, CONTRIBUTE_USAGE_DATA_KEY);
        } else {
            PreferenceCategory dicManagementCat = (PreferenceCategory) findPreference(DICTIONARY_MANAGEMENT_CAT);
            ACAccount account = this.connect.getAccounts().getActiveAccount();
            if (account == null) {
                removePreference(DICTIONARY_MANAGEMENT_CAT, ACTIVATION_CODE_KEY);
                removePreference(DICTIONARY_MANAGEMENT_CAT, ACCOUNT_MANAGE_KEY);
            } else if (!account.isLinked()) {
                removePreference(DICTIONARY_MANAGEMENT_CAT, REGISTER_KEY);
                Preference p = findPreference(ACCOUNT_MANAGE_KEY);
                if (p != null) {
                    p.setEnabled(false);
                }
            } else {
                removePreference(DICTIONARY_MANAGEMENT_CAT, REGISTER_KEY);
                removePreference(DICTIONARY_MANAGEMENT_CAT, ACTIVATION_CODE_KEY);
            }
            addConnectionAwarePreference(dicManagementCat, findPreference(REGISTER_KEY));
            addConnectionAwarePreference(dicManagementCat, findPreference(ACTIVATION_CODE_KEY));
            CheckBoxPreference hotwords = (CheckBoxPreference) findPreference(HOTWORDS_KEY);
            if (hotwords != null) {
                hotwords.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.6
                    @Override // android.preference.Preference.OnPreferenceChangeListener
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Connect.LivingLanguage l = MyWordsPrefs.this.connect.getLivingLanguage(MyWordsPrefs.this.livingLanguageLegalPresenter);
                        boolean newCheckedState = ((Boolean) newValue).booleanValue();
                        if (newCheckedState) {
                            l.enable();
                            UserPreferences userPrefs = UserPreferences.from(MyWordsPrefs.this.activity);
                            if (!MyWordsPrefs.this.activity.getResources().getBoolean(R.bool.enable_china_connect_special) || !userPrefs.shouldShowNetworkAgreementDialog()) {
                                if (ConnectLegal.from(MyWordsPrefs.this.activity).isOptInAccepted() && ConnectLegal.from(MyWordsPrefs.this.activity).isTosAccepted()) {
                                    MyWordsPrefs.this.setStatsCollection(true);
                                }
                            } else {
                                MyWordsPrefs.this.doShowDialog(9, null);
                                return false;
                            }
                        } else {
                            l.disable();
                        }
                        if (newCheckedState == l.isEnabled()) {
                            MyWordsPrefs.this.settingsChanged = true;
                        }
                        return newCheckedState == l.isEnabled();
                    }
                });
                Connect.LivingLanguage l = this.connect.getLivingLanguage(this.livingLanguageLegalPresenter);
                hotwords.setChecked(l.isEnabled());
            }
            addConnectionAwarePreference(dicManagementCat, hotwords);
            if (!AppPreferences.from(this.activity).isPersonalizationEnable()) {
                dicManagementCat.removePreference(findPreference(SOCIAL_INTEGRATION));
            }
            CheckBoxPreference usageData = (CheckBoxPreference) findPreference(CONTRIBUTE_USAGE_DATA_KEY);
            if (usageData != null) {
                usageData.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.7
                    @Override // android.preference.Preference.OnPreferenceChangeListener
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        UserPreferences userPrefs = UserPreferences.from(MyWordsPrefs.this.activity);
                        if (MyWordsPrefs.this.activity.getResources().getBoolean(R.bool.enable_china_connect_special) && userPrefs.shouldShowNetworkAgreementDialog() && MyWordsPrefs.this.activity.getResources().getBoolean(R.bool.enable_china_connect_special) && userPrefs.shouldShowNetworkAgreementDialog()) {
                            MyWordsPrefs.this.doShowDialog(9, null);
                            return false;
                        }
                        boolean change = false;
                        boolean oldStateChecked = (preference instanceof CheckBoxPreference) && ((CheckBoxPreference) preference).isChecked();
                        if (!oldStateChecked && MyWordsPrefs.this.showLegalRequirements(1, true, true, null)) {
                            return false;
                        }
                        if (oldStateChecked) {
                            MyWordsPrefs.this.doShowDialog(6, null);
                        } else {
                            change = true;
                            MyWordsPrefs.this.setStatsCollection(true);
                            StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(MyWordsPrefs.this.activity);
                            if (sessionScribe != null) {
                                boolean value = ((Boolean) newValue).booleanValue();
                                sessionScribe.recordSettingsChange(MyWordsPrefs.CONTRIBUTE_USAGE_DATA_KEY, Boolean.valueOf(value), Boolean.valueOf(value ? false : true));
                            }
                        }
                        return change;
                    }
                });
                usageData.setChecked(Connect.from(this.activity).isDataUsageOptInAccepted());
            }
        }
        boolean disableBackupAndSync = (this.activity == null || this.activity.getResources().getBoolean(R.bool.enable_backup_and_sync)) ? false : true;
        if (!disableBackupAndSync) {
            Connect connect = IMEApplication.from(this.activity).getConnect();
            SystemState systemState = IMEApplication.from(this.activity).getSystemState();
            String countryCode = connect.getISOCountry();
            this.log.d("rebuildSettings GeoIP: " + countryCode + "; MCC: " + systemState.getNetworkOperatorMCC());
            if ("RU".equals(countryCode) || systemState.getNetworkOperatorMCC() == 250) {
                disableBackupAndSync = true;
            }
        }
        if (disableBackupAndSync) {
            removePreference(DICTIONARY_MANAGEMENT_CAT, REGISTER_KEY);
            removePreference(DICTIONARY_MANAGEMENT_CAT, ACTIVATION_CODE_KEY);
            removePreference(DICTIONARY_MANAGEMENT_CAT, ACCOUNT_MANAGE_KEY);
        }
    }

    private void addConnectionAwarePreference(PreferenceCategory category, Preference preference) {
        if (category != null && preference != null && !this.connectedStatus.isConnected()) {
            preference.setOnPreferenceClickListener(this.connectionListener);
        }
    }

    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {
        String key = pref.getKey();
        if (key.equals(REGISTER_KEY) || key.equals(ACCOUNT_MANAGE_KEY) || key.equals(ACTIVATION_CODE_KEY) || key.equals(HOTWORDS_KEY) || key.equals(SOCIAL_INTEGRATION) || key.equals(CONTRIBUTE_USAGE_DATA_KEY)) {
            UserPreferences userPrefs = UserPreferences.from(this.activity);
            if (this.activity.getResources().getBoolean(R.bool.enable_china_connect_special) && userPrefs.shouldShowNetworkAgreementDialog()) {
                this.mPrefKey = key;
                if (key.equals(HOTWORDS_KEY) || key.equals(CONTRIBUTE_USAGE_DATA_KEY)) {
                    return true;
                }
                doShowDialog(9, null);
                return true;
            }
            if (this.connectedStatus.isConnected()) {
                if (key.equals(ACTIVATION_CODE_KEY)) {
                    doShowDialog(5, null);
                    return true;
                }
                if (key.equals(REGISTER_KEY)) {
                    startRegisterActivity();
                    return true;
                }
                if (key.equals(SOCIAL_INTEGRATION) && showLegalRequirements(4, true, false, null)) {
                    return true;
                }
            }
            return false;
        }
        if (key.equals(ACTIVATION_CODE_KEY)) {
            doShowDialog(5, null);
            return true;
        }
        if (!key.equals(REGISTER_KEY)) {
            return false;
        }
        startRegisterActivity();
        return true;
    }

    protected void setStatsCollection(boolean enabled) {
        Preference usageData = findPreference(CONTRIBUTE_USAGE_DATA_KEY);
        if (usageData != null) {
            ((CheckBoxPreference) usageData).setChecked(enabled);
            Connect.from(this.activity).setContributeUsageData(enabled);
        }
        rebuildSettings();
        if (usageData != null) {
            this.settingsChanged = true;
        }
    }

    protected boolean validate(String code) {
        boolean processed = this.connect.getAccounts().verifyAccount(code);
        rebuildSettings();
        return processed;
    }

    protected void sendReverify() {
        this.connect.getAccounts().reverify();
    }

    protected void startRegisterActivity() {
        Intent intent = new Intent(this.activity, (Class<?>) StartupActivity.class);
        intent.putExtra("launch_mode", "standalone");
        intent.putExtra("launch_screen", StartupActivity.ACCOUNT_REGISTER);
        intent.setFlags(872415232);
        this.activity.startActivity(intent);
        Connect.from(this.activity).getLegal().isTosAccepted();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClickEmailChangeButton(AlertProgressDialog dlg) {
        if (!this.connectedStatus.isConnected()) {
            doShowDialog(2, null);
        } else {
            startRegisterActivity();
            dlg.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClickResendCodeButton(AlertProgressDialog dlg) {
        if (!this.connectedStatus.isConnected()) {
            doShowDialog(2, null);
        } else {
            sendReverify();
            dlg.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClickConfirmButton(Context context, AlertProgressDialog dlg, View v) {
        if (!this.connectedStatus.isConnected()) {
            doShowDialog(2, null);
            dlg.dismiss();
            return;
        }
        EditText code = (EditText) v.findViewById(R.id.editText_activation_code);
        ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(code.getWindowToken(), 0);
        validate(code.getText().toString());
        dlg.inProgress();
        dlg.updateView();
    }

    public Dialog createEraseUdbConfirmationDlg() {
        return new AlertDialog.Builder(this.activity).setTitle(R.string.clear_language_data).setIcon(R.drawable.swype_logo).setMessage(R.string.pref_clear_lang_data_dialog).setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.15
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int whichButton) {
                MyWordsPrefs.this.userDictionaryIterator.eraseAll();
                MyWordsPrefs.this.connect.getSync().disable();
                MyWordsPrefs.this.connect.getLivingLanguage(null).disable();
                MyWordsPrefs.connectResetHotwords(MyWordsPrefs.this.activity);
                MyWordsPrefs.this.rebuildSettings();
                MyWordsPrefs.this.settingsChanged = true;
            }
        }).setNegativeButton(R.string.no_button, (DialogInterface.OnClickListener) null).create();
    }

    public Dialog createAskBeforeAddCheckDlg() {
        String[] str_items = {this.activity.getString(R.string.dictionary_explicitadd), this.activity.getString(R.string.dictionary_autoadd)};
        return new AlertDialog.Builder(this.activity).setIcon(R.drawable.swype_logo).setTitle(R.string.change_dictionary_behavior_title).setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null).setSingleChoiceItems(str_items, UserPreferences.from(this.activity).getAskBeforeAdd() ? 0 : 1, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.MyWordsPrefs.16
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                boolean value = which == 0;
                UserPreferences.from(MyWordsPrefs.this.activity).setAskBeforeAdd(value);
                dialog.dismiss();
                StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(MyWordsPrefs.this.activity);
                if (sessionScribe != null) {
                    sessionScribe.recordSettingsChange("Change Dictionary Behavior", Boolean.valueOf(value), Boolean.valueOf(value ? false : true));
                }
                MyWordsPrefs.this.settingsChanged = true;
            }
        }).create();
    }

    /* loaded from: classes.dex */
    public static class AlertProgressDialog extends AlertDialog {
        public static final String PREF_PROGRESS_SHOWING = "progress_showing";
        private boolean progressShowing;

        protected AlertProgressDialog(Context context) {
            super(context);
        }

        public void inProgress() {
            this.progressShowing = true;
        }

        public void updateView() {
            Button positive;
            Button negative;
            Button neutral;
            View main = findViewById(R.id.main);
            View progress = findViewById(R.id.progress);
            if (Build.VERSION.SDK_INT < 21) {
                positive = getButton(-1);
                negative = getButton(-2);
                neutral = getButton(-3);
            } else {
                positive = (Button) main.findViewById(R.id.confirm_ok);
                negative = (Button) main.findViewById(R.id.change_email);
                neutral = (Button) main.findViewById(R.id.resend_code);
            }
            if (main != null) {
                main.setVisibility(4);
            }
            if (progress != null) {
                progress.setVisibility(0);
            }
            if (positive != null) {
                positive.setEnabled(!this.progressShowing);
            }
            if (negative != null) {
                negative.setEnabled(!this.progressShowing);
            }
            if (neutral != null) {
                neutral.setEnabled(!this.progressShowing);
            }
            if (((EditText) main.findViewById(R.id.editText_activation_code)).getText().length() >= 4) {
                positive.setEnabled(this.progressShowing ? false : true);
            } else {
                positive.setEnabled(false);
            }
        }

        public boolean isProgressShowing() {
            return this.progressShowing;
        }

        @Override // android.app.AlertDialog, android.app.Dialog
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (Build.VERSION.SDK_INT < 21) {
                Button buttonPos = getButton(-1);
                Button buttonNeg = getButton(-2);
                Button buttonNeu = getButton(-3);
                setEqualWidth(buttonPos);
                setEqualWidth(buttonNeg);
                setEqualWidth(buttonNeu);
            }
        }

        private void setEqualWidth(Button button) {
            if (button != null) {
                button.setMaxLines(5);
                ViewGroup.LayoutParams lp = button.getLayoutParams();
                if (lp instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams linearLp = (LinearLayout.LayoutParams) lp;
                    linearLp.width = 0;
                    linearLp.weight = 1.0f;
                    button.setLayoutParams(linearLp);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean showLegalRequirements(int requestCode, boolean tosRequired, boolean optInRequired, Bundle resultData) {
        Intent intent = ConnectLegal.getLegalActivitiesStartIntent(this.activity, tosRequired, optInRequired, resultData);
        if (intent == null) {
            return false;
        }
        doStartActivityForResult(intent, requestCode);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void connectResetHotwords(Context context) {
        Connect.from(context).getLivingLanguage(null).disable();
        StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(context);
        if (sessionScribe != null) {
            sessionScribe.recordSettingsChange("Clear language data", "Clear:Yes", "Clear:NO");
        }
    }

    public static void resetToDefault(Context context) {
        IMEApplication.from(context).createUserDictionaryIterator(InputMethods.from(context).getCurrentInputLanguage()).eraseAll();
        Connect.from(context).setContributeUsageData(false);
        Connect.Sync sync = Connect.from(context).getSync();
        if (sync.isEnabled()) {
            int coreId = 1;
            InputMethods.Language language = InputMethods.from(context).getCurrentInputLanguage();
            if (language.isJapaneseLanguage()) {
                coreId = 4;
            } else if (language.isChineseLanguage() || language.isChineseSimplified() || language.isChineseTraditional()) {
                coreId = 3;
            } else if (language.isKoreanLanguage()) {
                coreId = 2;
            }
            sync.requestBackup(coreId);
            sync.requestRestore(coreId);
        }
        if (Connect.from(context).getLivingLanguage(null).isEnabled()) {
            connectResetHotwords(context);
        }
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
        StatisticsManager.SessionStatsScribe sessionScribe;
        StatisticsManager.SessionStatsScribe sessionScribe2;
        if (!onPreferenceTreeClick(this.screen, findPreference(this.mPrefKey))) {
            String str = this.mPrefKey;
            char c = 65535;
            switch (str.hashCode()) {
                case -1542528098:
                    if (str.equals(SOCIAL_INTEGRATION)) {
                        c = 0;
                        break;
                    }
                    break;
                case -1214409389:
                    if (str.equals(ACCOUNT_MANAGE_KEY)) {
                        c = 1;
                        break;
                    }
                    break;
                case -752477719:
                    if (str.equals(CONTRIBUTE_USAGE_DATA_KEY)) {
                        c = 3;
                        break;
                    }
                    break;
                case 230242509:
                    if (str.equals(HOTWORDS_KEY)) {
                        c = 2;
                        break;
                    }
                    break;
                case 370205457:
                    if (str.equals(REGISTER_KEY)) {
                        c = 4;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                case 1:
                    showPreferenceFragment(findPreference(this.mPrefKey));
                    break;
                case 2:
                    if (((CheckBoxPreference) findPreference(HOTWORDS_KEY)) != null && !showLegalRequirements(3, true, true, null)) {
                        this.livingLanguageLegalPresenter.accepted();
                        break;
                    }
                    break;
                case 3:
                    if (((CheckBoxPreference) findPreference(CONTRIBUTE_USAGE_DATA_KEY)) != null && !showLegalRequirements(1, true, true, null) && (sessionScribe2 = StatisticsManager.getSessionStatsScribe(this.activity)) != null) {
                        sessionScribe2.recordSettingsChange(CONTRIBUTE_USAGE_DATA_KEY, true, false);
                        break;
                    }
                    break;
                case 4:
                    if (((CheckBoxPreference) findPreference(REGISTER_KEY)) != null && !showLegalRequirements(1, true, true, null) && (sessionScribe = StatisticsManager.getSessionStatsScribe(this.activity)) != null) {
                        sessionScribe.recordSettingsChange(REGISTER_KEY, true, false);
                        break;
                    }
                    break;
                default:
                    this.log.d("Unknown key:", this.mPrefKey);
                    break;
            }
        }
        return true;
    }

    private void recordMyWordsSettings() {
        UsageData.BackupAndSyncStatus status;
        IMEApplication app = IMEApplication.from(this.activity.getApplicationContext());
        UserPreferences userPrefs = app.getUserPreferences();
        ACAccount account = app.getConnect().getAccounts().getActiveAccount();
        if (account == null) {
            status = UsageData.BackupAndSyncStatus.UNREGISTERED;
        } else if (!account.isLinked()) {
            status = UsageData.BackupAndSyncStatus.REGISTERED;
        } else if (app.getConnect().getSync().isEnabled()) {
            status = UsageData.BackupAndSyncStatus.ON;
        } else {
            status = UsageData.BackupAndSyncStatus.OFF;
        }
        boolean livingLanguageEnabled = app.getConnect().getLivingLanguage(null).isEnabled();
        boolean askBeforeAdd = userPrefs.getAskBeforeAdd();
        UsageData.recordMyWordsSettings(status, livingLanguageEnabled, askBeforeAdd);
    }
}
