package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzrj;

/* loaded from: classes.dex */
public interface zzrk extends IInterface {
    void zza(zzrj zzrjVar) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class zza extends Binder implements zzrk {

        /* renamed from: com.google.android.gms.internal.zzrk$zza$zza, reason: collision with other inner class name */
        /* loaded from: classes.dex */
        private static class C0107zza implements zzrk {
            private IBinder zzahn;

            C0107zza(IBinder iBinder) {
                this.zzahn = iBinder;
            }

            @Override // android.os.IInterface
            public final IBinder asBinder() {
                return this.zzahn;
            }

            @Override // com.google.android.gms.internal.zzrk
            public final void zza(zzrj zzrjVar) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.common.internal.service.ICommonService");
                    obtain.writeStrongBinder(zzrjVar != null ? zzrjVar.asBinder() : null);
                    this.zzahn.transact(1, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }

        public static zzrk zzea(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.service.ICommonService");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzrk)) ? new C0107zza(iBinder) : (zzrk) queryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            zzrj c0106zza;
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.google.android.gms.common.internal.service.ICommonService");
                    IBinder readStrongBinder = parcel.readStrongBinder();
                    if (readStrongBinder == null) {
                        c0106zza = null;
                    } else {
                        IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.common.internal.service.ICommonCallbacks");
                        c0106zza = (queryLocalInterface == null || !(queryLocalInterface instanceof zzrj)) ? new zzrj.zza.C0106zza(readStrongBinder) : (zzrj) queryLocalInterface;
                    }
                    zza(c0106zza);
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.gms.common.internal.service.ICommonService");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }
}
