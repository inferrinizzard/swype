package com.google.android.gms.internal;

import android.content.Context;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.android.gms.ads.internal.overlay.AdLauncherIntentInfoParcel;
import com.google.android.gms.ads.internal.overlay.AdOverlayInfoParcel;
import com.google.api.client.http.HttpMethods;
import com.nuance.connect.sqlite.ChinesePredictionDataSource;
import com.nuance.swype.input.R;
import com.nuance.swype.input.ThemeManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@zzin
/* loaded from: classes.dex */
public class zzli extends WebViewClient {
    private static final String[] zzcoj = {ChinesePredictionDataSource.UNKNOWN, "HOST_LOOKUP", "UNSUPPORTED_AUTH_SCHEME", "AUTHENTICATION", "PROXY_AUTHENTICATION", HttpMethods.CONNECT, "IO", "TIMEOUT", "REDIRECT_LOOP", "UNSUPPORTED_SCHEME", "FAILED_SSL_HANDSHAKE", "BAD_URL", "FILE", "FILE_NOT_FOUND", "TOO_MANY_REQUESTS"};
    private static final String[] zzcok = {"NOT_YET_VALID", "EXPIRED", "ID_MISMATCH", "UNTRUSTED", "DATE_INVALID", "INVALID"};
    final Object zzail;
    boolean zzark;
    private com.google.android.gms.ads.internal.client.zza zzatk;
    protected zzlh zzbgf;
    private zzel zzbhm;
    private zzet zzbir;
    public com.google.android.gms.ads.internal.zze zzbit;
    public zzha zzbiu;
    private zzer zzbiw;
    private zzhg zzbqn;
    public zza zzbya;
    private final HashMap<String, List<zzep>> zzcol;
    private com.google.android.gms.ads.internal.overlay.zzg zzcom;
    public zzb zzcon;
    boolean zzcoo;
    private boolean zzcop;
    private com.google.android.gms.ads.internal.overlay.zzp zzcoq;
    public final zzhe zzcor;
    public zzd zzcos;
    protected zzjo zzcot;
    private boolean zzcou;
    private boolean zzcov;
    private boolean zzcow;
    private int zzcox;

    /* loaded from: classes.dex */
    public interface zza {
        void zza(zzlh zzlhVar, boolean z);
    }

    /* loaded from: classes.dex */
    public interface zzb {
        void zzen();
    }

    /* loaded from: classes.dex */
    private static class zzc implements com.google.android.gms.ads.internal.overlay.zzg {
        private com.google.android.gms.ads.internal.overlay.zzg zzcom;
        private zzlh zzcoz;

        public zzc(zzlh zzlhVar, com.google.android.gms.ads.internal.overlay.zzg zzgVar) {
            this.zzcoz = zzlhVar;
            this.zzcom = zzgVar;
        }

        @Override // com.google.android.gms.ads.internal.overlay.zzg
        public final void onPause() {
        }

        @Override // com.google.android.gms.ads.internal.overlay.zzg
        public final void onResume() {
        }

        @Override // com.google.android.gms.ads.internal.overlay.zzg
        public final void zzdx() {
            this.zzcom.zzdx();
            this.zzcoz.zzuc();
        }

        @Override // com.google.android.gms.ads.internal.overlay.zzg
        public final void zzdy() {
            this.zzcom.zzdy();
            this.zzcoz.zzoa();
        }
    }

    /* loaded from: classes.dex */
    public interface zzd {
        void zzem();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class zze implements zzep {
        private zze() {
        }

        /* synthetic */ zze(zzli zzliVar, byte b) {
            this();
        }

        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            if (map.keySet().contains("start")) {
                zzli.zza(zzli.this);
            } else if (map.keySet().contains("stop")) {
                zzli.zzb(zzli.this);
            } else if (map.keySet().contains("cancel")) {
                zzli.zzc(zzli.this);
            }
        }
    }

    public zzli(zzlh zzlhVar, boolean z) {
        this(zzlhVar, z, new zzhe(zzlhVar, zzlhVar.zzuf(), new zzcu(zzlhVar.getContext())));
    }

    private zzli(zzlh zzlhVar, boolean z, zzhe zzheVar) {
        this.zzcol = new HashMap<>();
        this.zzail = new Object();
        this.zzcoo = false;
        this.zzbgf = zzlhVar;
        this.zzark = z;
        this.zzcor = zzheVar;
        this.zzbiu = null;
    }

    private void zza(AdOverlayInfoParcel adOverlayInfoParcel) {
        com.google.android.gms.ads.internal.zzu.zzfo().zza(this.zzbgf.getContext(), adOverlayInfoParcel, this.zzbiu != null ? this.zzbiu.zzmw() : false ? false : true);
        if (this.zzcot == null || adOverlayInfoParcel.url != null || adOverlayInfoParcel.zzbtj == null) {
            return;
        }
        String str = adOverlayInfoParcel.zzbtj.url;
    }

    static /* synthetic */ zzb zza$73fd7829(zzli zzliVar) {
        zzliVar.zzcon = null;
        return null;
    }

    private void zzi(Uri uri) {
        String path = uri.getPath();
        List<zzep> list = this.zzcol.get(path);
        if (list == null) {
            String valueOf = String.valueOf(uri);
            zzkd.v(new StringBuilder(String.valueOf(valueOf).length() + 32).append("No GMSG handler found for GMSG: ").append(valueOf).toString());
            return;
        }
        com.google.android.gms.ads.internal.zzu.zzfq();
        Map<String, String> zzf = zzkh.zzf(uri);
        if (zzkd.zzaz(2)) {
            String valueOf2 = String.valueOf(path);
            zzkd.v(valueOf2.length() != 0 ? "Received GMSG: ".concat(valueOf2) : new String("Received GMSG: "));
            for (String str : zzf.keySet()) {
                String str2 = zzf.get(str);
                zzkd.v(new StringBuilder(String.valueOf(str).length() + 4 + String.valueOf(str2).length()).append(ThemeManager.NO_PRICE).append(str).append(": ").append(str2).toString());
            }
        }
        Iterator<zzep> it = list.iterator();
        while (it.hasNext()) {
            it.next().zza(this.zzbgf, zzf);
        }
    }

    private void zzve() {
        if (this.zzbya != null && ((this.zzcov && this.zzcox <= 0) || this.zzcow)) {
            this.zzbya.zza(this.zzbgf, !this.zzcow);
            this.zzbya = null;
        }
        this.zzbgf.zzuv();
    }

    @Override // android.webkit.WebViewClient
    public final void onLoadResource(WebView webView, String str) {
        String valueOf = String.valueOf(str);
        zzkd.v(valueOf.length() != 0 ? "Loading resource: ".concat(valueOf) : new String("Loading resource: "));
        Uri parse = Uri.parse(str);
        if ("gmsg".equalsIgnoreCase(parse.getScheme()) && "mobileads.google.com".equalsIgnoreCase(parse.getHost())) {
            zzi(parse);
        }
    }

    @Override // android.webkit.WebViewClient
    public final void onPageFinished(WebView webView, String str) {
        synchronized (this.zzail) {
            if (this.zzcou) {
                zzkd.v("Blank page loaded, 1...");
                this.zzbgf.zzuo();
            } else {
                this.zzcov = true;
                zzve();
            }
        }
    }

    @Override // android.webkit.WebViewClient
    public final void onReceivedError(WebView webView, int i, String str, String str2) {
        zza(this.zzbgf.getContext(), "http_err", (i >= 0 || (-i) + (-1) >= zzcoj.length) ? String.valueOf(i) : zzcoj[(-i) - 1], str2);
        super.onReceivedError(webView, i, str, str2);
    }

    @Override // android.webkit.WebViewClient
    public final void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
        if (sslError != null) {
            int primaryError = sslError.getPrimaryError();
            zza(this.zzbgf.getContext(), "ssl_err", (primaryError < 0 || primaryError >= zzcok.length) ? String.valueOf(primaryError) : zzcok[primaryError], com.google.android.gms.ads.internal.zzu.zzfs().zza(sslError));
        }
        super.onReceivedSslError(webView, sslErrorHandler, sslError);
    }

    public final void reset() {
        if (this.zzcot != null) {
            this.zzcot = null;
        }
        synchronized (this.zzail) {
            this.zzcol.clear();
            this.zzatk = null;
            this.zzcom = null;
            this.zzbya = null;
            this.zzbhm = null;
            this.zzcoo = false;
            this.zzark = false;
            this.zzcop = false;
            this.zzbiw = null;
            this.zzcoq = null;
            this.zzcon = null;
            if (this.zzbiu != null) {
                this.zzbiu.zzs(true);
                this.zzbiu = null;
            }
        }
    }

    @Override // android.webkit.WebViewClient
    public boolean shouldOverrideKeyEvent(WebView webView, KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case 79:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 126:
            case 127:
            case 128:
            case 129:
            case 130:
            case R.styleable.ThemeTemplate_keyboardBackgroundHwrBottomRow /* 222 */:
                return true;
            default:
                return false;
        }
    }

    public final void zza(AdLauncherIntentInfoParcel adLauncherIntentInfoParcel) {
        boolean zzun = this.zzbgf.zzun();
        zza(new AdOverlayInfoParcel(adLauncherIntentInfoParcel, (!zzun || this.zzbgf.zzdn().zzaus) ? this.zzatk : null, zzun ? null : this.zzcom, this.zzcoq, this.zzbgf.zzum()));
    }

    public final void zza(String str, zzep zzepVar) {
        synchronized (this.zzail) {
            List<zzep> list = this.zzcol.get(str);
            if (list == null) {
                list = new CopyOnWriteArrayList<>();
                this.zzcol.put(str, list);
            }
            list.add(zzepVar);
        }
    }

    public final void zza(boolean z, int i) {
        zza(new AdOverlayInfoParcel((!this.zzbgf.zzun() || this.zzbgf.zzdn().zzaus) ? this.zzatk : null, this.zzcom, this.zzcoq, this.zzbgf, z, i, this.zzbgf.zzum()));
    }

    public final void zza(boolean z, int i, String str) {
        boolean zzun = this.zzbgf.zzun();
        zza(new AdOverlayInfoParcel((!zzun || this.zzbgf.zzdn().zzaus) ? this.zzatk : null, zzun ? null : new zzc(this.zzbgf, this.zzcom), this.zzbhm, this.zzcoq, this.zzbgf, z, i, str, this.zzbgf.zzum(), this.zzbiw));
    }

    public final void zza(boolean z, int i, String str, String str2) {
        boolean zzun = this.zzbgf.zzun();
        zza(new AdOverlayInfoParcel((!zzun || this.zzbgf.zzdn().zzaus) ? this.zzatk : null, zzun ? null : new zzc(this.zzbgf, this.zzcom), this.zzbhm, this.zzcoq, this.zzbgf, z, i, str, str2, this.zzbgf.zzum(), this.zzbiw));
    }

    public final void zzb(String str, zzep zzepVar) {
        synchronized (this.zzail) {
            List<zzep> list = this.zzcol.get(str);
            if (list == null) {
                return;
            }
            list.remove(zzepVar);
        }
    }

    public final boolean zzho() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzark;
        }
        return z;
    }

    public final void zzl(zzlh zzlhVar) {
        this.zzbgf = zzlhVar;
    }

    public final boolean zzuy() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzcop;
        }
        return z;
    }

    public final void zzuz() {
        synchronized (this.zzail) {
            zzkd.v("Loading blank page in WebView, 2...");
            this.zzcou = true;
            this.zzbgf.zzcy("about:blank");
        }
    }

    public final void zzva() {
        if (this.zzcot != null) {
            zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.internal.zzli.1
                @Override // java.lang.Runnable
                public final void run() {
                    if (zzli.this.zzcot != null) {
                        zzjo zzjoVar = zzli.this.zzcot;
                        zzlh zzlhVar = zzli.this.zzbgf;
                    }
                }
            });
        }
    }

    public final void zza(com.google.android.gms.ads.internal.client.zza zzaVar, com.google.android.gms.ads.internal.overlay.zzg zzgVar, zzel zzelVar, com.google.android.gms.ads.internal.overlay.zzp zzpVar, boolean z, zzer zzerVar, zzet zzetVar, com.google.android.gms.ads.internal.zze zzeVar, zzhg zzhgVar, zzjo zzjoVar) {
        if (zzeVar == null) {
            zzeVar = new com.google.android.gms.ads.internal.zze(this.zzbgf.getContext());
        }
        this.zzbiu = new zzha(this.zzbgf, zzhgVar);
        this.zzcot = zzjoVar;
        zza("/appEvent", new zzek(zzelVar));
        zza("/backButton", zzeo.zzbhx);
        zza("/refresh", zzeo.zzbhy);
        zza("/canOpenURLs", zzeo.zzbho);
        zza("/canOpenIntents", zzeo.zzbhp);
        zza("/click", zzeo.zzbhq);
        zza("/close", zzeo.zzbhr);
        zza("/customClose", zzeo.zzbht);
        zza("/instrument", zzeo.zzbic);
        zza("/delayPageLoaded", new zze(this, (byte) 0));
        zza("/httpTrack", zzeo.zzbhu);
        zza("/log", zzeo.zzbhv);
        zza("/mraid", new zzev(zzeVar, this.zzbiu));
        zza("/mraidLoaded", this.zzcor);
        zza("/open", new zzew(zzerVar, zzeVar, this.zzbiu));
        zza("/precache", zzeo.zzbib);
        zza("/touch", zzeo.zzbhw);
        zza("/video", zzeo.zzbhz);
        zza("/videoMeta", zzeo.zzbia);
        zza("/appStreaming", zzeo.zzbhs);
        if (zzetVar != null) {
            zza("/setInterstitialProperties", new zzes(zzetVar));
        }
        this.zzatk = zzaVar;
        this.zzcom = zzgVar;
        this.zzbhm = zzelVar;
        this.zzbiw = zzerVar;
        this.zzcoq = zzpVar;
        this.zzbit = zzeVar;
        this.zzbqn = zzhgVar;
        this.zzbir = zzetVar;
        this.zzcoo = z;
    }

    private void zza(Context context, String str, String str2, String str3) {
        String str4;
        if (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbav)).booleanValue()) {
            Bundle bundle = new Bundle();
            bundle.putString("err", str);
            bundle.putString("code", str2);
            if (!TextUtils.isEmpty(str3)) {
                Uri parse = Uri.parse(str3);
                if (parse.getHost() != null) {
                    str4 = parse.getHost();
                    bundle.putString("host", str4);
                    com.google.android.gms.ads.internal.zzu.zzfq().zza(context, this.zzbgf.zzum().zzcs, "gmob-apps", bundle, true);
                }
            }
            str4 = "";
            bundle.putString("host", str4);
            com.google.android.gms.ads.internal.zzu.zzfq().zza(context, this.zzbgf.zzum().zzcs, "gmob-apps", bundle, true);
        }
    }

    @Override // android.webkit.WebViewClient
    public final boolean shouldOverrideUrlLoading(WebView webView, String str) {
        Uri uri;
        String valueOf = String.valueOf(str);
        zzkd.v(valueOf.length() != 0 ? "AdWebView shouldOverrideUrlLoading: ".concat(valueOf) : new String("AdWebView shouldOverrideUrlLoading: "));
        Uri parse = Uri.parse(str);
        if ("gmsg".equalsIgnoreCase(parse.getScheme()) && "mobileads.google.com".equalsIgnoreCase(parse.getHost())) {
            zzi(parse);
        } else {
            if (this.zzcoo && webView == this.zzbgf.getWebView()) {
                String scheme = parse.getScheme();
                if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
                    if (this.zzatk != null) {
                        if (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzazu)).booleanValue()) {
                            this.zzatk.onAdClicked();
                            this.zzatk = null;
                        }
                    }
                    return super.shouldOverrideUrlLoading(webView, str);
                }
            }
            if (this.zzbgf.getWebView().willNotDraw()) {
                String valueOf2 = String.valueOf(str);
                zzkd.zzcx(valueOf2.length() != 0 ? "AdWebView unable to handle URL: ".concat(valueOf2) : new String("AdWebView unable to handle URL: "));
            } else {
                try {
                    zzas zzul = this.zzbgf.zzul();
                    if (zzul != null && zzul.zzc(parse)) {
                        parse = zzul.zzb(parse, this.zzbgf.getContext());
                    }
                    uri = parse;
                } catch (zzat e) {
                    String valueOf3 = String.valueOf(str);
                    zzkd.zzcx(valueOf3.length() != 0 ? "Unable to append parameter to URL: ".concat(valueOf3) : new String("Unable to append parameter to URL: "));
                    uri = parse;
                }
                if (this.zzbit == null || this.zzbit.zzel()) {
                    zza(new AdLauncherIntentInfoParcel("android.intent.action.VIEW", uri.toString(), null, null, null, null, null));
                } else {
                    this.zzbit.zzt(str);
                }
            }
        }
        return true;
    }

    static /* synthetic */ void zza(zzli zzliVar) {
        synchronized (zzliVar.zzail) {
            zzliVar.zzcop = true;
        }
        zzliVar.zzcox++;
        zzliVar.zzve();
    }

    static /* synthetic */ void zzb(zzli zzliVar) {
        zzliVar.zzcox--;
        zzliVar.zzve();
    }

    static /* synthetic */ void zzc(zzli zzliVar) {
        zzliVar.zzcow = true;
        zzliVar.zzve();
    }
}
