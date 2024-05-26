package com.google.android.gms.internal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Base64;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.internal.zzfm;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;

@zzin
/* loaded from: classes.dex */
public final class zzfk {
    final Map<zzfl, zzfm> zzbko = new HashMap();
    final LinkedList<zzfl> zzbkp = new LinkedList<>();
    zzfh zzbkq;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza(String str, zzfl zzflVar) {
        if (zzkd.zzaz(2)) {
            zzkd.v(String.format(str, zzflVar));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String[] zzbe(String str) {
        try {
            String[] split = str.split("\u0000");
            for (int i = 0; i < split.length; i++) {
                split[i] = new String(Base64.decode(split[i], 0), "UTF-8");
            }
            return split;
        } catch (UnsupportedEncodingException e) {
            return new String[0];
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Bundle zzi(AdRequestParcel adRequestParcel) {
        Bundle bundle = adRequestParcel.zzatw;
        if (bundle == null) {
            return null;
        }
        return bundle.getBundle("com.google.ads.mediation.admob.AdMobAdapter");
    }

    private String zzlp() {
        try {
            StringBuilder sb = new StringBuilder();
            Iterator<zzfl> it = this.zzbkp.iterator();
            while (it.hasNext()) {
                sb.append(Base64.encodeToString(it.next().toString().getBytes("UTF-8"), 0));
                if (it.hasNext()) {
                    sb.append("\u0000");
                }
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void zzlo() {
        if (this.zzbkq == null) {
            return;
        }
        for (Map.Entry<zzfl, zzfm> entry : this.zzbko.entrySet()) {
            zzfl key = entry.getKey();
            zzfm value = entry.getValue();
            if (zzkd.zzaz(2)) {
                int size = value.zzbkr.size();
                Iterator<zzfm.zza> it = value.zzbkr.iterator();
                int i = 0;
                while (it.hasNext()) {
                    i = it.next().zzbkz ? i + 1 : i;
                }
                if (i < size) {
                    zzkd.v(String.format("Loading %s/%s pooled interstitials for %s.", Integer.valueOf(size - i), Integer.valueOf(size), key));
                }
            }
            Iterator<zzfm.zza> it2 = value.zzbkr.iterator();
            while (it2.hasNext()) {
                it2.next().zzlv();
            }
            while (value.zzbkr.size() < ((Integer) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbaj)).intValue()) {
                zza("Pooling and loading one new interstitial for %s.", key);
                zzfm.zza zzaVar = new zzfm.zza(this.zzbkq);
                value.zzbkr.add(zzaVar);
                zzaVar.zzlv();
            }
        }
        if (this.zzbkq != null) {
            SharedPreferences.Editor edit = this.zzbkq.mContext.getApplicationContext().getSharedPreferences("com.google.android.gms.ads.internal.interstitial.InterstitialAdPool", 0).edit();
            edit.clear();
            for (Map.Entry<zzfl, zzfm> entry2 : this.zzbko.entrySet()) {
                zzfl key2 = entry2.getKey();
                zzfm value2 = entry2.getValue();
                if (value2.zzbku) {
                    edit.putString(key2.toString(), new zzfo(value2).zzlx());
                    zza("Saved interstitial queue for %s.", key2);
                }
            }
            edit.putString("PoolKeys", zzlp());
            edit.apply();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean zzbf(String str) {
        try {
            return Pattern.matches((String) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbal), str);
        } catch (RuntimeException e) {
            com.google.android.gms.ads.internal.zzu.zzft().zzb((Throwable) e, true);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AdRequestParcel zzl(AdRequestParcel adRequestParcel) {
        Parcel obtain = Parcel.obtain();
        adRequestParcel.writeToParcel(obtain, 0);
        obtain.setDataPosition(0);
        AdRequestParcel adRequestParcel2 = (AdRequestParcel) AdRequestParcel.CREATOR.createFromParcel(obtain);
        obtain.recycle();
        for (String str : ((String) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbah)).split(",")) {
            Bundle bundle = adRequestParcel2.zzatw;
            while (true) {
                String[] split = str.split("/", 2);
                if (split.length != 0) {
                    String str2 = split[0];
                    if (split.length == 1) {
                        bundle.remove(str2);
                        break;
                    }
                    bundle = bundle.getBundle(str2);
                    if (bundle != null) {
                        str = split[1];
                    }
                }
            }
        }
        return adRequestParcel2;
    }
}
