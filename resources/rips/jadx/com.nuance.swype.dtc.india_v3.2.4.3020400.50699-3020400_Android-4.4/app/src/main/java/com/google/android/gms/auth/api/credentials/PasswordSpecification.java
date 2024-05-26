package com.google.android.gms.auth.api.credentials;

import android.os.Parcel;
import android.text.TextUtils;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

/* loaded from: classes.dex */
public final class PasswordSpecification extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final zzf CREATOR = new zzf();
    public static final PasswordSpecification cZ = new zza().zzj$3e63f106().zzfl("abcdefghijkmnopqrstxyzABCDEFGHJKLMNPQRSTXY3456789").zze$6bc52feb("abcdefghijkmnopqrstxyz").zze$6bc52feb("ABCDEFGHJKLMNPQRSTXY").zze$6bc52feb("3456789").zzafg();
    public static final PasswordSpecification da = new zza().zzj$3e63f106().zzfl("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890").zze$6bc52feb("abcdefghijklmnopqrstuvwxyz").zze$6bc52feb("ABCDEFGHIJKLMNOPQRSTUVWXYZ").zze$6bc52feb("1234567890").zzafg();
    final String db;
    final List<String> dc;
    final List<Integer> dd;
    final int de;
    final int df;
    private final int[] dg;
    final int mVersionCode;
    private final Random zzavp;

    /* loaded from: classes.dex */
    public static class zzb extends Error {
        public zzb(String str) {
            super(str);
        }
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        zzf.zza$18e098bf(this, parcel);
    }

    /* loaded from: classes.dex */
    public static class zza {
        private final TreeSet<Character> dh = new TreeSet<>();
        private final List<String> dc = new ArrayList();
        private final List<Integer> dd = new ArrayList();
        private int de = 12;
        private int df = 16;

        private static TreeSet<Character> zzw(String str, String str2) {
            if (TextUtils.isEmpty(str)) {
                throw new zzb(String.valueOf(str2).concat(" cannot be null or empty"));
            }
            TreeSet<Character> treeSet = new TreeSet<>();
            for (char c : str.toCharArray()) {
                if (PasswordSpecification.zzc$4868d312(c)) {
                    throw new zzb(String.valueOf(str2).concat(" must only contain ASCII printable characters"));
                }
                treeSet.add(Character.valueOf(c));
            }
            return treeSet;
        }

        public final zza zze$6bc52feb(String str) {
            this.dc.add(PasswordSpecification.zzc(zzw(str, "requiredChars")));
            this.dd.add(1);
            return this;
        }

        public final zza zzfl(String str) {
            this.dh.addAll(zzw(str, "allowedChars"));
            return this;
        }

        public final zza zzj$3e63f106() {
            this.de = 12;
            this.df = 16;
            return this;
        }

        public final PasswordSpecification zzafg() {
            if (this.dh.isEmpty()) {
                throw new zzb("no allowed characters specified");
            }
            Iterator<Integer> it = this.dd.iterator();
            int i = 0;
            while (it.hasNext()) {
                i = it.next().intValue() + i;
            }
            if (i > this.df) {
                throw new zzb("required character count cannot be greater than the max password size");
            }
            boolean[] zArr = new boolean[95];
            Iterator<String> it2 = this.dc.iterator();
            while (it2.hasNext()) {
                for (char c : it2.next().toCharArray()) {
                    if (zArr[c - ' ']) {
                        throw new zzb(new StringBuilder(58).append("character ").append(c).append(" occurs in more than one required character set").toString());
                    }
                    zArr[c - ' '] = true;
                }
            }
            return new PasswordSpecification(1, PasswordSpecification.zzc(this.dh), this.dc, this.dd, this.de, this.df);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PasswordSpecification(int i, String str, List<String> list, List<Integer> list2, int i2, int i3) {
        this.mVersionCode = i;
        this.db = str;
        this.dc = Collections.unmodifiableList(list);
        this.dd = Collections.unmodifiableList(list2);
        this.de = i2;
        this.df = i3;
        int[] iArr = new int[95];
        Arrays.fill(iArr, -1);
        Iterator<String> it = this.dc.iterator();
        int i4 = 0;
        while (it.hasNext()) {
            int length = it.next().toCharArray().length;
            for (int i5 = 0; i5 < length; i5++) {
                iArr[r5[i5] - ' '] = i4;
            }
            i4++;
        }
        this.dg = iArr;
        this.zzavp = new SecureRandom();
    }

    static /* synthetic */ String zzc(Collection collection) {
        char[] cArr = new char[collection.size()];
        int i = 0;
        Iterator it = collection.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return new String(cArr);
            }
            i = i2 + 1;
            cArr[i2] = ((Character) it.next()).charValue();
        }
    }

    static /* synthetic */ boolean zzc$4868d312(int i) {
        return i < 32 || i > 126;
    }
}
