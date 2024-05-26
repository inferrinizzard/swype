package com.google.android.gms.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzpm;
import com.google.android.gms.internal.zzqh;
import com.nuance.swype.input.ThemeManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class zzpq implements zzqh {
    private final Context mContext;
    private final zzpy th;
    final zzqa ti;
    final zzqa tj;
    private final Map<Api.zzc<?>, zzqa> tk;
    private final Api.zze tm;
    Bundle tn;
    final Lock tr;
    private final Looper zzahv;
    private final Set<zzqt> tl = Collections.newSetFromMap(new WeakHashMap());
    ConnectionResult to = null;
    ConnectionResult tp = null;
    boolean tq = false;
    private int ts = 0;

    private zzpq(Context context, zzpy zzpyVar, Lock lock, Looper looper, com.google.android.gms.common.zzc zzcVar, Map<Api.zzc<?>, Api.zze> map, Map<Api.zzc<?>, Api.zze> map2, com.google.android.gms.common.internal.zzg zzgVar, Api.zza<? extends zzvu, zzvv> zzaVar, Api.zze zzeVar, ArrayList<zzpp> arrayList, ArrayList<zzpp> arrayList2, Map<Api<?>, Integer> map3, Map<Api<?>, Integer> map4) {
        this.mContext = context;
        this.th = zzpyVar;
        this.tr = lock;
        this.zzahv = looper;
        this.tm = zzeVar;
        this.ti = new zzqa(context, this.th, lock, looper, zzcVar, map2, null, map4, null, arrayList2, new zza(this, (byte) 0));
        this.tj = new zzqa(context, this.th, lock, looper, zzcVar, map, zzgVar, map3, zzaVar, arrayList, new zzb(this, (byte) 0));
        ArrayMap arrayMap = new ArrayMap();
        Iterator<Api.zzc<?>> it = map2.keySet().iterator();
        while (it.hasNext()) {
            arrayMap.put(it.next(), this.ti);
        }
        Iterator<Api.zzc<?>> it2 = map.keySet().iterator();
        while (it2.hasNext()) {
            arrayMap.put(it2.next(), this.tj);
        }
        this.tk = Collections.unmodifiableMap(arrayMap);
    }

    public static zzpq zza(Context context, zzpy zzpyVar, Lock lock, Looper looper, com.google.android.gms.common.zzc zzcVar, Map<Api.zzc<?>, Api.zze> map, com.google.android.gms.common.internal.zzg zzgVar, Map<Api<?>, Integer> map2, Api.zza<? extends zzvu, zzvv> zzaVar, ArrayList<zzpp> arrayList) {
        Api.zze zzeVar = null;
        ArrayMap arrayMap = new ArrayMap();
        ArrayMap arrayMap2 = new ArrayMap();
        for (Map.Entry<Api.zzc<?>, Api.zze> entry : map.entrySet()) {
            Api.zze value = entry.getValue();
            if (value.zzafz()) {
                zzeVar = value;
            }
            if (value.zzafk()) {
                arrayMap.put(entry.getKey(), value);
            } else {
                arrayMap2.put(entry.getKey(), value);
            }
        }
        com.google.android.gms.common.internal.zzab.zza(!arrayMap.isEmpty(), "CompositeGoogleApiClient should not be used without any APIs that require sign-in.");
        ArrayMap arrayMap3 = new ArrayMap();
        ArrayMap arrayMap4 = new ArrayMap();
        for (Api<?> api : map2.keySet()) {
            Api.zzc<?> zzans = api.zzans();
            if (arrayMap.containsKey(zzans)) {
                arrayMap3.put(api, map2.get(api));
            } else {
                if (!arrayMap2.containsKey(zzans)) {
                    throw new IllegalStateException("Each API in the apiTypeMap must have a corresponding client in the clients map.");
                }
                arrayMap4.put(api, map2.get(api));
            }
        }
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        Iterator<zzpp> it = arrayList.iterator();
        while (it.hasNext()) {
            zzpp next = it.next();
            if (arrayMap3.containsKey(next.pN)) {
                arrayList2.add(next);
            } else {
                if (!arrayMap4.containsKey(next.pN)) {
                    throw new IllegalStateException("Each ClientCallbacks must have a corresponding API in the apiTypeMap");
                }
                arrayList3.add(next);
            }
        }
        return new zzpq(context, zzpyVar, lock, looper, zzcVar, arrayMap, arrayMap2, zzgVar, zzaVar, zzeVar, arrayList2, arrayList3, arrayMap3, arrayMap4);
    }

    private void zzapg() {
        Iterator<zzqt> it = this.tl.iterator();
        while (it.hasNext()) {
            it.next().zzafy();
        }
        this.tl.clear();
    }

    private void zzb(ConnectionResult connectionResult) {
        switch (this.ts) {
            case 2:
                this.th.zzd(connectionResult);
            case 1:
                zzapg();
                break;
            default:
                Log.wtf("CompositeGAC", "Attempted to call failure callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new Exception());
                break;
        }
        this.ts = 0;
    }

    private static boolean zzc(ConnectionResult connectionResult) {
        return connectionResult != null && connectionResult.isSuccess();
    }

    @Override // com.google.android.gms.internal.zzqh
    public final ConnectionResult blockingConnect() {
        throw new UnsupportedOperationException();
    }

    @Override // com.google.android.gms.internal.zzqh
    public final void disconnect() {
        this.tp = null;
        this.to = null;
        this.ts = 0;
        this.ti.disconnect();
        this.tj.disconnect();
        zzapg();
    }

    @Override // com.google.android.gms.internal.zzqh
    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.append((CharSequence) str).append("authClient").println(":");
        this.tj.dump(String.valueOf(str).concat(ThemeManager.NO_PRICE), fileDescriptor, printWriter, strArr);
        printWriter.append((CharSequence) str).append("anonClient").println(":");
        this.ti.dump(String.valueOf(str).concat(ThemeManager.NO_PRICE), fileDescriptor, printWriter, strArr);
    }

    @Override // com.google.android.gms.internal.zzqh
    public final boolean isConnecting() {
        this.tr.lock();
        try {
            return this.ts == 2;
        } finally {
            this.tr.unlock();
        }
    }

    @Override // com.google.android.gms.internal.zzqh
    public final void zzaof() {
        this.tr.lock();
        try {
            boolean isConnecting = isConnecting();
            this.tj.disconnect();
            this.tp = new ConnectionResult(4);
            if (isConnecting) {
                new Handler(this.zzahv).post(new Runnable() { // from class: com.google.android.gms.internal.zzpq.1
                    @Override // java.lang.Runnable
                    public final void run() {
                        zzpq.this.tr.lock();
                        try {
                            zzpq.zzb(zzpq.this);
                        } finally {
                            zzpq.this.tr.unlock();
                        }
                    }
                });
            } else {
                zzapg();
            }
        } finally {
            this.tr.unlock();
        }
    }

    @Override // com.google.android.gms.internal.zzqh
    public final void zzapb() {
        this.ti.zzapb();
        this.tj.zzapb();
    }

    @Override // com.google.android.gms.internal.zzqh
    public final <A extends Api.zzb, R extends Result, T extends zzpm.zza<R, A>> T zzc(T t) {
        if (!zze(t)) {
            return (T) this.ti.zzc(t);
        }
        if (!zzaph()) {
            return (T) this.tj.zzc(t);
        }
        t.zzz(new Status(4, null, zzapi()));
        return t;
    }

    @Override // com.google.android.gms.internal.zzqh
    public final <A extends Api.zzb, T extends zzpm.zza<? extends Result, A>> T zzd(T t) {
        if (!zze(t)) {
            return (T) this.ti.zzd(t);
        }
        if (!zzaph()) {
            return (T) this.tj.zzd(t);
        }
        t.zzz(new Status(4, null, zzapi()));
        return t;
    }

    /* loaded from: classes.dex */
    private class zza implements zzqh.zza {
        private zza() {
        }

        /* synthetic */ zza(zzpq zzpqVar, byte b) {
            this();
        }

        @Override // com.google.android.gms.internal.zzqh.zza
        public final void zzm(Bundle bundle) {
            zzpq.this.tr.lock();
            try {
                zzpq zzpqVar = zzpq.this;
                if (zzpqVar.tn == null) {
                    zzpqVar.tn = bundle;
                } else if (bundle != null) {
                    zzpqVar.tn.putAll(bundle);
                }
                zzpq.this.to = ConnectionResult.rb;
                zzpq.zzb(zzpq.this);
            } finally {
                zzpq.this.tr.unlock();
            }
        }

        @Override // com.google.android.gms.internal.zzqh.zza
        public final void zzd(ConnectionResult connectionResult) {
            zzpq.this.tr.lock();
            try {
                zzpq.this.to = connectionResult;
                zzpq.zzb(zzpq.this);
            } finally {
                zzpq.this.tr.unlock();
            }
        }

        @Override // com.google.android.gms.internal.zzqh.zza
        public final void zzc(int i, boolean z) {
            zzpq.this.tr.lock();
            try {
                if (zzpq.this.tq || zzpq.this.tp == null || !zzpq.this.tp.isSuccess()) {
                    zzpq.this.tq = false;
                    zzpq.zza(zzpq.this, i, z);
                } else {
                    zzpq.this.tq = true;
                    zzpq.this.tj.onConnectionSuspended(i);
                }
            } finally {
                zzpq.this.tr.unlock();
            }
        }
    }

    /* loaded from: classes.dex */
    private class zzb implements zzqh.zza {
        private zzb() {
        }

        /* synthetic */ zzb(zzpq zzpqVar, byte b) {
            this();
        }

        @Override // com.google.android.gms.internal.zzqh.zza
        public final void zzm(Bundle bundle) {
            zzpq.this.tr.lock();
            try {
                zzpq.this.tp = ConnectionResult.rb;
                zzpq.zzb(zzpq.this);
            } finally {
                zzpq.this.tr.unlock();
            }
        }

        @Override // com.google.android.gms.internal.zzqh.zza
        public final void zzd(ConnectionResult connectionResult) {
            zzpq.this.tr.lock();
            try {
                zzpq.this.tp = connectionResult;
                zzpq.zzb(zzpq.this);
            } finally {
                zzpq.this.tr.unlock();
            }
        }

        @Override // com.google.android.gms.internal.zzqh.zza
        public final void zzc(int i, boolean z) {
            zzpq.this.tr.lock();
            try {
                if (zzpq.this.tq) {
                    zzpq.this.tq = false;
                    zzpq.zza(zzpq.this, i, z);
                } else {
                    zzpq.this.tq = true;
                    zzpq.this.ti.onConnectionSuspended(i);
                }
            } finally {
                zzpq.this.tr.unlock();
            }
        }
    }

    @Override // com.google.android.gms.internal.zzqh
    public final void connect() {
        this.ts = 2;
        this.tq = false;
        this.tp = null;
        this.to = null;
        this.ti.connect();
        this.tj.connect();
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x001e, code lost:            if (r2.ts == 1) goto L11;     */
    @Override // com.google.android.gms.internal.zzqh
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean isConnected() {
        /*
            r2 = this;
            r0 = 1
            java.util.concurrent.locks.Lock r1 = r2.tr
            r1.lock()
            com.google.android.gms.internal.zzqa r1 = r2.ti     // Catch: java.lang.Throwable -> L28
            boolean r1 = r1.isConnected()     // Catch: java.lang.Throwable -> L28
            if (r1 == 0) goto L26
            com.google.android.gms.internal.zzqa r1 = r2.tj     // Catch: java.lang.Throwable -> L28
            boolean r1 = r1.isConnected()     // Catch: java.lang.Throwable -> L28
            if (r1 != 0) goto L20
            boolean r1 = r2.zzaph()     // Catch: java.lang.Throwable -> L28
            if (r1 != 0) goto L20
            int r1 = r2.ts     // Catch: java.lang.Throwable -> L28
            if (r1 != r0) goto L26
        L20:
            java.util.concurrent.locks.Lock r1 = r2.tr
            r1.unlock()
            return r0
        L26:
            r0 = 0
            goto L20
        L28:
            r0 = move-exception
            java.util.concurrent.locks.Lock r1 = r2.tr
            r1.unlock()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzpq.isConnected():boolean");
    }

    @Override // com.google.android.gms.internal.zzqh
    public final boolean zza(zzqt zzqtVar) {
        this.tr.lock();
        try {
            if ((!isConnecting() && !isConnected()) || this.tj.isConnected()) {
                this.tr.unlock();
                return false;
            }
            this.tl.add(zzqtVar);
            if (this.ts == 0) {
                this.ts = 1;
            }
            this.tp = null;
            this.tj.connect();
            return true;
        } finally {
            this.tr.unlock();
        }
    }

    private boolean zzaph() {
        return this.tp != null && this.tp.ok == 4;
    }

    private boolean zze(zzpm.zza<? extends Result, ? extends Api.zzb> zzaVar) {
        Api.zzc<? extends Api.zzb> zzcVar = zzaVar.sJ;
        com.google.android.gms.common.internal.zzab.zzb(this.tk.containsKey(zzcVar), "GoogleApiClient is not configured to use the API required for this call.");
        return this.tk.get(zzcVar).equals(this.tj);
    }

    private PendingIntent zzapi() {
        if (this.tm == null) {
            return null;
        }
        return PendingIntent.getActivity(this.mContext, System.identityHashCode(this.th), this.tm.zzaga(), 134217728);
    }

    static /* synthetic */ void zzb(zzpq zzpqVar) {
        if (!zzc(zzpqVar.to)) {
            if (zzpqVar.to != null && zzc(zzpqVar.tp)) {
                zzpqVar.tj.disconnect();
                zzpqVar.zzb(zzpqVar.to);
                return;
            } else {
                if (zzpqVar.to == null || zzpqVar.tp == null) {
                    return;
                }
                ConnectionResult connectionResult = zzpqVar.to;
                if (zzpqVar.tj.uA < zzpqVar.ti.uA) {
                    connectionResult = zzpqVar.tp;
                }
                zzpqVar.zzb(connectionResult);
                return;
            }
        }
        if (!zzc(zzpqVar.tp) && !zzpqVar.zzaph()) {
            if (zzpqVar.tp != null) {
                if (zzpqVar.ts == 1) {
                    zzpqVar.zzapg();
                    return;
                } else {
                    zzpqVar.zzb(zzpqVar.tp);
                    zzpqVar.ti.disconnect();
                    return;
                }
            }
            return;
        }
        switch (zzpqVar.ts) {
            case 2:
                zzpqVar.th.zzm(zzpqVar.tn);
            case 1:
                zzpqVar.zzapg();
                break;
            default:
                Log.wtf("CompositeGAC", "Attempted to call success callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new AssertionError());
                break;
        }
        zzpqVar.ts = 0;
    }

    static /* synthetic */ void zza(zzpq zzpqVar, int i, boolean z) {
        zzpqVar.th.zzc(i, z);
        zzpqVar.tp = null;
        zzpqVar.to = null;
    }
}
