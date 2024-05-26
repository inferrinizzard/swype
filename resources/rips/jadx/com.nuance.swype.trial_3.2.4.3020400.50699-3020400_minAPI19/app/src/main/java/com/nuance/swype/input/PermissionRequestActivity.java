package com.nuance.swype.input;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.LogManager;

@SuppressLint({"Registered"})
/* loaded from: classes.dex */
public class PermissionRequestActivity extends Activity {
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 0;
    protected static final LogManager.Log log = LogManager.getLog("PermissionRequestActivity");
    private boolean isPermissionDialogRationale;
    private AlertDialog postDNSADlg;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
    }

    @Override // android.app.Activity
    @TargetApi(23)
    protected void onResume() {
        super.onResume();
        if (this.postDNSADlg == null) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.RECORD_AUDIO") != 0) {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.RECORD_AUDIO"}, 0);
            }
            if (Build.VERSION.SDK_INT >= 23) {
                this.isPermissionDialogRationale = shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO");
            }
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        if (this.postDNSADlg != null && this.postDNSADlg.isShowing()) {
            this.postDNSADlg.dismiss();
        }
    }

    @Override // android.app.Activity
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        recordUserActionForPermissionDialog(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            int len = permissions.length;
            for (int i = 0; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == -1) {
                    if (!shouldShowRequestPermissionRationale(permission)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        this.postDNSADlg = builder.setCancelable(false).setIcon(R.drawable.swype_logo).setTitle(R.string.perms_mic_title_2).setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.PermissionRequestActivity.1
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int which) {
                                PermissionRequestActivity.this.finish();
                            }
                        }).create();
                        this.postDNSADlg.setMessage(getResources().getString(R.string.perms_mic_desc) + "\n\n" + getResources().getString(R.string.perms_enable));
                        this.postDNSADlg.show();
                        return;
                    }
                    finish();
                } else {
                    finish();
                }
            }
            return;
        }
        finish();
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @TargetApi(23)
    private void recordUserActionForPermissionDialog(int requestCode, String[] permissions, int[] grantResults) {
        UsageData.PermissionUserAction userAction;
        if (Build.VERSION.SDK_INT >= 23) {
            boolean isPermissionDialogRationale = shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO");
            if (requestCode == 0 && grantResults.length != 0 && permissions[0].equals("android.permission.RECORD_AUDIO")) {
                if (grantResults[0] == 0) {
                    userAction = UsageData.PermissionUserAction.ALLOWED;
                } else {
                    if (isPermissionDialogRationale) {
                        userAction = UsageData.PermissionUserAction.DENIED;
                    } else if (this.isPermissionDialogRationale) {
                        userAction = UsageData.PermissionUserAction.NEVER_SHOW_AGAIN;
                    } else {
                        userAction = UsageData.PermissionUserAction.BLOCKED;
                    }
                    this.isPermissionDialogRationale = isPermissionDialogRationale;
                }
                UsageData.recordPermissionRequest(UsageData.Permission.RECORD_AUDIO_KB_VOICE_KEY, userAction);
            }
        }
    }
}
