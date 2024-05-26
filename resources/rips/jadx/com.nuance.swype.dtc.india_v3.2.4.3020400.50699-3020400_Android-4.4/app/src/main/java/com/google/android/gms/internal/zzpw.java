package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ResolveAccountResponse;
import com.google.android.gms.common.internal.zzd;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.internal.zzpm;
import com.google.android.gms.internal.zzqa;
import com.google.android.gms.signin.internal.SignInResponse;
import com.nuance.connect.sqlite.ChinesePredictionDataSource;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

/* loaded from: classes.dex */
public final class zzpw implements zzpz {
    final Context mContext;
    private final Api.zza<? extends zzvu, zzvv> si;
    private ConnectionResult tA;
    private int tB;
    private int tD;
    zzvu tG;
    private int tH;
    boolean tI;
    boolean tJ;
    com.google.android.gms.common.internal.zzq tK;
    boolean tL;
    boolean tM;
    final com.google.android.gms.common.internal.zzg tN;
    private final Map<Api<?>, Integer> tO;
    final Lock tr;
    final zzqa tw;
    final com.google.android.gms.common.zzc tz;
    private int tC = 0;
    private final Bundle tE = new Bundle();
    private final Set<Api.zzc> tF = new HashSet();
    private ArrayList<Future<?>> tP = new ArrayList<>();

    public zzpw(zzqa zzqaVar, com.google.android.gms.common.internal.zzg zzgVar, Map<Api<?>, Integer> map, com.google.android.gms.common.zzc zzcVar, Api.zza<? extends zzvu, zzvv> zzaVar, Lock lock, Context context) {
        this.tw = zzqaVar;
        this.tN = zzgVar;
        this.tO = map;
        this.tz = zzcVar;
        this.si = zzaVar;
        this.tr = lock;
        this.mContext = context;
    }

    private void zzapr() {
        Iterator<Future<?>> it = this.tP.iterator();
        while (it.hasNext()) {
            it.next().cancel(true);
        }
        this.tP.clear();
    }

    private void zzbm(boolean z) {
        if (this.tG != null) {
            if (this.tG.isConnected() && z) {
                this.tG.zzbzo();
            }
            this.tG.disconnect();
            this.tK = null;
        }
    }

    private static String zzfj(int i) {
        switch (i) {
            case 0:
                return "STEP_SERVICE_BINDINGS_AND_SIGN_IN";
            case 1:
                return "STEP_GETTING_REMOTE_SERVICE";
            default:
                return ChinesePredictionDataSource.UNKNOWN;
        }
    }

    @Override // com.google.android.gms.internal.zzpz
    public final void connect() {
    }

    @Override // com.google.android.gms.internal.zzpz
    public final boolean disconnect() {
        zzapr();
        zzbm(true);
        this.tw.zzi(null);
        return true;
    }

    @Override // com.google.android.gms.internal.zzpz
    public final void onConnected(Bundle bundle) {
        if (zzfi(1)) {
            if (bundle != null) {
                this.tE.putAll(bundle);
            }
            if (zzapm()) {
                zzapp();
            }
        }
    }

    @Override // com.google.android.gms.internal.zzpz
    public final void onConnectionSuspended(int i) {
        zzg(new ConnectionResult(8, null));
    }

    @Override // com.google.android.gms.internal.zzpz
    public final void zza(ConnectionResult connectionResult, Api<?> api, int i) {
        if (zzfi(1)) {
            zzb(connectionResult, api, i);
            if (zzapm()) {
                zzapp();
            }
        }
    }

    final boolean zzapm() {
        this.tD--;
        if (this.tD > 0) {
            return false;
        }
        if (this.tD < 0) {
            Log.w("GoogleApiClientConnecting", this.tw.th.zzapy());
            Log.wtf("GoogleApiClientConnecting", "GoogleApiClient received too many callbacks for the given step. Clients may be in an unexpected state; GoogleApiClient will now disconnect.", new Exception());
            zzg(new ConnectionResult(8, null));
            return false;
        }
        if (this.tA == null) {
            return true;
        }
        this.tw.uA = this.tB;
        zzg(this.tA);
        return false;
    }

    final void zzapq() {
        this.tI = false;
        this.tw.th.uj = Collections.emptySet();
        for (Api.zzc<?> zzcVar : this.tF) {
            if (!this.tw.ux.containsKey(zzcVar)) {
                this.tw.ux.put(zzcVar, new ConnectionResult(17, null));
            }
        }
    }

    @Override // com.google.android.gms.internal.zzpz
    public final <A extends Api.zzb, R extends Result, T extends zzpm.zza<R, A>> T zzc(T t) {
        this.tw.th.uc.add(t);
        return t;
    }

    @Override // com.google.android.gms.internal.zzpz
    public final <A extends Api.zzb, T extends zzpm.zza<? extends Result, A>> T zzd(T t) {
        throw new IllegalStateException("GoogleApiClient is not connected yet.");
    }

    final boolean zzf(ConnectionResult connectionResult) {
        if (this.tH != 2) {
            return this.tH == 1 && !connectionResult.hasResolution();
        }
        return true;
    }

    final boolean zzfi(int i) {
        if (this.tC == i) {
            return true;
        }
        Log.w("GoogleApiClientConnecting", this.tw.th.zzapy());
        String valueOf = String.valueOf(this);
        Log.w("GoogleApiClientConnecting", new StringBuilder(String.valueOf(valueOf).length() + 23).append("Unexpected callback in ").append(valueOf).toString());
        Log.w("GoogleApiClientConnecting", new StringBuilder(33).append("mRemainingConnections=").append(this.tD).toString());
        String valueOf2 = String.valueOf(zzfj(this.tC));
        String valueOf3 = String.valueOf(zzfj(i));
        Log.wtf("GoogleApiClientConnecting", new StringBuilder(String.valueOf(valueOf2).length() + 70 + String.valueOf(valueOf3).length()).append("GoogleApiClient connecting is in step ").append(valueOf2).append(" but received callback for step ").append(valueOf3).toString(), new Exception());
        zzg(new ConnectionResult(8, null));
        return false;
    }

    final void zzg(ConnectionResult connectionResult) {
        zzapr();
        zzbm(!connectionResult.hasResolution());
        this.tw.zzi(connectionResult);
        this.tw.uB.zzd(connectionResult);
    }

    /* loaded from: classes.dex */
    private static class zza implements zzd.zzf {
        private final Api<?> pN;
        private final WeakReference<zzpw> tR;
        final int tf;

        public zza(zzpw zzpwVar, Api<?> api, int i) {
            this.tR = new WeakReference<>(zzpwVar);
            this.pN = api;
            this.tf = i;
        }

        @Override // com.google.android.gms.common.internal.zzd.zzf
        public final void zzh(ConnectionResult connectionResult) {
            zzpw zzpwVar = this.tR.get();
            if (zzpwVar == null) {
                return;
            }
            com.google.android.gms.common.internal.zzab.zza(Looper.myLooper() == zzpwVar.tw.th.zzahv, "onReportServiceBinding must be called on the GoogleApiClient handler thread");
            zzpwVar.tr.lock();
            try {
                if (zzpwVar.zzfi(0)) {
                    if (!connectionResult.isSuccess()) {
                        zzpwVar.zzb(connectionResult, this.pN, this.tf);
                    }
                    if (zzpwVar.zzapm()) {
                        zzpwVar.zzapn();
                    }
                }
            } finally {
                zzpwVar.tr.unlock();
            }
        }
    }

    /* loaded from: classes.dex */
    private class zzb extends zzf {
        private final Map<Api.zze, zza> tS;

        public zzb(Map<Api.zze, zza> map) {
            super(zzpw.this, (byte) 0);
            this.tS = map;
        }

        @Override // com.google.android.gms.internal.zzpw.zzf
        public final void zzapl() {
            boolean z;
            boolean z2;
            boolean z3;
            boolean z4 = true;
            Iterator<Api.zze> it = this.tS.keySet().iterator();
            boolean z5 = true;
            boolean z6 = false;
            while (true) {
                if (!it.hasNext()) {
                    z4 = z6;
                    z = false;
                    break;
                }
                Api.zze next = it.next();
                if (!next.zzanu()) {
                    z2 = false;
                    z3 = z6;
                } else if (this.tS.get(next).tf == 0) {
                    z = true;
                    break;
                } else {
                    z2 = z5;
                    z3 = true;
                }
                z6 = z3;
                z5 = z2;
            }
            int isGooglePlayServicesAvailable = z4 ? zzpw.this.tz.isGooglePlayServicesAvailable(zzpw.this.mContext) : 0;
            if (isGooglePlayServicesAvailable != 0 && (z || z5)) {
                final ConnectionResult connectionResult = new ConnectionResult(isGooglePlayServicesAvailable, null);
                zzpw.this.tw.zza(new zzqa.zza(zzpw.this) { // from class: com.google.android.gms.internal.zzpw.zzb.1
                    @Override // com.google.android.gms.internal.zzqa.zza
                    public final void zzapl() {
                        zzpw.this.zzg(connectionResult);
                    }
                });
                return;
            }
            if (zzpw.this.tI) {
                zzpw.this.tG.connect();
            }
            for (Api.zze zzeVar : this.tS.keySet()) {
                final zza zzaVar = this.tS.get(zzeVar);
                if (!zzeVar.zzanu() || isGooglePlayServicesAvailable == 0) {
                    zzeVar.zza(zzaVar);
                } else {
                    zzpw.this.tw.zza(new zzqa.zza(zzpw.this) { // from class: com.google.android.gms.internal.zzpw.zzb.2
                        @Override // com.google.android.gms.internal.zzqa.zza
                        public final void zzapl() {
                            zzaVar.zzh(new ConnectionResult(16, null));
                        }
                    });
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class zzc extends zzf {
        private final ArrayList<Api.zze> tW;

        public zzc(ArrayList<Api.zze> arrayList) {
            super(zzpw.this, (byte) 0);
            this.tW = arrayList;
        }

        @Override // com.google.android.gms.internal.zzpw.zzf
        public final void zzapl() {
            Set<Scope> set;
            zzpy zzpyVar = zzpw.this.tw.th;
            zzpw zzpwVar = zzpw.this;
            if (zzpwVar.tN == null) {
                set = Collections.emptySet();
            } else {
                HashSet hashSet = new HashSet(zzpwVar.tN.rX);
                Map<Api<?>, zzg.zza> map = zzpwVar.tN.yk;
                for (Api<?> api : map.keySet()) {
                    if (!zzpwVar.tw.ux.containsKey(api.zzans())) {
                        hashSet.addAll(map.get(api).dT);
                    }
                }
                set = hashSet;
            }
            zzpyVar.uj = set;
            Iterator<Api.zze> it = this.tW.iterator();
            while (it.hasNext()) {
                it.next().zza(zzpw.this.tK, zzpw.this.tw.th.uj);
            }
        }
    }

    /* loaded from: classes.dex */
    private static class zzd extends com.google.android.gms.signin.internal.zzb {
        private final WeakReference<zzpw> tR;

        zzd(zzpw zzpwVar) {
            this.tR = new WeakReference<>(zzpwVar);
        }

        @Override // com.google.android.gms.signin.internal.zzb, com.google.android.gms.signin.internal.zzd
        public final void zzb(final SignInResponse signInResponse) {
            final zzpw zzpwVar = this.tR.get();
            if (zzpwVar == null) {
                return;
            }
            zzpwVar.tw.zza(new zzqa.zza(zzpwVar) { // from class: com.google.android.gms.internal.zzpw.zzd.1
                @Override // com.google.android.gms.internal.zzqa.zza
                public final void zzapl() {
                    zzpw zzpwVar2 = zzpwVar;
                    SignInResponse signInResponse2 = signInResponse;
                    if (zzpwVar2.zzfi(0)) {
                        ConnectionResult connectionResult = signInResponse2.rF;
                        if (!connectionResult.isSuccess()) {
                            if (!zzpwVar2.zzf(connectionResult)) {
                                zzpwVar2.zzg(connectionResult);
                                return;
                            } else {
                                zzpwVar2.zzapq();
                                zzpwVar2.zzapn();
                                return;
                            }
                        }
                        ResolveAccountResponse resolveAccountResponse = signInResponse2.aue;
                        ConnectionResult connectionResult2 = resolveAccountResponse.rF;
                        if (!connectionResult2.isSuccess()) {
                            String valueOf = String.valueOf(connectionResult2);
                            Log.wtf("GoogleApiClientConnecting", new StringBuilder(String.valueOf(valueOf).length() + 48).append("Sign-in succeeded with resolve account failure: ").append(valueOf).toString(), new Exception());
                            zzpwVar2.zzg(connectionResult2);
                        } else {
                            zzpwVar2.tJ = true;
                            zzpwVar2.tK = zzq.zza.zzdp(resolveAccountResponse.xj);
                            zzpwVar2.tL = resolveAccountResponse.tL;
                            zzpwVar2.tM = resolveAccountResponse.zj;
                            zzpwVar2.zzapn();
                        }
                    }
                }
            });
        }
    }

    /* loaded from: classes.dex */
    private class zze implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        private zze() {
        }

        /* synthetic */ zze(zzpw zzpwVar, byte b) {
            this();
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
        public final void onConnectionSuspended(int i) {
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
        public final void onConnected(Bundle bundle) {
            zzpw.this.tG.zza(new zzd(zzpw.this));
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
        public final void onConnectionFailed(ConnectionResult connectionResult) {
            zzpw.this.tr.lock();
            try {
                if (zzpw.this.zzf(connectionResult)) {
                    zzpw.this.zzapq();
                    zzpw.this.zzapn();
                } else {
                    zzpw.this.zzg(connectionResult);
                }
            } finally {
                zzpw.this.tr.unlock();
            }
        }
    }

    /* loaded from: classes.dex */
    private abstract class zzf implements Runnable {
        private zzf() {
        }

        /* synthetic */ zzf(zzpw zzpwVar, byte b) {
            this();
        }

        protected abstract void zzapl();

        @Override // java.lang.Runnable
        public void run() {
            zzpw.this.tr.lock();
            try {
                if (!Thread.interrupted()) {
                    zzapl();
                    zzpw.this.tr.unlock();
                }
            } catch (RuntimeException e) {
                zzqa zzqaVar = zzpw.this.tw;
                zzqaVar.uw.sendMessage(zzqaVar.uw.obtainMessage(2, e));
            } finally {
                zzpw.this.tr.unlock();
            }
        }
    }

    @Override // com.google.android.gms.internal.zzpz
    public final void begin() {
        byte b = 0;
        this.tw.ux.clear();
        this.tI = false;
        this.tA = null;
        this.tC = 0;
        this.tH = 2;
        this.tJ = false;
        this.tL = false;
        HashMap hashMap = new HashMap();
        for (Api<?> api : this.tO.keySet()) {
            Api.zze zzeVar = this.tw.ui.get(api.zzans());
            int intValue = this.tO.get(api).intValue();
            if (zzeVar.zzafk()) {
                this.tI = true;
                if (intValue < this.tH) {
                    this.tH = intValue;
                }
                if (intValue != 0) {
                    this.tF.add(api.zzans());
                }
            }
            hashMap.put(zzeVar, new zza(this, api, intValue));
        }
        if (this.tI) {
            this.tN.ym = Integer.valueOf(System.identityHashCode(this.tw.th));
            zze zzeVar2 = new zze(this, b);
            this.tG = this.si.zza(this.mContext, this.tw.th.zzahv, this.tN, this.tN.yl, zzeVar2, zzeVar2);
        }
        this.tD = this.tw.ui.size();
        this.tP.add(zzqb.zzaqc().submit(new zzb(hashMap)));
    }

    final void zzapn() {
        if (this.tD != 0) {
            return;
        }
        if (!this.tI || this.tJ) {
            ArrayList arrayList = new ArrayList();
            this.tC = 1;
            this.tD = this.tw.ui.size();
            for (Api.zzc<?> zzcVar : this.tw.ui.keySet()) {
                if (!this.tw.ux.containsKey(zzcVar)) {
                    arrayList.add(this.tw.ui.get(zzcVar));
                } else if (zzapm()) {
                    zzapp();
                }
            }
            if (arrayList.isEmpty()) {
                return;
            }
            this.tP.add(zzqb.zzaqc().submit(new zzc(arrayList)));
        }
    }

    private void zzapp() {
        zzqa zzqaVar = this.tw;
        zzqaVar.tr.lock();
        try {
            zzqaVar.th.zzapw();
            zzqaVar.uy = new zzpv(zzqaVar);
            zzqaVar.uy.begin();
            zzqaVar.uv.signalAll();
            zzqaVar.tr.unlock();
            zzqb.zzaqc().execute(new Runnable() { // from class: com.google.android.gms.internal.zzpw.1
                @Override // java.lang.Runnable
                public final void run() {
                    com.google.android.gms.common.zzc.zzbp(zzpw.this.mContext);
                }
            });
            if (this.tG != null) {
                if (this.tL) {
                    this.tG.zza(this.tK, this.tM);
                }
                zzbm(false);
            }
            Iterator<Api.zzc<?>> it = this.tw.ux.keySet().iterator();
            while (it.hasNext()) {
                this.tw.ui.get(it.next()).disconnect();
            }
            this.tw.uB.zzm(this.tE.isEmpty() ? null : this.tE);
        } catch (Throwable th) {
            zzqaVar.tr.unlock();
            throw th;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x0019, code lost:            if (Integer.MAX_VALUE >= r5.tB) goto L21;     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0011, code lost:            if (r2 != false) goto L9;     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x003b, code lost:            r0 = false;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    final void zzb(com.google.android.gms.common.ConnectionResult r6, com.google.android.gms.common.api.Api<?> r7, int r8) {
        /*
            r5 = this;
            r4 = 2147483647(0x7fffffff, float:NaN)
            r1 = 0
            r0 = 1
            r2 = 2
            if (r8 == r2) goto L21
            if (r8 != r0) goto L13
            boolean r2 = r6.hasResolution()
            if (r2 == 0) goto L2d
            r2 = r0
        L11:
            if (r2 == 0) goto L3b
        L13:
            com.google.android.gms.common.ConnectionResult r2 = r5.tA
            if (r2 == 0) goto L1b
            int r2 = r5.tB
            if (r4 >= r2) goto L3b
        L1b:
            if (r0 == 0) goto L21
            r5.tA = r6
            r5.tB = r4
        L21:
            com.google.android.gms.internal.zzqa r0 = r5.tw
            java.util.Map<com.google.android.gms.common.api.Api$zzc<?>, com.google.android.gms.common.ConnectionResult> r0 = r0.ux
            com.google.android.gms.common.api.Api$zzc r1 = r7.zzans()
            r0.put(r1, r6)
            return
        L2d:
            com.google.android.gms.common.zzc r2 = r5.tz
            int r3 = r6.ok
            android.content.Intent r2 = r2.zzfc(r3)
            if (r2 == 0) goto L39
            r2 = r0
            goto L11
        L39:
            r2 = r1
            goto L11
        L3b:
            r0 = r1
            goto L1b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzpw.zzb(com.google.android.gms.common.ConnectionResult, com.google.android.gms.common.api.Api, int):void");
    }
}
