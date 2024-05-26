package com.google.android.gms.gass.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.HandlerThread;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzd;
import com.google.android.gms.internal.zzae;
import com.nuance.swype.input.IME;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public final class zza {

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.google.android.gms.gass.internal.zza$zza, reason: collision with other inner class name */
    /* loaded from: classes.dex */
    public static class C0056zza implements zzd.zzb, zzd.zzc {
        protected zzb YS;
        private final String YT;
        private final LinkedBlockingQueue<zzae.zza> YU;
        private final HandlerThread YV = new HandlerThread("GassClient");
        private final String packageName;

        private zze zzbla() {
            try {
                return this.YS.zzblb();
            } catch (DeadObjectException | IllegalStateException e) {
                return null;
            }
        }

        private void zzqw() {
            if (this.YS != null) {
                if (this.YS.isConnected() || this.YS.isConnecting()) {
                    this.YS.disconnect();
                }
            }
        }

        @Override // com.google.android.gms.common.internal.zzd.zzb
        public final void onConnected(Bundle bundle) {
            zze zzbla = zzbla();
            if (zzbla != null) {
                try {
                    this.YU.put(zzbla.zza(new GassRequestParcel(this.packageName, this.YT)).zzbld());
                } catch (Throwable th) {
                } finally {
                    zzqw();
                    this.YV.quit();
                }
            }
        }

        @Override // com.google.android.gms.common.internal.zzd.zzc
        public final void onConnectionFailed(ConnectionResult connectionResult) {
            try {
                this.YU.put(new zzae.zza());
            } catch (InterruptedException e) {
            }
        }

        @Override // com.google.android.gms.common.internal.zzd.zzb
        public final void onConnectionSuspended(int i) {
            try {
                this.YU.put(new zzae.zza());
            } catch (InterruptedException e) {
            }
        }

        public final zzae.zza zzsi$1d3d48d2() {
            zzae.zza zzaVar;
            try {
                zzaVar = this.YU.poll(IME.NEXT_SCAN_IN_MILLIS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                zzaVar = null;
            }
            return zzaVar == null ? new zzae.zza() : zzaVar;
        }

        public C0056zza(Context context, String str, String str2) {
            this.packageName = str;
            this.YT = str2;
            this.YV.start();
            this.YS = new zzb(context, this.YV.getLooper(), this, this);
            this.YU = new LinkedBlockingQueue<>();
            this.YS.zzarx();
        }
    }
}
