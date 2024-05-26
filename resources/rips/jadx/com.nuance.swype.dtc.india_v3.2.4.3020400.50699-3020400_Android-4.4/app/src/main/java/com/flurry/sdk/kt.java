package com.flurry.sdk;

import com.flurry.sdk.ks;
import com.flurry.sdk.ku;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public class kt {
    String b;
    LinkedHashMap<String, List<String>> c;
    private static final String d = kt.class.getSimpleName();
    public static final Integer a = 50;

    public kt(String str) {
        this.b = str + "Main";
        b(this.b);
    }

    private void b(String str) {
        this.c = new LinkedHashMap<>();
        ArrayList<String> arrayList = new ArrayList();
        if (i(str)) {
            List<String> j = j(str);
            if (j != null && j.size() > 0) {
                arrayList.addAll(j);
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    c((String) it.next());
                }
            }
            h(str);
        } else {
            List list = (List) new jy(jr.a().a.getFileStreamPath(d(this.b)), str, 1, new lc<List<ku>>() { // from class: com.flurry.sdk.kt.1
                @Override // com.flurry.sdk.lc
                public final kz<List<ku>> a$1f969724() {
                    return new ky(new ku.a());
                }
            }).a();
            if (list == null) {
                kf.c(d, "New main file also not found. returning..");
                return;
            } else {
                Iterator it2 = list.iterator();
                while (it2.hasNext()) {
                    arrayList.add(((ku) it2.next()).a);
                }
            }
        }
        for (String str2 : arrayList) {
            this.c.put(str2, g(str2));
        }
    }

    private void c(String str) {
        List<String> j = j(str);
        if (j == null) {
            kf.c(d, "No old file to replace");
            return;
        }
        for (String str2 : j) {
            byte[] k = k(str2);
            if (k == null) {
                kf.a(6, d, "File does not exist");
            } else {
                a(str2, k);
                lr.b();
                kf.a(5, d, "Deleting  block File for " + str2 + " file name:" + jr.a().a.getFileStreamPath(".flurrydatasenderblock." + str2));
                File fileStreamPath = jr.a().a.getFileStreamPath(".flurrydatasenderblock." + str2);
                if (fileStreamPath.exists()) {
                    kf.a(5, d, "Found file for " + str2 + ". Deleted - " + fileStreamPath.delete());
                }
            }
        }
        if (j != null) {
            a(str, j, ".YFlurrySenderIndex.info.");
            h(str);
        }
    }

    private static String d(String str) {
        return ".YFlurrySenderIndex.info." + str;
    }

    private synchronized void a() {
        LinkedList linkedList = new LinkedList(this.c.keySet());
        new jy(jr.a().a.getFileStreamPath(d(this.b)), ".YFlurrySenderIndex.info.", 1, new lc<List<ku>>() { // from class: com.flurry.sdk.kt.4
            @Override // com.flurry.sdk.lc
            public final kz<List<ku>> a$1f969724() {
                return new ky(new ku.a());
            }
        }).b();
        if (!linkedList.isEmpty()) {
            a(this.b, linkedList, this.b);
        }
    }

    public final synchronized void a(ks ksVar, String str) {
        List<String> list;
        boolean z = false;
        synchronized (this) {
            kf.a(4, d, "addBlockInfo" + str);
            String str2 = ksVar.a;
            List<String> list2 = this.c.get(str);
            if (list2 == null) {
                kf.a(4, d, "New Data Key");
                list = new LinkedList();
                z = true;
            } else {
                list = list2;
            }
            list.add(str2);
            if (list.size() > a.intValue()) {
                e(list.get(0));
                list.remove(0);
            }
            this.c.put(str, list);
            a(str, list, ".YFlurrySenderIndex.info.");
            if (z) {
                a();
            }
        }
    }

    private boolean e(String str) {
        return new jy(jr.a().a.getFileStreamPath(ks.a(str)), ".yflurrydatasenderblock.", 1, new lc<ks>() { // from class: com.flurry.sdk.kt.2
            @Override // com.flurry.sdk.lc
            public final kz<ks> a$1f969724() {
                return new ks.a();
            }
        }).b();
    }

    public final boolean a(String str, String str2) {
        List<String> list = this.c.get(str2);
        boolean z = false;
        if (list != null) {
            e(str);
            z = list.remove(str);
        }
        if (list != null && !list.isEmpty()) {
            this.c.put(str2, list);
            a(str2, list, ".YFlurrySenderIndex.info.");
        } else {
            f(str2);
        }
        return z;
    }

    public final List<String> a(String str) {
        return this.c.get(str);
    }

    private synchronized boolean f(String str) {
        boolean b;
        lr.b();
        jy jyVar = new jy(jr.a().a.getFileStreamPath(d(str)), ".YFlurrySenderIndex.info.", 1, new lc<List<ku>>() { // from class: com.flurry.sdk.kt.3
            @Override // com.flurry.sdk.lc
            public final kz<List<ku>> a$1f969724() {
                return new ky(new ku.a());
            }
        });
        List<String> a2 = a(str);
        if (a2 != null) {
            kf.a(4, d, "discardOutdatedBlocksForDataKey: notSentBlocks = " + a2.size());
            for (String str2 : a2) {
                e(str2);
                kf.a(4, d, "discardOutdatedBlocksForDataKey: removed block = " + str2);
            }
        }
        this.c.remove(str);
        b = jyVar.b();
        a();
        return b;
    }

    private synchronized List<String> g(String str) {
        ArrayList arrayList;
        lr.b();
        kf.a(5, d, "Reading Index File for " + str + " file name:" + jr.a().a.getFileStreamPath(d(str)));
        List list = (List) new jy(jr.a().a.getFileStreamPath(d(str)), ".YFlurrySenderIndex.info.", 1, new lc<List<ku>>() { // from class: com.flurry.sdk.kt.5
            @Override // com.flurry.sdk.lc
            public final kz<List<ku>> a$1f969724() {
                return new ky(new ku.a());
            }
        }).a();
        arrayList = new ArrayList();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(((ku) it.next()).a);
        }
        return arrayList;
    }

    private synchronized void a(String str, byte[] bArr) {
        lr.b();
        kf.a(5, d, "Saving Block File for " + str + " file name:" + jr.a().a.getFileStreamPath(ks.a(str)));
        new jy(jr.a().a.getFileStreamPath(ks.a(str)), ".yflurrydatasenderblock.", 1, new lc<ks>() { // from class: com.flurry.sdk.kt.6
            @Override // com.flurry.sdk.lc
            public final kz<ks> a$1f969724() {
                return new ks.a();
            }
        }).a(new ks(bArr));
    }

    private static void h(String str) {
        lr.b();
        kf.a(5, d, "Deleting Index File for " + str + " file name:" + jr.a().a.getFileStreamPath(".FlurrySenderIndex.info." + str));
        File fileStreamPath = jr.a().a.getFileStreamPath(".FlurrySenderIndex.info." + str);
        if (fileStreamPath.exists()) {
            kf.a(5, d, "Found file for " + str + ". Deleted - " + fileStreamPath.delete());
        }
    }

    private synchronized void a(String str, List<String> list, String str2) {
        lr.b();
        kf.a(5, d, "Saving Index File for " + str + " file name:" + jr.a().a.getFileStreamPath(d(str)));
        jy jyVar = new jy(jr.a().a.getFileStreamPath(d(str)), str2, 1, new lc<List<ku>>() { // from class: com.flurry.sdk.kt.7
            @Override // com.flurry.sdk.lc
            public final kz<List<ku>> a$1f969724() {
                return new ky(new ku.a());
            }
        });
        ArrayList arrayList = new ArrayList();
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(new ku(it.next()));
        }
        jyVar.a(arrayList);
    }

    private synchronized boolean i(String str) {
        File fileStreamPath;
        fileStreamPath = jr.a().a.getFileStreamPath(".FlurrySenderIndex.info." + str);
        kf.a(5, d, "isOldIndexFilePresent: for " + str + fileStreamPath.exists());
        return fileStreamPath.exists();
    }

    private synchronized List<String> j(String str) {
        ArrayList arrayList;
        DataInputStream dataInputStream;
        Throwable th;
        int readUnsignedShort;
        ArrayList arrayList2 = null;
        synchronized (this) {
            lr.b();
            kf.a(5, d, "Reading Index File for " + str + " file name:" + jr.a().a.getFileStreamPath(".FlurrySenderIndex.info." + str));
            File fileStreamPath = jr.a().a.getFileStreamPath(".FlurrySenderIndex.info." + str);
            if (fileStreamPath.exists()) {
                kf.a(5, d, "Reading Index File for " + str + " Found file.");
                try {
                    dataInputStream = new DataInputStream(new FileInputStream(fileStreamPath));
                    try {
                        try {
                            readUnsignedShort = dataInputStream.readUnsignedShort();
                        } catch (Throwable th2) {
                            th = th2;
                            lr.a((Closeable) dataInputStream);
                            throw th;
                        }
                    } catch (Throwable th3) {
                        arrayList = null;
                        th = th3;
                    }
                } catch (Throwable th4) {
                    th = th4;
                    dataInputStream = null;
                    lr.a((Closeable) dataInputStream);
                    throw th;
                }
                if (readUnsignedShort == 0) {
                    lr.a((Closeable) dataInputStream);
                } else {
                    arrayList = new ArrayList(readUnsignedShort);
                    for (int i = 0; i < readUnsignedShort; i++) {
                        try {
                            int readUnsignedShort2 = dataInputStream.readUnsignedShort();
                            kf.a(4, d, "read iter " + i + " dataLength = " + readUnsignedShort2);
                            byte[] bArr = new byte[readUnsignedShort2];
                            dataInputStream.readFully(bArr);
                            arrayList.add(new String(bArr));
                        } catch (Throwable th5) {
                            th = th5;
                            kf.a(6, d, "Error when loading persistent file", th);
                            lr.a((Closeable) dataInputStream);
                            arrayList2 = arrayList;
                            return arrayList2;
                        }
                    }
                    dataInputStream.readUnsignedShort();
                    lr.a((Closeable) dataInputStream);
                }
            } else {
                kf.a(5, d, "Agent cache file doesn't exist.");
                arrayList = null;
            }
            arrayList2 = arrayList;
        }
        return arrayList2;
    }

    private static byte[] k(String str) {
        DataInputStream dataInputStream;
        Throwable th;
        byte[] bArr = null;
        lr.b();
        kf.a(5, d, "Reading block File for " + str + " file name:" + jr.a().a.getFileStreamPath(".flurrydatasenderblock." + str));
        File fileStreamPath = jr.a().a.getFileStreamPath(".flurrydatasenderblock." + str);
        if (fileStreamPath.exists()) {
            kf.a(5, d, "Reading Index File for " + str + " Found file.");
            try {
                dataInputStream = new DataInputStream(new FileInputStream(fileStreamPath));
            } catch (Throwable th2) {
                th = th2;
                dataInputStream = null;
            }
            try {
                try {
                    int readUnsignedShort = dataInputStream.readUnsignedShort();
                    if (readUnsignedShort == 0) {
                        lr.a((Closeable) dataInputStream);
                    } else {
                        bArr = new byte[readUnsignedShort];
                        dataInputStream.readFully(bArr);
                        dataInputStream.readUnsignedShort();
                        lr.a((Closeable) dataInputStream);
                    }
                } catch (Throwable th3) {
                    th = th3;
                    lr.a((Closeable) dataInputStream);
                    throw th;
                }
            } catch (Throwable th4) {
                th = th4;
                kf.a(6, d, "Error when loading persistent file", th);
                lr.a((Closeable) dataInputStream);
                return bArr;
            }
        } else {
            kf.a(4, d, "Agent cache file doesn't exist.");
        }
        return bArr;
    }
}
