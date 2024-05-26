package com.nuance.swype.startup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.connect.ConnectedStatus;
import com.nuance.swype.connect.SDKDownloadManager;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.R;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class DownloadLanguageDelegate extends StartupDelegate {
    private static final LogManager.Log log = LogManager.getLog("DownloadLanguageDelegate");
    private ConnectedStatus connectedStatus;
    private int downloadLanguageId;
    private SDKDownloadManager downloadManager;
    private SDKDownloadManager.DownloadableLanguage downloadingLanguage;
    private ProgressBar mProgressBar;
    private final View.OnClickListener cancelListener = new View.OnClickListener() { // from class: com.nuance.swype.startup.DownloadLanguageDelegate.2
        @Override // android.view.View.OnClickListener
        public final void onClick(View v) {
            AppPreferences.from(DownloadLanguageDelegate.this.getActivity()).setStartupSequenceDownloadLanguageID("");
            DownloadLanguageDelegate.this.downloadManager.languageCancel(DownloadLanguageDelegate.this.downloadLanguageId, true);
            DownloadLanguageDelegate.this.mActivityCallbacks.restartSequence(DownloadLanguageDelegate.this.mFlags, DownloadLanguageDelegate.this.mResultData);
        }
    };
    private final SDKDownloadManager.DataCallback dataCallback = new SDKDownloadManager.DataCallback() { // from class: com.nuance.swype.startup.DownloadLanguageDelegate.3
        @Override // com.nuance.swype.connect.SDKDownloadManager.DataCallback
        public final void listUpdated() {
        }

        @Override // com.nuance.swype.connect.SDKDownloadManager.DataCallback
        public final void languageUpdated(int languageId) {
            LogManager.Log log2 = DownloadLanguageDelegate.log;
            Object[] objArr = new Object[4];
            objArr[0] = "languageUpdated...languageId: ";
            objArr[1] = Integer.valueOf(languageId);
            objArr[2] = "...downloadingLanguage status: ";
            objArr[3] = DownloadLanguageDelegate.this.downloadingLanguage == null ? "null" : DownloadLanguageDelegate.this.downloadingLanguage.getStatus();
            log2.d(objArr);
            if (languageId == DownloadLanguageDelegate.this.downloadLanguageId && DownloadLanguageDelegate.this.downloadingLanguage != null) {
                switch (AnonymousClass5.$SwitchMap$com$nuance$swype$connect$SDKDownloadManager$DownloadableLanguage$Status[DownloadLanguageDelegate.this.downloadingLanguage.getStatus().ordinal()]) {
                    case 5:
                    case 8:
                        InputMethods.Language lang = DownloadLanguageDelegate.this.downloadingLanguage.getLanguage();
                        if (lang != null) {
                            DownloadLanguageDelegate.log.d("languageUpdated...lang: ", lang.toString());
                            InputMethods.from(DownloadLanguageDelegate.this.getActivity()).setCurrentLanguage(lang.getLanguageId());
                            DownloadLanguageDelegate.this.mStartupSequenceInfo.setLanguageSelectedAndInstalled$1385ff();
                        }
                        if (DownloadLanguageDelegate.this.downloadLanguageId != 0 && DownloadLanguageDelegate.this.dataCallback != null) {
                            DownloadLanguageDelegate.this.downloadManager.unregisterDataCallback(DownloadLanguageDelegate.this.dataCallback);
                        }
                        DownloadLanguageDelegate.this.mActivityCallbacks.startNextScreen(DownloadLanguageDelegate.this.mFlags, DownloadLanguageDelegate.this.mResultData);
                        return;
                    case 6:
                    default:
                        DownloadLanguageDelegate.log.d("Language download case not covered: ", DownloadLanguageDelegate.this.downloadingLanguage.getStatus());
                        return;
                    case 7:
                        DownloadLanguageDelegate.this.mProgressBar.setProgress(DownloadLanguageDelegate.this.downloadingLanguage.getProgress());
                        return;
                }
            }
        }

        @Override // com.nuance.swype.connect.SDKDownloadManager.DataCallback
        public final void onError(int reason) {
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DownloadLanguageDelegate newInstance(Bundle savedInstanceState) {
        DownloadLanguageDelegate f = new DownloadLanguageDelegate();
        f.setArguments(savedInstanceState);
        return f;
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.connectedStatus = new ConnectedStatus(getActivity()) { // from class: com.nuance.swype.startup.DownloadLanguageDelegate.1
            @Override // com.nuance.swype.connect.ConnectedStatus
            public final void onConnectionChanged(boolean isConnected) {
                DownloadLanguageDelegate.log.d("onConnectionChanged(", Boolean.valueOf(isConnected), ")");
                DownloadLanguageDelegate.this.connectionLost();
            }
        };
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loadTemplateToContentView(inflater, R.layout.startup_template_one_button, R.layout.startup_download_language, "");
        this.mProgressBar = (ProgressBar) this.view.findViewById(R.id.progressBar1);
        this.downloadManager = Connect.from(getActivity()).getDownloadManager();
        setupPositiveButton$411327c6(getActivity().getResources().getString(R.string.cancel_button), this.cancelListener);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public void onResume() {
        super.onResume();
        this.downloadLanguageId = Integer.parseInt(AppPreferences.from(getActivity()).getStartupSequenceDownloadLanguageID());
        this.downloadingLanguage = this.downloadManager.getSettingDownloadLanguageList(false).get(Integer.valueOf(this.downloadLanguageId));
        String downloadLanguageDisplayName = AppPreferences.from(getActivity()).getStartupSequenceDownloadLanguageDisplayName();
        ((TextView) this.view.findViewById(R.id.languageText)).setText(getActivity().getString(R.string.startup_lang_download_status) + '\n' + downloadLanguageDisplayName);
        log.d("onResume: downloadLanguageId: " + this.downloadLanguageId);
        log.d("onResume: downloadLanguageStatus: " + this.downloadingLanguage.getStatus());
        switch (this.downloadingLanguage.getStatus()) {
            case CANCELED:
            case DOWNLOAD_AVAILABLE:
            case STOPPED:
            case UPDATE_AVAILABLE:
                this.downloadManager.languageDownload(this.downloadLanguageId);
                break;
            case INSTALLED:
            case COMPLETE:
                InputMethods.from(getActivity()).setCurrentLanguage(this.downloadingLanguage.getLanguage().getLanguageId());
                this.mStartupSequenceInfo.setLanguageSelectedAndInstalled$1385ff();
                this.mActivityCallbacks.startNextScreen(this.mFlags, this.mResultData);
                break;
        }
        this.connectedStatus.register();
        this.downloadManager.registerDataCallback(this.dataCallback);
        log.d("onResume: connected: " + this.connectedStatus.isConnected());
        if (!this.connectedStatus.isConnected()) {
            connectionLost();
        }
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public void onPause() {
        super.onPause();
        this.downloadManager.unregisterDataCallback(this.dataCallback);
        this.connectedStatus.unregister();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void connectionLost() {
        if (this.connectedStatus.isConnected()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.startup_internet_connection);
        builder.setIcon(R.drawable.icon_settings_error);
        builder.setMessage(R.string.startup_connection_error);
        builder.setPositiveButton(getActivity().getResources().getString(R.string.startup_close), new DialogInterface.OnClickListener() { // from class: com.nuance.swype.startup.DownloadLanguageDelegate.4
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface arg0, int arg1) {
                AppPreferences.from(DownloadLanguageDelegate.this.getActivity()).setStartupSequenceDownloadLanguageID("");
                DownloadLanguageDelegate.this.mActivityCallbacks.restartSequence(DownloadLanguageDelegate.this.mFlags, DownloadLanguageDelegate.this.mResultData);
            }
        });
        showDialog(builder.create());
    }
}
