package com.nuance.swype.input.emoji;

import java.util.List;

/* loaded from: classes.dex */
public abstract class AbstractCategory {
    protected final int iconRes;
    protected final String name;

    public abstract List<String> getItemList();

    public abstract boolean hasItems();

    public abstract boolean isDynamic();

    public AbstractCategory(String name, int iconResId) {
        this.name = name;
        this.iconRes = iconResId;
    }

    public String getName() {
        return this.name;
    }

    public int getIconRes() {
        return this.iconRes;
    }

    public int hashCode() {
        return (this.name == null ? 0 : this.name.hashCode()) + 31;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof AbstractCategory)) {
            AbstractCategory other = (AbstractCategory) obj;
            return this.name == null ? other.name == null : this.name.equals(other.name);
        }
        return false;
    }

    public String toString() {
        return getName();
    }
}
