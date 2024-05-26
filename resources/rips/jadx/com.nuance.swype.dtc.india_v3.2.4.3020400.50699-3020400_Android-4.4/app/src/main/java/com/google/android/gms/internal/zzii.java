package com.google.android.gms.internal;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;
import com.google.android.gms.ads.internal.formats.zzh;
import com.google.android.gms.internal.zzfs;
import com.google.android.gms.internal.zzju;
import com.google.android.gms.internal.zzkn;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzii implements Callable<zzju> {
    private static final long zzbyt = TimeUnit.SECONDS.toMillis(60);
    private final Context mContext;
    private final Object zzail = new Object();
    private final zzih zzbgb;
    private final zzas zzbgd;
    private final zzju.zza zzbxr;
    private int zzbyi;
    private final zzkn zzbzc;
    private final com.google.android.gms.ads.internal.zzq zzbzd;
    private boolean zzbze;
    private List<String> zzbzf;
    private JSONObject zzbzg;

    /* loaded from: classes.dex */
    public interface zza<T extends zzh.zza> {
        T zza(zzii zziiVar, JSONObject jSONObject) throws JSONException, InterruptedException, ExecutionException;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class zzb {
        public zzep zzbzz;

        zzb() {
        }
    }

    private zzju zzb(zzh.zza zzaVar) {
        int i;
        synchronized (this.zzail) {
            i = this.zzbyi;
            if (zzaVar == null && this.zzbyi == -2) {
                i = 0;
            }
        }
        return new zzju(this.zzbxr.zzcip.zzcar, null, this.zzbxr.zzciq.zzbnm, i, this.zzbxr.zzciq.zzbnn, this.zzbzf, this.zzbxr.zzciq.orientation, this.zzbxr.zzciq.zzbns, this.zzbxr.zzcip.zzcau, false, null, null, null, null, null, 0L, this.zzbxr.zzapa, this.zzbxr.zzciq.zzcbx, this.zzbxr.zzcik, this.zzbxr.zzcil, this.zzbxr.zzciq.zzccd, this.zzbzg, i != -2 ? null : zzaVar, null, null, null, this.zzbxr.zzciq.zzccq, this.zzbxr.zzciq.zzccr, null, this.zzbxr.zzciq.zzbnp);
    }

    private static Integer zzb(JSONObject jSONObject, String str) {
        try {
            JSONObject jSONObject2 = jSONObject.getJSONObject(str);
            return Integer.valueOf(Color.rgb(jSONObject2.getInt("r"), jSONObject2.getInt("g"), jSONObject2.getInt("b")));
        } catch (JSONException e) {
            return null;
        }
    }

    private static String[] zzc(JSONObject jSONObject, String str) throws JSONException {
        JSONArray optJSONArray = jSONObject.optJSONArray(str);
        if (optJSONArray == null) {
            return null;
        }
        String[] strArr = new String[optJSONArray.length()];
        for (int i = 0; i < optJSONArray.length(); i++) {
            strArr[i] = optJSONArray.getString(i);
        }
        return strArr;
    }

    public final zzky<com.google.android.gms.ads.internal.formats.zzc> zza(JSONObject jSONObject, String str, boolean z, boolean z2) throws JSONException {
        JSONObject jSONObject2 = z ? jSONObject.getJSONObject(str) : jSONObject.optJSONObject(str);
        if (jSONObject2 == null) {
            jSONObject2 = new JSONObject();
        }
        return zza(jSONObject2, z, z2);
    }

    public final List<zzky<com.google.android.gms.ads.internal.formats.zzc>> zza(JSONObject jSONObject, String str, boolean z, boolean z2, boolean z3) throws JSONException {
        JSONArray jSONArray = z ? jSONObject.getJSONArray(str) : jSONObject.optJSONArray(str);
        ArrayList arrayList = new ArrayList();
        if (jSONArray == null || jSONArray.length() == 0) {
            zza(0, z);
            return arrayList;
        }
        int length = z3 ? jSONArray.length() : 1;
        for (int i = 0; i < length; i++) {
            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
            if (jSONObject2 == null) {
                jSONObject2 = new JSONObject();
            }
            arrayList.add(zza(jSONObject2, z, z2));
        }
        return arrayList;
    }

    public final void zza(int i, boolean z) {
        if (z) {
            zzan(i);
        }
    }

    public final void zzan(int i) {
        synchronized (this.zzail) {
            this.zzbze = true;
            this.zzbyi = i;
        }
    }

    public final boolean zzqs() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzbze;
        }
        return z;
    }

    public zzii(Context context, com.google.android.gms.ads.internal.zzq zzqVar, zzkn zzknVar, zzas zzasVar, zzju.zza zzaVar) {
        this.mContext = context;
        this.zzbzd = zzqVar;
        this.zzbzc = zzknVar;
        this.zzbxr = zzaVar;
        this.zzbgd = zzasVar;
        this.zzbgb = new zzih(context, zzaVar, zzqVar, zzasVar);
        final zzih zzihVar = this.zzbgb;
        if (zzihVar.zzbyz) {
            synchronized (zzih.zzamr) {
                if (!zzih.zzbyu) {
                    zzih.zzbyv = new zzfs(zzihVar.mContext.getApplicationContext() != null ? zzihVar.mContext.getApplicationContext() : zzihVar.mContext, zzihVar.zzbxr.zzcip.zzaow, zzih.zzd(zzihVar.zzbxr), new zzkl<zzfp>() { // from class: com.google.android.gms.internal.zzih.3
                        @Override // com.google.android.gms.internal.zzkl
                        public final /* synthetic */ void zzd(zzfp zzfpVar) {
                            zzfpVar.zza$279dadbc$3b0b03b9(zzih.this.zzbfx, zzih.this.zzbfx, zzih.this.zzbfx, zzih.this.zzbfx);
                        }
                    }, new zzfs.zzb());
                    zzih.zzbyu = true;
                }
            }
        } else {
            zzihVar.zzbyw = new zzfq();
        }
        this.zzbze = false;
        this.zzbyi = -2;
        this.zzbzf = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:22:0x006a  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x006f A[Catch: CancellationException -> 0x00cb, ExecutionException -> 0x0165, InterruptedException -> 0x016d, JSONException -> 0x01ae, TimeoutException -> 0x01b7, TryCatch #2 {InterruptedException -> 0x016d, CancellationException -> 0x00cb, ExecutionException -> 0x0165, TimeoutException -> 0x01b7, JSONException -> 0x01ae, blocks: (B:3:0x0002, B:5:0x0008, B:6:0x0017, B:9:0x0026, B:11:0x002c, B:13:0x003b, B:14:0x0044, B:16:0x004c, B:17:0x0055, B:19:0x005e, B:20:0x0064, B:23:0x006b, B:25:0x006f, B:26:0x0089, B:30:0x0170, B:33:0x0181, B:34:0x01a9, B:35:0x00ff, B:37:0x0108, B:38:0x0110, B:40:0x0119, B:42:0x0139, B:43:0x0141, B:45:0x0155, B:46:0x0159, B:47:0x015f, B:48:0x0168, B:52:0x00d8, B:53:0x008e), top: B:2:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0170 A[Catch: CancellationException -> 0x00cb, ExecutionException -> 0x0165, InterruptedException -> 0x016d, JSONException -> 0x01ae, TimeoutException -> 0x01b7, TryCatch #2 {InterruptedException -> 0x016d, CancellationException -> 0x00cb, ExecutionException -> 0x0165, TimeoutException -> 0x01b7, JSONException -> 0x01ae, blocks: (B:3:0x0002, B:5:0x0008, B:6:0x0017, B:9:0x0026, B:11:0x002c, B:13:0x003b, B:14:0x0044, B:16:0x004c, B:17:0x0055, B:19:0x005e, B:20:0x0064, B:23:0x006b, B:25:0x006f, B:26:0x0089, B:30:0x0170, B:33:0x0181, B:34:0x01a9, B:35:0x00ff, B:37:0x0108, B:38:0x0110, B:40:0x0119, B:42:0x0139, B:43:0x0141, B:45:0x0155, B:46:0x0159, B:47:0x015f, B:48:0x0168, B:52:0x00d8, B:53:0x008e), top: B:2:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:58:0x00d0  */
    @Override // java.util.concurrent.Callable
    /* renamed from: zzqr, reason: merged with bridge method [inline-methods] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.google.android.gms.internal.zzju call() {
        /*
            Method dump skipped, instructions count: 448
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzii.call():com.google.android.gms.internal.zzju");
    }

    /*  JADX ERROR: JadxRuntimeException in pass: ProcessVariables
        jadx.core.utils.exceptions.JadxRuntimeException: Method arg registers not loaded: com.google.android.gms.internal.zzkx.1.<init>(com.google.android.gms.internal.zzkv, com.google.android.gms.internal.zzkx$zza, com.google.android.gms.internal.zzky):void, class status: GENERATED_AND_UNLOADED
        	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:289)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables$1.isArgUnused(ProcessVariables.java:146)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables$1.lambda$isVarUnused$0(ProcessVariables.java:131)
        	at jadx.core.utils.ListUtils.allMatch(ListUtils.java:172)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables$1.isVarUnused(ProcessVariables.java:131)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables$1.processBlock(ProcessVariables.java:82)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:64)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:19)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables.removeUnusedResults(ProcessVariables.java:73)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables.visit(ProcessVariables.java:48)
        */
    public final com.google.android.gms.internal.zzky<com.google.android.gms.ads.internal.formats.zza> zzg(org.json.JSONObject r15) throws org.json.JSONException {
        /*
            r14 = this;
            r5 = 1
            r3 = 0
            java.lang.String r0 = "attribution"
            org.json.JSONObject r1 = r15.optJSONObject(r0)
            if (r1 != 0) goto L12
            com.google.android.gms.internal.zzkw r0 = new com.google.android.gms.internal.zzkw
            r1 = 0
            r0.<init>(r1)
        L11:
            return r0
        L12:
            java.lang.String r0 = "text"
            java.lang.String r9 = r1.optString(r0)
            java.lang.String r0 = "text_size"
            r2 = -1
            int r10 = r1.optInt(r0, r2)
            java.lang.String r0 = "text_color"
            java.lang.Integer r11 = zzb(r1, r0)
            java.lang.String r0 = "bg_color"
            java.lang.Integer r12 = zzb(r1, r0)
            java.lang.String r0 = "animation_ms"
            r2 = 1000(0x3e8, float:1.401E-42)
            int r7 = r1.optInt(r0, r2)
            java.lang.String r0 = "presentation_ms"
            r2 = 4000(0xfa0, float:5.605E-42)
            int r6 = r1.optInt(r0, r2)
            com.google.android.gms.internal.zzju$zza r0 = r14.zzbxr
            com.google.android.gms.ads.internal.request.AdRequestInfoParcel r0 = r0.zzcip
            com.google.android.gms.ads.internal.formats.NativeAdOptionsParcel r0 = r0.zzapo
            if (r0 == 0) goto L90
            com.google.android.gms.internal.zzju$zza r0 = r14.zzbxr
            com.google.android.gms.ads.internal.request.AdRequestInfoParcel r0 = r0.zzcip
            com.google.android.gms.ads.internal.formats.NativeAdOptionsParcel r0 = r0.zzapo
            int r0 = r0.versionCode
            r2 = 2
            if (r0 < r2) goto L90
            com.google.android.gms.internal.zzju$zza r0 = r14.zzbxr
            com.google.android.gms.ads.internal.request.AdRequestInfoParcel r0 = r0.zzcip
            com.google.android.gms.ads.internal.formats.NativeAdOptionsParcel r0 = r0.zzapo
            int r8 = r0.zzbgs
        L5c:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.String r2 = "images"
            org.json.JSONArray r2 = r1.optJSONArray(r2)
            if (r2 == 0) goto L92
            java.lang.String r2 = "images"
            r0 = r14
            r4 = r3
            java.util.List r0 = r0.zza(r1, r2, r3, r4, r5)
        L73:
            com.google.android.gms.internal.zzky r13 = com.google.android.gms.internal.zzkx.zzn(r0)
            com.google.android.gms.internal.zzii$5 r0 = new com.google.android.gms.internal.zzii$5
            r1 = r14
            r2 = r9
            r3 = r12
            r4 = r11
            r5 = r10
            r0.<init>()
            com.google.android.gms.internal.zzkv r1 = new com.google.android.gms.internal.zzkv
            r1.<init>()
            com.google.android.gms.internal.zzkx$1 r2 = new com.google.android.gms.internal.zzkx$1
            r2.<init>()
            r13.zzc(r2)
            r0 = r1
            goto L11
        L90:
            r8 = r5
            goto L5c
        L92:
            java.lang.String r2 = "image"
            com.google.android.gms.internal.zzky r1 = r14.zza(r1, r2, r3, r3)
            r0.add(r1)
            goto L73
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzii.zzg(org.json.JSONObject):com.google.android.gms.internal.zzky");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final zzky<com.google.android.gms.ads.internal.formats.zzc> zza(JSONObject jSONObject, final boolean z, boolean z2) throws JSONException {
        final String string = z ? jSONObject.getString("url") : jSONObject.optString("url");
        final double optDouble = jSONObject.optDouble("scale", 1.0d);
        if (TextUtils.isEmpty(string)) {
            zza(0, z);
            return new zzkw(null);
        }
        if (z2) {
            return new zzkw(new com.google.android.gms.ads.internal.formats.zzc(null, Uri.parse(string), optDouble));
        }
        zzkn zzknVar = this.zzbzc;
        zzkn.zza<com.google.android.gms.ads.internal.formats.zzc> zzaVar = new zzkn.zza<com.google.android.gms.ads.internal.formats.zzc>() { // from class: com.google.android.gms.internal.zzii.6
            /* JADX INFO: Access modifiers changed from: private */
            @Override // com.google.android.gms.internal.zzkn.zza
            /* renamed from: zzqt, reason: merged with bridge method [inline-methods] */
            public com.google.android.gms.ads.internal.formats.zzc zzqu() {
                zzii.this.zza(2, z);
                return null;
            }

            /* JADX INFO: Access modifiers changed from: private */
            @Override // com.google.android.gms.internal.zzkn.zza
            /* renamed from: zzg, reason: merged with bridge method [inline-methods] */
            public com.google.android.gms.ads.internal.formats.zzc zzh(InputStream inputStream) {
                byte[] bArr;
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    com.google.android.gms.common.util.zzo.zza$40f06453$1ade7318(inputStream, byteArrayOutputStream);
                    bArr = byteArrayOutputStream.toByteArray();
                } catch (IOException e) {
                    bArr = null;
                }
                if (bArr == null) {
                    zzii.this.zza(2, z);
                    return null;
                }
                Bitmap decodeByteArray = BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
                if (decodeByteArray == null) {
                    zzii.this.zza(2, z);
                    return null;
                }
                decodeByteArray.setDensity((int) (160.0d * optDouble));
                return new com.google.android.gms.ads.internal.formats.zzc(new BitmapDrawable(Resources.getSystem(), decodeByteArray), Uri.parse(string), optDouble);
            }
        };
        zzkn.zzc zzcVar = new zzkn.zzc(zzknVar, (byte) 0);
        zzkn.zzcmc.zze(new zzkn.zzb(string, zzaVar, zzcVar));
        return zzcVar;
    }

    static /* synthetic */ void zza(zzii zziiVar, zzdz zzdzVar, String str) {
        try {
            zzed zzv = zziiVar.zzbzd.zzv(zzdzVar.getCustomTemplateId());
            if (zzv != null) {
                zzv.zza(zzdzVar, str);
            }
        } catch (RemoteException e) {
            zzkd.zzd(new StringBuilder(String.valueOf(str).length() + 40).append("Failed to call onCustomClick for asset ").append(str).append(".").toString(), e);
        }
    }

    static /* synthetic */ List zzi(List list) throws RemoteException {
        ArrayList arrayList = new ArrayList();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            arrayList.add((Drawable) com.google.android.gms.dynamic.zze.zzad(((com.google.android.gms.ads.internal.formats.zzc) it.next()).zzkt()));
        }
        return arrayList;
    }
}
