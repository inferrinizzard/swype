package com.nuance.connect.comm;

import com.nuance.connect.util.Logger;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/* loaded from: classes.dex */
public class CustomProtocolSocketFactory {
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, "SSLSocketFactory");
    private static final List<String> blackList = Arrays.asList("SSL_RSA_WITH_RC4_128_SHA", "SSL_RSA_EXPORT_WITH_RC4_40_MD5", "SSL_DH_anon_WITH_RC4_128_MD5", "SSL_DH_anon_EXPORT_WITH_RC4_40_MD5", "TLS_ECDHE_ECDSA_WITH_RC4_128_SHA", "TLS_ECDHE_RSA_WITH_RC4_128_SHA", "TLS_ECDH_RSA_WITH_RC4_128_SHA", "TLS_ECDH_ECDSA_WITH_RC4_128_SHA", "TLS_ECDHE_ECDSA_WITH_RC4_128_SHA", "TLS_ECDH_anon_WITH_RC4_128_SHA", "TLS_PSK_WITH_RC4_128_SHA", "TLS_RSA_WITH_RC4_128_MD5", "TLS_RSA_WITH_RC4_128_SHA", "TLS_RSA_EXPORT_WITH_RC4_40_MD5");
    private static final Map<String, WeakReference<SSLSocketFactory>> factoryMap = new HashMap();

    /* loaded from: classes.dex */
    private static class BlackListSSLSocketFactory extends SSLSocketFactory {
        private final ArrayList<String> blackList = new ArrayList<>();
        private final String[] defaultList;
        private final SSLSocketFactory delegate;
        private final String[] supportedList;

        BlackListSSLSocketFactory(SSLSocketFactory sSLSocketFactory, List<String> list) {
            this.delegate = sSLSocketFactory;
            this.blackList.addAll(list);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            arrayList2.addAll(Arrays.asList(sSLSocketFactory.getSupportedCipherSuites()));
            arrayList.addAll(Arrays.asList(sSLSocketFactory.getDefaultCipherSuites()));
            arrayList2.removeAll(this.blackList);
            arrayList.removeAll(this.blackList);
            this.supportedList = (String[]) arrayList2.toArray(new String[0]);
            this.defaultList = (String[]) arrayList.toArray(new String[0]);
        }

        @Override // javax.net.SocketFactory
        public Socket createSocket(String str, int i) throws IOException, UnknownHostException {
            Socket createSocket = this.delegate.createSocket(str, i);
            ((SSLSocket) createSocket).setEnabledCipherSuites(getDefaultCipherSuites());
            return createSocket;
        }

        @Override // javax.net.SocketFactory
        public Socket createSocket(String str, int i, InetAddress inetAddress, int i2) throws IOException, UnknownHostException {
            Socket createSocket = this.delegate.createSocket(str, i, inetAddress, i2);
            ((SSLSocket) createSocket).setEnabledCipherSuites(getDefaultCipherSuites());
            return createSocket;
        }

        @Override // javax.net.SocketFactory
        public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
            Socket createSocket = this.delegate.createSocket(inetAddress, i);
            ((SSLSocket) createSocket).setEnabledCipherSuites(getDefaultCipherSuites());
            return createSocket;
        }

        @Override // javax.net.SocketFactory
        public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress2, int i2) throws IOException {
            Socket createSocket = this.delegate.createSocket(inetAddress, i, inetAddress2, i2);
            ((SSLSocket) createSocket).setEnabledCipherSuites(getDefaultCipherSuites());
            return createSocket;
        }

        @Override // javax.net.ssl.SSLSocketFactory
        public Socket createSocket(Socket socket, String str, int i, boolean z) throws IOException {
            Socket createSocket = this.delegate.createSocket(socket, str, i, z);
            ((SSLSocket) createSocket).setEnabledCipherSuites(getDefaultCipherSuites());
            return createSocket;
        }

        @Override // javax.net.ssl.SSLSocketFactory
        public String[] getDefaultCipherSuites() {
            return (String[]) this.defaultList.clone();
        }

        @Override // javax.net.ssl.SSLSocketFactory
        public String[] getSupportedCipherSuites() {
            return (String[]) this.supportedList.clone();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static synchronized SSLSocketFactory createSocketFactory(String str) {
        SSLSocketFactory sSLSocketFactory;
        synchronized (CustomProtocolSocketFactory.class) {
            if (str == null) {
                log.d("protocol == null");
                sSLSocketFactory = null;
            } else {
                if (factoryMap.containsKey(str)) {
                    sSLSocketFactory = factoryMap.get(str).get();
                    if (sSLSocketFactory == null) {
                        factoryMap.remove(str);
                    }
                }
                try {
                    try {
                        try {
                            try {
                                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                                keyStore.load(null, null);
                                KeyStore keyStore2 = KeyStore.getInstance("AndroidCAStore");
                                keyStore2.load(null, null);
                                Enumeration<String> aliases = keyStore2.aliases();
                                while (aliases.hasMoreElements()) {
                                    String nextElement = aliases.nextElement();
                                    if (nextElement.startsWith("system:")) {
                                        keyStore.setCertificateEntry(nextElement, keyStore2.getCertificate(nextElement));
                                    }
                                }
                                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                                trustManagerFactory.init(keyStore);
                                SSLContext sSLContext = SSLContext.getInstance(str);
                                sSLContext.init(null, trustManagerFactory.getTrustManagers(), null);
                                SSLSocketFactory socketFactory = sSLContext.getSocketFactory();
                                if (socketFactory != null) {
                                    sSLSocketFactory = new BlackListSSLSocketFactory(socketFactory, blackList);
                                    factoryMap.put(str, new WeakReference<>(sSLSocketFactory));
                                }
                            } catch (IOException e) {
                                log.e("IOException: ", e.getMessage());
                            } catch (CertificateException e2) {
                                log.e("CertificateException: ", e2.getMessage());
                            }
                        } catch (KeyManagementException e3) {
                            log.e("KeyManagementException: ", e3.getMessage());
                        }
                    } catch (KeyStoreException e4) {
                        log.e("KeyStoreException: ", e4.getMessage());
                    }
                } catch (NoSuchAlgorithmException e5) {
                    log.e("NoSuchAlgorithmException: ", e5.getMessage());
                }
                sSLSocketFactory = null;
            }
        }
        return sSLSocketFactory;
    }
}
