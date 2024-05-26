package com.google.api.client.repackaged.com.google.common.base;

import java.util.Arrays;

/* loaded from: classes.dex */
public abstract class CharMatcher {
    public static final CharMatcher ANY;
    public static final CharMatcher DIGIT;
    public static final CharMatcher INVISIBLE;
    public static final CharMatcher JAVA_DIGIT;
    public static final CharMatcher JAVA_ISO_CONTROL;
    public static final CharMatcher JAVA_LETTER;
    public static final CharMatcher JAVA_LETTER_OR_DIGIT;
    public static final CharMatcher JAVA_LOWER_CASE;
    public static final CharMatcher JAVA_UPPER_CASE;
    private static final String NINES;
    public static final CharMatcher NONE;
    public static final CharMatcher SINGLE_WIDTH;
    public static final CharMatcher WHITESPACE;
    static final int WHITESPACE_SHIFT;
    final String description;
    public static final CharMatcher BREAKING_WHITESPACE = new CharMatcher() { // from class: com.google.api.client.repackaged.com.google.common.base.CharMatcher.1
        @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
        public final boolean matches(char c) {
            switch (c) {
                case '\t':
                case '\n':
                case 11:
                case '\f':
                case '\r':
                case ' ':
                case 133:
                case 5760:
                case 8232:
                case 8233:
                case 8287:
                case 12288:
                    return true;
                case 8199:
                    return false;
                default:
                    return c >= 8192 && c <= 8202;
            }
        }

        @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
        public final String toString() {
            return "CharMatcher.BREAKING_WHITESPACE";
        }
    };
    public static final CharMatcher ASCII = inRange(0, 127, "CharMatcher.ASCII");

    public abstract boolean matches(char c);

    static {
        StringBuilder builder = new StringBuilder(31);
        for (int i = 0; i < 31; i++) {
            builder.append((char) ("0٠۰߀०০੦૦୦௦౦೦൦๐໐༠၀႐០᠐᥆᧐᭐᮰᱀᱐꘠꣐꤀꩐０".charAt(i) + '\t'));
        }
        NINES = builder.toString();
        DIGIT = new RangesMatcher("CharMatcher.DIGIT", "0٠۰߀०০੦૦୦௦౦೦൦๐໐༠၀႐០᠐᥆᧐᭐᮰᱀᱐꘠꣐꤀꩐０".toCharArray(), NINES.toCharArray());
        JAVA_DIGIT = new CharMatcher("CharMatcher.JAVA_DIGIT") { // from class: com.google.api.client.repackaged.com.google.common.base.CharMatcher.2
            @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
            public final boolean matches(char c) {
                return Character.isDigit(c);
            }
        };
        JAVA_LETTER = new CharMatcher("CharMatcher.JAVA_LETTER") { // from class: com.google.api.client.repackaged.com.google.common.base.CharMatcher.3
            @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
            public final boolean matches(char c) {
                return Character.isLetter(c);
            }
        };
        JAVA_LETTER_OR_DIGIT = new CharMatcher("CharMatcher.JAVA_LETTER_OR_DIGIT") { // from class: com.google.api.client.repackaged.com.google.common.base.CharMatcher.4
            @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
            public final boolean matches(char c) {
                return Character.isLetterOrDigit(c);
            }
        };
        JAVA_UPPER_CASE = new CharMatcher("CharMatcher.JAVA_UPPER_CASE") { // from class: com.google.api.client.repackaged.com.google.common.base.CharMatcher.5
            @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
            public final boolean matches(char c) {
                return Character.isUpperCase(c);
            }
        };
        JAVA_LOWER_CASE = new CharMatcher("CharMatcher.JAVA_LOWER_CASE") { // from class: com.google.api.client.repackaged.com.google.common.base.CharMatcher.6
            @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
            public final boolean matches(char c) {
                return Character.isLowerCase(c);
            }
        };
        JAVA_ISO_CONTROL = inRange((char) 0, (char) 31).or(inRange((char) 127, (char) 159)).withToString("CharMatcher.JAVA_ISO_CONTROL");
        INVISIBLE = new RangesMatcher("CharMatcher.INVISIBLE", "\u0000\u007f\u00ad\u0600\u061c\u06dd\u070f\u1680\u180e\u2000\u2028\u205f\u2066\u2067\u2068\u2069\u206a\u3000\ud800\ufeff\ufff9\ufffa".toCharArray(), "  \u00ad\u0604\u061c\u06dd\u070f\u1680\u180e\u200f \u2064\u2066\u2067\u2068\u2069\u206f\u3000\uf8ff\ufeff\ufff9\ufffb".toCharArray());
        SINGLE_WIDTH = new RangesMatcher("CharMatcher.SINGLE_WIDTH", "\u0000־א׳\u0600ݐ\u0e00Ḁ℀ﭐﹰ｡".toCharArray(), "ӹ־ת״ۿݿ\u0e7f₯℺﷿\ufeffￜ".toCharArray());
        ANY = new FastMatcher("CharMatcher.ANY") { // from class: com.google.api.client.repackaged.com.google.common.base.CharMatcher.7
            @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
            public final boolean matches(char c) {
                return true;
            }

            @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
            public final int indexIn(CharSequence sequence, int start) {
                int length = sequence.length();
                Preconditions.checkPositionIndex(start, length);
                if (start == length) {
                    return -1;
                }
                return start;
            }

            @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
            public final CharMatcher or(CharMatcher other) {
                Preconditions.checkNotNull(other);
                return this;
            }
        };
        NONE = new FastMatcher("CharMatcher.NONE") { // from class: com.google.api.client.repackaged.com.google.common.base.CharMatcher.8
            @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
            public final boolean matches(char c) {
                return false;
            }

            @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
            public final int indexIn(CharSequence sequence, int start) {
                int length = sequence.length();
                Preconditions.checkPositionIndex(start, length);
                return -1;
            }

            @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
            public final CharMatcher or(CharMatcher other) {
                return (CharMatcher) Preconditions.checkNotNull(other);
            }
        };
        WHITESPACE_SHIFT = Integer.numberOfLeadingZeros(31);
        WHITESPACE = new FastMatcher("WHITESPACE") { // from class: com.google.api.client.repackaged.com.google.common.base.CharMatcher.15
            @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
            public final boolean matches(char c) {
                return "\u2002\u3000\r\u0085\u200a\u2005\u2000\u3000\u2029\u000b\u3000\u2008\u2003\u205f\u3000\u1680\t \u2006\u2001  \f\u2009\u3000\u2004\u3000\u3000\u2028\n \u3000".charAt((48906 * c) >>> WHITESPACE_SHIFT) == c;
            }
        };
    }

    /* loaded from: classes.dex */
    private static class RangesMatcher extends CharMatcher {
        private final char[] rangeEnds;
        private final char[] rangeStarts;

        RangesMatcher(String description, char[] rangeStarts, char[] rangeEnds) {
            super(description);
            this.rangeStarts = rangeStarts;
            this.rangeEnds = rangeEnds;
            Preconditions.checkArgument(rangeStarts.length == rangeEnds.length);
            for (int i = 0; i < rangeStarts.length; i++) {
                Preconditions.checkArgument(rangeStarts[i] <= rangeEnds[i]);
                if (i + 1 < rangeStarts.length) {
                    Preconditions.checkArgument(rangeEnds[i] < rangeStarts[i + 1]);
                }
            }
        }

        @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
        public final boolean matches(char c) {
            int index = Arrays.binarySearch(this.rangeStarts, c);
            if (index >= 0) {
                return true;
            }
            int index2 = (index ^ (-1)) - 1;
            return index2 >= 0 && c <= this.rangeEnds[index2];
        }
    }

    private static String showCharacter(char c) {
        char[] tmp = {'\\', 'u', 0, 0, 0, 0};
        for (int i = 0; i < 4; i++) {
            tmp[5 - i] = "0123456789ABCDEF".charAt(c & 15);
            c = (char) (c >> 4);
        }
        return String.copyValueOf(tmp);
    }

    public static CharMatcher is$2a9d1698() {
        String description = "CharMatcher.is('" + showCharacter(',') + "')";
        return new FastMatcher(description, ',') { // from class: com.google.api.client.repackaged.com.google.common.base.CharMatcher.9
            final /* synthetic */ char val$match = ',';

            @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
            public final boolean matches(char c) {
                return c == this.val$match;
            }

            @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
            public final CharMatcher or(CharMatcher other) {
                return other.matches(this.val$match) ? other : super.or(other);
            }
        };
    }

    private static CharMatcher inRange(char startInclusive, char endInclusive) {
        Preconditions.checkArgument(endInclusive >= startInclusive);
        String description = "CharMatcher.inRange('" + showCharacter(startInclusive) + "', '" + showCharacter(endInclusive) + "')";
        return inRange(startInclusive, endInclusive, description);
    }

    private static CharMatcher inRange(final char startInclusive, final char endInclusive, String description) {
        return new FastMatcher(description) { // from class: com.google.api.client.repackaged.com.google.common.base.CharMatcher.13
            @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
            public final boolean matches(char c) {
                return startInclusive <= c && c <= endInclusive;
            }
        };
    }

    CharMatcher(String description) {
        this.description = description;
    }

    protected CharMatcher() {
        this.description = super.toString();
    }

    public CharMatcher or(CharMatcher other) {
        return new Or(this, (CharMatcher) Preconditions.checkNotNull(other));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Or extends CharMatcher {
        final CharMatcher first;
        final CharMatcher second;

        private Or(CharMatcher a, CharMatcher b, String description) {
            super(description);
            this.first = (CharMatcher) Preconditions.checkNotNull(a);
            this.second = (CharMatcher) Preconditions.checkNotNull(b);
        }

        Or(CharMatcher a, CharMatcher b) {
            this(a, b, "CharMatcher.or(" + a + ", " + b + ")");
        }

        @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
        public final boolean matches(char c) {
            return this.first.matches(c) || this.second.matches(c);
        }

        @Override // com.google.api.client.repackaged.com.google.common.base.CharMatcher
        final CharMatcher withToString(String description) {
            return new Or(this.first, this.second, description);
        }
    }

    CharMatcher withToString(String description) {
        throw new UnsupportedOperationException();
    }

    /* loaded from: classes.dex */
    static abstract class FastMatcher extends CharMatcher {
        FastMatcher(String description) {
            super(description);
        }
    }

    public int indexIn(CharSequence sequence, int start) {
        int length = sequence.length();
        Preconditions.checkPositionIndex(start, length);
        for (int i = start; i < length; i++) {
            if (matches(sequence.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public String toString() {
        return this.description;
    }
}
