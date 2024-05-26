package com.nuance.swype.input;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.Xml;
import com.nuance.swype.input.R;
import com.nuance.swype.util.LogManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public class LanguageTable {
    public static final int LANG_INDEX_ANDROID_LANG_ID = 2;
    public static final int LANG_INDEX_ENGLISH_NAME = 4;
    public static final int LANG_INDEX_ISO_2LETTER_CODE = 0;
    public static final int LANG_INDEX_ISO_FULL_CODE = 1;
    public static final int LANG_INDEX_NATIVE_NAME = 5;
    public static final int LANG_INDEX_XT9_LANG_ID = 3;
    private static final String TAG_LANGUAGE = "Language";
    private static final LogManager.Log log = LogManager.getLog("LanguageTable");
    Map<String, Integer> languageTable = new HashMap();

    LanguageTable(Context context) {
        Resources res = context.getResources();
        XmlResourceParser parser = null;
        int languageID = 0;
        String languageEnglishName = "";
        try {
            try {
                parser = res.getXml(R.xml.inputmethodsconfig);
                while (true) {
                    int event = parser.next();
                    if (event == 1) {
                        break;
                    }
                    if (event == 2) {
                        String tag = parser.getName();
                        if (TAG_LANGUAGE.equals(tag)) {
                            TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser), R.styleable.XT9);
                            CharSequence[] lang_values = a.getTextArray(R.styleable.XT9_language);
                            languageID = Integer.decode(lang_values[3].toString()).intValue();
                            languageEnglishName = lang_values[4].toString();
                            a.recycle();
                        }
                    } else if (event == 3) {
                        String tag2 = parser.getName();
                        if (TAG_LANGUAGE.equals(tag2)) {
                            this.languageTable.put(languageEnglishName, Integer.valueOf(languageID));
                        }
                    }
                }
                if (parser != null) {
                    parser.close();
                }
            } catch (IOException ex) {
                log.e(ex.getMessage(), ex);
                if (parser != null) {
                    parser.close();
                }
            } catch (XmlPullParserException ex2) {
                log.e(ex2.getMessage(), ex2);
                if (parser != null) {
                    parser.close();
                }
            }
        } catch (Throwable th) {
            if (parser != null) {
                parser.close();
            }
            throw th;
        }
    }

    public int getLanguageID(String languageName) {
        if (this.languageTable.containsKey(languageName)) {
            return this.languageTable.get(languageName).intValue();
        }
        return 0;
    }

    public static String getLanguageAndroidCode(Context context, String languageName) {
        try {
            int arrayId = R.array.class.getField("language_" + languageName).getInt(null);
            Resources r = context.getResources();
            String androidCode = r.getStringArray(arrayId)[2].trim();
            if (androidCode.length() == 0) {
                return r.getStringArray(arrayId)[1].trim();
            }
            return androidCode;
        } catch (Exception e) {
            log.e("getLanguageAndroidCode() - Failure to get android code for " + languageName, e);
            return "";
        }
    }
}
