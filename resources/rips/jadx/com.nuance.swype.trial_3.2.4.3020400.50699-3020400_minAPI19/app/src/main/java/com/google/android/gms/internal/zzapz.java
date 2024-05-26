package com.google.android.gms.internal;

import com.nuance.swype.input.R;
import java.io.IOException;
import java.util.Arrays;

/* loaded from: classes.dex */
public interface zzapz {

    /* loaded from: classes.dex */
    public static final class zza extends zzapp<zza> implements Cloneable {
        public String[] bjP = zzapy.bjM;
        public String[] bjQ = zzapy.bjM;
        public int[] bjR = zzapy.bjH;
        public long[] bjS = zzapy.bjI;
        public long[] bjT = zzapy.bjI;

        /* JADX INFO: Access modifiers changed from: private */
        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        /* renamed from: aO, reason: merged with bridge method [inline-methods] */
        public zza clone() {
            try {
                zza zzaVar = (zza) super.clone();
                if (this.bjP != null && this.bjP.length > 0) {
                    zzaVar.bjP = (String[]) this.bjP.clone();
                }
                if (this.bjQ != null && this.bjQ.length > 0) {
                    zzaVar.bjQ = (String[]) this.bjQ.clone();
                }
                if (this.bjR != null && this.bjR.length > 0) {
                    zzaVar.bjR = (int[]) this.bjR.clone();
                }
                if (this.bjS != null && this.bjS.length > 0) {
                    zzaVar.bjS = (long[]) this.bjS.clone();
                }
                if (this.bjT != null && this.bjT.length > 0) {
                    zzaVar.bjT = (long[]) this.bjT.clone();
                }
                return zzaVar;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        @Override // com.google.android.gms.internal.zzapp
        /* renamed from: aA */
        public final /* synthetic */ zza clone() throws CloneNotSupportedException {
            return (zza) clone();
        }

        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        /* renamed from: aB */
        public final /* synthetic */ zzapv clone() throws CloneNotSupportedException {
            return (zza) clone();
        }

        public final boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza zzaVar = (zza) obj;
            if (zzapt.equals(this.bjP, zzaVar.bjP) && zzapt.equals(this.bjQ, zzaVar.bjQ) && zzapt.equals(this.bjR, zzaVar.bjR) && zzapt.equals(this.bjS, zzaVar.bjS) && zzapt.equals(this.bjT, zzaVar.bjT)) {
                return (this.bjx == null || this.bjx.isEmpty()) ? zzaVar.bjx == null || zzaVar.bjx.isEmpty() : this.bjx.equals(zzaVar.bjx);
            }
            return false;
        }

        public final int hashCode() {
            return ((this.bjx == null || this.bjx.isEmpty()) ? 0 : this.bjx.hashCode()) + ((((((((((((getClass().getName().hashCode() + 527) * 31) + zzapt.hashCode(this.bjP)) * 31) + zzapt.hashCode(this.bjQ)) * 31) + zzapt.hashCode(this.bjR)) * 31) + zzapt.hashCode(this.bjS)) * 31) + zzapt.hashCode(this.bjT)) * 31);
        }

        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        public final void zza(zzapo zzapoVar) throws IOException {
            if (this.bjP != null && this.bjP.length > 0) {
                for (int i = 0; i < this.bjP.length; i++) {
                    String str = this.bjP[i];
                    if (str != null) {
                        zzapoVar.zzr(1, str);
                    }
                }
            }
            if (this.bjQ != null && this.bjQ.length > 0) {
                for (int i2 = 0; i2 < this.bjQ.length; i2++) {
                    String str2 = this.bjQ[i2];
                    if (str2 != null) {
                        zzapoVar.zzr(2, str2);
                    }
                }
            }
            if (this.bjR != null && this.bjR.length > 0) {
                for (int i3 = 0; i3 < this.bjR.length; i3++) {
                    zzapoVar.zzae(3, this.bjR[i3]);
                }
            }
            if (this.bjS != null && this.bjS.length > 0) {
                for (int i4 = 0; i4 < this.bjS.length; i4++) {
                    zzapoVar.zzb(4, this.bjS[i4]);
                }
            }
            if (this.bjT != null && this.bjT.length > 0) {
                for (int i5 = 0; i5 < this.bjT.length; i5++) {
                    zzapoVar.zzb(5, this.bjT[i5]);
                }
            }
            super.zza(zzapoVar);
        }

        public zza() {
            this.bjx = null;
            this.bjG = -1;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        public final int zzx() {
            int i;
            int zzx = super.zzx();
            if (this.bjP == null || this.bjP.length <= 0) {
                i = zzx;
            } else {
                int i2 = 0;
                int i3 = 0;
                for (int i4 = 0; i4 < this.bjP.length; i4++) {
                    String str = this.bjP[i4];
                    if (str != null) {
                        i3++;
                        i2 += zzapo.zztx(str);
                    }
                }
                i = zzx + i2 + (i3 * 1);
            }
            if (this.bjQ != null && this.bjQ.length > 0) {
                int i5 = 0;
                int i6 = 0;
                for (int i7 = 0; i7 < this.bjQ.length; i7++) {
                    String str2 = this.bjQ[i7];
                    if (str2 != null) {
                        i6++;
                        i5 += zzapo.zztx(str2);
                    }
                }
                i = i + i5 + (i6 * 1);
            }
            if (this.bjR != null && this.bjR.length > 0) {
                int i8 = 0;
                for (int i9 = 0; i9 < this.bjR.length; i9++) {
                    i8 += zzapo.zzafx(this.bjR[i9]);
                }
                i = i + i8 + (this.bjR.length * 1);
            }
            if (this.bjS != null && this.bjS.length > 0) {
                int i10 = 0;
                for (int i11 = 0; i11 < this.bjS.length; i11++) {
                    i10 += zzapo.zzdc(this.bjS[i11]);
                }
                i = i + i10 + (this.bjS.length * 1);
            }
            if (this.bjT == null || this.bjT.length <= 0) {
                return i;
            }
            int i12 = 0;
            for (int i13 = 0; i13 < this.bjT.length; i13++) {
                i12 += zzapo.zzdc(this.bjT[i13]);
            }
            return i + i12 + (this.bjT.length * 1);
        }

        @Override // com.google.android.gms.internal.zzapv
        public final /* synthetic */ zzapv zzb(zzapn zzapnVar) throws IOException {
            while (true) {
                int ah = zzapnVar.ah();
                switch (ah) {
                    case 0:
                        break;
                    case 10:
                        int zzc = zzapy.zzc(zzapnVar, 10);
                        int length = this.bjP == null ? 0 : this.bjP.length;
                        String[] strArr = new String[zzc + length];
                        if (length != 0) {
                            System.arraycopy(this.bjP, 0, strArr, 0, length);
                        }
                        while (length < strArr.length - 1) {
                            strArr[length] = zzapnVar.readString();
                            zzapnVar.ah();
                            length++;
                        }
                        strArr[length] = zzapnVar.readString();
                        this.bjP = strArr;
                        break;
                    case 18:
                        int zzc2 = zzapy.zzc(zzapnVar, 18);
                        int length2 = this.bjQ == null ? 0 : this.bjQ.length;
                        String[] strArr2 = new String[zzc2 + length2];
                        if (length2 != 0) {
                            System.arraycopy(this.bjQ, 0, strArr2, 0, length2);
                        }
                        while (length2 < strArr2.length - 1) {
                            strArr2[length2] = zzapnVar.readString();
                            zzapnVar.ah();
                            length2++;
                        }
                        strArr2[length2] = zzapnVar.readString();
                        this.bjQ = strArr2;
                        break;
                    case 24:
                        int zzc3 = zzapy.zzc(zzapnVar, 24);
                        int length3 = this.bjR == null ? 0 : this.bjR.length;
                        int[] iArr = new int[zzc3 + length3];
                        if (length3 != 0) {
                            System.arraycopy(this.bjR, 0, iArr, 0, length3);
                        }
                        while (length3 < iArr.length - 1) {
                            iArr[length3] = zzapnVar.aq();
                            zzapnVar.ah();
                            length3++;
                        }
                        iArr[length3] = zzapnVar.aq();
                        this.bjR = iArr;
                        break;
                    case 26:
                        int zzafr = zzapnVar.zzafr(zzapnVar.aq());
                        int position = zzapnVar.getPosition();
                        int i = 0;
                        while (zzapnVar.av() > 0) {
                            zzapnVar.aq();
                            i++;
                        }
                        zzapnVar.zzaft(position);
                        int length4 = this.bjR == null ? 0 : this.bjR.length;
                        int[] iArr2 = new int[i + length4];
                        if (length4 != 0) {
                            System.arraycopy(this.bjR, 0, iArr2, 0, length4);
                        }
                        while (length4 < iArr2.length) {
                            iArr2[length4] = zzapnVar.aq();
                            length4++;
                        }
                        this.bjR = iArr2;
                        zzapnVar.zzafs(zzafr);
                        break;
                    case 32:
                        int zzc4 = zzapy.zzc(zzapnVar, 32);
                        int length5 = this.bjS == null ? 0 : this.bjS.length;
                        long[] jArr = new long[zzc4 + length5];
                        if (length5 != 0) {
                            System.arraycopy(this.bjS, 0, jArr, 0, length5);
                        }
                        while (length5 < jArr.length - 1) {
                            jArr[length5] = zzapnVar.ar();
                            zzapnVar.ah();
                            length5++;
                        }
                        jArr[length5] = zzapnVar.ar();
                        this.bjS = jArr;
                        break;
                    case 34:
                        int zzafr2 = zzapnVar.zzafr(zzapnVar.aq());
                        int position2 = zzapnVar.getPosition();
                        int i2 = 0;
                        while (zzapnVar.av() > 0) {
                            zzapnVar.ar();
                            i2++;
                        }
                        zzapnVar.zzaft(position2);
                        int length6 = this.bjS == null ? 0 : this.bjS.length;
                        long[] jArr2 = new long[i2 + length6];
                        if (length6 != 0) {
                            System.arraycopy(this.bjS, 0, jArr2, 0, length6);
                        }
                        while (length6 < jArr2.length) {
                            jArr2[length6] = zzapnVar.ar();
                            length6++;
                        }
                        this.bjS = jArr2;
                        zzapnVar.zzafs(zzafr2);
                        break;
                    case 40:
                        int zzc5 = zzapy.zzc(zzapnVar, 40);
                        int length7 = this.bjT == null ? 0 : this.bjT.length;
                        long[] jArr3 = new long[zzc5 + length7];
                        if (length7 != 0) {
                            System.arraycopy(this.bjT, 0, jArr3, 0, length7);
                        }
                        while (length7 < jArr3.length - 1) {
                            jArr3[length7] = zzapnVar.ar();
                            zzapnVar.ah();
                            length7++;
                        }
                        jArr3[length7] = zzapnVar.ar();
                        this.bjT = jArr3;
                        break;
                    case 42:
                        int zzafr3 = zzapnVar.zzafr(zzapnVar.aq());
                        int position3 = zzapnVar.getPosition();
                        int i3 = 0;
                        while (zzapnVar.av() > 0) {
                            zzapnVar.ar();
                            i3++;
                        }
                        zzapnVar.zzaft(position3);
                        int length8 = this.bjT == null ? 0 : this.bjT.length;
                        long[] jArr4 = new long[i3 + length8];
                        if (length8 != 0) {
                            System.arraycopy(this.bjT, 0, jArr4, 0, length8);
                        }
                        while (length8 < jArr4.length) {
                            jArr4[length8] = zzapnVar.ar();
                            length8++;
                        }
                        this.bjT = jArr4;
                        zzapnVar.zzafs(zzafr3);
                        break;
                    default:
                        if (!super.zza(zzapnVar, ah)) {
                            break;
                        } else {
                            break;
                        }
                }
            }
            return this;
        }
    }

    /* loaded from: classes.dex */
    public static final class zzb extends zzapp<zzb> implements Cloneable {
        public int bjU = 0;
        public String bjV = "";
        public String version = "";

        /* JADX INFO: Access modifiers changed from: private */
        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        /* renamed from: aQ, reason: merged with bridge method [inline-methods] */
        public zzb clone() {
            try {
                return (zzb) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        @Override // com.google.android.gms.internal.zzapp
        /* renamed from: aA */
        public final /* synthetic */ zzb clone() throws CloneNotSupportedException {
            return (zzb) clone();
        }

        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        /* renamed from: aB */
        public final /* synthetic */ zzapv clone() throws CloneNotSupportedException {
            return (zzb) clone();
        }

        public final boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb zzbVar = (zzb) obj;
            if (this.bjU != zzbVar.bjU) {
                return false;
            }
            if (this.bjV == null) {
                if (zzbVar.bjV != null) {
                    return false;
                }
            } else if (!this.bjV.equals(zzbVar.bjV)) {
                return false;
            }
            if (this.version == null) {
                if (zzbVar.version != null) {
                    return false;
                }
            } else if (!this.version.equals(zzbVar.version)) {
                return false;
            }
            return (this.bjx == null || this.bjx.isEmpty()) ? zzbVar.bjx == null || zzbVar.bjx.isEmpty() : this.bjx.equals(zzbVar.bjx);
        }

        public final int hashCode() {
            int i = 0;
            int hashCode = ((this.version == null ? 0 : this.version.hashCode()) + (((this.bjV == null ? 0 : this.bjV.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + this.bjU) * 31)) * 31)) * 31;
            if (this.bjx != null && !this.bjx.isEmpty()) {
                i = this.bjx.hashCode();
            }
            return hashCode + i;
        }

        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        public final void zza(zzapo zzapoVar) throws IOException {
            if (this.bjU != 0) {
                zzapoVar.zzae(1, this.bjU);
            }
            if (!this.bjV.equals("")) {
                zzapoVar.zzr(2, this.bjV);
            }
            if (!this.version.equals("")) {
                zzapoVar.zzr(3, this.version);
            }
            super.zza(zzapoVar);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        public final int zzx() {
            int zzx = super.zzx();
            if (this.bjU != 0) {
                zzx += zzapo.zzag(1, this.bjU);
            }
            if (!this.bjV.equals("")) {
                zzx += zzapo.zzs(2, this.bjV);
            }
            return !this.version.equals("") ? zzx + zzapo.zzs(3, this.version) : zzx;
        }

        public zzb() {
            this.bjx = null;
            this.bjG = -1;
        }

        @Override // com.google.android.gms.internal.zzapv
        public final /* synthetic */ zzapv zzb(zzapn zzapnVar) throws IOException {
            while (true) {
                int ah = zzapnVar.ah();
                switch (ah) {
                    case 0:
                        break;
                    case 8:
                        this.bjU = zzapnVar.aq();
                        break;
                    case 18:
                        this.bjV = zzapnVar.readString();
                        break;
                    case 26:
                        this.version = zzapnVar.readString();
                        break;
                    default:
                        if (!super.zza(zzapnVar, ah)) {
                            break;
                        } else {
                            break;
                        }
                }
            }
            return this;
        }
    }

    /* loaded from: classes.dex */
    public static final class zzc extends zzapp<zzc> implements Cloneable {
        public byte[] bjW = zzapy.bjO;
        public String bjX = "";
        public byte[][] bjY = zzapy.bjN;
        public boolean bjZ = false;

        /* JADX INFO: Access modifiers changed from: private */
        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        /* renamed from: aS, reason: merged with bridge method [inline-methods] */
        public zzc clone() {
            try {
                zzc zzcVar = (zzc) super.clone();
                if (this.bjY != null && this.bjY.length > 0) {
                    zzcVar.bjY = (byte[][]) this.bjY.clone();
                }
                return zzcVar;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        @Override // com.google.android.gms.internal.zzapp
        /* renamed from: aA */
        public final /* synthetic */ zzc clone() throws CloneNotSupportedException {
            return (zzc) clone();
        }

        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        /* renamed from: aB */
        public final /* synthetic */ zzapv clone() throws CloneNotSupportedException {
            return (zzc) clone();
        }

        public final boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzc)) {
                return false;
            }
            zzc zzcVar = (zzc) obj;
            if (!Arrays.equals(this.bjW, zzcVar.bjW)) {
                return false;
            }
            if (this.bjX == null) {
                if (zzcVar.bjX != null) {
                    return false;
                }
            } else if (!this.bjX.equals(zzcVar.bjX)) {
                return false;
            }
            if (zzapt.zza(this.bjY, zzcVar.bjY) && this.bjZ == zzcVar.bjZ) {
                return (this.bjx == null || this.bjx.isEmpty()) ? zzcVar.bjx == null || zzcVar.bjx.isEmpty() : this.bjx.equals(zzcVar.bjx);
            }
            return false;
        }

        public final int hashCode() {
            int i = 0;
            int hashCode = ((this.bjZ ? 1231 : 1237) + (((((this.bjX == null ? 0 : this.bjX.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + Arrays.hashCode(this.bjW)) * 31)) * 31) + zzapt.zzb(this.bjY)) * 31)) * 31;
            if (this.bjx != null && !this.bjx.isEmpty()) {
                i = this.bjx.hashCode();
            }
            return hashCode + i;
        }

        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        public final void zza(zzapo zzapoVar) throws IOException {
            if (!Arrays.equals(this.bjW, zzapy.bjO)) {
                zzapoVar.zza(1, this.bjW);
            }
            if (this.bjY != null && this.bjY.length > 0) {
                for (int i = 0; i < this.bjY.length; i++) {
                    byte[] bArr = this.bjY[i];
                    if (bArr != null) {
                        zzapoVar.zza(2, bArr);
                    }
                }
            }
            if (this.bjZ) {
                zzapoVar.zzj(3, this.bjZ);
            }
            if (!this.bjX.equals("")) {
                zzapoVar.zzr(4, this.bjX);
            }
            super.zza(zzapoVar);
        }

        public zzc() {
            this.bjx = null;
            this.bjG = -1;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        public final int zzx() {
            int zzx = super.zzx();
            if (!Arrays.equals(this.bjW, zzapy.bjO)) {
                zzx += zzapo.zzb(1, this.bjW);
            }
            if (this.bjY != null && this.bjY.length > 0) {
                int i = 0;
                int i2 = 0;
                for (int i3 = 0; i3 < this.bjY.length; i3++) {
                    byte[] bArr = this.bjY[i3];
                    if (bArr != null) {
                        i2++;
                        i += zzapo.zzbg(bArr);
                    }
                }
                zzx = zzx + i + (i2 * 1);
            }
            if (this.bjZ) {
                zzx += zzapo.zzaga(3) + 1;
            }
            return !this.bjX.equals("") ? zzx + zzapo.zzs(4, this.bjX) : zzx;
        }

        @Override // com.google.android.gms.internal.zzapv
        public final /* synthetic */ zzapv zzb(zzapn zzapnVar) throws IOException {
            while (true) {
                int ah = zzapnVar.ah();
                switch (ah) {
                    case 0:
                        break;
                    case 10:
                        this.bjW = zzapnVar.readBytes();
                        break;
                    case 18:
                        int zzc = zzapy.zzc(zzapnVar, 18);
                        int length = this.bjY == null ? 0 : this.bjY.length;
                        byte[][] bArr = new byte[zzc + length];
                        if (length != 0) {
                            System.arraycopy(this.bjY, 0, bArr, 0, length);
                        }
                        while (length < bArr.length - 1) {
                            bArr[length] = zzapnVar.readBytes();
                            zzapnVar.ah();
                            length++;
                        }
                        bArr[length] = zzapnVar.readBytes();
                        this.bjY = bArr;
                        break;
                    case 24:
                        this.bjZ = zzapnVar.an();
                        break;
                    case 34:
                        this.bjX = zzapnVar.readString();
                        break;
                    default:
                        if (!super.zza(zzapnVar, ah)) {
                            break;
                        } else {
                            break;
                        }
                }
            }
            return this;
        }
    }

    /* loaded from: classes.dex */
    public static final class zzd extends zzapp<zzd> implements Cloneable {
        public long bka = 0;
        public long bkb = 0;
        public long bkc = 0;
        public String tag = "";
        public int bkd = 0;
        public int zzahl = 0;
        public boolean aTs = false;
        public zze[] bke = zze.aV();
        public byte[] bkf = zzapy.bjO;
        public zzb bkg = null;
        public byte[] bkh = zzapy.bjO;
        public String bki = "";
        public String bkj = "";
        public zza bkk = null;
        public String bkl = "";
        public long bkm = 180000;
        public zzc bkn = null;
        public byte[] bko = zzapy.bjO;
        public String bkp = "";
        public int bkq = 0;
        public int[] bkr = zzapy.bjH;
        public long bks = 0;
        public zzf bkt = null;

        /* JADX INFO: Access modifiers changed from: private */
        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        /* renamed from: aU, reason: merged with bridge method [inline-methods] */
        public zzd clone() {
            try {
                zzd zzdVar = (zzd) super.clone();
                if (this.bke != null && this.bke.length > 0) {
                    zzdVar.bke = new zze[this.bke.length];
                    int i = 0;
                    while (true) {
                        int i2 = i;
                        if (i2 >= this.bke.length) {
                            break;
                        }
                        if (this.bke[i2] != null) {
                            zzdVar.bke[i2] = (zze) this.bke[i2].clone();
                        }
                        i = i2 + 1;
                    }
                }
                if (this.bkg != null) {
                    zzdVar.bkg = (zzb) this.bkg.clone();
                }
                if (this.bkk != null) {
                    zzdVar.bkk = (zza) this.bkk.clone();
                }
                if (this.bkn != null) {
                    zzdVar.bkn = (zzc) this.bkn.clone();
                }
                if (this.bkr != null && this.bkr.length > 0) {
                    zzdVar.bkr = (int[]) this.bkr.clone();
                }
                if (this.bkt != null) {
                    zzdVar.bkt = (zzf) this.bkt.clone();
                }
                return zzdVar;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        @Override // com.google.android.gms.internal.zzapp
        /* renamed from: aA */
        public final /* synthetic */ zzd clone() throws CloneNotSupportedException {
            return (zzd) clone();
        }

        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        /* renamed from: aB */
        public final /* synthetic */ zzapv clone() throws CloneNotSupportedException {
            return (zzd) clone();
        }

        public final boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzd)) {
                return false;
            }
            zzd zzdVar = (zzd) obj;
            if (this.bka == zzdVar.bka && this.bkb == zzdVar.bkb && this.bkc == zzdVar.bkc) {
                if (this.tag == null) {
                    if (zzdVar.tag != null) {
                        return false;
                    }
                } else if (!this.tag.equals(zzdVar.tag)) {
                    return false;
                }
                if (this.bkd == zzdVar.bkd && this.zzahl == zzdVar.zzahl && this.aTs == zzdVar.aTs && zzapt.equals(this.bke, zzdVar.bke) && Arrays.equals(this.bkf, zzdVar.bkf)) {
                    if (this.bkg == null) {
                        if (zzdVar.bkg != null) {
                            return false;
                        }
                    } else if (!this.bkg.equals(zzdVar.bkg)) {
                        return false;
                    }
                    if (!Arrays.equals(this.bkh, zzdVar.bkh)) {
                        return false;
                    }
                    if (this.bki == null) {
                        if (zzdVar.bki != null) {
                            return false;
                        }
                    } else if (!this.bki.equals(zzdVar.bki)) {
                        return false;
                    }
                    if (this.bkj == null) {
                        if (zzdVar.bkj != null) {
                            return false;
                        }
                    } else if (!this.bkj.equals(zzdVar.bkj)) {
                        return false;
                    }
                    if (this.bkk == null) {
                        if (zzdVar.bkk != null) {
                            return false;
                        }
                    } else if (!this.bkk.equals(zzdVar.bkk)) {
                        return false;
                    }
                    if (this.bkl == null) {
                        if (zzdVar.bkl != null) {
                            return false;
                        }
                    } else if (!this.bkl.equals(zzdVar.bkl)) {
                        return false;
                    }
                    if (this.bkm != zzdVar.bkm) {
                        return false;
                    }
                    if (this.bkn == null) {
                        if (zzdVar.bkn != null) {
                            return false;
                        }
                    } else if (!this.bkn.equals(zzdVar.bkn)) {
                        return false;
                    }
                    if (!Arrays.equals(this.bko, zzdVar.bko)) {
                        return false;
                    }
                    if (this.bkp == null) {
                        if (zzdVar.bkp != null) {
                            return false;
                        }
                    } else if (!this.bkp.equals(zzdVar.bkp)) {
                        return false;
                    }
                    if (this.bkq == zzdVar.bkq && zzapt.equals(this.bkr, zzdVar.bkr) && this.bks == zzdVar.bks) {
                        if (this.bkt == null) {
                            if (zzdVar.bkt != null) {
                                return false;
                            }
                        } else if (!this.bkt.equals(zzdVar.bkt)) {
                            return false;
                        }
                        return (this.bjx == null || this.bjx.isEmpty()) ? zzdVar.bjx == null || zzdVar.bjx.isEmpty() : this.bjx.equals(zzdVar.bjx);
                    }
                    return false;
                }
                return false;
            }
            return false;
        }

        public final int hashCode() {
            int i = 0;
            int hashCode = ((this.bkt == null ? 0 : this.bkt.hashCode()) + (((((((((this.bkp == null ? 0 : this.bkp.hashCode()) + (((((this.bkn == null ? 0 : this.bkn.hashCode()) + (((((this.bkl == null ? 0 : this.bkl.hashCode()) + (((this.bkk == null ? 0 : this.bkk.hashCode()) + (((this.bkj == null ? 0 : this.bkj.hashCode()) + (((this.bki == null ? 0 : this.bki.hashCode()) + (((((this.bkg == null ? 0 : this.bkg.hashCode()) + (((((((this.aTs ? 1231 : 1237) + (((((((this.tag == null ? 0 : this.tag.hashCode()) + ((((((((getClass().getName().hashCode() + 527) * 31) + ((int) (this.bka ^ (this.bka >>> 32)))) * 31) + ((int) (this.bkb ^ (this.bkb >>> 32)))) * 31) + ((int) (this.bkc ^ (this.bkc >>> 32)))) * 31)) * 31) + this.bkd) * 31) + this.zzahl) * 31)) * 31) + zzapt.hashCode(this.bke)) * 31) + Arrays.hashCode(this.bkf)) * 31)) * 31) + Arrays.hashCode(this.bkh)) * 31)) * 31)) * 31)) * 31)) * 31) + ((int) (this.bkm ^ (this.bkm >>> 32)))) * 31)) * 31) + Arrays.hashCode(this.bko)) * 31)) * 31) + this.bkq) * 31) + zzapt.hashCode(this.bkr)) * 31) + ((int) (this.bks ^ (this.bks >>> 32)))) * 31)) * 31;
            if (this.bjx != null && !this.bjx.isEmpty()) {
                i = this.bjx.hashCode();
            }
            return hashCode + i;
        }

        public zzd() {
            this.bjx = null;
            this.bjG = -1;
        }

        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        public final void zza(zzapo zzapoVar) throws IOException {
            if (this.bka != 0) {
                zzapoVar.zzb(1, this.bka);
            }
            if (!this.tag.equals("")) {
                zzapoVar.zzr(2, this.tag);
            }
            if (this.bke != null && this.bke.length > 0) {
                for (int i = 0; i < this.bke.length; i++) {
                    zze zzeVar = this.bke[i];
                    if (zzeVar != null) {
                        zzapoVar.zza(3, zzeVar);
                    }
                }
            }
            if (!Arrays.equals(this.bkf, zzapy.bjO)) {
                zzapoVar.zza(4, this.bkf);
            }
            if (!Arrays.equals(this.bkh, zzapy.bjO)) {
                zzapoVar.zza(6, this.bkh);
            }
            if (this.bkk != null) {
                zzapoVar.zza(7, this.bkk);
            }
            if (!this.bki.equals("")) {
                zzapoVar.zzr(8, this.bki);
            }
            if (this.bkg != null) {
                zzapoVar.zza(9, this.bkg);
            }
            if (this.aTs) {
                zzapoVar.zzj(10, this.aTs);
            }
            if (this.bkd != 0) {
                zzapoVar.zzae(11, this.bkd);
            }
            if (this.zzahl != 0) {
                zzapoVar.zzae(12, this.zzahl);
            }
            if (!this.bkj.equals("")) {
                zzapoVar.zzr(13, this.bkj);
            }
            if (!this.bkl.equals("")) {
                zzapoVar.zzr(14, this.bkl);
            }
            if (this.bkm != 180000) {
                long j = this.bkm;
                zzapoVar.zzai(15, 0);
                zzapoVar.zzdb(zzapo.zzde(j));
            }
            if (this.bkn != null) {
                zzapoVar.zza(16, this.bkn);
            }
            if (this.bkb != 0) {
                zzapoVar.zzb(17, this.bkb);
            }
            if (!Arrays.equals(this.bko, zzapy.bjO)) {
                zzapoVar.zza(18, this.bko);
            }
            if (this.bkq != 0) {
                zzapoVar.zzae(19, this.bkq);
            }
            if (this.bkr != null && this.bkr.length > 0) {
                for (int i2 = 0; i2 < this.bkr.length; i2++) {
                    zzapoVar.zzae(20, this.bkr[i2]);
                }
            }
            if (this.bkc != 0) {
                zzapoVar.zzb(21, this.bkc);
            }
            if (this.bks != 0) {
                zzapoVar.zzb(22, this.bks);
            }
            if (this.bkt != null) {
                zzapoVar.zza(23, this.bkt);
            }
            if (!this.bkp.equals("")) {
                zzapoVar.zzr(24, this.bkp);
            }
            super.zza(zzapoVar);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        public final int zzx() {
            int zzx = super.zzx();
            if (this.bka != 0) {
                zzx += zzapo.zze(1, this.bka);
            }
            if (!this.tag.equals("")) {
                zzx += zzapo.zzs(2, this.tag);
            }
            if (this.bke != null && this.bke.length > 0) {
                int i = zzx;
                for (int i2 = 0; i2 < this.bke.length; i2++) {
                    zze zzeVar = this.bke[i2];
                    if (zzeVar != null) {
                        i += zzapo.zzc(3, zzeVar);
                    }
                }
                zzx = i;
            }
            if (!Arrays.equals(this.bkf, zzapy.bjO)) {
                zzx += zzapo.zzb(4, this.bkf);
            }
            if (!Arrays.equals(this.bkh, zzapy.bjO)) {
                zzx += zzapo.zzb(6, this.bkh);
            }
            if (this.bkk != null) {
                zzx += zzapo.zzc(7, this.bkk);
            }
            if (!this.bki.equals("")) {
                zzx += zzapo.zzs(8, this.bki);
            }
            if (this.bkg != null) {
                zzx += zzapo.zzc(9, this.bkg);
            }
            if (this.aTs) {
                zzx += zzapo.zzaga(10) + 1;
            }
            if (this.bkd != 0) {
                zzx += zzapo.zzag(11, this.bkd);
            }
            if (this.zzahl != 0) {
                zzx += zzapo.zzag(12, this.zzahl);
            }
            if (!this.bkj.equals("")) {
                zzx += zzapo.zzs(13, this.bkj);
            }
            if (!this.bkl.equals("")) {
                zzx += zzapo.zzs(14, this.bkl);
            }
            if (this.bkm != 180000) {
                zzx += zzapo.zzdc(zzapo.zzde(this.bkm)) + zzapo.zzaga(15);
            }
            if (this.bkn != null) {
                zzx += zzapo.zzc(16, this.bkn);
            }
            if (this.bkb != 0) {
                zzx += zzapo.zze(17, this.bkb);
            }
            if (!Arrays.equals(this.bko, zzapy.bjO)) {
                zzx += zzapo.zzb(18, this.bko);
            }
            if (this.bkq != 0) {
                zzx += zzapo.zzag(19, this.bkq);
            }
            if (this.bkr != null && this.bkr.length > 0) {
                int i3 = 0;
                for (int i4 = 0; i4 < this.bkr.length; i4++) {
                    i3 += zzapo.zzafx(this.bkr[i4]);
                }
                zzx = zzx + i3 + (this.bkr.length * 2);
            }
            if (this.bkc != 0) {
                zzx += zzapo.zze(21, this.bkc);
            }
            if (this.bks != 0) {
                zzx += zzapo.zze(22, this.bks);
            }
            if (this.bkt != null) {
                zzx += zzapo.zzc(23, this.bkt);
            }
            return !this.bkp.equals("") ? zzx + zzapo.zzs(24, this.bkp) : zzx;
        }

        @Override // com.google.android.gms.internal.zzapv
        public final /* synthetic */ zzapv zzb(zzapn zzapnVar) throws IOException {
            while (true) {
                int ah = zzapnVar.ah();
                switch (ah) {
                    case 0:
                        break;
                    case 8:
                        this.bka = zzapnVar.ar();
                        break;
                    case 18:
                        this.tag = zzapnVar.readString();
                        break;
                    case 26:
                        int zzc = zzapy.zzc(zzapnVar, 26);
                        int length = this.bke == null ? 0 : this.bke.length;
                        zze[] zzeVarArr = new zze[zzc + length];
                        if (length != 0) {
                            System.arraycopy(this.bke, 0, zzeVarArr, 0, length);
                        }
                        while (length < zzeVarArr.length - 1) {
                            zzeVarArr[length] = new zze();
                            zzapnVar.zza(zzeVarArr[length]);
                            zzapnVar.ah();
                            length++;
                        }
                        zzeVarArr[length] = new zze();
                        zzapnVar.zza(zzeVarArr[length]);
                        this.bke = zzeVarArr;
                        break;
                    case 34:
                        this.bkf = zzapnVar.readBytes();
                        break;
                    case 50:
                        this.bkh = zzapnVar.readBytes();
                        break;
                    case 58:
                        if (this.bkk == null) {
                            this.bkk = new zza();
                        }
                        zzapnVar.zza(this.bkk);
                        break;
                    case 66:
                        this.bki = zzapnVar.readString();
                        break;
                    case 74:
                        if (this.bkg == null) {
                            this.bkg = new zzb();
                        }
                        zzapnVar.zza(this.bkg);
                        break;
                    case 80:
                        this.aTs = zzapnVar.an();
                        break;
                    case 88:
                        this.bkd = zzapnVar.aq();
                        break;
                    case 96:
                        this.zzahl = zzapnVar.aq();
                        break;
                    case 106:
                        this.bkj = zzapnVar.readString();
                        break;
                    case 114:
                        this.bkl = zzapnVar.readString();
                        break;
                    case 120:
                        long ar = zzapnVar.ar();
                        this.bkm = (-(ar & 1)) ^ (ar >>> 1);
                        break;
                    case 130:
                        if (this.bkn == null) {
                            this.bkn = new zzc();
                        }
                        zzapnVar.zza(this.bkn);
                        break;
                    case 136:
                        this.bkb = zzapnVar.ar();
                        break;
                    case R.styleable.ThemeTemplate_keyContent5rowLowerBaseline /* 146 */:
                        this.bko = zzapnVar.readBytes();
                        break;
                    case R.styleable.ThemeTemplate_popupRadius /* 152 */:
                        int aq = zzapnVar.aq();
                        switch (aq) {
                            case 0:
                            case 1:
                            case 2:
                                this.bkq = aq;
                                break;
                        }
                    case 160:
                        int zzc2 = zzapy.zzc(zzapnVar, 160);
                        int length2 = this.bkr == null ? 0 : this.bkr.length;
                        int[] iArr = new int[zzc2 + length2];
                        if (length2 != 0) {
                            System.arraycopy(this.bkr, 0, iArr, 0, length2);
                        }
                        while (length2 < iArr.length - 1) {
                            iArr[length2] = zzapnVar.aq();
                            zzapnVar.ah();
                            length2++;
                        }
                        iArr[length2] = zzapnVar.aq();
                        this.bkr = iArr;
                        break;
                    case R.styleable.ThemeTemplate_btnKeyboardActionKeyNormalTop /* 162 */:
                        int zzafr = zzapnVar.zzafr(zzapnVar.aq());
                        int position = zzapnVar.getPosition();
                        int i = 0;
                        while (zzapnVar.av() > 0) {
                            zzapnVar.aq();
                            i++;
                        }
                        zzapnVar.zzaft(position);
                        int length3 = this.bkr == null ? 0 : this.bkr.length;
                        int[] iArr2 = new int[i + length3];
                        if (length3 != 0) {
                            System.arraycopy(this.bkr, 0, iArr2, 0, length3);
                        }
                        while (length3 < iArr2.length) {
                            iArr2[length3] = zzapnVar.aq();
                            length3++;
                        }
                        this.bkr = iArr2;
                        zzapnVar.zzafs(zzafr);
                        break;
                    case R.styleable.ThemeTemplate_btnKeyboardActionKeyPressedBottom /* 168 */:
                        this.bkc = zzapnVar.ar();
                        break;
                    case R.styleable.ThemeTemplate_btnKeyboardKeyChecked /* 176 */:
                        this.bks = zzapnVar.ar();
                        break;
                    case R.styleable.ThemeTemplate_btnKeyboardKeySelected5row /* 186 */:
                        if (this.bkt == null) {
                            this.bkt = new zzf();
                        }
                        zzapnVar.zza(this.bkt);
                        break;
                    case R.styleable.ThemeTemplate_btnKeyboardKeyNormal5rowBottom /* 194 */:
                        this.bkp = zzapnVar.readString();
                        break;
                    default:
                        if (!super.zza(zzapnVar, ah)) {
                            break;
                        } else {
                            break;
                        }
                }
            }
            return this;
        }
    }

    /* loaded from: classes.dex */
    public static final class zze extends zzapp<zze> implements Cloneable {
        private static volatile zze[] bku;
        public String zzcb = "";
        public String value = "";

        public static zze[] aV() {
            if (bku == null) {
                synchronized (zzapt.bjF) {
                    if (bku == null) {
                        bku = new zze[0];
                    }
                }
            }
            return bku;
        }

        /* JADX INFO: Access modifiers changed from: private */
        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        /* renamed from: aX, reason: merged with bridge method [inline-methods] */
        public zze clone() {
            try {
                return (zze) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        @Override // com.google.android.gms.internal.zzapp
        /* renamed from: aA */
        public final /* synthetic */ zze clone() throws CloneNotSupportedException {
            return (zze) clone();
        }

        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        /* renamed from: aB */
        public final /* synthetic */ zzapv clone() throws CloneNotSupportedException {
            return (zze) clone();
        }

        public final boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zze)) {
                return false;
            }
            zze zzeVar = (zze) obj;
            if (this.zzcb == null) {
                if (zzeVar.zzcb != null) {
                    return false;
                }
            } else if (!this.zzcb.equals(zzeVar.zzcb)) {
                return false;
            }
            if (this.value == null) {
                if (zzeVar.value != null) {
                    return false;
                }
            } else if (!this.value.equals(zzeVar.value)) {
                return false;
            }
            return (this.bjx == null || this.bjx.isEmpty()) ? zzeVar.bjx == null || zzeVar.bjx.isEmpty() : this.bjx.equals(zzeVar.bjx);
        }

        public final int hashCode() {
            int i = 0;
            int hashCode = ((this.value == null ? 0 : this.value.hashCode()) + (((this.zzcb == null ? 0 : this.zzcb.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
            if (this.bjx != null && !this.bjx.isEmpty()) {
                i = this.bjx.hashCode();
            }
            return hashCode + i;
        }

        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        public final void zza(zzapo zzapoVar) throws IOException {
            if (!this.zzcb.equals("")) {
                zzapoVar.zzr(1, this.zzcb);
            }
            if (!this.value.equals("")) {
                zzapoVar.zzr(2, this.value);
            }
            super.zza(zzapoVar);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        public final int zzx() {
            int zzx = super.zzx();
            if (!this.zzcb.equals("")) {
                zzx += zzapo.zzs(1, this.zzcb);
            }
            return !this.value.equals("") ? zzx + zzapo.zzs(2, this.value) : zzx;
        }

        public zze() {
            this.bjx = null;
            this.bjG = -1;
        }

        @Override // com.google.android.gms.internal.zzapv
        public final /* synthetic */ zzapv zzb(zzapn zzapnVar) throws IOException {
            while (true) {
                int ah = zzapnVar.ah();
                switch (ah) {
                    case 0:
                        break;
                    case 10:
                        this.zzcb = zzapnVar.readString();
                        break;
                    case 18:
                        this.value = zzapnVar.readString();
                        break;
                    default:
                        if (!super.zza(zzapnVar, ah)) {
                            break;
                        } else {
                            break;
                        }
                }
            }
            return this;
        }
    }

    /* loaded from: classes.dex */
    public static final class zzf extends zzapp<zzf> implements Cloneable {
        public int bkv = -1;

        /* JADX INFO: Access modifiers changed from: private */
        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        /* renamed from: aZ, reason: merged with bridge method [inline-methods] */
        public zzf clone() {
            try {
                return (zzf) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        @Override // com.google.android.gms.internal.zzapp
        /* renamed from: aA */
        public final /* synthetic */ zzf clone() throws CloneNotSupportedException {
            return (zzf) clone();
        }

        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        /* renamed from: aB */
        public final /* synthetic */ zzapv clone() throws CloneNotSupportedException {
            return (zzf) clone();
        }

        public final boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzf)) {
                return false;
            }
            zzf zzfVar = (zzf) obj;
            if (this.bkv != zzfVar.bkv) {
                return false;
            }
            return (this.bjx == null || this.bjx.isEmpty()) ? zzfVar.bjx == null || zzfVar.bjx.isEmpty() : this.bjx.equals(zzfVar.bjx);
        }

        public final int hashCode() {
            return ((this.bjx == null || this.bjx.isEmpty()) ? 0 : this.bjx.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + this.bkv) * 31);
        }

        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        public final void zza(zzapo zzapoVar) throws IOException {
            if (this.bkv != -1) {
                zzapoVar.zzae(1, this.bkv);
            }
            super.zza(zzapoVar);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        public final int zzx() {
            int zzx = super.zzx();
            return this.bkv != -1 ? zzx + zzapo.zzag(1, this.bkv) : zzx;
        }

        public zzf() {
            this.bjx = null;
            this.bjG = -1;
        }

        @Override // com.google.android.gms.internal.zzapv
        public final /* synthetic */ zzapv zzb(zzapn zzapnVar) throws IOException {
            while (true) {
                int ah = zzapnVar.ah();
                switch (ah) {
                    case 0:
                        break;
                    case 8:
                        int aq = zzapnVar.aq();
                        switch (aq) {
                            case -1:
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                            case 16:
                            case 17:
                                this.bkv = aq;
                                break;
                        }
                    default:
                        if (!super.zza(zzapnVar, ah)) {
                            break;
                        } else {
                            break;
                        }
                }
            }
            return this;
        }
    }
}
