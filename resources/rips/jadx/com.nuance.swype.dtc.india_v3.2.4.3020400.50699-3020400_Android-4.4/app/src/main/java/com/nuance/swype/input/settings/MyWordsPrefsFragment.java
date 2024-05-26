package com.nuance.swype.input.settings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.v4.app.DialogFragment;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.swype.input.settings.MyWordsPrefs;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class MyWordsPrefsFragment extends ActionbarPreferenceFragment {
    private static final String KEY_DIALOG_ID = "DIALOG_ID";
    private static final int MESSAGE_CANCEL_DIALOG = 0;
    private static final int MESSAGE_SHOW_DIALOG = 1;
    private static final LogManager.Log log = LogManager.getLog("MyWordsPrefs");
    protected MyWordsPrefs delegate;
    private Handler.Callback callback = new Handler.Callback() { // from class: com.nuance.swype.input.settings.MyWordsPrefsFragment.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    MyWordsPrefsFragment.this.delegate.doCancelDialog(msg.arg1);
                    return false;
                case 1:
                    MyWordsPrefsFragment.this.delegate.doShowDialog(msg.arg1, msg.getData());
                    return false;
                default:
                    MyWordsPrefsFragment.log.d("Unknow message: ", Integer.valueOf(msg.what));
                    return false;
            }
        }
    };
    private Handler queue = WeakReferenceHandler.create(this.callback);

    @Override // com.nuance.swype.input.settings.ActionbarPreferenceFragment, com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.delegate = createMyWordsPrefs();
        setHasOptionsMenu(false);
    }

    protected MyWordsPrefs createMyWordsPrefs() {
        return new MyWordsPrefs(getActivity()) { // from class: com.nuance.swype.input.settings.MyWordsPrefsFragment.2
            @Override // com.nuance.swype.input.settings.MyWordsPrefs
            protected PreferenceScreen addPreferences() {
                MyWordsPrefsFragment.this.addPreferencesFromResource(MY_WORDS_PREFS_XML);
                return MyWordsPrefsFragment.this.getPreferenceScreen();
            }

            @Override // com.nuance.swype.input.settings.MyWordsPrefs
            void doShowDialog(int id, Bundle args) {
                DialogFragment dialog;
                if (args == null) {
                    args = new Bundle();
                }
                if (MyWordsPrefsFragment.this.getFragmentManager() == null || !MyWordsPrefsFragment.this.isResumed()) {
                    Message m = MyWordsPrefsFragment.this.queue.obtainMessage(1);
                    m.arg1 = id;
                    m.setData(args);
                    MyWordsPrefsFragment.this.queue.sendMessageDelayed(m, 50L);
                    return;
                }
                doCancelDialog(id);
                args.putInt(MyWordsPrefsFragment.KEY_DIALOG_ID, id);
                switch (id) {
                    case 5:
                        dialog = AccountActivationDialog.newInstance();
                        break;
                    case 6:
                        dialog = new PrivacyDialog();
                        break;
                    case 7:
                        dialog = new AskBeforeAddDialg();
                        break;
                    case 8:
                        dialog = new ShowEraseUdbDialg();
                        break;
                    case 9:
                        dialog = new NetworkNotificationDialg();
                        break;
                    default:
                        dialog = new GenericDialog();
                        break;
                }
                dialog.setArguments(args);
                dialog.setTargetFragment(MyWordsPrefsFragment.this, 0);
                dialog.show(MyWordsPrefsFragment.this.getFragmentManager(), String.valueOf(id));
            }

            @Override // com.nuance.swype.input.settings.MyWordsPrefs
            void doCancelDialog(int id) {
                if (MyWordsPrefsFragment.this.getFragmentManager() == null || !MyWordsPrefsFragment.this.isResumed()) {
                    Message m = MyWordsPrefsFragment.this.queue.obtainMessage(0);
                    m.arg1 = id;
                    MyWordsPrefsFragment.this.queue.sendMessageDelayed(m, 50L);
                    return;
                }
                MyWordsPrefsFragment.this.delegate.removeActiveRef(id);
                DialogFragment fragment = (DialogFragment) MyWordsPrefsFragment.this.getFragmentManager().findFragmentByTag(String.valueOf(id));
                if (fragment != null) {
                    if (!fragment.isResumed()) {
                        Message m2 = MyWordsPrefsFragment.this.queue.obtainMessage(0);
                        m2.arg1 = id;
                        MyWordsPrefsFragment.this.queue.sendMessageDelayed(m2, 50L);
                        return;
                    }
                    fragment.dismiss();
                }
            }

            @Override // com.nuance.swype.input.settings.MyWordsPrefs
            protected void doStartActivityForResult(Intent intent, int requestCode) {
                MyWordsPrefsFragment.this.startActivityForResult(intent, requestCode);
            }

            @Override // com.nuance.swype.input.settings.MyWordsPrefs
            protected void showPreferenceFragment(Preference pref) {
                MyWordsPrefsFragment.this.getActivity();
                pref.getFragment();
            }

            @Override // com.nuance.swype.input.settings.MyWordsPrefs
            protected void doStartActivity(Intent intent) {
                MyWordsPrefsFragment.this.startActivity(intent);
            }
        };
    }

    @Override // com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
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

    @Override // com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onStop() {
        super.onStop();
        this.delegate.onStop();
        this.queue.removeMessages(0);
        this.queue.removeMessages(1);
    }

    @Override // com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.delegate.onActivityResult(requestCode, resultCode, data);
    }

    @Override // com.android.support.v4.preference.PreferenceFragment, com.android.support.v4.preference.PreferenceManagerCompat.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {
        return this.delegate.onPreferenceTreeClick(prefScreen, pref) || super.onPreferenceTreeClick(prefScreen, pref);
    }

    /* loaded from: classes.dex */
    public static class GenericDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((MyWordsPrefsFragment) getTargetFragment()).delegate.doCreateDialog(getArguments().getInt(MyWordsPrefsFragment.KEY_DIALOG_ID), getActivity(), getArguments());
        }
    }

    /* loaded from: classes.dex */
    public static class AskBeforeAddDialg extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((MyWordsPrefsFragment) getTargetFragment()).delegate.createAskBeforeAddCheckDlg();
        }
    }

    /* loaded from: classes.dex */
    public static class ShowEraseUdbDialg extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((MyWordsPrefsFragment) getTargetFragment()).delegate.createEraseUdbConfirmationDlg();
        }
    }

    /* loaded from: classes.dex */
    public static class PrivacyDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((MyWordsPrefsFragment) getTargetFragment()).delegate.doCreateDialog(getArguments().getInt(MyWordsPrefsFragment.KEY_DIALOG_ID), getActivity(), getArguments());
        }

        @Override // android.support.v4.app.DialogFragment, android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
        }
    }

    /* loaded from: classes.dex */
    public static class AccountActivationDialog extends DialogFragment {
        static AccountActivationDialog newInstance() {
            return new AccountActivationDialog();
        }

        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            boolean inProgress = false;
            if (savedInstanceState != null) {
                inProgress = savedInstanceState.getBoolean(MyWordsPrefs.AlertProgressDialog.PREF_PROGRESS_SHOWING);
            }
            Dialog d = ((MyWordsPrefsFragment) getTargetFragment()).delegate.doCreateDialog(getArguments().getInt(MyWordsPrefsFragment.KEY_DIALOG_ID), getActivity(), getArguments());
            d.getWindow().setSoftInputMode(4);
            if ((d instanceof MyWordsPrefs.AlertProgressDialog) && inProgress) {
                ((MyWordsPrefs.AlertProgressDialog) d).inProgress();
            }
            return d;
        }

        @Override // android.support.v4.app.DialogFragment, android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialog) {
            ((MyWordsPrefsFragment) getTargetFragment()).delegate.doCancelDialog(5);
            super.onCancel(dialog);
        }

        @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
        public void onSaveInstanceState(Bundle outState) {
            Dialog d = getDialog();
            if (d instanceof MyWordsPrefs.AlertProgressDialog) {
                outState.putBoolean(MyWordsPrefs.AlertProgressDialog.PREF_PROGRESS_SHOWING, ((MyWordsPrefs.AlertProgressDialog) d).isProgressShowing());
            }
            super.onSaveInstanceState(outState);
        }

        @Override // android.support.v4.app.Fragment
        public void onResume() {
            Dialog d = getDialog();
            if ((d instanceof MyWordsPrefs.AlertProgressDialog) && ((MyWordsPrefs.AlertProgressDialog) d).isProgressShowing()) {
                ((MyWordsPrefs.AlertProgressDialog) d).updateView();
            }
            super.onResume();
        }
    }

    /* loaded from: classes.dex */
    public static class NetworkNotificationDialg extends DialogFragment {
        private String mPrefKey = null;

        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            MyWordsPrefs delegate = ((MyWordsPrefsFragment) getTargetFragment()).delegate;
            if (this.mPrefKey != null) {
                delegate.mPrefKey = this.mPrefKey;
            }
            return delegate.createNetworkNotificationDialg();
        }

        @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            if (outState != null) {
                MyWordsPrefs delegate = ((MyWordsPrefsFragment) getTargetFragment()).delegate;
                outState.putString("prefKey", delegate.mPrefKey);
            }
        }

        @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
        public void onCreate(Bundle savedInstanceState) {
            String prefKey;
            super.onCreate(savedInstanceState);
            if (savedInstanceState != null && (prefKey = savedInstanceState.getString("prefKey")) != null) {
                this.mPrefKey = prefKey;
            }
        }
    }
}
