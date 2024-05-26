package com.bumptech.glide;

/* loaded from: classes.dex */
public enum MemoryCategory {
    LOW(0.5f),
    NORMAL(1.0f),
    HIGH(1.5f);

    float multiplier;

    MemoryCategory(float multiplier) {
        this.multiplier = multiplier;
    }
}
