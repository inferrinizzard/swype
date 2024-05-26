package com.google.android.gms.internal;

import android.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import com.nuance.swype.input.InputFieldInfo;
import org.json.JSONException;
import org.json.JSONObject;

@TargetApi(14)
@zzin
/* loaded from: classes.dex */
public final class zzco extends Thread {
    private final zzcn zzask;
    public final zzcm zzasl;
    final zzim zzasm;
    private boolean mStarted = false;
    public boolean zzasj = false;
    private boolean zzbl = false;
    public final Object zzail = new Object();
    final int zzarv = ((Integer) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzazi)).intValue();
    final int zzaso = ((Integer) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzazj)).intValue();
    final int zzarx = ((Integer) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzazk)).intValue();
    final int zzasp = ((Integer) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzazl)).intValue();
    private final int zzasn = ((Integer) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzazm)).intValue();

    /* JADX INFO: Access modifiers changed from: package-private */
    @zzin
    /* loaded from: classes.dex */
    public class zza {
        final int zzasx;
        final int zzasy;

        zza(int i, int i2) {
            this.zzasx = i;
            this.zzasy = i2;
        }
    }

    private void zzic() {
        synchronized (this.zzail) {
            this.zzasj = true;
            zzkd.zzcv(new StringBuilder(42).append("ContentFetchThread: paused, mPause = ").append(this.zzasj).toString());
        }
    }

    public final void zzhz() {
        synchronized (this.zzail) {
            if (this.mStarted) {
                zzkd.zzcv("Content hash thread already started, quiting...");
            } else {
                this.mStarted = true;
                start();
            }
        }
    }

    public zzco(zzcn zzcnVar, zzcm zzcmVar, zzim zzimVar) {
        this.zzask = zzcnVar;
        this.zzasl = zzcmVar;
        this.zzasm = zzimVar;
        setName("ContentFetchTask");
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public final void run() {
        while (true) {
            try {
                if (zzia()) {
                    Activity activity = this.zzask.mActivity;
                    if (activity == null) {
                        zzkd.zzcv("ContentFetchThread: no activity. Sleeping.");
                        zzic();
                    } else if (activity != null) {
                        final View view = null;
                        try {
                            if (activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
                                view = activity.getWindow().getDecorView().findViewById(R.id.content);
                            }
                        } catch (Throwable th) {
                            zzkd.zzcv("Failed getting root view of activity. Content not extracted.");
                        }
                        if (view != null && view != null) {
                            view.post(new Runnable() { // from class: com.google.android.gms.internal.zzco.1
                                @Override // java.lang.Runnable
                                public final void run() {
                                    zzco zzcoVar = zzco.this;
                                    View view2 = view;
                                    try {
                                        zzcl zzclVar = new zzcl(zzcoVar.zzarv, zzcoVar.zzaso, zzcoVar.zzarx, zzcoVar.zzasp);
                                        zza zza2 = zzcoVar.zza(view2, zzclVar);
                                        zzclVar.zzhw();
                                        if (zza2.zzasx == 0 && zza2.zzasy == 0) {
                                            return;
                                        }
                                        if (zza2.zzasy == 0 && zzclVar.zzasb == 0) {
                                            return;
                                        }
                                        if (zza2.zzasy == 0 && zzcoVar.zzasl.zza(zzclVar)) {
                                            return;
                                        }
                                        zzcm zzcmVar = zzcoVar.zzasl;
                                        synchronized (zzcmVar.zzail) {
                                            if (zzcmVar.zzasi.size() >= 10) {
                                                zzkd.zzcv(new StringBuilder(41).append("Queue is full, current size = ").append(zzcmVar.zzasi.size()).toString());
                                                zzcmVar.zzasi.remove(0);
                                            }
                                            int i = zzcmVar.zzash;
                                            zzcmVar.zzash = i + 1;
                                            zzclVar.zzasc = i;
                                            zzcmVar.zzasi.add(zzclVar);
                                        }
                                    } catch (Exception e) {
                                        zzkd.zzb("Exception in fetchContentOnUIThread", e);
                                        zzcoVar.zzasm.zza(e, true);
                                    }
                                }
                            });
                        }
                    }
                } else {
                    zzkd.zzcv("ContentFetchTask: sleeping");
                    zzic();
                }
                Thread.sleep(this.zzasn * 1000);
            } catch (Throwable th2) {
                zzkd.zzb("Error in ContentFetchTask", th2);
                this.zzasm.zza(th2, true);
            }
            synchronized (this.zzail) {
                while (this.zzasj) {
                    try {
                        zzkd.zzcv("ContentFetchTask: waiting");
                        this.zzail.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x0046, code lost:            if (r0.importance != 100) goto L30;     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0048, code lost:            r0 = true;     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0049, code lost:            if (r0 == false) goto L33;     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x004f, code lost:            if (r1.inKeyguardRestrictedInputMode() != false) goto L33;     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0051, code lost:            r0 = (android.os.PowerManager) r4.getSystemService("power");     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x005a, code lost:            if (r0 != null) goto L31;     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x005c, code lost:            r0 = false;     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x005d, code lost:            if (r0 == false) goto L33;     */
    /* JADX WARN: Code restructure failed: missing block: B:32:?, code lost:            return true;     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x0063, code lost:            r0 = r0.isScreenOn();     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0061, code lost:            r0 = false;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean zzia() {
        /*
            r8 = this;
            r3 = 1
            r2 = 0
            com.google.android.gms.internal.zzcn r0 = r8.zzask     // Catch: java.lang.Throwable -> L6a
            android.content.Context r4 = r0.mContext     // Catch: java.lang.Throwable -> L6a
            if (r4 != 0) goto La
            r0 = r2
        L9:
            return r0
        La:
            java.lang.String r0 = "activity"
            java.lang.Object r0 = r4.getSystemService(r0)     // Catch: java.lang.Throwable -> L6a
            android.app.ActivityManager r0 = (android.app.ActivityManager) r0     // Catch: java.lang.Throwable -> L6a
            java.lang.String r1 = "keyguard"
            java.lang.Object r1 = r4.getSystemService(r1)     // Catch: java.lang.Throwable -> L6a
            android.app.KeyguardManager r1 = (android.app.KeyguardManager) r1     // Catch: java.lang.Throwable -> L6a
            if (r0 == 0) goto L20
            if (r1 != 0) goto L22
        L20:
            r0 = r2
            goto L9
        L22:
            java.util.List r0 = r0.getRunningAppProcesses()     // Catch: java.lang.Throwable -> L6a
            if (r0 != 0) goto L2a
            r0 = r2
            goto L9
        L2a:
            java.util.Iterator r5 = r0.iterator()     // Catch: java.lang.Throwable -> L6a
        L2e:
            boolean r0 = r5.hasNext()     // Catch: java.lang.Throwable -> L6a
            if (r0 == 0) goto L68
            java.lang.Object r0 = r5.next()     // Catch: java.lang.Throwable -> L6a
            android.app.ActivityManager$RunningAppProcessInfo r0 = (android.app.ActivityManager.RunningAppProcessInfo) r0     // Catch: java.lang.Throwable -> L6a
            int r6 = android.os.Process.myPid()     // Catch: java.lang.Throwable -> L6a
            int r7 = r0.pid     // Catch: java.lang.Throwable -> L6a
            if (r6 != r7) goto L2e
            int r0 = r0.importance     // Catch: java.lang.Throwable -> L6a
            r5 = 100
            if (r0 != r5) goto L61
            r0 = r3
        L49:
            if (r0 == 0) goto L68
            boolean r0 = r1.inKeyguardRestrictedInputMode()     // Catch: java.lang.Throwable -> L6a
            if (r0 != 0) goto L68
            java.lang.String r0 = "power"
            java.lang.Object r0 = r4.getSystemService(r0)     // Catch: java.lang.Throwable -> L6a
            android.os.PowerManager r0 = (android.os.PowerManager) r0     // Catch: java.lang.Throwable -> L6a
            if (r0 != 0) goto L63
            r0 = r2
        L5d:
            if (r0 == 0) goto L68
            r0 = r3
            goto L9
        L61:
            r0 = r2
            goto L49
        L63:
            boolean r0 = r0.isScreenOn()     // Catch: java.lang.Throwable -> L6a
            goto L5d
        L68:
            r0 = r2
            goto L9
        L6a:
            r0 = move-exception
            r0 = r2
            goto L9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzco.zzia():boolean");
    }

    final zza zza(View view, final zzcl zzclVar) {
        boolean z;
        if (view == null) {
            return new zza(0, 0);
        }
        final boolean globalVisibleRect = view.getGlobalVisibleRect(new Rect());
        if ((view instanceof TextView) && !(view instanceof EditText)) {
            CharSequence text = ((TextView) view).getText();
            if (TextUtils.isEmpty(text)) {
                return new zza(0, 0);
            }
            zzclVar.zzf(text.toString(), globalVisibleRect);
            return new zza(1, 0);
        }
        if ((view instanceof WebView) && !(view instanceof zzlh)) {
            zzclVar.zzhv();
            final WebView webView = (WebView) view;
            if (com.google.android.gms.common.util.zzs.zzhb(19)) {
                zzclVar.zzhv();
                webView.post(new Runnable() { // from class: com.google.android.gms.internal.zzco.2
                    ValueCallback<String> zzass = new ValueCallback<String>() { // from class: com.google.android.gms.internal.zzco.2.1
                        @Override // android.webkit.ValueCallback
                        public final /* synthetic */ void onReceiveValue(String str) {
                            String str2 = str;
                            zzco zzcoVar = zzco.this;
                            zzcl zzclVar2 = zzclVar;
                            WebView webView2 = webView;
                            boolean z2 = globalVisibleRect;
                            synchronized (zzclVar2.zzail) {
                                zzclVar2.zzasd--;
                            }
                            try {
                                if (!TextUtils.isEmpty(str2)) {
                                    String optString = new JSONObject(str2).optString(InputFieldInfo.INPUT_TYPE_TEXT);
                                    if (TextUtils.isEmpty(webView2.getTitle())) {
                                        zzclVar2.zzd(optString, z2);
                                    } else {
                                        String valueOf = String.valueOf(webView2.getTitle());
                                        zzclVar2.zzd(new StringBuilder(String.valueOf(valueOf).length() + 1 + String.valueOf(optString).length()).append(valueOf).append("\n").append(optString).toString(), z2);
                                    }
                                }
                                if (zzclVar2.zzhq()) {
                                    zzcoVar.zzasl.zzb(zzclVar2);
                                }
                            } catch (JSONException e) {
                                zzkd.zzcv("Json string may be malformed.");
                            } catch (Throwable th) {
                                zzkd.zza("Failed to get webview content.", th);
                                zzcoVar.zzasm.zza(th, true);
                            }
                        }
                    };

                    @Override // java.lang.Runnable
                    public final void run() {
                        if (webView.getSettings().getJavaScriptEnabled()) {
                            try {
                                webView.evaluateJavascript("(function() { return  {text:document.body.innerText}})();", this.zzass);
                            } catch (Throwable th) {
                                this.zzass.onReceiveValue("");
                            }
                        }
                    }
                });
                z = true;
            } else {
                z = false;
            }
            return z ? new zza(0, 1) : new zza(0, 0);
        }
        if (!(view instanceof ViewGroup)) {
            return new zza(0, 0);
        }
        ViewGroup viewGroup = (ViewGroup) view;
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < viewGroup.getChildCount(); i3++) {
            zza zza2 = zza(viewGroup.getChildAt(i3), zzclVar);
            i2 += zza2.zzasx;
            i += zza2.zzasy;
        }
        return new zza(i2, i);
    }
}
