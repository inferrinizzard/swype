package com.nuance.swypeconnect.ac;

import com.nuance.connect.api.DLMConnector;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.StringUtils;
import com.nuance.swypeconnect.ac.ACScannerService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public final class ACScannerList extends ACScanner {
    private static final int LIST_MAX_DEFAULT = -1;
    private static final int MAX_WORD_LENGTH = 32;
    private static final int MIN_WORD_LENGTH = 1;
    private static final String SCANNER_LIST_LAST_RUN_LIST = "SCANNER_LIST_LAST_RUN_LIST";
    private final List<String> scanContent;
    private int wordQuality;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, ACScannerList.class.getSimpleName());
    static final ACScannerService.ScannerType TYPE = ACScannerService.ScannerType.LIST;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ACScannerList(ACScannerService aCScannerService) {
        super(aCScannerService, SCANNER_LIST_LAST_RUN_LIST);
        this.wordQuality = 0;
        this.scanContent = new ArrayList();
        this.maxToProcess = -1;
    }

    public final void addCollection(Collection<String> collection) throws ACException {
        if (collection == null) {
            throw new ACException(122, "Cannot pass in null to addCollection");
        }
        this.scanContent.addAll(collection);
    }

    public final void addItem(String str) throws ACException {
        if (str == null) {
            throw new ACException(122, "Cannot pass in null to addItem");
        }
        this.scanContent.add(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final void fail(int i, String str) {
        if (this.callback != null) {
            this.callback.onFailure(i, str);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final ACScannerService.ScannerType getType() {
        return TYPE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final void scan() {
        startScan();
        log.d("scan since:" + (this.lastRunCalendar != null ? this.lastRunCalendar.getTimeInMillis() : 0L));
        log.d("scanning items: " + this.scanContent.size());
        if (this.scanContent.size() == 0) {
            return;
        }
        log.d("getting buckets...");
        DLMConnector.ScannerBucket bucket = getBucket(this.scanCoreList, this.wordQuality, true);
        log.d("done getting buckets...");
        try {
            for (String str : this.scanContent) {
                this.currentProcess++;
                if (str != null) {
                    bucket.scan(str);
                    Iterator<String> it = StringUtils.scanWordsTokens(str, 1, 32).iterator();
                    while (it.hasNext()) {
                        bucket.scan(it.next());
                    }
                }
            }
            bucket.close();
            this.scanContent.clear();
            endScan(true);
        } catch (Throwable th) {
            bucket.close();
            this.scanContent.clear();
            throw th;
        }
    }

    public final void setWordQuality(int i) throws ACException {
        if (i != 0 && i != 1) {
            throw new ACException(122, "Word Quality must be high or low");
        }
        this.wordQuality = i;
    }

    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final void start(ACScannerService.ACScannerCallback aCScannerCallback) throws ACScannerException {
        super.start(aCScannerCallback);
        this.service.scheduleScan(this);
    }
}
