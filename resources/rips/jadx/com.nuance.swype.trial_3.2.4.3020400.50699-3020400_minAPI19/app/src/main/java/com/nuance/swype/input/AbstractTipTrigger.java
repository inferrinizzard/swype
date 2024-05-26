package com.nuance.swype.input;

/* loaded from: classes.dex */
public abstract class AbstractTipTrigger {
    protected abstract int getDisplayCount();

    protected abstract int getMaxDisplayCount();

    protected abstract int getTriggerPoint();

    protected abstract int incrementAndGetCount();

    protected abstract void incrementDisplayCount();

    protected abstract void resetCount();

    public final boolean trigger() {
        if (getDisplayCount() >= getMaxDisplayCount() || incrementAndGetCount() < getTriggerPoint()) {
            return false;
        }
        resetCount();
        incrementDisplayCount();
        return true;
    }
}
