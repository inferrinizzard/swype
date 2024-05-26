package com.google.android.gms.ads.identifier;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.stats.zzb;
import com.google.android.gms.common.zzc;
import com.google.android.gms.internal.zzcc;
import com.nuance.swype.input.IME;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* loaded from: classes.dex */
public class AdvertisingIdClient {
    private final Context mContext;
    com.google.android.gms.common.zza zzajb;
    zzcc zzajc;
    boolean zzajd;
    Object zzaje;
    zza zzajf;
    final long zzajg;

    /* loaded from: classes.dex */
    public static final class Info {
        private final String zzajl;
        private final boolean zzajm;

        public Info(String str, boolean z) {
            this.zzajl = str;
            this.zzajm = z;
        }

        public final String getId() {
            return this.zzajl;
        }

        public final boolean isLimitAdTrackingEnabled() {
            return this.zzajm;
        }

        public final String toString() {
            String str = this.zzajl;
            return new StringBuilder(String.valueOf(str).length() + 7).append("{").append(str).append("}").append(this.zzajm).toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class zza extends Thread {
        private WeakReference<AdvertisingIdClient> zzajh;
        private long zzaji;
        CountDownLatch zzajj = new CountDownLatch(1);
        boolean zzajk = false;

        public zza(AdvertisingIdClient advertisingIdClient, long j) {
            this.zzajh = new WeakReference<>(advertisingIdClient);
            this.zzaji = j;
            start();
        }

        private void disconnect() {
            AdvertisingIdClient advertisingIdClient = this.zzajh.get();
            if (advertisingIdClient != null) {
                advertisingIdClient.finish();
                this.zzajk = true;
            }
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public final void run() {
            try {
                if (this.zzajj.await(this.zzaji, TimeUnit.MILLISECONDS)) {
                    return;
                }
                disconnect();
            } catch (InterruptedException e) {
                disconnect();
            }
        }
    }

    public AdvertisingIdClient(Context context) {
        this(context, 30000L);
    }

    public AdvertisingIdClient(Context context, long j) {
        this.zzaje = new Object();
        zzab.zzy(context);
        this.mContext = context;
        this.zzajd = false;
        this.zzajg = j;
    }

    public static Info getAdvertisingIdInfo(Context context) throws IOException, IllegalStateException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        AdvertisingIdClient advertisingIdClient = new AdvertisingIdClient(context, -1L);
        try {
            advertisingIdClient.zze(false);
            return advertisingIdClient.getInfo();
        } finally {
            advertisingIdClient.finish();
        }
    }

    public static void setShouldSkipGmsCoreVersionCheck(boolean z) {
    }

    private void zze(boolean z) throws IOException, IllegalStateException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        zzab.zzhj("Calling this from your main thread can lead to deadlock");
        synchronized (this) {
            if (this.zzajd) {
                finish();
            }
            this.zzajb = zzh(this.mContext);
            this.zzajc = zza$4541900a(this.zzajb);
            this.zzajd = true;
            if (z) {
                zzdi();
            }
        }
    }

    private static com.google.android.gms.common.zza zzh(Context context) throws IOException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        try {
            context.getPackageManager().getPackageInfo("com.android.vending", 0);
            switch (zzc.zzang().isGooglePlayServicesAvailable(context)) {
                case 0:
                case 2:
                    com.google.android.gms.common.zza zzaVar = new com.google.android.gms.common.zza();
                    Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
                    intent.setPackage("com.google.android.gms");
                    try {
                        if (zzb.zzaux().zza$31a3108d(context, intent, zzaVar)) {
                            return zzaVar;
                        }
                        throw new IOException("Connection failure");
                    } catch (Throwable th) {
                        throw new IOException(th);
                    }
                case 1:
                default:
                    throw new IOException("Google Play services not available");
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new GooglePlayServicesNotAvailableException(9);
        }
    }

    protected void finalize() throws Throwable {
        finish();
        super.finalize();
    }

    public void finish() {
        zzab.zzhj("Calling this from your main thread can lead to deadlock");
        synchronized (this) {
            if (this.mContext == null || this.zzajb == null) {
                return;
            }
            try {
                if (this.zzajd) {
                    zzb.zzaux();
                    zzb.zza(this.mContext, this.zzajb);
                }
            } catch (IllegalArgumentException e) {
                Log.i("AdvertisingIdClient", "AdvertisingIdClient unbindService failed.", e);
            }
            this.zzajd = false;
            this.zzajc = null;
            this.zzajb = null;
        }
    }

    public void start() throws IOException, IllegalStateException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        zze(true);
    }

    private void zzdi() {
        synchronized (this.zzaje) {
            if (this.zzajf != null) {
                this.zzajf.zzajj.countDown();
                try {
                    this.zzajf.join();
                } catch (InterruptedException e) {
                }
            }
            if (this.zzajg > 0) {
                this.zzajf = new zza(this, this.zzajg);
            }
        }
    }

    public Info getInfo() throws IOException {
        Info info;
        zzab.zzhj("Calling this from your main thread can lead to deadlock");
        synchronized (this) {
            if (!this.zzajd) {
                synchronized (this.zzaje) {
                    if (this.zzajf == null || !this.zzajf.zzajk) {
                        throw new IOException("AdvertisingIdClient is not connected.");
                    }
                }
                try {
                    zze(false);
                    if (!this.zzajd) {
                        throw new IOException("AdvertisingIdClient cannot reconnect.");
                    }
                } catch (Exception e) {
                    throw new IOException("AdvertisingIdClient cannot reconnect.", e);
                }
            }
            zzab.zzy(this.zzajb);
            zzab.zzy(this.zzajc);
            try {
                info = new Info(this.zzajc.getId(), this.zzajc.zzf(true));
            } catch (RemoteException e2) {
                Log.i("AdvertisingIdClient", "GMS remote exception ", e2);
                throw new IOException("Remote exception");
            }
        }
        zzdi();
        return info;
    }

    private static zzcc zza$4541900a(com.google.android.gms.common.zza zzaVar) throws IOException {
        try {
            TimeUnit timeUnit = TimeUnit.MILLISECONDS;
            zzab.zzhj("BlockingServiceConnection.getServiceWithTimeout() called on main thread");
            if (zzaVar.qZ) {
                throw new IllegalStateException("Cannot call get on this connection more than once");
            }
            zzaVar.qZ = true;
            IBinder poll = zzaVar.ra.poll(IME.RETRY_DELAY_IN_MILLIS, timeUnit);
            if (poll == null) {
                throw new TimeoutException("Timed out waiting for the service connection");
            }
            return zzcc.zza.zzf(poll);
        } catch (InterruptedException e) {
            throw new IOException("Interrupted exception");
        } catch (Throwable th) {
            throw new IOException(th);
        }
    }
}
