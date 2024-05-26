package com.nuance.swype.input.settings;

import android.R;
import android.os.Bundle;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputMethods;

/* loaded from: classes.dex */
public class LanguagePrefsFragmentActivity extends SwypeAbstractFragmentActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null) {
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new LanguagePrefsFragment()).commit();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        InputMethods.Language currentLanguage = IMEApplication.from(getApplicationContext()).getInputMethods().getCurrentInputLanguage();
        if (!currentLanguage.isKoreanLanguage() && !currentLanguage.isJapaneseLanguage()) {
            IMEApplication.from(getApplicationContext()).showMainSettings();
            finish();
        }
    }
}
