package com.google.android.gms.internal;

import com.google.android.gms.internal.zzapp;
import java.io.IOException;
import java.lang.reflect.Array;

/* loaded from: classes.dex */
public final class zzapq<M extends zzapp<M>, T> {
    protected final Class<T> baj;
    protected final boolean bjy;
    public final int tag;
    protected final int type;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzapq)) {
            return false;
        }
        zzapq zzapqVar = (zzapq) obj;
        return this.type == zzapqVar.type && this.baj == zzapqVar.baj && this.tag == zzapqVar.tag && this.bjy == zzapqVar.bjy;
    }

    public final int hashCode() {
        return (this.bjy ? 1 : 0) + ((((((this.type + 1147) * 31) + this.baj.hashCode()) * 31) + this.tag) * 31);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void zza(Object obj, zzapo zzapoVar) throws IOException {
        if (!this.bjy) {
            zzb(obj, zzapoVar);
            return;
        }
        int length = Array.getLength(obj);
        for (int i = 0; i < length; i++) {
            Object obj2 = Array.get(obj, i);
            if (obj2 != null) {
                zzb(obj2, zzapoVar);
            }
        }
    }

    private void zzb(Object obj, zzapo zzapoVar) {
        try {
            zzapoVar.zzagb(this.tag);
            switch (this.type) {
                case 10:
                    int zzagj = zzapy.zzagj(this.tag);
                    ((zzapv) obj).zza(zzapoVar);
                    zzapoVar.zzai(zzagj, 4);
                    return;
                case 11:
                    zzapoVar.zzc((zzapv) obj);
                    return;
                default:
                    throw new IllegalArgumentException(new StringBuilder(24).append("Unknown type ").append(this.type).toString());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int zzcp(Object obj) {
        int i = 0;
        if (!this.bjy) {
            return zzcr(obj);
        }
        int length = Array.getLength(obj);
        for (int i2 = 0; i2 < length; i2++) {
            if (Array.get(obj, i2) != null) {
                i += zzcr(Array.get(obj, i2));
            }
        }
        return i;
    }

    private int zzcr(Object obj) {
        int zzagj = zzapy.zzagj(this.tag);
        switch (this.type) {
            case 10:
                return (zzapo.zzaga(zzagj) * 2) + ((zzapv) obj).aM();
            case 11:
                return zzapo.zzc(zzagj, (zzapv) obj);
            default:
                throw new IllegalArgumentException(new StringBuilder(24).append("Unknown type ").append(this.type).toString());
        }
    }
}
