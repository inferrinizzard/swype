package com.google.android.gms.internal;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import com.google.android.gms.internal.zzb;
import com.google.android.gms.internal.zzm;
import com.google.android.gms.internal.zzs;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

/* loaded from: classes.dex */
public abstract class zzk<T> implements Comparable<zzk<T>> {
    private final zzs.zza zzac;
    final int zzad;
    final String zzae;
    final int zzaf;
    final zzm.zza zzag;
    Integer zzah;
    zzl zzai;
    boolean zzaj;
    private boolean zzak;
    boolean zzal;
    private long zzam;
    zzo zzan;
    zzb.zza zzao;

    /* loaded from: classes.dex */
    public enum zza {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static zzr zzb(zzr zzrVar) {
        return zzrVar;
    }

    public Map<String, String> getHeaders() throws com.google.android.gms.internal.zza {
        return Collections.emptyMap();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract zzm<T> zza(zzi zziVar);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void zza(T t);

    public final void zzc(String str) {
        if (zzs.zza.zzbj) {
            this.zzac.zza(str, Thread.currentThread().getId());
        } else if (this.zzam == 0) {
            this.zzam = SystemClock.elapsedRealtime();
        }
    }

    public byte[] zzp() throws com.google.android.gms.internal.zza {
        return null;
    }

    public final int zzs() {
        return this.zzan.zzc();
    }

    public zzk(int i, String str, zzm.zza zzaVar) {
        Uri parse;
        String host;
        this.zzac = zzs.zza.zzbj ? new zzs.zza() : null;
        this.zzaj = true;
        this.zzak = false;
        this.zzal = false;
        this.zzam = 0L;
        this.zzao = null;
        this.zzad = i;
        this.zzae = str;
        this.zzag = zzaVar;
        this.zzan = new zzd();
        this.zzaf = (TextUtils.isEmpty(str) || (parse = Uri.parse(str)) == null || (host = parse.getHost()) == null) ? 0 : host.hashCode();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void zzd(final String str) {
        if (this.zzai != null) {
            zzl zzlVar = this.zzai;
            synchronized (zzlVar.zzaz) {
                zzlVar.zzaz.remove(this);
            }
            synchronized (zzlVar.zzbe) {
                Iterator<Object> it = zzlVar.zzbe.iterator();
                while (it.hasNext()) {
                    it.next();
                }
            }
            if (this.zzaj) {
                synchronized (zzlVar.zzay) {
                    String str2 = this.zzae;
                    Queue<zzk<?>> remove = zzlVar.zzay.remove(str2);
                    if (remove != null) {
                        if (zzs.DEBUG) {
                            zzs.zza("Releasing %d waiting requests for cacheKey=%s.", Integer.valueOf(remove.size()), str2);
                        }
                        zzlVar.zzba.addAll(remove);
                    }
                }
            }
        }
        if (!zzs.zza.zzbj) {
            long elapsedRealtime = SystemClock.elapsedRealtime() - this.zzam;
            if (elapsedRealtime >= 3000) {
                zzs.zzb("%d ms: %s", Long.valueOf(elapsedRealtime), toString());
                return;
            }
            return;
        }
        final long id = Thread.currentThread().getId();
        if (Looper.myLooper() != Looper.getMainLooper()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.google.android.gms.internal.zzk.1
                @Override // java.lang.Runnable
                public final void run() {
                    zzk.this.zzac.zza(str, id);
                    zzk.this.zzac.zzd(toString());
                }
            });
        } else {
            this.zzac.zza(str, id);
            this.zzac.zzd(toString());
        }
    }

    public static String zzo() {
        String valueOf = String.valueOf("UTF-8");
        return valueOf.length() != 0 ? "application/x-www-form-urlencoded; charset=".concat(valueOf) : new String("application/x-www-form-urlencoded; charset=");
    }

    public String toString() {
        String valueOf = String.valueOf(Integer.toHexString(this.zzaf));
        String concat = valueOf.length() != 0 ? "0x".concat(valueOf) : new String("0x");
        String valueOf2 = String.valueOf(this.zzae);
        String valueOf3 = String.valueOf(zza.NORMAL);
        String valueOf4 = String.valueOf(this.zzah);
        return new StringBuilder(String.valueOf("[ ] ").length() + 3 + String.valueOf(valueOf2).length() + String.valueOf(concat).length() + String.valueOf(valueOf3).length() + String.valueOf(valueOf4).length()).append("[ ] ").append(valueOf2).append(XMLResultsHandler.SEP_SPACE).append(concat).append(XMLResultsHandler.SEP_SPACE).append(valueOf3).append(XMLResultsHandler.SEP_SPACE).append(valueOf4).toString();
    }

    @Override // java.lang.Comparable
    public /* synthetic */ int compareTo(Object obj) {
        zzk zzkVar = (zzk) obj;
        zza zzaVar = zza.NORMAL;
        zza zzaVar2 = zza.NORMAL;
        return zzaVar == zzaVar2 ? this.zzah.intValue() - zzkVar.zzah.intValue() : zzaVar2.ordinal() - zzaVar.ordinal();
    }
}
