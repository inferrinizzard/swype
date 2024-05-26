package com.google.android.gms.ads.internal.client;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.internal.zzgj;

/* loaded from: classes.dex */
public interface zzt extends IInterface {

    /* loaded from: classes.dex */
    public static abstract class zza extends Binder implements zzt {

        /* renamed from: com.google.android.gms.ads.internal.client.zzt$zza$zza, reason: collision with other inner class name */
        /* loaded from: classes.dex */
        private static class C0017zza implements zzt {
            private IBinder zzahn;

            C0017zza(IBinder iBinder) {
                this.zzahn = iBinder;
            }

            @Override // android.os.IInterface
            public final IBinder asBinder() {
                return this.zzahn;
            }

            @Override // com.google.android.gms.ads.internal.client.zzt
            public final IBinder zza(com.google.android.gms.dynamic.zzd zzdVar, String str, zzgj zzgjVar, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.ads.internal.client.IAdLoaderBuilderCreator");
                    obtain.writeStrongBinder(zzdVar != null ? zzdVar.asBinder() : null);
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
        }

        public static zzt zzm(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdLoaderBuilderCreator");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzt)) ? new C0017zza(iBinder) : (zzt) queryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.google.android.gms.ads.internal.client.IAdLoaderBuilderCreator");
                    IBinder zza = zza(zzd.zza.zzfc(parcel.readStrongBinder()), parcel.readString(), zzgj.zza.zzaj(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(zza);
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.gms.ads.internal.client.IAdLoaderBuilderCreator");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    IBinder zza(com.google.android.gms.dynamic.zzd zzdVar, String str, zzgj zzgjVar, int i) throws RemoteException;
}
