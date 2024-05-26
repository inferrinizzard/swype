package com.nuance.swype.input;

import android.content.Context;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
class Whitelist {
    private final Set<CharSequence> strings = new HashSet();

    public static Whitelist fromWhitelist(Context context, int resIdList) {
        return new Whitelist(context, resIdList);
    }

    public static Whitelist fromBlacklist(Context context, int resIdList) {
        return new Whitelist(context, resIdList) { // from class: com.nuance.swype.input.Whitelist.1
            @Override // com.nuance.swype.input.Whitelist
            public final boolean allows(String what) {
                return !contains(what);
            }
        };
    }

    public Whitelist() {
    }

    protected Whitelist(Context context, int resIdList) {
        CharSequence[] list = context.getResources().getTextArray(resIdList);
        if (list != null) {
            for (CharSequence cs : list) {
                if (cs != null) {
                    CharSequence cs2 = cs.toString().trim();
                    if (cs2.length() > 0) {
                        this.strings.add(cs2);
                    }
                }
            }
        }
    }

    protected boolean contains(String what) {
        return this.strings.contains(what);
    }

    public boolean isEmpty() {
        return this.strings.isEmpty();
    }

    public boolean allows(String what) {
        return this.strings.isEmpty() || this.strings.contains(what);
    }
}
