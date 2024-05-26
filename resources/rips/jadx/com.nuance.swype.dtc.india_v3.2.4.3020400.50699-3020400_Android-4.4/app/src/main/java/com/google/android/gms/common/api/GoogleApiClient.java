package com.google.android.gms.common.api;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.view.View;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.internal.zzpk;
import com.google.android.gms.internal.zzpm;
import com.google.android.gms.internal.zzpp;
import com.google.android.gms.internal.zzpy;
import com.google.android.gms.internal.zzqi;
import com.google.android.gms.internal.zzqt;
import com.google.android.gms.internal.zzqx;
import com.google.android.gms.internal.zzvt;
import com.google.android.gms.internal.zzvu;
import com.google.android.gms.internal.zzvv;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: classes.dex */
public abstract class GoogleApiClient {
    private static final Set<GoogleApiClient> rW = Collections.newSetFromMap(new WeakHashMap());

    /* loaded from: classes.dex */
    public interface ConnectionCallbacks {
        void onConnected(Bundle bundle);

        void onConnectionSuspended(int i);
    }

    /* loaded from: classes.dex */
    public interface OnConnectionFailedListener {
        void onConnectionFailed(ConnectionResult connectionResult);
    }

    public static Set<GoogleApiClient> zzaoe() {
        Set<GoogleApiClient> set;
        synchronized (rW) {
            set = rW;
        }
        return set;
    }

    public abstract ConnectionResult blockingConnect();

    public abstract PendingResult<Status> clearDefaultAccountAndReconnect();

    public abstract void connect();

    public abstract void disconnect();

    public abstract void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    public Context getContext() {
        throw new UnsupportedOperationException();
    }

    public Looper getLooper() {
        throw new UnsupportedOperationException();
    }

    public abstract boolean isConnected();

    public abstract boolean isConnecting();

    public abstract void registerConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener);

    public abstract void unregisterConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener);

    public <C extends Api.zze> C zza(Api.zzc<C> zzcVar) {
        throw new UnsupportedOperationException();
    }

    public void zza(zzqx zzqxVar) {
        throw new UnsupportedOperationException();
    }

    public boolean zza(Api<?> api) {
        throw new UnsupportedOperationException();
    }

    public boolean zza(zzqt zzqtVar) {
        throw new UnsupportedOperationException();
    }

    public void zzaof() {
        throw new UnsupportedOperationException();
    }

    public void zzb(zzqx zzqxVar) {
        throw new UnsupportedOperationException();
    }

    public <A extends Api.zzb, R extends Result, T extends zzpm.zza<R, A>> T zzc(T t) {
        throw new UnsupportedOperationException();
    }

    public <A extends Api.zzb, T extends zzpm.zza<? extends Result, A>> T zzd(T t) {
        throw new UnsupportedOperationException();
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private Account aL;
        private String bX;
        private final Context mContext;
        private int rZ;
        private View sa;
        private String sb;
        public zzqi se;
        public OnConnectionFailedListener sg;
        public Looper zzahv;
        private final Set<Scope> rX = new HashSet();
        private final Set<Scope> rY = new HashSet();
        private final Map<Api<?>, zzg.zza> sc = new ArrayMap();
        private final Map<Api<?>, Api.ApiOptions> sd = new ArrayMap();
        public int sf = -1;
        private GoogleApiAvailability sh = GoogleApiAvailability.getInstance();
        private Api.zza<? extends zzvu, zzvv> si = zzvt.bK;
        private final ArrayList<ConnectionCallbacks> sj = new ArrayList<>();
        private final ArrayList<OnConnectionFailedListener> sk = new ArrayList<>();

        public Builder(Context context) {
            this.mContext = context;
            this.zzahv = context.getMainLooper();
            this.bX = context.getPackageName();
            this.sb = context.getClass().getName();
        }

        public final Builder addConnectionCallbacks(ConnectionCallbacks connectionCallbacks) {
            zzab.zzb(connectionCallbacks, "Listener must not be null");
            this.sj.add(connectionCallbacks);
            return this;
        }

        public final Builder addOnConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
            zzab.zzb(onConnectionFailedListener, "Listener must not be null");
            this.sk.add(onConnectionFailedListener);
            return this;
        }

        public final zzg zzaoh() {
            zzvv zzvvVar = zzvv.atR;
            if (this.sd.containsKey(zzvt.API)) {
                zzvvVar = (zzvv) this.sd.get(zzvt.API);
            }
            return new zzg(this.aL, this.rX, this.sc, this.rZ, this.sa, this.bX, this.sb, zzvvVar);
        }

        public final Builder addApi(Api<? extends Api.ApiOptions.NotRequiredOptions> api) {
            zzab.zzb(api, "Api must not be null");
            this.sd.put(api, null);
            List<Scope> zzp = api.rB.zzp(null);
            this.rY.addAll(zzp);
            this.rX.addAll(zzp);
            return this;
        }

        public final <O extends Api.ApiOptions.HasOptions> Builder addApi(Api<O> api, O o) {
            zzab.zzb(api, "Api must not be null");
            zzab.zzb(o, "Null options are not permitted for this Api");
            this.sd.put(api, o);
            List<Scope> zzp = api.rB.zzp(o);
            this.rY.addAll(zzp);
            this.rX.addAll(zzp);
            return this;
        }

        /* JADX WARN: Type inference failed for: r1v24, types: [com.google.android.gms.common.api.Api$zze, java.lang.Object] */
        public final GoogleApiClient build() {
            zzab.zzb(!this.sd.isEmpty(), "must call addApi() to add at least one API");
            zzg zzaoh = zzaoh();
            Api<?> api = null;
            Map<Api<?>, zzg.zza> map = zzaoh.yk;
            ArrayMap arrayMap = new ArrayMap();
            ArrayMap arrayMap2 = new ArrayMap();
            ArrayList arrayList = new ArrayList();
            for (Api<?> api2 : this.sd.keySet()) {
                Api.ApiOptions apiOptions = this.sd.get(api2);
                int i = map.get(api2) != null ? map.get(api2).yn ? 1 : 2 : 0;
                arrayMap.put(api2, Integer.valueOf(i));
                zzpp zzppVar = new zzpp(api2, i);
                arrayList.add(zzppVar);
                ?? zza = api2.zzanq().zza(this.mContext, this.zzahv, zzaoh, apiOptions, zzppVar, zzppVar);
                arrayMap2.put(api2.zzans(), zza);
                if (!zza.zzafz()) {
                    api2 = api;
                } else if (api != null) {
                    String valueOf = String.valueOf(api2.mName);
                    String valueOf2 = String.valueOf(api.mName);
                    throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 21 + String.valueOf(valueOf2).length()).append(valueOf).append(" cannot be used with ").append(valueOf2).toString());
                }
                api = api2;
            }
            if (api != null) {
                zzab.zza(this.aL == null, "Must not set an account in GoogleApiClient.Builder when using %s. Set account in GoogleSignInOptions.Builder instead", api.mName);
                zzab.zza(this.rX.equals(this.rY), "Must not set scopes in GoogleApiClient.Builder when using %s. Set account in GoogleSignInOptions.Builder instead.", api.mName);
            }
            zzpy zzpyVar = new zzpy(this.mContext, new ReentrantLock(), this.zzahv, zzaoh, this.sh, this.si, arrayMap, this.sj, this.sk, arrayMap2, this.sf, zzpy.zza(arrayMap2.values(), true), arrayList);
            synchronized (GoogleApiClient.rW) {
                GoogleApiClient.rW.add(zzpyVar);
            }
            if (this.sf >= 0) {
                zzpk.zza(this.se).zza(this.sf, zzpyVar, this.sg);
            }
            return zzpyVar;
        }
    }
}
