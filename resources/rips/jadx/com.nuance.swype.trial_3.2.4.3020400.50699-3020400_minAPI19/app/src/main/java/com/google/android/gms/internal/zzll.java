package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.facebook.internal.ServerProtocol;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
@zzin
/* loaded from: classes.dex */
public final class zzll extends WebView implements ViewTreeObserver.OnGlobalLayoutListener, DownloadListener, zzlh {
    private final Object zzail;
    private final com.google.android.gms.ads.internal.zzd zzajv;
    private final VersionInfoParcel zzalo;
    private AdSizeParcel zzani;
    private zzku zzaqg;
    private final WindowManager zzaqm;
    private final zzas zzbgd;
    private int zzbrf;
    private int zzbrg;
    private int zzbri;
    private int zzbrj;
    private String zzbvq;
    private Boolean zzcjw;
    private final zza zzcpc;
    private final com.google.android.gms.ads.internal.zzs zzcpd;
    private zzli zzcpe;
    private com.google.android.gms.ads.internal.overlay.zzd zzcpf;
    private boolean zzcpg;
    private boolean zzcph;
    private boolean zzcpi;
    private boolean zzcpj;
    private int zzcpk;
    private boolean zzcpl;
    boolean zzcpm;
    private zzlm zzcpn;
    private boolean zzcpo;
    private zzdi zzcpp;
    private zzdi zzcpq;
    private zzdi zzcpr;
    private zzdj zzcps;
    private WeakReference<View.OnClickListener> zzcpt;
    private com.google.android.gms.ads.internal.overlay.zzd zzcpu;
    private Map<String, zzfd> zzcpv;

    @zzin
    /* loaded from: classes.dex */
    public static class zza extends MutableContextWrapper {
        private Context zzaql;
        Activity zzcmv;
        Context zzcpx;

        public zza(Context context) {
            super(context);
            setBaseContext(context);
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public final Object getSystemService(String str) {
            return this.zzcpx.getSystemService(str);
        }

        @Override // android.content.MutableContextWrapper
        public final void setBaseContext(Context context) {
            this.zzaql = context.getApplicationContext();
            this.zzcmv = context instanceof Activity ? (Activity) context : null;
            this.zzcpx = context;
            super.setBaseContext(this.zzaql);
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public final void startActivity(Intent intent) {
            if (this.zzcmv != null) {
                this.zzcmv.startActivity(intent);
            } else {
                intent.setFlags(268435456);
                this.zzaql.startActivity(intent);
            }
        }
    }

    private void zzal(boolean z) {
        HashMap hashMap = new HashMap();
        hashMap.put("isVisible", z ? "1" : "0");
        zza("onAdVisibilityChanged", hashMap);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static zzll zzb$71c2c049(Context context, AdSizeParcel adSizeParcel, boolean z, zzas zzasVar, VersionInfoParcel versionInfoParcel, zzdk zzdkVar, com.google.android.gms.ads.internal.zzs zzsVar, com.google.android.gms.ads.internal.zzd zzdVar) {
        return new zzll(new zza(context), adSizeParcel, z, zzasVar, versionInfoParcel, zzdkVar, zzsVar, zzdVar);
    }

    private void zzdb(String str) {
        synchronized (this.zzail) {
            if (isDestroyed()) {
                zzkd.zzcx("The webview is destroyed. Ignoring action.");
            } else {
                loadUrl(str);
            }
        }
    }

    private Boolean zzsq() {
        Boolean bool;
        synchronized (this.zzail) {
            bool = this.zzcjw;
        }
        return bool;
    }

    private void zzvj() {
        synchronized (this.zzail) {
            if (this.zzcpi || this.zzani.zzaus) {
                if (Build.VERSION.SDK_INT < 14) {
                    zzkd.zzcv("Disabling hardware acceleration on an overlay.");
                    zzvk();
                } else {
                    zzkd.zzcv("Enabling hardware acceleration on an overlay.");
                    zzvl();
                }
            } else if (Build.VERSION.SDK_INT < 18) {
                zzkd.zzcv("Disabling hardware acceleration on an AdView.");
                zzvk();
            } else {
                zzkd.zzcv("Enabling hardware acceleration on an AdView.");
                zzvl();
            }
        }
    }

    private void zzvk() {
        synchronized (this.zzail) {
            if (!this.zzcpj) {
                com.google.android.gms.ads.internal.zzu.zzfs().zzp(this);
            }
            this.zzcpj = true;
        }
    }

    private void zzvl() {
        synchronized (this.zzail) {
            if (this.zzcpj) {
                com.google.android.gms.ads.internal.zzu.zzfs().zzo(this);
            }
            this.zzcpj = false;
        }
    }

    private void zzvm() {
        synchronized (this.zzail) {
            this.zzcpv = null;
        }
    }

    @Override // android.webkit.WebView, com.google.android.gms.internal.zzlh
    public final void destroy() {
        synchronized (this.zzail) {
            zzvn();
            this.zzaqg.zztt();
            if (this.zzcpf != null) {
                this.zzcpf.close();
                this.zzcpf.onDestroy();
                this.zzcpf = null;
            }
            this.zzcpe.reset();
            if (this.zzcph) {
                return;
            }
            com.google.android.gms.ads.internal.zzu.zzgj();
            zzfc.zzd(this);
            zzvm();
            this.zzcph = true;
            zzkd.v("Initiating WebView self destruct sequence in 3...");
            this.zzcpe.zzuz();
        }
    }

    @Override // android.webkit.WebView
    @TargetApi(19)
    public final void evaluateJavascript(String str, ValueCallback<String> valueCallback) {
        synchronized (this.zzail) {
            if (!isDestroyed()) {
                super.evaluateJavascript(str, valueCallback);
                return;
            }
            zzkd.zzcx("The webview is destroyed. Ignoring action.");
            if (valueCallback != null) {
                valueCallback.onReceiveValue(null);
            }
        }
    }

    protected final void finalize() throws Throwable {
        synchronized (this.zzail) {
            if (!this.zzcph) {
                this.zzcpe.reset();
                com.google.android.gms.ads.internal.zzu.zzgj();
                zzfc.zzd(this);
                zzvm();
            }
        }
        super.finalize();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final String getRequestId() {
        String str;
        synchronized (this.zzail) {
            str = this.zzbvq;
        }
        return str;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final int getRequestedOrientation() {
        int i;
        synchronized (this.zzail) {
            i = this.zzcpk;
        }
        return i;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final View getView() {
        return this;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final WebView getWebView() {
        return this;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final boolean isDestroyed() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzcph;
        }
        return z;
    }

    @Override // android.webkit.WebView, com.google.android.gms.internal.zzlh
    public final void loadData(String str, String str2, String str3) {
        synchronized (this.zzail) {
            if (isDestroyed()) {
                zzkd.zzcx("The webview is destroyed. Ignoring action.");
            } else {
                super.loadData(str, str2, str3);
            }
        }
    }

    @Override // android.webkit.WebView, com.google.android.gms.internal.zzlh
    public final void loadDataWithBaseURL(String str, String str2, String str3, String str4, String str5) {
        synchronized (this.zzail) {
            if (isDestroyed()) {
                zzkd.zzcx("The webview is destroyed. Ignoring action.");
            } else {
                super.loadDataWithBaseURL(str, str2, str3, str4, str5);
            }
        }
    }

    @Override // android.webkit.WebView, com.google.android.gms.internal.zzlh
    public final void loadUrl(String str) {
        synchronized (this.zzail) {
            if (isDestroyed()) {
                zzkd.zzcx("The webview is destroyed. Ignoring action.");
            } else {
                try {
                    super.loadUrl(str);
                } catch (Throwable th) {
                    String valueOf = String.valueOf(th);
                    zzkd.zzcx(new StringBuilder(String.valueOf(valueOf).length() + 24).append("Could not call loadUrl. ").append(valueOf).toString());
                }
            }
        }
    }

    @Override // android.webkit.WebView, android.view.ViewGroup, android.view.View
    protected final void onAttachedToWindow() {
        synchronized (this.zzail) {
            super.onAttachedToWindow();
            if (!isDestroyed()) {
                this.zzaqg.onAttachedToWindow();
            }
            zzal(this.zzcpo);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected final void onDetachedFromWindow() {
        synchronized (this.zzail) {
            if (!isDestroyed()) {
                this.zzaqg.onDetachedFromWindow();
            }
            super.onDetachedFromWindow();
        }
        zzal(false);
    }

    @Override // android.webkit.DownloadListener
    public final void onDownloadStart(String str, String str2, String str3, String str4, long j) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(Uri.parse(str), str4);
            com.google.android.gms.ads.internal.zzu.zzfq();
            zzkh.zzb(getContext(), intent);
        } catch (ActivityNotFoundException e) {
            zzkd.zzcv(new StringBuilder(String.valueOf(str).length() + 51 + String.valueOf(str4).length()).append("Couldn't find an Activity to view url/mimetype: ").append(str).append(" / ").append(str4).toString());
        }
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public final void onGlobalLayout() {
        boolean zzvg = zzvg();
        com.google.android.gms.ads.internal.overlay.zzd zzuh = zzuh();
        if (zzuh == null || !zzvg) {
            return;
        }
        zzuh.zznz();
    }

    @Override // android.webkit.WebView, android.widget.AbsoluteLayout, android.view.View
    protected final void onMeasure(int i, int i2) {
        synchronized (this.zzail) {
            if (isDestroyed()) {
                setMeasuredDimension(0, 0);
                return;
            }
            if (isInEditMode() || this.zzcpi || this.zzani.zzauu || this.zzani.zzauv) {
                super.onMeasure(i, i2);
                return;
            }
            if (this.zzani.zzaus) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                this.zzaqm.getDefaultDisplay().getMetrics(displayMetrics);
                setMeasuredDimension(displayMetrics.widthPixels, displayMetrics.heightPixels);
                return;
            }
            int mode = View.MeasureSpec.getMode(i);
            int size = View.MeasureSpec.getSize(i);
            int mode2 = View.MeasureSpec.getMode(i2);
            int size2 = View.MeasureSpec.getSize(i2);
            int i3 = (mode == Integer.MIN_VALUE || mode == 1073741824) ? size : Integer.MAX_VALUE;
            int i4 = (mode2 == Integer.MIN_VALUE || mode2 == 1073741824) ? size2 : Integer.MAX_VALUE;
            if (this.zzani.widthPixels > i3 || this.zzani.heightPixels > i4) {
                float f = this.zzcpc.getResources().getDisplayMetrics().density;
                int i5 = (int) (size / f);
                zzkd.zzcx(new StringBuilder(103).append("Not enough space to show ad. Needs ").append((int) (this.zzani.widthPixels / f)).append("x").append((int) (this.zzani.heightPixels / f)).append(" dp, but only has ").append(i5).append("x").append((int) (size2 / f)).append(" dp.").toString());
                if (getVisibility() != 8) {
                    setVisibility(4);
                }
                setMeasuredDimension(0, 0);
            } else {
                if (getVisibility() != 8) {
                    setVisibility(0);
                }
                setMeasuredDimension(this.zzani.widthPixels, this.zzani.heightPixels);
            }
        }
    }

    @Override // android.webkit.WebView, android.view.View
    public final boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.zzbgd != null) {
            this.zzbgd.zza(motionEvent);
        }
        if (isDestroyed()) {
            return false;
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override // android.view.View, com.google.android.gms.internal.zzlh
    public final void setOnClickListener(View.OnClickListener onClickListener) {
        this.zzcpt = new WeakReference<>(onClickListener);
        super.setOnClickListener(onClickListener);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void setRequestedOrientation(int i) {
        synchronized (this.zzail) {
            this.zzcpk = i;
            if (this.zzcpf != null) {
                this.zzcpf.setRequestedOrientation(this.zzcpk);
            }
        }
    }

    @Override // android.webkit.WebView, com.google.android.gms.internal.zzlh
    public final void setWebViewClient(WebViewClient webViewClient) {
        super.setWebViewClient(webViewClient);
        if (webViewClient instanceof zzli) {
            this.zzcpe = (zzli) webViewClient;
        }
    }

    @Override // android.webkit.WebView, com.google.android.gms.internal.zzlh
    public final void stopLoading() {
        if (isDestroyed()) {
            return;
        }
        try {
            super.stopLoading();
        } catch (Exception e) {
            zzkd.zzb("Could not stop loading webview.", e);
        }
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zza(Context context, AdSizeParcel adSizeParcel, zzdk zzdkVar) {
        synchronized (this.zzail) {
            this.zzaqg.zztt();
            setContext(context);
            this.zzcpf = null;
            this.zzani = adSizeParcel;
            this.zzcpi = false;
            this.zzcpg = false;
            this.zzbvq = "";
            this.zzcpk = -1;
            com.google.android.gms.ads.internal.zzu.zzfs();
            zzki.zzj(this);
            loadUrl("about:blank");
            this.zzcpe.reset();
            setOnTouchListener(null);
            setOnClickListener(null);
            this.zzcpl = true;
            this.zzcpm = false;
            this.zzcpn = null;
            zzd(zzdkVar);
            this.zzcpo = false;
            com.google.android.gms.ads.internal.zzu.zzgj();
            zzfc.zzd(this);
            zzvm();
        }
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zza(AdSizeParcel adSizeParcel) {
        synchronized (this.zzail) {
            this.zzani = adSizeParcel;
            requestLayout();
        }
    }

    @Override // com.google.android.gms.internal.zzce
    public final void zza(zzcd zzcdVar, boolean z) {
        synchronized (this.zzail) {
            this.zzcpo = z;
        }
        zzal(z);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zza(zzlm zzlmVar) {
        synchronized (this.zzail) {
            if (this.zzcpn != null) {
                zzkd.e("Attempt to create multiple AdWebViewVideoControllers.");
            } else {
                this.zzcpn = zzlmVar;
            }
        }
    }

    @Override // com.google.android.gms.internal.zzft
    public final void zza(String str, zzep zzepVar) {
        if (this.zzcpe != null) {
            this.zzcpe.zza(str, zzepVar);
        }
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zza(String str, Map<String, ?> map) {
        try {
            zzb(str, com.google.android.gms.ads.internal.zzu.zzfq().zzam(map));
        } catch (JSONException e) {
            zzkd.zzcx("Could not convert parameters to JSON.");
        }
    }

    @Override // com.google.android.gms.internal.zzlh, com.google.android.gms.internal.zzft
    public final void zza(String str, JSONObject jSONObject) {
        if (jSONObject == null) {
            jSONObject = new JSONObject();
        }
        zzj(str, jSONObject.toString());
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzaf(int i) {
        zzvi();
        HashMap hashMap = new HashMap(2);
        hashMap.put("closetype", String.valueOf(i));
        hashMap.put(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, this.zzalo.zzcs);
        zza("onhide", hashMap);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzah(boolean z) {
        synchronized (this.zzail) {
            this.zzcpi = z;
            zzvj();
        }
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzai(boolean z) {
        synchronized (this.zzail) {
            if (this.zzcpf != null) {
                this.zzcpf.zza(this.zzcpe.zzho(), z);
            } else {
                this.zzcpg = z;
            }
        }
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzaj(boolean z) {
        synchronized (this.zzail) {
            this.zzcpl = z;
        }
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzb(com.google.android.gms.ads.internal.overlay.zzd zzdVar) {
        synchronized (this.zzail) {
            this.zzcpf = zzdVar;
        }
    }

    @Override // com.google.android.gms.internal.zzft
    public final void zzb(String str, zzep zzepVar) {
        if (this.zzcpe != null) {
            this.zzcpe.zzb(str, zzepVar);
        }
    }

    @Override // com.google.android.gms.internal.zzft
    public final void zzb(String str, JSONObject jSONObject) {
        if (jSONObject == null) {
            jSONObject = new JSONObject();
        }
        String jSONObject2 = jSONObject.toString();
        StringBuilder sb = new StringBuilder();
        sb.append("AFMA_ReceiveMessage('");
        sb.append(str);
        sb.append("'");
        sb.append(",");
        sb.append(jSONObject2);
        sb.append(");");
        String valueOf = String.valueOf(sb.toString());
        zzkd.v(valueOf.length() != 0 ? "Dispatching AFMA event: ".concat(valueOf) : new String("Dispatching AFMA event: "));
        zzdc(sb.toString());
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzc(com.google.android.gms.ads.internal.overlay.zzd zzdVar) {
        synchronized (this.zzail) {
            this.zzcpu = zzdVar;
        }
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzcy(String str) {
        synchronized (this.zzail) {
            try {
                super.loadUrl(str);
            } catch (Throwable th) {
                String valueOf = String.valueOf(th);
                zzkd.zzcx(new StringBuilder(String.valueOf(valueOf).length() + 24).append("Could not call loadUrl. ").append(valueOf).toString());
            }
        }
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzcz(String str) {
        synchronized (this.zzail) {
            if (str == null) {
                str = "";
            }
            this.zzbvq = str;
        }
    }

    @Override // com.google.android.gms.internal.zzlh
    public final AdSizeParcel zzdn() {
        AdSizeParcel adSizeParcel;
        synchronized (this.zzail) {
            adSizeParcel = this.zzani;
        }
        return adSizeParcel;
    }

    @Override // com.google.android.gms.ads.internal.zzs
    public final void zzef() {
        synchronized (this.zzail) {
            this.zzcpm = true;
            if (this.zzcpd != null) {
                this.zzcpd.zzef();
            }
        }
    }

    @Override // com.google.android.gms.ads.internal.zzs
    public final void zzeg() {
        synchronized (this.zzail) {
            this.zzcpm = false;
            if (this.zzcpd != null) {
                this.zzcpd.zzeg();
            }
        }
    }

    @Override // com.google.android.gms.internal.zzlh, com.google.android.gms.internal.zzft
    public final void zzj(String str, String str2) {
        zzdc(new StringBuilder(String.valueOf(str).length() + 3 + String.valueOf(str2).length()).append(str).append("(").append(str2).append(");").toString());
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzuc() {
        zzvi();
        HashMap hashMap = new HashMap(1);
        hashMap.put(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, this.zzalo.zzcs);
        zza("onhide", hashMap);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final com.google.android.gms.ads.internal.zzd zzug() {
        return this.zzajv;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final com.google.android.gms.ads.internal.overlay.zzd zzuh() {
        com.google.android.gms.ads.internal.overlay.zzd zzdVar;
        synchronized (this.zzail) {
            zzdVar = this.zzcpf;
        }
        return zzdVar;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final com.google.android.gms.ads.internal.overlay.zzd zzui() {
        com.google.android.gms.ads.internal.overlay.zzd zzdVar;
        synchronized (this.zzail) {
            zzdVar = this.zzcpu;
        }
        return zzdVar;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final zzli zzuj() {
        return this.zzcpe;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final boolean zzuk() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzcpg;
        }
        return z;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final zzas zzul() {
        return this.zzbgd;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final VersionInfoParcel zzum() {
        return this.zzalo;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final boolean zzun() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzcpi;
        }
        return z;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzuo() {
        synchronized (this.zzail) {
            zzkd.v("Destroying WebView!");
            zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.internal.zzll.1
                @Override // java.lang.Runnable
                public final void run() {
                    zzll.super.destroy();
                }
            });
        }
    }

    @Override // com.google.android.gms.internal.zzlh
    public final boolean zzup() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzcpm;
        }
        return z;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final zzlg zzuq() {
        return null;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final zzdi zzur() {
        return this.zzcpr;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final zzdj zzus() {
        return this.zzcps;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final zzlm zzut() {
        zzlm zzlmVar;
        synchronized (this.zzail) {
            zzlmVar = this.zzcpn;
        }
        return zzlmVar;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzuu() {
        this.zzaqg.zzts();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final View.OnClickListener zzuw() {
        return this.zzcpt.get();
    }

    private zzll(zza zzaVar, AdSizeParcel adSizeParcel, boolean z, zzas zzasVar, VersionInfoParcel versionInfoParcel, zzdk zzdkVar, com.google.android.gms.ads.internal.zzs zzsVar, com.google.android.gms.ads.internal.zzd zzdVar) {
        super(zzaVar);
        this.zzail = new Object();
        this.zzcpl = true;
        this.zzcpm = false;
        this.zzbvq = "";
        this.zzbrg = -1;
        this.zzbrf = -1;
        this.zzbri = -1;
        this.zzbrj = -1;
        this.zzcpc = zzaVar;
        this.zzani = adSizeParcel;
        this.zzcpi = z;
        this.zzcpk = -1;
        this.zzbgd = zzasVar;
        this.zzalo = versionInfoParcel;
        this.zzcpd = zzsVar;
        this.zzajv = zzdVar;
        this.zzaqm = (WindowManager) getContext().getSystemService("window");
        setBackgroundColor(0);
        WebSettings settings = getSettings();
        settings.setAllowFileAccess(false);
        settings.setJavaScriptEnabled(true);
        settings.setSavePassword(false);
        settings.setSupportMultipleWindows(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= 21) {
            settings.setMixedContentMode(2);
        }
        settings.setUserAgentString(com.google.android.gms.ads.internal.zzu.zzfq().zzg(zzaVar, versionInfoParcel.zzcs));
        com.google.android.gms.ads.internal.zzu.zzfs().zza(getContext(), settings);
        setDownloadListener(this);
        zzvj();
        if (com.google.android.gms.common.util.zzs.zzhb(17)) {
            addJavascriptInterface(new zzln(this), "googleAdsJsInterface");
        }
        if (com.google.android.gms.common.util.zzs.zzhb(11)) {
            removeJavascriptInterface("accessibility");
            removeJavascriptInterface("accessibilityTraversal");
        }
        this.zzaqg = new zzku(this.zzcpc.zzcmv, this, this, null);
        zzd(zzdkVar);
    }

    private boolean zzvg() {
        int i;
        int i2;
        if (!this.zzcpe.zzho()) {
            return false;
        }
        com.google.android.gms.ads.internal.zzu.zzfq();
        DisplayMetrics zza2 = zzkh.zza(this.zzaqm);
        int zzb = com.google.android.gms.ads.internal.client.zzm.zziw().zzb(zza2, zza2.widthPixels);
        int zzb2 = com.google.android.gms.ads.internal.client.zzm.zziw().zzb(zza2, zza2.heightPixels);
        Activity activity = this.zzcpc.zzcmv;
        if (activity == null || activity.getWindow() == null) {
            i = zzb2;
            i2 = zzb;
        } else {
            com.google.android.gms.ads.internal.zzu.zzfq();
            int[] zzh = zzkh.zzh(activity);
            i2 = com.google.android.gms.ads.internal.client.zzm.zziw().zzb(zza2, zzh[0]);
            i = com.google.android.gms.ads.internal.client.zzm.zziw().zzb(zza2, zzh[1]);
        }
        if (this.zzbrf == zzb && this.zzbrg == zzb2 && this.zzbri == i2 && this.zzbrj == i) {
            return false;
        }
        boolean z = (this.zzbrf == zzb && this.zzbrg == zzb2) ? false : true;
        this.zzbrf = zzb;
        this.zzbrg = zzb2;
        this.zzbri = i2;
        this.zzbrj = i;
        new zzhf(this).zza(zzb, zzb2, i2, i, zza2.density, this.zzaqm.getDefaultDisplay().getRotation());
        return z;
    }

    private void zzdc(String str) {
        if (!com.google.android.gms.common.util.zzs.zzhb(19)) {
            String valueOf = String.valueOf(str);
            zzdb(valueOf.length() != 0 ? "javascript:".concat(valueOf) : new String("javascript:"));
            return;
        }
        if (zzsq() == null) {
            synchronized (this.zzail) {
                this.zzcjw = com.google.android.gms.ads.internal.zzu.zzft().zzsq();
                if (this.zzcjw == null) {
                    try {
                        evaluateJavascript("(function(){})()", null);
                        zzb((Boolean) true);
                    } catch (IllegalStateException e) {
                        zzb((Boolean) false);
                    }
                }
            }
        }
        if (!zzsq().booleanValue()) {
            String valueOf2 = String.valueOf(str);
            zzdb(valueOf2.length() != 0 ? "javascript:".concat(valueOf2) : new String("javascript:"));
        } else {
            synchronized (this.zzail) {
                if (isDestroyed()) {
                    zzkd.zzcx("The webview is destroyed. Ignoring action.");
                } else {
                    evaluateJavascript(str, null);
                }
            }
        }
    }

    private void zzb(Boolean bool) {
        synchronized (this.zzail) {
            this.zzcjw = bool;
        }
        zzjx zzft = com.google.android.gms.ads.internal.zzu.zzft();
        synchronized (zzft.zzail) {
            zzft.zzcjw = bool;
        }
    }

    private void zzvi() {
        zzdg.zza(this.zzcps.zzajn, this.zzcpq, "aeh2");
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzoa() {
        if (this.zzcpp == null) {
            zzdg.zza(this.zzcps.zzajn, this.zzcpr, "aes");
            this.zzcpp = zzdg.zzb(this.zzcps.zzajn);
            this.zzcps.zza("native:view_show", this.zzcpp);
        }
        HashMap hashMap = new HashMap(1);
        hashMap.put(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, this.zzalo.zzcs);
        zza("onshow", hashMap);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzud() {
        float f = 0.0f;
        HashMap hashMap = new HashMap(3);
        com.google.android.gms.ads.internal.zzu.zzfq();
        hashMap.put("app_muted", String.valueOf(zzkh.zzfa()));
        com.google.android.gms.ads.internal.zzu.zzfq();
        hashMap.put("app_volume", String.valueOf(zzkh.zzey()));
        com.google.android.gms.ads.internal.zzu.zzfq();
        AudioManager zzak = zzkh.zzak(getContext());
        if (zzak != null) {
            int streamMaxVolume = zzak.getStreamMaxVolume(3);
            int streamVolume = zzak.getStreamVolume(3);
            if (streamMaxVolume != 0) {
                f = streamVolume / streamMaxVolume;
            }
        }
        hashMap.put("device_volume", String.valueOf(f));
        zza("volume", hashMap);
    }

    private void zzd(zzdk zzdkVar) {
        zzvn();
        this.zzcps = new zzdj(new zzdk(true, "make_wv", this.zzani.zzaur));
        zzdk zzdkVar2 = this.zzcps.zzajn;
        synchronized (zzdkVar2.zzail) {
            zzdkVar2.zzbej = zzdkVar;
        }
        this.zzcpq = zzdg.zzb(this.zzcps.zzajn);
        this.zzcps.zza("native:view_create", this.zzcpq);
        this.zzcpr = null;
        this.zzcpp = null;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void setContext(Context context) {
        this.zzcpc.setBaseContext(context);
        this.zzaqg.zzcmv = this.zzcpc.zzcmv;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final Activity zzue() {
        return this.zzcpc.zzcmv;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final Context zzuf() {
        return this.zzcpc.zzcpx;
    }

    @Override // android.webkit.WebView, android.view.View
    @TargetApi(21)
    protected final void onDraw(Canvas canvas) {
        if (isDestroyed()) {
            return;
        }
        if (Build.VERSION.SDK_INT == 21 && canvas.isHardwareAccelerated() && !isAttachedToWindow()) {
            return;
        }
        super.onDraw(canvas);
        if (this.zzcpe == null || this.zzcpe.zzcos == null) {
            return;
        }
        this.zzcpe.zzcos.zzem();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzuv() {
        if (this.zzcpr == null) {
            this.zzcpr = zzdg.zzb(this.zzcps.zzajn);
            this.zzcps.zza("native:view_load", this.zzcpr);
        }
    }

    @Override // android.webkit.WebView, com.google.android.gms.internal.zzlh
    public final void onPause() {
        if (isDestroyed()) {
            return;
        }
        try {
            if (com.google.android.gms.common.util.zzs.zzhb(11)) {
                super.onPause();
            }
        } catch (Exception e) {
            zzkd.zzb("Could not pause webview.", e);
        }
    }

    @Override // android.webkit.WebView, com.google.android.gms.internal.zzlh
    public final void onResume() {
        if (isDestroyed()) {
            return;
        }
        try {
            if (com.google.android.gms.common.util.zzs.zzhb(11)) {
                super.onResume();
            }
        } catch (Exception e) {
            zzkd.zzb("Could not resume webview.", e);
        }
    }

    @Override // com.google.android.gms.internal.zzlh
    public final boolean zzou() {
        boolean z;
        synchronized (this.zzail) {
            zzdg.zza(this.zzcps.zzajn, this.zzcpq, "aebb2");
            z = this.zzcpl;
        }
        return z;
    }

    private void zzvn() {
        zzdk zzdkVar;
        if (this.zzcps == null || (zzdkVar = this.zzcps.zzajn) == null || com.google.android.gms.ads.internal.zzu.zzft().zzsl() == null) {
            return;
        }
        com.google.android.gms.ads.internal.zzu.zzft().zzsl().zza(zzdkVar);
    }
}
