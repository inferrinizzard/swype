package com.nuance.swype.startup;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputFieldInfo;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.util.LocalizationUtils;
import com.nuance.swype.util.LogManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public final class StartupSequenceInfo {
    public static final LogManager.Log log = LogManager.getLog("StartupSequenceInfo");
    final Context mContext;
    private final ArrayList<ScreenInfo> mScreenOrder;
    final HashMap<String, ScreenInfo> mScreenOrderSetting;
    boolean mShowTos = false;
    private boolean mDebugShouldShowStartupActive = false;

    /* loaded from: classes.dex */
    public enum SwypeIMEState {
        DISABLED,
        ENABLED,
        SELECTED
    }

    /* loaded from: classes.dex */
    public static class ScreenInfo {
        public ScreenInfo nextScreenInfo;
        public boolean omitIfRussia;
        public String screenName;
        public boolean showWarning;

        public ScreenInfo() {
        }

        public ScreenInfo(String screenName, boolean showWarning, boolean omitIfRussia) {
            this.screenName = screenName;
            this.nextScreenInfo = null;
            this.showWarning = showWarning;
            this.omitIfRussia = omitIfRussia;
        }
    }

    public StartupSequenceInfo(Context context) {
        int startupSequenceInfoResId = R.xml.startup_sequence_info;
        this.mContext = context;
        this.mScreenOrder = new ArrayList<>();
        this.mScreenOrderSetting = new HashMap<>();
        int internalStatus = -1;
        try {
            XmlResourceParser xmlparser = this.mContext.getResources().getXml(startupSequenceInfoResId);
            ScreenInfo previousScreen = null;
            ScreenInfo currentScreen = null;
            while (true) {
                try {
                    int event = xmlparser.next();
                    if (event == 1) {
                        break;
                    }
                    String tag = xmlparser.getName();
                    if (event == 2) {
                        if ("startup".equals(tag)) {
                            log.d("start reading first_time");
                            internalStatus = 0;
                        } else if ("setting".equals(tag)) {
                            log.d("start reading setting");
                            internalStatus = 2;
                        } else if ("screen".equals(tag)) {
                            log.d("start reading screen");
                            String screenName = xmlparser.getAttributeValue(null, "name");
                            boolean showWarning = xmlparser.getAttributeBooleanValue(null, "showWarning", false);
                            boolean omitIfRussia = xmlparser.getAttributeBooleanValue(null, "omitIfRussia", false);
                            log.d("raw - name: ", screenName, " - Warn: ", Boolean.valueOf(showWarning), " - Russia ", Boolean.valueOf(omitIfRussia));
                            if (!TextUtils.isEmpty(screenName)) {
                                currentScreen = new ScreenInfo(screenName, showWarning, omitIfRussia);
                            }
                        } else {
                            log.d("unknown tag: ", tag);
                        }
                    } else if (event == 3) {
                        if ("startup".equals(tag)) {
                            previousScreen = null;
                        } else if ("screen".equals(tag)) {
                            log.d("stop reading screen");
                            if (previousScreen != null && internalStatus != 2) {
                                previousScreen.nextScreenInfo = currentScreen;
                            }
                            previousScreen = currentScreen;
                            if (internalStatus == 0) {
                                this.mScreenOrder.add(currentScreen);
                            } else if (internalStatus == 2) {
                                this.mScreenOrderSetting.put(currentScreen.screenName, currentScreen);
                            }
                        } else {
                            log.d("unknown tag: ", tag);
                        }
                    }
                } catch (Throwable th) {
                    if (xmlparser != null) {
                        xmlparser.close();
                        log.d("stop reading file");
                    }
                    throw th;
                }
            }
            if (xmlparser != null) {
                xmlparser.close();
                log.d("stop reading file");
            }
        } catch (IOException ex) {
            log.d("error: ", ex.getMessage());
        } catch (XmlPullParserException ex2) {
            log.d("error: ", ex2.getMessage());
        }
    }

    public final ScreenInfo getFirstStartupScreenInfo() {
        if (this.mScreenOrder != null && this.mScreenOrder.size() > 0) {
            ScreenInfo screenInfo = this.mScreenOrder.get(0);
            return screenInfo;
        }
        log.e("getFirstStartupScreenInfo: screenOrderFirstTime not available!");
        return null;
    }

    private boolean setSettingScreenInfo(String key, ScreenInfo screenInfo) {
        if (screenInfo == null) {
            return false;
        }
        if (this.mScreenOrderSetting.containsKey(key)) {
            this.mScreenOrderSetting.remove(key);
        }
        this.mScreenOrderSetting.put(key, screenInfo);
        return true;
    }

    public final boolean removeSettingScreenInfo(String key) {
        if (this.mScreenOrderSetting == null) {
            return false;
        }
        this.mScreenOrderSetting.remove(key);
        return true;
    }

    public final boolean shouldShowStartup(EditorInfo editorInfo, InputFieldInfo inputFieldInfo) {
        boolean shouldShow = false;
        LogManager.Log log2 = log;
        Object[] objArr = new Object[1];
        objArr[0] = "shouldShowStartup: packageName: " + this.mContext.getPackageName() + ", currentEditorInput: " + (editorInfo == null ? "null" : editorInfo.packageName);
        log2.d(objArr);
        LogManager.Log log3 = log;
        Object[] objArr2 = new Object[1];
        objArr2[0] = "shouldShowStartup: isPasswordField: " + (inputFieldInfo == null ? "null" : Boolean.valueOf(inputFieldInfo.isPasswordField()));
        log3.d(objArr2);
        if (!isInputFieldStartupOrPassword(editorInfo, inputFieldInfo) && IMEApplication.from(this.mContext).isUserUnlockFinished()) {
            if (editorInfo != null && this.mContext.getPackageName().equalsIgnoreCase(editorInfo.packageName)) {
                log.d("shouldShowStartup: started by ourselves, will not show startup");
                return false;
            }
            if (inputFieldInfo != null && inputFieldInfo.isPasswordField()) {
                log.d("shouldShowStartup: in password field, will not show startup");
                return false;
            }
            ScreenInfo screenInfo = getFirstStartupScreenInfo();
            if (this.mDebugShouldShowStartupActive) {
                log.e("shouldShowStartup: circular loop detected");
            }
            this.mDebugShouldShowStartupActive = true;
            while (screenInfo != null && !shouldShow) {
                shouldShow = shouldShowDelegate(screenInfo);
                if (!shouldShow) {
                    screenInfo = screenInfo.nextScreenInfo;
                }
            }
            this.mDebugShouldShowStartupActive = false;
            return shouldShow;
        }
        return false;
    }

    public static SwypeIMEState getSwypeIMEState(Context context) {
        List<InputMethodInfo> inputMethods = ((InputMethodManager) context.getApplicationContext().getSystemService("input_method")).getEnabledInputMethodList();
        String selectedInputMethods = Settings.Secure.getString(context.getContentResolver(), "default_input_method");
        for (int i = 0; i < inputMethods.size(); i++) {
            if (inputMethods.get(i).getPackageName().equals(context.getPackageName())) {
                if (inputMethods.get(i).getId().equals(selectedInputMethods)) {
                    return SwypeIMEState.SELECTED;
                }
                return SwypeIMEState.ENABLED;
            }
        }
        if (selectedInputMethods != null && selectedInputMethods.startsWith(context.getPackageName() + "/")) {
            return SwypeIMEState.SELECTED;
        }
        return SwypeIMEState.DISABLED;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean shouldShowDelegate(ScreenInfo screen) {
        if (!IMEApplication.from(this.mContext).isUserUnlockFinished()) {
            return false;
        }
        boolean shouldShow = false;
        if (AccountRegisterDelegate.class.getSimpleName().equals(screen.screenName)) {
            shouldShow = true;
        } else if (BackupAndSyncDelegate.class.getSimpleName().equals(screen.screenName)) {
            AppPreferences appPrefs = AppPreferences.from(this.mContext);
            shouldShow = AppPreferences.from(this.mContext).getBoolean("startup_show_backup_sync", true) && appPrefs.showStartupRegistration() && appPrefs.isBackupAndSyncSupported() && Connect.from(this.mContext).getAccounts().getActiveAccount() == null && !(isLocationRussia() && screen.omitIfRussia);
        } else if (ChooseLanguageDelegate.class.getSimpleName().equals(screen.screenName)) {
            shouldShow = !getLanguageSelectedAndInstalled() && "".equals(getStartupSequenceDownloadLanguageID());
        } else if (ConnectTOSDelegate.class.getSimpleName().equals(screen.screenName)) {
            if (!isTosAccepted() && (!isLocationRussia() || !screen.omitIfRussia)) {
                shouldShow = true;
            }
        } else if (ContributeUsageDataDelegate.class.getSimpleName().equals(screen.screenName)) {
            shouldShow = (AppPreferences.from(this.mContext).getBoolean("startup_show_cud", true) || (wasOptInAccepted() && isOptInChanged())) && isTosAccepted() && !isOptInAccepted() && !(isLocationRussia() && screen.omitIfRussia);
        } else if (DownloadLanguageDelegate.class.getSimpleName().equals(screen.screenName)) {
            if (getLanguageSelectedAndInstalled()) {
                shouldShow = false;
            } else {
                String languageIDString = getStartupSequenceDownloadLanguageID();
                if (!"".equals(languageIDString)) {
                    int languageId = Integer.parseInt(languageIDString);
                    if (Connect.from(this.mContext).getDownloadManager().getSettingDownloadLanguageList(false).get(Integer.valueOf(languageId)) != null) {
                        shouldShow = true;
                    } else {
                        log.e("shouldShowDelegate: could not get DownloadableLanguage for " + languageIDString);
                        AppPreferences.from(this.mContext).setStartupSequenceDownloadLanguageID("");
                    }
                }
            }
        } else if (EulaGooglePlayDelegate.class.getSimpleName().equals(screen.screenName)) {
            shouldShow = !isEulaAccepted();
        } else if (EnableSwypeDelegate.class.getSimpleName().equals(screen.screenName)) {
            shouldShow = !isSwypeEnabled();
        } else if (GettingStartedDelegate.class.getSimpleName().equals(screen.screenName)) {
            shouldShow = AppPreferences.from(this.mContext).getBoolean("startup_show_getting_started", true) && AppPreferences.from(this.mContext).showSwypeWelcomeStartupScreens();
        } else if (LegalDocsSplashDelegate.class.getSimpleName().equals(screen.screenName)) {
            shouldShow = (isEulaAccepted() && isTosAccepted() && !getShowSplash()) ? false : true;
        } else if (LegalDocsSplashOemDelegate.class.getSimpleName().equals(screen.screenName)) {
            shouldShow = (!isTosAccepted() && wasTosAccepted()) || getShowSplash();
        } else if (SelectSwypeDelegate.class.getSimpleName().equals(screen.screenName)) {
            shouldShow = !isSwypeSelected();
        } else if (StartupConnectTOSDelegate.class.getSimpleName().equals(screen.screenName)) {
            if (this.mShowTos && !isTosAccepted() && (!isLocationRussia() || !screen.omitIfRussia)) {
                shouldShow = true;
            }
        } else if (UsageOptInDelegate.class.getSimpleName().equals(screen.screenName)) {
            shouldShow = (isOptInAccepted() || (isLocationRussia() && screen.omitIfRussia)) ? false : true;
        } else {
            log.e("unknown start-up delegate: " + screen.screenName);
        }
        log.d("shouldShowDelegate: screen: " + screen.screenName + ", shouldShow: " + shouldShow);
        return shouldShow;
    }

    public static Intent getLegalActivitiesStartIntent(Context context, boolean tosRequired, boolean optInRequired, Bundle resultData) {
        boolean showTOS = !Connect.from(context).getLegal().isTosAccepted();
        boolean showOptIn = !Connect.from(context).getLegal().isOptInAccepted();
        if (showTOS || showOptIn) {
            StartupSequenceInfo startupSequenceInfo = IMEApplication.from(context).getStartupSequenceInfo();
            ScreenInfo tempScreenInfo = null;
            if (showTOS && tosRequired) {
                tempScreenInfo = new ScreenInfo();
                tempScreenInfo.screenName = ConnectTOSDelegate.class.getSimpleName();
            }
            if (showOptIn && optInRequired) {
                if (tempScreenInfo == null) {
                    tempScreenInfo = new ScreenInfo();
                }
                if (tempScreenInfo.screenName == null || tempScreenInfo.screenName.isEmpty()) {
                    tempScreenInfo.screenName = UsageOptInDelegate.class.getSimpleName();
                } else {
                    ScreenInfo nextScreenInfo = new ScreenInfo();
                    nextScreenInfo.screenName = UsageOptInDelegate.class.getSimpleName();
                    tempScreenInfo.nextScreenInfo = nextScreenInfo;
                }
            }
            if (tempScreenInfo != null) {
                String key = "LegalRequirements" + tempScreenInfo.screenName;
                startupSequenceInfo.setSettingScreenInfo(key, tempScreenInfo);
                Intent intent = new Intent(context, (Class<?>) StartupActivity.class);
                intent.putExtra("launch_mode", "standalone");
                intent.putExtra("launch_screen", key);
                intent.putExtra("start_flags", 30);
                intent.putExtra("result_data", resultData);
                return intent;
            }
        }
        return null;
    }

    public static Intent getLegalCUDActivitiesStartIntent(Context context, boolean optInRequired, Bundle resultData) {
        StartupSequenceInfo startupSequenceInfo = IMEApplication.from(context).getStartupSequenceInfo();
        ScreenInfo tempScreenInfo = null;
        if (optInRequired) {
            tempScreenInfo = new ScreenInfo();
            if (tempScreenInfo.screenName == null || tempScreenInfo.screenName.isEmpty()) {
                tempScreenInfo.screenName = UsageOptInDelegate.class.getSimpleName();
            } else {
                ScreenInfo nextScreenInfo = new ScreenInfo();
                nextScreenInfo.screenName = UsageOptInDelegate.class.getSimpleName();
                tempScreenInfo.nextScreenInfo = nextScreenInfo;
            }
        }
        if (tempScreenInfo != null) {
            String key = "LegalRequirements" + tempScreenInfo.screenName;
            startupSequenceInfo.setSettingScreenInfo(key, tempScreenInfo);
            Intent intent = new Intent(context, (Class<?>) StartupActivity.class);
            intent.putExtra("launch_mode", "standalone");
            intent.putExtra("launch_screen", key);
            intent.putExtra("start_flags", 30);
            intent.putExtra("result_data", resultData);
            return intent;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void acceptTos() {
        log.d("acceptTos");
        ConnectLegal.from(this.mContext).acceptTos();
    }

    private boolean getShowSplash() {
        return AppPreferences.from(this.mContext).getBoolean("startup_show_splash", true);
    }

    private boolean getLanguageSelectedAndInstalled() {
        return AppPreferences.from(this.mContext).getBoolean("startup_language_installed", false);
    }

    private String getStartupSequenceDownloadLanguageID() {
        return AppPreferences.from(this.mContext).getStartupSequenceDownloadLanguageID();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isEulaAccepted() {
        return ConnectLegal.from(this.mContext).isEulaAccepted();
    }

    private boolean isLocationRussia() {
        int[] russiaMcc = {250};
        return LocalizationUtils.isUsersLocation(this.mContext, "RU", russiaMcc);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isOptInAccepted() {
        return ConnectLegal.from(this.mContext).isOptInAccepted();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isOptInChanged() {
        return ConnectLegal.from(this.mContext).isOptInChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isSwypeEnabled() {
        return getSwypeIMEState(this.mContext) != SwypeIMEState.DISABLED;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isSwypeSelected() {
        return getSwypeIMEState(this.mContext) == SwypeIMEState.SELECTED;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isTosAccepted() {
        return ConnectLegal.from(this.mContext).isTosAccepted();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isTosChanged() {
        return ConnectLegal.from(this.mContext).isTosChanged();
    }

    public final void setHotWordsStatus$1385ff() {
        Connect.from(this.mContext).getLivingLanguage(null).enable();
    }

    public final void setLanguageSelectedAndInstalled$1385ff() {
        AppPreferences.from(this.mContext).setStartupSequenceDownloadLanguageID("");
        AppPreferences.from(this.mContext).setBoolean("startup_language_installed", true);
    }

    public final void setShowBackupSync(boolean show) {
        AppPreferences.from(this.mContext).setBoolean("startup_show_backup_sync", show);
    }

    public final void setShowCud(boolean show) {
        AppPreferences.from(this.mContext).setBoolean("startup_show_cud", show);
    }

    public final void setShowGettingStarted(boolean show) {
        AppPreferences.from(this.mContext).setBoolean("startup_show_getting_started", show);
    }

    public final void setShowSplash(boolean show) {
        AppPreferences.from(this.mContext).setBoolean("startup_show_splash", show);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean shouldShowNetworkAgreementDialog() {
        return this.mContext.getResources().getBoolean(R.bool.enable_china_connect_special) && UserPreferences.from(this.mContext).shouldShowNetworkAgreementDialog();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean wasOptInAccepted() {
        return ConnectLegal.from(this.mContext).wasOptInAccepted();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean wasTosAccepted() {
        return ConnectLegal.from(this.mContext).wasTosAccepted();
    }

    public final boolean isInputFieldStartupOrPassword(EditorInfo editorInfo, InputFieldInfo inputFieldInfo) {
        if (editorInfo == null || !this.mContext.getPackageName().equalsIgnoreCase(editorInfo.packageName)) {
            return inputFieldInfo != null && inputFieldInfo.isPasswordField();
        }
        return true;
    }
}
