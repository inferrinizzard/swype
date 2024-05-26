package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import com.google.android.gms.internal.zzkn;
import com.google.android.gms.internal.zzm;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@TargetApi(11)
@zzin
/* loaded from: classes.dex */
public final class zzlp extends zzli {
    public zzlp(zzlh zzlhVar, boolean z) {
        super(zzlhVar, z);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.webkit.WebViewClient
    public final WebResourceResponse shouldInterceptRequest(WebView webView, String str) {
        try {
            if (!"mraid.js".equalsIgnoreCase(new File(str).getName())) {
                return super.shouldInterceptRequest(webView, str);
            }
            if (!(webView instanceof zzlh)) {
                zzkd.zzcx("Tried to intercept request from a WebView that wasn't an AdWebView.");
                return super.shouldInterceptRequest(webView, str);
            }
            zzlh zzlhVar = (zzlh) webView;
            final zzli zzuj = zzlhVar.zzuj();
            synchronized (zzuj.zzail) {
                zzuj.zzcoo = false;
                zzuj.zzark = true;
                com.google.android.gms.ads.internal.zzu.zzfq();
                zzkh.runOnUiThread(new Runnable() { // from class: com.google.android.gms.internal.zzli.2
                    public AnonymousClass2() {
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        zzli.this.zzbgf.zzuu();
                        com.google.android.gms.ads.internal.overlay.zzd zzuh = zzli.this.zzbgf.zzuh();
                        if (zzuh != null) {
                            zzuh.zznx();
                        }
                        if (zzli.this.zzcon != null) {
                            zzli.this.zzcon.zzen();
                            zzli.zza$73fd7829(zzli.this);
                        }
                    }
                });
            }
            String str2 = zzlhVar.zzdn().zzaus ? (String) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzazd) : zzlhVar.zzun() ? (String) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzazc) : (String) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzazb);
            zzkd.v(new StringBuilder(String.valueOf(str2).length() + 24).append("shouldInterceptRequest(").append(str2).append(")").toString());
            Context context = zzlhVar.getContext();
            String str3 = this.zzbgf.zzum().zzcs;
            HashMap hashMap = new HashMap();
            hashMap.put("User-Agent", com.google.android.gms.ads.internal.zzu.zzfq().zzg(context, str3));
            hashMap.put("Cache-Control", "max-stale=3600");
            zzkn zzknVar = new zzkn(context);
            zzkn.zzc zzcVar = new zzkn.zzc(zzknVar, (byte) 0);
            zzkn.zzcmc.zze(new zzab(str2, zzcVar, new zzm.zza() { // from class: com.google.android.gms.internal.zzkn.2
                final /* synthetic */ String zzbjh;
                final /* synthetic */ zzc zzcmf;

                public AnonymousClass2(String str22, zzc zzcVar2) {
                    r2 = str22;
                    r3 = zzcVar2;
                }

                @Override // com.google.android.gms.internal.zzm.zza
                public final void zze(zzr zzrVar) {
                    String str4 = r2;
                    String valueOf = String.valueOf(zzrVar.toString());
                    zzkd.zzcx(new StringBuilder(String.valueOf(str4).length() + 21 + String.valueOf(valueOf).length()).append("Failed to load URL: ").append(str4).append("\n").append(valueOf).toString());
                    r3.zzb(null);
                }
            }) { // from class: com.google.android.gms.internal.zzkn.3
                final /* synthetic */ byte[] zzcmh = null;
                final /* synthetic */ Map zzcmi;

                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                public AnonymousClass3(String str22, zzc zzcVar2, zzm.zza zzaVar, Map hashMap2) {
                    super(0, str22, zzcVar2, zzaVar);
                    r6 = hashMap2;
                }

                @Override // com.google.android.gms.internal.zzk
                public final Map<String, String> getHeaders() throws com.google.android.gms.internal.zza {
                    return r6 == null ? super.getHeaders() : r6;
                }

                @Override // com.google.android.gms.internal.zzk
                public final byte[] zzp() throws com.google.android.gms.internal.zza {
                    return this.zzcmh == null ? super.zzp() : this.zzcmh;
                }
            });
            String str4 = (String) zzcVar2.get(60L, TimeUnit.SECONDS);
            if (str4 == null) {
                return null;
            }
            return new WebResourceResponse("application/javascript", "UTF-8", new ByteArrayInputStream(str4.getBytes("UTF-8")));
        } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
            String valueOf = String.valueOf(e.getMessage());
            zzkd.zzcx(valueOf.length() != 0 ? "Could not fetch MRAID JS. ".concat(valueOf) : new String("Could not fetch MRAID JS. "));
            return super.shouldInterceptRequest(webView, str);
        }
    }
}
