package com.google.android.gms.ads.internal.purchase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkd;
import java.util.Locale;

@zzin
/* loaded from: classes.dex */
public class zzh {
    private static zzh zzbxm;
    private final zza zzbxl;
    private static final String zzbxk = String.format(Locale.US, "CREATE TABLE IF NOT EXISTS %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL, %s INTEGER)", "InAppPurchase", "purchase_id", "product_id", "developer_payload", "record_time");
    private static final Object zzail = new Object();

    /* loaded from: classes.dex */
    public class zza extends SQLiteOpenHelper {
        public zza(Context context, String str) {
            super(context, str, (SQLiteDatabase.CursorFactory) null, 4);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL(zzh.zzbxk);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            zzkd.zzcw(new StringBuilder(64).append("Database updated from version ").append(i).append(" to version ").append(i2).toString());
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS InAppPurchase");
            onCreate(sQLiteDatabase);
        }
    }

    private zzh(Context context) {
        this.zzbxl = new zza(context, "google_inapp_purchase.db");
    }

    public static zzh zzs(Context context) {
        zzh zzhVar;
        synchronized (zzail) {
            if (zzbxm == null) {
                zzbxm = new zzh(context);
            }
            zzhVar = zzbxm;
        }
        return zzhVar;
    }

    public int getRecordCount() {
        Cursor cursor = null;
        int i = 0;
        synchronized (zzail) {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            try {
                if (writableDatabase != null) {
                    try {
                        cursor = writableDatabase.rawQuery("select count(*) from InAppPurchase", null);
                    } catch (SQLiteException e) {
                        String valueOf = String.valueOf(e.getMessage());
                        zzkd.zzcx(valueOf.length() != 0 ? "Error getting record count".concat(valueOf) : new String("Error getting record count"));
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    if (cursor.moveToFirst()) {
                        i = cursor.getInt(0);
                    } else if (cursor != null) {
                        cursor.close();
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return i;
    }

    public SQLiteDatabase getWritableDatabase() {
        try {
            return this.zzbxl.getWritableDatabase();
        } catch (SQLiteException e) {
            zzkd.zzcx("Error opening writable conversion tracking database");
            return null;
        }
    }

    public zzf zza(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        return new zzf(cursor.getLong(0), cursor.getString(1), cursor.getString(2));
    }

    public void zza(zzf zzfVar) {
        if (zzfVar == null) {
            return;
        }
        synchronized (zzail) {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            if (writableDatabase != null) {
                writableDatabase.delete("InAppPurchase", String.format(Locale.US, "%s = %d", "purchase_id", Long.valueOf(zzfVar.zzbxf)), null);
            }
        }
    }

    public void zzb(zzf zzfVar) {
        if (zzfVar == null) {
            return;
        }
        synchronized (zzail) {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            if (writableDatabase == null) {
                return;
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put("product_id", zzfVar.zzbxh);
            contentValues.put("developer_payload", zzfVar.zzbxg);
            contentValues.put("record_time", Long.valueOf(SystemClock.elapsedRealtime()));
            zzfVar.zzbxf = writableDatabase.insert("InAppPurchase", null, contentValues);
            if (getRecordCount() > 20000) {
                zzpt();
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x0032, code lost:            if (r1.moveToFirst() != false) goto L16;     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0034, code lost:            r9.add(zza(r1));     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x003f, code lost:            if (r1.moveToNext() != false) goto L50;     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0041, code lost:            if (r1 == null) goto L20;     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0043, code lost:            r1.close();     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.util.List<com.google.android.gms.ads.internal.purchase.zzf> zzg(long r14) {
        /*
            r13 = this;
            r10 = 0
            java.lang.Object r11 = com.google.android.gms.ads.internal.purchase.zzh.zzail
            monitor-enter(r11)
            java.util.LinkedList r9 = new java.util.LinkedList     // Catch: java.lang.Throwable -> L69
            r9.<init>()     // Catch: java.lang.Throwable -> L69
            r0 = 0
            int r0 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1))
            if (r0 > 0) goto L12
            monitor-exit(r11)     // Catch: java.lang.Throwable -> L69
            r0 = r9
        L11:
            return r0
        L12:
            android.database.sqlite.SQLiteDatabase r0 = r13.getWritableDatabase()     // Catch: java.lang.Throwable -> L69
            if (r0 != 0) goto L1b
            monitor-exit(r11)     // Catch: java.lang.Throwable -> L69
            r0 = r9
            goto L11
        L1b:
            java.lang.String r7 = "record_time ASC"
            java.lang.String r1 = "InAppPurchase"
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            java.lang.String r8 = java.lang.String.valueOf(r14)     // Catch: android.database.sqlite.SQLiteException -> L49 java.lang.Throwable -> L79
            android.database.Cursor r1 = r0.query(r1, r2, r3, r4, r5, r6, r7, r8)     // Catch: android.database.sqlite.SQLiteException -> L49 java.lang.Throwable -> L79
            boolean r0 = r1.moveToFirst()     // Catch: java.lang.Throwable -> L72 android.database.sqlite.SQLiteException -> L7c
            if (r0 == 0) goto L41
        L34:
            com.google.android.gms.ads.internal.purchase.zzf r0 = r13.zza(r1)     // Catch: java.lang.Throwable -> L72 android.database.sqlite.SQLiteException -> L7c
            r9.add(r0)     // Catch: java.lang.Throwable -> L72 android.database.sqlite.SQLiteException -> L7c
            boolean r0 = r1.moveToNext()     // Catch: java.lang.Throwable -> L72 android.database.sqlite.SQLiteException -> L7c
            if (r0 != 0) goto L34
        L41:
            if (r1 == 0) goto L46
            r1.close()     // Catch: java.lang.Throwable -> L69
        L46:
            monitor-exit(r11)     // Catch: java.lang.Throwable -> L69
            r0 = r9
            goto L11
        L49:
            r0 = move-exception
            r1 = r10
        L4b:
            java.lang.String r2 = "Error extracing purchase info: "
            java.lang.String r0 = r0.getMessage()     // Catch: java.lang.Throwable -> L72
            java.lang.String r0 = java.lang.String.valueOf(r0)     // Catch: java.lang.Throwable -> L72
            int r3 = r0.length()     // Catch: java.lang.Throwable -> L72
            if (r3 == 0) goto L6c
            java.lang.String r0 = r2.concat(r0)     // Catch: java.lang.Throwable -> L72
        L60:
            com.google.android.gms.internal.zzkd.zzcx(r0)     // Catch: java.lang.Throwable -> L72
            if (r1 == 0) goto L46
            r1.close()     // Catch: java.lang.Throwable -> L69
            goto L46
        L69:
            r0 = move-exception
            monitor-exit(r11)     // Catch: java.lang.Throwable -> L69
            throw r0
        L6c:
            java.lang.String r0 = new java.lang.String     // Catch: java.lang.Throwable -> L72
            r0.<init>(r2)     // Catch: java.lang.Throwable -> L72
            goto L60
        L72:
            r0 = move-exception
        L73:
            if (r1 == 0) goto L78
            r1.close()     // Catch: java.lang.Throwable -> L69
        L78:
            throw r0     // Catch: java.lang.Throwable -> L69
        L79:
            r0 = move-exception
            r1 = r10
            goto L73
        L7c:
            r0 = move-exception
            goto L4b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.ads.internal.purchase.zzh.zzg(long):java.util.List");
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x0060 A[Catch: all -> 0x0034, TRY_ENTER, TryCatch #3 {, blocks: (B:4:0x0004, B:6:0x000a, B:13:0x002f, B:14:0x0032, B:33:0x0060, B:34:0x0063, B:28:0x0053), top: B:3:0x0004 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void zzpt() {
        /*
            r11 = this;
            r9 = 0
            java.lang.Object r10 = com.google.android.gms.ads.internal.purchase.zzh.zzail
            monitor-enter(r10)
            android.database.sqlite.SQLiteDatabase r0 = r11.getWritableDatabase()     // Catch: java.lang.Throwable -> L34
            if (r0 != 0) goto Lc
            monitor-exit(r10)     // Catch: java.lang.Throwable -> L34
        Lb:
            return
        Lc:
            java.lang.String r7 = "record_time ASC"
            java.lang.String r1 = "InAppPurchase"
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            java.lang.String r8 = "1"
            android.database.Cursor r1 = r0.query(r1, r2, r3, r4, r5, r6, r7, r8)     // Catch: android.database.sqlite.SQLiteException -> L37 java.lang.Throwable -> L64
            if (r1 == 0) goto L2d
            boolean r0 = r1.moveToFirst()     // Catch: java.lang.Throwable -> L5d android.database.sqlite.SQLiteException -> L67
            if (r0 == 0) goto L2d
            com.google.android.gms.ads.internal.purchase.zzf r0 = r11.zza(r1)     // Catch: java.lang.Throwable -> L5d android.database.sqlite.SQLiteException -> L67
            r11.zza(r0)     // Catch: java.lang.Throwable -> L5d android.database.sqlite.SQLiteException -> L67
        L2d:
            if (r1 == 0) goto L32
            r1.close()     // Catch: java.lang.Throwable -> L34
        L32:
            monitor-exit(r10)     // Catch: java.lang.Throwable -> L34
            goto Lb
        L34:
            r0 = move-exception
            monitor-exit(r10)     // Catch: java.lang.Throwable -> L34
            throw r0
        L37:
            r0 = move-exception
            r1 = r9
        L39:
            java.lang.String r2 = "Error remove oldest record"
            java.lang.String r0 = r0.getMessage()     // Catch: java.lang.Throwable -> L5d
            java.lang.String r0 = java.lang.String.valueOf(r0)     // Catch: java.lang.Throwable -> L5d
            int r3 = r0.length()     // Catch: java.lang.Throwable -> L5d
            if (r3 == 0) goto L57
            java.lang.String r0 = r2.concat(r0)     // Catch: java.lang.Throwable -> L5d
        L4e:
            com.google.android.gms.internal.zzkd.zzcx(r0)     // Catch: java.lang.Throwable -> L5d
            if (r1 == 0) goto L32
            r1.close()     // Catch: java.lang.Throwable -> L34
            goto L32
        L57:
            java.lang.String r0 = new java.lang.String     // Catch: java.lang.Throwable -> L5d
            r0.<init>(r2)     // Catch: java.lang.Throwable -> L5d
            goto L4e
        L5d:
            r0 = move-exception
        L5e:
            if (r1 == 0) goto L63
            r1.close()     // Catch: java.lang.Throwable -> L34
        L63:
            throw r0     // Catch: java.lang.Throwable -> L34
        L64:
            r0 = move-exception
            r1 = r9
            goto L5e
        L67:
            r0 = move-exception
            goto L39
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.ads.internal.purchase.zzh.zzpt():void");
    }
}