package com.google.android.gms.ads.internal.client;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.internal.zzgj;

/* loaded from: classes.dex */
public interface zzv extends IInterface {

    /* loaded from: classes.dex */
    public static abstract class zza extends Binder implements zzv {

        /* renamed from: com.google.android.gms.ads.internal.client.zzv$zza$zza, reason: collision with other inner class name */
        /* loaded from: classes.dex */
        private static class C0019zza implements zzv {
            private IBinder zzahn;

            C0019zza(IBinder iBinder) {
                this.zzahn = iBinder;
            }

            @Override // android.os.IInterface
            public final IBinder asBinder() {
                return this.zzahn;
            }

            @Override // com.google.android.gms.ads.internal.client.zzv
            public final IBinder zza(com.google.android.gms.dynamic.zzd zzdVar, AdSizeParcel adSizeParcel, String str, zzgj zzgjVar, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.ads.internal.client.IAdManagerCreator");
                    obtain.writeStrongBinder(zzdVar != null ? zzdVar.asBinder() : null);
                    if (adSizeParcel != null) {
                        obtain.writeInt(1);
                        adSizeParcel.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeStrongBinder(zzgjVar != null ? zzgjVar.asBinder() : null);
                    obtain.writeInt(i);
                    this.zzahn.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readStrongBinder();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.google.android.gms.ads.internal.client.zzv
            public final IBinder zza(com.google.android.gms.dynamic.zzd zzdVar, AdSizeParcel adSizeParcel, String str, zzgj zzgjVar, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.ads.internal.client.IAdManagerCreator");
                    obtain.writeStrongBinder(zzdVar != null ? zzdVar.asBinder() : null);
                    if (adSizeParcel != null) {
                        obtain.writeInt(1);
                        adSizeParcel.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeStrongBinder(zzgjVar != null ? zzgjVar.asBinder() : null);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.zzahn.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readStrongBinder();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static zzv zzo(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdManagerCreator");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzv)) ? new C0019zza(iBinder) : (zzv) queryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.google.android.gms.ads.internal.client.IAdManagerCreator");
                    IBinder zza = zza(zzd.zza.zzfc(parcel.readStrongBinder()), parcel.readInt() != 0 ? (AdSizeParcel) AdSizeParcel.CREATOR.createFromParcel(parcel) : null, parcel.readString(), zzgj.zza.zzaj(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(zza);
                    return true;
                case 2:
                    parcel.enforceInterface("com.google.android.gms.ads.internal.client.IAdManagerCreator");
                    IBinder zza2 = zza(zzd.zza.zzfc(parcel.readStrongBinder()), parcel.readInt() != 0 ? (AdSizeParcel) AdSizeParcel.CREATOR.createFromParcel(parcel) : null, parcel.readString(), zzgj.zza.zzaj(parcel.readStrongBinder()), parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(zza2);
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.gms.ads.internal.client.IAdManagerCreator");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    IBinder zza(com.google.android.gms.dynamic.zzd zzdVar, AdSizeParcel adSizeParcel, String str, zzgj zzgjVar, int i) throws RemoteException;

    IBinder zza(com.google.android.gms.dynamic.zzd zzdVar, AdSizeParcel adSizeParcel, String str, zzgj zzgjVar, int i, int i2) throws RemoteException;
}