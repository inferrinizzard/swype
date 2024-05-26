package com.google.android.gms.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.internal.zzpm;
import com.google.android.gms.internal.zzqh;
import com.nuance.swype.input.ThemeManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/* loaded from: classes.dex */
public final class zzqa implements zzqh {
    final Context mContext;
    final Api.zza<? extends zzvu, zzvv> si;
    final com.google.android.gms.common.internal.zzg tN;
    final Map<Api<?>, Integer> tO;
    final zzpy th;
    final Lock tr;
    final com.google.android.gms.common.zzc tz;
    int uA;
    final zzqh.zza uB;
    final Map<Api.zzc<?>, Api.zze> ui;
    final Condition uv;
    final zzb uw;
    volatile zzpz uy;
    final Map<Api.zzc<?>, ConnectionResult> ux = new HashMap();
    private ConnectionResult uz = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class zzb extends Handler {
        zzb(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    ((zza) message.obj).zzd(zzqa.this);
                    return;
                case 2:
                    throw ((RuntimeException) message.obj);
                default:
                    Log.w("GACStateManager", new StringBuilder(31).append("Unknown message id: ").append(message.what).toString());
                    return;
            }
        }
    }

    @Override // com.google.android.gms.internal.zzqh
    public final ConnectionResult blockingConnect() {
        connect();
        while (isConnecting()) {
            try {
                this.uv.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new ConnectionResult(15, null);
            }
        }
        return isConnected() ? ConnectionResult.rb : this.uz != null ? this.uz : new ConnectionResult(13, null);
    }

    @Override // com.google.android.gms.internal.zzqh
    public final void connect() {
        this.uy.connect();
    }

    @Override // com.google.android.gms.internal.zzqh
    public final void disconnect() {
        if (this.uy.disconnect()) {
            this.ux.clear();
        }
    }

    @Override // com.google.android.gms.internal.zzqh
    public final boolean isConnected() {
        return this.uy instanceof zzpv;
    }

    @Override // com.google.android.gms.internal.zzqh
    public final boolean isConnecting() {
        return this.uy instanceof zzpw;
    }

    public final void onConnectionSuspended(int i) {
        this.tr.lock();
        try {
            this.uy.onConnectionSuspended(i);
        } finally {
            this.tr.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void zza(zza zzaVar) {
        this.uw.sendMessage(this.uw.obtainMessage(1, zzaVar));
    }

    @Override // com.google.android.gms.internal.zzqh
    public final boolean zza(zzqt zzqtVar) {
        return false;
    }

    @Override // com.google.android.gms.internal.zzqh
    public final void zzaof() {
    }

    @Override // com.google.android.gms.internal.zzqh
    public final <A extends Api.zzb, R extends Result, T extends zzpm.zza<R, A>> T zzc(T t) {
        t.zzaow();
        return (T) this.uy.zzc(t);
    }

    @Override // com.google.android.gms.internal.zzqh
    public final <A extends Api.zzb, T extends zzpm.zza<? extends Result, A>> T zzd(T t) {
        t.zzaow();
        return (T) this.uy.zzd(t);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void zzi(ConnectionResult connectionResult) {
        this.tr.lock();
        try {
            this.uz = connectionResult;
            this.uy = new zzpx(this);
            this.uy.begin();
            this.uv.signalAll();
        } finally {
            this.tr.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static abstract class zza {
        private final zzpz uC;

        /* JADX INFO: Access modifiers changed from: protected */
        public zza(zzpz zzpzVar) {
            this.uC = zzpzVar;
        }

        protected abstract void zzapl();

        public final void zzd(zzqa zzqaVar) {
            zzqaVar.tr.lock();
            try {
                if (zzqaVar.uy != this.uC) {
                    return;
                }
                zzapl();
            } finally {
                zzqaVar.tr.unlock();
            }
        }
    }

    public zzqa(Context context, zzpy zzpyVar, Lock lock, Looper looper, com.google.android.gms.common.zzc zzcVar, Map<Api.zzc<?>, Api.zze> map, com.google.android.gms.common.internal.zzg zzgVar, Map<Api<?>, Integer> map2, Api.zza<? extends zzvu, zzvv> zzaVar, ArrayList<zzpp> arrayList, zzqh.zza zzaVar2) {
        this.mContext = context;
        this.tr = lock;
        this.tz = zzcVar;
        this.ui = map;
        this.tN = zzgVar;
        this.tO = map2;
        this.si = zzaVar;
        this.th = zzpyVar;
        this.uB = zzaVar2;
        Iterator<zzpp> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().tg = this;
        }
        this.uw = new zzb(looper);
        this.uv = lock.newCondition();
        this.uy = new zzpx(this);
    }

    @Override // com.google.android.gms.internal.zzqh
    public final void zzapb() {
        if (isConnected()) {
            zzpv zzpvVar = (zzpv) this.uy;
            if (zzpvVar.tx) {
                zzpvVar.tx = false;
                zzpvVar.tw.th.uo.release();
                zzpvVar.disconnect();
            }
        }
    }

    @Override // com.google.android.gms.internal.zzqh
    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String concat = String.valueOf(str).concat(ThemeManager.NO_PRICE);
        printWriter.append((CharSequence) str).append("mState=").println(this.uy);
        for (Api<?> api : this.tO.keySet()) {
            printWriter.append((CharSequence) str).append((CharSequence) api.mName).println(":");
            this.ui.get(api.zzans()).dump(concat, fileDescriptor, printWriter, strArr);
        }
    }
}
