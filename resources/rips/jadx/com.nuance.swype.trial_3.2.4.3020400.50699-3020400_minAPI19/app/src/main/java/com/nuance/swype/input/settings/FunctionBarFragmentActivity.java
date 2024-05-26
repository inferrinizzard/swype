package com.nuance.swype.input.settings;

import android.R;
import android.os.Bundle;
import com.nuance.swype.input.IMEApplication;

/* loaded from: classes.dex */
public class FunctionBarFragmentActivity extends SwypeAbstractFragmentActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null) {
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new FunctionBarFragment()).commit();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        if (!IMEApplication.from(getApplicationContext()).getInputMethods().getCurrentInputLanguage().isChineseLanguage()) {
            IMEApplication.from(getApplicationContext()).showMainSettings();
            finish();
        }
    }
}
