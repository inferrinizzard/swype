package com.nuance.swype.util.storage;

/* loaded from: classes.dex */
public final class ThemeDataManager {
    private static final ThemeDataManager manager = new ThemeDataManager();
    private final ThemeData themeData = new ThemeData();

    private ThemeDataManager() {
    }

    public static ThemeDataManager getInstance() {
        return manager;
    }
}
