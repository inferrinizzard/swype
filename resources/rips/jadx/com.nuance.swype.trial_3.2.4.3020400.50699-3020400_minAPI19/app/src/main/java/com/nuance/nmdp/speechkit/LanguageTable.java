package com.nuance.nmdp.speechkit;

/* loaded from: classes.dex */
interface LanguageTable {

    /* loaded from: classes.dex */
    public interface Language {
        String getAddress(int i);

        int getAddressCount();

        String getDescription();

        String getLanguage();
    }

    Language getAvailableLanguage(int i);

    int getAvailableLanguageCount();

    int getChecksum();

    String getLanguage();

    boolean isStatusSuccess();
}
