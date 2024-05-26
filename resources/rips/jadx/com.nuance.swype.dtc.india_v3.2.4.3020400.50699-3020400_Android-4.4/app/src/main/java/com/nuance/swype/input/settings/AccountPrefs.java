package com.nuance.swype.input.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.connect.ConnectedStatus;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.preference.ConnectionAwarePreferences;
import com.nuance.swype.stats.StatisticsManager;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACAccount;
import com.nuance.swypeconnect.ac.ACAccountService;
import com.nuance.swypeconnect.ac.ACDLMSyncService;
import com.nuance.swypeconnect.ac.ACDevice;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/* loaded from: classes.dex */
public abstract class AccountPrefs implements Preference.OnPreferenceClickListener {
    private static final String KEY_ACCOUNT = "pref_account";
    private static final String KEY_DELETE_ACCOUNT = "pref_account_delete";
    private static final String KEY_ENABLED = "pref_backup_enabled";
    private static final String KEY_MY_DEVICES_CAT = "account_my_devices";
    private static final String KEY_OPTIONS = "account_options";
    private static final String KEY_SYNC_ACCOUNT = "pref_account_sync_now";
    private static final String KEY_YOUR_ACCOUNT = "your_account";
    private static final long MILLIS_IN_DAY = 86400000;
    private static final long MILLIS_IN_HOUR = 3600000;
    private static final long MILLIS_IN_MINUTE = 60000;
    private static final int REQUEST_CODE_BACKUP_SYNC = 1;
    private final Activity activity;
    private Connect connect;
    private final ConnectedStatus connectedStatus;
    private final ConnectionAwarePreferences connection;
    Preference dlmEnabled;
    private PreferenceScreen screen;
    private boolean settingsChanged;
    private static final LogManager.Log log = LogManager.getLog("AccountPrefs");
    public static final int ACCOUNT_PREFS_XML = R.xml.connect_account_preferences;
    private long mLastClickTime = 0;
    private AsyncTask<Void, Void, BuildSettingHolder> currentBuildingSettingAsync = null;
    private ACAccountService.ACAccountCallback callback = new ACAccountService.ACAccountCallback() { // from class: com.nuance.swype.input.settings.AccountPrefs.1
        @Override // com.nuance.swypeconnect.ac.ACAccountService.ACAccountCallback
        public void linked() {
        }

        @Override // com.nuance.swypeconnect.ac.ACAccountService.ACAccountCallback
        public void devicesUpdated(ACDevice[] devices) {
            AccountPrefs.log.d("devices updated: ", Arrays.asList(devices));
            AccountPrefs.this.rebuildSettings();
        }

        @Override // com.nuance.swypeconnect.ac.ACAccountService.ACAccountCallback
        public void created() {
            AccountPrefs.this.rebuildSettings();
        }

        @Override // com.nuance.swypeconnect.ac.ACAccountService.ACAccountCallback
        public void onError(int type, String message) {
            AccountPrefs.log.d("ACAccountCallback.onError(" + type + ", " + message + ")");
            if (type == 512 || type == 256 || type == 1024) {
                IMEApplication.from(AccountPrefs.this.activity).showMyWordsPrefs();
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACAccountService.ACAccountCallback
        public void status(int type, String message) {
            AccountPrefs.log.d("ACAccountCallback.status(" + type + ", " + message + ")");
        }

        @Override // com.nuance.swypeconnect.ac.ACAccountService.ACAccountCallback
        public void verifyFailed() {
        }
    };
    private final ACDLMSyncService.ACDLMSyncCallback syncCallback = new ACDLMSyncService.ACDLMSyncCallback() { // from class: com.nuance.swype.input.settings.AccountPrefs.2
        @Override // com.nuance.swypeconnect.ac.ACDLMSyncService.ACDLMSyncCallback
        public void backupOccurred(int core, int sentEvents) {
            AccountPrefs.log.d("backupOccurred # events: ", Integer.valueOf(sentEvents));
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMSyncService.ACDLMSyncCallback
        public void restoreOccurred(int core, int receivedEvents) {
            AccountPrefs.log.d("restoreOccurred # events: ", Integer.valueOf(receivedEvents));
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMSyncService.ACDLMSyncCallback
        public void sentEvents(int core, int sentEvents) {
            AccountPrefs.log.d("sent events: ", Integer.valueOf(sentEvents));
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMSyncService.ACDLMSyncCallback
        public void receivedEvents(int core, int receivedEvents) {
            AccountPrefs.log.d("received events: ", Integer.valueOf(receivedEvents));
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMSyncService.ACDLMSyncCallback
        public void onError(int type, String message) {
            AccountPrefs.log.d("onError() type: ", Integer.valueOf(type), " message: ", message);
        }
    };
    private Preference.OnPreferenceChangeListener syncEnabledChangeListener = new Preference.OnPreferenceChangeListener() { // from class: com.nuance.swype.input.settings.AccountPrefs.3
        @Override // android.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference.getKey().equals("pref_backup_enabled")) {
                AccountPrefs.this.toggleSyncEnabled(((Boolean) newValue).booleanValue());
                StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(AccountPrefs.this.activity);
                if (sessionScribe != null) {
                    boolean value = ((Boolean) newValue).booleanValue();
                    sessionScribe.recordSettingsChange("pref_backup_enabled", Boolean.valueOf(value), Boolean.valueOf(!value));
                }
            }
            return true;
        }
    };
    private Connect.LegalDocumentPresenter backupAndSyncLegalPresenter = new Connect.LegalDocumentPresenter() { // from class: com.nuance.swype.input.settings.AccountPrefs.4
        Runnable accepted = null;

        @Override // com.nuance.swype.connect.Connect.LegalDocumentPresenter
        public boolean presentLegalRequirements(boolean tosRequired, boolean optInRequired, Runnable accepted) {
            this.accepted = accepted;
            AccountPrefs.this.showLegalRequirements(1, tosRequired, optInRequired, null);
            return false;
        }

        @Override // com.nuance.swype.connect.Connect.LegalDocumentPresenter
        public void accepted() {
            if (this.accepted != null) {
                this.accepted.run();
            }
        }
    };

    protected abstract PreferenceScreen addPreferences();

    protected abstract void doStartActivityForResult(Intent intent, int i);

    protected abstract void showConnectDialog();

    protected abstract void showDeleteAccountDialog();

    protected abstract void showUnlinkDialog(String str);

    /* JADX INFO: Access modifiers changed from: private */
    public boolean showLegalRequirements(int requestCode, boolean tosRequired, boolean optInRequired, Bundle resultData) {
        Intent intent = ConnectLegal.getLegalActivitiesStartIntent(this.activity, tosRequired, optInRequired, resultData);
        if (intent == null) {
            return false;
        }
        doStartActivityForResult(intent, requestCode);
        return true;
    }

    public AccountPrefs(Activity activity) {
        this.activity = activity;
        this.connect = Connect.from(activity);
        this.connection = new ConnectionAwarePreferences(activity) { // from class: com.nuance.swype.input.settings.AccountPrefs.5
            @Override // com.nuance.swype.preference.ConnectionAwarePreferences
            public void showConnectDialog() {
                AccountPrefs.this.showConnectDialog();
            }
        };
        this.connectedStatus = new ConnectedStatus(activity) { // from class: com.nuance.swype.input.settings.AccountPrefs.6
            @Override // com.nuance.swype.connect.ConnectedStatus
            public void onConnectionChanged(boolean isConnected) {
                AccountPrefs.this.rebuildSettings();
            }
        };
    }

    public void onStart() {
        this.connectedStatus.register();
    }

    public void onResume() {
        rebuildSettings();
        this.connect.getAccounts().registerCallback(this.callback);
        this.connect.getSync().registerCallback(this.syncCallback);
    }

    public void onPause() {
        this.connect.getAccounts().unregisterCallback(this.callback);
        this.connect.getSync().unregisterCallback(this.syncCallback);
    }

    public void onStop() {
        this.connectedStatus.unregister();
        if (this.settingsChanged) {
            recordMyWordsSettings();
        }
    }

    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {
        if (SystemClock.elapsedRealtime() - this.mLastClickTime < 700) {
            return false;
        }
        this.mLastClickTime = SystemClock.elapsedRealtime();
        String key = pref.getKey();
        if (!this.connectedStatus.isConnected()) {
            showConnectDialog();
            return true;
        }
        if ("pref_backup_enabled".equals(key)) {
            log.d("has been processed by syncEnabledChangeListener... key:", key);
            return true;
        }
        if (KEY_DELETE_ACCOUNT.equals(key)) {
            showDeleteAccountDialog();
            return true;
        }
        if (KEY_SYNC_ACCOUNT.equals(key)) {
            this.connect.getSync().doSync();
            this.connect.getAccounts().refreshDevicesList();
            return false;
        }
        if (KEY_ACCOUNT.equals(key)) {
            return true;
        }
        showUnlinkDialog(key);
        return false;
    }

    @Override // android.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getThisDeviceId() {
        String thisDeviceId = this.connect.getDeviceId();
        return thisDeviceId.length() > 0 ? thisDeviceId : "_UNKNOWN_";
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class BuildSettingHolder {
        ACAccount activeAccount;
        boolean backupEnabled;
        ACDevice[] devices;
        boolean isAvailable;
        String thisDeviceId;

        private BuildSettingHolder() {
        }
    }

    protected final void rebuildSettings() {
        if (this.currentBuildingSettingAsync != null) {
            this.currentBuildingSettingAsync.cancel(true);
            this.currentBuildingSettingAsync = null;
        }
        AsyncTask<Void, Void, BuildSettingHolder> task = new AsyncTask<Void, Void, BuildSettingHolder>() { // from class: com.nuance.swype.input.settings.AccountPrefs.7
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public BuildSettingHolder doInBackground(Void... params) {
                BuildSettingHolder holder = new BuildSettingHolder();
                holder.backupEnabled = AccountPrefs.this.connect.getSync().isEnabled();
                holder.activeAccount = AccountPrefs.this.connect.getAccounts().getActiveAccount();
                holder.thisDeviceId = AccountPrefs.this.getThisDeviceId();
                holder.devices = AccountPrefs.this.connect.getAccounts().getDevices();
                holder.isAvailable = AccountPrefs.this.connect.getSync().isAvailable();
                return holder;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(BuildSettingHolder result) {
                AccountPrefs.this.rebuildSettings(result.backupEnabled, result.activeAccount, result.isAvailable, result.thisDeviceId, result.devices);
                AccountPrefs.this.currentBuildingSettingAsync = null;
            }
        };
        this.currentBuildingSettingAsync = task;
        task.execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0227  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0265  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x02fc  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0303 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:47:0x03ab  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x0330  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void rebuildSettings(boolean r32, com.nuance.swypeconnect.ac.ACAccount r33, boolean r34, java.lang.String r35, com.nuance.swypeconnect.ac.ACDevice[] r36) {
        /*
            Method dump skipped, instructions count: 960
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.settings.AccountPrefs.rebuildSettings(boolean, com.nuance.swypeconnect.ac.ACAccount, boolean, java.lang.String, com.nuance.swypeconnect.ac.ACDevice[]):void");
    }

    protected final Preference findPreference(String key) {
        if (this.screen != null) {
            return this.screen.findPreference(key);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Dialog createDeleteAccountDialog() {
        return new AlertDialog.Builder(this.activity).setTitle(R.string.pref_my_devices).setMessage(R.string.connect_delete_account_desc).setNegativeButton(R.string.no_button, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.AccountPrefs.8
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                AccountPrefs.this.connect.getAccounts().deleteActiveAccount(true);
                IMEApplication.from(AccountPrefs.this.activity).showMyWordsPrefs();
                StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(AccountPrefs.this.activity);
                if (sessionScribe != null) {
                    sessionScribe.recordSettingsChange(AccountPrefs.KEY_DELETE_ACCOUNT, "account deleted", "account existing");
                }
            }
        }).setIcon(R.drawable.icon_settings_error).create();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Dialog createUnlinkDialog(final String deviceId) {
        String deviceName = getDeviceName(deviceId);
        return new AlertDialog.Builder(this.activity).setTitle(R.string.pref_my_devices).setMessage(this.activity.getResources().getString(R.string.remove_device_desc, deviceName)).setNegativeButton(R.string.no_button, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.AccountPrefs.9
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                ACDevice[] devices = AccountPrefs.this.connect.getAccounts().getDevices();
                if (devices != null && deviceId != null) {
                    for (ACDevice device : devices) {
                        if (deviceId.equals(device.getIdentifier())) {
                            AccountPrefs.this.connect.getAccounts().unlinkDevice(device);
                            AccountPrefs.log.d("UnlinkDialog...unlink device...", deviceId);
                            if (deviceId.equals(AccountPrefs.this.getThisDeviceId())) {
                                IMEApplication.from(AccountPrefs.this.activity).showMyWordsPrefs();
                                return;
                            } else {
                                AccountPrefs.this.rebuildSettings();
                                return;
                            }
                        }
                    }
                }
            }
        }).setIcon(R.drawable.icon_settings_error).create();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Dialog createConnectDialog() {
        return this.connection.getConnectDialog().create();
    }

    private String getDeviceName(String deviceId) {
        ACDevice[] devices = this.connect.getAccounts().getDevices();
        if (devices != null && deviceId != null) {
            for (ACDevice device : devices) {
                if (deviceId.equals(device.getIdentifier())) {
                    return device.getName();
                }
            }
        }
        return "";
    }

    public void refreshDevicesRequest() {
        this.connect.getAccounts().refreshDevicesList();
    }

    protected void toggleSyncEnabled(boolean enabled) {
        Connect.Sync sync = this.connect.getSync(this.backupAndSyncLegalPresenter);
        if (enabled != sync.isEnabled()) {
            if (enabled) {
                sync.enable();
            } else {
                sync.disable();
            }
            rebuildSettings();
            this.settingsChanged = true;
        }
    }

    public long getLastCheckinDiff(Date lastCheckin) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        c.setTime(lastCheckin);
        Calendar cNow = Calendar.getInstance();
        cNow.setTimeZone(TimeZone.getTimeZone("UTC"));
        cNow.setTime(new Date());
        return cNow.getTimeInMillis() - c.getTimeInMillis();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == -1) {
                    this.backupAndSyncLegalPresenter.accepted();
                    return;
                }
                return;
            default:
                log.d("No response to request code: ", Integer.valueOf(requestCode));
                return;
        }
    }
}
