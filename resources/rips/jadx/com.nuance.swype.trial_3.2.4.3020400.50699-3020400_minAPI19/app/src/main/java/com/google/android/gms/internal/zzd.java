package com.google.android.gms.internal;

/* loaded from: classes.dex */
public final class zzd implements zzo {
    private int zzn;
    private int zzo;
    private final int zzp;
    private final float zzq;

    public zzd() {
        this((byte) 0);
    }

    private zzd(byte b) {
        this.zzn = 2500;
        this.zzp = 1;
        this.zzq = 1.0f;
    }

    @Override // com.google.android.gms.internal.zzo
    public final int zzc() {
        return this.zzn;
    }

    @Override // com.google.android.gms.internal.zzo
    public final int zzd() {
        return this.zzo;
    }

    @Override // com.google.android.gms.internal.zzo
    public final void zza(zzr zzrVar) throws zzr {
        this.zzo++;
        this.zzn = (int) (this.zzn + (this.zzn * this.zzq));
        if (!(this.zzo <= this.zzp)) {
            throw zzrVar;
        }
    }
}
