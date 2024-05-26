package com.android.support.v4.preference;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.android.support.v4.preference.PreferenceManagerCompat;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public abstract class PreferenceFragment extends Fragment implements PreferenceManagerCompat.OnPreferenceTreeClickListener {
    private static final int FIRST_REQUEST_CODE = 100;
    private static final int MSG_BIND_PREFERENCES = 1;
    private static final String PREFERENCES_TAG = "android:preferences";
    private boolean mHavePrefs;
    private boolean mInitDone;
    private ListView mList;
    private PreferenceManager mPreferenceManager;
    private Handler mHandler = new Handler() { // from class: com.android.support.v4.preference.PreferenceFragment.1
        @Override // android.os.Handler
        public final void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    PreferenceFragment.this.bindPreferences();
                    return;
                default:
                    return;
            }
        }
    };
    private final Runnable mRequestFocus = new Runnable() { // from class: com.android.support.v4.preference.PreferenceFragment.2
        @Override // java.lang.Runnable
        public final void run() {
            PreferenceFragment.this.mList.focusableViewAvailable(PreferenceFragment.this.mList);
        }
    };
    private View.OnKeyListener mListOnKeyListener = new View.OnKeyListener() { // from class: com.android.support.v4.preference.PreferenceFragment.3
        @Override // android.view.View.OnKeyListener
        public final boolean onKey(View v, int keyCode, KeyEvent event) {
            if (PreferenceFragment.this.mList.getSelectedItem() instanceof Preference) {
                PreferenceFragment.this.mList.getSelectedView();
            }
            return false;
        }
    };

    /* loaded from: classes.dex */
    public interface OnPreferenceStartFragmentCallback {
        boolean onPreferenceStartFragment$1d864d12();
    }

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        this.mPreferenceManager = PreferenceManagerCompat.newInstance$65cee2cb(getActivity());
        PreferenceManagerCompat.setFragment$7b6d5139();
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        return paramLayoutInflater.inflate(R.layout.preference_list_fragment, paramViewGroup, false);
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle container;
        PreferenceScreen preferenceScreen;
        super.onActivityCreated(savedInstanceState);
        if (this.mHavePrefs) {
            bindPreferences();
        }
        this.mInitDone = true;
        if (savedInstanceState != null && (container = savedInstanceState.getBundle(PREFERENCES_TAG)) != null && (preferenceScreen = getPreferenceScreen()) != null) {
            preferenceScreen.restoreHierarchyState(container);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        PreferenceManagerCompat.setOnPreferenceTreeClickListener(this.mPreferenceManager, this);
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        super.onStop();
        PreferenceManagerCompat.dispatchActivityStop(this.mPreferenceManager);
        PreferenceManagerCompat.setOnPreferenceTreeClickListener(this.mPreferenceManager, null);
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroyView() {
        this.mList = null;
        this.mHandler.removeCallbacks(this.mRequestFocus);
        this.mHandler.removeMessages(1);
        super.onDestroyView();
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        PreferenceManagerCompat.dispatchActivityDestroy(this.mPreferenceManager);
    }

    @Override // android.support.v4.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            Bundle container = new Bundle();
            preferenceScreen.saveHierarchyState(container);
            outState.putBundle(PREFERENCES_TAG, container);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PreferenceManagerCompat.dispatchActivityResult(this.mPreferenceManager, requestCode, resultCode, data);
    }

    public PreferenceManager getPreferenceManager() {
        return this.mPreferenceManager;
    }

    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        if (PreferenceManagerCompat.setPreferences(this.mPreferenceManager, preferenceScreen) && preferenceScreen != null) {
            this.mHavePrefs = true;
            if (this.mInitDone) {
                postBindPreferences();
            }
        }
    }

    public PreferenceScreen getPreferenceScreen() {
        return PreferenceManagerCompat.getPreferenceScreen(this.mPreferenceManager);
    }

    public void addPreferencesFromIntent(Intent intent) {
        requirePreferenceManager();
        setPreferenceScreen(PreferenceManagerCompat.inflateFromIntent(this.mPreferenceManager, intent, getPreferenceScreen()));
    }

    public void addPreferencesFromResource(int preferencesResId) {
        requirePreferenceManager();
        setPreferenceScreen(PreferenceManagerCompat.inflateFromResource(this.mPreferenceManager, getActivity(), preferencesResId, getPreferenceScreen()));
    }

    @Override // com.android.support.v4.preference.PreferenceManagerCompat.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return (getActivity() instanceof OnPreferenceStartFragmentCallback) && ((OnPreferenceStartFragmentCallback) getActivity()).onPreferenceStartFragment$1d864d12();
    }

    public Preference findPreference(CharSequence key) {
        if (this.mPreferenceManager == null) {
            return null;
        }
        return this.mPreferenceManager.findPreference(key);
    }

    private void requirePreferenceManager() {
        if (this.mPreferenceManager == null) {
            throw new RuntimeException("This should be called after super.onCreate.");
        }
    }

    private void postBindPreferences() {
        if (!this.mHandler.hasMessages(1)) {
            this.mHandler.obtainMessage(1).sendToTarget();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bindPreferences() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.bind(getListView());
        }
    }

    public ListView getListView() {
        ensureList();
        return this.mList;
    }

    private void ensureList() {
        if (this.mList == null) {
            View root = getView();
            if (root == null) {
                throw new IllegalStateException("Content view not yet created");
            }
            View rawListView = root.findViewById(android.R.id.list);
            if (!(rawListView instanceof ListView)) {
                throw new RuntimeException("Content has view with id attribute 'android.R.id.list' that is not a ListView class");
            }
            this.mList = (ListView) rawListView;
            if (this.mList == null) {
                throw new RuntimeException("Your content must have a ListView whose id attribute is 'android.R.id.list'");
            }
            this.mList.setOnKeyListener(this.mListOnKeyListener);
            this.mHandler.post(this.mRequestFocus);
        }
    }
}
