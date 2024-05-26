package com.nuance.swype.input.settings;

import android.R;
import android.os.Bundle;

/* loaded from: classes.dex */
public class LanguageUpdateFragmentActivity extends SwypeAbstractFragmentActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null) {
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new LanguageUpdateFragment()).commit();
        }
    }
}
