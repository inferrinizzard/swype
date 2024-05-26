package com.google.android.gms.ads.internal.overlay;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.internal.zzdi;
import com.google.android.gms.internal.zzdk;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkh;
import com.google.android.gms.internal.zzlh;
import java.util.HashMap;

@zzin
/* loaded from: classes.dex */
public class zzk extends FrameLayout implements zzh {
    private final zzlh zzbgf;
    private String zzbjc;
    final FrameLayout zzbtw;
    private final zzy zzbtx;
    zzi zzbty;
    private boolean zzbtz;
    private boolean zzbua;
    TextView zzbub;
    long zzbuc;
    private long zzbud;
    private String zzbue;

    public zzk(Context context, zzlh zzlhVar, int i, boolean z, zzdk zzdkVar, zzdi zzdiVar) {
        super(context);
        this.zzbgf = zzlhVar;
        this.zzbtw = new FrameLayout(context);
        addView(this.zzbtw, new FrameLayout.LayoutParams(-1, -1));
        com.google.android.gms.common.internal.zzb.zzu(zzlhVar.zzug());
        this.zzbty = zzlhVar.zzug().zzakk.zza(context, zzlhVar, i, z, zzdkVar, zzdiVar);
        if (this.zzbty != null) {
            this.zzbtw.addView(this.zzbty, new FrameLayout.LayoutParams(-1, -1, 17));
        }
        this.zzbub = new TextView(context);
        this.zzbub.setBackgroundColor(-16777216);
        zzop();
        this.zzbtx = new zzy(this);
        this.zzbtx.zzpk();
        if (this.zzbty != null) {
            this.zzbty.zza(this);
        }
        if (this.zzbty == null) {
            zzl("AdVideoUnderlay Error", "Allocating player failed.");
        }
    }

    public static void zzh(zzlh zzlhVar) {
        HashMap hashMap = new HashMap();
        hashMap.put("event", "no_video_view");
        zzlhVar.zza("onVideoEvent", hashMap);
    }

    private void zzop() {
        if (zzor()) {
            return;
        }
        this.zzbtw.addView(this.zzbub, new FrameLayout.LayoutParams(-1, -1));
        this.zzbtw.bringChildToFront(this.zzbub);
    }

    private void zzot() {
        if (this.zzbgf.zzue() == null || !this.zzbtz || this.zzbua) {
            return;
        }
        this.zzbgf.zzue().getWindow().clearFlags(128);
        this.zzbtz = false;
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzh
    public void onPaused() {
        zza("pause", new String[0]);
        zzot();
    }

    public void pause() {
        if (this.zzbty == null) {
            return;
        }
        this.zzbty.pause();
    }

    public void play() {
        if (this.zzbty == null) {
            return;
        }
        this.zzbty.play();
    }

    public void seekTo(int i) {
        if (this.zzbty == null) {
            return;
        }
        this.zzbty.seekTo(i);
    }

    public void setMimeType(String str) {
        this.zzbue = str;
    }

    public void zza(float f) {
        if (this.zzbty == null) {
            return;
        }
        this.zzbty.zza(f);
    }

    public void zza(float f, float f2) {
        if (this.zzbty != null) {
            this.zzbty.zza(f, f2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void zza(String str, String... strArr) {
        HashMap hashMap = new HashMap();
        hashMap.put("event", str);
        int length = strArr.length;
        int i = 0;
        String str2 = null;
        while (i < length) {
            String str3 = strArr[i];
            if (str2 != null) {
                hashMap.put(str2, str3);
                str3 = null;
            }
            i++;
            str2 = str3;
        }
        this.zzbgf.zza("onVideoEvent", hashMap);
    }

    public void zzbw(String str) {
        this.zzbjc = str;
    }

    public void zzd(int i, int i2, int i3, int i4) {
        if (i3 == 0 || i4 == 0) {
            return;
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(i3 + 2, i4 + 2);
        layoutParams.setMargins(i - 1, i2 - 1, 0, 0);
        this.zzbtw.setLayoutParams(layoutParams);
        requestLayout();
    }

    public void zzd(MotionEvent motionEvent) {
        if (this.zzbty == null) {
            return;
        }
        this.zzbty.dispatchTouchEvent(motionEvent);
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzh
    public void zzl(String str, String str2) {
        zza("error", "what", str, "extra", str2);
    }

    public void zzlv() {
        if (this.zzbty == null) {
            return;
        }
        if (TextUtils.isEmpty(this.zzbjc)) {
            zza("no_src", new String[0]);
        } else {
            this.zzbty.setMimeType(this.zzbue);
            this.zzbty.setVideoPath(this.zzbjc);
        }
    }

    public void zzno() {
        if (this.zzbty == null) {
            return;
        }
        this.zzbty.zzno();
    }

    public void zznp() {
        if (this.zzbty == null) {
            return;
        }
        this.zzbty.zznp();
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzh
    public void zzoi() {
        zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.overlay.zzk.1
            @Override // java.lang.Runnable
            public final void run() {
                zzk.this.zza("surfaceCreated", new String[0]);
            }
        });
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzh
    public void zzoj() {
        if (this.zzbty != null && this.zzbud == 0) {
            zza("canplaythrough", "duration", String.valueOf(this.zzbty.getDuration() / 1000.0f), "videoWidth", String.valueOf(this.zzbty.getVideoWidth()), "videoHeight", String.valueOf(this.zzbty.getVideoHeight()));
        }
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzh
    public void zzol() {
        zza("ended", new String[0]);
        zzot();
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzh
    public void zzom() {
        zzop();
        this.zzbud = this.zzbuc;
        zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.overlay.zzk.2
            @Override // java.lang.Runnable
            public final void run() {
                zzk.this.zza("surfaceDestroyed", new String[0]);
            }
        });
    }

    public void zzon() {
        if (this.zzbty == null) {
            return;
        }
        TextView textView = new TextView(this.zzbty.getContext());
        String valueOf = String.valueOf(this.zzbty.zzni());
        textView.setText(valueOf.length() != 0 ? "AdMob - ".concat(valueOf) : new String("AdMob - "));
        textView.setTextColor(-65536);
        textView.setBackgroundColor(-256);
        this.zzbtw.addView(textView, new FrameLayout.LayoutParams(-2, -2, 17));
        this.zzbtw.bringChildToFront(textView);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean zzor() {
        return this.zzbub.getParent() != null;
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzh
    public void zzok() {
        if (this.zzbgf.zzue() == null || this.zzbtz) {
            return;
        }
        this.zzbua = (this.zzbgf.zzue().getWindow().getAttributes().flags & 128) != 0;
        if (this.zzbua) {
            return;
        }
        this.zzbgf.zzue().getWindow().addFlags(128);
        this.zzbtz = true;
    }

    public void destroy() {
        zzy zzyVar = this.zzbtx;
        zzyVar.mCancelled = true;
        zzkh.zzclc.removeCallbacks(zzyVar);
        if (this.zzbty != null) {
            this.zzbty.stop();
        }
        zzot();
    }
}
