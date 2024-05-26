package io.fabric.sdk.android.services.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;

/* loaded from: classes.dex */
public final class HttpRequest {
    private String httpProxyHost;
    private int httpProxyPort;
    private boolean multipart;
    private RequestOutputStream output;
    private final String requestMethod;
    public final URL url;
    private static final String[] EMPTY_STRINGS = new String[0];
    private static ConnectionFactory CONNECTION_FACTORY = ConnectionFactory.DEFAULT;
    private HttpURLConnection connection = null;
    private boolean ignoreCloseExceptions = true;
    private boolean uncompress = false;
    private int bufferSize = 8192;

    /* loaded from: classes.dex */
    public interface ConnectionFactory {
        public static final ConnectionFactory DEFAULT = new ConnectionFactory() { // from class: io.fabric.sdk.android.services.network.HttpRequest.ConnectionFactory.1
            @Override // io.fabric.sdk.android.services.network.HttpRequest.ConnectionFactory
            public final HttpURLConnection create(URL url) throws IOException {
                return (HttpURLConnection) url.openConnection();
            }

            @Override // io.fabric.sdk.android.services.network.HttpRequest.ConnectionFactory
            public final HttpURLConnection create(URL url, Proxy proxy) throws IOException {
                return (HttpURLConnection) url.openConnection(proxy);
            }
        };

        HttpURLConnection create(URL url) throws IOException;

        HttpURLConnection create(URL url, Proxy proxy) throws IOException;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getValidCharset(String charset) {
        return (charset == null || charset.length() <= 0) ? "UTF-8" : charset;
    }

    /* loaded from: classes.dex */
    public static class HttpRequestException extends RuntimeException {
        protected HttpRequestException(IOException cause) {
            super(cause);
        }

        @Override // java.lang.Throwable
        public final /* bridge */ /* synthetic */ Throwable getCause() {
            return (IOException) super.getCause();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static abstract class Operation<V> implements Callable<V> {
        protected abstract void done() throws IOException;

        protected abstract V run() throws HttpRequestException, IOException;

        protected Operation() {
        }

        @Override // java.util.concurrent.Callable
        public V call() throws HttpRequestException {
            try {
                try {
                    V run = run();
                    try {
                        done();
                        return run;
                    } catch (IOException e) {
                        throw new HttpRequestException(e);
                    }
                } catch (HttpRequestException e2) {
                    throw e2;
                } catch (IOException e3) {
                    throw new HttpRequestException(e3);
                }
            } catch (Throwable th) {
                try {
                    done();
                } catch (IOException e4) {
                    if (0 == 0) {
                        throw new HttpRequestException(e4);
                    }
                }
                throw th;
            }
        }
    }

    /* loaded from: classes.dex */
    protected static abstract class CloseOperation<V> extends Operation<V> {
        private final Closeable closeable;
        private final boolean ignoreCloseExceptions;

        protected CloseOperation(Closeable closeable, boolean ignoreCloseExceptions) {
            this.closeable = closeable;
            this.ignoreCloseExceptions = ignoreCloseExceptions;
        }

        @Override // io.fabric.sdk.android.services.network.HttpRequest.Operation
        protected final void done() throws IOException {
            if (this.closeable instanceof Flushable) {
                ((Flushable) this.closeable).flush();
            }
            if (this.ignoreCloseExceptions) {
                try {
                    this.closeable.close();
                } catch (IOException e) {
                }
            } else {
                this.closeable.close();
            }
        }
    }

    /* loaded from: classes.dex */
    public static class RequestOutputStream extends BufferedOutputStream {
        private final CharsetEncoder encoder;

        public RequestOutputStream(OutputStream stream, String charset, int bufferSize) {
            super(stream, bufferSize);
            this.encoder = Charset.forName(HttpRequest.getValidCharset(charset)).newEncoder();
        }

        public final RequestOutputStream write(String value) throws IOException {
            ByteBuffer bytes = this.encoder.encode(CharBuffer.wrap(value));
            super.write(bytes.array(), 0, bytes.limit());
            return this;
        }
    }

    private static String encode(CharSequence url) throws HttpRequestException {
        try {
            URL parsed = new URL(url.toString());
            String host = parsed.getHost();
            int port = parsed.getPort();
            if (port != -1) {
                host = host + ':' + Integer.toString(port);
            }
            try {
                String encoded = new URI(parsed.getProtocol(), host, parsed.getPath(), parsed.getQuery(), null).toASCIIString();
                int paramsStart = encoded.indexOf(63);
                if (paramsStart > 0 && paramsStart + 1 < encoded.length()) {
                    return encoded.substring(0, paramsStart + 1) + encoded.substring(paramsStart + 1).replace("+", "%2B");
                }
                return encoded;
            } catch (URISyntaxException e) {
                IOException io2 = new IOException("Parsing URI failed");
                io2.initCause(e);
                throw new HttpRequestException(io2);
            }
        } catch (IOException e2) {
            throw new HttpRequestException(e2);
        }
    }

    private static String append(CharSequence url, Map<?, ?> params) {
        String baseUrl = url.toString();
        if (params != null && !params.isEmpty()) {
            StringBuilder result = new StringBuilder(baseUrl);
            if (baseUrl.indexOf(58) + 2 == baseUrl.lastIndexOf(47)) {
                result.append('/');
            }
            int indexOf = baseUrl.indexOf(63);
            int length = result.length() - 1;
            if (indexOf == -1) {
                result.append('?');
            } else if (indexOf < length && baseUrl.charAt(length) != '&') {
                result.append('&');
            }
            Iterator<?> iterator = params.entrySet().iterator();
            Map.Entry<?, ?> entry = iterator.next();
            result.append(entry.getKey().toString());
            result.append('=');
            Object value = entry.getValue();
            if (value != null) {
                result.append(value);
            }
            while (iterator.hasNext()) {
                result.append('&');
                Map.Entry<?, ?> entry2 = iterator.next();
                result.append(entry2.getKey().toString());
                result.append('=');
                Object value2 = entry2.getValue();
                if (value2 != null) {
                    result.append(value2);
                }
            }
            return result.toString();
        }
        return baseUrl;
    }

    public static HttpRequest get$6df498ee(CharSequence baseUrl, Map<?, ?> params) {
        return new HttpRequest(encode(append(baseUrl, params)), "GET");
    }

    public static HttpRequest post$6df498ee(CharSequence baseUrl, Map<?, ?> params) {
        return new HttpRequest(encode(append(baseUrl, params)), "POST");
    }

    public static HttpRequest put(CharSequence url) throws HttpRequestException {
        return new HttpRequest(url, "PUT");
    }

    public static HttpRequest delete(CharSequence url) throws HttpRequestException {
        return new HttpRequest(url, "DELETE");
    }

    private HttpRequest(CharSequence url, String method) throws HttpRequestException {
        try {
            this.url = new URL(url.toString());
            this.requestMethod = method;
        } catch (MalformedURLException e) {
            throw new HttpRequestException(e);
        }
    }

    private HttpURLConnection createConnection() {
        HttpURLConnection connection;
        try {
            if (this.httpProxyHost != null) {
                connection = CONNECTION_FACTORY.create(this.url, new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.httpProxyHost, this.httpProxyPort)));
            } else {
                connection = CONNECTION_FACTORY.create(this.url);
            }
            connection.setRequestMethod(this.requestMethod);
            return connection;
        } catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    public final String toString() {
        return getConnection().getRequestMethod() + ' ' + getConnection().getURL();
    }

    public final HttpURLConnection getConnection() {
        if (this.connection == null) {
            this.connection = createConnection();
        }
        return this.connection;
    }

    public final int code() throws HttpRequestException {
        try {
            closeOutput();
            return getConnection().getResponseCode();
        } catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    private InputStream stream() throws HttpRequestException {
        InputStream stream;
        if (code() < 400) {
            try {
                stream = getConnection().getInputStream();
            } catch (IOException e) {
                throw new HttpRequestException(e);
            }
        } else {
            stream = getConnection().getErrorStream();
            if (stream == null) {
                try {
                    stream = getConnection().getInputStream();
                } catch (IOException e2) {
                    throw new HttpRequestException(e2);
                }
            }
        }
        if (!this.uncompress || !"gzip".equals(header("Content-Encoding"))) {
            return stream;
        }
        try {
            return new GZIPInputStream(stream);
        } catch (IOException e3) {
            throw new HttpRequestException(e3);
        }
    }

    public final HttpRequest header(String name, String value) {
        getConnection().setRequestProperty(name, value);
        return this;
    }

    public final String header(String name) throws HttpRequestException {
        closeOutputQuietly();
        return getConnection().getHeaderField(name);
    }

    private static String getParam(String value, String paramName) {
        String paramValue;
        int valueLength;
        if (value == null || value.length() == 0) {
            return null;
        }
        int length = value.length();
        int start = value.indexOf(59) + 1;
        if (start == 0 || start == length) {
            return null;
        }
        int end = value.indexOf(59, start);
        if (end == -1) {
            end = length;
        }
        while (start < end) {
            int nameEnd = value.indexOf(61, start);
            if (nameEnd != -1 && nameEnd < end && paramName.equals(value.substring(start, nameEnd).trim()) && (valueLength = (paramValue = value.substring(nameEnd + 1, end).trim()).length()) != 0) {
                if (valueLength > 2 && '\"' == paramValue.charAt(0) && '\"' == paramValue.charAt(valueLength - 1)) {
                    return paramValue.substring(1, valueLength - 1);
                }
                return paramValue;
            }
            start = end + 1;
            end = value.indexOf(59, start);
            if (end == -1) {
                end = length;
            }
        }
        return null;
    }

    private HttpRequest copy(final InputStream input, final OutputStream output) throws IOException {
        return new CloseOperation<HttpRequest>(input, this.ignoreCloseExceptions) { // from class: io.fabric.sdk.android.services.network.HttpRequest.8
            @Override // io.fabric.sdk.android.services.network.HttpRequest.Operation
            public final /* bridge */ /* synthetic */ Object run() throws HttpRequestException, IOException {
                byte[] bArr = new byte[HttpRequest.this.bufferSize];
                while (true) {
                    int read = input.read(bArr);
                    if (read != -1) {
                        output.write(bArr, 0, read);
                    } else {
                        return HttpRequest.this;
                    }
                }
            }
        }.call();
    }

    private HttpRequest closeOutput() throws IOException {
        if (this.output != null) {
            if (this.multipart) {
                this.output.write("\r\n--00content0boundary00--\r\n");
            }
            if (this.ignoreCloseExceptions) {
                try {
                    this.output.close();
                } catch (IOException e) {
                }
            } else {
                this.output.close();
            }
            this.output = null;
        }
        return this;
    }

    private HttpRequest closeOutputQuietly() throws HttpRequestException {
        try {
            return closeOutput();
        } catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    private HttpRequest openOutput() throws IOException {
        if (this.output == null) {
            getConnection().setDoOutput(true);
            String charset = getParam(getConnection().getRequestProperty("Content-Type"), "charset");
            this.output = new RequestOutputStream(getConnection().getOutputStream(), charset, this.bufferSize);
        }
        return this;
    }

    private HttpRequest startPart() throws IOException {
        if (!this.multipart) {
            this.multipart = true;
            header("Content-Type", "multipart/form-data; boundary=00content0boundary00").openOutput();
            this.output.write("--00content0boundary00\r\n");
        } else {
            this.output.write("\r\n--00content0boundary00\r\n");
        }
        return this;
    }

    private HttpRequest writePartHeader(String name, String filename, String contentType) throws IOException {
        StringBuilder partBuffer = new StringBuilder();
        partBuffer.append("form-data; name=\"").append(name);
        if (filename != null) {
            partBuffer.append("\"; filename=\"").append(filename);
        }
        partBuffer.append('\"');
        partHeader("Content-Disposition", partBuffer.toString());
        if (contentType != null) {
            partHeader("Content-Type", contentType);
        }
        return send("\r\n");
    }

    public final HttpRequest part$d4ee95d(String name, String filename, String part) throws HttpRequestException {
        try {
            startPart();
            writePartHeader(name, null, null);
            this.output.write(part);
            return this;
        } catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    public final HttpRequest part(String name, String filename, String contentType, File part) throws HttpRequestException {
        InputStream stream = null;
        try {
            try {
                InputStream stream2 = new BufferedInputStream(new FileInputStream(part));
                try {
                    HttpRequest part2 = part(name, filename, contentType, stream2);
                    try {
                        stream2.close();
                    } catch (IOException e) {
                    }
                    return part2;
                } catch (IOException e2) {
                    e = e2;
                    stream = stream2;
                    throw new HttpRequestException(e);
                } catch (Throwable th) {
                    th = th;
                    stream = stream2;
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e3) {
                        }
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e4) {
            e = e4;
        }
    }

    public final HttpRequest part(String name, String filename, String contentType, InputStream part) throws HttpRequestException {
        try {
            startPart();
            writePartHeader(name, filename, contentType);
            copy(part, this.output);
            return this;
        } catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    private HttpRequest partHeader(String name, String value) throws HttpRequestException {
        return send(name).send(": ").send(value).send("\r\n");
    }

    private HttpRequest send(CharSequence value) throws HttpRequestException {
        try {
            openOutput();
            this.output.write(value.toString());
            return this;
        } catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    private String body(String charset) throws HttpRequestException {
        ByteArrayOutputStream output;
        closeOutputQuietly();
        int headerFieldInt = getConnection().getHeaderFieldInt("Content-Length", -1);
        if (headerFieldInt > 0) {
            output = new ByteArrayOutputStream(headerFieldInt);
        } else {
            output = new ByteArrayOutputStream();
        }
        try {
            copy(new BufferedInputStream(stream(), this.bufferSize), output);
            return output.toString(getValidCharset(charset));
        } catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    public final String body() throws HttpRequestException {
        return body(getParam(header("Content-Type"), "charset"));
    }

    public final HttpRequest part(String name, Number part) throws HttpRequestException {
        return part$d4ee95d(name, null, part != null ? part.toString() : null);
    }
}
