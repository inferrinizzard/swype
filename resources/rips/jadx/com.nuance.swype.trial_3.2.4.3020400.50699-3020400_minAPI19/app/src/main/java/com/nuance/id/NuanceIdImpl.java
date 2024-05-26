package com.nuance.id;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* loaded from: classes.dex */
public final class NuanceIdImpl {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static String generateHash(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            if (str != null) {
                try {
                    messageDigest.update(str.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                }
            }
            messageDigest.update(strToBytes("2beebf614f0f4f6096051804940a8d6e"));
            String bytesToStr = bytesToStr(messageDigest.digest());
            StringBuilder sb = new StringBuilder();
            sb.append((CharSequence) bytesToStr, 0, 4).append(Integer.toHexString(0)).append((CharSequence) bytesToStr, 4, bytesToStr.length());
            return sb.toString();
        } catch (NoSuchAlgorithmException e2) {
            return "00000000000000000000000000000000000000000000000000000000000000000";
        }
    }

    public static String sha1hash(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            if (str != null) {
                try {
                    messageDigest.update(str.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                }
            }
            return bytesToStr(messageDigest.digest());
        } catch (NoSuchAlgorithmException e2) {
            return "00000000000000000000000000000000000000000000000000000000000000000";
        }
    }

    private static byte[] strToBytes(String str) {
        byte[] bArr = new byte[str.length() / 2];
        for (int i = 0; i < str.length(); i += 2) {
            try {
                bArr[i / 2] = (byte) (Integer.parseInt(str.substring(i, i + 2), 16) & 255);
            } catch (NumberFormatException e) {
            }
        }
        return bArr;
    }

    private static String bytesToStr(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            int i = b & 255;
            String hexString = Integer.toHexString(i);
            if (i < 16) {
                hexString = "0" + hexString;
            }
            sb.append(hexString);
        }
        return sb.toString();
    }
}
