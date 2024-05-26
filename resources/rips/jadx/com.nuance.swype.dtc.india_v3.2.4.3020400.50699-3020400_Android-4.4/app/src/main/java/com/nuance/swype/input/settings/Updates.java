package com.nuance.swype.input.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.View;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.connect.ConnectedStatus;
import com.nuance.swype.connect.SDKUpdateManager;
import com.nuance.swype.input.R;
import com.nuance.swype.preference.ConnectionAwarePreferences;
import com.nuance.swype.preference.FillPreferenceScreen;
import com.nuance.swype.preference.IconPreferenceScreen;
import com.nuance.swype.preference.ProgressBarPreference;
import com.nuance.swypeconnect.ac.ACException;
import com.nuance.swypeconnect.ac.ACPlatformUpdateService;
import java.io.File;

/* loaded from: classes.dex */
public abstract class Updates {
    private static final int REQUEST_CODE_UPGRADE_INSTALL = 1;
    private static final String SOFTWARE_UPDATES_CAT = "software_updates_cat";
    public static final int UPDATES_XML = R.xml.updates;
    private static final String UPDATE_KEY = "OTA_Key";
    private final Activity activity;
    private final Connect connect;
    private final ConnectedStatus connectedStatus;
    private final ConnectionAwarePreferences connection;
    private boolean isCanceling;
    private boolean isPaused;
    private boolean isUpgrading;
    private int progress;
    private PreferenceScreen screen;
    private final SDKUpdateManager updateManager;
    private final ACPlatformUpdateService.ACPlatformUpdateDownloadCallback downloadCallback = new ACPlatformUpdateService.ACPlatformUpdateDownloadCallback() { // from class: com.nuance.swype.input.settings.Updates.1
        @Override // com.nuance.swypeconnect.ac.ACPlatformUpdateService.ACPlatformUpdateDownloadCallback
        public void downloadStopped(int reasonCode) {
            if (reasonCode == 3) {
                Updates.this.isCanceling = false;
            }
            Updates.this.rebuildSettings();
        }

        @Override // com.nuance.swypeconnect.ac.ACPlatformUpdateService.ACPlatformUpdateDownloadCallback
        public void downloadStarted() {
        }

        @Override // com.nuance.swypeconnect.ac.ACPlatformUpdateService.ACPlatformUpdateDownloadCallback
        public void downloadPercentage(int percent) {
            Updates.this.progress = percent;
            Updates.this.rebuildSettings();
        }

        @Override // com.nuance.swypeconnect.ac.ACPlatformUpdateService.ACPlatformUpdateDownloadCallback
        public void downloadFailed(int reasonCode) {
            if (reasonCode == -999) {
                Updates.this.cancelUpgradeDownload();
            } else {
                Updates.this.isCanceling = false;
                Updates.this.rebuildSettings();
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACPlatformUpdateService.ACPlatformUpdateDownloadCallback
        public void downloadComplete(File file) {
            Updates.this.rebuildSettings();
        }
    };
    private final ProgressBarPreference.OnCancelListener upgradeCancelListener = new ProgressBarPreference.OnCancelListener() { // from class: com.nuance.swype.input.settings.Updates.2
        @Override // com.nuance.swype.preference.ProgressBarPreference.OnCancelListener
        public void onCancel(ProgressBarPreference pref) {
            Updates.this.cancelUpgradeDownload();
        }
    };
    private final Preference.OnPreferenceClickListener upgrader = new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.Updates.3
        @Override // android.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference pref) {
            if (Updates.this.connectedStatus.isConnected()) {
                if (!Updates.this.isUpgrading) {
                    Updates.this.installUpgrade();
                }
            } else {
                Updates.this.showConnectDialog();
            }
            return true;
        }
    };

    protected abstract PreferenceScreen addPreferences();

    protected abstract void doStartActivityForResult(Intent intent, int i);

    protected abstract void showConnectDialog();

    protected abstract void showUnknownSourcesDialog();

    public Updates(Activity activity) {
        this.activity = activity;
        this.connect = Connect.from(activity);
        this.updateManager = SDKUpdateManager.from(activity);
        this.connection = new ConnectionAwarePreferences(activity) { // from class: com.nuance.swype.input.settings.Updates.4
            @Override // com.nuance.swype.preference.ConnectionAwarePreferences
            public void showConnectDialog() {
                Updates.this.showConnectDialog();
            }
        };
        this.connectedStatus = new ConnectedStatus(activity) { // from class: com.nuance.swype.input.settings.Updates.5
            @Override // com.nuance.swype.connect.ConnectedStatus
            public void onInitialized() {
            }

            @Override // com.nuance.swype.connect.ConnectedStatus
            public void onConnectionChanged(boolean isConnected) {
                super.onConnectionChanged(isConnected);
                if (!isConnected) {
                    Updates.this.rebuildSettings();
                    Updates.this.showConnectDialog();
                } else {
                    Updates.this.rebuildSettings();
                }
            }
        };
        if (this.updateManager.isDownloading()) {
            this.updateManager.updateDownloadCallback(this.downloadCallback);
            this.progress = this.updateManager.getProgress();
        }
    }

    public void onStart() {
        this.connectedStatus.register();
    }

    public void onResume() {
        this.isUpgrading = false;
        this.isPaused = false;
        rebuildSettings();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPause() {
        this.isPaused = true;
    }

    public void onStop() {
        this.connectedStatus.unregister();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == -1) {
                    installUpgrade();
                    return;
                }
                return;
            default:
                return;
        }
    }

    public Dialog createConnectDialog() {
        return this.connection.getConnectDialog().create();
    }

    public Dialog createUnknownSourcesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setTitle(R.string.third_party_apps_title);
        builder.setMessage(R.string.third_party_apps_message);
        builder.setIcon(R.drawable.icon_settings_error);
        builder.setPositiveButton(this.activity.getResources().getString(R.string.ok_button), new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.Updates.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                Updates.this.activity.startActivity(new Intent("android.settings.SECURITY_SETTINGS"));
            }
        });
        return builder.create();
    }

    private Preference findPreference(String key) {
        if (this.screen != null) {
            return this.screen.findPreference(key);
        }
        return null;
    }

    private boolean removePreference(String key) {
        Preference pref = findPreference(key);
        return pref != null && removePreference(pref);
    }

    private boolean removePreference(Preference pref) {
        return this.screen != null && this.screen.removePreference(pref);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void rebuildSettings() {
        Preference pref;
        if (!this.isPaused) {
            if (this.screen != null) {
                this.screen.removeAll();
            }
            this.screen = addPreferences();
            if (!this.connect.isLicensed()) {
                Preference p = new FillPreferenceScreen(this.activity);
                p.setSummary(R.string.unlicensed_dialog_body);
                this.screen.removeAll();
                this.screen.addPreference(p);
            } else {
                if (this.updateManager == null || !this.updateManager.isAvailable()) {
                    removePreference(SOFTWARE_UPDATES_CAT);
                } else {
                    PreferenceCategory cat = (PreferenceCategory) findPreference(SOFTWARE_UPDATES_CAT);
                    if (!this.connectedStatus.isConnected() && this.updateManager.isDownloading()) {
                        Preference pref2 = this.connection.getConnectPreference();
                        pref2.setTitle(R.string.update_downloading_title);
                        pref2.setSummary(R.string.connect_enable_wifi);
                        cat.addPreference(pref2);
                    } else if (this.updateManager.isDownloading()) {
                        if (this.connectedStatus.isConnectedWifi()) {
                            if (this.isCanceling) {
                                pref = new Preference(this.activity);
                                pref.setSummary(R.string.canceling);
                            } else {
                                ProgressBarPreference p2 = new ProgressBarPreference(this.activity, null);
                                p2.setMax(100);
                                p2.setProgress(this.progress);
                                p2.cancelListener = this.upgradeCancelListener;
                                if (p2.cancel != null) {
                                    p2.cancel.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.preference.ProgressBarPreference.2
                                        public AnonymousClass2() {
                                        }

                                        @Override // android.view.View.OnClickListener
                                        public final void onClick(View v) {
                                            ProgressBarPreference.this.cancelListener.onCancel(ProgressBarPreference.this);
                                        }
                                    });
                                }
                                p2.setKey(UPDATE_KEY);
                                pref = p2;
                            }
                        } else {
                            pref = this.connection.getConnectPreference();
                        }
                        pref.setTitle(R.string.update_downloading_title);
                        cat.addPreference(pref);
                    } else {
                        Drawable d = this.activity.getResources().getDrawable(R.drawable.swype_logo);
                        Preference pref3 = IconPreferenceScreen.createPreferenceWithIcon(this.activity, d);
                        pref3.setKey(UPDATE_KEY);
                        if (this.updateManager.isUpdateDownloaded()) {
                            pref3.setTitle(R.string.update_install_title);
                            pref3.setSummary(R.string.update_install_desc);
                        } else {
                            pref3.setTitle(R.string.update_download_title);
                            if (this.updateManager.isOutOfSpace()) {
                                pref3.setSummary(R.string.error_out_of_disc_space);
                                this.updateManager.resetOutOfSpace();
                            } else {
                                pref3.setSummary(R.string.notification_language_update);
                            }
                        }
                        pref3.setOnPreferenceClickListener(this.upgrader);
                        cat.addPreference(pref3);
                    }
                }
                if (this.screen.getPreferenceCount() == 0) {
                    Preference pref4 = new FillPreferenceScreen(this.activity);
                    pref4.setSummary(R.string.no_available_updates);
                    this.screen.addPreference(pref4);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r1v6, types: [com.nuance.swype.input.settings.Updates$7] */
    public void installUpgrade() {
        if (!showLegalRequirements(1, true, false, null)) {
            if (this.updateManager != null) {
                if (this.updateManager.isUpdateDownloaded()) {
                    boolean unknownSourcesAllowed = false;
                    try {
                        unknownSourcesAllowed = Settings.Secure.getInt(this.activity.getContentResolver(), "install_non_market_apps") == 1;
                    } catch (Settings.SettingNotFoundException e) {
                    }
                    if (!unknownSourcesAllowed) {
                        showUnknownSourcesDialog();
                        return;
                    }
                    new AsyncTask<Void, Void, Void>() { // from class: com.nuance.swype.input.settings.Updates.7
                        /* JADX INFO: Access modifiers changed from: protected */
                        @Override // android.os.AsyncTask
                        public Void doInBackground(Void... params) {
                            Updates.this.isUpgrading = true;
                            Updates.this.updateManager.installUpdate();
                            return null;
                        }
                    }.execute(new Void[0]);
                } else if (this.updateManager.isAvailable()) {
                    try {
                        this.updateManager.downloadUpdate(this.downloadCallback);
                    } catch (ACException e2) {
                    }
                }
            }
            rebuildSettings();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelUpgradeDownload() {
        if (this.updateManager != null) {
            synchronized (this) {
                this.isCanceling = true;
                this.progress = 0;
            }
            rebuildSettings();
            this.updateManager.cancelDownload();
        }
    }

    private boolean showLegalRequirements(int requestCode, boolean tosRequired, boolean optInRequired, Bundle resultData) {
        Intent intent = ConnectLegal.getLegalActivitiesStartIntent(this.activity, tosRequired, optInRequired, resultData);
        if (intent == null) {
            return false;
        }
        doStartActivityForResult(intent, requestCode);
        return true;
    }
}
