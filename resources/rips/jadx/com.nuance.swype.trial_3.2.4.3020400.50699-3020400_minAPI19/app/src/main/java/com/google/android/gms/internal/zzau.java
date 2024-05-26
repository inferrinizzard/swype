package com.google.android.gms.internal;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes.dex */
public final class zzau {
    private static Cipher zzagb = null;
    private static final Object zzagc = new Object();
    private static final Object zzagd = new Object();
    private final SecureRandom zzaga = null;

    /* loaded from: classes.dex */
    public class zza extends Exception {
        public zza() {
        }

        public zza(Throwable th) {
            super(th);
        }
    }

    private static Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher;
        synchronized (zzagd) {
            if (zzagb == null) {
                zzagb = Cipher.getInstance("AES/CBC/PKCS5Padding");
            }
            cipher = zzagb;
        }
        return cipher;
    }

    public final byte[] zzc(byte[] bArr, String str) throws zza {
        byte[] doFinal;
        if (bArr.length != 16) {
            throw new zza();
        }
        try {
            byte[] zza2 = zzaj.zza(str, false);
            if (zza2.length <= 16) {
                throw new zza();
            }
            ByteBuffer allocate = ByteBuffer.allocate(zza2.length);
            allocate.put(zza2);
            allocate.flip();
            byte[] bArr2 = new byte[16];
            byte[] bArr3 = new byte[zza2.length - 16];
            allocate.get(bArr2);
            allocate.get(bArr3);
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "AES");
            synchronized (zzagc) {
                getCipher().init(2, secretKeySpec, new IvParameterSpec(bArr2));
                doFinal = getCipher().doFinal(bArr3);
            }
            return doFinal;
        } catch (IllegalArgumentException e) {
            throw new zza(e);
        } catch (InvalidAlgorithmParameterException e2) {
            throw new zza(e2);
        } catch (InvalidKeyException e3) {
            throw new zza(e3);
        } catch (NoSuchAlgorithmException e4) {
            throw new zza(e4);
        } catch (BadPaddingException e5) {
            throw new zza(e5);
        } catch (IllegalBlockSizeException e6) {
            throw new zza(e6);
        } catch (NoSuchPaddingException e7) {
            throw new zza(e7);
        }
    }

    public final String zzd(byte[] bArr, byte[] bArr2) throws zza {
        byte[] doFinal;
        byte[] iv;
        if (bArr.length != 16) {
            throw new zza();
        }
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "AES");
            synchronized (zzagc) {
                getCipher().init(1, secretKeySpec, this.zzaga);
                doFinal = getCipher().doFinal(bArr2);
                iv = getCipher().getIV();
            }
            int length = doFinal.length + iv.length;
            ByteBuffer allocate = ByteBuffer.allocate(length);
            allocate.put(iv).put(doFinal);
            allocate.flip();
            byte[] bArr3 = new byte[length];
            allocate.get(bArr3);
            return zzaj.zza(bArr3, false);
        } catch (InvalidKeyException e) {
            throw new zza(e);
        } catch (NoSuchAlgorithmException e2) {
            throw new zza(e2);
        } catch (BadPaddingException e3) {
            throw new zza(e3);
        } catch (IllegalBlockSizeException e4) {
            throw new zza(e4);
        } catch (NoSuchPaddingException e5) {
            throw new zza(e5);
        }
    }

    public final byte[] zzl(String str) throws zza {
        try {
            byte[] zza2 = zzaj.zza(str, false);
            if (zza2.length != 32) {
                throw new zza();
            }
            byte[] bArr = new byte[16];
            ByteBuffer.wrap(zza2, 4, 16).get(bArr);
            for (int i = 0; i < 16; i++) {
                bArr[i] = (byte) (bArr[i] ^ 68);
            }
            return bArr;
        } catch (IllegalArgumentException e) {
            throw new zza(e);
        }
    }
}
