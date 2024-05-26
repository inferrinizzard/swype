package com.nuance.android.util;

import android.content.Context;
import android.content.res.XmlResourceParser;
import com.nuance.swype.input.IMEApplication;

/* loaded from: classes.dex */
public final class ColorStateListParser extends XmlResourceParserWrapper {
    private final IMEApplication imeApp;

    public ColorStateListParser(XmlResourceParser target, Context context) {
        super(target);
        this.imeApp = IMEApplication.from(context);
    }

    @Override // com.nuance.android.util.XmlResourceParserWrapper, android.util.AttributeSet
    public final int getAttributeIntValue(int index, int defaultValue) {
        String value;
        int result = super.getAttributeIntValue(index, defaultValue);
        if (result == defaultValue && (value = getAttributeValue(index)) != null && value.length() > 1 && value.startsWith("?")) {
            try {
                int themeAttr = Integer.parseInt(value.substring(1));
                return this.imeApp.getThemedColor(themeAttr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return result;
            }
        }
        return result;
    }
}
