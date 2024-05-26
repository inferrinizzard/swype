package com.google.api.client.util.escape;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/* loaded from: classes.dex */
public final class CharEscapers {
    private static final Escaper URI_ESCAPER = new PercentEscaper("-_.*", true);
    private static final Escaper URI_PATH_ESCAPER = new PercentEscaper("-_.!~*'()@:$&,;=", false);
    private static final Escaper URI_RESERVED_ESCAPER = new PercentEscaper("-_.!~*'()@:$&,;=+/?", false);
    private static final Escaper URI_USERINFO_ESCAPER = new PercentEscaper("-_.!~*'():$&,;=", false);
    private static final Escaper URI_QUERY_STRING_ESCAPER = new PercentEscaper("-_.!~*'()@:$,;/?:", false);

    public static String escapeUri(String value) {
        return URI_ESCAPER.escape(value);
    }

    public static String decodeUri(String uri) {
        try {
            return URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String escapeUriPath(String value) {
        return URI_PATH_ESCAPER.escape(value);
    }

    public static String escapeUriPathWithoutReserved(String value) {
        return URI_RESERVED_ESCAPER.escape(value);
    }

    public static String escapeUriUserInfo(String value) {
        return URI_USERINFO_ESCAPER.escape(value);
    }

    public static String escapeUriQuery(String value) {
        return URI_QUERY_STRING_ESCAPER.escape(value);
    }
}
