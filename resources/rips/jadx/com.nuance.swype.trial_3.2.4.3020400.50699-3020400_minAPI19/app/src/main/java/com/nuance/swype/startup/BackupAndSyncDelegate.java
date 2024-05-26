package com.nuance.swype.startup;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.zzab;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.connect.ConnectedStatus;
import com.nuance.swype.input.ChinaNetworkNotificationDialog;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.preference.ConnectionAwarePreferences;
import com.nuance.swype.service.impl.AccountUtil;
import com.nuance.swype.util.LogManager;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class BackupAndSyncDelegate extends StartupDelegate {
    private static final LogManager.Log log = LogManager.getLog("BackupAndSyncDelegate");
    private Dialog cnNwDialog;
    private ConnectedStatus connectedStatus;
    private Dialog connectionDialog;
    protected Dialog invalidEmailDialog;
    private WeakReference<Activity> mActivity;
    private GoogleApiClient mCredentialsApiClient;
    private boolean mAccountListShown = false;
    private final View.OnClickListener registerButtonListener = new View.OnClickListener() { // from class: com.nuance.swype.startup.BackupAndSyncDelegate.5
        @Override // android.view.View.OnClickListener
        public final void onClick(View arg0) {
            if (BackupAndSyncDelegate.this.connectedStatus != null && !BackupAndSyncDelegate.this.connectedStatus.isConnected()) {
                BackupAndSyncDelegate.this.showDialog(BackupAndSyncDelegate.this.connectionDialog);
            } else if (BackupAndSyncDelegate.this.mStartupSequenceInfo.shouldShowNetworkAgreementDialog() && BackupAndSyncDelegate.this.cnNwDialog != null) {
                BackupAndSyncDelegate.this.showDialog(BackupAndSyncDelegate.this.cnNwDialog);
            } else {
                BackupAndSyncDelegate.access$000(BackupAndSyncDelegate.this);
            }
        }
    };
    private final View.OnClickListener skipButtonListener = new View.OnClickListener() { // from class: com.nuance.swype.startup.BackupAndSyncDelegate.6
        @Override // android.view.View.OnClickListener
        public final void onClick(View arg0) {
            BackupAndSyncDelegate.this.mStartupSequenceInfo.setShowBackupSync(false);
            BackupAndSyncDelegate.this.mActivityCallbacks.startNextScreen(BackupAndSyncDelegate.this.mFlags, BackupAndSyncDelegate.this.mResultData);
        }
    };

    static /* synthetic */ boolean access$102$7e8f9767(BackupAndSyncDelegate x0) {
        x0.mAccountListShown = true;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BackupAndSyncDelegate newInstance(Bundle savedInstanceState) {
        BackupAndSyncDelegate f = new BackupAndSyncDelegate();
        f.setArguments(savedInstanceState);
        return f;
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.mAccountListShown = savedInstanceState.getBoolean("account_list_shown", false);
        }
        this.mActivity = new WeakReference<>(getActivity());
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ConnectionAwarePreferences connection = new ConnectionAwarePreferences(this.mActivity.get()) { // from class: com.nuance.swype.startup.BackupAndSyncDelegate.1
            @Override // com.nuance.swype.preference.ConnectionAwarePreferences
            public final void showConnectDialog() {
            }
        };
        this.mCredentialsApiClient = new GoogleApiClient.Builder(this.mActivity.get()).addApi(Auth.CREDENTIALS_API).build();
        this.invalidEmailDialog = new AlertDialog.Builder(new ContextThemeWrapper(this.mActivity.get(), R.style.Theme.DeviceDefault.Light.Dialog)).setTitle(com.nuance.swype.input.R.string.invalid_email_title).setMessage(com.nuance.swype.input.R.string.invalid_email_description).setPositiveButton(com.nuance.swype.input.R.string.ok_button, (DialogInterface.OnClickListener) null).create();
        this.connectionDialog = connection.getConnectDialog().create();
        if (this.mStartupSequenceInfo.shouldShowNetworkAgreementDialog() && this.mActivity.get() != null) {
            this.cnNwDialog = ChinaNetworkNotificationDialog.create(this.mActivity.get(), new ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener() { // from class: com.nuance.swype.startup.BackupAndSyncDelegate.2
                @Override // com.nuance.swype.input.ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener
                public final boolean onPositiveButtonClick() {
                    BackupAndSyncDelegate.access$000(BackupAndSyncDelegate.this);
                    return false;
                }

                @Override // com.nuance.swype.input.ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener
                public final boolean onNegativeButtonClick() {
                    return false;
                }
            });
        }
        loadTemplateToContentView(inflater, com.nuance.swype.input.R.layout.startup_template, com.nuance.swype.input.R.layout.startup_backup_sync, com.nuance.swype.input.R.string.back_and_sync_title);
        setUpNegativeButton();
        setupPositiveButton$411327c6(this.mActivity.get().getResources().getString(com.nuance.swype.input.R.string.register_button), this.registerButtonListener);
        EditText editText = (EditText) this.view.findViewById(com.nuance.swype.input.R.id.emailSelect);
        editText.setInputType(33);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.nuance.swype.startup.BackupAndSyncDelegate.3
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !BackupAndSyncDelegate.this.mAccountListShown) {
                    HintRequest.Builder builder = new HintRequest.Builder();
                    CredentialPickerConfig.Builder builder2 = new CredentialPickerConfig.Builder();
                    builder2.cQ = true;
                    builder2.cP = false;
                    builder2.mShowCancelButton = true;
                    builder.cV = (CredentialPickerConfig) zzab.zzy(builder2.build());
                    builder.cW = true;
                    builder.cX = false;
                    builder.cS = new String[]{"https://accounts.google.com"};
                    if (builder.cS == null) {
                        builder.cS = new String[0];
                    }
                    if (!builder.cW && !builder.cX && builder.cS.length == 0) {
                        throw new IllegalStateException("At least one authentication method must be specified");
                    }
                    HintRequest hintRequest = new HintRequest(builder, (byte) 0);
                    PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(BackupAndSyncDelegate.this.mCredentialsApiClient, hintRequest);
                    try {
                        if (BackupAndSyncDelegate.this.mActivity.get() != null) {
                            ((Activity) BackupAndSyncDelegate.this.mActivity.get()).startIntentSenderForResult(intent.getIntentSender(), 0, null, 0, 0, 0);
                        }
                    } catch (IntentSender.SendIntentException e) {
                        BackupAndSyncDelegate.log.e("Could not start hint picker Intent", e);
                    }
                    BackupAndSyncDelegate.access$102$7e8f9767(BackupAndSyncDelegate.this);
                }
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void setUpNegativeButton() {
        if (this.mActivity.get() != null) {
            setupNegativeButton$411327c6(this.mActivity.get().getResources().getString(com.nuance.swype.input.R.string.skip_button), this.skipButtonListener);
        }
    }

    @Override // android.app.Fragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == -1) {
                Credential credential = (Credential) data.getParcelableExtra("com.google.android.gms.credentials.Credential");
                log.d("onActivityResult: credential = " + credential.zzbgg);
                ((EditText) this.view.findViewById(com.nuance.swype.input.R.id.emailSelect)).setText(credential.zzbgg);
            } else if (resultCode == 0) {
                log.d("onActivityResult: user canceled");
            } else {
                log.e("onActivityResult: unknown result code: " + resultCode);
            }
        }
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mActivity.get() != null) {
            this.connectedStatus = new ConnectedStatus(this.mActivity.get()) { // from class: com.nuance.swype.startup.BackupAndSyncDelegate.4
            };
            this.connectedStatus.register();
        }
    }

    @Override // android.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("account_list_shown", this.mAccountListShown);
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public void onPause() {
        super.onPause();
        if (this.connectedStatus != null) {
            this.connectedStatus.unregister();
        }
    }

    protected boolean register(Context context) {
        String email = ((EditText) this.view.findViewById(com.nuance.swype.input.R.id.emailSelect)).getText().toString();
        if (this.mActivity.get() != null && !Connect.from(this.mActivity.get()).getAccounts().isValidEmail(email)) {
            showDialog(this.invalidEmailDialog);
            return false;
        }
        boolean tosAccepted = this.mStartupSequenceInfo.isTosAccepted();
        boolean optInAccepted = this.mStartupSequenceInfo.isOptInAccepted();
        if (!tosAccepted || !optInAccepted) {
            this.mStartupSequenceInfo.setShowBackupSync(false);
            this.mStartupSequenceInfo.setShowCud(true);
            this.mStartupSequenceInfo.mShowTos = true;
            this.mActivityCallbacks.restartSequence(this.mFlags, this.mResultData);
            IMEApplication.from(this.mActivity.get()).getAppPreferences().setStartupSequenceAccountEmail(email);
            return false;
        }
        this.mStartupSequenceInfo.setHotWordsStatus$1385ff();
        return Connect.from(this.mActivity.get()).getAccounts().createAccount(email, AccountUtil.isTablet(this.mActivity.get()), AccountUtil.buildDeviceName(this.mActivity.get()), true);
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mActivity.clear();
    }

    static /* synthetic */ void access$000(BackupAndSyncDelegate x0) {
        if (!x0.register(x0.mActivity.get())) {
            return;
        }
        x0.mStartupSequenceInfo.setShowBackupSync(false);
        x0.mActivityCallbacks.startNextScreen(x0.mFlags, x0.mResultData);
    }
}
