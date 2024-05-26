package com.nuance.swype.input.settings;

import android.R;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

/* loaded from: classes.dex */
public abstract class SwypeAbstractFragmentActivity extends FragmentActivity {
    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
