package com.google.android.gms.internal;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@zzin
/* loaded from: classes.dex */
public abstract class zzcp {
    private static MessageDigest zzasz = null;
    protected Object zzail = new Object();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract byte[] zzaa(String str);

    /* JADX INFO: Access modifiers changed from: protected */
    public final MessageDigest zzie() {
        MessageDigest messageDigest;
        synchronized (this.zzail) {
            if (zzasz != null) {
                messageDigest = zzasz;
            } else {
                for (int i = 0; i < 2; i++) {
                    try {
                        zzasz = MessageDigest.getInstance("MD5");
                    } catch (NoSuchAlgorithmException e) {
                    }
                }
                messageDigest = zzasz;
            }
        }
        return messageDigest;
    }
}
