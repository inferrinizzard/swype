package com.flurry.sdk;

import android.provider.Settings;
import android.text.TextUtils;
import com.flurry.sdk.le;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class je {
    private static final String b = je.class.getSimpleName();
    private static je c;
    public final Map<jm, byte[]> a;
    private final Set<String> d;
    private final ka<le> e;
    private a f;
    private jo g;
    private String h;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum a {
        NONE,
        ADVERTISING,
        DEVICE,
        REPORTED_IDS,
        FINISHED
    }

    public static synchronized je a() {
        je jeVar;
        synchronized (je.class) {
            if (c == null) {
                c = new je();
            }
            jeVar = c;
        }
        return jeVar;
    }

    private je() {
        HashSet hashSet = new HashSet();
        hashSet.add("null");
        hashSet.add("9774d56d682e549c");
        hashSet.add("dead00beef");
        this.d = Collections.unmodifiableSet(hashSet);
        this.a = new HashMap();
        this.e = new ka<le>() { // from class: com.flurry.sdk.je.1
            @Override // com.flurry.sdk.ka
            public final /* synthetic */ void a(le leVar) {
                switch (AnonymousClass4.a[leVar.c - 1]) {
                    case 1:
                        if (!je.this.c()) {
                            return;
                        }
                        jr.a().b(new lw() { // from class: com.flurry.sdk.je.1.1
                            @Override // com.flurry.sdk.lw
                            public final void a() {
                                je.this.e();
                            }
                        });
                        return;
                    default:
                        return;
                }
            }
        };
        this.f = a.NONE;
        kb.a().a("com.flurry.android.sdk.FlurrySessionEvent", this.e);
        jr.a().b(new lw() { // from class: com.flurry.sdk.je.2
            @Override // com.flurry.sdk.lw
            public final void a() {
                je.b(je.this);
            }
        });
    }

    public final boolean c() {
        return a.FINISHED.equals(this.f);
    }

    public final boolean d() {
        return this.g == null || !this.g.b;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.flurry.sdk.je$4, reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] a;

        static {
            try {
                b[a.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                b[a.ADVERTISING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                b[a.DEVICE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                b[a.REPORTED_IDS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            a = new int[le.a.a().length];
            try {
                a[le.a.a - 1] = 1;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void e() {
        lr.b();
        if (ls.a(jr.a().a)) {
            this.g = ls.b(jr.a().a);
            if (c()) {
                h();
                kb.a().a(new jg());
            }
        }
    }

    private static void a(String str, File file) {
        DataOutputStream dataOutputStream;
        try {
            dataOutputStream = new DataOutputStream(new FileOutputStream(file));
        } catch (Throwable th) {
            th = th;
            dataOutputStream = null;
            lr.a(dataOutputStream);
            throw th;
        }
        try {
            try {
                dataOutputStream.writeInt(1);
                dataOutputStream.writeUTF(str);
                lr.a(dataOutputStream);
            } catch (Throwable th2) {
                th = th2;
                lr.a(dataOutputStream);
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            kf.a(6, b, "Error when saving deviceId", th);
            lr.a(dataOutputStream);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v1, types: [boolean] */
    /* JADX WARN: Type inference failed for: r2v2, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r2v3 */
    private static String f() {
        DataInputStream dataInputStream;
        File fileStreamPath = jr.a().a.getFileStreamPath(".flurryb.");
        if (fileStreamPath != null) {
            ?? exists = fileStreamPath.exists();
            try {
                if (exists != 0) {
                    try {
                        dataInputStream = new DataInputStream(new FileInputStream(fileStreamPath));
                    } catch (Throwable th) {
                        exists = 0;
                        th = th;
                        lr.a((Closeable) exists);
                        throw th;
                    }
                    try {
                        r0 = 1 == dataInputStream.readInt() ? dataInputStream.readUTF() : null;
                        lr.a((Closeable) dataInputStream);
                    } catch (Throwable th2) {
                        th = th2;
                        kf.a(6, b, "Error when loading deviceId", th);
                        lr.a((Closeable) dataInputStream);
                        return r0;
                    }
                }
            } catch (Throwable th3) {
                th = th3;
            }
        }
        return r0;
    }

    private String g() {
        String[] list;
        DataInputStream dataInputStream;
        Throwable th;
        String str = null;
        File filesDir = jr.a().a.getFilesDir();
        if (filesDir != null && (list = filesDir.list(new FilenameFilter() { // from class: com.flurry.sdk.je.3
            @Override // java.io.FilenameFilter
            public final boolean accept(File file, String str2) {
                return str2.startsWith(".flurryagent.");
            }
        })) != null && list.length != 0) {
            File fileStreamPath = jr.a().a.getFileStreamPath(list[0]);
            if (fileStreamPath != null && fileStreamPath.exists()) {
                try {
                    dataInputStream = new DataInputStream(new FileInputStream(fileStreamPath));
                } catch (Throwable th2) {
                    dataInputStream = null;
                    th = th2;
                    lr.a((Closeable) dataInputStream);
                    throw th;
                }
                try {
                    try {
                        if (46586 == dataInputStream.readUnsignedShort() && 2 == dataInputStream.readUnsignedShort()) {
                            dataInputStream.readUTF();
                            str = dataInputStream.readUTF();
                        }
                        lr.a((Closeable) dataInputStream);
                    } catch (Throwable th3) {
                        th = th3;
                        lr.a((Closeable) dataInputStream);
                        throw th;
                    }
                } catch (Throwable th4) {
                    th = th4;
                    kf.a(6, b, "Error when loading deviceId", th);
                    lr.a((Closeable) dataInputStream);
                    return str;
                }
            }
        }
        return str;
    }

    private void h() {
        String str;
        if (this.g == null) {
            str = null;
        } else {
            str = this.g.a;
        }
        if (str != null) {
            kf.a(3, b, "Fetched advertising id");
            this.a.put(jm.AndroidAdvertisingId, lr.e(str));
        }
        String str2 = this.h;
        if (str2 != null) {
            kf.a(3, b, "Fetched device id");
            this.a.put(jm.DeviceId, lr.e(str2));
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:5:0x0017. Please report as an issue. */
    static /* synthetic */ void b(je jeVar) {
        boolean z;
        String str;
        while (!a.FINISHED.equals(jeVar.f)) {
            switch (jeVar.f) {
                case NONE:
                    jeVar.f = a.ADVERTISING;
                    break;
                case ADVERTISING:
                    jeVar.f = a.DEVICE;
                    break;
                case DEVICE:
                    jeVar.f = a.REPORTED_IDS;
                    break;
                case REPORTED_IDS:
                    jeVar.f = a.FINISHED;
                    break;
            }
            try {
                switch (jeVar.f) {
                    case ADVERTISING:
                        jeVar.e();
                        continue;
                    case DEVICE:
                        lr.b();
                        String string = Settings.Secure.getString(jr.a().a.getContentResolver(), "android_id");
                        if (TextUtils.isEmpty(string)) {
                            z = false;
                        } else {
                            z = !jeVar.d.contains(string.toLowerCase(Locale.US));
                        }
                        if (!z) {
                            str = null;
                        } else {
                            str = "AND" + string;
                        }
                        if (TextUtils.isEmpty(str)) {
                            str = f();
                            if (TextUtils.isEmpty(str)) {
                                str = jeVar.g();
                                if (TextUtils.isEmpty(str)) {
                                    str = "ID" + Long.toString(Double.doubleToLongBits(Math.random()) + ((System.nanoTime() + (lr.i(lo.a(jr.a().a)) * 37)) * 37), 16);
                                }
                                if (!TextUtils.isEmpty(str)) {
                                    File fileStreamPath = jr.a().a.getFileStreamPath(".flurryb.");
                                    if (lq.a(fileStreamPath)) {
                                        a(str, fileStreamPath);
                                    }
                                }
                            }
                        }
                        jeVar.h = str;
                        continue;
                    case REPORTED_IDS:
                        jeVar.h();
                        continue;
                    default:
                        continue;
                }
            } catch (Exception e) {
                kf.a(4, b, "Exception during id fetch:" + jeVar.f + ", " + e);
            }
            kf.a(4, b, "Exception during id fetch:" + jeVar.f + ", " + e);
        }
        kb.a().a(new jf());
    }
}
