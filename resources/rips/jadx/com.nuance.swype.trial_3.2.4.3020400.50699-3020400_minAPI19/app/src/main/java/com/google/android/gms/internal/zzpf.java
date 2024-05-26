package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.clearcut.LogEventParcelable;
import com.google.android.gms.internal.zzpe;

/* loaded from: classes.dex */
public interface zzpf extends IInterface {
    void zza(zzpe zzpeVar, LogEventParcelable logEventParcelable) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class zza extends Binder implements zzpf {

        /* renamed from: com.google.android.gms.internal.zzpf$zza$zza, reason: collision with other inner class name */
        /* loaded from: classes.dex */
        private static class C0105zza implements zzpf {
            private IBinder zzahn;

            C0105zza(IBinder iBinder) {
                this.zzahn = iBinder;
            }

            @Override // android.os.IInterface
            public final IBinder asBinder() {
                return this.zzahn;
            }

            @Override // com.google.android.gms.internal.zzpf
            public final void zza(zzpe zzpeVar, LogEventParcelable logEventParcelable) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.clearcut.internal.IClearcutLoggerService");
                    obtain.writeStrongBinder(zzpeVar != null ? zzpeVar.asBinder() : null);
                    if (logEventParcelable != null) {
                        obtain.writeInt(1);
                        logEventParcelable.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzahn.transact(1, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }

        public static zzpf zzdm(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.clearcut.internal.IClearcutLoggerService");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzpf)) ? new C0105zza(iBinder) : (zzpf) queryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            zzpe c0104zza;
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.google.android.gms.clearcut.internal.IClearcutLoggerService");
                    IBinder readStrongBinder = parcel.readStrongBinder();
                    if (readStrongBinder == null) {
                        c0104zza = null;
                    } else {
                        IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.clearcut.internal.IClearcutLoggerCallbacks");
                        c0104zza = (queryLocalInterface == null || !(queryLocalInterface instanceof zzpe)) ? new zzpe.zza.C0104zza(readStrongBinder) : (zzpe) queryLocalInterface;
                    }
                    zza(c0104zza, parcel.readInt() != 0 ? (LogEventParcelable) LogEventParcelable.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.gms.clearcut.internal.IClearcutLoggerService");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }
}
