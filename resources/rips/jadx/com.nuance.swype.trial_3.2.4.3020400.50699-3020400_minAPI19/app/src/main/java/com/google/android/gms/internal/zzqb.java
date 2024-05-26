package com.google.android.gms.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes.dex */
public abstract class zzqb {
    private static final ExecutorService uE = Executors.newFixedThreadPool(2, new zzrm("GAC_Executor"));

    public static ExecutorService zzaqc() {
        return uE;
    }
}
