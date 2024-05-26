package com.google.android.gms.internal;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import com.facebook.internal.ServerProtocol;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public abstract class zzcd implements ViewTreeObserver.OnGlobalLayoutListener, ViewTreeObserver.OnScrollChangedListener {
    private final WeakReference<zzju> zzaqh;
    private final zzck zzaqj;
    protected final zzcf zzaqk;
    private final Context zzaql;
    private final WindowManager zzaqm;
    private final PowerManager zzaqn;
    private final KeyguardManager zzaqo;
    private zzch zzaqp;
    private boolean zzaqq;
    private boolean zzaqt;
    BroadcastReceiver zzaqv;
    protected final Object zzail = new Object();
    private boolean zzane = false;
    private boolean zzaqr = false;
    final HashSet<zzce> zzaqw = new HashSet<>();
    private final zzep zzaqx = new zzep() { // from class: com.google.android.gms.internal.zzcd.2
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            if (zzcd.this.zzb(map)) {
                zzcd.this.zza$6e25d58();
            }
        }
    };
    private final zzep zzaqy = new zzep() { // from class: com.google.android.gms.internal.zzcd.3
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            if (zzcd.this.zzb(map)) {
                String valueOf = String.valueOf(zzcd.this.zzaqk.zzari);
                zzkd.zzcv(valueOf.length() != 0 ? "Received request to untrack: ".concat(valueOf) : new String("Received request to untrack: "));
                zzcd.this.destroy();
            }
        }
    };
    private final zzep zzaqz = new zzep() { // from class: com.google.android.gms.internal.zzcd.4
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            if (zzcd.this.zzb(map) && map.containsKey("isVisible")) {
                Boolean valueOf = Boolean.valueOf("1".equals(map.get("isVisible")) || ServerProtocol.DIALOG_RETURN_SCOPES_TRUE.equals(map.get("isVisible")));
                zzcd zzcdVar = zzcd.this;
                boolean booleanValue = valueOf.booleanValue();
                Iterator<zzce> it = zzcdVar.zzaqw.iterator();
                while (it.hasNext()) {
                    it.next().zza(zzcdVar, booleanValue);
                }
            }
        }
    };
    private WeakReference<ViewTreeObserver> zzaqi = new WeakReference<>(null);
    private boolean zzaqs = true;
    private boolean zzaqu = false;
    private zzkr zzaqb = new zzkr(200);

    /* loaded from: classes.dex */
    public static class zza implements zzck {
        private WeakReference<com.google.android.gms.ads.internal.formats.zzh> zzarb;

        public zza(com.google.android.gms.ads.internal.formats.zzh zzhVar) {
            this.zzarb = new WeakReference<>(zzhVar);
        }

        @Override // com.google.android.gms.internal.zzck
        public final View zzhh() {
            com.google.android.gms.ads.internal.formats.zzh zzhVar = this.zzarb.get();
            if (zzhVar != null) {
                return zzhVar.zzlc();
            }
            return null;
        }

        @Override // com.google.android.gms.internal.zzck
        public final boolean zzhi() {
            return this.zzarb.get() == null;
        }

        @Override // com.google.android.gms.internal.zzck
        public final zzck zzhj() {
            return new zzb(this.zzarb.get());
        }
    }

    /* loaded from: classes.dex */
    public static class zzb implements zzck {
        private com.google.android.gms.ads.internal.formats.zzh zzarc;

        public zzb(com.google.android.gms.ads.internal.formats.zzh zzhVar) {
            this.zzarc = zzhVar;
        }

        @Override // com.google.android.gms.internal.zzck
        public final View zzhh() {
            return this.zzarc.zzlc();
        }

        @Override // com.google.android.gms.internal.zzck
        public final boolean zzhi() {
            return this.zzarc == null;
        }

        @Override // com.google.android.gms.internal.zzck
        public final zzck zzhj() {
            return this;
        }
    }

    /* loaded from: classes.dex */
    public static class zzc implements zzck {
        private final View mView;
        private final zzju zzard;

        public zzc(View view, zzju zzjuVar) {
            this.mView = view;
            this.zzard = zzjuVar;
        }

        @Override // com.google.android.gms.internal.zzck
        public final View zzhh() {
            return this.mView;
        }

        @Override // com.google.android.gms.internal.zzck
        public final boolean zzhi() {
            return this.zzard == null || this.mView == null;
        }

        @Override // com.google.android.gms.internal.zzck
        public final zzck zzhj() {
            return this;
        }
    }

    /* loaded from: classes.dex */
    public static class zzd implements zzck {
        private final WeakReference<View> zzare;
        private final WeakReference<zzju> zzarf;

        public zzd(View view, zzju zzjuVar) {
            this.zzare = new WeakReference<>(view);
            this.zzarf = new WeakReference<>(zzjuVar);
        }

        @Override // com.google.android.gms.internal.zzck
        public final View zzhh() {
            return this.zzare.get();
        }

        @Override // com.google.android.gms.internal.zzck
        public final boolean zzhi() {
            return this.zzare.get() == null || this.zzarf.get() == null;
        }

        @Override // com.google.android.gms.internal.zzck
        public final zzck zzhj() {
            return new zzc(this.zzare.get(), this.zzarf.get());
        }
    }

    public zzcd(Context context, AdSizeParcel adSizeParcel, zzju zzjuVar, VersionInfoParcel versionInfoParcel, zzck zzckVar) {
        this.zzaqh = new WeakReference<>(zzjuVar);
        this.zzaqj = zzckVar;
        this.zzaqk = new zzcf(UUID.randomUUID().toString(), versionInfoParcel, adSizeParcel.zzaur, zzjuVar.zzcie, zzjuVar.zzho(), adSizeParcel.zzauu);
        this.zzaqm = (WindowManager) context.getSystemService("window");
        this.zzaqn = (PowerManager) context.getApplicationContext().getSystemService("power");
        this.zzaqo = (KeyguardManager) context.getSystemService("keyguard");
        this.zzaql = context;
    }

    private static int zza(int i, DisplayMetrics displayMetrics) {
        return (int) (i / displayMetrics.density);
    }

    private void zzgz() {
        if (this.zzaqp != null) {
            this.zzaqp.zza(this);
        }
    }

    private void zzhc() {
        ViewTreeObserver viewTreeObserver = this.zzaqi.get();
        if (viewTreeObserver == null || !viewTreeObserver.isAlive()) {
            return;
        }
        viewTreeObserver.removeOnScrollChangedListener(this);
        viewTreeObserver.removeGlobalOnLayoutListener(this);
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        zzk(2);
    }

    @Override // android.view.ViewTreeObserver.OnScrollChangedListener
    public void onScrollChanged() {
        zzk(1);
    }

    public final void pause() {
        synchronized (this.zzail) {
            this.zzane = true;
            zzk(3);
        }
    }

    public final void resume() {
        synchronized (this.zzail) {
            this.zzane = false;
            zzk(3);
        }
    }

    public final void stop() {
        synchronized (this.zzail) {
            this.zzaqr = true;
            zzk(3);
        }
    }

    public final void zza(zzce zzceVar) {
        this.zzaqw.add(zzceVar);
    }

    public final void zza(zzch zzchVar) {
        synchronized (this.zzail) {
            this.zzaqp = zzchVar;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void zza(JSONObject jSONObject) {
        try {
            JSONArray jSONArray = new JSONArray();
            JSONObject jSONObject2 = new JSONObject();
            jSONArray.put(jSONObject);
            jSONObject2.put("units", jSONArray);
            zzb(jSONObject2);
        } catch (Throwable th) {
            zzkd.zzb("Skipping active view message.", th);
        }
    }

    protected final void zza$6e25d58() {
        zzk(3);
    }

    protected abstract void zzb(JSONObject jSONObject);

    /* JADX INFO: Access modifiers changed from: protected */
    public final void zzc(zzft zzftVar) {
        zzftVar.zza("/updateActiveView", this.zzaqx);
        zzftVar.zza("/untrackActiveViewUnit", this.zzaqy);
        zzftVar.zza("/visibilityChanged", this.zzaqz);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void zzd(zzft zzftVar) {
        zzftVar.zzb("/visibilityChanged", this.zzaqz);
        zzftVar.zzb("/untrackActiveViewUnit", this.zzaqy);
        zzftVar.zzb("/updateActiveView", this.zzaqx);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void zzgw() {
        synchronized (this.zzail) {
            if (this.zzaqv != null) {
                return;
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            this.zzaqv = new BroadcastReceiver() { // from class: com.google.android.gms.internal.zzcd.1
                @Override // android.content.BroadcastReceiver
                public final void onReceive(Context context, Intent intent) {
                    zzcd.this.zzk(3);
                }
            };
            this.zzaql.registerReceiver(this.zzaqv, intentFilter);
        }
    }

    public final boolean zzha() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzaqs;
        }
        return z;
    }

    protected abstract boolean zzhe();

    /* JADX INFO: Access modifiers changed from: protected */
    public void destroy() {
        synchronized (this.zzail) {
            zzhc();
            synchronized (this.zzail) {
                if (this.zzaqv != null) {
                    try {
                        this.zzaql.unregisterReceiver(this.zzaqv);
                    } catch (IllegalStateException e) {
                        zzkd.zzb("Failed trying to unregister the receiver", e);
                    } catch (Exception e2) {
                        com.google.android.gms.ads.internal.zzu.zzft().zzb((Throwable) e2, true);
                    }
                    this.zzaqv = null;
                }
            }
            this.zzaqs = false;
            zzgz();
        }
    }

    public void zzgy() {
        synchronized (this.zzail) {
            if (this.zzaqs) {
                this.zzaqt = true;
                try {
                    JSONObject zzhd = zzhd();
                    zzhd.put("doneReasonCode", "u");
                    zza(zzhd);
                } catch (RuntimeException e) {
                    zzkd.zzb("Failure while processing active view data.", e);
                } catch (JSONException e2) {
                    zzkd.zzb("JSON failure while processing active view data.", e2);
                }
                String valueOf = String.valueOf(this.zzaqk.zzari);
                zzkd.zzcv(valueOf.length() != 0 ? "Untracking ad unit: ".concat(valueOf) : new String("Untracking ad unit: "));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Removed duplicated region for block: B:17:0x003d A[Catch: all -> 0x0042, TryCatch #1 {, blocks: (B:4:0x0005, B:6:0x000b, B:8:0x0011, B:10:0x0019, B:12:0x0026, B:15:0x0033, B:17:0x003d, B:18:0x0040, B:25:0x004c, B:27:0x0054, B:29:0x0058, B:33:0x005c, B:36:0x0062, B:39:0x0064, B:40:0x006b, B:42:0x0077, B:44:0x0085, B:47:0x008e, B:49:0x009d, B:50:0x0094, B:51:0x00a4, B:52:0x00a7, B:57:0x00ab, B:59:0x000f), top: B:3:0x0005 }] */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0047  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void zzk(int r7) {
        /*
            r6 = this;
            r0 = 0
            r1 = 1
            java.lang.Object r3 = r6.zzail
            monitor-enter(r3)
            boolean r2 = r6.zzhe()     // Catch: java.lang.Throwable -> L42
            if (r2 == 0) goto Lf
            boolean r2 = r6.zzaqs     // Catch: java.lang.Throwable -> L42
            if (r2 != 0) goto L11
        Lf:
            monitor-exit(r3)     // Catch: java.lang.Throwable -> L42
        L10:
            return
        L11:
            com.google.android.gms.internal.zzck r2 = r6.zzaqj     // Catch: java.lang.Throwable -> L42
            android.view.View r4 = r2.zzhh()     // Catch: java.lang.Throwable -> L42
            if (r4 == 0) goto L45
            com.google.android.gms.ads.internal.zzu.zzfq()     // Catch: java.lang.Throwable -> L42
            android.os.PowerManager r2 = r6.zzaqn     // Catch: java.lang.Throwable -> L42
            android.app.KeyguardManager r5 = r6.zzaqo     // Catch: java.lang.Throwable -> L42
            boolean r2 = com.google.android.gms.internal.zzkh.zza(r4, r2, r5)     // Catch: java.lang.Throwable -> L42
            if (r2 == 0) goto L45
            android.graphics.Rect r2 = new android.graphics.Rect     // Catch: java.lang.Throwable -> L42
            r2.<init>()     // Catch: java.lang.Throwable -> L42
            r5 = 0
            boolean r2 = r4.getGlobalVisibleRect(r2, r5)     // Catch: java.lang.Throwable -> L42
            if (r2 == 0) goto L45
            r2 = r1
        L33:
            r6.zzaqu = r2     // Catch: java.lang.Throwable -> L42
            com.google.android.gms.internal.zzck r5 = r6.zzaqj     // Catch: java.lang.Throwable -> L42
            boolean r5 = r5.zzhi()     // Catch: java.lang.Throwable -> L42
            if (r5 == 0) goto L47
            r6.zzgy()     // Catch: java.lang.Throwable -> L42
            monitor-exit(r3)     // Catch: java.lang.Throwable -> L42
            goto L10
        L42:
            r0 = move-exception
            monitor-exit(r3)     // Catch: java.lang.Throwable -> L42
            throw r0
        L45:
            r2 = r0
            goto L33
        L47:
            if (r7 != r1) goto L4a
            r0 = r1
        L4a:
            if (r0 == 0) goto L5a
            com.google.android.gms.internal.zzkr r0 = r6.zzaqb     // Catch: java.lang.Throwable -> L42
            boolean r0 = r0.tryAcquire()     // Catch: java.lang.Throwable -> L42
            if (r0 != 0) goto L5a
            boolean r0 = r6.zzaqu     // Catch: java.lang.Throwable -> L42
            if (r2 != r0) goto L5a
            monitor-exit(r3)     // Catch: java.lang.Throwable -> L42
            goto L10
        L5a:
            if (r2 != 0) goto L64
            boolean r0 = r6.zzaqu     // Catch: java.lang.Throwable -> L42
            if (r0 != 0) goto L64
            if (r7 != r1) goto L64
            monitor-exit(r3)     // Catch: java.lang.Throwable -> L42
            goto L10
        L64:
            org.json.JSONObject r0 = r6.zzd(r4)     // Catch: java.lang.Throwable -> L42 java.lang.RuntimeException -> Laa org.json.JSONException -> Lb2
            r6.zza(r0)     // Catch: java.lang.Throwable -> L42 java.lang.RuntimeException -> Laa org.json.JSONException -> Lb2
        L6b:
            com.google.android.gms.internal.zzck r0 = r6.zzaqj     // Catch: java.lang.Throwable -> L42
            com.google.android.gms.internal.zzck r0 = r0.zzhj()     // Catch: java.lang.Throwable -> L42
            android.view.View r1 = r0.zzhh()     // Catch: java.lang.Throwable -> L42
            if (r1 == 0) goto La4
            java.lang.ref.WeakReference<android.view.ViewTreeObserver> r0 = r6.zzaqi     // Catch: java.lang.Throwable -> L42
            java.lang.Object r0 = r0.get()     // Catch: java.lang.Throwable -> L42
            android.view.ViewTreeObserver r0 = (android.view.ViewTreeObserver) r0     // Catch: java.lang.Throwable -> L42
            android.view.ViewTreeObserver r1 = r1.getViewTreeObserver()     // Catch: java.lang.Throwable -> L42
            if (r1 == r0) goto La4
            r6.zzhc()     // Catch: java.lang.Throwable -> L42
            boolean r2 = r6.zzaqq     // Catch: java.lang.Throwable -> L42
            if (r2 == 0) goto L94
            if (r0 == 0) goto L9d
            boolean r0 = r0.isAlive()     // Catch: java.lang.Throwable -> L42
            if (r0 == 0) goto L9d
        L94:
            r0 = 1
            r6.zzaqq = r0     // Catch: java.lang.Throwable -> L42
            r1.addOnScrollChangedListener(r6)     // Catch: java.lang.Throwable -> L42
            r1.addOnGlobalLayoutListener(r6)     // Catch: java.lang.Throwable -> L42
        L9d:
            java.lang.ref.WeakReference r0 = new java.lang.ref.WeakReference     // Catch: java.lang.Throwable -> L42
            r0.<init>(r1)     // Catch: java.lang.Throwable -> L42
            r6.zzaqi = r0     // Catch: java.lang.Throwable -> L42
        La4:
            r6.zzgz()     // Catch: java.lang.Throwable -> L42
            monitor-exit(r3)     // Catch: java.lang.Throwable -> L42
            goto L10
        Laa:
            r0 = move-exception
        Lab:
            java.lang.String r1 = "Active view update failed."
            com.google.android.gms.internal.zzkd.zza(r1, r0)     // Catch: java.lang.Throwable -> L42
            goto L6b
        Lb2:
            r0 = move-exception
            goto Lab
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzcd.zzk(int):void");
    }

    protected final boolean zzb(Map<String, String> map) {
        if (map == null) {
            return false;
        }
        String str = map.get("hashCode");
        return !TextUtils.isEmpty(str) && str.equals(this.zzaqk.zzari);
    }

    private JSONObject zzhd() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("afmaVersion", this.zzaqk.zzarj).put("activeViewJSON", this.zzaqk.zzarh).put("timestamp", com.google.android.gms.ads.internal.zzu.zzfu().elapsedRealtime()).put("adFormat", this.zzaqk.zzarg).put("hashCode", this.zzaqk.zzari).put("isMraid", this.zzaqk.zzark).put("isStopped", this.zzaqr).put("isPaused", this.zzane).put("isScreenOn", this.zzaqn.isScreenOn()).put("isNative", this.zzaqk.zzarl);
        return jSONObject;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final JSONObject zzd(View view) throws JSONException {
        if (view == null) {
            return zzhd().put("isAttachedToWindow", false).put("isScreenOn", this.zzaqn.isScreenOn()).put("isVisible", false);
        }
        boolean isAttachedToWindow = com.google.android.gms.ads.internal.zzu.zzfs().isAttachedToWindow(view);
        int[] iArr = new int[2];
        int[] iArr2 = new int[2];
        try {
            view.getLocationOnScreen(iArr);
            view.getLocationInWindow(iArr2);
        } catch (Exception e) {
            zzkd.zzb("Failure getting view location.", e);
        }
        DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
        Rect rect = new Rect();
        rect.left = iArr[0];
        rect.top = iArr[1];
        rect.right = rect.left + view.getWidth();
        rect.bottom = rect.top + view.getHeight();
        Rect rect2 = new Rect();
        rect2.right = this.zzaqm.getDefaultDisplay().getWidth();
        rect2.bottom = this.zzaqm.getDefaultDisplay().getHeight();
        Rect rect3 = new Rect();
        boolean globalVisibleRect = view.getGlobalVisibleRect(rect3, null);
        Rect rect4 = new Rect();
        boolean localVisibleRect = view.getLocalVisibleRect(rect4);
        Rect rect5 = new Rect();
        view.getHitRect(rect5);
        JSONObject zzhd = zzhd();
        JSONObject put = zzhd.put("windowVisibility", view.getWindowVisibility()).put("isAttachedToWindow", isAttachedToWindow).put("viewBox", new JSONObject().put("top", zza(rect2.top, displayMetrics)).put("bottom", zza(rect2.bottom, displayMetrics)).put("left", zza(rect2.left, displayMetrics)).put("right", zza(rect2.right, displayMetrics))).put("adBox", new JSONObject().put("top", zza(rect.top, displayMetrics)).put("bottom", zza(rect.bottom, displayMetrics)).put("left", zza(rect.left, displayMetrics)).put("right", zza(rect.right, displayMetrics))).put("globalVisibleBox", new JSONObject().put("top", zza(rect3.top, displayMetrics)).put("bottom", zza(rect3.bottom, displayMetrics)).put("left", zza(rect3.left, displayMetrics)).put("right", zza(rect3.right, displayMetrics))).put("globalVisibleBoxVisible", globalVisibleRect).put("localVisibleBox", new JSONObject().put("top", zza(rect4.top, displayMetrics)).put("bottom", zza(rect4.bottom, displayMetrics)).put("left", zza(rect4.left, displayMetrics)).put("right", zza(rect4.right, displayMetrics))).put("localVisibleBoxVisible", localVisibleRect).put("hitBox", new JSONObject().put("top", zza(rect5.top, displayMetrics)).put("bottom", zza(rect5.bottom, displayMetrics)).put("left", zza(rect5.left, displayMetrics)).put("right", zza(rect5.right, displayMetrics))).put("screenDensity", displayMetrics.density);
        com.google.android.gms.ads.internal.zzu.zzfq();
        put.put("isVisible", zzkh.zza(view, this.zzaqn, this.zzaqo));
        return zzhd;
    }
}
