package com.facebook;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import com.facebook.internal.CustomTab;

/* loaded from: classes.dex */
public class CustomTabMainActivity extends Activity {
    private static final String OAUTH_DIALOG = "oauth";
    private BroadcastReceiver redirectReceiver;
    private boolean shouldCloseCustomTab = true;
    public static final String EXTRA_PARAMS = CustomTabMainActivity.class.getSimpleName() + ".extra_params";
    public static final String EXTRA_CHROME_PACKAGE = CustomTabMainActivity.class.getSimpleName() + ".extra_chromePackage";
    public static final String EXTRA_URL = CustomTabMainActivity.class.getSimpleName() + ".extra_url";
    public static final String REFRESH_ACTION = CustomTabMainActivity.class.getSimpleName() + ".action_refresh";

    public static final String getRedirectUrl() {
        return "fb" + FacebookSdk.getApplicationId() + "://authorize";
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (CustomTabActivity.CUSTOM_TAB_REDIRECT_ACTION.equals(getIntent().getAction())) {
            setResult(0);
            finish();
        } else if (savedInstanceState == null) {
            Bundle parameters = getIntent().getBundleExtra(EXTRA_PARAMS);
            String chromePackage = getIntent().getStringExtra(EXTRA_CHROME_PACKAGE);
            new CustomTab("oauth", parameters).openCustomTab(this, chromePackage);
            this.shouldCloseCustomTab = false;
            this.redirectReceiver = new BroadcastReceiver() { // from class: com.facebook.CustomTabMainActivity.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    Intent newIntent = new Intent(CustomTabMainActivity.this, (Class<?>) CustomTabMainActivity.class);
                    newIntent.setAction(CustomTabMainActivity.REFRESH_ACTION);
                    newIntent.putExtra(CustomTabMainActivity.EXTRA_URL, intent.getStringExtra(CustomTabMainActivity.EXTRA_URL));
                    newIntent.addFlags(603979776);
                    CustomTabMainActivity.this.startActivity(newIntent);
                }
            };
            LocalBroadcastManager.getInstance(this).registerReceiver(this.redirectReceiver, new IntentFilter(CustomTabActivity.CUSTOM_TAB_REDIRECT_ACTION));
        }
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (REFRESH_ACTION.equals(intent.getAction())) {
            Intent broadcast = new Intent(CustomTabActivity.DESTROY_ACTION);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
            sendResult(-1, intent);
        } else if (CustomTabActivity.CUSTOM_TAB_REDIRECT_ACTION.equals(intent.getAction())) {
            sendResult(-1, intent);
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.shouldCloseCustomTab) {
            sendResult(0, null);
        }
        this.shouldCloseCustomTab = true;
    }

    private void sendResult(int resultCode, Intent resultIntent) {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.redirectReceiver);
        if (resultIntent != null) {
            setResult(resultCode, resultIntent);
        } else {
            setResult(resultCode);
        }
        finish();
    }
}
