package com.google.android.gms.internal;

import com.nuance.connect.common.Integers;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@zzin
/* loaded from: classes.dex */
public final class zzcm {
    int zzash;
    final Object zzail = new Object();
    List<zzcl> zzasi = new LinkedList();

    public final boolean zza(zzcl zzclVar) {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzasi.contains(zzclVar);
        }
        return z;
    }

    public final zzcl zzhy() {
        int i;
        zzcl zzclVar;
        zzcl zzclVar2 = null;
        synchronized (this.zzail) {
            if (this.zzasi.size() == 0) {
                zzkd.zzcv("Queue empty");
                return null;
            }
            if (this.zzasi.size() < 2) {
                zzcl zzclVar3 = this.zzasi.get(0);
                synchronized (zzclVar3.zzail) {
                    zzclVar3.zzase -= 100;
                }
                return zzclVar3;
            }
            int i2 = Integers.STATUS_SUCCESS;
            for (zzcl zzclVar4 : this.zzasi) {
                int i3 = zzclVar4.zzase;
                if (i3 > i2) {
                    zzclVar = zzclVar4;
                    i = i3;
                } else {
                    i = i2;
                    zzclVar = zzclVar2;
                }
                i2 = i;
                zzclVar2 = zzclVar;
            }
            this.zzasi.remove(zzclVar2);
            return zzclVar2;
        }
    }

    public final boolean zzb(zzcl zzclVar) {
        boolean z;
        synchronized (this.zzail) {
            Iterator<zzcl> it = this.zzasi.iterator();
            while (true) {
                if (!it.hasNext()) {
                    z = false;
                    break;
                }
                zzcl next = it.next();
                if (zzclVar != next && next.zzasf.equals(zzclVar.zzasf)) {
                    it.remove();
                    z = true;
                    break;
                }
            }
        }
        return z;
    }
}
