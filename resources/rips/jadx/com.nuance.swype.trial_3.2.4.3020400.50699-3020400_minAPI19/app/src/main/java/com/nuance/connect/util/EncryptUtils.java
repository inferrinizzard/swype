package com.nuance.connect.util;

import android.content.Context;
import android.provider.Settings;
import android.util.Base64;
import com.nuance.connect.util.Logger;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes.dex */
public class EncryptUtils {
    private static final String DATABASE_PREFIX = "A";
    private static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, "EncryptUtils");
    private static final Charset ENCODING = Charset.forName("UTF-8");
    private static final int[] dbSecretKeyLock = new int[0];
    private static byte[] databaseSecretKey = null;
    private static String defaultSecretKey = null;
    private static final IvParameterSpec IV_PARAMETER_SPEC = new IvParameterSpec(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});

    public static byte[] asByteArray(UUID uuid) {
        long mostSignificantBits = uuid.getMostSignificantBits();
        long leastSignificantBits = uuid.getLeastSignificantBits();
        byte[] bArr = new byte[16];
        for (int i = 0; i < 8; i++) {
            bArr[i] = (byte) (mostSignificantBits >>> ((7 - i) * 8));
        }
        for (int i2 = 8; i2 < 16; i2++) {
            bArr[i2] = (byte) (leastSignificantBits >>> ((7 - i2) * 8));
        }
        return bArr;
    }

    public static final String base64ToString(String str) {
        return bytesToString(Base64.decode(str, 0));
    }

    public static String bytesToString(byte[] bArr) {
        byte[] bArr2 = new byte[bArr.length + 1];
        bArr2[0] = 1;
        System.arraycopy(bArr, 0, bArr2, 1, bArr.length);
        return new BigInteger(bArr2).toString(36);
    }

    public static final String databaseDecryptString(Context context, String str) {
        byte[] secretDecrypt;
        if (str == null || !str.startsWith(DATABASE_PREFIX) || (secretDecrypt = secretDecrypt(stringToBytes(str.substring(1)), databaseSecretKey(context))) == null) {
            return null;
        }
        return new String(secretDecrypt, ENCODING);
    }

    public static final String databaseEncryptString(Context context, String str) {
        if (str == null) {
            return null;
        }
        return DATABASE_PREFIX + bytesToString(secretEncrypt(str, databaseSecretKey(context)));
    }

    public static final byte[] databaseSecretKey(Context context) {
        synchronized (dbSecretKeyLock) {
            if (databaseSecretKey == null) {
                databaseSecretKey = defaultSecretKey(context).getBytes(ENCODING);
            }
        }
        return databaseSecretKey;
    }

    public static final String decodeString(String str) {
        return new String(Base64.decode(str, 0), ENCODING);
    }

    public static final String decodeString(String str, int i) {
        return new String(Base64.decode(str, i), ENCODING);
    }

    private static byte[] decryptAESKey(byte[] bArr, String str, String str2) {
        try {
            PublicKey generatePublic = KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(new BigInteger(str, 16), new BigInteger(str2, 16)));
            Cipher cipher = Cipher.getInstance("RSA/ECB/Pkcs1Padding");
            cipher.init(2, generatePublic);
            return cipher.doFinal(bArr, 0, 128);
        } catch (IllegalArgumentException e) {
            log.d("decrypt() Exception: ", e.getMessage());
            return null;
        } catch (InvalidKeyException e2) {
            log.d("decryptAESKey() Exception: ", e2.getMessage());
            return null;
        } catch (NoSuchAlgorithmException e3) {
            log.d("decryptAESKey() Exception: ", e3.getMessage());
            return null;
        } catch (InvalidKeySpecException e4) {
            log.d("decryptAESKey() Exception: ", e4.getMessage());
            return null;
        } catch (BadPaddingException e5) {
            log.d("decryptAESKey() Exception: ", e5.getMessage());
            return null;
        } catch (IllegalBlockSizeException e6) {
            log.d("decryptAESKey() Exception: ", e6.getMessage());
            return null;
        } catch (NoSuchPaddingException e7) {
            log.d("decryptAESKey() Exception: ", e7.getMessage());
            return null;
        }
    }

    public static final String decryptString(String str, String str2) {
        return decryptString(str, str2, 0);
    }

    public static final String decryptString(String str, String str2, int i) {
        return decryptString(str, str2.getBytes(ENCODING), i);
    }

    public static final String decryptString(String str, byte[] bArr, int i) {
        if (str != null) {
            try {
                byte[] secretDecrypt = secretDecrypt(Base64.decode(str, i), bArr);
                if (secretDecrypt != null) {
                    return new String(secretDecrypt, ENCODING);
                }
            } catch (IllegalArgumentException e) {
            }
        }
        return null;
    }

    public static final String decryptStringBase64(String str, String str2, int i) {
        return decryptString(str, Base64.decode(str2, i), i);
    }

    public static final String defaultSecretKey(Context context) {
        synchronized (dbSecretKeyLock) {
            if (defaultSecretKey == null) {
                String string = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), "android_id");
                defaultSecretKey = string;
                if (string == null) {
                    log.e("Could not create the default security key");
                    throw new RuntimeException("Could not create the default security key");
                }
                try {
                    String encodeByteArray = encodeByteArray(MessageDigest.getInstance("SHA-1").digest(defaultSecretKey.getBytes(ENCODING)));
                    defaultSecretKey = encodeByteArray;
                    if (encodeByteArray.length() > 16) {
                        defaultSecretKey = defaultSecretKey.substring(0, 16);
                    } else {
                        defaultSecretKey += "0000000000000000".substring(defaultSecretKey.length());
                    }
                } catch (NoSuchAlgorithmException e) {
                    log.e("Could not generate the default security key");
                    throw new RuntimeException("Could not generate the default security key");
                }
            }
        }
        return defaultSecretKey;
    }

    public static final String encodeByteArray(byte[] bArr) {
        return encodeByteArray(bArr, 0);
    }

    public static final String encodeByteArray(byte[] bArr, int i) {
        return Base64.encodeToString(bArr, i);
    }

    public static final String encodeString(String str) {
        return encodeString(str, 0);
    }

    public static final String encodeString(String str, int i) {
        return Base64.encodeToString(str.getBytes(ENCODING), i);
    }

    public static final String encryptString(String str, String str2) {
        return encryptString(str, str2, 0);
    }

    public static final String encryptString(String str, String str2, int i) {
        byte[] secretEncrypt;
        if (str == null || (secretEncrypt = secretEncrypt(str, str2)) == null) {
            return null;
        }
        return Base64.encodeToString(secretEncrypt, i);
    }

    public static final String encryptStringBase64(String str, String str2, int i) {
        if (str != null) {
            return Base64.encodeToString(secretEncrypt(str, Base64.decode(str2, 0)), i);
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:35:0x0030 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static byte[] getBytesOfFile(java.lang.String r5) {
        /*
            r1 = 0
            java.io.File r0 = new java.io.File     // Catch: java.io.IOException -> L1c java.lang.Throwable -> L2d
            r0.<init>(r5)     // Catch: java.io.IOException -> L1c java.lang.Throwable -> L2d
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch: java.io.IOException -> L1c java.lang.Throwable -> L2d
            r2.<init>(r0)     // Catch: java.io.IOException -> L1c java.lang.Throwable -> L2d
            int r0 = r2.available()     // Catch: java.lang.Throwable -> L40 java.io.IOException -> L48
            if (r0 <= 0) goto L3a
            byte[] r0 = new byte[r0]     // Catch: java.lang.Throwable -> L40 java.io.IOException -> L48
            r2.read(r0)     // Catch: java.lang.Throwable -> L40 java.io.IOException -> L48
            if (r2 == 0) goto L1b
            r2.close()     // Catch: java.io.IOException -> L38
        L1b:
            return r0
        L1c:
            r0 = move-exception
            r0 = r1
        L1e:
            com.nuance.connect.util.Logger$Log r2 = com.nuance.connect.util.EncryptUtils.log     // Catch: java.lang.Throwable -> L43
            java.lang.String r3 = "Could not open the file: "
            r2.d(r3, r5)     // Catch: java.lang.Throwable -> L43
            if (r0 == 0) goto L2b
            r0.close()     // Catch: java.io.IOException -> L36
        L2b:
            r0 = r1
            goto L1b
        L2d:
            r0 = move-exception
        L2e:
            if (r1 == 0) goto L33
            r1.close()     // Catch: java.io.IOException -> L34
        L33:
            throw r0
        L34:
            r1 = move-exception
            goto L33
        L36:
            r0 = move-exception
            goto L2b
        L38:
            r1 = move-exception
            goto L1b
        L3a:
            if (r2 == 0) goto L2b
            r2.close()     // Catch: java.io.IOException -> L36
            goto L2b
        L40:
            r0 = move-exception
            r1 = r2
            goto L2e
        L43:
            r1 = move-exception
            r4 = r1
            r1 = r0
            r0 = r4
            goto L2e
        L48:
            r0 = move-exception
            r0 = r2
            goto L1e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.util.EncryptUtils.getBytesOfFile(java.lang.String):byte[]");
    }

    public static final String hashPassword(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(str.getBytes("iso-8859-1"), 0, str.length());
            return new BigInteger(1, messageDigest.digest()).toString(16);
        } catch (UnsupportedEncodingException | NullPointerException | NumberFormatException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static final byte[] legacyDecrypt(byte[] bArr, String str, String str2) {
        byte[] strToBytes;
        byte[] decryptAESKey = decryptAESKey(bArr, str, str2);
        if (decryptAESKey != null && (strToBytes = strToBytes(new String(decryptAESKey, Charset.forName("UTF-8")))) != null) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(strToBytes, "AES");
            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(2, secretKeySpec);
                return cipher.doFinal(bArr, 128, bArr.length - 128);
            } catch (IllegalArgumentException e) {
                log.d("decrypt() Exception: ", e.getMessage());
            } catch (InvalidKeyException e2) {
                log.d("decrypt() Exception: ", e2.getMessage());
            } catch (NoSuchAlgorithmException e3) {
                log.d("decrypt() Exception: ", e3.getMessage());
            } catch (BadPaddingException e4) {
                log.d("decrypt() Exception: ", e4.getMessage());
            } catch (IllegalBlockSizeException e5) {
                log.d("decrypt() Exception: ", e5.getMessage());
            } catch (NoSuchPaddingException e6) {
                log.d("decrypt() Exception: ", e6.getMessage());
            }
        }
        return null;
    }

    public static final String legacyDecryptString(String str, String str2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(str2.getBytes(ENCODING), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(2, secretKeySpec);
            byte[] doFinal = cipher.doFinal(Base64.decode(str.getBytes(ENCODING), 0));
            if (doFinal != null) {
                return new String(doFinal, ENCODING);
            }
        } catch (Exception e) {
            log.w("Error Unpacking: ", e.getMessage());
        }
        return null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:115:0x0009, code lost:            if (r2 == true) goto L114;     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:80:0x0115 A[Catch: IOException -> 0x0119, TRY_LEAVE, TryCatch #0 {IOException -> 0x0119, blocks: (B:85:0x0110, B:80:0x0115), top: B:84:0x0110 }] */
    /* JADX WARN: Removed duplicated region for block: B:84:0x0110 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r2v16 */
    /* JADX WARN: Type inference failed for: r2v2 */
    /* JADX WARN: Type inference failed for: r2v22 */
    /* JADX WARN: Type inference failed for: r2v23 */
    /* JADX WARN: Type inference failed for: r2v24 */
    /* JADX WARN: Type inference failed for: r2v3, types: [java.io.BufferedInputStream] */
    /* JADX WARN: Type inference failed for: r2v4 */
    /* JADX WARN: Type inference failed for: r2v9 */
    /* JADX WARN: Type inference failed for: r4v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r4v10, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r4v11, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r4v12, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r4v13, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r4v14, types: [java.io.FileInputStream, java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r4v2, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r4v3 */
    /* JADX WARN: Type inference failed for: r4v4 */
    /* JADX WARN: Type inference failed for: r4v5 */
    /* JADX WARN: Type inference failed for: r4v6 */
    /* JADX WARN: Type inference failed for: r4v7 */
    /* JADX WARN: Type inference failed for: r4v8 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.String md5(java.io.File r7) {
        /*
            Method dump skipped, instructions count: 326
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.util.EncryptUtils.md5(java.io.File):java.lang.String");
    }

    public static String md5(byte[] bArr) {
        if (bArr == null) {
            return "";
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(bArr);
            byte[] digest = messageDigest.digest();
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (int i = 0; i < digest.length; i++) {
                if ((digest[i] & 255) < 16) {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(digest[i] & 255));
            }
            return sb.toString();
        } catch (OutOfMemoryError e) {
            log.e("Out of Memory attempting to generate an MD5: ", e.getMessage());
            return "";
        } catch (NoSuchAlgorithmException e2) {
            log.e("Error getting the md5!!");
            return "";
        }
    }

    public static final byte[] secretDecrypt(byte[] bArr, byte[] bArr2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr2, "AES");
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(2, secretKeySpec, IV_PARAMETER_SPEC);
            return cipher.doFinal(bArr);
        } catch (Exception e) {
            log.w("Error Unpacking: ", e.getMessage());
            return null;
        }
    }

    public static final byte[] secretEncrypt(String str, String str2) {
        return secretEncrypt(str, str2.getBytes(ENCODING));
    }

    public static final byte[] secretEncrypt(String str, byte[] bArr) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "AES");
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(1, secretKeySpec, IV_PARAMETER_SPEC);
            return cipher.doFinal(str.getBytes(ENCODING));
        } catch (Exception e) {
            log.e("Error Bundling: ", e.getMessage());
            return null;
        }
    }

    public static String sha1(String str) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA1").digest(str.getBytes(ENCODING));
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : digest) {
                stringBuffer.append(Integer.toString((b & 255) + 256, 16).substring(1));
            }
            return stringBuffer.toString();
        } catch (NullPointerException | NumberFormatException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String shrinkUUID(String str) {
        return encodeByteArray(asByteArray(UUID.fromString(str)), 9).trim();
    }

    public static byte[] strToBytes(String str) {
        byte[] bArr = new byte[str.length() / 2];
        for (int i = 0; i < str.length(); i += 2) {
            try {
                bArr[i / 2] = (byte) (Integer.parseInt(str.substring(i, i + 2), 16) & 255);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return bArr;
    }

    public static byte[] stringToBytes(String str) {
        byte[] byteArray = new BigInteger(str, 36).toByteArray();
        return Arrays.copyOfRange(byteArray, 1, byteArray.length);
    }
}
