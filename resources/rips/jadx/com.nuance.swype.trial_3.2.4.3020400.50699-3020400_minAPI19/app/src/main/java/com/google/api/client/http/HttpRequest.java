package com.google.api.client.http;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.IOUtils;
import com.google.api.client.util.LoggingStreamingContent;
import com.google.api.client.util.ObjectParser;
import com.google.api.client.util.Sleeper;
import com.google.api.client.util.StreamingContent;
import com.google.api.client.util.StringUtils;
import com.nuance.nmsp.client.sdk.components.core.internal.calllog.CalllogImpl;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/* loaded from: classes.dex */
public final class HttpRequest {
    public static final int DEFAULT_NUMBER_OF_RETRIES = 10;
    public static final String USER_AGENT_SUFFIX = "Google-HTTP-Java-Client/1.22.0 (gzip)";
    public static final String VERSION = "1.22.0";

    @Deprecated
    private BackOffPolicy backOffPolicy;
    private HttpContent content;
    private HttpEncoding encoding;
    private HttpExecuteInterceptor executeInterceptor;
    private HttpIOExceptionHandler ioExceptionHandler;
    private ObjectParser objectParser;
    private String requestMethod;
    private HttpResponseInterceptor responseInterceptor;
    private boolean suppressUserAgentSuffix;
    private final HttpTransport transport;
    private HttpUnsuccessfulResponseHandler unsuccessfulResponseHandler;
    private GenericUrl url;
    private HttpHeaders headers = new HttpHeaders();
    private HttpHeaders responseHeaders = new HttpHeaders();
    private int numRetries = 10;
    private int contentLoggingLimit = HardKeyboardManager.META_CTRL_RIGHT_ON;
    private boolean loggingEnabled = true;
    private boolean curlLoggingEnabled = true;
    private int connectTimeout = CalllogImpl.CALLLOG_CHUNK_SIZE_MINIMUM;
    private int readTimeout = CalllogImpl.CALLLOG_CHUNK_SIZE_MINIMUM;
    private boolean followRedirects = true;
    private boolean throwExceptionOnExecuteError = true;

    @Deprecated
    private boolean retryOnExecuteIOException = false;
    private Sleeper sleeper = Sleeper.DEFAULT;

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpRequest(HttpTransport transport, String requestMethod) {
        this.transport = transport;
        setRequestMethod(requestMethod);
    }

    public final HttpTransport getTransport() {
        return this.transport;
    }

    public final String getRequestMethod() {
        return this.requestMethod;
    }

    public final HttpRequest setRequestMethod(String requestMethod) {
        Preconditions.checkArgument(requestMethod == null || HttpMediaType.matchesToken(requestMethod));
        this.requestMethod = requestMethod;
        return this;
    }

    public final GenericUrl getUrl() {
        return this.url;
    }

    public final HttpContent getContent() {
        return this.content;
    }

    public final HttpRequest setContent(HttpContent content) {
        this.content = content;
        return this;
    }

    public final HttpEncoding getEncoding() {
        return this.encoding;
    }

    public final HttpRequest setEncoding(HttpEncoding encoding) {
        this.encoding = encoding;
        return this;
    }

    @Deprecated
    public final BackOffPolicy getBackOffPolicy() {
        return this.backOffPolicy;
    }

    @Deprecated
    public final HttpRequest setBackOffPolicy(BackOffPolicy backOffPolicy) {
        this.backOffPolicy = backOffPolicy;
        return this;
    }

    public final int getContentLoggingLimit() {
        return this.contentLoggingLimit;
    }

    public final HttpRequest setContentLoggingLimit(int contentLoggingLimit) {
        com.google.api.client.util.Preconditions.checkArgument(contentLoggingLimit >= 0, "The content logging limit must be non-negative.");
        this.contentLoggingLimit = contentLoggingLimit;
        return this;
    }

    public final boolean isLoggingEnabled() {
        return this.loggingEnabled;
    }

    public final HttpRequest setLoggingEnabled(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
        return this;
    }

    public final boolean isCurlLoggingEnabled() {
        return this.curlLoggingEnabled;
    }

    public final HttpRequest setCurlLoggingEnabled(boolean curlLoggingEnabled) {
        this.curlLoggingEnabled = curlLoggingEnabled;
        return this;
    }

    public final int getConnectTimeout() {
        return this.connectTimeout;
    }

    public final HttpRequest setConnectTimeout(int connectTimeout) {
        Preconditions.checkArgument(connectTimeout >= 0);
        this.connectTimeout = connectTimeout;
        return this;
    }

    public final int getReadTimeout() {
        return this.readTimeout;
    }

    public final HttpRequest setReadTimeout(int readTimeout) {
        Preconditions.checkArgument(readTimeout >= 0);
        this.readTimeout = readTimeout;
        return this;
    }

    public final HttpHeaders getHeaders() {
        return this.headers;
    }

    public final HttpHeaders getResponseHeaders() {
        return this.responseHeaders;
    }

    public final HttpExecuteInterceptor getInterceptor() {
        return this.executeInterceptor;
    }

    public final HttpRequest setInterceptor(HttpExecuteInterceptor interceptor) {
        this.executeInterceptor = interceptor;
        return this;
    }

    public final HttpUnsuccessfulResponseHandler getUnsuccessfulResponseHandler() {
        return this.unsuccessfulResponseHandler;
    }

    public final HttpRequest setUnsuccessfulResponseHandler(HttpUnsuccessfulResponseHandler unsuccessfulResponseHandler) {
        this.unsuccessfulResponseHandler = unsuccessfulResponseHandler;
        return this;
    }

    public final HttpIOExceptionHandler getIOExceptionHandler() {
        return this.ioExceptionHandler;
    }

    public final HttpRequest setIOExceptionHandler(HttpIOExceptionHandler ioExceptionHandler) {
        this.ioExceptionHandler = ioExceptionHandler;
        return this;
    }

    public final HttpResponseInterceptor getResponseInterceptor() {
        return this.responseInterceptor;
    }

    public final HttpRequest setResponseInterceptor(HttpResponseInterceptor responseInterceptor) {
        this.responseInterceptor = responseInterceptor;
        return this;
    }

    public final int getNumberOfRetries() {
        return this.numRetries;
    }

    public final HttpRequest setNumberOfRetries(int numRetries) {
        Preconditions.checkArgument(numRetries >= 0);
        this.numRetries = numRetries;
        return this;
    }

    public final HttpRequest setParser(ObjectParser parser) {
        this.objectParser = parser;
        return this;
    }

    public final ObjectParser getParser() {
        return this.objectParser;
    }

    public final boolean getFollowRedirects() {
        return this.followRedirects;
    }

    public final HttpRequest setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }

    public final boolean getThrowExceptionOnExecuteError() {
        return this.throwExceptionOnExecuteError;
    }

    public final HttpRequest setThrowExceptionOnExecuteError(boolean throwExceptionOnExecuteError) {
        this.throwExceptionOnExecuteError = throwExceptionOnExecuteError;
        return this;
    }

    @Deprecated
    public final boolean getRetryOnExecuteIOException() {
        return this.retryOnExecuteIOException;
    }

    @Deprecated
    public final HttpRequest setRetryOnExecuteIOException(boolean retryOnExecuteIOException) {
        this.retryOnExecuteIOException = retryOnExecuteIOException;
        return this;
    }

    public final boolean getSuppressUserAgentSuffix() {
        return this.suppressUserAgentSuffix;
    }

    public final HttpRequest setSuppressUserAgentSuffix(boolean suppressUserAgentSuffix) {
        this.suppressUserAgentSuffix = suppressUserAgentSuffix;
        return this;
    }

    public final HttpResponse execute() throws IOException {
        IOException executeException;
        boolean retryRequest;
        String contentEncoding;
        long contentLength;
        Preconditions.checkArgument(this.numRetries >= 0);
        int retriesRemaining = this.numRetries;
        if (this.backOffPolicy != null) {
            this.backOffPolicy.reset();
        }
        HttpResponse response = null;
        Preconditions.checkNotNull(this.requestMethod);
        Preconditions.checkNotNull(this.url);
        do {
            if (response != null) {
                response.ignore();
            }
            response = null;
            executeException = null;
            if (this.executeInterceptor != null) {
                this.executeInterceptor.intercept(this);
            }
            String urlString = this.url.build();
            LowLevelHttpRequest lowLevelHttpRequest = this.transport.buildRequest(this.requestMethod, urlString);
            Logger logger = HttpTransport.LOGGER;
            boolean loggable = this.loggingEnabled && logger.isLoggable(Level.CONFIG);
            StringBuilder logbuf = null;
            StringBuilder curlbuf = null;
            if (loggable) {
                logbuf = new StringBuilder();
                logbuf.append("-------------- REQUEST  --------------").append(StringUtils.LINE_SEPARATOR);
                logbuf.append(this.requestMethod).append(' ').append(urlString).append(StringUtils.LINE_SEPARATOR);
                if (this.curlLoggingEnabled) {
                    curlbuf = new StringBuilder("curl -v --compressed");
                    if (!this.requestMethod.equals("GET")) {
                        curlbuf.append(" -X ").append(this.requestMethod);
                    }
                }
            }
            String originalUserAgent = this.headers.getUserAgent();
            if (!this.suppressUserAgentSuffix) {
                if (originalUserAgent == null) {
                    this.headers.setUserAgent(USER_AGENT_SUFFIX);
                } else {
                    this.headers.setUserAgent(originalUserAgent + " Google-HTTP-Java-Client/1.22.0 (gzip)");
                }
            }
            HttpHeaders.serializeHeaders(this.headers, logbuf, curlbuf, logger, lowLevelHttpRequest);
            if (!this.suppressUserAgentSuffix) {
                this.headers.setUserAgent(originalUserAgent);
            }
            StreamingContent streamingContent = this.content;
            boolean contentRetrySupported = streamingContent == null || this.content.retrySupported();
            if (streamingContent != null) {
                String contentType = this.content.getType();
                if (loggable) {
                    streamingContent = new LoggingStreamingContent(streamingContent, HttpTransport.LOGGER, Level.CONFIG, this.contentLoggingLimit);
                }
                if (this.encoding == null) {
                    contentEncoding = null;
                    contentLength = this.content.getLength();
                } else {
                    contentEncoding = this.encoding.getName();
                    StreamingContent streamingContent2 = new HttpEncodingStreamingContent(streamingContent, this.encoding);
                    if (contentRetrySupported) {
                        contentLength = IOUtils.computeLength(streamingContent2);
                        streamingContent = streamingContent2;
                    } else {
                        contentLength = -1;
                        streamingContent = streamingContent2;
                    }
                }
                if (loggable) {
                    if (contentType != null) {
                        String header = "Content-Type: " + contentType;
                        logbuf.append(header).append(StringUtils.LINE_SEPARATOR);
                        if (curlbuf != null) {
                            curlbuf.append(" -H '" + header + "'");
                        }
                    }
                    if (contentEncoding != null) {
                        String header2 = "Content-Encoding: " + contentEncoding;
                        logbuf.append(header2).append(StringUtils.LINE_SEPARATOR);
                        if (curlbuf != null) {
                            curlbuf.append(" -H '" + header2 + "'");
                        }
                    }
                    if (contentLength >= 0) {
                        logbuf.append("Content-Length: " + contentLength).append(StringUtils.LINE_SEPARATOR);
                    }
                }
                if (curlbuf != null) {
                    curlbuf.append(" -d '@-'");
                }
                lowLevelHttpRequest.setContentType(contentType);
                lowLevelHttpRequest.setContentEncoding(contentEncoding);
                lowLevelHttpRequest.setContentLength(contentLength);
                lowLevelHttpRequest.setStreamingContent(streamingContent);
            }
            if (loggable) {
                logger.config(logbuf.toString());
                if (curlbuf != null) {
                    curlbuf.append(" -- '");
                    curlbuf.append(urlString.replaceAll("'", "'\"'\"'"));
                    curlbuf.append("'");
                    if (streamingContent != null) {
                        curlbuf.append(" << $$$");
                    }
                    logger.config(curlbuf.toString());
                }
            }
            boolean retryRequest2 = contentRetrySupported && retriesRemaining > 0;
            lowLevelHttpRequest.setTimeout(this.connectTimeout, this.readTimeout);
            try {
                LowLevelHttpResponse lowLevelHttpResponse = lowLevelHttpRequest.execute();
                try {
                    HttpResponse response2 = new HttpResponse(this, lowLevelHttpResponse);
                    response = response2;
                } catch (Throwable th) {
                    InputStream lowLevelContent = lowLevelHttpResponse.getContent();
                    if (lowLevelContent != null) {
                        lowLevelContent.close();
                    }
                    throw th;
                    break;
                }
            } catch (IOException e) {
                if (!this.retryOnExecuteIOException && (this.ioExceptionHandler == null || !this.ioExceptionHandler.handleIOException(this, retryRequest2))) {
                    throw e;
                }
                executeException = e;
                logger.log(Level.WARNING, "exception thrown while executing request", (Throwable) e);
            }
            if (response != null) {
                try {
                    if (!response.isSuccessStatusCode()) {
                        boolean errorHandled = false;
                        if (this.unsuccessfulResponseHandler != null) {
                            errorHandled = this.unsuccessfulResponseHandler.handleResponse(this, response, retryRequest2);
                        }
                        if (!errorHandled) {
                            if (handleRedirect(response.getStatusCode(), response.getHeaders())) {
                                errorHandled = true;
                            } else if (retryRequest2 && this.backOffPolicy != null && this.backOffPolicy.isBackOffRequired(response.getStatusCode())) {
                                long backOffTime = this.backOffPolicy.getNextBackOffMillis();
                                if (backOffTime != -1) {
                                    try {
                                        this.sleeper.sleep(backOffTime);
                                    } catch (InterruptedException e2) {
                                    }
                                    errorHandled = true;
                                }
                            }
                        }
                        retryRequest = retryRequest2 & errorHandled;
                        if (retryRequest) {
                            response.ignore();
                        }
                        retriesRemaining--;
                        if (response == null) {
                        }
                    }
                } catch (Throwable th2) {
                    if (response != null) {
                    }
                    throw th2;
                }
            }
            retryRequest = retryRequest2 & (response == null);
            retriesRemaining--;
            if (response == null) {
            }
        } while (retryRequest);
        if (response == null) {
            throw executeException;
        }
        if (this.responseInterceptor != null) {
            this.responseInterceptor.interceptResponse(response);
        }
        if (this.throwExceptionOnExecuteError && !response.isSuccessStatusCode()) {
            try {
                throw new HttpResponseException(response);
            } finally {
                response.disconnect();
            }
        }
        return response;
    }

    public final Future<HttpResponse> executeAsync(Executor executor) {
        FutureTask<HttpResponse> future = new FutureTask<>(new Callable<HttpResponse>() { // from class: com.google.api.client.http.HttpRequest.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public HttpResponse call() throws Exception {
                return HttpRequest.this.execute();
            }
        });
        executor.execute(future);
        return future;
    }

    public final Future<HttpResponse> executeAsync() {
        return executeAsync(Executors.newSingleThreadExecutor());
    }

    public final boolean handleRedirect(int statusCode, HttpHeaders responseHeaders) {
        String redirectLocation = responseHeaders.getLocation();
        if (!getFollowRedirects() || !HttpStatusCodes.isRedirect(statusCode) || redirectLocation == null) {
            return false;
        }
        setUrl(new GenericUrl(this.url.toURL(redirectLocation)));
        if (statusCode == 303) {
            setRequestMethod("GET");
            setContent(null);
        }
        this.headers.setAuthorization((String) null);
        this.headers.setIfMatch(null);
        this.headers.setIfNoneMatch(null);
        this.headers.setIfModifiedSince(null);
        this.headers.setIfUnmodifiedSince(null);
        this.headers.setIfRange(null);
        return true;
    }

    public final Sleeper getSleeper() {
        return this.sleeper;
    }

    public final HttpRequest setUrl(GenericUrl url) {
        this.url = (GenericUrl) Preconditions.checkNotNull(url);
        return this;
    }

    public final HttpRequest setHeaders(HttpHeaders headers) {
        this.headers = (HttpHeaders) Preconditions.checkNotNull(headers);
        return this;
    }

    public final HttpRequest setResponseHeaders(HttpHeaders responseHeaders) {
        this.responseHeaders = (HttpHeaders) Preconditions.checkNotNull(responseHeaders);
        return this;
    }

    public final HttpRequest setSleeper(Sleeper sleeper) {
        this.sleeper = (Sleeper) Preconditions.checkNotNull(sleeper);
        return this;
    }
}
