package com.nuance.swype.input.chinese.symbol;

import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.emoji.MoveToFirstStrategy;
import com.nuance.swype.input.emoji.RecentListOrderingStrategy;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

/* loaded from: classes.dex */
public class MoveToFirstStrategySymbolDecorator implements RecentListOrderingStrategy<String, String> {
    MoveToFirstStrategy strategy;

    public MoveToFirstStrategySymbolDecorator(MoveToFirstStrategy orderingStrategy) {
        this.strategy = orderingStrategy;
        readFromStore();
    }

    @Override // com.nuance.swype.input.emoji.RecentListOrderingStrategy
    public void add(String emoji) {
        this.strategy.add(emoji);
    }

    @Override // com.nuance.swype.input.emoji.RecentListOrderingStrategy
    public List<String> getAll() {
        return this.strategy.getAll();
    }

    @Override // com.nuance.swype.input.emoji.RecentListOrderingStrategy
    public void saveToStore() {
        JSONArray array = new JSONArray();
        Iterator<String> it = this.strategy.getCache().iterator();
        while (it.hasNext()) {
            String emoji = it.next();
            array.put(emoji);
        }
        String jsonString = array.toString();
        AppPreferences.from(this.strategy.getContext()).setChineseSymbolRecentList(jsonString);
    }

    @Override // com.nuance.swype.input.emoji.RecentListOrderingStrategy
    public void readFromStore() {
        this.strategy.getCache().clear();
        String jsonString = AppPreferences.from(this.strategy.getContext()).getChineseSymbolRecentList();
        if (!jsonString.isEmpty()) {
            try {
                JSONArray array = (JSONArray) new JSONTokener(jsonString).nextValue();
                for (int i = 0; i < array.length(); i++) {
                    this.strategy.getCache().add(array.getString(i));
                }
            } catch (JSONException e) {
            }
        }
    }

    @Override // com.nuance.swype.input.emoji.RecentListOrderingStrategy
    public int getSize() {
        return this.strategy.getSize();
    }
}
