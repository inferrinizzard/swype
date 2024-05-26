package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public final class zzl implements Handler.Callback {
    public final Handler mHandler;
    public final zza yE;
    public final ArrayList<GoogleApiClient.ConnectionCallbacks> yF = new ArrayList<>();
    public final ArrayList<GoogleApiClient.ConnectionCallbacks> yG = new ArrayList<>();
    public final ArrayList<GoogleApiClient.OnConnectionFailedListener> yH = new ArrayList<>();
    public volatile boolean yI = false;
    public final AtomicInteger yJ = new AtomicInteger(0);
    public boolean yK = false;
    public final Object zzail = new Object();

    /* loaded from: classes.dex */
    public interface zza {
        boolean isConnected();

        Bundle zzamh();
    }

    public zzl(Looper looper, zza zzaVar) {
        this.yE = zzaVar;
        this.mHandler = new Handler(looper, this);
    }

    @Override // android.os.Handler.Callback
    public final boolean handleMessage(Message message) {
        if (message.what != 1) {
            Log.wtf("GmsClientEvents", new StringBuilder(45).append("Don't know how to handle message: ").append(message.what).toString(), new Exception());
            return false;
        }
        GoogleApiClient.ConnectionCallbacks connectionCallbacks = (GoogleApiClient.ConnectionCallbacks) message.obj;
        synchronized (this.zzail) {
            if (this.yI && this.yE.isConnected() && this.yF.contains(connectionCallbacks)) {
                connectionCallbacks.onConnected(this.yE.zzamh());
            }
        }
        return true;
    }

    public final void registerConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        zzab.zzy(onConnectionFailedListener);
        synchronized (this.zzail) {
            if (this.yH.contains(onConnectionFailedListener)) {
                String valueOf = String.valueOf(onConnectionFailedListener);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 67).append("registerConnectionFailedListener(): listener ").append(valueOf).append(" is already registered").toString());
            } else {
                this.yH.add(onConnectionFailedListener);
            }
        }
    }

    public final void zzasw() {
        this.yI = false;
        this.yJ.incrementAndGet();
    }
}
