package com.google.android.gms.internal;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public final class zzqo {
    private final Set<zzqn<?>> mb = Collections.newSetFromMap(new WeakHashMap());

    public final void release() {
        Iterator<zzqn<?>> it = this.mb.iterator();
        while (it.hasNext()) {
            it.next().mListener = null;
        }
        this.mb.clear();
    }
}
