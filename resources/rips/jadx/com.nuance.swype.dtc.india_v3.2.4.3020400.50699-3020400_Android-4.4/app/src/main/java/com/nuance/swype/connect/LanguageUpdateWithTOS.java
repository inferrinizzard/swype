package com.nuance.swype.connect;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.ChinaNetworkNotificationDialog;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.settings.LanguageUpdateDispatch;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class LanguageUpdateWithTOS extends Activity implements ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener {
    public static final String LANGUAGE_ID = "language.id";
    private static final int REQUEST_FOR_RESULT_OK = 1;
    private LogManager.Log log = LogManager.getLog("LanguageUpdateWithTOS");
    private Bundle mBundle;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mBundle = bundle;
        Connect connect = Connect.from(this);
        UserPreferences userPrefs = UserPreferences.from(this);
        if (getResources().getBoolean(R.bool.enable_china_connect_special) && userPrefs.shouldShowNetworkAgreementDialog()) {
            createNetworkNotificationDialg().show();
            return;
        }
        if (BuildInfo.from(this).isDTCbuild() && !connect.getLegal().isTosAccepted()) {
            Intent intent = ConnectLegal.getLegalActivitiesStartIntent(this, true, false, bundle);
            if (intent != null) {
                startActivityForResult(intent, 1);
                return;
            }
            return;
        }
        if (connect.getLegal().isTosAccepted() || connect.getLegal().isEulaAccepted()) {
            doDownloadLanguage(connect);
            finish();
        } else {
            Intent intent2 = ConnectLegal.getLegalActivitiesStartIntent(this, true, false, bundle);
            if (intent2 != null) {
                startActivityForResult(intent2, 1);
            }
        }
    }

    private void doDownloadLanguage(Connect connect) {
        int languageId = getIntent().getIntExtra(LANGUAGE_ID, 0);
        this.log.d("doDownloadLanguage: ", Integer.valueOf(languageId));
        if (languageId != 0) {
            connect.getDownloadManager().languageDownload(languageId);
            Intent languageUpdate = new Intent(this, (Class<?>) LanguageUpdateDispatch.class);
            startActivity(languageUpdate);
            return;
        }
        LogManager.getLog().e("NO LANGUAGE FOUND!");
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null) {
            Connect connect = Connect.from(this);
            UserPreferences userPrefs = UserPreferences.from(this);
            if (getResources().getBoolean(R.bool.enable_china_connect_special) && userPrefs.shouldShowNetworkAgreementDialog()) {
                createNetworkNotificationDialg().show();
                return;
            } else if (connect.getLegal().isTosAccepted()) {
                doDownloadLanguage(connect);
            }
        }
        finish();
    }

    public Dialog createNetworkNotificationDialg() {
        return ChinaNetworkNotificationDialog.create(this, this);
    }

    @Override // com.nuance.swype.input.ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener
    public boolean onNegativeButtonClick() {
        finish();
        return true;
    }

    @Override // com.nuance.swype.input.ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener
    public boolean onPositiveButtonClick() {
        Connect connect = Connect.from(this);
        if (connect.getLegal().isTosAccepted() || connect.getLegal().isEulaAccepted()) {
            doDownloadLanguage(connect);
            finish();
        } else {
            Intent intent = ConnectLegal.getLegalActivitiesStartIntent(this, true, false, this.mBundle);
            if (intent != null) {
                startActivityForResult(intent, 1);
            }
        }
        return true;
    }
}
