package com.nuance.swype.plugin;

import com.nuance.swype.util.LogManager;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ThemeLayoutAttributeParser {
    static final LogManager.Log log = LogManager.getLog("ThemeLayoutAttributeParser");
    final ThemeLayoutInflateFactory mLayoutInflateFactory = new ThemeLayoutInflateFactory();

    /* loaded from: classes.dex */
    public static class ThemeStyledItem {
        int id;
        ArrayList<ThemeAttrAssociation> mThemeAttrs = new ArrayList<>();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void clear() {
        this.mLayoutInflateFactory.mThemeItems.clear();
    }
}
