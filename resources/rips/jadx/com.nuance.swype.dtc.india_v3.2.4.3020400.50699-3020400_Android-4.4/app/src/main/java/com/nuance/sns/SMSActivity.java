package com.nuance.sns;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import com.nuance.sns.SocialNetworkActivity;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACScannerException;
import com.nuance.swypeconnect.ac.ACScannerService;
import com.nuance.swypeconnect.ac.ACScannerSms;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class SMSActivity extends SocialNetworkActivity {
    private static final LogManager.Log log = LogManager.getLog("SMSActivity");

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.sns.SocialNetworkActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.READ_SMS") != 0 || ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.READ_CONTACTS") != 0)) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_SMS", "android.permission.READ_CONTACTS"}, 1);
            return;
        }
        init();
        closing();
        startScanningService();
    }

    private void init() {
        this.scraperStatus = IMEApplication.from(getApplicationContext()).getScraperStatusFactory().getSMSStatusPreference();
        if (this.mScannerService != null) {
            try {
                this.mScanner = new WeakReference<>(this.mScannerService.getScanner(ACScannerService.ScannerType.SMS));
                if (this.mScanner.get() != null) {
                    ((ACScannerSms) this.mScanner.get()).setScanType(new ACScannerSms.ACSmsScannerType[]{ACScannerSms.ACSmsScannerType.SENT});
                    ((ACScannerSms) this.mScanner.get()).setScanContacts(true);
                    this.mScanner.get().setMaxToProcess(R.styleable.ThemeTemplate_pressableBackgroundHighlight);
                }
            } catch (ACScannerException e) {
                log.e("init..." + e.getMessage());
                e.printStackTrace();
                updateErrorStatus(this.scraperStatus, getStringLastRun());
                closing();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.sns.SocialNetworkActivity
    public boolean startScanningService() {
        if (super.startScanningService() && this.mScanner.get() != null) {
            try {
                this.mScanner.get().start(new SocialNetworkActivity.ScannerCallBack());
                UsageData.recordUsingSmsScanner();
                updateWorkingStatus(this.scraperStatus, getStringDateNow());
                return true;
            } catch (ACScannerException e) {
                log.e("startScanningService" + e.getMessage());
            }
        }
        updateErrorStatus(this.scraperStatus, getStringLastRun());
        closing();
        return false;
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                UsageData.recordPermissionGrantResult(permissions[i], grantResults[i], this);
                if (grantResults[i] == -1) {
                    closing();
                    return;
                }
            }
            init();
            closing();
            startScanningService();
        }
    }
}
