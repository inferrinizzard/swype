package io.fabric.sdk.android.services.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import io.fabric.sdk.android.Fabric;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class AdvertisingInfoServiceStrategy implements AdvertisingInfoStrategy {
    private final Context context;

    public AdvertisingInfoServiceStrategy(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override // io.fabric.sdk.android.services.common.AdvertisingInfoStrategy
    public final AdvertisingInfo getAdvertisingInfo() {
        byte b = 0;
        AdvertisingInfo advertisingInfo = null;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Fabric.getLogger();
        } else {
            try {
                this.context.getPackageManager().getPackageInfo("com.android.vending", 0);
                AdvertisingConnection connection = new AdvertisingConnection(b);
                Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
                intent.setPackage("com.google.android.gms");
                try {
                    try {
                    } catch (Exception e) {
                        Fabric.getLogger().w("Fabric", "Exception in binding to Google Play Service to capture AdvertisingId", e);
                    } finally {
                        this.context.unbindService(connection);
                    }
                    if (this.context.bindService(intent, connection, 1)) {
                        AdvertisingInterface adInterface = new AdvertisingInterface(connection.getBinder());
                        advertisingInfo = new AdvertisingInfo(adInterface.getId(), adInterface.isLimitAdTrackingEnabled());
                    } else {
                        Fabric.getLogger();
                    }
                } catch (Throwable th) {
                    Fabric.getLogger();
                }
            } catch (Exception e2) {
                Fabric.getLogger();
            }
        }
        return advertisingInfo;
    }

    /* loaded from: classes.dex */
    private static final class AdvertisingConnection implements ServiceConnection {
        private final LinkedBlockingQueue<IBinder> queue;
        private boolean retrieved;

        private AdvertisingConnection() {
            this.retrieved = false;
            this.queue = new LinkedBlockingQueue<>(1);
        }

        /* synthetic */ AdvertisingConnection(byte b) {
            this();
        }

        @Override // android.content.ServiceConnection
        public final void onServiceConnected(ComponentName name, IBinder service) {
            try {
                this.queue.put(service);
            } catch (InterruptedException e) {
            }
        }

        @Override // android.content.ServiceConnection
        public final void onServiceDisconnected(ComponentName name) {
            this.queue.clear();
        }

        public final IBinder getBinder() {
            if (this.retrieved) {
                Fabric.getLogger().e("Fabric", "getBinder already called");
            }
            this.retrieved = true;
            try {
                return this.queue.poll(200L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                return null;
            }
        }
    }

    /* loaded from: classes.dex */
    private static final class AdvertisingInterface implements IInterface {
        private final IBinder binder;

        public AdvertisingInterface(IBinder binder) {
            this.binder = binder;
        }

        @Override // android.os.IInterface
        public final IBinder asBinder() {
            return this.binder;
        }

        public final String getId() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            String id = null;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                this.binder.transact(1, data, reply, 0);
                reply.readException();
                id = reply.readString();
            } catch (Exception e) {
                Fabric.getLogger();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return id;
        }

        public final boolean isLimitAdTrackingEnabled() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            boolean limitAdTracking = false;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                data.writeInt(1);
                this.binder.transact(2, data, reply, 0);
                reply.readException();
                limitAdTracking = reply.readInt() != 0;
            } catch (Exception e) {
                Fabric.getLogger();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return limitAdTracking;
        }
    }
}
