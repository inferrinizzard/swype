package com.google.android.gms.internal;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import com.facebook.internal.ServerProtocol;
import com.google.android.gms.ads.internal.overlay.AdLauncherIntentInfoParcel;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@zzin
/* loaded from: classes.dex */
public final class zzew implements zzep {
    private final com.google.android.gms.ads.internal.zze zzbit;
    private final zzha zzbiu;
    private final zzer zzbiw;

    /* loaded from: classes.dex */
    public static class zza {
        final zzlh zzbgf;

        public zza(zzlh zzlhVar) {
            this.zzbgf = zzlhVar;
        }

        private static Intent zza(Intent intent, ResolveInfo resolveInfo) {
            Intent intent2 = new Intent(intent);
            intent2.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
            return intent2;
        }

        private static ResolveInfo zza(Context context, Intent intent) {
            return zza(context, intent, new ArrayList());
        }

        private static ResolveInfo zza(Context context, Intent intent, ArrayList<ResolveInfo> arrayList) {
            ResolveInfo resolveInfo;
            PackageManager packageManager = context.getPackageManager();
            if (packageManager == null) {
                return null;
            }
            List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 65536);
            ResolveInfo resolveActivity = packageManager.resolveActivity(intent, 65536);
            if (queryIntentActivities != null && resolveActivity != null) {
                int i = 0;
                while (true) {
                    int i2 = i;
                    if (i2 >= queryIntentActivities.size()) {
                        break;
                    }
                    ResolveInfo resolveInfo2 = queryIntentActivities.get(i2);
                    if (resolveActivity != null && resolveActivity.activityInfo.name.equals(resolveInfo2.activityInfo.name)) {
                        resolveInfo = resolveActivity;
                        break;
                    }
                    i = i2 + 1;
                }
            }
            resolveInfo = null;
            arrayList.addAll(queryIntentActivities);
            return resolveInfo;
        }

        private static Intent zze(Uri uri) {
            if (uri == null) {
                return null;
            }
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addFlags(268435456);
            intent.setData(uri);
            intent.setAction("android.intent.action.VIEW");
            return intent;
        }

        public final Intent zza(Context context, Map<String, String> map) {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses;
            ResolveInfo zza;
            ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
            String str = map.get("u");
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            if (this.zzbgf != null) {
                com.google.android.gms.ads.internal.zzu.zzfq();
                str = zzkh.zza(this.zzbgf, str);
            }
            Uri parse = Uri.parse(str);
            boolean parseBoolean = Boolean.parseBoolean(map.get("use_first_package"));
            boolean parseBoolean2 = Boolean.parseBoolean(map.get("use_running_process"));
            Uri build = "http".equalsIgnoreCase(parse.getScheme()) ? parse.buildUpon().scheme("https").build() : "https".equalsIgnoreCase(parse.getScheme()) ? parse.buildUpon().scheme("http").build() : null;
            ArrayList arrayList = new ArrayList();
            Intent zze = zze(parse);
            Intent zze2 = zze(build);
            ResolveInfo zza2 = zza(context, zze, arrayList);
            if (zza2 != null) {
                return zza(zze, zza2);
            }
            if (zze2 != null && (zza = zza(context, zze2)) != null) {
                Intent zza3 = zza(zze, zza);
                if (zza(context, zza3) != null) {
                    return zza3;
                }
            }
            if (arrayList.size() == 0) {
                return zze;
            }
            if (parseBoolean2 && activityManager != null && (runningAppProcesses = activityManager.getRunningAppProcesses()) != null) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    ResolveInfo resolveInfo = (ResolveInfo) it.next();
                    Iterator<ActivityManager.RunningAppProcessInfo> it2 = runningAppProcesses.iterator();
                    while (it2.hasNext()) {
                        if (it2.next().processName.equals(resolveInfo.activityInfo.packageName)) {
                            return zza(zze, resolveInfo);
                        }
                    }
                }
            }
            return parseBoolean ? zza(zze, (ResolveInfo) arrayList.get(0)) : zze;
        }
    }

    public zzew(zzer zzerVar, com.google.android.gms.ads.internal.zze zzeVar, zzha zzhaVar) {
        this.zzbiw = zzerVar;
        this.zzbit = zzeVar;
        this.zzbiu = zzhaVar;
    }

    private static boolean zzc(Map<String, String> map) {
        return "1".equals(map.get("custom_close"));
    }

    private static int zzd(Map<String, String> map) {
        String str = map.get("o");
        if (str != null) {
            if ("p".equalsIgnoreCase(str)) {
                return com.google.android.gms.ads.internal.zzu.zzfs().zztk();
            }
            if ("l".equalsIgnoreCase(str)) {
                return com.google.android.gms.ads.internal.zzu.zzfs().zztj();
            }
            if ("c".equalsIgnoreCase(str)) {
                return com.google.android.gms.ads.internal.zzu.zzfs().zztl();
            }
        }
        return -1;
    }

    private void zzr(boolean z) {
        if (this.zzbiu != null) {
            this.zzbiu.zzs(z);
        }
    }

    @Override // com.google.android.gms.internal.zzep
    public final void zza(zzlh zzlhVar, Map<String, String> map) {
        String str;
        String str2 = map.get("a");
        if (str2 == null) {
            zzkd.zzcx("Action missing from an open GMSG.");
            return;
        }
        if (this.zzbit != null && !this.zzbit.zzel()) {
            this.zzbit.zzt(map.get("u"));
            return;
        }
        zzli zzuj = zzlhVar.zzuj();
        if ("expand".equalsIgnoreCase(str2)) {
            if (zzlhVar.zzun()) {
                zzkd.zzcx("Cannot expand WebView that is already expanded.");
                return;
            } else {
                zzr(false);
                zzuj.zza(zzc(map), zzd(map));
                return;
            }
        }
        if ("webapp".equalsIgnoreCase(str2)) {
            String str3 = map.get("u");
            zzr(false);
            if (str3 != null) {
                zzuj.zza(zzc(map), zzd(map), str3);
                return;
            } else {
                zzuj.zza(zzc(map), zzd(map), map.get("html"), map.get("baseurl"));
                return;
            }
        }
        if ("in_app_purchase".equalsIgnoreCase(str2)) {
            String str4 = map.get("product_id");
            String str5 = map.get("report_urls");
            if (this.zzbiw != null) {
                if (str5 == null || str5.isEmpty()) {
                    this.zzbiw.zza(str4, new ArrayList<>());
                    return;
                } else {
                    this.zzbiw.zza(str4, new ArrayList<>(Arrays.asList(str5.split(XMLResultsHandler.SEP_SPACE))));
                    return;
                }
            }
            return;
        }
        if (!"app".equalsIgnoreCase(str2) || !ServerProtocol.DIALOG_RETURN_SCOPES_TRUE.equalsIgnoreCase(map.get("system_browser"))) {
            zzr(true);
            String str6 = map.get("u");
            if (TextUtils.isEmpty(str6)) {
                str = str6;
            } else {
                com.google.android.gms.ads.internal.zzu.zzfq();
                str = zzkh.zza(zzlhVar, str6);
            }
            zzuj.zza(new AdLauncherIntentInfoParcel(map.get("i"), str, map.get("m"), map.get("p"), map.get("c"), map.get("f"), map.get("e")));
            return;
        }
        zzr(true);
        Context context = zzlhVar.getContext();
        if (TextUtils.isEmpty(map.get("u"))) {
            zzkd.zzcx("Destination url cannot be empty.");
            return;
        }
        try {
            zzlhVar.zzuj().zza(new AdLauncherIntentInfoParcel(new zza(zzlhVar).zza(context, map)));
        } catch (ActivityNotFoundException e) {
            zzkd.zzcx(e.getMessage());
        }
    }
}
