package com.nuance.swype.input.emoji;

import android.content.Context;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class MoveToFirstStrategy implements RecentListOrderingStrategy<String, String> {
    protected Context mAppContext;
    protected ArrayDeque<String> mCache;
    private final int mMaxSize;

    public MoveToFirstStrategy(Context context, int maxSize) {
        this.mMaxSize = maxSize;
        this.mCache = new ArrayDeque<>(this.mMaxSize);
        this.mAppContext = context.getApplicationContext();
    }

    @Override // com.nuance.swype.input.emoji.RecentListOrderingStrategy
    public void add(String emoji) {
        if (this.mCache.contains(emoji)) {
            this.mCache.remove(emoji);
        }
        if (this.mCache.size() + 1 > this.mMaxSize) {
            this.mCache.removeLast();
        }
        this.mCache.addFirst(emoji);
    }

    @Override // com.nuance.swype.input.emoji.RecentListOrderingStrategy
    public List<String> getAll() {
        List<String> list = new ArrayList<>();
        Iterator<String> it = this.mCache.iterator();
        while (it.hasNext()) {
            String emoji = it.next();
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

    public ArrayDeque<String> getCache() {
        return this.mCache;
    }

    public Context getContext() {
        return this.mAppContext;
    }
}
