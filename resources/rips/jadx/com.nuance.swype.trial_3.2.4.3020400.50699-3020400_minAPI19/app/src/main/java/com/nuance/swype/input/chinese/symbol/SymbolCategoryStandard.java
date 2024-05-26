package com.nuance.swype.input.chinese.symbol;

import android.content.Context;
import android.content.res.Resources;
import com.nuance.swype.input.emoji.AbstractCategory;
import com.nuance.swype.util.CollectionUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class SymbolCategoryStandard extends AbstractCategory {
    private static final int TYPE_INT = 0;
    private static final int TYPE_STRING = 1;
    private final int arrayRes;
    private final int arrayType;
    private Context context;
    private List<String> emojis;

    public SymbolCategoryStandard(Context context, int arrayRes, int arrayType, String name, int iconResId) {
        super(name, iconResId);
        this.context = context;
        this.arrayRes = arrayRes;
        this.arrayType = arrayType;
    }

    @Override // com.nuance.swype.input.emoji.AbstractCategory
    public boolean hasItems() {
        return !getItemList().isEmpty();
    }

    @Override // com.nuance.swype.input.emoji.AbstractCategory
    public List<String> getItemList() {
        if (this.emojis == null) {
            load();
        }
        return Collections.unmodifiableList(this.emojis);
    }

    private void load() {
        Resources res = this.context.getResources();
        switch (this.arrayType) {
            case 0:
                loadCodePoints(res);
                return;
            case 1:
                loadStrings(res);
                return;
            default:
                throw new AssertionError("Bad array type");
        }
    }

    private void loadCodePoints(Resources res) {
        StringBuffer sb = new StringBuffer();
        int[] codePoints = res.getIntArray(this.arrayRes);
        this.emojis = CollectionUtils.newArrayList(codePoints.length);
        for (int codePoint : codePoints) {
            sb.setLength(0);
            sb.appendCodePoint(codePoint);
            this.emojis.add(sb.toString());
        }
    }

    private void loadStrings(Resources res) {
        String[] strings = res.getStringArray(this.arrayRes);
        this.emojis = Arrays.asList(strings);
    }

    @Override // com.nuance.swype.input.emoji.AbstractCategory
    public boolean isDynamic() {
        return false;
    }
}
