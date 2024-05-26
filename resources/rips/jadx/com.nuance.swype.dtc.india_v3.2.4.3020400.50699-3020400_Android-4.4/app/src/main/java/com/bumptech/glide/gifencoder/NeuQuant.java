package com.bumptech.glide.gifencoder;

import com.nuance.swype.input.hardkey.HardKeyboardManager;

/* loaded from: classes.dex */
final class NeuQuant {
    protected int alphadec;
    protected int lengthcount;
    protected int samplefac;
    protected byte[] thepicture;
    protected int[] netindex = new int[256];
    protected int[] bias = new int[256];
    protected int[] freq = new int[256];
    protected int[] radpower = new int[32];
    protected int[][] network = new int[256];

    public NeuQuant(byte[] thepic, int len, int sample) {
        this.thepicture = thepic;
        this.lengthcount = len;
        this.samplefac = sample;
        for (int i = 0; i < 256; i++) {
            this.network[i] = new int[4];
            int[] p = this.network[i];
            int i2 = (i << 12) / 256;
            p[2] = i2;
            p[1] = i2;
            p[0] = i2;
            this.freq[i] = 256;
            this.bias[i] = 0;
        }
    }

    public final byte[] colorMap() {
        byte[] map = new byte[768];
        int[] index = new int[256];
        for (int i = 0; i < 256; i++) {
            index[this.network[i][3]] = i;
        }
        int i2 = 0;
        int k = 0;
        while (i2 < 256) {
            int j = index[i2];
            int k2 = k + 1;
            map[k] = (byte) this.network[j][0];
            int k3 = k2 + 1;
            map[k2] = (byte) this.network[j][1];
            map[k3] = (byte) this.network[j][2];
            i2++;
            k = k3 + 1;
        }
        return map;
    }

    public final void inxbuild() {
        int previouscol = 0;
        int startpos = 0;
        for (int i = 0; i < 256; i++) {
            int[] p = this.network[i];
            int smallpos = i;
            int smallval = p[1];
            for (int j = i + 1; j < 256; j++) {
                int[] q = this.network[j];
                if (q[1] < smallval) {
                    smallpos = j;
                    smallval = q[1];
                }
            }
            int[] q2 = this.network[smallpos];
            if (i != smallpos) {
                int j2 = q2[0];
                q2[0] = p[0];
                p[0] = j2;
                int j3 = q2[1];
                q2[1] = p[1];
                p[1] = j3;
                int j4 = q2[2];
                q2[2] = p[2];
                p[2] = j4;
                int j5 = q2[3];
                q2[3] = p[3];
                p[3] = j5;
            }
            if (smallval != previouscol) {
                this.netindex[previouscol] = (startpos + i) >> 1;
                for (int j6 = previouscol + 1; j6 < smallval; j6++) {
                    this.netindex[j6] = i;
                }
                previouscol = smallval;
                startpos = i;
            }
        }
        this.netindex[previouscol] = (startpos + 255) >> 1;
        for (int j7 = previouscol + 1; j7 < 256; j7++) {
            this.netindex[j7] = 255;
        }
    }

    public final void learn() {
        int step;
        int i;
        int i2;
        if (this.lengthcount < 1509) {
            this.samplefac = 1;
        }
        this.alphadec = ((this.samplefac - 1) / 3) + 30;
        byte[] p = this.thepicture;
        int pix = 0;
        int lim = this.lengthcount;
        int samplepixels = this.lengthcount / (this.samplefac * 3);
        int delta = samplepixels / 100;
        int alpha = 1024;
        int radius = HardKeyboardManager.META_SELECTING;
        int rad = 32;
        for (int i3 = 0; i3 < 32; i3++) {
            this.radpower[i3] = (((1024 - (i3 * i3)) * 256) / 1024) * 1024;
        }
        if (this.lengthcount < 1509) {
            step = 3;
        } else if (this.lengthcount % 499 != 0) {
            step = 1497;
        } else if (this.lengthcount % 491 != 0) {
            step = 1473;
        } else if (this.lengthcount % 487 != 0) {
            step = 1461;
        } else {
            step = 1509;
        }
        int i4 = 0;
        while (i4 < samplepixels) {
            int b = (p[pix + 0] & 255) << 4;
            int g = (p[pix + 1] & 255) << 4;
            int r = (p[pix + 2] & 255) << 4;
            int i5 = Integer.MAX_VALUE;
            int i6 = Integer.MAX_VALUE;
            int i7 = -1;
            int j = -1;
            int i8 = 0;
            while (i8 < 256) {
                int[] iArr = this.network[i8];
                int i9 = iArr[0] - b;
                if (i9 < 0) {
                    i9 = -i9;
                }
                int i10 = iArr[1] - g;
                if (i10 < 0) {
                    i10 = -i10;
                }
                int i11 = i10 + i9;
                int i12 = iArr[2] - r;
                if (i12 < 0) {
                    i12 = -i12;
                }
                int i13 = i11 + i12;
                if (i13 < i5) {
                    i = i13;
                    i2 = i8;
                } else {
                    i = i5;
                    i2 = i7;
                }
                int i14 = i13 - (this.bias[i8] >> 12);
                if (i14 < i6) {
                    j = i8;
                } else {
                    i14 = i6;
                }
                int i15 = this.freq[i8] >> 10;
                int[] iArr2 = this.freq;
                iArr2[i8] = iArr2[i8] - i15;
                int[] iArr3 = this.bias;
                iArr3[i8] = (i15 << 10) + iArr3[i8];
                i8++;
                i6 = i14;
                i7 = i2;
                i5 = i;
            }
            int[] iArr4 = this.freq;
            iArr4[i7] = iArr4[i7] + 64;
            int[] iArr5 = this.bias;
            iArr5[i7] = iArr5[i7] - 65536;
            int[] iArr6 = this.network[j];
            iArr6[0] = iArr6[0] - (((iArr6[0] - b) * alpha) / 1024);
            iArr6[1] = iArr6[1] - (((iArr6[1] - g) * alpha) / 1024);
            iArr6[2] = iArr6[2] - (((iArr6[2] - r) * alpha) / 1024);
            if (rad != 0) {
                int i16 = j - rad;
                int i17 = i16 < -1 ? -1 : i16;
                int i18 = j + rad;
                if (i18 > 256) {
                    i18 = 256;
                }
                int i19 = 1;
                int i20 = j + 1;
                int i21 = j - 1;
                while (true) {
                    if (i20 >= i18 && i21 <= i17) {
                        break;
                    }
                    int i22 = i19 + 1;
                    int i23 = this.radpower[i19];
                    if (i20 < i18) {
                        int i24 = i20 + 1;
                        int[] iArr7 = this.network[i20];
                        try {
                            iArr7[0] = iArr7[0] - (((iArr7[0] - b) * i23) / HardKeyboardManager.META_META_RIGHT_ON);
                            iArr7[1] = iArr7[1] - (((iArr7[1] - g) * i23) / HardKeyboardManager.META_META_RIGHT_ON);
                            iArr7[2] = iArr7[2] - (((iArr7[2] - r) * i23) / HardKeyboardManager.META_META_RIGHT_ON);
                            i20 = i24;
                        } catch (Exception e) {
                            i20 = i24;
                        }
                    }
                    if (i21 > i17) {
                        int i25 = i21 - 1;
                        int[] iArr8 = this.network[i21];
                        try {
                            iArr8[0] = iArr8[0] - (((iArr8[0] - b) * i23) / HardKeyboardManager.META_META_RIGHT_ON);
                            iArr8[1] = iArr8[1] - (((iArr8[1] - g) * i23) / HardKeyboardManager.META_META_RIGHT_ON);
                            iArr8[2] = iArr8[2] - ((i23 * (iArr8[2] - r)) / HardKeyboardManager.META_META_RIGHT_ON);
                            i21 = i25;
                            i19 = i22;
                        } catch (Exception e2) {
                            i21 = i25;
                            i19 = i22;
                        }
                    } else {
                        i19 = i22;
                    }
                }
            }
            pix += step;
            if (pix >= lim) {
                pix -= this.lengthcount;
            }
            i4++;
            if (delta == 0) {
                delta = 1;
            }
            if (i4 % delta == 0) {
                alpha -= alpha / this.alphadec;
                radius -= radius / 30;
                rad = radius >> 6;
                if (rad <= 1) {
                    rad = 0;
                }
                for (int j2 = 0; j2 < rad; j2++) {
                    this.radpower[j2] = ((((rad * rad) - (j2 * j2)) * 256) / (rad * rad)) * alpha;
                }
            }
        }
    }

    public final int map(int b, int g, int r) {
        int bestd = 1000;
        int best = -1;
        int i = this.netindex[g];
        int j = i - 1;
        while (true) {
            if (i < 256 || j >= 0) {
                if (i < 256) {
                    int[] p = this.network[i];
                    int dist = p[1] - g;
                    if (dist >= bestd) {
                        i = 256;
                    } else {
                        i++;
                        if (dist < 0) {
                            dist = -dist;
                        }
                        int a = p[0] - b;
                        if (a < 0) {
                            a = -a;
                        }
                        int dist2 = dist + a;
                        if (dist2 < bestd) {
                            int a2 = p[2] - r;
                            if (a2 < 0) {
                                a2 = -a2;
                            }
                            int dist3 = dist2 + a2;
                            if (dist3 < bestd) {
                                bestd = dist3;
                                best = p[3];
                            }
                        }
                    }
                }
                if (j >= 0) {
                    int[] p2 = this.network[j];
                    int dist4 = g - p2[1];
                    if (dist4 >= bestd) {
                        j = -1;
                    } else {
                        j--;
                        if (dist4 < 0) {
                            dist4 = -dist4;
                        }
                        int a3 = p2[0] - b;
                        if (a3 < 0) {
                            a3 = -a3;
                        }
                        int dist5 = dist4 + a3;
                        if (dist5 < bestd) {
                            int a4 = p2[2] - r;
                            if (a4 < 0) {
                                a4 = -a4;
                            }
                            int dist6 = dist5 + a4;
                            if (dist6 < bestd) {
                                bestd = dist6;
                                best = p2[3];
                            }
                        }
                    }
                }
            } else {
                return best;
            }
        }
    }

    public final void unbiasnet() {
        for (int i = 0; i < 256; i++) {
            int[] iArr = this.network[i];
            iArr[0] = iArr[0] >> 4;
            int[] iArr2 = this.network[i];
            iArr2[1] = iArr2[1] >> 4;
            int[] iArr3 = this.network[i];
            iArr3[2] = iArr3[2] >> 4;
            this.network[i][3] = i;
        }
    }
}
