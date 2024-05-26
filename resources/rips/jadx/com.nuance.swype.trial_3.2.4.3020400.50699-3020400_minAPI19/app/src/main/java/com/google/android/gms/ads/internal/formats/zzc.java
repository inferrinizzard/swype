package com.google.android.gms.ads.internal.formats;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.RemoteException;
import com.google.android.gms.internal.zzdr;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public class zzc extends zzdr.zza {
    private final Uri mUri;
    private final Drawable zzbfe;
    private final double zzbff;

    public zzc(Drawable drawable, Uri uri, double d) {
        this.zzbfe = drawable;
        this.mUri = uri;
        this.zzbff = d;
    }

    @Override // com.google.android.gms.internal.zzdr
    public double getScale() {
        return this.zzbff;
    }

    @Override // com.google.android.gms.internal.zzdr
    public Uri getUri() throws RemoteException {
        return this.mUri;
    }

    @Override // com.google.android.gms.internal.zzdr
    public com.google.android.gms.dynamic.zzd zzkt() throws RemoteException {
        return com.google.android.gms.dynamic.zze.zzac(this.zzbfe);
    }
}
