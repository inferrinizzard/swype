package com.nuance.swype.inapp;

import com.nuance.swype.input.ThemeManager;
import java.util.List;

/* loaded from: classes.dex */
public final class CategoryItem {
    public String categoryId;
    public List<ThemeManager.SwypeTheme> themes;
    public String title;

    public CategoryItem(String title, String categoryId, List<ThemeManager.SwypeTheme> themes) {
        this.title = title;
        this.categoryId = categoryId;
        this.themes = themes;
    }
}
