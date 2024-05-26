package com.nuance.swypeconnect.ac;

import com.nuance.connect.api.DLMConnector;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.util.VersionUtils;
import com.nuance.swypeconnect.ac.ACScannerService;
import java.security.InvalidParameterException;
import java.util.Calendar;

/* loaded from: classes.dex */
public abstract class ACScanner {
    public static final int CATEGORY_USER = 0;
    public static final int CORE_ALPHA = 1;
    public static final int CORE_CHINESE = 3;
    public static final int CORE_KOREAN = 2;
    static final int DLM_TEXT_SCAN_ACTION_DELAY_REORDER = 1;
    static final int DLM_TEXT_USER_ACTION_DELAY_REORDER = 1;
    static final int EXPLICIT_WORD_ADDITION_DELAY_REORDER = 0;
    public static final int FAILURE_FAILURE = 1;
    public static final int FAILURE_INVALID_TOKEN = 2;
    static final int NAMES_SCAN_ACTION_DELAY_REORDER = 1;
    static final int NAMES_USER_ACTION_DELAY_REORDER = 1;
    static final int NEW_WORD_SCAN_ACTION_DELAY_REORDER = 1;
    static final int NEW_WORD_USER_ACTION_DELAY_REORDER = 1;
    protected String appContext1;
    protected String appContext2;
    protected String appContext3;
    protected String calendarPreferenceName;
    protected ACScannerService.ACScannerCallback callback;
    protected int categoryId;
    protected int currentProcess;
    protected volatile Calendar lastRunCalendar;
    protected int maxToProcess;
    protected boolean removeDuplicates;
    protected int scanAction;
    protected int[] scanCoreList;
    protected ACScannerService service;
    protected PersistentDataStore store;
    protected int userAction;

    ACScanner(ACScannerService aCScannerService) {
        this.scanCoreList = new int[]{1};
        this.categoryId = 0;
        this.removeDuplicates = true;
        this.userAction = 1;
        this.scanAction = 1;
        this.calendarPreferenceName = null;
        this.maxToProcess = -1;
        this.currentProcess = 0;
        this.service = aCScannerService;
        this.store = aCScannerService.getStore();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ACScanner(ACScannerService aCScannerService, String str) {
        this.scanCoreList = new int[]{1};
        this.categoryId = 0;
        this.removeDuplicates = true;
        this.userAction = 1;
        this.scanAction = 1;
        this.calendarPreferenceName = null;
        this.maxToProcess = -1;
        this.currentProcess = 0;
        this.service = aCScannerService;
        this.store = aCScannerService.getStore();
        this.calendarPreferenceName = str;
        this.lastRunCalendar = (Calendar) this.store.readObject(this.calendarPreferenceName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean checkDependencies() {
        return true;
    }

    public void clearLastRun() {
        this.lastRunCalendar = null;
        this.store.delete(this.calendarPreferenceName);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void endScan(boolean z) {
        if (z) {
            this.lastRunCalendar = Calendar.getInstance();
            this.store.saveObject(this.calendarPreferenceName, this.lastRunCalendar);
        }
        this.callback.onFinish();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void fail(int i, String str);

    DLMConnector.ScannerBucket getBucket(int i, int i2, boolean z) {
        return this.service.getBucket(this.appContext1, this.appContext2, this.appContext3, this.userAction, this.scanAction, i, i2, z, this.categoryId, this.removeDuplicates);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DLMConnector.ScannerBucket getBucket(int[] iArr, int i, boolean z) {
        return this.service.getBucket(this.appContext1, this.appContext2, this.appContext3, this.userAction, this.scanAction, iArr, i, z, this.categoryId, this.removeDuplicates);
    }

    public Calendar getLastRun() {
        return this.lastRunCalendar;
    }

    public int getMaxToProcess() {
        return this.maxToProcess;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract ACScannerService.ScannerType getType();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void scan();

    public void setApplicationPredictionContext(String str, String str2, String str3) {
        this.appContext1 = str;
        this.appContext2 = str2;
        this.appContext3 = str3;
    }

    void setCategoryId(int i) {
        this.categoryId = i;
    }

    public void setMaxToProcess(int i) throws ACScannerException {
        if (i < 0) {
            throw new ACScannerException(122, "value must be greater then zero");
        }
        this.maxToProcess = i;
    }

    public void setScanCore(int[] iArr) {
        if (iArr == null || iArr.length == 0) {
            throw new InvalidParameterException("A valid core must be set");
        }
        for (int i : iArr) {
            if (i != 1 && i != 2) {
                throw new InvalidParameterException(i + " is not a valid core selection.");
            }
            try {
                this.service.getManager().getCoreVersion(i);
            } catch (ACException e) {
                throw new InvalidParameterException(i + " is not available.");
            }
        }
        this.scanCoreList = (int[]) iArr.clone();
    }

    public void start(ACScannerService.ACScannerCallback aCScannerCallback) throws ACScannerException {
        if (this.scanCoreList == null || this.scanCoreList.length == 0) {
            throw new ACScannerException(102, "You must set at least one core active for a scanner");
        }
        if (this.categoryId != 0) {
            try {
                for (int i : this.scanCoreList) {
                    if (!VersionUtils.isAtLeastVerson(this.service.getManager().getCoreVersion(i), "9.8")) {
                        throw new ACScannerException(124, "Category Scanning is not available until core 9.8");
                    }
                }
            } catch (ACException e) {
                throw new ACScannerException(124, "Category Scanning is not available until core 9.8");
            }
        }
        this.callback = aCScannerCallback;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void startScan() {
        this.callback.onStart();
        this.currentProcess = 0;
    }
}
