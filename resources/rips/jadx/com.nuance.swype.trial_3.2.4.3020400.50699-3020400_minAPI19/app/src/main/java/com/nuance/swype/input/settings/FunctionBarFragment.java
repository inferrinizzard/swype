package com.nuance.swype.input.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public class FunctionBarFragment extends ActionbarPreferenceFragment {
    private String MAX_TAG = "max_item";
    private FunctionBar delegate;

    @Override // com.nuance.swype.input.settings.ActionbarPreferenceFragment, com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.function_bar);
        this.delegate = new FunctionBar(getActivity(), getPreferenceScreen()) { // from class: com.nuance.swype.input.settings.FunctionBarFragment.1
            @Override // com.nuance.swype.input.settings.FunctionBar
            protected void showMaxCountdialog() {
                MaxItemDlg dialog = new MaxItemDlg();
                dialog.setTargetFragment(FunctionBarFragment.this, 0);
                dialog.show(FunctionBarFragment.this.getFragmentManager(), FunctionBarFragment.this.MAX_TAG);
            }
        };
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        this.delegate.onResume();
    }

    /* loaded from: classes.dex */
    public static class MaxItemDlg extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((FunctionBarFragment) getTargetFragment()).delegate.createMaxItemDlg();
        }
    }
}
