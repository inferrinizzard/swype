package com.nuance.swype.util;

import android.content.Context;
import android.os.Build;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.LanguageList;
import com.nuance.swype.input.LanguageTable;
import com.nuance.swype.input.SystemState;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/* loaded from: classes.dex */
public final class LocalizationUtils {
    public static final String ASSETS_URL_PREFIX_HELPS = "file:///android_asset/helps" + File.separatorChar;

    public static String getHtmlFileUrl(String fileString, Context context) {
        return ASSETS_URL_PREFIX_HELPS + getHtmlFileName(fileString, context);
    }

    private static String getHtmlFileName(String fileString, Context context) {
        Locale locale = Locale.getDefault();
        String localeLanguage = locale.getLanguage();
        if (localeLanguage.equalsIgnoreCase("fil")) {
            localeLanguage = "tl";
        }
        if (localeLanguage.length() != 0) {
            String localeCountry = locale.getCountry();
            try {
                Set<String> helps = new HashSet<>(Arrays.asList(IMEApplication.from(context).getAssets().list("helps")));
                if (localeCountry.length() > 0) {
                    String androidCode = localeLanguage + "-r" + localeCountry;
                    String filename = String.format(fileString, androidCode);
                    if (helps.contains(filename)) {
                        return filename;
                    }
                }
                String filename2 = String.format(fileString, localeLanguage);
                if (helps.contains(filename2)) {
                    return filename2;
                }
            } catch (IOException e) {
                LanguageList languagelist = new LanguageList(context);
                String defaultAndroidCode = LanguageTable.getLanguageAndroidCode(context, languagelist.getDefaultLanguage());
                String filename3 = String.format(fileString, defaultAndroidCode);
                return filename3;
            }
        }
        LanguageList languagelist2 = new LanguageList(context);
        String defaultAndroidCode2 = LanguageTable.getLanguageAndroidCode(context, languagelist2.getDefaultLanguage());
        String filename4 = String.format(fileString, defaultAndroidCode2);
        return filename4;
    }

    public static Locale forLanguageTag(String s) {
        String[] parts = s.split(XMLResultsHandler.SEP_HYPHEN);
        String lang = parts[0];
        if (lang.equalsIgnoreCase("tl") && Build.VERSION.SDK_INT >= 21) {
            lang = "fil";
        }
        return parts.length > 1 ? new Locale(lang, parts[1]) : new Locale(lang);
    }

    public static Locale forAndroidQualifier(String s) {
        return forLanguageTag(s.replace("-r", XMLResultsHandler.SEP_HYPHEN));
    }

    public static boolean isUsersLocation(Context ctx, String countryCode, int[] mccCodes) {
        IMEApplication imeApp = IMEApplication.from(ctx);
        if (imeApp.getConnect().getISOCountry().equalsIgnoreCase(countryCode)) {
            return true;
        }
        SystemState systemState = imeApp.getSystemState();
        for (int i = 0; i <= 0; i++) {
            if (mccCodes[0] == systemState.getNetworkOperatorMCC()) {
                return true;
            }
        }
        return false;
    }
}
