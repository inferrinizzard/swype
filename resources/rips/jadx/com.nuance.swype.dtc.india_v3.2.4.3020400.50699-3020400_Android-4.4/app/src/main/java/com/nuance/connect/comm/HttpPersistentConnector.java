package com.nuance.connect.comm;

import com.nuance.connect.comm.Command;
import com.nuance.connect.comm.CommandQueue;
import com.nuance.connect.util.Logger;
import com.nuance.swype.input.R;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;

/* loaded from: classes.dex */
public class HttpPersistentConnector extends Connector {
    private static final int MAX_ERROR_BODY_SIZE = 1000;
    private boolean connected;
    private int connectionTimeoutMillis;
    private final Logger.Log log;
    private PersistantConnectionConfig persistantConfig;
    private int readTimeoutMillis;
    private SSLSocketFactory sslSocketFactory;

    public HttpPersistentConnector(MessageSendingBus messageSendingBus, CommandQueue.ConnectionStatus connectionStatus, ConnectorCallback connectorCallback, AnalyticsDataUsageScribe analyticsDataUsageScribe, String str, String str2) {
        super(messageSendingBus, connectionStatus, connectorCallback, analyticsDataUsageScribe, str2);
        this.log = Logger.getThreadLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
        this.connected = false;
        this.connectionTimeoutMillis = 0;
        this.readTimeoutMillis = 0;
        updateMinimumSSLProtocol(str);
        setCompressionThreshold(-1);
    }

    private HttpURLConnection connectToUrl() throws IOException, MalformedURLException, URISyntaxException {
        URL url;
        URL url2 = new URL(this.persistantConfig.getURL());
        this.log.d("connectToUrl url=" + url2.toString());
        try {
            url2.toURI();
            url = url2;
        } catch (URISyntaxException e) {
            url = new URI(url2.getProtocol(), url2.getUserInfo(), url2.getHost(), url2.getPort(), url2.getPath(), url2.getQuery(), url2.getRef()).toURL();
        }
        if (!url.getProtocol().equals("https")) {
            return (HttpURLConnection) url.openConnection();
        }
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
        synchronized (this) {
            if (this.sslSocketFactory == null) {
                throw new IOException("Could not set up the SSL protocol");
            }
            httpsURLConnection.setSSLSocketFactory(this.sslSocketFactory);
        }
        return httpsURLConnection;
    }

    private String getErrorBody(HttpURLConnection httpURLConnection) {
        int contentLength;
        BufferedReader bufferedReader;
        try {
            contentLength = httpURLConnection.getContentLength();
        } catch (Exception e) {
            this.log.d("getErrorBody() - generating error body - Exception ", e.getMessage());
        }
        if (contentLength <= 0 || contentLength >= MAX_ERROR_BODY_SIZE) {
            this.log.d("getErrorBody() - error body is too long: ", Integer.valueOf(contentLength));
            return null;
        }
        InputStream errorStream = httpURLConnection.getErrorStream();
        try {
            StringBuilder sb = new StringBuilder();
            bufferedReader = new BufferedReader(new InputStreamReader(errorStream, "UTF-8"), 1024);
            try {
                sb.append(bufferedReader.readLine());
                this.log.d("getErrorBody() - error status message: ", sb.toString());
                String sb2 = sb.toString();
                if (errorStream != null) {
                    errorStream.close();
                }
                bufferedReader.close();
                return sb2;
            } catch (Throwable th) {
                th = th;
                if (errorStream != null) {
                    errorStream.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            bufferedReader = null;
        }
    }

    public void connect() {
        this.log.d("connect() connected=" + this.connected);
        if (this.connected) {
            return;
        }
        HttpURLConnection createConnection = createConnection();
        if (createConnection == null) {
            this.log.e("connect - unable to connect");
            return;
        }
        try {
            createConnection.connect();
        } catch (IOException e) {
        } catch (Exception e2) {
        }
    }

    protected synchronized HttpURLConnection createConnection() {
        HttpURLConnection httpURLConnection;
        this.log.d("createConnection called");
        this.log.d("createConnection -- creating new connection");
        try {
            try {
                httpURLConnection = connectToUrl();
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setConnectTimeout(this.connectionTimeoutMillis);
                httpURLConnection.setReadTimeout(this.readTimeoutMillis);
                if (allowsOutput()) {
                    httpURLConnection.setDoOutput(true);
                }
                httpURLConnection.setAllowUserInteraction(false);
                httpURLConnection.setChunkedStreamingMode(0);
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setRequestMethod("POST");
            } catch (IOException e) {
                this.log.e("Error Creating Connection: ", e.getMessage());
                httpURLConnection = null;
            } catch (Exception e2) {
                this.log.e("Error Creating Connection: ", e2.getMessage());
                httpURLConnection = null;
            }
        } catch (URISyntaxException e3) {
            this.log.e("Error Creating Connection: ", e3.getMessage());
            httpURLConnection = null;
        }
        return httpURLConnection;
    }

    @Override // com.nuance.connect.comm.Connector
    public void destroyConnection() {
        this.analyticsScribe.flush();
    }

    public void disconnect() {
        this.log.d("HttpPersistentConnector disconnect");
    }

    protected boolean isConnected() {
        this.log.d("isConnected() - ", Boolean.valueOf(this.connected));
        return this.connected;
    }

    @Override // com.nuance.connect.comm.Connector
    public boolean processCommand(Command command, Transaction transaction, CommandQueue.NetworkExpirer networkExpirer) throws ConnectorException {
        HttpURLConnection httpURLConnection;
        boolean z;
        GZIPOutputStream gZIPOutputStream;
        InputStream inputStream = null;
        String shortString = command.shortString();
        this.analyticsScribe.mark();
        try {
            try {
            } catch (ConnectorException e) {
                throw e;
            } catch (NullPointerException e2) {
                e = e2;
            } catch (MalformedURLException e3) {
                e = e3;
            } catch (ProtocolException e4) {
                e = e4;
            } catch (SSLException e5) {
                e = e5;
            } catch (IOException e6) {
                e = e6;
            } catch (Exception e7) {
                e = e7;
            } catch (Throwable th) {
                th = th;
                httpURLConnection = null;
            }
        } catch (Throwable th2) {
            th = th2;
        }
        if (command.canceled) {
            throw new TransactionCancelConnectorException("transaction cancelled.", 3);
        }
        httpURLConnection = createConnection();
        try {
            this.log.d("processCommand()  URL: [", httpURLConnection.getURL(), "]");
            long j = 0;
            if (command.hasBody) {
                byte[] generateBody = generateBody(command);
                httpURLConnection.setRequestProperty("Content-Type", command.getContentType());
                j = generateBody.length;
                if (shouldCompress(j)) {
                    this.log.d("Compressing Content");
                    httpURLConnection.setRequestProperty("Content-Encoding", "gzip");
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    try {
                        gZIPOutputStream = new GZIPOutputStream(outputStream, HardKeyboardManager.META_CTRL_ON);
                    } catch (Throwable th3) {
                        th = th3;
                        gZIPOutputStream = null;
                    }
                    try {
                        gZIPOutputStream.write(generateBody);
                        gZIPOutputStream.finish();
                        gZIPOutputStream.close();
                        outputStream.close();
                    } catch (Throwable th4) {
                        th = th4;
                        if (gZIPOutputStream != null) {
                            gZIPOutputStream.close();
                        }
                        outputStream.close();
                        throw th;
                    }
                } else {
                    this.log.d("Writing to output stream");
                    OutputStream outputStream2 = httpURLConnection.getOutputStream();
                    outputStream2.write(generateBody);
                    outputStream2.close();
                }
            }
            httpURLConnection.connect();
            this.analyticsScribe.writeRequest(command, j);
            networkExpirer.tick();
            try {
                try {
                    try {
                        int responseCode = httpURLConnection.getResponseCode();
                        this.log.d("serverConn.getResponseCode(", Integer.valueOf(responseCode), ") command: ", shortString);
                        switch (responseCode) {
                            case 200:
                                try {
                                    try {
                                        try {
                                            Response response = new Response();
                                            response.delayedFor = -1;
                                            try {
                                                int contentLength = httpURLConnection.getContentLength();
                                                this.analyticsScribe.writeResponse(command, contentLength);
                                                this.log.d(shortString, " Response Content Type: ", httpURLConnection.getContentType());
                                                if (contentLength == 0 || !(command.dataResponse.equals(Command.DATA_SOURCE.JSON) || command.dataResponse.equals(Command.DATA_SOURCE.PBUFF))) {
                                                    z = false;
                                                } else {
                                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                    inputStream = httpURLConnection.getInputStream();
                                                    byte[] bArr = new byte[1024];
                                                    while (true) {
                                                        int read = inputStream.read(bArr);
                                                        if (read != -1) {
                                                            byteArrayOutputStream.write(bArr, 0, read);
                                                        } else {
                                                            inputStream.close();
                                                            z = processResult(response, command, byteArrayOutputStream.toByteArray());
                                                        }
                                                    }
                                                }
                                                if (inputStream != null) {
                                                    inputStream.close();
                                                }
                                                networkExpirer.complete();
                                                if (httpURLConnection != null) {
                                                    try {
                                                        httpURLConnection.disconnect();
                                                    } catch (Exception e8) {
                                                        this.log.w("Exception closing server connection.");
                                                    }
                                                }
                                                if (!z) {
                                                    this.connectorCallback.onSuccess(command);
                                                } else if (networkExpirer.isExpired()) {
                                                    throw new IOConnectorException("Expirer has expired.", 2);
                                                }
                                                return !z;
                                            } catch (Throwable th5) {
                                                if (inputStream != null) {
                                                    inputStream.close();
                                                }
                                                throw th5;
                                            }
                                        } catch (ConnectorException e9) {
                                            throw e9;
                                        } catch (IOException e10) {
                                            this.log.d("processCommand() - receive - IOException ", e10.getMessage());
                                            throw new IOConnectorException(e10.getMessage(), 2);
                                        }
                                    } catch (SSLException e11) {
                                        this.log.d("processCommand() - receive - SSLException ", e11.getMessage());
                                        throw new IOConnectorException(e11.getMessage(), 4);
                                    }
                                } catch (Exception e12) {
                                    this.log.d("processCommand() - receive - Exception ", e12.getMessage());
                                    throw new IOConnectorException(e12.getMessage(), 2);
                                }
                            case 403:
                            case R.styleable.ThemeTemplate_symKeyboardFeedbackHandwriting /* 406 */:
                                throw new IOConnectorException(String.valueOf(responseCode), 7);
                            default:
                                String errorBody = getErrorBody(httpURLConnection);
                                if (errorBody == null) {
                                    errorBody = String.valueOf(responseCode);
                                }
                                throw new IOConnectorException(errorBody, 2);
                        }
                    } catch (ConnectorException e13) {
                        throw e13;
                    } catch (SSLException e14) {
                        this.log.d("SSLException trying to get response code ", e14.getMessage());
                        throw new IOConnectorException(e14.getMessage(), 4);
                    }
                } catch (IOException e15) {
                    this.log.d("IOException trying to get response code ", e15.getMessage());
                    throw new IOConnectorException(e15.getMessage(), 2);
                } catch (NullPointerException e16) {
                    this.log.d("NPE trying to get response code ", e16.getMessage());
                    throw new IOConnectorException(e16.getMessage(), 2);
                }
            } catch (Exception e17) {
                this.log.d("Exception trying to get response code ", e17.getMessage());
                throw new IOConnectorException(e17.getMessage(), 2);
            }
        } catch (ConnectorException e18) {
            throw e18;
        } catch (MalformedURLException e19) {
            e = e19;
            this.log.d("processCommand() - send - MalformedURLException ", e.getMessage());
            throw new IOConnectorException(e.getMessage(), 6);
        } catch (ProtocolException e20) {
            e = e20;
            this.log.d("processCommand() - send - ProtocolException ", e.getMessage());
            throw new IOConnectorException(e.getMessage(), 6);
        } catch (SSLException e21) {
            e = e21;
            inputStream = httpURLConnection;
            try {
                this.log.d("processCommand() - send - SSLException ", e.getMessage());
                throw new IOConnectorException(e.getMessage(), 4);
            } catch (Throwable th6) {
                th = th6;
                httpURLConnection = inputStream;
            }
        } catch (IOException e22) {
            e = e22;
            this.log.d("processCommand() - send - IOException ", e.getMessage());
            throw new IOConnectorException(e.getMessage(), 2);
        } catch (NullPointerException e23) {
            e = e23;
            this.log.d("processCommand() - send - NullPointerException ", e.getMessage());
            throw new IOConnectorException(e.getMessage(), 2);
        } catch (Exception e24) {
            e = e24;
            this.log.d("processCommand() - send - Exception ", e.getMessage());
            throw new IOConnectorException(e.getMessage(), 2);
        }
        th = th2;
        networkExpirer.complete();
        if (httpURLConnection != null) {
            try {
                httpURLConnection.disconnect();
            } catch (Exception e25) {
                this.log.w("Exception closing server connection.");
            }
        }
        throw th;
    }

    public void setPersistentConfig(PersistantConnectionConfig persistantConnectionConfig, boolean z) {
        if (this.connected && persistantConnectionConfig.equals(this.persistantConfig)) {
            disconnect();
        }
        this.persistantConfig = persistantConnectionConfig;
        setCompressionThreshold(this.persistantConfig.getCompressionThreshold());
        if (z) {
            connect();
        }
    }

    public synchronized void updateMinimumSSLProtocol(String str) {
        this.log.d("updateMinimumSSLProtocol: ", str);
        this.sslSocketFactory = CustomProtocolSocketFactory.createSocketFactory(str);
    }
}
