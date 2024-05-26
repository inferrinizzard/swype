package com.google.android.gms.ads.internal.client;

import com.google.android.gms.ads.internal.client.zzy;
import com.google.android.gms.internal.zzin;
import java.util.Random;

@zzin
/* loaded from: classes.dex */
public class zzn extends zzy.zza {
    private Object zzail = new Object();
    private final Random zzavp = new Random();
    private long zzavq;

    public zzn() {
        zziy();
    }

    @Override // com.google.android.gms.ads.internal.client.zzy
    public long getValue() {
        return this.zzavq;
    }

    public void zziy() {
        synchronized (this.zzail) {
            int i = 3;
            long j = 0;
            while (true) {
                i--;
                if (i <= 0) {
                    break;
                }
                j = this.zzavp.nextInt() + 2147483648L;
                if (j != this.zzavq && j != 0) {
                    break;
                }
            }
            this.zzavq = j;
        }
    }
}
