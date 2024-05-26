package com.nuance.swypeconnect.ac;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import com.nuance.connect.api.DLMConnector;
import com.nuance.connect.common.FeaturesLastUsed;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.PermissionUtils;
import com.nuance.swypeconnect.ac.ACScannerService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class ACScannerSms extends ACScanner {
    private static final String SCANNER_SMS_LAST_RUN_CALENDAR = "SCANNER_SMS_LAST_RUN_CALENDAR";
    private static final String SCANNER_SMS_MAX_MESSAGES = "SCANNER_SMS_MAX_MESSAGES";
    private static final String SCANNER_SMS_SCANCONTACTS = "SCANNER_SMS_SCANCONTACTS";
    private static final int SMS_MAX_DEFAULT = 420;
    private boolean scanContacts;
    private ArrayList<ACSmsScannerType> scanTypes;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, ACScannerSms.class.getSimpleName());
    private static final String[] PHONE_LOOKUP_DISPLAY_NAME = {"display_name"};
    static final ACScannerService.ScannerType TYPE = ACScannerService.ScannerType.SMS;

    /* loaded from: classes.dex */
    public enum ACSmsScannerType {
        ALL,
        INBOX,
        SENT
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SMSReader {
        public static final String ALL = "";
        public static final String INBOX = "inbox";
        public static final String OUTBOX = "outbox";
        public static final String SENT = "sent";
        private static final int SMS_COLUMN_ADDRESS = 2;
        private static final int SMS_COLUMN_BODY = 5;
        public static final Uri SMS_CONTENT_URI = Uri.parse("content://sms");

        private SMSReader() {
        }

        public static String getAddress(Cursor cursor) {
            return cursor.getString(2);
        }

        public static String getBody(Cursor cursor) {
            return cursor.getString(5);
        }

        public Cursor read(Context context, String str, long j) {
            try {
                return context.getContentResolver().query(Uri.withAppendedPath(SMS_CONTENT_URI, str), new String[]{"_id", "thread_id", "address", "person", "date", "body"}, "date>" + String.valueOf(j), null, "date DESC");
            } catch (SQLiteException e) {
                return null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ACScannerSms(ACScannerService aCScannerService) {
        super(aCScannerService, SCANNER_SMS_LAST_RUN_CALENDAR);
        this.scanContacts = true;
        setScanType(new ACSmsScannerType[]{ACSmsScannerType.SENT});
        this.scanContacts = this.store.readBoolean(SCANNER_SMS_SCANCONTACTS, true);
        this.maxToProcess = this.store.readInt(SCANNER_SMS_MAX_MESSAGES, 420);
    }

    private void processMailbox(Context context, Cursor cursor) {
        if (cursor != null) {
            log.d("getting buckets...");
            DLMConnector.ScannerBucket bucket = getBucket(this.scanCoreList, 0, true);
            DLMConnector.ScannerBucket bucket2 = getBucket(this.scanCoreList, 1, false);
            log.d("done getting buckets...");
            try {
                HashSet hashSet = new HashSet();
                while (this.currentProcess < this.maxToProcess && cursor.moveToNext()) {
                    String body = SMSReader.getBody(cursor);
                    if (!TextUtils.isEmpty(body)) {
                        bucket.scan(body);
                    }
                    if (this.scanContacts) {
                        String address = SMSReader.getAddress(cursor);
                        if (!TextUtils.isEmpty(address)) {
                            if (PhoneNumberUtils.isWellFormedSmsAddress(address)) {
                                hashSet.add(address);
                            } else {
                                bucket2.scan(address);
                            }
                        }
                    }
                    this.currentProcess++;
                }
                Iterator it = hashSet.iterator();
                while (it.hasNext()) {
                    Cursor query = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode((String) it.next())), PHONE_LOOKUP_DISPLAY_NAME, null, null, null);
                    if (query != null) {
                        while (query.moveToNext()) {
                            try {
                                String string = query.getString(0);
                                if (!TextUtils.isEmpty(string)) {
                                    bucket2.scan(string);
                                }
                            } catch (Throwable th) {
                                query.close();
                                throw th;
                            }
                        }
                        query.close();
                    }
                }
            } finally {
                cursor.close();
                bucket2.close();
                bucket.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final void fail(int i, String str) {
        if (this.callback != null) {
            this.callback.onFailure(i, str);
        }
    }

    public final boolean getScanContacts() {
        return this.scanContacts;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final ACScannerService.ScannerType getType() {
        return TYPE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final void scan() {
        this.service.getManager().getConnect().updateFeatureLastUsed(FeaturesLastUsed.Feature.SCANNER_SMS, System.currentTimeMillis());
        startScan();
        long timeInMillis = this.lastRunCalendar != null ? this.lastRunCalendar.getTimeInMillis() : 0L;
        log.d("scan since:" + timeInMillis);
        SMSReader sMSReader = new SMSReader();
        Context context = this.service.getContext();
        if (this.scanTypes.contains(ACSmsScannerType.ALL)) {
            processMailbox(context, sMSReader.read(context, "", timeInMillis));
        } else {
            if (this.scanTypes.contains(ACSmsScannerType.INBOX)) {
                processMailbox(context, sMSReader.read(context, SMSReader.INBOX, timeInMillis));
            }
            if (this.scanTypes.contains(ACSmsScannerType.SENT)) {
                processMailbox(context, sMSReader.read(context, SMSReader.SENT, timeInMillis));
            }
        }
        endScan(true);
    }

    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final void setMaxToProcess(int i) throws ACScannerException {
        super.setMaxToProcess(i);
        this.store.saveInt(SCANNER_SMS_MAX_MESSAGES, i);
    }

    public final void setScanContacts(boolean z) {
        this.scanContacts = z;
        this.store.saveBoolean(SCANNER_SMS_SCANCONTACTS, this.scanContacts);
    }

    public final void setScanType(ACSmsScannerType[] aCSmsScannerTypeArr) {
        if (aCSmsScannerTypeArr == null) {
            this.scanTypes = null;
        } else {
            this.scanTypes = new ArrayList<>(Arrays.asList(aCSmsScannerTypeArr));
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final void start(ACScannerService.ACScannerCallback aCScannerCallback) throws ACScannerException {
        super.start(aCScannerCallback);
        if (this.scanTypes == null || this.scanTypes.size() == 0) {
            throw new ACScannerException(101);
        }
        if (!PermissionUtils.checkPermission(this.service.getContext(), "android.permission.READ_SMS") || !PermissionUtils.checkPermission(this.service.getContext(), "android.permission.READ_CONTACTS")) {
            throw new ACScannerException(100);
        }
        if (this.maxToProcess < 0) {
            throw new ACScannerException(104, "Max SMS messages set to an unusable value: " + this.maxToProcess);
        }
        this.callback = aCScannerCallback;
        this.service.scheduleScan(this);
    }
}
