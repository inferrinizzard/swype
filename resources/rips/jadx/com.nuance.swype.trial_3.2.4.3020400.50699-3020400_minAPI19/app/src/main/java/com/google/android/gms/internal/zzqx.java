package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.ResultTransform;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.TransformedResult;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public final class zzqx<R extends Result> extends TransformedResult<R> implements ResultCallback<R> {
    final Object sT;
    final WeakReference<GoogleApiClient> sV;
    Status vA;
    final zza vB;
    boolean vC;
    ResultTransform<? super R, ? extends Result> vw;
    zzqx<? extends Result> vx;
    volatile ResultCallbacks<? super R> vy;
    PendingResult<R> vz;

    private boolean zzaqy() {
        return (this.vy == null || this.sV.get() == null) ? false : true;
    }

    @Override // com.google.android.gms.common.api.ResultCallback
    public final void onResult(final R r) {
        synchronized (this.sT) {
            if (!r.getStatus().isSuccess()) {
                zzac(r.getStatus());
            } else if (this.vw != null) {
                zzqr.zzaqc().submit(new Runnable() { // from class: com.google.android.gms.internal.zzqx.1
                    @Override // java.lang.Runnable
                    public final void run() {
                        try {
                            try {
                                zzpo.sS.set(true);
                                zzqx.this.vB.sendMessage(zzqx.this.vB.obtainMessage(0, zzqx.this.vw.onSuccess$1e5d55c()));
                                zzpo.sS.set(false);
                                GoogleApiClient googleApiClient = zzqx.this.sV.get();
                                if (googleApiClient != null) {
                                    googleApiClient.zzb(zzqx.this);
                                }
                            } catch (RuntimeException e) {
                                zzqx.this.vB.sendMessage(zzqx.this.vB.obtainMessage(1, e));
                                zzpo.sS.set(false);
                                GoogleApiClient googleApiClient2 = zzqx.this.sV.get();
                                if (googleApiClient2 != null) {
                                    googleApiClient2.zzb(zzqx.this);
                                }
                            }
                        } finally {
                        }
                    }
                });
            } else if (zzaqy()) {
                ResultCallbacks<? super R> resultCallbacks = this.vy;
            }
        }
    }

    final void zzac(Status status) {
        synchronized (this.sT) {
            this.vA = status;
            zzad(this.vA);
        }
    }

    final void zzad(Status status) {
        synchronized (this.sT) {
            if (this.vw != null) {
                com.google.android.gms.common.internal.zzab.zzb(status, "onFailure must not return null");
                this.vx.zzac(status);
            } else if (zzaqy()) {
                ResultCallbacks<? super R> resultCallbacks = this.vy;
            }
        }
    }

    /* loaded from: classes.dex */
    private final class zza extends Handler {
        final /* synthetic */ zzqx vE;

        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    PendingResult<R> pendingResult = (PendingResult) message.obj;
                    synchronized (this.vE.sT) {
                        if (pendingResult == null) {
                            this.vE.vx.zzac(new Status(13, "Transform returned null"));
                        } else if (pendingResult instanceof zzqs) {
                            this.vE.vx.zzac(((zzqs) pendingResult).bY);
                        } else {
                            zzqx<? extends Result> zzqxVar = this.vE.vx;
                            synchronized (zzqxVar.sT) {
                                zzqxVar.vz = pendingResult;
                                if (zzqxVar.vw != null || zzqxVar.vy != null) {
                                    GoogleApiClient googleApiClient = zzqxVar.sV.get();
                                    if (!zzqxVar.vC && zzqxVar.vw != null && googleApiClient != null) {
                                        googleApiClient.zza(zzqxVar);
                                        zzqxVar.vC = true;
                                    }
                                    if (zzqxVar.vA != null) {
                                        zzqxVar.zzad(zzqxVar.vA);
                                    } else if (zzqxVar.vz != null) {
                                        zzqxVar.vz.setResultCallback(zzqxVar);
                                    }
                                }
                            }
                        }
                    }
                    return;
                case 1:
                    RuntimeException runtimeException = (RuntimeException) message.obj;
                    String valueOf = String.valueOf(runtimeException.getMessage());
                    Log.e("TransformedResultImpl", valueOf.length() != 0 ? "Runtime exception on the transformation worker thread: ".concat(valueOf) : new String("Runtime exception on the transformation worker thread: "));
                    throw runtimeException;
                default:
                    Log.e("TransformedResultImpl", new StringBuilder(70).append("TransformationResultHandler received unknown message type: ").append(message.what).toString());
                    return;
            }
        }
    }
}
