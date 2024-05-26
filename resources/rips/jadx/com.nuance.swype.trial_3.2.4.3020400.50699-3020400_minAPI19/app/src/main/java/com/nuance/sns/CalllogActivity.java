package com.nuance.sns;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import com.nuance.connect.util.PermissionUtils;
import com.nuance.sns.SocialNetworkActivity;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACScannerException;
import com.nuance.swypeconnect.ac.ACScannerService;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class CalllogActivity extends SocialNetworkActivity {
    private static final LogManager.Log log = LogManager.getLog("CalllogActivity");

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.sns.SocialNetworkActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getApplicationContext(), PermissionUtils.READ_CALL_LOG) != 0) {
            ActivityCompat.requestPermissions(this, new String[]{PermissionUtils.READ_CALL_LOG}, 2);
            return;
        }
        init();
        closing();
        startScanningService();
    }

    private void init() {
        this.scraperStatus = IMEApplication.from(getApplicationContext()).getScraperStatusFactory().getCalllogScraperStatus();
        if (this.mScannerService != null) {
            try {
                this.mScanner = new WeakReference<>(this.mScannerService.getScanner(ACScannerService.ScannerType.CALL_LOG));
                this.mScanner.get().setMaxToProcess(60);
            } catch (ACScannerException e) {
                log.e(e.getMessage());
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
                UsageData.recordUsingCallLogScanner();
                updateWorkingStatus(this.scraperStatus, getStringDateNow());
                return true;
            } catch (ACScannerException e) {
                log.e(e.getMessage());
            }
        }
        updateErrorStatus(this.scraperStatus, getStringLastRun());
        closing();
        return false;
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 2) {
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
