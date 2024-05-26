package com.nuance.swype.input.emoji;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.text.TextUtils;
import android.util.Xml;
import com.nuance.swype.input.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class EmojiCategoryList {
    private static final String CATEGORY_TAG_NAME = "category";
    private static final String FAMILY_CATEGORY_NAME = "families";
    private static final String FLAG_CATEGORY_NAME = "flags";
    private final List<EmojiCategory> categoryList = new ArrayList();
    private EmojiCategoryRecents recentCat;
    private RecentListManager recentListManager;

    @SuppressLint({"PrivateResource"})
    public EmojiCategoryList(Context context, RecentListManager recentListManager) {
        this.recentListManager = recentListManager;
        parseXml(context, R.xml.emoji_category_list);
    }

    public EmojiCategoryRecents getRecentCat() {
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
            if (!name.equals(FLAG_CATEGORY_NAME) || isCategorySupportMultiCode()) {
                this.categoryList.add(new EmojiCategoryStandard(context, arrRes, arrType, name, iconResId));
                return;
            }
            return;
        }
        if (this.recentCat != null) {
            throw new IllegalStateException("Only one recent category is allowed");
        }
        if (this.categoryList.size() > 0) {
            throw new IllegalStateException("Recent category must be first in list");
        }
        this.recentCat = new EmojiCategoryRecents(this.recentListManager, name, iconResId);
        this.categoryList.add(this.recentCat);
    }

    public int getCategoryIndex(EmojiCategory cat) {
        return this.categoryList.indexOf(cat);
    }

    public EmojiCategory getCategoryFromName(String categoryName) {
        if (TextUtils.isEmpty(categoryName)) {
            return null;
        }
        for (EmojiCategory category : this.categoryList) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        return null;
    }

    public List<EmojiCategory> getAllCategories() {
        return Collections.unmodifiableList(this.categoryList);
    }

    public EmojiCategory getDefaultCategory() {
        return this.recentCat.hasItems() ? this.recentCat : this.categoryList.get(1);
    }

    public static boolean isCategorySupportMultiCode() {
        return Build.VERSION.SDK_INT > 23;
    }
}
