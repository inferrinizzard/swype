package com.google.android.gms.internal;

import android.content.Context;
import android.view.MotionEvent;

@zzin
/* loaded from: classes.dex */
public final class zzkk {
    final Context mContext;
    private int mState;
    private final float zzbre;
    public String zzcll;
    private float zzclm;
    private float zzcln;
    private float zzclo;

    public zzkk(Context context) {
        this.mState = 0;
        this.mContext = context;
        this.zzbre = context.getResources().getDisplayMetrics().density;
    }

    public zzkk(Context context, String str) {
        this(context);
        this.zzcll = str;
    }

    public final void zze(MotionEvent motionEvent) {
        int historySize = motionEvent.getHistorySize();
        for (int i = 0; i < historySize; i++) {
            zza(motionEvent.getActionMasked(), motionEvent.getHistoricalX(0, i), motionEvent.getHistoricalY(0, i));
        }
        zza(motionEvent.getActionMasked(), motionEvent.getX(), motionEvent.getY());
    }

    /* JADX WARN: Code restructure failed: missing block: B:65:0x010e, code lost:            if (android.text.TextUtils.isEmpty(r0) == false) goto L58;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void zza(int r8, float r9, float r10) {
        /*
            Method dump skipped, instructions count: 323
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzkk.zza(int, float, float):void");
    }
}
