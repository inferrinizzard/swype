package com.google.android.gms.common.api;

import android.content.Context;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.internal.zzpj;
import com.google.android.gms.internal.zzqc;
import com.google.android.gms.internal.zzqo;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public abstract class zzc<O extends Api.ApiOptions> {
    public final Context mContext;
    public final int mId;
    public final Api<O> pN;
    public final zzqo rO;
    public final O rP;
    public final zzpj<O> rQ;
    public final zzqc rR;
    public final AtomicBoolean rT;
    public final AtomicInteger rU;
}
