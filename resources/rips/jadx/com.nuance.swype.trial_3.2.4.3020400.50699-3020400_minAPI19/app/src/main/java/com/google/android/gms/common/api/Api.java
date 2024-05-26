package com.google.android.gms.common.api;

import android.content.Context;
import android.content.Intent;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzd;
import com.google.android.gms.common.internal.zzq;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public final class Api<O extends ApiOptions> {
    public final String mName;
    final zza<?, O> rB;
    private final zzh<?, O> rC;
    private final zzf<?> rD;
    private final zzi<?> rE;

    /* loaded from: classes.dex */
    public interface ApiOptions {

        /* loaded from: classes.dex */
        public interface HasOptions extends ApiOptions {
        }

        /* loaded from: classes.dex */
        public interface NotRequiredOptions extends ApiOptions {
        }

        /* loaded from: classes.dex */
        public interface Optional extends HasOptions, NotRequiredOptions {
        }
    }

    /* loaded from: classes.dex */
    public static abstract class zza<T extends zze, O> extends zzd<T, O> {
        public abstract T zza(Context context, Looper looper, com.google.android.gms.common.internal.zzg zzgVar, O o, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener);
    }

    /* loaded from: classes.dex */
    public interface zzb {
    }

    /* loaded from: classes.dex */
    public static class zzc<C extends zzb> {
    }

    /* loaded from: classes.dex */
    public static abstract class zzd<T extends zzb, O> {
        public List<Scope> zzp(O o) {
            return Collections.emptyList();
        }
    }

    /* loaded from: classes.dex */
    public interface zze extends zzb {
        void disconnect();

        void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

        boolean isConnected();

        boolean isConnecting();

        void zza(zzd.zzf zzfVar);

        void zza(zzq zzqVar, Set<Scope> set);

        boolean zzafk();

        boolean zzafz();

        Intent zzaga();

        boolean zzanu();
    }

    /* loaded from: classes.dex */
    public static final class zzf<C extends zze> extends zzc<C> {
    }

    /* loaded from: classes.dex */
    public interface zzg<T extends IInterface> extends zzb {
        T zzbb$13514312();

        String zzqz();

        String zzra();
    }

    /* loaded from: classes.dex */
    public static abstract class zzh<T extends zzg, O> extends zzd<T, O> {
    }

    /* loaded from: classes.dex */
    public static final class zzi<C extends zzg> extends zzc<C> {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <C extends zze> Api(String str, zza<C, O> zzaVar, zzf<C> zzfVar) {
        zzab.zzb(zzaVar, "Cannot construct an Api with a null ClientBuilder");
        zzab.zzb(zzfVar, "Cannot construct an Api with a null ClientKey");
        this.mName = str;
        this.rB = zzaVar;
        this.rC = null;
        this.rD = zzfVar;
        this.rE = null;
    }

    public final zza<?, O> zzanq() {
        zzab.zza(this.rB != null, "This API was constructed with a SimpleClientBuilder. Use getSimpleClientBuilder");
        return this.rB;
    }

    public final zzc<?> zzans() {
        if (this.rD != null) {
            return this.rD;
        }
        throw new IllegalStateException("This API was constructed with null client keys. This should not be possible.");
    }
}
