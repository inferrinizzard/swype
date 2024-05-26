package com.google.android.gms.ads.internal.formats;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import com.google.android.gms.internal.zzin;
import com.nuance.swype.input.R;
import java.util.List;

@zzin
/* loaded from: classes.dex */
public class zza {
    private static final int zzbes = Color.rgb(12, R.styleable.ThemeTemplate_btnKeyboardActionKeyPressed5rowMid, 206);
    private static final int zzbet;
    static final int zzbeu;
    static final int zzbev;
    private final int mBackgroundColor;
    private final int mTextColor;
    private final String zzbew;
    private final List<Drawable> zzbex;
    private final int zzbey;
    private final int zzbez;
    private final int zzbfa;

    static {
        int rgb = Color.rgb(204, 204, 204);
        zzbet = rgb;
        zzbeu = rgb;
        zzbev = zzbes;
    }

    public zza(String str, List<Drawable> list, Integer num, Integer num2, Integer num3, int i, int i2) {
        this.zzbew = str;
        this.zzbex = list;
        this.mBackgroundColor = num != null ? num.intValue() : zzbeu;
        this.mTextColor = num2 != null ? num2.intValue() : zzbev;
        this.zzbey = num3 != null ? num3.intValue() : 12;
        this.zzbez = i;
        this.zzbfa = i2;
    }

    public int getBackgroundColor() {
        return this.mBackgroundColor;
    }

    public String getText() {
        return this.zzbew;
    }

    public int getTextColor() {
        return this.mTextColor;
    }

    public int getTextSize() {
        return this.zzbey;
    }

    public List<Drawable> zzkp() {
        return this.zzbex;
    }

    public int zzkq() {
        return this.zzbez;
    }

    public int zzkr() {
        return this.zzbfa;
    }
}
