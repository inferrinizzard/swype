package com.nuance.swypeconnect.ac;

import android.content.Context;
import com.nuance.connect.api.DLMConnector;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.util.Logger;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes.dex */
public final class ACScannerService extends ACService {
    static final int HIGH_QUALITY_WORDS = 1;
    static final int LOW_QUALITY_WORDS = 0;
    static final int MAXWORDLEN = 64;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, ACScannerService.class.getSimpleName());
    private ACScannerApplications appScanner;
    private ACScannerCalllog callScanner;
    private ACScannerContacts contactScanner;
    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private ACScannerFacebook facebookScanner;
    private ACScannerGmail2 gmailScanner2;
    private ACScannerList listScanner;
    private ACManager manager;
    private final DLMConnector service;
    private ACScannerSms smsScanner;
    private final PersistentDataStore store;
    private ACScannerTwitter twitterScanner;

    /* loaded from: classes.dex */
    public interface ACScannerCallback {
        void onFailure(int i, String str);

        void onFinish();

        void onStart();
    }

    /* loaded from: classes.dex */
    public enum ScannerType {
        SMS,
        GMAIL2,
        YAHOO_MAIL,
        FACEBOOK,
        TWITTER,
        CALL_LOG,
        APPLICATION,
        CONTACTS,
        LIST
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ACScannerService(Context context, DLMConnector dLMConnector, PersistentDataStore persistentDataStore, ACManager aCManager) {
        this.context = context;
        this.service = dLMConnector;
        this.store = persistentDataStore;
        this.manager = aCManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final DLMConnector.ScannerBucket getBucket(String str, String str2, String str3, int i, int i2, int i3, int i4, boolean z, int i5, boolean z2) {
        return this.service.getScannerBucket(str, str2, str3, i, i2, i3, i4, z, i5, z2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final DLMConnector.ScannerBucket getBucket(String str, String str2, String str3, int i, int i2, int[] iArr, int i3, boolean z, int i4, boolean z2) {
        return this.service.getScannerBucket(str, str2, str3, i, i2, iArr, i3, z, i4, z2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Context getContext() {
        return this.context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ACManager getManager() {
        return this.manager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final String getName() {
        return ACManager.SCANNER_SERVICE;
    }

    public final ACScanner getScanner(ScannerType scannerType) throws ACScannerException {
        log.d("getScanner type=" + scannerType);
        switch (scannerType) {
            case SMS:
                if (this.smsScanner == null) {
                    if (!ACScannerSms.checkDependencies()) {
                        throw new ACScannerException(106);
                    }
                    this.smsScanner = new ACScannerSms(this);
                }
                return this.smsScanner;
            case APPLICATION:
                if (this.appScanner == null) {
                    if (!ACScannerApplications.checkDependencies()) {
                        throw new ACScannerException(106);
                    }
                    this.appScanner = new ACScannerApplications(this);
                }
                return this.appScanner;
            case TWITTER:
                if (this.twitterScanner == null) {
                    if (!ACScannerTwitter.checkDependencies()) {
                        throw new ACScannerException(106);
                    }
                    this.twitterScanner = new ACScannerTwitter(this);
                }
                return this.twitterScanner;
            case CALL_LOG:
                if (this.callScanner == null) {
                    if (!ACScannerCalllog.checkDependencies()) {
                        throw new ACScannerException(106);
                    }
                    this.callScanner = new ACScannerCalllog(this);
                }
                return this.callScanner;
            case CONTACTS:
                if (this.contactScanner == null) {
                    if (!ACScannerContacts.checkDependencies()) {
                        throw new ACScannerException(106);
                    }
                    this.contactScanner = new ACScannerContacts(this);
                }
                return this.contactScanner;
            case FACEBOOK:
                if (this.facebookScanner == null) {
                    if (!ACScannerFacebook.checkDependencies()) {
                        throw new ACScannerException(106);
                    }
                    this.facebookScanner = new ACScannerFacebook(this);
                }
                return this.facebookScanner;
            case GMAIL2:
                if (this.gmailScanner2 == null) {
                    if (!ACScannerGmail2.checkDependencies()) {
                        throw new ACScannerException(106);
                    }
                    this.gmailScanner2 = new ACScannerGmail2(this);
                }
                return this.gmailScanner2;
            case LIST:
                if (this.listScanner == null) {
                    if (!ACScannerList.checkDependencies()) {
                        throw new ACScannerException(106);
                    }
                    this.listScanner = new ACScannerList(this);
                }
                return this.listScanner;
            default:
                throw new ACScannerException(105);
        }
    }

    public final List<ScannerType> getScannerList() {
        ArrayList arrayList = new ArrayList();
        if (ACScannerSms.checkDependencies()) {
            arrayList.add(ACScannerSms.TYPE);
        }
        if (ACScannerApplications.checkDependencies()) {
            arrayList.add(ACScannerApplications.TYPE);
        }
        if (ACScannerTwitter.checkDependencies()) {
            arrayList.add(ACScannerTwitter.TYPE);
        }
        if (ACScannerCalllog.checkDependencies()) {
            arrayList.add(ACScannerCalllog.TYPE);
        }
        if (ACScannerContacts.checkDependencies()) {
            arrayList.add(ACScannerContacts.TYPE);
        }
        if (ACScannerFacebook.checkDependencies()) {
            arrayList.add(ACScannerFacebook.TYPE);
        }
        if (ACScannerGmail2.checkDependencies()) {
            arrayList.add(ACScannerGmail2.TYPE);
        }
        if (ACScannerList.checkDependencies()) {
            arrayList.add(ACScannerList.TYPE);
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final PersistentDataStore getStore() {
        return this.store;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final boolean requiresDocument(int i) {
        return i == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void scheduleScan(final ACScanner aCScanner) {
        if (this.isShutdown) {
            log.e("Running after shutdown: " + aCScanner.getType());
        } else {
            log.d("scheduleScan: " + aCScanner.getType());
            this.executor.execute(new Runnable() { // from class: com.nuance.swypeconnect.ac.ACScannerService.1
                @Override // java.lang.Runnable
                public void run() {
                    Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() { // from class: com.nuance.swypeconnect.ac.ACScannerService.1.1
                        @Override // java.lang.Thread.UncaughtExceptionHandler
                        public void uncaughtException(Thread thread, Throwable th) {
                            ACScannerService.log.e("Error running scanner: " + aCScanner.getType());
                            th.printStackTrace();
                            aCScanner.fail(0, th.getMessage());
                        }
                    });
                    ACScannerService.log.d("running: " + aCScanner.getType());
                    aCScanner.scan();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final void shutdown() {
        this.isShutdown = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final void start() {
        this.isShutdown = false;
    }
}
