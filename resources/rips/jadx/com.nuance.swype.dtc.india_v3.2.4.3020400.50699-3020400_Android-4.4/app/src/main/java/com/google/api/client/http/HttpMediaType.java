package com.google.api.client.http;

import com.google.api.client.util.Preconditions;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public final class HttpMediaType {
    private static final Pattern PARAMETER_REGEX;
    private String cachedBuildResult;
    private static final Pattern TYPE_REGEX = Pattern.compile("[\\w!#$&.+\\-\\^_]+|[*]");
    private static final Pattern TOKEN_REGEX = Pattern.compile("[\\p{ASCII}&&[^\\p{Cntrl} ;/=\\[\\]\\(\\)\\<\\>\\@\\,\\:\\\"\\?\\=]]+");
    private static final Pattern FULL_MEDIA_TYPE_REGEX = Pattern.compile("\\s*([^\\s/=;\"]+)/([^\\s/=;\"]+)\\s*(;.*)?", 32);
    private String type = "application";
    private String subType = "octet-stream";
    private final SortedMap<String, String> parameters = new TreeMap();

    static {
        String parameterValue = "\"([^\"]*)\"|[^\\s;\"]*";
        PARAMETER_REGEX = Pattern.compile("\\s*;\\s*([^\\s/=;\"]+)=(" + parameterValue + ")");
    }

    public HttpMediaType(String type, String subType) {
        setType(type);
        setSubType(subType);
    }

    public HttpMediaType(String mediaType) {
        fromString(mediaType);
    }

    public final HttpMediaType setType(String type) {
        Preconditions.checkArgument(TYPE_REGEX.matcher(type).matches(), "Type contains reserved characters");
        this.type = type;
        this.cachedBuildResult = null;
        return this;
    }

    public final String getType() {
        return this.type;
    }

    public final HttpMediaType setSubType(String subType) {
        Preconditions.checkArgument(TYPE_REGEX.matcher(subType).matches(), "Subtype contains reserved characters");
        this.subType = subType;
        this.cachedBuildResult = null;
        return this;
    }

    public final String getSubType() {
        return this.subType;
    }

    private HttpMediaType fromString(String combinedType) {
        Matcher matcher = FULL_MEDIA_TYPE_REGEX.matcher(combinedType);
        Preconditions.checkArgument(matcher.matches(), "Type must be in the 'maintype/subtype; parameter=value' format");
        setType(matcher.group(1));
        setSubType(matcher.group(2));
        String params = matcher.group(3);
        if (params != null) {
            Matcher matcher2 = PARAMETER_REGEX.matcher(params);
            while (matcher2.find()) {
                String key = matcher2.group(1);
                String value = matcher2.group(3);
                if (value == null) {
                    value = matcher2.group(2);
                }
                setParameter(key, value);
            }
        }
        return this;
    }

    public final HttpMediaType setParameter(String name, String value) {
        if (value == null) {
            removeParameter(name);
        } else {
            Preconditions.checkArgument(TOKEN_REGEX.matcher(name).matches(), "Name contains reserved characters");
            this.cachedBuildResult = null;
            this.parameters.put(name.toLowerCase(), value);
        }
        return this;
    }

    public final String getParameter(String name) {
        return this.parameters.get(name.toLowerCase());
    }

    public final HttpMediaType removeParameter(String name) {
        this.cachedBuildResult = null;
        this.parameters.remove(name.toLowerCase());
        return this;
    }

    public final void clearParameters() {
        this.cachedBuildResult = null;
        this.parameters.clear();
    }

    public final Map<String, String> getParameters() {
        return Collections.unmodifiableMap(this.parameters);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean matchesToken(String value) {
        return TOKEN_REGEX.matcher(value).matches();
    }

    private static String quoteString(String unquotedString) {
        String escapedString = unquotedString.replace("\\", "\\\\").replace("\"", "\\\"");
        return "\"" + escapedString + "\"";
    }

    public final String build() {
        if (this.cachedBuildResult != null) {
            return this.cachedBuildResult;
        }
        StringBuilder str = new StringBuilder();
        str.append(this.type);
        str.append('/');
        str.append(this.subType);
        if (this.parameters != null) {
            for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
                String value = entry.getValue();
                str.append("; ");
                str.append(entry.getKey());
                str.append("=");
                if (!matchesToken(value)) {
                    value = quoteString(value);
                }
                str.append(value);
            }
        }
        this.cachedBuildResult = str.toString();
        return this.cachedBuildResult;
    }

    public final String toString() {
        return build();
    }

    public final boolean equalsIgnoreParameters(HttpMediaType mediaType) {
        return mediaType != null && getType().equalsIgnoreCase(mediaType.getType()) && getSubType().equalsIgnoreCase(mediaType.getSubType());
    }

    public static boolean equalsIgnoreParameters(String mediaTypeA, String mediaTypeB) {
        return (mediaTypeA == null && mediaTypeB == null) || !(mediaTypeA == null || mediaTypeB == null || !new HttpMediaType(mediaTypeA).equalsIgnoreParameters(new HttpMediaType(mediaTypeB)));
    }

    public final HttpMediaType setCharsetParameter(Charset charset) {
        setParameter("charset", charset == null ? null : charset.name());
        return this;
    }

    public final Charset getCharsetParameter() {
        String value = getParameter("charset");
        if (value == null) {
            return null;
        }
        return Charset.forName(value);
    }

    public final int hashCode() {
        return build().hashCode();
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof HttpMediaType)) {
            return false;
        }
        HttpMediaType otherType = (HttpMediaType) obj;
        return equalsIgnoreParameters(otherType) && this.parameters.equals(otherType.parameters);
    }
}
