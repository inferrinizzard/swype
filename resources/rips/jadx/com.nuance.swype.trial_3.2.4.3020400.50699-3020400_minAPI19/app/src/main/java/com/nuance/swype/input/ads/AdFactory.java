package com.nuance.swype.input.ads;

import android.content.Context;
import com.nuance.swype.input.ads.admob.AdmobProvider;

/* loaded from: classes.dex */
public class AdFactory {
    public static AdProvider createAdForCandidatesView(Context context) {
        return new AdmobProvider(context);
    }
}
