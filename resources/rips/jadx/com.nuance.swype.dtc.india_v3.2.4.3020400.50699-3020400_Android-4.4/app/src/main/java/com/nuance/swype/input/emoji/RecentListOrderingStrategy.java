package com.nuance.swype.input.emoji;

import java.util.List;

/* loaded from: classes.dex */
public interface RecentListOrderingStrategy<T, U> {
    void add(U u);

    List<T> getAll();

    int getSize();

    void readFromStore();

    void saveToStore();
}
