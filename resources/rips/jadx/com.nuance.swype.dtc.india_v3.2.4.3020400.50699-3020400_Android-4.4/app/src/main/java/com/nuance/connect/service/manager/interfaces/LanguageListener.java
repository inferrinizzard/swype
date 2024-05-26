package com.nuance.connect.service.manager.interfaces;

import java.util.Locale;

/* loaded from: classes.dex */
public interface LanguageListener {
    void onLanguageUpdate(int[] iArr);

    void onLocaleUpdate(Locale locale);
}
