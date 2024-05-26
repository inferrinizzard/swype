package com.nuance.speech.dragon;

import android.content.Context;
import android.os.Handler;
import com.nuance.nmdp.speechkit.CustomWordsSynchronizer;
import com.nuance.nmdp.speechkit.Recognizer;
import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.TextContext;
import com.nuance.speech.CustomWordSynchronizer;
import com.nuance.speech.SpeechConfig;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.udb.UserDictionaryIterator;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public final class SpeechKitWrapper {
    private static final LogManager.Log log = LogManager.getLog("SpeechKitWrapper");
    private final Context mContext;
    private String mCurrentLanguageName;
    private DragonCustomWordSynchronizer mCustomWordSyncWrapper;
    private final SpeechConfig mSpeechConfig;
    private SpeechKit mSpeechKitInstance = null;

    public SpeechKitWrapper(Context context, String languageName) {
        this.mContext = context;
        this.mSpeechConfig = IMEApplication.from(context).getSpeechConfig();
        createSpeechKitInstance(languageName);
    }

    public final synchronized void release() {
        releaseSpeechKitInstance();
        this.mSpeechKitInstance = null;
    }

    public final Recognizer createTextContextRecognizer(String recognizerType, int endOfSpeechDetectionMode, TextContext textContext, Recognizer.Listener listener, Handler callbackHandler) {
        log.d("#createTextContextRecognizer: langCode: ", getCurrentLanguageCode());
        log.d("#createTextContextRecognizer: server: ", getCurrentLanguageServer());
        return getSpeechKitInstance().createTextContextRecognizer(recognizerType, endOfSpeechDetectionMode, getSpeechResultsMode(), getCurrentLanguageCode(), textContext, listener, callbackHandler);
    }

    public final CustomWordsSynchronizer createCustomWordsSynchronizer(String dictationType, CustomWordsSynchronizer.Listener listener, Handler callbackHandler) {
        return getSpeechKitInstance().createCustomWordsSynchronizer(dictationType, getCurrentLanguageCode(), listener, callbackHandler);
    }

    public final String getCurrentLanguageName() {
        return this.mCurrentLanguageName;
    }

    public final String getCurrentLanguageCode() {
        return this.mSpeechConfig.getLanguageCode(getCurrentLanguageName());
    }

    public final String getCurrentLanguageServer() {
        return this.mSpeechConfig.getLanguageServerName(getCurrentLanguageName());
    }

    public final boolean isCustomWordsSynchronizationSupported() {
        return this.mSpeechConfig.isCustomWordsSynchronizationSupported(getCurrentLanguageName());
    }

    public final SpeechKit.PartialResultsMode getSpeechResultsMode() {
        return this.mSpeechConfig.getResponseMode(getCurrentLanguageName());
    }

    public final SpeechKit.CmdSetType getSpeechCmd() {
        return this.mSpeechConfig.getSpeechCmd(getCurrentLanguageName());
    }

    public final Context getContext() {
        return this.mContext;
    }

    private SpeechKit getSpeechKitInstance() {
        createSpeechKitInstance(this.mCurrentLanguageName);
        return this.mSpeechKitInstance;
    }

    private void createSpeechKitInstance(String languageName) {
        if (this.mSpeechKitInstance != null && !languageName.equals(this.mCurrentLanguageName)) {
            releaseSpeechKitInstance();
        }
        if (this.mSpeechKitInstance == null) {
            this.mCurrentLanguageName = languageName;
            String appName = this.mSpeechConfig.getAppName();
            String server = this.mSpeechConfig.getLanguageServerName(getCurrentLanguageName());
            short portId = this.mSpeechConfig.getPortId(getCurrentLanguageName());
            byte[] appKey = this.mSpeechConfig.getAppKey(this.mContext);
            if (appKey == null) {
                throw new IllegalStateException("App keys is missing in speech config (comes from swype core lib)");
            }
            log.d("appName: ", appName);
            log.d("server: ", server);
            log.d("portId: ", Short.valueOf(portId));
            StringBuilder hexString = new StringBuilder();
            for (byte b : appKey) {
                int v = b & 255;
                String hex = Integer.toHexString(v);
                if (v < 16) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            log.d("AppKey ", Integer.valueOf(appKey.length), " bytes");
            log.d("AppKey: ", hexString.toString());
            this.mSpeechKitInstance = SpeechKit.initialize(this.mContext, IMEApplication.from(this.mContext).getBuildInfo().getBuildVersion(), appName, server, portId, false, appKey, getSpeechCmd());
            this.mSpeechKitInstance.connect();
        }
    }

    private void releaseSpeechKitInstance() {
        if (this.mCustomWordSyncWrapper != null) {
            this.mCustomWordSyncWrapper.releaseCustomWordsSynchronizerInstance();
            this.mCustomWordSyncWrapper = null;
        }
        if (this.mSpeechKitInstance != null) {
            this.mSpeechKitInstance.cancelCurrent();
            this.mSpeechKitInstance.release();
            this.mSpeechKitInstance = null;
        }
    }

    public final synchronized CustomWordSynchronizer getCustomWordsSyncWrapper(UserDictionaryIterator iterator) {
        if (this.mCustomWordSyncWrapper == null) {
            this.mCustomWordSyncWrapper = new DragonCustomWordSynchronizer(this, iterator);
        }
        return this.mCustomWordSyncWrapper;
    }

    public final void cancelCustomWordSync() {
        if (this.mCustomWordSyncWrapper != null) {
            this.mCustomWordSyncWrapper.cancel();
        }
    }
}
