package com.nuance.nmsp.client.util.dictationresult;

/* loaded from: classes.dex */
public interface Token {

    /* loaded from: classes.dex */
    public static class TokenType {
        public static final TokenType VOICE_TOKEN = new TokenType();
        public static final TokenType TEXT_TOKEN = new TokenType();

        private TokenType() {
        }
    }

    double getConfidenceScore();

    long getEndTime();

    long getStartTime();

    TokenType getTokenType();

    boolean hasNoSpaceAfterDirective();

    boolean hasNoSpaceBeforeDirective();

    String toString();
}
