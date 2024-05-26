package com.google.android.gms.internal;

import android.content.Context;
import android.text.TextUtils;
import com.google.android.gms.common.api.Releasable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

@zzin
/* loaded from: classes.dex */
public abstract class zzfd implements Releasable {
    protected Context mContext;
    protected String zzbjf;
    protected WeakReference<zzlh> zzbjg;

    public zzfd(zzlh zzlhVar) {
        this.mContext = zzlhVar.getContext();
        this.zzbjf = com.google.android.gms.ads.internal.zzu.zzfq().zzg(this.mContext, zzlhVar.zzum().zzcs);
        this.zzbjg = new WeakReference<>(zzlhVar);
    }

    public abstract void abort();

    /* JADX INFO: Access modifiers changed from: protected */
    public final void zza(final String str, final String str2, final int i) {
        com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.internal.zzfd.2
            @Override // java.lang.Runnable
            public final void run() {
                HashMap hashMap = new HashMap();
                hashMap.put("event", "precacheComplete");
                hashMap.put("src", str);
                hashMap.put("cachedSrc", str2);
                hashMap.put("totalBytes", Integer.toString(i));
                zzfd.zza(zzfd.this, "onPrecacheEvent", hashMap);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void zza(final String str, final String str2, final String str3, final String str4) {
        com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.internal.zzfd.3
            @Override // java.lang.Runnable
            public final void run() {
                HashMap hashMap = new HashMap();
                hashMap.put("event", "precacheCanceled");
                hashMap.put("src", str);
                if (!TextUtils.isEmpty(str2)) {
                    hashMap.put("cachedSrc", str2);
                }
                String str5 = str3;
                String str6 = "internal";
                char c = 65535;
                switch (str5.hashCode()) {
                    case -1396664534:
                        if (str5.equals("badUrl")) {
                            c = 6;
                            break;
                        }
                        break;
                    case -1347010958:
                        if (str5.equals("inProgress")) {
                            c = 2;
                            break;
                        }
                        break;
                    case -918817863:
                        if (str5.equals("downloadTimeout")) {
                            c = 7;
                            break;
                        }
                        break;
                    case -659376217:
                        if (str5.equals("contentLengthMissing")) {
                            c = 3;
                            break;
                        }
                        break;
                    case -642208130:
                        if (str5.equals("playerFailed")) {
                            c = 1;
                            break;
                        }
                        break;
                    case -354048396:
                        if (str5.equals("sizeExceeded")) {
                            c = '\b';
                            break;
                        }
                        break;
                    case -32082395:
                        if (str5.equals("externalAbort")) {
                            c = '\t';
                            break;
                        }
                        break;
                    case 96784904:
                        if (str5.equals("error")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 580119100:
                        if (str5.equals("expireFailed")) {
                            c = 5;
                            break;
                        }
                        break;
                    case 725497484:
                        if (str5.equals("noCacheDir")) {
                            c = 4;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        str6 = "internal";
                        break;
                    case 4:
                    case 5:
                        str6 = "io";
                        break;
                    case 6:
                    case 7:
                        str6 = "network";
                        break;
                    case '\b':
                    case '\t':
                        str6 = "policy";
                        break;
                }
                hashMap.put("type", str6);
                hashMap.put("reason", str3);
                if (!TextUtils.isEmpty(str4)) {
                    hashMap.put("message", str4);
                }
                zzfd.zza(zzfd.this, "onPrecacheEvent", hashMap);
            }
        });
    }

    public abstract boolean zzaz(String str);

    static /* synthetic */ void zza(zzfd zzfdVar, String str, Map map) {
        zzlh zzlhVar = zzfdVar.zzbjg.get();
        if (zzlhVar != null) {
            zzlhVar.zza(str, (Map<String, ?>) map);
        }
    }
}
