package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.security.NetworkSecurityPolicy;
import android.text.TextUtils;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.dynamite.descriptors.com.google.android.gms.ads.dynamite.ModuleDescriptor;
import com.google.android.gms.internal.zzkf;
import com.google.android.gms.internal.zzsb;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.Future;

@zzin
/* loaded from: classes.dex */
public final class zzjx implements zzkf.zzb {
    private Context mContext;
    public zzcg zzaju;
    private VersionInfoParcel zzalo;
    private String zzbjf;
    private String zzcjv;
    public final Object zzail = new Object();
    private BigInteger zzcjo = BigInteger.ONE;
    public final HashSet<zzjv> zzcjp = new HashSet<>();
    public final HashMap<String, zzka> zzcjq = new HashMap<>();
    private boolean zzcjr = false;
    private boolean zzcff = true;
    private int zzcjs = 0;
    private boolean zzamt = false;
    private zzde zzcjt = null;
    private boolean zzcfg = true;
    private zzcn zzask = null;
    private zzco zzcju = null;
    private zzcm zzasl = null;
    Boolean zzcjw = null;
    public boolean zzcjx = false;
    private boolean zzcjy = false;
    private boolean zzcfo = false;
    public boolean zzcjz = false;
    private String zzcka = "";
    private long zzckb = 0;
    public final String zzcjm = zzkh.zztf();
    private final zzjy zzcjn = new zzjy(this.zzcjm);

    private boolean zzsi() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzcfg;
        }
        return z;
    }

    public final Bundle zza(Context context, zzjz zzjzVar, String str) {
        Bundle bundle;
        synchronized (this.zzail) {
            bundle = new Bundle();
            bundle.putBundle("app", this.zzcjn.zze(context, str));
            Bundle bundle2 = new Bundle();
            for (String str2 : this.zzcjq.keySet()) {
                bundle2.putBundle(str2, this.zzcjq.get(str2).toBundle());
            }
            bundle.putBundle("slots", bundle2);
            ArrayList<? extends Parcelable> arrayList = new ArrayList<>();
            Iterator<zzjv> it = this.zzcjp.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next().toBundle());
            }
            bundle.putParcelableArrayList("ads", arrayList);
            zzjzVar.zza(this.zzcjp);
            this.zzcjp.clear();
        }
        return bundle;
    }

    public final void zza(zzjv zzjvVar) {
        synchronized (this.zzail) {
            this.zzcjp.add(zzjvVar);
        }
    }

    public final void zzb(Throwable th, boolean z) {
        new zzim(this.mContext, this.zzalo, null, null).zza(th, z);
    }

    @Override // com.google.android.gms.internal.zzkf.zzb
    public final void zzg(Bundle bundle) {
        synchronized (this.zzail) {
            this.zzcff = bundle.containsKey("use_https") ? bundle.getBoolean("use_https") : this.zzcff;
            this.zzcjs = bundle.containsKey("webview_cache_version") ? bundle.getInt("webview_cache_version") : this.zzcjs;
            if (bundle.containsKey("content_url_opted_out")) {
                zzae(bundle.getBoolean("content_url_opted_out"));
            }
            if (bundle.containsKey("content_url_hashes")) {
                this.zzcjv = bundle.getString("content_url_hashes");
            }
            this.zzcfo = bundle.containsKey("auto_collect_location") ? bundle.getBoolean("auto_collect_location") : this.zzcfo;
            this.zzcka = bundle.containsKey("app_settings_json") ? bundle.getString("app_settings_json") : this.zzcka;
            this.zzckb = bundle.containsKey("app_settings_last_update_ms") ? bundle.getLong("app_settings_last_update_ms") : 0L;
        }
    }

    public final String zzsj() {
        String bigInteger;
        synchronized (this.zzail) {
            bigInteger = this.zzcjo.toString();
            this.zzcjo = this.zzcjo.add(BigInteger.ONE);
        }
        return bigInteger;
    }

    public final zzjy zzsk() {
        zzjy zzjyVar;
        synchronized (this.zzail) {
            zzjyVar = this.zzcjn;
        }
        return zzjyVar;
    }

    public final zzde zzsl() {
        zzde zzdeVar;
        synchronized (this.zzail) {
            zzdeVar = this.zzcjt;
        }
        return zzdeVar;
    }

    public final boolean zzsm() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzcjr;
            this.zzcjr = true;
        }
        return z;
    }

    public final boolean zzsn() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzcff || this.zzcjy;
        }
        return z;
    }

    public final String zzso() {
        String str;
        synchronized (this.zzail) {
            str = this.zzbjf;
        }
        return str;
    }

    public final String zzsp() {
        String str;
        synchronized (this.zzail) {
            str = this.zzcjv;
        }
        return str;
    }

    public final Boolean zzsq() {
        Boolean bool;
        synchronized (this.zzail) {
            bool = this.zzcjw;
        }
        return bool;
    }

    public final boolean zzsr() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzcfo;
        }
        return z;
    }

    public final zzjw zzst() {
        zzjw zzjwVar;
        synchronized (this.zzail) {
            zzjwVar = new zzjw(this.zzcka, this.zzckb);
        }
        return zzjwVar;
    }

    public final boolean zzsv() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzcjx;
        }
        return z;
    }

    public final zzco zzaa(Context context) {
        if (!((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzazh)).booleanValue() || !com.google.android.gms.common.util.zzs.zzhb(14) || zzsi()) {
            return null;
        }
        synchronized (this.zzail) {
            if (Looper.getMainLooper() == null || context == null) {
                return null;
            }
            if (this.zzask == null) {
                Application application = (Application) context.getApplicationContext();
                if (application == null) {
                    application = (Application) context;
                }
                this.zzask = new zzcn(application, context);
            }
            if (this.zzasl == null) {
                this.zzasl = new zzcm();
            }
            if (this.zzcju == null) {
                this.zzcju = new zzco(this.zzask, this.zzasl, new zzim(this.mContext, this.zzalo, null, null));
            }
            this.zzcju.zzhz();
            return this.zzcju;
        }
    }

    public final void zzae(boolean z) {
        synchronized (this.zzail) {
            if (this.zzcfg != z) {
                new zzkf.zza() { // from class: com.google.android.gms.internal.zzkf.7
                    final /* synthetic */ Context zzala;
                    final /* synthetic */ boolean zzckr;

                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    public AnonymousClass7(Context context, boolean z2) {
                        super((byte) 0);
                        r2 = context;
                        r3 = z2;
                    }

                    @Override // com.google.android.gms.internal.zzkc
                    public final void zzew() {
                        SharedPreferences.Editor edit = r2.getSharedPreferences("admob", 0).edit();
                        edit.putBoolean("content_url_opted_out", r3);
                        edit.apply();
                    }
                }.zzpy();
            }
            this.zzcfg = z2;
            zzco zzaa = zzaa(this.mContext);
            if (zzaa != null && !zzaa.isAlive()) {
                zzkd.zzcw("start fetching content...");
                zzaa.zzhz();
            }
        }
    }

    public final Future zzc(Context context, boolean z) {
        Future future;
        synchronized (this.zzail) {
            if (z != this.zzcff) {
                this.zzcff = z;
                future = (Future) new zzkf.zza() { // from class: com.google.android.gms.internal.zzkf.1
                    final /* synthetic */ Context zzala;
                    final /* synthetic */ boolean zzckn;

                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    public AnonymousClass1(Context context2, boolean z2) {
                        super((byte) 0);
                        r2 = context2;
                        r3 = z2;
                    }

                    @Override // com.google.android.gms.internal.zzkc
                    public final void zzew() {
                        SharedPreferences.Editor edit = r2.getSharedPreferences("admob", 0).edit();
                        edit.putBoolean("use_https", r3);
                        edit.apply();
                    }
                }.zzpy();
            } else {
                future = null;
            }
        }
        return future;
    }

    public final Future zzcm(String str) {
        Future future;
        synchronized (this.zzail) {
            if (str != null) {
                if (!str.equals(this.zzcjv)) {
                    this.zzcjv = str;
                    future = (Future) new zzkf.zza() { // from class: com.google.android.gms.internal.zzkf.9
                        final /* synthetic */ Context zzala;
                        final /* synthetic */ String zzcks;

                        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                        public AnonymousClass9(Context context, String str2) {
                            super((byte) 0);
                            r2 = context;
                            r3 = str2;
                        }

                        @Override // com.google.android.gms.internal.zzkc
                        public final void zzew() {
                            SharedPreferences.Editor edit = r2.getSharedPreferences("admob", 0).edit();
                            edit.putString("content_url_hashes", r3);
                            edit.apply();
                        }
                    }.zzpy();
                }
            }
            future = null;
        }
        return future;
    }

    public final Future zzd(Context context, boolean z) {
        Future future;
        synchronized (this.zzail) {
            if (z != this.zzcfo) {
                this.zzcfo = z;
                future = (Future) new zzkf.zza() { // from class: com.google.android.gms.internal.zzkf.11
                    final /* synthetic */ Context zzala;
                    final /* synthetic */ boolean zzckt;

                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    public AnonymousClass11(Context context2, boolean z2) {
                        super((byte) 0);
                        r2 = context2;
                        r3 = z2;
                    }

                    @Override // com.google.android.gms.internal.zzkc
                    public final void zzew() {
                        SharedPreferences.Editor edit = r2.getSharedPreferences("admob", 0).edit();
                        edit.putBoolean("auto_collect_location", r3);
                        edit.apply();
                    }
                }.zzpy();
            } else {
                future = null;
            }
        }
        return future;
    }

    public final Future zzd(Context context, String str) {
        Future future;
        this.zzckb = com.google.android.gms.ads.internal.zzu.zzfu().currentTimeMillis();
        synchronized (this.zzail) {
            if (str != null) {
                if (!str.equals(this.zzcka)) {
                    this.zzcka = str;
                    future = (Future) new zzkf.zza() { // from class: com.google.android.gms.internal.zzkf.3
                        final /* synthetic */ Context zzala;
                        final /* synthetic */ String zzckp;
                        final /* synthetic */ long zzckq;

                        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                        public AnonymousClass3(Context context2, String str2, long j) {
                            super((byte) 0);
                            r2 = context2;
                            r3 = str2;
                            r4 = j;
                        }

                        @Override // com.google.android.gms.internal.zzkc
                        public final void zzew() {
                            SharedPreferences.Editor edit = r2.getSharedPreferences("admob", 0).edit();
                            edit.putString("app_settings_json", r3);
                            edit.putLong("app_settings_last_update_ms", r4);
                            edit.apply();
                        }
                    }.zzpy();
                }
            }
            future = null;
        }
        return future;
    }

    @TargetApi(23)
    public final void zzb(Context context, VersionInfoParcel versionInfoParcel) {
        zzde zzdeVar;
        synchronized (this.zzail) {
            if (!this.zzamt) {
                this.mContext = context.getApplicationContext();
                this.zzalo = versionInfoParcel;
                new zzkf.zza() { // from class: com.google.android.gms.internal.zzkf.5
                    final /* synthetic */ Context zzala;
                    final /* synthetic */ zzb zzcko;

                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    public AnonymousClass5(Context context2, zzb this) {
                        super((byte) 0);
                        r2 = context2;
                        r3 = this;
                    }

                    @Override // com.google.android.gms.internal.zzkc
                    public final void zzew() {
                        SharedPreferences sharedPreferences = r2.getSharedPreferences("admob", 0);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("use_https", sharedPreferences.getBoolean("use_https", true));
                        if (r3 != null) {
                            r3.zzg(bundle);
                        }
                    }
                }.zzpy();
                new zzkf.zza() { // from class: com.google.android.gms.internal.zzkf.6
                    final /* synthetic */ Context zzala;
                    final /* synthetic */ zzb zzcko;

                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    public AnonymousClass6(Context context2, zzb this) {
                        super((byte) 0);
                        r2 = context2;
                        r3 = this;
                    }

                    @Override // com.google.android.gms.internal.zzkc
                    public final void zzew() {
                        SharedPreferences sharedPreferences = r2.getSharedPreferences("admob", 0);
                        Bundle bundle = new Bundle();
                        bundle.putInt("webview_cache_version", sharedPreferences.getInt("webview_cache_version", 0));
                        if (r3 != null) {
                            r3.zzg(bundle);
                        }
                    }
                }.zzpy();
                new zzkf.zza() { // from class: com.google.android.gms.internal.zzkf.8
                    final /* synthetic */ Context zzala;
                    final /* synthetic */ zzb zzcko;

                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    public AnonymousClass8(Context context2, zzb this) {
                        super((byte) 0);
                        r2 = context2;
                        r3 = this;
                    }

                    @Override // com.google.android.gms.internal.zzkc
                    public final void zzew() {
                        SharedPreferences sharedPreferences = r2.getSharedPreferences("admob", 0);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("content_url_opted_out", sharedPreferences.getBoolean("content_url_opted_out", true));
                        if (r3 != null) {
                            r3.zzg(bundle);
                        }
                    }
                }.zzpy();
                new zzkf.zza() { // from class: com.google.android.gms.internal.zzkf.10
                    final /* synthetic */ Context zzala;
                    final /* synthetic */ zzb zzcko;

                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    public AnonymousClass10(Context context2, zzb this) {
                        super((byte) 0);
                        r2 = context2;
                        r3 = this;
                    }

                    @Override // com.google.android.gms.internal.zzkc
                    public final void zzew() {
                        SharedPreferences sharedPreferences = r2.getSharedPreferences("admob", 0);
                        Bundle bundle = new Bundle();
                        bundle.putString("content_url_hashes", sharedPreferences.getString("content_url_hashes", ""));
                        if (r3 != null) {
                            r3.zzg(bundle);
                        }
                    }
                }.zzpy();
                new zzkf.zza() { // from class: com.google.android.gms.internal.zzkf.2
                    final /* synthetic */ Context zzala;
                    final /* synthetic */ zzb zzcko;

                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    public AnonymousClass2(Context context2, zzb this) {
                        super((byte) 0);
                        r2 = context2;
                        r3 = this;
                    }

                    @Override // com.google.android.gms.internal.zzkc
                    public final void zzew() {
                        SharedPreferences sharedPreferences = r2.getSharedPreferences("admob", 0);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("auto_collect_location", sharedPreferences.getBoolean("auto_collect_location", false));
                        if (r3 != null) {
                            r3.zzg(bundle);
                        }
                    }
                }.zzpy();
                new zzkf.zza() { // from class: com.google.android.gms.internal.zzkf.4
                    final /* synthetic */ Context zzala;
                    final /* synthetic */ zzb zzcko;

                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    public AnonymousClass4(Context context2, zzb this) {
                        super((byte) 0);
                        r2 = context2;
                        r3 = this;
                    }

                    @Override // com.google.android.gms.internal.zzkc
                    public final void zzew() {
                        SharedPreferences sharedPreferences = r2.getSharedPreferences("admob", 0);
                        Bundle bundle = new Bundle();
                        bundle.putString("app_settings_json", sharedPreferences.getString("app_settings_json", ""));
                        bundle.putLong("app_settings_last_update_ms", sharedPreferences.getLong("app_settings_last_update_ms", 0L));
                        if (r3 != null) {
                            r3.zzg(bundle);
                        }
                    }
                }.zzpy();
                zzim.zza(this.mContext, Thread.currentThread(), this.zzalo);
                this.zzbjf = com.google.android.gms.ads.internal.zzu.zzfq().zzg(context2, versionInfoParcel.zzcs);
                if (com.google.android.gms.common.util.zzs.zzhb(23) && !NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted()) {
                    this.zzcjy = true;
                }
                this.zzaju = new zzcg(context2.getApplicationContext(), this.zzalo, com.google.android.gms.ads.internal.zzu.zzfq().zzc(context2, versionInfoParcel));
                zzdd zzddVar = new zzdd(this.mContext, this.zzalo.zzcs);
                try {
                    com.google.android.gms.ads.internal.zzu.zzfv();
                    if (!zzddVar.zzbdo) {
                        zzkd.v("CsiReporterFactory: CSI is not enabled. No CSI reporter created.");
                        zzdeVar = null;
                    } else {
                        if (zzddVar.mContext == null) {
                            throw new IllegalArgumentException("Context can't be null. Please set up context in CsiConfiguration.");
                        }
                        if (TextUtils.isEmpty(zzddVar.zzarj)) {
                            throw new IllegalArgumentException("AfmaVersion can't be null or empty. Please set up afmaVersion in CsiConfiguration.");
                        }
                        zzdeVar = new zzde(zzddVar.mContext, zzddVar.zzarj, zzddVar.zzbdp, zzddVar.zzbdq);
                    }
                    this.zzcjt = zzdeVar;
                } catch (IllegalArgumentException e) {
                    zzkd.zzd("Cannot initialize CSI reporter.", e);
                }
                com.google.android.gms.ads.internal.zzu.zzga().zzt(this.mContext);
                this.zzamt = true;
            }
        }
    }

    public final Resources getResources() {
        if (this.zzalo.zzcnm) {
            return this.mContext.getResources();
        }
        try {
            zzsb zza = zzsb.zza(this.mContext, zzsb.KI, ModuleDescriptor.MODULE_ID);
            if (zza != null) {
                return zza.KN.getResources();
            }
            return null;
        } catch (zzsb.zza e) {
            zzkd.zzd("Cannot load resource from dynamite apk or local jar", e);
            return null;
        }
    }
}
