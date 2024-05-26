package com.google.android.gms.internal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import com.google.android.gms.ads.internal.request.AdResponseParcel;
import com.google.android.gms.internal.zzli;

@zzin
/* loaded from: classes.dex */
public final class zzhz implements Runnable {
    private final int zzaie;
    private final int zzaif;
    protected final zzlh zzbgf;
    final Handler zzbxx;
    final long zzbxy;
    private long zzbxz;
    private zzli.zza zzbya;
    protected boolean zzbyb;
    protected boolean zzbyc;

    private zzhz(zzli.zza zzaVar, zzlh zzlhVar, int i, int i2) {
        this.zzbxy = 200L;
        this.zzbxz = 50L;
        this.zzbxx = new Handler(Looper.getMainLooper());
        this.zzbgf = zzlhVar;
        this.zzbya = zzaVar;
        this.zzbyb = false;
        this.zzbyc = false;
        this.zzaif = i2;
        this.zzaie = i;
    }

    public zzhz(zzli.zza zzaVar, zzlh zzlhVar, int i, int i2, byte b) {
        this(zzaVar, zzlhVar, i, i2);
    }

    static /* synthetic */ long zzc(zzhz zzhzVar) {
        long j = zzhzVar.zzbxz - 1;
        zzhzVar.zzbxz = j;
        return j;
    }

    @Override // java.lang.Runnable
    public final void run() {
        if (this.zzbgf == null || zzqb()) {
            this.zzbya.zza(this.zzbgf, true);
        } else {
            new zza(this.zzbgf.getWebView()).execute(new Void[0]);
        }
    }

    public final synchronized void zzqa() {
        this.zzbyb = true;
    }

    public final synchronized boolean zzqb() {
        return this.zzbyb;
    }

    public final boolean zzqc() {
        return this.zzbyc;
    }

    /* loaded from: classes.dex */
    protected final class zza extends AsyncTask<Void, Void, Boolean> {
        private final WebView zzbyd;
        private Bitmap zzbye;

        public zza(WebView webView) {
            this.zzbyd = webView;
        }

        private synchronized Boolean zza$5f8445a4() {
            boolean z;
            int width = this.zzbye.getWidth();
            int height = this.zzbye.getHeight();
            if (width == 0 || height == 0) {
                z = false;
            } else {
                int i = 0;
                for (int i2 = 0; i2 < width; i2 += 10) {
                    for (int i3 = 0; i3 < height; i3 += 10) {
                        if (this.zzbye.getPixel(i2, i3) != 0) {
                            i++;
                        }
                    }
                }
                z = Boolean.valueOf(((double) i) / (((double) (width * height)) / 100.0d) > 0.1d);
            }
            return z;
        }

        @Override // android.os.AsyncTask
        protected final /* synthetic */ Boolean doInBackground(Void[] voidArr) {
            return zza$5f8445a4();
        }

        @Override // android.os.AsyncTask
        protected final synchronized void onPreExecute() {
            this.zzbye = Bitmap.createBitmap(zzhz.this.zzaie, zzhz.this.zzaif, Bitmap.Config.ARGB_8888);
            this.zzbyd.setVisibility(0);
            this.zzbyd.measure(View.MeasureSpec.makeMeasureSpec(zzhz.this.zzaie, 0), View.MeasureSpec.makeMeasureSpec(zzhz.this.zzaif, 0));
            this.zzbyd.layout(0, 0, zzhz.this.zzaie, zzhz.this.zzaif);
            this.zzbyd.draw(new Canvas(this.zzbye));
            this.zzbyd.invalidate();
        }

        @Override // android.os.AsyncTask
        protected final /* synthetic */ void onPostExecute(Boolean bool) {
            Boolean bool2 = bool;
            zzhz.zzc(zzhz.this);
            if (bool2.booleanValue() || zzhz.this.zzqb() || zzhz.this.zzbxz <= 0) {
                zzhz.this.zzbyc = bool2.booleanValue();
                zzhz.this.zzbya.zza(zzhz.this.zzbgf, true);
            } else if (zzhz.this.zzbxz > 0) {
                if (zzkd.zzaz(2)) {
                    zzkd.zzcv("Ad not detected, scheduling another run.");
                }
                zzhz.this.zzbxx.postDelayed(zzhz.this, zzhz.this.zzbxy);
            }
        }
    }

    public final void zza(AdResponseParcel adResponseParcel) {
        String zzco;
        this.zzbgf.setWebViewClient(new zzlr(this, this.zzbgf, adResponseParcel.zzccf));
        zzlh zzlhVar = this.zzbgf;
        if (TextUtils.isEmpty(adResponseParcel.zzbto)) {
            zzco = null;
        } else {
            com.google.android.gms.ads.internal.zzu.zzfq();
            zzco = zzkh.zzco(adResponseParcel.zzbto);
        }
        zzlhVar.loadDataWithBaseURL(zzco, adResponseParcel.body, "text/html", "UTF-8", null);
    }
}
