package com.google.android.gms.ads.internal.request;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import com.google.android.gms.ads.internal.zzu;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkd;
import java.io.DataInputStream;
import java.io.IOException;

@zzin
/* loaded from: classes.dex */
public final class LargeParcelTeleporter extends AbstractSafeParcelable {
    public static final Parcelable.Creator<LargeParcelTeleporter> CREATOR = new zzm();
    final int mVersionCode;
    ParcelFileDescriptor zzccz;
    private Parcelable zzcda;
    private boolean zzcdb;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LargeParcelTeleporter(int i, ParcelFileDescriptor parcelFileDescriptor) {
        this.mVersionCode = i;
        this.zzccz = parcelFileDescriptor;
        this.zzcda = null;
        this.zzcdb = true;
    }

    public LargeParcelTeleporter(SafeParcelable safeParcelable) {
        this.mVersionCode = 1;
        this.zzccz = null;
        this.zzcda = safeParcelable;
        this.zzcdb = false;
    }

    private <T> ParcelFileDescriptor zzi(final byte[] bArr) {
        final ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream;
        ParcelFileDescriptor[] createPipe;
        try {
            createPipe = ParcelFileDescriptor.createPipe();
            autoCloseOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream(createPipe[1]);
        } catch (IOException e) {
            e = e;
            autoCloseOutputStream = null;
        }
        try {
            new Thread(new Runnable() { // from class: com.google.android.gms.ads.internal.request.LargeParcelTeleporter.1
                /* JADX WARN: Removed duplicated region for block: B:20:0x0037  */
                /* JADX WARN: Removed duplicated region for block: B:22:0x003d  */
                @Override // java.lang.Runnable
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct code enable 'Show inconsistent code' option in preferences
                */
                public final void run() {
                    /*
                        r4 = this;
                        r2 = 0
                        java.io.DataOutputStream r1 = new java.io.DataOutputStream     // Catch: java.io.IOException -> L17 java.lang.Throwable -> L33
                        java.io.OutputStream r0 = r2     // Catch: java.io.IOException -> L17 java.lang.Throwable -> L33
                        r1.<init>(r0)     // Catch: java.io.IOException -> L17 java.lang.Throwable -> L33
                        byte[] r0 = r3     // Catch: java.lang.Throwable -> L41 java.io.IOException -> L43
                        int r0 = r0.length     // Catch: java.lang.Throwable -> L41 java.io.IOException -> L43
                        r1.writeInt(r0)     // Catch: java.lang.Throwable -> L41 java.io.IOException -> L43
                        byte[] r0 = r3     // Catch: java.lang.Throwable -> L41 java.io.IOException -> L43
                        r1.write(r0)     // Catch: java.lang.Throwable -> L41 java.io.IOException -> L43
                        com.google.android.gms.common.util.zzo.zzb(r1)
                    L16:
                        return
                    L17:
                        r0 = move-exception
                        r1 = r2
                    L19:
                        java.lang.String r2 = "Error transporting the ad response"
                        com.google.android.gms.internal.zzkd.zzb(r2, r0)     // Catch: java.lang.Throwable -> L41
                        com.google.android.gms.internal.zzjx r2 = com.google.android.gms.ads.internal.zzu.zzft()     // Catch: java.lang.Throwable -> L41
                        r3 = 1
                        r2.zzb(r0, r3)     // Catch: java.lang.Throwable -> L41
                        if (r1 != 0) goto L2f
                        java.io.OutputStream r0 = r2
                        com.google.android.gms.common.util.zzo.zzb(r0)
                        goto L16
                    L2f:
                        com.google.android.gms.common.util.zzo.zzb(r1)
                        goto L16
                    L33:
                        r0 = move-exception
                        r1 = r2
                    L35:
                        if (r1 != 0) goto L3d
                        java.io.OutputStream r1 = r2
                        com.google.android.gms.common.util.zzo.zzb(r1)
                    L3c:
                        throw r0
                    L3d:
                        com.google.android.gms.common.util.zzo.zzb(r1)
                        goto L3c
                    L41:
                        r0 = move-exception
                        goto L35
                    L43:
                        r0 = move-exception
                        goto L19
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.ads.internal.request.LargeParcelTeleporter.AnonymousClass1.run():void");
                }
            }).start();
            return createPipe[0];
        } catch (IOException e2) {
            e = e2;
            zzkd.zzb("Error transporting the ad response", e);
            zzu.zzft().zzb((Throwable) e, true);
            com.google.android.gms.common.util.zzo.zzb(autoCloseOutputStream);
            return null;
        }
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        if (this.zzccz == null) {
            Parcel obtain = Parcel.obtain();
            try {
                this.zzcda.writeToParcel(obtain, 0);
                byte[] marshall = obtain.marshall();
                obtain.recycle();
                this.zzccz = zzi(marshall);
            } catch (Throwable th) {
                obtain.recycle();
                throw th;
            }
        }
        zzm.zza(this, parcel, i);
    }

    public final <T extends SafeParcelable> T zza(Parcelable.Creator<T> creator) {
        if (this.zzcdb) {
            if (this.zzccz == null) {
                zzkd.e("File descriptor is empty, returning null.");
                return null;
            }
            DataInputStream dataInputStream = new DataInputStream(new ParcelFileDescriptor.AutoCloseInputStream(this.zzccz));
            try {
                try {
                    byte[] bArr = new byte[dataInputStream.readInt()];
                    dataInputStream.readFully(bArr, 0, bArr.length);
                    com.google.android.gms.common.util.zzo.zzb(dataInputStream);
                    Parcel obtain = Parcel.obtain();
                    try {
                        obtain.unmarshall(bArr, 0, bArr.length);
                        obtain.setDataPosition(0);
                        this.zzcda = creator.createFromParcel(obtain);
                        obtain.recycle();
                        this.zzcdb = false;
                    } catch (Throwable th) {
                        obtain.recycle();
                        throw th;
                    }
                } catch (IOException e) {
                    throw new IllegalStateException("Could not read from parcel file descriptor", e);
                }
            } catch (Throwable th2) {
                com.google.android.gms.common.util.zzo.zzb(dataInputStream);
                throw th2;
            }
        }
        return (T) this.zzcda;
    }
}
