package com.nuance.swype.input.corelibmgr;

import com.nuance.android.compat.ApplicationInfoCompat;
import com.nuance.input.swypecorelib.SwypeCoreLibrary;
import com.nuance.input.swypecorelib.T9WriteAlpha;
import com.nuance.input.swypecorelib.T9WriteCJK;
import com.nuance.input.swypecorelib.T9WriteChinese;
import com.nuance.input.swypecorelib.T9WriteJapanese;
import com.nuance.input.swypecorelib.T9WriteKorean;
import com.nuance.input.swypecorelib.XT9CoreAlphaInput;
import com.nuance.input.swypecorelib.XT9CoreChineseInput;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.input.swypecorelib.XT9CoreJapaneseInput;
import com.nuance.input.swypecorelib.XT9CoreKoreanInput;
import com.nuance.input.swypecorelib.XT9Status;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.input.DatabaseConfig;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.emoji.EmojiLoader;
import com.nuance.swype.util.LogManager;
import jp.co.omronsoft.openwnn.JAJP.OpenWnnEngineJAJP;

/* loaded from: classes.dex */
public class SwypeCoreLibManager implements XT9CoreInput.DlmEventHandler {
    protected static final LogManager.Log log = LogManager.getLog("SwypeCoreLibManager");
    protected static final LogManager.Trace trace = LogManager.getTrace();
    final IMEApplication imeApp;
    final SwypeCoreLibrary swypecoreInstance;

    public SwypeCoreLibManager(IMEApplication imeApp, String coreLibName) {
        this.imeApp = imeApp;
        this.swypecoreInstance = SwypeCoreLibrary.getInstance(imeApp, coreLibName, ApplicationInfoCompat.getNativeLibraryDir(imeApp.getApplicationInfo()));
    }

    public void setRunningState(int runningState) {
        if (this.swypecoreInstance != null) {
            this.swypecoreInstance.setRunningState(runningState);
            this.imeApp.getConnect().setRunningState(runningState);
            if (this.swypecoreInstance.getAlphaCoreInstance().hasInputContext()) {
                this.swypecoreInstance.getAlphaCoreInstance().setRunningState(runningState);
            }
            if (this.swypecoreInstance.getChineseCoreInstance().hasInputContext()) {
                this.swypecoreInstance.getChineseCoreInstance().setRunningState(runningState);
            }
        }
    }

    public XT9CoreAlphaInput getXT9CoreAlphaInputSession() {
        return createXT9CoreAlphaInputSession(DatabaseConfig.getDatabaseConfigFile(this.imeApp), DatabaseConfig.getExternalDatabasePath(this.imeApp));
    }

    private XT9CoreAlphaInput createXT9CoreAlphaInputSession(String databaseConfigFile, String[] externalDatabasePath) {
        XT9CoreAlphaInput coreAlphaInput = this.swypecoreInstance.getAlphaCoreInstance();
        coreAlphaInput.createSession(this, databaseConfigFile, EmojiLoader.getFilter(), externalDatabasePath);
        return coreAlphaInput;
    }

    public XT9CoreChineseInput getXT9CoreChineseInputSession() {
        return createXT9CoreChineseInputSession(DatabaseConfig.getDatabaseConfigFile(this.imeApp), DatabaseConfig.getExternalDatabasePath(this.imeApp));
    }

    private XT9CoreChineseInput createXT9CoreChineseInputSession(String databaseConfigFile, String[] externalDatabasePath) {
        XT9CoreChineseInput chineseInput = this.swypecoreInstance.getChineseCoreInstance();
        chineseInput.createSession(this, databaseConfigFile, EmojiLoader.getFilter(), externalDatabasePath);
        return chineseInput;
    }

    public XT9CoreKoreanInput getXT9CoreKoreanInputSession() {
        return createXT9CoreKoreanInputSession(DatabaseConfig.getDatabaseConfigFile(this.imeApp), DatabaseConfig.getExternalDatabasePath(this.imeApp));
    }

    private XT9CoreKoreanInput createXT9CoreKoreanInputSession(String databaseConfigFile, String[] externalDatabasePath) {
        XT9CoreKoreanInput koreanInput = this.swypecoreInstance.getKoreanCoreInstance(createXT9CoreAlphaInputSession(databaseConfigFile, externalDatabasePath));
        koreanInput.createSession(this, databaseConfigFile, EmojiLoader.getFilter(), externalDatabasePath);
        return koreanInput;
    }

    public XT9CoreJapaneseInput getXT9CoreJapaneseInputSession() {
        return createXT9CoreJapaneseInputSession(DatabaseConfig.getDatabaseConfigFile(this.imeApp), DatabaseConfig.getExternalDatabasePath(this.imeApp));
    }

    private XT9CoreJapaneseInput createXT9CoreJapaneseInputSession(String databaseConfigFile, String[] externalDatabasePath) {
        XT9CoreJapaneseInput japaneseInput = this.swypecoreInstance.getJapaneseCoreInstance(createXT9CoreAlphaInputSession(databaseConfigFile, externalDatabasePath));
        japaneseInput.createSession(this, databaseConfigFile, EmojiLoader.getFilter(), externalDatabasePath);
        return japaneseInput;
    }

    public T9WriteAlpha getT9WriteAlphaSession() {
        return createT9WriteAlphaSession(DatabaseConfig.getDatabaseConfigFile(this.imeApp), DatabaseConfig.getExternalDatabasePath(this.imeApp));
    }

    private T9WriteAlpha createT9WriteAlphaSession(String databaseConfigFile, String[] externalDatabasePath) {
        T9WriteAlpha t9writeAlpha = this.swypecoreInstance.getT9WriteAlphaInstance();
        t9writeAlpha.createSession(databaseConfigFile, externalDatabasePath);
        return t9writeAlpha;
    }

    public T9WriteCJK getT9WriteChineseSession() {
        return createT9WriteChineseSession(DatabaseConfig.getDatabaseConfigFile(this.imeApp), DatabaseConfig.getExternalDatabasePath(this.imeApp));
    }

    private T9WriteCJK createT9WriteChineseSession(String databaseConfigFile, String[] externalDatabasePath) {
        T9WriteChinese t9writeChinese = this.swypecoreInstance.getT9WriteChineseInstance();
        t9writeChinese.createSession(databaseConfigFile, externalDatabasePath);
        return t9writeChinese;
    }

    public T9WriteCJK getT9WriteKoreanSession() {
        return createT9WriteKoreanSession(DatabaseConfig.getDatabaseConfigFile(this.imeApp), DatabaseConfig.getExternalDatabasePath(this.imeApp));
    }

    private T9WriteCJK createT9WriteKoreanSession(String databaseConfigFile, String[] externalDatabasePath) {
        T9WriteKorean t9writeKorean = this.swypecoreInstance.getT9WriteKoreanInstance();
        t9writeKorean.createSession(databaseConfigFile, externalDatabasePath);
        return t9writeKorean;
    }

    public T9WriteCJK getT9WriteJapaneseSession() {
        return createT9WriteJapaneseSession(DatabaseConfig.getDatabaseConfigFile(this.imeApp), DatabaseConfig.getExternalDatabasePath(this.imeApp));
    }

    private T9WriteCJK createT9WriteJapaneseSession(String databaseConfigFile, String[] externalDatabasePath) {
        T9WriteJapanese t9writeJapanese = this.swypecoreInstance.getT9WriteJapaneseInstance();
        t9writeJapanese.createSession(databaseConfigFile, externalDatabasePath);
        return t9writeJapanese;
    }

    public SwypeCoreLibrary getSwypeCoreLibInstance() {
        return this.swypecoreInstance;
    }

    public OpenWnnEngineJAJP createOpenWnnEngineJAJP(String coreLibName) {
        return this.swypecoreInstance.createOpenWnnEngineJAJP(coreLibName, ApplicationInfoCompat.getNativeLibraryDir(this.imeApp.getApplicationInfo()));
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput.DlmEventHandler
    public void onBeginDlmBackup(int category) {
        log.d("DLM Backup Begining for category=", Integer.valueOf(category));
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput.DlmEventHandler
    public void onDlmEvent(byte[] event, boolean highPriority, long timestamp, int category) {
        log.d("#onDlmEvent()");
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput.DlmEventHandler
    public void onDlmInitializeStatus(XT9Status status, int coretInputCategory) {
        log.d("#onDlmInitializeStatus() status = " + status);
        if (status == XT9Status.ET9STATUS_INVALID_SIZE) {
            Connect.Sync sync = this.imeApp.getConnect().getSync();
            if (sync.isAvailable() && sync.isEnabled()) {
                log.e("Issue initializing XT9, restoring...");
                sync.requestRestore(coretInputCategory);
            } else {
                log.e("Issue initializing XT9.");
            }
        }
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput.DlmEventHandler
    public void onEndDlmBackup() {
        log.d("DLM Backup Compelete");
    }
}
