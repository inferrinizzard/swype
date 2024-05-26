package com.google.android.gms.internal;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.ViewGroup;
import com.facebook.internal.NativeProtocol;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.util.Map;
import java.util.WeakHashMap;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzez implements zzep {
    private final Map<zzlh, Integer> zzbiz = new WeakHashMap();
    private boolean zzbja;

    private static int zza(Context context, Map<String, String> map, String str, int i) {
        String str2 = map.get(str);
        if (str2 == null) {
            return i;
        }
        try {
            return com.google.android.gms.ads.internal.client.zzm.zziw().zza(context, Integer.parseInt(str2));
        } catch (NumberFormatException e) {
            zzkd.zzcx(new StringBuilder(String.valueOf(str).length() + 34 + String.valueOf(str2).length()).append("Could not parse ").append(str).append(" in a video GMSG: ").append(str2).toString());
            return i;
        }
    }

    @Override // com.google.android.gms.internal.zzep
    public final void zza(zzlh zzlhVar, Map<String, String> map) {
        int i;
        com.google.android.gms.ads.internal.overlay.zzk zzub;
        String str = map.get(NativeProtocol.WEB_DIALOG_ACTION);
        if (str == null) {
            zzkd.zzcx("Action missing from video GMSG.");
            return;
        }
        if (zzkd.zzaz(3)) {
            JSONObject jSONObject = new JSONObject(map);
            jSONObject.remove("google.afma.Notify_dt");
            String valueOf = String.valueOf(jSONObject.toString());
            zzkd.zzcv(new StringBuilder(String.valueOf(str).length() + 13 + String.valueOf(valueOf).length()).append("Video GMSG: ").append(str).append(XMLResultsHandler.SEP_SPACE).append(valueOf).toString());
        }
        if ("background".equals(str)) {
            String str2 = map.get("color");
            if (TextUtils.isEmpty(str2)) {
                zzkd.zzcx("Color parameter missing from color video GMSG.");
                return;
            }
            try {
                int parseColor = Color.parseColor(str2);
                zzlg zzuq = zzlhVar.zzuq();
                if (zzuq == null || (zzub = zzuq.zzub()) == null) {
                    this.zzbiz.put(zzlhVar, Integer.valueOf(parseColor));
                } else {
                    zzub.setBackgroundColor(parseColor);
                }
                return;
            } catch (IllegalArgumentException e) {
                zzkd.zzcx("Invalid color parameter in video GMSG.");
                return;
            }
        }
        zzlg zzuq2 = zzlhVar.zzuq();
        if (zzuq2 == null) {
            zzkd.zzcx("Could not get underlay container for a video GMSG.");
            return;
        }
        boolean equals = "new".equals(str);
        boolean equals2 = "position".equals(str);
        if (equals || equals2) {
            Context context = zzlhVar.getContext();
            int zza = zza(context, map, "x", 0);
            int zza2 = zza(context, map, "y", 0);
            int zza3 = zza(context, map, "w", -1);
            int zza4 = zza(context, map, "h", -1);
            try {
                i = Integer.parseInt(map.get("player"));
            } catch (NumberFormatException e2) {
                i = 0;
            }
            boolean parseBoolean = Boolean.parseBoolean(map.get("spherical"));
            if (!equals || zzuq2.zzub() != null) {
                com.google.android.gms.common.internal.zzab.zzhi("The underlay may only be modified from the UI thread.");
                if (zzuq2.zzbwf != null) {
                    zzuq2.zzbwf.zzd(zza, zza2, zza3, zza4);
                    return;
                }
                return;
            }
            if (zzuq2.zzbwf == null) {
                zzdg.zza(zzuq2.zzbgf.zzus().zzajn, zzuq2.zzbgf.zzur(), "vpr");
                zzuq2.zzbwf = new com.google.android.gms.ads.internal.overlay.zzk(zzuq2.mContext, zzuq2.zzbgf, i, parseBoolean, zzuq2.zzbgf.zzus().zzajn, zzdg.zzb(zzuq2.zzbgf.zzus().zzajn));
                zzuq2.zzcoi.addView(zzuq2.zzbwf, 0, new ViewGroup.LayoutParams(-1, -1));
                zzuq2.zzbwf.zzd(zza, zza2, zza3, zza4);
                zzuq2.zzbgf.zzuj().zzcoo = false;
            }
            if (this.zzbiz.containsKey(zzlhVar)) {
                zzuq2.zzub().setBackgroundColor(this.zzbiz.get(zzlhVar).intValue());
                return;
            }
            return;
        }
        com.google.android.gms.ads.internal.overlay.zzk zzub2 = zzuq2.zzub();
        if (zzub2 == null) {
            com.google.android.gms.ads.internal.overlay.zzk.zzh(zzlhVar);
            return;
        }
        if ("click".equals(str)) {
            Context context2 = zzlhVar.getContext();
            int zza5 = zza(context2, map, "x", 0);
            int zza6 = zza(context2, map, "y", 0);
            long uptimeMillis = SystemClock.uptimeMillis();
            MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 0, zza5, zza6, 0);
            zzub2.zzd(obtain);
            obtain.recycle();
            return;
        }
        if ("currentTime".equals(str)) {
            String str3 = map.get("time");
            if (str3 == null) {
                zzkd.zzcx("Time parameter missing from currentTime video GMSG.");
                return;
            }
            try {
                zzub2.seekTo((int) (Float.parseFloat(str3) * 1000.0f));
                return;
            } catch (NumberFormatException e3) {
                String valueOf2 = String.valueOf(str3);
                zzkd.zzcx(valueOf2.length() != 0 ? "Could not parse time parameter from currentTime video GMSG: ".concat(valueOf2) : new String("Could not parse time parameter from currentTime video GMSG: "));
                return;
            }
        }
        if ("hide".equals(str)) {
            zzub2.setVisibility(4);
            return;
        }
        if ("load".equals(str)) {
            zzub2.zzlv();
            return;
        }
        if ("mimetype".equals(str)) {
            zzub2.setMimeType(map.get("mimetype"));
            return;
        }
        if ("muted".equals(str)) {
            if (Boolean.parseBoolean(map.get("muted"))) {
                zzub2.zzno();
                return;
            } else {
                zzub2.zznp();
                return;
            }
        }
        if ("pause".equals(str)) {
            zzub2.pause();
            return;
        }
        if ("play".equals(str)) {
            zzub2.play();
            return;
        }
        if ("show".equals(str)) {
            zzub2.setVisibility(0);
            return;
        }
        if ("src".equals(str)) {
            zzub2.zzbw(map.get("src"));
            return;
        }
        if ("touchMove".equals(str)) {
            Context context3 = zzlhVar.getContext();
            zzub2.zza(zza(context3, map, "dx", 0), zza(context3, map, "dy", 0));
            if (this.zzbja) {
                return;
            }
            zzlhVar.zzuh().zzob();
            this.zzbja = true;
            return;
        }
        if (!"volume".equals(str)) {
            if ("watermark".equals(str)) {
                zzub2.zzon();
                return;
            } else {
                String valueOf3 = String.valueOf(str);
                zzkd.zzcx(valueOf3.length() != 0 ? "Unknown video action: ".concat(valueOf3) : new String("Unknown video action: "));
                return;
            }
        }
        String str4 = map.get("volume");
        if (str4 == null) {
            zzkd.zzcx("Level parameter missing from volume video GMSG.");
            return;
        }
        try {
            zzub2.zza(Float.parseFloat(str4));
        } catch (NumberFormatException e4) {
            String valueOf4 = String.valueOf(str4);
            zzkd.zzcx(valueOf4.length() != 0 ? "Could not parse volume parameter from volume video GMSG: ".concat(valueOf4) : new String("Could not parse volume parameter from volume video GMSG: "));
        }
    }
}
