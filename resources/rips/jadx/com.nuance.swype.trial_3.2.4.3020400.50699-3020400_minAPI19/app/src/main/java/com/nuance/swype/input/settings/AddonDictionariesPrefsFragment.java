package com.nuance.swype.input.settings;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

/* loaded from: classes.dex */
public class AddonDictionariesPrefsFragment extends ActionbarListFragment {
    protected AddonDictionariesPrefs delegate;

    @Override // com.nuance.swype.input.settings.ActionbarListFragment, android.support.v4.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.delegate = new AddonDictionariesPrefs(getActivity()) { // from class: com.nuance.swype.input.settings.AddonDictionariesPrefsFragment.1
            @Override // com.nuance.swype.input.settings.AddonDictionariesPrefs
            protected void showConnectDialog() {
                DialogFragment dialog = new ConnectDialog();
                dialog.setTargetFragment(AddonDictionariesPrefsFragment.this, 0);
                dialog.show(AddonDictionariesPrefsFragment.this.getFragmentManager(), "connect");
            }

            @Override // com.nuance.swype.input.settings.AddonDictionariesPrefs
            protected void showRemoveDictionaryDialog(Bundle args) {
                DialogFragment dialog = new RemoveDictionaryDialog();
                dialog.setTargetFragment(AddonDictionariesPrefsFragment.this, 0);
                dialog.setArguments(args);
                dialog.show(AddonDictionariesPrefsFragment.this.getFragmentManager(), "remove");
            }

            @Override // com.nuance.swype.input.settings.AddonDictionariesPrefs
            protected void showTimeoutDialog() {
                DialogFragment dialog = new TimeoutDialog();
                dialog.setTargetFragment(AddonDictionariesPrefsFragment.this, 0);
                dialog.show(AddonDictionariesPrefsFragment.this.getFragmentManager(), "connect");
            }
        };
        BaseAdapter adapter = this.delegate.getAdapter();
        if (adapter != null) {
            setListAdapter(adapter);
        }
    }

    @Override // com.nuance.swype.input.settings.ActionbarListFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override // android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        this.delegate.onStart();
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        this.delegate.onResume();
    }

    @Override // android.support.v4.app.Fragment
    public void onPause() {
        super.onPause();
        this.delegate.onPause();
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        super.onStop();
        this.delegate.onStop();
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override // android.support.v4.app.ListFragment
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        this.delegate.onListItemClick(l, v, position, id);
    }

    /* loaded from: classes.dex */
    public static class RemoveDictionaryDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((AddonDictionariesPrefsFragment) getTargetFragment()).delegate.createRemoveDictionaryDialog(getArguments());
        }
    }

    /* loaded from: classes.dex */
    public static class ConnectDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((AddonDictionariesPrefsFragment) getTargetFragment()).delegate.createConnectDialog();
        }
    }

    /* loaded from: classes.dex */
    public static class TimeoutDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((AddonDictionariesPrefsFragment) getTargetFragment()).delegate.timeoutDialog();
        }
    }
}
