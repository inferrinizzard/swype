package com.flurry.sdk;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public final class kb {
    private static kb a = null;
    private final jw<String, ko<ka<?>>> b = new jw<>();
    private final jw<ko<ka<?>>, String> c = new jw<>();

    public static synchronized kb a() {
        kb kbVar;
        synchronized (kb.class) {
            if (a == null) {
                a = new kb();
            }
            kbVar = a;
        }
        return kbVar;
    }

    private kb() {
    }

    public final synchronized void a(String str, ka<?> kaVar) {
        synchronized (this) {
            if (!TextUtils.isEmpty(str) && kaVar != null) {
                ko<ka<?>> koVar = new ko<>(kaVar);
                List<ko<ka<?>>> a2 = this.b.a((jw<String, ko<ka<?>>>) str, false);
                if (!(a2 != null ? a2.contains(koVar) : false)) {
                    this.b.a((jw<String, ko<ka<?>>>) str, (String) koVar);
                    this.c.a((jw<ko<ka<?>>, String>) koVar, (ko<ka<?>>) str);
                }
            }
        }
    }

    public final synchronized void b(String str, ka<?> kaVar) {
        if (!TextUtils.isEmpty(str)) {
            ko<ka<?>> koVar = new ko<>(kaVar);
            this.b.b(str, koVar);
            this.c.b(koVar, str);
        }
    }

    public final synchronized void a(ka<?> kaVar) {
        if (kaVar != null) {
            ko<ka<?>> koVar = new ko<>(kaVar);
            Iterator<String> it = this.c.a(koVar).iterator();
            while (it.hasNext()) {
                this.b.b(it.next(), koVar);
            }
            this.c.b(koVar);
        }
    }

    public final synchronized int b(String str) {
        return TextUtils.isEmpty(str) ? 0 : this.b.a(str).size();
    }

    private synchronized List<ka<?>> c(String str) {
        List<ka<?>> list;
        if (TextUtils.isEmpty(str)) {
            list = Collections.emptyList();
        } else {
            ArrayList arrayList = new ArrayList();
            Iterator<ko<ka<?>>> it = this.b.a(str).iterator();
            while (it.hasNext()) {
                ka kaVar = (ka) it.next().get();
                if (kaVar == null) {
                    it.remove();
                } else {
                    arrayList.add(kaVar);
                }
            }
            list = arrayList;
        }
        return list;
    }

    public final void a(final jz jzVar) {
        if (jzVar != null) {
            for (final ka<?> kaVar : c(jzVar.a())) {
                jr.a().b(new lw() { // from class: com.flurry.sdk.kb.1
                    @Override // com.flurry.sdk.lw
                    public final void a() {
                        kaVar.a(jzVar);
                    }
                });
            }
        }
    }
}
