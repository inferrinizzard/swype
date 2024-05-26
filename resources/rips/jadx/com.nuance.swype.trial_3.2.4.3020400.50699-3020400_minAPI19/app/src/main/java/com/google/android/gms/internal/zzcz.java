package com.google.android.gms.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@zzin
/* loaded from: classes.dex */
public final class zzcz {
    final Collection<zzcy> zzaxr = new ArrayList();
    final Collection<zzcy<String>> zzaxs = new ArrayList();
    final Collection<zzcy<String>> zzaxt = new ArrayList();

    public final List<String> zzjx() {
        ArrayList arrayList = new ArrayList();
        Iterator<zzcy<String>> it = this.zzaxs.iterator();
        while (it.hasNext()) {
            String str = (String) com.google.android.gms.ads.internal.zzu.zzfz().zzd(it.next());
            if (str != null) {
                arrayList.add(str);
            }
        }
        return arrayList;
    }
}
