package com.google.android.gms.internal;

import java.util.ArrayList;
import java.util.Iterator;

@zzin
/* loaded from: classes.dex */
public final class zzcl {
    private final int zzarv;
    private final int zzarw;
    private final int zzarx;
    private final zzcq zzary;
    int zzase;
    final Object zzail = new Object();
    private ArrayList<String> zzarz = new ArrayList<>();
    private ArrayList<String> zzasa = new ArrayList<>();
    int zzasb = 0;
    int zzasc = 0;
    int zzasd = 0;
    public String zzasf = "";
    public String zzasg = "";

    public zzcl(int i, int i2, int i3, int i4) {
        this.zzarv = i;
        this.zzarw = i2;
        this.zzarx = i3;
        this.zzary = new zzcq(i4);
    }

    private static String zza$19d919ee(ArrayList<String> arrayList) {
        if (arrayList.isEmpty()) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            stringBuffer.append(it.next());
            stringBuffer.append(' ');
            if (stringBuffer.length() > 100) {
                break;
            }
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        String stringBuffer2 = stringBuffer.toString();
        return stringBuffer2.length() >= 100 ? stringBuffer2.substring(0, 100) : stringBuffer2;
    }

    public final String toString() {
        int i = this.zzasc;
        int i2 = this.zzase;
        int i3 = this.zzasb;
        String valueOf = String.valueOf(zza$19d919ee(this.zzarz));
        String valueOf2 = String.valueOf(zza$19d919ee(this.zzasa));
        String str = this.zzasf;
        String str2 = this.zzasg;
        return new StringBuilder(String.valueOf(valueOf).length() + 133 + String.valueOf(valueOf2).length() + String.valueOf(str).length() + String.valueOf(str2).length()).append("ActivityContent fetchId: ").append(i).append(" score:").append(i2).append(" total_length:").append(i3).append("\n text: ").append(valueOf).append("\n viewableText").append(valueOf2).append("\n signture: ").append(str).append("\n viewableSignture: ").append(str2).toString();
    }

    public final void zzd(String str, boolean z) {
        zzf(str, z);
        synchronized (this.zzail) {
            if (this.zzasd < 0) {
                zzkd.zzcv("ActivityContent: negative number of WebViews.");
            }
            zzhw();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void zzf(String str, boolean z) {
        if (str == null || str.length() < this.zzarx) {
            return;
        }
        synchronized (this.zzail) {
            this.zzarz.add(str);
            this.zzasb += str.length();
            if (z) {
                this.zzasa.add(str);
            }
        }
    }

    public final boolean zzhq() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzasd == 0;
        }
        return z;
    }

    public final void zzhv() {
        synchronized (this.zzail) {
            this.zzasd++;
        }
    }

    public final void zzhw() {
        synchronized (this.zzail) {
            int i = (this.zzasb * this.zzarv) + (this.zzasc * this.zzarw);
            if (i > this.zzase) {
                this.zzase = i;
                this.zzasf = this.zzary.zza(this.zzarz);
                this.zzasg = this.zzary.zza(this.zzasa);
            }
        }
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof zzcl)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        zzcl zzclVar = (zzcl) obj;
        return zzclVar.zzasf != null && zzclVar.zzasf.equals(this.zzasf);
    }

    public final int hashCode() {
        return this.zzasf.hashCode();
    }
}
