package com.nuance.swypeconnect.ac;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import com.nuance.connect.api.DLMConnector;
import com.nuance.connect.common.FeaturesLastUsed;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.PermissionUtils;
import com.nuance.swypeconnect.ac.ACScannerService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class ACScannerContacts extends ACScanner {
    private static final int CONTACT_MAX_DEFAULT = 60;
    private static final String SCANNER_CONTACTS_LAST_RUN_CALENDAR = "SCANNER_CONTACTS_LAST_RUN_CALENDAR";
    private ArrayList<ACScannerContactsType> scanTypes;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, ACScannerContacts.class.getSimpleName());
    static final ACScannerService.ScannerType TYPE = ACScannerService.ScannerType.CONTACTS;

    /* loaded from: classes.dex */
    public enum ACScannerContactsType {
        ALL,
        DISPLAY_NAME,
        FAMILY_NAME,
        GIVEN_NAME,
        CITY,
        REGION,
        COUNTRY
    }

    /* loaded from: classes.dex */
    private static class ContactReader {
        protected String dateField;

        private ContactReader() {
            this.dateField = "contact_last_updated_timestamp";
        }

        public static int getInt(Cursor cursor, String str) {
            return cursor.getInt(cursor.getColumnIndex(str));
        }

        public static String getString(Cursor cursor, String str) {
            return cursor.getString(cursor.getColumnIndex(str));
        }

        public Cursor read(Uri uri, Context context, String[] strArr, String str, String[] strArr2, long j) {
            String str2 = this.dateField + " DESC";
            StringBuilder sb = new StringBuilder();
            if (j > 0) {
                sb.append(str.equals("") ? "" : str + " AND ");
                sb.append(this.dateField);
                sb.append(">");
                sb.append(String.valueOf(j));
            } else {
                sb.append(str);
            }
            try {
                return context.getContentResolver().query(uri, strArr, sb.toString(), strArr2, str2);
            } catch (SQLiteException e) {
                ACScannerContacts.log.d("issue with query:", e.getMessage());
                return null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ACScannerContacts(ACScannerService aCScannerService) {
        super(aCScannerService, SCANNER_CONTACTS_LAST_RUN_CALENDAR);
        this.maxToProcess = 60;
        setScanType(new ACScannerContactsType[]{ACScannerContactsType.ALL});
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
        boolean z;
        this.service.getManager().getConnect().updateFeatureLastUsed(FeaturesLastUsed.Feature.SCANNER_CONTACTS, System.currentTimeMillis());
        startScan();
        this.callback.onStart();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ContactReader contactReader = new ContactReader();
        Context context = this.service.getContext();
        long timeInMillis = this.lastRunCalendar != null ? this.lastRunCalendar.getTimeInMillis() : 0L;
        this.removeDuplicates = true;
        log.d("begin contact scanning. scan since:" + timeInMillis);
        arrayList.add("contact_id");
        if (this.scanTypes.contains(ACScannerContactsType.ALL)) {
            arrayList.add("data3");
            arrayList.add("data2");
            arrayList.add("data1");
            arrayList2.add("data7");
            arrayList2.add("data8");
            arrayList2.add("data10");
        } else {
            if (this.scanTypes.contains(ACScannerContactsType.FAMILY_NAME)) {
                arrayList.add("data3");
            }
            if (this.scanTypes.contains(ACScannerContactsType.GIVEN_NAME)) {
                arrayList.add("data2");
            }
            if (this.scanTypes.contains(ACScannerContactsType.DISPLAY_NAME)) {
                arrayList.add("data1");
            }
            if (this.scanTypes.contains(ACScannerContactsType.CITY)) {
                arrayList2.add("data7");
            }
            if (this.scanTypes.contains(ACScannerContactsType.REGION)) {
                arrayList2.add("data8");
            }
            if (this.scanTypes.contains(ACScannerContactsType.COUNTRY)) {
                arrayList2.add("data10");
            }
        }
        String[] strArr = (String[]) arrayList2.toArray(new String[arrayList2.size()]);
        Cursor read = arrayList.size() > 0 ? contactReader.read(ContactsContract.Data.CONTENT_URI, context, (String[]) arrayList.toArray(new String[arrayList.size()]), "mimetype = ? ", new String[]{"vnd.android.cursor.item/name"}, timeInMillis) : null;
        arrayList.remove("contact_id");
        if (read != null) {
            DLMConnector.ScannerBucket bucket = getBucket(this.scanCoreList, 1, false);
            boolean z2 = this.maxToProcess > 0;
            while (z2) {
                boolean z3 = false;
                try {
                    if (read.moveToNext()) {
                        int i = ContactReader.getInt(read, "contact_id");
                        Iterator it = arrayList.iterator();
                        while (it.hasNext()) {
                            String string = ContactReader.getString(read, (String) it.next());
                            if (TextUtils.isEmpty(string) || TextUtils.isDigitsOnly(string)) {
                                z = z3;
                            } else {
                                z = true;
                                bucket.scan(string);
                            }
                            z3 = z;
                        }
                        if (i != Integer.MIN_VALUE && arrayList2.size() > 0) {
                            Cursor read2 = contactReader.read(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, context, strArr, "mimetype = ? AND contact_id = ?", new String[]{"vnd.android.cursor.item/postal-address_v2", String.valueOf(i)}, 0L);
                            if (read2 != null) {
                                while (read2.moveToNext()) {
                                    try {
                                        boolean z4 = z3;
                                        for (String str : strArr) {
                                            String string2 = ContactReader.getString(read2, str);
                                            if (!TextUtils.isEmpty(string2) && !TextUtils.isDigitsOnly(string2)) {
                                                z4 = true;
                                                bucket.scan(string2);
                                            }
                                        }
                                        z3 = z4;
                                    } finally {
                                    }
                                }
                            }
                            if (read2 != null) {
                                read2.close();
                            }
                        }
                        if (z3) {
                            this.currentProcess++;
                        }
                        z2 = this.currentProcess < this.maxToProcess;
                    } else {
                        z2 = false;
                    }
                } finally {
                    read.close();
                    bucket.close();
                }
            }
        }
        log.d("end contact scanning. Scanned " + this.currentProcess + " contacts.");
        endScan(true);
    }

    public final void setScanType(ACScannerContactsType[] aCScannerContactsTypeArr) {
        if (aCScannerContactsTypeArr == null) {
            this.scanTypes = null;
        } else {
            this.scanTypes = new ArrayList<>(Arrays.asList(aCScannerContactsTypeArr));
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final void start(ACScannerService.ACScannerCallback aCScannerCallback) throws ACScannerException {
        super.start(aCScannerCallback);
        if (this.scanTypes == null || this.scanTypes.size() == 0) {
            throw new ACScannerException(101);
        }
        if (!PermissionUtils.checkPermission(this.service.getContext(), "android.permission.READ_CONTACTS")) {
            throw new ACScannerException(100, "Contacts Scanner Requires permission to read contacts");
        }
        this.callback = aCScannerCallback;
        this.service.scheduleScan(this);
    }
}
