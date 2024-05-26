package com.nuance.swype.input.emoji;

import android.content.Context;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class MoveToFirstStrategyEmoji implements RecentListOrderingStrategy<Emoji, Emoji> {
    protected Context mAppContext;
    protected ArrayDeque<Emoji> mCache;
    private final int mMaxSize;

    public MoveToFirstStrategyEmoji(Context context, int maxSize) {
        this.mMaxSize = maxSize;
        this.mCache = new ArrayDeque<>(this.mMaxSize);
        this.mAppContext = context.getApplicationContext();
    }

    @Override // com.nuance.swype.input.emoji.RecentListOrderingStrategy
    public void add(Emoji emoji) {
        if (this.mCache.contains(emoji)) {
            this.mCache.remove(emoji);
        }
        Iterator<Emoji> it = this.mCache.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Emoji mEmoji = it.next();
            if (emoji.getEmojiDisplayCode().equals(mEmoji.getEmojiDisplayCode())) {
                this.mCache.remove(mEmoji);
                break;
            }
        }
        this.mCache.addFirst(emoji);
    }

    @Override // com.nuance.swype.input.emoji.RecentListOrderingStrategy
    public List<Emoji> getAll() {
        List<Emoji> list = new ArrayList<>();
        Iterator<Emoji> it = this.mCache.iterator();
        while (it.hasNext()) {
            Emoji emoji = it.next();
            list.add(emoji);
        }
        return list;
    }

    @Override // com.nuance.swype.input.emoji.RecentListOrderingStrategy
    public void saveToStore() {
    }

    @Override // com.nuance.swype.input.emoji.RecentListOrderingStrategy
    public void readFromStore() {
    }

    @Override // com.nuance.swype.input.emoji.RecentListOrderingStrategy
    public int getSize() {
        return this.mCache.size();
    }

    public ArrayDeque<Emoji> getCache() {
        return this.mCache;
    }

    public Context getContext() {
        return this.mAppContext;
    }
}
