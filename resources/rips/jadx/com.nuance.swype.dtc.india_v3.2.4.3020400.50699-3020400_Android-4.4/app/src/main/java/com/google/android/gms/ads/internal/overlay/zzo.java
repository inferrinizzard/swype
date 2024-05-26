package com.google.android.gms.ads.internal.overlay;

import android.R;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public class zzo extends FrameLayout implements View.OnClickListener {
    private final ImageButton zzbug;
    private final zzu zzbuh;

    public zzo(Context context, int i, zzu zzuVar) {
        super(context);
        this.zzbuh = zzuVar;
        setOnClickListener(this);
        this.zzbug = new ImageButton(context);
        this.zzbug.setImageResource(R.drawable.btn_dialog);
        this.zzbug.setBackgroundColor(0);
        this.zzbug.setOnClickListener(this);
        this.zzbug.setPadding(0, 0, 0, 0);
        this.zzbug.setContentDescription("Interstitial close button");
        int zza = com.google.android.gms.ads.internal.client.zzm.zziw().zza(context, i);
        addView(this.zzbug, new FrameLayout.LayoutParams(zza, zza, 17));
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.zzbuh != null) {
            this.zzbuh.zznv();
        }
    }

    public void zza(boolean z, boolean z2) {
        if (!z2) {
            this.zzbug.setVisibility(0);
        } else if (z) {
            this.zzbug.setVisibility(4);
        } else {
            this.zzbug.setVisibility(8);
        }
    }
}
