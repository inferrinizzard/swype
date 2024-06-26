package com.google.api.client.json.webtoken;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.webtoken.JsonWebToken;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.Key;
import com.google.api.client.util.SecurityUtils;
import com.google.api.client.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/* loaded from: classes.dex */
public class JsonWebSignature extends JsonWebToken {
    private final byte[] signatureBytes;
    private final byte[] signedContentBytes;

    public JsonWebSignature(Header header, JsonWebToken.Payload payload, byte[] signatureBytes, byte[] signedContentBytes) {
        super(header, payload);
        this.signatureBytes = (byte[]) Preconditions.checkNotNull(signatureBytes);
        this.signedContentBytes = (byte[]) Preconditions.checkNotNull(signedContentBytes);
    }

    /* loaded from: classes.dex */
    public static class Header extends JsonWebToken.Header {

        @Key("alg")
        private String algorithm;

        @Key("crit")
        private List<String> critical;

        @Key("jwk")
        private String jwk;

        @Key("jku")
        private String jwkUrl;

        @Key("kid")
        private String keyId;

        @Key("x5c")
        private List<String> x509Certificates;

        @Key("x5t")
        private String x509Thumbprint;

        @Key("x5u")
        private String x509Url;

        @Override // com.google.api.client.json.webtoken.JsonWebToken.Header
        public Header setType(String type) {
            super.setType(type);
            return this;
        }

        public final String getAlgorithm() {
            return this.algorithm;
        }

        public Header setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public final String getJwkUrl() {
            return this.jwkUrl;
        }

        public Header setJwkUrl(String jwkUrl) {
            this.jwkUrl = jwkUrl;
            return this;
        }

        public final String getJwk() {
            return this.jwk;
        }

        public Header setJwk(String jwk) {
            this.jwk = jwk;
            return this;
        }

        public final String getKeyId() {
            return this.keyId;
        }

        public Header setKeyId(String keyId) {
            this.keyId = keyId;
            return this;
        }

        public final String getX509Url() {
            return this.x509Url;
        }

        public Header setX509Url(String x509Url) {
            this.x509Url = x509Url;
            return this;
        }

        public final String getX509Thumbprint() {
            return this.x509Thumbprint;
        }

        public Header setX509Thumbprint(String x509Thumbprint) {
            this.x509Thumbprint = x509Thumbprint;
            return this;
        }

        @Deprecated
        public final String getX509Certificate() {
            if (this.x509Certificates == null || this.x509Certificates.isEmpty()) {
                return null;
            }
            return this.x509Certificates.get(0);
        }

        public final List<String> getX509Certificates() {
            return this.x509Certificates;
        }

        @Deprecated
        public Header setX509Certificate(String x509Certificate) {
            ArrayList<String> x509Certificates = new ArrayList<>();
            x509Certificates.add(x509Certificate);
            this.x509Certificates = x509Certificates;
            return this;
        }

        public Header setX509Certificates(List<String> x509Certificates) {
            this.x509Certificates = x509Certificates;
            return this;
        }

        public final List<String> getCritical() {
            return this.critical;
        }

        public Header setCritical(List<String> critical) {
            this.critical = critical;
            return this;
        }

        @Override // com.google.api.client.json.webtoken.JsonWebToken.Header, com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData
        public Header set(String fieldName, Object value) {
            return (Header) super.set(fieldName, value);
        }

        @Override // com.google.api.client.json.webtoken.JsonWebToken.Header, com.google.api.client.json.GenericJson, com.google.api.client.util.GenericData, java.util.AbstractMap
        public Header clone() {
            return (Header) super.clone();
        }
    }

    @Override // com.google.api.client.json.webtoken.JsonWebToken
    public Header getHeader() {
        return (Header) super.getHeader();
    }

    public final boolean verifySignature(PublicKey publicKey) throws GeneralSecurityException {
        String algorithm = getHeader().getAlgorithm();
        if (!"RS256".equals(algorithm)) {
            return false;
        }
        Signature signatureAlg = Signature.getInstance("SHA256withRSA");
        return SecurityUtils.verify(signatureAlg, publicKey, this.signatureBytes, this.signedContentBytes);
    }

    public final X509Certificate verifySignature(X509TrustManager trustManager) throws GeneralSecurityException {
        List<String> x509Certificates = getHeader().getX509Certificates();
        if (x509Certificates == null || x509Certificates.isEmpty()) {
            return null;
        }
        String algorithm = getHeader().getAlgorithm();
        if (!"RS256".equals(algorithm)) {
            return null;
        }
        Signature signatureAlg = Signature.getInstance("SHA256withRSA");
        return SecurityUtils.verify(signatureAlg, trustManager, x509Certificates, this.signatureBytes, this.signedContentBytes);
    }

    public final X509Certificate verifySignature() throws GeneralSecurityException {
        X509TrustManager trustManager = getDefaultX509TrustManager();
        if (trustManager == null) {
            return null;
        }
        return verifySignature(trustManager);
    }

    private static X509TrustManager getDefaultX509TrustManager() {
        try {
            TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            factory.init((KeyStore) null);
            TrustManager[] arr$ = factory.getTrustManagers();
            for (TrustManager manager : arr$) {
                if (manager instanceof X509TrustManager) {
                    return (X509TrustManager) manager;
                }
            }
            return null;
        } catch (KeyStoreException e) {
            return null;
        } catch (NoSuchAlgorithmException e2) {
            return null;
        }
    }

    public final byte[] getSignatureBytes() {
        return this.signatureBytes;
    }

    public final byte[] getSignedContentBytes() {
        return this.signedContentBytes;
    }

    public static JsonWebSignature parse(JsonFactory jsonFactory, String tokenString) throws IOException {
        return parser(jsonFactory).parse(tokenString);
    }

    public static Parser parser(JsonFactory jsonFactory) {
        return new Parser(jsonFactory);
    }

    /* loaded from: classes.dex */
    public static final class Parser {
        private final JsonFactory jsonFactory;
        private Class<? extends Header> headerClass = Header.class;
        private Class<? extends JsonWebToken.Payload> payloadClass = JsonWebToken.Payload.class;

        public Parser(JsonFactory jsonFactory) {
            this.jsonFactory = (JsonFactory) Preconditions.checkNotNull(jsonFactory);
        }

        public final Class<? extends Header> getHeaderClass() {
            return this.headerClass;
        }

        public final Parser setHeaderClass(Class<? extends Header> headerClass) {
            this.headerClass = headerClass;
            return this;
        }

        public final Class<? extends JsonWebToken.Payload> getPayloadClass() {
            return this.payloadClass;
        }

        public final Parser setPayloadClass(Class<? extends JsonWebToken.Payload> payloadClass) {
            this.payloadClass = payloadClass;
            return this;
        }

        public final JsonFactory getJsonFactory() {
            return this.jsonFactory;
        }

        public final JsonWebSignature parse(String tokenString) throws IOException {
            int firstDot = tokenString.indexOf(46);
            Preconditions.checkArgument(firstDot != -1);
            byte[] headerBytes = Base64.decodeBase64(tokenString.substring(0, firstDot));
            int secondDot = tokenString.indexOf(46, firstDot + 1);
            Preconditions.checkArgument(secondDot != -1);
            Preconditions.checkArgument(tokenString.indexOf(46, secondDot + 1) == -1);
            byte[] payloadBytes = Base64.decodeBase64(tokenString.substring(firstDot + 1, secondDot));
            byte[] signatureBytes = Base64.decodeBase64(tokenString.substring(secondDot + 1));
            byte[] signedContentBytes = StringUtils.getBytesUtf8(tokenString.substring(0, secondDot));
            Header header = (Header) this.jsonFactory.fromInputStream(new ByteArrayInputStream(headerBytes), this.headerClass);
            Preconditions.checkArgument(header.getAlgorithm() != null);
            JsonWebToken.Payload payload = (JsonWebToken.Payload) this.jsonFactory.fromInputStream(new ByteArrayInputStream(payloadBytes), this.payloadClass);
            return new JsonWebSignature(header, payload, signatureBytes, signedContentBytes);
        }
    }

    public static String signUsingRsaSha256(PrivateKey privateKey, JsonFactory jsonFactory, Header header, JsonWebToken.Payload payload) throws GeneralSecurityException, IOException {
        String content = Base64.encodeBase64URLSafeString(jsonFactory.toByteArray(header)) + "." + Base64.encodeBase64URLSafeString(jsonFactory.toByteArray(payload));
        byte[] contentBytes = StringUtils.getBytesUtf8(content);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(contentBytes);
        byte[] signature2 = signature.sign();
        return content + "." + Base64.encodeBase64URLSafeString(signature2);
    }
}
