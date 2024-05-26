package com.google.android.gms.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@zzin
/* loaded from: classes.dex */
public final class zzkx {

    /* loaded from: classes.dex */
    public interface zza<D, R> {
        R apply(D d);
    }

    public static <V> zzky<List<V>> zzn(final List<zzky<V>> list) {
        final zzkv zzkvVar = new zzkv();
        final int size = list.size();
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        Iterator<zzky<V>> it = list.iterator();
        while (it.hasNext()) {
            it.next().zzc(new Runnable() { // from class: com.google.android.gms.internal.zzkx.2
                @Override // java.lang.Runnable
                public final void run() {
                    if (atomicInteger.incrementAndGet() >= size) {
                        try {
                            zzkv zzkvVar2 = zzkvVar;
                            List list2 = list;
                            ArrayList arrayList = new ArrayList();
                            Iterator it2 = list2.iterator();
                            while (it2.hasNext()) {
                                A a = ((zzky) it2.next()).get();
                                if (a != 0) {
                                    arrayList.add(a);
                                }
                            }
                            zzkvVar2.zzh(arrayList);
                        } catch (InterruptedException | ExecutionException e) {
                            zzkd.zzd("Unable to convert list of futures to a future of list", e);
                        }
                    }
                }
            });
        }
        return zzkvVar;
    }
}
