package com.google.api.client.http;

import com.google.api.client.repackaged.com.google.common.base.CharMatcher;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.repackaged.com.google.common.base.Splitter;
import com.google.api.client.util.Data;
import com.google.api.client.util.FieldInfo;
import com.google.api.client.util.Types;
import com.google.api.client.util.escape.CharEscapers;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;

/* loaded from: classes.dex */
public class UriTemplate {
    private static final String COMPOSITE_NON_EXPLODE_JOINER = ",";
    static final Map<Character, CompositeOutput> COMPOSITE_PREFIXES = new HashMap();

    static {
        CompositeOutput.values();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum CompositeOutput {
        PLUS('+', "", ",", false, true),
        HASH('#', "#", ",", false, true),
        DOT('.', ".", ".", false, false),
        FORWARD_SLASH('/', "/", "/", false, false),
        SEMI_COLON(';', ";", ";", true, false),
        QUERY('?', "?", "&", true, false),
        AMP('&', "&", "&", true, false),
        SIMPLE(null, "", ",", false, false);

        private final String explodeJoiner;
        private final String outputPrefix;
        private final Character propertyPrefix;
        private final boolean requiresVarAssignment;
        private final boolean reservedExpansion;

        CompositeOutput(Character propertyPrefix, String outputPrefix, String explodeJoiner, boolean requiresVarAssignment, boolean reservedExpansion) {
            this.propertyPrefix = propertyPrefix;
            this.outputPrefix = (String) Preconditions.checkNotNull(outputPrefix);
            this.explodeJoiner = (String) Preconditions.checkNotNull(explodeJoiner);
            this.requiresVarAssignment = requiresVarAssignment;
            this.reservedExpansion = reservedExpansion;
            if (propertyPrefix != null) {
                UriTemplate.COMPOSITE_PREFIXES.put(propertyPrefix, this);
            }
        }

        final String getOutputPrefix() {
            return this.outputPrefix;
        }

        final String getExplodeJoiner() {
            return this.explodeJoiner;
        }

        final boolean requiresVarAssignment() {
            return this.requiresVarAssignment;
        }

        final int getVarNameStartIndex() {
            return this.propertyPrefix == null ? 0 : 1;
        }

        final String getEncodedValue(String value) {
            if (this.reservedExpansion) {
                String encodedValue = CharEscapers.escapeUriPath(value);
                return encodedValue;
            }
            String encodedValue2 = CharEscapers.escapeUri(value);
            return encodedValue2;
        }

        final boolean getReservedExpansion() {
            return this.reservedExpansion;
        }
    }

    static CompositeOutput getCompositeOutput(String propertyName) {
        CompositeOutput compositeOutput = COMPOSITE_PREFIXES.get(Character.valueOf(propertyName.charAt(0)));
        return compositeOutput == null ? CompositeOutput.SIMPLE : compositeOutput;
    }

    private static Map<String, Object> getMap(Object obj) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : Data.mapOf(obj).entrySet()) {
            Object value = entry.getValue();
            if (value != null && !Data.isNull(value)) {
                map.put(entry.getKey(), value);
            }
        }
        return map;
    }

    public static String expand(String baseUrl, String uriTemplate, Object parameters, boolean addUnusedParamsAsQueryParams) {
        String pathUri;
        if (uriTemplate.startsWith("/")) {
            GenericUrl url = new GenericUrl(baseUrl);
            url.setRawPath(null);
            pathUri = url.build() + uriTemplate;
        } else if (uriTemplate.startsWith("http://") || uriTemplate.startsWith("https://")) {
            pathUri = uriTemplate;
        } else {
            pathUri = baseUrl + uriTemplate;
        }
        return expand(pathUri, parameters, addUnusedParamsAsQueryParams);
    }

    public static String expand(String pathUri, Object parameters, boolean addUnusedParamsAsQueryParams) {
        Map<String, Object> variableMap = getMap(parameters);
        StringBuilder pathBuf = new StringBuilder();
        int cur = 0;
        int length = pathUri.length();
        while (true) {
            if (cur >= length) {
                break;
            }
            int next = pathUri.indexOf(123, cur);
            if (next == -1) {
                if (cur != 0 || addUnusedParamsAsQueryParams) {
                    pathBuf.append(pathUri.substring(cur));
                } else {
                    return pathUri;
                }
            } else {
                pathBuf.append(pathUri.substring(cur, next));
                int close = pathUri.indexOf(125, next + 2);
                cur = close + 1;
                String templates = pathUri.substring(next + 1, close);
                CompositeOutput compositeOutput = getCompositeOutput(templates);
                CharMatcher is$2a9d1698 = CharMatcher.is$2a9d1698();
                Preconditions.checkNotNull(is$2a9d1698);
                ListIterator<String> templateIterator = new Splitter(new Splitter.Strategy() { // from class: com.google.api.client.repackaged.com.google.common.base.Splitter.1
                    public AnonymousClass1() {
                    }

                    /* renamed from: com.google.api.client.repackaged.com.google.common.base.Splitter$1$1 */
                    /* loaded from: classes.dex */
                    final class C01151 extends SplittingIterator {
                        C01151(Splitter x0, CharSequence x1) {
                            super(x0, x1);
                        }

                        @Override // com.google.api.client.repackaged.com.google.common.base.Splitter.SplittingIterator
                        final int separatorStart(int start) {
                            return CharMatcher.this.indexIn(this.toSplit, start);
                        }

                        @Override // com.google.api.client.repackaged.com.google.common.base.Splitter.SplittingIterator
                        final int separatorEnd(int separatorPosition) {
                            return separatorPosition + 1;
                        }
                    }

                    @Override // com.google.api.client.repackaged.com.google.common.base.Splitter.Strategy
                    public final /* bridge */ /* synthetic */ Iterator iterator(Splitter x0, CharSequence x1) {
                        return new SplittingIterator(x0, x1) { // from class: com.google.api.client.repackaged.com.google.common.base.Splitter.1.1
                            C01151(Splitter x02, CharSequence x12) {
                                super(x02, x12);
                            }

                            @Override // com.google.api.client.repackaged.com.google.common.base.Splitter.SplittingIterator
                            final int separatorStart(int start) {
                                return CharMatcher.this.indexIn(this.toSplit, start);
                            }

                            @Override // com.google.api.client.repackaged.com.google.common.base.Splitter.SplittingIterator
                            final int separatorEnd(int separatorPosition) {
                                return separatorPosition + 1;
                            }
                        };
                    }
                }).splitToList(templates).listIterator();
                boolean isFirstParameter = true;
                while (templateIterator.hasNext()) {
                    String template = templateIterator.next();
                    boolean containsExplodeModifier = template.endsWith("*");
                    int varNameStartIndex = templateIterator.nextIndex() == 1 ? compositeOutput.getVarNameStartIndex() : 0;
                    int varNameEndIndex = template.length();
                    if (containsExplodeModifier) {
                        varNameEndIndex--;
                    }
                    String varName = template.substring(varNameStartIndex, varNameEndIndex);
                    Object value = variableMap.remove(varName);
                    if (value != null) {
                        if (!isFirstParameter) {
                            pathBuf.append(compositeOutput.getExplodeJoiner());
                        } else {
                            pathBuf.append(compositeOutput.getOutputPrefix());
                            isFirstParameter = false;
                        }
                        if (value instanceof Iterator) {
                            Iterator<?> iterator = (Iterator) value;
                            value = getListPropertyValue(varName, iterator, containsExplodeModifier, compositeOutput);
                        } else if ((value instanceof Iterable) || value.getClass().isArray()) {
                            Iterator<?> iterator2 = Types.iterableOf(value).iterator();
                            value = getListPropertyValue(varName, iterator2, containsExplodeModifier, compositeOutput);
                        } else if (value.getClass().isEnum()) {
                            if (FieldInfo.of((Enum<?>) value).name != null) {
                                if (compositeOutput.requiresVarAssignment()) {
                                    value = String.format("%s=%s", varName, value);
                                }
                                value = CharEscapers.escapeUriPath(value.toString());
                            }
                        } else if (!Data.isValueOfPrimitiveType(value)) {
                            Map<String, Object> map = getMap(value);
                            value = getMapPropertyValue(varName, map, containsExplodeModifier, compositeOutput);
                        } else {
                            if (compositeOutput.requiresVarAssignment()) {
                                value = String.format("%s=%s", varName, value);
                            }
                            if (compositeOutput.getReservedExpansion()) {
                                value = CharEscapers.escapeUriPathWithoutReserved(value.toString());
                            } else {
                                value = CharEscapers.escapeUriPath(value.toString());
                            }
                        }
                        pathBuf.append(value);
                    }
                }
            }
        }
        if (addUnusedParamsAsQueryParams) {
            GenericUrl.addQueryParams(variableMap.entrySet(), pathBuf);
        }
        return pathBuf.toString();
    }

    private static String getListPropertyValue(String varName, Iterator<?> iterator, boolean containsExplodeModifier, CompositeOutput compositeOutput) {
        String joiner;
        if (!iterator.hasNext()) {
            return "";
        }
        StringBuilder retBuf = new StringBuilder();
        if (containsExplodeModifier) {
            joiner = compositeOutput.getExplodeJoiner();
        } else {
            joiner = ",";
            if (compositeOutput.requiresVarAssignment()) {
                retBuf.append(CharEscapers.escapeUriPath(varName));
                retBuf.append("=");
            }
        }
        while (iterator.hasNext()) {
            if (containsExplodeModifier && compositeOutput.requiresVarAssignment()) {
                retBuf.append(CharEscapers.escapeUriPath(varName));
                retBuf.append("=");
            }
            retBuf.append(compositeOutput.getEncodedValue(iterator.next().toString()));
            if (iterator.hasNext()) {
                retBuf.append(joiner);
            }
        }
        return retBuf.toString();
    }

    private static String getMapPropertyValue(String varName, Map<String, Object> map, boolean containsExplodeModifier, CompositeOutput compositeOutput) {
        String joiner;
        String mapElementsJoiner;
        if (map.isEmpty()) {
            return "";
        }
        StringBuilder retBuf = new StringBuilder();
        if (containsExplodeModifier) {
            joiner = compositeOutput.getExplodeJoiner();
            mapElementsJoiner = "=";
        } else {
            joiner = ",";
            mapElementsJoiner = ",";
            if (compositeOutput.requiresVarAssignment()) {
                retBuf.append(CharEscapers.escapeUriPath(varName));
                retBuf.append("=");
            }
        }
        Iterator<Map.Entry<String, Object>> mapIterator = map.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry<String, Object> entry = mapIterator.next();
            String encodedKey = compositeOutput.getEncodedValue(entry.getKey());
            String encodedValue = compositeOutput.getEncodedValue(entry.getValue().toString());
            retBuf.append(encodedKey);
            retBuf.append(mapElementsJoiner);
            retBuf.append(encodedValue);
            if (mapIterator.hasNext()) {
                retBuf.append(joiner);
            }
        }
        return retBuf.toString();
    }
}
