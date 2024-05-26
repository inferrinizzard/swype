package com.bumptech.glide.load.engine;

/* loaded from: classes.dex */
public enum DiskCacheStrategy {
    ALL(true, true),
    NONE(false, false),
    SOURCE(true, false),
    RESULT(false, true);

    public final boolean cacheResult;
    public final boolean cacheSource;

    DiskCacheStrategy(boolean cacheSource, boolean cacheResult) {
        this.cacheSource = cacheSource;
        this.cacheResult = cacheResult;
    }
}
