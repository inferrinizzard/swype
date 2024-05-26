package com.google.android.gms.internal;

import com.google.android.gms.internal.zzae;
import com.nuance.swype.input.R;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class zzak {
    static boolean zzxe = false;
    private static MessageDigest zzxf = null;
    private static final Object zzxg = new Object();
    private static final Object zzxh = new Object();
    static CountDownLatch zzxi = new CountDownLatch(1);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class zza implements Runnable {
        private zza() {
        }

        /* synthetic */ zza(byte b) {
            this();
        }

        @Override // java.lang.Runnable
        public final void run() {
            try {
                MessageDigest unused = zzak.zzxf = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
            } finally {
                zzak.zzxi.countDown();
            }
        }
    }

    private static Vector<byte[]> zza$113108e5(byte[] bArr) {
        if (bArr == null || bArr.length <= 0) {
            return null;
        }
        int length = ((bArr.length + 255) - 1) / 255;
        Vector<byte[]> vector = new Vector<>();
        for (int i = 0; i < length; i++) {
            int i2 = i * 255;
            try {
                vector.add(Arrays.copyOfRange(bArr, i2, bArr.length - i2 > 255 ? i2 + 255 : bArr.length));
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }
        return vector;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String zza$166451eb(String str, String str2) {
        byte[] zzb$37a54452 = zzb$37a54452(str, str2);
        return zzb$37a54452 != null ? zzaj.zza(zzb$37a54452, true) : Integer.toString(7);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zzar() {
        synchronized (zzxh) {
            if (!zzxe) {
                zzxe = true;
                new Thread(new zza((byte) 0)).start();
            }
        }
    }

    private static MessageDigest zzas() {
        zzar();
        boolean z = false;
        try {
            z = zzxi.await(2L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
        if (z && zzxf != null) {
            return zzxf;
        }
        return null;
    }

    private static byte[] zzb$37a54452(String str, String str2) {
        zzae.zzc zzcVar = new zzae.zzc();
        try {
            zzcVar.zzer = str.length() < 3 ? str.getBytes("ISO-8859-1") : zzaj.zza(str, true);
            zzcVar.zzes = str2.length() < 3 ? str2.getBytes("ISO-8859-1") : zzaj.zza(str2, true);
            return zzapv.zzf(zzcVar);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    private static zzae.zza zzb$5a50df73() {
        zzae.zza zzaVar = new zzae.zza();
        zzaVar.zzdl = 4096L;
        return zzaVar;
    }

    public static byte[] zzg(byte[] bArr) throws NoSuchAlgorithmException {
        byte[] digest;
        synchronized (zzxg) {
            MessageDigest zzas = zzas();
            if (zzas == null) {
                throw new NoSuchAlgorithmException("Cannot compute hash");
            }
            zzas.reset();
            zzas.update(bArr);
            digest = zzxf.digest();
        }
        return digest;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String zza(zzae.zza zzaVar, String str, boolean z) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] zzb;
        byte[] zzf = zzapv.zzf(zzaVar);
        if (z) {
            zzb = zzb(zzf, str, true);
        } else {
            Vector<byte[]> zza$113108e5 = zza$113108e5(zzf);
            if (zza$113108e5 != null && zza$113108e5.size() != 0) {
                zzae.zzf zzfVar = new zzae.zzf();
                zzfVar.zzey = new byte[zza$113108e5.size()];
                Iterator<byte[]> it = zza$113108e5.iterator();
                int i = 0;
                while (it.hasNext()) {
                    zzfVar.zzey[i] = zzb(it.next(), str, false);
                    i++;
                }
                zzfVar.zzet = zzg(zzf);
                zzb = zzapv.zzf(zzfVar);
            } else {
                zzb = zzb(zzapv.zzf(zzb$5a50df73()), str, true);
            }
        }
        return zzaj.zza(zzb, true);
    }

    private static byte[] zzb(byte[] bArr, String str, boolean z) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] array;
        int i = z ? R.styleable.ThemeTemplate_listSelectorBackgroundPressed : 255;
        if (bArr.length > i) {
            bArr = zzapv.zzf(zzb$5a50df73());
        }
        if (bArr.length < i) {
            byte[] bArr2 = new byte[i - bArr.length];
            new SecureRandom().nextBytes(bArr2);
            array = ByteBuffer.allocate(i + 1).put((byte) bArr.length).put(bArr).put(bArr2).array();
        } else {
            array = ByteBuffer.allocate(i + 1).put((byte) bArr.length).put(bArr).array();
        }
        if (z) {
            array = ByteBuffer.allocate(256).put(zzg(array)).put(array).array();
        }
        byte[] bArr3 = new byte[256];
        new zzal().zzb(array, bArr3);
        if (str != null && str.length() > 0) {
            if (str.length() > 32) {
                str = str.substring(0, 32);
            }
            new zzaoq(str.getBytes("UTF-8")).zzax(bArr3);
        }
        return bArr3;
    }
}
