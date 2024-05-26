package com.google.android.gms.internal;

import java.io.IOException;

/* loaded from: classes.dex */
public final class zzapn {
    private int bjo;
    private int bjp;
    private int bjr;
    private int bjt;
    final byte[] buffer;
    private int bjs = Integer.MAX_VALUE;
    private int bju = 64;
    private int bjv = 67108864;
    int bjn = 0;
    private int bjq = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public zzapn(byte[] bArr, int i) {
        this.buffer = bArr;
        this.bjo = i + 0;
    }

    private void au() {
        this.bjo += this.bjp;
        int i = this.bjo;
        if (i <= this.bjs) {
            this.bjp = 0;
        } else {
            this.bjp = i - this.bjs;
            this.bjo -= this.bjp;
        }
    }

    private byte ax() throws IOException {
        if (this.bjq == this.bjo) {
            throw zzapu.aE();
        }
        byte[] bArr = this.buffer;
        int i = this.bjq;
        this.bjq = i + 1;
        return bArr[i];
    }

    private void zzafu(int i) throws IOException {
        if (i < 0) {
            throw zzapu.aF();
        }
        if (this.bjq + i > this.bjs) {
            zzafu(this.bjs - this.bjq);
            throw zzapu.aE();
        }
        if (i > this.bjo - this.bjq) {
            throw zzapu.aE();
        }
        this.bjq += i;
    }

    public final boolean an() throws IOException {
        return aq() != 0;
    }

    public final int aq() throws IOException {
        byte ax = ax();
        if (ax >= 0) {
            return ax;
        }
        int i = ax & Byte.MAX_VALUE;
        byte ax2 = ax();
        if (ax2 >= 0) {
            return i | (ax2 << 7);
        }
        int i2 = i | ((ax2 & Byte.MAX_VALUE) << 7);
        byte ax3 = ax();
        if (ax3 >= 0) {
            return i2 | (ax3 << 14);
        }
        int i3 = i2 | ((ax3 & Byte.MAX_VALUE) << 14);
        byte ax4 = ax();
        if (ax4 >= 0) {
            return i3 | (ax4 << 21);
        }
        int i4 = i3 | ((ax4 & Byte.MAX_VALUE) << 21);
        byte ax5 = ax();
        int i5 = i4 | (ax5 << 28);
        if (ax5 >= 0) {
            return i5;
        }
        for (int i6 = 0; i6 < 5; i6++) {
            if (ax() >= 0) {
                return i5;
            }
        }
        throw zzapu.aG();
    }

    public final long ar() throws IOException {
        long j = 0;
        for (int i = 0; i < 64; i += 7) {
            j |= (r3 & Byte.MAX_VALUE) << i;
            if ((ax() & 128) == 0) {
                return j;
            }
        }
        throw zzapu.aG();
    }

    public final int av() {
        if (this.bjs == Integer.MAX_VALUE) {
            return -1;
        }
        return this.bjs - this.bjq;
    }

    public final int getPosition() {
        return this.bjq - this.bjn;
    }

    public final byte[] readBytes() throws IOException {
        int aq = aq();
        if (aq < 0) {
            throw zzapu.aF();
        }
        if (aq == 0) {
            return zzapy.bjO;
        }
        if (aq > this.bjo - this.bjq) {
            throw zzapu.aE();
        }
        byte[] bArr = new byte[aq];
        System.arraycopy(this.buffer, this.bjq, bArr, 0, aq);
        this.bjq = aq + this.bjq;
        return bArr;
    }

    public final String readString() throws IOException {
        int aq = aq();
        if (aq < 0) {
            throw zzapu.aF();
        }
        if (aq > this.bjo - this.bjq) {
            throw zzapu.aE();
        }
        String str = new String(this.buffer, this.bjq, aq, zzapt.UTF_8);
        this.bjq = aq + this.bjq;
        return str;
    }

    public final void zza(zzapv zzapvVar) throws IOException {
        int aq = aq();
        if (this.bjt >= this.bju) {
            throw zzapu.aK();
        }
        int zzafr = zzafr(aq);
        this.bjt++;
        zzapvVar.zzb(this);
        zzafo(0);
        this.bjt--;
        zzafs(zzafr);
    }

    public final void zzafo(int i) throws zzapu {
        if (this.bjr != i) {
            throw zzapu.aI();
        }
    }

    public final int zzafr(int i) throws zzapu {
        if (i < 0) {
            throw zzapu.aF();
        }
        int i2 = this.bjq + i;
        int i3 = this.bjs;
        if (i2 > i3) {
            throw zzapu.aE();
        }
        this.bjs = i2;
        au();
        return i3;
    }

    public final void zzafs(int i) {
        this.bjs = i;
        au();
    }

    public final void zzaft(int i) {
        if (i > this.bjq - this.bjn) {
            throw new IllegalArgumentException(new StringBuilder(50).append("Position ").append(i).append(" is beyond current ").append(this.bjq - this.bjn).toString());
        }
        if (i < 0) {
            throw new IllegalArgumentException(new StringBuilder(24).append("Bad position ").append(i).toString());
        }
        this.bjq = this.bjn + i;
    }

    public final int ah() throws IOException {
        if (this.bjq == this.bjo) {
            this.bjr = 0;
            return 0;
        }
        this.bjr = aq();
        if (this.bjr == 0) {
            throw zzapu.aH();
        }
        return this.bjr;
    }

    public final boolean zzafp(int i) throws IOException {
        int ah;
        switch (zzapy.zzagi(i)) {
            case 0:
                aq();
                return true;
            case 1:
                ax();
                ax();
                ax();
                ax();
                ax();
                ax();
                ax();
                ax();
                return true;
            case 2:
                zzafu(aq());
                return true;
            case 3:
                break;
            case 4:
                return false;
            case 5:
                ax();
                ax();
                ax();
                ax();
                return true;
            default:
                throw zzapu.aJ();
        }
        do {
            ah = ah();
            if (ah != 0) {
            }
            zzafo(zzapy.zzaj(zzapy.zzagj(i), 4));
            return true;
        } while (zzafp(ah));
        zzafo(zzapy.zzaj(zzapy.zzagj(i), 4));
        return true;
    }
}
