package com.nuance.swype.input.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/* loaded from: classes.dex */
public class PersonalDictionaryFragment extends ActionbarListFragment {
    private final View.OnClickListener confirmDeletions = new View.OnClickListener() { // from class: com.nuance.swype.input.settings.PersonalDictionaryFragment.1
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            DialogFragment dialog = new ConfirmDeletions();
            dialog.setTargetFragment(PersonalDictionaryFragment.this, 0);
            dialog.show(PersonalDictionaryFragment.this.getFragmentManager(), "dialog");
        }
    };
    private PersonalDictionary pd;

    @Override // com.nuance.swype.input.settings.ActionbarListFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.pd = new PersonalDictionary(getActivity(), savedInstanceState);
        setListAdapter(this.pd.getListAdapter());
    }

    @Override // android.support.v4.app.ListFragment, android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return this.pd.onCreateView(inflater, container, this.confirmDeletions);
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        this.pd.onResume();
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        super.onStop();
        this.pd.onStop();
    }

    @Override // android.support.v4.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.pd.onSaveInstanceState(outState);
    }

    @Override // android.support.v4.app.ListFragment
    public void onListItemClick(ListView l, View v, int position, long id) {
        this.pd.onListItemClick(v);
    }

    /* loaded from: classes.dex */
    public static class ConfirmDeletions extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((PersonalDictionaryFragment) getTargetFragment()).pd.createDialog();
        }
    }
}
