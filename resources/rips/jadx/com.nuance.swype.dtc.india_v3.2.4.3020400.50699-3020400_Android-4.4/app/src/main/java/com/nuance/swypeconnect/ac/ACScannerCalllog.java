package com.nuance.swypeconnect.ac;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.provider.CallLog;
import com.nuance.connect.api.DLMConnector;
import com.nuance.connect.common.FeaturesLastUsed;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.PermissionUtils;
import com.nuance.connect.util.StringUtils;
import com.nuance.swypeconnect.ac.ACScannerService;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class ACScannerCalllog extends ACScanner {
    private static final int CALLLOG_MAX_DEFAULT = 60;
    private static final int MAX_WORD_LENGTH = 32;
    private static final int MIN_WORD_LENGTH = 1;
    private static final String SCANNER_CALLLOG_LAST_RUN_CALENDAR = "SCANNER_CALLLOG_LAST_RUN_CALENDAR";
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, ACScannerCalllog.class.getSimpleName());
    static final ACScannerService.ScannerType TYPE = ACScannerService.ScannerType.CALL_LOG;

    /* loaded from: classes.dex */
    private static class CallLogReader {
        private static final String[] CALLLOGS_PROJECTION = {"name", "date"};
        private static final String CALLLOGS_SELECTION = "name IS NOT NULL";
        private static final int CALLLOG_COLUMN_NAME = 0;

        private CallLogReader() {
        }

        public static String getString(Cursor cursor, int i) {
            return cursor.getString(i);
        }

        public Cursor read(Context context, long j) {
            try {
                return context.getContentResolver().query(CallLog.Calls.CONTENT_URI, CALLLOGS_PROJECTION, "name IS NOT NULL AND date>" + String.valueOf(j), null, "date DESC");
            } catch (SQLiteException e) {
                return null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ACScannerCalllog(ACScannerService aCScannerService) {
        super(aCScannerService, SCANNER_CALLLOG_LAST_RUN_CALENDAR);
        this.maxToProcess = 60;
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
        this.service.getManager().getConnect().updateFeatureLastUsed(FeaturesLastUsed.Feature.SCANNER_CALLLOG, System.currentTimeMillis());
        startScan();
        long timeInMillis = this.lastRunCalendar != null ? this.lastRunCalendar.getTimeInMillis() : 0L;
        log.d("scan since:" + timeInMillis);
        Cursor read = new CallLogReader().read(this.service.getContext(), timeInMillis);
        if (read != null) {
            log.d("getting buckets...");
            DLMConnector.ScannerBucket bucket = getBucket(this.scanCoreList, 1, true);
            log.d("done getting buckets...");
            while (this.currentProcess < this.maxToProcess && read.moveToNext()) {
                try {
                    String string = CallLogReader.getString(read, 0);
                    this.currentProcess++;
                    if (string != null) {
                        bucket.scan(string);
                        Iterator<String> it = StringUtils.scanWordsTokens(string, 1, 32).iterator();
                        while (it.hasNext()) {
                            bucket.scan(it.next());
                        }
                    }
                } finally {
                    read.close();
                    bucket.close();
                }
            }
        }
        endScan(true);
    }

    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final void start(ACScannerService.ACScannerCallback aCScannerCallback) throws ACScannerException {
        super.start(aCScannerCallback);
        if (!PermissionUtils.checkPermission(this.service.getContext(), 16, PermissionUtils.READ_CALL_LOG, "android.permission.READ_CONTACTS")) {
            throw new ACScannerException(100);
        }
        this.callback = aCScannerCallback;
        this.service.scheduleScan(this);
    }
}
