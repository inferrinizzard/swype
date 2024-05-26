package android.support.customtabs;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.dex */
public interface ICustomTabsCallback extends IInterface {
    void extraCallback(String str, Bundle bundle) throws RemoteException;

    void onNavigationEvent(int i, Bundle bundle) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ICustomTabsCallback {
        public Stub() {
            attachInterface(this, "android.support.customtabs.ICustomTabsCallback");
        }

        public static ICustomTabsCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface("android.support.customtabs.ICustomTabsCallback");
            if (iin != null && (iin instanceof ICustomTabsCallback)) {
                return (ICustomTabsCallback) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Bundle _arg1;
            Bundle _arg12;
            switch (code) {
                case 2:
                    data.enforceInterface("android.support.customtabs.ICustomTabsCallback");
                    int _arg0 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg12 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    onNavigationEvent(_arg0, _arg12);
                    return true;
                case 3:
                    data.enforceInterface("android.support.customtabs.ICustomTabsCallback");
                    String _arg02 = data.readString();
                    if (data.readInt() != 0) {
                        _arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    extraCallback(_arg02, _arg1);
                    return true;
                case 1598968902:
                    reply.writeString("android.support.customtabs.ICustomTabsCallback");
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ICustomTabsCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public final IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.support.customtabs.ICustomTabsCallback
            public final void onNavigationEvent(int navigationEvent, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.customtabs.ICustomTabsCallback");
                    _data.writeInt(navigationEvent);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.customtabs.ICustomTabsCallback
            public final void extraCallback(String callbackName, Bundle args) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.customtabs.ICustomTabsCallback");
                    _data.writeString(callbackName);
                    if (args != null) {
                        _data.writeInt(1);
                        args.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
