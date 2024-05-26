package com.nuance.swype.startup;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.startup.StartupDelegate;
import com.nuance.swype.startup.StartupSequenceInfo;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class StartupActivity extends Activity implements StartupDelegate.StartupActivityCallbacks {
    private StartupSequenceInfo.ScreenInfo currentScreenInfo;
    private int flags;
    private boolean launchToSettings;
    private StartupDelegate.StartupListener mStartupListener;
    private ArrayList<String> pendingRemoveScreenInfos;
    private int themeID;
    private static final LogManager.Log log = LogManager.getLog("StartupActivity");
    public static final String ACCOUNT_REGISTER = AccountRegisterDelegate.class.getSimpleName();
    private String launchMode = "standalone";
    private boolean showKeyboardOnFinish = false;

    @Override // android.app.Activity
    public void onBackPressed() {
        StartupDelegate delegate = getCurrentDelegate();
        if (delegate != null) {
            delegate.onBackPressed();
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        StartupDelegate delegate = getCurrentDelegate();
        if (delegate != null) {
            delegate.onWindowFocusChanged(hasFocus);
        }
    }

    @Override // com.nuance.swype.startup.StartupDelegate.StartupActivityCallbacks
    public final void showDelegate(StartupDelegate delegate) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        StartupDelegate currentDelegate = getCurrentDelegate();
        if (currentDelegate != null) {
            log.d("showDelegate: remove " + currentDelegate);
            ft.remove(currentDelegate);
        }
        if (delegate != null) {
            log.d("showDelegate: show " + delegate);
            ft.add(R.id.startup_fragment_swap, delegate, "startup_delegate");
            ft.commit();
        } else {
            log.d("showDelegate: finishSequence");
            finishSequence(this.launchToSettings ? false : this.showKeyboardOnFinish);
        }
    }

    @Override // com.nuance.swype.startup.StartupDelegate.StartupActivityCallbacks
    public final void showKeyboardOnFinish$1385ff() {
        this.showKeyboardOnFinish = true;
    }

    @Override // com.nuance.swype.startup.StartupDelegate.StartupActivityCallbacks
    public final StartupDelegate getCurrentDelegate() {
        Fragment prev = getFragmentManager().findFragmentByTag("startup_delegate");
        if (prev != null) {
            return (StartupDelegate) prev;
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x007e  */
    /* JADX WARN: Removed duplicated region for block: B:6:0x001b  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0025  */
    @Override // com.nuance.swype.startup.StartupDelegate.StartupActivityCallbacks
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void startNextScreen(int r9, android.os.Bundle r10) {
        /*
            r8 = this;
            r7 = 1
            r6 = 0
            com.nuance.swype.input.IMEApplication r2 = com.nuance.swype.input.IMEApplication.from(r8)
            com.nuance.swype.startup.StartupSequenceInfo r1 = r2.getStartupSequenceInfo()
            com.nuance.swype.startup.StartupSequenceInfo$ScreenInfo r2 = r8.currentScreenInfo
            if (r2 != 0) goto L29
            com.nuance.swype.util.LogManager$Log r2 = com.nuance.swype.startup.StartupActivity.log
            java.lang.String r3 = "startNextScreen: currentScreenInfo unexpectedly null"
            r2.e(r3)
        L16:
            r0 = 0
            com.nuance.swype.startup.StartupSequenceInfo$ScreenInfo r2 = r8.currentScreenInfo
            if (r2 == 0) goto L23
            com.nuance.swype.startup.StartupSequenceInfo$ScreenInfo r2 = r8.currentScreenInfo
            int r3 = r8.themeID
            com.nuance.swype.startup.StartupDelegate r0 = com.nuance.swype.startup.StartupDelegate.createDelegate(r2, r3, r9, r10)
        L23:
            if (r0 == 0) goto L7e
            r8.showDelegate(r0)
        L28:
            return
        L29:
            com.nuance.swype.util.LogManager$Log r2 = com.nuance.swype.startup.StartupActivity.log
            java.lang.Object[] r3 = new java.lang.Object[r7]
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            java.lang.String r5 = "startNextScreen: currentScreenInfo: "
            r4.<init>(r5)
            com.nuance.swype.startup.StartupSequenceInfo$ScreenInfo r5 = r8.currentScreenInfo
            java.lang.String r5 = r5.screenName
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3[r6] = r4
            r2.d(r3)
            com.nuance.swype.startup.StartupSequenceInfo$ScreenInfo r2 = r8.currentScreenInfo
            com.nuance.swype.startup.StartupSequenceInfo$ScreenInfo r2 = r2.nextScreenInfo
            r8.currentScreenInfo = r2
            com.nuance.swype.util.LogManager$Log r3 = com.nuance.swype.startup.StartupActivity.log
            java.lang.Object[] r4 = new java.lang.Object[r7]
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            java.lang.String r2 = "startNextScreen: nextScreenInfo: "
            r5.<init>(r2)
            com.nuance.swype.startup.StartupSequenceInfo$ScreenInfo r2 = r8.currentScreenInfo
            if (r2 != 0) goto L79
            java.lang.String r2 = "null"
        L5f:
            java.lang.StringBuilder r2 = r5.append(r2)
            java.lang.String r2 = r2.toString()
            r4[r6] = r2
            r3.d(r4)
            com.nuance.swype.startup.StartupSequenceInfo$ScreenInfo r2 = r8.currentScreenInfo
            if (r2 == 0) goto L16
            com.nuance.swype.startup.StartupSequenceInfo$ScreenInfo r2 = r8.currentScreenInfo
            boolean r2 = r1.shouldShowDelegate(r2)
            if (r2 == 0) goto L29
            goto L16
        L79:
            com.nuance.swype.startup.StartupSequenceInfo$ScreenInfo r2 = r8.currentScreenInfo
            java.lang.String r2 = r2.screenName
            goto L5f
        L7e:
            r2 = 0
            r8.showDelegate(r2)
            goto L28
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.startup.StartupActivity.startNextScreen(int, android.os.Bundle):void");
    }

    @Override // com.nuance.swype.startup.StartupDelegate.StartupActivityCallbacks
    public final void restartSequence(int flags, Bundle resultData) {
        StartupSequenceInfo ssInfo = IMEApplication.from(this).getStartupSequenceInfo();
        this.currentScreenInfo = ssInfo.getFirstStartupScreenInfo();
        if (ssInfo.shouldShowDelegate(this.currentScreenInfo)) {
            StartupDelegate startupDelegate = StartupDelegate.createDelegate(this.currentScreenInfo, this.themeID, flags, resultData);
            showDelegate(startupDelegate);
        } else {
            startNextScreen(flags, resultData);
        }
    }

    @Override // com.nuance.swype.startup.StartupDelegate.StartupActivityCallbacks
    public final void cancelSequence() {
        StartupSequenceInfo startupSequenceInfo = IMEApplication.from(this).getStartupSequenceInfo();
        Iterator<String> it = this.pendingRemoveScreenInfos.iterator();
        while (it.hasNext()) {
            String pendingRemoveScreenInfo = it.next();
            startupSequenceInfo.removeSettingScreenInfo(pendingRemoveScreenInfo);
        }
        this.pendingRemoveScreenInfos.clear();
        this.flags &= -17;
        finish();
    }

    @Override // com.nuance.swype.startup.StartupDelegate.StartupActivityCallbacks
    public final void finishSequence(boolean showKeyboard) {
        StartupSequenceInfo startupSequenceInfo = IMEApplication.from(this).getStartupSequenceInfo();
        Iterator<String> it = this.pendingRemoveScreenInfos.iterator();
        while (it.hasNext()) {
            String pendingRemoveScreenInfo = it.next();
            startupSequenceInfo.removeSettingScreenInfo(pendingRemoveScreenInfo);
        }
        this.pendingRemoveScreenInfos.clear();
        this.flags &= -17;
        if (showKeyboard) {
            IME ime = IMEApplication.from(getApplicationContext()).getIME();
            if (ime != null) {
                ime.show();
            }
        } else if (this.launchToSettings) {
            IMEApplication.from(getApplicationContext()).showMainSettings();
        }
        finish();
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        StartupDelegate currentDelegate = getCurrentDelegate();
        if (currentDelegate != null) {
            currentDelegate.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        log.d("onDestroy: " + this);
        StartupDelegate currentDelegate = getCurrentDelegate();
        if (currentDelegate != null) {
            currentDelegate.onDestroy();
        }
        super.onDestroy();
    }

    @Override // com.nuance.swype.startup.StartupDelegate.StartupActivityCallbacks
    public final void registerListener(StartupDelegate.StartupListener listener) {
        this.mStartupListener = listener;
    }

    @Override // com.nuance.swype.startup.StartupDelegate.StartupActivityCallbacks
    public final void unregisterListener() {
        this.mStartupListener = null;
    }

    @Override // com.nuance.swype.startup.StartupDelegate.StartupActivityCallbacks
    public final void notifyStartupListener(StartupDelegate.StartupListener.LISTENER_KEY listenerKey, Bundle resultData) {
        switch (listenerKey) {
            case SKIP_LISTENER_KEY:
                if (this.mStartupListener == null) {
                    log.e("notifyStartupListener: no listener to notify!");
                    return;
                } else {
                    this.mStartupListener.onSkipListener();
                    return;
                }
            case CANCEL_LISTENER_KEY:
                if (this.mStartupListener == null) {
                    log.e("notifyStartupListener: no listener to notify!");
                    return;
                } else {
                    this.mStartupListener.onCancelListener();
                    return;
                }
            case ACCEPT_LISTENER_KEY:
                if (this.mStartupListener == null) {
                    log.e("notifyStartupListener: no listener to notify!");
                    return;
                } else {
                    this.mStartupListener.onAcceptListener(this);
                    return;
                }
            case ACTIVITY_RESULT_OK_LISTENER_KEY:
                Intent resultIntent = new Intent();
                resultIntent.putExtra("result_data", resultData);
                setResult(-1, resultIntent);
                finishSequence(this.launchToSettings ? false : this.showKeyboardOnFinish);
                return;
            case ACTIVITY_RESULT_CANCEL_LISTENER_KEY:
                setResult(0);
                cancelSequence();
                return;
            default:
                log.d("unknown listener key: ", listenerKey);
                return;
        }
    }

    @Override // com.nuance.swype.startup.StartupDelegate.StartupActivityCallbacks
    public final StartupSequenceInfo getStartupSequenceInfo() {
        return IMEApplication.from(this).getStartupSequenceInfo();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        if (!IMEApplication.from(this).isScreenLayoutTablet()) {
            this.themeID = R.style.AppStartupTheme;
            setRequestedOrientation(1);
        } else {
            this.themeID = R.style.AppStartupTheme_FloatingActivity;
        }
        super.setTheme(this.themeID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup);
        this.pendingRemoveScreenInfos = new ArrayList<>();
        this.launchToSettings = getIntent().getBooleanExtra("launch_to_settings", false);
        String mode = getIntent().getStringExtra("launch_mode");
        if (mode != null) {
            this.launchMode = mode;
        }
        String launchScreen = getIntent().getStringExtra("launch_screen");
        if (launchScreen == null) {
            launchScreen = "";
        }
        this.flags = getIntent().getIntExtra("start_flags", 0);
        Bundle resultData = getIntent().getBundleExtra("result_data");
        StartupSequenceInfo startupSequenceInfo = IMEApplication.from(this).getStartupSequenceInfo();
        if (this.launchMode.equals("startup_sequence")) {
            this.currentScreenInfo = startupSequenceInfo.getFirstStartupScreenInfo();
        } else {
            this.currentScreenInfo = startupSequenceInfo.mScreenOrderSetting.get(launchScreen);
            if ((this.flags & 16) == 16 && !this.pendingRemoveScreenInfos.contains(launchScreen)) {
                this.pendingRemoveScreenInfos.add(launchScreen);
            }
        }
        log.d("onCreate: launchToSettings: " + this.launchToSettings + ", launchMode = " + this.launchMode + ", flags = " + this.flags + ", launchScreen = " + launchScreen);
        if (savedInstanceState == null) {
            if (this.currentScreenInfo != null) {
                if (startupSequenceInfo.shouldShowDelegate(this.currentScreenInfo)) {
                    log.d("onCreate: delegate not found, creating new");
                    StartupDelegate startupDelegate = StartupDelegate.createDelegate(this.currentScreenInfo, this.themeID, this.flags, resultData);
                    showDelegate(startupDelegate);
                    return;
                }
                startNextScreen(this.flags, resultData);
                return;
            }
            finishSequence(this.launchToSettings ? false : this.showKeyboardOnFinish);
        }
    }
}
