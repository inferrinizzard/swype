package com.google.android.gms.ads.internal.formats;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.ads.internal.client.zzm;
import com.google.android.gms.ads.internal.zzu;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.internal.zzin;
import java.util.Iterator;
import java.util.List;

@zzin
/* loaded from: classes.dex */
final class zzb extends RelativeLayout {
    private static final float[] zzbfb = {5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f};
    final RelativeLayout zzbfc;
    private AnimationDrawable zzbfd;

    public zzb(Context context, zza zzaVar) {
        super(context);
        zzab.zzy(zzaVar);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        switch (zzaVar.zzkr()) {
            case 0:
                layoutParams.addRule(10);
                layoutParams.addRule(9);
                break;
            case 1:
            default:
                layoutParams.addRule(10);
                layoutParams.addRule(11);
                break;
            case 2:
                layoutParams.addRule(12);
                layoutParams.addRule(11);
                break;
            case 3:
                layoutParams.addRule(12);
                layoutParams.addRule(9);
                break;
        }
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(zzbfb, null, null));
        shapeDrawable.getPaint().setColor(zzaVar.getBackgroundColor());
        this.zzbfc = new RelativeLayout(context);
        this.zzbfc.setLayoutParams(layoutParams);
        zzu.zzfs().zza(this.zzbfc, shapeDrawable);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
        if (!TextUtils.isEmpty(zzaVar.getText())) {
            RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(-2, -2);
            TextView textView = new TextView(context);
            textView.setLayoutParams(layoutParams3);
            textView.setId(1195835393);
            textView.setTypeface(Typeface.DEFAULT);
            textView.setText(zzaVar.getText());
            textView.setTextColor(zzaVar.getTextColor());
            textView.setTextSize(zzaVar.getTextSize());
            textView.setPadding(zzm.zziw().zza(context, 4), 0, zzm.zziw().zza(context, 4), 0);
            this.zzbfc.addView(textView);
            layoutParams2.addRule(1, textView.getId());
        }
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(layoutParams2);
        imageView.setId(1195835394);
        List<Drawable> zzkp = zzaVar.zzkp();
        if (zzkp.size() > 1) {
            this.zzbfd = new AnimationDrawable();
            Iterator<Drawable> it = zzkp.iterator();
            while (it.hasNext()) {
                this.zzbfd.addFrame(it.next(), zzaVar.zzkq());
            }
            zzu.zzfs().zza(imageView, this.zzbfd);
        } else if (zzkp.size() == 1) {
            imageView.setImageDrawable(zzkp.get(0));
        }
        this.zzbfc.addView(imageView);
        addView(this.zzbfc);
    }

    @Override // android.view.ViewGroup, android.view.View
    public final void onAttachedToWindow() {
        if (this.zzbfd != null) {
            this.zzbfd.start();
        }
        super.onAttachedToWindow();
    }
}
