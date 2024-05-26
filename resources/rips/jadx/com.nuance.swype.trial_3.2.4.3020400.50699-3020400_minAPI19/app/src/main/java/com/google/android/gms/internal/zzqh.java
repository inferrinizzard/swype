package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.internal.zzpm;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: classes.dex */
public interface zzqh {

    /* loaded from: classes.dex */
    public interface zza {
        void zzc(int i, boolean z);

        void zzd(ConnectionResult connectionResult);

        void zzm(Bundle bundle);
    }

    ConnectionResult blockingConnect();

    void connect();

    void disconnect();

    void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    boolean isConnected();

    boolean isConnecting();

    boolean zza(zzqt zzqtVar);

    void zzaof();

    void zzapb();

    <A extends Api.zzb, R extends Result, T extends zzpm.zza<R, A>> T zzc(T t);

    <A extends Api.zzb, T extends zzpm.zza<? extends Result, A>> T zzd(T t);
}
