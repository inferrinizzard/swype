package com.nuance.swype.plugin;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import com.nuance.swype.plugin.ThemeLayoutAttributeParser;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ThemeLayoutInflateFactory implements LayoutInflater.Factory {
    private static final LogManager.Log log = LogManager.getLog("ThemeLayoutInflateFactory");
    final ArrayList<ThemeLayoutAttributeParser.ThemeStyledItem> mThemeItems = new ArrayList<>();

    @Override // android.view.LayoutInflater.Factory
    public final View onCreateView(String widgetType, Context paramContext, AttributeSet paramAttributeSet) {
        String id = paramAttributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "id");
        if (id != null) {
            ThemeLayoutAttributeParser.ThemeStyledItem newItem = new ThemeLayoutAttributeParser.ThemeStyledItem();
            newItem.id = Integer.parseInt(id.substring(1));
            newItem.mThemeAttrs = new ArrayList<>();
            parseViewAttrSet(paramAttributeSet, newItem.mThemeAttrs);
            if (newItem.mThemeAttrs.size() > 0) {
                this.mThemeItems.add(newItem);
                return null;
            }
            return null;
        }
        return null;
    }

    private static void parseViewAttrSet(AttributeSet paramAttributeSet, ArrayList<ThemeAttrAssociation> paramArrayList) {
        if (paramArrayList != null) {
            MainApkResourceBroker localResMgr = MainApkResourceBroker.getInstance();
            for (int j = 0; j < paramAttributeSet.getAttributeCount(); j++) {
                String attrResIdStr = paramAttributeSet.getAttributeValue(j);
                String attrName = paramAttributeSet.getAttributeName(j);
                if (attrResIdStr.startsWith("?")) {
                    try {
                        int attrResId = Integer.parseInt(attrResIdStr.substring(1));
                        if (localResMgr.mThemeStyleableAttrResIds.contains(Integer.valueOf(attrResId))) {
                            String attrFullName = localResMgr.mContext.getResources().getResourceName(attrResId);
                            paramArrayList.add(new ThemeAttrAssociation(attrName, attrFullName));
                        }
                    } catch (Resources.NotFoundException e) {
                        log.e("parse attributeValueReferenceName Exception");
                    } catch (NumberFormatException e2) {
                        log.e("parse attributeValueReferenceId Exception");
                    }
                }
            }
        }
    }
}
