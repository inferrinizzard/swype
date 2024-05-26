package com.nuance.swype.input.settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.v4.app.DialogFragment;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.connect.ConnectedStatus;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;

/* loaded from: classes.dex */
public class ChinesePrefsFragment extends ActionbarPreferenceFragment {
    protected static final String KEY_DIALOG_ID = "DIALOG_ID";
    private static final int MESSAGE_CANCEL_DIALOG = 0;
    private static final int MESSAGE_SHOW_DIALOG = 1;
    private static final int REQUEST_CODE_ACCEPT_LICENSE = 2;
    private ConnectedStatus connectedStatus;
    protected ChinesePrefs delegate;
    protected final String MAX_TAG = "max_item";
    private boolean isSwitchingToDefaultScreen = false;
    private Handler.Callback callback = new Handler.Callback() { // from class: com.nuance.swype.input.settings.ChinesePrefsFragment.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ChinesePrefsFragment.this.delegate.doCancelDialog(msg.arg1);
                    return true;
                case 1:
                    ChinesePrefsFragment.this.delegate.doShowDialog(msg.arg1, msg.getData());
                    return true;
                default:
                    return false;
            }
        }
    };
    private Handler queue = WeakReferenceHandler.create(this.callback);

    @Override // com.nuance.swype.input.settings.ActionbarPreferenceFragment, com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.delegate = createChinesePrefs();
        setHasOptionsMenu(false);
        this.connectedStatus = new ConnectedStatus(getActivity()) { // from class: com.nuance.swype.input.settings.ChinesePrefsFragment.2
            @Override // com.nuance.swype.connect.ConnectedStatus
            public void onConnectionChanged(boolean isConnected) {
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.nuance.swype.input.settings.ChinesePrefsFragment$3, reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass3 extends ChinesePrefs {
        AnonymousClass3(Activity activity) {
            super(activity);
        }

        @Override // com.nuance.swype.input.settings.ChinesePrefs
        protected Preference createInputModePref(Bundle args) {
            Context context = ChinesePrefsFragment.this.getActivity();
            Preference pref = new Preference(context);
            pref.setIntent(new Intent(context, (Class<?>) InputPrefsFragmentActivity.class).putExtras(args));
            return pref;
        }

        @Override // com.nuance.swype.input.settings.ChinesePrefs
        protected Preference createFunctionBarPref() {
            Context context = ChinesePrefsFragment.this.getActivity();
            Preference pref = new Preference(context);
            pref.setIntent(new Intent(context, (Class<?>) FunctionBarFragmentActivity.class));
            return pref;
        }

        @Override // com.nuance.swype.input.settings.ChinesePrefs
        protected Preference createClouldNetWorkPref() {
            Preference pref = new Preference(ChinesePrefsFragment.this.getActivity());
            pref.setKey(UserPreferences.PREF_CLOUD_INPUT);
            return pref;
        }

        @Override // com.nuance.swype.input.settings.ChinesePrefs
        protected Preference creatAddOnDictionaryPref() {
            Context context = ChinesePrefsFragment.this.getActivity();
            Preference pref = new Preference(context);
            pref.setIntent(new Intent(context, (Class<?>) AddonDictionariesPrefsFragmentActivity.class));
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.ChinesePrefsFragment.3.1
                @Override // android.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent;
                    if (!ChinesePrefsFragment.this.connectedStatus.isConnectedWifi() && (!ChinesePrefsFragment.this.connectedStatus.isConnectedCellular() || !ChinesePrefsFragment.this.connectedStatus.isDataConnectionPermitted())) {
                        AnonymousClass3.this.showConnectDialog();
                        return true;
                    }
                    UserPreferences userPrefs = UserPreferences.from(AnonymousClass3.this.activity);
                    if (AnonymousClass3.this.activity.getResources().getBoolean(R.bool.enable_china_connect_special) && userPrefs.shouldShowNetworkAgreementDialog()) {
                        AnonymousClass3.this.showNetworkNotificationDialog();
                        return true;
                    }
                    if (ConnectLegal.from(ChinesePrefsFragment.this.getActivity()).isTosAccepted() || (intent = ConnectLegal.getLegalActivitiesStartIntent(AnonymousClass3.this.activity, true, false, null)) == null) {
                        return false;
                    }
                    AnonymousClass3.this.doStartActivityForResult(intent, 2);
                    return true;
                }
            });
            return pref;
        }

        @Override // com.nuance.swype.input.settings.ChinesePrefs
        protected PreferenceScreen addPreferences() {
            ChinesePrefsFragment.this.addPreferencesFromResource(CHINESE_PREFS_XML);
            return ChinesePrefsFragment.this.getPreferenceScreen();
        }

        @Override // com.nuance.swype.input.settings.ChinesePrefs
        protected void showMaxCountdialog() {
            MaxItemDlg dialog = new MaxItemDlg();
            dialog.setTargetFragment(ChinesePrefsFragment.this, 0);
            dialog.show(ChinesePrefsFragment.this.getFragmentManager(), "max_item");
        }

        @Override // com.nuance.swype.input.settings.ChinesePrefs
        void doShowDialog(int id, Bundle args) {
            if (args == null) {
                args = new Bundle();
            }
            if (ChinesePrefsFragment.this.getFragmentManager() == null) {
                Message m = ChinesePrefsFragment.this.queue.obtainMessage(1);
                m.arg1 = id;
                m.setData(args);
                ChinesePrefsFragment.this.queue.sendMessageDelayed(m, 50L);
                return;
            }
            doCancelDialog(id);
            args.putInt(ChinesePrefsFragment.KEY_DIALOG_ID, id);
            DialogFragment dialog = new GenericDialog();
            dialog.setArguments(args);
            dialog.setTargetFragment(ChinesePrefsFragment.this, 0);
            dialog.show(ChinesePrefsFragment.this.getFragmentManager(), String.valueOf(id));
        }

        @Override // com.nuance.swype.input.settings.ChinesePrefs
        void doCancelDialog(int id) {
            if (ChinesePrefsFragment.this.getFragmentManager() == null) {
                Message m = ChinesePrefsFragment.this.queue.obtainMessage(0);
                m.arg1 = id;
                ChinesePrefsFragment.this.queue.sendMessageDelayed(m, 50L);
            } else {
                ChinesePrefsFragment.this.delegate.removeActiveRef(id);
                DialogFragment fragment = (DialogFragment) ChinesePrefsFragment.this.getFragmentManager().findFragmentByTag(String.valueOf(id));
                if (fragment != null) {
                    fragment.dismiss();
                }
            }
        }

        @Override // android.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference arg0) {
            return false;
        }

        @Override // com.nuance.swype.input.settings.ChinesePrefs
        void doStartActivityForResult(Intent intent, int requestCode) {
            ChinesePrefsFragment.this.startActivityForResult(intent, requestCode);
        }

        @Override // com.nuance.swype.input.settings.ChinesePrefs
        protected void showNetworkNotificationDialog() {
            NetworkNotificationDialg dialog = new NetworkNotificationDialg();
            dialog.setTargetFragment(ChinesePrefsFragment.this, 0);
            dialog.show(ChinesePrefsFragment.this.getFragmentManager(), "network");
        }

        @Override // com.nuance.swype.input.settings.ChinesePrefs
        protected void showDownloadAddonDictionaries() {
            IMEApplication.from(ChinesePrefsFragment.this.getActivity()).showAddonDictionaries(false);
        }

        @Override // com.nuance.swype.input.settings.ChinesePrefs
        protected void showConnectDialog() {
            DialogFragment dialog = new ConnectDialog();
            dialog.setTargetFragment(ChinesePrefsFragment.this, 0);
            dialog.show(ChinesePrefsFragment.this.getFragmentManager(), "connect");
        }
    }

    protected ChinesePrefs createChinesePrefs() {
        return new AnonymousClass3(getActivity());
    }

    @Override // com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                this.delegate.onActivityResult(requestCode, resultCode, data);
                return;
            case 2:
                if (resultCode == -1) {
                    Context context = getActivity();
                    Preference preference = new Preference(context);
                    preference.setIntent(new Intent(context, (Class<?>) AddonDictionariesPrefsFragmentActivity.class));
                    preference.setTitle(R.string.download_addon_dictionaries);
                    return;
                }
                return;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                return;
        }
    }

    @Override // com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        this.isSwitchingToDefaultScreen = false;
        this.delegate.onStart();
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        Activity activity = getActivity();
        if (activity != null && !IMEApplication.from(activity).getInputMethods().getCurrentInputLanguage().isChineseLanguage()) {
            if (!this.isSwitchingToDefaultScreen) {
                this.delegate.dismissDialog();
                if (activity instanceof SettingsV11) {
                    this.isSwitchingToDefaultScreen = true;
                    ((SettingsV11) activity).switchToDefaultScreen();
                    return;
                } else {
                    if (activity instanceof SettingsV19) {
                        this.isSwitchingToDefaultScreen = true;
                        ((SettingsV19) activity).switchToDefaultScreen();
                        return;
                    }
                    return;
                }
            }
            return;
        }
        this.delegate.onResume();
        this.connectedStatus.register();
    }

    @Override // android.support.v4.app.Fragment
    public void onPause() {
        super.onPause();
        this.connectedStatus.unregister();
    }

    @Override // com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onStop() {
        super.onStop();
        this.delegate.onStop();
        this.queue.removeMessages(0);
        this.queue.removeMessages(1);
    }

    /* loaded from: classes.dex */
    public static class GenericDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((ChinesePrefsFragment) getTargetFragment()).delegate.doCreateDialog(getArguments().getInt(ChinesePrefsFragment.KEY_DIALOG_ID), getActivity(), getArguments());
        }
    }

    /* loaded from: classes.dex */
    public static class MaxItemDlg extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((ChinesePrefsFragment) getTargetFragment()).delegate.createMaxItemDlg();
        }
    }

    /* loaded from: classes.dex */
    public static class NetworkNotificationDialg extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((ChinesePrefsFragment) getTargetFragment()).delegate.createNetworkNotificationDialg();
        }
    }

    /* loaded from: classes.dex */
    public static class ConnectDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((ChinesePrefsFragment) getTargetFragment()).delegate.createConnectDialog();
        }
    }
}
