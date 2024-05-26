package com.google.android.gms.ads.internal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.client.VideoOptionsParcel;
import com.google.android.gms.ads.internal.client.zzu;
import com.google.android.gms.ads.internal.client.zzw;
import com.google.android.gms.ads.internal.client.zzy;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.internal.zzbw;
import com.google.android.gms.internal.zzbx;
import com.google.android.gms.internal.zzdc;
import com.google.android.gms.internal.zzdo;
import com.google.android.gms.internal.zzho;
import com.google.android.gms.internal.zzhs;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkg;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@zzin
/* loaded from: classes.dex */
public class zzt extends zzu.zza {
    private final Context mContext;
    private com.google.android.gms.ads.internal.client.zzq zzalf;
    private final VersionInfoParcel zzalo;
    private final AdSizeParcel zzani;
    private final Future<zzbw> zzanj = zzkg.zza(new Callable<zzbw>() { // from class: com.google.android.gms.ads.internal.zzt.3
        @Override // java.util.concurrent.Callable
        public final /* synthetic */ zzbw call() throws Exception {
            return new zzbw(zzt.this.zzalo.zzcs, zzt.this.mContext);
        }
    });
    private final zzb zzank;
    private WebView zzanl;
    private zzbw zzanm;
    private AsyncTask<Void, Void, Void> zzann;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class zzb {
        final String zzanp;
        final Map<String, String> zzanq = new TreeMap();
        String zzanr;
        String zzans;

        public zzb(String str) {
            this.zzanp = str;
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void destroy() throws RemoteException {
        zzab.zzhi("destroy must be called on the main UI thread.");
        this.zzann.cancel(true);
        this.zzanj.cancel(true);
        this.zzanl.destroy();
        this.zzanl = null;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public String getMediationAdapterClassName() throws RemoteException {
        return null;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public boolean isLoading() throws RemoteException {
        return false;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public boolean isReady() throws RemoteException {
        return false;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void pause() throws RemoteException {
        zzab.zzhi("pause must be called on the main UI thread.");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void resume() throws RemoteException {
        zzab.zzhi("resume must be called on the main UI thread.");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void setManualImpressionsEnabled(boolean z) throws RemoteException {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void setUserId(String str) throws RemoteException {
        throw new IllegalStateException("Unused method");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void showInterstitial() throws RemoteException {
        throw new IllegalStateException("Unused method");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void stopLoading() throws RemoteException {
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(AdSizeParcel adSizeParcel) throws RemoteException {
        throw new IllegalStateException("AdSize must be set before initialization");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(VideoOptionsParcel videoOptionsParcel) {
        throw new IllegalStateException("Unused method");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(com.google.android.gms.ads.internal.client.zzp zzpVar) throws RemoteException {
        throw new IllegalStateException("Unused method");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(com.google.android.gms.ads.internal.client.zzq zzqVar) throws RemoteException {
        this.zzalf = zzqVar;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(zzw zzwVar) throws RemoteException {
        throw new IllegalStateException("Unused method");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(zzy zzyVar) throws RemoteException {
        throw new IllegalStateException("Unused method");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(com.google.android.gms.ads.internal.reward.client.zzd zzdVar) throws RemoteException {
        throw new IllegalStateException("Unused method");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(zzdo zzdoVar) throws RemoteException {
        throw new IllegalStateException("Unused method");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(zzho zzhoVar) throws RemoteException {
        throw new IllegalStateException("Unused method");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(zzhs zzhsVar, String str) throws RemoteException {
        throw new IllegalStateException("Unused method");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public com.google.android.gms.dynamic.zzd zzdm() throws RemoteException {
        zzab.zzhi("getAdFrame must be called on the main UI thread.");
        return com.google.android.gms.dynamic.zze.zzac(this.zzanl);
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public AdSizeParcel zzdn() throws RemoteException {
        return this.zzani;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zzdp() throws RemoteException {
        throw new IllegalStateException("Unused method");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public com.google.android.gms.ads.internal.client.zzab zzdq() {
        return null;
    }

    final void zzj(int i) {
        if (this.zzanl == null) {
            return;
        }
        this.zzanl.setLayoutParams(new ViewGroup.LayoutParams(-1, i));
    }

    final int zzw(String str) {
        String queryParameter = Uri.parse(str).getQueryParameter("height");
        if (TextUtils.isEmpty(queryParameter)) {
            return 0;
        }
        try {
            return com.google.android.gms.ads.internal.client.zzm.zziw().zza(this.mContext, Integer.parseInt(queryParameter));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /* loaded from: classes.dex */
    private class zza extends AsyncTask<Void, Void, Void> {
        private zza() {
        }

        /* synthetic */ zza(zzt zztVar, byte b) {
            this();
        }

        @Override // android.os.AsyncTask
        protected final /* bridge */ /* synthetic */ Void doInBackground(Void[] voidArr) {
            return doInBackground$10299ca();
        }

        private Void doInBackground$10299ca() {
            try {
                zzt.this.zzanm = (zzbw) zzt.this.zzanj.get(((Long) zzu.zzfz().zzd(zzdc.zzbdd)).longValue(), TimeUnit.MILLISECONDS);
                return null;
            } catch (InterruptedException e) {
                e = e;
                zzkd.zzd("Failed to load ad data", e);
                return null;
            } catch (ExecutionException e2) {
                e = e2;
                zzkd.zzd("Failed to load ad data", e);
                return null;
            } catch (TimeoutException e3) {
                zzkd.zzcx("Timed out waiting for ad data");
                return null;
            }
        }

        @Override // android.os.AsyncTask
        protected final /* bridge */ /* synthetic */ void onPostExecute(Void r3) {
            String zzfe = zzt.this.zzfe();
            if (zzt.this.zzanl != null) {
                zzt.this.zzanl.loadUrl(zzfe);
            }
        }
    }

    public zzt(Context context, AdSizeParcel adSizeParcel, String str, VersionInfoParcel versionInfoParcel) {
        this.mContext = context;
        this.zzalo = versionInfoParcel;
        this.zzani = adSizeParcel;
        this.zzanl = new WebView(this.mContext);
        this.zzank = new zzb(str);
        zzj(0);
        this.zzanl.setVerticalScrollBarEnabled(false);
        this.zzanl.getSettings().setJavaScriptEnabled(true);
        this.zzanl.setWebViewClient(new WebViewClient() { // from class: com.google.android.gms.ads.internal.zzt.1
            @Override // android.webkit.WebViewClient
            public final void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                if (zzt.this.zzalf != null) {
                    try {
                        zzt.this.zzalf.onAdFailedToLoad(0);
                    } catch (RemoteException e) {
                        zzkd.zzd("Could not call AdListener.onAdFailedToLoad().", e);
                    }
                }
            }

            @Override // android.webkit.WebViewClient
            public final boolean shouldOverrideUrlLoading(WebView webView, String str2) {
                if (str2.startsWith(zzt.this.zzff())) {
                    return false;
                }
                if (str2.startsWith((String) zzu.zzfz().zzd(zzdc.zzbcy))) {
                    if (zzt.this.zzalf != null) {
                        try {
                            zzt.this.zzalf.onAdFailedToLoad(3);
                        } catch (RemoteException e) {
                            zzkd.zzd("Could not call AdListener.onAdFailedToLoad().", e);
                        }
                    }
                    zzt.this.zzj(0);
                    return true;
                }
                if (str2.startsWith((String) zzu.zzfz().zzd(zzdc.zzbcz))) {
                    if (zzt.this.zzalf != null) {
                        try {
                            zzt.this.zzalf.onAdFailedToLoad(0);
                        } catch (RemoteException e2) {
                            zzkd.zzd("Could not call AdListener.onAdFailedToLoad().", e2);
                        }
                    }
                    zzt.this.zzj(0);
                    return true;
                }
                if (str2.startsWith((String) zzu.zzfz().zzd(zzdc.zzbda))) {
                    if (zzt.this.zzalf != null) {
                        try {
                            zzt.this.zzalf.onAdLoaded();
                        } catch (RemoteException e3) {
                            zzkd.zzd("Could not call AdListener.onAdLoaded().", e3);
                        }
                    }
                    zzt.this.zzj(zzt.this.zzw(str2));
                    return true;
                }
                if (str2.startsWith("gmsg://")) {
                    return true;
                }
                if (zzt.this.zzalf != null) {
                    try {
                        zzt.this.zzalf.onAdLeftApplication();
                    } catch (RemoteException e4) {
                        zzkd.zzd("Could not call AdListener.onAdLeftApplication().", e4);
                    }
                }
                zzt.zzb(zzt.this, zzt.this.zzx(str2));
                return true;
            }
        });
        this.zzanl.setOnTouchListener(new View.OnTouchListener() { // from class: com.google.android.gms.ads.internal.zzt.2
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                if (zzt.this.zzanm == null) {
                    return false;
                }
                try {
                    zzt.this.zzanm.zzaip.zzd(com.google.android.gms.dynamic.zze.zzac(motionEvent));
                    return false;
                } catch (RemoteException e) {
                    zzkd.zzd("Unable to process ad data", e);
                    return false;
                }
            }
        });
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public boolean zzb(AdRequestParcel adRequestParcel) throws RemoteException {
        byte b = 0;
        zzab.zzb(this.zzanl, "This Search Ad has already been torn down");
        zzb zzbVar = this.zzank;
        zzbVar.zzanr = adRequestParcel.zzatt.zzaxl;
        Bundle bundle = adRequestParcel.zzatw != null ? adRequestParcel.zzatw.getBundle(AdMobAdapter.class.getName()) : null;
        if (bundle != null) {
            String str = (String) zzu.zzfz().zzd(zzdc.zzbdc);
            for (String str2 : bundle.keySet()) {
                if (str.equals(str2)) {
                    zzbVar.zzans = bundle.getString(str2);
                } else if (str2.startsWith("csa_")) {
                    zzbVar.zzanq.put(str2.substring(4), bundle.getString(str2));
                }
            }
        }
        this.zzann = new zza(this, b).execute(new Void[0]);
        return true;
    }

    final String zzfe() {
        com.google.android.gms.dynamic.zzd zza2;
        Uri uri;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https://").appendEncodedPath((String) zzu.zzfz().zzd(zzdc.zzbdb));
        builder.appendQueryParameter("query", this.zzank.zzanr);
        builder.appendQueryParameter("pubId", this.zzank.zzanp);
        Map<String, String> map = this.zzank.zzanq;
        for (String str : map.keySet()) {
            builder.appendQueryParameter(str, map.get(str));
        }
        Uri build = builder.build();
        if (this.zzanm != null) {
            try {
                zzbw zzbwVar = this.zzanm;
                Context context = this.mContext;
                zza2 = zzbwVar.zzaip.zza(com.google.android.gms.dynamic.zze.zzac(build), com.google.android.gms.dynamic.zze.zzac(context));
            } catch (RemoteException | zzbx e) {
                zzkd.zzd("Unable to process ad data", e);
            }
            if (zza2 == null) {
                throw new zzbx();
            }
            uri = (Uri) com.google.android.gms.dynamic.zze.zzad(zza2);
            String valueOf = String.valueOf(zzff());
            String valueOf2 = String.valueOf(uri.getEncodedQuery());
            return new StringBuilder(String.valueOf(valueOf).length() + 1 + String.valueOf(valueOf2).length()).append(valueOf).append("#").append(valueOf2).toString();
        }
        uri = build;
        String valueOf3 = String.valueOf(zzff());
        String valueOf22 = String.valueOf(uri.getEncodedQuery());
        return new StringBuilder(String.valueOf(valueOf3).length() + 1 + String.valueOf(valueOf22).length()).append(valueOf3).append("#").append(valueOf22).toString();
    }

    final String zzff() {
        String str = this.zzank.zzans;
        String str2 = TextUtils.isEmpty(str) ? "www.google.com" : str;
        String valueOf = String.valueOf("https://");
        String str3 = (String) zzu.zzfz().zzd(zzdc.zzbdb);
        return new StringBuilder(String.valueOf(valueOf).length() + 0 + String.valueOf(str2).length() + String.valueOf(str3).length()).append(valueOf).append(str2).append(str3).toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String zzx(String str) {
        Uri uri;
        com.google.android.gms.dynamic.zzd zzb2;
        if (this.zzanm == null) {
            return str;
        }
        Uri parse = Uri.parse(str);
        try {
            zzbw zzbwVar = this.zzanm;
            Context context = this.mContext;
            zzb2 = zzbwVar.zzaip.zzb(com.google.android.gms.dynamic.zze.zzac(parse), com.google.android.gms.dynamic.zze.zzac(context));
        } catch (RemoteException e) {
            zzkd.zzd("Unable to process ad data", e);
            uri = parse;
        } catch (zzbx e2) {
            zzkd.zzd("Unable to parse ad click url", e2);
            uri = parse;
        }
        if (zzb2 == null) {
            throw new zzbx();
        }
        uri = (Uri) com.google.android.gms.dynamic.zze.zzad(zzb2);
        return uri.toString();
    }

    static /* synthetic */ void zzb(zzt zztVar, String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(str));
        zztVar.mContext.startActivity(intent);
    }
}
