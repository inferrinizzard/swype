package com.google.android.gms.internal;

import java.io.IOException;

/* loaded from: classes.dex */
public interface zzad {

    /* loaded from: classes.dex */
    public static final class zza extends zzapp<zza> {
        public String zzck = null;
        public Long zzcl = null;
        public String stackTrace = null;
        public String zzcm = null;
        public String zzcn = null;
        public Long zzco = null;
        public Long zzcp = null;
        public String zzcq = null;
        public Long zzcr = null;
        public String zzcs = null;

        public zza() {
            this.bjG = -1;
        }

        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        public final void zza(zzapo zzapoVar) throws IOException {
            if (this.zzck != null) {
                zzapoVar.zzr(1, this.zzck);
            }
            if (this.zzcl != null) {
                zzapoVar.zzb(2, this.zzcl.longValue());
            }
            if (this.stackTrace != null) {
                zzapoVar.zzr(3, this.stackTrace);
            }
            if (this.zzcm != null) {
                zzapoVar.zzr(4, this.zzcm);
            }
            if (this.zzcn != null) {
                zzapoVar.zzr(5, this.zzcn);
            }
            if (this.zzco != null) {
                zzapoVar.zzb(6, this.zzco.longValue());
            }
            if (this.zzcp != null) {
                zzapoVar.zzb(7, this.zzcp.longValue());
            }
            if (this.zzcq != null) {
                zzapoVar.zzr(8, this.zzcq);
            }
            if (this.zzcr != null) {
                zzapoVar.zzb(9, this.zzcr.longValue());
            }
            if (this.zzcs != null) {
                zzapoVar.zzr(10, this.zzcs);
            }
            super.zza(zzapoVar);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.internal.zzapp, com.google.android.gms.internal.zzapv
        public final int zzx() {
            int zzx = super.zzx();
            if (this.zzck != null) {
                zzx += zzapo.zzs(1, this.zzck);
            }
            if (this.zzcl != null) {
                zzx += zzapo.zze(2, this.zzcl.longValue());
            }
            if (this.stackTrace != null) {
                zzx += zzapo.zzs(3, this.stackTrace);
            }
            if (this.zzcm != null) {
                zzx += zzapo.zzs(4, this.zzcm);
            }
            if (this.zzcn != null) {
                zzx += zzapo.zzs(5, this.zzcn);
            }
            if (this.zzco != null) {
                zzx += zzapo.zze(6, this.zzco.longValue());
            }
            if (this.zzcp != null) {
                zzx += zzapo.zze(7, this.zzcp.longValue());
            }
            if (this.zzcq != null) {
                zzx += zzapo.zzs(8, this.zzcq);
            }
            if (this.zzcr != null) {
                zzx += zzapo.zze(9, this.zzcr.longValue());
            }
            return this.zzcs != null ? zzx + zzapo.zzs(10, this.zzcs) : zzx;
        }

        @Override // com.google.android.gms.internal.zzapv
        public final /* synthetic */ zzapv zzb(zzapn zzapnVar) throws IOException {
            while (true) {
                int ah = zzapnVar.ah();
                switch (ah) {
                    case 0:
                        break;
                    case 10:
                        this.zzck = zzapnVar.readString();
                        break;
                    case 16:
                        this.zzcl = Long.valueOf(zzapnVar.ar());
                        break;
                    case 26:
                        this.stackTrace = zzapnVar.readString();
                        break;
                    case 34:
                        this.zzcm = zzapnVar.readString();
                        break;
                    case 42:
                        this.zzcn = zzapnVar.readString();
                        break;
                    case 48:
                        this.zzco = Long.valueOf(zzapnVar.ar());
                        break;
                    case 56:
                        this.zzcp = Long.valueOf(zzapnVar.ar());
                        break;
                    case 66:
                        this.zzcq = zzapnVar.readString();
                        break;
                    case 72:
                        this.zzcr = Long.valueOf(zzapnVar.ar());
                        break;
                    case 82:
                        this.zzcs = zzapnVar.readString();
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
}
