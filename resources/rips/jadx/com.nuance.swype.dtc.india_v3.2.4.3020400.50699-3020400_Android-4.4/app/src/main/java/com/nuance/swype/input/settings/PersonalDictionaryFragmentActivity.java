package com.nuance.swype.input.settings;

import android.R;
import android.annotation.TargetApi;
import android.os.Bundle;

@TargetApi(23)
/* loaded from: classes.dex */
public class PersonalDictionaryFragmentActivity extends SwypeAbstractFragmentActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null) {
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new PersonalDictionaryFragment()).commit();
        }
    }
}
