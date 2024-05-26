package com.google.android.gms.internal;

import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.clearcut.LogEventParcelable;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzpe;
import com.google.android.gms.internal.zzpm;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public final class zzpb implements com.google.android.gms.clearcut.zzc {
    private static ScheduledExecutorService qF;
    private GoogleApiClient gY;
    private final zza qI;
    private final Object qJ;
    private long qK;
    private final long qL;
    private ScheduledFuture<?> qM;
    private final Runnable qN;
    private final com.google.android.gms.common.util.zze zzaoc;
    private static final Object qE = new Object();
    private static final zze qG = new zze(0);
    private static final long qH = TimeUnit.MILLISECONDS.convert(2, TimeUnit.MINUTES);

    /* loaded from: classes.dex */
    public interface zza {
    }

    /* loaded from: classes.dex */
    public static class zzb implements zza {
    }

    /* loaded from: classes.dex */
    static abstract class zzc<R extends Result> extends zzpm.zza<R, zzpc> {
        public zzc(GoogleApiClient googleApiClient) {
            super(com.google.android.gms.clearcut.zzb.API, googleApiClient);
        }
    }

    /* loaded from: classes.dex */
    private static final class zze {
        private int mSize;

        private zze() {
            this.mSize = 0;
        }

        /* synthetic */ zze(byte b) {
            this();
        }

        public final synchronized void decrement() {
            if (this.mSize == 0) {
                throw new RuntimeException("too many decrements");
            }
            this.mSize--;
            if (this.mSize == 0) {
                notifyAll();
            }
        }

        public final synchronized void increment() {
            this.mSize++;
        }
    }

    public zzpb() {
        this(new com.google.android.gms.common.util.zzh(), qH, new zzb());
    }

    private zzpb(com.google.android.gms.common.util.zze zzeVar, long j, zza zzaVar) {
        this.qJ = new Object();
        this.qK = 0L;
        this.qM = null;
        this.gY = null;
        this.qN = new Runnable() { // from class: com.google.android.gms.internal.zzpb.1
            @Override // java.lang.Runnable
            public final void run() {
                synchronized (zzpb.this.qJ) {
                    if (zzpb.zzb$7fc76702() <= zzpb.this.zzaoc.elapsedRealtime() && zzpb.this.gY != null) {
                        Log.i("ClearcutLoggerApiImpl", "disconnect managed GoogleApiClient");
                        zzpb.this.gY.disconnect();
                        zzpb.zza$75eb1a6e(zzpb.this);
                    }
                }
            }
        };
        this.zzaoc = zzeVar;
        this.qL = j;
        this.qI = zzaVar;
    }

    static /* synthetic */ GoogleApiClient zza$75eb1a6e(zzpb zzpbVar) {
        zzpbVar.gY = null;
        return null;
    }

    private ScheduledExecutorService zzanc() {
        synchronized (qE) {
            if (qF == null) {
                qF = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() { // from class: com.google.android.gms.internal.zzpb.2
                    @Override // java.util.concurrent.ThreadFactory
                    public final Thread newThread(final Runnable runnable) {
                        return new Thread(new Runnable() { // from class: com.google.android.gms.internal.zzpb.2.1
                            @Override // java.lang.Runnable
                            public final void run() {
                                Process.setThreadPriority(10);
                                runnable.run();
                            }
                        }, "ClearcutLoggerApiImpl");
                    }
                });
            }
        }
        return qF;
    }

    static /* synthetic */ long zzb$7fc76702() {
        return 0L;
    }

    /* loaded from: classes.dex */
    static final class zzd extends zzc<Status> {
        private final LogEventParcelable qS;

        zzd(LogEventParcelable logEventParcelable, GoogleApiClient googleApiClient) {
            super(googleApiClient);
            this.qS = logEventParcelable;
        }

        public final boolean equals(Object obj) {
            if (obj instanceof zzd) {
                return this.qS.equals(((zzd) obj).qS);
            }
            return false;
        }

        public final String toString() {
            String valueOf = String.valueOf(this.qS);
            return new StringBuilder(String.valueOf(valueOf).length() + 12).append("MethodImpl(").append(valueOf).append(")").toString();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.internal.zzpo
        public final /* synthetic */ Result zzc(Status status) {
            return status;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.internal.zzpm.zza
        public final /* bridge */ /* synthetic */ void zza(zzpc zzpcVar) throws RemoteException {
            zzpc zzpcVar2 = zzpcVar;
            zzpe.zza zzaVar = new zzpe.zza() { // from class: com.google.android.gms.internal.zzpb.zzd.1
                @Override // com.google.android.gms.internal.zzpe
                public final void zzw(Status status) {
                    zzd.this.zzc((zzd) status);
                }
            };
            try {
                zzpb.zzb(this.qS);
                ((zzpf) zzpcVar2.zzasa()).zza(zzaVar, this.qS);
            } catch (RuntimeException e) {
                Log.e("ClearcutLoggerApiImpl", "derived ClearcutLogger.MessageProducer ", e);
                zzz(new Status(10, "MessageProducer"));
            }
        }
    }

    @Override // com.google.android.gms.clearcut.zzc
    public final PendingResult<Status> zza(final GoogleApiClient googleApiClient, LogEventParcelable logEventParcelable) {
        qG.increment();
        final zzd zzdVar = new zzd(logEventParcelable, googleApiClient);
        zzdVar.zza(new PendingResult.zza() { // from class: com.google.android.gms.internal.zzpb.4
            @Override // com.google.android.gms.common.api.PendingResult.zza
            public final void zzv$e184e5d() {
                zzpb.qG.decrement();
            }
        });
        zzanc().execute(new Runnable() { // from class: com.google.android.gms.internal.zzpb.3
            @Override // java.lang.Runnable
            public final void run() {
                googleApiClient.zzc(zzdVar);
            }
        });
        return zzdVar;
    }

    static /* synthetic */ void zzb(LogEventParcelable logEventParcelable) {
        if (logEventParcelable.qC != null && logEventParcelable.qB.bkh.length == 0) {
            logEventParcelable.qB.bkh = logEventParcelable.qC.zzanb();
        }
        if (logEventParcelable.qD != null && logEventParcelable.qB.bko.length == 0) {
            logEventParcelable.qB.bko = logEventParcelable.qD.zzanb();
        }
        logEventParcelable.qv = zzapv.zzf(logEventParcelable.qB);
    }
}
