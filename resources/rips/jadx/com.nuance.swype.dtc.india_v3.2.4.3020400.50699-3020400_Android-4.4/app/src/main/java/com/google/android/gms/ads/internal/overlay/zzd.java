package com.google.android.gms.ads.internal.overlay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.google.android.gms.internal.zzdc;
import com.google.android.gms.internal.zzhi;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkc;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkh;
import com.google.android.gms.internal.zzki;
import com.google.android.gms.internal.zzkk;
import com.google.android.gms.internal.zzkp;
import com.google.android.gms.internal.zzlh;
import com.google.android.gms.internal.zzli;
import com.google.android.gms.internal.zzlj;
import java.util.Collections;
import java.util.Map;

@zzin
/* loaded from: classes.dex */
public class zzd extends zzhi.zza implements zzu {
    static final int zzbsn = Color.argb(0, 0, 0, 0);
    private final Activity mActivity;
    zzlh zzbgf;
    AdOverlayInfoParcel zzbso;
    zzc zzbsp;
    zzo zzbsq;
    FrameLayout zzbss;
    WebChromeClient.CustomViewCallback zzbst;
    zzb zzbsw;
    private boolean zzbta;
    boolean zzbsr = false;
    boolean zzbsu = false;
    boolean zzbsv = false;
    boolean zzbsx = false;
    int zzbsy = 0;
    private boolean zzbtb = false;
    private boolean zzbtc = true;
    zzl zzbsz = new zzs();

    /* JADX INFO: Access modifiers changed from: private */
    @zzin
    /* loaded from: classes.dex */
    public static final class zza extends Exception {
        public zza(String str) {
            super(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @zzin
    /* loaded from: classes.dex */
    public static class zzb extends RelativeLayout {
        zzkk zzaqf;
        boolean zzbte;

        public zzb(Context context, String str) {
            super(context);
            this.zzaqf = new zzkk(context, str);
        }

        @Override // android.view.ViewGroup
        public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (this.zzbte) {
                return false;
            }
            this.zzaqf.zze(motionEvent);
            return false;
        }
    }

    @zzin
    /* loaded from: classes.dex */
    public static class zzc {
        public final int index;
        public final Context zzagf;
        public final ViewGroup.LayoutParams zzbtf;
        public final ViewGroup zzbtg;

        public zzc(zzlh zzlhVar) throws zza {
            this.zzbtf = zzlhVar.getLayoutParams();
            ViewParent parent = zzlhVar.getParent();
            this.zzagf = zzlhVar.zzuf();
            if (parent == null || !(parent instanceof ViewGroup)) {
                throw new zza("Could not get the parent of the WebView for an overlay.");
            }
            this.zzbtg = (ViewGroup) parent;
            this.index = this.zzbtg.indexOfChild(zzlhVar.getView());
            this.zzbtg.removeView(zzlhVar.getView());
            zzlhVar.zzah(true);
        }
    }

    public zzd(Activity activity) {
        this.mActivity = activity;
    }

    private void zzoa() {
        this.zzbgf.zzoa();
    }

    public void close() {
        this.zzbsy = 2;
        this.mActivity.finish();
    }

    @Override // com.google.android.gms.internal.zzhi
    public void onActivityResult(int i, int i2, Intent intent) {
    }

    @Override // com.google.android.gms.internal.zzhi
    public void onBackPressed() {
        this.zzbsy = 0;
    }

    @Override // com.google.android.gms.internal.zzhi
    public void onDestroy() {
        if (this.zzbgf != null) {
            this.zzbsw.removeView(this.zzbgf.getView());
        }
        zzny();
    }

    @Override // com.google.android.gms.internal.zzhi
    public void onPause() {
        this.zzbsz.pause();
        zznu();
        if (this.zzbso.zzbtl != null) {
            this.zzbso.zzbtl.onPause();
        }
        if (this.zzbgf != null && (!this.mActivity.isFinishing() || this.zzbsp == null)) {
            com.google.android.gms.ads.internal.zzu.zzfs();
            zzki.zzi(this.zzbgf);
        }
        zzny();
    }

    @Override // com.google.android.gms.internal.zzhi
    public void onRestart() {
    }

    @Override // com.google.android.gms.internal.zzhi
    public void onResume() {
        if (this.zzbso != null && this.zzbso.zzbts == 4) {
            if (this.zzbsu) {
                this.zzbsy = 3;
                this.mActivity.finish();
            } else {
                this.zzbsu = true;
            }
        }
        if (this.zzbso.zzbtl != null) {
            this.zzbso.zzbtl.onResume();
        }
        if (this.zzbgf == null || this.zzbgf.isDestroyed()) {
            zzkd.zzcx("The webview does not exit. Ignoring action.");
        } else {
            com.google.android.gms.ads.internal.zzu.zzfs();
            zzki.zzj(this.zzbgf);
        }
        this.zzbsz.resume();
    }

    @Override // com.google.android.gms.internal.zzhi
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean("com.google.android.gms.ads.internal.overlay.hasResumed", this.zzbsu);
    }

    @Override // com.google.android.gms.internal.zzhi
    public void onStart() {
    }

    @Override // com.google.android.gms.internal.zzhi
    public void onStop() {
        zzny();
    }

    public void setRequestedOrientation(int i) {
        this.mActivity.setRequestedOrientation(i);
    }

    public void zza(View view, WebChromeClient.CustomViewCallback customViewCallback) {
        this.zzbss = new FrameLayout(this.mActivity);
        this.zzbss.setBackgroundColor(-16777216);
        this.zzbss.addView(view, -1, -1);
        this.mActivity.setContentView(this.zzbss);
        zzdb();
        this.zzbst = customViewCallback;
        this.zzbsr = true;
    }

    public void zza(boolean z, boolean z2) {
        if (this.zzbsq != null) {
            this.zzbsq.zza(z, z2);
        }
    }

    @Override // com.google.android.gms.internal.zzhi
    public void zzdb() {
        this.zzbta = true;
    }

    public void zzf(zzlh zzlhVar, Map<String, String> map) {
        this.zzbsz.zzf(zzlhVar, map);
    }

    public void zznu() {
        if (this.zzbso != null && this.zzbsr) {
            setRequestedOrientation(this.zzbso.orientation);
        }
        if (this.zzbss != null) {
            this.mActivity.setContentView(this.zzbsw);
            zzdb();
            this.zzbss.removeAllViews();
            this.zzbss = null;
        }
        if (this.zzbst != null) {
            this.zzbst.onCustomViewHidden();
            this.zzbst = null;
        }
        this.zzbsr = false;
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzu
    public void zznv() {
        this.zzbsy = 1;
        this.mActivity.finish();
    }

    @Override // com.google.android.gms.internal.zzhi
    public boolean zznw() {
        this.zzbsy = 0;
        if (this.zzbgf != null) {
            r0 = this.zzbgf.zzou() && this.zzbsz.zzou();
            if (!r0) {
                this.zzbgf.zza("onbackblocked", Collections.emptyMap());
            }
        }
        return r0;
    }

    public void zznx() {
        this.zzbsw.removeView(this.zzbsq);
        zzz(true);
    }

    public void zznz() {
        if (this.zzbsx) {
            this.zzbsx = false;
            zzoa();
        }
    }

    public void zzz(boolean z) {
        this.zzbsq = new zzo(this.mActivity, z ? 50 : 32, this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams.addRule(10);
        layoutParams.addRule(z ? 11 : 9);
        this.zzbsq.zza(z, this.zzbso.zzbtp);
        this.zzbsw.addView(this.zzbsq, layoutParams);
    }

    @zzin
    /* renamed from: com.google.android.gms.ads.internal.overlay.zzd$zzd, reason: collision with other inner class name */
    /* loaded from: classes.dex */
    private class C0026zzd extends zzkc {
        private C0026zzd() {
        }

        /* synthetic */ C0026zzd(zzd zzdVar, byte b) {
            this();
        }

        @Override // com.google.android.gms.internal.zzkc
        public final void onStop() {
        }

        @Override // com.google.android.gms.internal.zzkc
        public final void zzew() {
            zzkp zzgh = com.google.android.gms.ads.internal.zzu.zzgh();
            Bitmap bitmap = zzgh.zzcmp.get(Integer.valueOf(zzd.this.zzbso.zzbtv.zzamj));
            if (bitmap != null) {
                final Drawable zza = com.google.android.gms.ads.internal.zzu.zzfs().zza(zzd.this.mActivity, bitmap, zzd.this.zzbso.zzbtv.zzamh, zzd.this.zzbso.zzbtv.zzami);
                zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.overlay.zzd.zzd.1
                    @Override // java.lang.Runnable
                    public final void run() {
                        zzd.this.mActivity.getWindow().setBackgroundDrawable(zza);
                    }
                });
            }
        }
    }

    @Override // com.google.android.gms.internal.zzhi
    public void onCreate(Bundle bundle) {
        this.mActivity.requestWindowFeature(1);
        this.zzbsu = bundle != null ? bundle.getBoolean("com.google.android.gms.ads.internal.overlay.hasResumed", false) : false;
        try {
            this.zzbso = AdOverlayInfoParcel.zzb(this.mActivity.getIntent());
            if (this.zzbso == null) {
                throw new zza("Could not get info for ad overlay.");
            }
            if (this.zzbso.zzaow.zzcnl > 7500000) {
                this.zzbsy = 3;
            }
            if (this.mActivity.getIntent() != null) {
                this.zzbtc = this.mActivity.getIntent().getBooleanExtra("shouldCallOnOverlayOpened", true);
            }
            if (this.zzbso.zzbtv != null) {
                this.zzbsv = this.zzbso.zzbtv.zzame;
            } else {
                this.zzbsv = false;
            }
            if (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbca)).booleanValue() && this.zzbsv && this.zzbso.zzbtv.zzamj != -1) {
                new C0026zzd(this, (byte) 0).zzpy();
            }
            if (bundle == null) {
                if (this.zzbso.zzbtl != null && this.zzbtc) {
                    this.zzbso.zzbtl.zzdy();
                }
                if (this.zzbso.zzbts != 1 && this.zzbso.zzbtk != null) {
                    this.zzbso.zzbtk.onAdClicked();
                }
            }
            this.zzbsw = new zzb(this.mActivity, this.zzbso.zzbtu);
            this.zzbsw.setId(1000);
            switch (this.zzbso.zzbts) {
                case 1:
                    zzaa(false);
                    return;
                case 2:
                    this.zzbsp = new zzc(this.zzbso.zzbtm);
                    zzaa(false);
                    return;
                case 3:
                    zzaa(true);
                    return;
                case 4:
                    if (this.zzbsu) {
                        this.zzbsy = 3;
                        this.mActivity.finish();
                        return;
                    } else {
                        if (com.google.android.gms.ads.internal.zzu.zzfn().zza(this.mActivity, this.zzbso.zzbtj, this.zzbso.zzbtr)) {
                            return;
                        }
                        this.zzbsy = 3;
                        this.mActivity.finish();
                        return;
                    }
                default:
                    throw new zza("Could not determine ad overlay type.");
            }
        } catch (zza e) {
            zzkd.zzcx(e.getMessage());
            this.zzbsy = 3;
            this.mActivity.finish();
        }
    }

    private void zzaa(boolean z) throws zza {
        if (!this.zzbta) {
            this.mActivity.requestWindowFeature(1);
        }
        Window window = this.mActivity.getWindow();
        if (window == null) {
            throw new zza("Invalid activity, no window available.");
        }
        if (!this.zzbsv || (this.zzbso.zzbtv != null && this.zzbso.zzbtv.zzamf)) {
            window.setFlags(1024, 1024);
        }
        zzli zzuj = this.zzbso.zzbtm.zzuj();
        boolean zzho = zzuj != null ? zzuj.zzho() : false;
        this.zzbsx = false;
        if (zzho) {
            if (this.zzbso.orientation == com.google.android.gms.ads.internal.zzu.zzfs().zztj()) {
                this.zzbsx = this.mActivity.getResources().getConfiguration().orientation == 1;
            } else if (this.zzbso.orientation == com.google.android.gms.ads.internal.zzu.zzfs().zztk()) {
                this.zzbsx = this.mActivity.getResources().getConfiguration().orientation == 2;
            }
        }
        zzkd.zzcv(new StringBuilder(46).append("Delay onShow to next orientation change: ").append(this.zzbsx).toString());
        setRequestedOrientation(this.zzbso.orientation);
        if (com.google.android.gms.ads.internal.zzu.zzfs().zza(window)) {
            zzkd.zzcv("Hardware acceleration on the AdActivity window enabled.");
        }
        if (this.zzbsv) {
            this.zzbsw.setBackgroundColor(zzbsn);
        } else {
            this.zzbsw.setBackgroundColor(-16777216);
        }
        this.mActivity.setContentView(this.zzbsw);
        zzdb();
        if (z) {
            com.google.android.gms.ads.internal.zzu.zzfr();
            this.zzbgf = zzlj.zza(this.mActivity, this.zzbso.zzbtm.zzdn(), true, zzho, null, this.zzbso.zzaow, null, null, this.zzbso.zzbtm.zzug());
            this.zzbgf.zzuj().zza(null, null, this.zzbso.zzbtn, this.zzbso.zzbtr, true, this.zzbso.zzbtt, null, this.zzbso.zzbtm.zzuj().zzbit, null, null);
            this.zzbgf.zzuj().zzbya = new zzli.zza() { // from class: com.google.android.gms.ads.internal.overlay.zzd.1
                @Override // com.google.android.gms.internal.zzli.zza
                public final void zza(zzlh zzlhVar, boolean z2) {
                    zzlhVar.zzoa();
                }
            };
            if (this.zzbso.url != null) {
                this.zzbgf.loadUrl(this.zzbso.url);
            } else {
                if (this.zzbso.zzbtq == null) {
                    throw new zza("No URL or HTML to display in ad overlay.");
                }
                this.zzbgf.loadDataWithBaseURL(this.zzbso.zzbto, this.zzbso.zzbtq, "text/html", "UTF-8", null);
            }
            if (this.zzbso.zzbtm != null) {
                this.zzbso.zzbtm.zzc(this);
            }
        } else {
            this.zzbgf = this.zzbso.zzbtm;
            this.zzbgf.setContext(this.mActivity);
        }
        this.zzbgf.zzb(this);
        ViewParent parent = this.zzbgf.getParent();
        if (parent != null && (parent instanceof ViewGroup)) {
            ((ViewGroup) parent).removeView(this.zzbgf.getView());
        }
        if (this.zzbsv) {
            this.zzbgf.setBackgroundColor(zzbsn);
        }
        this.zzbsw.addView(this.zzbgf.getView(), -1, -1);
        if (!z && !this.zzbsx) {
            zzoa();
        }
        zzz(zzho);
        if (this.zzbgf.zzuk()) {
            zza(zzho, true);
        }
        com.google.android.gms.ads.internal.zzd zzug = this.zzbgf.zzug();
        zzm zzmVar = zzug != null ? zzug.zzakl : null;
        if (zzmVar != null) {
            this.zzbsz = zzmVar.zza(this.mActivity, this.zzbgf, this.zzbsw);
        } else {
            zzkd.zzcx("Appstreaming controller is null.");
        }
    }

    private void zzny() {
        if (!this.mActivity.isFinishing() || this.zzbtb) {
            return;
        }
        this.zzbtb = true;
        if (this.zzbgf != null) {
            this.zzbgf.zzaf(this.zzbsy);
            this.zzbsw.removeView(this.zzbgf.getView());
            if (this.zzbsp != null) {
                this.zzbgf.setContext(this.zzbsp.zzagf);
                this.zzbgf.zzah(false);
                this.zzbsp.zzbtg.addView(this.zzbgf.getView(), this.zzbsp.index, this.zzbsp.zzbtf);
                this.zzbsp = null;
            } else if (this.mActivity.getApplicationContext() != null) {
                this.zzbgf.setContext(this.mActivity.getApplicationContext());
            }
            this.zzbgf = null;
        }
        if (this.zzbso != null && this.zzbso.zzbtl != null) {
            this.zzbso.zzbtl.zzdx();
        }
        this.zzbsz.destroy();
    }

    public void zzob() {
        this.zzbsw.zzbte = true;
    }
}
