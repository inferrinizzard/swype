package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzah;
import com.google.android.gms.common.internal.zzd;
import com.google.android.gms.internal.zzpi;
import com.google.android.gms.internal.zzpm;
import com.google.android.gms.internal.zzqy;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes.dex */
public final class zzqc implements Handler.Callback {
    private static zzqc uG;
    static final Object zzamr = new Object();
    private final Context mContext;
    public final Handler mHandler;
    private final GoogleApiAvailability sh;
    private long uF;
    private int uH;
    private final SparseArray<zzc<?>> uJ;
    private final Map<zzpj<?>, zzc<?>> uK;
    zzpr uL;
    final Set<zzpj<?>> uM;
    private final ReferenceQueue<com.google.android.gms.common.api.zzc<?>> uN;
    private final SparseArray<zza> uO;
    private zzb uP;
    private long ue;
    private long uf;

    /* loaded from: classes.dex */
    private final class zza extends PhantomReference<com.google.android.gms.common.api.zzc<?>> {
        final int sx;

        public zza(com.google.android.gms.common.api.zzc zzcVar, int i, ReferenceQueue<com.google.android.gms.common.api.zzc<?>> referenceQueue) {
            super(zzcVar, referenceQueue);
            this.sx = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class zzd implements zzd.zzf {
        private final zzpj<?> rQ;
        private final Api.zze uT;

        public zzd(Api.zze zzeVar, zzpj<?> zzpjVar) {
            this.uT = zzeVar;
            this.rQ = zzpjVar;
        }

        @Override // com.google.android.gms.common.internal.zzd.zzf
        public final void zzh(ConnectionResult connectionResult) {
            if (connectionResult.isSuccess()) {
                this.uT.zza(null, Collections.emptySet());
            } else {
                ((zzc) zzqc.this.uK.get(this.rQ)).onConnectionFailed(connectionResult);
            }
        }
    }

    public static zzqc zzaqd() {
        zzqc zzqcVar;
        synchronized (zzamr) {
            zzqcVar = uG;
        }
        return zzqcVar;
    }

    static /* synthetic */ zzpr zzd$7270594f() {
        return null;
    }

    private void zze(int i, boolean z) {
        zzc<?> zzcVar = this.uJ.get(i);
        if (zzcVar == null) {
            Log.wtf("GoogleApiManager", new StringBuilder(52).append("onRelease received for unknown instance: ").append(i).toString(), new Exception());
            return;
        }
        if (!z) {
            this.uJ.delete(i);
        }
        zzcVar.zzf(i, z);
    }

    public final void zza(ConnectionResult connectionResult, int i) {
        if (zzc(connectionResult, i)) {
            return;
        }
        this.mHandler.sendMessage(this.mHandler.obtainMessage(5, i, 0));
    }

    public final void zzaoo() {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3));
    }

    /* loaded from: classes.dex */
    private static final class zzb extends Thread {
        private final ReferenceQueue<com.google.android.gms.common.api.zzc<?>> uN;
        private final SparseArray<zza> uO;
        private final AtomicBoolean uR;

        public zzb(ReferenceQueue<com.google.android.gms.common.api.zzc<?>> referenceQueue, SparseArray<zza> sparseArray) {
            super("GoogleApiCleanup");
            this.uR = new AtomicBoolean();
            this.uN = referenceQueue;
            this.uO = sparseArray;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public final void run() {
            this.uR.set(true);
            Process.setThreadPriority(10);
            while (this.uR.get()) {
                try {
                    zza zzaVar = (zza) this.uN.remove();
                    this.uO.remove(zzaVar.sx);
                    zzqc.this.mHandler.sendMessage(zzqc.this.mHandler.obtainMessage(2, zzaVar.sx, 2));
                } catch (InterruptedException e) {
                    return;
                } finally {
                    this.uR.set(false);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class zzc<O extends Api.ApiOptions> implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        final zzpj<O> rQ;
        final Api.zze uT;
        private final Api.zzb uU;
        boolean ud;
        final Queue<zzpi> uS = new LinkedList();
        final SparseArray<zzqy> uV = new SparseArray<>();
        final Set<zzpl> uW = new HashSet();
        private final SparseArray<Map<Object, zzpm.zza>> uX = new SparseArray<>();
        ConnectionResult uY = null;

        private void zzj(ConnectionResult connectionResult) {
            Iterator<zzpl> it = this.uW.iterator();
            while (it.hasNext()) {
                it.next().zza(this.rQ, connectionResult);
            }
            this.uW.clear();
        }

        final void connect() {
            if (this.uT.isConnected() || this.uT.isConnecting()) {
                return;
            }
            if (this.uT.zzanu() && zzqc.this.uH != 0) {
                zzqc.this.uH = zzqc.this.sh.isGooglePlayServicesAvailable(zzqc.this.mContext);
                if (zzqc.this.uH != 0) {
                    onConnectionFailed(new ConnectionResult(zzqc.this.uH, null));
                    return;
                }
            }
            this.uT.zza(new zzd(this.uT, this.rQ));
        }

        final void zzab(Status status) {
            Iterator<zzpi> it = this.uS.iterator();
            while (it.hasNext()) {
                it.next().zzx(status);
            }
            this.uS.clear();
        }

        final void zzaqk() {
            if (this.ud) {
                zzqc.this.mHandler.removeMessages(9, this.rQ);
                zzqc.this.mHandler.removeMessages(8, this.rQ);
                this.ud = false;
            }
        }

        final void zzaql() {
            zzqc.this.mHandler.removeMessages(10, this.rQ);
            zzqc.this.mHandler.sendMessageDelayed(zzqc.this.mHandler.obtainMessage(10, this.rQ), zzqc.this.uF);
        }

        final void zzc(zzpi zzpiVar) {
            Map map;
            zzpiVar.zza(this.uV);
            if (zzpiVar.iq == 3) {
                try {
                    Map<Object, zzpm.zza> map2 = this.uX.get(zzpiVar.sx);
                    if (map2 == null) {
                        ArrayMap arrayMap = new ArrayMap(1);
                        this.uX.put(zzpiVar.sx, arrayMap);
                        map = arrayMap;
                    } else {
                        map = map2;
                    }
                    Object obj = ((zzpi.zza) zzpiVar).sy;
                    map.put(((zzqm) obj).zzaqu(), obj);
                } catch (ClassCastException e) {
                    throw new IllegalStateException("Listener registration methods must implement ListenerApiMethod");
                }
            } else if (zzpiVar.iq == 4) {
                try {
                    Map<Object, zzpm.zza> map3 = this.uX.get(zzpiVar.sx);
                    zzqm zzqmVar = (zzqm) ((zzpi.zza) zzpiVar).sy;
                    if (map3 != null) {
                        map3.remove(zzqmVar.zzaqu());
                    } else {
                        Log.w("GoogleApiManager", "Received call to unregister a listener without a matching registration call.");
                    }
                } catch (ClassCastException e2) {
                    throw new IllegalStateException("Listener unregistration methods must implement ListenerApiMethod");
                }
            }
            try {
                zzpiVar.zzb(this.uU);
            } catch (DeadObjectException e3) {
                this.uT.disconnect();
                onConnectionSuspended(1);
            }
        }

        public final void zzf(int i, boolean z) {
            Iterator<zzpi> it = this.uS.iterator();
            while (it.hasNext()) {
                zzpi next = it.next();
                if (next.sx == i && next.iq != 1 && next.cancel()) {
                    it.remove();
                }
            }
            this.uV.get(i).release();
            this.uX.delete(i);
            if (z) {
                return;
            }
            this.uV.remove(i);
            zzqc.this.uO.remove(i);
            if (this.uV.size() == 0 && this.uS.isEmpty()) {
                zzaqk();
                this.uT.disconnect();
                zzqc.this.uK.remove(this.rQ);
                synchronized (zzqc.zzamr) {
                    zzqc.this.uM.remove(this.rQ);
                }
            }
        }

        /* JADX WARN: Type inference failed for: r0v7, types: [com.google.android.gms.common.api.Api$zze] */
        public zzc(com.google.android.gms.common.api.zzc<O> zzcVar) {
            this.uT = zzcVar.pN.zzanq().zza(zzcVar.mContext, zzqc.this.mHandler.getLooper(), new GoogleApiClient.Builder(zzcVar.mContext).zzaoh(), zzcVar.rP, this, this);
            if (this.uT instanceof zzah) {
                this.uU = ((zzah) this.uT).zn;
            } else {
                this.uU = this.uT;
            }
            this.rQ = zzcVar.rQ;
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
        public final void onConnected(Bundle bundle) {
            this.uY = null;
            zzj(ConnectionResult.rb);
            zzaqk();
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= this.uX.size()) {
                    break;
                }
                Iterator<zzpm.zza> it = this.uX.get(this.uX.keyAt(i2)).values().iterator();
                while (it.hasNext()) {
                    try {
                        it.next().zzb(this.uU);
                    } catch (DeadObjectException e) {
                        this.uT.disconnect();
                        onConnectionSuspended(1);
                    }
                }
                i = i2 + 1;
            }
            while (this.uT.isConnected() && !this.uS.isEmpty()) {
                zzc(this.uS.remove());
            }
            zzaql();
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
        public final void onConnectionSuspended(int i) {
            this.uY = null;
            this.ud = true;
            zzqc.this.mHandler.sendMessageDelayed(Message.obtain(zzqc.this.mHandler, 8, this.rQ), zzqc.this.uf);
            zzqc.this.mHandler.sendMessageDelayed(Message.obtain(zzqc.this.mHandler, 9, this.rQ), zzqc.this.ue);
            zzqc.this.uH = -1;
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
        public final void onConnectionFailed(ConnectionResult connectionResult) {
            this.uY = null;
            zzqc.this.uH = -1;
            zzj(connectionResult);
            int keyAt = this.uV.keyAt(0);
            if (this.uS.isEmpty()) {
                this.uY = connectionResult;
                return;
            }
            synchronized (zzqc.zzamr) {
                zzqc.zzd$7270594f();
            }
            if (zzqc.this.zzc(connectionResult, keyAt)) {
                return;
            }
            if (connectionResult.ok == 18) {
                this.ud = true;
            }
            if (this.ud) {
                zzqc.this.mHandler.sendMessageDelayed(Message.obtain(zzqc.this.mHandler, 8, this.rQ), zzqc.this.uf);
            } else {
                String valueOf = String.valueOf(this.rQ.pN.mName);
                zzab(new Status(17, new StringBuilder(String.valueOf(valueOf).length() + 38).append("API: ").append(valueOf).append(" is not available on this device.").toString()));
            }
        }

        final void zzaqm() {
            boolean z;
            if (this.uT.isConnected() && this.uX.size() == 0) {
                for (int i = 0; i < this.uV.size(); i++) {
                    zzpm.zza[] zzaVarArr = (zzpm.zza[]) this.uV.get(this.uV.keyAt(i)).vG.toArray(zzqy.vF);
                    int length = zzaVarArr.length;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= length) {
                            z = false;
                            break;
                        } else {
                            if (!zzaVarArr[i2].isReady()) {
                                z = true;
                                break;
                            }
                            i2++;
                        }
                    }
                    if (z) {
                        zzaql();
                        return;
                    }
                }
                this.uT.disconnect();
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.os.Handler.Callback
    public final boolean handleMessage(Message message) {
        Map map = null;
        switch (message.what) {
            case 1:
                zzpl zzplVar = (zzpl) message.obj;
                Iterator it = map.keySet().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    } else {
                        zzpj<?> zzpjVar = (zzpj) it.next();
                        zzc<?> zzcVar = this.uK.get(zzpjVar);
                        if (zzcVar == null) {
                            zzplVar.cancel();
                            break;
                        } else if (zzcVar.uT.isConnected()) {
                            zzplVar.zza(zzpjVar, ConnectionResult.rb);
                        } else if (zzcVar.uY != null) {
                            zzplVar.zza(zzpjVar, zzcVar.uY);
                        } else {
                            zzcVar.uW.add(zzplVar);
                        }
                    }
                }
            case 2:
                final int i = message.arg1;
                final zzc<?> zzcVar2 = this.uJ.get(i);
                if (zzcVar2 != null) {
                    this.uJ.delete(i);
                    zzqy zzqyVar = zzcVar2.uV.get(i);
                    zzqy.zzc zzcVar3 = new zzqy.zzc() { // from class: com.google.android.gms.internal.zzqc.zzc.1
                        @Override // com.google.android.gms.internal.zzqy.zzc
                        public final void zzaqn() {
                            if (zzc.this.uS.isEmpty()) {
                                zzc.this.zzf(i, false);
                            }
                        }
                    };
                    if (zzqyVar.vG.isEmpty()) {
                        zzcVar3.zzaqn();
                    }
                    zzqyVar.vI = zzcVar3;
                    break;
                } else {
                    Log.wtf("GoogleApiManager", new StringBuilder(64).append("onCleanupLeakInternal received for unknown instance: ").append(i).toString(), new Exception());
                    break;
                }
            case 3:
                for (zzc<?> zzcVar4 : this.uK.values()) {
                    zzcVar4.uY = null;
                    zzcVar4.connect();
                }
                break;
            case 4:
                zzpi zzpiVar = (zzpi) message.obj;
                zzc<?> zzcVar5 = this.uJ.get(zzpiVar.sx);
                if (zzcVar5.uT.isConnected()) {
                    zzcVar5.zzc(zzpiVar);
                    zzcVar5.zzaql();
                    break;
                } else {
                    zzcVar5.uS.add(zzpiVar);
                    if (zzcVar5.uY == null || !zzcVar5.uY.hasResolution()) {
                        zzcVar5.connect();
                        break;
                    } else {
                        zzcVar5.onConnectionFailed(zzcVar5.uY);
                        break;
                    }
                }
                break;
            case 5:
                if (this.uJ.get(message.arg1) != null) {
                    this.uJ.get(message.arg1).zzab(new Status(17, "Error resolution was canceled by the user."));
                    break;
                }
                break;
            case 6:
                com.google.android.gms.common.api.zzc zzcVar6 = (com.google.android.gms.common.api.zzc) message.obj;
                int i2 = message.arg1;
                Object obj = zzcVar6.rQ;
                if (!this.uK.containsKey(obj)) {
                    this.uK.put(obj, new zzc(zzcVar6));
                }
                zzc<?> zzcVar7 = this.uK.get(obj);
                zzcVar7.uV.put(i2, new zzqy(zzcVar7.rQ.pN.zzans(), zzcVar7.uT));
                this.uJ.put(i2, zzcVar7);
                zzcVar7.connect();
                this.uO.put(i2, new zza(zzcVar6, i2, this.uN));
                if (this.uP == null || !this.uP.uR.get()) {
                    this.uP = new zzb(this.uN, this.uO);
                    this.uP.start();
                    break;
                }
                break;
            case 7:
                zze(message.arg1, message.arg2 == 1);
                break;
            case 8:
                if (this.uK.containsKey(message.obj)) {
                    zzc<?> zzcVar8 = this.uK.get(message.obj);
                    if (zzcVar8.ud) {
                        zzcVar8.connect();
                        break;
                    }
                }
                break;
            case 9:
                if (this.uK.containsKey(message.obj)) {
                    zzc<?> zzcVar9 = this.uK.get(message.obj);
                    if (zzcVar9.ud) {
                        zzcVar9.zzaqk();
                        zzcVar9.zzab(zzqc.this.sh.isGooglePlayServicesAvailable(zzqc.this.mContext) == 18 ? new Status(8, "Connection timed out while waiting for Google Play services update to complete.") : new Status(8, "API failed to connect while resuming due to an unknown error."));
                        zzcVar9.uT.disconnect();
                        break;
                    }
                }
                break;
            case 10:
                if (this.uK.containsKey(message.obj)) {
                    this.uK.get(message.obj).zzaqm();
                    break;
                }
                break;
            default:
                Log.w("GoogleApiManager", new StringBuilder(31).append("Unknown message id: ").append(message.what).toString());
                return false;
        }
        return true;
    }

    final boolean zzc(ConnectionResult connectionResult, int i) {
        if (!connectionResult.hasResolution() && !this.sh.isUserResolvableError(connectionResult.ok)) {
            return false;
        }
        this.sh.zza(this.mContext, connectionResult, i);
        return true;
    }
}
