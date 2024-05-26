package com.nuance.swype.input;

import com.nuance.swype.util.LogManager;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class AppContextPredictionSetter {
    public static final String APPCONTEXT_EMAIL = "EMAIL";
    public static final String APPCONTEXT_MESSAGING = "MESSAGING";
    public static final String APPCONTEXT_SCAN_ANDROID_SMS = "com.android.mms";
    public static final String APPCONTEXT_SCAN_EMAIL = "com.android.email";
    public static final String APPCONTEXT_SCAN_GMAIL = "com.google.android.gm";
    public static final String APPCONTEXT_SCAN_TWITTER = "com.twitter.android";
    public static final String APPCONTEXT_SOCIAL = "SOCIAL";
    private static final LogManager.Log log = LogManager.getLog("AppContextSetter");
    private final Map<String, String> packageToGroupTable = new HashMap();

    /* JADX WARN: Removed duplicated region for block: B:28:0x0060  */
    /* JADX WARN: Removed duplicated region for block: B:30:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public AppContextPredictionSetter(android.content.Context r9) {
        /*
            r8 = this;
            r8.<init>()
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            r8.packageToGroupTable = r5
            android.content.res.Resources r5 = r9.getResources()
            int r6 = com.nuance.swype.input.R.xml.appcontext_group_config
            android.content.res.XmlResourceParser r4 = r5.getXml(r6)
        L14:
            int r0 = r4.next()     // Catch: org.xmlpull.v1.XmlPullParserException -> L5d java.lang.Throwable -> L6a java.io.IOException -> L71
            r5 = 1
            if (r0 == r5) goto L64
            r5 = 2
            if (r0 != r5) goto L14
            java.lang.String r2 = r4.getName()     // Catch: org.xmlpull.v1.XmlPullParserException -> L5d java.lang.Throwable -> L6a java.io.IOException -> L71
            r5 = 0
            java.lang.String r6 = "package_name"
            java.lang.String r3 = r4.getAttributeValue(r5, r6)     // Catch: org.xmlpull.v1.XmlPullParserException -> L5d java.lang.Throwable -> L6a java.io.IOException -> L71
            if (r3 == 0) goto L14
            java.util.Map<java.lang.String, java.lang.String> r5 = r8.packageToGroupTable     // Catch: org.xmlpull.v1.XmlPullParserException -> L5d java.lang.Throwable -> L6a java.io.IOException -> L71
            java.lang.Object r1 = r5.get(r3)     // Catch: org.xmlpull.v1.XmlPullParserException -> L5d java.lang.Throwable -> L6a java.io.IOException -> L71
            java.lang.String r1 = (java.lang.String) r1     // Catch: org.xmlpull.v1.XmlPullParserException -> L5d java.lang.Throwable -> L6a java.io.IOException -> L71
            if (r1 != 0) goto L3c
            java.util.Map<java.lang.String, java.lang.String> r5 = r8.packageToGroupTable     // Catch: org.xmlpull.v1.XmlPullParserException -> L5d java.lang.Throwable -> L6a java.io.IOException -> L71
            r5.put(r3, r2)     // Catch: org.xmlpull.v1.XmlPullParserException -> L5d java.lang.Throwable -> L6a java.io.IOException -> L71
            r1 = r2
        L3c:
            com.nuance.swype.util.LogManager$Log r5 = com.nuance.swype.input.AppContextPredictionSetter.log     // Catch: org.xmlpull.v1.XmlPullParserException -> L5d java.lang.Throwable -> L6a java.io.IOException -> L71
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch: org.xmlpull.v1.XmlPullParserException -> L5d java.lang.Throwable -> L6a java.io.IOException -> L71
            java.lang.String r7 = "[AppContextSetter XML Reader] package: "
            r6.<init>(r7)     // Catch: org.xmlpull.v1.XmlPullParserException -> L5d java.lang.Throwable -> L6a java.io.IOException -> L71
            java.lang.StringBuilder r6 = r6.append(r3)     // Catch: org.xmlpull.v1.XmlPullParserException -> L5d java.lang.Throwable -> L6a java.io.IOException -> L71
            java.lang.String r7 = ", group: "
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch: org.xmlpull.v1.XmlPullParserException -> L5d java.lang.Throwable -> L6a java.io.IOException -> L71
            java.lang.StringBuilder r6 = r6.append(r1)     // Catch: org.xmlpull.v1.XmlPullParserException -> L5d java.lang.Throwable -> L6a java.io.IOException -> L71
            java.lang.String r6 = r6.toString()     // Catch: org.xmlpull.v1.XmlPullParserException -> L5d java.lang.Throwable -> L6a java.io.IOException -> L71
            r5.i(r6)     // Catch: org.xmlpull.v1.XmlPullParserException -> L5d java.lang.Throwable -> L6a java.io.IOException -> L71
            goto L14
        L5d:
            r5 = move-exception
        L5e:
            if (r4 == 0) goto L63
            r4.close()
        L63:
            return
        L64:
            if (r4 == 0) goto L63
            r4.close()
            goto L63
        L6a:
            r5 = move-exception
            if (r4 == 0) goto L70
            r4.close()
        L70:
            throw r5
        L71:
            r5 = move-exception
            goto L5e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.AppContextPredictionSetter.<init>(android.content.Context):void");
    }

    public String getGroupName(String packageName) {
        if (packageName != null) {
            return this.packageToGroupTable.get(packageName);
        }
        return null;
    }

    public String getTimeOfDayMarker() {
        int h24 = new GregorianCalendar().get(11);
        int d4 = (h24 + 2) / 6;
        if (d4 == 0) {
            d4 = 4;
        }
        log.d("[appcontext] hour of the day: " + h24 + " quarter slot of day: " + d4);
        return Integer.toString(d4);
    }
}
