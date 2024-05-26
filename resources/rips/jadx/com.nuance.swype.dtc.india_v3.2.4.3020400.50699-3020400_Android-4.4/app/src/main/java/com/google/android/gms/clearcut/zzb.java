package com.google.android.gms.clearcut;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.common.util.zzh;
import com.google.android.gms.internal.zzapz;
import com.google.android.gms.internal.zzpb;
import com.google.android.gms.internal.zzpc;
import com.google.android.gms.internal.zzpg;
import com.google.android.gms.playlog.internal.PlayLoggerContext;
import java.util.ArrayList;
import java.util.TimeZone;

/* loaded from: classes.dex */
public final class zzb {
    private final String aM;
    private final Context mContext;
    private final int qa;
    private String qb;
    private int qc;
    private String qd;
    private String qe;
    private final boolean qf;
    private int qg;
    private final com.google.android.gms.clearcut.zzc qh;
    private final com.google.android.gms.clearcut.zza qi;
    private zzd qj;
    private final InterfaceC0045zzb qk;
    private final zze zzaoc;
    public static final Api.zzf<zzpc> bJ = new Api.zzf<>();
    public static final Api.zza<zzpc, Object> bK = new Api.zza<zzpc, Object>() { // from class: com.google.android.gms.clearcut.zzb.1
        @Override // com.google.android.gms.common.api.Api.zza
        public final /* synthetic */ zzpc zza(Context context, Looper looper, zzg zzgVar, Object obj, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new zzpc(context, looper, zzgVar, connectionCallbacks, onConnectionFailedListener);
        }
    };
    public static final Api<Object> API = new Api<>("ClearcutLogger.API", bK, bJ);
    public static final com.google.android.gms.clearcut.zzc pZ = new zzpb();

    /* renamed from: com.google.android.gms.clearcut.zzb$zzb, reason: collision with other inner class name */
    /* loaded from: classes.dex */
    public interface InterfaceC0045zzb {
        boolean zzg(String str, int i);
    }

    /* loaded from: classes.dex */
    public interface zzc {
        byte[] zzanb();
    }

    /* loaded from: classes.dex */
    public static class zzd {
    }

    public zzb(Context context, String str) {
        this(context, str, pZ, zzh.zzavm(), com.google.android.gms.clearcut.zza.pY, new zzpg(context));
    }

    private zzb(Context context, String str, com.google.android.gms.clearcut.zzc zzcVar, zze zzeVar, com.google.android.gms.clearcut.zza zzaVar, InterfaceC0045zzb interfaceC0045zzb) {
        this.qc = -1;
        this.qg = 0;
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext == null ? context : applicationContext;
        this.aM = context.getPackageName();
        this.qa = zzbl(context);
        this.qc = -1;
        this.qb = str;
        this.qd = null;
        this.qe = null;
        this.qf = false;
        this.qh = zzcVar;
        this.zzaoc = zzeVar;
        this.qj = new zzd();
        this.qi = zzaVar;
        this.qg = 0;
        this.qk = interfaceC0045zzb;
        if (this.qf) {
            zzab.zzb(this.qd == null, "can't be anonymous with an upload account");
        }
    }

    private static int zzbl(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.wtf("ClearcutLogger", "This can't happen.");
            return 0;
        }
    }

    static /* synthetic */ int zze$4b6941f3() {
        return 0;
    }

    /* loaded from: classes.dex */
    public class zza {
        private String qb;
        private int qc;
        private String qd;
        private String qe;
        private int qg;
        private final zzc ql;
        private ArrayList<Integer> qm;
        private ArrayList<String> qn;
        private ArrayList<Integer> qo;
        private ArrayList<byte[]> qp;
        private boolean qq;
        public final zzapz.zzd qr;
        public boolean qs;

        private zza(zzb zzbVar, byte[] bArr) {
            this(bArr, (byte) 0);
        }

        public /* synthetic */ zza(zzb zzbVar, byte[] bArr, char c) {
            this(zzbVar, bArr);
        }

        public final LogEventParcelable zzana() {
            return new LogEventParcelable(new PlayLoggerContext(zzb.this.aM, zzb.this.qa, this.qc, this.qb, this.qd, this.qe, zzb.this.qf, this.qg), this.qr, this.ql, zzb.zze$97c022f(), zzb.zzf$1d3331e4(), zzb.zze$97c022f(), zzb.zzg$26044621(), this.qq);
        }

        private zza(byte[] bArr, byte b) {
            this.qc = zzb.this.qc;
            this.qb = zzb.this.qb;
            this.qd = zzb.this.qd;
            this.qe = zzb.this.qe;
            this.qg = zzb.zze$4b6941f3();
            this.qm = null;
            this.qn = null;
            this.qo = null;
            this.qp = null;
            this.qq = true;
            this.qr = new zzapz.zzd();
            this.qs = false;
            this.qd = zzb.this.qd;
            this.qe = zzb.this.qe;
            this.qr.bka = zzb.this.zzaoc.currentTimeMillis();
            this.qr.bkb = zzb.this.zzaoc.elapsedRealtime();
            zzapz.zzd zzdVar = this.qr;
            com.google.android.gms.clearcut.zza unused = zzb.this.qi;
            zzdVar.bks = com.google.android.gms.clearcut.zza.zzbk(zzb.this.mContext);
            zzapz.zzd zzdVar2 = this.qr;
            zzd unused2 = zzb.this.qj;
            zzdVar2.bkm = TimeZone.getDefault().getOffset(this.qr.bka) / 1000;
            if (bArr != null) {
                this.qr.bkh = bArr;
            }
            this.ql = null;
        }
    }

    static /* synthetic */ int[] zze$97c022f() {
        return null;
    }

    static /* synthetic */ String[] zzf$1d3331e4() {
        return null;
    }

    static /* synthetic */ byte[][] zzg$26044621() {
        return null;
    }
}
