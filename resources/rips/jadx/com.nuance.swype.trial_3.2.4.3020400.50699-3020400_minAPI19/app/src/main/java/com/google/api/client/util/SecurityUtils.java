package com.google.api.client.util;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.X509TrustManager;

/* loaded from: classes.dex */
public final class SecurityUtils {
    public static void loadKeyStore(KeyStore keyStore, InputStream keyStream, String storePass) throws IOException, GeneralSecurityException {
        try {
            keyStore.load(keyStream, storePass.toCharArray());
        } finally {
            keyStream.close();
        }
    }

    public static boolean verify(Signature signatureAlgorithm, PublicKey publicKey, byte[] signatureBytes, byte[] contentBytes) throws InvalidKeyException, SignatureException {
        signatureAlgorithm.initVerify(publicKey);
        signatureAlgorithm.update(contentBytes);
        try {
            return signatureAlgorithm.verify(signatureBytes);
        } catch (SignatureException e) {
            return false;
        }
    }

    public static void loadKeyStoreFromCertificates(KeyStore keyStore, CertificateFactory certificateFactory, InputStream certificateStream) throws GeneralSecurityException {
        int i = 0;
        for (Certificate cert : certificateFactory.generateCertificates(certificateStream)) {
            keyStore.setCertificateEntry(String.valueOf(i), cert);
            i++;
        }
    }

    public static X509Certificate verify(Signature signatureAlgorithm, X509TrustManager trustManager, List<String> certChainBase64, byte[] signatureBytes, byte[] contentBytes) throws InvalidKeyException, SignatureException {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate[] certificates = new X509Certificate[certChainBase64.size()];
            int currentCert = 0;
            Iterator i$ = certChainBase64.iterator();
            while (i$.hasNext()) {
                byte[] certDer = Base64.decodeBase64(i$.next());
                ByteArrayInputStream bis = new ByteArrayInputStream(certDer);
                try {
                    Certificate cert = certificateFactory.generateCertificate(bis);
                    if (!(cert instanceof X509Certificate)) {
                        return null;
                    }
                    int currentCert2 = currentCert + 1;
                    try {
                        certificates[currentCert] = (X509Certificate) cert;
                        currentCert = currentCert2;
                    } catch (CertificateException e) {
                        return null;
                    }
                } catch (CertificateException e2) {
                }
            }
            try {
                trustManager.checkServerTrusted(certificates, "RSA");
                PublicKey pubKey = certificates[0].getPublicKey();
                if (verify(signatureAlgorithm, pubKey, signatureBytes, contentBytes)) {
                    return certificates[0];
                }
                return null;
            } catch (CertificateException e3) {
                return null;
            }
        } catch (CertificateException e4) {
            return null;
        }
    }
}
