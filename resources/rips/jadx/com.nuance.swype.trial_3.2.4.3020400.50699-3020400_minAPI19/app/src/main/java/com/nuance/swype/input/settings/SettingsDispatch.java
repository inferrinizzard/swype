package com.nuance.swype.input.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.android.compat.UserManagerCompat;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.ShowFirstTimeStartupMessages;
import com.nuance.swype.usagedata.UsageData;

/* loaded from: classes.dex */
public class SettingsDispatch extends Activity {
    private static final int REQUEST_CODE_TOS = 1;
    private ShowFirstTimeStartupMessages mShowFirstTimeStartupMessages;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!ActivityManagerCompat.isUserAMonkey()) {
            IMEApplication imeApp = IMEApplication.from(this);
            if (UserManagerCompat.isUserUnlocked(this) && !imeApp.isUserUnlockFinished()) {
                imeApp.postUserUnlock();
            }
            if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("fromNotification")) {
                UsageData.recordScreenVisited(UsageData.Screen.SYSTEM_NOTIFICATION);
            }
            if (this.mShowFirstTimeStartupMessages == null) {
                this.mShowFirstTimeStartupMessages = IMEApplication.from(this).createFirstTimeStartupMessages();
            }
            if (!this.mShowFirstTimeStartupMessages.showStartup(true, true, null, null)) {
                imeApp.showMainSettings();
            }
        }
        finish();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onAttachedToWindow() {
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                IMEApplication.from(this).showMainSettings();
                finish();
                return;
            default:
                return;
        }
    }
}
