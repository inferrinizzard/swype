package com.nuance.swype.input.settings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public class LanguageUpdateFragment extends ActionbarListFragment {
    protected LanguageUpdate delegate;

    @Override // com.nuance.swype.input.settings.ActionbarListFragment, android.support.v4.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.delegate = new LanguageUpdate(getActivity()) { // from class: com.nuance.swype.input.settings.LanguageUpdateFragment.1
            @Override // com.nuance.swype.input.settings.LanguageUpdate
            protected void showConnectDialog() {
                DialogFragment dialog = new ConnectDialog();
                dialog.setTargetFragment(LanguageUpdateFragment.this, 0);
                dialog.show(LanguageUpdateFragment.this.getFragmentManager(), "connect");
            }

            @Override // com.nuance.swype.input.settings.LanguageUpdate
            protected void showRemoveLanguageDialog(Bundle args) {
                DialogFragment dialog = new RemoveLanguageDialog();
                dialog.setTargetFragment(LanguageUpdateFragment.this, 0);
                dialog.setArguments(args);
                dialog.show(LanguageUpdateFragment.this.getFragmentManager(), "remove");
            }

            @Override // com.nuance.swype.input.settings.LanguageUpdate
            protected void doStartActivityForResult(Intent intent, int requestCode) {
                LanguageUpdateFragment.this.startActivityForResult(intent, requestCode);
            }

            @Override // com.nuance.swype.input.settings.LanguageUpdate
            protected ListView doGetListView() {
                return LanguageUpdateFragment.this.getListView();
            }

            @Override // com.nuance.swype.input.settings.LanguageUpdate
            protected void showTimeoutDialog() {
                DialogFragment dialog = new TimeoutDialog();
                dialog.setTargetFragment(LanguageUpdateFragment.this, 0);
                dialog.show(LanguageUpdateFragment.this.getFragmentManager(), "connect");
            }
        };
        setListAdapter(this.delegate.getAdapter());
    }

    @Override // com.nuance.swype.input.settings.ActionbarListFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override // android.support.v4.app.ListFragment, android.support.v4.app.Fragment
    @SuppressLint({"InflateParams"})
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.language_download_list, (ViewGroup) null);
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
    public void onDestroy() {
        super.onDestroy();
        this.delegate.onDestroy();
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.delegate.onActivityResult(requestCode, resultCode, data);
    }

    @Override // android.support.v4.app.ListFragment
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        this.delegate.onListItemClick(l, v, position, id);
    }

    /* loaded from: classes.dex */
    public static class RemoveLanguageDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((LanguageUpdateFragment) getTargetFragment()).delegate.createRemoveLanguageDialog(getArguments());
        }
    }

    /* loaded from: classes.dex */
    public static class ConnectDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((LanguageUpdateFragment) getTargetFragment()).delegate.createConnectDialog();
        }
    }

    /* loaded from: classes.dex */
    public static class TimeoutDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((LanguageUpdateFragment) getTargetFragment()).delegate.timeoutDialog();
        }
    }
}
