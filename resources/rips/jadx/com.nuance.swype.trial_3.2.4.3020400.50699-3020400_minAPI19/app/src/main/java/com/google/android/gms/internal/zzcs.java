package com.google.android.gms.internal;

import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;

@zzin
/* loaded from: classes.dex */
public final class zzcs extends zzcp {
    private MessageDigest zzath;

    @Override // com.google.android.gms.internal.zzcp
    public final byte[] zzaa(String str) {
        byte[] bArr;
        byte[] bArr2;
        String[] split = str.split(XMLResultsHandler.SEP_SPACE);
        if (split.length == 1) {
            int zzac = zzcr.zzac(split[0]);
            ByteBuffer allocate = ByteBuffer.allocate(4);
            allocate.order(ByteOrder.LITTLE_ENDIAN);
            allocate.putInt(zzac);
            bArr = allocate.array();
        } else if (split.length < 5) {
            byte[] bArr3 = new byte[split.length * 2];
            for (int i = 0; i < split.length; i++) {
                int zzac2 = zzcr.zzac(split[i]);
                int i2 = ((zzac2 & (-65536)) >> 16) ^ (65535 & zzac2);
                byte[] bArr4 = {(byte) i2, (byte) (i2 >> 8)};
                bArr3[i * 2] = bArr4[0];
                bArr3[(i * 2) + 1] = bArr4[1];
            }
            bArr = bArr3;
        } else {
            bArr = new byte[split.length];
            for (int i3 = 0; i3 < split.length; i3++) {
                int zzac3 = zzcr.zzac(split[i3]);
                bArr[i3] = (byte) (((zzac3 & (-16777216)) >> 24) ^ (((zzac3 & 255) ^ ((65280 & zzac3) >> 8)) ^ ((16711680 & zzac3) >> 16)));
            }
        }
        this.zzath = zzie();
        synchronized (this.zzail) {
            if (this.zzath == null) {
                bArr2 = new byte[0];
            } else {
                this.zzath.reset();
                this.zzath.update(bArr);
                byte[] digest = this.zzath.digest();
                bArr2 = new byte[digest.length > 4 ? 4 : digest.length];
                System.arraycopy(digest, 0, bArr2, 0, bArr2.length);
            }
        }
        return bArr2;
    }
}
