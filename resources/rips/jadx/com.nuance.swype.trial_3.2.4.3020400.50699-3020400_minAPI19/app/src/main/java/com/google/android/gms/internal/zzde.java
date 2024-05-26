package com.google.android.gms.internal;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import com.facebook.internal.NativeProtocol;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@zzin
/* loaded from: classes.dex */
public final class zzde {
    final Context mContext;
    final String zzarj;
    String zzbdp;
    BlockingQueue<zzdk> zzbdr;
    ExecutorService zzbds;
    public LinkedHashMap<String, String> zzbdt = new LinkedHashMap<>();
    Map<String, zzdh> zzbdu = new HashMap();
    AtomicBoolean zzbdv = new AtomicBoolean(false);
    File zzbdw;

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Map<String, String> zza(Map<String, String> map, Map<String, String> map2) {
        LinkedHashMap linkedHashMap = new LinkedHashMap(map);
        if (map2 == null) {
            return linkedHashMap;
        }
        for (Map.Entry<String, String> entry : map2.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            linkedHashMap.put(key, zzaq(key).zzg((String) linkedHashMap.get(key), value));
        }
        return linkedHashMap;
    }

    public final boolean zza(zzdk zzdkVar) {
        return this.zzbdr.offer(zzdkVar);
    }

    public final zzdh zzaq(String str) {
        zzdh zzdhVar = this.zzbdu.get(str);
        return zzdhVar != null ? zzdhVar : zzdh.zzbdy;
    }

    public zzde(Context context, String str, String str2, Map<String, String> map) {
        File externalStorageDirectory;
        this.mContext = context;
        this.zzarj = str;
        this.zzbdp = str2;
        this.zzbdv.set(((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzazg)).booleanValue());
        if (this.zzbdv.get() && (externalStorageDirectory = Environment.getExternalStorageDirectory()) != null) {
            this.zzbdw = new File(externalStorageDirectory, "sdk_csi_data.txt");
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            this.zzbdt.put(entry.getKey(), entry.getValue());
        }
        this.zzbdr = new ArrayBlockingQueue(30);
        this.zzbds = Executors.newSingleThreadExecutor();
        this.zzbds.execute(new Runnable() { // from class: com.google.android.gms.internal.zzde.1
            @Override // java.lang.Runnable
            public final void run() {
                FileOutputStream fileOutputStream;
                zzde zzdeVar = zzde.this;
                while (true) {
                    try {
                        zzdk take = zzdeVar.zzbdr.take();
                        String zzki = take.zzki();
                        if (!TextUtils.isEmpty(zzki)) {
                            Map<String, String> zza = zzdeVar.zza(zzdeVar.zzbdt, take.zzm());
                            Uri.Builder buildUpon = Uri.parse(zzdeVar.zzbdp).buildUpon();
                            for (Map.Entry<String, String> entry2 : zza.entrySet()) {
                                buildUpon.appendQueryParameter(entry2.getKey(), entry2.getValue());
                            }
                            StringBuilder sb = new StringBuilder(buildUpon.build().toString());
                            sb.append("&it=").append(zzki);
                            String sb2 = sb.toString();
                            if (!zzdeVar.zzbdv.get()) {
                                com.google.android.gms.ads.internal.zzu.zzfq();
                                zzkh.zzc(zzdeVar.mContext, zzdeVar.zzarj, sb2);
                            } else {
                                File file = zzdeVar.zzbdw;
                                if (file != null) {
                                    try {
                                        fileOutputStream = new FileOutputStream(file, true);
                                    } catch (IOException e) {
                                        e = e;
                                        fileOutputStream = null;
                                    } catch (Throwable th) {
                                        th = th;
                                        fileOutputStream = null;
                                    }
                                    try {
                                        try {
                                            fileOutputStream.write(sb2.getBytes());
                                            fileOutputStream.write(10);
                                            try {
                                                fileOutputStream.close();
                                            } catch (IOException e2) {
                                                zzkd.zzd("CsiReporter: Cannot close file: sdk_csi_data.txt.", e2);
                                            }
                                        } catch (Throwable th2) {
                                            th = th2;
                                            if (fileOutputStream != null) {
                                                try {
                                                    fileOutputStream.close();
                                                } catch (IOException e3) {
                                                    zzkd.zzd("CsiReporter: Cannot close file: sdk_csi_data.txt.", e3);
                                                }
                                            }
                                            throw th;
                                        }
                                    } catch (IOException e4) {
                                        e = e4;
                                        zzkd.zzd("CsiReporter: Cannot write to file: sdk_csi_data.txt.", e);
                                        if (fileOutputStream != null) {
                                            try {
                                                fileOutputStream.close();
                                            } catch (IOException e5) {
                                                zzkd.zzd("CsiReporter: Cannot close file: sdk_csi_data.txt.", e5);
                                            }
                                        }
                                    }
                                } else {
                                    zzkd.zzcx("CsiReporter: File doesn't exists. Cannot write CSI data to file.");
                                }
                            }
                        }
                    } catch (InterruptedException e6) {
                        zzkd.zzd("CsiReporter:reporter interrupted", e6);
                        return;
                    }
                }
            }
        });
        this.zzbdu.put(NativeProtocol.WEB_DIALOG_ACTION, zzdh.zzbdz);
        this.zzbdu.put("ad_format", zzdh.zzbdz);
        this.zzbdu.put("e", zzdh.zzbea);
    }
}
