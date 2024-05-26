package com.flurry.sdk;

import android.text.TextUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public final class ly {
    private static final Pattern a = Pattern.compile("/");

    private static URI i(String str) {
        try {
            return new URI(str);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static boolean h(String str) {
        URI i;
        if (TextUtils.isEmpty(str) || (i = i(str)) == null) {
            return false;
        }
        return i.getScheme() == null || "http".equalsIgnoreCase(i.getScheme()) || "https".equalsIgnoreCase(i.getScheme());
    }

    public static String b(String str, String str2) {
        if (!TextUtils.isEmpty(str)) {
            try {
                if (!new URI(str).isAbsolute() && !TextUtils.isEmpty(str2)) {
                    URI uri = new URI(str2);
                    return uri.getScheme() + "://" + uri.getHost() + str;
                }
                return str;
            } catch (Exception e) {
                return str;
            }
        }
        return str;
    }
}
