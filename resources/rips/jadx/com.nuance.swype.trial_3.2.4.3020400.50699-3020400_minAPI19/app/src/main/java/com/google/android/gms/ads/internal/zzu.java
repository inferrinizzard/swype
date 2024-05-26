package com.google.android.gms.ads.internal;

import android.os.Build;
import com.google.android.gms.internal.zzcz;
import com.google.android.gms.internal.zzda;
import com.google.android.gms.internal.zzdb;
import com.google.android.gms.internal.zzdf;
import com.google.android.gms.internal.zzfc;
import com.google.android.gms.internal.zzfk;
import com.google.android.gms.internal.zzgf;
import com.google.android.gms.internal.zzic;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zziw;
import com.google.android.gms.internal.zzjx;
import com.google.android.gms.internal.zzkh;
import com.google.android.gms.internal.zzki;
import com.google.android.gms.internal.zzko;
import com.google.android.gms.internal.zzkp;
import com.google.android.gms.internal.zzlc;
import com.google.android.gms.internal.zzlj;

@zzin
/* loaded from: classes.dex */
public class zzu {
    private static final Object zzamr = new Object();
    private static zzu zzant;
    private final com.google.android.gms.ads.internal.request.zza zzanu = new com.google.android.gms.ads.internal.request.zza();
    private final com.google.android.gms.ads.internal.overlay.zza zzanv = new com.google.android.gms.ads.internal.overlay.zza();
    private final com.google.android.gms.ads.internal.overlay.zze zzanw = new com.google.android.gms.ads.internal.overlay.zze();
    private final zzic zzanx = new zzic();
    private final zzkh zzany = new zzkh();
    private final zzlj zzanz = new zzlj();
    private final zzki zzaoa;
    private final zzjx zzaob;
    private final com.google.android.gms.common.util.zze zzaoc;
    private final zzdf zzaod;
    private final zziw zzaoe;
    private final zzda zzaof;
    private final zzcz zzaog;
    private final zzdb zzaoh;
    private final com.google.android.gms.ads.internal.purchase.zzi zzaoi;
    private final zzfk zzaoj;
    private final zzko zzaok;
    private final com.google.android.gms.ads.internal.overlay.zzq zzaol;
    private final com.google.android.gms.ads.internal.overlay.zzr zzaom;
    private final zzgf zzaon;
    private final zzkp zzaoo;
    private final zzg zzaop;
    private final zzp zzaoq;
    private final zzfc zzaor;
    private final zzlc zzaos;

    private static zzu zzfl() {
        zzu zzuVar;
        synchronized (zzamr) {
            zzuVar = zzant;
        }
        return zzuVar;
    }

    public static com.google.android.gms.ads.internal.request.zza zzfm() {
        return zzfl().zzanu;
    }

    public static com.google.android.gms.ads.internal.overlay.zza zzfn() {
        return zzfl().zzanv;
    }

    public static com.google.android.gms.ads.internal.overlay.zze zzfo() {
        return zzfl().zzanw;
    }

    public static zzic zzfp() {
        return zzfl().zzanx;
    }

    public static zzkh zzfq() {
        return zzfl().zzany;
    }

    public static zzlj zzfr() {
        return zzfl().zzanz;
    }

    public static zzki zzfs() {
        return zzfl().zzaoa;
    }

    public static zzjx zzft() {
        return zzfl().zzaob;
    }

    public static com.google.android.gms.common.util.zze zzfu() {
        return zzfl().zzaoc;
    }

    public static zzdf zzfv() {
        return zzfl().zzaod;
    }

    public static zziw zzfw() {
        return zzfl().zzaoe;
    }

    public static zzda zzfx() {
        return zzfl().zzaof;
    }

    public static zzcz zzfy() {
        return zzfl().zzaog;
    }

    public static zzdb zzfz() {
        return zzfl().zzaoh;
    }

    public static com.google.android.gms.ads.internal.purchase.zzi zzga() {
        return zzfl().zzaoi;
    }

    public static zzfk zzgb() {
        return zzfl().zzaoj;
    }

    public static zzko zzgc() {
        return zzfl().zzaok;
    }

    public static com.google.android.gms.ads.internal.overlay.zzq zzgd() {
        return zzfl().zzaol;
    }

    public static com.google.android.gms.ads.internal.overlay.zzr zzge() {
        return zzfl().zzaom;
    }

    public static zzgf zzgf() {
        return zzfl().zzaon;
    }

    public static zzp zzgg() {
        return zzfl().zzaoq;
    }

    public static zzkp zzgh() {
        return zzfl().zzaoo;
    }

    public static zzg zzgi() {
        return zzfl().zzaop;
    }

    public static zzfc zzgj() {
        return zzfl().zzaor;
    }

    public static zzlc zzgk() {
        return zzfl().zzaos;
    }

    protected zzu() {
        zzki zzgVar;
        int i = Build.VERSION.SDK_INT;
        if (i >= 21) {
            zzgVar = new zzki.zzh();
        } else {
            zzgVar = i >= 19 ? new zzki.zzg() : i >= 18 ? new zzki.zze() : i >= 17 ? new zzki.zzd() : i >= 16 ? new zzki.zzf() : i >= 14 ? new zzki.zzc() : i >= 11 ? new zzki.zzb() : i >= 9 ? new zzki.zza() : new zzki();
        }
        this.zzaoa = zzgVar;
        this.zzaob = new zzjx();
        this.zzaoc = new com.google.android.gms.common.util.zzh();
        this.zzaod = new zzdf();
        this.zzaoe = new zziw();
        this.zzaof = new zzda();
        this.zzaog = new zzcz();
        this.zzaoh = new zzdb();
        this.zzaoi = new com.google.android.gms.ads.internal.purchase.zzi();
        this.zzaoj = new zzfk();
        this.zzaok = new zzko();
        this.zzaol = new com.google.android.gms.ads.internal.overlay.zzq();
        this.zzaom = new com.google.android.gms.ads.internal.overlay.zzr();
        this.zzaon = new zzgf();
        this.zzaoo = new zzkp();
        this.zzaop = new zzg();
        this.zzaoq = new zzp();
        this.zzaor = new zzfc();
        this.zzaos = new zzlc();
    }

    static {
        zzu zzuVar = new zzu();
        synchronized (zzamr) {
            zzant = zzuVar;
        }
    }
}
