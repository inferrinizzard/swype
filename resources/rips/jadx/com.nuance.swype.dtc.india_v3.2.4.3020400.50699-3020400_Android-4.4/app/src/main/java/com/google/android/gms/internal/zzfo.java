package com.google.android.gms.internal;

import android.os.Parcel;
import android.util.Base64;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/* JADX INFO: Access modifiers changed from: package-private */
@zzin
/* loaded from: classes.dex */
public final class zzfo {
    final String zzaln;
    final AdRequestParcel zzanc;
    final int zzbkt;

    private zzfo(AdRequestParcel adRequestParcel, String str, int i) {
        this.zzanc = adRequestParcel;
        this.zzaln = str;
        this.zzbkt = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public zzfo(String str) throws IOException {
        String[] split = str.split("\u0000");
        if (split.length != 3) {
            throw new IOException("Incorrect field count for QueueSeed.");
        }
        Parcel obtain = Parcel.obtain();
        try {
            try {
                this.zzaln = new String(Base64.decode(split[0], 0), "UTF-8");
                this.zzbkt = Integer.parseInt(split[1]);
                byte[] decode = Base64.decode(split[2], 0);
                obtain.unmarshall(decode, 0, decode.length);
                obtain.setDataPosition(0);
                this.zzanc = (AdRequestParcel) AdRequestParcel.CREATOR.createFromParcel(obtain);
            } catch (Throwable th) {
                com.google.android.gms.ads.internal.zzu.zzft().zzb(th, true);
                throw new IOException("Malformed QueueSeed encoding.", th);
            }
        } finally {
            obtain.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String zzlx() {
        String str;
        Parcel obtain = Parcel.obtain();
        try {
            try {
                String encodeToString = Base64.encodeToString(this.zzaln.getBytes("UTF-8"), 0);
                String num = Integer.toString(this.zzbkt);
                this.zzanc.writeToParcel(obtain, 0);
                String encodeToString2 = Base64.encodeToString(obtain.marshall(), 0);
                str = new StringBuilder(String.valueOf(encodeToString).length() + 2 + String.valueOf(num).length() + String.valueOf(encodeToString2).length()).append(encodeToString).append("\u0000").append(num).append("\u0000").append(encodeToString2).toString();
            } catch (UnsupportedEncodingException e) {
                zzkd.e("QueueSeed encode failed because UTF-8 is not available.");
                obtain.recycle();
                str = "";
            }
            return str;
        } finally {
            obtain.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public zzfo(zzfm zzfmVar) {
        this(zzfmVar.zzbks, zzfmVar.zzaln, zzfmVar.zzbkt);
    }
}
