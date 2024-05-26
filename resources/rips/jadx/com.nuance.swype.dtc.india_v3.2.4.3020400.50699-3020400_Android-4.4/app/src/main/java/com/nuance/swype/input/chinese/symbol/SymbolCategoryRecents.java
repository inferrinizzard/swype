package com.nuance.swype.input.chinese.symbol;

import com.nuance.swype.input.emoji.AbstractCategory;
import com.nuance.swype.input.emoji.RecentListManager;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SymbolCategoryRecents extends AbstractCategory {
    private RecentListManager recentListManager;

    public SymbolCategoryRecents(RecentListManager recentListManager, String name, int iconResId) {
        super(name, iconResId);
        this.recentListManager = recentListManager;
    }

    @Override // com.nuance.swype.input.emoji.AbstractCategory
    public List<String> getItemList() {
        List<String> symbols = new ArrayList<>();
        for (Object o : this.recentListManager.getAll()) {
            symbols.add((String) o);
        }
        return symbols;
    }

    public void add(String item, boolean pending) {
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

    public boolean commit() {
        return this.recentListManager.commit();
    }
}
