package com.google.android.gms.internal;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.CalendarContract;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.facebook.share.internal.ShareConstants;
import com.google.android.gms.R;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import java.util.Collections;
import java.util.Map;

@zzin
/* loaded from: classes.dex */
public final class zzev implements zzep {
    static final Map<String, Integer> zzbiv;
    private final com.google.android.gms.ads.internal.zze zzbit;
    private final zzha zzbiu;

    public zzev(com.google.android.gms.ads.internal.zze zzeVar, zzha zzhaVar) {
        this.zzbit = zzeVar;
        this.zzbiu = zzhaVar;
    }

    @Override // com.google.android.gms.internal.zzep
    public final void zza(zzlh zzlhVar, Map<String, String> map) {
        char c = 65535;
        int intValue = zzbiv.get(map.get("a")).intValue();
        if (intValue != 5 && this.zzbit != null && !this.zzbit.zzel()) {
            this.zzbit.zzt(null);
            return;
        }
        switch (intValue) {
            case 1:
                final zzha zzhaVar = this.zzbiu;
                synchronized (zzhaVar.zzail) {
                    if (zzhaVar.zzbpu == null) {
                        zzhaVar.zzbt("Not an activity context. Cannot resize.");
                        return;
                    }
                    if (zzhaVar.zzbgf.zzdn() == null) {
                        zzhaVar.zzbt("Webview is not yet available, size is not set.");
                        return;
                    }
                    if (zzhaVar.zzbgf.zzdn().zzaus) {
                        zzhaVar.zzbt("Is interstitial. Cannot resize an interstitial.");
                        return;
                    }
                    if (zzhaVar.zzbgf.zzun()) {
                        zzhaVar.zzbt("Cannot resize an expanded banner.");
                        return;
                    }
                    if (!TextUtils.isEmpty(map.get("width"))) {
                        com.google.android.gms.ads.internal.zzu.zzfq();
                        zzhaVar.zzaie = zzkh.zzcp(map.get("width"));
                    }
                    if (!TextUtils.isEmpty(map.get("height"))) {
                        com.google.android.gms.ads.internal.zzu.zzfq();
                        zzhaVar.zzaif = zzkh.zzcp(map.get("height"));
                    }
                    if (!TextUtils.isEmpty(map.get("offsetX"))) {
                        com.google.android.gms.ads.internal.zzu.zzfq();
                        zzhaVar.zzbqj = zzkh.zzcp(map.get("offsetX"));
                    }
                    if (!TextUtils.isEmpty(map.get("offsetY"))) {
                        com.google.android.gms.ads.internal.zzu.zzfq();
                        zzhaVar.zzbqk = zzkh.zzcp(map.get("offsetY"));
                    }
                    if (!TextUtils.isEmpty(map.get("allowOffscreen"))) {
                        zzhaVar.zzbqg = Boolean.parseBoolean(map.get("allowOffscreen"));
                    }
                    String str = map.get("customClosePosition");
                    if (!TextUtils.isEmpty(str)) {
                        zzhaVar.zzbqf = str;
                    }
                    if (!(zzhaVar.zzaie >= 0 && zzhaVar.zzaif >= 0)) {
                        zzhaVar.zzbt("Invalid width and height options. Cannot resize.");
                        return;
                    }
                    Window window = zzhaVar.zzbpu.getWindow();
                    if (window == null || window.getDecorView() == null) {
                        zzhaVar.zzbt("Activity context is not ready, cannot get window or decor view.");
                        return;
                    }
                    int[] zzmv = zzhaVar.zzmv();
                    if (zzmv == null) {
                        zzhaVar.zzbt("Resize location out of screen or close button is not visible.");
                        return;
                    }
                    int zza = com.google.android.gms.ads.internal.client.zzm.zziw().zza(zzhaVar.zzbpu, zzhaVar.zzaie);
                    int zza2 = com.google.android.gms.ads.internal.client.zzm.zziw().zza(zzhaVar.zzbpu, zzhaVar.zzaif);
                    ViewParent parent = zzhaVar.zzbgf.getView().getParent();
                    if (parent == null || !(parent instanceof ViewGroup)) {
                        zzhaVar.zzbt("Webview is detached, probably in the middle of a resize or expand.");
                        return;
                    }
                    ((ViewGroup) parent).removeView(zzhaVar.zzbgf.getView());
                    if (zzhaVar.zzbqo == null) {
                        zzhaVar.zzbqq = (ViewGroup) parent;
                        com.google.android.gms.ads.internal.zzu.zzfq();
                        Bitmap zzk = zzkh.zzk(zzhaVar.zzbgf.getView());
                        zzhaVar.zzbql = new ImageView(zzhaVar.zzbpu);
                        zzhaVar.zzbql.setImageBitmap(zzk);
                        zzhaVar.zzani = zzhaVar.zzbgf.zzdn();
                        zzhaVar.zzbqq.addView(zzhaVar.zzbql);
                    } else {
                        zzhaVar.zzbqo.dismiss();
                    }
                    zzhaVar.zzbqp = new RelativeLayout(zzhaVar.zzbpu);
                    zzhaVar.zzbqp.setBackgroundColor(0);
                    zzhaVar.zzbqp.setLayoutParams(new ViewGroup.LayoutParams(zza, zza2));
                    com.google.android.gms.ads.internal.zzu.zzfq();
                    zzhaVar.zzbqo = zzkh.zza$490f73c3(zzhaVar.zzbqp, zza, zza2);
                    zzhaVar.zzbqo.setOutsideTouchable(true);
                    zzhaVar.zzbqo.setTouchable(true);
                    zzhaVar.zzbqo.setClippingEnabled(!zzhaVar.zzbqg);
                    zzhaVar.zzbqp.addView(zzhaVar.zzbgf.getView(), -1, -1);
                    zzhaVar.zzbqm = new LinearLayout(zzhaVar.zzbpu);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(com.google.android.gms.ads.internal.client.zzm.zziw().zza(zzhaVar.zzbpu, 50), com.google.android.gms.ads.internal.client.zzm.zziw().zza(zzhaVar.zzbpu, 50));
                    String str2 = zzhaVar.zzbqf;
                    switch (str2.hashCode()) {
                        case -1364013995:
                            if (str2.equals("center")) {
                                c = 2;
                                break;
                            }
                            break;
                        case -1012429441:
                            if (str2.equals("top-left")) {
                                c = 0;
                                break;
                            }
                            break;
                        case -655373719:
                            if (str2.equals("bottom-left")) {
                                c = 3;
                                break;
                            }
                            break;
                        case 1163912186:
                            if (str2.equals("bottom-right")) {
                                c = 5;
                                break;
                            }
                            break;
                        case 1288627767:
                            if (str2.equals("bottom-center")) {
                                c = 4;
                                break;
                            }
                            break;
                        case 1755462605:
                            if (str2.equals("top-center")) {
                                c = 1;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            layoutParams.addRule(10);
                            layoutParams.addRule(9);
                            break;
                        case 1:
                            layoutParams.addRule(10);
                            layoutParams.addRule(14);
                            break;
                        case 2:
                            layoutParams.addRule(13);
                            break;
                        case 3:
                            layoutParams.addRule(12);
                            layoutParams.addRule(9);
                            break;
                        case 4:
                            layoutParams.addRule(12);
                            layoutParams.addRule(14);
                            break;
                        case 5:
                            layoutParams.addRule(12);
                            layoutParams.addRule(11);
                            break;
                        default:
                            layoutParams.addRule(10);
                            layoutParams.addRule(11);
                            break;
                    }
                    zzhaVar.zzbqm.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.gms.internal.zzha.1
                        public AnonymousClass1() {
                        }

                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            zzha.this.zzs(true);
                        }
                    });
                    zzhaVar.zzbqm.setContentDescription("Close button");
                    zzhaVar.zzbqp.addView(zzhaVar.zzbqm, layoutParams);
                    try {
                        zzhaVar.zzbqo.showAtLocation(window.getDecorView(), 0, com.google.android.gms.ads.internal.client.zzm.zziw().zza(zzhaVar.zzbpu, zzmv[0]), com.google.android.gms.ads.internal.client.zzm.zziw().zza(zzhaVar.zzbpu, zzmv[1]));
                        int i = zzmv[0];
                        int i2 = zzmv[1];
                        if (zzhaVar.zzbqn != null) {
                            zzhaVar.zzbqn.zza(i, i2, zzhaVar.zzaie, zzhaVar.zzaif);
                        }
                        zzhaVar.zzbgf.zza(new AdSizeParcel(zzhaVar.zzbpu, new AdSize(zzhaVar.zzaie, zzhaVar.zzaif)));
                        zzhaVar.zzc(zzmv[0], zzmv[1]);
                        zzhaVar.zzbv("resized");
                        return;
                    } catch (RuntimeException e) {
                        String valueOf = String.valueOf(e.getMessage());
                        zzhaVar.zzbt(valueOf.length() != 0 ? "Cannot show popup window: ".concat(valueOf) : new String("Cannot show popup window: "));
                        zzhaVar.zzbqp.removeView(zzhaVar.zzbgf.getView());
                        if (zzhaVar.zzbqq != null) {
                            zzhaVar.zzbqq.removeView(zzhaVar.zzbql);
                            zzhaVar.zzbqq.addView(zzhaVar.zzbgf.getView());
                            zzhaVar.zzbgf.zza(zzhaVar.zzani);
                        }
                        return;
                    }
                }
            case 2:
            default:
                zzkd.zzcw("Unknown MRAID command called.");
                return;
            case 3:
                zzhc zzhcVar = new zzhc(zzlhVar, map);
                if (zzhcVar.mContext == null) {
                    zzhcVar.zzbt("Activity context is not available");
                    return;
                }
                com.google.android.gms.ads.internal.zzu.zzfq();
                if (!zzkh.zzag(zzhcVar.mContext).zzjr()) {
                    zzhcVar.zzbt("Feature is not supported by the device.");
                    return;
                }
                String str3 = zzhcVar.zzbeg.get("iurl");
                if (TextUtils.isEmpty(str3)) {
                    zzhcVar.zzbt("Image url cannot be empty.");
                    return;
                }
                if (!URLUtil.isValidUrl(str3)) {
                    String valueOf2 = String.valueOf(str3);
                    zzhcVar.zzbt(valueOf2.length() != 0 ? "Invalid image url: ".concat(valueOf2) : new String("Invalid image url: "));
                    return;
                }
                String lastPathSegment = Uri.parse(str3).getLastPathSegment();
                com.google.android.gms.ads.internal.zzu.zzfq();
                if (!zzkh.zzcq(lastPathSegment)) {
                    String valueOf3 = String.valueOf(lastPathSegment);
                    zzhcVar.zzbt(valueOf3.length() != 0 ? "Image type not recognized: ".concat(valueOf3) : new String("Image type not recognized: "));
                    return;
                }
                Resources resources = com.google.android.gms.ads.internal.zzu.zzft().getResources();
                com.google.android.gms.ads.internal.zzu.zzfq();
                AlertDialog.Builder zzaf = zzkh.zzaf(zzhcVar.mContext);
                zzaf.setTitle(resources != null ? resources.getString(R.string.store_picture_title) : "Save image");
                zzaf.setMessage(resources != null ? resources.getString(R.string.store_picture_message) : "Allow Ad to store image in Picture gallery?");
                zzaf.setPositiveButton(resources != null ? resources.getString(R.string.accept) : "Accept", new DialogInterface.OnClickListener() { // from class: com.google.android.gms.internal.zzhc.1
                    final /* synthetic */ String zzbqu;
                    final /* synthetic */ String zzbqv;

                    public AnonymousClass1(String str32, String lastPathSegment2) {
                        r2 = str32;
                        r3 = lastPathSegment2;
                    }

                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        DownloadManager downloadManager = (DownloadManager) zzhc.this.mContext.getSystemService("download");
                        try {
                            String str4 = r2;
                            String str5 = r3;
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(str4));
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, str5);
                            com.google.android.gms.ads.internal.zzu.zzfs().zza(request);
                            downloadManager.enqueue(request);
                        } catch (IllegalStateException e2) {
                            zzhc.this.zzbt("Could not store picture.");
                        }
                    }
                });
                zzaf.setNegativeButton(resources != null ? resources.getString(R.string.decline) : "Decline", new DialogInterface.OnClickListener() { // from class: com.google.android.gms.internal.zzhc.2
                    public AnonymousClass2() {
                    }

                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        zzhc.this.zzbt("User canceled the download.");
                    }
                });
                zzaf.create().show();
                return;
            case 4:
                final zzgz zzgzVar = new zzgz(zzlhVar, map);
                if (zzgzVar.mContext == null) {
                    zzgzVar.zzbt("Activity context is not available.");
                    return;
                }
                com.google.android.gms.ads.internal.zzu.zzfq();
                if (!zzkh.zzag(zzgzVar.mContext).zzju()) {
                    zzgzVar.zzbt("This feature is not available on the device.");
                    return;
                }
                com.google.android.gms.ads.internal.zzu.zzfq();
                AlertDialog.Builder zzaf2 = zzkh.zzaf(zzgzVar.mContext);
                Resources resources2 = com.google.android.gms.ads.internal.zzu.zzft().getResources();
                zzaf2.setTitle(resources2 != null ? resources2.getString(R.string.create_calendar_title) : "Create calendar event");
                zzaf2.setMessage(resources2 != null ? resources2.getString(R.string.create_calendar_message) : "Allow Ad to create a calendar event?");
                zzaf2.setPositiveButton(resources2 != null ? resources2.getString(R.string.accept) : "Accept", new DialogInterface.OnClickListener() { // from class: com.google.android.gms.internal.zzgz.1
                    public AnonymousClass1() {
                    }

                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        zzgz zzgzVar2 = zzgz.this;
                        Intent data = new Intent("android.intent.action.EDIT").setData(CalendarContract.Events.CONTENT_URI);
                        data.putExtra(ShareConstants.WEB_DIALOG_PARAM_TITLE, zzgzVar2.zzbpy);
                        data.putExtra("eventLocation", zzgzVar2.zzbqc);
                        data.putExtra("description", zzgzVar2.zzbqb);
                        if (zzgzVar2.zzbpz > -1) {
                            data.putExtra("beginTime", zzgzVar2.zzbpz);
                        }
                        if (zzgzVar2.zzbqa > -1) {
                            data.putExtra("endTime", zzgzVar2.zzbqa);
                        }
                        data.setFlags(268435456);
                        com.google.android.gms.ads.internal.zzu.zzfq();
                        zzkh.zzb(zzgz.this.mContext, data);
                    }
                });
                zzaf2.setNegativeButton(resources2 != null ? resources2.getString(R.string.decline) : "Decline", new DialogInterface.OnClickListener() { // from class: com.google.android.gms.internal.zzgz.2
                    public AnonymousClass2() {
                    }

                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        zzgz.this.zzbt("Operation denied by user.");
                    }
                });
                zzaf2.create().show();
                return;
            case 5:
                zzhb zzhbVar = new zzhb(zzlhVar, map);
                if (zzhbVar.zzbgf == null) {
                    zzkd.zzcx("AdWebView is null");
                    return;
                } else {
                    zzhbVar.zzbgf.setRequestedOrientation("portrait".equalsIgnoreCase(zzhbVar.zzbqt) ? com.google.android.gms.ads.internal.zzu.zzfs().zztk() : "landscape".equalsIgnoreCase(zzhbVar.zzbqt) ? com.google.android.gms.ads.internal.zzu.zzfs().zztj() : zzhbVar.zzbqs ? -1 : com.google.android.gms.ads.internal.zzu.zzfs().zztl());
                    return;
                }
            case 6:
                this.zzbiu.zzs(true);
                return;
        }
    }

    static {
        ArrayMap arrayMap = new ArrayMap(6);
        arrayMap.put("resize", 1);
        arrayMap.put("playVideo", 2);
        arrayMap.put("storePicture", 3);
        arrayMap.put("createCalendarEvent", 4);
        arrayMap.put("setOrientationProperties", 5);
        arrayMap.put("closeResizedAd", 6);
        zzbiv = Collections.unmodifiableMap(arrayMap);
    }
}
