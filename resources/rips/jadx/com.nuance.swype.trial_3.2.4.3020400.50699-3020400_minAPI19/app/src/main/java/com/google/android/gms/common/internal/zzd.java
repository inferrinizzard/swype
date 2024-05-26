package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzt;
import com.google.android.gms.common.internal.zzu;
import com.nuance.connect.sqlite.ChinesePredictionDataSource;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public abstract class zzd<T extends IInterface> {
    public static final String[] xE = {"service_esmobile", "service_googleme"};
    private final Context mContext;
    final Handler mHandler;
    private final com.google.android.gms.common.zzc tz;
    private final zzc xA;
    private final int xB;
    private final String xC;
    protected AtomicInteger xD;
    int xm;
    long xn;
    private long xo;
    private int xp;
    private long xq;
    private final zzm xr;
    private final Object xs;
    private zzu xt;
    private zzf xu;
    private T xv;
    private final ArrayList<zze<?>> xw;
    private zzh xx;
    private int xy;
    private final zzb xz;
    private final Looper zzahv;
    private final Object zzail;

    /* loaded from: classes.dex */
    public interface zzb {
        void onConnected(Bundle bundle);

        void onConnectionSuspended(int i);
    }

    /* loaded from: classes.dex */
    public interface zzc {
        void onConnectionFailed(ConnectionResult connectionResult);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public abstract class zze<TListener> {
        private TListener mListener;
        private boolean xH = false;

        public zze(TListener tlistener) {
            this.mListener = tlistener;
        }

        public final void unregister() {
            zzasg();
            synchronized (zzd.this.xw) {
                zzd.this.xw.remove(this);
            }
        }

        public final void zzasf() {
            TListener tlistener;
            synchronized (this) {
                tlistener = this.mListener;
                if (this.xH) {
                    String valueOf = String.valueOf(this);
                    Log.w("GmsClient", new StringBuilder(String.valueOf(valueOf).length() + 47).append("Callback proxy ").append(valueOf).append(" being reused. This is not safe.").toString());
                }
            }
            if (tlistener != null) {
                try {
                    zzv(tlistener);
                } catch (RuntimeException e) {
                    throw e;
                }
            }
            synchronized (this) {
                this.xH = true;
            }
            unregister();
        }

        public final void zzasg() {
            synchronized (this) {
                this.mListener = null;
            }
        }

        protected abstract void zzv(TListener tlistener);
    }

    /* loaded from: classes.dex */
    public interface zzf {
        void zzh(ConnectionResult connectionResult);
    }

    /* loaded from: classes.dex */
    public final class zzh implements ServiceConnection {
        private final int xJ;

        public zzh(int i) {
            this.xJ = i;
        }

        @Override // android.content.ServiceConnection
        public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            zzab.zzb(iBinder, "Expecting a valid IBinder");
            synchronized (zzd.this.xs) {
                zzd.this.xt = zzu.zza.zzdt(iBinder);
            }
            zzd.this.zza$4c85f423(0, this.xJ);
        }

        @Override // android.content.ServiceConnection
        public final void onServiceDisconnected(ComponentName componentName) {
            synchronized (zzd.this.xs) {
                zzd.this.xt = null;
            }
            zzd.this.mHandler.sendMessage(zzd.this.mHandler.obtainMessage(4, this.xJ, 1));
        }
    }

    /* loaded from: classes.dex */
    protected class zzi implements zzf {
        public zzi() {
        }

        @Override // com.google.android.gms.common.internal.zzd.zzf
        public final void zzh(ConnectionResult connectionResult) {
            if (connectionResult.isSuccess()) {
                zzd.this.zza((zzq) null, zzd.this.zzasc());
            } else if (zzd.this.xA != null) {
                zzd.this.xA.onConnectionFailed(connectionResult);
            }
        }
    }

    /* loaded from: classes.dex */
    protected final class zzj extends zza {
        public final IBinder xK;

        public zzj(int i, IBinder iBinder, Bundle bundle) {
            super(i, bundle);
            this.xK = iBinder;
        }

        @Override // com.google.android.gms.common.internal.zzd.zza
        protected final boolean zzasd() {
            try {
                String interfaceDescriptor = this.xK.getInterfaceDescriptor();
                if (!zzd.this.zzra().equals(interfaceDescriptor)) {
                    String valueOf = String.valueOf(zzd.this.zzra());
                    Log.e("GmsClient", new StringBuilder(String.valueOf(valueOf).length() + 34 + String.valueOf(interfaceDescriptor).length()).append("service descriptor mismatch: ").append(valueOf).append(" vs. ").append(interfaceDescriptor).toString());
                    return false;
                }
                IInterface zzbb = zzd.this.zzbb(this.xK);
                if (zzbb == null || !zzd.this.zza(2, 3, zzbb)) {
                    return false;
                }
                Bundle zzamh = zzd.this.zzamh();
                if (zzd.this.xz != null) {
                    zzd.this.xz.onConnected(zzamh);
                }
                return true;
            } catch (RemoteException e) {
                Log.w("GmsClient", "service probably died");
                return false;
            }
        }

        @Override // com.google.android.gms.common.internal.zzd.zza
        protected final void zzl(ConnectionResult connectionResult) {
            if (zzd.this.xA != null) {
                zzd.this.xA.onConnectionFailed(connectionResult);
            }
            zzd.this.onConnectionFailed(connectionResult);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public final class zzk extends zza {
        public zzk(int i) {
            super(i, null);
        }

        @Override // com.google.android.gms.common.internal.zzd.zza
        protected final boolean zzasd() {
            zzd.this.xu.zzh(ConnectionResult.rb);
            return true;
        }

        @Override // com.google.android.gms.common.internal.zzd.zza
        protected final void zzl(ConnectionResult connectionResult) {
            zzd.this.xu.zzh(connectionResult);
            zzd.this.onConnectionFailed(connectionResult);
        }
    }

    public zzd(Context context, Looper looper, int i, zzb zzbVar, zzc zzcVar, String str) {
        this(context, looper, zzm.zzce(context), com.google.android.gms.common.zzc.zzang(), i, (zzb) zzab.zzy(zzbVar), (zzc) zzab.zzy(zzcVar), null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public zzd(Context context, Looper looper, zzm zzmVar, com.google.android.gms.common.zzc zzcVar, int i, zzb zzbVar, zzc zzcVar2, String str) {
        this.zzail = new Object();
        this.xs = new Object();
        this.xw = new ArrayList<>();
        this.xy = 1;
        this.xD = new AtomicInteger(0);
        this.mContext = (Context) zzab.zzb(context, "Context must not be null");
        this.zzahv = (Looper) zzab.zzb(looper, "Looper must not be null");
        this.xr = (zzm) zzab.zzb(zzmVar, "Supervisor must not be null");
        this.tz = (com.google.android.gms.common.zzc) zzab.zzb(zzcVar, "API availability must not be null");
        this.mHandler = new HandlerC0047zzd(looper);
        this.xB = i;
        this.xz = zzbVar;
        this.xA = zzcVar2;
        this.xC = str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean zza(int i, int i2, T t) {
        boolean z;
        synchronized (this.zzail) {
            if (this.xy != i) {
                z = false;
            } else {
                zzb(i2, t);
                z = true;
            }
        }
        return z;
    }

    private String zzaru() {
        return this.xC == null ? this.mContext.getClass().getName() : this.xC;
    }

    public void disconnect() {
        this.xD.incrementAndGet();
        synchronized (this.xw) {
            int size = this.xw.size();
            for (int i = 0; i < size; i++) {
                this.xw.get(i).zzasg();
            }
            this.xw.clear();
        }
        synchronized (this.xs) {
            this.xt = null;
        }
        zzb(1, null);
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        int i;
        T t;
        synchronized (this.zzail) {
            i = this.xy;
            t = this.xv;
        }
        printWriter.append((CharSequence) str).append("mConnectState=");
        switch (i) {
            case 1:
                printWriter.print("DISCONNECTED");
                break;
            case 2:
                printWriter.print("CONNECTING");
                break;
            case 3:
                printWriter.print("CONNECTED");
                break;
            case 4:
                printWriter.print("DISCONNECTING");
                break;
            default:
                printWriter.print(ChinesePredictionDataSource.UNKNOWN);
                break;
        }
        printWriter.append(" mService=");
        if (t == null) {
            printWriter.println("null");
        } else {
            printWriter.append((CharSequence) zzra()).append("@").println(Integer.toHexString(System.identityHashCode(t.asBinder())));
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        if (this.xo > 0) {
            PrintWriter append = printWriter.append((CharSequence) str).append("lastConnectedTime=");
            long j = this.xo;
            String valueOf = String.valueOf(simpleDateFormat.format(new Date(this.xo)));
            append.println(new StringBuilder(String.valueOf(valueOf).length() + 21).append(j).append(XMLResultsHandler.SEP_SPACE).append(valueOf).toString());
        }
        if (this.xn > 0) {
            printWriter.append((CharSequence) str).append("lastSuspendedCause=");
            switch (this.xm) {
                case 1:
                    printWriter.append("CAUSE_SERVICE_DISCONNECTED");
                    break;
                case 2:
                    printWriter.append("CAUSE_NETWORK_LOST");
                    break;
                default:
                    printWriter.append((CharSequence) String.valueOf(this.xm));
                    break;
            }
            PrintWriter append2 = printWriter.append(" lastSuspendedTime=");
            long j2 = this.xn;
            String valueOf2 = String.valueOf(simpleDateFormat.format(new Date(this.xn)));
            append2.println(new StringBuilder(String.valueOf(valueOf2).length() + 21).append(j2).append(XMLResultsHandler.SEP_SPACE).append(valueOf2).toString());
        }
        if (this.xq > 0) {
            printWriter.append((CharSequence) str).append("lastFailedStatus=").append((CharSequence) CommonStatusCodes.getStatusCodeString(this.xp));
            PrintWriter append3 = printWriter.append(" lastFailedTime=");
            long j3 = this.xq;
            String valueOf3 = String.valueOf(simpleDateFormat.format(new Date(this.xq)));
            append3.println(new StringBuilder(String.valueOf(valueOf3).length() + 21).append(j3).append(XMLResultsHandler.SEP_SPACE).append(valueOf3).toString());
        }
    }

    public Account getAccount() {
        return null;
    }

    public final Context getContext() {
        return this.mContext;
    }

    public final Looper getLooper() {
        return this.zzahv;
    }

    public boolean isConnected() {
        boolean z;
        synchronized (this.zzail) {
            z = this.xy == 3;
        }
        return z;
    }

    public boolean isConnecting() {
        boolean z;
        synchronized (this.zzail) {
            z = this.xy == 2;
        }
        return z;
    }

    public void zza(zzf zzfVar) {
        this.xu = (zzf) zzab.zzb(zzfVar, "Connection progress callbacks cannot be null.");
        zzb(2, null);
    }

    protected final void zza$4c85f423(int i, int i2) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(5, i2, -1, new zzk(i)));
    }

    public Bundle zzaeu() {
        return new Bundle();
    }

    public boolean zzafk() {
        return false;
    }

    public boolean zzafz() {
        return false;
    }

    public Intent zzaga() {
        throw new UnsupportedOperationException("Not a sign in API");
    }

    public Bundle zzamh() {
        return null;
    }

    public boolean zzanu() {
        return true;
    }

    public IBinder zzanv() {
        IBinder asBinder;
        synchronized (this.xs) {
            asBinder = this.xt == null ? null : this.xt.asBinder();
        }
        return asBinder;
    }

    public void zzarx() {
        int isGooglePlayServicesAvailable = this.tz.isGooglePlayServicesAvailable(this.mContext);
        if (isGooglePlayServicesAvailable == 0) {
            zza(new zzi());
            return;
        }
        zzb(1, null);
        this.xu = new zzi();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3, this.xD.get(), isGooglePlayServicesAvailable));
    }

    public final Account zzary() {
        return getAccount() != null ? getAccount() : new Account("<<default account>>", "com.google");
    }

    public boolean zzasb() {
        return false;
    }

    protected Set<Scope> zzasc() {
        return Collections.EMPTY_SET;
    }

    public abstract T zzbb(IBinder iBinder);

    public void zzgc(int i) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(4, this.xD.get(), i));
    }

    public abstract String zzqz();

    public abstract String zzra();

    /* loaded from: classes.dex */
    private abstract class zza extends zze<Boolean> {
        public final int statusCode;
        public final Bundle xF;

        protected zza(int i, Bundle bundle) {
            super(true);
            this.statusCode = i;
            this.xF = bundle;
        }

        protected abstract boolean zzasd();

        protected abstract void zzl(ConnectionResult connectionResult);

        @Override // com.google.android.gms.common.internal.zzd.zze
        protected final /* synthetic */ void zzv(Boolean bool) {
            if (bool == null) {
                zzd.this.zzb(1, null);
                return;
            }
            switch (this.statusCode) {
                case 0:
                    if (zzasd()) {
                        return;
                    }
                    zzd.this.zzb(1, null);
                    zzl(new ConnectionResult(8, null));
                    return;
                case 10:
                    zzd.this.zzb(1, null);
                    throw new IllegalStateException("A fatal developer error has occurred. Check the logs for further information.");
                default:
                    zzd.this.zzb(1, null);
                    zzl(new ConnectionResult(this.statusCode, this.xF != null ? (PendingIntent) this.xF.getParcelable("pendingIntent") : null));
                    return;
            }
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zzd$zzd, reason: collision with other inner class name */
    /* loaded from: classes.dex */
    final class HandlerC0047zzd extends Handler {
        public HandlerC0047zzd(Looper looper) {
            super(looper);
        }

        private static void zza(Message message) {
            ((zze) message.obj).unregister();
        }

        private static boolean zzb(Message message) {
            return message.what == 2 || message.what == 1 || message.what == 5;
        }

        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            if (zzd.this.xD.get() != message.arg1) {
                if (zzb(message)) {
                    zza(message);
                    return;
                }
                return;
            }
            if ((message.what == 1 || message.what == 5) && !zzd.this.isConnecting()) {
                zza(message);
                return;
            }
            if (message.what == 3) {
                ConnectionResult connectionResult = new ConnectionResult(message.arg2, message.obj instanceof PendingIntent ? (PendingIntent) message.obj : null);
                zzd.this.xu.zzh(connectionResult);
                zzd.this.onConnectionFailed(connectionResult);
                return;
            }
            if (message.what == 4) {
                zzd.this.zzb(4, null);
                if (zzd.this.xz != null) {
                    zzd.this.xz.onConnectionSuspended(message.arg2);
                }
                zzd zzdVar = zzd.this;
                zzdVar.xm = message.arg2;
                zzdVar.xn = System.currentTimeMillis();
                zzd.this.zza(4, 1, null);
                return;
            }
            if (message.what == 2 && !zzd.this.isConnected()) {
                zza(message);
            } else if (zzb(message)) {
                ((zze) message.obj).zzasf();
            } else {
                Log.wtf("GmsClient", new StringBuilder(45).append("Don't know how to handle message: ").append(message.what).toString(), new Exception());
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class zzg extends zzt.zza {
        private zzd xI;
        private final int xJ;

        public zzg(zzd zzdVar, int i) {
            this.xI = zzdVar;
            this.xJ = i;
        }

        @Override // com.google.android.gms.common.internal.zzt
        public final void zzb(int i, Bundle bundle) {
            Log.wtf("GmsClient", "received deprecated onAccountValidationComplete callback, ignoring", new Exception());
        }

        @Override // com.google.android.gms.common.internal.zzt
        public final void zza(int i, IBinder iBinder, Bundle bundle) {
            zzab.zzb(this.xI, "onPostInitComplete can be called only once per call to getRemoteService");
            zzd zzdVar = this.xI;
            zzdVar.mHandler.sendMessage(zzdVar.mHandler.obtainMessage(1, this.xJ, -1, new zzj(i, iBinder, bundle)));
            this.xI = null;
        }
    }

    protected final void onConnectionFailed(ConnectionResult connectionResult) {
        this.xp = connectionResult.ok;
        this.xq = System.currentTimeMillis();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void zzb(int i, T t) {
        if (!((i == 3) == (t != null))) {
            throw new IllegalArgumentException();
        }
        synchronized (this.zzail) {
            this.xy = i;
            this.xv = t;
            switch (i) {
                case 1:
                    if (this.xx != null) {
                        zzm zzmVar = this.xr;
                        String zzqz = zzqz();
                        zzh zzhVar = this.xx;
                        zzaru();
                        zzmVar.zzb$3185ab25(zzqz, "com.google.android.gms", zzhVar);
                        this.xx = null;
                        break;
                    }
                    break;
                case 2:
                    if (this.xx != null) {
                        String valueOf = String.valueOf(zzqz());
                        String valueOf2 = String.valueOf("com.google.android.gms");
                        Log.e("GmsClient", new StringBuilder(String.valueOf(valueOf).length() + 70 + String.valueOf(valueOf2).length()).append("Calling connect() while still connected, missing disconnect() for ").append(valueOf).append(" on ").append(valueOf2).toString());
                        zzm zzmVar2 = this.xr;
                        String zzqz2 = zzqz();
                        zzh zzhVar2 = this.xx;
                        zzaru();
                        zzmVar2.zzb$3185ab25(zzqz2, "com.google.android.gms", zzhVar2);
                        this.xD.incrementAndGet();
                    }
                    this.xx = new zzh(this.xD.get());
                    if (!this.xr.zza(zzqz(), "com.google.android.gms", this.xx, zzaru())) {
                        String valueOf3 = String.valueOf(zzqz());
                        String valueOf4 = String.valueOf("com.google.android.gms");
                        Log.e("GmsClient", new StringBuilder(String.valueOf(valueOf3).length() + 34 + String.valueOf(valueOf4).length()).append("unable to connect to service: ").append(valueOf3).append(" on ").append(valueOf4).toString());
                        zza$4c85f423(16, this.xD.get());
                        break;
                    }
                    break;
                case 3:
                    this.xo = System.currentTimeMillis();
                    break;
            }
        }
    }

    public void zza(zzf zzfVar, ConnectionResult connectionResult) {
        this.xu = (zzf) zzab.zzb(zzfVar, "Connection progress callbacks cannot be null.");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3, this.xD.get(), connectionResult.ok, connectionResult.mPendingIntent));
    }

    public final T zzasa() throws DeadObjectException {
        T t;
        synchronized (this.zzail) {
            if (this.xy == 4) {
                throw new DeadObjectException();
            }
            if (!isConnected()) {
                throw new IllegalStateException("Not connected. Call connect() and wait for onConnected() to be called.");
            }
            zzab.zza(this.xv != null, "Client is connected but service is null");
            t = this.xv;
        }
        return t;
    }

    public void zza(zzq zzqVar, Set<Scope> set) {
        try {
            Bundle zzaeu = zzaeu();
            GetServiceRequest getServiceRequest = new GetServiceRequest(this.xB);
            getServiceRequest.yw = this.mContext.getPackageName();
            getServiceRequest.yz = zzaeu;
            if (set != null) {
                getServiceRequest.yy = (Scope[]) set.toArray(new Scope[set.size()]);
            }
            if (zzafk()) {
                getServiceRequest.yA = zzary();
                if (zzqVar != null) {
                    getServiceRequest.yx = zzqVar.asBinder();
                }
            } else if (zzasb()) {
                getServiceRequest.yA = getAccount();
            }
            synchronized (this.xs) {
                if (this.xt != null) {
                    this.xt.zza(new zzg(this, this.xD.get()), getServiceRequest);
                } else {
                    Log.w("GmsClient", "mServiceBroker is null, client disconnected");
                }
            }
        } catch (DeadObjectException e) {
            Log.w("GmsClient", "service died");
            zzgc(1);
        } catch (RemoteException e2) {
            Log.w("GmsClient", "Remote exception occurred", e2);
        }
    }
}
