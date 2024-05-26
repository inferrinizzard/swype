package com.google.api.client.util;

/* loaded from: classes.dex */
public final class StringUtils {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static byte[] getBytesUtf8(String string) {
        return com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils.getBytesUnchecked(string, "UTF-8");
    }
}
