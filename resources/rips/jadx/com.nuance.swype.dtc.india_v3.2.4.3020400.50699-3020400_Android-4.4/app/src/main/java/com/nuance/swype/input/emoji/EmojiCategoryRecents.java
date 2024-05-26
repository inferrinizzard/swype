package com.nuance.swype.input.emoji;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class EmojiCategoryRecents extends EmojiCategory<Emoji> {
    private RecentListManager recentListManager;

    public EmojiCategoryRecents(RecentListManager recentListManager, String name, int iconResId) {
        super(name, iconResId);
        this.recentListManager = recentListManager;
    }

    @Override // com.nuance.swype.input.emoji.EmojiCategory
    public List<Emoji> getEmojiList() {
        List<Object> objects = this.recentListManager.getAll();
        List<Emoji> emojis = new ArrayList<>();
        for (Object o : objects) {
            emojis.add((Emoji) o);
        }
        return emojis;
    }

    @Override // com.nuance.swype.input.emoji.EmojiCategory
    public boolean isRecentCategory() {
        return true;
    }

    public void add(Emoji item, boolean pending) {
        if (pending) {
            this.recentListManager.addPending(item);
        } else {
            this.recentListManager.add(item);
        }
    }

    @Override // com.nuance.swype.input.emoji.AbstractCategory
    public boolean hasItems() {
        return this.recentListManager.hasItems();
    }

    @Override // com.nuance.swype.input.emoji.AbstractCategory
    public boolean isDynamic() {
        return true;
    }

    public synchronized boolean commit() {
        return this.recentListManager.commit();
    }

    @Override // com.nuance.swype.input.emoji.AbstractCategory
    public List<String> getItemList() {
        return null;
    }
}
