package com.nuance.swype.input;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Xml;
import com.nuance.swype.util.LogManager;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;

/* loaded from: classes.dex */
public class LocaleSettings {
    private static final String TAG_LOCALE = "Locale";
    private static LocaleSettings instance;
    private static final LogManager.Log log = LogManager.getLog("LocaleSettings");
    private Map<Locale, Settings> localeMap;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Settings {
        private final int currencyType;

        public Settings(int currencyType) {
            this.currencyType = currencyType;
        }
    }

    private LocaleSettings(Context context) {
        loadSettings(context);
    }

    public int getCurrencyType(Locale locale) {
        Settings settings = this.localeMap.get(locale);
        if (settings != null) {
            return settings.currencyType;
        }
        return 0;
    }

    public static synchronized LocaleSettings from(Context context) {
        LocaleSettings localeSettings;
        synchronized (LocaleSettings.class) {
            if (instance == null) {
                instance = new LocaleSettings(context);
            }
            localeSettings = instance;
        }
        return localeSettings;
    }

    private void loadSettings(Context context) {
        try {
            XmlPullParser parser = context.getResources().getXml(R.xml.locale_settings);
            this.localeMap = new HashMap();
            while (true) {
                int event = parser.next();
                if (event != 1) {
                    if (event == 2) {
                        String tag = parser.getName();
                        if (TAG_LOCALE.equals(tag)) {
                            AttributeSet attr = Xml.asAttributeSet(parser);
                            TypedArray a = context.obtainStyledAttributes(attr, R.styleable.Locale);
                            String language = a.getString(R.styleable.Locale_language_code);
                            String country = a.getString(R.styleable.Locale_country_code);
                            int currencyType = a.getInt(R.styleable.Locale_currencyType, 0);
                            a.recycle();
                            this.localeMap.put(new Locale(language, country), new Settings(currencyType));
                        }
                    }
                } else {
                    return;
                }
            }
        } catch (Exception ex) {
            log.e("Error reading gestures file", ex);
        }
    }
}
