package com.nuance.swype.plugin;

import android.content.res.XmlResourceParser;
import android.util.Log;
import com.facebook.internal.ServerProtocol;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public final class ThemeMetaData {
    String themeStyleName;
    final String themeStyleableName = "SwypeThemeTemplate";
    public final List<WordListMetadata> wordListMetadataList = new ArrayList();

    /* JADX INFO: Access modifiers changed from: package-private */
    public ThemeMetaData(XmlResourceParser paramXmlResourceParser) {
        try {
            int eventType = paramXmlResourceParser.getEventType();
            String strTag = null;
            while (eventType != 1) {
                switch (eventType) {
                    case 2:
                        strTag = paramXmlResourceParser.getName();
                        if (!strTag.equals("wordlist")) {
                            break;
                        } else {
                            WordListMetadata wordListMetadata = new WordListMetadata(paramXmlResourceParser.getAttributeValue(null, "type"), paramXmlResourceParser.getAttributeValue(null, ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION), paramXmlResourceParser.getAttributeValue(null, "file"));
                            this.wordListMetadataList.add(wordListMetadata);
                            break;
                        }
                    case 4:
                        if (strTag != null && !strTag.equals("id") && !strTag.equals("name") && !strTag.equals("description") && !strTag.equals("snapshot") && !strTag.equals(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION) && !strTag.equals("date") && !strTag.equals("author")) {
                            char c = 65535;
                            switch (strTag.hashCode()) {
                                case 110327241:
                                    if (strTag.equals("theme")) {
                                        c = 0;
                                    }
                                default:
                                    switch (c) {
                                        case 0:
                                            this.themeStyleName = paramXmlResourceParser.getText();
                                            break;
                                        default:
                                            Log.w("ThemeInfo", "unknown tag: " + strTag);
                                            break;
                                    }
                            }
                        }
                        break;
                }
                eventType = paramXmlResourceParser.next();
            }
        } catch (IOException e) {
            e = e;
            e.printStackTrace();
        } catch (XmlPullParserException e2) {
            e = e2;
            e.printStackTrace();
        }
    }

    /* loaded from: classes.dex */
    public static class WordListMetadata {
        public final String fileName;
        public final String type;
        public final String version;

        public WordListMetadata(String type, String version, String file) {
            this.type = type;
            this.version = version;
            this.fileName = file;
        }
    }
}
