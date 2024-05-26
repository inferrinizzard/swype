package com.nuance.swype.input.emoji;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class RecentListManager {
    private final List<Object> pendingList = new ArrayList();
    private final RecentListOrderingStrategy recentList;

    public RecentListManager(RecentListOrderingStrategy recentList) {
        this.recentList = recentList;
    }

    public void addPending(Object emoji) {
        this.pendingList.add(emoji);
    }

    public void add(Object emoji) {
        this.recentList.add(emoji);
    }

    public boolean commit() {
        boolean changed = !this.pendingList.isEmpty();
        for (Object emoji : this.pendingList) {
            this.recentList.add(emoji);
        }
        saveToStore();
        this.pendingList.clear();
        return changed;
    }

    public List<Object> getAll() {
        return this.recentList.getAll();
    }

    private void readFromStore() {
        this.recentList.readFromStore();
    }

    private void saveToStore() {
        this.recentList.saveToStore();
    }

    public boolean hasItems() {
        return this.recentList.getSize() > 0;
    }
}
