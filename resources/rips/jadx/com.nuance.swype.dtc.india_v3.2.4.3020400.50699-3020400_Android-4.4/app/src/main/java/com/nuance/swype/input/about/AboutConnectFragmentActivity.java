package com.nuance.swype.input.about;

import android.R;
import android.annotation.TargetApi;
import android.os.Bundle;
import com.nuance.swype.input.settings.SwypeAbstractFragmentActivity;

/* loaded from: classes.dex */
public class AboutConnectFragmentActivity extends SwypeAbstractFragmentActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    @TargetApi(14)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, new AboutConnectFragment()).commit();
    }
}
