package com.google.android.gms.common.internal;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class zzn extends zzm implements Handler.Callback {
    private final Handler mHandler;
    private final HashMap<zza, zzb> yN = new HashMap<>();
    private final com.google.android.gms.common.stats.zzb yO = com.google.android.gms.common.stats.zzb.zzaux();
    private final long yP = 5000;
    private final Context zzaql;

    /* JADX INFO: Access modifiers changed from: package-private */
    public zzn(Context context) {
        this.zzaql = context.getApplicationContext();
        this.mHandler = new Handler(context.getMainLooper(), this);
    }

    @Override // com.google.android.gms.common.internal.zzm
    public final boolean zza(ComponentName componentName, ServiceConnection serviceConnection, String str) {
        return zza(new zza(componentName), serviceConnection, str);
    }

    @Override // com.google.android.gms.common.internal.zzm
    public final boolean zza(String str, String str2, ServiceConnection serviceConnection, String str3) {
        return zza(new zza(str, str2), serviceConnection, str3);
    }

    @Override // com.google.android.gms.common.internal.zzm
    public final void zzb$3185ab25(String str, String str2, ServiceConnection serviceConnection) {
        zzb$7cfb6f03(new zza(str, str2), serviceConnection);
    }

    @Override // com.google.android.gms.common.internal.zzm
    public final void zzb$9b3168c(ComponentName componentName, ServiceConnection serviceConnection) {
        zzb$7cfb6f03(new zza(componentName), serviceConnection);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class zzb {
        IBinder xL;
        ComponentName yR;
        boolean yU;
        final zza yV;
        final zza yS = new zza();
        final Set<ServiceConnection> yT = new HashSet();
        int mState = 2;

        public zzb(zza zzaVar) {
            this.yV = zzaVar;
        }

        public final boolean zza(ServiceConnection serviceConnection) {
            return this.yT.contains(serviceConnection);
        }

        public final boolean zzasz() {
            return this.yT.isEmpty();
        }

        @TargetApi(14)
        public final void zzhm(String str) {
            this.mState = 3;
            com.google.android.gms.common.stats.zzb unused = zzn.this.yO;
            this.yU = com.google.android.gms.common.stats.zzb.zza$58d5677d(zzn.this.zzaql, this.yV.zzasy(), this.yS, 129);
            if (this.yU) {
                return;
            }
            this.mState = 2;
            try {
                com.google.android.gms.common.stats.zzb unused2 = zzn.this.yO;
                com.google.android.gms.common.stats.zzb.zza(zzn.this.zzaql, this.yS);
            } catch (IllegalArgumentException e) {
            }
        }

        /* loaded from: classes.dex */
        public class zza implements ServiceConnection {
            public zza() {
            }

            @Override // android.content.ServiceConnection
            public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                synchronized (zzn.this.yN) {
                    zzb.this.xL = iBinder;
                    zzb.this.yR = componentName;
                    Iterator<ServiceConnection> it = zzb.this.yT.iterator();
                    while (it.hasNext()) {
                        it.next().onServiceConnected(componentName, iBinder);
                    }
                    zzb.this.mState = 1;
                }
            }

            @Override // android.content.ServiceConnection
            public final void onServiceDisconnected(ComponentName componentName) {
                synchronized (zzn.this.yN) {
                    zzb.this.xL = null;
                    zzb.this.yR = componentName;
                    Iterator<ServiceConnection> it = zzb.this.yT.iterator();
                    while (it.hasNext()) {
                        it.next().onServiceDisconnected(componentName);
                    }
                    zzb.this.mState = 2;
                }
            }
        }

        public final void zza$2d8eac7(ServiceConnection serviceConnection) {
            com.google.android.gms.common.stats.zzb unused = zzn.this.yO;
            Context unused2 = zzn.this.zzaql;
            this.yV.zzasy();
            com.google.android.gms.common.stats.zzb.zzb(serviceConnection);
            this.yT.add(serviceConnection);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class zza {
        private final String yQ;
        private final ComponentName yR;
        private final String zzcvc;

        public zza(ComponentName componentName) {
            this.zzcvc = null;
            this.yQ = null;
            this.yR = (ComponentName) zzab.zzy(componentName);
        }

        public zza(String str, String str2) {
            this.zzcvc = zzab.zzhr(str);
            this.yQ = zzab.zzhr(str2);
            this.yR = null;
        }

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza zzaVar = (zza) obj;
            return zzaa.equal(this.zzcvc, zzaVar.zzcvc) && zzaa.equal(this.yR, zzaVar.yR);
        }

        public final String toString() {
            return this.zzcvc == null ? this.yR.flattenToString() : this.zzcvc;
        }

        public final Intent zzasy() {
            return this.zzcvc != null ? new Intent(this.zzcvc).setPackage(this.yQ) : new Intent().setComponent(this.yR);
        }

        public final int hashCode() {
            return Arrays.hashCode(new Object[]{this.zzcvc, this.yR});
        }
    }

    private boolean zza(zza zzaVar, ServiceConnection serviceConnection, String str) {
        boolean z;
        zzab.zzb(serviceConnection, "ServiceConnection must not be null");
        synchronized (this.yN) {
            zzb zzbVar = this.yN.get(zzaVar);
            if (zzbVar != null) {
                this.mHandler.removeMessages(0, zzbVar);
                if (!zzbVar.zza(serviceConnection)) {
                    zzbVar.zza$2d8eac7(serviceConnection);
                    switch (zzbVar.mState) {
                        case 1:
                            serviceConnection.onServiceConnected(zzbVar.yR, zzbVar.xL);
                            break;
                        case 2:
                            zzbVar.zzhm(str);
                            break;
                    }
                } else {
                    String valueOf = String.valueOf(zzaVar);
                    throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 81).append("Trying to bind a GmsServiceConnection that was already connected before.  config=").append(valueOf).toString());
                }
            } else {
                zzbVar = new zzb(zzaVar);
                zzbVar.zza$2d8eac7(serviceConnection);
                zzbVar.zzhm(str);
                this.yN.put(zzaVar, zzbVar);
            }
            z = zzbVar.yU;
        }
        return z;
    }

    private void zzb$7cfb6f03(zza zzaVar, ServiceConnection serviceConnection) {
        zzab.zzb(serviceConnection, "ServiceConnection must not be null");
        synchronized (this.yN) {
            zzb zzbVar = this.yN.get(zzaVar);
            if (zzbVar == null) {
                String valueOf = String.valueOf(zzaVar);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 50).append("Nonexistent connection status for service config: ").append(valueOf).toString());
            }
            if (!zzbVar.zza(serviceConnection)) {
                String valueOf2 = String.valueOf(zzaVar);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf2).length() + 76).append("Trying to unbind a GmsServiceConnection  that was not bound before.  config=").append(valueOf2).toString());
            }
            com.google.android.gms.common.stats.zzb.zzb(serviceConnection);
            zzbVar.yT.remove(serviceConnection);
            if (zzbVar.zzasz()) {
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(0, zzbVar), this.yP);
            }
        }
    }

    @Override // android.os.Handler.Callback
    public final boolean handleMessage(Message message) {
        switch (message.what) {
            case 0:
                zzb zzbVar = (zzb) message.obj;
                synchronized (this.yN) {
                    if (zzbVar.zzasz()) {
                        if (zzbVar.yU) {
                            com.google.android.gms.common.stats.zzb.zza(zzn.this.zzaql, zzbVar.yS);
                            zzbVar.yU = false;
                            zzbVar.mState = 2;
                        }
                        this.yN.remove(zzbVar.yV);
                    }
                }
                return true;
            default:
                return false;
        }
    }
}
