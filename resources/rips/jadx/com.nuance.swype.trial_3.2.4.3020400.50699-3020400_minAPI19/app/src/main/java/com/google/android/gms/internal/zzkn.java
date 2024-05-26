package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.internal.zzm;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@zzin
/* loaded from: classes.dex */
public final class zzkn {
    static zzl zzcmc;
    private static final Object zzcmd = new Object();
    public static final zza<Void> zzcme = new zza<Void>() { // from class: com.google.android.gms.internal.zzkn.1
        @Override // com.google.android.gms.internal.zzkn.zza
        public final /* synthetic */ Void zzh(InputStream inputStream) {
            return null;
        }

        @Override // com.google.android.gms.internal.zzkn.zza
        public final /* synthetic */ Void zzqu() {
            return null;
        }
    };

    /* loaded from: classes.dex */
    public interface zza<T> {
        T zzh(InputStream inputStream);

        T zzqu();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class zzc<T> extends zzkv<T> implements zzm.zzb<T> {
        private zzc() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public /* synthetic */ zzc(zzkn zzknVar, byte b) {
            this();
        }

        @Override // com.google.android.gms.internal.zzm.zzb
        public final void zzb(T t) {
            super.zzh(t);
        }
    }

    public zzkn(Context context) {
        zzap(context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class zzb<T> extends zzk<InputStream> {
        private final zzm.zzb<T> zzcg;
        private final zza<T> zzcmj;

        public zzb(String str, final zza<T> zzaVar, final zzm.zzb<T> zzbVar) {
            super(0, str, new zzm.zza() { // from class: com.google.android.gms.internal.zzkn.zzb.1
                /* JADX WARN: Multi-variable type inference failed */
                @Override // com.google.android.gms.internal.zzm.zza
                public final void zze(zzr zzrVar) {
                    zzm.zzb.this.zzb(zzaVar.zzqu());
                }
            });
            this.zzcmj = zzaVar;
            this.zzcg = zzbVar;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.internal.zzk
        public final zzm<InputStream> zza(zzi zziVar) {
            return zzm.zza(new ByteArrayInputStream(zziVar.data), zzx.zzb(zziVar));
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.internal.zzk
        public final /* synthetic */ void zza(InputStream inputStream) {
            this.zzcg.zzb(this.zzcmj.zzh(inputStream));
        }
    }

    private static zzl zzap(Context context) {
        zzl zzlVar;
        synchronized (zzcmd) {
            if (zzcmc == null) {
                zzcmc = zzac.zza$575a9856(context.getApplicationContext());
            }
            zzlVar = zzcmc;
        }
        return zzlVar;
    }
}
