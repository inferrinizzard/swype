package com.nuance.swype.input.chinese.symbol;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import android.util.Xml;
import com.nuance.swype.input.R;
import com.nuance.swype.input.emoji.AbstractCategory;
import com.nuance.swype.input.emoji.RecentListManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class SymbolCategoryList {
    private static final String CATEGORY_TAG_NAME = "category";
    private final List<AbstractCategory> categoryList = new ArrayList();
    private SymbolCategoryRecents recentCat;
    private RecentListManager recentListManager;

    public SymbolCategoryList(Context context, RecentListManager recentListManager) {
        this.recentListManager = recentListManager;
        parseXml(context, R.xml.symbol_category_list);
    }

    public SymbolCategoryRecents getRecentCat() {
        return this.recentCat;
    }

    private void parseXml(Context context, int resourceId) {
        XmlResourceParser parser = context.getResources().getXml(resourceId);
        try {
            for (int eventType = parser.next(); eventType != 1; eventType = parser.next()) {
                String tag = parser.getName();
                switch (eventType) {
                    case 2:
                        if (tag.equals(CATEGORY_TAG_NAME)) {
                            addCategory(context, parser);
                            break;
                        } else {
                            break;
                        }
                }
            }
        } catch (IOException e) {
        } catch (XmlPullParserException e2) {
        }
    }

    private void addCategory(Context context, XmlResourceParser parser) {
        TypedArray a = context.obtainStyledAttributes(Xml.asAttributeSet(parser), R.styleable.AbstractCategory);
        int arrRes = a.getResourceId(R.styleable.AbstractCategory_categoryItems, 0);
        int arrType = a.getInt(R.styleable.AbstractCategory_arrayType, 0);
        String name = a.getString(R.styleable.AbstractCategory_categoryName);
        int iconResId = a.getResourceId(R.styleable.AbstractCategory_categoryIcon, 0);
        a.recycle();
        if (arrRes != 0) {
            this.categoryList.add(new SymbolCategoryStandard(context, arrRes, arrType, name, iconResId));
        } else {
            if (this.recentCat != null) {
                throw new IllegalStateException("Only one recent category is allowed");
            }
            if (this.categoryList.size() > 0) {
                throw new IllegalStateException("Recent category must be first in list");
            }
            this.recentCat = new SymbolCategoryRecents(this.recentListManager, name, iconResId);
            this.categoryList.add(this.recentCat);
        }
    }

    public int getCategoryIndex(AbstractCategory cat) {
        return this.categoryList.indexOf(cat);
    }

    public AbstractCategory getCategoryFromName(String categoryName) {
        if (TextUtils.isEmpty(categoryName)) {
            return null;
        }
        for (AbstractCategory category : this.categoryList) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        return null;
    }

    public List<AbstractCategory> getAllCategories() {
        return Collections.unmodifiableList(this.categoryList);
    }

    public AbstractCategory getDefaultCategory() {
        return this.recentCat.hasItems() ? this.recentCat : this.categoryList.get(1);
    }
}
