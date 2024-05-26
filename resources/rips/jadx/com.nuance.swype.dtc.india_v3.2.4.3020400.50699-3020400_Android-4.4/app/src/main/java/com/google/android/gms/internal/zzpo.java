package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public abstract class zzpo<R extends Result> extends PendingResult<R> {
    static final ThreadLocal<Boolean> sS = new ThreadLocal<Boolean>() { // from class: com.google.android.gms.internal.zzpo.1
        @Override // java.lang.ThreadLocal
        protected final /* synthetic */ Boolean initialValue() {
            return false;
        }
    };
    private final Object sT;
    protected final zza<R> sU;
    protected final WeakReference<GoogleApiClient> sV;
    private final ArrayList<PendingResult.zza> sW;
    private ResultCallback<? super R> sX;
    private zzb sY;
    private volatile boolean sZ;
    private R sm;
    private boolean ta;
    private com.google.android.gms.common.internal.zzr tb;
    private volatile zzqx<R> tc;
    private boolean td;
    private boolean zzak;
    private final CountDownLatch zzale;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class zzb {
        private zzb() {
        }

        /* synthetic */ zzb(zzpo zzpoVar, byte b) {
            this();
        }

        protected final void finalize() throws Throwable {
            zzpo.zze(zzpo.this.sm);
            super.finalize();
        }
    }

    @Deprecated
    zzpo() {
        this.sT = new Object();
        this.zzale = new CountDownLatch(1);
        this.sW = new ArrayList<>();
        this.td = false;
        this.sU = new zza<>(Looper.getMainLooper());
        this.sV = new WeakReference<>(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Deprecated
    public zzpo(Looper looper) {
        this.sT = new Object();
        this.zzale = new CountDownLatch(1);
        this.sW = new ArrayList<>();
        this.td = false;
        this.sU = new zza<>(looper);
        this.sV = new WeakReference<>(null);
    }

    public zzpo(GoogleApiClient googleApiClient) {
        this.sT = new Object();
        this.zzale = new CountDownLatch(1);
        this.sW = new ArrayList<>();
        this.td = false;
        this.sU = new zza<>(googleApiClient != null ? googleApiClient.getLooper() : Looper.getMainLooper());
        this.sV = new WeakReference<>(googleApiClient);
    }

    private R get() {
        R r;
        synchronized (this.sT) {
            com.google.android.gms.common.internal.zzab.zza(this.sZ ? false : true, "Result has already been consumed.");
            com.google.android.gms.common.internal.zzab.zza(isReady(), "Result is not ready.");
            r = this.sm;
            this.sm = null;
            this.sX = null;
            this.sZ = true;
        }
        zzaos();
        return r;
    }

    private boolean isCanceled() {
        boolean z;
        synchronized (this.sT) {
            z = this.zzak;
        }
        return z;
    }

    public static void zze(Result result) {
        if (result instanceof Releasable) {
        }
    }

    public final R await$140a99d0(TimeUnit timeUnit) {
        com.google.android.gms.common.internal.zzab.zza(0 <= 0 || Looper.myLooper() != Looper.getMainLooper(), "await must not be called on the UI thread when time is greater than zero.");
        com.google.android.gms.common.internal.zzab.zza(!this.sZ, "Result has already been consumed.");
        com.google.android.gms.common.internal.zzab.zza(this.tc == null, "Cannot await if then() has been called.");
        try {
            if (!this.zzale.await(0L, timeUnit)) {
                zzaa(Status.st);
            }
        } catch (InterruptedException e) {
            zzaa(Status.sr);
        }
        com.google.android.gms.common.internal.zzab.zza(isReady(), "Result is not ready.");
        return get();
    }

    public final void cancel() {
        synchronized (this.sT) {
            if (this.zzak || this.sZ) {
                return;
            }
            this.zzak = true;
            zzd(zzc(Status.su));
        }
    }

    public final boolean isReady() {
        return this.zzale.getCount() == 0;
    }

    @Override // com.google.android.gms.common.api.PendingResult
    public final void setResultCallback(ResultCallback<? super R> resultCallback) {
        synchronized (this.sT) {
            if (resultCallback == null) {
                this.sX = null;
                return;
            }
            com.google.android.gms.common.internal.zzab.zza(!this.sZ, "Result has already been consumed.");
            com.google.android.gms.common.internal.zzab.zza(this.tc == null, "Cannot set callbacks if then() has been called.");
            if (isCanceled()) {
                return;
            }
            if (isReady()) {
                this.sU.zza(resultCallback, get());
            } else {
                this.sX = resultCallback;
            }
        }
    }

    @Override // com.google.android.gms.common.api.PendingResult
    public final void zza(PendingResult.zza zzaVar) {
        com.google.android.gms.common.internal.zzab.zza(!this.sZ, "Result has already been consumed.");
        com.google.android.gms.common.internal.zzab.zzb(zzaVar != null, "Callback cannot be null.");
        synchronized (this.sT) {
            if (isReady()) {
                this.sm.getStatus();
                zzaVar.zzv$e184e5d();
            } else {
                this.sW.add(zzaVar);
            }
        }
    }

    public final void zzaa(Status status) {
        synchronized (this.sT) {
            if (!isReady()) {
                zzc((zzpo<R>) zzc(status));
                this.ta = true;
            }
        }
    }

    protected void zzaos() {
    }

    public final boolean zzaov() {
        boolean isCanceled;
        synchronized (this.sT) {
            if (this.sV.get() == null || !this.td) {
                cancel();
            }
            isCanceled = isCanceled();
        }
        return isCanceled;
    }

    public final void zzaow() {
        this.td = this.td || sS.get().booleanValue();
    }

    public abstract R zzc(Status status);

    public final void zzc(R r) {
        synchronized (this.sT) {
            if (this.ta || this.zzak) {
                return;
            }
            if (isReady()) {
            }
            com.google.android.gms.common.internal.zzab.zza(!isReady(), "Results have already been set");
            com.google.android.gms.common.internal.zzab.zza(this.sZ ? false : true, "Result has already been consumed");
            zzd(r);
        }
    }

    /* loaded from: classes.dex */
    public static class zza<R extends Result> extends Handler {
        public zza() {
            this(Looper.getMainLooper());
        }

        public zza(Looper looper) {
            super(looper);
        }

        public final void zza(ResultCallback<? super R> resultCallback, R r) {
            sendMessage(obtainMessage(1, new Pair(resultCallback, r)));
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Pair pair = (Pair) message.obj;
                    ResultCallback resultCallback = (ResultCallback) pair.first;
                    Result result = (Result) pair.second;
                    try {
                        resultCallback.onResult(result);
                        return;
                    } catch (RuntimeException e) {
                        zzpo.zze(result);
                        throw e;
                    }
                case 2:
                    ((zzpo) message.obj).zzaa(Status.st);
                    return;
                default:
                    Log.wtf("BasePendingResult", new StringBuilder(45).append("Don't know how to handle message: ").append(message.what).toString(), new Exception());
                    return;
            }
        }
    }

    private void zzd(R r) {
        this.sm = r;
        this.tb = null;
        this.zzale.countDown();
        this.sm.getStatus();
        if (this.zzak) {
            this.sX = null;
        } else if (this.sX != null) {
            this.sU.removeMessages(2);
            this.sU.zza(this.sX, get());
        } else if (this.sm instanceof Releasable) {
            this.sY = new zzb(this, (byte) 0);
        }
        Iterator<PendingResult.zza> it = this.sW.iterator();
        while (it.hasNext()) {
            it.next().zzv$e184e5d();
        }
        this.sW.clear();
    }
}
