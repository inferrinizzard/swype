package com.nuance.nmsp.client.sdk.oem.socket.ssl;

import com.nuance.nmsp.client.sdk.common.util.Base64;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/* loaded from: classes.dex */
public class NmspX509TrustManager implements X509TrustManager {
    private static X509TrustManager b;
    private SSLSettings a;

    public NmspX509TrustManager(SSLSettings sSLSettings) throws GeneralSecurityException {
        this.a = sSLSettings;
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        b = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
    }

    @Override // javax.net.ssl.X509TrustManager
    public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
    }

    @Override // javax.net.ssl.X509TrustManager
    public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
        boolean z;
        boolean z2 = false;
        try {
            b.checkServerTrusted(x509CertificateArr, str);
        } catch (CertificateException e) {
            if (!this.a.enableSelfSignedCert) {
                throw e;
            }
        }
        if (this.a.certSummary != null) {
            int i = 0;
            while (true) {
                if (i >= x509CertificateArr.length) {
                    z = false;
                    break;
                }
                String name = x509CertificateArr[i].getSubjectDN().getName();
                int indexOf = name.indexOf("CN=");
                if (indexOf != -1) {
                    int i2 = indexOf + 3;
                    int indexOf2 = name.indexOf(44, i2);
                    if (this.a.certSummary.equals(indexOf2 == -1 ? name.substring(i2) : name.substring(i2, indexOf2))) {
                        z = true;
                        break;
                    }
                }
                i++;
            }
            if (!z) {
                throw new CertificateException("certificate summary is not identical");
            }
        }
        if (this.a.certData != null) {
            int i3 = 0;
            while (true) {
                if (i3 >= x509CertificateArr.length) {
                    break;
                }
                if (this.a.certData.equals(Base64.encode(x509CertificateArr[i3].getEncoded()))) {
                    z2 = true;
                    break;
                }
                i3++;
            }
            if (!z2) {
                throw new CertificateException("certificate data is not identical");
            }
        }
    }

    @Override // javax.net.ssl.X509TrustManager
    public X509Certificate[] getAcceptedIssuers() {
        return b.getAcceptedIssuers();
    }
}
