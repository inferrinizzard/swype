package com.nuance.swype.input.emoji;

import java.util.List;

/* loaded from: classes.dex */
public abstract class EmojiCategory<T> extends AbstractCategory {
    public abstract List<T> getEmojiList();

    public abstract boolean isRecentCategory();

    public EmojiCategory(String name, int iconResId) {
        super(name, iconResId);
    }

    @Override // com.nuance.swype.input.emoji.AbstractCategory
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof EmojiCategory)) {
            EmojiCategory other = (EmojiCategory) obj;
            return this.name == null ? other.name == null : this.name.equals(other.name);
        }
        return false;
    }
}
