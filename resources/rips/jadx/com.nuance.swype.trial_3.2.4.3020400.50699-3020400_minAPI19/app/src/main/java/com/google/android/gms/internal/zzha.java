package com.google.android.gms.internal;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzha extends zzhf {
    static final Set<String> zzbqe = Collections.unmodifiableSet(new com.google.android.gms.common.util.zza(Arrays.asList("top-left", "top-right", "top-center", "center", "bottom-left", "bottom-right", "bottom-center")));
    int zzaie;
    int zzaif;
    public final Object zzail;
    AdSizeParcel zzani;
    final zzlh zzbgf;
    public final Activity zzbpu;
    String zzbqf;
    boolean zzbqg;
    public int zzbqh;
    public int zzbqi;
    int zzbqj;
    int zzbqk;
    ImageView zzbql;
    LinearLayout zzbqm;
    zzhg zzbqn;
    public PopupWindow zzbqo;
    RelativeLayout zzbqp;
    ViewGroup zzbqq;

    public zzha(zzlh zzlhVar, zzhg zzhgVar) {
        super(zzlhVar, "resize");
        this.zzbqf = "top-right";
        this.zzbqg = true;
        this.zzbqh = 0;
        this.zzbqi = 0;
        this.zzaif = -1;
        this.zzbqj = 0;
        this.zzbqk = 0;
        this.zzaie = -1;
        this.zzail = new Object();
        this.zzbgf = zzlhVar;
        this.zzbpu = zzlhVar.zzue();
        this.zzbqn = zzhgVar;
    }

    public final boolean zzmw() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzbqo != null;
        }
        return z;
    }

    public final void zzs(boolean z) {
        synchronized (this.zzail) {
            if (this.zzbqo != null) {
                this.zzbqo.dismiss();
                this.zzbqp.removeView(this.zzbgf.getView());
                if (this.zzbqq != null) {
                    this.zzbqq.removeView(this.zzbql);
                    this.zzbqq.addView(this.zzbgf.getView());
                    this.zzbgf.zza(this.zzani);
                }
                if (z) {
                    zzbv("default");
                    if (this.zzbqn != null) {
                        this.zzbqn.zzej();
                    }
                }
                this.zzbqo = null;
                this.zzbqp = null;
                this.zzbqq = null;
                this.zzbqm = null;
            }
        }
    }

    public final int[] zzmv() {
        boolean z;
        int i;
        int i2;
        com.google.android.gms.ads.internal.zzu.zzfq();
        int[] zzi = zzkh.zzi(this.zzbpu);
        com.google.android.gms.ads.internal.zzu.zzfq();
        int[] zzk = zzkh.zzk(this.zzbpu);
        int i3 = zzi[0];
        int i4 = zzi[1];
        if (this.zzaie < 50 || this.zzaie > i3) {
            zzkd.zzcx("Width is too small or too large.");
            z = false;
        } else if (this.zzaif < 50 || this.zzaif > i4) {
            zzkd.zzcx("Height is too small or too large.");
            z = false;
        } else if (this.zzaif == i4 && this.zzaie == i3) {
            zzkd.zzcx("Cannot resize to a full-screen ad.");
            z = false;
        } else {
            if (this.zzbqg) {
                String str = this.zzbqf;
                char c = 65535;
                switch (str.hashCode()) {
                    case -1364013995:
                        if (str.equals("center")) {
                            c = 2;
                            break;
                        }
                        break;
                    case -1012429441:
                        if (str.equals("top-left")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -655373719:
                        if (str.equals("bottom-left")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 1163912186:
                        if (str.equals("bottom-right")) {
                            c = 5;
                            break;
                        }
                        break;
                    case 1288627767:
                        if (str.equals("bottom-center")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 1755462605:
                        if (str.equals("top-center")) {
                            c = 1;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        i = this.zzbqj + this.zzbqh;
                        i2 = this.zzbqi + this.zzbqk;
                        break;
                    case 1:
                        i = ((this.zzbqh + this.zzbqj) + (this.zzaie / 2)) - 25;
                        i2 = this.zzbqi + this.zzbqk;
                        break;
                    case 2:
                        i = ((this.zzbqh + this.zzbqj) + (this.zzaie / 2)) - 25;
                        i2 = ((this.zzbqi + this.zzbqk) + (this.zzaif / 2)) - 25;
                        break;
                    case 3:
                        i = this.zzbqj + this.zzbqh;
                        i2 = ((this.zzbqi + this.zzbqk) + this.zzaif) - 50;
                        break;
                    case 4:
                        i = ((this.zzbqh + this.zzbqj) + (this.zzaie / 2)) - 25;
                        i2 = ((this.zzbqi + this.zzbqk) + this.zzaif) - 50;
                        break;
                    case 5:
                        i = ((this.zzbqh + this.zzbqj) + this.zzaie) - 50;
                        i2 = ((this.zzbqi + this.zzbqk) + this.zzaif) - 50;
                        break;
                    default:
                        i = ((this.zzbqh + this.zzbqj) + this.zzaie) - 50;
                        i2 = this.zzbqi + this.zzbqk;
                        break;
                }
                if (i < 0 || i + 50 > i3 || i2 < zzk[0] || i2 + 50 > zzk[1]) {
                    z = false;
                }
            }
            z = true;
        }
        if (!z) {
            return null;
        }
        if (this.zzbqg) {
            return new int[]{this.zzbqh + this.zzbqj, this.zzbqi + this.zzbqk};
        }
        com.google.android.gms.ads.internal.zzu.zzfq();
        int[] zzi2 = zzkh.zzi(this.zzbpu);
        com.google.android.gms.ads.internal.zzu.zzfq();
        int[] zzk2 = zzkh.zzk(this.zzbpu);
        int i5 = zzi2[0];
        int i6 = this.zzbqh + this.zzbqj;
        int i7 = this.zzbqi + this.zzbqk;
        if (i6 < 0) {
            i6 = 0;
        } else if (this.zzaie + i6 > i5) {
            i6 = i5 - this.zzaie;
        }
        if (i7 < zzk2[0]) {
            i7 = zzk2[0];
        } else if (this.zzaif + i7 > zzk2[1]) {
            i7 = zzk2[1] - this.zzaif;
        }
        return new int[]{i6, i7};
    }

    public final void zzc(int i, int i2) {
        com.google.android.gms.ads.internal.zzu.zzfq();
        int i3 = i2 - zzkh.zzk(this.zzbpu)[0];
        int i4 = this.zzaie;
        try {
            super.zzbgf.zzb("onSizeChanged", new JSONObject().put("x", i).put("y", i3).put("width", i4).put("height", this.zzaif));
        } catch (JSONException e) {
            zzkd.zzb("Error occured while dispatching size change.", e);
        }
    }
}
