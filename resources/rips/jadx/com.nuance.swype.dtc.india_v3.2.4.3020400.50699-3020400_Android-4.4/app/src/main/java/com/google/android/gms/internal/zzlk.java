package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
@zzin
/* loaded from: classes.dex */
public final class zzlk extends FrameLayout implements zzlh {
    private final zzlh zzcpa;
    private final zzlg zzcpb;

    public zzlk(zzlh zzlhVar) {
        super(zzlhVar.getContext());
        this.zzcpa = zzlhVar;
        this.zzcpb = new zzlg(zzlhVar.zzuf(), this, this);
        zzli zzuj = this.zzcpa.zzuj();
        if (zzuj != null) {
            zzuj.zzl(this);
        }
        addView(this.zzcpa.getView());
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void destroy() {
        this.zzcpa.destroy();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final String getRequestId() {
        return this.zzcpa.getRequestId();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final int getRequestedOrientation() {
        return this.zzcpa.getRequestedOrientation();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final View getView() {
        return this;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final WebView getWebView() {
        return this.zzcpa.getWebView();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final boolean isDestroyed() {
        return this.zzcpa.isDestroyed();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void loadData(String str, String str2, String str3) {
        this.zzcpa.loadData(str, str2, str3);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void loadDataWithBaseURL(String str, String str2, String str3, String str4, String str5) {
        this.zzcpa.loadDataWithBaseURL(str, str2, str3, str4, str5);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void loadUrl(String str) {
        this.zzcpa.loadUrl(str);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void onResume() {
        this.zzcpa.onResume();
    }

    @Override // android.view.View, com.google.android.gms.internal.zzlh
    public final void setBackgroundColor(int i) {
        this.zzcpa.setBackgroundColor(i);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void setContext(Context context) {
        this.zzcpa.setContext(context);
    }

    @Override // android.view.View, com.google.android.gms.internal.zzlh
    public final void setOnClickListener(View.OnClickListener onClickListener) {
        this.zzcpa.setOnClickListener(onClickListener);
    }

    @Override // android.view.View, com.google.android.gms.internal.zzlh
    public final void setOnTouchListener(View.OnTouchListener onTouchListener) {
        this.zzcpa.setOnTouchListener(onTouchListener);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void setRequestedOrientation(int i) {
        this.zzcpa.setRequestedOrientation(i);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void setWebChromeClient(WebChromeClient webChromeClient) {
        this.zzcpa.setWebChromeClient(webChromeClient);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void setWebViewClient(WebViewClient webViewClient) {
        this.zzcpa.setWebViewClient(webViewClient);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void stopLoading() {
        this.zzcpa.stopLoading();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zza(Context context, AdSizeParcel adSizeParcel, zzdk zzdkVar) {
        this.zzcpb.onDestroy();
        this.zzcpa.zza(context, adSizeParcel, zzdkVar);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zza(AdSizeParcel adSizeParcel) {
        this.zzcpa.zza(adSizeParcel);
    }

    @Override // com.google.android.gms.internal.zzce
    public final void zza(zzcd zzcdVar, boolean z) {
        this.zzcpa.zza(zzcdVar, z);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zza(zzlm zzlmVar) {
        this.zzcpa.zza(zzlmVar);
    }

    @Override // com.google.android.gms.internal.zzft
    public final void zza(String str, zzep zzepVar) {
        this.zzcpa.zza(str, zzepVar);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zza(String str, Map<String, ?> map) {
        this.zzcpa.zza(str, map);
    }

    @Override // com.google.android.gms.internal.zzlh, com.google.android.gms.internal.zzft
    public final void zza(String str, JSONObject jSONObject) {
        this.zzcpa.zza(str, jSONObject);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzaf(int i) {
        this.zzcpa.zzaf(i);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzah(boolean z) {
        this.zzcpa.zzah(z);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzai(boolean z) {
        this.zzcpa.zzai(z);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzaj(boolean z) {
        this.zzcpa.zzaj(z);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzb(com.google.android.gms.ads.internal.overlay.zzd zzdVar) {
        this.zzcpa.zzb(zzdVar);
    }

    @Override // com.google.android.gms.internal.zzft
    public final void zzb(String str, zzep zzepVar) {
        this.zzcpa.zzb(str, zzepVar);
    }

    @Override // com.google.android.gms.internal.zzft
    public final void zzb(String str, JSONObject jSONObject) {
        this.zzcpa.zzb(str, jSONObject);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzc(com.google.android.gms.ads.internal.overlay.zzd zzdVar) {
        this.zzcpa.zzc(zzdVar);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzcy(String str) {
        this.zzcpa.zzcy(str);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzcz(String str) {
        this.zzcpa.zzcz(str);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final AdSizeParcel zzdn() {
        return this.zzcpa.zzdn();
    }

    @Override // com.google.android.gms.ads.internal.zzs
    public final void zzef() {
        this.zzcpa.zzef();
    }

    @Override // com.google.android.gms.ads.internal.zzs
    public final void zzeg() {
        this.zzcpa.zzeg();
    }

    @Override // com.google.android.gms.internal.zzlh, com.google.android.gms.internal.zzft
    public final void zzj(String str, String str2) {
        this.zzcpa.zzj(str, str2);
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzoa() {
        this.zzcpa.zzoa();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final boolean zzou() {
        return this.zzcpa.zzou();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzuc() {
        this.zzcpa.zzuc();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzud() {
        this.zzcpa.zzud();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final Activity zzue() {
        return this.zzcpa.zzue();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final Context zzuf() {
        return this.zzcpa.zzuf();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final com.google.android.gms.ads.internal.zzd zzug() {
        return this.zzcpa.zzug();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final com.google.android.gms.ads.internal.overlay.zzd zzuh() {
        return this.zzcpa.zzuh();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final com.google.android.gms.ads.internal.overlay.zzd zzui() {
        return this.zzcpa.zzui();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final zzli zzuj() {
        return this.zzcpa.zzuj();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final boolean zzuk() {
        return this.zzcpa.zzuk();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final zzas zzul() {
        return this.zzcpa.zzul();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final VersionInfoParcel zzum() {
        return this.zzcpa.zzum();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final boolean zzun() {
        return this.zzcpa.zzun();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzuo() {
        this.zzcpb.onDestroy();
        this.zzcpa.zzuo();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final boolean zzup() {
        return this.zzcpa.zzup();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final zzlg zzuq() {
        return this.zzcpb;
    }

    @Override // com.google.android.gms.internal.zzlh
    public final zzdi zzur() {
        return this.zzcpa.zzur();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final zzdj zzus() {
        return this.zzcpa.zzus();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final zzlm zzut() {
        return this.zzcpa.zzut();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzuu() {
        this.zzcpa.zzuu();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void zzuv() {
        this.zzcpa.zzuv();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final View.OnClickListener zzuw() {
        return this.zzcpa.zzuw();
    }

    @Override // com.google.android.gms.internal.zzlh
    public final void onPause() {
        zzlg zzlgVar = this.zzcpb;
        com.google.android.gms.common.internal.zzab.zzhi("onPause must be called from the UI thread.");
        if (zzlgVar.zzbwf != null) {
            zzlgVar.zzbwf.pause();
        }
        this.zzcpa.onPause();
    }
}
