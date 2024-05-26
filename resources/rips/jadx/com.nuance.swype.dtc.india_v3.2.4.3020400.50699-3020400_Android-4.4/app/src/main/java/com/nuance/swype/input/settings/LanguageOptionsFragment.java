package com.nuance.swype.input.settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import com.nuance.connect.common.Strings;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes.dex */
public class LanguageOptionsFragment extends ActionbarPreferenceFragment {
    protected LanguageOptions delegate;
    private boolean keyIsDown = false;

    @Override // com.nuance.swype.input.settings.ActionbarPreferenceFragment, com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(LanguageOptions.LANGUAGE_PREFS_XML);
        this.delegate = createLanguageOptions();
        setHasOptionsMenu(false);
    }

    protected LanguageOptions createLanguageOptions() {
        return new LanguageOptions(getActivity(), getPreferenceScreen()) { // from class: com.nuance.swype.input.settings.LanguageOptionsFragment.1
            @Override // com.nuance.swype.input.settings.LanguageOptions
            protected void showConnectDialog() {
                DialogFragment dialog = new ConnectDialog();
                dialog.setTargetFragment(LanguageOptionsFragment.this, 0);
                dialog.show(LanguageOptionsFragment.this.getFragmentManager(), "dialog");
            }

            @Override // com.nuance.swype.input.settings.LanguageOptions
            protected void doStartActivityForResult(Intent intent, int requestCode) {
                LanguageOptionsFragment.this.startActivityForResult(intent, requestCode);
            }

            private void showDialog(Bundle args, DialogFragment dialog, String tag) {
                dialog.setTargetFragment(LanguageOptionsFragment.this, 0);
                dialog.setArguments(args);
                FragmentTransaction transaction = LanguageOptionsFragment.this.getFragmentManager().beginTransaction();
                transaction.add(dialog, tag);
                transaction.commitAllowingStateLoss();
            }

            @Override // com.nuance.swype.input.settings.LanguageOptions
            protected void showLanguageDialog(Bundle args) {
                showDialog(args, new LanguageDialog(), Strings.MESSAGE_BUNDLE_LANGUAGE);
            }

            @Override // com.nuance.swype.input.settings.LanguageOptions
            protected void showKeyboardDialog(Bundle args) {
                showDialog(args, new KeyboardDialog(), "keyboard");
            }

            @Override // com.nuance.swype.input.settings.LanguageOptions
            protected void showLanguageDownload(Preference downloadLanguages) {
                if (downloadLanguages != null && LanguageOptionsFragment.this.isAdded() && LanguageOptionsFragment.this.delegate.activity.getResources().getBoolean(R.bool.enable_china_connect_special) && downloadLanguages.getKey().equals("download_languages") && UserPreferences.from(LanguageOptionsFragment.this.delegate.activity).shouldShowNetworkAgreementDialog()) {
                    LanguageOptionsFragment.this.delegate.showNetworkNotificationDialog(downloadLanguages.getKey());
                    return;
                }
                if (downloadLanguages != null) {
                    Activity activity = LanguageOptionsFragment.this.getActivity();
                    if (downloadLanguages.getFragment() != null || downloadLanguages.getIntent() != null) {
                        IMEApplication.from(activity).showLanguageDownloads();
                    }
                }
            }

            @Override // com.nuance.swype.input.settings.LanguageOptions
            protected void refresh() {
                Activity a = LanguageOptionsFragment.this.getActivity();
                if (a instanceof SettingsV11) {
                    ((SettingsV11) a).updateDrawerList();
                }
            }

            @Override // com.nuance.swype.input.settings.LanguageOptions
            protected void showNetworkNotificationDialog(String mPrefKey) {
                DialogFragment dialog = new NetworkNotificationDialg();
                dialog.setTargetFragment(LanguageOptionsFragment.this, 0);
                dialog.show(LanguageOptionsFragment.this.getFragmentManager(), "network");
            }

            @Override // com.nuance.swype.input.settings.LanguageOptions
            protected void showLanguageDownload(String mPrefKey) {
                Preference pref = LanguageOptionsFragment.this.getPreferenceManager().findPreference(mPrefKey);
                if (pref != null) {
                    Activity activity = LanguageOptionsFragment.this.getActivity();
                    if (pref.getFragment() != null || pref.getIntent() != null) {
                        IMEApplication.from(activity).showLanguageDownloads();
                    }
                }
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
    }

    @Override // com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.delegate.onActivityResult(requestCode, resultCode, data);
    }

    /* loaded from: classes.dex */
    public static class ConnectDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LanguageOptions delegate = ((LanguageOptionsFragment) getTargetFragment()).delegate;
            if (delegate != null) {
                return delegate.createConnectDialog();
            }
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static class CommonDialog extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment, android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            LanguageOptions delegate = ((LanguageOptionsFragment) getTargetFragment()).delegate;
            if (delegate != null) {
                delegate.setDialogShowing(false);
            }
            Activity a = getActivity();
            if (a instanceof SettingsV11) {
                ((SettingsV11) a).updateDrawerList();
            }
        }
    }

    @Override // com.android.support.v4.preference.PreferenceFragment, com.android.support.v4.preference.PreferenceManagerCompat.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {
        if (this.delegate == null || !this.delegate.activity.getResources().getBoolean(R.bool.enable_china_connect_special) || pref == null || pref.getKey() == null || !pref.getKey().equals("download_languages") || !UserPreferences.from(this.delegate.activity).shouldShowNetworkAgreementDialog()) {
            return super.onPreferenceTreeClick(prefScreen, pref);
        }
        this.delegate.showNetworkNotificationDialog(pref.getKey());
        return true;
    }

    /* loaded from: classes.dex */
    public static class LanguageDialog extends CommonDialog {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LanguageOptions delegate = ((LanguageOptionsFragment) getTargetFragment()).delegate;
            if (delegate != null) {
                return delegate.createLanguageDialog(getArguments());
            }
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyboardDialog extends CommonDialog {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LanguageOptions delegate = ((LanguageOptionsFragment) getTargetFragment()).delegate;
            if (delegate != null) {
                return delegate.createKeyboardDialog(getArguments());
            }
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static class NetworkNotificationDialg extends DialogFragment {
        @Override // android.support.v4.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ((LanguageOptionsFragment) getTargetFragment()).delegate.createNetworkNotificationDialg();
        }

        @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            if (outState != null) {
                LanguageOptions delegate = ((LanguageOptionsFragment) getTargetFragment()).delegate;
                outState.putString("prefKey", delegate.mPrefKey);
            }
        }

        @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
        public void onCreate(Bundle savedInstanceState) {
            String prefKey;
            super.onCreate(savedInstanceState);
            if (savedInstanceState != null && (prefKey = savedInstanceState.getString("prefKey")) != null) {
                ((LanguageOptionsFragment) getTargetFragment()).delegate.mPrefKey = prefKey;
            }
        }
    }

    @Override // com.nuance.swype.input.settings.ActionbarPreferenceFragment, com.android.support.v4.preference.PreferenceFragment, android.support.v4.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            Object obj = getClass().getMethod("getListView", new Class[0]).invoke(this, new Object[0]);
            if (obj != null && (obj instanceof ListView)) {
                final ListView list = (ListView) obj;
                list.setOnKeyListener(new View.OnKeyListener() { // from class: com.nuance.swype.input.settings.LanguageOptionsFragment.2
                    @Override // android.view.View.OnKeyListener
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode != 20 && keyCode != 19 && keyCode != 21 && keyCode != 22) {
                            return false;
                        }
                        if (event.getAction() == 0) {
                            LanguageOptionsFragment.this.keyIsDown = true;
                            return true;
                        }
                        if (!LanguageOptionsFragment.this.keyIsDown) {
                            return true;
                        }
                        LanguageOptionsFragment.this.keyIsDown = false;
                        if (list.getSelectedItemPosition() != 1) {
                            LanguageOptionsFragment.this.onKeyForFocusChanging(v, keyCode, event);
                        } else {
                            View currentLanguage = list.getSelectedView();
                            View language = currentLanguage.findViewById(R.id.language_item);
                            View additionalLanguage = currentLanguage.findViewById(R.id.language_preference_additional_name);
                            View mode = currentLanguage.findViewById(android.R.id.widget_frame);
                            if (!language.isFocused() && !additionalLanguage.isFocused() && !mode.isFocused()) {
                                language.requestFocus();
                                return true;
                            }
                        }
                        return true;
                    }
                });
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onKeyForFocusChanging(View v, int keyCode, KeyEvent event) {
        View nextView = null;
        if (keyCode == 19) {
            nextView = v.focusSearch(33);
        } else if (keyCode == 21) {
            nextView = v.focusSearch(17);
        } else if (keyCode == 22) {
            nextView = v.focusSearch(66);
        } else if (keyCode == 20) {
            nextView = v.focusSearch(130);
        }
        if (nextView != null) {
            nextView.requestFocus();
            return true;
        }
        return true;
    }
}
