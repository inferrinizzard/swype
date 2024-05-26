package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzl;
import com.google.android.gms.internal.zzpm;
import com.google.android.gms.internal.zzqe;
import com.google.android.gms.internal.zzqh;
import com.google.android.gms.internal.zzqy;
import com.nuance.connect.sqlite.ChinesePredictionDataSource;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

/* loaded from: classes.dex */
public final class zzpy extends GoogleApiClient implements zzqh.zza {
    private final Context mContext;
    private final int sf;
    private final GoogleApiAvailability sh;
    final Api.zza<? extends zzvu, zzvv> si;
    final com.google.android.gms.common.internal.zzg tN;
    final Map<Api<?>, Integer> tO;
    private final Lock tr;
    private final com.google.android.gms.common.internal.zzl ua;
    private volatile boolean ud;
    private final zza ug;
    zzqe uh;
    final Map<Api.zzc<?>, Api.zze> ui;
    private final ArrayList<zzpp> ul;
    private Integer um;
    final zzqy uo;
    final Looper zzahv;
    private zzqh ub = null;
    final Queue<zzpm.zza<?, ?>> uc = new LinkedList();
    private long ue = 120000;
    private long uf = 5000;
    Set<Scope> uj = new HashSet();
    private final zzqo uk = new zzqo();
    Set<zzqx> un = null;
    private final zzl.zza up = new zzl.zza() { // from class: com.google.android.gms.internal.zzpy.1
        @Override // com.google.android.gms.common.internal.zzl.zza
        public final boolean isConnected() {
            return zzpy.this.isConnected();
        }

        @Override // com.google.android.gms.common.internal.zzl.zza
        public final Bundle zzamh() {
            return null;
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class zza extends Handler {
        zza(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    zzpy.zzb(zzpy.this);
                    return;
                case 2:
                    zzpy.zza(zzpy.this);
                    return;
                default:
                    Log.w("GoogleApiClientImpl", new StringBuilder(31).append("Unknown message id: ").append(message.what).toString());
                    return;
            }
        }
    }

    /* loaded from: classes.dex */
    static class zzb extends zzqe.zza {
        private WeakReference<zzpy> uu;

        zzb(zzpy zzpyVar) {
            this.uu = new WeakReference<>(zzpyVar);
        }

        @Override // com.google.android.gms.internal.zzqe.zza
        public final void zzaou() {
            zzpy zzpyVar = this.uu.get();
            if (zzpyVar == null) {
                return;
            }
            zzpy.zza(zzpyVar);
        }
    }

    public static int zza(Iterable<Api.zze> iterable, boolean z) {
        boolean z2 = false;
        boolean z3 = false;
        for (Api.zze zzeVar : iterable) {
            if (zzeVar.zzafk()) {
                z3 = true;
            }
            z2 = zzeVar.zzafz() ? true : z2;
        }
        if (z3) {
            return (z2 && z) ? 2 : 1;
        }
        return 3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void zza(final GoogleApiClient googleApiClient, final zzqu zzquVar, final boolean z) {
        zzre.zt.zzg(googleApiClient).setResultCallback(new ResultCallback<Status>() { // from class: com.google.android.gms.internal.zzpy.4
            @Override // com.google.android.gms.common.api.ResultCallback
            public final /* synthetic */ void onResult(Status status) {
                Status status2 = status;
                com.google.android.gms.auth.api.signin.internal.zzk.zzbc(zzpy.this.mContext).zzagl();
                if (status2.isSuccess() && zzpy.this.isConnected()) {
                    zzpy zzpyVar = zzpy.this;
                    zzpyVar.disconnect();
                    zzpyVar.connect();
                }
                zzquVar.zzc((zzqu) status2);
                if (z) {
                    googleApiClient.disconnect();
                }
            }
        });
    }

    private void zzfk(int i) {
        if (this.um == null) {
            this.um = Integer.valueOf(i);
        } else if (this.um.intValue() != i) {
            String valueOf = String.valueOf(zzfl(i));
            String valueOf2 = String.valueOf(zzfl(this.um.intValue()));
            throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 51 + String.valueOf(valueOf2).length()).append("Cannot use sign-in mode: ").append(valueOf).append(". Mode was already set to ").append(valueOf2).toString());
        }
        if (this.ub != null) {
            return;
        }
        boolean z = false;
        boolean z2 = false;
        for (Api.zze zzeVar : this.ui.values()) {
            if (zzeVar.zzafk()) {
                z2 = true;
            }
            z = zzeVar.zzafz() ? true : z;
        }
        switch (this.um.intValue()) {
            case 1:
                if (!z2) {
                    throw new IllegalStateException("SIGN_IN_MODE_REQUIRED cannot be used on a GoogleApiClient that does not contain any authenticated APIs. Use connect() instead.");
                }
                if (z) {
                    throw new IllegalStateException("Cannot use SIGN_IN_MODE_REQUIRED with GOOGLE_SIGN_IN_API. Use connect(SIGN_IN_MODE_OPTIONAL) instead.");
                }
                break;
            case 2:
                if (z2) {
                    this.ub = zzpq.zza(this.mContext, this, this.tr, this.zzahv, this.sh, this.ui, this.tN, this.tO, this.si, this.ul);
                    return;
                }
                break;
        }
        this.ub = new zzqa(this.mContext, this, this.tr, this.zzahv, this.sh, this.ui, this.tN, this.tO, this.si, this.ul, this);
    }

    private static String zzfl(int i) {
        switch (i) {
            case 1:
                return "SIGN_IN_MODE_REQUIRED";
            case 2:
                return "SIGN_IN_MODE_OPTIONAL";
            case 3:
                return "SIGN_IN_MODE_NONE";
            default:
                return ChinesePredictionDataSource.UNKNOWN;
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final void disconnect() {
        this.tr.lock();
        try {
            this.uo.release();
            if (this.ub != null) {
                this.ub.disconnect();
            }
            this.uk.release();
            for (zzpm.zza<?, ?> zzaVar : this.uc) {
                zzaVar.zza((zzqy.zzb) null);
                zzaVar.cancel();
            }
            this.uc.clear();
            if (this.ub == null) {
                return;
            }
            zzapw();
            this.ua.zzasw();
        } finally {
            this.tr.unlock();
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final Context getContext() {
        return this.mContext;
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final Looper getLooper() {
        return this.zzahv;
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final boolean isConnected() {
        return this.ub != null && this.ub.isConnected();
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final boolean isConnecting() {
        return this.ub != null && this.ub.isConnecting();
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final void registerConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        this.ua.registerConnectionFailedListener(onConnectionFailedListener);
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final <C extends Api.zze> C zza(Api.zzc<C> zzcVar) {
        C c = (C) this.ui.get(zzcVar);
        com.google.android.gms.common.internal.zzab.zzb(c, "Appropriate Api was not requested.");
        return c;
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final void zza(zzqx zzqxVar) {
        this.tr.lock();
        try {
            if (this.un == null) {
                this.un = new HashSet();
            }
            this.un.add(zzqxVar);
        } finally {
            this.tr.unlock();
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final boolean zza(Api<?> api) {
        return this.ui.containsKey(api.zzans());
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final boolean zza(zzqt zzqtVar) {
        return this.ub != null && this.ub.zza(zzqtVar);
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final void zzaof() {
        if (this.ub != null) {
            this.ub.zzaof();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean zzapx() {
        this.tr.lock();
        try {
            if (this.un != null) {
                r0 = this.un.isEmpty() ? false : true;
            }
            return r0;
        } finally {
            this.tr.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String zzapy() {
        StringWriter stringWriter = new StringWriter();
        dump("", null, new PrintWriter(stringWriter), null);
        return stringWriter.toString();
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final void zzb(zzqx zzqxVar) {
        this.tr.lock();
        try {
            if (this.un == null) {
                Log.wtf("GoogleApiClientImpl", "Attempted to remove pending transform when no transforms are registered.", new Exception());
            } else if (!this.un.remove(zzqxVar)) {
                Log.wtf("GoogleApiClientImpl", "Failed to remove pending transform - this may lead to memory leaks!", new Exception());
            } else if (!zzapx()) {
                this.ub.zzapb();
            }
        } finally {
            this.tr.unlock();
        }
    }

    public zzpy(Context context, Lock lock, Looper looper, com.google.android.gms.common.internal.zzg zzgVar, GoogleApiAvailability googleApiAvailability, Api.zza<? extends zzvu, zzvv> zzaVar, Map<Api<?>, Integer> map, List<GoogleApiClient.ConnectionCallbacks> list, List<GoogleApiClient.OnConnectionFailedListener> list2, Map<Api.zzc<?>, Api.zze> map2, int i, int i2, ArrayList<zzpp> arrayList) {
        this.um = null;
        this.mContext = context;
        this.tr = lock;
        this.ua = new com.google.android.gms.common.internal.zzl(looper, this.up);
        this.zzahv = looper;
        this.ug = new zza(looper);
        this.sh = googleApiAvailability;
        this.sf = i;
        if (this.sf >= 0) {
            this.um = Integer.valueOf(i2);
        }
        this.tO = map;
        this.ui = map2;
        this.ul = arrayList;
        this.uo = new zzqy(this.ui);
        for (GoogleApiClient.ConnectionCallbacks connectionCallbacks : list) {
            com.google.android.gms.common.internal.zzl zzlVar = this.ua;
            com.google.android.gms.common.internal.zzab.zzy(connectionCallbacks);
            synchronized (zzlVar.zzail) {
                if (zzlVar.yF.contains(connectionCallbacks)) {
                    String valueOf = String.valueOf(connectionCallbacks);
                    Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 62).append("registerConnectionCallbacks(): listener ").append(valueOf).append(" is already registered").toString());
                } else {
                    zzlVar.yF.add(connectionCallbacks);
                }
            }
            if (zzlVar.yE.isConnected()) {
                zzlVar.mHandler.sendMessage(zzlVar.mHandler.obtainMessage(1, connectionCallbacks));
            }
        }
        Iterator<GoogleApiClient.OnConnectionFailedListener> it = list2.iterator();
        while (it.hasNext()) {
            this.ua.registerConnectionFailedListener(it.next());
        }
        this.tN = zzgVar;
        this.si = zzaVar;
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final <A extends Api.zzb, R extends Result, T extends zzpm.zza<R, A>> T zzc(T t) {
        com.google.android.gms.common.internal.zzab.zzb(t.sJ != null, "This task can not be enqueued (it's probably a Batch or malformed)");
        boolean containsKey = this.ui.containsKey(t.sJ);
        String str = t.pN != null ? t.pN.mName : "the API";
        com.google.android.gms.common.internal.zzab.zzb(containsKey, new StringBuilder(String.valueOf(str).length() + 65).append("GoogleApiClient is not configured to use ").append(str).append(" required for this call.").toString());
        this.tr.lock();
        try {
            if (this.ub == null) {
                this.uc.add(t);
            } else {
                t = (T) this.ub.zzc(t);
            }
            return t;
        } finally {
            this.tr.unlock();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final <A extends Api.zzb, T extends zzpm.zza<? extends Result, A>> T zzd(T t) {
        com.google.android.gms.common.internal.zzab.zzb(t.sJ != null, "This task can not be executed (it's probably a Batch or malformed)");
        boolean containsKey = this.ui.containsKey(t.sJ);
        String str = t.pN != null ? t.pN.mName : "the API";
        com.google.android.gms.common.internal.zzab.zzb(containsKey, new StringBuilder(String.valueOf(str).length() + 65).append("GoogleApiClient is not configured to use ").append(str).append(" required for this call.").toString());
        this.tr.lock();
        try {
            if (this.ub == null) {
                throw new IllegalStateException("GoogleApiClient is not connected yet.");
            }
            if (this.ud) {
                this.uc.add(t);
                while (!this.uc.isEmpty()) {
                    zzpm.zza<?, ?> remove = this.uc.remove();
                    this.uo.zzg(remove);
                    remove.zzz(Status.ss);
                }
            } else {
                t = (T) this.ub.zzd(t);
            }
            return t;
        } finally {
            this.tr.unlock();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:33:0x006c, code lost:            r0 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0072, code lost:            throw r0;     */
    @Override // com.google.android.gms.common.api.GoogleApiClient
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void connect() {
        /*
            r5 = this;
            r4 = 2
            r1 = 1
            r0 = 0
            java.util.concurrent.locks.Lock r2 = r5.tr
            r2.lock()
            int r2 = r5.sf     // Catch: java.lang.Throwable -> L6c
            if (r2 < 0) goto L56
            java.lang.Integer r2 = r5.um     // Catch: java.lang.Throwable -> L6c
            if (r2 == 0) goto L54
            r2 = r1
        L11:
            java.lang.String r3 = "Sign-in mode should have been set explicitly by auto-manage."
            com.google.android.gms.common.internal.zzab.zza(r2, r3)     // Catch: java.lang.Throwable -> L6c
        L17:
            java.lang.Integer r2 = r5.um     // Catch: java.lang.Throwable -> L6c
            int r2 = r2.intValue()     // Catch: java.lang.Throwable -> L6c
            java.util.concurrent.locks.Lock r3 = r5.tr     // Catch: java.lang.Throwable -> L6c
            r3.lock()     // Catch: java.lang.Throwable -> L6c
            r3 = 3
            if (r2 == r3) goto L29
            if (r2 == r1) goto L29
            if (r2 != r4) goto L2a
        L29:
            r0 = r1
        L2a:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L84
            r3 = 33
            r1.<init>(r3)     // Catch: java.lang.Throwable -> L84
            java.lang.String r3 = "Illegal sign-in mode: "
            java.lang.StringBuilder r1 = r1.append(r3)     // Catch: java.lang.Throwable -> L84
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> L84
            java.lang.String r1 = r1.toString()     // Catch: java.lang.Throwable -> L84
            com.google.android.gms.common.internal.zzab.zzb(r0, r1)     // Catch: java.lang.Throwable -> L84
            r5.zzfk(r2)     // Catch: java.lang.Throwable -> L84
            r5.zzapt()     // Catch: java.lang.Throwable -> L84
            java.util.concurrent.locks.Lock r0 = r5.tr     // Catch: java.lang.Throwable -> L6c
            r0.unlock()     // Catch: java.lang.Throwable -> L6c
            java.util.concurrent.locks.Lock r0 = r5.tr
            r0.unlock()
            return
        L54:
            r2 = r0
            goto L11
        L56:
            java.lang.Integer r2 = r5.um     // Catch: java.lang.Throwable -> L6c
            if (r2 != 0) goto L73
            java.util.Map<com.google.android.gms.common.api.Api$zzc<?>, com.google.android.gms.common.api.Api$zze> r2 = r5.ui     // Catch: java.lang.Throwable -> L6c
            java.util.Collection r2 = r2.values()     // Catch: java.lang.Throwable -> L6c
            r3 = 0
            int r2 = zza(r2, r3)     // Catch: java.lang.Throwable -> L6c
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch: java.lang.Throwable -> L6c
            r5.um = r2     // Catch: java.lang.Throwable -> L6c
            goto L17
        L6c:
            r0 = move-exception
            java.util.concurrent.locks.Lock r1 = r5.tr
            r1.unlock()
            throw r0
        L73:
            java.lang.Integer r2 = r5.um     // Catch: java.lang.Throwable -> L6c
            int r2 = r2.intValue()     // Catch: java.lang.Throwable -> L6c
            if (r2 != r4) goto L17
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch: java.lang.Throwable -> L6c
            java.lang.String r1 = "Cannot call connect() when SignInMode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead."
            r0.<init>(r1)     // Catch: java.lang.Throwable -> L6c
            throw r0     // Catch: java.lang.Throwable -> L6c
        L84:
            r0 = move-exception
            java.util.concurrent.locks.Lock r1 = r5.tr     // Catch: java.lang.Throwable -> L6c
            r1.unlock()     // Catch: java.lang.Throwable -> L6c
            throw r0     // Catch: java.lang.Throwable -> L6c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzpy.connect():void");
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final ConnectionResult blockingConnect() {
        com.google.android.gms.common.internal.zzab.zza(Looper.myLooper() != Looper.getMainLooper(), "blockingConnect must not be called on the UI thread");
        this.tr.lock();
        try {
            if (this.sf >= 0) {
                com.google.android.gms.common.internal.zzab.zza(this.um != null, "Sign-in mode should have been set explicitly by auto-manage.");
            } else if (this.um == null) {
                this.um = Integer.valueOf(zza(this.ui.values(), false));
            } else if (this.um.intValue() == 2) {
                throw new IllegalStateException("Cannot call blockingConnect() when sign-in mode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
            }
            zzfk(this.um.intValue());
            this.ua.yI = true;
            return this.ub.blockingConnect();
        } finally {
            this.tr.unlock();
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final PendingResult<Status> clearDefaultAccountAndReconnect() {
        com.google.android.gms.common.internal.zzab.zza(isConnected(), "GoogleApiClient is not connected yet.");
        com.google.android.gms.common.internal.zzab.zza(this.um.intValue() != 2, "Cannot use clearDefaultAccountAndReconnect with GOOGLE_SIGN_IN_API");
        final zzqu zzquVar = new zzqu(this);
        if (this.ui.containsKey(zzre.bJ)) {
            zza(this, zzquVar, false);
        } else {
            final AtomicReference atomicReference = new AtomicReference();
            GoogleApiClient.Builder addOnConnectionFailedListener = new GoogleApiClient.Builder(this.mContext).addApi(zzre.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() { // from class: com.google.android.gms.internal.zzpy.2
                @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
                public final void onConnected(Bundle bundle) {
                    zzpy.this.zza((GoogleApiClient) atomicReference.get(), zzquVar, true);
                }

                @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
                public final void onConnectionSuspended(int i) {
                }
            }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() { // from class: com.google.android.gms.internal.zzpy.3
                @Override // com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
                public final void onConnectionFailed(ConnectionResult connectionResult) {
                    zzquVar.zzc((zzqu) new Status(8));
                }
            });
            zza zzaVar = this.ug;
            com.google.android.gms.common.internal.zzab.zzb(zzaVar, "Handler must not be null");
            addOnConnectionFailedListener.zzahv = zzaVar.getLooper();
            GoogleApiClient build = addOnConnectionFailedListener.build();
            atomicReference.set(build);
            build.connect();
        }
        return zzquVar;
    }

    private void zzapt() {
        this.ua.yI = true;
        this.ub.connect();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean zzapw() {
        if (!this.ud) {
            return false;
        }
        this.ud = false;
        this.ug.removeMessages(2);
        this.ug.removeMessages(1);
        if (this.uh != null) {
            this.uh.unregister();
            this.uh = null;
        }
        return true;
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final void unregisterConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        com.google.android.gms.common.internal.zzl zzlVar = this.ua;
        com.google.android.gms.common.internal.zzab.zzy(onConnectionFailedListener);
        synchronized (zzlVar.zzail) {
            if (!zzlVar.yH.remove(onConnectionFailedListener)) {
                String valueOf = String.valueOf(onConnectionFailedListener);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 57).append("unregisterConnectionFailedListener(): listener ").append(valueOf).append(" not found").toString());
            }
        }
    }

    @Override // com.google.android.gms.internal.zzqh.zza
    public final void zzm(Bundle bundle) {
        while (!this.uc.isEmpty()) {
            zzd((zzpy) this.uc.remove());
        }
        com.google.android.gms.common.internal.zzl zzlVar = this.ua;
        com.google.android.gms.common.internal.zzab.zza(Looper.myLooper() == zzlVar.mHandler.getLooper(), "onConnectionSuccess must only be called on the Handler thread");
        synchronized (zzlVar.zzail) {
            com.google.android.gms.common.internal.zzab.zzbn(!zzlVar.yK);
            zzlVar.mHandler.removeMessages(1);
            zzlVar.yK = true;
            com.google.android.gms.common.internal.zzab.zzbn(zzlVar.yG.size() == 0);
            ArrayList arrayList = new ArrayList(zzlVar.yF);
            int i = zzlVar.yJ.get();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                GoogleApiClient.ConnectionCallbacks connectionCallbacks = (GoogleApiClient.ConnectionCallbacks) it.next();
                if (!zzlVar.yI || !zzlVar.yE.isConnected() || zzlVar.yJ.get() != i) {
                    break;
                } else if (!zzlVar.yG.contains(connectionCallbacks)) {
                    connectionCallbacks.onConnected(bundle);
                }
            }
            zzlVar.yG.clear();
            zzlVar.yK = false;
        }
    }

    @Override // com.google.android.gms.internal.zzqh.zza
    public final void zzd(ConnectionResult connectionResult) {
        if (!this.sh.zzc(this.mContext, connectionResult.ok)) {
            zzapw();
        }
        if (this.ud) {
            return;
        }
        com.google.android.gms.common.internal.zzl zzlVar = this.ua;
        com.google.android.gms.common.internal.zzab.zza(Looper.myLooper() == zzlVar.mHandler.getLooper(), "onConnectionFailure must only be called on the Handler thread");
        zzlVar.mHandler.removeMessages(1);
        synchronized (zzlVar.zzail) {
            ArrayList arrayList = new ArrayList(zzlVar.yH);
            int i = zzlVar.yJ.get();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = (GoogleApiClient.OnConnectionFailedListener) it.next();
                if (!zzlVar.yI || zzlVar.yJ.get() != i) {
                    break;
                } else if (zzlVar.yH.contains(onConnectionFailedListener)) {
                    onConnectionFailedListener.onConnectionFailed(connectionResult);
                }
            }
        }
        this.ua.zzasw();
    }

    @Override // com.google.android.gms.internal.zzqh.zza
    public final void zzc(int i, boolean z) {
        if (i == 1 && !z && !this.ud) {
            this.ud = true;
            if (this.uh == null) {
                this.uh = GoogleApiAvailability.zza(this.mContext.getApplicationContext(), new zzb(this));
            }
            this.ug.sendMessageDelayed(this.ug.obtainMessage(1), this.ue);
            this.ug.sendMessageDelayed(this.ug.obtainMessage(2), this.uf);
        }
        for (zzpm.zza zzaVar : (zzpm.zza[]) this.uo.vG.toArray(zzqy.vF)) {
            zzaVar.zzaa(new Status(8, "The connection to Google Play services was lost"));
        }
        com.google.android.gms.common.internal.zzl zzlVar = this.ua;
        com.google.android.gms.common.internal.zzab.zza(Looper.myLooper() == zzlVar.mHandler.getLooper(), "onUnintentionalDisconnection must only be called on the Handler thread");
        zzlVar.mHandler.removeMessages(1);
        synchronized (zzlVar.zzail) {
            zzlVar.yK = true;
            ArrayList arrayList = new ArrayList(zzlVar.yF);
            int i2 = zzlVar.yJ.get();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                GoogleApiClient.ConnectionCallbacks connectionCallbacks = (GoogleApiClient.ConnectionCallbacks) it.next();
                if (!zzlVar.yI || zzlVar.yJ.get() != i2) {
                    break;
                } else if (zzlVar.yF.contains(connectionCallbacks)) {
                    connectionCallbacks.onConnectionSuspended(i);
                }
            }
            zzlVar.yG.clear();
            zzlVar.yK = false;
        }
        this.ua.zzasw();
        if (i == 2) {
            zzapt();
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.append((CharSequence) str).append("mContext=").println(this.mContext);
        printWriter.append((CharSequence) str).append("mResuming=").print(this.ud);
        printWriter.append(" mWorkQueue.size()=").print(this.uc.size());
        printWriter.append(" mUnconsumedApiCalls.size()=").println(this.uo.vG.size());
        if (this.ub != null) {
            this.ub.dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    static /* synthetic */ void zza(zzpy zzpyVar) {
        zzpyVar.tr.lock();
        try {
            if (zzpyVar.ud) {
                zzpyVar.zzapt();
            }
        } finally {
            zzpyVar.tr.unlock();
        }
    }

    static /* synthetic */ void zzb(zzpy zzpyVar) {
        zzpyVar.tr.lock();
        try {
            if (zzpyVar.zzapw()) {
                zzpyVar.zzapt();
            }
        } finally {
            zzpyVar.tr.unlock();
        }
    }
}
