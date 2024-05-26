package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import com.google.android.gms.internal.zzic;
import com.google.android.gms.internal.zzju;

@TargetApi(19)
@zzin
/* loaded from: classes.dex */
public final class zzie extends zzid {
    private Object zzbyj;
    private PopupWindow zzbyk;
    private boolean zzbyl;

    /* JADX INFO: Access modifiers changed from: package-private */
    public zzie(Context context, zzju.zza zzaVar, zzlh zzlhVar, zzic.zza zzaVar2) {
        super(context, zzaVar, zzlhVar, zzaVar2);
        this.zzbyj = new Object();
        this.zzbyl = false;
    }

    private void zzqe() {
        synchronized (this.zzbyj) {
            this.zzbyl = true;
            if ((this.mContext instanceof Activity) && ((Activity) this.mContext).isDestroyed()) {
                this.zzbyk = null;
            }
            if (this.zzbyk != null) {
                if (this.zzbyk.isShowing()) {
                    this.zzbyk.dismiss();
                }
                this.zzbyk = null;
            }
        }
    }

    @Override // com.google.android.gms.internal.zzhy, com.google.android.gms.internal.zzkj
    public final void cancel() {
        zzqe();
        super.cancel();
    }

    @Override // com.google.android.gms.internal.zzhy
    protected final void zzaj(int i) {
        zzqe();
        super.zzaj(i);
    }

    @Override // com.google.android.gms.internal.zzid
    protected final void zzqd() {
        Window window = this.mContext instanceof Activity ? ((Activity) this.mContext).getWindow() : null;
        if (window == null || window.getDecorView() == null || ((Activity) this.mContext).isDestroyed()) {
            return;
        }
        FrameLayout frameLayout = new FrameLayout(this.mContext);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        frameLayout.addView(this.zzbgf.getView(), -1, -1);
        synchronized (this.zzbyj) {
            if (this.zzbyl) {
                return;
            }
            this.zzbyk = new PopupWindow((View) frameLayout, 1, 1, false);
            this.zzbyk.setOutsideTouchable(true);
            this.zzbyk.setClippingEnabled(false);
            zzkd.zzcv("Displaying the 1x1 popup off the screen.");
            try {
                this.zzbyk.showAtLocation(window.getDecorView(), 0, -1, -1);
            } catch (Exception e) {
                this.zzbyk = null;
            }
        }
    }
}
