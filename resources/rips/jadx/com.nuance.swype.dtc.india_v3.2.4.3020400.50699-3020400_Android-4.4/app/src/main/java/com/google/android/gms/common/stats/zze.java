package com.google.android.gms.common.stats;

import android.support.v4.util.SimpleArrayMap;
import com.nuance.connect.util.TimeConversion;

/* loaded from: classes.dex */
public final class zze {
    private final long AH;
    private final int AI;
    private final SimpleArrayMap<String, Long> AJ;

    public zze() {
        this.AH = TimeConversion.MILLIS_IN_MINUTE;
        this.AI = 10;
        this.AJ = new SimpleArrayMap<>(10);
    }

    public zze(long j) {
        this.AH = j;
        this.AI = 1024;
        this.AJ = new SimpleArrayMap<>();
    }
}
