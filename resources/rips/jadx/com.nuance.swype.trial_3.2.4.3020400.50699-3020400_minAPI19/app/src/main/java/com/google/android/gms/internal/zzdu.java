package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.zzd;

/* loaded from: classes.dex */
public interface zzdu extends IInterface {

    /* loaded from: classes.dex */
    public static abstract class zza extends Binder implements zzdu {

        /* renamed from: com.google.android.gms.internal.zzdu$zza$zza, reason: collision with other inner class name */
        /* loaded from: classes.dex */
        private static class C0068zza implements zzdu {
            private IBinder zzahn;

            C0068zza(IBinder iBinder) {
                this.zzahn = iBinder;
            }

            @Override // android.os.IInterface
            public final IBinder asBinder() {
                return this.zzahn;
            }

            @Override // com.google.android.gms.internal.zzdu
            public final IBinder zza(com.google.android.gms.dynamic.zzd zzdVar, com.google.android.gms.dynamic.zzd zzdVar2, com.google.android.gms.dynamic.zzd zzdVar3, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.ads.internal.formats.client.INativeAdViewDelegateCreator");
                    obtain.writeStrongBinder(zzdVar != null ? zzdVar.asBinder() : null);
                    obtain.writeStrongBinder(zzdVar2 != null ? zzdVar2.asBinder() : null);
                    obtain.writeStrongBinder(zzdVar3 != null ? zzdVar3.asBinder() : null);
                    obtain.writeInt(i);
                    this.zzahn.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readStrongBinder();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static zzdu zzaa(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.ads.internal.formats.client.INativeAdViewDelegateCreator");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzdu)) ? new C0068zza(iBinder) : (zzdu) queryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeAdViewDelegateCreator");
                    IBinder zza = zza(zzd.zza.zzfc(parcel.readStrongBinder()), zzd.zza.zzfc(parcel.readStrongBinder()), zzd.zza.zzfc(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(zza);
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.gms.ads.internal.formats.client.INativeAdViewDelegateCreator");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    IBinder zza(com.google.android.gms.dynamic.zzd zzdVar, com.google.android.gms.dynamic.zzd zzdVar2, com.google.android.gms.dynamic.zzd zzdVar3, int i) throws RemoteException;
}
