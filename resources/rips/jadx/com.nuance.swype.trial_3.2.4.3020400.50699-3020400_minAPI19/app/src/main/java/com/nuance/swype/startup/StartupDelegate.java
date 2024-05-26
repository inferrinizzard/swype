package com.nuance.swype.startup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.R;
import com.nuance.swype.startup.StartupDelegate;
import com.nuance.swype.startup.StartupSequenceInfo;
import com.nuance.swype.stats.UsageManager;
import com.nuance.swype.util.LogManager;
import java.util.Locale;

/* loaded from: classes.dex */
public class StartupDelegate<T extends StartupDelegate<T>> extends Fragment {
    protected StartupActivityCallbacks mActivityCallbacks;
    private Dialog mDialog;
    protected int mFlags;
    protected Bundle mResultData;
    protected StartupSequenceInfo mStartupSequenceInfo;
    protected View view;
    private static final LogManager.Log log = LogManager.getLog("StartupDelegate");
    private static final int DEFAULT_LAYOUT_TEMPLATE = R.layout.startup_template;
    private static final int CONTENT_AREA_ID = R.id.firstStartContent;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface StartupActivityCallbacks {
        void cancelSequence();

        void finishSequence(boolean z);

        StartupDelegate getCurrentDelegate();

        StartupSequenceInfo getStartupSequenceInfo();

        void notifyStartupListener(StartupListener.LISTENER_KEY listener_key, Bundle bundle);

        void registerListener(StartupListener startupListener);

        void restartSequence(int i, Bundle bundle);

        void showDelegate(StartupDelegate startupDelegate);

        void showKeyboardOnFinish$1385ff();

        void startNextScreen(int i, Bundle bundle);

        void unregisterListener();
    }

    /* loaded from: classes.dex */
    interface StartupListener {

        /* loaded from: classes.dex */
        public enum LISTENER_KEY {
            SKIP_LISTENER_KEY,
            CANCEL_LISTENER_KEY,
            ACCEPT_LISTENER_KEY,
            ACTIVITY_RESULT_OK_LISTENER_KEY,
            ACTIVITY_RESULT_CANCEL_LISTENER_KEY
        }

        void onAcceptListener(Context context);

        void onCancelListener();

        void onSkipListener();
    }

    @Override // android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.d("onCreate: " + this);
        if (getArguments() == null) {
            throw new IllegalArgumentException("Bundle data required");
        }
        if (!getArguments().containsKey("param_next_screen_is_null")) {
            throw new IllegalArgumentException("param_next_screen_is_null value required");
        }
        if (!getArguments().containsKey("param_show_registration_warning")) {
            throw new IllegalArgumentException("param_show_registration_warning value required");
        }
        if (!getArguments().containsKey("param_theme_id")) {
            throw new IllegalArgumentException("param_theme_id value required");
        }
        this.mResultData = getArguments().getBundle("param_data");
        this.mFlags = getArguments().getInt("param_flags");
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return this.view;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean getShowRegistrationWarning() {
        return getArguments().getBoolean("param_show_registration_warning");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int getThemeID() {
        return getArguments().getInt("param_theme_id");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean isNextScreenNull() {
        return getArguments().getBoolean("param_next_screen_is_null");
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        log.d("onAttach: " + this + ", activity: " + activity);
        try {
            this.mActivityCallbacks = (StartupActivityCallbacks) activity;
            this.mStartupSequenceInfo = this.mActivityCallbacks.getStartupSequenceInfo();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement StartupActivityCallbacks");
        }
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        log.d("onResume: " + this);
    }

    @Override // android.app.Fragment
    public void onPause() {
        super.onPause();
        log.d("onPause: " + this);
    }

    @Override // android.app.Fragment
    public void onStop() {
        super.onStop();
        log.d("onStop: " + this);
        if (this.mDialog != null && this.mDialog.isShowing()) {
            this.mDialog.dismiss();
        }
    }

    @Override // android.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        log.d("onDestroy: " + this);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
    }

    public void onBackPressed() {
        log.d("onBackPressed");
        showSelectSwypeDialog();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isSwypeEnabled(Context context) {
        return StartupSequenceInfo.getSwypeIMEState(context) != StartupSequenceInfo.SwypeIMEState.DISABLED;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void showSelectSwypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme.DeviceDefault.Light.Dialog));
        builder.setTitle(R.string.legal_doc_cud_title).setMessage(R.string.startup_exit_error).setIcon(R.drawable.swype_logo).setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.legal_doc_decline, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.startup.StartupDelegate.1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int which) {
                StartupDelegate.this.mActivityCallbacks.cancelSequence();
            }
        }).create();
        builder.show();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void showDialog(Dialog dialog) {
        this.mDialog = dialog;
        this.mDialog.show();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void loadTemplateToContentView(LayoutInflater inflater, int innerContentId, int titleId) {
        loadTemplateToContentView(inflater, DEFAULT_LAYOUT_TEMPLATE, innerContentId, titleId);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void loadTemplateToContentView(LayoutInflater inflater, int layoutTemplateId, int innerContentId, int titleId) {
        loadTemplateToContentView(inflater, layoutTemplateId, innerContentId, (String) getActivity().getText(titleId));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void loadTemplateToContentView(LayoutInflater inflater, int layoutTemplateId, int innerContentId, String title) {
        this.view = inflater.inflate(layoutTemplateId, (ViewGroup) null);
        TextView pageTitle = (TextView) this.view.findViewById(R.id.pageDesc);
        if (pageTitle != null) {
            if (title == null) {
                pageTitle.setVisibility(8);
            } else {
                pageTitle.setText(title);
            }
        }
        ViewGroup pageContents = (ViewGroup) this.view.findViewById(CONTENT_AREA_ID);
        pageContents.removeAllViews();
        inflater.inflate(innerContentId, pageContents);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setupNegativeButton$411327c6(String buttonText, View.OnClickListener l) {
        Button b = (Button) this.view.findViewById(R.id.startup_button_negative);
        if (b != null) {
            b.setEnabled(true);
            b.setText(buttonText);
            b.setVisibility(0);
            if (l != null) {
                b.setOnClickListener(l);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setupPositiveButton$411327c6(String buttonText, View.OnClickListener l) {
        Button b = (Button) this.view.findViewById(R.id.startup_button_positive);
        if (b != null) {
            b.setEnabled(true);
            b.setText(buttonText);
            b.setVisibility(0);
            if (l != null) {
                b.setOnClickListener(l);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void enableUsageOption() {
        StartupSequenceInfo startupSequenceInfo = this.mStartupSequenceInfo;
        StartupSequenceInfo.log.d("acceptOptIn");
        ConnectLegal.from(startupSequenceInfo.mContext).acceptOptIn();
        if ((this.mFlags & 4) == 4) {
            this.mFlags &= -5;
            return;
        }
        if (this.mStartupSequenceInfo.isTosAccepted() && this.mStartupSequenceInfo.isOptInAccepted()) {
            Connect.from(getActivity()).getLivingLanguage(null).enable();
        }
        UsageManager usageMgr = UsageManager.from(getActivity());
        if (usageMgr != null) {
            InputMethods.Language lang = InputMethods.from(getActivity()).getCurrentInputLanguage();
            Locale currentLocale = getActivity().getResources().getConfiguration().locale;
            String localeSetting = "Initial Locale:" + currentLocale.toString() + "|Lang:" + lang.mEnglishName + "|KeyboardLayoutId:" + lang.getCurrentInputMode().getCurrentLayout().mLayoutId;
            usageMgr.getKeyboardUsageScribe().recordInitialLocaleSetting(localeSetting);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static StartupDelegate createDelegate(StartupSequenceInfo.ScreenInfo desScreen, int themeId, int flags, Bundle data) {
        Bundle args = new Bundle();
        args.putBundle("param_data", data);
        args.putInt("param_flags", flags);
        args.putBoolean("param_next_screen_is_null", desScreen.nextScreenInfo == null);
        args.putBoolean("param_show_registration_warning", desScreen.showWarning);
        args.putInt("param_theme_id", themeId);
        return createDelegate(desScreen.screenName, args);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static StartupDelegate createDelegate(String screenName, int themeId, int flags) {
        Bundle args = new Bundle();
        args.putInt("param_flags", flags);
        args.putInt("param_theme_id", themeId);
        args.putBundle("param_data", null);
        args.putBoolean("param_next_screen_is_null", true);
        args.putBoolean("param_show_registration_warning", false);
        return createDelegate(screenName, args);
    }

    private static StartupDelegate createDelegate(String screenName, Bundle args) {
        if (AccountRegisterDelegate.class.getSimpleName().equals(screenName)) {
            StartupDelegate startupDelegate = AccountRegisterDelegate.newInstance(args);
            return startupDelegate;
        }
        if (BackupAndSyncDelegate.class.getSimpleName().equals(screenName)) {
            StartupDelegate startupDelegate2 = BackupAndSyncDelegate.newInstance(args);
            return startupDelegate2;
        }
        if (ChooseLanguageDelegate.class.getSimpleName().equals(screenName)) {
            StartupDelegate startupDelegate3 = ChooseLanguageDelegate.newInstance(args);
            return startupDelegate3;
        }
        if (ConnectTOSDelegate.class.getSimpleName().equals(screenName)) {
            StartupDelegate startupDelegate4 = ConnectTOSDelegate.newInstance(args);
            return startupDelegate4;
        }
        if (ContributeUsageDataDelegate.class.getSimpleName().equals(screenName)) {
            StartupDelegate startupDelegate5 = ContributeUsageDataDelegate.newInstance(args);
            return startupDelegate5;
        }
        if (DownloadLanguageDelegate.class.getSimpleName().equals(screenName)) {
            StartupDelegate startupDelegate6 = DownloadLanguageDelegate.newInstance(args);
            return startupDelegate6;
        }
        if (EulaGooglePlayDelegate.class.getSimpleName().equals(screenName)) {
            StartupDelegate startupDelegate7 = EulaGooglePlayDelegate.newInstance(args);
            return startupDelegate7;
        }
        if (EnableSwypeDelegate.class.getSimpleName().equals(screenName)) {
            StartupDelegate startupDelegate8 = EnableSwypeDelegate.newInstance(args);
            return startupDelegate8;
        }
        if (GettingStartedDelegate.class.getSimpleName().equals(screenName)) {
            StartupDelegate startupDelegate9 = GettingStartedDelegate.newInstance(args);
            return startupDelegate9;
        }
        if (LegalDocsSplashDelegate.class.getSimpleName().equals(screenName)) {
            StartupDelegate startupDelegate10 = LegalDocsSplashDelegate.newInstance(args);
            return startupDelegate10;
        }
        if (LegalDocsSplashOemDelegate.class.getSimpleName().equals(screenName)) {
            StartupDelegate startupDelegate11 = LegalDocsSplashOemDelegate.newInstance(args);
            return startupDelegate11;
        }
        if (StartupConnectTOSDelegate.class.getSimpleName().equals(screenName)) {
            StartupDelegate startupDelegate12 = StartupConnectTOSDelegate.newInstance(args);
            return startupDelegate12;
        }
        if (UsageOptInDelegate.class.getSimpleName().equals(screenName)) {
            StartupDelegate startupDelegate13 = UsageOptInDelegate.newInstance(args);
            return startupDelegate13;
        }
        if (SelectSwypeDelegate.class.getSimpleName().equals(screenName)) {
            StartupDelegate startupDelegate14 = SelectSwypeDelegate.newInstance(args);
            return startupDelegate14;
        }
        log.e("unknown start-up delegate: " + screenName);
        return null;
    }
}
